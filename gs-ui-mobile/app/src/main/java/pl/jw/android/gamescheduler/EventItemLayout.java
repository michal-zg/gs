package pl.jw.android.gamescheduler;

import android.app.Notification;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.jw.android.gamescheduler.data.Event;
import pl.jw.android.gamescheduler.data.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jacek on 2016-04-10.
 */
public class EventItemLayout extends LinearLayout {

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

    private Event data;

    public EventItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(Event data) {
        this.data = data;


        //TODO: organiztor, odswiezanie po zmianie
        editTextLabelName.setText(data.name);

        String dateTimeFormatted = DateUtils.formatDateTime(getContext(), data.date.getMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        editTextLabelDateTime.setText(dateTimeFormatted);

        listViewConfirmed.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, data.accountsConfirmed));
        listViewRejected.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, data.accountsRejected));

        String userName = GameSchedulerApplication.getInstance().getUserName();
        boolean confirmed = data.accountsConfirmed.contains(userName);
        boolean rejected = data.accountsRejected.contains(userName);

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

        GameSchedulerApplication.getInstance().getRestApi().confirm(data.id, GameSchedulerApplication.getInstance().getUserName()).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                Notification.Builder noti = new Notification.Builder(EventItemLayout.this.getContext())
                        .setContentTitle("Potwierdziłeś obecność")
                        .setContentText("Data rozgrywki: " + data.date);

                GameSchedulerApplication.getInstance().notificationShow(EventItemLayout.this.getContext(), noti);
            }
        });

    }

    @OnClick(R.id.eventButtonReject)
    void reject() {

        GameSchedulerApplication.getInstance().getRestApi().reject(data.id, GameSchedulerApplication.getInstance().getUserName()).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                Notification.Builder noti = new Notification.Builder(EventItemLayout.this.getContext())
                        .setContentTitle("Anulowałeś obecność")
                        .setContentText("Data rozgrywki: " + data.date);

                GameSchedulerApplication.getInstance().notificationShow(EventItemLayout.this.getContext(), noti);
            }
        });
    }
}