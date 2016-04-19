/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： DownloadTask.java
 * 内容摘要： 下载任务Util类，提供新建下载任务的接口
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

import com.android.push.constant.PushConstant;
import com.android.push.bean.DownloadInfoBean;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static com.android.push.util.SharedPreferenceUtil.putDownloadInfo;
import static com.android.push.config.Config.*;

/**
 * 类描述 ：下载任务Util类，提供新建下载任务的接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class DownloadTask {

    private static final String TAG = "push.DownloadTask";

    /**
     * 开启下载任务
     *
     * @param url          下载地址
     * @param downloadInfo 下载信息
     */
    public static long startDownload(Context context, String url, DownloadInfoBean downloadInfo) {

        long reference = 0L;
        if (url.startsWith("http://")) {
            Uri uri = Uri.parse(url);
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    url.substring(url.lastIndexOf("/") + 1, url.length()));
            /*request.setDestinationUri(Uri.fromFile(
                    new File(PushConstant.FILE_DOWNLOAD_PATH,
                            url.substring(url.lastIndexOf("/") + 1, url.length()))));*/
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_HIDDEN);// 隐藏通知栏
            request.setVisibleInDownloadsUi(false);// 设置为不可见和不可管理

            if (downloadInfo.file_type == PushConstant.APK_FILE_TYPE) {
                request.setMimeType("application/vnd.android.package-archive");
            }

            //  静默安装
            if (downloadInfo.is_silent_install) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            }

            if (downloadInfo.install_mode == PushConstant.CLICK_INSTALL) {
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 显示通知栏
                request.setVisibleInDownloadsUi(true);// 设置为可见可管理
            } else {
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE);// 显示通知栏
                request.setVisibleInDownloadsUi(true);// 设置为可见可管理
            }

            request.allowScanningByMediaScanner();// 设置为可被媒体扫描器找到
            reference = dm.enqueue(request);
            String info = JsonUtil.parseDownloadInfoBean(downloadInfo);
            putDownloadInfo(context, reference, info);

            if (LOG_DEBUG) {
                Log.d(TAG, "reference: " + reference);
                Log.d(TAG, "downloadInfo: " + info);
            }
        }

        return reference;
    }
}
