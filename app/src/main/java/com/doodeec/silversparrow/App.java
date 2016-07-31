package com.doodeec.silversparrow;

import android.app.Application;

import com.doodeec.silversparrow.dagger.component.AppComponent;
import com.doodeec.silversparrow.dagger.component.DaggerAppComponent;
import com.doodeec.silversparrow.dagger.module.AppModule;
import com.doodeec.silversparrow.dagger.module.NetModule;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class App extends Application {

    private static AppComponent appComponent;

    private Thread.UncaughtExceptionHandler defaultUEH;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(AppConfig.BASE_SERVER_URL))
                .build();

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            ex.printStackTrace();
            Timber.e(ex, "Unhandled exception");
            defaultUEH.uncaughtException(thread, ex);
        });

        // configure picasso
        final Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener((picasso, uri, exception) -> Timber.e("Error loading image %s : %s", uri, exception.getMessage()));
        Picasso.setSingletonInstance(builder.build());

        //init logs
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //init fabric - crashlytics
        //TODO set crashlytics key and uncomment its definition in manifest
        /*if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }*/
    }

    @Override
    public void onLowMemory() {
        Timber.d("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.d("onTrimMemory - %s", level);
        super.onTrimMemory(level);
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
