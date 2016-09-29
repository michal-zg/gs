package pl.jw.android.gamescheduler.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import pl.jw.android.gamescheduler.R;

/**
 * Created by jacek on 2016-05-18.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName();

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

    public static SSLContext getSSLConfig(Context context) {

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            Certificate ca;
            try (InputStream cert = context.getResources().openRawResource(R.raw.cert);
                 InputStream certTest = context.getResources().openRawResource(R.raw.test_cert)) {


                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", cf.generateCertificate(cert));
                keyStore.setCertificateEntry("ca-test", cf.generateCertificate(certTest));

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                sslContext.init(null, tmf.getTrustManagers(), null);
            }


        } catch (Exception e) {
            sslContext = null;
            Log.e(TAG, "Error:", e);
        }
        return sslContext;
    }
}
