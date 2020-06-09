/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.BalanceStrategy;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RandomBalanceStrategy
implements BalanceStrategy {
    @Override
    public void destroy() {
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
    }

    @Override
    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        int numHosts = configuredHosts.size();
        SQLException ex = null;
        ArrayList<String> whiteList = new ArrayList<String>((int)numHosts);
        whiteList.addAll(configuredHosts);
        Map<String, Long> blackList = proxy.getGlobalBlacklist();
        whiteList.removeAll(blackList.keySet());
        Map<String, Integer> whiteListMap = this.getArrayIndexMap(whiteList);
        int attempts = 0;
        do {
            if (attempts >= numRetries) {
                if (ex == null) return null;
                throw ex;
            }
            int random = (int)Math.floor((double)(Math.random() * (double)whiteList.size()));
            if (whiteList.size() == 0) {
                throw SQLError.createSQLException((String)"No hosts configured", null);
            }
            String hostPortSpec = (String)whiteList.get((int)random);
            ConnectionImpl conn = liveConnections.get((Object)hostPortSpec);
            if (conn != null) return conn;
            try {
                return proxy.createConnectionForHost((String)hostPortSpec);
            }
            catch (SQLException sqlEx) {
                ex = sqlEx;
                if (!proxy.shouldExceptionTriggerConnectionSwitch((Throwable)sqlEx)) throw sqlEx;
                Integer whiteListIndex = whiteListMap.get((Object)hostPortSpec);
                if (whiteListIndex != null) {
                    whiteList.remove((int)whiteListIndex.intValue());
                    whiteListMap = this.getArrayIndexMap(whiteList);
                }
                proxy.addToGlobalBlacklist((String)hostPortSpec);
                if (whiteList.size() != 0) continue;
                ++attempts;
                try {
                    Thread.sleep((long)250L);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                whiteListMap = new HashMap<String, Integer>((int)numHosts);
                whiteList.addAll(configuredHosts);
                blackList = proxy.getGlobalBlacklist();
                whiteList.removeAll(blackList.keySet());
                whiteListMap = this.getArrayIndexMap(whiteList);
                continue;
            }
            break;
        } while (true);
    }

    private Map<String, Integer> getArrayIndexMap(List<String> l) {
        HashMap<String, Integer> m = new HashMap<String, Integer>((int)l.size());
        int i = 0;
        while (i < l.size()) {
            m.put((String)l.get((int)i), (Integer)Integer.valueOf((int)i));
            ++i;
        }
        return m;
    }
}

