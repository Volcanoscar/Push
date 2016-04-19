/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： RequestBean.java
 * 内容摘要： 请求信息Bean，封装手机轮询请求的相关信息
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
import org.json.JSONObject;

/**
 * 类描述 ：请求信息Bean，封装手机轮询请求的相关信息
 *
 * @author 李翊星
 * @version 1.0
 */
public class RequestBean {

    public String imei;         //手机IMEI号

    public String model;        //手机型号

    public String software_version;//软件版本号

    public JSONObject location;    //位置信息

    public JSONArray push_infos;   //本地记录的PushInfo

    public RequestBean() {
        imei = model = software_version = "";
        location = new JSONObject();
        push_infos = new JSONArray();
    }
}
