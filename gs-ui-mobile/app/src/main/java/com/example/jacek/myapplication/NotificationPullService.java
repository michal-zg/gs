package com.example.jacek.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.jacek.myapplication.data.Notification;

import org.joda.time.DateTime;

import java.util.List;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.example.jacek.myapplication.GameSchedulerApplication.NOTIFICATION_LAST_DATETIME;

/**
 * TODO:
 * uwzgledniane zmiany danych :
 * - dodanie event
 * - zapis/wypis innych graczy
 * - zmiana godziny
 * - propozycje innej godziny
 * <p/>
 * zmiana danych -> backend -> backend generuje notyfikacje ->
 * klienci pulluja je co okreslony czas,
 * - po dodaniu eventu czas sie zmniejsza -  bedzie aktywnosc
 * - wydluzenie czasu - po wydarzeniu sie eventu /
 */
public class NotificationPullService extends IntentService {
    public static final String NOTIFICATION_RECEIVED = "NOTIFICATION_RECEIVED";
    private static final String ACTION_PULL = "com.example.jacek.myapplication.action.notification.pull";
    public static final String NOTIFICATION_DATA = "NOTIFICATION_DATA";


    public NotificationPullService() {
        super("NotificationPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PULL.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handle();
            }
        }
    }


    private void handle() {
        GameSchedulerApplication.getInstance().getRestApi().getLastNotificationDateTime().subscribeOn(Schedulers.immediate()).
                observeOn(AndroidSchedulers.mainThread()).filter(new Func1<DateTime, Boolean>() {

            @Override
            public Boolean call(DateTime lastNotificationOnBackendDateTime) {
                boolean noNotifications = lastNotificationOnBackendDateTime == null;

                return !noNotifications;
            }
        }).filter(new Func1<DateTime, Boolean>() {
            @Override
            public Boolean call(DateTime lastNotificationOnBackendDateTime) {

                SharedPreferences sharedPreferences = GameSchedulerApplication.getInstance().getSharedPreferences();
                try {
                    String lastProcessedNotificationDateTime = sharedPreferences.getString(NOTIFICATION_LAST_DATETIME, null);

                    boolean noNotificationsProcessed = lastProcessedNotificationDateTime == null;
                    boolean newNotificationsAvailable = noNotificationsProcessed ? true : lastNotificationOnBackendDateTime.isAfter(DateTime.parse(lastProcessedNotificationDateTime));
                    return newNotificationsAvailable;

                } finally {
                    //bezwzględnie zapisanie  - zapewnia obs dla sytuacji braku shared preference - pierwszej synchr., gdy są dane
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(NOTIFICATION_LAST_DATETIME, lastNotificationOnBackendDateTime.toString());
                    editor.commit();
                }
            }
        }).
//        FIXME        chain requestu [pbierajacego notyfikacje

        subscribe(new Subscriber<DateTime>() {
    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Log.d(this.getClass().getName(), "Błąd.", e);
    }

    @Override
    public void onNext(DateTime lastNotificationOnBackendDateTime) {
        String lastProcessedNotificationDateTime = GameSchedulerApplication.getInstance().getSharedPreferences().getString(NOTIFICATION_LAST_DATETIME, null);

        GameSchedulerApplication.getInstance().getRestApi().getNotificationsFrom(DateTime.parse(lastProcessedNotificationDateTime).toString("")).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Notification>>() {
            @Override
            public void onCompleted() {

            }


            @Override
            public void onError(Throwable e) {
                Log.d(this.getClass().getName(), "Błąd.", e);
            }

            @Override
            public void onNext(List<Notification> list) {

                for (Notification notification :
                        list) {
                    Intent intent = new Intent(NOTIFICATION_RECEIVED);
                    intent.putExtra(NOTIFICATION_DATA, notification);
                    LocalBroadcastManager.getInstance(NotificationPullService.this
                    ).sendBroadcast(intent);
                }

            }

        });
    }
});
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationPullService.class);
        intent.setAction(ACTION_PULL);
//        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }


    public static IntentFilter getBroadcastIntentFilter() {
        return new IntentFilter(NotificationPullService.NOTIFICATION_RECEIVED);
    }
}
