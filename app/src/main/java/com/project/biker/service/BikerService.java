package com.project.biker.service;

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

import static com.project.biker.tools.Constant.PROGRESS_STATUS;

/**
 * Created by Vikas Patel on 8/17/2017.
 */

public class BikerService extends APIService {

    private static final String TAG = "BikerService";
    /* URL */
    private static final String URL_BIKER_LIST = BASE_URL + "/user/bikerList";
    private static final String URL_LOCATION_UPDATE = BASE_URL + "/gps/add";

    /* Getting Biker List api */
    public static void bikerList(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        if (PROGRESS_STATUS) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }

        logDebug(URL_BIKER_LIST);
        logDebug(params.toString());
        StringRequest bikerrequest = new StringRequest(Request.Method.POST, URL_BIKER_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                logDebug("Biker List Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< Biker List()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (PROGRESS_STATUS) {
                    progressDialog.dismiss();
                }
                logDebug("<<<<< Biker List()Error Exception >>>>> \n ");
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
                Log.e("Cookies response", response.headers.toString());
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(bikerrequest);
        apiController.addRequest(bikerrequest, TAG);
    }

    /* Getting Biker List api for homeFragment*/
    public static void bikerListHome(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        logDebug(URL_BIKER_LIST);
        logDebug(params.toString());
        StringRequest bikerrequest = new StringRequest(Request.Method.POST, URL_BIKER_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("Biker List Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Biker List Home()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Biker List Home()Error Exception >>>>> \n ");
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
                Log.e("Cookies response", response.headers.toString());
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(bikerrequest);
        apiController.addRequest(bikerrequest, TAG);
    }

    /* Sending the lat long Api */
    public static void addLatLong(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {


        logDebug(URL_LOCATION_UPDATE);
        logDebug(params.toString());

        StringRequest locationUpdate = new StringRequest(Request.Method.POST, URL_LOCATION_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< add latLong()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< add latLong()Error Exception >>>>> \n ");
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

        APIService.setRequestPolicy(locationUpdate);
        apiController.addRequest(locationUpdate, TAG);
    }

    /**
     * Method to display log
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }
}
