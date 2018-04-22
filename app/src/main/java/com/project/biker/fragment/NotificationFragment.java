package com.project.biker.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.iid.FirebaseInstanceId;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.ResponseParser.RideParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.adapter.InvitationAdapter;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.BikerService;
import com.project.biker.service.GPSTracker;
import com.project.biker.service.GpsService;
import com.project.biker.service.RideService;
import com.project.biker.service.UserService;
import com.project.biker.tools.SharePref;
import com.project.biker.tools.SharePrefAutostart;
import com.project.biker.utils.AutoStartPermission;
import com.project.biker.utils.DialogUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.project.biker.service.JobUtil.scheduleJob;
import static com.project.biker.service.UserService.PARAM_LAT;
import static com.project.biker.service.UserService.PARAM_LONG;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_STATUS;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_FOUR_ZERO_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE_ZERO_ONE;
import static com.project.biker.utils.DialogUtility.AlertDialogUtility;
import static com.project.biker.utils.DialogUtility.invalidCredentialsAlert;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    /**
     * RecyclerView
     */
    //RecyclerView rv_frg_notification;

    SharePref sharePref;
    SharePrefAutostart sharePrefAutostart;

    TextView tvToolBarTitle;
    Toolbar toolbar;
    ToggleButton toggle_toolbar;
    TextView tvMainActivityNotSharingLoc;


    String regId;
    String deviceId;

    ListView listView;

    GPSTracker gps;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, null);

        //Initialization View
        initializeView(view);
        //Object Initializer
        objectCreator();

        getInvitation(view);


        toggle_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle_toolbar.isChecked()) {
                    if (isNetworkAvailable(getActivity())) {
                        ((MainActivity) getContext()).displayLocationSettingsRequest(getActivity());
                        if (sharePref.getPopupCheck()) {
                        } else {
                            DialogUtility.locationDialog(getActivity());
                        }
                        statusUpdateApi("1");
                    } else {
                        toggle_toolbar.setChecked(false);
                        Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.login_dark_back));
                    if (isNetworkAvailable(getActivity())) {
                        statusUpdateApi("0");
                    } else {
                        toggle_toolbar.setChecked(true);
                        Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                    }
                    //  UserParser.BikerListResponse.bikerList.clear();

                }
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new NotificationFragment());
    }

    /**
     * Method For objectInitialization
     * Initialize shared Preference
     */
    private void objectCreator() {
        sharePref = new SharePref(getContext());
    }

    /**
     * Initialization View
     */
    private void initializeView(View view) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toggle_toolbar = (ToggleButton) toolbar.findViewById(R.id.toggle_toolbar);
        listView = (ListView) view.findViewById(R.id.lvInvitationList);
        tvMainActivityNotSharingLoc = (TextView) getActivity().findViewById(R.id.tvMainActivityNotSharingLoc);
        gps = new GPSTracker(getActivity());
        sharePref = new SharePref(getActivity());
        sharePrefAutostart = new SharePrefAutostart(getActivity());
    }

    /**
     * Api call for sending LatLong
     */
    private void latLongApiCall() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_LAT, String.valueOf(gps.getLatitude()));
        param.put(PARAM_LONG, String.valueOf(gps.getLongitude()));
        param.put(PARAM_PLATFORM, PLATFORM);

        BikerService.addLatLong(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Location Response-->" + response.toString());
            }
        });

    }

    /**
     * Call the api to get the ride invitation
     *
     * @param view
     */
    private void getInvitation(final View view) {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_PLATFORM, PLATFORM);

        RideService.rideInvitation(getContext(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Invitation Response-->" + response.toString());
                RideParser.InvitationResponse invitationResponse = RideParser.InvitationResponse.invitation(response);
                ArrayList<RideParser.InvitationResponse.InvitationItem> list = invitationResponse.getInvitationItemArrayList();

                if (invitationResponse != null && invitationResponse.getStatusCode() == API_STATUS_ONE) {
                    if (list.size() > 0) {
                        InvitationAdapter invitationAdapter = new InvitationAdapter(getActivity(), list);
                        listView.setAdapter(invitationAdapter);
                        listView.setEmptyView(view.findViewById(R.id.txtNoinvitation));
                    }
                }
            }
        });
    }

    /**
     * Status update api
     * 1 for online
     * 0 for offline
     *
     * @param status
     */
    public void statusUpdateApi(final String status) {

        // generate the device token and device it using fireBase
        regId = FirebaseInstanceId.getInstance().getToken();
        deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_STATUS, status);
        param.put(PARAM_PLATFORM, PLATFORM);

        UserService.statusUpdate(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                UserParser.UpdateStatesResponse updateStatesResponse = UserParser.UpdateStatesResponse.updateStatesResponse(response);

                if (updateStatesResponse != null && updateStatesResponse.getStatusCode() == API_STATUS_ONE) {

                    if (updateStatesResponse.getUserStatus().equalsIgnoreCase("1")) {

                        /**
                         * This if condition is for to ask use
                         * for autostart permission
                         * autostart is for the background task
                         */
                        if (sharePrefAutostart.getFirstTimeStatus().equalsIgnoreCase("0")) {
                            logDebug("FIRST TIME IF" + sharePrefAutostart.getFirstTimeStatus());
                            sharePrefAutostart.setFirstTimeStatus("1");
                            Boolean status = AutoStartPermission.displayAutoStartPermission();
                            if (status) {
                                DialogUtility.alertOk(getActivity(),getString(R.string.auto_start_message), new INTFAlertOk() {
                                    @Override
                                    public void alertOk() {
                                        AutoStartPermission.autoStartBackgroundPermission(getActivity());
                                    }
                                });
                            }
                        }


                        toolbar.setBackgroundColor(getResources().getColor(R.color.tab_color));
                        tvToolBarTitle.setText(getString(R.string.online));
                        tvMainActivityNotSharingLoc.setVisibility(View.GONE);
                        if (isNetworkAvailable(getActivity())) {
                            sharePref.setUserStatus("1");
                            latLongApiCall();
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                scheduleJob(getActivity());
                            } else {
                                getActivity().startService(new Intent(getActivity(), GpsService.class));
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                        }
                    } else if (updateStatesResponse.getUserStatus().equalsIgnoreCase("0")) {
                        toolbar.setBackgroundColor(getResources().getColor(R.color.login_dark_back));
                        tvToolBarTitle.setText(getString(R.string.offline));
                        tvMainActivityNotSharingLoc.setVisibility(View.VISIBLE);
                        sharePref.setUserStatus("0");
                        getActivity().stopService(new Intent(getActivity(), GpsService.class));
                    }

                } else if (updateStatesResponse.getStatusCode() == API_STATUS_ONE_ZERO_ONE) {
                    if (sharePref.getUserStatus().equals("1")) {
                        toggle_toolbar.setChecked(true);
                    } else {
                        toggle_toolbar.setChecked(false);
                    }
                    AlertDialogUtility(getActivity(), updateStatesResponse.getMessage());
                } else if (updateStatesResponse.getStatusCode() == API_STATUS_FOUR_ZERO_ONE) {
                    invalidCredentialsAlert(getActivity(), updateStatesResponse.getMessage());
                }


            }
        });
    }

    /**
     * Method to Print Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return NotificationFragment.class.getName();
    }

}
