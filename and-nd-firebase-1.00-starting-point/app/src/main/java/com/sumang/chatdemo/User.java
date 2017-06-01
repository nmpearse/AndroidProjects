package com.sumang.chatdemo;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sumang.bajaj on 5/31/2017.
 */

public class User implements Serializable{
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String displayName;
    private Uri photoUri;
    private String emailID;
    private Map<String, Boolean> friends;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }
}
