package com.project.biker.service;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.biker.utils.DialogUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vishal Gadhiya on 7/21/2017.
 */

public class RideService extends APIService {

    private static final String TAG = "RideService";
    /* URL */
    private static final String URL_ACCEPT_RIDE = BASE_URL + "/ride/acceptRide";
    private static final String URL_REJECT_RIDE = BASE_URL + "/ride/rejectRide";
    private static final String URL_RIDE_INVITATION = BASE_URL + "/ride/rideInvition";
    private static final String URL_CREATE_BIKE_RIDE = BASE_URL + "/ride/create";
    private static final String URL_GET_RIDER_LAT_LONG = BASE_URL + "/ride/getRiderLatLng";
    private static final String URL_START_RIDE = BASE_URL + "/ride/rideStart";
    private static final String URL_STOP_RIDE = BASE_URL + "/ride/rideStop";
    private static final String URL_RIDE_STATUS = BASE_URL + "/ride/getRideStatus";


    /* Accept ride API */
    public static void acceptRide(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        logDebug("<<<<<< Accept Ride API >>>>>>");
        logDebug(URL_ACCEPT_RIDE);
        logDebug(params.toString());
        final Dialog dialog = DialogUtility.showProgress(context);
        StringRequest acceptRideRequest = new StringRequest(Request.Method.POST, URL_ACCEPT_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                logDebug("<<<<<< Accept Ride API 1 >>>>>>" + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        logDebug("<<<<<< Accept Ride API 1 >>>>>>");
                        dialog.dismiss();
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        logDebug("<<<<< Accept Ride() Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< Accept Ride() Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);
        APIService.setRequestPolicy(acceptRideRequest);
        apiController.addRequest(acceptRideRequest, TAG);
    }

    /* Reject ride API */
    public static void rejectRide(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< Reject Ride API >>>>>>");
        logDebug(URL_REJECT_RIDE);
        logDebug(params.toString());
        StringRequest rejectRideRequest = new StringRequest(Request.Method.POST, URL_REJECT_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            dialog.dismiss();
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        logDebug("<<<<< Reject Ride()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< Reject Ride()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);
        APIService.setRequestPolicy(rejectRideRequest);
        apiController.addRequest(rejectRideRequest, TAG);
    }

    /* Reject ride API */
    public static void rideInvitation(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< Ride InvitationResponse API >>>>>>");
        logDebug(URL_RIDE_INVITATION);
        logDebug(params.toString());
        StringRequest rideInvitationRequest = new StringRequest(Request.Method.POST, URL_RIDE_INVITATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            dialog.dismiss();
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        logDebug("<<<<< Ride InvitationResponse()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< Ride InvitationResponse()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
               logDebug("Cookies response"+response.headers.toString());
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(rideInvitationRequest);
        apiController.addRequest(rideInvitationRequest, TAG);
    }

    /* Create Ride Api */
    public static void createBikeRide(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();
        logDebug(URL_CREATE_BIKE_RIDE);
        logDebug(params.toString());
        StringRequest createBikeRide = new StringRequest(Request.Method.POST, URL_CREATE_BIKE_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("all benefits List Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            progressDialog.dismiss();
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< Create Bike Ride()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                logDebug("<<<<< Create Bike Ride()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);
        APIService.setRequestPolicy(createBikeRide);
        apiController.addRequest(createBikeRide, TAG);
    }

    /* Send Rider Lat Long */
    public static void RiderLatLong(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {


        logDebug(URL_GET_RIDER_LAT_LONG);
        logDebug(params.toString());
        StringRequest getRiderLatLong = new StringRequest(Request.Method.POST, URL_GET_RIDER_LAT_LONG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Rider LatLong()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Rider LatLong()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);
        APIService.setRequestPolicy(getRiderLatLong);
        apiController.addRequest(getRiderLatLong, TAG);
    }

    /* Start Ride Api */
    public static void RideStart(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {


        logDebug(URL_START_RIDE);
        logDebug(params.toString());
        StringRequest startRide = new StringRequest(Request.Method.POST, URL_START_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Start Ride()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Start Ride()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);
        APIService.setRequestPolicy(startRide);
        apiController.addRequest(startRide, TAG);
    }

    /* Stop Ride Api */
    public static void RideStop(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {


        logDebug(URL_STOP_RIDE);
        logDebug(params.toString());
        StringRequest startStop = new StringRequest(Request.Method.POST, URL_STOP_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Ride Stop()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Ride Stop()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(startStop);
        apiController.addRequest(startStop, TAG);
    }

    /* Get Ride Status Api */
    public static void RideStatus(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {


        logDebug(URL_RIDE_STATUS);
        logDebug(params.toString());
        StringRequest rideStatus = new StringRequest(Request.Method.POST, URL_RIDE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Ride Status()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Ride Status()Error Exception >>>>> \n ");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeader(context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(rideStatus);
        apiController.addRequest(rideStatus, TAG);
    }

    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }
}
