package com.codepath.travelapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import static android.provider.Settings.System.getString;

@ParseClassName("Achievment")
public class Achievement extends ParseObject implements Parcelable {
    // Instance variables
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";

    //Getters
    public String getName() { return getString(KEY_NAME);}

    public ParseFile getImage() { return getParseFile(KEY_IMAGE);}

    public ParseRelation<User> getUser() { return getRelation(KEY_USER);}
}
