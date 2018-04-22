package com.project.biker.ResponseParser;

import android.util.Log;

import com.project.biker.model.ModelBenefitList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vikas Patel on 8/17/2017.
 */

public class BenefitParser {

    private static final String TAG = "BenefitParser";

    public static ArrayList<ModelBenefitList> benefitLists = new ArrayList<>();

    int statusCode;
    String message;

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

    public static BenefitParser benefitResponse(JSONObject response) {
        BenefitParser benefitParser = new BenefitParser();
        try {
            JSONObject jsonObject = response.getJSONObject("data");

            benefitParser.setStatusCode(response.optInt("status_code"));
            benefitParser.setMessage(response.optString("message"));

            JSONArray jsonArray = jsonObject.getJSONArray("record");

            benefitLists.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                ModelBenefitList model = new ModelBenefitList();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                model.setBenefitId(jsonObject1.optString("i_id"));
                model.setLati(jsonObject1.optString("d_latitude"));
                model.setLongi(jsonObject1.optString("d_longitude"));
                model.setState(jsonObject1.optString("v_state"));
                model.setDescription(jsonObject1.optString("t_description"));
                model.setBenefitCoverUrl(jsonObject1.optString("benefit_cover_image_url"));
                model.setCountry(jsonObject1.optString("v_country"));
                model.setBenefitDescriptionUrl(jsonObject1.optString("benefit_desceiption_image_url"));
                model.setBenefitPrice(jsonObject1.optString("v_benefit_price"));
                model.setCity(jsonObject1.optString("v_city"));
                model.setBenefitFormattedAddress(jsonObject1.optString("t_formatted_address"));
                model.setBenefitLogoUrl(jsonObject1.optString("benefit_logo_image_url"));
                model.setBenefitName(jsonObject1.optString("v_benefit_name"));

                benefitLists.add(model);
            }

        } catch (JSONException e) {
            logDebug("<<<<< Benefit Parser()Response Exception >>>>> \n ");
            e.printStackTrace();
            return null;
        }


        return benefitParser;
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
