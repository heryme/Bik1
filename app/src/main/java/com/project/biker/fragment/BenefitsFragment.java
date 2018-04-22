package com.project.biker.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.biker.R;
import com.project.biker.ResponseParser.BenefitParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.adapter.BenefitsAdapter;
import com.project.biker.model.ModelBenefitList;
import com.project.biker.service.APIService;
import com.project.biker.service.BenefitService;
import com.project.biker.service.GPSTracker;
import com.project.biker.tools.SharePref;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.project.biker.fragment.HomeFragment.createDrawableFromView;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.BENEFIT_MAP_BEARING;
import static com.project.biker.tools.Constant.BENEFIT_MAP_ZOOMING;
import static com.project.biker.tools.Constant.BENEFIT_SEEMORE_ANIMATION_DELAY;
import static com.project.biker.tools.Constant.BENEFIT_ZOOM_GOOGLE_CAMERA;
import static com.project.biker.tools.Constant.SHOW_BENEFIT_LIST;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017
 */
public class BenefitsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener {

    private static final String TAG = "BenefitsFragment";

    /**
     * Button
     */
    Button btnFrgBenefitSeeMore;

    /**
     * Toolbar
     */
    Toolbar toolbar;

    /**
     * Toggle Button
     */
    ToggleButton toggle_toolbar;

    /**
     * TextView
     */
    TextView tvToolBarTitle;

    /**
     * RecyclerView
     */
    RecyclerView rv_frg_benefits;

    /**
     * Fragment
     */
    SupportMapFragment mapFragment;

    /**
     * Google Map
     */
    GoogleMap googleMap;

    /**
     * Marker
     */
    Marker mCurrLocationMarker = null;

    /**
     * Relative layout
     */
    RelativeLayout rl_benefit_frg;

    ImageView ivMyLocation;

    TextView tvFrgBenefitTitle;
    TextView txtNoBenefit;

    SharePref sharePref;
    GPSTracker gps;

    BenefitsAdapter benefitsAdapter;
    BenefitParser benefitResponse;

    Animation animation;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Check the view is already exist or not to avoid duplicate id issue
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_benefits, container, false);
        } catch (InflateException e) {
            logDebug("<<<<<<<< View inflate () Exception >>>>>>>>>");
            e.printStackTrace();
        }

        //Set the tab text color
        ((MainActivity) getContext()).textChange(new BenefitsFragment());

        // initialize the xml component
        initializeView(view);

        // set visibility of the seeMore
        visibleSeeMore();

        // Ask user to on location if user online
        locationSetting();

        //Call Benefit API
        apiCallBenefit();

        // initialize the animation object for animation
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_up);

        // initialize the on click listener
        initializeOnClick();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        View viewMarker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dynamic_green_myposition_marker, null);
        dynamicMarker(viewMarker, getString(R.string.markerYou));

        LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, BENEFIT_ZOOM_GOOGLE_CAMERA));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(BENEFIT_MAP_ZOOMING)
                .bearing(BENEFIT_MAP_BEARING)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equalsIgnoreCase(getString(R.string.markerYouTitle))) {

        } else {
            String id = marker.getTitle();
            String BenefitName = "";
            String BenefitCoverUrl = "";
            String StoreAddress = "";
            String StoreLogoUrl = "";
            String BenefitId = "";

            int size = BenefitParser.benefitLists.size();

            for (int i = 0; i < size; i++) {

                if (id.equalsIgnoreCase(BenefitParser.benefitLists.get(i).getBenefitId())) {
                    ModelBenefitList modelBenefitList1 = BenefitParser.benefitLists.get(i);
                    BenefitName = modelBenefitList1.getBenefitName();
                    BenefitCoverUrl = modelBenefitList1.getBenefitCoverUrl();
                    StoreAddress = modelBenefitList1.getBenefitFormattedAddress();
                    StoreLogoUrl = modelBenefitList1.getBenefitLogoUrl();
                    BenefitId = modelBenefitList1.getBenefitId();
                    break;
                }
            }

            BenefitsItemFragment benefitsItemFragment = new BenefitsItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("BenefitName", BenefitName);
            bundle.putString("CoverUrl", BenefitCoverUrl);
            bundle.putString("StoreAddress", StoreAddress);
            bundle.putString("StoreLogo", StoreLogoUrl);
            bundle.putString("BenefitId", BenefitId);
            benefitsItemFragment.setArguments(bundle);

            ((MainActivity) getContext()).loadFragment(benefitsItemFragment, false);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
       /* getChildFragmentManager().beginTransaction()
                .remove(mapFragment)
                .commit();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new BenefitsFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFrgBenefitSeeMore: {
                rl_benefit_frg.setVisibility(View.VISIBLE);
                btnFrgBenefitSeeMore.setVisibility(View.GONE);
                rl_benefit_frg.startAnimation(animation);
                break;
            }
            case R.id.tvFrgBenefitTitle: {

                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_down);
                animation1.setFillAfter(false);
                rl_benefit_frg.startAnimation(animation1);
                rl_benefit_frg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rl_benefit_frg.setVisibility(View.GONE);
                        btnFrgBenefitSeeMore.setVisibility(View.VISIBLE);
                    }
                }, BENEFIT_SEEMORE_ANIMATION_DELAY);

                break;
            }
            case R.id.ivMyLocation: {
                moveCurrentLocation();
                break;
            }

        }
    }

    /***
     * initializeView
     * @param v
     */
    private void initializeView(View v) {

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        tvToolBarTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toggle_toolbar = (ToggleButton) toolbar.findViewById(R.id.toggle_toolbar);
        btnFrgBenefitSeeMore = (Button) v.findViewById(R.id.btnFrgBenefitSeeMore);
        rv_frg_benefits = (RecyclerView) v.findViewById(R.id.rv_frg_benefits);
        rl_benefit_frg = (RelativeLayout) v.findViewById(R.id.rl_benefit_frg);
        tvFrgBenefitTitle = (TextView) v.findViewById(R.id.tvFrgBenefitTitle);
        ivMyLocation = (ImageView) v.findViewById(R.id.ivMyLocation);
        txtNoBenefit = (TextView) v.findViewById(R.id.txtNobenefit);


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapbenefit);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(getActivity());
        sharePref = new SharePref(getActivity());

    }

    /**
     * Call All Benefit API
     */
    private void allBenefitApi() {

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_PLATFORM, PLATFORM);

        BenefitService.allBenefits(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Benefits Response-->" + response.toString());
                benefitResponse = BenefitParser.benefitResponse(response);
                if (benefitResponse != null && benefitResponse.getStatusCode() == API_STATUS_ONE) {
                    addMarker(benefitResponse.benefitLists);
                    // RecyclerView Adapter
                    if (benefitResponse.benefitLists.size() > 0) {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        rv_frg_benefits.setLayoutManager(mLayoutManager);
                        benefitsAdapter = new BenefitsAdapter(getContext(), benefitResponse.benefitLists);
                        rv_frg_benefits.setAdapter(benefitsAdapter);
                        txtNoBenefit.setVisibility(View.GONE);
                    } else {
                        txtNoBenefit.setVisibility(View.VISIBLE);
                    }

                }

            }
        });
    }

    /**
     * addMarker to googleMap
     *
     * @param benefitLists
     */
    private void addMarker(ArrayList<ModelBenefitList> benefitLists) {
        View viewMarker;

        for (int i = 0; i < benefitLists.size(); i++) {

            int layout = R.layout.dynamic_marker_red;
            double latitude = Double.parseDouble(benefitLists.get(i).getLati());
            double longitude = Double.parseDouble(benefitLists.get(i).getLongi());
            String title = benefitLists.get(i).getBenefitId();
            String name = benefitLists.get(i).getBenefitName();

            drawMarker(latitude, longitude, name, title, layout);
        }

        Double latitude = 0.0;
        Double longitude = 0.0;
        int layout = R.layout.dynamic_green_myposition_marker;
        String title = getString(R.string.markerYouTitle);
        String name = getString(R.string.markerYou);

        if (gps.getLatitude() == 0.0) {
            latitude = Double.valueOf(sharePref.GetLat());
            longitude = Double.valueOf(sharePref.GetLong());
        } else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        drawMarker(latitude, longitude, name, title, layout);

        LatLng mapCenter = new LatLng(latitude,longitude);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(BENEFIT_MAP_ZOOMING)
                .bearing(BENEFIT_MAP_BEARING)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

      //  googleMap.animateCamera(CameraUpdateFactory.zoomTo(MAP_ZOOMING));
    }

    /**
     * Method to Inflate Dynamic Marker view
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
     * Move user to current location
     */
    private void moveCurrentLocation() {
        if (gps != null && gps.getLatitude() != 0f && gps.getLongitude() != 0f) {
            LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, BENEFIT_ZOOM_GOOGLE_CAMERA));

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(mapCenter)
                    .zoom(BENEFIT_MAP_ZOOMING)
                    .bearing(BENEFIT_MAP_BEARING)
                    .build();

            // Animate the change in camera view over 2 seconds
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    /**
     * Initialize the on click listener
     */
    private void initializeOnClick() {
        btnFrgBenefitSeeMore.setOnClickListener(this);
        tvFrgBenefitTitle.setOnClickListener(this);
        ivMyLocation.setOnClickListener(this);

    }

    /**
     * Set the visibility of seeMore Button
     * according to constant flag
     */
    private void visibleSeeMore() {
        if (SHOW_BENEFIT_LIST.equalsIgnoreCase("1")) {
            rl_benefit_frg.setVisibility(View.VISIBLE);
            btnFrgBenefitSeeMore.setVisibility(View.GONE);
            SHOW_BENEFIT_LIST = "0";
        }
    }

    /**
     * Call the benefit api if user online
     */
    private void apiCallBenefit() {
        if (isNetworkAvailable(getActivity())) {
            allBenefitApi();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Ask for location setting if user online
     */
    private void locationSetting() {
        if (sharePref.getUserStatus().equals("1")) {
            ((MainActivity) getContext()).locationCheck();
        }
    }

    /**
     * Method for Log Printing
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
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
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     * @return
     */
    public static String getFragmentName()
    {
        return BenefitsFragment.class.getName();
    }

}
