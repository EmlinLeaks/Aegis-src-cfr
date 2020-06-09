/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.StatementWrapper;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class JDBC4StatementWrapper
extends StatementWrapper {
    public JDBC4StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
        super((ConnectionWrapper)c, (MysqlPooledConnection)conn, (Statement)toWrap);
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
    public boolean isClosed() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.isClosed();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setPoolable((boolean)poolable);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public boolean isPoolable() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.isPoolable();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        boolean isInstance = iface.isInstance((Object)this);
        if (isInstance) {
            return true;
        }
        String interfaceClassName = iface.getName();
        if (interfaceClassName.equals((Object)"com.mysql.jdbc.Statement")) return true;
        if (interfaceClassName.equals((Object)"java.sql.Statement")) return true;
        if (interfaceClassName.equals((Object)"java.sql.Wrapper")) return true;
        return false;
    }

    @Override
    public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            Object cachedUnwrapped;
            if ("java.sql.Statement".equals((Object)iface.getName())) return (T)iface.cast((Object)this);
            if ("java.sql.Wrapper.class".equals((Object)iface.getName())) {
                return (T)iface.cast((Object)this);
            }
            if (this.unwrappedInterfaces == null) {
                this.unwrappedInterfaces = new HashMap<K, V>();
            }
            if ((cachedUnwrapped = this.unwrappedInterfaces.get(iface)) != null) return (T)iface.cast(cachedUnwrapped);
            cachedUnwrapped = Proxy.newProxyInstance((ClassLoader)this.wrappedStmt.getClass().getClassLoader(), new Class[]{iface}, (InvocationHandler)new WrapperBase.ConnectionErrorFiringInvocationHandler((WrapperBase)this, (Object)this.wrappedStmt));
            this.unwrappedInterfaces.put(iface, cachedUnwrapped);
            return (T)iface.cast(cachedUnwrapped);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
    }
}

