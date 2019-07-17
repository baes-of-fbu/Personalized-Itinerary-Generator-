package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Tags")
public class Tag extends ParseObject {

    // Instance variables
    private static final String KEY_NAME = "name";

    public String getName() {
        return getString(KEY_NAME);
    }
}
