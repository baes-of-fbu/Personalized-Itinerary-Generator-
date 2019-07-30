package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("City")
public class City extends ParseObject implements Parcelable {

    // Constants
    public static String KEY_NAME = "name";
    private static String KEY_STATE = "state";
    private static String KEY_DESCRIPTION = "description";
    private static String KEY_LOCATION = "location";
    private static String KEY_IMAGE = "coverPhoto";

    public City() {}

    // Getters and setter
    public String getName() {
        return getString(KEY_NAME);
    }

    public String getState() {
        return getString(KEY_STATE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }
}
