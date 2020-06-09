/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JDBC4ConnectionWrapper
extends ConnectionWrapper {
    public JDBC4ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
        super((MysqlPooledConnection)mysqlPooledConnection, (Connection)mysqlConnection, (boolean)forXa);
    }

    @Override
    public void close() throws SQLException {
        try {
            super.close();
            return;
        }
        finally {
            this.unwrappedInterfaces = null;
        }
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createSQLXML();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createArrayOf((String)typeName, (Object[])elements);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createStruct((String)typeName, (Object[])attributes);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getClientInfo();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getClientInfo((String)name);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public synchronized boolean isValid(int timeout) throws SQLException {
        try {
            return this.mc.isValid((int)timeout);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return false;
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            this.checkClosed();
            this.mc.setClientInfo((Properties)properties);
            return;
        }
        catch (SQLException sqlException) {
            try {
                this.checkAndFireConnectionError((SQLException)sqlException);
                return;
            }
            catch (SQLException sqlEx2) {
                SQLClientInfoException clientEx = new SQLClientInfoException();
                clientEx.initCause((Throwable)sqlEx2);
                throw clientEx;
            }
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            this.checkClosed();
            this.mc.setClientInfo((String)name, (String)value);
            return;
        }
        catch (SQLException sqlException) {
            try {
                this.checkAndFireConnectionError((SQLException)sqlException);
                return;
            }
            catch (SQLException sqlEx2) {
                SQLClientInfoException clientEx = new SQLClientInfoException();
                clientEx.initCause((Throwable)sqlEx2);
                throw clientEx;
            }
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        this.checkClosed();
        boolean isInstance = iface.isInstance((Object)this);
        if (isInstance) {
            return true;
        }
        if (iface.getName().equals((Object)"com.mysql.jdbc.Connection")) return true;
        if (iface.getName().equals((Object)"com.mysql.jdbc.ConnectionProperties")) return true;
        return false;
    }

    @Override
    public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            Object cachedUnwrapped;
            if ("java.sql.Connection".equals((Object)iface.getName())) return (T)iface.cast((Object)this);
            if ("java.sql.Wrapper.class".equals((Object)iface.getName())) {
                return (T)iface.cast((Object)this);
            }
            if (this.unwrappedInterfaces == null) {
                this.unwrappedInterfaces = new HashMap<K, V>();
            }
            if ((cachedUnwrapped = this.unwrappedInterfaces.get(iface)) != null) return (T)iface.cast(cachedUnwrapped);
            cachedUnwrapped = Proxy.newProxyInstance((ClassLoader)this.mc.getClass().getClassLoader(), new Class[]{iface}, (InvocationHandler)new WrapperBase.ConnectionErrorFiringInvocationHandler((WrapperBase)this, (Object)this.mc));
            this.unwrappedInterfaces.put(iface, cachedUnwrapped);
            return (T)iface.cast(cachedUnwrapped);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
    }

    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createBlob();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createClob();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.createNClob();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }
}

