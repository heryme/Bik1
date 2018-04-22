package com.project.biker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.UserService;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.ValidationUtility;

import org.json.JSONObject;

import java.util.HashMap;
import static com.project.biker.service.UserService.PARAM_EMAIL;
import static com.project.biker.service.UserService.PARAM_PLATFORM;
import static com.project.biker.service.UserService.PLATFORM;
import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    EditText etForgotPass;
    Button btnSendOtp;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendOtp:
                sendOtp();
                break;
        }
    }

    /**
     * Getting all the ids from xml component
     */
    public void initializeView() {
        etForgotPass = (EditText) findViewById(R.id.etActForgotPass);
        btnSendOtp = (Button) findViewById(R.id.btnSendOtp);
        btnSendOtp.setOnClickListener(this);
    }

    /**
     * get data from and validation
     *
     */
    public void sendOtp() {
        email = etForgotPass.getText().toString();
        if (ValidationUtility.isValidEditText(etForgotPass, getResources().getString(R.string.hint_enter_email))) {
            if (ValidationUtility.isValidEmail(ForgotPasswordActivity.this, etForgotPass)) {
                if (isNetworkAvailable(this)) {
                    forgotPasswordApi();
                } else {
                    Toast.makeText(this, getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Call Api of forgotPassword
     */
    public void forgotPasswordApi() {
        HashMap<String, String> param = new HashMap<>();
        param.put(PARAM_EMAIL, email);
        param.put(PARAM_PLATFORM, PLATFORM);
        UserService.forgotPassword(ForgotPasswordActivity.this, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("forgotPasswordResponse response-->" + response.toString());
                UserParser.ForgotPasswordResponse forgotPasswordResponse = UserParser.ForgotPasswordResponse.forgotPasswordResponse(response);
                if (forgotPasswordResponse != null && forgotPasswordResponse.getStatusCode() == API_STATUS_ONE) {
                    DialogUtility.alertOk(ForgotPasswordActivity.this, forgotPasswordResponse.getMessage(), new INTFAlertOk() {
                        @Override
                        public void alertOk() {
                            Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });
                }
            }
        });
    }

    /**
     * Method for Log
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }
}
