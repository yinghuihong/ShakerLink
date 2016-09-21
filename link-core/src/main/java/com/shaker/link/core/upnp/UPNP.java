package com.shaker.link.core.upnp;

import java.util.UUID;

/**
 * UPNP constants
 * Created by yinghuihong on 16/7/19.
 */
public interface UPNP {

    String ACTION_SEARCH = "search";
    String ACTION_SEARCH_RESP = "search_resp";
    String ACTION_NOTIFY = "notify";
    String CATEGORY_NOTIFY_ALIVE = "alive";
    String CATEGORY_NOTIFY_BYEBYE = "byebye";
    /* 设备唯一标识 */
    String uuid = UUID.randomUUID().toString();
    /* 设备端发送存活组播的时间间隔 */
    int NOTIFY_ALIVE_PERIOD = 30 * 1000;
    /* 设备端定义自身的存活时长(当控制点在该时间段内没有再次接收到ALIVE组播,则判定设备为离线) */
    int ALIVE_INTERVAL = 90 * 1000;
    /* 用于消除消息延迟时间 */
    int DISPOSER_ALIVE_MARGIN = 3 * 1000;
    /* 移除超时设备的执行间隔 */
    int DISPOSER_PERIOD = 30 * 1000;

}
