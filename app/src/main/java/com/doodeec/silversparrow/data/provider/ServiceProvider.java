package com.doodeec.silversparrow.data.provider;

import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.Order;
import com.doodeec.silversparrow.network.RestService;
import com.doodeec.silversparrow.network.request.CreateContactRequest;
import com.doodeec.silversparrow.network.response.BaseResponse;
import com.doodeec.silversparrow.network.response.ContactsResponse;
import com.doodeec.silversparrow.network.response.OrdersResponse;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class ServiceProvider {
    private final RestService restService;

    public ServiceProvider(RestService restService) {
        this.restService = restService;
    }

    public Observable<Boolean> createContact(String name, String phone) {
        return restService.createContact(new CreateContactRequest(name, phone))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .doOnNext(x -> Timber.d("Create contact response: %s", x))
                .map(x -> true)
                .firstOrDefault(false);
    }

    public Observable<List<Contact>> getContacts() {
        return restService.getContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .doOnNext(x -> Timber.d("Get contacts response: %s", x))
                .map(ContactsResponse::getContacts);
    }

    public Observable<List<Order>> getOrders(String contactId) {
        return restService.getOrders(contactId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .doOnNext(x -> Timber.d("Get orders response: %s", x))
                .map(OrdersResponse::getOrders);
    }

    private <T extends BaseResponse> Observable.Transformer<T, T> handleCommonErrors() {
        return br -> br
                .map(Observable::just)
                .switchMap(baseResponse -> Observable.merge(
                        baseResponse.filter(BaseResponse::hasError)
                                .flatMap(r -> Observable.<T>error(
                                        new Throwable(r.getError().getMessage()))),
                        baseResponse
                ));
    }
}
