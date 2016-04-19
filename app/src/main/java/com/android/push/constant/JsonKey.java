/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： JsonKey.java
 * 内容摘要： Json Key的常量类，定义了Push项目中Json相关的Key的定义值
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-24
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.constant;

/**
 * 类描述 ：Json Key的常量类，定义了Push项目中Json相关的Key的定义值
 *
 * @author 李翊星
 * @version 1.0
 */
public final class JsonKey {

    /* Public */
    public static final String IMEI = "imei";
    public static final String MD5 = "md5";

    /* Push */
    public static final String TYPE = "type";
    public static final String SHOW_TYPE = "show_type";
    public static final String TITLE = "title";
    public static final String CONTENT_TEXT = "content_text";
    public static final String URL = "url";
    public static final String PHONE_COMMAND = "phone_command";
    public static final String POLLING_INTERVAL = "polling_interval";
    public static final String PICTURE_URLS = "picture_urls";
    public static final String INSTALL_MODE = "install_mode";
    public static final String BUTTON_CONTENT = "button_content";

    /* PushInfo */
    public static final String PUSH_ID = "push_id";
    public static final String PUSH_PRIORITY = "push_priority";
    public static final String PUSH_COUNT = "push_count";

    /* Request */
    public static final String REQUEST = "request";
    public static final String MODEL = "model";
    public static final String SOFTWARE_VERSION = "software_version";
    public static final String LOCATION = "location";
    public static final String PUSH_INFOS = "push_infos";

    /* Feedback */
    public static final String FEEDBACK = "feedback";
    public static final String PUSH_FEEDBACK = "push_feedback";
    public static final String IS_CLICKED = "is_clicked";
    public static final String IS_DOWNLOADED = "is_downloaded";

    /* GPS Location*/
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /* BaseStation location */
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";
    public static final String CID = "cid";
    public static final String LAC = "lac";

}
