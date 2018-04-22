package com.project.biker.google_place_api;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.project.biker.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Rahul Padaliya on 5/18/2017.
 */
public class Utils {

    private static final String TAG = "Utils";

    /**
     * Return JSON response
     *
     * @param url
     * @return string
     */
    public static String getService(String url) {

        InputStream is = null;
        String json = "";

		/* Making HTTP request */
        try {
            /* defaultHttpClient */
            DefaultHttpClient httpClient = new DefaultHttpClient();
            logDebug("URL" + url);
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            logDebug(" <<<< GetService UnsupportedEncodingException >>>>");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            logDebug(" <<<< GetService ClientProtocolException >>>>");
            e.printStackTrace();
        } catch (IOException e) {
            logDebug(" <<<< GetService IOException >>>>");
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            logDebug(" <<<< Error converting result >>>>");
            e.printStackTrace();
        }

        return json;

    }

    /**
     * City,State and Country Change dialog
     */
    public static void openChangeCityStateCountryDialog(final Context context,
                                                        final AutoCompleteTextView act_dialogChangeCityStateCountry
    ) {

        act_dialogChangeCityStateCountry
                .setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.row_google_autocomplete_text, false));

        act_dialogChangeCityStateCountry.setCursorVisible(false);

		/* AutoComplete on click */
        act_dialogChangeCityStateCountry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                act_dialogChangeCityStateCountry.setFocusableInTouchMode(true);
                act_dialogChangeCityStateCountry.requestFocus();
                act_dialogChangeCityStateCountry.setCursorVisible(true);
            }
        });

		/* AutoComplete on item click *//*
        act_dialogChangeCityStateCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                *//*
                 * Get data associated with the specified position in the list
				 * (AdapterView)
				 *//*

                act_dialogChangeCityStateCountry.setFocusableInTouchMode(false);
                act_dialogChangeCityStateCountry.setCursorVisible(false);
                HashMap<String, Object> mapPlaceIdAndDesc = GooglePlaceApi.getPlaceIdAndDesc(position);

                act_dialogChangeCityStateCountry.setText(mapPlaceIdAndDesc.get("DESCRIPTION").toString());

				*//* set user selected city,country,state *//*
                String userSelectedCityStateCountry = mapPlaceIdAndDesc.get("DESCRIPTION").toString();
                String place_id = mapPlaceIdAndDesc.get("PLACE_ID").toString();
                act_dialogChangeCityStateCountry.setText(userSelectedCityStateCountry);
                GooglePlaceParser googlePlaceParser ;

                //Call API For Get The Address
                AddressService.getAddress(context, getPlaceInfo(place_id), new APIService.Success<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("TAG","Place Response-->" + response.toString());
                        GooglePlaceParser googlePlaceParser =  GooglePlaceParser.parseResponse(response);


                    }
                });
            }
        });
    }

    *//**
         * Pass Place Id  And Return Full API
         * @param input
         * @return
         *//*
    public static String getPlaceInfo(String input) {
        String place = "https://maps.googleapis.com/maps/api/place/details/json?"+
                "placeid="+input+"&key=" + Constant.API_KEY;
        Log.d("FINAL URL:::   ","----------->"+ place);
        //return urlString.toString();
        return place;
    }*/
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
