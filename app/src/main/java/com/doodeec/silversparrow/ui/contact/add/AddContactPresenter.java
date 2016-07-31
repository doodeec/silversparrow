package com.doodeec.silversparrow.ui.contact.add;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.doodeec.silversparrow.R;
import com.doodeec.silversparrow.base.RxMvpBasePresenter;
import com.doodeec.silversparrow.dagger.scope.ActivityScope;
import com.doodeec.silversparrow.data.provider.DataProvider;
import com.doodeec.silversparrow.util.Tuple2;
import com.doodeec.silversparrow.util.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@ActivityScope
public class AddContactPresenter extends RxMvpBasePresenter<AddContactView> {

    private final Context context;
    private final Resources resources;
    private final DataProvider dataProvider;
    private final ValidationUtils.Validator validator;

    @Inject AddContactPresenter(Context context,
                                Resources resources,
                                DataProvider dataProvider) {
        this.context = context;
        this.resources = resources;
        this.dataProvider = dataProvider;
        this.validator = new ValidationUtils.Validator();

        final Map<String, Tuple2<Func1<String, Boolean>, Integer>> predicates = new HashMap<>();
        predicates.put("name", new Tuple2<>(
                val -> !TextUtils.isEmpty(val) && val.length() > 4,
                R.string.validation_error_name));
        predicates.put("phone", new Tuple2<>(
                val -> !TextUtils.isEmpty(val) && val.length() > 4,
                R.string.validation_error_phone));
        this.validator.setPredicateMap(predicates);
    }

    public void onAddClick(String name, String phone) {
        final Map<String, String> values = new HashMap<>();
        values.put("name", name);
        values.put("phone", phone);

        if (isViewAttached()) {
            final List<Integer> messages = validator.validate(values);
            if (messages == null) {
                add(dataProvider.createContact(name, phone)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> getView().showLoading(R.string.loading))
                        .doOnEach(notification -> getView().hideLoading())
                        .filter(Boolean::valueOf)
                        .subscribe(x -> {
                            dataProvider.refreshContacts();
                            getView().closeActivity();
                        }));
            } else {
                final StringBuilder message = new StringBuilder();
                for (Integer msg : messages) {
                    try {
                        message.append(resources.getString(msg));
                        message.append("\n");
                    } catch (Exception e) {
                        Timber.e(e, "Cannot get validation message");
                    }
                }

                new AlertDialog.Builder(context)
                        .setTitle(R.string.validator_dialog_title)
                        .setMessage(message.toString())
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
        } else {
            Timber.w("View is not attached, cannot add contact");
        }
    }
}
