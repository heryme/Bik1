package com.project.biker.ResponseParser;

import android.util.Log;

import com.project.biker.model.ModelBikerList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vikas Patel on 8/17/2017.
 */

public class BikerParser {

    private static final String TAG = "BikerParser";

    public static class BikerListResponse {

        public static ArrayList<ModelBikerList> bikerList = new ArrayList<>();

        int statusCode;
        String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static BikerParser.BikerListResponse bikerResponse(JSONObject response) {

            BikerParser.BikerListResponse bikerListResponse = new BikerParser.BikerListResponse();

            bikerListResponse.setStatusCode(response.optInt("status_code"));
            bikerListResponse.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");

                JSONArray jsonArray = jsonObject.getJSONArray("nearByUser");

                bikerList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    ModelBikerList model = new ModelBikerList();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    model.setUserId(jsonObject1.optString("i_user_id"));
                    model.setFirstName(jsonObject1.optString("v_firstname"));
                    model.setLastName(jsonObject1.optString("v_lastname"));
                    model.setLatitude(jsonObject1.optString("d_latitude"));
                    model.setLongitude(jsonObject1.optString("d_longitude"));
                    model.setStatus(jsonObject1.optString("t_status"));
                    model.setIsFriend(jsonObject1.optInt("is_friend"));
                    model.setProfilePic(jsonObject1.optString("v_profile_pic"));
                    model.setCoverPic(jsonObject1.optString("v_cover_pic"));
                    model.setBikeModel(jsonObject1.optString("v_bike_model"));
                    model.setBikeNumber(jsonObject1.optString("v_bike_number"));
                    model.setIsSelected(false);

                    bikerList.add(model);
                }

            } catch (JSONException e) {
                logDebug("<<<<< BikerListResponse ()Response Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }


            return bikerListResponse;
        }


    }

    public static class BikerListResponseHome {

        public static ArrayList<ModelBikerList> bikerListHome = new ArrayList<>();

        int statusCode;
        String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static BikerListResponseHome bikerResponseHome(JSONObject response) {

            BikerListResponseHome bikerListResponseHome = new BikerListResponseHome();

            bikerListResponseHome.setStatusCode(response.optInt("status_code"));
            bikerListResponseHome.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");

                JSONArray jsonArray = jsonObject.getJSONArray("nearByUser");
                bikerListHome.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    ModelBikerList model = new ModelBikerList();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    model.setUserId(jsonObject1.optString("i_user_id"));
                    model.setFirstName(jsonObject1.optString("v_firstname"));
                    model.setLastName(jsonObject1.optString("v_lastname"));
                    model.setLatitude(jsonObject1.optString("d_latitude"));
                    model.setLongitude(jsonObject1.optString("d_longitude"));
                    model.setStatus(jsonObject1.optString("t_status"));
                    model.setIsFriend(jsonObject1.optInt("is_friend"));
                    model.setProfilePic(jsonObject1.optString("v_profile_pic"));
                    model.setCoverPic(jsonObject1.optString("v_cover_pic"));
                    model.setBikeModel(jsonObject1.optString("v_bike_model"));
                    model.setBikeNumber(jsonObject1.optString("v_bike_number"));
                    model.setIsSelected(false);
                    bikerListHome.add(model);
                }

            } catch (JSONException e) {
                logDebug("<<<<< BikerListResponseHome()Response Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return bikerListResponseHome;
        }

    }

    public static class AddLatLongResponse {

        int statusCode;
        String message;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public static AddLatLongResponse addLatLongResponse(JSONObject response) {
            AddLatLongResponse addLatLongResponse = new AddLatLongResponse();
            addLatLongResponse.setStatusCode(response.optInt("status_code"));
            addLatLongResponse.setMessage(response.optString("message"));
            return addLatLongResponse;
        }


    }

    /**
     * Method for Log Printing
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }

}
