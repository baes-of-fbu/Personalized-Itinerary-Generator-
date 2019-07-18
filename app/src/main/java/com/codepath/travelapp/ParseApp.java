package com.codepath.travelapp;

import android.app.Application;

import com.codepath.travelapp.Models.Trip;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.facebook.ParseFacebookUtils;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Trip.class);
        // Set up configuration
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("travel-app-fbu-2019")
                .clientKey("westlake-bestlake")
                .server("https://travel-app-fbu.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);

        ParseFacebookUtils.initialize(this);
    }
}
