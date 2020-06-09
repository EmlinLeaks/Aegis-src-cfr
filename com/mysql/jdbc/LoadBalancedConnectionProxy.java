/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.BalanceStrategy;
import com.mysql.jdbc.BestResponseTimeBalanceStrategy;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionGroup;
import com.mysql.jdbc.ConnectionGroupManager;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.LoadBalanceExceptionChecker;
import com.mysql.jdbc.LoadBalancedAutoCommitInterceptor;
import com.mysql.jdbc.LoadBalancedConnection;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.LoadBalancedMySQLConnection;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PingTarget;
import com.mysql.jdbc.RandomBalanceStrategy;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ServerAffinityStrategy;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LoadBalancedConnectionProxy
extends MultiHostConnectionProxy
implements PingTarget {
    private ConnectionGroup connectionGroup = null;
    private long connectionGroupProxyID = 0L;
    protected Map<String, ConnectionImpl> liveConnections;
    private Map<String, Integer> hostsToListIndexMap;
    private Map<ConnectionImpl, String> connectionsToHostsMap;
    private long totalPhysicalConnections = 0L;
    private long[] responseTimes;
    private int retriesAllDown;
    private BalanceStrategy balancer;
    private int autoCommitSwapThreshold = 0;
    public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
    private int globalBlacklistTimeout = 0;
    private static Map<String, Long> globalBlacklist = new HashMap<String, Long>();
    public static final String HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY = "loadBalanceHostRemovalGracePeriod";
    private int hostRemovalGracePeriod = 0;
    private Set<String> hostsToRemove = new HashSet<String>();
    private boolean inTransaction = false;
    private long transactionStartTime = 0L;
    private long transactionCount = 0L;
    private LoadBalanceExceptionChecker exceptionChecker;
    private static Constructor<?> JDBC_4_LB_CONNECTION_CTOR;
    private static Class<?>[] INTERFACES_TO_PROXY;
    private static LoadBalancedConnection nullLBConnectionInstance;

    public static LoadBalancedConnection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
        LoadBalancedConnectionProxy connProxy = new LoadBalancedConnectionProxy(hosts, (Properties)props);
        return (LoadBalancedConnection)Proxy.newProxyInstance((ClassLoader)LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, (InvocationHandler)connProxy);
    }

    private LoadBalancedConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        String group = props.getProperty((String)"loadBalanceConnectionGroup", null);
        boolean enableJMX = false;
        String enableJMXAsString = props.getProperty((String)"loadBalanceEnableJMX", (String)"false");
        try {
            enableJMX = Boolean.parseBoolean((String)enableJMXAsString);
        }
        catch (Exception e) {
            throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForLoadBalanceEnableJMX", (Object[])new Object[]{enableJMXAsString}), (String)"S1009", null);
        }
        if (group != null) {
            this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance((String)group);
            if (enableJMX) {
                ConnectionGroupManager.registerJmx();
            }
            this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy((LoadBalancedConnectionProxy)this, hosts);
            hosts = new ArrayList<String>(this.connectionGroup.getInitialHosts());
        }
        int numHosts = this.initializeHostsSpecs(hosts, (Properties)props);
        this.liveConnections = new HashMap<String, ConnectionImpl>((int)numHosts);
        this.hostsToListIndexMap = new HashMap<String, Integer>((int)numHosts);
        for (int i = 0; i < numHosts; ++i) {
            this.hostsToListIndexMap.put(this.hostList.get((int)i), (Integer)Integer.valueOf((int)i));
        }
        this.connectionsToHostsMap = new HashMap<ConnectionImpl, String>((int)numHosts);
        this.responseTimes = new long[numHosts];
        String retriesAllDownAsString = this.localProps.getProperty((String)"retriesAllDown", (String)"120");
        try {
            this.retriesAllDown = Integer.parseInt((String)retriesAllDownAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForRetriesAllDown", (Object[])new Object[]{retriesAllDownAsString}), (String)"S1009", null);
        }
        String blacklistTimeoutAsString = this.localProps.getProperty((String)BLACKLIST_TIMEOUT_PROPERTY_KEY, (String)"0");
        try {
            this.globalBlacklistTimeout = Integer.parseInt((String)blacklistTimeoutAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForLoadBalanceBlacklistTimeout", (Object[])new Object[]{blacklistTimeoutAsString}), (String)"S1009", null);
        }
        String hostRemovalGracePeriodAsString = this.localProps.getProperty((String)HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY, (String)"15000");
        try {
            this.hostRemovalGracePeriod = Integer.parseInt((String)hostRemovalGracePeriodAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", (Object[])new Object[]{hostRemovalGracePeriodAsString}), (String)"S1009", null);
        }
        String strategy = this.localProps.getProperty((String)"loadBalanceStrategy", (String)"random");
        this.balancer = "random".equals((Object)strategy) ? (BalanceStrategy)Util.loadExtensions(null, (Properties)props, (String)RandomBalanceStrategy.class.getName(), (String)"InvalidLoadBalanceStrategy", null).get((int)0) : ("bestResponseTime".equals((Object)strategy) ? (BalanceStrategy)Util.loadExtensions(null, (Properties)props, (String)BestResponseTimeBalanceStrategy.class.getName(), (String)"InvalidLoadBalanceStrategy", null).get((int)0) : ("serverAffinity".equals((Object)strategy) ? (BalanceStrategy)Util.loadExtensions(null, (Properties)props, (String)ServerAffinityStrategy.class.getName(), (String)"InvalidLoadBalanceStrategy", null).get((int)0) : (BalanceStrategy)Util.loadExtensions(null, (Properties)props, (String)strategy, (String)"InvalidLoadBalanceStrategy", null).get((int)0)));
        String autoCommitSwapThresholdAsString = props.getProperty((String)"loadBalanceAutoCommitStatementThreshold", (String)"0");
        try {
            this.autoCommitSwapThreshold = Integer.parseInt((String)autoCommitSwapThresholdAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", (Object[])new Object[]{autoCommitSwapThresholdAsString}), (String)"S1009", null);
        }
        String autoCommitSwapRegex = props.getProperty((String)"loadBalanceAutoCommitStatementRegex", (String)"");
        if (!"".equals((Object)autoCommitSwapRegex)) {
            try {
                "".matches((String)autoCommitSwapRegex);
            }
            catch (Exception e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", (Object[])new Object[]{autoCommitSwapRegex}), (String)"S1009", null);
            }
        }
        if (this.autoCommitSwapThreshold > 0) {
            String statementInterceptors = this.localProps.getProperty((String)"statementInterceptors");
            if (statementInterceptors == null) {
                this.localProps.setProperty((String)"statementInterceptors", (String)"com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
            } else if (statementInterceptors.length() > 0) {
                this.localProps.setProperty((String)"statementInterceptors", (String)(statementInterceptors + ",com.mysql.jdbc.LoadBalancedAutoCommitInterceptor"));
            }
            props.setProperty((String)"statementInterceptors", (String)this.localProps.getProperty((String)"statementInterceptors"));
        }
        this.balancer.init(null, (Properties)props);
        String lbExceptionChecker = this.localProps.getProperty((String)"loadBalanceExceptionChecker", (String)"com.mysql.jdbc.StandardLoadBalanceExceptionChecker");
        this.exceptionChecker = (LoadBalanceExceptionChecker)Util.loadExtensions(null, (Properties)props, (String)lbExceptionChecker, (String)"InvalidLoadBalanceExceptionChecker", null).get((int)0);
        this.pickNewConnection();
    }

    @Override
    MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (Util.isJdbc4()) return (MySQLConnection)Util.handleNewInstance(JDBC_4_LB_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
        if (JDBC_4_LB_CONNECTION_CTOR == null) return new LoadBalancedMySQLConnection((LoadBalancedConnectionProxy)this);
        return (MySQLConnection)Util.handleNewInstance(JDBC_4_LB_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
    }

    @Override
    protected void propagateProxyDown(MySQLConnection proxyConn) {
        Iterator<ConnectionImpl> i$ = this.liveConnections.values().iterator();
        while (i$.hasNext()) {
            ConnectionImpl c = i$.next();
            c.setProxy((MySQLConnection)proxyConn);
        }
    }

    @Override
    boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        if (!(t instanceof SQLException)) return false;
        if (!this.exceptionChecker.shouldExceptionTriggerFailover((SQLException)((SQLException)t))) return false;
        return true;
    }

    @Override
    boolean isMasterConnection() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    synchronized void invalidateConnection(MySQLConnection conn) throws SQLException {
        super.invalidateConnection((MySQLConnection)conn);
        if (this.isGlobalBlacklistEnabled()) {
            this.addToGlobalBlacklist((String)this.connectionsToHostsMap.get((Object)conn));
        }
        this.liveConnections.remove((Object)this.connectionsToHostsMap.get((Object)conn));
        String mappedHost = this.connectionsToHostsMap.remove((Object)conn);
        if (mappedHost == null) return;
        if (!this.hostsToListIndexMap.containsKey((Object)mappedHost)) return;
        int hostIndex = this.hostsToListIndexMap.get((Object)mappedHost).intValue();
        long[] arrl = this.responseTimes;
        // MONITORENTER : arrl
        this.responseTimes[hostIndex] = 0L;
        // MONITOREXIT : arrl
        return;
    }

    @Override
    synchronized void pickNewConnection() throws SQLException {
        if (this.isClosed && this.closedExplicitly) {
            return;
        }
        if (this.currentConnection == null) {
            this.currentConnection = this.balancer.pickConnection((LoadBalancedConnectionProxy)this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])((long[])this.responseTimes.clone()), (int)this.retriesAllDown);
            return;
        }
        if (this.currentConnection.isClosed()) {
            this.invalidateCurrentConnection();
        }
        int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
        boolean pingBeforeReturn = this.currentConnection.getLoadBalanceValidateConnectionOnSwapServer();
        int hostsTried = 0;
        int hostsToTry = this.hostList.size();
        do {
            if (hostsTried >= hostsToTry) {
                this.isClosed = true;
                this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
                return;
            }
            ConnectionImpl newConn = null;
            try {
                newConn = this.balancer.pickConnection((LoadBalancedConnectionProxy)this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])((long[])this.responseTimes.clone()), (int)this.retriesAllDown);
                if (this.currentConnection != null) {
                    if (pingBeforeReturn) {
                        if (pingTimeout == 0) {
                            newConn.ping();
                        } else {
                            newConn.pingInternal((boolean)true, (int)pingTimeout);
                        }
                    }
                    this.syncSessionState((Connection)this.currentConnection, (Connection)newConn);
                }
                this.currentConnection = newConn;
                return;
            }
            catch (SQLException e) {
                if (this.shouldExceptionTriggerConnectionSwitch((Throwable)e) && newConn != null) {
                    this.invalidateConnection(newConn);
                }
                ++hostsTried;
                continue;
            }
            break;
        } while (true);
    }

    @Override
    public synchronized ConnectionImpl createConnectionForHost(String hostPortSpec) throws SQLException {
        StatementInterceptorV2 stmtInterceptor;
        ConnectionImpl conn = super.createConnectionForHost((String)hostPortSpec);
        this.liveConnections.put((String)hostPortSpec, (ConnectionImpl)conn);
        this.connectionsToHostsMap.put((ConnectionImpl)conn, (String)hostPortSpec);
        ++this.totalPhysicalConnections;
        Iterator<StatementInterceptorV2> i$ = conn.getStatementInterceptorsInstances().iterator();
        do {
            if (!i$.hasNext()) return conn;
        } while (!((stmtInterceptor = i$.next()) instanceof LoadBalancedAutoCommitInterceptor));
        ((LoadBalancedAutoCommitInterceptor)stmtInterceptor).resumeCounters();
        return conn;
    }

    @Override
    void syncSessionState(Connection source, Connection target, boolean readOnly) throws SQLException {
        LoadBalancedAutoCommitInterceptor lbAutoCommitStmtInterceptor = null;
        for (StatementInterceptorV2 stmtInterceptor : ((MySQLConnection)target).getStatementInterceptorsInstances()) {
            if (!(stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor)) continue;
            lbAutoCommitStmtInterceptor = (LoadBalancedAutoCommitInterceptor)stmtInterceptor;
            lbAutoCommitStmtInterceptor.pauseCounters();
            break;
        }
        super.syncSessionState((Connection)source, (Connection)target, (boolean)readOnly);
        if (lbAutoCommitStmtInterceptor == null) return;
        lbAutoCommitStmtInterceptor.resumeCounters();
    }

    private synchronized void closeAllConnections() {
        for (ConnectionImpl c : this.liveConnections.values()) {
            try {
                c.close();
            }
            catch (SQLException e) {}
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            if (this.connectionGroup != null) {
                this.connectionGroup.closeConnectionProxy((LoadBalancedConnectionProxy)this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    @Override
    synchronized void doClose() {
        this.closeAllConnections();
    }

    @Override
    synchronized void doAbortInternal() {
        for (ConnectionImpl c : this.liveConnections.values()) {
            try {
                c.abortInternal();
            }
            catch (SQLException e) {}
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            if (this.connectionGroup != null) {
                this.connectionGroup.closeConnectionProxy((LoadBalancedConnectionProxy)this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    @Override
    synchronized void doAbort(Executor executor) {
        for (ConnectionImpl c : this.liveConnections.values()) {
            try {
                c.abort((Executor)executor);
            }
            catch (SQLException e) {}
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            if (this.connectionGroup != null) {
                this.connectionGroup.closeConnectionProxy((LoadBalancedConnectionProxy)this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    public synchronized Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (this.isClosed && !this.allowedOnClosedConnection((Method)method) && method.getExceptionTypes().length > 0) {
            if (this.autoReconnect && !this.closedExplicitly) {
                this.currentConnection = null;
                this.pickNewConnection();
                this.isClosed = false;
                this.closedReason = null;
            } else {
                String reason = "No operations allowed after connection closed.";
                if (this.closedReason == null) throw SQLError.createSQLException((String)reason, (String)"08003", null);
                reason = reason + " " + this.closedReason;
                throw SQLError.createSQLException((String)reason, (String)"08003", null);
            }
        }
        if (!this.inTransaction) {
            this.inTransaction = true;
            this.transactionStartTime = System.nanoTime();
            ++this.transactionCount;
        }
        Object result = null;
        try {
            block22 : {
                try {
                    result = method.invoke((Object)this.thisAsConnection, (Object[])args);
                    if (result == null) break block22;
                    if (result instanceof Statement) {
                        ((Statement)result).setPingTarget((PingTarget)this);
                    }
                    result = this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), (Object)result);
                }
                catch (InvocationTargetException e) {
                    this.dealWithInvocationException((InvocationTargetException)e);
                    Object var8_8 = null;
                    if (!"commit".equals((Object)methodName)) {
                        if (!"rollback".equals((Object)methodName)) return result;
                    }
                    this.inTransaction = false;
                    String host = this.connectionsToHostsMap.get((Object)this.currentConnection);
                    if (host != null) {
                        long[] arrl = this.responseTimes;
                        // MONITORENTER : arrl
                        Integer hostIndex = this.hostsToListIndexMap.get((Object)host);
                        if (hostIndex != null && hostIndex.intValue() < this.responseTimes.length) {
                            this.responseTimes[hostIndex.intValue()] = System.nanoTime() - this.transactionStartTime;
                        }
                        // MONITOREXIT : arrl
                    }
                    this.pickNewConnection();
                    return result;
                }
            }
            Object var8_7 = null;
            if (!"commit".equals((Object)methodName)) {
                if (!"rollback".equals((Object)methodName)) return result;
            }
            this.inTransaction = false;
            String host = this.connectionsToHostsMap.get((Object)this.currentConnection);
            if (host != null) {
                long[] arrl = this.responseTimes;
                // MONITORENTER : arrl
                Integer hostIndex = this.hostsToListIndexMap.get((Object)host);
                if (hostIndex != null && hostIndex.intValue() < this.responseTimes.length) {
                    this.responseTimes[hostIndex.intValue()] = System.nanoTime() - this.transactionStartTime;
                }
                // MONITOREXIT : arrl
            }
            this.pickNewConnection();
            return result;
        }
        catch (Throwable throwable) {
            Object var8_9 = null;
            if (!"commit".equals((Object)methodName)) {
                if (!"rollback".equals((Object)methodName)) throw throwable;
            }
            this.inTransaction = false;
            String host = this.connectionsToHostsMap.get((Object)this.currentConnection);
            if (host != null) {
                long[] arrl = this.responseTimes;
                // MONITORENTER : arrl
                Integer hostIndex = this.hostsToListIndexMap.get((Object)host);
                if (hostIndex != null && hostIndex.intValue() < this.responseTimes.length) {
                    this.responseTimes[hostIndex.intValue()] = System.nanoTime() - this.transactionStartTime;
                }
                // MONITOREXIT : arrl
            }
            this.pickNewConnection();
            throw throwable;
        }
    }

    @Override
    public synchronized void doPing() throws SQLException {
        SQLException se = null;
        boolean foundHost = false;
        int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
        for (String host : this.hostList) {
            ConnectionImpl conn = this.liveConnections.get((Object)host);
            if (conn == null) continue;
            try {
                if (pingTimeout == 0) {
                    conn.ping();
                } else {
                    conn.pingInternal((boolean)true, (int)pingTimeout);
                }
                foundHost = true;
            }
            catch (SQLException e) {
                if (host.equals((Object)this.connectionsToHostsMap.get((Object)this.currentConnection))) {
                    this.closeAllConnections();
                    this.isClosed = true;
                    this.closedReason = "Connection closed because ping of current connection failed.";
                    throw e;
                }
                if (e.getMessage().equals((Object)Messages.getString((String)"Connection.exceededConnectionLifetime"))) {
                    if (se == null) {
                        se = e;
                    }
                } else {
                    se = e;
                    if (this.isGlobalBlacklistEnabled()) {
                        this.addToGlobalBlacklist((String)host);
                    }
                }
                this.liveConnections.remove((Object)this.connectionsToHostsMap.get((Object)conn));
            }
        }
        if (foundHost) return;
        this.closeAllConnections();
        this.isClosed = true;
        this.closedReason = "Connection closed due to inability to ping any active connections.";
        if (se != null) {
            throw se;
        }
        ((ConnectionImpl)this.currentConnection).throwConnectionClosedException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToGlobalBlacklist(String host, long timeout) {
        if (!this.isGlobalBlacklistEnabled()) return;
        Map<String, Long> map = globalBlacklist;
        // MONITORENTER : map
        globalBlacklist.put((String)host, (Long)Long.valueOf((long)timeout));
        // MONITOREXIT : map
        return;
    }

    public void addToGlobalBlacklist(String host) {
        this.addToGlobalBlacklist((String)host, (long)(System.currentTimeMillis() + (long)this.globalBlacklistTimeout));
    }

    public boolean isGlobalBlacklistEnabled() {
        if (this.globalBlacklistTimeout <= 0) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Map<String, Long> getGlobalBlacklist() {
        if (!this.isGlobalBlacklistEnabled()) {
            if (this.hostsToRemove.isEmpty()) {
                return new HashMap<String, Long>((int)1);
            }
            HashMap<String, Long> fakedBlacklist = new HashMap<String, Long>();
            Iterator<String> i$ = this.hostsToRemove.iterator();
            while (i$.hasNext()) {
                String h = i$.next();
                fakedBlacklist.put((String)h, (Long)Long.valueOf((long)(System.currentTimeMillis() + 5000L)));
            }
            return fakedBlacklist;
        }
        HashMap<String, Long> blacklistClone = new HashMap<String, Long>((int)globalBlacklist.size());
        Map<String, Long> i$ = globalBlacklist;
        // MONITORENTER : i$
        blacklistClone.putAll(globalBlacklist);
        // MONITOREXIT : i$
        Set<K> keys = blacklistClone.keySet();
        keys.retainAll(this.hostList);
        Iterator<K> i = keys.iterator();
        do {
            if (!i.hasNext()) {
                if (keys.size() != this.hostList.size()) return blacklistClone;
                return new HashMap<String, Long>((int)1);
            }
            String host = (String)i.next();
            Long timeout = globalBlacklist.get((Object)host);
            if (timeout == null || timeout.longValue() >= System.currentTimeMillis()) continue;
            Map<String, Long> map = globalBlacklist;
            // MONITORENTER : map
            globalBlacklist.remove((Object)host);
            // MONITOREXIT : map
            i.remove();
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeHostWhenNotInUse(String hostPortPair) throws SQLException {
        if (this.hostRemovalGracePeriod <= 0) {
            this.removeHost((String)hostPortPair);
            return;
        }
        int timeBetweenChecks = this.hostRemovalGracePeriod > 1000 ? 1000 : this.hostRemovalGracePeriod;
        LoadBalancedConnectionProxy loadBalancedConnectionProxy = this;
        // MONITORENTER : loadBalancedConnectionProxy
        this.addToGlobalBlacklist((String)hostPortPair, (long)(System.currentTimeMillis() + (long)this.hostRemovalGracePeriod + (long)timeBetweenChecks));
        long cur = System.currentTimeMillis();
        while (System.currentTimeMillis() < cur + (long)this.hostRemovalGracePeriod) {
            this.hostsToRemove.add((String)hostPortPair);
            if (!hostPortPair.equals((Object)this.currentConnection.getHostPortPair())) {
                this.removeHost((String)hostPortPair);
                // MONITOREXIT : loadBalancedConnectionProxy
                return;
            }
            try {
                Thread.sleep((long)((long)timeBetweenChecks));
            }
            catch (InterruptedException e) {}
        }
        // MONITOREXIT : loadBalancedConnectionProxy
        this.removeHost((String)hostPortPair);
    }

    public synchronized void removeHost(String hostPortPair) throws SQLException {
        if (this.connectionGroup != null && this.connectionGroup.getInitialHosts().size() == 1 && this.connectionGroup.getInitialHosts().contains((Object)hostPortPair)) {
            throw SQLError.createSQLException((String)"Cannot remove only configured host.", null);
        }
        this.hostsToRemove.add((String)hostPortPair);
        this.connectionsToHostsMap.remove((Object)this.liveConnections.remove((Object)hostPortPair));
        if (this.hostsToListIndexMap.remove((Object)hostPortPair) != null) {
            long[] newResponseTimes = new long[this.responseTimes.length - 1];
            int newIdx = 0;
            for (String h : this.hostList) {
                if (this.hostsToRemove.contains((Object)h)) continue;
                Integer idx = this.hostsToListIndexMap.get((Object)h);
                if (idx != null && idx.intValue() < this.responseTimes.length) {
                    newResponseTimes[newIdx] = this.responseTimes[idx.intValue()];
                }
                this.hostsToListIndexMap.put((String)h, (Integer)Integer.valueOf((int)newIdx++));
            }
            this.responseTimes = newResponseTimes;
        }
        if (!hostPortPair.equals((Object)this.currentConnection.getHostPortPair())) return;
        this.invalidateConnection((MySQLConnection)this.currentConnection);
        this.pickNewConnection();
    }

    public synchronized boolean addHost(String hostPortPair) {
        if (this.hostsToListIndexMap.containsKey((Object)hostPortPair)) {
            return false;
        }
        long[] newResponseTimes = new long[this.responseTimes.length + 1];
        System.arraycopy((Object)this.responseTimes, (int)0, (Object)newResponseTimes, (int)0, (int)this.responseTimes.length);
        this.responseTimes = newResponseTimes;
        if (!this.hostList.contains((Object)hostPortPair)) {
            this.hostList.add(hostPortPair);
        }
        this.hostsToListIndexMap.put((String)hostPortPair, (Integer)Integer.valueOf((int)(this.responseTimes.length - 1)));
        this.hostsToRemove.remove((Object)hostPortPair);
        return true;
    }

    public synchronized boolean inTransaction() {
        return this.inTransaction;
    }

    public synchronized long getTransactionCount() {
        return this.transactionCount;
    }

    public synchronized long getActivePhysicalConnectionCount() {
        return (long)this.liveConnections.size();
    }

    public synchronized long getTotalPhysicalConnectionCount() {
        return this.totalPhysicalConnections;
    }

    public synchronized long getConnectionGroupProxyID() {
        return this.connectionGroupProxyID;
    }

    public synchronized String getCurrentActiveHost() {
        MySQLConnection c = this.currentConnection;
        if (c == null) return null;
        String o = this.connectionsToHostsMap.get((Object)c);
        if (o == null) return null;
        return o.toString();
    }

    public synchronized long getCurrentTransactionDuration() {
        if (!this.inTransaction) return 0L;
        if (this.transactionStartTime <= 0L) return 0L;
        return System.nanoTime() - this.transactionStartTime;
    }

    static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
        if (nullLBConnectionInstance != null) return nullLBConnectionInstance;
        nullLBConnectionInstance = (LoadBalancedConnection)Proxy.newProxyInstance((ClassLoader)LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, (InvocationHandler)new NullLoadBalancedConnectionProxy());
        return nullLBConnectionInstance;
    }

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_LB_CONNECTION_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4LoadBalancedMySQLConnection").getConstructor(LoadBalancedConnectionProxy.class);
                INTERFACES_TO_PROXY = new Class[]{LoadBalancedConnection.class, Class.forName((String)"com.mysql.jdbc.JDBC4MySQLConnection")};
            }
            catch (SecurityException e) {
                throw new RuntimeException((Throwable)e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException((Throwable)e);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException((Throwable)e);
            }
        } else {
            INTERFACES_TO_PROXY = new Class[]{LoadBalancedConnection.class};
        }
        nullLBConnectionInstance = null;
    }
}

