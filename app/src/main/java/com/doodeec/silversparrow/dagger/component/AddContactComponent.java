package com.doodeec.silversparrow.dagger.component;

import android.content.Context;

import com.doodeec.silversparrow.dagger.module.ContextModule;
import com.doodeec.silversparrow.dagger.scope.ActivityScope;
import com.doodeec.silversparrow.ui.contact.add.AddContactPresenter;

import dagger.Component;

/**
 * @author Dusan Bartos
 */
@ActivityScope
@Component(modules = ContextModule.class, dependencies = AppComponent.class)
public interface AddContactComponent {
    AddContactPresenter presenter();

    Context context();
}
