/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class StatementWrapper
extends WrapperBase
implements java.sql.Statement {
    private static final Constructor<?> JDBC_4_STATEMENT_WRAPPER_CTOR;
    protected java.sql.Statement wrappedStmt;
    protected ConnectionWrapper wrappedConn;

    protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap) throws SQLException {
        if (Util.isJdbc4()) return (StatementWrapper)Util.handleNewInstance(JDBC_4_STATEMENT_WRAPPER_CTOR, (Object[])new Object[]{c, conn, toWrap}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new StatementWrapper((ConnectionWrapper)c, (MysqlPooledConnection)conn, (java.sql.Statement)toWrap);
    }

    public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap) {
        super((MysqlPooledConnection)conn);
        this.wrappedStmt = toWrap;
        this.wrappedConn = c;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedConn;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setCursorName((String)name);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setEscapeProcessing((boolean)enable);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setFetchDirection((int)direction);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getFetchDirection();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 1000;
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setFetchSize((int)rows);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getFetchSize();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0;
        }
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getGeneratedKeys();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setMaxFieldSize((int)max);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getMaxFieldSize();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0;
        }
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setMaxRows((int)max);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public int getMaxRows() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getMaxRows();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0;
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getMoreResults();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getMoreResults((int)current);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            this.wrappedStmt.setQueryTimeout((int)seconds);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getQueryTimeout();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0;
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            ResultSet rs = this.wrappedStmt.getResultSet();
            if (rs == null) return rs;
            ((ResultSetInternalMethods)rs).setWrapperStatement((java.sql.Statement)this);
            return rs;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getResultSetConcurrency();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0;
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getResultSetHoldability();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 1;
        }
    }

    @Override
    public int getResultSetType() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getResultSetType();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 1003;
        }
    }

    @Override
    public int getUpdateCount() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getUpdateCount();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.getWarnings();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        try {
            if (this.wrappedStmt == null) return;
            this.wrappedStmt.addBatch((String)sql);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void cancel() throws SQLException {
        try {
            if (this.wrappedStmt == null) return;
            this.wrappedStmt.cancel();
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        try {
            if (this.wrappedStmt == null) return;
            this.wrappedStmt.clearBatch();
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            if (this.wrappedStmt == null) return;
            this.wrappedStmt.clearWarnings();
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        try {
            block4 : {
                try {
                    if (this.wrappedStmt == null) break block4;
                    this.wrappedStmt.close();
                }
                catch (SQLException sqlEx) {
                    this.checkAndFireConnectionError((SQLException)sqlEx);
                    Object var3_2 = null;
                    this.wrappedStmt = null;
                    this.pooledConnection = null;
                    return;
                }
            }
            Object var3_1 = null;
            this.wrappedStmt = null;
            this.pooledConnection = null;
            return;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.wrappedStmt = null;
            this.pooledConnection = null;
            throw throwable;
        }
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.execute((String)sql, (int)autoGeneratedKeys);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.execute((String)sql, (int[])columnIndexes);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.execute((String)sql, (String[])columnNames);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.execute((String)sql);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return false;
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.executeBatch();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs = null;
        try {
            if (this.wrappedStmt == null) {
                throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            }
            rs = this.wrappedStmt.executeQuery((String)sql);
            ((ResultSetInternalMethods)rs).setWrapperStatement((java.sql.Statement)this);
            return rs;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
        return rs;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.executeUpdate((String)sql, (int)autoGeneratedKeys);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1;
        }
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.executeUpdate((String)sql, (int[])columnIndexes);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1;
        }
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.executeUpdate((String)sql, (String[])columnNames);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1;
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return this.wrappedStmt.executeUpdate((String)sql);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1;
        }
    }

    public void enableStreamingResults() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((Statement)this.wrappedStmt).enableStreamingResults();
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).executeLargeBatch();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return null;
        }
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate((String)sql);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1L;
        }
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate((String)sql, (int)autoGeneratedKeys);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1L;
        }
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate((String)sql, (int[])columnIndexes);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1L;
        }
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate((String)sql, (String[])columnNames);
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1L;
        }
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).getLargeMaxRows();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return 0L;
        }
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            return ((StatementImpl)this.wrappedStmt).getLargeUpdateCount();
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
            return -1L;
        }
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"Statement already closed", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            ((StatementImpl)this.wrappedStmt).setLargeMaxRows((long)max);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    static {
        if (!Util.isJdbc4()) {
            JDBC_4_STATEMENT_WRAPPER_CTOR = null;
            return;
        }
        try {
            JDBC_4_STATEMENT_WRAPPER_CTOR = Class.forName((String)"com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper").getConstructor(ConnectionWrapper.class, MysqlPooledConnection.class, java.sql.Statement.class);
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

