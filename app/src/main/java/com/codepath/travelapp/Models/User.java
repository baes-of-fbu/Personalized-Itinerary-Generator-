package com.codepath.travelapp.Models;

import com.parse.ParseUser;

public class User extends ParseUser {

    // Instance Variables
    private static final String KEY_FOLLOWING = "following";
    private static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_BIO = "bio";
    private static final String KEY_IMAGE ="image";

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
    public String getBo() {
        return  getString(KEY_BIO);
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
}
