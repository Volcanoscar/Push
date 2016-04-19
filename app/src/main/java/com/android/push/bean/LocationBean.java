/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： LocationBean.java
 * 内容摘要： 位置信息Bean
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
 * 类描述 ：位置信息Bean
 *
 * @author 李翊星
 * @version 1.0
 */
public class LocationBean {

    public String mcc;

    public String mnc;

    public String cid;

    public String lac;

    public double latitude;

    public double longitude;

    public LocationBean() {
        mcc = "0";
        mnc = "0";
        cid = "0";
        lac = "0";
        latitude = 0.0;
        longitude = 0.0;
    }
}
