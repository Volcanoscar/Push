/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： NotificationReceiver.java
 * 内容摘要： 通知的广播接收器，主要接收Push通知的点击、清除事件
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

import com.android.push.activity.FullScreenActivity;
import com.android.push.activity.ImageDialogActivity;
import com.android.push.activity.TextDialogActivity;
import com.android.push.constant.PushConstant;
import com.android.push.bean.FeedbackBean;
import com.android.push.bean.PushBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.db.PushDataBase;
import com.android.push.util.FeedbackUtil;
import com.android.push.util.JsonUtil;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.android.push.config.Config.*;
import static com.android.push.constant.PushConstant.PUSH_PARCEL_KEY;

/**
 * 类描述 ：Notification状态的广播接收器，接收点击、清除等广播
 *
 * @author 李翊星
 * @version 1.0
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "push.NotifyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "notification.click");

        if (intent == null || context == null) {
            return;
        }

        PushBean push = intent.getParcelableExtra(PUSH_PARCEL_KEY);
        if (push == null) {
            return;
        }

        switch (push.show_type) {

            case PushConstant.NOTIFICATION_SHOW:
                if (push.type == PushConstant.WEB_PUSH) {
                    webPush(context, push);
                }

                if (push.type == PushConstant.APP_PUSH && !"".equals(push.button_content)) {
                    Intent textDialog = new Intent(context, TextDialogActivity.class);
                    textDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    textDialog.putExtra(PUSH_PARCEL_KEY, push);
                    context.startActivity(textDialog);
                }

                if (push.type == PushConstant.OTA_PUSH) {
                    try {
                        otaPush(context, push);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            //  弹窗展现
            case PushConstant.DIALOG_SHOW:
                //  图片弹窗
                if (push.type == PushConstant.PICTURE_PUSH && push.picture_urls.length() != 0) {
                    Intent imageDialog = new Intent(context, ImageDialogActivity.class);
                    imageDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    imageDialog.putExtra(PUSH_PARCEL_KEY, push);
                    context.startActivity(imageDialog);
                }
                //  文字弹窗
                else {
                    Intent textDialog = new Intent(context, TextDialogActivity.class);
                    textDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    textDialog.putExtra(PUSH_PARCEL_KEY, push);
                    context.startActivity(textDialog);
                }
                break;

            //  全屏展现
            case PushConstant.FULLSCREEN_SHOW:
                Intent fullScreen = new Intent(context, FullScreenActivity.class);
                fullScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                fullScreen.putExtra(PUSH_PARCEL_KEY, push);
                context.startActivity(fullScreen);
                break;
        }

    }

    /**
     * 网页推送
     *
     * @param context Context
     * @param push    Push信息
     */
    private void webPush(Context context, PushBean push) {

        String url = push.url;
        PushInfoBean pushInfo = push.push_info;

        //  跳转到浏览器，打开指定页面
        if (url.startsWith("http://")) {
            context.startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        //  记录PushInfo
        PushDataBase pushDataBase = PushDataBase.getInstance(context);
        pushDataBase.open();
        pushDataBase.recordPushInfo(pushInfo);
        pushDataBase.close();

        //  反馈操作
        FeedbackBean feedback = new FeedbackBean();
        feedback.push_id = pushInfo.push_id;
        feedback.is_clicked = 1;
        FeedbackUtil.feedbackToServer(context, feedback);

    }

    /**
     * 系统更新推送
     */
    private void otaPush(Context context, PushBean push) {
        PushInfoBean pushInfo = push.push_info;

        //  记录PushInfo
        PushDataBase pushDataBase = PushDataBase.getInstance(context);
        pushDataBase.open();
        pushDataBase.recordPushInfo(pushInfo);
        pushDataBase.close();

        //  反馈操作
        FeedbackBean feedback = new FeedbackBean();
        feedback.push_id = pushInfo.push_id;
        feedback.is_clicked = 1;
        FeedbackUtil.feedbackToServer(context, feedback);

        Intent otaIntent = new Intent(PushConstant.OTA_INTENT_ACTION);
        otaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(otaIntent);
    }

}
