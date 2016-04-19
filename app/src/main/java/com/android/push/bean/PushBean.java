/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PushBean.java
 * 内容摘要： Push信息Bean，封装从服务器获取到的Push信息
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-24
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.bean;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类描述 ：Push信息Bean，封装从服务器获取到的Push信息
 *
 * @author 李翊星
 * @version 1.0
 */
public class PushBean implements Parcelable {

    public int type;    //推送类型

    public int show_type;   //展现形式

    public String title;    //标题

    public String content_text; //内容

    public String url;  //链接

    public String phone_command;    //手机指令

    public long polling_interval;   //轮询周期

    public JSONArray picture_urls;  //图片下载链接

    public int install_mode;        //安装模式

    public String button_content;   //按钮文本内容

    public PushInfoBean push_info;  //Push信息

    public PushBean() {
        type = show_type = 0;
        title = content_text = url = phone_command = "";
        polling_interval = 0;
        picture_urls = new JSONArray();
        install_mode = 0;
        button_content = "";
        push_info = new PushInfoBean();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(show_type);
        dest.writeString(title);
        dest.writeString(content_text);
        dest.writeString(url);
        dest.writeString(phone_command);
        dest.writeLong(polling_interval);
        dest.writeString(picture_urls.toString());
        dest.writeInt(install_mode);
        dest.writeString(button_content);
        dest.writeParcelable(push_info, 0);
    }

    private PushBean(Parcel in) {
        type = in.readInt();
        show_type = in.readInt();
        title = in.readString();
        content_text = in.readString();
        url = in.readString();
        phone_command = in.readString();
        polling_interval = in.readLong();
        try {
            picture_urls = new JSONArray(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        install_mode = in.readInt();
        button_content = in.readString();
        push_info = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Parcelable.Creator<PushBean> CREATOR = new Creator<PushBean>() {
        @Override
        public PushBean createFromParcel(Parcel source) {
            return new PushBean(source);
        }

        @Override
        public PushBean[] newArray(int size) {
            return new PushBean[size];
        }
    };

    @Override
    public String toString() {
        return "push --> id: " + push_info.push_id + ", type:" + type + ", showType:" + show_type
                + ", title:" + title + ", url:" + url + ", pictureUrls:" + picture_urls
                + ", installMode:" + install_mode;
    }
}
