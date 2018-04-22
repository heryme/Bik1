package com.project.biker.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.project.biker.tools.SharePref;

import static com.project.biker.tools.Constant.HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN;
import static com.project.biker.tools.Constant.SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE;

/**
 * Created by Vikas Patel on 8/26/2017.
 */

public class JobUtil {

    private static String TAG = "JobUtil";
    public static void scheduleJob(Context context) {


        JobScheduler jobScheduler = null;
        SharePref sharePref = new SharePref(context);

        ComponentName serviceComponent = new ComponentName(context, SchdulerJobService.class);
        JobInfo.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            builder = new JobInfo.Builder(0, serviceComponent);
       //     builder.setMinimumLatency(8 * 1000); // wait at least
         //   builder.setOverrideDeadline(8 * 1000); // maximum delay
            Log.d(TAG,"scheduleJob");

            /**
             * it set the interval depending on user currently open which screen
             * change the background task delay execution  according to screen
             * 1 for ride
             * 2 for home
             * 0 for in background
             */
            if (sharePref.getExecutionTime().equalsIgnoreCase("1")) {
                Log.d(TAG,"execution is one");
                builder.setMinimumLatency(RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN);
                builder.setOverrideDeadline(RIDE_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN);
                sharePref.setExecutionTime("0");
            } else if (sharePref.getExecutionTime().equalsIgnoreCase("2")) {
                Log.d(TAG,"execution is two");
                builder.setMinimumLatency(HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN);
                builder.setOverrideDeadline(HOME_SERVICE_SEND_LAT_LONG_INTERVAL_APP_OPEN);
                sharePref.setExecutionTime("0");
            } else {
                Log.d(TAG,"execution is zero");
                builder.setMinimumLatency(SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE);
                builder.setOverrideDeadline(SERVICE_SEND_LAT_LONG_INTERVAL_APP_CLOSE);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Log.d(TAG,"scheduleJob 123");
                jobScheduler = context.getSystemService(JobScheduler.class);
                jobScheduler.schedule(builder.build());
                Log.d(TAG,"scheduleJob 456");
            }


        }
    }


}
