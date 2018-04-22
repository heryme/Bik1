package com.project.biker.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.project.biker.ResponseParser.BikerParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.tools.SharePref;

import org.json.JSONObject;

import java.util.HashMap;

import static com.project.biker.service.UserService.PARAM_LAT;
import static com.project.biker.service.UserService.PARAM_LONG;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.JOB_UTIL_SERVICE_DEFAULT_API_INTERVAL;
import static com.project.biker.tools.Constant.HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE;

/**
 * Created by Vikas Patel on 8/26/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SchdulerJobService extends JobService implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SyncService";
    SharePref sharePref;

    private long INTERVAL = JOB_UTIL_SERVICE_DEFAULT_API_INTERVAL;
    //   private long FASTEST_INTERVAL = 1000 * 10;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Override
    public boolean onStartJob(JobParameters params) {


        sharePref = new SharePref(SchdulerJobService.this);
        logDebug("execution is =======" + sharePref.getExecutionTime());

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

        this.mGoogleApiClient.connect();

        Intent service = new Intent(getApplicationContext(), MainActivity.class);
        getApplicationContext().startService(service);
        //  scheduleJob(getApplicationContext());

        // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // mGoogleApiClient.disconnect();
        // stopLocationUpdates();
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        logDebug("Location lat" + location.getLatitude());
        logDebug("Location long" + location.getLongitude());
        logDebug("userid" + sharePref.getUserId());

        logDebug("??????????????????");

        sharePref.SetLat(String.valueOf(location.getLatitude()));
        sharePref.SetLong(String.valueOf(location.getLongitude()));

        String lati = String.valueOf(location.getLatitude());
        String longi = String.valueOf(location.getLongitude());

        if (sharePref.getUserStatus().equalsIgnoreCase("1")) {
            locationUpdateApi(lati, longi);
        }


    }


    /**
     * This method is start the location update
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    /**
     * this method is create the location request
     * and set the intervals
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        //   mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Call the location update api
     *
     * @param lati
     * @param longi
     */
    private void locationUpdateApi(String lati, String longi) {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_LAT, lati);
        param.put(PARAM_LONG, longi);
        param.put(PARAM_PLATFORM, PLATFORM);

        BikerService.addLatLong(SchdulerJobService.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Location Response-->" + response.toString());
                BikerParser.AddLatLongResponse addLatLongResponse = BikerParser.AddLatLongResponse.addLatLongResponse(response);
                if (addLatLongResponse.getStatusCode() == API_STATUS_ONE) {

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
     * This method is to stop the
     * location update
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        logDebug("Location update stopped .......................");
    }
}