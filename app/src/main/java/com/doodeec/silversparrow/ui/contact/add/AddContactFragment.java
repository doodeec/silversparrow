package com.doodeec.silversparrow.ui.contact.add;

import android.support.annotation.NonNull;
import android.widget.EditText;

import com.doodeec.silversparrow.App;
import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.InjectableMvpFragment;
import com.doodeec.silversparrow.base.Layout;
import com.doodeec.silversparrow.dagger.component.AddContactComponent;
import com.doodeec.silversparrow.dagger.component.DaggerAddContactComponent;
import com.doodeec.silversparrow.dagger.module.ContextModule;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_add_contact)
public class AddContactFragment extends InjectableMvpFragment<AddContactView, AddContactPresenter>
        implements AddContactView {

    @BindView(R.id.new_contact_name) EditText name;
    @BindView(R.id.new_contact_phone) EditText phone;

    private AddContactComponent addContactComponent;

    @Override protected void inject() {
        addContactComponent = DaggerAddContactComponent.builder()
                .appComponent(App.getAppComponent())
                .contextModule(new ContextModule(this))
                .build();
    }

    @NonNull @Override public AddContactPresenter createPresenter() {
        return addContactComponent.presenter();
    }

    @OnClick(R.id.new_contact_add_btn) void addContactClick() {
        presenter.onAddClick(name.getText().toString().trim(), phone.getText().toString().trim());
    }
}
