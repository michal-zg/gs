package pl.jw.android.gamescheduler.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by jacek on 2016-05-18.
 */
public class UtilGui {

    public static class DatePickerFragment extends DialogFragment {

        public static final String TAG = DatePickerFragment.class.getSimpleName();

        private DatePickerDialog.OnDateSetListener listener;


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);

            return new DatePickerDialog(getActivity(), listener, tomorrow.getYear(), tomorrow.getMonthOfYear() - 1, tomorrow.getDayOfMonth());
        }

        public static void show(FragmentManager manager, DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.listener = listener;

            newFragment.show(manager, TAG);
        }

    }

    public static class TimePickerFragment extends DialogFragment {
        public static final String TAG = TimePickerFragment.class.getSimpleName();

        private TimePickerDialog.OnTimeSetListener listener;


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LocalTime fiveOClock = LocalTime.MIDNIGHT.withHourOfDay(17);

            return new TimePickerDialog(getActivity(), listener, fiveOClock.getHourOfDay(), fiveOClock.getMinuteOfHour(), true);
        }

        public static void show(FragmentManager manager, TimePickerDialog.OnTimeSetListener listener) {
            TimePickerFragment newFragment = new TimePickerFragment();
            newFragment.listener = listener;
            newFragment.show(manager, TAG);
        }
    }
}
