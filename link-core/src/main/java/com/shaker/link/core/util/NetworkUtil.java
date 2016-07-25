package com.shaker.link.core.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * network util
 * Created by yinghuihong on 16/7/25.
 */
public class NetworkUtil {

    public static String getSiteLocalAddress() throws UnknownHostException {
        try {
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while (b.hasMoreElements()) {
                for (InterfaceAddress f : b.nextElement().getInterfaceAddresses())
                    if (f.getAddress().isSiteLocalAddress())
                        return f.getAddress().getHostAddress();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return InetAddress.getLocalHost().getHostAddress();
    }

}
