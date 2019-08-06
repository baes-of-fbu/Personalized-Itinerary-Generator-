package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;


@ParseClassName("Tags")
public class Tag extends ParseObject implements Parcelable {

    // Instance variables
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_EVENTS = "events";

    public Tag() {}

    // Getters
    public String getName() {
        return getString(KEY_NAME);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseRelation<Event> getEventsRelation() {
        return getRelation(KEY_EVENTS);
    }
}
