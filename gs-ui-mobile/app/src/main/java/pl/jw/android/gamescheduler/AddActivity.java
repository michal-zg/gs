package pl.jw.android.gamescheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.jw.android.gamescheduler.data.Event;
import pl.jw.android.gamescheduler.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddActivity extends AppCompatActivity {

    @Bind(R.id.add_cancel)
    Button buttonCancel;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.time)
    EditText time;

    @Bind(R.id.date)
    EditText date;

    private LocalDate selectedDate;
    private LocalTime selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);

        selectedTime = null;
        time.setClickable(true);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });
        selectedDate = null;
        date.setClickable(true);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateAndTime();
            }
        });

    }

    @OnClick(R.id.add_cancel)
    public void onCancel(View v) {
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
    }

    @OnClick(R.id.add_ok)
    public void onAdd(final View v) {

        if (validate()) {
            return;
        }

        DateTime date = LocalDateTime.now().withFields(selectedDate).withFields(selectedTime).toDateTime();
        Event event = new Event(GameSchedulerApplication.getInstance().getUser(), name.getText().toString(), date);

        GameSchedulerApplication.getInstance().getRestApi(this).addEvent(event).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
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

    private boolean validate() {
        boolean error = false;
        if (TextUtils.isEmpty(name.getText()) || name.getText().length() < 4) {
            name.setError(getString(R.string.error_field_required));
            error = true;
        }

        if (TextUtils.isEmpty(date.getText())) {
            date.setError(getString(R.string.error_field_required));
            error = true;
        }
        if (TextUtils.isEmpty(time.getText())) {
            time.setError(getString(R.string.error_field_required));
            error = true;
        }
        return error;
    }


    private void selectDateAndTime() {
        Util.DatePickerFragment.show(getFragmentManager(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate = LocalDate.now().withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);

                DateTimeFormatter format = DateTimeFormat.fullDate().withLocale(GameSchedulerApplication.getLocale(AddActivity.this));
                String date = selectedDate.toString(format);
                AddActivity.this.date.setText(date);

                selectTime();
            }
        });
    }

    private void selectTime() {
        Util.TimePickerFragment.show(getFragmentManager(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                selectedTime = LocalTime.MIDNIGHT.withHourOfDay(hourOfDay).withMinuteOfHour(minute);
                int millisOfDay = selectedTime.getMillisOfDay();
                String hourWithMinutes = DateUtils.formatDateTime(AddActivity.this, millisOfDay, DateUtils.FORMAT_SHOW_TIME);
                time.setText(hourWithMinutes);
            }
        });
    }

}
