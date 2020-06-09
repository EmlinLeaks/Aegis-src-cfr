/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Blob;
import com.mysql.jdbc.Clob;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC4ClientInfoProvider;
import com.mysql.jdbc.JDBC4MySQLConnection;
import com.mysql.jdbc.JDBC4MysqlSQLXML;
import com.mysql.jdbc.JDBC4NClob;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Util;
import java.sql.Array;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.Properties;

public class JDBC4Connection
extends ConnectionImpl
implements JDBC4MySQLConnection {
    private static final long serialVersionUID = 2877471301981509475L;
    private JDBC4ClientInfoProvider infoProvider;

    public JDBC4Connection(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
        super((String)hostToConnectTo, (int)portToConnectTo, (Properties)info, (String)databaseToConnectTo, (String)url);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return new JDBC4MysqlSQLXML((ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.getClientInfoProviderImpl().getClientInfo((Connection)this);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.getClientInfoProviderImpl().getClientInfo((Connection)this, (String)name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.isClosed()) {
            // MONITOREXIT : object
            return false;
        }
        try {
            try {
                this.pingInternal((boolean)false, (int)(timeout * 1000));
                return true;
            }
            catch (Throwable t) {
                try {
                    this.abortInternal();
                    return false;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return false;
            }
        }
        catch (Throwable t) {
            // MONITOREXIT : object
            return false;
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            this.getClientInfoProviderImpl().setClientInfo((Connection)this, (Properties)properties);
            return;
        }
        catch (SQLClientInfoException ciEx) {
            throw ciEx;
        }
        catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause((Throwable)sqlEx);
            throw clientInfoEx;
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            this.getClientInfoProviderImpl().setClientInfo((Connection)this, (String)name, (String)value);
            return;
        }
        catch (SQLClientInfoException ciEx) {
            throw ciEx;
        }
        catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause((Throwable)sqlEx);
            throw clientInfoEx;
        }
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
    public java.sql.Blob createBlob() {
        return new Blob((ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public java.sql.Clob createClob() {
        return new Clob((ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public NClob createNClob() {
        return new JDBC4NClob((ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.infoProvider == null) {
            try {
                try {
                    this.infoProvider = (JDBC4ClientInfoProvider)Util.getInstance((String)this.getClientInfoProvider(), new Class[0], (Object[])new Object[0], (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                catch (SQLException sqlEx) {
                    if (sqlEx.getCause() instanceof ClassCastException) {
                        this.infoProvider = (JDBC4ClientInfoProvider)Util.getInstance((String)("com.mysql.jdbc." + this.getClientInfoProvider()), new Class[0], (Object[])new Object[0], (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            }
            catch (ClassCastException cce) {
                throw SQLError.createSQLException((String)Messages.getString((String)"JDBC4Connection.ClientInfoNotImplemented", (Object[])new Object[]{this.getClientInfoProvider()}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            this.infoProvider.initialize((Connection)this, (Properties)this.props);
        }
        // MONITOREXIT : object
        return this.infoProvider;
    }
}

