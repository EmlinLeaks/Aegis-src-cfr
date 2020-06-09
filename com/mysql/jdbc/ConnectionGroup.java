/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConnectionGroup {
    private String groupName;
    private long connections = 0L;
    private long activeConnections = 0L;
    private HashMap<Long, LoadBalancedConnectionProxy> connectionProxies = new HashMap<K, V>();
    private Set<String> hostList = new HashSet<String>();
    private boolean isInitialized = false;
    private long closedProxyTotalPhysicalConnections = 0L;
    private long closedProxyTotalTransactions = 0L;
    private int activeHosts = 0;
    private Set<String> closedHosts = new HashSet<String>();

    ConnectionGroup(String groupName) {
        this.groupName = groupName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long registerConnectionProxy(LoadBalancedConnectionProxy proxy, List<String> localHostList) {
        ConnectionGroup connectionGroup = this;
        // MONITORENTER : connectionGroup
        if (!this.isInitialized) {
            this.hostList.addAll(localHostList);
            this.isInitialized = true;
            this.activeHosts = localHostList.size();
        }
        long currentConnectionId = ++this.connections;
        this.connectionProxies.put((Long)Long.valueOf((long)currentConnectionId), (LoadBalancedConnectionProxy)proxy);
        // MONITOREXIT : connectionGroup
        ++this.activeConnections;
        return currentConnectionId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Collection<String> getInitialHosts() {
        return this.hostList;
    }

    public int getActiveHostCount() {
        return this.activeHosts;
    }

    public Collection<String> getClosedHosts() {
        return this.closedHosts;
    }

    public long getTotalLogicalConnectionCount() {
        return this.connections;
    }

    public long getActiveLogicalConnectionCount() {
        return this.activeConnections;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getActivePhysicalConnectionCount() {
        long result = 0L;
        HashMap<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<Long, LoadBalancedConnectionProxy>();
        HashMap<Long, LoadBalancedConnectionProxy> hashMap = this.connectionProxies;
        // MONITORENTER : hashMap
        proxyMap.putAll(this.connectionProxies);
        // MONITOREXIT : hashMap
        Iterator<V> i$ = proxyMap.values().iterator();
        while (i$.hasNext()) {
            LoadBalancedConnectionProxy proxy = (LoadBalancedConnectionProxy)i$.next();
            result += proxy.getActivePhysicalConnectionCount();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getTotalPhysicalConnectionCount() {
        long allConnections = this.closedProxyTotalPhysicalConnections;
        HashMap<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<Long, LoadBalancedConnectionProxy>();
        HashMap<Long, LoadBalancedConnectionProxy> hashMap = this.connectionProxies;
        // MONITORENTER : hashMap
        proxyMap.putAll(this.connectionProxies);
        // MONITOREXIT : hashMap
        Iterator<V> i$ = proxyMap.values().iterator();
        while (i$.hasNext()) {
            LoadBalancedConnectionProxy proxy = (LoadBalancedConnectionProxy)i$.next();
            allConnections += proxy.getTotalPhysicalConnectionCount();
        }
        return allConnections;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getTotalTransactionCount() {
        long transactions = this.closedProxyTotalTransactions;
        HashMap<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<Long, LoadBalancedConnectionProxy>();
        HashMap<Long, LoadBalancedConnectionProxy> hashMap = this.connectionProxies;
        // MONITORENTER : hashMap
        proxyMap.putAll(this.connectionProxies);
        // MONITOREXIT : hashMap
        Iterator<V> i$ = proxyMap.values().iterator();
        while (i$.hasNext()) {
            LoadBalancedConnectionProxy proxy = (LoadBalancedConnectionProxy)i$.next();
            transactions += proxy.getTransactionCount();
        }
        return transactions;
    }

    public void closeConnectionProxy(LoadBalancedConnectionProxy proxy) {
        --this.activeConnections;
        this.connectionProxies.remove((Object)Long.valueOf((long)proxy.getConnectionGroupProxyID()));
        this.closedProxyTotalPhysicalConnections += proxy.getTotalPhysicalConnectionCount();
        this.closedProxyTotalTransactions += proxy.getTransactionCount();
    }

    public void removeHost(String hostPortPair) throws SQLException {
        this.removeHost((String)hostPortPair, (boolean)false);
    }

    public void removeHost(String hostPortPair, boolean removeExisting) throws SQLException {
        this.removeHost((String)hostPortPair, (boolean)removeExisting, (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void removeHost(String hostPortPair, boolean removeExisting, boolean waitForGracefulFailover) throws SQLException {
        if (this.activeHosts == 1) {
            throw SQLError.createSQLException((String)"Cannot remove host, only one configured host active.", null);
        }
        if (!this.hostList.remove((Object)hostPortPair)) throw SQLError.createSQLException((String)("Host is not configured: " + hostPortPair), null);
        --this.activeHosts;
        if (removeExisting) {
            HashMap<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<Long, LoadBalancedConnectionProxy>();
            HashMap<Long, LoadBalancedConnectionProxy> hashMap = this.connectionProxies;
            // MONITORENTER : hashMap
            proxyMap.putAll(this.connectionProxies);
            // MONITOREXIT : hashMap
            for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
                if (waitForGracefulFailover) {
                    proxy.removeHostWhenNotInUse((String)hostPortPair);
                    continue;
                }
                proxy.removeHost((String)hostPortPair);
            }
        }
        this.closedHosts.add((String)hostPortPair);
    }

    public void addHost(String hostPortPair) {
        this.addHost((String)hostPortPair, (boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHost(String hostPortPair, boolean forExisting) {
        ConnectionGroup connectionGroup = this;
        // MONITORENTER : connectionGroup
        if (this.hostList.add((String)hostPortPair)) {
            ++this.activeHosts;
        }
        // MONITOREXIT : connectionGroup
        if (!forExisting) {
            return;
        }
        HashMap<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap<Long, LoadBalancedConnectionProxy>();
        HashMap<Long, LoadBalancedConnectionProxy> hashMap = this.connectionProxies;
        // MONITORENTER : hashMap
        proxyMap.putAll(this.connectionProxies);
        // MONITOREXIT : hashMap
        Iterator<V> i$ = proxyMap.values().iterator();
        while (i$.hasNext()) {
            LoadBalancedConnectionProxy proxy = (LoadBalancedConnectionProxy)i$.next();
            proxy.addHost((String)hostPortPair);
        }
    }
}

