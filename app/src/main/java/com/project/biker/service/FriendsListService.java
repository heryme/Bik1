package com.project.biker.service;

import android.app.Dialog;
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
 * Created by Rahul Padaliya on 5/12/2017.
 */
public class FriendsListService extends APIService {

    private static final String TAG = "FriendsListService";
    private static final String URL_FRIEND_LIST = APIService.BASE_URL + "/friend/friendlist";
    private static final String URL_FRIEND_PENDING = APIService.BASE_URL + "/friend/pending";
    private static final String URL_FRIEND_CONNECT = APIService.BASE_URL + "/friend/connect";
    private static final String URL_CHECK_FRIEND_STATUS = APIService.BASE_URL + "/friend/checkFriendStatus";
    private static final String URL_FRIEND_REJECT = APIService.BASE_URL + "/friend/reject";
    private static final String URL_FRIEND_ACCEPT = APIService.BASE_URL + "/friend/accept";
    private static final String URL_UN_FRIEND = APIService.BASE_URL + "/friend/unfriend";

    /* Reject ride API */
    public static void friendList(final Context context,
                                  final HashMap<String, String> params,
                                  final int page,
                                  final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< FRIEND LIST API >>>>>>");
        logDebug(URL_FRIEND_LIST);
        logDebug(params.toString());

        //When Page is 0 then Call Without Page Counter Else Pass Page Counter
        if (page == 0) {
            StringRequest friendListRequest = new StringRequest(Request.Method.POST, URL_FRIEND_LIST/* + "/" + page * 5*/,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            logDebug("Page--->" + page);
                            logDebug("URL Page--->" + URL_FRIEND_LIST);
                            logDebug("Response>> \n " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (handleAPIError(context, jsonObject)) {
                                    dialog.dismiss();
                                    successListener.onSuccess(new JSONObject(response));
                                }
                            } catch (Exception e) {
                                dialog.dismiss();
                                logDebug("<<<<<  FRIEND LIST ()Response Exception >>>>> \n ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND LIST  Exception >>>>> \n ");
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

            APIService.setRequestPolicy(friendListRequest);
            apiController.addRequest(friendListRequest, TAG);

        } else {

            logDebug("Final Page--->" + page);
            StringRequest friendListRequest = new StringRequest(Request.Method.POST, URL_FRIEND_LIST + "/" + page,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            logDebug("Page--->" + page);
                            logDebug("URL Page--->" + URL_FRIEND_LIST);
                            logDebug("Response>> \n " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (handleAPIError(context, jsonObject)) {
                                    dialog.dismiss();
                                    successListener.onSuccess(new JSONObject(response));
                                }
                            } catch (Exception e) {
                                dialog.dismiss();
                                logDebug("<<<<<  FRIEND LIST ()Response Exception >>>>> \n ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND LIST  Exception >>>>> \n ");
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

            APIService.setRequestPolicy(friendListRequest);
            apiController.addRequest(friendListRequest, TAG);
        }
    }


    /* Friend Pending API */
    public static void friendPending(final Context context,
                                     int page,
                                     final HashMap<String, String> params,
                                     final Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< FRIEND PENDING API >>>>>>");
        logDebug(URL_FRIEND_PENDING);
        logDebug(params.toString());

        //When Page is 0 then Call Without Page Counter Else Pass Page Counter
        if (page == 0) {

            StringRequest friendPendingRequest = new StringRequest(Request.Method.POST,
                    URL_FRIEND_PENDING, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    logDebug("Response>> \n " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            dialog.dismiss();
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        logDebug("<<<<< FRIEND PENDING ()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND PENDING Exception >>>>> \n ");
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

            APIService.setRequestPolicy(friendPendingRequest);
            apiController.addRequest(friendPendingRequest, TAG);
        } else {

            StringRequest friendPendingRequest = new StringRequest(Request.Method.POST,
                    URL_FRIEND_PENDING + "/" + page * 5, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    logDebug("Response>> \n " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (handleAPIError(context, jsonObject)) {
                            dialog.dismiss();
                            successListener.onSuccess(new JSONObject(response));
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        logDebug("<<<<<  FFRIEND PENDING  ()Response Exception >>>>> \n ");
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND PENDING Exception >>>>> \n ");
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

            APIService.setRequestPolicy(friendPendingRequest);
            apiController.addRequest(friendPendingRequest, TAG);
        }
    }


    /* Friend Connect API */
    public static void friendConnect(final Context context,
                                     final HashMap<String, String> params,
                                     final Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< URl FRIEND CONNECT API >>>>>>");
        logDebug(URL_FRIEND_CONNECT);
        logDebug(params.toString());
        StringRequest friendPendingRequest = new StringRequest(Request.Method.POST, URL_FRIEND_CONNECT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("---->Response>> \n " + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (true/*handleAPIError(context, jsonObject)*/) {
                        successListener.onSuccess(new JSONObject(response));
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND PENDING Response Exception >>>>> \n ");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< FRIEND PENDING Exception >>>>> \n ");
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

        APIService.setRequestPolicy(friendPendingRequest);
        apiController.addRequest(friendPendingRequest, TAG);
    }


    /* check Friend Status */
    public static void checkFriendStatus(final Context context,
                                         final HashMap<String, String> params,
                                         final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< CHECK FRIEND_STATUS>>>>>>");
        logDebug(URL_CHECK_FRIEND_STATUS);
        logDebug(params.toString());
        StringRequest friendListRequest = new StringRequest(Request.Method.POST, URL_CHECK_FRIEND_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("Response>> \n " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (handleAPIError(context, jsonObject)) {
                        dialog.dismiss();
                        successListener.onSuccess(new JSONObject(response));
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    logDebug("<<<<< Check Friend ()Response Exception >>>>> \n ");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< CHECK FRIEND STATUS Exception >>>>> \n ");
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

        APIService.setRequestPolicy(friendListRequest);
        apiController.addRequest(friendListRequest, TAG);
    }

    /* Reject Friend API */
    public static void friendReject(final Context context,
                                    final HashMap<String, String> params,
                                    final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< FRIEND REJECT API >>>>>>");
        logDebug(URL_FRIEND_REJECT);
        logDebug(params.toString());
        StringRequest friendRejectRequest = new StringRequest(Request.Method.POST, URL_FRIEND_REJECT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                logDebug("Reject Response>> \n " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                  /*  if (handleAPIError(context, jsonObject)) {
                        dialog.dismiss();*/
                        successListener.onSuccess(new JSONObject(response));
                    //}
                } catch (Exception e) {
                    dialog.dismiss();
                    logDebug("<<<<< FRIEND REJECT ()Response Exception >>>>> \n ");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< FRIEND REJECT  Exception >>>>> \n ");
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

        APIService.setRequestPolicy(friendRejectRequest);
        apiController.addRequest(friendRejectRequest, TAG);
    }


    /* Accept Friend API */
    public static void friendAccept(final Context context,
                                    final HashMap<String, String> params,
                                    final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< FRIEND ACCEPT API >>>>>>");
        logDebug(URL_FRIEND_ACCEPT);
        logDebug(params.toString());
        StringRequest friendRejectRequest = new StringRequest(Request.Method.POST, URL_FRIEND_ACCEPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("Response>> \n " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (handleAPIError(context, jsonObject)) {
                        dialog.dismiss();
                        successListener.onSuccess(new JSONObject(response));
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< FRIEND ACCEPT  Exception >>>>> \n ");
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

        APIService.setRequestPolicy(friendRejectRequest);
        apiController.addRequest(friendRejectRequest, TAG);
    }

    /* Un Friend API */
    public static void unFriend(final Context context,
                                    final HashMap<String, String> params,
                                    final APIService.Success<JSONObject> successListener) {

        final Dialog dialog = DialogUtility.showProgress(context);
        logDebug("<<<<<< FRIEND ACCEPT API >>>>>>");
        logDebug("URL UN FRIEND-->" + URL_UN_FRIEND);
        logDebug(params.toString());
        StringRequest friendRejectRequest = new StringRequest(Request.Method.POST, URL_UN_FRIEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                logDebug("Response>> \n " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (handleAPIError(context, jsonObject)) {
                        dialog.dismiss();
                        successListener.onSuccess(new JSONObject(response));
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                logDebug("<<<<< Un Friend  Exception >>>>> \n ");
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

        APIService.setRequestPolicy(friendRejectRequest);
        apiController.addRequest(friendRejectRequest, TAG);
    }


    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }
}
