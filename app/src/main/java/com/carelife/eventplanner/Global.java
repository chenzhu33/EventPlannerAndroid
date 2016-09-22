package com.carelife.eventplanner;

/**
 * 修改,添加两个常量
 */
public class Global {
    /**
     * 上次路径查询时间
     */
    public static long SEND_TIME = 0;

    /**
     * 间隔时间
     */
    public static long ELAPSED_TIME = 1000 * 60 * 10;

    /**
     * 地点轮询间隔
     */
    public static long LOCATION_REQUEST_TIME = 1000 * 5;

    /**
     * 活动通知提前量
     */
    public static long EVENT_NOTIFY_THRESHOLD = 1000 * 60 * 15;
}
