/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： PushConstant.java
 * 内容摘要： 公共类，定义了Push项目中多个文件共用的常量
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
 * 类描述 ：Push的常量类，定义Push类型、展现形式等常量
 *
 * @author 李翊星
 * @version 1.0
 */
public class PushConstant {

    /* Push的展现形式 */
    public static final int NOTIFICATION_SHOW = 1;  //通知
    public static final int DIALOG_SHOW = 2;        //弹窗
    public static final int FULLSCREEN_SHOW = 3;    //全屏
    public static final int SILENT_INSTALL_SHOW = 4;//静默安装

    /* Push内容的类型 */
    public static final int WEB_PUSH = 1;       //网页
    public static final int PICTURE_PUSH = 2;   //图片
    public static final int OTA_PUSH = 3;       //系统更新
    public static final int APP_PUSH = 4;       //应用

    /* 下载的文件类型 */
    public static final int PIC_FILE_TYPE = 1;  //图片
    public static final int APK_FILE_TYPE = 2;  //APK

    /* 安装方式 */
    public static final int CLICK_INSTALL = 1;  //点击安装
    public static final int AUTO_INSTALL = 2;   //自动安装

    /* 所需最少手机内置存储空间*/
    public static final int MINIMUM_AVALIABLE_SPACE_REQUIRED = 300;

    /* 跳转到OTA的Intent Action */
    public static String OTA_INTENT_ACTION = "com.mediatek.intent.System_Update_Entry";

    public static final String LOCATION_ACTION = "com.android.push.location";

    public static final String PUSH_PARCEL_KEY = "push";
}
