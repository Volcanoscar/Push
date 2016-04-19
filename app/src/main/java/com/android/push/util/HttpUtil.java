/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： HttpUtil.java
 * 内容摘要： 网络操作的Util类，提供发送请求、反馈信息的接口
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

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 类描述 ：网络操作的Util类，提供发送请求、反馈信息的接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class HttpUtil {

    private static final String TAG = "push.HttpUtil";

    private static final String PING_URL = "http://www.baidu.com";

    public static boolean checkIsNetAvailable() {

        try {
            URL url = new URL(PING_URL);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("ser-Agent", "Fiddler");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Log.d(TAG, "response code = " + connection.getResponseCode());
                try {
                    if (connection.getResponseCode() == 200) {
                        Log.d(TAG, "connect success");
                        return true;
                    }
                } catch (SocketTimeoutException e) {
                    Log.d(TAG, "failed to connect after timeout");
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向服务器发送请求，获取Push数据
     *
     * @param url     请求地址
     * @param jsonStr 请求数据
     * @return 得到的Push数据
     */
    public static String requestForPush(URL url, String jsonStr) {

        String responseJsonStr = "";
        String requestStr = null;

        try {
            requestStr = "request=" + URLEncoder.encode(jsonStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpURLConnection != null) {
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setDoOutput(true);//设置允许输出
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("ser-Agent", "Fiddler");

                //把上面访问方式改为异步操作,就不会出现 android.os.NetworkOnMainThreadException异常
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
                    out.print(requestStr);
                    out.flush();
                    out.close();
                    if (httpURLConnection.getResponseCode() == 200) {
                        responseJsonStr = URLDecoder.decode(
                                readInStream(httpURLConnection.getInputStream()),
                                "utf-8");
                    }
                } catch (SocketTimeoutException e) {
                    Log.d(TAG, "failed to connect after timeout");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return responseJsonStr;
        }
    }

    /**
     * 将push反馈给服务器
     *
     * @param url     服务器地址
     * @param jsonStr Json数据
     * @return code   服务器返回的请求码，200为成功
     */
    public static int postFeedback(URL url, String jsonStr) {

        int result = -1;

        String infoCollectStr = null;
        try {
            infoCollectStr = "feedback=" + URLEncoder.encode(jsonStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpURLConnection != null) {
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setDoOutput(true);//设置允许输出
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("ser-Agent", "Fiddler");
            }

            //把上面访问方式改为异步操作,就不会出现 android.os.NetworkOnMainThreadException异常
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
            out.print(infoCollectStr);
            out.flush();
            out.close();

            if (httpURLConnection.getResponseCode() == 200) {
                String response = URLDecoder.decode(
                        readInStream(httpURLConnection.getInputStream()),
                        "utf-8");
                result = Integer.valueOf(response.substring(1, response.length() - 1));
                Log.d(TAG, "feedback result: " + result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * 从InputStream中读取String数据
     */
    private static String readInStream(InputStream is) {

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
