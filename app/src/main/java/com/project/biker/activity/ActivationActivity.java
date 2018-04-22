package com.project.biker.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.UserService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.ValidationUtility;

import org.json.JSONObject;

import java.util.HashMap;

import static com.project.biker.service.UserService.DEVICE_TYPE;
import static com.project.biker.service.UserService.MALE;
import static com.project.biker.service.UserService.PARAM_ACTIVATION_CODE;
import static com.project.biker.service.UserService.PARAM_DEVICE_ID;
import static com.project.biker.service.UserService.PARAM_DEVICE_TYPE;
import static com.project.biker.service.UserService.PARAM_EMAIL;
import static com.project.biker.service.UserService.PARAM_FACEBOOK_ID;
import static com.project.biker.service.UserService.PARAM_FIRST_NAME;
import static com.project.biker.service.UserService.PARAM_GENDER;
import static com.project.biker.service.UserService.PARAM_LAST_NAME;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PARAM_PROFILE_PIC;
import static com.project.biker.service.UserService.PARAM_SOCIAL_LOGIN;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.service.UserService.YES;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

public class ActivationActivity extends AppCompatActivity {

    private static final String TAG = ActivationActivity.class.getName();

    private String EMAIL_ID = "email";
    private String FNAME = "fname";
    private String LNAME = "lname";
    private String GENDERS = "gender";
    private String FB_ID = "fb_id";
    private String PROFILE_PIC = "profile_pic";

    EditText etActivationActEmail;
    EditText etActivationActKey;
    Button btnActivationAcyLogin;
    SharePref sharePref;
    String email;
    String firstName;
    String lastName;
    String gender;
    String fbId;
    String profilePIc;
    Boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        sharePref = new SharePref(ActivationActivity.this);
        //Initialization view
        initializationView();

        Intent intent = getIntent();
        //Get Data From The Intent
        getIntentData(intent);

        //Show Hide Email Field
        showHideEditText();
        //Login Button Click Event
        btnClickEvent();

    }

    /**
     * Initialization View
     */
    private void initializationView() {
        etActivationActEmail = (EditText) findViewById(R.id.etActivationActEmail);
        etActivationActKey = (EditText) findViewById(R.id.etActivationActKey);
        btnActivationAcyLogin = (Button) findViewById(R.id.btnActivationAcyLogin);
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
     * Show Hide Email Filed
     */
    private void showHideEditText() {
        if (email.equalsIgnoreCase("")) {
            isVisible = true;
            etActivationActEmail.setVisibility(View.VISIBLE);
        } else {
            isVisible = false;
            etActivationActEmail.setVisibility(View.GONE);
        }
    }

    /**
     * Button Click Of The Login Button
     */
    private void btnClickEvent() {

        btnActivationAcyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check Network Availability
                if (isNetworkAvailable(ActivationActivity.this)) {
                    //Check Validation
                    if (checkValidation()) {
                        //Call the SignUp Api
                        signUpApi();
                    }
                } else {
                    Toast.makeText(ActivationActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Check Validation
     *//*
    private void checkValidation() {
        if (isNetworkAvailable(ActivationActivity.this)) {
            if (isVisible) {
                if (ValidationUtility.isValidEditText(etActivationActEmail,
                        getResources().getString(R.string.hint_enter_email)) &&
                        ValidationUtility.isValidEmail(ActivationActivity.this, etActivationActEmail) &&
                        ValidationUtility.isValidEditText(etActivationActKey,
                                getResources().getString(R.string.hint_enter_activation_key))) {
                    signUpApi();
                }
            } else if (ValidationUtility.isValidEditText(etActivationActKey,
                    getResources().getString(R.string.hint_enter_activation_key))) {
                signUpApi();

            }


        } else {
            Toast.makeText(ActivationActivity.this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }
    }*/

    /**
     * Check Validation
     */
    private boolean checkValidation() {
        if (isVisible) {
            if (ValidationUtility.isValidEditText(etActivationActEmail,
                    getResources().getString(R.string.hint_enter_email)) &&
                    ValidationUtility.isValidEmail(ActivationActivity.this, etActivationActEmail) &&
                    ValidationUtility.isValidEditText(etActivationActKey,
                            getResources().getString(R.string.hint_enter_activation_key))) {
                return true;
                //signUpApi();
            }
        } else if (ValidationUtility.isValidEditText(etActivationActKey,
                getResources().getString(R.string.hint_enter_activation_key))) {
            return true;
            //signUpApi();
        }
        return false;
    }

    /**
     * Call the SignUp Api
     * Because When User First Time Login With Facebook Then We Need To
     * Asked Activation Key So We Need To Call Here SighUP API
     */
    private void signUpApi() {

        String regId = FirebaseInstanceId.getInstance().getToken();

        HashMap<String, String> param = new HashMap<>();

        param.put(PARAM_FIRST_NAME, firstName);
        param.put(PARAM_LAST_NAME, lastName);

        //IF Not Getting Gender From The Login With FaceBook Then By Default Pass Male
        if (gender != null && !gender.equalsIgnoreCase("")) {
            param.put(PARAM_GENDER, gender);
        } else {
            param.put(PARAM_GENDER, MALE);
        }

      /*  if(email != null && !email.equalsIgnoreCase("") ) {
            param.put(PARAM_EMAIL, email);
        }else {*/
        param.put(PARAM_EMAIL, etActivationActEmail.getText().toString());
        //}

        //"YES" value pass for when user register by login with facebook
        param.put(PARAM_SOCIAL_LOGIN, YES);
        param.put(PARAM_DEVICE_ID, regId);
        param.put(PARAM_DEVICE_TYPE, DEVICE_TYPE);
        param.put(PARAM_FACEBOOK_ID, fbId);
        param.put(PARAM_PLATFORM, PLATFORM);
        param.put(PARAM_PROFILE_PIC, profilePIc);
        param.put(PARAM_ACTIVATION_CODE, etActivationActKey.getText().toString());

        UserService.signUp(ActivationActivity.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Registration response-->" + response.toString());
                sharePref.clear();
                logDebug("Registration Response-->" + response.toString());
                UserParser.LoginResponse loginResponse = UserParser.LoginResponse.SetLoginResponse(response);
                if (loginResponse != null && loginResponse.getStatusCode() == API_STATUS_ONE) {
                    sharePref.setUserId(loginResponse.getUserId());
                    sharePref.setSessionId(loginResponse.getSessionId());
                    sharePref.setUserStatus(loginResponse.getStatus());
                    Intent intent = new Intent(ActivationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    DialogUtility.alertOk(ActivationActivity.this, loginResponse.getMessage(), new INTFAlertOk() {
                        @Override
                        public void alertOk() {

                        }
                    });
                }
            }
        });
    }

    /**
     * Get Data From The Intent When Do Login With FaceBook
     *
     * @param intent
     */
    private void getIntentData(Intent intent) {
        if (intent.getStringExtra(EMAIL_ID) != null) {
            email = intent.getStringExtra(EMAIL_ID);
            etActivationActEmail.setText(email);
            logDebug("Email--->" + email);
        }

        if (intent.getStringExtra(FNAME) != null) {
            firstName = intent.getStringExtra(FNAME);
            logDebug("FNAME--->" + intent.getStringExtra(FNAME));
        }

        if (intent.getStringExtra(LNAME) != null) {
            lastName = intent.getStringExtra(LNAME);
            logDebug("LNAME--->" + intent.getStringExtra(LNAME));
        }

        if (intent.getStringExtra(GENDERS) != null &&
                !intent.getStringExtra(GENDERS).equalsIgnoreCase("")) {
            gender = intent.getStringExtra(GENDERS);
            logDebug("GENDERS--->" + intent.getStringExtra(GENDERS));
        }

        if (intent.getStringExtra(FB_ID) != null) {
            fbId = intent.getStringExtra(FB_ID);
            logDebug("FB_ID--->" + intent.getStringExtra(FB_ID));
        }

        if (intent.getStringExtra(PROFILE_PIC) != null) {
            profilePIc = intent.getStringExtra(PROFILE_PIC);
            logDebug("PROFILE_PIC--->" + intent.getStringExtra(PROFILE_PIC));
        }

    }
}

