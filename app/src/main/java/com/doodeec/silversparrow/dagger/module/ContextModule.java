package com.doodeec.silversparrow.dagger.module;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.doodeec.silversparrow.dagger.scope.ActivityScope;

import dagger.Provides;

/**
 * @author Dusan Bartos
 */
@dagger.Module
public class ContextModule {
    Fragment fragment;

    public ContextModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides @ActivityScope Context provideContext() {
        return fragment.getContext();
    }
}