package com.example.urop_application;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VPNJobService extends JobService {
    private static final String TAG = "VPNJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//              VPN on detection taken from Stack Overflow
                Log.d(TAG, "JobService started");
                List<String> networkList = new ArrayList<>();
                try {
                    for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                        if (networkInterface.isUp())
                            networkList.add(networkInterface.getName());
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "isVpnUsing Network List not received");
                }

                boolean vpnOn = networkList.contains("tun0");
//                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                NetworkInfo info = null;
//                if (cm != null) {
//                    info = cm.getActiveNetworkInfo();
//                    if (info == null || !info.isConnected()) {
//                        return;
//                    }
//                }
                if (vpnOn) {
//                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    assert wm != null;
                    List<ScanResult> wifiList = wm.getScanResults();

                    WifiInfo info = wm.getConnectionInfo();
                    String currentSSID = info.getSSID();

                    if (wifiList != null) {
                        for (ScanResult wifi : wifiList) {
                            if (currentSSID.equals(wifi.SSID)) {
                                String capabilities = wifi.capabilities;
                                Log.d(TAG, wifi.SSID + " capabilities : " + capabilities);
                                if (!(capabilities.toUpperCase().contains("WEP") || capabilities.toUpperCase().contains("WPA")
                                        || capabilities.toUpperCase().contains("WPA2"))) {
                                    // Open Network (not secured)
                                    Toast.makeText(VPNJobService.this, "VPN connected while using public network", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "VPN connected while using public network");
                                }
                            }

                        }
                    }
                }

                jobFinished(jobParameters, false);
            }
//                for (int i = 0; i < 10; i++) {
//                    if (jobCancelled) {
//                        return;
//                    }
//                    Log.d(TAG, "run: " + i);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.d(TAG, "Job finished");
        }).start();

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return false;
    }

}
