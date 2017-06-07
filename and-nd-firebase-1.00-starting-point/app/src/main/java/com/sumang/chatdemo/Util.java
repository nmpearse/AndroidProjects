package com.sumang.chatdemo;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sumang.bajaj on 6/1/2017.
 */

public class Util {

    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    public static User currentUser;

    public static void updateFCMToken(Context context, String token)
    {
        DatabaseReference mUsersDatabaseRef = mFirebaseDatabase.getReference().child("users");
        String userID = SharedPreferencesUtil.getInstance(context).getString("userID", "-1");
        if(!userID.equals("-1")){
            mUsersDatabaseRef.child(userID).child("fcmToken").setValue(token);
        }
    }

    public static int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
