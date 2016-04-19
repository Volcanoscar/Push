/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： DownloadInfoBean.java
 * 内容摘要： 下载信息Bean，封装下载任务对应的push_id等信息
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

/**
 * 类描述 ：下载信息Bean，封装下载任务对应的push_id等信息
 *
 * @author 李翊星
 * @version 1.0
 */
public class DownloadInfoBean {

    public int push_id; //Push的ID

    public String push_priority;    //Push优先级

    public int push_count;  //Push推送次数

    public int file_type;   //文件类型，存在图片、apk两种类型

    public boolean is_silent_install;   //静默安装

    public int install_mode;    //安装模式

    public DownloadInfoBean() {
        push_id = 0;
        push_priority = "";
        push_count = 0;
        file_type = 0;
        is_silent_install = false;
    }
}
