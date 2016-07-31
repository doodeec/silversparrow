package com.doodeec.silversparrow.dagger.module;

import android.util.Log;

import com.doodeec.silversparrow.dagger.scope.ApplicationScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DI module for network communication
 * provides network specific constructors for partial components
 * - GSON
 * - OkHttp client
 * - logging and header interceptors
 * - Retrofit instance
 *
 * @author Dusan Bartos
 */
@Module
public class NetModule {

    // base URL for server communication
    private String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides @ApplicationScope Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create();
    }

    @Provides @ApplicationScope HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return interceptor;
    }

    @Provides @ApplicationScope Interceptor provideHeaderInterceptor() {
        return chain -> {
            Request original = chain.request();
            Log.v("Request", original.url().toString());

            Request request = original.newBuilder()
                    //this is application/json by default
                    .header("content_type", "application/json")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

    @Provides @ApplicationScope OkHttpClient provideClient(HttpLoggingInterceptor interceptor/*,
                                                           Interceptor headersInterceptor*/) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //headers contain application/json by default, add this again if more overrides are necessary (i.e. access_token)
//        clientBuilder.addInterceptor(headersInterceptor);
        clientBuilder.addInterceptor(interceptor);
        return clientBuilder.build();
    }

    @Provides @ApplicationScope Retrofit provideRetro(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }
}
