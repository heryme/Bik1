package com.project.biker.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static String reg_id;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        logDebug("Refreshed token: " + refreshedToken);
        reg_id = refreshedToken;
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        logDebug("Refreshed token: " + token);
    }

    /**
     * Method for log
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }
}