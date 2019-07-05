package com.example.urop_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class GetIdThread extends AppCompatActivity {
    private static final String TAG = "GetIdThread";

    private Handler thisHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_id_thread);

//        if (getIdThread()) {
//            isLATToast();
//        } else {
//            isNotLATToast();
//        }

    }


    public void startThread(View view) {
        ExampleThread thread = new ExampleThread(this, getApplicationContext());
        thread.start();
    }

    public void stopThread(View view) {

    }

    public void isLATToast() {
        Toast toast = Toast.makeText(getApplicationContext(), "Limit Ad Tracking is enabled", Toast.LENGTH_LONG);
        toast.show();
    }

    public void isNotLATToast() {
        Toast toast = Toast.makeText(getApplicationContext(), "Limit Ad Tracking is NOT enabled", Toast.LENGTH_LONG);
        toast.show();
    }

    class ExampleThread extends Thread {
        private String id;
        Context context;
        Context applicationContext;

        public ExampleThread(Context context, Context applicationContext) {
            this.context = context;
            this.applicationContext = applicationContext;
        }

        @Override
        public void run() {
            id = "Hello world";
            getIdThread();
            thisHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(applicationContext, id, Toast.LENGTH_LONG);
                    toast.show();
                }
            });

        }

        public void getIdThread() {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                AdvertisingIdClient.Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext);
                } catch (IOException e) {
                    System.out.println("IOException found - process not executed.");
                } catch (IllegalStateException e) {
                    System.out.println("IllegalStateException found - process not executed.");
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
    }
}
