package com.project.biker.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vikas Patel on 8/17/2017.
 */

public class BenefitService extends APIService {

    private static final String TAG = "BenefitService";
    /* URL */
    private static final String URL_ALL_BENEFITS = BASE_URL + "/benefit/getAllBenefits";

    /* Getting all Benefit */
    public static void allBenefits(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        logDebug(URL_ALL_BENEFITS);
        logDebug(params.toString());
        StringRequest allBenefits = new StringRequest(Request.Method.POST, URL_ALL_BENEFITS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< All Benefit List() Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< All Benefit List()Error Exception >>>>> \n ");
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
                parseHeader(response, context);
                return super.parseNetworkResponse(response);
            }
        };
        APIController apiController = APIController.getInstance(context);

        APIService.setRequestPolicy(allBenefits);
        apiController.addRequest(allBenefits, TAG);
    }

    /**
     * Method to display the log
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }
}
