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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AdvertisingID extends AppCompatActivity {
    private static final String TAG = "AdvertisingID";

    private Handler thisHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_id_thread);


    }


    public void startThread(View view) {
        ExampleThread thread = new ExampleThread(this);
        thread.start();
    }

    public void stopThread(View view) {

    }

    class ExampleThread extends Thread {
        private String id;
        Context context;
        Context applicationContext;

        public ExampleThread(Context context) {
            this.context = context;
            this.applicationContext = context.getApplicationContext();
        }

        @Override
        public void run() {
            id = "Hello world";
            getIdThread();
//          Need to change reading from and writing to file into something actually useful
            writeToFile(id, context);
            final String idFromFile = readFromFile(context);
            thisHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast1 = Toast.makeText(applicationContext, id, Toast.LENGTH_LONG);
                    toast1.show();
//                    Toast toast2 = Toast.makeText(applicationContext, idFromFile, Toast.LENGTH_LONG);
//                    toast2.show();
                }
            });

        }

        public void getIdThread() {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                AdvertisingIdClient.Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext);
                } catch (IOException e) {
                    Log.e("Exception", "IOException found - process not executed");
                } catch (IllegalStateException e) {
                    Log.e("Exception", "IllegalStateException found - process not executed");
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("Exception", "GooglePlayServicesNotAvailableException found - process not executed");
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("Exception", "GooglePlayServicesRepairableException found - process not executed");
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

        //  Function for writing to file from Stack Overflow
        public void writeToFile(String data, Context context) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        //  Function for reading from file from Stack Overflow
        private String readFromFile(Context context) {
            String ret = "";

            try {
                InputStream inputStream = context.openFileInput("config.txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }
    }
}
