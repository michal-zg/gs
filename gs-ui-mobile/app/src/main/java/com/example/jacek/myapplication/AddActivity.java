package com.example.jacek.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.jacek.myapplication.data.Event;
import com.example.jacek.myapplication.util.UtilGui;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddActivity extends AppCompatActivity {

    @Bind(R.id.add_cancel)
    Button buttonCancel;

    @Bind(R.id.timePicker)
    TimePicker timePicker;

    @Bind(R.id.datePicker)
    DatePicker datePicker;

    @OnClick(R.id.add_cancel)
    public void onCancel(View v) {
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
    }

    @OnClick(R.id.add_ok)
    public void onAdd(final View v) {


        Event event = new Event( GameSchedulerApplication.getInstance().getUserName(),"planszówki");

        GameSchedulerApplication.getInstance().getRestApi().addEvent(event).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Event>() {
            @Override
            public void onCompleted() {
                Snackbar.make(v, "Dodano", Snackbar.LENGTH_LONG)
                        .setAction("Dodano", null).show();

            }

            @Override
            public void onError(Throwable e) {
                Log.d(this.getClass().getName(), "Błąd.", e);
            }

            @Override
            public void onNext(Event integer) {

                finish();
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);

        UtilGui.setupMonthPicker(datePicker);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(17);
        timePicker.setCurrentMinute(00);

    }
}
