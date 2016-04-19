/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： ApkManager.java
 * 内容摘要： Apk管理Util类，提供静默安装Apk的接口
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
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 类描述 ：
 *
 * @author 李翊星
 * @version 1.0
 */
public class ApkManager {

    private Context mContext;

    public ApkManager(Context context) {
        mContext = context;
    }

    /**
     * 静默安装apk
     *
     * @param apkAbsolutePath apk的绝对路径
     * @return 是否安装成功，0：失败，1：成功
     */
    public void installApkSlient(String apkAbsolutePath) {

        new InstallThread(apkAbsolutePath).run();
    }

    class InstallThread extends Thread {

        private String filePath;

        public InstallThread(String _filePath) {
            filePath = _filePath;
        }

        @Override
        public void run() {
            Process process = null;
            InputStream errIs = null;
            InputStream inIs = null;

            try {
                Runtime.getRuntime().exec("chmod 777 " + filePath);

                String[] args = {"pm", "install", "-r", filePath};
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                process = processBuilder.start();
                errIs = process.getErrorStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read = -1;
                while ((read = errIs.read()) != -1) {
                    baos.write(read);
                }

                baos.write("/n".getBytes());
                inIs = process.getInputStream();

                while ((read = inIs.read()) != -1) {
                    baos.write(read);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (errIs != null) {
                        errIs.close();
                    }

                    if (inIs != null) {
                        inIs.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (process != null) {
                    process.destroy();
                }
            }
        }
    }
}
