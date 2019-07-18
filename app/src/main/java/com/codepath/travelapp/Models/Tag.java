package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Tags")
public class Tag extends ParseObject {

    // Instance variables
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";

    public String getName() {
        return getString(KEY_NAME);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
}
