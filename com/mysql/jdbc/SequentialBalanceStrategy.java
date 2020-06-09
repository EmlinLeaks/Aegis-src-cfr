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
public class SequentialBalanceStrategy
implements BalanceStrategy {
    private int currentHostIndex = -1;

    @Override
    public void destroy() {
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        numHosts = configuredHosts.size();
        ex = null;
        blackList = proxy.getGlobalBlacklist();
        attempts = 0;
        do {
            block21 : {
                block20 : {
                    block19 : {
                        block18 : {
                            if (attempts >= numRetries) {
                                if (ex == null) return null;
                                throw ex;
                            }
                            if (numHosts != 1) break block18;
                            this.currentHostIndex = 0;
                            ** GOTO lbl-1000
                        }
                        if (this.currentHostIndex == -1) break block19;
                        foundGoodHost = false;
                        break block20;
                    }
                    for (i = random = (int)java.lang.Math.floor((double)(java.lang.Math.random() * (double)numHosts)); i < numHosts; ++i) {
                        if (blackList.containsKey((Object)configuredHosts.get((int)i))) continue;
                        this.currentHostIndex = i;
                        break;
                    }
                    if (this.currentHostIndex == -1) {
                        for (i = 0; i < random; ++i) {
                            if (blackList.containsKey((Object)configuredHosts.get((int)i))) continue;
                            this.currentHostIndex = i;
                            break;
                        }
                    }
                    if (this.currentHostIndex != -1) ** GOTO lbl-1000
                    blackList = proxy.getGlobalBlacklist();
                    try {
                        Thread.sleep((long)250L);
                    }
                    catch (InterruptedException e) {}
                    break block21;
                }
                for (i = this.currentHostIndex + 1; i < numHosts; ++i) {
                    if (blackList.containsKey((Object)configuredHosts.get((int)i))) continue;
                    this.currentHostIndex = i;
                    foundGoodHost = true;
                    break;
                }
                if (!foundGoodHost) {
                    for (i = 0; i < this.currentHostIndex; ++i) {
                        if (blackList.containsKey((Object)configuredHosts.get((int)i))) continue;
                        this.currentHostIndex = i;
                        foundGoodHost = true;
                        break;
                    }
                }
                if (!foundGoodHost) {
                    blackList = proxy.getGlobalBlacklist();
                    try {
                        Thread.sleep((long)250L);
                    }
                    catch (InterruptedException e) {}
                } else lbl-1000: // 3 sources:
                {
                    hostPortSpec = configuredHosts.get((int)this.currentHostIndex);
                    conn = liveConnections.get((Object)hostPortSpec);
                    if (conn != null) return conn;
                    try {
                        return proxy.createConnectionForHost((String)hostPortSpec);
                    }
                    catch (SQLException sqlEx) {
                        ex = sqlEx;
                        if (proxy.shouldExceptionTriggerConnectionSwitch((Throwable)sqlEx) == false) throw sqlEx;
                        proxy.addToGlobalBlacklist((String)hostPortSpec);
                        try {
                            Thread.sleep((long)250L);
                        }
                        catch (InterruptedException e) {}
                    }
                }
            }
            ++attempts;
        } while (true);
    }
}

