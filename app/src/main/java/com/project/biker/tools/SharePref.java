package com.project.biker.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vikas Patel on 7/8/2017.
 */

public class SharePref {

    Context mContext;
    SharedPreferences sharedPref;

    public SharePref(Context mContext) {
        this.mContext = mContext;
        sharedPref = mContext.getSharedPreferences(Constant.SharedPrefConstant.SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * User Id
     *
     * @param userId
     */
    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_USER_ID, userId);
        editor.commit();
    }

    public String getUserId() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_USER_ID, " ");
    }


    public void setSessionId(String sessionId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_SESSION_ID, sessionId);
        editor.commit();
    }

    public String getSessionId() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_SESSION_ID, null);
    }


    public void setUserStatus(String status) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_USER_STATUS, status);
        editor.commit();

    }


    public String getUserStatus() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_USER_STATUS, "0");
    }


    public void clear() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public void setRideId(String rideId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_RIDE_ID, rideId);
        editor.commit();
    }

    public String getRideId() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_RIDE_ID, " ");
    }

    public void setPopupCheck(Boolean status) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constant.SharedPrefConstant.KEY_POUP_CHECK, status);
        editor.commit();
    }

    public boolean getPopupCheck() {
        return sharedPref.getBoolean(Constant.SharedPrefConstant.KEY_POUP_CHECK, false);
    }

    public void SetNotificationImage(String url) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_NOTIFICATION_IMAGE, url);
        editor.commit();
    }

    public String GetNotificationImage() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_NOTIFICATION_IMAGE, "");
    }

    public void SetNotificationName(String name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_NOTIFICATION_NAME, name);
        editor.commit();
    }

    public String GetNotificationName() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_NOTIFICATION_NAME, "");
    }


    public void SetLat(String lat) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_GET_LAT, lat);
        editor.commit();
    }

    public String GetLat() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_GET_LAT, "0.0");
    }

    public void SetLong(String longi) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_GET_LONG, longi);
        editor.commit();
    }

    public String GetLong() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_GET_LONG, "0.0");
    }


    public void SetCookie(String cookie) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_COOKIE, cookie);
        editor.commit();
    }

    public String GetCookie() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_COOKIE, "");
    }

    public void setExecutionTime(String executionTime) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_EXECUTION_TIME, executionTime);
        editor.commit();
    }

    public String getExecutionTime() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_EXECUTION_TIME, "0");
    }


}

