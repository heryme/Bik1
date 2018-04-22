package com.project.biker.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vikas Patel on 8/28/2017.
 */

public class SharePrefAutostart {

    Context mContext;
    SharedPreferences sharedPref;

    public SharePrefAutostart(Context mContext) {
        this.mContext = mContext;
        sharedPref = mContext.getSharedPreferences(Constant.SharedPrefConstant.SP_AUTO_PERMISSION, Context.MODE_PRIVATE);
    }

    public void setFirstTimeStatus(String firstTimeStatus) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SharedPrefConstant.KEY_FIRST_TIME, firstTimeStatus);
        editor.commit();
    }

    public String getFirstTimeStatus() {
        return sharedPref.getString(Constant.SharedPrefConstant.KEY_FIRST_TIME, "0");
    }
}
