package com.project.biker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by Vikas Patel on 8/28/2017.
 */

public class AutoStartPermission {

    /**
     * this method is check the re-direct
     * the use to autostart screen in phone setting
     *
     * @return
     */
    public static void autoStartBackgroundPermission(Context context) {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is check the manufacturer
     * of phone to start autostart Permission
     *
     * @return
     */
    public static boolean displayAutoStartPermission() {
        Boolean status = false;
        String manufacturer = android.os.Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            status = true;
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            status = true;
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            status = true;
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            status = true;
        } else if ("Honor".equalsIgnoreCase(manufacturer)) {
            status = true;
        }
        return status;
    }

}
