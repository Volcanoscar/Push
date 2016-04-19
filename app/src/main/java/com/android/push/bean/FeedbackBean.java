/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： FeedbackBean.java
 * 内容摘要： 反馈信息Bean，封装push_id等需要反馈的信息
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
 * 类描述 ：反馈信息Bean，封装push_id等需要反馈的信息
 *
 * @author 李翊星
 * @version 1.0
 */
public class FeedbackBean {

    public int push_id;     //Push的ID

    public int is_clicked;  //是否点击

    public int is_downloaded;//是否下载

    public FeedbackBean() {
        push_id = 0;
        is_clicked = 0;
        is_downloaded = 0;
    }
}
