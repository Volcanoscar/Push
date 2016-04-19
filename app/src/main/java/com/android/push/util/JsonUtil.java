/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： JsonUtil.java
 * 内容摘要： Json的Util类，封装Json相关的操作接口
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

import com.android.push.bean.DownloadInfoBean;
import com.android.push.bean.FeedbackBean;
import com.android.push.bean.LocationBean;
import com.android.push.bean.PushBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.bean.RequestBean;
import com.android.push.constant.JsonKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类描述 ：Json的Util类，封装Json相关的操作接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class JsonUtil {

    private static final String TAG = "push.JsonUtil";

    /**
     * 获取请求信息Bean的Json字符串
     *
     * @param bean 请求信息Bean
     * @return Json字符串
     */
    public static String getRequestJsonStr(RequestBean bean) {

        JSONObject request = new JSONObject();
        try {
            request.put(JsonKey.IMEI, bean.imei);
            request.put(JsonKey.MODEL, bean.model);
            request.put(JsonKey.SOFTWARE_VERSION, bean.software_version);
            request.put(JsonKey.LOCATION, bean.location);
            request.put(JsonKey.PUSH_INFOS, bean.push_infos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject requestWithMD5 = new JSONObject();
        try {
            requestWithMD5.put(JsonKey.REQUEST, request);
            requestWithMD5.put(JsonKey.MD5, MD5Util.getMD5(String.valueOf(request)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return String.valueOf(requestWithMD5);
    }

    /**
     * 根据LocationBean对象获取相应JSONObject对象
     *
     * @param bean 位置信息Bean
     * @return Json字符串
     */
    public static JSONObject getLocationJson(LocationBean bean) {

        JSONObject location = new JSONObject();
        try {
            location.put(JsonKey.MCC, bean.mcc);
            location.put(JsonKey.MNC, bean.mnc);
            location.put(JsonKey.CID, bean.cid);
            location.put(JsonKey.LAC, bean.lac);
            location.put(JsonKey.LATITUDE, bean.latitude);
            location.put(JsonKey.LONGITUDE, bean.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * 根据PushInfoBean对象获取相应JSONObject对象
     *
     * @param bean Push信息Bean
     * @return Json字符串
     */
    public static JSONObject getPushInfoJson(PushInfoBean bean) {

        JSONObject pushInfo = new JSONObject();
        try {
            pushInfo.put(JsonKey.PUSH_ID, bean.push_id);
            pushInfo.put(JsonKey.PUSH_PRIORITY, bean.push_priority);
            pushInfo.put(JsonKey.PUSH_COUNT, bean.push_count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pushInfo;
    }

    /**
     * 获取反馈信息的Json字符串
     *
     * @param imei      手机IMEI号
     * @param feedbacks 需要上传的反馈信息
     * @return Json字符串
     */
    public static String getFeedbackJsonStr(String imei, JSONArray feedbacks) {

        JSONObject feedback = new JSONObject();
        try {
            feedback.put(JsonKey.IMEI, imei);
            feedback.put(JsonKey.PUSH_FEEDBACK, feedbacks);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject feedbackWithMD5 = new JSONObject();
        try {
            feedbackWithMD5.put(JsonKey.FEEDBACK, feedback);
            feedbackWithMD5.put(JsonKey.MD5, MD5Util.getMD5(String.valueOf(feedback)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return String.valueOf(feedbackWithMD5);
    }

    /**
     * 将反馈信息Bean的信息封装成Json
     *
     * @param bean 反馈信息Bean
     * @return Json对象
     */
    public static JSONObject getFeedbackJson(FeedbackBean bean) {

        JSONObject feedback = new JSONObject();
        try {
            feedback.put(JsonKey.PUSH_ID, bean.push_id);
            feedback.put(JsonKey.IS_CLICKED, bean.is_clicked);
            feedback.put(JsonKey.IS_DOWNLOADED, bean.is_downloaded);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return feedback;
    }

    /**
     * 解析Push的Json字符串得到PushBean
     *
     * @param jsonStr Json字符串
     * @return PushBean
     */
    public static PushBean parsePushJson(String jsonStr) {
        PushBean push = new PushBean();

        try {
            JSONObject object = new JSONObject(jsonStr);
            push.type = object.getInt(JsonKey.TYPE);
            push.show_type = object.getInt(JsonKey.SHOW_TYPE);
            push.title = object.getString(JsonKey.TITLE);
            push.content_text = object.getString(JsonKey.CONTENT_TEXT);
            push.url = object.getString(JsonKey.URL);
            push.phone_command = object.getString(JsonKey.PHONE_COMMAND);
            push.polling_interval = object.getLong(JsonKey.POLLING_INTERVAL);
            push.picture_urls = object.getJSONArray(JsonKey.PICTURE_URLS);
            push.install_mode = object.getInt(JsonKey.INSTALL_MODE);
            push.button_content = object.getString(JsonKey.BUTTON_CONTENT);

            PushInfoBean pushInfo = new PushInfoBean();
            pushInfo.push_id = object.getInt(JsonKey.PUSH_ID);
            pushInfo.push_priority = object.getString(JsonKey.PUSH_PRIORITY);
            pushInfo.push_count = object.getInt(JsonKey.PUSH_COUNT);
            push.push_info = pushInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return push;
    }

    /**
     * 解析PushBean得到Json字符串
     *
     * @param bean PushBean
     * @return Json字符串
     */
    public static String parsePushBean(PushBean bean) {
        JSONObject push = new JSONObject();

        try {
            push.put(JsonKey.TYPE, bean.type);
            push.put(JsonKey.SHOW_TYPE, bean.show_type);
            push.put(JsonKey.TITLE, bean.title);
            push.put(JsonKey.CONTENT_TEXT, bean.content_text);
            push.put(JsonKey.URL, bean.url);
            push.put(JsonKey.PHONE_COMMAND, bean.phone_command);
            push.put(JsonKey.POLLING_INTERVAL, bean.polling_interval);
            push.put(JsonKey.PICTURE_URLS, bean.picture_urls);
            push.put(JsonKey.INSTALL_MODE, bean.install_mode);
            push.put(JsonKey.BUTTON_CONTENT, bean.button_content);
            push.put(JsonKey.PUSH_ID, bean.push_info.push_id);
            push.put(JsonKey.PUSH_PRIORITY, bean.push_info.push_priority);
            push.put(JsonKey.PUSH_COUNT, bean.push_info.push_count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return String.valueOf(push);
    }

    /**
     * 解析DownloadInfo的Json字符串得到DownloadInfoBean
     *
     * @param jsonStr Json字符串
     * @return DownloadInfoBean
     */
    public static DownloadInfoBean parseDownloadInfoJson(String jsonStr) {
        DownloadInfoBean downloadInfo = new DownloadInfoBean();

        try {
            JSONObject object = new JSONObject(jsonStr);
            downloadInfo.push_id = object.getInt("push_id");
            downloadInfo.push_priority = object.getString("push_priority");
            downloadInfo.push_count = object.getInt("push_count");
            downloadInfo.file_type = object.getInt("file_type");
            downloadInfo.is_silent_install = object.getBoolean("is_silent_install");
            downloadInfo.install_mode = object.getInt("install_mode");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return downloadInfo;
    }

    /**
     * 解析DownloadInfoBean得到Json字符串
     *
     * @param bean DownloadInfoBean
     * @return Json字符串
     */
    public static String parseDownloadInfoBean(DownloadInfoBean bean) {
        JSONObject downloadInfo = new JSONObject();

        try {
            downloadInfo.put("push_id", bean.push_id);
            downloadInfo.put("push_priority", bean.push_priority);
            downloadInfo.put("push_count", bean.push_count);
            downloadInfo.put("file_type", bean.file_type);
            downloadInfo.put("is_silent_install", bean.is_silent_install);
            downloadInfo.put("install_mode", bean.install_mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return String.valueOf(downloadInfo);
    }
}
