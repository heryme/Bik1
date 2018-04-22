package com.project.biker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import static com.project.biker.service.JobUtil.scheduleJob;

/**
 * Created by Vikas Patel on 8/26/2017.
 */

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scheduleJob(context);
        }
    }

    /**
     * this receiver is used to restart scheduler after
     * phone is restart
     */
}