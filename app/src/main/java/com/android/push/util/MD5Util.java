/************************************************************************
 * 版权所有 (C)2012, 深圳市康佳集团股份有限公司。
 *
 * 文件名称： MD5Util.java
 * 内容摘要： MD5的Util类，封装生成MD5、校验MD5等操作接口
 * 当前版本： 1.0
 * 作 者： 	 李翊星
 * 完成日期： 2015-11-24
 * 修改记录：
 * 修改日期：
 * 版 本 号：
 * 修 改 人：
 * 修改内容：
 **************************************************************************/

package com.android.push.util;

import java.security.MessageDigest;

/**
 * 类描述 ：MD5的Util类，封装生成MD5、校验MD5等操作接口
 *
 * @author 李翊星
 * @version 1.0
 */
public class MD5Util {

    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'};

    /**
     * 计算String的MD5值
     */
    public static String getMD5(String s) {

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 校验MD5
     *
     * @param s   需要校验的数据
     * @param md5 MD5值
     * @return 校验结果，true：成功，false：失败
     */
    public static boolean checkMD5(String s, String md5) {

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).equals(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
