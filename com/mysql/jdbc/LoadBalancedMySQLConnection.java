/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.LoadBalancedConnection;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MultiHostMySQLConnection;
import com.mysql.jdbc.MySQLConnection;
import java.sql.SQLException;

public class LoadBalancedMySQLConnection
extends MultiHostMySQLConnection
implements LoadBalancedConnection {
    public LoadBalancedMySQLConnection(LoadBalancedConnectionProxy proxy) {
        super((MultiHostConnectionProxy)proxy);
    }

    @Override
    protected LoadBalancedConnectionProxy getThisAsProxy() {
        return (LoadBalancedConnectionProxy)super.getThisAsProxy();
    }

    @Override
    public void close() throws SQLException {
        this.getThisAsProxy().doClose();
    }

    @Override
    public void ping() throws SQLException {
        this.ping((boolean)true);
    }

    @Override
    public void ping(boolean allConnections) throws SQLException {
        if (allConnections) {
            this.getThisAsProxy().doPing();
            return;
        }
        this.getActiveMySQLConnection().ping();
    }

    @Override
    public boolean addHost(String host) throws SQLException {
        return this.getThisAsProxy().addHost((String)host);
    }

    @Override
    public void removeHost(String host) throws SQLException {
        this.getThisAsProxy().removeHost((String)host);
    }

    @Override
    public void removeHostWhenNotInUse(String host) throws SQLException {
        this.getThisAsProxy().removeHostWhenNotInUse((String)host);
    }
}

