package com.example.urop_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.Marker;

public class AddTrustedPlaceDialogFragment extends DialogFragment {
    private static final String TAG = "AddTrustedPlaceDialogFr";
    private String text = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input)
                .setMessage(R.string.add_trusted_place_dialog_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Marker marker = MapsActivity.getMarker();
                        Location locationToAdd;
                        if (marker == null) {
                            locationToAdd = MapsActivity.getmLastKnownLocation();
                        } else {
                            locationToAdd = new Location("");
                            locationToAdd.setLatitude(marker.getPosition().latitude);
                            locationToAdd.setLongitude(marker.getPosition().longitude);
                        }
                        for (Location location : MainActivity.getTrustedLocations()) {
                            if (location.getLatitude() == locationToAdd.getLatitude() && location.getLongitude() == locationToAdd.getLongitude()) {
                                Log.d(TAG, "This location is already a trusted place.");
                                return;
                            }
                        }
                        text = input.getText().toString();
                        MapsActivity.addLocationToTrustedLocations(locationToAdd);
                        /* Need to ask user for name of trusted place */
                        MapsActivity.addLatLongGeofence(text);
                        Log.d(TAG, text + " added as trusted place (with geofence)");

                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
