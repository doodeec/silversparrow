package com.doodeec.silversparrow.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doodeec.silversparrow.util.Util;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import butterknife.ButterKnife;

/**
 * @author Dusan Bartos
 */
public abstract class InjectableMvpFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends MvpFragment<V, P> implements MvpView {

    private ProgressDialog progressDialog;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int layout = Util.getLayoutRes(this);
        if (layout == -1) {
            throw new IllegalStateException("Cannot inflate fragment without layout. " +
                    "Check if @Layout annotation is present");
        }

        return inflater.inflate(layout, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        inject();
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean onMenuItemClick(int id) {
        //if presenter can consume menu clicks, propagate this event
        if (presenter instanceof IMenuClickablePresenter) {
            return ((IMenuClickablePresenter) presenter).onMenuItemClick(id);
        }
        return false;
    }

    public void toast(@StringRes int text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    public void closeActivity() {
        getActivity().finish();
    }

    public void showLoading(int message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(getContext(), null, getString(message), true, false);
        }
    }

    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    protected abstract void inject();
}
