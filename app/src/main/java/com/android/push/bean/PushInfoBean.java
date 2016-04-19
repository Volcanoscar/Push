/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PushInfoBean.java
 * 内容摘要： PushInfo信息Bean，封装Push对应的push_id、push_priority、push_count等信息
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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类描述 ：PushInfo信息Bean，封装Push对应的push_id、push_priority、push_count等信息
 *
 * @author 李翊星
 * @version 1.0
 */
public class PushInfoBean implements Parcelable{

    public int push_id;         //Push的ID

    public String push_priority;//Push优先级

    public int push_count;      //Push次数

    public PushInfoBean() {
        push_id = 0;
        push_priority = String.valueOf(0);
        push_count = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(push_id);
        dest.writeString(push_priority);
        dest.writeInt(push_count);
    }

    private PushInfoBean(Parcel in) {
        push_id = in.readInt();
        push_priority = in.readString();
        push_count = in.readInt();
    }

    public static final Parcelable.Creator<PushInfoBean> CREATOR = new Creator<PushInfoBean>() {
        @Override
        public PushInfoBean createFromParcel(Parcel source) {
            return new PushInfoBean(source);
        }

        @Override
        public PushInfoBean[] newArray(int size) {
            return new PushInfoBean[size];
        }
    };
}
