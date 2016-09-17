package com.example.jacek.myapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserManager;
import android.util.Log;

import com.example.jacek.myapplication.rest.RestApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

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


    public static final String NOTIFICATION_LAST_DATETIME = "NOTIFICATION_LAST_DATETIME";

    public static String REST_API_BASE_URL_PROPERTY = "backend.baseurl";

    private static GameSchedulerApplication INSTANCE;

    private Properties properties = new Properties();

    @Override
    public void onCreate() {
        JodaTimeAndroid.init(this);

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

//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(properties.getProperty(REST_API_BASE_URL_PROPERTY))
                .addConverterFactory(GsonConverterFactory.create(/*gson*/))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//createWithScheduler(Schedulers.io())
                .build();
        return retrofit.create(RestApi.class);
    }

    public String getUserName() {
        //TODO: dodac kolekcje aliasow i endpoint  pochodzacych z combo odpalanego z ustawien/ przy pierwszym oidpaleniu
        String name = "<nierozpoznany>";

        AccountManager accountManager = AccountManager.get(this);
        for (Account account : accountManager.getAccountsByType("com.google")) {

            name = account.name;
            Log.i(getClass().getName(), "User name by google account: \"" + account.name + "\"");
        }
        return name;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("application-state.properties", Context.MODE_PRIVATE);
    }

    public static GameSchedulerApplication getInstance() {
        return INSTANCE;
    }
}
