package com.doodeec.silversparrow.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Dusan Bartos
 */
public abstract class RxMvpBasePresenter<P extends BaseMvpView> extends MvpBasePresenter<P> {

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        subscriptions.clear();
    }

    protected void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    protected void showError(String message) {
        if (isViewAttached()) {
            getView().toast(message);
        }
    }
}
