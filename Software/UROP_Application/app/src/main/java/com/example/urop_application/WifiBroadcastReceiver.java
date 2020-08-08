package com.example.urop_application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: WIFI STATE CHANGED");
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiStateExtra == WifiManager.WIFI_STATE_DISABLING) {
                if (!MainActivity.isAnyTCPConnections()) {
                    String message = "Wifi being switched off when there are no active TCP/TCP6 connections";
                    FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.WIFI, message, context);
                }
//                Log.d(TAG, "Wifi switched off");
//                String message = "Wifi switched off";
//                FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.WIFI, message, context);
//                String messageReadFromFile = FileUtility.readFromInternalFile(context);
//                Log.d(TAG, messageReadFromFile);
            }
        }
    }
}