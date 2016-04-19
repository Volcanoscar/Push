package com.android.push.constant;

/**
 * 类描述 ：
 *
 * @author 李翊星
 * @version 1.0
 */
public final class BroadcastAction {

    /* 点击通知 */
    public static final String ACTION_NOTIFICATION_CLICK = "action.notification.click";

    /* 清除通知 */
    public static final String ACTION_NOTIFICATION_CANCEL = "action.notification.cancel";

    /* 关闭解析服务 */
    public static final String ACTION_STOP_PUSH_ANALYSE_SERVICE = "action.stop.push.analyse" +
            ".service";

    /* 开启解析服务 */
    public static final String ACTION_START_PUSH_ANALYSE_SERVICE = "action.start.push.analyse" +
            ".service";

    /* 关闭轮询服务 */
    public static final String ACTION_STOP_POLLING_SERVICE = "action.stop.polling.service";

}
