package com.doodeec.silversparrow.dagger.module;

import com.doodeec.silversparrow.dagger.scope.ApplicationScope;
import com.doodeec.silversparrow.network.RestService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * This module provides REST API interface (either mock or real one)
 *
 * @author Dusan Bartos
 */
@Module(includes = NetModule.class)
public class ApiModule {

    @Provides @ApplicationScope RestService provideApiInterface(Retrofit retrofit) {
        return retrofit.create(RestService.class);
    }
}
