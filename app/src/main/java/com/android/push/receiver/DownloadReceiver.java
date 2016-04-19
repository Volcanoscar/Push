/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： DownloadReceiver.java
 * 内容摘要： 下载任务的广播接收器，主要接受下载完成、下载任务通知点击的广播
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

import com.android.push.config.Config;
import com.android.push.constant.PushConstant;
import com.android.push.bean.DownloadInfoBean;
import com.android.push.bean.FeedbackBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.db.PushDataBase;
import com.android.push.util.ApkManager;
import com.android.push.util.FeedbackUtil;
import com.android.push.util.JsonUtil;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static com.android.push.util.SharedPreferenceUtil.getDownloadInfo;

/**
 * 类描述 ：下载任务状态广播接收器
 *
 * @author 李翊星
 * @version 1.0
 */
public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "push.DownloadReceiver";

    private DownloadManager mDownloadManager;
    private PushDataBase mPushDataBase;

    @Override
    public void onReceive(Context context, Intent intent) {

        mDownloadManager = (DownloadManager) context.getSystemService(Context
                .DOWNLOAD_SERVICE);
        mPushDataBase = PushDataBase.getInstance(context);

        //  获取下载任务的ID
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        //  下载完成广播
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

            //  查看是否存在相关下载记录
            String infoStr = getDownloadInfo(context, reference);
            if (!"".equals(infoStr)) {

                //  解析infoStr得到DownloadInfo
                DownloadInfoBean downloadInfo = JsonUtil.parseDownloadInfoJson(infoStr);

                //  查看下载状态，是否下载成功
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(reference);
                Cursor cursor = mDownloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager
                            .COLUMN_REASON));
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager
                            .COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL && reason == 0) {
                        Log.d(TAG, "download complete");
                        if (downloadInfo.file_type != PushConstant.PIC_FILE_TYPE) {
                            analyseDownloadInfo(context, reference, infoStr);
                        }
                    }
                }
                cursor.close();
            }

        }
        //  通知点击广播
        else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            Log.d(TAG, "download click");
            Intent downloadView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            downloadView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(downloadView);
        }
    }

    /**
     * 解析下载信息
     */
    private void analyseDownloadInfo(Context context, long reference, String infoStr) {
        DownloadInfoBean downloadInfo = JsonUtil.parseDownloadInfoJson(infoStr);

        if (Config.LOG_DEBUG) {
            Log.d(TAG, "download complete,download info:" + downloadInfo);
        }

        //  设置PushInfo
        PushInfoBean pushInfo = new PushInfoBean();
        pushInfo.push_id = downloadInfo.push_id;
        pushInfo.push_priority = downloadInfo.push_priority;
        pushInfo.push_count = downloadInfo.push_count;

        //  记录PushInfo
        mPushDataBase.open();
        mPushDataBase.recordPushInfo(pushInfo);
        mPushDataBase.close();

        //  反馈操作
        FeedbackBean feedback = new FeedbackBean();
        feedback.push_id = pushInfo.push_id;
        feedback.is_clicked = 1;
        feedback.is_downloaded = 1;
        FeedbackUtil.feedbackToServer(context, feedback);

        Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(reference);
        if (Config.LOG_DEBUG) {
            Log.d(TAG, "file path: " + downloadFileUri.getPath());
        }

        if (downloadInfo.file_type == PushConstant.APK_FILE_TYPE) {

            //  跳转到安装界面
            if (downloadInfo.install_mode == PushConstant.CLICK_INSTALL) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
            //  静默安装
            else {
                new ApkManager(context).installApkSlient(downloadFileUri.getPath());
            }
        }
    }

}
