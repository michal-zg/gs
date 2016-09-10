package com.example.jacek.myapplication.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacek on 2016-09-09.
 */
public enum EventStatus {

    @SerializedName("new") NEW,  @SerializedName("canceled") CANCELED;
}
