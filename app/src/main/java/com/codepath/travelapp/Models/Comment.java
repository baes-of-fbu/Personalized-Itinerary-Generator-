package com.codepath.travelapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    private static String KEY_USER = "user";
    private static String KEY_COMMENT = "comment";

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }
}
