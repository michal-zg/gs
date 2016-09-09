package com.example.jacek.myapplication.rest;

import com.example.jacek.myapplication.data.Event;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by jacek on 2016-09-07.
 */
public interface RestApi {

    @POST("events")
    Observable<Event> addEvent(@Body Event data);

    @GET("events")
    Observable<List<Event>> getEvents();

}
