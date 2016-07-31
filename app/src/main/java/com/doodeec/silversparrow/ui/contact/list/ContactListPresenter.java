package com.doodeec.silversparrow.ui.contact.list;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.IMenuClickablePresenter;
import com.doodeec.silversparrow.base.RxMvpBasePresenter;
import com.doodeec.silversparrow.dagger.scope.ActivityScope;
import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.provider.DataProvider;
import com.doodeec.silversparrow.ui.contact.add.AddContactActivity;
import com.doodeec.silversparrow.ui.contact.detail.ContactDetailActivity;
import com.doodeec.silversparrow.util.rx.RxUtils;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@ActivityScope
public class ContactListPresenter extends RxMvpBasePresenter<ContactListView> implements IMenuClickablePresenter {

    private final Context context;
    private final Resources resources;
    private final DataProvider dataProvider;

    private ContactListAdapter adapter;

    @Inject ContactListPresenter(Context context,
                                 Resources resources,
                                 DataProvider dataProvider) {
        this.context = context;
        this.resources = resources;
        this.dataProvider = dataProvider;
    }

    @Override public void attachView(ContactListView view) {
        super.attachView(view);

        add(dataProvider.observeErrors()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> showError(t.getMessage())));

        add(dataProvider.observeLoadingState()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(x -> isViewAttached())
                .subscribe(loading -> {
                    if (loading) {
                        getView().showLoading(R.string.loading);
                    } else {
                        getView().hideLoading();
                    }
                }));

        dataProvider.refreshContacts();
    }

    @Override public boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_add_contact:
                openAddContact();
                return true;

            case R.id.action_refresh:
                dataProvider.refreshContacts();
                return true;

            default:
                return false;
        }
    }

    public void initProductList(RecyclerView productList) {
        adapter = new ContactListAdapter();
        productList.setAdapter(adapter);

        add(dataProvider.observeContacts()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> Timber.d("Contacts updated: %s", x.size()))
                .compose(RxUtils.handleErrorNever("Error observing contacts"))
                .doOnNext(this::updateAdapterCount)
                .subscribe(adapter::updateData));
    }

    public void onContactClicked(int position) {
        final Contact contact = adapter.getItem(position);
        final boolean isPortrait = resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (contact != null) {
            dataProvider.selectedContactId().onNext(contact.getId());
            if (isPortrait && isViewAttached()) {
                getView().openActivity(new Intent(context, ContactDetailActivity.class));
            }
        } else {
            if (isViewAttached()) {
                getView().toast(R.string.contact_not_found);
            }
        }
    }

    private void openAddContact() {
        if (isViewAttached()) {
            getView().openActivity(new Intent(context, AddContactActivity.class));
        }
    }

    private void updateAdapterCount(List<Contact> data) {
        if (isViewAttached()) {
            getView().showEmptyMessage(data.isEmpty());
        }
    }
}
