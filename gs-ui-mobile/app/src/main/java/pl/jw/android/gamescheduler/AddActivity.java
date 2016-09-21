package pl.jw.android.gamescheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.jw.android.gamescheduler.data.Event;
import pl.jw.android.gamescheduler.util.UtilGui;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddActivity extends AppCompatActivity {

    @Bind(R.id.add_cancel)
    Button buttonCancel;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.date)
    TextView date;

    @OnClick(R.id.add_cancel)
    public void onCancel(View v) {
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
    }

    @OnClick(R.id.add_ok)
    public void onAdd(final View v) {

        validate();

        Event event = new Event(GameSchedulerApplication.getInstance().getUserName(), name.getText().toString());

        GameSchedulerApplication.getInstance().getRestApi().addEvent(event).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Snackbar.make(v, "Dodano", Snackbar.LENGTH_LONG).setAction("Dodano", null).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(this.getClass().getName(), "Błąd.", e);
            }

            @Override
            public void onNext(String id) {

                finish();
            }
        });


    }

    private void validate() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateAndTime();
            }
        });
    }

    private void selectDateAndTime() {
        UtilGui.DatePickerFragment.show(getFragmentManager(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                LocalDate localDate = LocalDate.now().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);

                DateTimeFormatter format = DateTimeFormat.fullDate().withLocale(GameSchedulerApplication.getLocale(AddActivity.this));
                String date = localDate.toString(format);
                AddActivity.this.date.setText(date);

                selectTime();
            }
        });
    }

    private void selectTime() {
        UtilGui.TimePickerFragment.show(getFragmentManager(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                int millisOfDay = LocalTime.MIDNIGHT.withHourOfDay(hourOfDay).withMinuteOfHour(minute).getMillisOfDay();
                String hourWithMinutes = DateUtils.formatDateTime(AddActivity.this, millisOfDay, DateUtils.FORMAT_SHOW_TIME);
                time.setText(hourWithMinutes);
            }
        });
    }

}
