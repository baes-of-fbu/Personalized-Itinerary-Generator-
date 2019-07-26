package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("cityImages")
public class CityImages extends ParseObject implements Parcelable {

    // Constants
    public static String KEY_CITY = "city";
    public static String KEY_IMAGE = "image";

    public CityImages() {}

    // Getters
    public City getCity() {
        return (City) getParseObject(KEY_CITY);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

}
