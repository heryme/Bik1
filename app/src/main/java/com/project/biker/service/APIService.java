package com.project.biker.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.StringRequest;
import com.project.biker.activity.MainActivity;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.project.biker.tools.Constant.API_SERVICE_REQUEST_TIME;

/**
 * Created by hirenk on 19-05-2016.
 */
public abstract class APIService {

    private static final String TAG = "APIService";
    private static SharePref sharePref = null;


      public static final String BASE_URL = "http://local.websoptimization.com:89/bikersystem";  // Demo live
     //public static final String BASE_URL = "http://projectbiker.com/bikersystem"; // Production
     //public static final String BASE_URL = "http://103.244.121.68:89/bikersystem";// Local Backup
    //public static final String BASE_URL = "http://192.168.1.55/biker/";// Local Backup

   // private static final int REQUEST_TIME = 5000; // time in millisecond

    private static final int NO_OF_RETRIES = 0;


    public static String KEY_HEADER = "Cookie";

    public interface Success<T> {
        public void onSuccess(T response);
    }

    public static HashMap<String, String> setHeader(Context context) {

        SharePref sharePref = new SharePref(context);
        HashMap<String, String> header = new HashMap<>();
        header.put(KEY_HEADER, sharePref.GetCookie());
        return header;
    }

    public static void parseHeader(NetworkResponse response,Context context)
    {
        Log.e("Cookies response", response.headers.toString());
        Map<String, String> responseHeaders = response.headers;

        if (responseHeaders.containsKey("Set-Cookie")) {
            String rawCookies = responseHeaders.get("Set-Cookie");
            String[] splitCookie = rawCookies.split(";");
            Log.e("Cookies", ":" + splitCookie[0]);
            SharePref sharePref = getSharePref(context);
            sharePref.SetCookie(splitCookie[0]);
        }
    }


    public static void setRequestPolicy(StringRequest stringRequest) {
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                NO_OF_RETRIES,
                API_SERVICE_REQUEST_TIME,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public static SharePref getSharePref(Context context) {
        if (APIService.sharePref == null) {
            APIService.sharePref = new SharePref(context);
        }
        return APIService.sharePref;
    }

    /**
     * Handle Status Code 401 : invalid credential or session expired
     * Status Code 101 : parameter errors
     * @param context
     * @param jsonObject
     * @return
     */
    public static boolean handleAPIError(final Context context, JSONObject jsonObject) {
        boolean flag = false;
        try {

            int statusCode = jsonObject.optInt("status_code");
            String message = jsonObject.optString("message");

            if (statusCode == Constant.API_STATUS_ONE_ZERO_ONE) {
                DialogUtility.AlertDialogUtility(context, message);
                flag = false;
            } else if (statusCode == Constant.API_STATUS_FOUR_ZERO_ONE) {
                flag = false;
                DialogUtility.alertOk(context, message, new INTFAlertOk() {
                    @Override
                    public void alertOk() {
                        ((MainActivity) context).logout();
                    }
                });
            } else {
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
            Log.d(TAG, "<<<<< handleAPIError()Error exception >>>>> \n");
            e.printStackTrace();
        }

        return flag;
    }

}
