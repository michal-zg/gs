package pl.jw.android.gamescheduler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import pl.jw.android.gamescheduler.data.Notification;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static pl.jw.android.gamescheduler.GameSchedulerApplication.NOTIFICATION_LAST_DATETIME;

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
 * <p/>
 * BootCompleteReceiverNotifications - zapewnia start po restarcie kompa, niezależnie od startu apki
 * MainActivity - startuje usługę manualnie po swoim starcie
 */
public class NotificationPullService extends IntentService {


    public static final String TAG = NotificationPullService.class.getName();

    public static final String NOTIFICATION_RECEIVED = "NOTIFICATION_RECEIVED";
    private static final String ACTION_PULL = "pl.jw.android.gamescheduler.action.notification.pull";
    public static final String NOTIFICATION_DATA = "NOTIFICATION_DATA";


    public NotificationPullService() {
        super("NotificationPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PULL.equals(action)) {

                Log.d(TAG, "Notification service - start.");

                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handle();
            }
        }
    }


    private void handle() {
        GameSchedulerApplication.getInstance().getRestApi().getLastNotification().subscribeOn(Schedulers.immediate()).
                observeOn(AndroidSchedulers.mainThread()).filter(new Func1<Notification, Boolean>() {

            @Override
            public Boolean call(Notification lastNotificationOnBackend) {
                boolean anyNotificationsOnBackend = lastNotificationOnBackend != null;

                Log.d(TAG, "Notification service - any on backend: " + anyNotificationsOnBackend + " last from date: " + (anyNotificationsOnBackend ? lastNotificationOnBackend.getDate() : "") + ".");

                return anyNotificationsOnBackend;
            }
        }).filter(new Func1<Notification, Boolean>() {
            @Override
            public Boolean call(Notification lastNotificationOnBackend) {

                SharedPreferences sharedPreferences = GameSchedulerApplication.getInstance().getSharedPreferences();
                DateTime lastNotificationOnBackendDate = lastNotificationOnBackend.getDate();
                try {
                    String lastProcessedNotificationDateTime = sharedPreferences.getString(NOTIFICATION_LAST_DATETIME, null);

                    boolean noNotificationsProcessed = lastProcessedNotificationDateTime == null;
                    boolean newNotificationsAvailable = noNotificationsProcessed ? true : lastNotificationOnBackendDate.isAfter(DateTime.parse(lastProcessedNotificationDateTime));

                    Log.d(TAG, "Notification service - any NEW on backend: " + newNotificationsAvailable + ".");

                    return newNotificationsAvailable;

                } finally {
                    //bezwzględnie zapisanie  - zapewnia obs dla sytuacji braku shared preference - pierwszej synchr., gdy są dane
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(NOTIFICATION_LAST_DATETIME, lastNotificationOnBackendDate.toString(ISODateTimeFormat.dateTime()));
                    editor.commit();
                }
            }
//        FIXME        chain requestu [pbierajacego notyfikacje
        }).subscribe(new Subscriber<Notification>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(this.getClass().getName(), "Błąd.", e);
            }

            @Override
            public void onNext(Notification lastNotificationOnBackend) {
                String lastProcessedNotificationDateTime = GameSchedulerApplication.getInstance().getSharedPreferences().getString(NOTIFICATION_LAST_DATETIME, null);


                String serverDateTime = DateTime.parse(lastProcessedNotificationDateTime).toString(ISODateTimeFormat.dateTime());

                Log.d(TAG, "Notification service - downloading NEW notification from date: " + serverDateTime + ".");

                GameSchedulerApplication.getInstance().getRestApi().getNotificationsFrom(serverDateTime).subscribeOn(Schedulers.newThread()).
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

                        Log.d(TAG, "Notification service - pulled: " + list + ".");

                        for (Notification notification : list) {
                            Intent intent = new Intent(NOTIFICATION_RECEIVED);
                            intent.putExtra(NOTIFICATION_DATA, notification);
                            LocalBroadcastManager.getInstance(NotificationPullService.this).sendBroadcast(intent);
                        }

                    }

                });
            }
        });
    }


    public static void start(Context context) {
        Intent intent = getIntent(context);
        context.startService(intent);
    }

    @NonNull
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NotificationPullService.class);
        intent.setClass(context, NotificationPullService.class);
        intent.setAction(ACTION_PULL);
//        intent.putExtra(EXTRA_PARAM1, param1);
        return intent;
    }


    public static IntentFilter getBroadcastIntentFilter() {
        return new IntentFilter(NotificationPullService.NOTIFICATION_RECEIVED);
    }
}
