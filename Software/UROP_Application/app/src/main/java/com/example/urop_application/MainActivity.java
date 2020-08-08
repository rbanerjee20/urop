package com.example.urop_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.urop_application.Whitelists.AdBlockerWhitelist;
import com.example.urop_application.Whitelists.AntivirusWhitelist;
import com.example.urop_application.Whitelists.FinanceShoppingWhitelist;
import com.example.urop_application.Whitelists.Whitelist;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    private static final String SHOPPING_CSV_PATH = "shopping_top_100_review_count.csv";
    private static final String FINANCE_CSV_PATH = "finance_top_100_review_count.csv";
    private static final String COMMA_DELIMITER = ",";

    //    private static final int FIFTEEN_MINUTES_IN_MILLISECONDS = 15 * 60 * 1000;
    private static final String TAG = "MainActivity";
    private static String adBlockerName = "", antivirusName = "", financeShoppingName = "";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static String getTrackingFileInfo() {
        return trackingFileInfo;
    }

    private static String trackingFileInfo = "";
    BluetoothBroadcastReceiver bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
    WifiBroadcastReceiver wifiBroadcastReceiver = new WifiBroadcastReceiver();
    ScreenLockReceiver screenLockReceiver = new ScreenLockReceiver();
    AppInstallBroadcastReceiver appInstallReceiver = new AppInstallBroadcastReceiver();

    private Handler handler;
    private Runnable runnable;
    private static final int ONE_MINUTE_IN_MILLISECONDS = 60 * 1000;


    private boolean advertisingIDFlag = false,
            adblockerFlag = false,
            antivirusFlag = false,
            VPNFlag = false,
            wifiFlag = false,
            sensorFlag = false,
            financeShoppingFlag = false;


    private boolean VPNOn = false;

    private boolean bluetoothReceiverOn = false, wifiReceiverOn = false, screenLockReceiverOn = false, appInstallReceiverOn = false;

    private CheckBox advertisingIDCheckBox,
            adblockerCheckBox,
            antivirusCheckBox,
            bluetoothCheckBox,
            passwordCheckBox,
            VPNCheckBox,
            sensorCheckBox,
            wifiCheckBox,
            financeShoppingCheckBox,
            appInstallCheckBox;

    private List<CheckBox> checkBoxes;
    private boolean someCheckBoxChecked = false;
    private boolean pollingCurrently = false;

    ComponentName passwordComponentName;
    public static final int RESULT_ENABLE = 11;

    private String id = "";
    private String pollId = "";

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener sensorEventListener;


    private final String TAG_GEOFENCE = "Geofence Tag";

    public static List<Geofence> getGeofenceList() {
        return geofenceList;
    }

    private static List<Geofence> geofenceList;
    private PendingIntent pendingIntent;

    public static GeofencingClient getClient() {
        return client;
    }

    private static GeofencingClient client;

    private static boolean inTrustedPlace;
    /* Hardcoded San Francisco International Airport coordinates for testing purposes */
//    private double currLatitude = 37.783888;
//    private double currLongitude = -122.4009012;

    private double currLatitude = 0;
    private double currLongitude = 0;
    FusedLocationProviderClient locationProviderClient;
    private static final int GEOFENCE_RADIUS_IN_METRES = 100;

    private static List<Location> trustedLocations = new ArrayList<>();


    public static List<Location> getTrustedLocations() {
        return trustedLocations;
    }

    private boolean mLocationPermissionGranted = false;


    private static final String trackingFileName = FileUtility.getTrackingFileName();

    private static final String TCPexampleLine = "8: 7601A8C0:BD2D 4EA9D9AC:01BB 01 00000000:00000234 00:00000000 00000000 10027        0 306435 1 0000000000000000 22 3 28 10 1400";
    private static final String TCP6exampleLine = "      15: C523002A003F184E273C2A2087BE781D:A8A0 5014002A00080940000000000A200000:01BB 01 00000000:00000000 00:00000000 00000000 10003        0 308896 2 0000000000000000 22 5 22 10 1400";

    private static final String TCP_FILE_NAME = "/proc/net/tcp";
    private static final String TCP6_FILE_NAME = "/proc/net/tcp6";
    private static boolean anyTCPConnections;

//    private static final int SENSOR_SENSITIVITY = 4;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appInstallReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordComponentName = new ComponentName(this, PasswordReceiver.class);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        advertisingIDCheckBox = findViewById(R.id.advertisingIDCheckBox);
        adblockerCheckBox = findViewById(R.id.adblockerCheckBox);
        antivirusCheckBox = findViewById(R.id.antivirusCheckBox);
        bluetoothCheckBox = findViewById(R.id.bluetoothCheckBox);
        passwordCheckBox = findViewById(R.id.passwordCheckBox);
        VPNCheckBox = findViewById(R.id.VPNCheckBox);
        sensorCheckBox = findViewById(R.id.sensorCheckBox);
        wifiCheckBox = findViewById(R.id.wifiCheckBox);
        financeShoppingCheckBox = findViewById(R.id.financeShoppingCheckBox);
        appInstallCheckBox = findViewById(R.id.appInstalledCheckBox);

        checkBoxes = new ArrayList<>(Arrays.asList(advertisingIDCheckBox, adblockerCheckBox, antivirusCheckBox,
                bluetoothCheckBox, passwordCheckBox, VPNCheckBox, sensorCheckBox, wifiCheckBox, financeShoppingCheckBox, appInstallCheckBox));

        geofenceList = new ArrayList<>();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        client = LocationServices.getGeofencingClient(this);


//        /* Populating geofenceList */
//        geofenceList.add(new Geofence.Builder()
//                .setRequestId("Home")
//                .setCircularRegion(currLatitude, currLongitude, GEOFENCE_RADIUS_IN_METRES)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build());

//        Log.d(TAG, trustedLocations.toString());

        FileUtility.writeToInternalFile("", getApplicationContext());
    }

    public void startAllPollingServices(View view) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                someCheckBoxChecked = true;
            }
        }
        if (!someCheckBoxChecked) {
            Toast.makeText(this, "Please select a checkbox to start tracking!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            FileUtility.writeToTrackingFile(TrackingActivity.TRACKING_START_STOP, "Tracking started", getApplicationContext());
        }
        pollingCurrently = true;
        StringBuilder trackingStringBuilder = new StringBuilder("Started tracking changes in: ");
        if (advertisingIDCheckBox.isChecked()) {
//            Log.d(TAG, "AdvertisingID should be checked");
            advertisingIDFlag = true;
            trackingStringBuilder.append("Advertising ID, ");
        }
        if (antivirusCheckBox.isChecked()) {
            antivirusFlag = true;
            trackingStringBuilder.append("Antivirus, ");
        }
        if (adblockerCheckBox.isChecked()) {
            adblockerFlag = true;
            trackingStringBuilder.append("Adblocker, ");
        }
        if (VPNCheckBox.isChecked()) {
            VPNFlag = true;
            trackingStringBuilder.append("VPN, ");
        }
        if (wifiCheckBox.isChecked()) {
            wifiChecking(view);
            wifiFlag = true;
            trackingStringBuilder.append("WiFi, ");
        }
        if (sensorCheckBox.isChecked()) {
            if (!geofenceList.isEmpty()) {
                buildGeofences();
                sensorFlag = true;
                trackingStringBuilder.append("Sensor, ");
            }
        }
        if (financeShoppingCheckBox.isChecked()) {
            financeShoppingFlag = true;
            trackingStringBuilder.append("Finance/Shopping, ");
        }
        if (advertisingIDFlag || antivirusFlag || adblockerFlag || VPNFlag || wifiFlag || sensorFlag || financeShoppingFlag) {
            scheduleHandler(view);
        }
        if (bluetoothCheckBox.isChecked()) {
            bluetoothReceiverOn = true;
            bluetoothChecking(view);
            trackingStringBuilder.append("Bluetooth, ");
        }
        if (passwordCheckBox.isChecked()) {
            passwordChecking(view);
            trackingStringBuilder.append("Password, ");
        }

        if (appInstallCheckBox.isChecked()) {
            appInstallReceiverOn = true;
            appInstallChecking(view);
            trackingStringBuilder.append("App install, ");
        }

        trackingStringBuilder.setLength(trackingStringBuilder.length() - 2);
        String trackingMessage = trackingStringBuilder.toString();
        Toast myToast = Toast.makeText(this, trackingMessage, Toast.LENGTH_SHORT);
        myToast.show();

        if (sensorCheckBox.isChecked() && geofenceList.isEmpty()) {
//            myToast.cancel();
//            Toast newToast = Toast.makeText(this, "Sensor could not be tracked - please set up a trusted place", Toast.LENGTH_SHORT);
//            newToast.show();
//        }
//            myToast.setText("Sensor could not be tracked - please set up a trusted place");
//            myToast.show();
            Handler toastHandler = new Handler();
            toastHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Sensor could not be tracked - please set up a trusted place", Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }
    }

    public void stopAllPollingServices(View view) {
        if (!pollingCurrently) {
            Toast.makeText(this, "No service to stop - tracking was never started", Toast.LENGTH_SHORT).show();
            return;
        }
        if (advertisingIDFlag || antivirusFlag || adblockerFlag || VPNFlag) {
            handler.removeCallbacks(runnable);
        }
        if (bluetoothReceiverOn) {
            bluetoothReceiverOn = false;
            unregisterReceiver(bluetoothBroadcastReceiver);
        }
        if (wifiReceiverOn) {
            wifiReceiverOn = false;
            unregisterReceiver(wifiBroadcastReceiver);
        }
        if (screenLockReceiverOn) {
            screenLockReceiverOn = false;
            unregisterReceiver(screenLockReceiver);
        }

        if (appInstallReceiverOn) {
            appInstallReceiverOn = false;
            unregisterReceiver(appInstallReceiver);
        }

        pollingCurrently = false;
        someCheckBoxChecked = false;
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();

        /* STOP PASSWORD BACKGROUND SERVICE */
        FileUtility.writeToTrackingFile(TrackingActivity.TRACKING_START_STOP, "Tracking stopped", getApplicationContext());
    }


    public void geofenceTestButton(View view) {
        if (geofenceList.isEmpty()) {
            Toast.makeText(this, "Please set up a trusted place to test geofences", Toast.LENGTH_SHORT).show();
        } else {
            buildGeofences();
            Toast.makeText(this, "Geofences set up successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToGoogleMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public static void addTrustedLocation(Location trustedLocation) {
        trustedLocations.add(trustedLocation);
        Log.d(TAG, "addTrustedLocation: successfully added " + trustedLocation.getLatitude() + ", " + trustedLocation.getLongitude());
    }

    public static boolean containsTrustedLocation(Location trustedLocation) {
        return trustedLocations.contains(trustedLocation);
    }

//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

//    public void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(this)
//                        .setTitle("Allow this app to access your current location?")
//                        .setMessage("Click OK if you are happy for this app to access your current location.")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(MainActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();
//
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//            }
////            return false;
//        } else {
////            return true;
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        // If request is cancelled, the result arrays are empty.
//        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                // permission was granted, yay! Do the
//                // location-related task you need to do.
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED) {
//
//
//                }
//
//            } else {
//
//                // permission denied, boo! Disable the
//                // functionality that depends on this permission.
//                Log.d(TAG, "Location access - PERMISSION DENIED!");
//            }
//        }
//    }


//    /* WORK IN PROGRESS */
//    public void addCurrLocationAsGeofence(View view) {
//        /* Get string input (name of trusted place) from user */
//        /* IMPORTANT! NOT COMPLETE */
//        String name = "";
//        checkLocationPermission();
//        locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    currLatitude = location.getLatitude();
//                    currLongitude = location.getLongitude();
//                }
//            }
//        });
//        if (currLatitude != 0 && currLongitude != 0) {
//            addGeofence(name, currLatitude, currLongitude);
//        }
//    }

//    public void readFromShoppingCSV(View view) {
//        List<List<String>> records = new ArrayList<>();
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(getAssets().open("shopping_top_100_review_count.csv")));
//
//            // do reading, usually loop until end of file reading
//            String line;
//            while ((line = reader.readLine()) != null) {
//                //process line
//                String[] values = line.split(COMMA_DELIMITER);
//                records.add(Arrays.asList(values));
//                Log.d(TAG, Arrays.toString(values));
//            }
////            List<String> questionableStr = records.get(11);
////            Log.d(TAG, "readFromShoppingCSV: " + questionableStr.toString());
////            if (questionableStr == null) {
////                Log.d(TAG, "readFromShoppingCSV: NULL");
////            } else {
////                Log.d(TAG, "readFromShoppingCSV: " + questionableStr);
////            }
//        } catch (IOException e) {
//            Log.e(TAG, "readFromShoppingCSV: ", e);
//            //log the exception
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    //log the exception
//                    Log.e(TAG, "readFromShoppingCSV: ", e);
//                }
//            }
//        }
//    }


    public void readShoppingUsingOpenCSV(View view) {
        FileUtility.readFromCSV(SHOPPING_CSV_PATH, this);
    }

    /* Assign to button and test */
    public void readFinanceUsingOpenCSV(View view) {
        FileUtility.readFromCSV(FINANCE_CSV_PATH, this);
    }

    public static boolean isAnyTCPConnections() {
        return anyTCPConnections;
    }

    public void wifiHandlerActivity() {
        anyTCPConnections = checkForActiveConnections(TCP_FILE_NAME) || checkForActiveConnections(TCP6_FILE_NAME);
    }

    public boolean checkForActiveConnections(String fileName) {
//        String tcp6Connections = FileUtility.readFromProc("/proc/net/tcp6");
        List<String> lines = FileUtility.readFromProc(fileName);
        List<TCP> connections = new ArrayList<>();

        for (String line : lines) {
            Log.d(TAG, line);
            TCP tcp = new TCP(line);
            String firstPart = tcp.getFirstPartOfLocalAddress();
//            int intFirstPart = Integer.parseInt(firstPart);
//            if (intFirstPart != 0) {
//                return true;
//            }
//            Log.d(TAG, firstPart);
            try {
                Integer.parseInt(firstPart);
//                if (intFirstPart == 0) {
//                Log.d(TAG, "Not a TCP6 connection");
//                }
//                Log.d(TAG, Integer.toString(intFirstPart));
            } catch (NumberFormatException e) {
                /* if there is an exception, the connection has alphabets - some internet connection */
                connections.add(tcp);
//                Log.d(TAG, "Valid TCP connection");
            }
        }
        return !connections.isEmpty();
//        return false;
//        Log.d(TAG, "checkForActiveConnections: \n" + tcp6Connections);
//        Log.d(TAG, "checkForActiveConnections: \n" + lines.toString());
//        FileUtility.lineTokenizer(TCP6exampleLine);
    }

    public static void addGeofence(String areaID, double latitude, double longitude) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(areaID)
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_IN_METRES)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(5000)
                .build());
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
//        updateLocationUI();
    }

    public void buildGeofences() {
//        if (client.isConnected()) {
//        try {

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
//            getLocationPermission();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        } else {
            client.addGeofences(request, pendingIntent)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String message = "Geofence(s) successfully added";
                            Log.d(TAG_GEOFENCE, message);
//                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String message = "Error: failed to add geofence(s)";
                            Log.d(TAG_GEOFENCE, message);
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    });
        }


//                LocationServices.GeofencingApi.addGeofences(
//                        client,
//                        geofencingRequest,
//                        pendingIntent
//                ).setResultCallback(this);
//        } catch (SecurityException e) {
//            Log.e(TAG, "buildGeofences: ", e);
//        }
//        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG_GEOFENCE, "Connected now");
    }

    @Override
    public void onConnectionSuspended(int i) {
//        client.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG_GEOFENCE, "Connection Failed");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }


    //    @Override
//    protected void onStart() {
//        super.onStart();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(broadcastReceiver, filter);
//    }
//


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(exampleBroadcastReceiver);
//        unregisterReceiver(bluetoothBroadcastReceiver);
//        unregisterReceiver(wifiBroadcastReceiver);
////        unregisterReceiver(passwordBroadcastReceiver);
////        devicePolicyManager.removeActiveAdmin(passwordComponentName);
//
//
//    }

    enum TrackingActivity {
        ADVERTISING_ID,
        BLUETOOTH,
        PASSWORD,
        PHONE_COVERING,
        ADBLOCKER,
        ANTIVIRUS,
        VPN,
        WIFI,
        FINANCE_SHOPPING,
        DEVELOPER_NAME,
        STORE_NAME,
        SMS,
        SUSPICIOUS_COMMUNICATIONS_DELETE,
        POP_UPS,
        TRACKING_START_STOP,
        GEOFENCE_TEST,
        ERROR
    }


    public static void setTrustedPlaceBoolean(boolean inPlace) {
        inTrustedPlace = inPlace;
    }

    public void setPollId(String newPollId) {
        pollId = newPollId;
    }

    public void appInstallChecking(View view) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        registerReceiver(appInstallReceiver, filter);
    }

    public void screenUnlockedChecking(View view) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenLockReceiver, filter);
    }

    public void bluetoothChecking(View view) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothBroadcastReceiver, filter);
    }

    public void wifiChecking(View view) {
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, filter);
    }

    public void passwordChecking(View view) {
//        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
//        assert km != null;
//        if (km.isDeviceSecure()) {
//            Toast.makeText(this, "Password set", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Password NOT set", Toast.LENGTH_SHORT).show();
//        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, passwordComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission.");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_ENABLE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Problem enabling Admin Device features", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void VPNHandlerActivity() {
        /* Check if Wifi is connected and if network is captive */
//        if (isWifiConnected()) {
        if (isCaptivePortal() || isWifiNotPasswordProtected()) {
            if (isVPNOn()) {
                Log.d(TAG, "VPNHandlerActivity");
                if (!VPNOn) {
                    FileUtility.writeToTrackingFile(TrackingActivity.VPN, "Started using VPN while connected to public network (captive portal)", getApplicationContext());
                    VPNOn = true;
                }
            } else {
                if (VPNOn) {
                    FileUtility.writeToTrackingFile(TrackingActivity.VPN, "Stopped using VPN while connected to public network (captive portal)", getApplicationContext());
                    VPNOn = false;
                }
            }
        }
//        }
    }

    /* UNTESTED */
    public boolean isWifiNotPasswordProtected() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
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
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public boolean isWifiConnected() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return wifiInfo.getNetworkId() != -1;
            }
        }
        return false;
    }

    public void displayGeofencesButton(View view) {
        if (geofenceList.isEmpty()) {
            Toast.makeText(this, "No geofences set up", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "displayGeofencesButton: No trusted location geofences set up");
        } else {
            Toast.makeText(this, "Geofences: " + geofenceList.toString(), Toast.LENGTH_LONG).show();
//            Log.d(TAG, "displayGeofencesButton: " + trustedLocations.toString());
        }
    }

    public void displayTrustedLocationsButton(View view) {
        if (trustedLocations.isEmpty()) {
            Toast.makeText(this, "No trusted locations set up", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "displayGeofencesButton: No trusted location geofences set up");
        } else {
            Toast.makeText(this, "Trusted locations: " + geofenceList.toString(), Toast.LENGTH_LONG).show();
//            Log.d(TAG, "displayGeofencesButton: " + trustedLocations.toString());
        }
    }

    /* TESTED SENSOR ONLY - STILL NEED TO TEST GEOFENCES */
    public void sensorHandlerActivity() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                /* Something is close to the sensor - phone being covered */
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    Log.d(TAG, "onSensorChanged: HAND CLOSE");
                    if (!isScreenLocked()) {
                        if (!inTrustedPlace) {
                            FileUtility.writeToTrackingFile(TrackingActivity.PHONE_COVERING, "Hand covering phone while outside a trusted place", getApplicationContext());
                        }
                    }
//                    Log.d(TAG, "onSensorChanged: CLOSE");
//                } else {
//                    Log.d(TAG, "onSensorChanged: FAR");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorEventListener, proximitySensor, 2 * 1000 * 100);
    }

    public void testActivity(View view) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                antivirusHandlerActivity();
                handler.postDelayed(runnable, 5000);
            }
        };
        runnable.run();
    }

    public void advertisingIDHandlerActivity() {
        AdvertisingIDRunnable advertisingIDRunnable = new AdvertisingIDRunnable(new Handler(), this);
        Thread thread = new Thread(advertisingIDRunnable);
        thread.start();
        String message;
        if (pollId != null) {
            if (!id.equals(pollId)) {
                if (!id.equals("")) {
                    message = "AdvertisingID changed from " + id + " to " + pollId;
                    Log.d(TAG, "advertisingIDHandlerActivity: ADVERTISING ID CHANGED");
                } else {
                    message = "Original AdvertisingID: " + pollId;
                }
                FileUtility.writeToTrackingFile(TrackingActivity.ADVERTISING_ID, message, getApplicationContext());
                id = pollId;
            }
        }
//        Log.d(TAG, "Advertising ID changed from: " + id + ", to: " + pollId);
//        Log.d(TAG, "Advertising ID: " + id);
    }

    public String getNameFromPackageName(String packageName) {
        String appName = "";
        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }


    public void adblockerHandlerActivity() {
        Log.d(TAG, "adblockerHandlerActivity");
//        adBlockerName = isAppRunning(new AdBlockerWhitelist().getSet());
//        if (!adBlockerName.equals("")) {
//            String message = adBlockerName + " is running";
//            FileUtility.writeToTrackingFile(TrackingActivity.ADBLOCKER, message, getApplicationContext());
//            Log.d(TAG, message);
//        }
//        else {
//            message = "Adblocker is not running";
//        }
        for (String adblockerPackageName : new AdBlockerWhitelist().getSet()) {
            if (isPackageInstalled(adblockerPackageName)) {
                String appName = getNameFromPackageName(adblockerPackageName);
                if (!appName.equals("")) {
                    String message = appName + " is installed";
                    FileUtility.writeToTrackingFile(TrackingActivity.ADBLOCKER, message, getApplicationContext());
                    Log.d(TAG, "adblockerHandlerActivity: " + appName + " installed");
                }
            }
        }

    }

    public void antivirusHandlerActivity() {
//        antivirusName = isAppRunning(new AntivirusWhitelist().getSet());
//        if (!antivirusName.equals("")) {
//            String message = antivirusName + " is running";
//            FileUtility.writeToTrackingFile(TrackingActivity.ANTIVIRUS, message, getApplicationContext());
//            Log.d(TAG, message);
//        }
//        else {
//            message = "Antivirus is not running";
//        }
        for (String antivirusAppName : new AntivirusWhitelist().getSet()) {
            if (isPackageInstalled(antivirusAppName)) {
                String appName = getNameFromPackageName(antivirusAppName);
                if (!appName.equals("")) {
                    String message = appName + " is installed";
                    FileUtility.writeToTrackingFile(TrackingActivity.ANTIVIRUS, message, getApplicationContext());
                    Log.d(TAG, "antivirusHandlerActivity: " + appName + " installed");
                }
            }
        }
    }

    public void financeShoppingHandlerActivity() {
        financeShoppingName = isAppRunning(new FinanceShoppingWhitelist(this).getSet());
        if (!financeShoppingName.equals("")) {
            String message = financeShoppingName + " is running";
            FileUtility.writeToTrackingFile(TrackingActivity.FINANCE_SHOPPING, message, getApplicationContext());
            Log.d(TAG, message);
        }
    }

    public void scheduleHandler(View view) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (adblockerFlag) {
                    adblockerHandlerActivity();
                }
                if (antivirusFlag) {
                    antivirusHandlerActivity();
                }
                if (advertisingIDFlag) {
                    advertisingIDHandlerActivity();
                }
                if (VPNFlag) {
                    VPNHandlerActivity();
                }
                if (wifiFlag) {
                    wifiHandlerActivity();
                }
                if (sensorFlag) {
                    sensorHandlerActivity();
                }
                if (financeShoppingFlag) {
                    financeShoppingHandlerActivity();
                }
                /* Repeat every 1 minute */
                handler.postDelayed(runnable, ONE_MINUTE_IN_MILLISECONDS);
            }
        };
        runnable.run();
    }

//    public void scheduleAdblocker(View view) {
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                adblockerHandlerActivity();
//                /* Repeat every 5 seconds */
//                handler.postDelayed(runnable, 5000);
//            }
//        };
//        runnable.run();
//    }

//    public void scheduleAntivirus(View view) {
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                antivirusHandlerActivity();
//                /* Repeat every 5 seconds */
//                handler.postDelayed(runnable, 5000);
//            }
//        };
//        runnable.run();
//    }

//    public void scheduleAdvertisingID(View view) {
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                advertisingIDHandlerActivity();
//                handler.postDelayed(runnable, 5000);
//            }
//        };
//        runnable.run();
//    }


//    /* From Stack Overflow */
//    /* CONSIDER USING MERLIN: https://github.com/novoda/merlin */
//    /* Test using real Android phone */
//    public void getNetworkConnectionsInfo(View view) {
//        Process process = null;
//        try {
//            /* Try netstat -at for all active tcp connections ? */
//            process = Runtime.getRuntime().exec("netstat -n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (process != null) {
//                process.getOutputStream().close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedReader reader = null;
//        if (process != null) {
//            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        }
//
//        String line = "";
//        while (true) {
//            try {
//                if (reader != null && (line = reader.readLine()) == null) {
//                    break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            // Parse line for required info
//            Log.d("NetworkConnectionInfo", line);
//        }
//
//        try {
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public boolean isScreenLocked() {
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM != null) {
            return myKM.inKeyguardRestrictedInputMode();
        }
        return false;
    }

    public String isAppRunning(Set<String> whitelist) {
        final ActivityManager activityManager =
                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos =
                activityManager != null ? activityManager.getRunningAppProcesses() : null;
        if (procInfos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                Log.d(TAG, "isAppRunning: " + processInfo.processName);
                if (whitelist.contains(processInfo.processName)) {
                    if (isAppInForeground(processInfo)) {
                        Log.d(TAG, "App is in FOREGROUND");
                    }
                    return processInfo.processName;
                }
            }
        }
        return "";
    }


    public boolean isAppInForeground(ActivityManager.RunningAppProcessInfo processInfo) {
        return (processInfo.uid == getApplicationInfo().uid && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
    }

    public boolean isThereAnyForegroundActivity() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public void readFromFileButton(View view) {
        trackingFileInfo = FileUtility.readFromInternalFile(getApplicationContext(), trackingFileName);
//        Log.d(TAG, "readFromFileButton: " + trackingFileInfo);
        Intent intent = new Intent(this, ReadFileActivity.class);
        startActivity(intent);
    }


//    public void isAdblockerInstalled(View view) {
//        boolean found = isAppInstalled(new AdBlockerWhitelist());
//        if (found) {
//            Toast.makeText(getApplicationContext(), "Adblocker (" + adBlockerName + ") found", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "Adblocker NOT found", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void isAntivirusInstalled(View view) {
//        boolean found = isAppInstalled(new AntivirusWhitelist());
//        if (found) {
//            Toast.makeText(getApplicationContext(), "Antivirus (" + antivirusName + ") found", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "Antivirus NOT found", Toast.LENGTH_SHORT).show();
//        }
//    }

    public boolean isAppInstalled(Whitelist whitelist) {
        boolean found = false;
        PackageManager pm = getPackageManager();
        ApplicationInfo applicationInfo = null;
        for (String string : whitelist.getSet()) {
            if (isPackageInstalled(string)) {
                found = true;
                try {
                    applicationInfo = pm.getApplicationInfo(string, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String name = (String) ((applicationInfo != null) ? pm.getApplicationLabel(applicationInfo) : "???");
                if (whitelist instanceof AdBlockerWhitelist) {
                    adBlockerName = name;
                } else if (whitelist instanceof AntivirusWhitelist) {
                    antivirusName = name;
                }
                break;
            }
        }
        return found;
    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager packageManager = getPackageManager();
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public boolean isVPNOn() {
        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception e) {
            Log.d(TAG, "isVpnUsing Network List not received");
        }

//        return networkList.contains("tun0");
        return startsWith(networkList);
    }

    public boolean startsWith(List<String> networkList) {
        for (String network : networkList) {
            if (network.length() >= 3) {
                if (network.substring(0, 3).equals("tun")) {
                    return true;
                }
            }
        }
        return false;
    }


    /* TESTED */
    public boolean isCaptivePortal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL));
        }
        return false;
    }

    /* From official AOSP (Android Open Source Project) - courtesy of Stack Overflow */
//    private static final String mWalledGardenUrl = "http://clients3.google.com/generate_204";
//    private static final int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10000;
//
//    public void isWalledGardenConnection(View view) {
//        HttpURLConnection urlConnection = null;
//        boolean isWalledGarden = false;
//        try {
//            URL url = new URL(mWalledGardenUrl); // "http://clients3.google.com/generate_204"
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setInstanceFollowRedirects(false);
//            urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
//            urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
//            urlConnection.setUseCaches(false);
//            urlConnection.getInputStream();
//            // We got a valid response, but not from the real google
//            isWalledGarden = urlConnection.getResponseCode() != 204;
//        } catch (IOException e) {
//            Log.e(TAG, "isWalledGardenConnection: ", e);
////            if (DBG) {
////            log("Walled garden check - probably not a portal: exception "
////                    + e);
////            }
////            isWalledGarden = false;
//        }
////        finally {
//        if (urlConnection != null) {
//            urlConnection.disconnect();
//        }
////        }
//        if (isWalledGarden) {
//            Toast.makeText(this, "Captive portal detected", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Captive portal NOT detected", Toast.LENGTH_SHORT).show();
//        }
//    }


    /* Stack Overflow */
    private boolean isPhoneIsLockedOrNot(Context context) {
        boolean isPhoneLock = false;
        if (context != null) {
            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (myKM != null && myKM.isKeyguardLocked()) {
                isPhoneLock = true;
            }
        }
        return isPhoneLock;
    }

    public void clearFile(View view) {
        FileUtility.clearFile(getApplicationContext());
        Toast.makeText(this, "Tracking file cleared", Toast.LENGTH_SHORT).show();
    }

}

