package com.doodeec.silversparrow.data.provider;

import android.support.annotation.Nullable;

import com.doodeec.silversparrow.AppConfig;
import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.DBEntity;
import com.doodeec.silversparrow.data.model.Order;
import com.doodeec.silversparrow.util.Tuple2;
import com.doodeec.silversparrow.util.rx.RxUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Abstraction over Storage access
 *
 * @author Dusan Bartos
 */
public class DataProvider {

    private final StorageManager storageManager;
    private final ServiceProvider serviceProvider;

    private final BehaviorSubject<List<Contact>> contactsSubject = BehaviorSubject.create(new ArrayList<Contact>());
    private final BehaviorSubject<String> selectedContact = BehaviorSubject.create();
    private final BehaviorSubject<List<Order>> ordersSubject = BehaviorSubject.create(new ArrayList<Order>());

    private final BehaviorSubject<Boolean> loadingState = BehaviorSubject.create(false);
    private final PublishSubject<Throwable> errorSubject = PublishSubject.create();

    public DataProvider(StorageManager storageManager,
                        ServiceProvider serviceProvider) {
        this.storageManager = storageManager;
        this.serviceProvider = serviceProvider;

        // observe DB entries
        storageManager.observe(Contact.class)
                .observeOn(Schedulers.computation())
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.handleErrorNever("Error observing Contacts"))
                //TODO if new contact list does not contain currently selected one, refresh selected id
                .subscribe(contactsSubject::onNext);

        storageManager.observe(Order.class)
                .observeOn(Schedulers.computation())
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.handleErrorNever("Error observing Orders"))
                .subscribe(ordersSubject::onNext);
    }

    /**
     * Observes stored list of contacts
     * Contacts are read from DB, every API request invokes update in the table and therefore new event in this stream
     *
     * @return observable list of contacts
     */
    public Observable<List<Contact>> observeContacts() {
        return contactsSubject.asObservable();
    }

    public Observable<Contact> observeSelectedContact() {
        return selectedContact.map(this::getContact);
    }

    /**
     * Observes stored list of orders linked to the specific contact
     * Orders are read from DB, every API request invokes update in the table and therefore new event in this stream
     *
     * @return observable list of orders for given contact
     */
    public Observable<List<Order>> observeOrders(String contactId) {
        if (contactId == null || contactId.isEmpty()) return Observable.empty();

        return ordersSubject.asObservable()
                .flatMap(orders -> Observable.from(orders)
                        .filter(order -> contactId.equals(order.getContactId()))
                        .toList());
    }

    public Observable<Throwable> observeErrors() {
        return errorSubject.asObservable();
    }

    /**
     * @return observable state of API loading state - ongoing requests
     */
    public Observable<Boolean> observeLoadingState() {
        return loadingState.asObservable().onBackpressureLatest();
    }

    public BehaviorSubject<String> selectedContactId() {
        return selectedContact;
    }

    @Nullable public Contact getContact(String id) {
        if (id == null || id.isEmpty()) return null;

        return contactsSubject
                .flatMap(cl -> Observable.from(cl)
                        .filter(c -> id.equals(c.getId()))
                        .firstOrDefault(null))
                .toBlocking()
                .first();
    }

    public List<Order> getOrders(String contactId) {
        if (contactId == null || contactId.isEmpty()) return Collections.emptyList();

        return observeOrders(contactId)
                .firstOrDefault(Collections.emptyList())
                .toBlocking()
                .first();
    }

    public void addData(List<? extends DBEntity> entities) {
        try {
            storageManager.addAll(entities);
        } catch (Exception e) {
            Timber.e(e, "Error adding entities %s", entities);
            errorSubject.onNext(e);
        }
    }

    public void deleteData(List<? extends DBEntity> entities) {
        try {
            storageManager.delete(entities);
        } catch (Exception e) {
            Timber.e(e, "Error adding entities %s", entities);
            errorSubject.onNext(e);
        }
    }

    /**
     * Refresh list of contacts and update entries in DB
     */
    public void refreshContacts() {
        serviceProvider.getContacts()
                .doOnSubscribe(() -> loadingState.onNext(true))
                .doOnUnsubscribe(() -> loadingState.onNext(false))
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.handleErrorEmpty("Error getting contacts"))
                .doOnNext(x -> Timber.d("Contacts: %s", x))
                .flatMap(cl -> Observable.from(cl).compose(processImageUrl()).toList())
                .subscribe(this::addData);
    }

    private Observable.Transformer<Contact, Contact> processImageUrl() {
        return c -> c.doOnNext(contact -> {
            String url = contact.getImage();
            //check url just in case
            if (urlNeedPrefix(url)) {
                url = AppConfig.ENDPOINT + url;
            }
            contact.setImage(url);
        });
    }

    private boolean urlNeedPrefix(String url) {
        return url != null && !url.isEmpty() &&
                !url.startsWith("http://") &&
                !url.startsWith("https://") &&
                !url.startsWith("file:///");
    }

    /**
     * Refresh list of orders for this contact and replace entries in DB
     *
     * @param contactId guid of contact
     */
    public void refreshOrders(String contactId) {
        serviceProvider.getOrders(contactId)
                .doOnSubscribe(() -> loadingState.onNext(true))
                .doOnUnsubscribe(() -> loadingState.onNext(false))
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.handleErrorEmpty("Error getting orders"))
                .doOnNext(x -> Timber.d("Orders: %s", x))
                .flatMap(ol -> Observable.from(ol).doOnNext(o -> o.setContactId(contactId)).toList())
                .map(ol -> new Tuple2<>(contactId, ol))
                .subscribe(this::processOrdersResult);
    }

    private void processOrdersResult(Tuple2<String, List<Order>> result) {
        deleteData(getOrders(result.getFirst()));
        addData(result.getSecond());
    }

    public Observable<Boolean> createContact(String name, String phone) {
        return serviceProvider.createContact(name, phone)
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.handleErrorEmpty("Error creating contact"))
                .doOnNext(x -> Timber.d("Contact added: %s", x))
                .firstOrDefault(false);
    }
}