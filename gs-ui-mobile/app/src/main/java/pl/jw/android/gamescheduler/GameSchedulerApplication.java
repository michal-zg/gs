package pl.jw.android.gamescheduler;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;

import pl.jw.android.gamescheduler.rest.RestApi;
import pl.jw.android.gamescheduler.util.GsonConverters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Locale;
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

    private static final int NOTIFICATION_ID_EVENT_CHANGED = 75547;

    public static String PROPERTY_REST_API_BASE_URL = "backend.baseurl";

    public static String PROPERTY_NOTIFICATIONS_PERIOD_MINUTES = "notifications.pull.period";

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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new GsonConverters.DateTimeTypeConverter());
        Gson gson = gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(getPropertyValue(PROPERTY_REST_API_BASE_URL))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//createWithScheduler(Schedulers.io())
                .build();
        return retrofit.create(RestApi.class);
    }

    public String getPropertyValue(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public Double getPropertyValueInteger(String propertyName) {
        return Double.valueOf(getPropertyValue(propertyName));
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

    public void notificationShow(Context context, Notification.Builder noti) {

        noti.setSmallIcon(R.drawable.ic_airline_seat_recline_extra)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.game_of_thrones_board))
                .setPriority(android.app.Notification.PRIORITY_MAX);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID_EVENT_CHANGED, noti.build());
    }


    public static GameSchedulerApplication getInstance() {
        return INSTANCE;
    }

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }
}