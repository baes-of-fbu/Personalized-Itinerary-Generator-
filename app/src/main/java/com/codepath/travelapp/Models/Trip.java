package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Date;

@ParseClassName("Trip") // Needs to be same name as class on Parse website
public class Trip extends ParseObject implements Serializable {

    // Instance variables
    private static final String KEY_NAME = "name";
    public static final String KEY_OWNER = "owner";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_CITY = "city";
    private static final String KEY_NUMDAYS = "numDays";
    private static final String KEY_STARTDATE = "startDate";
    private static final String KEY_ENDDATE = "endDate";
    public static final String KEY_ISUPCOMING = "isUpcoming";
    public static final String KEY_ISFAVORITED = "isFavorited";

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

    public Number getBudget() { // TODO might need to be changed to an int
        return getNumber(KEY_BUDGET);
    }

    public void setBudget(Number budget) {
        put(KEY_BUDGET, budget);
    }

    public ParseObject getCity() { // TODO might need to be changed to an city
        return getParseObject(KEY_CITY);
    }

    public void setCity(ParseObject city) {
        put(KEY_CITY, city);
    }

    public Number getNumDays() { // TODO might need to be changed to an int
        return getNumber(KEY_NUMDAYS);
    }

    public void setNumDays(Number numDays) {
        put(KEY_NUMDAYS, numDays);
    }

    public Date getStartDate() { // TODO might need to be changed to an Date
        return getDate(KEY_STARTDATE);
    }

    public void setStartDate(Date date) {
        put(KEY_STARTDATE, date);
    }

    public Date getEndDate() {
        return getDate(KEY_ENDDATE);
    }

    public void setEndDate(Date date) { // Might need to change to date
        put(KEY_ENDDATE, date);
    }

    public Boolean isUpcoming() {
        return getBoolean(KEY_ISUPCOMING);
    }

    public Boolean isFavorited() {
        return getBoolean(KEY_ISFAVORITED);
    }

}
