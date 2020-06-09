/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MultiHostMySQLConnection;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
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
public abstract class MultiHostConnectionProxy
implements InvocationHandler {
    private static final String METHOD_GET_MULTI_HOST_SAFE_PROXY = "getMultiHostSafeProxy";
    private static final String METHOD_EQUALS = "equals";
    private static final String METHOD_HASH_CODE = "hashCode";
    private static final String METHOD_CLOSE = "close";
    private static final String METHOD_ABORT_INTERNAL = "abortInternal";
    private static final String METHOD_ABORT = "abort";
    private static final String METHOD_IS_CLOSED = "isClosed";
    private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
    private static final String METHOD_GET_CATALOG = "getCatalog";
    private static final String METHOD_GET_TRANSACTION_ISOLATION = "getTransactionIsolation";
    private static final String METHOD_GET_SESSION_MAX_ROWS = "getSessionMaxRows";
    List<String> hostList;
    Properties localProps;
    boolean autoReconnect = false;
    MySQLConnection thisAsConnection = this.getNewWrapperForThisAsConnection();
    MySQLConnection proxyConnection = null;
    MySQLConnection currentConnection = null;
    boolean isClosed = false;
    boolean closedExplicitly = false;
    String closedReason = null;
    protected Throwable lastExceptionDealtWith = null;
    private static Constructor<?> JDBC_4_MS_CONNECTION_CTOR;

    MultiHostConnectionProxy() throws SQLException {
    }

    MultiHostConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        this();
        this.initializeHostsSpecs(hosts, (Properties)props);
    }

    int initializeHostsSpecs(List<String> hosts, Properties props) {
        this.autoReconnect = "true".equalsIgnoreCase((String)props.getProperty((String)"autoReconnect")) || "true".equalsIgnoreCase((String)props.getProperty((String)"autoReconnectForPools"));
        this.hostList = hosts;
        int numHosts = this.hostList.size();
        this.localProps = (Properties)props.clone();
        this.localProps.remove((Object)"HOST");
        this.localProps.remove((Object)"PORT");
        int i = 0;
        do {
            if (i >= numHosts) {
                this.localProps.remove((Object)"NUM_HOSTS");
                return numHosts;
            }
            this.localProps.remove((Object)("HOST." + (i + 1)));
            this.localProps.remove((Object)("PORT." + (i + 1)));
            ++i;
        } while (true);
    }

    MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (Util.isJdbc4()) return (MySQLConnection)Util.handleNewInstance(JDBC_4_MS_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
        if (JDBC_4_MS_CONNECTION_CTOR == null) return new MultiHostMySQLConnection((MultiHostConnectionProxy)this);
        return (MySQLConnection)Util.handleNewInstance(JDBC_4_MS_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
    }

    protected MySQLConnection getProxy() {
        MySQLConnection mySQLConnection;
        if (this.proxyConnection != null) {
            mySQLConnection = this.proxyConnection;
            return mySQLConnection;
        }
        mySQLConnection = this.thisAsConnection;
        return mySQLConnection;
    }

    protected final void setProxy(MySQLConnection proxyConn) {
        this.proxyConnection = proxyConn;
        this.propagateProxyDown((MySQLConnection)proxyConn);
    }

    protected void propagateProxyDown(MySQLConnection proxyConn) {
        this.currentConnection.setProxy((MySQLConnection)proxyConn);
    }

    Object proxyIfReturnTypeIsJdbcInterface(Class<?> returnType, Object toProxy) {
        if (toProxy == null) return toProxy;
        if (!Util.isJdbcInterface(returnType)) return toProxy;
        Class<?> toProxyClass = toProxy.getClass();
        return Proxy.newProxyInstance((ClassLoader)toProxyClass.getClassLoader(), Util.getImplementedInterfaces(toProxyClass), (InvocationHandler)this.getNewJdbcInterfaceProxy((Object)toProxy));
    }

    InvocationHandler getNewJdbcInterfaceProxy(Object toProxy) {
        return new JdbcInterfaceProxy((MultiHostConnectionProxy)this, (Object)toProxy);
    }

    void dealWithInvocationException(InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
        Throwable t = e.getTargetException();
        if (t == null) throw e;
        if (this.lastExceptionDealtWith == t) throw t;
        if (!this.shouldExceptionTriggerConnectionSwitch((Throwable)t)) throw t;
        this.invalidateCurrentConnection();
        this.pickNewConnection();
        this.lastExceptionDealtWith = t;
        throw t;
    }

    abstract boolean shouldExceptionTriggerConnectionSwitch(Throwable var1);

    abstract boolean isMasterConnection();

    synchronized void invalidateCurrentConnection() throws SQLException {
        this.invalidateConnection((MySQLConnection)this.currentConnection);
    }

    synchronized void invalidateConnection(MySQLConnection conn) throws SQLException {
        try {
            if (conn == null) return;
            if (conn.isClosed()) return;
            conn.realClose((boolean)true, (boolean)(!conn.getAutoCommit()), (boolean)true, null);
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    abstract void pickNewConnection() throws SQLException;

    synchronized ConnectionImpl createConnectionForHost(String hostPortSpec) throws SQLException {
        Properties connProps = (Properties)this.localProps.clone();
        String[] hostPortPair = NonRegisteringDriver.parseHostPortPair((String)hostPortSpec);
        String hostName = hostPortPair[0];
        String portNumber = hostPortPair[1];
        String dbName = connProps.getProperty((String)"DBNAME");
        if (hostName == null) {
            throw new SQLException((String)"Could not find a hostname to start a connection to");
        }
        if (portNumber == null) {
            portNumber = "3306";
        }
        connProps.setProperty((String)"HOST", (String)hostName);
        connProps.setProperty((String)"PORT", (String)portNumber);
        connProps.setProperty((String)"HOST.1", (String)hostName);
        connProps.setProperty((String)"PORT.1", (String)portNumber);
        connProps.setProperty((String)"NUM_HOSTS", (String)"1");
        connProps.setProperty((String)"roundRobinLoadBalance", (String)"false");
        ConnectionImpl conn = (ConnectionImpl)ConnectionImpl.getInstance((String)hostName, (int)Integer.parseInt((String)portNumber), (Properties)connProps, (String)dbName, (String)("jdbc:mysql://" + hostName + ":" + portNumber + "/"));
        conn.setProxy((MySQLConnection)this.getProxy());
        return conn;
    }

    void syncSessionState(Connection source, Connection target) throws SQLException {
        if (source == null) return;
        if (target == null) {
            return;
        }
        boolean prevUseLocalSessionState = source.getUseLocalSessionState();
        source.setUseLocalSessionState((boolean)true);
        boolean readOnly = source.isReadOnly();
        source.setUseLocalSessionState((boolean)prevUseLocalSessionState);
        this.syncSessionState((Connection)source, (Connection)target, (boolean)readOnly);
    }

    void syncSessionState(Connection source, Connection target, boolean readOnly) throws SQLException {
        if (target != null) {
            target.setReadOnly((boolean)readOnly);
        }
        if (source == null) return;
        if (target == null) {
            return;
        }
        boolean prevUseLocalSessionState = source.getUseLocalSessionState();
        source.setUseLocalSessionState((boolean)true);
        target.setAutoCommit((boolean)source.getAutoCommit());
        target.setCatalog((String)source.getCatalog());
        target.setTransactionIsolation((int)source.getTransactionIsolation());
        target.setSessionMaxRows((int)source.getSessionMaxRows());
        source.setUseLocalSessionState((boolean)prevUseLocalSessionState);
    }

    abstract void doClose() throws SQLException;

    abstract void doAbortInternal() throws SQLException;

    abstract void doAbort(Executor var1) throws SQLException;

    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("getMultiHostSafeProxy".equals((Object)methodName)) {
            return this.thisAsConnection;
        }
        if ("equals".equals((Object)methodName)) {
            return Boolean.valueOf((boolean)args[0].equals((Object)this));
        }
        if ("hashCode".equals((Object)methodName)) {
            return Integer.valueOf((int)this.hashCode());
        }
        if ("close".equals((Object)methodName)) {
            this.doClose();
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            this.closedExplicitly = true;
            return null;
        }
        if ("abortInternal".equals((Object)methodName)) {
            this.doAbortInternal();
            this.currentConnection.abortInternal();
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            return null;
        }
        if ("abort".equals((Object)methodName) && args.length == 1) {
            this.doAbort((Executor)((Executor)args[0]));
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            return null;
        }
        if ("isClosed".equals((Object)methodName)) {
            return Boolean.valueOf((boolean)this.isClosed);
        }
        try {
            return this.invokeMore((Object)proxy, (Method)method, (Object[])args);
        }
        catch (InvocationTargetException e) {
            Throwable throwable;
            if (e.getCause() != null) {
                throwable = e.getCause();
                throw throwable;
            }
            throwable = e;
            throw throwable;
        }
        catch (Exception e) {
            Class<?>[] declaredException;
            Class<?>[] arr$ = declaredException = method.getExceptionTypes();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Class<?> declEx = arr$[i$];
                if (declEx.isAssignableFrom(e.getClass())) {
                    throw e;
                }
                ++i$;
            }
            throw new IllegalStateException((String)e.getMessage(), (Throwable)e);
        }
    }

    abstract Object invokeMore(Object var1, Method var2, Object[] var3) throws Throwable;

    protected boolean allowedOnClosedConnection(Method method) {
        String methodName = method.getName();
        if (methodName.equals((Object)"getAutoCommit")) return true;
        if (methodName.equals((Object)"getCatalog")) return true;
        if (methodName.equals((Object)"getTransactionIsolation")) return true;
        if (methodName.equals((Object)"getSessionMaxRows")) return true;
        return false;
    }

    static {
        if (!Util.isJdbc4()) return;
        try {
            JDBC_4_MS_CONNECTION_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4MultiHostMySQLConnection").getConstructor(MultiHostConnectionProxy.class);
            return;
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
    }
}

