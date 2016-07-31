package com.doodeec.silversparrow.dagger.component;

import android.app.Application;
import android.content.res.Resources;

import com.doodeec.silversparrow.dagger.module.ApiModule;
import com.doodeec.silversparrow.dagger.module.AppModule;
import com.doodeec.silversparrow.dagger.scope.ApplicationScope;
import com.doodeec.silversparrow.data.provider.DataProvider;
import com.doodeec.silversparrow.data.provider.ServiceProvider;
import com.doodeec.silversparrow.data.provider.StorageManager;
import com.doodeec.silversparrow.data.storage.DBHelper;
import com.doodeec.silversparrow.network.RestService;

import dagger.Component;

/**
 * DI component
 *
 * @author Dusan Bartos
 */
@ApplicationScope
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {

    DataProvider dataProvider();

    ServiceProvider authProvider();

    StorageManager storageManager();

    Resources resources();

    Application application();

    DBHelper dbHelper();

    RestService restService();
}
