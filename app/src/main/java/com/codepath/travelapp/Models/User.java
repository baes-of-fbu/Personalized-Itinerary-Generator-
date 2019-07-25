package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser implements Parcelable {

    // Instance Variables
    private static final String KEY_FOLLOWING = "following";
    public static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_BIO = "bio";
    private static final String KEY_IMAGE ="image";
    private static final String KEY_HOMESTATE = "homeState";

    // Getters
    public Number getFollowing() {
        return getNumber(KEY_FOLLOWING);
    }
    public Number getFollowers() {
        return getNumber(KEY_FOLLOWERS);
    }
    public Number getFavorites() {
        return  getNumber(KEY_FAVORITES);
    }
    public String getBio() {
        return  getString(KEY_BIO);
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
    public void getFavorites(Number favorites) {
        put(KEY_FAVORITES, favorites);
    }
    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }
    public void setHomeState (String homeState) {
        put(KEY_HOMESTATE, homeState);
    }
}
