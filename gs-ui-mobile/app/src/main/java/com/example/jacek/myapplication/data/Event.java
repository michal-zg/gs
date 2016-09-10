package com.example.jacek.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by jacek on 2016-04-09.
 */
public class Event {

    public Event(){
    }

    public Event(String creator, String name) {
        this.creator = creator;
        this.name = name;
    }
    @SerializedName("_id")
    public String id;
    public String creator;
    public String name;
    public Date date;

    public List<String> accountsRejected;
    public List<String> accountsConfirmed;

    public EventStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (creator != null ? !creator.equals(event.creator) : event.creator != null) return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (accountsRejected != null ? !accountsRejected.equals(event.accountsRejected) : event.accountsRejected != null)
            return false;
        if (accountsConfirmed != null ? !accountsConfirmed.equals(event.accountsConfirmed) : event.accountsConfirmed != null)
            return false;
        return status == event.status;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (accountsRejected != null ? accountsRejected.hashCode() : 0);
        result = 31 * result + (accountsConfirmed != null ? accountsConfirmed.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

//    public String host;
    //    public boolean open;
//
//    public Date date;
//
//    public List<Account> accountsConfirmed = new ArrayList<>();
//    public List<Account> accountsRejected = new ArrayList<>();
//
//    public String name;

//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Event event = (Event) o;
//        return open == event.open &&
//                Objects.equals(host, event.host) &&
//                Objects.equals(date, event.date) &&
//                Objects.equals(accountsConfirmed, event.accountsConfirmed) &&
//                Objects.equals(accountsRejected, event.accountsRejected) &&
//                Objects.equals(name, event.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(host, open, date, accountsConfirmed, accountsRejected, name);
//    }
}
