/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.PreparedStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.PooledConnection;
import javax.sql.StatementEvent;

public class JDBC4PreparedStatementWrapper
extends PreparedStatementWrapper {
    public JDBC4PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
        super((ConnectionWrapper)c, (MysqlPooledConnection)conn, (PreparedStatement)toWrap);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void close() throws SQLException {
        if (this.pooledConnection == null) {
            return;
        }
        MysqlPooledConnection con = this.pooledConnection;
        try {
            super.close();
            return;
        }
        finally {
            try {
                StatementEvent e = new StatementEvent((PooledConnection)con, (PreparedStatement)this);
                if (con instanceof JDBC4MysqlPooledConnection) {
                    ((JDBC4MysqlPooledConnection)con).fireStatementEvent((StatementEvent)e);
                } else if (con instanceof JDBC4MysqlXAConnection) {
                    ((JDBC4MysqlXAConnection)con).fireStatementEvent((StatementEvent)e);
                } else if (con instanceof JDBC4SuspendableXAConnection) {
                    ((JDBC4SuspendableXAConnection)con).fireStatementEvent((StatementEvent)e);
                }
            }
            finally {
                this.unwrappedInterfaces = null;
            }
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
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setRowId((int)parameterIndex, (RowId)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNClob((int)parameterIndex, (NClob)value);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setSQLXML((int)parameterIndex, (SQLXML)xmlObject);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNString((int)parameterIndex, (String)value);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNCharacterStream((int)parameterIndex, (Reader)value, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setClob((int)parameterIndex, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setBlob((int)parameterIndex, (InputStream)inputStream, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNClob((int)parameterIndex, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setAsciiStream((int)parameterIndex, (InputStream)x, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setBinaryStream((int)parameterIndex, (InputStream)x, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setCharacterStream((int)parameterIndex, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setAsciiStream((int)parameterIndex, (InputStream)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setBinaryStream((int)parameterIndex, (InputStream)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setCharacterStream((int)parameterIndex, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNCharacterStream((int)parameterIndex, (Reader)value);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setClob((int)parameterIndex, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setBlob((int)parameterIndex, (InputStream)inputStream);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setNClob((int)parameterIndex, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
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
        if (interfaceClassName.equals((Object)"java.sql.PreparedStatement")) return true;
        if (interfaceClassName.equals((Object)"java.sql.Wrapper")) return true;
        return false;
    }

    @Override
    public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            Object cachedUnwrapped;
            if ("java.sql.Statement".equals((Object)iface.getName())) return (T)iface.cast((Object)this);
            if ("java.sql.PreparedStatement".equals((Object)iface.getName())) return (T)iface.cast((Object)this);
            if ("java.sql.Wrapper.class".equals((Object)iface.getName())) {
                return (T)iface.cast((Object)this);
            }
            if (this.unwrappedInterfaces == null) {
                this.unwrappedInterfaces = new HashMap<K, V>();
            }
            if ((cachedUnwrapped = this.unwrappedInterfaces.get(iface)) != null) return (T)iface.cast(cachedUnwrapped);
            if (cachedUnwrapped == null) {
                cachedUnwrapped = Proxy.newProxyInstance((ClassLoader)this.wrappedStmt.getClass().getClassLoader(), new Class[]{iface}, (InvocationHandler)new WrapperBase.ConnectionErrorFiringInvocationHandler((WrapperBase)this, (Object)this.wrappedStmt));
                this.unwrappedInterfaces.put(iface, cachedUnwrapped);
            }
            this.unwrappedInterfaces.put(iface, cachedUnwrapped);
            return (T)iface.cast(cachedUnwrapped);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        }
    }
}

