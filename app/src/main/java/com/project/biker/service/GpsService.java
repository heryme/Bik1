package com.project.biker.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.project.biker.ResponseParser.BikerParser;
import com.project.biker.tools.SharePref;

import org.json.JSONObject;

import java.util.HashMap;

import static com.project.biker.service.UserService.PARAM_LAT;
import static com.project.biker.service.UserService.PARAM_LONG;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_FOUR_ZERO_ONE;
import static com.project.biker.tools.Constant.GPS_SERVICE_FASTEST_INTERVAL;
import static com.project.biker.tools.Constant.GPS_SERVICE_SET_API_DEFAULT_INTERVAL;
import static com.project.biker.tools.Constant.HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE;


/**
 * Created by Vikas Patel on 7/11/2017.
 */

public class GpsService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GpsService";
    private long INTERVAL = GPS_SERVICE_SET_API_DEFAULT_INTERVAL;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;

    SharePref sharePref;

    @Override
    public void onCreate() {
        super.onCreate();
        logDebug("sevice start >>>>>>>>>>>>>>>>>>>>>>>>>......");
        sharePref = new SharePref(GpsService.this);


        /**
         * it set the interval depending on user currently open which screen
         * change the background task delay execution  according to screen
         * 1 for ride
         * 2 for home
         * 0 for in background
         */
        if (sharePref.getExecutionTime().equalsIgnoreCase("0")) {
            logDebug("execution is zero");
            INTERVAL = SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE;
        } else if (sharePref.getExecutionTime().equalsIgnoreCase("2")) {
            logDebug("execution is two");
            INTERVAL = HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
            sharePref.setExecutionTime("0");
        } else {
            logDebug("execution is one");
            INTERVAL = RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
            sharePref.setExecutionTime("0");
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logDebug("mGoogleApiClient >>>>>>>>>>>>>>>>>>>>>>>>>......");
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onLocationChanged(Location location) {
        //Save your location
        logDebug("GpsService Location lat" + location.getLatitude());
        logDebug("Gps Location long" + location.getLongitude());
        logDebug("GpsService userid" + sharePref.getUserId());

        sharePref.SetLat(String.valueOf(location.getLatitude()));
        sharePref.SetLong(String.valueOf(location.getLongitude()));

        String lati = String.valueOf(location.getLatitude());
        String longi = String.valueOf(location.getLongitude());

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_LAT, lati);
        param.put(PARAM_LONG, longi);
        param.put(PARAM_PLATFORM, PLATFORM);

        BikerService.addLatLong(GpsService.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.e("Location Response-->", "" + response.toString());
                BikerParser.AddLatLongResponse AddLatLongResponse = BikerParser.AddLatLongResponse.addLatLongResponse(response);
                if (AddLatLongResponse.getStatusCode() == API_STATUS_FOUR_ZERO_ONE) {
                    stopService(new Intent(GpsService.this, GpsService.class));
                }

            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        logDebug("ON Task Remove Method");

        stopService(new Intent(GpsService.this, GpsService.class));
        startService(new Intent(GpsService.this, GpsService.class));

        super.onTaskRemoved(rootIntent);
    }

    /**
     * This method is start the location update
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        logDebug("startLocationUpdates >>>>>>>>>>>>>>>>>>>>>>>>>......");
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        logDebug("Location update started ..............: ");

    }

    /**
     * this method is create the location request
     * and set the intervals
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(GPS_SERVICE_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * This method is to stop the
     * location update
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        logDebug("Location update stopped .......................");
    }

    /**
     * Method to Print Log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}