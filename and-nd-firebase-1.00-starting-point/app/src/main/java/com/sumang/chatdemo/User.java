package com.sumang.chatdemo;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

/**
 * Created by sumang.bajaj on 5/31/2017.
 */

public class User {
    private String displayName;
    private Uri photoUri;
    private String emailID;
    private Map<String, Boolean> friends;
}
