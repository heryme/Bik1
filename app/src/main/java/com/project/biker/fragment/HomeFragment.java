package com.project.biker.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.biker.R;
import com.project.biker.ResponseParser.BikerParser;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.model.INTFConfirmYesNo;
import com.project.biker.model.ModelBikerList;
import com.project.biker.service.APIService;
import com.project.biker.service.BikerService;
import com.project.biker.service.FriendsListService;
import com.project.biker.service.GPSTracker;
import com.project.biker.service.GpsService;
import com.project.biker.service.UserService;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;
import com.project.biker.tools.SharePrefAutostart;
import com.project.biker.utils.AutoStartPermission;
import com.project.biker.utils.CustomBoldTextView;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.RoundRectCornerImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;
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
import static com.project.biker.tools.Constant.HOME_BIKER_LIST_INTERVAL;
import static com.project.biker.tools.Constant.HOME_BIKER_PROFILE_POPUP_ANIMATION_DELAY;
import static com.project.biker.tools.Constant.HOME_GOOGLE_MAP_BEARING;
import static com.project.biker.tools.Constant.HOME_MOVE_CAMERA_ZOOM;
import static com.project.biker.tools.Constant.HOME_ZOOM_LEVEL;
import static com.project.biker.tools.Constant.PROGRESS_STATUS;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.
        OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener {


    private static final String TAG = "HomeFragment";

    private String SEND_REQUEST = "0";
    private String SEE_MORE = "1";
    private String CANCEL_REQUEST = "2";
    private String ACCEPT_REJECT = "3";


    View view;

    GoogleMap googleMap;
    Marker mCurrLocationMarker = null;
    Toolbar toolbar;
    ToggleButton toggle_toolbar;

    TextView tvToolBarTitle;
    TextView tvMainActivityNotSharingLoc;
    CustomBoldTextView tvName;


    ImageView ivMyLocation;

    RoundRectCornerImageView ivMarkerDialogCornerView;
    CircleImageView ivRowInviteFriendProfile;

    Button btnUnfriend, btnSendRequest, btnCancel, btnAccept, btnReject;

    String regId;
    String deviceId;
    SupportMapFragment mapFragment;

    LinearLayout ll_acceptReject;

    RelativeLayout relMain;

    SharePref sharePref;
    SharePrefAutostart sharePrefAutostart;

    String friendUserId, firstName, lastName;


    GPSTracker gps;

    float zoom;






    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //Check the view is already exist or not to avoid duplicate id issue
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (InflateException e) {
            logDebug("<<<<<< view inflate Exception >>>>>>>>");
            e.printStackTrace();
        }

        PROGRESS_STATUS = false;

        //Initialization View
         initializeView();
        //Basic Object Initialization
         objectInitializer();
        //Handler Biker List
         handlerBikerList();

        setExecutionTimeForService();

        locationSetting();

        //Register Click Listener
         initListener();

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.clear();

        googleMap.getUiSettings().setMapToolbarEnabled(false);

        View viewMarker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dynamic_green_myposition_marker, null);
        Bitmap bmp = dynamicMarker(viewMarker, getString(R.string.markerYou));

        LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, HOME_MOVE_CAMERA_ZOOM));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        if (sharePref.getUserStatus().equalsIgnoreCase("1")) {
            mCurrLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    .title("you")
                    .position(mapCenter));

        }
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(HOME_ZOOM_LEVEL)
                .bearing(HOME_GOOGLE_MAP_BEARING)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_down);
        relMain.startAnimation(animation1);
        relMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                relMain.setVisibility(View.GONE);
            }
        }, HOME_BIKER_PROFILE_POPUP_ANIMATION_DELAY);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        if (marker.getTitle().equalsIgnoreCase(getString(R.string.markerYouTitle))) {

        } else {
            String name = marker.getTitle();
            //    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_loction_blue));
            int size = BikerParser.BikerListResponseHome.bikerListHome.size();
            for (int i = 0; i < size; i++) {
                BikerParser.BikerListResponseHome.bikerListHome.get(i).getIsFriend();
                logDebug("IS Friends--->" + BikerParser.BikerListResponseHome.bikerListHome.get(i).getIsFriend());
                logDebug("friendUserId --->" + BikerParser.BikerListResponseHome.bikerListHome.get(i).getUserId());

                if (name.equalsIgnoreCase(BikerParser.BikerListResponseHome.bikerListHome.get(i).getUserId())) {

                    friendUserId = BikerParser.BikerListResponseHome.bikerListHome.get(i).getUserId();
                    firstName = BikerParser.BikerListResponseHome.bikerListHome.get(i).getFirstName();
                    lastName = BikerParser.BikerListResponseHome.bikerListHome.get(i).getLastName();

                    if (BikerParser.BikerListResponseHome.bikerListHome.get(i).getProfilePic().equalsIgnoreCase("")) {
                        Glide.with(getActivity())
                                .load(R.drawable.man)
                                .into(ivRowInviteFriendProfile);
                    } else {
                        Glide.with(getActivity())
                                .load(BikerParser.BikerListResponseHome.bikerListHome.get(i).getProfilePic())
                                .into(ivRowInviteFriendProfile);
                    }

                    if (BikerParser.BikerListResponseHome.bikerListHome.get(i).getCoverPic().equalsIgnoreCase("")) {
                        Glide.with(getActivity())
                                .load(R.drawable.ic_cover_pic)
                                .into(ivMarkerDialogCornerView);
                    } else {
                        Glide.with(getActivity())
                                .load(BikerParser.BikerListResponseHome.bikerListHome.get(i).getCoverPic())
                                .into(ivMarkerDialogCornerView);
                    }

                    tvName.setText(BikerParser.BikerListResponseHome.bikerListHome.get(i).getFirstName().toUpperCase() + " " + BikerParser.BikerListResponseHome.bikerListHome.get(i).getLastName().toUpperCase());
                }

            }

            if (isNetworkAvailable(getActivity())) {
                //Call Check Friend Status API
                callCheckFriendStatus();
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
            }
        }


        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new HomeFragment());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

                    googleMap.clear();

                }
                break;
            }
            case R.id.ivMyLocation: {
                moveCurrentLocation();
                break;
            }
            case R.id.btnUnfriend: {
                DialogUtility.confirmYesNo(getContext(), stringBuilderUserInfo(firstName, lastName), new INTFConfirmYesNo() {
                    @Override
                    public void yesClick() {
                        if (isNetworkAvailable(getActivity())) {
                            callUnFriendAPI();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            case R.id.btnCancel: {
                if (isNetworkAvailable(getActivity())) {
                    callRejectFriendAPI();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btnSendRequest: {
                if (isNetworkAvailable(getActivity())) {
                    callConnectFriendAPI();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btnAccept: {
                if (isNetworkAvailable(getActivity())) {
                    callAcceptFriendAPI();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btnReject: {
                if (isNetworkAvailable(getActivity())) {
                    callRejectFriendAPI();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }


        }
    }

    /**
     * Initialize view getting ids from xml component
     */
    private void initializeView() {

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toggle_toolbar = (ToggleButton) toolbar.findViewById(R.id.toggle_toolbar);
        tvMainActivityNotSharingLoc = (TextView) getActivity().findViewById(R.id.tvMainActivityNotSharingLoc);
        tvName = (CustomBoldTextView) view.findViewById(R.id.tvName);
        relMain = (RelativeLayout) view.findViewById(R.id.relMain);
        ll_acceptReject = (LinearLayout) view.findViewById(R.id.ll_acceptReject);
        ivMarkerDialogCornerView = (RoundRectCornerImageView) view.findViewById(R.id.ivMarkerDialogCornerView);
        ivRowInviteFriendProfile = (CircleImageView) view.findViewById(R.id.ivRowInviteFrndProfile);
        btnUnfriend = (Button) view.findViewById(R.id.btnUnfriend);
        btnSendRequest = (Button) view.findViewById(R.id.btnSendRequest);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnAccept = (Button) view.findViewById(R.id.btnAccept);
        btnReject = (Button) view.findViewById(R.id.btnReject);
        ivMyLocation = (ImageView) view.findViewById(R.id.ivMyLocation);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maphome);
    }

    /**
     * Object Initializer
     */
    private void objectInitializer(){
        mapFragment.getMapAsync(this);
        sharePref = new SharePref(getActivity());
        sharePrefAutostart = new SharePrefAutostart(getActivity());
        logDebug(sharePref.getUserId());
    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        toggle_toolbar.setOnClickListener(this);
        ivMyLocation.setOnClickListener(this);
        btnUnfriend.setOnClickListener(this);
        btnSendRequest.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);
    }


    /**
     * Handler For BikerList API Call 'X' Minute
     */
    private void handlerBikerList() {

        if (sharePref.getUserStatus().equals("1")) {
            toggle_toolbar.setChecked(true);
            if (isNetworkAvailable(getActivity())) {
                apiCallHandler();
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scheduleJob(getActivity());
                logDebug("VIKAS START SCHED");
            } else {
                getActivity().startService(new Intent(getActivity(), GpsService.class));
            }
        } else {
            toggle_toolbar.setChecked(false);
        }
        gps = new GPSTracker(getActivity());
    }

    /**
     * Call Status UpdateApi
     *
     * @param status
     */
    public void statusUpdateApi(final String status) {

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
                         * for autostart permission
                         * autostart is for the background task
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

                        ivMyLocation.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.tab_color));
                        tvToolBarTitle.setText(getString(R.string.online));
                        tvMainActivityNotSharingLoc.setVisibility(View.GONE);

                        if (isNetworkAvailable(getActivity())) {

                            sharePref.setUserStatus("1");
                            LatLongApiCall();

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

                        ivMyLocation.setVisibility(View.GONE);
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

                } else if (updateStatesResponse != null && updateStatesResponse.getStatusCode() == API_STATUS_FOUR_ZERO_ONE) {
                    toggle_toolbar.setChecked(false);
                    DialogUtility.invalidCredentialsAlert(getActivity(), updateStatesResponse.getMessage());
                }

            }
        });
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

        BikerService.bikerListHome(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Biker Response-->" + response.toString());
                BikerParser.BikerListResponseHome bikerListResponseHome = BikerParser.BikerListResponseHome.bikerResponseHome(response);
                logDebug(String.valueOf(bikerListResponseHome.bikerListHome.size()));
                zoom = googleMap.getCameraPosition().zoom;
                if (bikerListResponseHome != null && bikerListResponseHome.getStatusCode() == API_STATUS_ONE) {
                    addMarker(bikerListResponseHome.bikerListHome);
                }
            }
        });
    }

    /**
     * Add marker to google map
     *
     * @param bikerArray
     */
    private void addMarker(ArrayList<ModelBikerList> bikerArray) {
        View viewMarker;
        googleMap.clear();

        for (int i = 0; i < bikerArray.size(); i++) {
            int layout = 0;

            /**
             * Change the Marker color according to friend status
             * 1 = Friend
             * 0 = UnFriend
             */
            if (bikerArray.get(i).getIsFriend() == 0) {
                layout = R.layout.dynamic_marker_purple;
            } else if (bikerArray.get(i).getIsFriend() == 1) {
                layout = R.layout.dynamic_marker_green;
            }

            double latitude = Double.parseDouble(bikerArray.get(i).getLatitude());
            double longitude = Double.parseDouble(bikerArray.get(i).getLongitude());
            String title = bikerArray.get(i).getUserId();
            String name = bikerArray.get(i).getFirstName().toUpperCase() /*+ " " + bikerArray.get(i).getLastName().toUpperCase()*/;
            drawMarker(latitude, longitude, name, title, layout);
        }

        Double latitude = 0.0;
        Double longitude = 0.0;
        int layout = R.layout.dynamic_green_myposition_marker;
        String title = getString(R.string.markerYouTitle);
        String name = getString(R.string.markerYou);

        /*if (gps.getLatitude() == 0.0) {
            latitude = Double.valueOf(sharePref.GetLat());
            longitude = Double.valueOf(sharePref.GetLong());
        } else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //Toast.makeText(getActivity(),"Latitude-->" + gps.getLatitude() + "  " + "Longitude--->" + gps.getLongitude(),Toast.LENGTH_SHORT).show();
            //Log.d(TAG,"Latitude-->" + gps.getLatitude());
            //Log.d(TAG,"Longitude--->" + gps.getLongitude());
        }*/

        latitude = Double.valueOf(sharePref.GetLat());
        longitude = Double.valueOf(sharePref.GetLong());
        drawMarker(latitude, longitude, name, title, layout);
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));

        logDebug("ZOOM" + zoom);
    }

    /**
     * inflate dynamic view for marker
     *
     * @param view
     * @param biker
     * @return
     */
    public Bitmap dynamicMarker(View view, String biker) {
        TextView bikerName = (TextView) view.findViewById(R.id.bikerName);
        bikerName.setText(biker);
        Bitmap bmp = createDrawableFromView(getActivity(), view);
        return bmp;
    }

    /**
     * Generate Bitmap to set as dynamic marker
     *
     * @param context
     * @param view
     * @return
     */
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    /**
     * Api call of the to send latLong
     */
    private void LatLongApiCall() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_LAT, String.valueOf(gps.getLatitude()));
        param.put(PARAM_LONG, String.valueOf(gps.getLongitude()));
        param.put(PARAM_PLATFORM, PLATFORM);

        BikerService.addLatLong(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Location Response--> " + response.toString());
            }
        });

    }

    /**
     * set google map on users current location
     */
    private void moveCurrentLocation() {
        if (gps != null && gps.getLatitude() != 0f && gps.getLongitude() != 0f) {
            LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, HOME_MOVE_CAMERA_ZOOM));

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(mapCenter)
                    .zoom(HOME_ZOOM_LEVEL)
                    .bearing(HOME_GOOGLE_MAP_BEARING)
                    .build();

            // Animate the change in camera view over 2 seconds
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    /**
     * Handler to call the api every 1 minute
     */
    private void apiCallHandler() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Runnable runnable = new Runnable() {
                    public void run() {
                        try {

                            if (sharePref.getUserStatus().equals("1")) {
                                //  toggle_toolbar.setChecked(true);
                                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frg_main_activity);
                                String backStateName = currentFragment.getClass().getName();
                                if (backStateName.equalsIgnoreCase(getFragmentName())) {
                                    bikerListApi();
                                } else {
                                    timer.cancel();
                                }
                            }
                        } catch (Exception e) {
                            logDebug("<<<< Handler Exception>>>>");
                            e.printStackTrace();
                            // error, do something
                        }
                    }
                };
                handler.post(runnable);
            }
        };
        timer.schedule(task, 0, HOME_BIKER_LIST_INTERVAL);  // interval of one minute

    }

    /**
     * Call Connect Friend API
     */
    private void callConnectFriendAPI() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", friendUserId);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");

        FriendsListService.friendConnect(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Friend Connect Response--->" + response.toString());
                int status;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status = response.optInt("status_code");
                        message = response.getString("message");
                        if (status == API_STATUS_ONE) {
                            DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    relMain.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        logDebug("<<<<< Friend connect Response Exception>>>>>>>");
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    /**
     * Call checkFriendStatus API
     */
    private void callCheckFriendStatus() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", friendUserId);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.checkFriendStatus(getContext(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                String friendshipStatus;
                logDebug("checkFriendStatus Response" + response);
                if (response != null && response.length() > 0) {
                    try {
                        response.optInt("status_code");
                        response.getString("message");
                        JSONObject data = response.getJSONObject("data");
                        friendshipStatus = data.getString("friendshipStatus");
                        showHideButton(friendshipStatus);

                    } catch (JSONException e) {
                        logDebug("<<<<Check Friend status Api Response Exception>>>>");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Call the Method of the to on the location
     * when user click ok location is on directly
     * from setting
     */
    private void locationSetting() {
        if (sharePref.getUserStatus().equals("1")) {
            ((MainActivity) getContext()).displayLocationSettingsRequest(getActivity());
        } else if (sharePref.getUserStatus().equals("0")) {
            ivMyLocation.setVisibility(View.GONE);

        }
    }

    /**
     * Method to create marker from xml layout
     *
     * @param lat
     * @param longi
     * @param name
     * @param layout
     * @return
     */
    private View drawMarker(double lat, double longi, String name, String title, int layout) {

        View viewMarker1 = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
        Bitmap bmp = dynamicMarker(viewMarker1, name.toUpperCase());

        LatLng mapCenter = new LatLng(lat, longi);
        mCurrLocationMarker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .title(title)
                .position(mapCenter));

        return viewMarker1;
    }


    /**
     * Method to Print Log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Call Reject Friend API
     */
    private void callRejectFriendAPI() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", friendUserId);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.friendReject(getContext(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Response Reject Friend --->" + response.toString());
                int status;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        /**
                         * Here Handle Dialog For Status 1,101,400 Because In Special Case like
                         * For relMain Layout Hide When Receiver Friend Reject Request At That
                         * Time Sender Friend Show Cancel Button Then That Time That Cancel Friend Then
                         * Response Come 101 So That Time Hide relMain
                         */
                        status = response.optInt("status_code");
                        message = response.optString("message");
                        if (status == API_STATUS_ONE) {
                            DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    relMain.setVisibility(View.GONE);
                                }
                            });
                        } else if (status == API_STATUS_ONE_ZERO_ONE) {
                            relMain.setVisibility(View.GONE);
                            // DialogUtility.AlertDialogUtility(getActivity(), message);
                        } else if (status == API_STATUS_FOUR_ZERO_ONE) {
                            DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    ((MainActivity) getActivity()).logout();
                                }
                            });
                        }
                    } catch (Exception e) {
                        logDebug("<<<< Friend Reject Api Response Exception >>>>");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Call Accept Friend API
     */
    private void callAcceptFriendAPI() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", friendUserId);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.friendAccept(getContext(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug(" Response Accept Friend --->" + response.toString());
                int status;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status = response.optInt("status_code");
                        message = response.getString("message");
                        if (status == API_STATUS_ONE) {
                            DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    relMain.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        logDebug("<<<<Friend Accept Api Response Exception>>>>");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Call UnFriend APi
     */
    private void callUnFriendAPI() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", friendUserId);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.unFriend(getContext(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Un Friend Response-->" + response);
                int status_code;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status_code = response.getInt("status_code");
                        if (status_code == Constant.API_STATUS_ONE) {
                            message = response.getString("message");
                            DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    relMain.setVisibility(View.GONE);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Show Or Hide Button According FriendshipStatus
     */
    private void showHideButton(String status) {
        //Visible Send Request Button
        if (status.equalsIgnoreCase(SEND_REQUEST)) {
            if (relMain.getVisibility() == View.VISIBLE) {
            } else {
                relMain.setVisibility(View.VISIBLE);
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_up);
                relMain.startAnimation(animation1);
            }
            btnSendRequest.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.GONE);
            btnUnfriend.setVisibility(View.GONE);
            ll_acceptReject.setVisibility(view.GONE);
        } else {
            if (status.equalsIgnoreCase(SEND_REQUEST)) {
                btnCancel.setVisibility(View.GONE);
                btnUnfriend.setVisibility(View.GONE);
                ll_acceptReject.setVisibility(view.GONE);
            }
        }

        //Visible Cancel Button
        if (status.equalsIgnoreCase(CANCEL_REQUEST)) {
            if (relMain.getVisibility() == View.VISIBLE) {
            } else {
                relMain.setVisibility(View.VISIBLE);
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_up);
                relMain.startAnimation(animation1);
            }
            btnCancel.setVisibility(View.VISIBLE);
            btnSendRequest.setVisibility(View.GONE);
            btnUnfriend.setVisibility(View.GONE);
            ll_acceptReject.setVisibility(view.GONE);
        } else {
            if (status.equalsIgnoreCase(CANCEL_REQUEST)) {
                btnSendRequest.setVisibility(View.GONE);
                btnUnfriend.setVisibility(View.GONE);
                ll_acceptReject.setVisibility(view.GONE);
            }
        }

        //Visible See Profile Button
        if (status.equalsIgnoreCase(SEE_MORE)) {
            if (relMain.getVisibility() == View.VISIBLE) {
            } else {
                relMain.setVisibility(View.VISIBLE);
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_up);
                relMain.startAnimation(animation1);
            }
            btnUnfriend.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.GONE);
            btnSendRequest.setVisibility(View.GONE);
            ll_acceptReject.setVisibility(view.GONE);
        } else {
            if (status.equalsIgnoreCase(SEE_MORE)) {
                btnCancel.setVisibility(View.GONE);
                btnSendRequest.setVisibility(View.GONE);
                ll_acceptReject.setVisibility(view.GONE);
            }
        }

        //Visible Accept/Reject Button
        if (status.equalsIgnoreCase(ACCEPT_REJECT)) {
            if (relMain.getVisibility() == View.VISIBLE) {
            } else {
                relMain.setVisibility(View.VISIBLE);
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_up);
                relMain.startAnimation(animation1);
            }
            ll_acceptReject.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.GONE);
            btnSendRequest.setVisibility(View.GONE);
            btnUnfriend.setVisibility(view.GONE);
        } else {
            if (status.equalsIgnoreCase(ACCEPT_REJECT)) {
                btnCancel.setVisibility(View.GONE);
                btnSendRequest.setVisibility(View.GONE);
                btnUnfriend.setVisibility(view.GONE);
            }
        }
    }

    /**
     * Make Full String Using String Builder
     *
     * @param fName
     * @param lName
     * @return
     */
    private String stringBuilderUserInfo(String fName, String lName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getActivity().getString(R.string.msg_un_friend));
        stringBuilder.append(" ");
        stringBuilder.append(fName);
        stringBuilder.append(" ");
        stringBuilder.append(lName);
        stringBuilder.append(" ");
        stringBuilder.append(getActivity().getString(R.string.msg_as_friend));
        return stringBuilder.toString();
    }

    /**
     * set the status for the execution time in background service
     * when user is in home screen
     * here set 2 for home
     */
    private void setExecutionTimeForService() {
        sharePref.setExecutionTime("2");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        } else {
            Log.e("HOME SCREEN","ENTER");
            getActivity().stopService(new Intent(getActivity(), GpsService.class));
            getActivity().startService(new Intent(getActivity(), GpsService.class));
        }
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return HomeFragment.class.getName();
    }
}