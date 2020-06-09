/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.CallableStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class JDBC4CallableStatementWrapper
extends CallableStatementWrapper {
    public JDBC4CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) {
        super((ConnectionWrapper)c, (MysqlPooledConnection)conn, (CallableStatement)toWrap);
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

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setRowId((String)parameterName, (RowId)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setSQLXML((String)parameterName, (SQLXML)xmlObject);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getSQLXML((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getSQLXML((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getRowId((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNClob((String)parameterName, (NClob)value);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNClob((String)parameterName, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNClob((String)parameterName, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNString((String)parameterName, (String)value);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getCharacterStream((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getCharacterStream((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNCharacterStream((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNCharacterStream((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNClob((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNString((String)parameterName);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setAsciiStream((String)parameterName, (InputStream)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setAsciiStream((String)parameterName, (InputStream)x, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setBinaryStream((String)parameterName, (InputStream)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setBinaryStream((String)parameterName, (InputStream)x, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setBlob((String)parameterName, (InputStream)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setBlob((String)parameterName, (InputStream)x, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setBlob((String)parameterName, (Blob)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setCharacterStream((String)parameterName, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setCharacterStream((String)parameterName, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setClob((String)parameterName, (Clob)x);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setClob((String)parameterName, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setClob((String)parameterName, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNCharacterStream((String)parameterName, (Reader)reader);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setNCharacterStream((String)parameterName, (Reader)reader, (long)length);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNClob((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getNString((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((CallableStatement)this.wrappedStmt).getRowId((int)parameterIndex);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }
}

