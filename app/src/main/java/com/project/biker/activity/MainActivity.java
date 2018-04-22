package com.project.biker.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.soundcloud.android.crop.Crop;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.fragment.BenefitsFragment;
import com.project.biker.fragment.BenefitsItemFragment;
import com.project.biker.fragment.EditProfileFragment;
import com.project.biker.fragment.FriendsFragment;
import com.project.biker.fragment.FriendsInvitationFragment;
import com.project.biker.fragment.HomeFragment;
import com.project.biker.fragment.MeetUPFragment;
import com.project.biker.fragment.MeetUpRouteFragment;
import com.project.biker.fragment.NotificationFragment;
import com.project.biker.fragment.ProfileFragment;
import com.project.biker.fragment.RideFragment;
import com.project.biker.model.INTFConfirmYesNo;
import com.project.biker.service.APIService;
import com.project.biker.service.GpsService;
import com.project.biker.service.UserService;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_SESSION_ID;
import static com.project.biker.service.UserService.PARAM_USER_ID;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.RIDE_ACCEPTED;
import static com.project.biker.tools.Constant.RIDE_CREATE;
import static com.project.biker.tools.Constant.RIDE_FRAGMENT_FLAG;
import static com.project.biker.tools.Constant.RIDE_JOINE;
import static com.project.biker.tools.Constant.RIDE_REJECTED;
import static com.project.biker.tools.Constant.RIDE_STARTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    SharePref sharePref;

    public static final int REQUEST_CHECK_SETTINGS = 1;

    /**
     * Drawer Layout
     */
    private DrawerLayout drawerLayout;

    /**
     * Toolbar
     */
    private Toolbar toolbar;

    /**
     * NavigationView
     */
    private NavigationView navigationView;

    /**
     * ActionBarDrawerToggle
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    TextView tv_toolbar_title, tvMainActivityNotSharingLoc;
    ToggleButton toggle_toolbar;
    RelativeLayout rr_toolbar;

    TextView home;
    TextView ride;
    TextView benefit;
    TextView profile;

    public static RelativeLayout rrMain;

    IntentFilter filter;
    NetworkStateReceiver networkStateReceiver;

    static Snackbar snackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize xml view
        initializeView();

        //Object Initializer
        objectInitializer();

        // setup the ActionBar
        setSupportActionBar(toolbar);

        //Display Back Arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Not Set By Default Title Ex:By Default Set App Name :Biker
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // On or Off the toggle button according to preference data
        setToggleOnOff();

        //Initialization Drawer
        initNavigationDrawer();

        loadFragment(new HomeFragment(), false);

        // initialize the setOnClick Event
        initializeOnClick();

        // getting bundle data from notification
        getBundleData();

        // Clear the Cache Memory of Glide in app
        clearGlideCache();
    }

    @Override
    public void onBackPressed() {
        fragmentHandling();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logDebug("On onActivityResult Method Call -->");
        logDebug("requestCode -->" + requestCode);
        logDebug("resultCode" + resultCode);

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    // TODO Auto-generated method stub
                    // this is the return the user is enable location
                    logDebug("User agreed to make required location settings changes.");
                    break;
                case Activity.RESULT_CANCELED:
                    // TODO Auto-generated method stub
                    // this is the return the user is cancel location you can finish the app also
                    logDebug("User chose not to make required location settings changes.");
                    break;
            }
            //Still Here Handle onActivityResult Of The Edit ProfileFragment
        } else if (requestCode == EditProfileFragment.SELECT_FILE) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof EditProfileFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        } else if (requestCode == EditProfileFragment.SELECT_COVER_PIC) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof EditProfileFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        } else if (requestCode == EditProfileFragment.REQUEST_CAMERA_PROFILE_PIC) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof EditProfileFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        } else if (requestCode == EditProfileFragment.REQUEST_CAMERA_COVER_PIC) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof EditProfileFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        } else if(requestCode == Crop.REQUEST_CROP) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof EditProfileFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home: {
                loadFragment(new HomeFragment(), false);
                break;
            }
            case R.id.ride: {
                loadFragment(new RideFragment(), false);
                break;
            }
            case R.id.benefit: {
                loadFragment(new BenefitsFragment(), false);
                break;
            }
            case R.id.profile: {
                loadFragment(new ProfileFragment(), false);
                break;
            }
            case R.id.toggle_toolbar: {
                if (toggle_toolbar.isChecked()) {
                    tv_toolbar_title.setText(getString(R.string.online));
                    tvMainActivityNotSharingLoc.setVisibility(View.GONE);
                    if (sharePref.getPopupCheck()) {
                    } else {
                        DialogUtility.locationDialog(MainActivity.this);
                    }
                    toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_color));
                } else {
                    tv_toolbar_title.setText(getString(R.string.offline));
                    tvMainActivityNotSharingLoc.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.login_dark_back));
                }
            }
        }

    }

    /**
     * Initialization view
     */
    private void initializeView() {
        //Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_toolbar_title = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        toggle_toolbar = (ToggleButton) toolbar.findViewById(R.id.toggle_toolbar);
        tvMainActivityNotSharingLoc = (TextView) findViewById(R.id.tvMainActivityNotSharingLoc);
        rr_toolbar = (RelativeLayout) findViewById(R.id.rr_toolbar);
        rrMain = (RelativeLayout) findViewById(R.id.rrMain);
        home = (TextView) findViewById(R.id.home);
        ride = (TextView) findViewById(R.id.ride);
        benefit = (TextView) findViewById(R.id.benefit);
        profile = (TextView) findViewById(R.id.profile);
    }

    /**
     * Object Initializer
     */
    private void objectInitializer(){
        snackbar = Snackbar.make(rrMain, getString(R.string.snackbar_no_internet), BaseTransientBottomBar.LENGTH_INDEFINITE);
        filter = new IntentFilter();
        filter.addAction(getString(R.string.filter_action));
        networkStateReceiver = new NetworkStateReceiver();
        sharePref = new SharePref(this);
    }

    /**
     * Initialize Navigation Drawer
     */
    public void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    //Running Task Response
                    case R.id.Home:
                        RIDE_FRAGMENT_FLAG = "0";
                        loadFragment(new HomeFragment(), true);
                        break;
                    case R.id.notification:
                        loadFragment(new NotificationFragment(), true);
                        break;
                    case R.id.friend_invitation:
                        loadFragment(new FriendsInvitationFragment(), true);
                        break;
                    case R.id.logout:
                        DialogUtility.confirmYesNo(MainActivity.this, getString(R.string.logout_alert_messag), new INTFConfirmYesNo() {
                            @Override
                            public void yesClick() {
                                logout();
                            }
                        });
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * Method to load the fragment
     *
     * @param fragment
     * @param isMenuItem
     */
    public void loadFragment(Fragment fragment, boolean isMenuItem) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        logDebug("fragmentPopped >> " + fragmentPopped);
        logDebug("Already Added");

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frg_main_activity, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    /**
     * Parse the data coming in notification
     *
     * @param data
     */
    private void notificationDataParser(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String id = jsonObject.optString("i_id");
            logDebug("ID--> " + id);
            String profilePic = jsonObject.optString("v_profile_pic");
            sharePref.SetNotificationImage(profilePic);
            logDebug("v_profile_pic--> " + profilePic);
            String name = jsonObject.optString("name");
            sharePref.SetNotificationName(name);
            logDebug("name " + name);
            String coverPic = jsonObject.optString("v_cover_pic");
            logDebug("coverPic " + coverPic);
            String action = jsonObject.optString("action");
            logDebug("action " + action);
            String notificationTitle = jsonObject.optString("notification_title");
            logDebug("notificationTitle " + notificationTitle);
            String notificationMessage = jsonObject.optString("notification_message");
            logDebug("notificationMessage " + notificationMessage);
            String rideId = jsonObject.optString("rideId");
            logDebug("rideId " + rideId);
            sharePref.setRideId(rideId);

            if (action.equalsIgnoreCase(RIDE_ACCEPTED) ||
                    action.equalsIgnoreCase(RIDE_REJECTED) ||
                    action.equalsIgnoreCase(RIDE_JOINE) ||
                    action.equalsIgnoreCase(RIDE_STARTED)) {
                loadFragment(new MeetUpRouteFragment(), false);
            } else if (action.equalsIgnoreCase(RIDE_CREATE)) {
                textChange(new NotificationFragment());
                loadFragment(new NotificationFragment(), false);
            } else if (action.equalsIgnoreCase(Constant.FRIEND_SEND_REQUESTED)) {
                loadFragment(new FriendsInvitationFragment(), true);
            } else if (action.equalsIgnoreCase(Constant.FRIEND_REQUEST_ACCEPTED)) {
                loadFragment(new FriendsFragment(), false);
                textChange(new FriendsFragment());

            }
        } catch (JSONException e) {
            logDebug("<<<<<< Notification Data Parser()Response Exception >>>>>>>>");
            e.printStackTrace();
        }
    }


    /**
     * Get the which fragment is currently open
     * and change text color according to selection
     *
     * @param fragment
     */
    public void textChange(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

       /* Fragment fragment1 = getSupportFragmentManager().findFragmentByTag("home");*/

        logDebug("tag" + fragmentTag);

        if (fragmentTag.equalsIgnoreCase(HomeFragment.getFragmentName())) {
            setTabSelection(home);
        } else if (fragmentTag.equalsIgnoreCase(RideFragment.getFragmentName())) {
            setTabSelection(ride);
        } else if (fragmentTag.equalsIgnoreCase(BenefitsFragment.getFragmentName())) {
            setTabSelection(benefit);
        } else if (fragmentTag.equalsIgnoreCase(ProfileFragment.getFragmentName())) {
            setTabSelection(profile);
        } else if (fragmentTag.equalsIgnoreCase(NotificationFragment.getFragmentName())) {
            defaultTabTextColor();
        } else if (fragmentTag.equalsIgnoreCase(MeetUpRouteFragment.getFragmentName())) {
            setTabSelection(ride);
        } else if (fragmentTag.equalsIgnoreCase(MeetUPFragment.getFragmentName())) {
            setTabSelection(ride);
        } else if (fragmentTag.equalsIgnoreCase(BenefitsItemFragment.getFragmentName())) {
            setTabSelection(benefit);
        } else if (fragmentTag.equalsIgnoreCase(EditProfileFragment.getFragmentName())) {
            setTabSelection(profile);
        } else if (fragmentTag.equalsIgnoreCase(FriendsFragment.getFragmentName())) {
            setTabSelection(profile);
        } else if (fragmentTag.equalsIgnoreCase(FriendsInvitationFragment.getFragmentName())) {
            defaultTabTextColor();
        }

    }

    /**
     * Set the tab selection color
     *
     * @param textView
     */
    private void setTabSelection(TextView textView) {
        home.setTextColor(ContextCompat.getColor(this, R.color.white));
        ride.setTextColor(ContextCompat.getColor(this, R.color.white));
        benefit.setTextColor(ContextCompat.getColor(this, R.color.white));
        profile.setTextColor(ContextCompat.getColor(this, R.color.white));

        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    /**
     * Handel the backStack of fragment
     */
    private void fragmentHandling() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            logDebug("Fragment Name-->" + getCurrentFragment());
            logDebug("Back Stack Entry--->" + getSupportFragmentManager().getBackStackEntryCount());
        } else {
            //finish();
            super.onBackPressed();
            logDebug("Back Stack Entry--->" + getSupportFragmentManager().getBackStackEntryCount());
        }
    }

    /**
     * Check Current Fragment
     *
     * @return
     */
    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);

        return currentFragment;
    }

    /**
     * Automatically ask the user to on the location setting
     */
    public void locationCheck() {
        LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps_enabled && !network_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage(MainActivity.this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(MainActivity.this.getResources().getString(R.string.location_setting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    //  Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    //  MainActivity.this.startActivity(myIntent);
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 1);
                    //get gps
                }
            });
            dialog.show();
        }

    }

    /**
     * This Method for the Location Enable
     *
     * @param context
     */
    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        logDebug("All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        logDebug("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            logDebug("PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        logDebug("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }

        });

    }

    /**
     * Api for the Logout
     */
    public void logout() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_USER_ID, sharePref.getUserId());
        param.put(PARAM_SESSION_ID, sharePref.getSessionId());
        param.put(PARAM_PLATFORM, PLATFORM);

        UserService.Logout(MainActivity.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("logout Response-->" + response.toString());
                UserParser.LogoutResponse logoutResponse = UserParser.LogoutResponse.logoutResponse(response);
                if (logoutResponse != null && logoutResponse.getStatusCode() == API_STATUS_ONE) {
                    MainActivity.this.stopService(new Intent(MainActivity.this, GpsService.class));
                    sharePref.clear();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    /**
     * Broadcast receiver for the snackBar to check internet connection
     */
    public static class NetworkStateReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getExtras() != null) {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                Snackbar snackbar = initSnackBar(context);
                if (ni != null && ni.isConnectedOrConnecting()) {
                    logDebug("Network " + ni.getTypeName() + " connected");
                    snackbar.dismiss();
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    logDebug("There's no network connectivity");

                    snackbar.show();

                }

            }
        }
    }

    /**
     * Initialize the OnClick Event Of the component
     */
    private void initializeOnClick() {
        home.setOnClickListener(this);
        ride.setOnClickListener(this);
        benefit.setOnClickListener(this);
        profile.setOnClickListener(this);
        toggle_toolbar.setOnClickListener(this);
    }

    /**
     * Set the Toggle Button On or Off according to Preference value
     */
    private void setToggleOnOff() {
        if (sharePref.getUserStatus().equals("1")) {
            toggle_toolbar.setChecked(true);
            tv_toolbar_title.setText(getString(R.string.online));
            toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_color));
            tvMainActivityNotSharingLoc.setVisibility(View.GONE);
        } else {
            toggle_toolbar.setChecked(false);
            tv_toolbar_title.setText(getString(R.string.offline));
            toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.login_dark_back));
            tvMainActivityNotSharingLoc.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get the bundle data coming from notification
     */
    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            logDebug("message->" + getIntent().getExtras().get("message"));
            notificationDataParser(getIntent().getExtras().get("message").toString());
        }
    }

    /**
     * Clear Glide cache of whole Application
     */
    private void clearGlideCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(MainActivity.this).clearDiskCache();
            }
        }).start();

    }

    /**
     * Method for log
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * set the default text color
     * of tab layout
     */
    private void defaultTabTextColor() {
        home.setTextColor(ContextCompat.getColor(this, R.color.white));
        ride.setTextColor(ContextCompat.getColor(this, R.color.white));
        benefit.setTextColor(ContextCompat.getColor(this, R.color.white));
        profile.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    /**
     * Initialize the snackbar
     *
     * @param context
     * @return
     */
    private static Snackbar initSnackBar(Context context) {
        if (snackbar == null) {
            snackbar = Snackbar.make(rrMain, context.getString(R.string.snackbar_no_internet), BaseTransientBottomBar.LENGTH_INDEFINITE);
            colorSnackBar(snackbar);
            return snackbar;
        } else {
            colorSnackBar(snackbar);
            return snackbar;
        }
    }

    /**
     * Change the color of the snackbar
     *
     * @param snackbar
     */
    private static void colorSnackBar(Snackbar snackbar) {
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(Color.RED);
    }

}
