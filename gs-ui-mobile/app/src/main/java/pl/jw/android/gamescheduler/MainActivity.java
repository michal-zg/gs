package pl.jw.android.gamescheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.jw.android.gamescheduler.data.Event;
import pl.jw.android.gamescheduler.data.Notification;
import pl.jw.android.gamescheduler.util.ArrayAdapterItemWrapper;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

//    @Bind({ R.id.title, R.id.subtitle, R.id.hello })
//    List<View> headerViews;

    static final Intent INTENT_ROW_DATA_CHANGE = new Intent("INTENT_ROW_DATA_CHANGE");

    private class DataChangeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.filterEquals(INTENT_ROW_DATA_CHANGE)) {
                listViewEventsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Bind(R.id.listViewEvents)
    ListView listViewEvents;

    private ArrayAdapter<ArrayAdapterItemWrapper<Event>> listViewEventsAdapter;
    private BroadcastReceiverNotification receiverNotification;

    private DataChangeBroadcastReceiver receiverRowDataChange = new DataChangeBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        listViewEventsAdapter = new ArrayAdapter<ArrayAdapterItemWrapper<Event>>(MainActivity.this, R.layout.list_events_row, new ArrayList<ArrayAdapterItemWrapper<Event>>()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                EventItemLayout tli;
                if (null == convertView) {
                    tli = (EventItemLayout) View.inflate(getContext(), R.layout.list_events_row, null);
                } else {
                    tli = (EventItemLayout) convertView;
                }

                tli.setData(getItem(position));

                return tli;
            }
        };

        listViewEvents.setAdapter(listViewEventsAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();


        receiverNotification = new BroadcastReceiverNotification();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverNotification, NotificationPullService.getBroadcastIntentFilter());
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverRowDataChange, new IntentFilter(INTENT_ROW_DATA_CHANGE.getAction()));

        NotificationPullService.start(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverNotification);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverRowDataChange);
    }

    @Override
    protected void onResume() {

        GameSchedulerApplication.getInstance().getRestApi(this).getEvents().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Event>>() {
            @Override
            public void onCompleted() {
                Snackbar.make(listViewEvents, "Pobrano", Snackbar.LENGTH_LONG)
                        .setAction("Pobrano", null).show();

            }

            @Override
            public void onError(Throwable e) {
                Log.d(this.getClass().getName(), "Błąd.", e);
            }

            @Override
            public void onNext(List<Event> listData) {


                listViewEventsAdapter.clear();
                listViewEventsAdapter.addAll(ArrayAdapterItemWrapper.wrap(listData));
                listViewEventsAdapter.notifyDataSetChanged();
            }
        });


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            GameSchedulerApplication.getInstance().saveLoggedInUser(null);

            startActivity(new Intent(this, LoginActivity.class));

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BroadcastReceiverNotification extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Notification notification = (Notification) intent.getSerializableExtra(NotificationPullService.NOTIFICATION_DATA);

            //TODO: nie pokazywac userowi ktory jest zrodlem notyfikacji

            android.app.Notification.Builder noti = new android.app.Notification.Builder(MainActivity.this)
                    .setContentTitle(notification.getSubject())
                    .setContentText(notification.getMessage());

            GameSchedulerApplication.getInstance().notificationShow(context, noti);
        }
    }

}
