/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： FeedbackUtil.java
 * 内容摘要： 反馈信息Util类，提供向服务器反馈信息的接口
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

import com.android.push.bean.FeedbackBean;
import com.android.push.db.PushDataBase;

import org.json.JSONArray;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import static com.android.push.config.Config.*;

/**
 * 类描述 ：反馈信息Util类，提供向服务器反馈信息的接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class FeedbackUtil {

    private static final String TAG = "push.FeedbackUtil";

    public static void feedbackToServer(final Context context, final FeedbackBean feedback) {

        new Thread(){
            @Override
            public void run() {
                feedback(context, feedback);
            }
        }.run();
    }

    private static void feedback(Context context, FeedbackBean feedback) {
        PushDataBase pushDataBase = PushDataBase.getInstance(context);

        pushDataBase.open();

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService
                (Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        JSONArray feedbacks = new JSONArray();
        // 若反馈表不为空，则读取数据
        if (!pushDataBase.isFeedbackTableEmpty()) {
            feedbacks = pushDataBase.queryFeedbackTable();
        }
        feedbacks.put(JsonUtil.getFeedbackJson(feedback));

        String feedbackStr = JsonUtil.getFeedbackJsonStr(imei, feedbacks);

        if (LOG_DEBUG) {
            Log.d(TAG, "feedback: " + feedbackStr);
        }

        try {
            URL feedbackUrl = new URL(getFeedbackUrl(context));
            long responseCode = HttpUtil.postFeedback(feedbackUrl, feedbackStr);
            if (responseCode != 200) {
                pushDataBase.addFeedback(feedback);
            } else {
                pushDataBase.clearFeedbackTable();
            }

            if (LOG_DEBUG) {
                Log.d(TAG, "responseCode: " + responseCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        pushDataBase.close();
    }
}
