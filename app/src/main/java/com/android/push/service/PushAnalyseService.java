/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PushAnalyseService.java
 * 内容摘要： Push数据解析服务，完成Push数据的解析、Push信息的记录以及Push的展示
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

import com.android.push.R;
import com.android.push.bean.DownloadInfoBean;
import com.android.push.bean.PushBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.constant.BroadcastAction;
import com.android.push.constant.PushConstant;
import com.android.push.db.PushDataBase;
import com.android.push.util.DownloadTask;
import com.android.push.util.JsonUtil;
import com.android.push.util.PollingAlarmUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.android.push.config.Config.LOG_DEBUG;
import static com.android.push.util.SharedPreferenceUtil.getIntervalChangeFlag;
import static com.android.push.util.SharedPreferenceUtil.getLastIntervalChangeDate;
import static com.android.push.util.SharedPreferenceUtil.putCustomIntervalMobile;
import static com.android.push.util.SharedPreferenceUtil.putCustomIntervalWifi;
import static com.android.push.util.SharedPreferenceUtil.putIntervalChangeFlag;
import static com.android.push.util.SharedPreferenceUtil.putLastIntervalChangeDate;
import static com.android.push.constant.PushConstant.PUSH_PARCEL_KEY;

/**
 * 类描述 ：Push数据解析服务，负责Push数据的解析工作
 *
 * @author 李翊星
 * @version 1.0
 */
public class PushAnalyseService extends Service {

    private static final String TAG = "push.AnalyseService";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat NOTIFY_ID_FORMAT = new SimpleDateFormat("ddHHmmss");

    private Context mContext;

    private NotificationManager mNotificationManager;
    private PushDataBase mPushDataBase;

    private PushBean mPushBean;

    private int mPushState;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mPushBean = intent.getParcelableExtra(PUSH_PARCEL_KEY);
        if (checkPushState() != 0) {
            analysePush(mPushBean);
        }
        //  开始下一个Alarm
//        PollingAlarmUtil.startPollingAlarm(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 对Notification、DownloadManager相关变量进行初始化
     */
    private void init() {

        mPushDataBase = PushDataBase.getInstance(this);

        mNotificationManager = (NotificationManager) getSystemService
                (Context.NOTIFICATION_SERVICE);
        mPushBean = new PushBean();
    }

    /**
     * 检查当前收到的Push数据的状态
     *
     * @return 0：已经展示过或使用过，不再需要解析；1：记录不存在；2：记录存在，但id不一样且比较新
     * 3：记录存在，id一样，但count小于Push指定推送次数
     */
    private int checkPushState() {

        mPushState = 0;

        if (mPushBean == null) {
            return mPushState;
        }

        mPushDataBase.open();
        PushInfoBean newPushInfo = mPushBean.push_info;
        PushInfoBean lastPushInfo = mPushDataBase
                .queryByPushPriority(mPushBean.push_info.push_priority);
        mPushDataBase.close();

        //  不存在priority对应的记录
        if (lastPushInfo == null) {

            if (LOG_DEBUG) {
                Log.d(TAG, "不存在priority对应的记录");
            }
            mPushState = 1;
        }

        //  存在priority对应的记录
        else {

            //  记录存在，id不一样且比较新
            if (newPushInfo.push_id > lastPushInfo.push_id) {
                if (LOG_DEBUG) {
                    Log.d(TAG, "记录存在，id不一样且比较新");
                }
                mPushState = 2;
            }
            //  记录存在，id一样，count小于Push指定推送次数
            else if ((newPushInfo.push_id == lastPushInfo.push_id)
                    && (lastPushInfo.push_count < newPushInfo.push_count)) {

                if (LOG_DEBUG) {
                    Log.d(TAG, "记录存在，id一样，count小于Push指定推送次数");
                }
                mPushState = 3;
            }
        }

        return mPushState;
    }

    /**
     * 解析Push数据
     */
    private void analysePush(PushBean push) {

        if (LOG_DEBUG) {
            Log.d(TAG, "analysePush()");
        }

        recordPushInfo();

        //  解析轮询周期
        if (push.polling_interval != 0) {
            putIntervalChangeFlag(this, true);
            putLastIntervalChangeDate(this, sdf.format(new Date(System.currentTimeMillis())));
            putCustomIntervalMobile(this, push.polling_interval);
            putCustomIntervalWifi(this, push.polling_interval / 2);
        } else {
            String lastChangeDate = getLastIntervalChangeDate(this);
            String nowDate = sdf.format(new Date(System.currentTimeMillis()));
            if (getIntervalChangeFlag(this)
                    && !"".equals(lastChangeDate)
                    && daysBetween(nowDate, lastChangeDate) >= 7) {
                putIntervalChangeFlag(this, false);
            }
        }

        //  图片Push，开始缓存图片
        if (mPushBean.type == PushConstant.PICTURE_PUSH) {
            // picture cache
        }

        //  静默安装
        if (push.show_type == PushConstant.SILENT_INSTALL_SHOW
                && !"".equals(mPushBean.url)) {

            if (LOG_DEBUG) {
                Log.d(TAG, "静默安装");
            }

            DownloadInfoBean downloadInfo = new DownloadInfoBean();
            downloadInfo.push_id = push.push_info.push_id;
            downloadInfo.push_priority = push.push_info.push_priority;
            downloadInfo.push_count = push.push_info.push_count;
            downloadInfo.file_type = PushConstant.APK_FILE_TYPE;
            downloadInfo.is_silent_install = true;
            DownloadTask.startDownload(mContext, mPushBean.url, downloadInfo);
        } else {
            // 展示通知
            showNotification();
        }

    }

    /**
     * 记录当前Push信息
     */
    private void recordPushInfo() {

        mPushDataBase.open();

        PushInfoBean newPushInfo = mPushBean.push_info;
        PushInfoBean lastPushInfo = mPushDataBase
                .queryByPushPriority(mPushBean.push_info.push_priority);

        switch (mPushState) {

            //不存在记录
            case 1:
                // 记录存在，id不一样且比较新
            case 2: {
                PushInfoBean bean = new PushInfoBean();
                bean.push_id = newPushInfo.push_id;
                bean.push_priority = newPushInfo.push_priority;
                bean.push_count = 1;
                mPushDataBase.recordPushInfo(bean);
                break;
            }

            // 记录存在，id一样，count小于Push指定推送次数
            case 3: {
                PushInfoBean bean = new PushInfoBean();
                bean.push_id = newPushInfo.push_id;
                bean.push_priority = newPushInfo.push_priority;
                bean.push_count = lastPushInfo.push_count + 1;
                mPushDataBase.recordPushInfo(bean);
                break;
            }
        }

        mPushDataBase.close();
    }

    /**
     * 计算两个Date之间相隔多少天
     */
    private int daysBetween(String nowDate, String lastDate) {
        Calendar calendar = Calendar.getInstance();
        long nowCal = 0, lastCal = 0;
        try {
            calendar.setTime(sdf.parse(nowDate));
            nowCal = calendar.getTimeInMillis();
            calendar.setTime(sdf.parse(lastDate));
            lastCal = calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long betweenDays = (lastCal - nowCal) / (1000 * 3600 * 24);
        return Math.abs(Integer.parseInt(String.valueOf(betweenDays)));
    }

    /**
     * 以通知的形式展示
     */
    private void showNotification() {

        Notification.Builder notification = new Notification.Builder(this);

        notification.setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.notify_icon);

        notification.setContentTitle(mPushBean.title)
                .setContentText(mPushBean.content_text)
                .setTicker(mPushBean.content_text);

        int notifyID = Integer.valueOf(NOTIFY_ID_FORMAT
                .format(new Date(System.currentTimeMillis())));
        //  设置Notification点击Intent
        notification.setContentIntent(PendingIntent.getBroadcast(this, notifyID,
                new Intent(BroadcastAction.ACTION_NOTIFICATION_CLICK)
                        .putExtra(PUSH_PARCEL_KEY, mPushBean),
                PendingIntent.FLAG_UPDATE_CURRENT));
        //  设置Notification清除Intent
        notification.setDeleteIntent(PendingIntent.getBroadcast(this, notifyID,
                new Intent(BroadcastAction.ACTION_NOTIFICATION_CANCEL),
                PendingIntent.FLAG_UPDATE_CURRENT));

        mNotificationManager.notify(notifyID, notification.build());

        sendBroadcast(new Intent(BroadcastAction.ACTION_STOP_PUSH_ANALYSE_SERVICE));
    }
}
