package com.project.biker.google_place_api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rahul Padaliya on 5/19/2017.
 */
public class GooglePlaceParser {

    private static final String TAG = "GooglePlaceParser";

    private static String RESULT = "result";
    private static String ADDRESS_COMPONENTS = "address_components";
    private static String LONG_NAME = "long_name";
    private static String TYPES = "types";


    String city;
    String state;
    String country;
    String latitude;
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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


    public static GooglePlaceParser parseResponse(JSONObject jsonObject) {

        GooglePlaceParser googlePlaceParser = new GooglePlaceParser();
        String city = "", state = "", country = "";
        if (jsonObject != null && jsonObject.length() > 0) {
            try {
                jsonObject.getString("result");
                logDebug("result--->" + jsonObject.getString(RESULT));

                JSONObject main = jsonObject.getJSONObject(RESULT);
                JSONArray address_components = main.getJSONArray(ADDRESS_COMPONENTS);

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject jsonObject1 = address_components.getJSONObject(i);
                    logDebug("I-->" + i);
                    logDebug("long_name-->" + jsonObject1.getString(LONG_NAME));
                    JSONArray typeArray = jsonObject1.getJSONArray(TYPES);

                    for (int j = 0; j < typeArray.length(); j++) {
                        String type = typeArray.getString(j);
                        logDebug("type --->" + type);

                        if (type.equalsIgnoreCase("locality")) {
                            city = jsonObject1.getString(LONG_NAME);
                            googlePlaceParser.setCity(city);
                        }

                        if (type.equals("administrative_area_level_1")) {
                            state = jsonObject1.getString(LONG_NAME);
                            googlePlaceParser.setState(state);
                        }

                        if (type.equals("country")) {
                            country = jsonObject1.getString(LONG_NAME);
                            googlePlaceParser.setCountry(country);
                        }

                        logDebug("City-->" + city + "  " + "State-->" + state + " " + "Country--> " + country);
                    }

                    JSONObject geometry = main.optJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    logDebug("location---->" + geometry.optJSONObject("location"));
                    location.getString("lat");
                    googlePlaceParser.setLatitude(location.getString("lat"));
                    googlePlaceParser.setLongitude(location.getString("lng"));

                    location.getString("lng");
                    logDebug("lng---->" + location.getString("lng"));
                    logDebug("lat---->" + location.getString("lat"));
                }
                return googlePlaceParser;
            } catch (JSONException e) {
                logDebug("<<<< Google Parser Json Exception >>>>");
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
