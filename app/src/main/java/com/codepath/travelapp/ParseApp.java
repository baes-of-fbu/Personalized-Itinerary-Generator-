package com.codepath.travelapp;

import android.app.Application;

import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.CityImages;
import com.codepath.travelapp.Models.Comment;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.facebook.ParseFacebookUtils;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(City.class);
        ParseObject.registerSubclass(DayPlan.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Tag.class);
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(CityImages.class);
        ParseObject.registerSubclass(Achievement.class);
        ParseObject.registerSubclass(Comment.class);

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
