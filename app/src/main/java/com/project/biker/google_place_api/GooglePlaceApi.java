package com.project.biker.google_place_api;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.project.biker.tools.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class GooglePlaceApi {

    private static final String TAG = GooglePlaceApi.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyD8EUF1K_qviNGf0eRzQx-L5VtTd2jYZhw";
    public static ArrayList<HashMap<String, Object>> resultList = null;

    public ArrayList<HashMap<String, Object>> autocomplete(String input,
                                                           AlertDialog alertDialog,
                                                           Context context, Boolean isProfile) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + Constant.API_KEY);
            if (isProfile) {
                sb.append("&types=(cities)");
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            } else {
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            }


            //System.out.println("GooglePlaceApiURL : " + sb.toString());
           logDebug("GooglePlaceApiURL : "+sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            logDebug("<<<< Error processing Places API URL >>>>");
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            logDebug("<<<< Error connecting to Places API >>>>");
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Log.d(TAG, jsonResults.toString());

            // Create a JSON object hierarchy from the results
            System.out.println("Google Result---> : " + jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            String status = jsonObj.getString("status");
            logDebug("Google Status-->" + status);
            if (status.equalsIgnoreCase("OVER_QUERY_LIMIT")) {
                handler(context, alertDialog);
            }

            // Extract the Place descriptions and place id from the results
            resultList = new ArrayList<HashMap<String, Object>>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("DESCRIPTION", predsJsonArray.getJSONObject(i).getString("description"));
                map.put("PLACE_ID", predsJsonArray.getJSONObject(i).getString("place_id"));
                resultList.add(map);

            }

        } catch (JSONException e) {
            logDebug("<<<< Cannot process JSON results >>>>");
            e.printStackTrace();
        }

        return resultList;
    }

    public static HashMap<String, Object> getPlaceIdAndDesc(int position) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        map = resultList.get(position);
        return map;
    }

    /**
     * Show Dialog When Place Api Daily Limit Over
     *
     * @param context
     * @param alertDialog
     */
    private void handler(Context context, final AlertDialog alertDialog) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                if (!alertDialog.isShowing())
                    alertDialog.show();
            }
        });
    }

    /**
     * Method to Print Log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}