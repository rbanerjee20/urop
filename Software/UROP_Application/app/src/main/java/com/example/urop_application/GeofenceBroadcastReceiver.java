package com.example.urop_application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/* Based on Android documentation example */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String message;

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            MainActivity.setTrustedPlaceBoolean(true);
            StringBuilder builder = new StringBuilder();
            builder.append("Dwelling in the following trusted place(s): ");
            List<Geofence> geofencesEntered = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence: geofencesEntered) {
                 builder.append(geofence.getRequestId());
            }
            message = builder.toString();
            Log.d(TAG, message);
            FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.GEOFENCE_TEST, message, context);

            /* Untested - what I actually need to do */

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            MainActivity.setTrustedPlaceBoolean(false);
            message = "Left trusted place";
            Log.d(TAG, message);
            FileUtility.writeToTrackingFile(MainActivity.TrackingActivity.GEOFENCE_TEST, message, context);
        }
    }
}
