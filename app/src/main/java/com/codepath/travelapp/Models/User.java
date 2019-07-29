package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser implements Parcelable {

    // Instance Variables
    private static final String KEY_FOLLOWING = "following";
    public static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_BIO = "bio";
    private static final String KEY_IMAGE ="profileImage";
    private static final String KEY_HOMESTATE = "homeState";

    // Getters
    public ParseRelation<User> getFollowing() {
        return getRelation(KEY_FOLLOWING); //TODO change this to reflect new relation data structure
    }
    public int getFollowers() { //TODO assess method use in application 
        final int[] size = new int[1];
        ParseRelation<User> relation = getRelation(KEY_FOLLOWERS);
        relation.getQuery().findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    size[0] = objects.size();
                }
            }
        });
        return size[0];
    }
    public Number getFavorites() {
        return  getNumber(KEY_FAVORITES);
    }
    public String getBio() {
        return  getString(KEY_BIO);
    }
    public ParseFile getProfileImage() {
        return getParseFile(KEY_IMAGE);
    }
    public String getHomeState() {
        return getString(KEY_HOMESTATE);
    }

    // Setters
    public void setFollowing(Number following) {
        put(KEY_FOLLOWING, following);
    }
    public void setFollowers(Number followers) {
        put(KEY_FOLLOWERS, followers);
    }
    public void setFavorites(Number favorites) {
        put(KEY_FAVORITES, favorites);
    }
    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }
    public void setProfileImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }
    public void setHomeState (String homeState) {
        put(KEY_HOMESTATE, homeState);
    }
}
