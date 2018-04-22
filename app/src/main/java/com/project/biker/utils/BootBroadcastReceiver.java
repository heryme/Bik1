package com.project.biker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.project.biker.service.GpsService;
import com.project.biker.tools.SharePref;

/**
 * Created by Vikas Patel on 7/31/2017.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    SharePref sharePref;
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals(ACTION)) {
            sharePref=new SharePref(context);
            //Service
            if(sharePref.getUserStatus().equals("1"))
            {
                context.startService(new Intent(context, GpsService.class));
            }

        }
    }
}