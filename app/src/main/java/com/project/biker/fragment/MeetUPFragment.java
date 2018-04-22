package com.project.biker.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.project.biker.R;
import com.project.biker.ResponseParser.RideParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.google_place_api.AddressService;
import com.project.biker.google_place_api.GooglePlaceApi;
import com.project.biker.google_place_api.GooglePlaceParser;
import com.project.biker.google_place_api.PlacesAutoCompleteAdapter;
import com.project.biker.service.APIService;
import com.project.biker.service.GPSTracker;
import com.project.biker.service.RideService;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.project.biker.adapter.RiderRecycleViewAdapter.selectedBikerLst;
import static com.project.biker.service.UserService.PARAM_DESTINATION;
import static com.project.biker.service.UserService.PARAM_DESTINATION_LATITUDE;
import static com.project.biker.service.UserService.PARAM_DESTINATION_LONGITUDE;
import static com.project.biker.service.UserService.PARAM_INVITED_IDS;
import static com.project.biker.service.UserService.PARAM_MEETUP;
import static com.project.biker.service.UserService.PARAM_MEETUP_LATITUDE;
import static com.project.biker.service.UserService.PARAM_MEETUP_LONGITUDE;
import static com.project.biker.service.UserService.PARAM_MEETUP_TIME;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.MEETUP_GOOGLEMAP_ZOOM_LEVEL;
import static com.project.biker.tools.Constant.MEETUP_GOOGLE_CAMERA_ZOOM;
import static com.project.biker.tools.Constant.MEETUP_GOOGLE_MAP_BEARING;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017.
 */
public class MeetUPFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MeetUPFragment";
    //Google Place APi Parser
    private GooglePlaceParser googlePlaceParser;

    AutoCompleteTextView autoDialogMeetingLocationWhereGoing, edDialogMeetingLocationSetMeetUp;

    TextView bikerName;

    ImageView ivNext;

    SharePref sharePref;

    String userSelectedCityStateCountry;
    View view;

    TextView tvError;

    String ids;
    String userId;
    String sessionId;
    String meetUpPlace;
    String destinationPlace;
    String meetUpLatitude;
    String meetUpLongitude;
    String destinationLatitude;
    String destinationLongitude;
    String date;
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    GPSTracker gps;

    private boolean isTouchMeetUpSpot = false, isTouchWhereGo = false, isMapMove = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Remove old view to avoid duplicate id exception
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_meet_up, container, false);
        } catch (InflateException e) {
            logDebug("<<<<<<< view inflater Exception >>>>>>>>>");
            e.printStackTrace();
        }

        //Initialization View
        initializeView(view);
        //Object Initializer
        objectInitializer();



        // for the default selection of source
        defaultSourceSelection();

        edDialogMeetingLocationSetMeetUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edDialogMeetingLocationSetMeetUp.requestFocus();
                isTouchMeetUpSpot = true;
                isTouchWhereGo = false;
                bikerName.setText(getString(R.string.set_meeting_location));
                return false;
            }
        });

        autoDialogMeetingLocationWhereGoing.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoDialogMeetingLocationWhereGoing.requestFocus();
                isTouchWhereGo = true;
                isTouchMeetUpSpot = false;
                bikerName.setText(getString(R.string.set_destination));
                return false;
            }
        });
        edDialogMeetingLocationSetMeetUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvError.setVisibility(View.GONE);
            }
        });

        autoDialogMeetingLocationWhereGoing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvError.setVisibility(View.GONE);
            }
        });


        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectDataForRideCreation();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new RideFragment());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, MEETUP_GOOGLE_CAMERA_ZOOM));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(MEETUP_GOOGLEMAP_ZOOM_LEVEL)
                .bearing(MEETUP_GOOGLE_MAP_BEARING)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                isMapMove = true;
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Cleaning all the markers.
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                }
                double latitude = googleMap.getCameraPosition().target.latitude;
                double longitude = googleMap.getCameraPosition().target.longitude;

                if (latitude > 0 && longitude > 0) {
                    getFullLocation(latitude, longitude);
                }
            }
        });
    }

    /**
     * Initialization View
     *
     * @param view
     */
    private void initializeView(View view) {
        autoDialogMeetingLocationWhereGoing = (AutoCompleteTextView) view.findViewById(R.id.autoDialogMeetingLocationWhereGoing);
        edDialogMeetingLocationSetMeetUp = (AutoCompleteTextView) view.findViewById(R.id.edDialogMeetingLocationSetMeetup);
        ivNext = (ImageView) view.findViewById(R.id.ivNext);
        tvError = (TextView) view.findViewById(R.id.tvError);
        bikerName = (TextView) view.findViewById(R.id.bikerName);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapMeetUp);
    }

    /**
     * Object Initializer
     */
    private void objectInitializer() {
        mapFragment.getMapAsync(this);
        sharePref = new SharePref(getActivity());
        gps = new GPSTracker(getActivity());
        //Call Google Place API
        callGooglePlaceAPI(getContext(), autoDialogMeetingLocationWhereGoing, "destination");
        callGooglePlaceAPI(getContext(), edDialogMeetingLocationSetMeetUp, "source");
    }

    /**
     * City,State and Country Change dialog
     */
    private void callGooglePlaceAPI(final Context context, final AutoCompleteTextView act_dialogChangeCityStateCountry,
                                    final String flagString) {


        act_dialogChangeCityStateCountry.setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.row_google_autocomplete_text, false));
        act_dialogChangeCityStateCountry.setCursorVisible(false);

		/* AutoComplete on click */
        act_dialogChangeCityStateCountry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                act_dialogChangeCityStateCountry.setFocusableInTouchMode(true);
                act_dialogChangeCityStateCountry.requestFocus();
                act_dialogChangeCityStateCountry.setCursorVisible(true);
            }
        });


		/* AutoComplete on item click */
        act_dialogChangeCityStateCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Get data associated with the specified position in the list
				 * (AdapterView)
				 */
                hideSoftKeyboard(getActivity());

                act_dialogChangeCityStateCountry.setFocusableInTouchMode(false);
                act_dialogChangeCityStateCountry.setCursorVisible(false);
                HashMap<String, Object> mapPlaceIdAndDesc = GooglePlaceApi.getPlaceIdAndDesc(position);

                act_dialogChangeCityStateCountry.setText(mapPlaceIdAndDesc.get("DESCRIPTION").toString());

				/* set user selected city,country,state */
                userSelectedCityStateCountry = mapPlaceIdAndDesc.get("DESCRIPTION").toString();
                String place_id = mapPlaceIdAndDesc.get("PLACE_ID").toString();
                act_dialogChangeCityStateCountry.setText(userSelectedCityStateCountry);

                //Call API For Get The Address
                AddressService.getAddress(context, getPlaceInfo(place_id), new APIService.Success<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        logDebug("Place Response-->" + response.toString());
                        googlePlaceParser = GooglePlaceParser.parseResponse(response);

                        if (flagString.equals("source")) {
                            meetUpLatitude = googlePlaceParser.getLatitude();
                            meetUpLongitude = googlePlaceParser.getLongitude();
                            logDebug("source Latitude-->" + googlePlaceParser.getLatitude());
                            logDebug("source Longitude-->" + googlePlaceParser.getLongitude());
                        } else if (flagString.equals("destination")) {
                            destinationLatitude = googlePlaceParser.getLatitude();
                            destinationLongitude = googlePlaceParser.getLongitude();
                            logDebug("Destination Latitude-->" + googlePlaceParser.getLatitude());
                            logDebug("Destination Longitude-->" + googlePlaceParser.getLongitude());
                        }
                    }
                });
            }
        });
    }


    /**
     * Pass Place Id  And Return Full API
     *
     * @param input
     * @return
     */
    public static String getPlaceInfo(String input) {
        String place = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + input + "&key=" + Constant.API_KEY;
        logDebug("FINAL URL:::" + place);
        //return urlString.toString();
        return place;
    }

    /**
     * Call the Api for create ride
     */
    private void createRideApi() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, userId);
        param.put(PARAM_INVITED_IDS, ids);
        param.put(PARAM_SESSION_ID, sessionId);
        param.put(PARAM_MEETUP, meetUpPlace.trim());
        param.put(PARAM_DESTINATION, destinationPlace.trim());
        param.put(PARAM_MEETUP_LATITUDE, meetUpLatitude);
        param.put(PARAM_MEETUP_LONGITUDE, meetUpLongitude);
        param.put(PARAM_DESTINATION_LATITUDE, destinationLatitude);
        param.put(PARAM_DESTINATION_LONGITUDE, destinationLongitude);
        param.put(PARAM_MEETUP_TIME, "");
        param.put(PARAM_PLATFORM, PLATFORM);

        RideService.createBikeRide(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("CreateRide Response-->" + response.toString());

                RideParser.CreateRideResponse createRideResponse = RideParser.CreateRideResponse.createRideResponse(response);
                if (createRideResponse != null && createRideResponse.getStatusCode() == API_STATUS_ONE) {
                    sharePref.setRideId(createRideResponse.getRiderId());
                    ((MainActivity) getContext()).loadFragment(new MeetUpRouteFragment(), false);
                }
                createRideResponse.getRiderId();
            }
        });
    }

    /**
     * To Hide KeyBoard
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Method for to Print Log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }



    /**
     * Get Address From The Geo Coder
     *
     * @param latitude
     * @param longitude
     */
    private void getFullLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                String addressName = address + " " + city + " " + state + " " + country + " " + postalCode + " " + knownName;

                logDebug("Address Name : " + address + city + state + country + knownName);
                if (isTouchMeetUpSpot && isMapMove) {
                    meetUpLatitude = String.valueOf(latitude);
                    meetUpLongitude = String.valueOf(longitude);
                    edDialogMeetingLocationSetMeetUp.setText(addressName);
                }
                if (isTouchWhereGo && isMapMove) {
                    destinationLatitude = String.valueOf(latitude);
                    destinationLongitude = String.valueOf(longitude);
                    autoDialogMeetingLocationWhereGoing.setText(addressName);
                }
            }
        } catch (IOException e) {
            logDebug("<<<< get full location exception >>>>");
            e.printStackTrace();
        }
    }

    /**
     * this method create the collect
     * ids in comma seperated formate
     * and date and time
     */
    private void collectDataForRideCreation() {
        meetUpPlace = edDialogMeetingLocationSetMeetUp.getText().toString();
        destinationPlace = autoDialogMeetingLocationWhereGoing.getText().toString();

        if (meetUpPlace.equals("") && destinationPlace.equals("")) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.selectMeetUpSpotAndDestination));
        } else if (meetUpPlace.equals("")) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.selectMeetUpSpot));
        } else if (destinationPlace.equals("")) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.selectDestination));
        } else {
            for (int i = 0; i < selectedBikerLst.size(); i++) {
                if (i == 0) {
                    ids = selectedBikerLst.get(i);
                } else {
                    ids = ids + "," + selectedBikerLst.get(i);
                }

            }
            userId = sharePref.getUserId();
            sessionId = sharePref.getSessionId();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df3 = new SimpleDateFormat(getString(R.string.dateFormate));
            String formattedDate3 = df3.format(c.getTime());
            date = formattedDate3;
            if (isNetworkAvailable(getActivity())) {
                createRideApi();
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * this method is set the default source
     * editText selection
     */
    private void defaultSourceSelection() {
        edDialogMeetingLocationSetMeetUp.setFocusableInTouchMode(true);
        edDialogMeetingLocationSetMeetUp.requestFocus();
        edDialogMeetingLocationSetMeetUp.setCursorVisible(true);
        isTouchMeetUpSpot = true;
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return MeetUPFragment.class.getName();
    }

}
