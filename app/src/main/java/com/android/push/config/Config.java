/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： Config.java
 * 内容摘要： 配置文件，配置Log开关状态、请求地址、反馈地址等
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-12-16
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.config;

import android.content.Context;
import android.provider.Settings;

/**
 * 类描述 ：配置文件，配置Log开关状态、请求地址、反馈地址等
 *
 * @author 李翊星
 * @version 1.0
 */
public final class Config {

    /* Log开关 */
    public static final boolean LOG_DEBUG = true;

    private static final String SS_DEBUG_MODE_KEY = "sale_support_debug_mode";

    /* 手机网络状态下的默认轮询间隔 Debug&Normal */
    private static final long DEFAULT_INTERVAL_TIME_MOBILE_DEBUG = 2 * 60 * 1000L;
    private static final long DEFAULT_INTERVAL_TIME_MOBILE_NORMAL = 2 * 60 * 60 * 1000L;

    /* WiFi网络状态下的默认轮询间隔 Debug&Normal */
    private static final long DEFAULT_INTERVAL_TIME_WIFI_DEBUG =
            DEFAULT_INTERVAL_TIME_MOBILE_DEBUG / 2;
    private static final long DEFAULT_INTERVAL_TIME_WIFI_NORMAL =
            DEFAULT_INTERVAL_TIME_MOBILE_NORMAL / 2;

    /* Push请求地址 Debug&Normal*/
    private static final String PUSH_REQUEST_URL_DEBUG = "http://svc.konkamobile.com:8090" +
            "/push/message/getMessage.do";
    private static final String PUSH_REQUEST_URL_NORMAL = "http://svc.konkamobile.com:8080" +
            "/push/message/getMessage.do";

    /* 反馈地址 */
    private static final String FEEDBACK_URL_DEBUG = "http://svc.konkamobile.com:8090" +
            "/push/message/backMsg.do";
    private static final String FEEDBACK_URL_NORMAL = "http://svc.konkamobile.com:8080" +
            "/push/message/backMsg.do";

    /**
     * 获取Debug模式开关的值
     */
    public static int getSaleSupportDebugMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                SS_DEBUG_MODE_KEY, 1);
    }

    /**
     * 获取手机网络下默认的轮询时间间隔
     */
    public static long getDefaultIntervalTimeMobile(Context context) {
        int isDebugMode = getSaleSupportDebugMode(context);
        return isDebugMode == 0 ? DEFAULT_INTERVAL_TIME_MOBILE_NORMAL
                : DEFAULT_INTERVAL_TIME_MOBILE_DEBUG;
    }

    /**
     * 获取WiFi网络下默认的轮询时间间隔
     */
    public static long getDefaultIntervalTimeWifi(Context context) {
        int isDebugMode = getSaleSupportDebugMode(context);
        return isDebugMode == 0 ? DEFAULT_INTERVAL_TIME_WIFI_NORMAL
                : DEFAULT_INTERVAL_TIME_WIFI_DEBUG;
    }

    /**
     * 获取Push请求地址
     */
    public static String getPushRequestUrl(Context context) {
        int isDebugMode = getSaleSupportDebugMode(context);
        return isDebugMode == 0 ? PUSH_REQUEST_URL_NORMAL
                : PUSH_REQUEST_URL_DEBUG;
    }

    /**
     * 获取反馈地址
     */
    public static String getFeedbackUrl(Context context) {
        int isDebugMode = getSaleSupportDebugMode(context);
        return isDebugMode == 0 ? FEEDBACK_URL_NORMAL : FEEDBACK_URL_DEBUG;
    }
}
