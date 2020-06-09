/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.StandardSocketFactory;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;

public class SocksProxySocketFactory
extends StandardSocketFactory {
    public static int SOCKS_DEFAULT_PORT = 1080;

    @Override
    protected Socket createSocket(Properties props) {
        String socksProxyHost = props.getProperty((String)"socksProxyHost");
        String socksProxyPortString = props.getProperty((String)"socksProxyPort", (String)String.valueOf((int)SOCKS_DEFAULT_PORT));
        int socksProxyPort = SOCKS_DEFAULT_PORT;
        try {
            socksProxyPort = Integer.valueOf((String)socksProxyPortString).intValue();
            return new Socket((Proxy)new Proxy((Proxy.Type)Proxy.Type.SOCKS, (SocketAddress)new InetSocketAddress((String)socksProxyHost, (int)socksProxyPort)));
        }
        catch (NumberFormatException ex) {
            // empty catch block
        }
        return new Socket((Proxy)new Proxy((Proxy.Type)Proxy.Type.SOCKS, (SocketAddress)new InetSocketAddress((String)socksProxyHost, (int)socksProxyPort)));
    }
}

