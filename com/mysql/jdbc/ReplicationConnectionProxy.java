/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.LoadBalancedConnection;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.PingTarget;
import com.mysql.jdbc.ReplicationConnection;
import com.mysql.jdbc.ReplicationConnectionGroup;
import com.mysql.jdbc.ReplicationConnectionGroupManager;
import com.mysql.jdbc.ReplicationMySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReplicationConnectionProxy
extends MultiHostConnectionProxy
implements PingTarget {
    private ReplicationConnection thisAsReplicationConnection;
    private NonRegisteringDriver driver;
    protected boolean enableJMX;
    protected boolean allowMasterDownConnections;
    protected boolean allowSlaveDownConnections;
    protected boolean readFromMasterWhenNoSlaves;
    protected boolean readFromMasterWhenNoSlavesOriginal;
    protected boolean readOnly;
    ReplicationConnectionGroup connectionGroup;
    private long connectionGroupID;
    private List<String> masterHosts;
    private Properties masterProperties;
    protected LoadBalancedConnection masterConnection;
    private List<String> slaveHosts;
    private Properties slaveProperties;
    protected LoadBalancedConnection slavesConnection;
    private static Constructor<?> JDBC_4_REPL_CONNECTION_CTOR;
    private static Class<?>[] INTERFACES_TO_PROXY;

    public static ReplicationConnection createProxyInstance(List<String> masterHostList, Properties masterProperties, List<String> slaveHostList, Properties slaveProperties) throws SQLException {
        ReplicationConnectionProxy connProxy = new ReplicationConnectionProxy(masterHostList, (Properties)masterProperties, slaveHostList, (Properties)slaveProperties);
        return (ReplicationConnection)Proxy.newProxyInstance((ClassLoader)ReplicationConnection.class.getClassLoader(), INTERFACES_TO_PROXY, (InvocationHandler)connProxy);
    }

    private ReplicationConnectionProxy(List<String> masterHostList, Properties masterProperties, List<String> slaveHostList, Properties slaveProperties) throws SQLException {
        block17 : {
            this.enableJMX = false;
            this.allowMasterDownConnections = false;
            this.allowSlaveDownConnections = false;
            this.readFromMasterWhenNoSlaves = false;
            this.readFromMasterWhenNoSlavesOriginal = false;
            this.readOnly = false;
            this.connectionGroupID = -1L;
            this.thisAsReplicationConnection = (ReplicationConnection)this.thisAsConnection;
            String enableJMXAsString = masterProperties.getProperty((String)"replicationEnableJMX", (String)"false");
            try {
                this.enableJMX = Boolean.parseBoolean((String)enableJMXAsString);
            }
            catch (Exception e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.badValueForReplicationEnableJMX", (Object[])new Object[]{enableJMXAsString}), (String)"S1009", null);
            }
            String allowMasterDownConnectionsAsString = masterProperties.getProperty((String)"allowMasterDownConnections", (String)"false");
            try {
                this.allowMasterDownConnections = Boolean.parseBoolean((String)allowMasterDownConnectionsAsString);
            }
            catch (Exception e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.badValueForAllowMasterDownConnections", (Object[])new Object[]{allowMasterDownConnectionsAsString}), (String)"S1009", null);
            }
            String allowSlaveDownConnectionsAsString = masterProperties.getProperty((String)"allowSlaveDownConnections", (String)"false");
            try {
                this.allowSlaveDownConnections = Boolean.parseBoolean((String)allowSlaveDownConnectionsAsString);
            }
            catch (Exception e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.badValueForAllowSlaveDownConnections", (Object[])new Object[]{allowSlaveDownConnectionsAsString}), (String)"S1009", null);
            }
            String readFromMasterWhenNoSlavesAsString = masterProperties.getProperty((String)"readFromMasterWhenNoSlaves");
            try {
                this.readFromMasterWhenNoSlavesOriginal = Boolean.parseBoolean((String)readFromMasterWhenNoSlavesAsString);
            }
            catch (Exception e) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.badValueForReadFromMasterWhenNoSlaves", (Object[])new Object[]{readFromMasterWhenNoSlavesAsString}), (String)"S1009", null);
            }
            String group = masterProperties.getProperty((String)"replicationConnectionGroup", null);
            if (group != null) {
                this.connectionGroup = ReplicationConnectionGroupManager.getConnectionGroupInstance((String)group);
                if (this.enableJMX) {
                    ReplicationConnectionGroupManager.registerJmx();
                }
                this.connectionGroupID = this.connectionGroup.registerReplicationConnection((ReplicationConnection)this.thisAsReplicationConnection, masterHostList, slaveHostList);
                this.slaveHosts = new ArrayList<String>(this.connectionGroup.getSlaveHosts());
                this.masterHosts = new ArrayList<String>(this.connectionGroup.getMasterHosts());
            } else {
                this.slaveHosts = new ArrayList<String>(slaveHostList);
                this.masterHosts = new ArrayList<String>(masterHostList);
            }
            this.driver = new NonRegisteringDriver();
            this.slaveProperties = slaveProperties;
            this.masterProperties = masterProperties;
            this.resetReadFromMasterWhenNoSlaves();
            try {
                this.initializeSlavesConnection();
            }
            catch (SQLException e) {
                if (this.allowSlaveDownConnections) break block17;
                if (this.connectionGroup == null) throw e;
                this.connectionGroup.handleCloseConnection((ReplicationConnection)this.thisAsReplicationConnection);
                throw e;
            }
        }
        SQLException exCaught = null;
        try {
            this.currentConnection = this.initializeMasterConnection();
        }
        catch (SQLException e) {
            exCaught = e;
        }
        if (this.currentConnection != null) return;
        if (this.allowMasterDownConnections && this.slavesConnection != null) {
            this.readOnly = true;
            this.currentConnection = this.slavesConnection;
            return;
        }
        if (this.connectionGroup != null) {
            this.connectionGroup.handleCloseConnection((ReplicationConnection)this.thisAsReplicationConnection);
        }
        if (exCaught == null) throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.initializationWithEmptyHostsLists"), (String)"S1009", null);
        throw exCaught;
    }

    @Override
    MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (Util.isJdbc4()) return (MySQLConnection)Util.handleNewInstance(JDBC_4_REPL_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
        if (JDBC_4_REPL_CONNECTION_CTOR == null) return new ReplicationMySQLConnection((MultiHostConnectionProxy)this);
        return (MySQLConnection)Util.handleNewInstance(JDBC_4_REPL_CONNECTION_CTOR, (Object[])new Object[]{this}, null);
    }

    @Override
    protected void propagateProxyDown(MySQLConnection proxyConn) {
        if (this.masterConnection != null) {
            this.masterConnection.setProxy((MySQLConnection)proxyConn);
        }
        if (this.slavesConnection == null) return;
        this.slavesConnection.setProxy((MySQLConnection)proxyConn);
    }

    @Override
    boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        return false;
    }

    @Override
    public boolean isMasterConnection() {
        if (this.currentConnection == null) return false;
        if (this.currentConnection != this.masterConnection) return false;
        return true;
    }

    public boolean isSlavesConnection() {
        if (this.currentConnection == null) return false;
        if (this.currentConnection != this.slavesConnection) return false;
        return true;
    }

    @Override
    void pickNewConnection() throws SQLException {
    }

    @Override
    void syncSessionState(Connection source, Connection target, boolean readOnlyStatus) throws SQLException {
        try {
            super.syncSessionState((Connection)source, (Connection)target, (boolean)readOnlyStatus);
            return;
        }
        catch (SQLException e1) {
            try {
                super.syncSessionState((Connection)source, (Connection)target, (boolean)readOnlyStatus);
                return;
            }
            catch (SQLException e2) {
                // empty catch block
            }
        }
    }

    @Override
    void doClose() throws SQLException {
        if (this.masterConnection != null) {
            this.masterConnection.close();
        }
        if (this.slavesConnection != null) {
            this.slavesConnection.close();
        }
        if (this.connectionGroup == null) return;
        this.connectionGroup.handleCloseConnection((ReplicationConnection)this.thisAsReplicationConnection);
    }

    @Override
    void doAbortInternal() throws SQLException {
        this.masterConnection.abortInternal();
        this.slavesConnection.abortInternal();
        if (this.connectionGroup == null) return;
        this.connectionGroup.handleCloseConnection((ReplicationConnection)this.thisAsReplicationConnection);
    }

    @Override
    void doAbort(Executor executor) throws SQLException {
        this.masterConnection.abort((Executor)executor);
        this.slavesConnection.abort((Executor)executor);
        if (this.connectionGroup == null) return;
        this.connectionGroup.handleCloseConnection((ReplicationConnection)this.thisAsReplicationConnection);
    }

    @Override
    Object invokeMore(Object proxy, Method method, Object[] args) throws Throwable {
        this.checkConnectionCapabilityForMethod((Method)method);
        boolean invokeAgain = false;
        do {
            try {
                Object result = method.invoke((Object)this.thisAsConnection, (Object[])args);
                if (result == null) return result;
                if (!(result instanceof Statement)) return result;
                ((Statement)result).setPingTarget((PingTarget)this);
                return result;
            }
            catch (InvocationTargetException e) {
                if (invokeAgain) {
                    invokeAgain = false;
                    continue;
                }
                if (e.getCause() == null || !(e.getCause() instanceof SQLException) || ((SQLException)e.getCause()).getSQLState() != "25000" || ((SQLException)e.getCause()).getErrorCode() != 1000001) continue;
                try {
                    this.setReadOnly((boolean)this.readOnly);
                    invokeAgain = true;
                    continue;
                }
                catch (SQLException sqlEx) {
                    // empty catch block
                }
                if (invokeAgain) continue;
                throw e;
            }
            break;
        } while (true);
    }

    private void checkConnectionCapabilityForMethod(Method method) throws Throwable {
        if (!this.masterHosts.isEmpty()) return;
        if (!this.slaveHosts.isEmpty()) return;
        if (ReplicationConnection.class.isAssignableFrom(method.getDeclaringClass())) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"ReplicationConnectionProxy.noHostsInconsistentState"), (String)"25000", (int)1000002, (boolean)true, null);
    }

    @Override
    public void doPing() throws SQLException {
        SQLException slavesPingException;
        SQLException mastersPingException;
        boolean isMasterConn;
        block14 : {
            isMasterConn = this.isMasterConnection();
            mastersPingException = null;
            slavesPingException = null;
            if (this.masterConnection != null) {
                try {
                    this.masterConnection.ping();
                }
                catch (SQLException e) {
                    mastersPingException = e;
                }
            } else {
                this.initializeMasterConnection();
            }
            if (this.slavesConnection != null) {
                try {
                    this.slavesConnection.ping();
                }
                catch (SQLException e) {
                    slavesPingException = e;
                }
            } else {
                try {
                    this.initializeSlavesConnection();
                    if (this.switchToSlavesConnectionIfNecessary()) {
                        isMasterConn = false;
                    }
                }
                catch (SQLException e) {
                    if (this.masterConnection == null) throw e;
                    if (this.readFromMasterWhenNoSlaves) break block14;
                    throw e;
                }
            }
        }
        if (isMasterConn && mastersPingException != null) {
            if (this.slavesConnection == null) throw mastersPingException;
            if (slavesPingException != null) throw mastersPingException;
            this.masterConnection = null;
            this.currentConnection = this.slavesConnection;
            this.readOnly = true;
            throw mastersPingException;
        }
        if (isMasterConn) return;
        if (slavesPingException == null) {
            if (this.slavesConnection != null) return;
        }
        if (this.masterConnection != null && this.readFromMasterWhenNoSlaves && mastersPingException == null) {
            this.slavesConnection = null;
            this.currentConnection = this.masterConnection;
            this.readOnly = true;
            this.currentConnection.setReadOnly((boolean)true);
        }
        if (slavesPingException == null) return;
        throw slavesPingException;
    }

    private MySQLConnection initializeMasterConnection() throws SQLException {
        this.masterConnection = null;
        if (this.masterHosts.size() == 0) {
            return null;
        }
        LoadBalancedConnection newMasterConn = (LoadBalancedConnection)this.driver.connect((String)this.buildURL(this.masterHosts, (Properties)this.masterProperties), (Properties)this.masterProperties);
        newMasterConn.setProxy((MySQLConnection)this.getProxy());
        this.masterConnection = newMasterConn;
        return this.masterConnection;
    }

    private MySQLConnection initializeSlavesConnection() throws SQLException {
        this.slavesConnection = null;
        if (this.slaveHosts.size() == 0) {
            return null;
        }
        LoadBalancedConnection newSlavesConn = (LoadBalancedConnection)this.driver.connect((String)this.buildURL(this.slaveHosts, (Properties)this.slaveProperties), (Properties)this.slaveProperties);
        newSlavesConn.setProxy((MySQLConnection)this.getProxy());
        newSlavesConn.setReadOnly((boolean)true);
        this.slavesConnection = newSlavesConn;
        return this.slavesConnection;
    }

    private String buildURL(List<String> hosts, Properties props) {
        StringBuilder url = new StringBuilder((String)"jdbc:mysql:loadbalance://");
        boolean firstHost = true;
        Iterator<String> i$ = hosts.iterator();
        do {
            if (!i$.hasNext()) {
                url.append((String)"/");
                String masterDb = props.getProperty((String)"DBNAME");
                if (masterDb == null) return url.toString();
                url.append((String)masterDb);
                return url.toString();
            }
            String host = i$.next();
            if (!firstHost) {
                url.append((char)',');
            }
            url.append((String)host);
            firstHost = false;
        } while (true);
    }

    private synchronized boolean switchToMasterConnection() throws SQLException {
        if (this.masterConnection == null || this.masterConnection.isClosed()) {
            try {
                if (this.initializeMasterConnection() == null) {
                    return false;
                }
            }
            catch (SQLException e) {
                this.currentConnection = null;
                throw e;
            }
        }
        if (this.isMasterConnection()) return true;
        if (this.masterConnection == null) return true;
        this.syncSessionState((Connection)this.currentConnection, (Connection)this.masterConnection, (boolean)false);
        this.currentConnection = this.masterConnection;
        return true;
    }

    private synchronized boolean switchToSlavesConnection() throws SQLException {
        if (this.slavesConnection == null || this.slavesConnection.isClosed()) {
            try {
                if (this.initializeSlavesConnection() == null) {
                    return false;
                }
            }
            catch (SQLException e) {
                this.currentConnection = null;
                throw e;
            }
        }
        if (this.isSlavesConnection()) return true;
        if (this.slavesConnection == null) return true;
        this.syncSessionState((Connection)this.currentConnection, (Connection)this.slavesConnection, (boolean)true);
        this.currentConnection = this.slavesConnection;
        return true;
    }

    private boolean switchToSlavesConnectionIfNecessary() throws SQLException {
        if (this.currentConnection == null) return this.switchToSlavesConnection();
        if (this.isMasterConnection()) {
            if (this.readOnly) return this.switchToSlavesConnection();
            if (this.masterHosts.isEmpty()) {
                if (this.currentConnection.isClosed()) return this.switchToSlavesConnection();
            }
        }
        if (this.isMasterConnection()) return false;
        if (!this.currentConnection.isClosed()) return false;
        return this.switchToSlavesConnection();
    }

    public synchronized Connection getCurrentConnection() {
        MySQLConnection mySQLConnection;
        if (this.currentConnection == null) {
            mySQLConnection = LoadBalancedConnectionProxy.getNullLoadBalancedConnectionInstance();
            return mySQLConnection;
        }
        mySQLConnection = this.currentConnection;
        return mySQLConnection;
    }

    public long getConnectionGroupId() {
        return this.connectionGroupID;
    }

    public synchronized Connection getMasterConnection() {
        return this.masterConnection;
    }

    public synchronized void promoteSlaveToMaster(String hostPortPair) throws SQLException {
        this.masterHosts.add((String)hostPortPair);
        this.removeSlave((String)hostPortPair);
        if (this.masterConnection != null) {
            this.masterConnection.addHost((String)hostPortPair);
        }
        if (this.readOnly) return;
        if (this.isMasterConnection()) return;
        this.switchToMasterConnection();
    }

    public synchronized void removeMasterHost(String hostPortPair) throws SQLException {
        this.removeMasterHost((String)hostPortPair, (boolean)true);
    }

    public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
        this.removeMasterHost((String)hostPortPair, (boolean)waitUntilNotInUse, (boolean)false);
    }

    public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowSlave) throws SQLException {
        if (isNowSlave) {
            this.slaveHosts.add((String)hostPortPair);
            this.resetReadFromMasterWhenNoSlaves();
        }
        this.masterHosts.remove((Object)hostPortPair);
        if (this.masterConnection == null || this.masterConnection.isClosed()) {
            this.masterConnection = null;
            return;
        }
        if (waitUntilNotInUse) {
            this.masterConnection.removeHostWhenNotInUse((String)hostPortPair);
        } else {
            this.masterConnection.removeHost((String)hostPortPair);
        }
        if (!this.masterHosts.isEmpty()) return;
        this.masterConnection.close();
        this.masterConnection = null;
        this.switchToSlavesConnectionIfNecessary();
    }

    public boolean isHostMaster(String hostPortPair) {
        String masterHost;
        if (hostPortPair == null) {
            return false;
        }
        Iterator<String> i$ = this.masterHosts.iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!(masterHost = i$.next()).equalsIgnoreCase((String)hostPortPair));
        return true;
    }

    public synchronized Connection getSlavesConnection() {
        return this.slavesConnection;
    }

    public synchronized void addSlaveHost(String hostPortPair) throws SQLException {
        if (this.isHostSlave((String)hostPortPair)) {
            return;
        }
        this.slaveHosts.add((String)hostPortPair);
        this.resetReadFromMasterWhenNoSlaves();
        if (this.slavesConnection == null) {
            this.initializeSlavesConnection();
            this.switchToSlavesConnectionIfNecessary();
            return;
        }
        this.slavesConnection.addHost((String)hostPortPair);
    }

    public synchronized void removeSlave(String hostPortPair) throws SQLException {
        this.removeSlave((String)hostPortPair, (boolean)true);
    }

    public synchronized void removeSlave(String hostPortPair, boolean closeGently) throws SQLException {
        this.slaveHosts.remove((Object)hostPortPair);
        this.resetReadFromMasterWhenNoSlaves();
        if (this.slavesConnection == null || this.slavesConnection.isClosed()) {
            this.slavesConnection = null;
            return;
        }
        if (closeGently) {
            this.slavesConnection.removeHostWhenNotInUse((String)hostPortPair);
        } else {
            this.slavesConnection.removeHost((String)hostPortPair);
        }
        if (!this.slaveHosts.isEmpty()) return;
        this.slavesConnection.close();
        this.slavesConnection = null;
        this.switchToMasterConnection();
        if (!this.isMasterConnection()) return;
        this.currentConnection.setReadOnly((boolean)this.readOnly);
    }

    public boolean isHostSlave(String hostPortPair) {
        String test;
        if (hostPortPair == null) {
            return false;
        }
        Iterator<String> i$ = this.slaveHosts.iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!(test = i$.next()).equalsIgnoreCase((String)hostPortPair));
        return true;
    }

    public synchronized void setReadOnly(boolean readOnly) throws SQLException {
        if (readOnly) {
            if (!this.isSlavesConnection() || this.currentConnection.isClosed()) {
                boolean switched = true;
                SQLException exceptionCaught = null;
                try {
                    switched = this.switchToSlavesConnection();
                }
                catch (SQLException e) {
                    switched = false;
                    exceptionCaught = e;
                }
                if (!switched && this.readFromMasterWhenNoSlaves && this.switchToMasterConnection()) {
                    exceptionCaught = null;
                }
                if (exceptionCaught != null) {
                    throw exceptionCaught;
                }
            }
        } else if (!this.isMasterConnection() || this.currentConnection.isClosed()) {
            boolean switched = true;
            SQLException exceptionCaught = null;
            try {
                switched = this.switchToMasterConnection();
            }
            catch (SQLException e) {
                switched = false;
                exceptionCaught = e;
            }
            if (!switched && this.switchToSlavesConnectionIfNecessary()) {
                exceptionCaught = null;
            }
            if (exceptionCaught != null) {
                throw exceptionCaught;
            }
        }
        this.readOnly = readOnly;
        if (!this.readFromMasterWhenNoSlaves) return;
        if (!this.isMasterConnection()) return;
        this.currentConnection.setReadOnly((boolean)this.readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        if (!this.isMasterConnection()) return true;
        if (this.readOnly) return true;
        return false;
    }

    private void resetReadFromMasterWhenNoSlaves() {
        this.readFromMasterWhenNoSlaves = this.slaveHosts.isEmpty() || this.readFromMasterWhenNoSlavesOriginal;
    }

    static {
        if (!Util.isJdbc4()) {
            INTERFACES_TO_PROXY = new Class[]{ReplicationConnection.class};
            return;
        }
        try {
            JDBC_4_REPL_CONNECTION_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4ReplicationMySQLConnection").getConstructor(ReplicationConnectionProxy.class);
            INTERFACES_TO_PROXY = new Class[]{ReplicationConnection.class, Class.forName((String)"com.mysql.jdbc.JDBC4MySQLConnection")};
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

