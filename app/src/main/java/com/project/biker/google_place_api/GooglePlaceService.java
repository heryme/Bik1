package com.project.biker.google_place_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.project.biker.tools.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class GooglePlaceService extends AsyncTask<String, Void, String> {

    private static final String TAG = "GooglePlaceService";
    public Context context;

    public GooglePlaceService( Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String placeId = params[0];
        StringBuilder sb = new StringBuilder(Constant.PLACES_DETAIL_API_BASE + Constant.TYPE_DETAILS
                + Constant.OUT_JSON);
        sb.append("?key=" + Constant.API_KEY);
        sb.append("&placeid=" + placeId);
        System.out.println("URL" + sb.toString());
        return Utils.getService(sb.toString());

    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Location" + result);
        String lat = "", lng = "";
        super.onPostExecute(result);
        try {
            JSONObject jObject = new JSONObject(result);
            lat = jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat")
                    .toString();
            lng = jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng")
                    .toString();

        } catch (JSONException e) {
            logDebug("<<<< I/O  Exception >>>>");
            e.printStackTrace();
        } catch (Exception e) {
            logDebug("<<<< General Exception >>>>");
            e.printStackTrace();
        }
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
