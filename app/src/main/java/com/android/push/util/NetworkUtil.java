/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： NetworkUtil.java
 * 内容摘要： 网络的Util类，封装检测网络是否连接、获取当前网络类型等接口
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类描述 ：网络的Util类，封装检测网络是否连接、获取当前网络类型等接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class NetworkUtil {

    enum FileType{
        Apk,Picture
    }

    /**
     * 检测网络是否连接
     *
     * @return true：连接，false：未连接
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WiFi网络    2：手机网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return netType;
        }
        if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            netType = 2;
        }

        return netType;
    }

}
