/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.RandomBalanceStrategy;
import com.mysql.jdbc.StringUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ServerAffinityStrategy
extends RandomBalanceStrategy {
    public static final String AFFINITY_ORDER = "serverAffinityOrder";
    public String[] affinityOrderedServers = null;

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        super.init((Connection)conn, (Properties)props);
        String hosts = props.getProperty((String)AFFINITY_ORDER);
        if (StringUtils.isNullOrEmpty((String)hosts)) return;
        this.affinityOrderedServers = hosts.split((String)",");
    }

    @Override
    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        if (this.affinityOrderedServers == null) {
            return super.pickConnection((LoadBalancedConnectionProxy)proxy, configuredHosts, liveConnections, (long[])responseTimes, (int)numRetries);
        }
        Map<String, Long> blackList = proxy.getGlobalBlacklist();
        String[] arr$ = this.affinityOrderedServers;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            block6 : {
                String host = arr$[i$];
                if (configuredHosts.contains((Object)host) && !blackList.containsKey((Object)host)) {
                    ConnectionImpl conn = liveConnections.get((Object)host);
                    if (conn != null) {
                        return conn;
                    }
                    try {
                        return proxy.createConnectionForHost((String)host);
                    }
                    catch (SQLException sqlEx) {
                        if (!proxy.shouldExceptionTriggerConnectionSwitch((Throwable)sqlEx)) break block6;
                        proxy.addToGlobalBlacklist((String)host);
                    }
                }
            }
            ++i$;
        }
        return super.pickConnection((LoadBalancedConnectionProxy)proxy, configuredHosts, liveConnections, (long[])responseTimes, (int)numRetries);
    }
}

