/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PollingReceiver.java
 * 内容摘要： 轮询的广播接收器，主要接收开机完成广播和网络状态变化广播，并通过判断相关状态值来开启/设置轮询的Alarm
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-10-25
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.receiver;

import com.android.push.util.NetworkUtil;
import com.android.push.util.PollingAlarmUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.push.util.SharedPreferenceUtil.*;
import static com.android.push.config.Config.*;

/**
 * 类描述 ：轮询的广播接收器，主要接收开机完成广播和网络状态变化广播
 *
 * @author 李翊星
 * @version 1.0
 */
public class PollingReceiver extends BroadcastReceiver {

    private static final String TAG = "push.PollingReceiver";

    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION_NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        switch (action) {

            // 开机完成
            case ACTION_BOOT_COMPLETED:
                Log.d(TAG, "Boot complete");
                //  记录第一次开机标志
                putFirstBootFlag(context, true);
                break;

            // 网络状态变化
            case ACTION_NET_CHANGE:
                int oldNetType = intent.getIntExtra(
                        ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                int nowNetType = NetworkUtil.getNetworkType(context);

                //  网络状态变化，判断是否为有网状态,1-1：WiFi，0-2：手机网络
                if ((oldNetType == 1 && nowNetType == 1)
                        || (oldNetType == 0 && nowNetType == 2)) {

                    //  第一次开机，需要加随机数进行分散轮询时间
                    if (getFirstBootFlag(context)) {

                        putFirstBootFlag(context, false);
                        //设置随机分散的alarm
                        PollingAlarmUtil.startBootedPollingAlarm(context);
                    } else {

                        //  上次Alarm由于没网没有执行轮询操作
                        if (getAlarmFlag(context)) {

                            if (LOG_DEBUG) {
                                Log.d(TAG, "Start last alarm");
                            }

                            PollingAlarmUtil.startPollingAlarmNow(context);
                            putAlarmFlag(context, false);
                        } else {

                            //  获取上一次Alarm设置的间隔时间
                            long lpi = getLastPollingInterval(context);
                            String lastPollingTime = getLastPollingTime(context);
                            String nowTime = sdf.format(new Date(System.currentTimeMillis()));
                            //  比较当前时间与上一次轮询时间，是否大于或等于间隔时间
                            if (mSecBetween(nowTime, lastPollingTime) >= lpi) {
                                PollingAlarmUtil.startPollingAlarmNow(context);
                            }
                        }
                    }
                }
                break;

            default:
                break;
        }
    }


    /**
     * 计算两个DateString之间相差多少毫秒
     */
    private long mSecBetween(String nowTime, String lastTime) {

        try {
            Date now = sdf.parse(nowTime);
            Date last = sdf.parse(lastTime);
            return now.getTime() - last.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
