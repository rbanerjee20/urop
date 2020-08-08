package com.example.urop_application;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class AdvertisingIDRunnable implements Runnable {
    private volatile String id = "";
    private Handler thisHandler;
    private MainActivity mainActivity;

    public AdvertisingIDRunnable(Handler thisHandler, MainActivity mainActivity) {
//        this.context = context;
        this.mainActivity = mainActivity;
        this.thisHandler = thisHandler;
    }

    @Override
    public void run() {
        getIdThread();
        thisHandler.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(mainActivity, id, Toast.LENGTH_LONG).show();
                mainActivity.setPollId(id);
            }
        });
    }

    public void getIdThread() {
        Context applicationContext = mainActivity.getApplicationContext();
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS) {
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

}
//    private Context context;
////    private Context applicationContext;
//    private String id = "";
//    private String strID = "";
//
//
//    public String getAdvertisingID() {
//        return strID;
//    }
//
//    public AdvertisingIDRunnable(Context context) {
//        this.context = context;
////        this.applicationContext = context.getApplicationContext();
//    }
//
//    @Override
//    public void run() {
//        id = getIdThread();
//    }
//
////    public String getIdThread() {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
////                    AdvertisingIdClient.Info adInfo = getAdInfo(context);
////                    if (adInfo != null) {
////                        strID = adInfo.getId();
////                    } else {
////                        strID = "API available, adInfo null";
////                    }
////                } else {
////                    strID = "All unavailable";
////                }
////            }
////        }).start();
////        return strID;
////    }
//
//    public String getIdThread() {
//        String strID;
//        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
//            AdvertisingIdClient.Info adInfo = null;
//            try {
//                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
//            } catch (IOException e) {
//                Log.e("Exception", "IOException found - process not executed");
//            } catch (IllegalStateException e) {
//                Log.e("Exception", "IllegalStateException found - process not executed");
//            } catch (GooglePlayServicesNotAvailableException e) {
//                Log.e("Exception", "GooglePlayServicesNotAvailableException found - process not executed");
//            } catch (GooglePlayServicesRepairableException e) {
//                Log.e("Exception", "GooglePlayServicesRepairableException found - process not executed");
//            }
//            if (adInfo != null) {
//                strID = adInfo.getId();
//                final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
//            } else {
//                strID = "API available, adInfo null";
//            }
//        } else {
//            strID = "All unavailable";
//        }
//
////        return isLAT;
//        return strID;
//    }
//
//
//    public boolean isLATEnabled(Context context) {
//        boolean isLAT = false;
//        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
//            AdvertisingIdClient.Info adInfo = getAdInfo(context);
//            if (adInfo != null) {
//                isLAT = adInfo.isLimitAdTrackingEnabled();
//            }
//        }
//        return isLAT;
//    }
//
//    public AdvertisingIdClient.Info getAdInfo(Context context) {
//        AdvertisingIdClient.Info adInfo = null;
//        try {
//            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
//        } catch (IOException e) {
//            Log.e("Exception", "IOException found - process not executed");
//        } catch (IllegalStateException e) {
//            Log.e("Exception", "IllegalStateException found - process not executed");
//        } catch (GooglePlayServicesNotAvailableException e) {
//            Log.e("Exception", "GooglePlayServicesNotAvailableException found - process not executed");
//        } catch (GooglePlayServicesRepairableException e) {
//            Log.e("Exception", "GooglePlayServicesRepairableException found - process not executed");
//        }
//        return adInfo;
//    }


