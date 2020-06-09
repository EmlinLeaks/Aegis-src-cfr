/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.LoadBalancedConnection;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MultiHostMySQLConnection;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ReplicationConnection;
import com.mysql.jdbc.ReplicationConnectionProxy;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ReplicationMySQLConnection
extends MultiHostMySQLConnection
implements ReplicationConnection {
    public ReplicationMySQLConnection(MultiHostConnectionProxy proxy) {
        super((MultiHostConnectionProxy)proxy);
    }

    @Override
    protected ReplicationConnectionProxy getThisAsProxy() {
        return (ReplicationConnectionProxy)super.getThisAsProxy();
    }

    @Override
    public MySQLConnection getActiveMySQLConnection() {
        return (MySQLConnection)this.getCurrentConnection();
    }

    @Override
    public synchronized Connection getCurrentConnection() {
        return this.getThisAsProxy().getCurrentConnection();
    }

    @Override
    public long getConnectionGroupId() {
        return this.getThisAsProxy().getConnectionGroupId();
    }

    @Override
    public synchronized Connection getMasterConnection() {
        return this.getThisAsProxy().getMasterConnection();
    }

    private Connection getValidatedMasterConnection() {
        LoadBalancedConnection conn = this.getThisAsProxy().masterConnection;
        try {
            if (conn == null) return null;
            if (conn.isClosed()) return null;
            LoadBalancedConnection loadBalancedConnection = conn;
            return loadBalancedConnection;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void promoteSlaveToMaster(String host) throws SQLException {
        this.getThisAsProxy().promoteSlaveToMaster((String)host);
    }

    @Override
    public void removeMasterHost(String host) throws SQLException {
        this.getThisAsProxy().removeMasterHost((String)host);
    }

    @Override
    public void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
        this.getThisAsProxy().removeMasterHost((String)host, (boolean)waitUntilNotInUse);
    }

    @Override
    public boolean isHostMaster(String host) {
        return this.getThisAsProxy().isHostMaster((String)host);
    }

    @Override
    public synchronized Connection getSlavesConnection() {
        return this.getThisAsProxy().getSlavesConnection();
    }

    private Connection getValidatedSlavesConnection() {
        LoadBalancedConnection conn = this.getThisAsProxy().slavesConnection;
        try {
            if (conn == null) return null;
            if (conn.isClosed()) return null;
            LoadBalancedConnection loadBalancedConnection = conn;
            return loadBalancedConnection;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void addSlaveHost(String host) throws SQLException {
        this.getThisAsProxy().addSlaveHost((String)host);
    }

    @Override
    public void removeSlave(String host) throws SQLException {
        this.getThisAsProxy().removeSlave((String)host);
    }

    @Override
    public void removeSlave(String host, boolean closeGently) throws SQLException {
        this.getThisAsProxy().removeSlave((String)host, (boolean)closeGently);
    }

    @Override
    public boolean isHostSlave(String host) {
        return this.getThisAsProxy().isHostSlave((String)host);
    }

    @Override
    public void setReadOnly(boolean readOnlyFlag) throws SQLException {
        this.getThisAsProxy().setReadOnly((boolean)readOnlyFlag);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.getThisAsProxy().isReadOnly();
    }

    @Override
    public synchronized void ping() throws SQLException {
        Connection conn;
        block5 : {
            try {
                conn = this.getValidatedMasterConnection();
                if (conn != null) {
                    conn.ping();
                }
            }
            catch (SQLException e) {
                if (!this.isMasterConnection()) break block5;
                throw e;
            }
        }
        try {
            conn = this.getValidatedSlavesConnection();
            if (conn == null) return;
            conn.ping();
            return;
        }
        catch (SQLException e) {
            if (this.isMasterConnection()) return;
            throw e;
        }
    }

    @Override
    public synchronized void changeUser(String userName, String newPassword) throws SQLException {
        Connection conn = this.getValidatedMasterConnection();
        if (conn != null) {
            conn.changeUser((String)userName, (String)newPassword);
        }
        if ((conn = this.getValidatedSlavesConnection()) == null) return;
        conn.changeUser((String)userName, (String)newPassword);
    }

    @Override
    public synchronized void setStatementComment(String comment) {
        Connection conn = this.getValidatedMasterConnection();
        if (conn != null) {
            conn.setStatementComment((String)comment);
        }
        if ((conn = this.getValidatedSlavesConnection()) == null) return;
        conn.setStatementComment((String)comment);
    }

    @Override
    public boolean hasSameProperties(Connection c) {
        Connection connM = this.getValidatedMasterConnection();
        Connection connS = this.getValidatedSlavesConnection();
        if (connM == null && connS == null) {
            return false;
        }
        if (connM != null) {
            if (!connM.hasSameProperties((Connection)c)) return false;
        }
        if (connS == null) return true;
        if (!connS.hasSameProperties((Connection)c)) return false;
        return true;
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        Connection conn = this.getValidatedMasterConnection();
        if (conn != null) {
            props.putAll(conn.getProperties());
        }
        if ((conn = this.getValidatedSlavesConnection()) == null) return props;
        props.putAll(conn.getProperties());
        return props;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.getThisAsProxy().doAbort((Executor)executor);
    }

    @Override
    public void abortInternal() throws SQLException {
        this.getThisAsProxy().doAbortInternal();
    }

    @Override
    public boolean getAllowMasterDownConnections() {
        return this.getThisAsProxy().allowMasterDownConnections;
    }

    @Override
    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        this.getThisAsProxy().allowMasterDownConnections = connectIfMasterDown;
    }

    @Override
    public boolean getReplicationEnableJMX() {
        return this.getThisAsProxy().enableJMX;
    }

    @Override
    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        this.getThisAsProxy().enableJMX = replicationEnableJMX;
    }

    @Override
    public void setProxy(MySQLConnection proxy) {
        this.getThisAsProxy().setProxy((MySQLConnection)proxy);
    }
}

