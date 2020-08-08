package com.example.urop_application;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
//    private static final String TAG = "BluetoothBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
//            Log.d(TAG, "onReceive: BLUETOOTH STATE CHANGED");
            String message = "Bluetooth state changed: ";
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    message += "Bluetooth off";
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    message += "Turning Bluetooth off";
                    break;
                case BluetoothAdapter.STATE_ON:
                    message += "Bluetooth on";
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    message += "Turning Bluetooth on";
                    break;
            }
            FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.BLUETOOTH, message, context);
        }
    }
}
