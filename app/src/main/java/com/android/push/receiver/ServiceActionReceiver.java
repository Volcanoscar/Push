/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： ServiceActionReceiver.java
 * 内容摘要： 服务控制的广播接收器，主要接收服务的开启/关闭广播
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-10-25
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.receiver;

import com.android.push.service.PollingService;
import com.android.push.service.PushAnalyseService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.android.push.constant.BroadcastAction.*;
import static com.android.push.constant.PushConstant.PUSH_PARCEL_KEY;

/**
 * 类描述 ：服务控制的广播接收器，主要接收服务的开启/关闭广播
 *
 * @author 李翊星
 * @version 1.0
 */
public class ServiceActionReceiver extends BroadcastReceiver {

    private static final String TAG = "push.ActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        switch (action) {
            //  关闭轮询服务
            case ACTION_STOP_POLLING_SERVICE:
                context.stopService(new Intent(context, PollingService.class));
                break;

            //  开启Push数据解析服务
            case ACTION_START_PUSH_ANALYSE_SERVICE:

                Intent startService = new Intent(context, PushAnalyseService.class);
                startService.putExtra(PUSH_PARCEL_KEY, intent.getParcelableExtra(PUSH_PARCEL_KEY));
                context.startService(startService);
                //  关闭轮询服务
                context.sendBroadcast(new Intent(ACTION_STOP_POLLING_SERVICE));
                break;

            //  关闭Push数据解析服务
            case ACTION_STOP_PUSH_ANALYSE_SERVICE:
                context.stopService(new Intent(context, PushAnalyseService.class));
                break;

            default:
                break;
        }
    }
}
