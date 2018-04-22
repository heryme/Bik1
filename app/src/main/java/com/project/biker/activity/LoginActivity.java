package com.project.biker.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.UserService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.AndroidUtility;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.ValidationUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.project.biker.firebase.MyFirebaseInstanceIDService.reg_id;
import static com.project.biker.service.UserService.DEVICE_TYPE;
import static com.project.biker.service.UserService.PARAM_DEVICE_ID;
import static com.project.biker.service.UserService.PARAM_DEVICE_TYPE;
import static com.project.biker.service.UserService.PARAM_EMAIL;
import static com.project.biker.service.UserService.PARAM_FACEBOOK_ID;
import static com.project.biker.service.UserService.PARAM_FIRST_NAME;
import static com.project.biker.service.UserService.PARAM_ID_FACEBOOK;
import static com.project.biker.service.UserService.PARAM_LAST_NAME;
import static com.project.biker.service.UserService.PARAM_PASSWORD;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_PROFILE_PIC;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ZERO;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private String EMAIL_ID = "email";
    private String FNAME = "fname";
    private String LNAME = "lname";
    private String GENDERS = "gender";
    private String FB_ID = "fb_id";
    private String PROFILE_PIC = "profile_pic";


    SharePref sharePref;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION};


    private Button btnLoginSignIn;
    private Button btnLoginFb;
    private Button btnLoginSignUpWithUs;
    private Button btnLoginForgotPassword;

    private LoginButton loginFacebookButton;
    CallbackManager callbackManager;

    private EditText etLoginUsername;
    private EditText etLoginPassword;

    String email;
    String password;
    String regId;
    String deviceId;
    String lastName;
    String firstName;
    private HashMap<String, String> param;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (checkPermissions()) {
            fireBaseToken();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dozeMode();
            }

        }

        AndroidUtility.generateKeyHash(LoginActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginSignIn:
                regId = FirebaseInstanceId.getInstance().getToken();
                logDebug("regId login api >> " + regId);
                if (regId == null) {
                    DialogUtility.alertOk(LoginActivity.this, getString(R.string.alert_not_device_register), new INTFAlertOk() {
                        @Override
                        public void alertOk() {
                            finish();
                            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });
                } else {
                    signIn();
                }
                break;
            case R.id.btnLoginFb:
                if (isNetworkAvailable(LoginActivity.this)) {
                    if (regId == null) {
                        DialogUtility.alertOk(LoginActivity.this, getString(R.string.alert_not_device_register), new INTFAlertOk() {
                            @Override
                            public void alertOk() {
                                finish();
                                Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        });
                    } else {
                        facebookLogin();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.snackbar_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnLoginSignUpWithUs:
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.btnLoginForgotPassword:
                Intent j = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(j);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fireBaseToken();
                } else {
                    logDebug("PERMISSION NOT GRANTED");
                }
                return;
            }
        }
    }

    /**
     * Initialization View
     */
    private void initializeView() {
        btnLoginSignIn = (Button) findViewById(R.id.btnLoginSignIn);
        btnLoginFb = (Button) findViewById(R.id.btnLoginFb);
        btnLoginSignUpWithUs = (Button) findViewById(R.id.btnLoginSignUpWithUs);
        btnLoginForgotPassword = (Button) findViewById(R.id.btnLoginForgotPassword);
        loginFacebookButton = (LoginButton) findViewById(R.id.loginFacebookButton);
        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);

        /*sharePref = new SharePref(LoginActivity.this);


        if (sharePref.getUserId().equals(" ")) {
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/


    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        btnLoginSignIn.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
        btnLoginSignUpWithUs.setOnClickListener(this);
        btnLoginForgotPassword.setOnClickListener(this);
    }

    /**
     * signIn Click Method getting text from textBox
     */
    private void signIn() {
        email = etLoginUsername.getText().toString();
        password = etLoginPassword.getText().toString();

        if (ValidationUtility.isStringHaveSpace(LoginActivity.this, etLoginPassword) && ValidationUtility.isValidEditText(etLoginUsername, getResources().getString(R.string.hint_enter_email))
                && ValidationUtility.isValidEditText(etLoginPassword, getResources().getString(R.string.hint_enter_password))) {

            if (ValidationUtility.isValidEmail(LoginActivity.this, etLoginUsername)) {
                if (isNetworkAvailable(this)) {
                    loginApi();
                } else {
                    Toast.makeText(this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }

            }


        }


    }

    /**
     * Login With Facebook Method
     */
    private void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Handle success
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        logDebug("Facebook Login Callback--->" + object.toString());
                        try {
                            String id = object.optString("id");
                            String email = object.optString("email");
                            String name = object.optString("name");
                            String gender = object.getString("gender");
                            lastName = object.optString("last_name");
                            firstName = object.optString("first_name");


                            JSONObject jsonObject = object.getJSONObject("picture");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                            String profile_image = jsonObject1.optString("url");

                            logDebug(id);
                            logDebug(email);
                            logDebug(name);
                            logDebug(profile_image);
                            logDebug(firstName);
                            logDebug(lastName);

                            if (firstName.equalsIgnoreCase("")) {
                                getFirstName(name);
                            }

                            //Call Activation Key API
                            callUserExitsAPI(id, email, firstName, lastName, gender, profile_image);

                            param = new HashMap<>();
                            param.put(PARAM_EMAIL, email);
                            param.put(PARAM_FIRST_NAME, firstName);
                            param.put(PARAM_LAST_NAME, lastName);
                            param.put(PARAM_FACEBOOK_ID, id);
                            param.put(PARAM_DEVICE_ID, regId);
                            param.put(PARAM_DEVICE_TYPE, DEVICE_TYPE);
                            param.put(PARAM_PROFILE_PIC, profile_image);
                            param.put(PARAM_PLATFORM, PLATFORM);

                           /* if (isNetworkAvailable(LoginActivity.this)) {

                                UserService.loginSocial(LoginActivity.this, param, new APIService.Success<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject response) {

                                        logDebug("Login Response-->" + response.toString());

                                        UserParser.LoginResponse loginResponse = UserParser.LoginResponse.SetLoginResponse(response);
                                        if (loginResponse != null && loginResponse.getStatusCode() == API_STATUS_ONE) {
                                            sharePref.setUserId(loginResponse.getUserId());
                                            sharePref.setSessionId(loginResponse.getSessionId());
                                            sharePref.setUserStatus(loginResponse.getStatus());
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                            }*/
                        } catch (Exception e) {
                            logDebug("<<<<<<< Facebook Login()Response Exception");
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,gender,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                logDebug(exception.getMessage());
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    /**
     * Login Api Call
     */
    private void loginApi() {

        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_EMAIL, email);
        param.put(PARAM_PASSWORD, password);
        param.put(PARAM_DEVICE_ID, regId);
        param.put(PARAM_DEVICE_TYPE, DEVICE_TYPE);
        param.put(PARAM_PLATFORM, PLATFORM);

        UserService.login(LoginActivity.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Login Response-->" + response.toString());
                UserParser.LoginResponse loginResponse = UserParser.LoginResponse.SetLoginResponse(response);
                if (loginResponse != null && loginResponse.getStatusCode() == API_STATUS_ONE) {
                    sharePref.setUserId(loginResponse.getUserId());
                    sharePref.setSessionId(loginResponse.getSessionId());
                    sharePref.setUserStatus(loginResponse.getStatus());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }

    /**
     * Method to get the firstName from name
     *
     * @param name
     */
    private void getFirstName(String name) {
        String[] name1 = name.split(" ");
        for (int i = 0; i < name1.length; i++) {
            if (i == 0) {
                firstName = name1[i];
            } else if (i == 1) {
                lastName = name1[i];
            }
        }
    }

    /**
     * get all permission dialog from user at run time android
     */
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(LoginActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * Initialize the FireBase and get
     * the device token and device id from fcm
     */
    private void fireBaseToken() {
        FirebaseApp.initializeApp(LoginActivity.this);
        logDebug("regId FCM >> " + reg_id);
        // regId = reg_id;
        regId = FirebaseInstanceId.getInstance().getToken();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        logDebug("regId >> " + regId);

        //Initialization View
        initializeView();
        //Register Click Listener
        initListener();

        sharePref = new SharePref(LoginActivity.this);
        if (sharePref.getUserId().equals(" ")) {
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Method for log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.e(TAG, "" + msg);
    }

    /**
     * This Method is for Off the doze mode
     */
    private void dozeMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    /**
     * Call Check User Exits API
     */
    private void callUserExitsAPI(final String id, final String email,
                                  final String fName,
                                  final String lName,
                                  final String gender,
                                  final String profilePic) {
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(PARAM_ID_FACEBOOK, id);
        hashMap.put(PARAM_EMAIL, email);
        hashMap.put(PARAM_PLATFORM, PLATFORM);
        UserService.checkUserExits(LoginActivity.this, hashMap, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Response User Exits API--->" + response.toString());
                int statusCode;
                String message;
                if (response != null && response.length() > 0) {
                    //statusCode = response.optString("status_code");
                    message = response.optString("message");

                    // 1 For Success Normal Login
                    // 0 For Not Exist
                    UserParser.LoginResponse loginResponse = UserParser.LoginResponse.SetLoginResponse(response);
                    statusCode = loginResponse.getStatusCode();
                    if (statusCode == API_STATUS_ONE) {
                        sharePref.setUserId(loginResponse.getUserId());
                        sharePref.setSessionId(loginResponse.getSessionId());
                        sharePref.setUserStatus(loginResponse.getStatus());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (statusCode == API_STATUS_ZERO/*statusCode.equalsIgnoreCase("0")*/) {
                        Intent intent = new Intent(LoginActivity.this, ActivationActivity.class);
                        intent.putExtra(EMAIL_ID, email);
                        intent.putExtra(FNAME, fName);
                        intent.putExtra(LNAME, lName);
                        intent.putExtra(GENDERS, gender);
                        intent.putExtra(FB_ID, id);
                        intent.putExtra(PROFILE_PIC, profilePic);
                        startActivity(intent);
                    } else {
                        DialogUtility.alertOk(LoginActivity.this, loginResponse.getMessage(), new INTFAlertOk() {
                            @Override
                            public void alertOk() {

                            }
                        });
                    }
                }
            }
        });
    }
}




