package com.codepath.travelapp.Models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser implements Parcelable {

    // Instance Variables
    private static final String KEY_FOLLOWING = "following";
    private static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_BIO = "bio";
    private static final String KEY_IMAGE ="profileImage";
    private static final String KEY_HOMESTATE = "homeState";

    // Getters and setters
    public ParseRelation<User> getFollowing() {
        return getRelation(KEY_FOLLOWING);
    }

    public void setFollowing(Number following) {
        put(KEY_FOLLOWING, following);
    } //TODO make relevant

    public ParseRelation<User> getFollowers() {
        return getRelation(KEY_FOLLOWERS);
    }

    public void setFollowers(ParseRelation<User> followers) {
        put(KEY_FOLLOWERS, followers);
    } //TODO make relevant

    public Number getFavorites() {
        return  getNumber(KEY_FAVORITES);
    }

    public void setFavorites(Number favorites) {
        put(KEY_FAVORITES, favorites);
    }

    public String getBio() {
        return  getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setProfileImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getHomeState() {
        return getString(KEY_HOMESTATE);
    }

    public void setHomeState (String homeState) {
        put(KEY_HOMESTATE, homeState);
    }
}
