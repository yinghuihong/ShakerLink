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

    String uuid = UUID.randomUUID().toString();
    int ALIVE_INTERVAL = 60 * 1000;
    int DISPOSER_ALIVE_MARGIN = 3 * 1000;
    int DISPOSER_PERIOD = 30 * 1000;

}
