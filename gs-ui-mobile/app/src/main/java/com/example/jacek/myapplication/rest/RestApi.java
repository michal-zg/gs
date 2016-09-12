package com.example.jacek.myapplication.rest;

import com.example.jacek.myapplication.data.Event;
import com.example.jacek.myapplication.data.Notification;
import com.example.jacek.myapplication.data.User;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
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


    @POST("events/{eventId}/account/{userName}")
    Observable<User> confirm(@Path("eventId") String eventId, @Path("userName") String userName);

    @DELETE("events/{eventId}/account/{userName}")
    Observable<User> reject(@Path("eventId") String eventId, @Path("userName") String userName);

    @GET("notifications/last")
    Observable<Notification> getLastNotification();

    @GET("notifications/{date}")
    Observable<List<Notification>> getNotificationsFrom(@Path("date") String serverDateTime);
}
