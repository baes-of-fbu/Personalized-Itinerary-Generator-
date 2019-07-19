package com.codepath.travelapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;


@ParseClassName("Tags")
public class Tag extends ParseObject implements Parcelable {

    // Instance variables
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";

    public Tag() {};

    public String getName() {
        return getString(KEY_NAME);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
}
