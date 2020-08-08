package com.example.urop_application;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;

public class PasswordReceiver extends DeviceAdminReceiver {
    private static final String TAG = "PasswordReceiver";

    @Override
    public void onPasswordChanged(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
//        Toast.makeText(context, "Password has been changed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onPasswordChanged: worked");
        String message = "Password changed";
        FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.PASSWORD, message, context);
    }

}
