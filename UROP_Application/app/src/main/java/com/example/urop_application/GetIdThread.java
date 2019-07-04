package com.example.urop_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class GetIdThread extends AppCompatActivity {
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_id_thread);

//        if (getIdThread()) {
//            isLATToast();
//        } else {
//            isNotLATToast();
//        }

        getIdThread();
        Toast toast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG);
        toast.show();
    }

    public void getIdThread() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
            } catch (IOException e) {
                System.out.println("IOException found - process not executed.");
            } catch (GooglePlayServicesNotAvailableException e) {
                System.out.println("GooglePlayServicesNotAvailableException found - process not executed.");
            } catch (GooglePlayServicesRepairableException e) {
                System.out.println("GooglePlayServicesRepairableException found - process not executed.");
            }
            if (adInfo != null) {
                id = adInfo.getId();
                final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
            } else {
                id = "API available, adInfo null";
            }
        } else {
            id = "All unavailable";
        }

//        return isLAT;
    }

    public void isLATToast() {
        Toast toast = Toast.makeText(getApplicationContext(), "Limit Ad Tracking is enabled", Toast.LENGTH_LONG);
        toast.show();
    }

    public void isNotLATToast() {
        Toast toast = Toast.makeText(getApplicationContext(), "Limit Ad Tracking is NOT enabled", Toast.LENGTH_LONG);
        toast.show();
    }
}
