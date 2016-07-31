package com.doodeec.silversparrow.dagger.module;

import android.app.Application;
import android.content.res.Resources;

import com.doodeec.silversparrow.dagger.scope.ApplicationScope;
import com.doodeec.silversparrow.data.provider.DataProvider;
import com.doodeec.silversparrow.data.provider.ServiceProvider;
import com.doodeec.silversparrow.data.provider.StorageManager;
import com.doodeec.silversparrow.data.storage.DBHelper;
import com.doodeec.silversparrow.network.RestService;

import dagger.Module;
import dagger.Provides;

/**
 * Base DI Application module
 *
 * @author Dusan Bartos
 */
@Module(includes = {ApiModule.class})
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides @ApplicationScope Application providesApplication() {
        return application;
    }

    @Provides @ApplicationScope Resources providesResources() {
        return application.getResources();
    }

    @Provides @ApplicationScope DBHelper providesDBHelper() {
        return new DBHelper(application);
    }

    @Provides @ApplicationScope StorageManager providesStorage(DBHelper dbHelper) {
        return new StorageManager(dbHelper);
    }

    @Provides @ApplicationScope DataProvider providesDataProvider(StorageManager storageManager,
                                                                  ServiceProvider serviceProvider) {
        return new DataProvider(storageManager, serviceProvider);
    }

    @Provides @ApplicationScope ServiceProvider providesAuthProvider(RestService restService) {
        return new ServiceProvider(restService);
    }
}
