package com.doodeec.silversparrow.dagger.component;

import android.content.Context;

import com.doodeec.silversparrow.dagger.module.ContextModule;
import com.doodeec.silversparrow.dagger.scope.ActivityScope;
import com.doodeec.silversparrow.ui.contact.detail.ContactDetailPresenter;

import dagger.Component;

/**
 * @author Dusan Bartos
 */
@ActivityScope
@Component(modules = ContextModule.class, dependencies = AppComponent.class)
public interface ContactDetailComponent {
    ContactDetailPresenter presenter();

    Context context();
}
