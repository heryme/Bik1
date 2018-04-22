package com.project.biker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.ResponseParser.RideParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.fragment.MeetUpRouteFragment;
import com.project.biker.service.APIService;
import com.project.biker.service.RideService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_RIDER_ID;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_FOUR_ZERO_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE_ZERO_ONE;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

/**
 * Created by Rahul Padaliya on 7/21/2017.
 */

public class InvitationAdapter extends BaseAdapter {
    private static final String TAG = "InvitationAdapter";

    Context context;
    ArrayList<RideParser.InvitationResponse.InvitationItem> list;
    LayoutInflater inflater;
    SharePref sharePref;

    public InvitationAdapter(Context context, ArrayList<RideParser.InvitationResponse.InvitationItem> list) {
        this.context = context;
        this.list = list;
        sharePref = new SharePref(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, final View convertView, ViewGroup viewGroup) {


        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.row_ride_request, null);

        LinearLayout llAccept = (LinearLayout) vi.findViewById(R.id.llAccept);
        LinearLayout llReject = (LinearLayout) vi.findViewById(R.id.llReject);
        TextView txtName = (TextView) vi.findViewById(R.id.txtName);
        TextView txtMeetup = (TextView) vi.findViewById(R.id.txtMeetup);
        TextView txtDestination = (TextView) vi.findViewById(R.id.txtDestination);
        TextView txtRequestTime = (TextView) vi.findViewById(R.id.txtRequestTime);
        TextView txtRideRequest = (TextView) vi.findViewById(R.id.txtRideRequest);
        TextView txtAcceptRide = (TextView) vi.findViewById(R.id.txtAcceptRide);
        CircleImageView circleImageView=(CircleImageView)vi.findViewById(R.id.circleImageView);

        final RideParser.InvitationResponse.InvitationItem item = list.get(i);
        txtName.setText(item.getCreaterRideName());
        txtMeetup.setText(item.getMeetup());
        txtDestination.setText(item.getDestination());
        txtRequestTime.setText(item.getRideRequestTime());
        txtRideRequest.setText(String.valueOf(item.getRideRequest()));
        txtAcceptRide.setText(String.valueOf(item.getRideAccept()));


        if (item.getCreaterRideProfilePic().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.man)
                    .into(circleImageView);

        } else {
            Glide.with(context)
                    .load(item.getCreaterRideProfilePic())
                    .into(circleImageView);
        }



        llAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePref.setRideId(item.getRideId());
                rideAcceptApi();

            }
        });

        llReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePref.setRideId(item.getRideId());
                rideRejectApi(i);

            }
        });
        return vi;
    }

    /**
     * Call the Ride Reject Api
     */
    private void rideAcceptApi() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);

        if (isNetworkAvailable(context)) {


            RideService.acceptRide(context, param, new APIService.Success<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    logDebug("Ride Accept-->" + response.toString());
                    if (response != null && response.length() > 0) {
                        int status_code;
                        String message = "";
                        try {
                            status_code = response.optInt("status_code");
                            message = response.optString("message");

                            if (status_code == API_STATUS_ONE) {
                                JSONObject data = response.getJSONObject("data");
                                String rideID = data.optString("rideId");
                                sharePref.setRideId(rideID);
                                ((MainActivity) context).loadFragment(new MeetUpRouteFragment(), false);
                            } else if (status_code == API_STATUS_ONE_ZERO_ONE) {
                                DialogUtility.AlertDialogUtility(context, message);
                            } else if (status_code == API_STATUS_FOUR_ZERO_ONE) {
                                DialogUtility.invalidCredentialsAlert(context, message);
                            }

                        } catch (JSONException e) {
                            logDebug("<<<<<<<<<<< Accept Ride Response Exception >>>>>>>>>>>>");
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

    }

    /**
     * Call Ride Reject Api
     *
     * @param i
     */
    private void rideRejectApi(final int i) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);

        if (isNetworkAvailable(context)) {

            RideService.rejectRide(context, param, new APIService.Success<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, response.toString());
                    if (response != null && response.length() > 0) {
                        int status_code;
                        String message = "";
                        try {
                            status_code = response.getInt("status_code");
                            message = response.getString("message");

                            if (status_code == API_STATUS_ONE) {
                                JSONObject data = response.getJSONObject("data");
                                String rideID = data.getString("rideId");
                                sharePref.setRideId(rideID);
                                list.remove(i);
                                notifyDataSetChanged();
                            } else if (status_code == API_STATUS_ONE_ZERO_ONE) {
                                DialogUtility.AlertDialogUtility(context, message);
                            } else if (status_code == API_STATUS_FOUR_ZERO_ONE) {
                                DialogUtility.invalidCredentialsAlert(context, message);
                            }

                        } catch (JSONException e) {
                            logDebug("<<<<<<<<<<< Reject Ride Response Exception >>>>>>>>>>>>");
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }

    /**
     * Method to Print Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }
}
