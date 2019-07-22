package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DayPlan")
public class Dayplan extends ParseObject {

    private static String KEY_MORNING_EVENT = "morning_event";
    private static String KEY_AFTERNOON_EVENT = "afternoon_event";
    private static String KEY_EVENING_EVENT = "evening_event";

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
}
