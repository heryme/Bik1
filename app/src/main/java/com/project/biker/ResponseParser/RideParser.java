package com.project.biker.ResponseParser;

import android.util.Log;

import com.project.biker.model.ModelGetRiderLatLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rahul Padaliya on 7/21/2017.
 */
public class RideParser {

    private static final String TAG = "RideParser";

    public static class InvitationResponse {
        private int statusCode;
        private String message;
        private static ArrayList<InvitationItem> invitationItemArrayList;

        public ArrayList<InvitationItem> getInvitationItemArrayList() {
            return invitationItemArrayList;
        }

        public void setInvitationItemArrayList(ArrayList<InvitationItem> invitationItemArrayList) {
            this.invitationItemArrayList = invitationItemArrayList;
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

        public static class InvitationItem {
            private String rideId;
            private String createdRideId;
            private String meetup;
            private String destination;
            private String createrRideName;
            private String createrRideEmail;
            private String createrRideProfilePic;
            private String createrRideCoverPic;
            private String rideRequestTime;
            private int rideRequest;
            private int rideAccept;

            public String getRideRequestTime() {
                return rideRequestTime;
            }

            public void setRideRequestTime(String rideRequestTime) {
                this.rideRequestTime = rideRequestTime;
            }

            public int getRideRequest() {
                return rideRequest;
            }

            public void setRideRequest(int rideRequest) {
                this.rideRequest = rideRequest;
            }

            public int getRideAccept() {
                return rideAccept;
            }

            public void setRideAccept(int rideAccept) {
                this.rideAccept = rideAccept;
            }

            public String getRideId() {
                return rideId;
            }

            public void setRideId(String rideId) {
                this.rideId = rideId;
            }

            public String getCreatedRideId() {
                return createdRideId;
            }

            public void setCreatedRideId(String createdRideId) {
                this.createdRideId = createdRideId;
            }

            public String getMeetup() {
                return meetup;
            }

            public void setMeetup(String meetup) {
                this.meetup = meetup;
            }

            public String getDestination() {
                return destination;
            }

            public void setDestination(String destination) {
                this.destination = destination;
            }

            public String getCreaterRideName() {
                return createrRideName;
            }

            public void setCreaterRideName(String createrRideName) {
                this.createrRideName = createrRideName;
            }

            public String getCreaterRideEmail() {
                return createrRideEmail;
            }

            public void setCreaterRideEmail(String createrRideEmail) {
                this.createrRideEmail = createrRideEmail;
            }

            public String getCreaterRideProfilePic() {
                return createrRideProfilePic;
            }

            public void setCreaterRideProfilePic(String createrRideProfilePic) {
                this.createrRideProfilePic = createrRideProfilePic;
            }

            public String getCreaterRideCoverPic() {
                return createrRideCoverPic;
            }

            public void setCreaterRideCoverPic(String createrRideCoverPic) {
                this.createrRideCoverPic = createrRideCoverPic;
            }

        }

        public static InvitationResponse invitation(JSONObject jsonObject) {

            InvitationResponse invitationResponse = new InvitationResponse();
            try {
                if (jsonObject != null && jsonObject.length() > 0) {

                    invitationItemArrayList = new ArrayList<>();
                    invitationResponse.setStatusCode(jsonObject.getInt("status_code"));
                    invitationResponse.setMessage(jsonObject.getString("message"));

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        InvitationItem item = new InvitationItem();
                        item.setRideId(jo.getString("rideId"));
                        item.setCreatedRideId(jo.getString("createdRideId"));
                        item.setMeetup(jo.getString("meetup"));
                        item.setDestination(jo.getString("destination"));
                        item.setCreaterRideName(jo.getString("createrRideName"));
                        item.setCreaterRideEmail(jo.getString("createrRideEmail"));
                        item.setCreaterRideProfilePic(jo.getString("createrRideProfilePic"));
                        item.setCreaterRideCoverPic(jo.getString("createrRideCoverPic"));
                        item.setRideRequestTime(jo.optString("invitationTime"));
                        item.setRideRequest(jo.optInt("totalBiker"));
                        item.setRideAccept(jo.optInt("acceptedBiker"));

                        invitationItemArrayList.add(item);
                    }

                    invitationResponse.setInvitationItemArrayList(invitationItemArrayList);

                }
            } catch (Exception e) {
                logDebug("<<<<< Invitation Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return invitationResponse;
        }

    }

    public static class CreateRideResponse {

        String riderId;
        String message;
        int statusCode;

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

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

        public static CreateRideResponse createRideResponse(JSONObject response) {

            CreateRideResponse createRideResponse = new CreateRideResponse();
            createRideResponse.setStatusCode(response.optInt("status_code"));
            createRideResponse.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");
                createRideResponse.setRiderId(jsonObject.optString("rideId"));
            } catch (JSONException e) {
                logDebug("<<<<< CreateRideResponse Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }


            return createRideResponse;
        }


    }

    public static class RiderLatLong {

        int statusCode;
        String message;
        String meetUpLatitude;
        String meetUpLongitude;
        String destinationLatitude;
        String destinationLongitude;
        String riderId;
        String rideStatusName;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRideStatusName() {
            return rideStatusName;
        }

        public void setRideStatusName(String rideStatusName) {
            this.rideStatusName = rideStatusName;
        }

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

        public String getMeetUpLatitude() {
            return meetUpLatitude;
        }

        public void setMeetUpLatitude(String meetUpLatitude) {
            this.meetUpLatitude = meetUpLatitude;
        }

        public String getMeetUpLongitude() {
            return meetUpLongitude;
        }

        public void setMeetUpLongitude(String meetUpLongitude) {
            this.meetUpLongitude = meetUpLongitude;
        }

        public String getDestinationLatitude() {
            return destinationLatitude;
        }

        public void setDestinationLatitude(String destinationLatitude) {
            this.destinationLatitude = destinationLatitude;
        }

        public String getDestinationLongitude() {
            return destinationLongitude;
        }

        public void setDestinationLongitude(String destinationLongitude) {
            this.destinationLongitude = destinationLongitude;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public static ArrayList<ModelGetRiderLatLong> getRiderDetailList() {
            return riderDetailList;
        }

        public static void setRiderDetailList(ArrayList<ModelGetRiderLatLong> riderDetailList) {
            RiderLatLong.riderDetailList = riderDetailList;
        }

        public static ArrayList<ModelGetRiderLatLong> riderDetailList = new ArrayList<>();

        public static RiderLatLong riderLatLong(JSONObject response) {
            RiderLatLong riderLatLong = new RiderLatLong();

            try {
                riderLatLong.setStatusCode(response.optInt("status_code"));
                riderLatLong.setMessage(response.optString("message"));

                JSONObject jsonObject = response.getJSONObject("data");
                JSONObject jsonObject1 = jsonObject.getJSONObject("rideDetails");

                riderLatLong.setDestinationLatitude(jsonObject1.optString("destination_latitude"));
                riderLatLong.setDestinationLongitude(jsonObject1.optString("destination_longitude"));
                riderLatLong.setMeetUpLatitude(jsonObject1.optString("meetup_latitude"));
                riderLatLong.setMeetUpLongitude(jsonObject1.optString("meetup_longitude"));
                riderLatLong.setRiderId(jsonObject1.optString("i_id"));
                riderLatLong.setRideStatusName(jsonObject1.optString("e_ride_status_name"));

                JSONArray jsonArray = jsonObject.getJSONArray("userDetails");

                riderDetailList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {

                    ModelGetRiderLatLong model = new ModelGetRiderLatLong();
                    JSONObject jsonObject12 = jsonArray.getJSONObject(i);

                    model.setUserId(jsonObject12.optString("i_user_id"));
                    model.setRiderLat(jsonObject12.optString("latitude"));
                    model.setRiderLong(jsonObject12.optString("longitude"));
                    model.setFirstName(jsonObject12.optString("v_firstname"));
                    model.setLastName(jsonObject12.optString("v_lastname"));
                    model.setUserType(jsonObject12.optString("t_type"));
                    model.setIsFriend(jsonObject12.optString("is_friend"));
                    riderDetailList.add(model);
                }

            } catch (JSONException e) {
                logDebug("<<<<< RiderLatLong Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }
            return riderLatLong;
        }


    }

    public static class RideStart {

        int statusCode;
        String riderId;
        String message;

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

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

        public static RideStart rideStart(JSONObject response) {
            RideStart rideStart = new RideStart();

            rideStart.setStatusCode(response.optInt("status_code"));
            rideStart.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");
                rideStart.setRiderId(jsonObject.optString("rideId"));

            } catch (JSONException e) {
                logDebug("<<<<< RiderStart Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return rideStart;
        }


    }

    public static class RideStop {

        int statusCode;
        String riderId;
        String message;

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

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

        public static RideStop rideStop(JSONObject response) {
            RideStop rideStop = new RideStop();

            rideStop.setStatusCode(response.optInt("status_code"));
            rideStop.setMessage(response.optString("message"));

            try {
                JSONObject jsonObject = response.getJSONObject("data");
                rideStop.setRiderId(jsonObject.optString("rideId"));

            } catch (JSONException e) {
                logDebug("<<<<< RiderStop Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;
            }

            return rideStop;
        }


    }

    public static class RideStatusResponse {

        int statusCode;
        String riderId;
        String message;
        String rideStatusName;

        public String getRideStatusName() {
            return rideStatusName;
        }

        public void setRideStatusName(String rideStatusName) {
            this.rideStatusName = rideStatusName;
        }

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

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

        public static RideStatusResponse rideStatusResponse(JSONObject response) {
            RideStatusResponse rideStatusResponse = new RideStatusResponse();
            rideStatusResponse.setStatusCode(response.optInt("status_code"));
            rideStatusResponse.setMessage(response.optString("message"));
            //1=Not Started, 2=Started, 3=Completed, 4=Canceled
            try {
                JSONObject jsonObject = response.getJSONObject("data");
                rideStatusResponse.setRideStatusName(jsonObject.optString("e_ride_status_name"));
                rideStatusResponse.setRiderId(jsonObject.optString("i_ride_id"));

            } catch (JSONException e) {
                logDebug("<<<<< RideStatusResponse Parser()Error Exception >>>>> \n ");
                e.printStackTrace();
                return null;

            }

            return rideStatusResponse;
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



