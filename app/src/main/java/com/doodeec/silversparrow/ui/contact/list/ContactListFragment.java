package com.doodeec.silversparrow.ui.contact.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doodeec.silversparrow.App;
import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.InjectableMvpFragment;
import com.doodeec.silversparrow.base.Layout;
import com.doodeec.silversparrow.dagger.component.ContactListComponent;
import com.doodeec.silversparrow.dagger.component.DaggerContactListComponent;
import com.doodeec.silversparrow.dagger.module.ContextModule;
import com.doodeec.silversparrow.util.recycler.RecyclerItemClickListener;

import butterknife.BindView;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_contact_list)
public class ContactListFragment extends InjectableMvpFragment<ContactListView, ContactListPresenter>
        implements ContactListView, RecyclerItemClickListener.IOnItemClickListener {

    @BindView(R.id.contact_list) RecyclerView list;
    @BindView(R.id.contact_empty_list_msg) TextView emptyMsg;

    private ContactListComponent contactListComponent;

    @Override protected void inject() {
        contactListComponent = DaggerContactListComponent.builder()
                .appComponent(App.getAppComponent())
                .contextModule(new ContextModule(this))
                .build();
    }

    @NonNull @Override public ContactListPresenter createPresenter() {
        return contactListComponent.presenter();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setHasFixedSize(true);
        list.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        presenter.initProductList(list);
    }

    @Override public void onItemClick(View view, int position) {
        presenter.onContactClicked(position);
    }

    @Override public void showEmptyMessage(boolean show) {
        emptyMsg.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
