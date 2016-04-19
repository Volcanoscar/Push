package com.android.push.service;

import com.android.push.constant.PushConstant;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.text.SimpleDateFormat;

/**
 * class description:
 *
 * @author liyixing
 * @version 1.0
 */
public class LocationService extends Service {

    private static final String TAG = "push.LocationService";

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private static final long TIME_OUT = 30 * 1000;

    private LocationManager mLocationManager;
    private Location mLocation;
    private Looper mLooper;
    private LocationThread mLocationThread;
    private MyLocationListener mNetworkListner;
    private MyLocationListener mGPSListener;

    public static final int NOTIFY_LOCATION_MSG_CODE = 2;
    private final Messenger mMessenger = new Messenger(new MessengerHandler());
    private Messenger mReplyMessenger;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mMessenger.getBinder();
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case PollingService.GET_LOCATION_MSG_CODE:
                    Log.d(TAG, "receive msg from ServiceA");
                    mReplyMessenger = msg.replyTo;
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendLocationMsg() {
        if (mReplyMessenger == null) {
            return;
        }

        Message replyMsg = Message.obtain();
        replyMsg.what = NOTIFY_LOCATION_MSG_CODE;
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", mLocation);
        replyMsg.setData(bundle);

        try {
            mReplyMessenger.send(replyMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get location
     */
    private void getLocation() {
        mLocation = null;
        mLocationThread = new LocationThread();
        Log.d(TAG, "start time :" + sdf.format(System.currentTimeMillis()));
        mLocationThread.start();

        synchronized (mLocationThread) {
            try {
                Log.i(Thread.currentThread().getName(), "Waiting for LocationThread to complete...");
                // set timeout of thread
                mLocationThread.wait(TIME_OUT);
                Log.i(Thread.currentThread().getName(), "Completed.Now back to main thread");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "end time :" + sdf.format(System.currentTimeMillis()));
        sendLocationMsg();
    }

    private boolean isLocationValid(Location location) {
        long interval = location.getTime() - System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d(TAG, "isLocationValid() --> location time: " + sdf.format(location.getTime()) +
                "\tnow time: " + sdf.format(System.currentTimeMillis()) + "\tinterval = "
                + interval);
        return interval < TIME_OUT;
    }

    /**
     * The thread to get location
     */
    private class LocationThread extends Thread {

        @Override
        public void run() {
            setName("LocationThread");
            Log.i(Thread.currentThread().getName(), "--start--");
            Looper.prepare();
            mLooper = Looper.myLooper();
            registerLocationListener();
            Looper.loop();
            Log.i(Thread.currentThread().getName(), "--end--");
        }
    }

    /**
     * Check the Gps provider is or not enable
     */
    private boolean isGPSProviderEnable() {
        return mLocationManager != null
                && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Check the Network provider is or not enable
     */
    private boolean isNetworkProviderEnable() {
        return mLocationManager != null
                && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Register location listener
     */
    private void registerLocationListener() {
        Log.i(Thread.currentThread().getName(), "registerLocationListener");

        if (isGPSProviderEnable()) {
            mGPSListener = new MyLocationListener();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, mGPSListener);
        }

        if (isNetworkProviderEnable()) {
            mNetworkListner = new MyLocationListener();
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 0, mNetworkListner);
        }
    }

    /**
     * Unregister location listener
     */
    private void unRegisterLocationListener() {
        Log.i(Thread.currentThread().getName(), "unRegisterLocationListener");

        if (mGPSListener != null) {
            mLocationManager.removeUpdates(mGPSListener);
            mGPSListener = null;
        }

        if (mNetworkListner != null) {
            mLocationManager.removeUpdates(mNetworkListner);
            mNetworkListner = null;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(Thread.currentThread().getName(), "Got New Location of provider:" + location.getProvider());
            Log.i(TAG, "Latitude: " + location.getLatitude()
                    + "longitude: " + location.getLongitude());
            try {
                synchronized (mLocationThread) {
                    mLocation = location;
                    mLooper.quit();
                    // notify main thread to continue
                    mLocationThread.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /**
     * Notify the location info to PollingService
     */
    private void notifyLocationInfo() {
        Log.d(TAG, "notify");

        unRegisterLocationListener();

        Intent intent = new Intent();
        intent.setAction(PushConstant.LOCATION_ACTION);
        intent.putExtra("location", mLocation);
        sendBroadcast(intent);

        stopSelf();
    }
}
