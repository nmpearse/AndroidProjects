package com.sumang.chatdemo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sumang.bajaj on 5/31/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        // Add custom implementation, as needed.
        //SharedPreferencesUtil.getInstance(getApplicationContext()).putString(getString(R.string.firebase_cloud_messaging_token), token);

        // To implement: Only if user is registered, i.e. UserId is available in preference, update token on server.
        String userId = SharedPreferencesUtil.getInstance(getApplicationContext()).getString("userID", "-1");
        if(!userId.equals("-1")){
            Util.updateFCMToken(getApplicationContext(), token);
        }
    }
}
