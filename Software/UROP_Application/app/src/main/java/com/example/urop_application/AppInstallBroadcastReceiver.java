package com.example.urop_application;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AppInstallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInstallBroadcastReceiver";
    private static final int EXTRA_DEFAULT = -1;
    private String storeName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            Log.d(TAG, "New app installed");
            int uid = intent.getIntExtra(Intent.EXTRA_UID, EXTRA_DEFAULT);
            if (uid != EXTRA_DEFAULT) {
                /* Get name of app that has just been installed */
                PackageManager packageManager = context.getPackageManager();
                /* uid is NOT unique! */
                String[] packageNames = packageManager.getPackagesForUid(uid);
                assert packageNames != null;
                for (int i = 0; i < packageNames.length; i++) {
                    String packageName = packageNames[0];
                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                        String storePackageName = packageManager.getInstallerPackageName(applicationInfo.packageName);
                        if ("com.android.vending".equals(storePackageName)) {
                            storeName = "Google Play Store";
                        } else if ("com.amazon.venezia".equals(storePackageName)) {
                            Log.d(TAG, "onReceive: AMAZON");
                            storeName = "Amazon App Store";
                        } else {
                            storeName = "Other store";
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }



                }
            }

            /* Ask the user here if they know the developer name and store name of the app they have just downloaded */
        }
    }

}
