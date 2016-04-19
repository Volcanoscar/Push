package com.android.push;

import com.android.push.db.PushDataBase;
import com.android.push.util.PollingAlarmUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private final String URL_Post = "http://122.224.144.102:8080/publicback/httpurl";

    private static final String TAG = "111.MainActivity";

    private static final String ACTION_ELECTRIC_CARD_REGISTER = "electric.card.register";

    private static final String ACTION_SHOW_DIALOG = "push.show.type.dialog";
    private static final String ACTION_SHOW_NOTIFICATION = "push.show.type.notification";
    private static final String ACTION_SHOW_FULLSCREEN = "push.show.type.fullscreen";

    private static final int NOTIFY_ID = 9008;

    private Context mContext;

    private Button btnStart, btnStop, btnChange, btnSend, btnNotify;

    private Button btnImageDialog, btnTextDialog, btnUrlDialog, btnTextFullscreen, btnUrlFullScreen;

    NotificationManager mNotificationManager;

    Notification.Builder mNotification, mDownloadNotification;

    private PushDataBase mPushDataBase;

    long reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActionBar().hide();

        mContext = this;
        mPushDataBase = PushDataBase.getInstance(this);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnStart click.");
                //showNotification();
                PollingAlarmUtil.startPollingAlarmNow(mContext);
            }
        });

        btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnStop click.");
                PollingAlarmUtil.stopPolling(mContext);
            }
        });

        btnChange = (Button) findViewById(R.id.btn_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendBroadcast(new Intent("com.android.DataService.start"));
//                gpsLocationTest();
                Intent otaIntent = new Intent();
                otaIntent.setAction("com.android.push.intent.System_Update_Entry");
//                    otaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(otaIntent);
                try {
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
//                apnTest(mContext);
            }
        });

        btnSend = (Button) findViewById(R.id.btn_sendBroadcast);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyShow();
            }
        });

        btnNotify = (Button) findViewById(R.id.btn_notify);
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnNotify click.");
//                dialog1();
//                startActivity(new Intent(mContext, TextDialogActivity.class));
//                gotoFullScreen();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void alert() {
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text, null);

        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("测试");
        alert.show();

       /* alert.getWindow().setLayout((int) (width*0.95), height/2);
        alert.setTitle("测试");
        alert.getWindow().setContentView(R.layout.dialog_text);*/
    }

    private void notifyShow() {

        mNotificationManager = (NotificationManager) getSystemService
                (Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(this);

        notification.setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.notify_icon)
                /*.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))*/;

        notification.setContentTitle("title")
                .setContentText("content_text")
                .setTicker("content_text");

        mNotificationManager.notify(9008, notification.build());
    }


    private void apnTest(Context context) {

        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        // 检查当前连接的APN
        Cursor cr = getContentResolver().query(uri, null, null, null, null);

        Log.d(TAG, "cr" + cr);
        while (cr != null && cr.moveToNext()) {

            // if(cr.getString(cr.getColumnIndex("_id")))

            // APN id
            String id = cr.getString(cr.getColumnIndex("_id"));

            Log.d(TAG, "id" + id);

//			String apn_id= cr.getString(cr.getColumnIndex("apn_id"));
//
//			Log.d(TAG, "apn_id" + apn_id);
            // APN name
            String apn = cr.getString(cr.getColumnIndex("apn"));

            Log.d(TAG, apn);
            // Toast.makeText(getApplicationContext(),
            // "当前 id:" + id + " apn:" + apn, Toast.LENGTH_LONG).show();
        }

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "" + location.getLatitude());
            Toast.makeText(mContext, "is null" + location.getLatitude(), Toast.LENGTH_SHORT).show();
            if (location.getLatitude() != 0) {
                LocationManager locationManager = (LocationManager) getSystemService
                        (LOCATION_SERVICE);
                locationManager.removeUpdates(locationListener);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
