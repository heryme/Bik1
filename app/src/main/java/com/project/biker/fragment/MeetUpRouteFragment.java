package com.project.biker.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.project.biker.R;
import com.project.biker.ResponseParser.RideParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.model.INTFConfirmYesNo;
import com.project.biker.model.ModelGetRiderLatLong;
import com.project.biker.service.APIService;
import com.project.biker.service.GPSTracker;
import com.project.biker.service.GpsService;
import com.project.biker.service.RideService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.CustomButton;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.project.biker.R.id.map;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_RIDER_ID;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_GOOGLE_CAMERA_ZOOM;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_GOOGLE_MAP_BEARING;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_GOOGLE_MAP_ROTATION;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_GOOGLE_MAP_ZOOM_LEVEL;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_MAP_POLY_LINE_WIDTH;
import static com.project.biker.tools.Constant.MEETUP_ROUTE_RIDER_LAT_LONG_API_INTERVAL;
import static com.project.biker.tools.Constant.STATUS_RIDE_COMPLETED;
import static com.project.biker.tools.Constant.STATUS_RIDE_NOT_STARTED;
import static com.project.biker.tools.Constant.STATUS_RIDE_STARTED;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

/**
 * Created by Vikas Patel on 7/18/2017.
 */

public class MeetUpRouteFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private static final String TAG = "MeetUPFragment";
    View view;
    SupportMapFragment mapFragment;
    Marker mCurrLocationMarker = null;
    GoogleMap googleMap;
    GPSTracker gps;
    SharePref sharePref;

    ArrayList<LatLng> markerPoints = new ArrayList<>();
    Timer timer;

    // RelativeLayout rr_frg_marker;

    ImageView ivMyLocation;
    ImageView ivRowInviteFriendProfile;

    String ProfileImage;

    int riderListSize;


    double meetUpLat;
    double meetUpLong;

    double destinationLat;
    double destinationLong;

    CustomButton btnStart;
    CustomButton btnStop;

    String getRideStatus;

    Boolean startStatus = false;
    Boolean polyLineStatus = true;
    Boolean riderListSizeStatus = true;

    float zoom;

    int polyLineColor;


    ArrayList<Marker> markersToClear = new ArrayList<Marker>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_meetuprout, container, false);
        } catch (InflateException e) {
            logDebug("<<<<< onCreateView Exception >>>>>");
            e.printStackTrace();
        }

        // Receive bundle data
        getBundleData();

        // initialize the xml view
        initializeView(view);
        //Object Initializer
        objectInitializer();
        //Register Click Listener
        initListener();
        //Set execution time for service
        setExecutionTimeForService();
        //call Rider status api
        riderStatusApi();
        // ask user to on location service from phone
        locationSetting();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new RideFragment());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);


        LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, MEETUP_ROUTE_GOOGLE_MAP_ZOOM_LEVEL));


        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        mCurrLocationMarker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_loction_blue))
                .position(mapCenter)
                .flat(true)
                .rotation(MEETUP_ROUTE_GOOGLE_MAP_ROTATION));

        markersToClear.add(mCurrLocationMarker);

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(MEETUP_ROUTE_GOOGLE_MAP_ZOOM_LEVEL)
                .bearing(MEETUP_ROUTE_GOOGLE_MAP_BEARING)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // rr_frg_marker.setVisibility(View.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Toast.makeText(getActivity(),"Marker Click", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMyLocation: {
                moveCurrentLocation();
                break;
            }
            case R.id.btnStart: {
                if (isNetworkAvailable(getActivity())) {
                    alertDialogStart();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btnStop: {
                if (isNetworkAvailable(getActivity())) {
                    alertDialogStop();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    /**
     * Initialize the xml view
     *
     * @param view
     */
    private void initializeView(View view) {

        ivMyLocation = (ImageView) view.findViewById(R.id.ivMyLocation);
        ivRowInviteFriendProfile = (ImageView) view.findViewById(R.id.ivRowInviteFrndProfile);
        btnStart = (CustomButton) view.findViewById(R.id.btnStart);
        btnStop = (CustomButton) view.findViewById(R.id.btnStop);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Object Initializer
     */
    private void objectInitializer() {
        sharePref = new SharePref(getActivity());
        gps = new GPSTracker(getActivity());
        polyLineColor = getResources().getColor(R.color.tab_color);
    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        ivMyLocation.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    /**
     * This is handler for the call getRiderLatLong api every 1 minute
     */
    private void callEveryMinitHandler() {
        final Handler handler = new Handler();
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (isNetworkAvailable(getActivity())) {
                                if (sharePref.getRideId().equalsIgnoreCase(""))
                                    timer.cancel();
                                else
                                    getRiderLAtLongApi();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // error, do something
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, MEETUP_ROUTE_RIDER_LAT_LONG_API_INTERVAL);  // interval of one minute*/
    }

    /**
     * CAll API FOR GET LAT LONG
     */
    private void getRiderLAtLongApi() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);

        RideService.RiderLatLong(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("RiderLatLong Response-->" + response.toString());

                RideParser.RiderLatLong createRideResponse = RideParser.RiderLatLong.riderLatLong(response);

                if (createRideResponse != null && createRideResponse.getStatusCode() == API_STATUS_ONE) {

                    getRideStatus = createRideResponse.getRideStatusName();//Not Started, Started, Completed, Canceled

                    meetUpLat = Double.parseDouble(createRideResponse.getMeetUpLatitude());
                    meetUpLong = Double.parseDouble(createRideResponse.getMeetUpLongitude());

                    destinationLat = Double.parseDouble(createRideResponse.getDestinationLatitude());
                    destinationLong = Double.parseDouble(createRideResponse.getDestinationLongitude());

                    //getting last zoom level of google map
                    zoom = googleMap.getCameraPosition().zoom;

                    if (getRideStatus.equalsIgnoreCase(STATUS_RIDE_NOT_STARTED)) {
                        addMarker(createRideResponse.riderDetailList, createRideResponse.getMeetUpLatitude(), createRideResponse.getMeetUpLongitude());
                    } else if (getRideStatus.equalsIgnoreCase(STATUS_RIDE_STARTED)) {

                        addMarkerWhenRideStart(createRideResponse.riderDetailList, createRideResponse.getMeetUpLatitude(), createRideResponse.getMeetUpLongitude());
                    } else if (getRideStatus.equalsIgnoreCase(STATUS_RIDE_COMPLETED)) {

                        DialogUtility.alertOk(getActivity(), getString(R.string.ride_complete_alert), new INTFAlertOk() {
                            @Override
                            public void alertOk() {
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(i);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Adding Marker to Googlemap comming from api
     *
     * @param riderList
     * @param meetLati
     * @param meetLongi
     */

    private void addMarker(ArrayList<ModelGetRiderLatLong> riderList, String meetLati, String meetLongi) {
        googleMap.clear();
        markerPoints.clear();

        for (int i = 0; i < riderList.size(); i++) {

            String name;

            /**
             * Below if condition to check the user itself
             * for marker title "YOU"
             */

            if (sharePref.getUserId().equals(riderList.get(i).getUserId())) {

                /**
                 * Below if condition to show the ride start and stop button
                 * only the ride creator
                 */

                if (riderList.get(i).getUserType().equalsIgnoreCase("1")) {
                    if (!startStatus) {
                        btnStart.setVisibility(View.VISIBLE);
                    } else if (startStatus) {
                        btnStop.setVisibility(View.VISIBLE);
                    }

                }
                name = getString(R.string.you);
                int layout = R.layout.dynamic_green_myposition_marker;
                double lat = Double.parseDouble(riderList.get(i).getRiderLat());
                double longi = Double.parseDouble(riderList.get(i).getRiderLong());
                LatLng mapCenter = drawMarker(lat, longi, name, layout);
                markerPoints.add(mapCenter);

            } else {

                /**
                 * Change the Marker color according to friend status
                 * 1 = Friend
                 * 0 = UnFriend
                 */
                int layout;
                if (riderList.get(i).getIsFriend().equalsIgnoreCase("1")) {
                    layout = R.layout.dynamic_green_marker_for_meetup;
                } else {
                    layout = R.layout.dynamic_marker_purple;
                }
                name = riderList.get(i).getFirstName()/* + " " + riderList.get(i).getLastName()*/;
                double lat = Double.parseDouble(riderList.get(i).getRiderLat());
                double longi = Double.parseDouble(riderList.get(i).getRiderLong());
                LatLng mapCenter = drawMarker(lat, longi, name, layout);
                markerPoints.add(mapCenter);
            }


        }

        String name = getString(R.string.meetHere);
        int layout = R.layout.dynamic_marker_bule_meet_up_point;
        double lat = Double.parseDouble(meetLati);
        double longi = Double.parseDouble(meetLongi);
        drawMarker(lat, longi, name, layout);

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));

        createDirectionUrl();

    }

    /**
     * Add Marker after user start ride
     *
     * @param riderlist
     * @param meetLati
     * @param meetLongi
     */


    private void addMarkerWhenRideStart(ArrayList<ModelGetRiderLatLong> riderlist, String meetLati, String meetLongi) {

        /**
         * we need to call google map clear
         * method only one time when method is call
         * for this reason here we put condition
         */
        if (polyLineStatus) {
            googleMap.clear();
        }
        markerPoints.clear();
        for (Marker marker : markersToClear) {
            marker.remove();
        }
        markersToClear.clear();

        double meetlat = Double.parseDouble(meetLati);
        double meetlong = Double.parseDouble(meetLongi);
        LatLng meetlatlong = new LatLng(meetlat, meetlong);

        markerPoints.add(meetlatlong);

        for (int i = 0; i < riderlist.size(); i++) {

            String name;
            /**
             * Below if condition to check the user itself
             * for marker title "YOU"
             */
            if (sharePref.getUserId().equals(riderlist.get(i).getUserId())) {

                /**
                 * Below if condition to show the ride start and stop button
                 * only the ride creator
                 */
                if (riderlist.get(i).getUserType().equalsIgnoreCase("1")) {
                    if (!startStatus) {
                        btnStart.setVisibility(View.VISIBLE);
                    } else if (startStatus) {
                        btnStop.setVisibility(View.VISIBLE);
                    }
                }

                name = getString(R.string.you);
                int layout = R.layout.dynamic_green_myposition_marker;
                double lat = Double.parseDouble(riderlist.get(i).getRiderLat());
                double longi = Double.parseDouble(riderlist.get(i).getRiderLong());
                drawMarker(lat, longi, name, layout);

            } else {

                /**
                 * Change the Marker color according to friend status
                 * 1 = Friend
                 * 0 = UnFriend
                 */
                int layout;
                if (riderlist.get(i).getIsFriend().equalsIgnoreCase("1")) {
                    layout = R.layout.dynamic_green_marker_for_meetup;
                } else {
                    layout = R.layout.dynamic_marker_purple;
                }
                name = riderlist.get(i).getFirstName()/* + " " + riderlist.get(i).getLastName()*/;
                double lat = Double.parseDouble(riderlist.get(i).getRiderLat());
                double longi = Double.parseDouble(riderlist.get(i).getRiderLong());
                drawMarker(lat, longi, name, layout);
            }
        }
        String name = getString(R.string.meetupPoint);
        int layout = R.layout.dynamic_marker_bule_meet_up_point;
        drawMarker(meetUpLat, meetUpLong, name, layout);

        String name1 = getString(R.string.destination);
        int layout1 = R.layout.dynamic_marker_bule_meet_up_point;
        drawMarker(destinationLat, destinationLong, name1, layout1);

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));

        /**
         * we need to call polyLine
         * method only one time when method is call
         * for this reason here we put condition
         */
        if (polyLineStatus) {
            createDirectionUrl();
            polyLineStatus = false;
            logDebug("Polyline Enter");
        }

    }


    /**
     * Inflate the xml file as dynamic marker
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
     * Getting Direction Url
     *
     * @param origin
     * @param dest
     * @return
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // String sensor = "sensor=false&units=metric&mode=walking";

        //Adding Alternative parameter
        //   String alternative = "alternatives=true";

        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        // String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        String parameters = str_origin + "&" + str_dest + "&" + sensor;//+ "&" + alternative;   // map without waypoint point
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        logDebug(" url :: " + url);

        return url;
    }

    /**
     * This method download json from url and parse data
     *
     * @param strUrl
     * @return
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            logDebug("Exception while" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        logDebug("DOWN LOAD JSON DATA FROM URL :: " + data);
        return data;
    }

    /**
     * Featching Data From url
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                logDebug("Background Task" + e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data

            logDebug("Result data" + result);
            parserTask.execute(result);
        }
    }

    /**
     * This Method is drow the path
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                //   Log.e("RRR", "DirectionsJSONParser :: " + parser.toString());
                //  Toast.makeText(getActivity(), parser.toString(), Toast.LENGTH_SHORT).show();

                // Starts parsing data
                routes = parser.parse(jObject);

                for (int i = 0; i < parser.viaRoute.size(); i++) {
                    logDebug("Via" + parser.viaRoute.get(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null && result.size() > 0) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                    }


                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(MEETUP_ROUTE_MAP_POLY_LINE_WIDTH);
                    lineOptions.color(polyLineColor);
                    lineOptions.geodesic(true);
                }
            }

            // Drawing polyline in the Google Map for the i-th route

            if (lineOptions != null) {
                Polyline polyline = googleMap.addPolyline(lineOptions);
                polyline.setJointType(JointType.ROUND);
                polyline.setStartCap(new RoundCap());
                polyline.setEndCap(new RoundCap());
            }
            //   googleMap.addPolyline(lineOptions);
        }
    }

    /**
     * This for Creating Direction URL
     */
    private void createDirectionUrl() {
        for (int i = 0; i < markerPoints.size(); i++) {
            String url = "";
            LatLng origin = markerPoints.get(i);

            if (getRideStatus.equalsIgnoreCase(STATUS_RIDE_NOT_STARTED)) {

                LatLng dest = new LatLng(meetUpLat, meetUpLong);
                url = getDirectionsUrl(origin, dest);

            } else if (getRideStatus.equalsIgnoreCase(STATUS_RIDE_STARTED)) {

                LatLng dest = new LatLng(destinationLat, destinationLong);
                url = getDirectionsUrl(origin, dest);

            }


            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            logDebug("URL" + url);
            downloadTask.execute(url);
        }
    }

    /**
     * Move user to current location in google map
     */
    private void moveCurrentLocation() {
        if (gps != null && gps.getLatitude() != 0f && gps.getLongitude() != 0f) {
            LatLng mapCenter = new LatLng(gps.getLatitude(), gps.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, MEETUP_ROUTE_GOOGLE_CAMERA_ZOOM));

            // Flat markers will rotate when the map is rotated,
            // and change perspective when the map is tilted.
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(mapCenter)
                    .zoom(MEETUP_ROUTE_GOOGLE_MAP_ZOOM_LEVEL)
                    .bearing(MEETUP_ROUTE_GOOGLE_MAP_BEARING)
                    .build();

            // Animate the change in camera view over 2 seconds
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    /**
     * Api call to start Ride
     */
    private void rideStartApi() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);


        RideService.RideStart(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Ride Start Response-->" + response.toString());

                RideParser.RideStart rideStart = RideParser.RideStart.rideStart(response);
                if (rideStart != null && rideStart.getStatusCode() == API_STATUS_ONE) {
                    btnStart.setVisibility(View.GONE);
                    btnStop.setVisibility(View.VISIBLE);
                    startStatus = true;
                    sharePref.setRideId(rideStart.getRiderId());
                    callEveryMinitHandler();
                }
            }
        });
    }

    /**
     * Api call to stop Ride
     */
    private void rideStop() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_RIDER_ID, sharePref.getRideId());
        param.put(PARAM_PLATFORM, PLATFORM);

        RideService.RideStop(getActivity(), param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Ride stop Response-->" + response.toString());
                RideParser.RideStop rideStop = RideParser.RideStop.rideStop(response);
                if (rideStop != null && rideStop.getStatusCode() == API_STATUS_ONE) {
                    sharePref.setRideId("");
                    sharePref.setExecutionTime("0");
                    alertDialogStopRide(getActivity(), rideStop.getMessage());
                }
            }
        });
    }

    /**
     * Api to update user status
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
                    if (rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_NOT_STARTED)) {
                        startStatus = false;
                    } else if (rideStatusResponse.getRideStatusName().equalsIgnoreCase(STATUS_RIDE_STARTED)) {
                        startStatus = true;
                    }
                    callEveryMinitHandler();
                }
            }
        });
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
    private LatLng drawMarker(double lat, double longi, String name, int layout) {

        View viewMarker1 = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
        Bitmap bmp = dynamicMarker(viewMarker1, name.toUpperCase());

        LatLng mapCenter = new LatLng(lat, longi);
        mCurrLocationMarker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .position(mapCenter));

        markersToClear.add(mCurrLocationMarker);
        logDebug("Marker to Enter");

        return mapCenter;
    }

    /**
     * Alertdialog to display the ride stop successfully
     *
     * @param context
     * @param message
     */
    public void alertDialogStopRide(final Context context, String message) {

        DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
            @Override
            public void alertOk() {
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        });
    }

    /**
     * Alertdialog to Alert user for ride start
     */
    private void alertDialogStart() {
        DialogUtility.confirmYesNo(getActivity(), getString(R.string.start_ride_alertmessage), new INTFConfirmYesNo() {
            @Override
            public void yesClick() {
                rideStartApi();
            }
        });
    }

    /**
     * Alertdialog to Alert user for ride stop
     */
    private void alertDialogStop() {
        DialogUtility.confirmYesNo(getActivity(), getString(R.string.stop_ride_alertmessage), new INTFConfirmYesNo() {
            @Override
            public void yesClick() {
                rideStop();

            }
        });
    }

    /**
     * Getting the BundleData
     */
    private void getBundleData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            startStatus = bundle.getBoolean("startStatus", false);
        }
    }

    /**
     * Ask the user to on location setting
     */
    private void locationSetting() {
        if (sharePref.getUserStatus().equals("1")) {
            ((MainActivity) getContext()).displayLocationSettingsRequest(getActivity());
        }
    }

    /**
     * Method To Print Log
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
        return MeetUpRouteFragment.class.getName();
    }


    /**
     * set the status for the execution time in background service
     * when user is in home screen
     * here set 1 for ride
     */
    private void setExecutionTimeForService() {
        sharePref.setExecutionTime("1");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        } else {
            getActivity().stopService(new Intent(getActivity(), GpsService.class));
            getActivity().startService(new Intent(getActivity(), GpsService.class));
        }
    }

}
