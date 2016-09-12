package com.example.jacek.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by jacek on 2016-09-17.
 */
public class BootCompleteReceiverNotifications extends BroadcastReceiver {


    public static final String TAG = BootCompleteReceiverNotifications.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO obsluga sleep - lockowanie i wakeup - wake lock
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntentNotificationService = PendingIntent.getService(context, 0, NotificationPullService.getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
            //TODO: zmiana interwalu w przypadku ozywienia sie graczy lub ich deaktywowania
            double repeatIntervalMillis = GameSchedulerApplication.getInstance().getPropertyValueInteger(GameSchedulerApplication.PROPERTY_NOTIFICATIONS_PERIOD_MINUTES) * 60 * 1000;

            int repeatInterval = (int) repeatIntervalMillis;
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Calendar.getInstance().getTimeInMillis(), repeatInterval, pendingIntentNotificationService);

            Log.d(TAG, "Repeatable alarm added for notification service with interval: "+repeatInterval+" [ms].");
        }
    }
}
