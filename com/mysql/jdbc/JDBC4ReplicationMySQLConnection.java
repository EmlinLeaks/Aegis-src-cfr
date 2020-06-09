/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC4ClientInfoProvider;
import com.mysql.jdbc.JDBC4MySQLConnection;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ReplicationConnectionProxy;
import com.mysql.jdbc.ReplicationMySQLConnection;
import com.mysql.jdbc.SQLError;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.Properties;

public class JDBC4ReplicationMySQLConnection
extends ReplicationMySQLConnection
implements JDBC4MySQLConnection {
    public JDBC4ReplicationMySQLConnection(ReplicationConnectionProxy proxy) throws SQLException {
        super((MultiHostConnectionProxy)proxy);
    }

    private JDBC4MySQLConnection getJDBC4Connection() {
        return (JDBC4MySQLConnection)this.getActiveMySQLConnection();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.getJDBC4Connection().createSQLXML();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.getJDBC4Connection().createArrayOf((String)typeName, (Object[])elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.getJDBC4Connection().createStruct((String)typeName, (Object[])attributes);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.getJDBC4Connection().getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.getJDBC4Connection().getClientInfo((String)name);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.getJDBC4Connection().isValid((int)timeout);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.getJDBC4Connection().setClientInfo((Properties)properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.getJDBC4Connection().setClientInfo((String)name, (String)value);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        this.checkClosed();
        return iface.isInstance((Object)this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T)iface.cast((Object)this);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public Blob createBlob() {
        return this.getJDBC4Connection().createBlob();
    }

    @Override
    public Clob createClob() {
        return this.getJDBC4Connection().createClob();
    }

    @Override
    public NClob createNClob() {
        return this.getJDBC4Connection().createNClob();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
        ReplicationConnectionProxy replicationConnectionProxy = this.getThisAsProxy();
        // MONITORENTER : replicationConnectionProxy
        // MONITOREXIT : replicationConnectionProxy
        return this.getJDBC4Connection().getClientInfoProviderImpl();
    }
}

