/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PollingAlarmUtil.java
 * 内容摘要： 轮询Alarm的Util类，启动Alarm来定时启动轮询服务
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-24
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.util;

import com.android.push.service.PollingService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

import static com.android.push.util.SharedPreferenceUtil.*;
import static com.android.push.config.Config.*;

/**
 * 类描述 ：轮询Alarm的Util类，启动Alarm来定时启动轮询服务
 *
 * @author 李翊星
 * @version 1.0
 */
public class PollingAlarmUtil {

    private static final String TAG = "111.PollingAlarmUtil";

    public static void startBootedPollingAlarm(Context context) {
        Log.d(TAG, "Start FirstBoot Polling alarm");

        int netType = NetworkUtil.getNetworkType(context);

        /* to create random */
        long random = createRandom();

        if (netType == 1) {
            if (getIntervalChangeFlag(context) && getCustomIntervalWifi(context) != 0L) {
                setAlarmIntervalTime(context, getCustomIntervalWifi(context) + random);
            } else {
                setAlarmIntervalTime(context, getDefaultIntervalTimeWifi(context) + random);
            }
        } else {
            if (getIntervalChangeFlag(context) && getCustomIntervalMobile(context) != 0L) {
                setAlarmIntervalTime(context, getCustomIntervalMobile(context) + random);
            } else {
                setAlarmIntervalTime(context, getDefaultIntervalTimeMobile(context) + random);
            }
        }
    }

    public static void startPollingAlarmNow(Context context) {
        Log.d(TAG, "Start pollingAlarm now");
        setAlarmIntervalTime(context, 0L);
    }

    public static void startPollingAlarm(Context context) {
        Log.d(TAG, "Start pollingAlarm");

        int netType = NetworkUtil.getNetworkType(context);

        if (netType == 1) {
            if (getIntervalChangeFlag(context) && getCustomIntervalWifi(context) != 0L) {
                setAlarmIntervalTime(context, getCustomIntervalWifi(context));
            } else {
                setAlarmIntervalTime(context, getDefaultIntervalTimeWifi(context));
            }
        } else {
            if (getIntervalChangeFlag(context) && getCustomIntervalMobile(context) != 0L) {
                setAlarmIntervalTime(context, getCustomIntervalMobile(context));
            } else {
                setAlarmIntervalTime(context, getDefaultIntervalTimeMobile(context));
            }
        }
    }

    /**
     * 关闭轮询服务
     */
    public static void stopPolling(Context context) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PollingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.cancel(pendingIntent);
    }

    /**
     * 设置轮询周期
     */
    private static void setAlarmIntervalTime(Context context, long intervalTime) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PollingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                + intervalTime, pendingIntent);
    }

    /**
     * 创建随机数
     */
    private static long createRandom() {
        Random random = new Random();
        return (long) random.nextInt(60 * 60 * 100);
    }
}
