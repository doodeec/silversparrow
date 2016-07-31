package com.doodeec.silversparrow.ui.contact.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doodeec.silversparrow.App;
import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.InjectableMvpFragment;
import com.doodeec.silversparrow.base.Layout;
import com.doodeec.silversparrow.dagger.component.ContactDetailComponent;
import com.doodeec.silversparrow.dagger.component.DaggerContactDetailComponent;
import com.doodeec.silversparrow.dagger.module.ContextModule;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_contact_detail)
public class ContactDetailFragment extends InjectableMvpFragment<ContactDetailView, ContactDetailPresenter>
        implements ContactDetailView {

    @BindView(R.id.contact_phone) TextView phone;
    @BindView(R.id.contact_order_list) RecyclerView orderList;
    @BindView(R.id.order_empty_list_msg) TextView emptyMsg;
    @BindView(R.id.contact_not_selected) View notSelectedMsg;

    private ContactDetailComponent contactDetailComponent;

    @Override protected void inject() {
        contactDetailComponent = DaggerContactDetailComponent.builder()
                .appComponent(App.getAppComponent())
                .contextModule(new ContextModule(this))
                .build();
    }

    @NonNull @Override public ContactDetailPresenter createPresenter() {
        return contactDetailComponent.presenter();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderList.setLayoutManager(new LinearLayoutManager(getContext()));
        presenter.initProductList(orderList);
    }

    @Override public void setPhone(String phone) {
        this.phone.setText(phone);
    }

    @Override public void setName(String name) {
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setTitle(name);
            }
        }
    }

    @Override public void showEmptyMessage(boolean show) {
        emptyMsg.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override public void showContact(boolean show) {
        notSelectedMsg.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.contact_phone) void phoneClick() {
        presenter.onPhoneClicked();
    }
}
