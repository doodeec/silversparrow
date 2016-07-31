package com.doodeec.silversparrow.ui.contact.detail;

import android.support.v7.widget.RecyclerView;

import com.doodeec.silversparrow.base.RxMvpBasePresenter;
import com.doodeec.silversparrow.dagger.scope.ActivityScope;
import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.Order;
import com.doodeec.silversparrow.data.provider.DataProvider;
import com.doodeec.silversparrow.util.Util;
import com.doodeec.silversparrow.util.rx.RxUtils;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@ActivityScope
public class ContactDetailPresenter extends RxMvpBasePresenter<ContactDetailView> {

    private final DataProvider dataProvider;

    @Inject ContactDetailPresenter(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override public void attachView(ContactDetailView view) {
        super.attachView(view);

        add(dataProvider.observeErrors()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> showError(t.getMessage())));

        add(dataProvider.observeSelectedContact()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> Timber.d("Selected contact: %s", x))
                .compose(RxUtils.handleErrorNever("Error observing selected contact"))
                .subscribe(this::setContact));
    }

    private void setContact(Contact contact) {
        if (isViewAttached()) {
            getView().showContact(contact != null);
            if (contact != null) {
                dataProvider.refreshOrders(contact.getId());
                getView().setName(contact.getName());
                getView().setPhone(contact.getPhone());
            }
        }
    }

    public void initProductList(RecyclerView orderList) {
        OrderListAdapter adapter = new OrderListAdapter();
        orderList.setAdapter(adapter);

        add(dataProvider.observeSelectedContact()
                .flatMap(contact -> dataProvider.observeOrders(contact.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> Timber.d("orders: %s", x))
                .compose(RxUtils.handleErrorNever("Error observing orders"))
                .doOnNext(this::updateAdapterCount)
                .subscribe(adapter::updateData));
    }

    private void updateAdapterCount(List<Order> data) {
        if (isViewAttached()) {
            getView().showEmptyMessage(data.isEmpty());
        }
    }

    public void onPhoneClicked() {
        if (isViewAttached()) {
            String phone = dataProvider.observeSelectedContact()
                    .map(Contact::getPhone)
                    .toBlocking()
                    .firstOrDefault(null);
            getView().openActivity(Util.dialIntent(phone));
        }
    }
}
