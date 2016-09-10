package com.example.jacek.myapplication;

import android.app.Application;
import android.util.Log;

import com.example.jacek.myapplication.rest.RestApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jacek on 2016-09-09.
 */
public class GameSchedulerApplication extends Application {


    public static String REST_API_BASE_URL_PROPERTY = "backend.baseurl";

    private static GameSchedulerApplication INSTANCE;

    private Properties properties = new Properties();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            properties.load(getAssets().open("application.properties"));
        } catch (IOException e) {
            Log.d(getClass().getName(), "", e);
        }

        INSTANCE = this;
    }

    public RestApi getRestApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(properties.getProperty(REST_API_BASE_URL_PROPERTY))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//createWithScheduler(Schedulers.io())
                .build();
        return retrofit.create(RestApi.class);
    }

    public String getUserName(){
        //TODO: pobierać
        return "jacek";
    }

    public static GameSchedulerApplication getInstance() {
        return INSTANCE;
    }
}
