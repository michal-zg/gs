package com.example.jacek.myapplication.util;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;

import java.lang.reflect.Field;

/**
 * Created by jacek on 2016-05-18.
 */
public class UtilGui {

    public static void setupMonthPicker(DatePicker dp) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0) {
                View yearSpinner = dp.findViewById(yearSpinnerId);
                if (yearSpinner != null) {
                    yearSpinner.setVisibility(View.GONE);
                }
            }
        } else {
            Field f[] = dp.getClass().getDeclaredFields();
            for (Field field : f) {

                if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(dp);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) yearPicker).setVisibility(View.GONE);
                }
            }
        }
    }
}
