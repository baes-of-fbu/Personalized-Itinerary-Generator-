package com.codepath.travelapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.facebook.ParseFacebookUtils;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set up configuration
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("travel-app-fbu-2019")
                .clientKey("westlake-bestlake")
                .server("http://travel-app-fbu.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);

        ParseFacebookUtils.initialize(this);
    }
}
