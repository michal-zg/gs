package com.example.jacek.myapplication.rest;

import com.example.jacek.myapplication.data.Event;
import com.example.jacek.myapplication.data.Notification;

import org.joda.time.DateTime;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by jacek on 2016-09-07.
 */
public interface RestApi {

    @POST("events")
    Observable<Event> addEvent(@Body Event data);

    @GET("events")
    Observable<List<Event>> getEvents();

    @GET("notifications/last")
    Observable<DateTime> getLastNotificationDateTime();

    @GET("notifications/{date}")
    Observable<List<Notification>> getNotificationsFrom(@Path("date") String serverDateTime);

}
