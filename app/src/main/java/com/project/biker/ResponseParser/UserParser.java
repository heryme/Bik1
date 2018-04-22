package com.project.biker.ResponseParser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.tools.Constant.API_STATUS_ONE_ZERO_ONE;

/**
 * Created by Vikas Patel on 7/10/2017.
 */

public class UserParser {

    private static final String TAG = "UserParser";

    /**
     * Login Response
     */
    public static class LoginResponse {


        private int statusCode;
        private String message;
        private String userId;
        private String sessionId;
        private String iId;
        private String firstName;
        private String lastName;
        private String email;
        private String city;
        private String state;
        private String country;
        private String profilePic;
        private String coverPic;
        private String isDeleted;
        private String isActivated;
        private String emailVerified;
        private String status;


        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getiId() {
            return iId;

        }

        public void setiId(String iId) {
            this.iId = iId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getCoverPic() {
            return coverPic;
        }

        public void setCoverPic(String coverPic) {
            this.coverPic = coverPic;
        }

        public String getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getIsActivated() {
            return isActivated;
        }

        public void setIsActivated(String isActivated) {
            this.isActivated = isActivated;
        }

        public String getEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(String emailVerified) {
            this.emailVerified = emailVerified;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public static LoginResponse SetLoginResponse(JSONObject response) {
            LoginResponse loginResponse = new LoginResponse();

            try {

                loginResponse.setStatusCode(response.optInt("status_code"));
                loginResponse.setMessage(response.optString("message"));

                if (loginResponse.getStatusCode() == API_STATUS_ONE_ZERO_ONE) {

                } else if(loginResponse.getStatusCode() == API_STATUS_ONE){

                    JSONObject jsonObject = response.getJSONObject("data");

                    loginResponse.setUserId(jsonObject.optString("user_id"));
                    loginResponse.setSessionId(jsonObject.optString("session_id"));

                    JSONObject jsonObject1 = jsonObject.getJSONObject("userData");

                    loginResponse.setiId(jsonObject1.optString("i_id"));
                    loginResponse.setFirstName(jsonObject1.optString("v_firstname"));
                    loginResponse.setLastName(jsonObject1.optString("v_lastname"));
                    loginResponse.setEmail(jsonObject1.optString("v_email"));
                    loginResponse.setCity(jsonObject1.optString("v_city"));
                    loginResponse.setState(jsonObject1.optString("v_state"));
                    loginResponse.setCountry(jsonObject1.optString("v_country"));
                    loginResponse.setProfilePic(jsonObject1.optString("v_profile_pic"));
                    loginResponse.setCoverPic(jsonObject1.optString("v_cover_pic"));
                    loginResponse.setIsDeleted(jsonObject1.optString("t_is_deleted"));
                    loginResponse.setIsActivated(jsonObject1.optString("t_is_active"));
                    loginResponse.setEmailVerified(jsonObject1.optString("t_email_verified"));
                    loginResponse.setStatus(jsonObject1.optString("t_status")); // for online and offline
                }


            } catch (JSONException e) {
                logDebug("<<<<< LoginResponse Parser()Response Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }
            return loginResponse;
        }

    }

    /**
     * Register Response
     */
    public static class RegisterResponse {
        private int statusCode;
        private String message;
        private String userId;
        private String sessionId;
        private String id;
        private String firstName;
        private String lastName;
        private String email;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getVerifiedEmail() {
            return verifiedEmail;
        }

        public void setVerifiedEmail(String verifiedEmail) {
            this.verifiedEmail = verifiedEmail;
        }

        private String verifiedEmail;

        public static RegisterResponse setRegisterResponse(JSONObject response) {
            RegisterResponse registerResponse = new RegisterResponse();

            try {

                registerResponse.setStatusCode(response.optInt("status_code"));
                registerResponse.setMessage(response.optString("message"));

                if (registerResponse.getStatusCode() == API_STATUS_ONE_ZERO_ONE) {

                } else {

                    JSONObject jsonObject = response.getJSONObject("data");

                    registerResponse.setUserId(jsonObject.optString("user_id"));
                    registerResponse.setSessionId(jsonObject.optString("session_id"));

                    JSONObject jsonObject1 = jsonObject.getJSONObject("userData");

                    registerResponse.setId(jsonObject1.optString("i_id"));
                    registerResponse.setFirstName(jsonObject1.optString("v_firstname"));
                    registerResponse.setLastName(jsonObject1.optString("v_lastname"));
                    registerResponse.setEmail(jsonObject1.optString("v_email"));
                    registerResponse.setVerifiedEmail(jsonObject1.optString("t_email_verified"));

                }

            } catch (JSONException e) {
                logDebug("<<<<< RegisterResponse Parser()Response Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return registerResponse;
        }


    }

    /**
     * ForgotPassword Response
     */
    public static class ForgotPasswordResponse {

        private int statusCode;
        private String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static ForgotPasswordResponse forgotPasswordResponse(JSONObject response) {

            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();

            forgotPasswordResponse.setStatusCode(response.optInt("status_code"));
            forgotPasswordResponse.setMessage(response.optString("message"));

            return forgotPasswordResponse;
        }


    }

    /**
     * UpdateStatus Response
     */
    public static class UpdateStatesResponse {

        private int statusCode;
        private String message;
        private String userStatus;

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static UpdateStatesResponse updateStatesResponse(JSONObject response) {
            UpdateStatesResponse updateStatesResponse = new UpdateStatesResponse();

            updateStatesResponse.setStatusCode(response.optInt("status_code"));
            updateStatesResponse.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");

                updateStatesResponse.setUserStatus(jsonObject.getString("t_status"));

            } catch (JSONException e) {
                logDebug("<<<<< UpdateStatesResponse Parser()Response Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return updateStatesResponse;
        }


    }

    /**
     * logout Api Response
     */
    public static class LogoutResponse {

        private int statusCode;

        private String message;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public static LogoutResponse logoutResponse(JSONObject response) {
            LogoutResponse logoutResponse = new LogoutResponse();
            logoutResponse.setStatusCode(response.optInt("status_code"));
            logoutResponse.setMessage(response.optString("message"));
            return logoutResponse;
        }


    }

    /**
     * User Profile Parser
     */
    public static class UserProfileParser {

        private static final String TAG = "UserProfileParser";

        private int statusCode;
        private String message;
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String city;
        private String state;
        private String country;
        private String profilePic;
        private String coverPic;
        private String isActive;
        private String isDeleted;
        private String gender;
        private String totalFriends;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getCoverPic() {
            return coverPic;
        }

        public void setCoverPic(String coverPic) {
            this.coverPic = coverPic;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getTotalFriends() {
            return totalFriends;
        }

        public void setTotalFriends(String totalFriends) {
            this.totalFriends = totalFriends;
        }

        public static UserProfileParser parseGetProfileResponse(JSONObject jsonObject) {
            if (jsonObject != null &&
                    jsonObject.length() > 0) {

                try {

                    UserProfileParser userProfileParser = new UserProfileParser();

                    userProfileParser.setStatusCode(jsonObject.getInt("status_code"));
                    userProfileParser.setMessage(jsonObject.getString("message"));

                    String data = jsonObject.getString("data");
                    JSONObject dataOb = new JSONObject(data);
                    userProfileParser.setId(dataOb.getString("i_id"));
                    userProfileParser.setFirstName(dataOb.getString("v_firstname"));
                    userProfileParser.setLastName(dataOb.getString("v_lastname"));
                    userProfileParser.setEmail(dataOb.getString("v_email"));
                    userProfileParser.setCity(dataOb.getString("v_city"));
                    userProfileParser.setState(dataOb.getString("v_state"));
                    userProfileParser.setCountry(dataOb.getString("v_country"));
                    userProfileParser.setProfilePic(dataOb.getString("v_profile_pic_url"));
                    userProfileParser.setCoverPic(dataOb.getString("v_cover_pic_url"));
                    userProfileParser.setIsActive(dataOb.getString("t_is_active"));
                    userProfileParser.setIsDeleted(dataOb.getString("t_is_deleted"));
                    userProfileParser.setGender(dataOb.getString("e_gender"));
                    userProfileParser.setTotalFriends(dataOb.optString("totalFriends"));

                    return userProfileParser;

                } catch (JSONException e) {
                    logDebug("<<<<< UserProfileParser Parser()Response Exception >>>>> \n ");
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

    }


    /**
     * Method for Log Printing
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.e(TAG, msg);
    }

}
