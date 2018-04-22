package com.project.biker.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.biker.R;
import com.project.biker.activity.MainActivity;
import com.project.biker.tools.SharePref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SharePref sharePref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        remoteMessage.getCollapseKey();
        // Log.d("TAG", "message---->> " + remoteMessage.getData().get("message").toString());
        Map<String, String> temp = remoteMessage.getData();
        String data = temp.get("message");
        logDebug("Temp Data--->" + data);
        logDebug("Data--->" + temp.get("message"));
        logDebug("message---->> " + remoteMessage.getData().get("data"));

    /*    try {
            Intent intent = new Intent("vikas");
            sendBroadcast(intent);
            JSONObject jsonObject = new JSONObject(data);
            String action = jsonObject.optString("action");
            Log.e("INSTANCE UPDATE", "action" + action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        notificationDataParser(remoteMessage.getData().get("message").toString(), data);

    }


    private static void generateNotification(Context context, String title, String msg, String data) {
        Intent intentNot = new Intent(context, MainActivity.class);
        intentNot.putExtra("message", data);
        intentNot.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Generate the Random integer number to pass in pending intent
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        PendingIntent intent = PendingIntent.getActivity(context, i1, intentNot, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon_512)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(intent)
                .setWhen(System.currentTimeMillis());

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

    /**
     * Parse notification data
     *
     * @param data
     */
    private void notificationDataParser(String data, String msg) {
        try {
            sharePref = new SharePref(getBaseContext());

            JSONObject jsonObject = new JSONObject(data);
            String id = jsonObject.optString("i_id");
            logDebug("ID-->" + id);
            String profilePic = jsonObject.optString("v_profile_pic");
            logDebug("v_profile_pic-->" + profilePic);
            String name = jsonObject.optString("name");
            logDebug("name" + name);
            String coverPic = jsonObject.optString("v_cover_pic");
            logDebug("coverPic" + coverPic);
            String action = jsonObject.optString("action");
            logDebug("action" + action);
            String notificationTitle = jsonObject.optString("notification_title");
            logDebug("notificationTitle" + notificationTitle);
            String notificationMessage = jsonObject.optString("notification_message");
            logDebug("notificationMessage" + notificationMessage);
            String rideId = jsonObject.optString("rideId");
            logDebug("rideId" + rideId);
            sharePref.setRideId(rideId);


            //Generate Notification
            generateNotification(getBaseContext(), notificationTitle, notificationMessage, msg);

        } catch (JSONException e) {
            logDebug("<<<<< notificationDataParser Exception >>>>> \n ");
            e.printStackTrace();
        }

    }

    /**
     * Method for log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}
