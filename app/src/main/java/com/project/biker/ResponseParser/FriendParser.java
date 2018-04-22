package com.project.biker.ResponseParser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul Padaliya on 8/17/2017.
 */

public class FriendParser {

    public static class FriendsListParser {

        private static final String TAG = "FriendsListParser";
        int statusCode;
        String message;
        String totalRows;
        String perPage;
        String currentPage;
        String lastPage;
        List<Record> recordList;

        public List<Record> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<Record> recordList) {
            this.recordList = recordList;
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

        public String getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(String totalRows) {
            this.totalRows = totalRows;
        }

        public String getPerPage() {
            return perPage;
        }

        public void setPerPage(String perPage) {
            this.perPage = perPage;
        }

        public String getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(String currentPage) {
            this.currentPage = currentPage;
        }

        public String getLastPage() {
            return lastPage;
        }

        public void setLastPage(String lastPage) {
            this.lastPage = lastPage;
        }

        public class Record {
            String id;
            String firstName;
            String lastName;
            String email;
            String gender;
            String city;
            String state;
            String country;
            String profiePic;
            String coverPic;
            String status;

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return id;
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

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
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

            public String getProfiePic() {
                return profiePic;
            }

            public void setProfiePic(String profiePic) {
                this.profiePic = profiePic;
            }

            public String getCoverPic() {
                return coverPic;
            }

            public void setCoverPic(String coverPic) {
                this.coverPic = coverPic;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

        }

        public static FriendsListParser friendListParser(JSONObject jsonObject) {
            if (jsonObject != null && jsonObject.length() > 0) {
                try {

                    FriendsListParser friendsListParser = new FriendsListParser();

                    friendsListParser.setStatusCode(jsonObject.optInt("status_code"));
                    logDebug( "status_code" + jsonObject.getString("status_code"));

                    friendsListParser.setMessage(jsonObject.getString("message"));
                    logDebug("message" + jsonObject.getString("message"));

                    JSONObject data = jsonObject.getJSONObject("data");
                    friendsListParser.setTotalRows(data.getString("total_rows"));
                    logDebug("total_rows" + data.getString("total_rows"));

                    friendsListParser.setPerPage(data.getString("per_page"));
                    logDebug("per_page" + data.getString("per_page"));

                    friendsListParser.setCurrentPage(data.getString("current_page"));
                    logDebug( "current_page" + data.getString("current_page"));

                    friendsListParser.setLastPage(data.getString("last_page"));
                    logDebug("last_page" + data.getString("last_page"));

                    JSONArray jsonArray = data.getJSONArray("record");

                    ArrayList<Record> recordArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        FriendsListParser.Record record = friendsListParser.new Record();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        record.setId(jsonObject1.getString("i_id"));
                        record.setFirstName(jsonObject1.getString("v_firstname"));
                        record.setLastName(jsonObject1.getString("v_lastname"));
                        record.setEmail(jsonObject1.getString("v_email"));
                        record.setGender(jsonObject1.getString("e_gender"));
                        record.setCity(jsonObject1.getString("v_city"));
                        record.setState(jsonObject1.getString("v_state"));
                        record.setCountry(jsonObject1.getString("v_country"));
                        record.setProfiePic(jsonObject1.getString("v_profile_pic"));
                        record.setCoverPic(jsonObject1.getString("v_cover_pic"));
                        record.setStatus(jsonObject1.getString("t_status"));
                        recordArrayList.add(record);

                        logDebug("ID--->" + jsonObject1.getString("i_id"));
                    }

                    friendsListParser.setRecordList(recordArrayList);
                   logDebug("recordArrayList Size--->" + recordArrayList.size());
                    return friendsListParser;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return null;
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

    /**
     * Parse Friend Pending
     */
    public static class FriendPending {

        private static final String TAG = "FriendPending";
        int statusCode;
        String message;
        String totalRows;
        String perPage;
        String currentPage;
        String lastPage;
        List<Record> recordList;

        public List<Record> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<Record> recordList) {
            this.recordList = recordList;
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

        public String getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(String totalRows) {
            this.totalRows = totalRows;
        }

        public String getPerPage() {
            return perPage;
        }

        public void setPerPage(String perPage) {
            this.perPage = perPage;
        }

        public String getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(String currentPage) {
            this.currentPage = currentPage;
        }

        public String getLastPage() {
            return lastPage;
        }

        public void setLastPage(String lastPage) {
            this.lastPage = lastPage;
        }

        public class Record {
            String id;
            String fullName;
            String email;
            String city;
            String state;
            String country;
            String profilePic;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
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
        }

        public static FriendPending pendingFriendParser(JSONObject jsonObject) {
            if (jsonObject != null && jsonObject.length() > 0) {
                try {

                    FriendPending friendPending = new FriendPending();
                    friendPending.setStatusCode(jsonObject.optInt("status_code"));
                    friendPending.setMessage(jsonObject.getString("message"));

                    JSONObject data = jsonObject.getJSONObject("data");
                    friendPending.setTotalRows(data.getString("total_rows"));

                    friendPending.setPerPage(data.getString("per_page"));
                    logDebug("per_page" + data.getString("per_page"));

                    friendPending.setCurrentPage(data.getString("current_page"));
                    logDebug( "current_page" + data.getString("current_page"));

                    friendPending.setLastPage(data.getString("last_page"));
                    logDebug("last_page" + data.getString("last_page"));

                    JSONArray jsonArray = data.getJSONArray("record");

                    ArrayList<Record> recordArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        FriendPending.Record record = friendPending.new Record();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        record.setId(jsonObject1.getString("i_id"));
                        record.setFullName(jsonObject1.getString("v_fullname"));
                        record.setEmail(jsonObject1.getString("v_email"));
                        record.setCity(jsonObject1.getString("v_city"));
                        record.setState(jsonObject1.getString("v_state"));
                        record.setCountry(jsonObject1.getString("v_country"));
                        record.setProfilePic(jsonObject1.getString("profie_pic"));
                        logDebug("FullName--->" + jsonObject1.getString("v_fullname"));
                        recordArrayList.add(record);
                    }

                    friendPending.setRecordList(recordArrayList);
                   logDebug("recordArrayList---->" + recordArrayList.size());


                    return friendPending;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
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


}
