/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PollingService.java
 * 内容摘要： 轮询服务，负责向服务器发送轮询请求并获取Push数据，将Push数据传给PushAnalyseService进行解析
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-10-25
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.service;

import com.android.push.bean.LocationBean;
import com.android.push.bean.RequestBean;
import com.android.push.constant.PushConstant;
import com.android.push.db.PushDataBase;
import com.android.push.util.HttpUtil;
import com.android.push.util.JsonUtil;
import com.android.push.util.MD5Util;
import com.android.push.util.NetworkUtil;
import com.android.push.util.PollingAlarmUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
//import android.os.SystemProperties;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.push.config.Config.*;
import static com.android.push.constant.BroadcastAction.ACTION_START_PUSH_ANALYSE_SERVICE;
import static com.android.push.util.SharedPreferenceUtil.getLastPollingInterval;
import static com.android.push.util.SharedPreferenceUtil.putAlarmFlag;
import static com.android.push.util.SharedPreferenceUtil.putLastPollingInterval;
import static com.android.push.util.SharedPreferenceUtil.putLastPollingTime;

/**
 * 类描述 ：轮询服务，负责向服务器发送轮询请求并获取Push数据，将Push数据传给PushAnalyseService
 *
 * @author 李翊星
 * @version 1.0
 */
public class PollingService extends Service {

    private static final String TAG = "push.PoolingService";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String SOFTWARE_VERSION_KEY = "ro.mediatek.version.release";

    private Context mContext;

    private String pushData;
    private PushDataBase mPushDataBase;

    private Location mLocation;

    //  Messenger：发消息
    private Messenger mMessenger;
    //  Messenger：接收消息
    private final Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());
    //  Msg code
    public static final int GET_LOCATION_MSG_CODE = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mPushDataBase = PushDataBase.getInstance(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushConstant.LOCATION_ACTION);
        registerReceiver(mLocationReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mLocationReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //  网络状态：有网
        if (NetworkUtil.getNetworkType(this) != 0 && HttpUtil.checkIsNetAvailable()) {
            if (LOG_DEBUG) {
                Log.d(TAG, "Start polling");
            }
            startService(new Intent(this, LocationService.class));
        }
        //  网络状态：没网
        else {
            if (LOG_DEBUG) {
                Log.d(TAG, "start polling but no network");
            }
            //  记录Alarm
            putAlarmFlag(this, true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected(..)");
            mMessenger = new Messenger(service);
            Message msg = Message.obtain();
            msg.what = GET_LOCATION_MSG_CODE;
            msg.replyTo = mGetReplyMessenger;
            try {
                mMessenger.send(msg);
                Log.d(TAG, "send msg");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected(..)");
        }
    };

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.NOTIFY_LOCATION_MSG_CODE:
                    Log.d(TAG, "receive msg from ServiceB, msgData: " + msg.getData()
                            .getString("location"));
                    unbindService(mConnection);
                    new PollingThread().run();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mLocation = intent.getParcelableExtra("location");

            if (mLocation != null) {
                Log.d(TAG, "onReceive() location: latitude = " + mLocation.getLatitude() + "\t " +
                        "longitude = " + mLocation.getLongitude());
            }
            new PollingThread().run();
        }
    };

    class PollingThread extends Thread {

        @Override
        public void run() {

            //  打包请求数据
            String request = JsonUtil.getRequestJsonStr(getRequestBean());
            if (LOG_DEBUG) {
                Log.d(TAG, "requestJson: " + request);
            }
            //  发送请求
            try {
                URL url = new URL(getPushRequestUrl(mContext));
                pushData = HttpUtil.requestForPush(url, request);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //  解析push数据
            if (!"".equals(pushData)) {
                pushData = pushData.substring(1, pushData.length() - 1);
                if (LOG_DEBUG) {
                    Log.d(TAG, "pushData: " + pushData);
                }
                try {
                    JSONObject pushJson = new JSONObject(pushData);
                    String md5 = pushJson.getString("md5");
                    JSONObject message = pushJson.getJSONObject("message");

                    if (MD5Util.checkMD5(String.valueOf(message).replace("\\/", "/"), md5)) {
                        Intent intent = new Intent();
                        intent.putExtra(PushConstant.PUSH_PARCEL_KEY,
                                JsonUtil.parsePushJson(String.valueOf(message)));
                        intent.setAction(ACTION_START_PUSH_ANALYSE_SERVICE);
                        mContext.sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "pushData parse error");
                    PollingAlarmUtil.startPollingAlarm(mContext);
                }
            } else {
                PollingAlarmUtil.startPollingAlarm(mContext);
            }
        }
    }

    private void recordPollingInfo() {

        //  记录此次的轮询间隔、轮询时间
        long intervalTime = NetworkUtil.getNetworkType(mContext) == 1 ?
                getDefaultIntervalTimeWifi(mContext)
                : getDefaultIntervalTimeMobile(mContext);
        putLastPollingInterval(mContext, intervalTime);
        putLastPollingTime(mContext, DATE_FORMAT.format(
                new Date(System.currentTimeMillis())));
    }

    /**
     * 获取请求数据
     */
    private RequestBean getRequestBean() {
        RequestBean request = new RequestBean();

        TelephonyManager tm = (TelephonyManager) getSystemService
                (Context.TELEPHONY_SERVICE);
        request.imei = tm.getDeviceId();
        request.model = Build.MODEL;
        request.software_version = Build.DISPLAY;
//        request.software_version = (String) SystemProperties.get(SOFTWARE_VERSION_KEY, "");

        request.location = JsonUtil.getLocationJson(getLocationBean());
        mPushDataBase.open();
        request.push_infos = mPushDataBase.queryPushInfoTable();
        mPushDataBase.close();
        return request;
    }

    /**
     * 获取位置信息
     */
    private LocationBean getLocationBean() {

        LocationBean location = new LocationBean();

        TelephonyManager tm = (TelephonyManager) getSystemService
                (Context.TELEPHONY_SERVICE);
        LocationManager lm = (LocationManager) getSystemService
                (Context.LOCATION_SERVICE);

        //  基站信息
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            String operator = tm.getNetworkOperator();

            if (operator.length() != 0) {
                location.mcc = operator.substring(0, 3);
                location.mnc = operator.substring(3);

                CellLocation cellLocation = tm.getCellLocation();
                if (cellLocation != null) {
                    if (cellLocation instanceof GsmCellLocation) {
                        GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                        location.lac = "" + (gsmCellLocation.getLac());
                        location.cid = "" + gsmCellLocation.getCid();
                    }
                }
            }
        }

        //  gps信息
        if (mLocation != null) {
            location.latitude = mLocation.getLatitude();
            location.longitude = mLocation.getLongitude();
        }

        return location;
    }
}
