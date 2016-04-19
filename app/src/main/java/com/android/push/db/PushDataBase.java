/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PushDataBase.java
 * 内容摘要： 数据库操作方法调用类，提供数据库操作的相关接口
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-10-20
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.db;

import com.android.push.bean.FeedbackBean;
import com.android.push.bean.PushInfoBean;
import com.android.push.util.JsonUtil;

import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 类描述 ：数据库操作方法调用类
 *
 * @author 李翊星
 * @version 1.0
 */

public class PushDataBase {

    private static final String TAG = "push.PushDataBase";

    private static PushDataBase mAppInfoDataBase;

    private static final String CREATE_TABLE_PUSH_INFO = "create table if not exists '%s' ("
            + "push_priority varchar(10) primary key,"
            + "push_id integer,"
            + "push_count integer default 0)";

    private static final String CREATE_TABLE_FEED_BACK = "create table if not exists '%s' ("
            + "push_id integer primary key,"
            + "is_clicked integer default 0,"
            + "is_downloaded integer default 0)";

    private static final String QUERY_BY_PUSH_PRIORITY = "select * from '%s' where " +
            "push_priority=?";


    private static final String PUSH_INFO_TABLE_NAME = "push_info";
    private static final String FEED_BACK_TABLE_NAME = "feed_back";
    private static final String STRING_FORMAT = "'%s'";

    private PushDBHelper mPushDBHelper;

    private Context mContext;

    private SQLiteDatabase mDatabase;

    public PushDataBase(Context context) {
        mContext = context;
    }

    /**
     * 单例模式获取AppInfoDataBase对象
     */
    public static PushDataBase getInstance(Context context) {
        if (mAppInfoDataBase == null) {
            mAppInfoDataBase = new PushDataBase(context);
        }
        return mAppInfoDataBase;
    }

    /**
     * 打开数据库
     */
    public PushDataBase open() {
        mPushDBHelper = new PushDBHelper(mContext, "kkPush.db", null, 1);
        mDatabase = mPushDBHelper.getWritableDatabase();
        return this;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mPushDBHelper.close();
    }

    /**
     * 遍历PushInfo表
     */
    public JSONArray queryPushInfoTable() {
        JSONArray pushInfos = new JSONArray();

        Cursor cursor = mDatabase.query(PUSH_INFO_TABLE_NAME, null, null, null, null,
                null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                PushInfoBean bean = new PushInfoBean();
                bean.push_priority = cursor.getString(
                        cursor.getColumnIndex("push_priority"));
                bean.push_id = cursor.getInt(
                        cursor.getColumnIndex("push_id"));
                bean.push_count = cursor.getInt(
                        cursor.getColumnIndex("push_count"));
                pushInfos.put(JsonUtil.getPushInfoJson(bean));
            }
        }

        cursor.close();

        return pushInfos;
    }

    /**
     * 记录Push信息
     *
     * @param pushInfo 需要记录的Push信息
     */
    public void recordPushInfo(PushInfoBean pushInfo) {

        ContentValues cv = new ContentValues();
        cv.put("push_id", pushInfo.push_id);
        cv.put("push_priority", pushInfo.push_priority);
        Log.d(TAG, "push_count: " + pushInfo.push_count);
        cv.put("push_count", pushInfo.push_count);

        if (queryByPushPriority(pushInfo.push_priority) == null) {
            mDatabase.insert(PUSH_INFO_TABLE_NAME, null, cv);
        } else {
            mDatabase.update(PUSH_INFO_TABLE_NAME, cv,
                    "push_priority = ?",
                    new String[]{pushInfo.push_priority});
        }
    }

    /**
     * 根据Push优先级进行查询
     *
     * @param priority Push的优先级
     */
    public PushInfoBean queryByPushPriority(String priority) {

        PushInfoBean pushInfo = new PushInfoBean();

        Cursor cursor = mDatabase.rawQuery(String.format(QUERY_BY_PUSH_PRIORITY, PUSH_INFO_TABLE_NAME),
                new String[]{priority});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            while (cursor.moveToNext()) {
                pushInfo.push_id = cursor.getInt(
                        cursor.getColumnIndex("push_id"));
                pushInfo.push_priority = cursor.getString(
                        cursor.getColumnIndex("push_priority"));
                pushInfo.push_count = cursor.getInt(
                        cursor.getColumnIndex("push_count"));
            }
            cursor.close();
            return pushInfo;
        }
    }

    /**
     * 判断Feedback反馈表是否为空
     */
    public boolean isFeedbackTableEmpty() {
        Cursor cursor = mDatabase.query(FEED_BACK_TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = (cursor.getCount() == 0);
        cursor.close();
        return isEmpty;
    }

    /**
     * 遍历Feedback表
     */
    public JSONArray queryFeedbackTable() {
        JSONArray feedbacks = new JSONArray();

        Cursor cursor = mDatabase.query(FEED_BACK_TABLE_NAME, null, null, null, null,
                null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                FeedbackBean bean = new FeedbackBean();
                bean.push_id = cursor.getInt(
                        cursor.getColumnIndex("push_id"));
                bean.is_clicked = cursor.getInt(
                        cursor.getColumnIndex("is_clicked"));
                bean.is_downloaded = cursor.getInt(
                        cursor.getColumnIndex("is_downloaded"));
                feedbacks.put(JsonUtil.getFeedbackJson(bean));
            }
        }

        cursor.close();

        return feedbacks;
    }

    /**
     * 清空Feedback表
     */
    public void clearFeedbackTable() {
        mDatabase.delete(FEED_BACK_TABLE_NAME, null, null);
    }

    /**
     * 清空PushInfo表
     */
    public void clearPushInfoTable() {
        mDatabase.delete(PUSH_INFO_TABLE_NAME, null, null);
    }

    /**
     * 增加Push反馈记录
     *
     * @param feedback 反馈信息
     */
    public void addFeedback(FeedbackBean feedback) {
        ContentValues cv = new ContentValues();

        cv.put("push_id", feedback.push_id);
        cv.put("is_clicked", feedback.is_clicked);
        cv.put("is_downloaded", feedback.is_downloaded);

        mDatabase.insert(FEED_BACK_TABLE_NAME, null, cv);
    }

    /**
     * 删除数据库所有表
     */
    public void deleteAllTable() {
        Cursor cursor = mDatabase.rawQuery("select name from sqlite_master " +
                "where type='table' order by name", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            mDatabase.execSQL("drop table if exists '" + name + "'");
        }
        cursor.close();
    }

    /***
     * 数据库辅助类
     */
    class PushDBHelper extends SQLiteOpenHelper {

        public PushDBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory,
                            int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //  创建push_info表
            db.execSQL(String.format(CREATE_TABLE_PUSH_INFO, PUSH_INFO_TABLE_NAME));
            //  创建feed_back表
            db.execSQL(String.format(CREATE_TABLE_FEED_BACK, FEED_BACK_TABLE_NAME));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}

