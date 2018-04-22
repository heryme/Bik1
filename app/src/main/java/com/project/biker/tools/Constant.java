package com.project.biker.tools;

/**
 * Created by sagarp on 29/4/16.
 */
public class Constant {

    public static final String PLACES_DETAIL_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_DETAILS = "/details";
    public static final String OUT_JSON = "/json";

    public static final String API_KEY = "AIzaSyD6HGalFWmk03K-2fvLcwLEk40R1vebmJU"/*"AIzaSyBpziVcPNp4jyHTtNAYwr745d98B4jvG6g"*/;

    public static String RIDE_FRAGMENT_FLAG = "0";
    public static String VIEW_PAGER_POSITION = "0";

    public static String RIDE_ACCEPTED = "rideAccepted";
    public static String RIDE_REJECTED = "rideRejected";
    public static String RIDE_JOINE = "rideJoined";
    public static String RIDE_CREATE = "rideCreate";
    public static String RIDE_STARTED = "rideStarted";
    public static String FRIEND_REQUEST_ACCEPTED = "accept_request";
    public static String FRIEND_SEND_REQUESTED = "friend_request";

    // Ride status

    public static String STATUS_RIDE_NOT_STARTED = "Not Started";
    public static String STATUS_RIDE_STARTED = "Started";
    public static String STATUS_RIDE_COMPLETED = "Completed";
    public static String STATUS_RIDE_CANCEL = "Canceled";

    // api status

    public static int API_STATUS_ONE = 1;
    public static int API_STATUS_ZERO = 0;
    public static int API_STATUS_ONE_ZERO_ONE = 101;
    public static int API_STATUS_FOUR_ZERO_ONE = 401;
    public static int API_STATUS_ONE_ZERO_TWO = 102;


    /**
     * Set the interval for the to send lat long to ser ver in background
     * in service and schduler also
     */

    // in home screen it set the background service delay to send lat long to server
    public static long HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN = 60 * 1000;  // 60 sec

    // in ride screen it set the background service delay to send lat long to server
    public static long RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN = 10 * 1000; // 10 sec

    //set the delay to send lat long when user is remove app from memory
    public static long SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE = 60 * 1000; // 60 sec



    /**
     * Home fragment
     */


    // Constant for the biker list api call in home screen
    public static long HOME_BIKER_LIST_INTERVAL = 1 * 1000; // 1 sec

    // Set the zoomlevel of google map in home screen
    public static int HOME_ZOOM_LEVEL = 10;

    // Set the camera zoomlevel in home screen
    public static int HOME_MOVE_CAMERA_ZOOM = 10;

    //set the animation time for the profile popup selay in home screen
    public static int HOME_BIKER_PROFILE_POPUP_ANIMATION_DELAY = 800;

    // Set the google map bearing in home screen
    public static int HOME_GOOGLE_MAP_BEARING = 0;


    /**
     * Benefit fragment
     */


    // set the zoom level of the benefit screen
    public static int BENEFIT_MAP_ZOOMING = 10;

    //set the animation delay of see more list in benefit screen
    public static int BENEFIT_SEEMORE_ANIMATION_DELAY = 300;

    //set the bearing of the google map in benefit screen
    public static int BENEFIT_MAP_BEARING = 0;

    //set the camera zoom level of benefit screen
    public static int BENEFIT_ZOOM_GOOGLE_CAMERA = 10;


    /**
     * Meetup Fragment
     */

    // set the zoom level of the google map in meetup fragment screen
    public static int MEETUP_GOOGLEMAP_ZOOM_LEVEL = 10;

    // set the google map camera zoom level in meetup fragment screen
    public static int MEETUP_GOOGLE_CAMERA_ZOOM = 13;

    //set the google map bearing in meetup fragment screen
    public static int MEETUP_GOOGLE_MAP_BEARING = 0;


    /**
     * Meetup route fragment
     */

    //set the rider lat long api timer interval in meet up route screen
    public static long MEETUP_ROUTE_RIDER_LAT_LONG_API_INTERVAL = 10 * 1000; // 1 sec

    //  set the google map camera zoom level in meetup route screen
    public static int MEETUP_ROUTE_GOOGLE_MAP_ZOOM_LEVEL = 10;

    //set the google map camera zoom level in meetup route screen
    public static int MEETUP_ROUTE_GOOGLE_CAMERA_ZOOM = 13;

    // Sset the google map bearing in meetup route screen
    public static int MEETUP_ROUTE_GOOGLE_MAP_BEARING = 0;

    // set the google map rotation in meetup route screen
    public static int MEETUP_ROUTE_GOOGLE_MAP_ROTATION = 245;

    // set the polyline width in google map in meetup route screen
    public static int MEETUP_ROUTE_MAP_POLY_LINE_WIDTH = 12 ;


    /**
     *  Gps_servoce class
     */

    //set the add latlong api timer interval when applocation is open in phone
    public static long GPS_SERVICE_SET_API_INTERVAL_OPEN_APP = 1000 * 10;  // 10 sec

    //set the add latlong api timer interval when app is in background
    public static long GPS_SERVICE_SET_API_INTERVAL_CLOSE_APP = 1000 * 60; // 60 sec

    //set the default time interval of the api call ingps service class
    public static long GPS_SERVICE_SET_API_DEFAULT_INTERVAL = 1000 * 10; // 10 sec

    //set the fastest interval of the gps service class to get the lat long
    public static long GPS_SERVICE_FASTEST_INTERVAL = 1000 * 5; // 5 sec

    /**
     * Api serviced
     */

    //set retry policy request time in api service class
    public static int API_SERVICE_REQUEST_TIME = 5000;


    /**
     * job util
     */

    // set the job schduler minimum latency time when app is open
    public static long JOB_UTIL_SCHDULER_INTERVAL_APP_OPEN = 10 * 1000; // 10 sec

    // set the job schduler minimum latency time when app is close
    public static long JOB_UTIL_SCHDULER_INTERVAL_APP_CLOSE = 60 * 1000; // 60 sec

    //set the add latlong api timer interval when app is in job schduler  service class when app is background
    public static long JOB_UTIL_SERVICE_SET_API_INTERVAL_CLOSE_APP = 1000 * 60; // 60 sec

    //set the default time interval of the api call in job schduler  service class
    public static long JOB_UTIL_SERVICE_SET_API_DEFAULT_INTERVAL = 1000 * 10; // 10 sec

    //set the default interval of the of the job schduler service class
    public static long JOB_UTIL_SERVICE_DEFAULT_API_INTERVAL = 1000 * 10; // 10 sec

    /**
     * gps tracker
     */

    // The minimum distance to change Updates in meters IN GPS SERVICE class
    public static int GPS_TRACKER_MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // The minimum time between updates in milliseconds in gps service class
    public static  long GPS_SERVICE_MIN_TIME_BW_UPDATES = 1000 * 60 * 1;



    // set the retry policy of the api call
    public static  int API_RETRY_POLICY =  1000 * 60; // 60 sec




    public static boolean PROGRESS_STATUS = true;

    public static String SHOW_BENEFIT_LIST = "0";

    public class SharedPrefConstant {

        public static final String SP_NAME = "bikerSharedPreferance";
        public static final String SP_AUTO_PERMISSION = "autoStartSharedPreferance";
        public static final String KEY_USER_ID = "user_id";
        public static final String KEY_SESSION_ID = "sessionId";
        public static final String KEY_USER_STATUS = "user_status";
        public static final String KEY_RIDE_ID = "ride_id";
        public static final String KEY_POUP_CHECK = "isCheck";
        public static final String KEY_NOTIFICATION_IMAGE = "image";
        public static final String KEY_NOTIFICATION_NAME = "name";
        public static final String KEY_GET_LAT = "lat";
        public static final String KEY_GET_LONG = "long";
        public static final String KEY_COOKIE = "cookie";
        public static final String KEY_FIRST_TIME = "status_first_time";
        public static final String KEY_EXECUTION_TIME = "execution_time";
    }
}
