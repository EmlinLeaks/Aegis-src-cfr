/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.FailoverConnectionProxy;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FailoverConnectionProxy
extends MultiHostConnectionProxy {
    private static final String METHOD_SET_READ_ONLY = "setReadOnly";
    private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
    private static final String METHOD_COMMIT = "commit";
    private static final String METHOD_ROLLBACK = "rollback";
    private static final int NO_CONNECTION_INDEX = -1;
    private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
    private int secondsBeforeRetryPrimaryHost;
    private long queriesBeforeRetryPrimaryHost;
    private boolean failoverReadOnly;
    private int retriesAllDown;
    private int currentHostIndex = -1;
    private int primaryHostIndex = 0;
    private Boolean explicitlyReadOnly = null;
    private boolean explicitlyAutoCommit = true;
    private boolean enableFallBackToPrimaryHost = true;
    private long primaryHostFailTimeMillis = 0L;
    private long queriesIssuedSinceFailover = 0L;
    private static Class<?>[] INTERFACES_TO_PROXY;

    public static Connection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
        FailoverConnectionProxy connProxy = new FailoverConnectionProxy(hosts, (Properties)props);
        return (Connection)Proxy.newProxyInstance((ClassLoader)Connection.class.getClassLoader(), INTERFACES_TO_PROXY, (InvocationHandler)connProxy);
    }

    private FailoverConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        super(hosts, (Properties)props);
        ConnectionPropertiesImpl connProps = new ConnectionPropertiesImpl();
        connProps.initializeProperties((Properties)props);
        this.secondsBeforeRetryPrimaryHost = connProps.getSecondsBeforeRetryMaster();
        this.queriesBeforeRetryPrimaryHost = (long)connProps.getQueriesBeforeRetryMaster();
        this.failoverReadOnly = connProps.getFailOverReadOnly();
        this.retriesAllDown = connProps.getRetriesAllDown();
        this.enableFallBackToPrimaryHost = this.secondsBeforeRetryPrimaryHost > 0 || this.queriesBeforeRetryPrimaryHost > 0L;
        this.pickNewConnection();
        this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
    }

    @Override
    MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
        return new FailoverJdbcInterfaceProxy((FailoverConnectionProxy)this, (Object)toProxy);
    }

    @Override
    boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        if (!(t instanceof SQLException)) {
            return false;
        }
        String sqlState = ((SQLException)t).getSQLState();
        if (sqlState != null && sqlState.startsWith((String)"08")) {
            return true;
        }
        if (!(t instanceof CommunicationsException)) return false;
        return true;
    }

    @Override
    boolean isMasterConnection() {
        return this.connectedToPrimaryHost();
    }

    @Override
    synchronized void pickNewConnection() throws SQLException {
        if (this.isClosed && this.closedExplicitly) {
            return;
        }
        if (this.isConnected() && !this.readyToFallBackToPrimaryHost()) {
            this.failOver();
            return;
        }
        try {
            this.connectTo((int)this.primaryHostIndex);
            return;
        }
        catch (SQLException e) {
            this.resetAutoFallBackCounters();
            this.failOver((int)this.primaryHostIndex);
            return;
        }
    }

    synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex) throws SQLException {
        return this.createConnectionForHost((String)((String)this.hostList.get((int)hostIndex)));
    }

    private synchronized void connectTo(int hostIndex) throws SQLException {
        try {
            this.switchCurrentConnectionTo((int)hostIndex, (MySQLConnection)this.createConnectionForHostIndex((int)hostIndex));
            return;
        }
        catch (SQLException e) {
            if (this.currentConnection == null) throw e;
            StringBuilder msg = new StringBuilder((String)"Connection to ").append((String)(this.isPrimaryHostIndex((int)hostIndex) ? "primary" : "secondary")).append((String)" host '").append((String)((String)this.hostList.get((int)hostIndex))).append((String)"' failed");
            this.currentConnection.getLog().logWarn((Object)msg.toString(), (Throwable)e);
            throw e;
        }
    }

    private synchronized void switchCurrentConnectionTo(int hostIndex, MySQLConnection connection) throws SQLException {
        this.invalidateCurrentConnection();
        boolean readOnly = this.isPrimaryHostIndex((int)hostIndex) ? (this.explicitlyReadOnly == null ? false : this.explicitlyReadOnly.booleanValue()) : (this.failoverReadOnly ? true : (this.explicitlyReadOnly != null ? this.explicitlyReadOnly.booleanValue() : (this.currentConnection != null ? this.currentConnection.isReadOnly() : false)));
        this.syncSessionState((Connection)this.currentConnection, (Connection)connection, (boolean)readOnly);
        this.currentConnection = connection;
        this.currentHostIndex = hostIndex;
    }

    private synchronized void failOver() throws SQLException {
        this.failOver((int)this.currentHostIndex);
    }

    private synchronized void failOver(int failedHostIdx) throws SQLException {
        int nextHostIndex;
        int prevHostIndex = this.currentHostIndex;
        int firstHostIndexTried = nextHostIndex = this.nextHost((int)failedHostIdx, (boolean)false);
        SQLException lastExceptionCaught = null;
        int attempts = 0;
        boolean gotConnection = false;
        boolean firstConnOrPassedByPrimaryHost = prevHostIndex == -1 || this.isPrimaryHostIndex((int)prevHostIndex);
        do {
            try {
                firstConnOrPassedByPrimaryHost = firstConnOrPassedByPrimaryHost || this.isPrimaryHostIndex((int)nextHostIndex);
                this.connectTo((int)nextHostIndex);
                if (firstConnOrPassedByPrimaryHost && this.connectedToSecondaryHost()) {
                    this.resetAutoFallBackCounters();
                }
                gotConnection = true;
            }
            catch (SQLException e) {
                lastExceptionCaught = e;
                if (!this.shouldExceptionTriggerConnectionSwitch((Throwable)e)) throw e;
                int newNextHostIndex = this.nextHost((int)nextHostIndex, (boolean)(attempts > 0));
                if (newNextHostIndex == firstHostIndexTried) {
                    int n = newNextHostIndex;
                    newNextHostIndex = this.nextHost((int)nextHostIndex, (boolean)true);
                    if (n == newNextHostIndex) {
                        ++attempts;
                        try {
                            Thread.sleep((long)250L);
                        }
                        catch (InterruptedException ie) {
                            // empty catch block
                        }
                    }
                }
                nextHostIndex = newNextHostIndex;
            }
        } while (attempts < this.retriesAllDown && !gotConnection);
        if (gotConnection) return;
        throw lastExceptionCaught;
    }

    synchronized void fallBackToPrimaryIfAvailable() {
        ConnectionImpl connection = null;
        try {
            connection = this.createConnectionForHostIndex((int)this.primaryHostIndex);
            this.switchCurrentConnectionTo((int)this.primaryHostIndex, (MySQLConnection)connection);
            return;
        }
        catch (SQLException e1) {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e2) {
                    // empty catch block
                }
            }
            this.resetAutoFallBackCounters();
        }
    }

    private int nextHost(int currHostIdx, boolean vouchForPrimaryHost) {
        int nextHostIdx = (currHostIdx + 1) % this.hostList.size();
        if (!this.isPrimaryHostIndex((int)nextHostIdx)) return nextHostIdx;
        if (!this.isConnected()) return nextHostIdx;
        if (vouchForPrimaryHost) return nextHostIdx;
        if (!this.enableFallBackToPrimaryHost) return nextHostIdx;
        if (this.readyToFallBackToPrimaryHost()) return nextHostIdx;
        return this.nextHost((int)nextHostIdx, (boolean)vouchForPrimaryHost);
    }

    synchronized void incrementQueriesIssuedSinceFailover() {
        ++this.queriesIssuedSinceFailover;
    }

    synchronized boolean readyToFallBackToPrimaryHost() {
        if (!this.enableFallBackToPrimaryHost) return false;
        if (!this.connectedToSecondaryHost()) return false;
        if (this.secondsBeforeRetryPrimaryHostIsMet()) return true;
        if (!this.queriesBeforeRetryPrimaryHostIsMet()) return false;
        return true;
    }

    synchronized boolean isConnected() {
        if (this.currentHostIndex == -1) return false;
        return true;
    }

    synchronized boolean isPrimaryHostIndex(int hostIndex) {
        if (hostIndex != this.primaryHostIndex) return false;
        return true;
    }

    synchronized boolean connectedToPrimaryHost() {
        return this.isPrimaryHostIndex((int)this.currentHostIndex);
    }

    synchronized boolean connectedToSecondaryHost() {
        if (this.currentHostIndex < 0) return false;
        if (this.isPrimaryHostIndex((int)this.currentHostIndex)) return false;
        return true;
    }

    private synchronized boolean secondsBeforeRetryPrimaryHostIsMet() {
        if (this.secondsBeforeRetryPrimaryHost <= 0) return false;
        if (Util.secondsSinceMillis((long)this.primaryHostFailTimeMillis) < (long)this.secondsBeforeRetryPrimaryHost) return false;
        return true;
    }

    private synchronized boolean queriesBeforeRetryPrimaryHostIsMet() {
        if (this.queriesBeforeRetryPrimaryHost <= 0L) return false;
        if (this.queriesIssuedSinceFailover < this.queriesBeforeRetryPrimaryHost) return false;
        return true;
    }

    private synchronized void resetAutoFallBackCounters() {
        this.primaryHostFailTimeMillis = System.currentTimeMillis();
        this.queriesIssuedSinceFailover = 0L;
    }

    @Override
    synchronized void doClose() throws SQLException {
        this.currentConnection.close();
    }

    @Override
    synchronized void doAbortInternal() throws SQLException {
        this.currentConnection.abortInternal();
    }

    @Override
    synchronized void doAbort(Executor executor) throws SQLException {
        this.currentConnection.abort((Executor)executor);
    }

    @Override
    public synchronized Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (METHOD_SET_READ_ONLY.equals((Object)methodName)) {
            this.explicitlyReadOnly = (Boolean)args[0];
            if (this.failoverReadOnly && this.connectedToSecondaryHost()) {
                return null;
            }
        }
        if (this.isClosed && !this.allowedOnClosedConnection((Method)method)) {
            if (this.autoReconnect && !this.closedExplicitly) {
                this.currentHostIndex = -1;
                this.pickNewConnection();
                this.isClosed = false;
                this.closedReason = null;
            } else {
                String reason = "No operations allowed after connection closed.";
                if (this.closedReason == null) throw SQLError.createSQLException((String)reason, (String)"08003", null);
                reason = reason + "  " + this.closedReason;
                throw SQLError.createSQLException((String)reason, (String)"08003", null);
            }
        }
        Object result = null;
        try {
            result = method.invoke((Object)this.thisAsConnection, (Object[])args);
            result = this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), (Object)result);
        }
        catch (InvocationTargetException e) {
            this.dealWithInvocationException((InvocationTargetException)e);
        }
        if (METHOD_SET_AUTO_COMMIT.equals((Object)methodName)) {
            this.explicitlyAutoCommit = ((Boolean)args[0]).booleanValue();
        }
        if (!this.explicitlyAutoCommit && !METHOD_COMMIT.equals((Object)methodName)) {
            if (!METHOD_ROLLBACK.equals((Object)methodName)) return result;
        }
        if (!this.readyToFallBackToPrimaryHost()) return result;
        this.fallBackToPrimaryIfAvailable();
        return result;
    }

    static /* synthetic */ boolean access$000(FailoverConnectionProxy x0) {
        return x0.explicitlyAutoCommit;
    }

    static {
        if (!Util.isJdbc4()) {
            INTERFACES_TO_PROXY = new Class[]{MySQLConnection.class};
            return;
        }
        try {
            INTERFACES_TO_PROXY = new Class[]{Class.forName((String)"com.mysql.jdbc.JDBC4MySQLConnection")};
            return;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

