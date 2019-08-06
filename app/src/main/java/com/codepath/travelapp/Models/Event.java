package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable {

    // Constants
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "eventImage";
    private static final String KEY_COST = "cost";
    private static final String KEY_RATING = "rating";
    private static final String KEY_MORNING = "morning";
    private static final String KEY_AFTERNOON = "afternoon";
    private static final String KEY_EVENING = "evening";
    private static final String KEY_CITY = "city";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_WEBSITE = "website";

    // Getters
    public String getName() {
        return getString(KEY_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Number getCost() {
        return getNumber(KEY_COST);
    }

    public Number getRating() {
        return getNumber(KEY_RATING);
    }

    public Boolean isAvailableMorning() {
        return getBoolean(KEY_MORNING);
    }

    public Boolean isAvailableAfternoon() {
        return getBoolean(KEY_AFTERNOON);
    }

    public Boolean isAvailableEvening() {
        return getBoolean(KEY_EVENING);
    }

    public ParseObject getCity() {
        return getParseObject(KEY_CITY);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public String getWebsite() { return getString(KEY_WEBSITE); }
}
