package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("DayPlan")
public class DayPlan extends ParseObject implements Parcelable {

    private static String KEY_MORNING_EVENT = "morning_event";
    private static String KEY_AFTERNOON_EVENT = "afternoon_event";
    private static String KEY_EVENING_EVENT = "evening_event";
    public static String KEY_DATE = "date";
    public static String KEY_TRIP = "trip";

    public DayPlan() {}

    public Event getMorningEvent() {
        return (Event) getParseObject(KEY_MORNING_EVENT);
    }

    public void setMorningEvent(Event event) {
        put(KEY_MORNING_EVENT, event);
    }

    public Event getAfternoonEvent() {
        return (Event) getParseObject(KEY_AFTERNOON_EVENT);
    }

    public void setAfternoonEvent(Event event) {
        put(KEY_AFTERNOON_EVENT, event);
    }

    public Event getEveningEvent() {
        return (Event) getParseObject(KEY_EVENING_EVENT);
    }

    public void setEveningEvent(Event event) {
        put(KEY_EVENING_EVENT, event);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public Trip getTrip() {
        return (Trip) getParseObject(KEY_TRIP);
    }

    public void setTrip(Trip trip) {
        put(KEY_TRIP, trip);
    }
}
