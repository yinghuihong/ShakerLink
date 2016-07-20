package com.shaker.link.core.upnp;

import java.util.UUID;

/**
 * Created by yinghuihong on 16/7/19.
 */
public interface UPNP {
    String ACTION_SEARCH = "search";
    String ACTION_NOTIFY = "notify";
    String NOTIFY_ALIVE = "alive";
    String NOTIFY_BYEBYE = "byebye";

    String ACTION_SEARCH_RESP = "search_resp";

    String uuid = UUID.randomUUID().toString();
}
