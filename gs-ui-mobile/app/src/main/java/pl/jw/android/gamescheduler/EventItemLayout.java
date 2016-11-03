package pl.jw.android.gamescheduler;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.jw.android.gamescheduler.data.Event;
import pl.jw.android.gamescheduler.data.User;
import pl.jw.android.gamescheduler.util.ArrayAdapterItemWrapper;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jacek on 2016-04-10.
 */
public class EventItemLayout extends LinearLayout {

    private static final String TAG = EventItemLayout.class.getSimpleName();

    @Bind(R.id.eventEditTextLabelName)
    TextView editTextLabelName;
    @Bind(R.id.eventEditTextLabelDateTime)
    TextView editTextLabelDateTime;

    @Bind(R.id.eventTextViewLabelConfirmed)
    TextView textViewLabelConfirmed;
    @Bind(R.id.eventListViewConfirmed)
    ListView listViewConfirmed;

    @Bind(R.id.eventTextViewLabelRejected)
    TextView textViewLabelRejected;
    @Bind(R.id.eventListViewRejected)
    ListView listViewRejected;

    @Bind(R.id.eventButtonConfirm)
    Button buttonConfirm;
    @Bind(R.id.eventButtonReject)
    Button buttonReject;

    private ArrayAdapterItemWrapper<Event> data;

    public EventItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(ArrayAdapterItemWrapper<Event> itemWrapper) {
        this.data = itemWrapper;

        Event data = ArrayAdapterItemWrapper.unwrap(itemWrapper);

        editTextLabelName.setText(data.name);

        String dateTimeFormatted = DateUtils.formatDateTime(getContext(), data.date.getMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME);
        editTextLabelDateTime.setText(dateTimeFormatted);

        listViewConfirmed.setAdapter(new UserArrayAdapter(data.accountsConfirmed));
        listViewRejected.setAdapter(new UserArrayAdapter(data.accountsRejected));

        User user = GameSchedulerApplication.getInstance().getUser();
        boolean confirmed = data.accountsConfirmed.contains(user);
        boolean rejected = data.accountsRejected.contains(user);

        //obie opcje jeśli user się jeszcze nie okreśił
        boolean undefined = confirmed == false && rejected == false;

        //jeśli się określił - dostępna jedynie opcja pozwalająca zmienić zdanie
        int buttonConfirmVisibility = undefined || confirmed ? View.INVISIBLE : View.VISIBLE;
        int buttonRejectVisibility = undefined || rejected ? View.INVISIBLE : View.VISIBLE;

        buttonConfirm.setVisibility(buttonConfirmVisibility);
        buttonReject.setVisibility(buttonRejectVisibility);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

    }

    @OnClick(R.id.eventButtonConfirm)
    void confirm() {

        // TODO - ikona czekania

        String id = ArrayAdapterItemWrapper.<Event>unwrap(data).id;
        GameSchedulerApplication.getInstance()
                .getRestApi(this.getContext()).confirm(id, GameSchedulerApplication.getInstance().getUserName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Event>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Błąd.", e);
            }

            @Override
            public void onNext(Event modifiedData) {

                data.update(modifiedData);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(MainActivity.INTENT_ROW_DATA_CHANGE);
            }
        });

    }

    @OnClick(R.id.eventButtonReject)
    void reject() {

        String id = ArrayAdapterItemWrapper.<Event>unwrap(data).id;
        GameSchedulerApplication.getInstance()
                .getRestApi(this.getContext()).reject(id, GameSchedulerApplication.getInstance().getUserName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Event>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Błąd.", e);
            }

            @Override
            public void onNext(Event modifiedData) {

                data.update(modifiedData);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(MainActivity.INTENT_ROW_DATA_CHANGE);

            }
        });
    }

    private class UserArrayAdapter extends ArrayAdapter<User> {
        public UserArrayAdapter(List<User> data) {
            super(EventItemLayout.this.getContext(), R.layout.list_accounts_row, R.id.accountName, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            UserItemLayout tli;
            if (null == convertView) {
                tli = (UserItemLayout) View.inflate(getContext(), R.layout.list_accounts_row, null);
            } else {
                tli = (UserItemLayout) convertView;
            }

            tli.setData(getItem(position));

            return tli;
        }
    }
}
