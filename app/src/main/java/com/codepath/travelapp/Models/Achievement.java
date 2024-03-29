package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Achievement")
public class Achievement extends ParseObject implements Serializable {
    // Instance variables
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DESCRIPTION = "description";

    //Getters
    public String getName() { return getString(KEY_NAME);}

    public ParseFile getImage() { return getParseFile(KEY_IMAGE);}

    public String getDescription() { return getString(KEY_DESCRIPTION);}

}
