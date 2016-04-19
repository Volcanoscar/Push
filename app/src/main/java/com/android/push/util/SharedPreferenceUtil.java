/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： SharedPreferenceUtil.java
 * 内容摘要： 创建Push专属的SharedPreference，用以存储各类状态值
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

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 类描述 ：创建Push专属的SharedPreference，用以存储各类状态值
 *
 * @author 李翊星
 * @version 1.0
 */
public class SharedPreferenceUtil {

    private static final String SHARED_PREFERENCE_NAME = "polling_info";

    /* 第一次开机标志 */
    private static final String FIRST_BOOT_FLAG = "first_boot_flag";

    /* 上一个没执行Alarm的标志 */
    private static final String ALARM_NOT_POLLING_FLAG = "alarm_not_polling_flag";

    /* 上次轮询的时间 */
    private static final String LAST_POLLING_TIME = "last_polling_time";

    /* 上次轮询的时间间隔 */
    private static final String LAST_POLLING_ALARM_INTERVAL =
            "last_polling_alarm_interval_flag";

    /* 轮询间隔更改标志 */
    private static final String INTERVAL_CHANGE_FLAG =
            "interval_change_flag";

    /* 自定义的WiFi状态下的轮询间隔 */
    private static final String CUSTOM_INTERVAL_WIFI = "custom_interval_wifi";

    /* 自定义的手机网络状态下的轮询间隔 */
    private static final String CUSTOM_INTERVAL_MOBILE = "custom_interval_mobile";

    /* 最近更改轮询间隔的日期 */
    private static final String LAST_INTERVAL_CHANGE_DATE = "last_interval_change_date";

    /* 下载信息 */
    private static final String DOWNLOAD_INFO = "id_%d";

    /**
     * 存入第一次开机标志
     */
    public static void putFirstBootFlag(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FIRST_BOOT_FLAG, value);
        editor.apply();
    }

    /**
     * 获取第一次开机标志
     */
    public static boolean getFirstBootFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(FIRST_BOOT_FLAG, false);
    }

    /**
     * 存入上一个没执行Alarm的标志
     */
    public static void putAlarmFlag(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ALARM_NOT_POLLING_FLAG, value);
        editor.apply();
    }

    /**
     * 获取上一个没执行Alarm的标志
     */
    public static boolean getAlarmFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ALARM_NOT_POLLING_FLAG, false);
    }

    /**
     * 存入上一次轮询时间
     *
     * @param time 时间
     */
    public static void putLastPollingTime(Context context, String time) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LAST_POLLING_TIME, time);
        editor.apply();
    }

    /**
     * 获取上一次轮询时间
     */
    public static String getLastPollingTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(LAST_POLLING_TIME, "1993-06-30 00:00:00");
    }

    /**
     * 存入上一次轮询的时间间隔
     *
     * @param interval 轮询间隔
     */
    public static void putLastPollingInterval(Context context, long interval) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(LAST_POLLING_ALARM_INTERVAL, interval);
        editor.apply();
    }

    /**
     * 获取上一次轮询的时间间隔
     */
    public static long getLastPollingInterval(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(LAST_POLLING_ALARM_INTERVAL, -1);
    }

    /**
     * 存入Alarm周期是否调整的标志
     *
     * @param value true：已调整，false：未调整
     */
    public static void putIntervalChangeFlag(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(INTERVAL_CHANGE_FLAG, value);
        editor.apply();
    }

    /**
     * 获取Alarm周期是否调整的标志
     */
    public static boolean getIntervalChangeFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(INTERVAL_CHANGE_FLAG, false);
    }

    /**
     * 存入自定义Alarm周期的WiFi状态周期
     *
     * @param interval 轮询间隔
     */
    public static void putCustomIntervalWifi(Context context, long interval) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(CUSTOM_INTERVAL_WIFI, interval);
        editor.apply();
    }

    /**
     * 获取自定义Alarm周期的WiFi状态周期
     */
    public static long getCustomIntervalWifi(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(CUSTOM_INTERVAL_WIFI, 0L);
    }

    /**
     * 存入自定义Alarm周期的手机网络状态周期
     *
     * @param interval 轮询间隔
     */
    public static void putCustomIntervalMobile(Context context, long interval) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(CUSTOM_INTERVAL_MOBILE, interval);
        editor.apply();
    }

    /**
     * 获取自定义Alarm周期的手机网络状态周期
     */
    public static long getCustomIntervalMobile(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(CUSTOM_INTERVAL_MOBILE, 0L);
    }

    /**
     * 存入Alarm周期的修改日期
     *
     * @param date 日期
     */
    public static void putLastIntervalChangeDate(Context context, String date) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LAST_INTERVAL_CHANGE_DATE, date);
        editor.apply();
    }

    /**
     * 获取Alarm周期的修改日期
     */
    public static String getLastIntervalChangeDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(LAST_INTERVAL_CHANGE_DATE, "");
    }

    /**
     * 存入下载任务的信息
     *
     * @param id   下载任务ID
     * @param info 下载任务信息
     */
    public static void putDownloadInfo(Context context, long id, String info) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(String.format(DOWNLOAD_INFO, id), info);
        editor.apply();
    }

    /**
     * 获取下载任务ID对应的下载信息
     *
     * @param id 下载任务ID
     */
    public static String getDownloadInfo(Context context, long id) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(String.format(DOWNLOAD_INFO, id), "");
    }

}
