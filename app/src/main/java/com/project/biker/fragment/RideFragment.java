package com.project.biker.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.iid.FirebaseInstanceId;
import com.project.biker.R;
import com.project.biker.ResponseParser.BikerParser;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.ResponseParser.RideParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.adapter.RiderRecycleViewAdapter;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.model.ModelBikerList;
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

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.project.biker.adapter.RiderRecycleViewAdapter.selectedBikerLst;
import static com.project.biker.service.JobUtil.scheduleJob;
import static com.project.biker.service.UserService.PARAM_LAT;
import static com.project.biker.service.UserService.PARAM_LONG;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_RIDER_ID;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_STATUS;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_FOUR_ZERO_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE_ZERO_ONE;
import static com.project.biker.tools.Constant.PROGRESS_STATUS;
import static com.project.biker.tools.Constant.STATUS_RIDE_NOT_STARTED;
import static com.project.biker.tools.Constant.STATUS_RIDE_STARTED;
import static com.project.biker.utils.DialogUtility.AlertDialogUtility;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017.
 */
public class RideFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RideFragment";

    View view;

    public static RecyclerView rvRiderList;

    RiderRecycleViewAdapter riderAdapter;

    private ArrayList<ModelBikerList> bikerList = new ArrayList<>();
    SharePref sharePref;
    SharePrefAutostart sharePrefAutostart;

    public static boolean flag = false;

    TextView tvMainActivityNotSharingLoc;
    TextView tvToolBarTitle;
    Toolbar toolbar;
    ToggleButton toggle_toolbar;

    Boolean glideStatus = true;

    GPSTracker gps;


    String regId;
    String deviceId;

    ImageView ivNext;

    TextView txtNoRider;

    Boolean startStopStatus = false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ride, null);

        PROGRESS_STATUS = true;

        //Initialization View
        initializeView(view);
        //Object Initializer
        objectInitializer();
        //Call Rider API
        riderStatusApi();
        //Register Click Listener
        initListener();
        // call the rider list api
        callRiderListApi();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new RideFragment());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivNext: {
                selectedBikerLst.clear();
                for (int i = 0; i < BikerParser.BikerListResponse.bikerList.size(); i++) {
                    if (BikerParser.BikerListResponse.bikerList.get(i).getIsSelected() == true) {
                        BikerParser.BikerListResponse.bikerList.get(i).getUserId();
                        selectedBikerLst.add(BikerParser.BikerListResponse.bikerList.get(i).getUserId());
                    }
                }
                if (selectedBikerLst.size() == 0) {
                    AlertDialogUtility(getActivity(), getString(R.string.selectRider));
                } else {
                    ((MainActivity) getContext()).loadFragment(new MeetUPFragment(), false);
                }
                break;
            }
            case R.id.toggle_toolbar: {
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
                    if (isNetworkAvailable(getActivity())) {
                        statusUpdateApi("0");
                    } else {
                        toggle_toolbar.setChecked(true);
                        Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                    }

                    BikerParser.BikerListResponse.bikerList.clear();
                    riderAdapter = new RiderRecycleViewAdapter(getActivity(), BikerParser.BikerListResponse.bikerList, ivNext, glideStatus);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvRiderList.setLayoutManager(mLayoutManager);
                    rvRiderList.setItemAnimator(new DefaultItemAnimator());
                    rvRiderList.setAdapter(riderAdapter);
                    alertDialogUtility1(getActivity(), getString(R.string.off_line));
                }
                break;
            }
        }
    }

    /**
     * Getting Ids for xml component
     *
     * @param v
     */
    private void initializeView(View v) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toggle_toolbar = (ToggleButton) toolbar.findViewById(R.id.toggle_toolbar);
        tvMainActivityNotSharingLoc = (TextView) getActivity().findViewById(R.id.tvMainActivityNotSharingLoc);
        ivNext = (ImageView) v.findViewById(R.id.ivNext);
        rvRiderList = (RecyclerView) view.findViewById(R.id.rvRiderList);
        txtNoRider = (TextView) view.findViewById(R.id.txtNoRider);


    }

    /**
     * Object Initializer
     */
    private void objectInitializer() {
        sharePref = new SharePref(getActivity());
        sharePrefAutostart = new SharePrefAutostart(getActivity());
        gps = new GPSTracker(getActivity());
    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        ivNext.setOnClickListener(this);
        toggle_toolbar.setOnClickListener(this);
    }

    /**
     * Call BikerList Api
     */
    private void bikerListApi() {

        regId = FirebaseInstanceId.getInstance().getToken();
        deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_PLATFORM, PLATFORM);

        BikerService.bikerList(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Biker Response-->" + response.toString());

                BikerParser.BikerListResponse bikerListResponse = BikerParser.BikerListResponse.bikerResponse(response);
                if (bikerListResponse.getStatusCode() == API_STATUS_ONE) {
                    if (BikerParser.BikerListResponse.bikerList.size() > 0) {
                        riderAdapter = new RiderRecycleViewAdapter(getActivity(), BikerParser.BikerListResponse.bikerList, ivNext, glideStatus);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvRiderList.setLayoutManager(mLayoutManager);
                        rvRiderList.setItemAnimator(new DefaultItemAnimator());
                        rvRiderList.setAdapter(riderAdapter);
                        txtNoRider.setVisibility(View.GONE);
                    } else {
                        txtNoRider.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * Alert Dialog to display online and offline status
     *
     * @param context
     * @param message
     */
    public void alertDialogUtility1(Context context, String message) {
        DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
            @Override
            public void alertOk() {
                ((MainActivity) getContext()).loadFragment(new HomeFragment(), false);
            }
        });
    }

    /**
     * call the api for updating status of user 1 for online
     * and 0 for offline
     *
     * @param status
     */
    public void statusUpdateApi(final String status) {

        //getting the device id and device token in android
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

                logDebug("Status Response-->" + response.toString());

                UserParser.UpdateStatesResponse updateStatesResponse = UserParser.UpdateStatesResponse.updateStatesResponse(response);
                if (updateStatesResponse != null && updateStatesResponse.getStatusCode() == API_STATUS_ONE) {

                    if (updateStatesResponse.getUserStatus().equalsIgnoreCase("1")) {


                        /**
                         * This if condition is for to ask use
                         * for autoStart permission
                         * autoStart is for the background task
                         */
                        if (sharePrefAutostart.getFirstTimeStatus().equalsIgnoreCase("0")) {
                            logDebug("FIRST TIME IF" + sharePrefAutostart.getFirstTimeStatus());
                            sharePrefAutostart.setFirstTimeStatus("1");
                            Boolean status = AutoStartPermission.displayAutoStartPermission();
                            if (status) {
                                DialogUtility.alertOk(getActivity(), getString(R.string.auto_start_message), new INTFAlertOk() {
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

                            bikerListApi();
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
                } else if (updateStatesResponse != null && updateStatesResponse.getStatusCode() == API_STATUS_ONE_ZERO_ONE) {
                    if (sharePref.getUserStatus().equals("1")) {
                        toggle_toolbar.setChecked(true);
                    } else {
                        toggle_toolbar.setChecked(false);
                    }
                    DialogUtility.AlertDialogUtility(getActivity(), updateStatesResponse.getMessage());
                } else if (updateStatesResponse != null && updateStatesResponse.getStatusCode() == API_STATUS_FOUR_ZERO_ONE) {
                    DialogUtility.invalidCredentialsAlert(getActivity(), updateStatesResponse.getMessage());
                }
            }
        });
    }

    /**
     * call the api to send current lat long to server
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
     * call the api for getting rider status
     * ride is start or not
     */
    private void riderStatusApi() {

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);


        RideService.RideStatus(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Ride status Response-->" + response.toString());
                RideParser.RideStatusResponse rideStatusResponse = RideParser.RideStatusResponse.rideStatusResponse(response);

                if (rideStatusResponse != null && rideStatusResponse.getStatusCode() == API_STATUS_ONE) {
                    if (rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_STARTED) || rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_NOT_STARTED)) {
                        if (rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_NOT_STARTED)) {
                            startStopStatus = false;
                        } else if (rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_STARTED)) {
                            startStopStatus = true;
                        }
                        sharePref.setRideId(rideStatusResponse.getRiderId());
                        MeetUpRouteFragment meetUpRouteFragment = new MeetUpRouteFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("startStatus", startStopStatus);
                        meetUpRouteFragment.setArguments(bundle);
                        ((MainActivity) getActivity()).loadFragment(meetUpRouteFragment, false);
                    }
                }
            }
        });
    }

    /**
     * call the rider listApi if user is online
     * and clear the array if user offline
     */
    private void callRiderListApi() {
        if (isNetworkAvailable(getActivity())) {
            if (sharePref.getUserStatus().equals("1")) {
                bikerListApi();
            } else {
                BikerParser.BikerListResponse.bikerList.clear();
                riderAdapter = new RiderRecycleViewAdapter(getActivity(), BikerParser.BikerListResponse.bikerList, ivNext, glideStatus);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvRiderList.setLayoutManager(mLayoutManager);
                rvRiderList.setItemAnimator(new DefaultItemAnimator());
                rvRiderList.setAdapter(riderAdapter);

                alertDialogUtility1(getActivity(), getString(R.string.off_line));
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Method for the Printing Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return RideFragment.class.getName();
    }
}
