package com.project.biker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import static com.project.biker.service.UserService.PARAM_ACTIVATION_CODE;
import static com.project.biker.service.UserService.PARAM_DEVICE_ID;
import static com.project.biker.service.UserService.PARAM_DEVICE_TYPE;
import static com.project.biker.service.UserService.PARAM_EMAIL;
import static com.project.biker.service.UserService.PARAM_FIRST_NAME;
import static com.project.biker.service.UserService.PARAM_GENDER;
import static com.project.biker.service.UserService.PARAM_LAST_NAME;
import static com.project.biker.service.UserService.PARAM_PASSWORD;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    EditText etRegisterActFirstName;
    EditText etRegisterActLastName;
    EditText etRegisterActEmail;
    EditText etLoginPassword;
    EditText etLoginConfirmPassword;
    EditText etActivationKey;

    RadioGroup rd_registerAct_group;
    RadioButton rb_registerAct_male, rb_registerAct_female;

    Button btnSignUp;

    String firstName;
    String lastName;
    String email;
    String password;
    String confirmPassword;
    String activationKey;
    String regId;
    String deviceId;

    SharePref sharePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        sharePref = new SharePref(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Initialization View
        initializeView();
        //Initialize Listener
        initListener();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signUp();
                break;
        }
    }

    /**
     * Getting the ids of xml component
     */
    private void initializeView() {
        etRegisterActFirstName = (EditText) findViewById(R.id.etRegisterActFname);
        etRegisterActLastName = (EditText) findViewById(R.id.etRegisterActLname);
        etRegisterActEmail = (EditText) findViewById(R.id.etRegisterActEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        etLoginConfirmPassword = (EditText) findViewById(R.id.etLoginConfirmPassword);
        etActivationKey = (EditText) findViewById(R.id.etActivationKey);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        rd_registerAct_group = (RadioGroup) findViewById(R.id.rd_registerAct_group);
        rb_registerAct_male = (RadioButton) findViewById(R.id.rb_registerAct_male);
        rb_registerAct_female = (RadioButton) findViewById(R.id.rb_registerAct_female);
    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        btnSignUp.setOnClickListener(this);
    }

    /**
     * Getting Data from the all component
     */
    private void signUp() {
        firstName = etRegisterActFirstName.getText().toString();
        lastName = etRegisterActLastName.getText().toString();
        email = etRegisterActEmail.getText().toString();
        password = etLoginPassword.getText().toString();
        confirmPassword = etLoginConfirmPassword.getText().toString();
        activationKey = etActivationKey.getText().toString();

        if (ValidationUtility.isValidEditText(etRegisterActFirstName, getResources().getString(R.string.hint_enter_first_name))
                && ValidationUtility.isValidEditText(etRegisterActLastName, getResources().getString(R.string.hint_enter_last_name))
                && ValidationUtility.isValidEditText(etRegisterActEmail, getResources().getString(R.string.hint_enter_email))
                && ValidationUtility.isValidEditText(etLoginPassword, getResources().getString(R.string.hint_enter_password))
                && ValidationUtility.isValidEditText(etLoginConfirmPassword, getResources().getString(R.string.hint_login_confirm_password))
                && ValidationUtility.isValidEditText(etActivationKey, getResources().getString(R.string.hint_enter_activation_code))
                && ValidationUtility.isStringHaveSpace(RegisterActivity.this, etLoginPassword)
                && ValidationUtility.isStringHaveSpace(RegisterActivity.this, etLoginConfirmPassword)) {

            if (ValidationUtility.isValidEmail(RegisterActivity.this, etRegisterActEmail)) {
                if (ValidationUtility.isValidLength(RegisterActivity.this, etLoginPassword)) {

                    if (ValidationUtility.isBothPasswordEqual(RegisterActivity.this, etLoginPassword, etLoginConfirmPassword)) {
                        if (isNetworkAvailable(this)) {
                            signUpApi();
                        } else {
                            Toast.makeText(this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        }
    }

    /**
     * Call the SignUp Api
     */
    private void signUpApi() {

        regId = FirebaseInstanceId.getInstance().getToken();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, String> param = new HashMap<>();

        param.put(PARAM_FIRST_NAME, firstName);
        param.put(PARAM_LAST_NAME, lastName);
        if (rb_registerAct_male.isChecked()) {
            param.put(PARAM_GENDER, "male");
        } else if (rb_registerAct_female.isChecked()) {
            param.put(PARAM_GENDER, "female");
        }
        param.put(PARAM_EMAIL, email);
        param.put(PARAM_PASSWORD, password);
        param.put(PARAM_DEVICE_ID, regId);
        param.put(PARAM_DEVICE_TYPE, DEVICE_TYPE);
        param.put(PARAM_PLATFORM, PLATFORM);
        param.put(PARAM_ACTIVATION_CODE, activationKey);

        UserService.signUp(RegisterActivity.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                logDebug("Registration response-->" + response.toString());
                UserParser.RegisterResponse registerResponse = UserParser.RegisterResponse.setRegisterResponse(response);
                sharePref.clear();
                if (registerResponse != null && registerResponse.getStatusCode() == API_STATUS_ONE) {
                    DialogUtility.alertOk(RegisterActivity.this, registerResponse.getMessage(), new INTFAlertOk() {
                        @Override
                        public void alertOk() {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * Method for Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}