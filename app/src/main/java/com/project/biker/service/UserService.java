package com.project.biker.service;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.biker.utils.DialogUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.project.biker.tools.Constant.API_RETRY_POLICY;


/**
 * Created by Vikas Patel on 7/8/2017.
 */

public class UserService extends APIService {

    private static final String TAG = "UserService";


    /* URL */
    private static final String URL_LOGIN = BASE_URL + "/user/login";
    private static final String URL_LOGIN_SOCIAL = BASE_URL + "/user/socialRegister";
    private static final String URL_REGISTER = BASE_URL + "/user/register";
    private static final String URL_FORGOTT_PASSWORD = BASE_URL + "/user/forgetPassword";
    private static final String URL_STATUS_UPDATE = BASE_URL + "/user/updateStatus";
    private static final String URL_LOGOUT = BASE_URL + "/user/logout";
    private static final String URL_USER_EDIT = APIService.BASE_URL + "/user/edit";
    private static final String URL_USER_EXITS = APIService.BASE_URL + "/user/userExists";


    /* Parameters */
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_SESSION_ID = "session_id";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_GENDER = "gender";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_DEVICE_ID = "device_token";
    public static final String PARAM_DEVICE_TYPE = "device_type";
    public static final String PARAM_PLATFORM = "platform";
    public static final String PARAM_FACEBOOK_ID = "facebook_id";
    public static final String PARAM_GOOGLE_ID = "google_id";
    public static final String PARAM_PROFILE_PIC = "profile_pic";
    public static final String PARAM_FIRST_NAME = "firstname";
    public static final String PARAM_LAST_NAME = "lastname";
    public static final String PARAM_ACTIVATION_CODE = "activation_key";
    public static final String PARAM_LAT = "lat";
    public static final String PARAM_NEW_LONG = "lng";
    public static final String PARAM_LONG = "long";
    public static final String PARAM_INVITED_IDS = "invited_ids";
    public static final String PARAM_MEETUP = "meetup";
    public static final String PARAM_DESTINATION = "destination";
    public static final String PARAM_MEETUP_LONGITUDE = "meetup_longitude";
    public static final String PARAM_MEETUP_LATITUDE = "meetup_latitude";
    public static final String PARAM_DESTINATION_LATITUDE = "destination_latitude";
    public static final String PARAM_DESTINATION_LONGITUDE = "destination_longitude";
    public static final String PARAM_MEETUP_TIME = "meetup_time";
    public static final String PARAM_RIDER_ID = "ride_id";
    public static final String PARAM_ID_FACEBOOK = "facebook_id";
    public static final String PARAM_SOCIAL_LOGIN = "social_login";

    public static final String DEVICE_TYPE = "a";
    public static final String PLATFORM = "app";
    public static final String YES = "yes";
    public static final String MALE = "male";


    public static void login(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();

        logDebug(URL_LOGIN);
        logDebug(params.toString());
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                logDebug("Login response >> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(jsonObject);
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< login()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                logDebug("<<<<< login()Error Exception >>>>> \n ");
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
        apiController.addRequest(loginRequest, TAG);
    }

    public static void loginSocial(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {

        logDebug(URL_LOGIN_SOCIAL);
        logDebug(params.toString());
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL_LOGIN_SOCIAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                logDebug("Login response >> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< Social login()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logDebug("<<<<< Social login()Error Exception >>>>> \n ");
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
        apiController.addRequest(loginRequest, TAG);
    }

    public static void signUp(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();

        logDebug(URL_REGISTER);
        logDebug(params.toString());
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                logDebug("Register Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< Signup()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                logDebug("<<<<< Signup()Error Exception >>>>> \n ");
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

        APIService.setRequestPolicy(loginRequest);
        apiController.addRequest(loginRequest, TAG);
    }

    public static void forgotPassword(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();
        logDebug(URL_FORGOTT_PASSWORD);
        logDebug(params.toString());
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL_FORGOTT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                logDebug("Forgot Password Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< ForgotPassword()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                logDebug("<<<<< ForgotPassword()Error Exception >>>>> \n ");
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

        APIService.setRequestPolicy(loginRequest);
        apiController.addRequest(loginRequest, TAG);
    }

    public static void statusUpdate(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();

        logDebug(URL_STATUS_UPDATE);
        logDebug(params.toString());
        StringRequest statusRequest = new StringRequest(Request.Method.POST, URL_STATUS_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if ((progressDialog != null)) {
                    progressDialog.dismiss();
                }
                logDebug("Status Update Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        successListener.onSuccess(new JSONObject(response));
                    } catch (Exception e) {
                        if ((progressDialog != null)) {
                            progressDialog.dismiss();
                        }
                        logDebug("<<<<< Status Update()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((progressDialog != null)) {
                    progressDialog.dismiss();
                }
                logDebug("<<<<< Status Update()Error Exception >>>>> \n ");
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

        APIService.setRequestPolicy(statusRequest);
        apiController.addRequest(statusRequest, TAG);
    }

    public static void Logout(final Context context, final HashMap<String, String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();

        logDebug(URL_LOGOUT);
        logDebug(params.toString());
        StringRequest logout = new StringRequest(Request.Method.POST, URL_LOGOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if ((progressDialog != null)) {
                    progressDialog.dismiss();
                }
                logDebug(" logout Response>> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        logDebug("<<<<< logout()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                logDebug("<<<<< logout()Error Exception >>>>> \n ");
                if ((progressDialog != null)) {
                    progressDialog.dismiss();
                }
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

        APIService.setRequestPolicy(logout);
        apiController.addRequest(logout, TAG);
    }


    //Update User Profile
    public static void updateUserProfileInfo(final Context context,
                                             final HashMap<String, Object> params,
                                             final Success<JSONObject> successListener) {


        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("Update Edit Profile PARAM---> " + params.toString());

        Log.d("url >> ", URL_USER_EDIT);
        MultipartRequest sendImage = new MultipartRequest(URL_USER_EDIT, params, context,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.length() > 0) {
                            if ((dialog != null)) {
                                dialog.dismiss();
                            }

                            Log.d(TAG, response.toString());
                            if (handleAPIError(context, response)) {
                                successListener.onSuccess(response);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((dialog != null)) {
                            dialog.dismiss();
                        }
                        Log.e("UserProfileService :", "<<<<< UserProfileService() Exception >>>>> \n ");
                        error.printStackTrace();

                    }
                })/*{
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
        }*/;

        sendImage.setRetryPolicy(new DefaultRetryPolicy(API_RETRY_POLICY, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        APIController.getInstance(context).addRequest(sendImage, TAG);

    }

    //Get User Profile
    public static void getUserProfileInfo(final Context context, String userId, String sessionId, final Success<JSONObject> successListener) {

        Log.d(TAG, "Get Profile Data URL >> " + URL_USER_EDIT + "?user_id=" + userId + "&session_id" + sessionId + "&platform=app");
        // Log.d(TAG, "PARAM >> " + params);

        final ProgressDialog dialog = DialogUtility.showProgress(context);

        StringRequest changePwd = new StringRequest(Request.Method.GET,
                URL_USER_EDIT + "?user_id=" + userId + "&session_id=" + sessionId + "&platform=app"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
                        if ((dialog != null)) {
                            dialog.dismiss();
                        }
                        Log.d(TAG, "User Edit response >> " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        if ((dialog != null)) {
                            dialog.dismiss();
                        }
                        logDebug("<<<< getUserProfile Response Exception >>>>");
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((dialog != null)) {
                    dialog.dismiss();
                }
                Log.e("getUserProfileInfo :", "<<<<< getUserProfileInfo()Error Exception >>>>> \n");
                error.printStackTrace();
            }
        }) {
            /*@Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }*/

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

        APIController.getInstance(context).addRequest(changePwd, TAG);
    }



    //Activation Key API
    public static void checkUserExits(final Context context, final HashMap<String,
            String> params, final APIService.Success<JSONObject> successListener) {
        final ProgressDialog progressDialog = DialogUtility.showProgress(context);
        progressDialog.show();

        logDebug(URL_USER_EXITS);
        logDebug(params.toString());
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URL_USER_EXITS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                logDebug("activation response >> \n " + response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            successListener.onSuccess(jsonObject);
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        logDebug("<<<<< activation()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                logDebug("<<<<< activation()Error Exception >>>>> \n ");
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
        apiController.addRequest(loginRequest, TAG);
    }



    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }


}
