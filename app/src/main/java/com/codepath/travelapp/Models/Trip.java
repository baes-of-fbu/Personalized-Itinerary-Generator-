package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Trip") // Needs to be same name as class on Parse website
public class Trip extends ParseObject {

    // Instance variables
    public static final String KEY_NAME = "name";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_BUDGET = "budget";
    public static final String KEY_CITY = "city";
    public static final String KEY_NUMDAYS = "numDays";
    public static final String KEY_STARTDATE = "startDate";
    public static final String KEY_ENDDATE = "endDate";

    // Getters and setters
    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public Number getBudget() { // might need to be changed to an int
        return getNumber(KEY_BUDGET);
    }

    public void setBudget(Number budget) {
        put(KEY_BUDGET, budget);
    }

    public ParseObject getCity() {
        return getParseObject(KEY_CITY);
    }

    public void setCity(ParseObject city) {
        put(KEY_CITY, city);
    }

    public Number getNumDays() {
        return getNumber(KEY_NUMDAYS);
    }

    public void setNumDays(Number numDays) {
        put(KEY_NUMDAYS, numDays);
    }

    public ParseObject getStartDate() {
        return getParseObject(KEY_STARTDATE);
    }

    public void setStartDate(ParseObject date) { // Might need to change to date
        put(KEY_STARTDATE, date);
    }

    public ParseObject getEndDate() {
        return getParseObject(KEY_ENDDATE);
    }

    public void setEndDate(ParseObject date) { // Might need to change to date
        put(KEY_ENDDATE, date);
    }

}
