/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.BalanceStrategy;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BestResponseTimeBalanceStrategy
implements BalanceStrategy {
    @Override
    public void destroy() {
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
    }

    @Override
    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        Map<String, Long> blackList = proxy.getGlobalBlacklist();
        SQLException ex = null;
        int attempts = 0;
        do {
            String bestHost;
            ConnectionImpl conn;
            if (attempts >= numRetries) {
                if (ex == null) return null;
                throw ex;
            }
            long minResponseTime = Long.MAX_VALUE;
            int bestHostIndex = 0;
            if (blackList.size() == configuredHosts.size()) {
                blackList = proxy.getGlobalBlacklist();
            }
            for (int i = 0; i < responseTimes.length; ++i) {
                long candidateResponseTime = responseTimes[i];
                if (candidateResponseTime >= minResponseTime || blackList.containsKey((Object)configuredHosts.get((int)i))) continue;
                if (candidateResponseTime == 0L) {
                    bestHostIndex = i;
                    break;
                }
                bestHostIndex = i;
                minResponseTime = candidateResponseTime;
            }
            if ((conn = liveConnections.get((Object)(bestHost = configuredHosts.get((int)bestHostIndex)))) != null) return conn;
            try {
                return proxy.createConnectionForHost((String)bestHost);
            }
            catch (SQLException sqlEx) {
                ex = sqlEx;
                if (!proxy.shouldExceptionTriggerConnectionSwitch((Throwable)sqlEx)) throw sqlEx;
                proxy.addToGlobalBlacklist((String)bestHost);
                blackList.put((String)bestHost, null);
                if (blackList.size() != configuredHosts.size()) continue;
                ++attempts;
                try {
                    Thread.sleep((long)250L);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                blackList = proxy.getGlobalBlacklist();
                continue;
            }
            break;
        } while (true);
    }
}

