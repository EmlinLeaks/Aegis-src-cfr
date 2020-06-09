/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.jdbc2.optional.CallableStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.PreparedStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.StatementWrapper;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import com.mysql.jdbc.log.Log;
import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConnectionWrapper
extends WrapperBase
implements Connection {
    protected Connection mc = null;
    private String invalidHandleStr = "Logical handle no longer valid";
    private boolean closed;
    private boolean isForXa;
    private static final Constructor<?> JDBC_4_CONNECTION_WRAPPER_CTOR;

    protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
        if (Util.isJdbc4()) return (ConnectionWrapper)Util.handleNewInstance(JDBC_4_CONNECTION_WRAPPER_CTOR, (Object[])new Object[]{mysqlPooledConnection, mysqlConnection, Boolean.valueOf((boolean)forXa)}, (ExceptionInterceptor)mysqlPooledConnection.getExceptionInterceptor());
        return new ConnectionWrapper((MysqlPooledConnection)mysqlPooledConnection, (Connection)mysqlConnection, (boolean)forXa);
    }

    public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa) throws SQLException {
        super((MysqlPooledConnection)mysqlPooledConnection);
        this.mc = mysqlConnection;
        this.closed = false;
        this.isForXa = forXa;
        if (!this.isForXa) return;
        this.setInGlobalTx((boolean)false);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.checkClosed();
        if (autoCommit && this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't set autocommit to 'true' on an XAConnection", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            this.mc.setAutoCommit((boolean)autoCommit);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getAutoCommit();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return false;
        }
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.checkClosed();
        try {
            this.mc.setCatalog((String)catalog);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getCatalog();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (this.closed) return true;
        if (this.mc.isClosed()) return true;
        return false;
    }

    @Override
    public boolean isMasterConnection() {
        return this.mc.isMasterConnection();
    }

    @Override
    public void setHoldability(int arg0) throws SQLException {
        this.checkClosed();
        try {
            this.mc.setHoldability((int)arg0);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getHoldability();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return 1;
        }
    }

    @Override
    public long getIdleFor() {
        return this.mc.getIdleFor();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getMetaData();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.checkClosed();
        try {
            this.mc.setReadOnly((boolean)readOnly);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.isReadOnly();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return false;
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkClosed();
        if (this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't set autocommit to 'true' on an XAConnection", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            return this.mc.setSavepoint();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Savepoint setSavepoint(String arg0) throws SQLException {
        this.checkClosed();
        if (this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't set autocommit to 'true' on an XAConnection", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            return this.mc.setSavepoint((String)arg0);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.checkClosed();
        try {
            this.mc.setTransactionIsolation((int)level);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getTransactionIsolation();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return 4;
        }
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getTypeMap();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.getWarnings();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.checkClosed();
        try {
            this.mc.clearWarnings();
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public void close() throws SQLException {
        this.close((boolean)true);
    }

    @Override
    public void commit() throws SQLException {
        this.checkClosed();
        if (this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't call commit() on an XAConnection associated with a global transaction", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            this.mc.commit();
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        this.checkClosed();
        try {
            return StatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (Statement)this.mc.createStatement());
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        try {
            return StatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (Statement)this.mc.createStatement((int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
        this.checkClosed();
        try {
            return StatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (Statement)this.mc.createStatement((int)arg0, (int)arg1, (int)arg2));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.checkClosed();
        try {
            return this.mc.nativeSQL((String)sql);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.checkClosed();
        try {
            return CallableStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (CallableStatement)this.mc.prepareCall((String)sql));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        try {
            return CallableStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (CallableStatement)this.mc.prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
        this.checkClosed();
        try {
            return CallableStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (CallableStatement)this.mc.prepareCall((String)arg0, (int)arg1, (int)arg2, (int)arg3));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    public PreparedStatement clientPrepare(String sql) throws SQLException {
        this.checkClosed();
        try {
            return new PreparedStatementWrapper((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        try {
            return new PreparedStatementWrapper((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.checkClosed();
        PreparedStatementWrapper res = null;
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)sql));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
        return res;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)arg0, (int)arg1, (int)arg2, (int)arg3));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)arg0, (int)arg1));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)arg0, (int[])arg1));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.prepareStatement((String)arg0, (String[])arg1));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public void releaseSavepoint(Savepoint arg0) throws SQLException {
        this.checkClosed();
        try {
            this.mc.releaseSavepoint((Savepoint)arg0);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public void rollback() throws SQLException {
        this.checkClosed();
        if (this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't call rollback() on an XAConnection associated with a global transaction", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            this.mc.rollback();
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public void rollback(Savepoint arg0) throws SQLException {
        this.checkClosed();
        if (this.isInGlobalTx()) {
            throw SQLError.createSQLException((String)"Can't call rollback() on an XAConnection associated with a global transaction", (String)"2D000", (int)1401, (ExceptionInterceptor)this.exceptionInterceptor);
        }
        try {
            this.mc.rollback((Savepoint)arg0);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public boolean isSameResource(Connection c) {
        if (!(c instanceof ConnectionWrapper)) return this.mc.isSameResource((Connection)c);
        return this.mc.isSameResource((Connection)((ConnectionWrapper)c).mc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void close(boolean fireClosedEvent) throws SQLException {
        MysqlPooledConnection mysqlPooledConnection = this.pooledConnection;
        // MONITORENTER : mysqlPooledConnection
        if (this.closed) {
            // MONITOREXIT : mysqlPooledConnection
            return;
        }
        if (!this.isInGlobalTx() && this.mc.getRollbackOnPooledClose() && !this.getAutoCommit()) {
            this.rollback();
        }
        if (fireClosedEvent) {
            this.pooledConnection.callConnectionEventListeners((int)2, null);
        }
        this.closed = true;
        // MONITOREXIT : mysqlPooledConnection
        return;
    }

    @Override
    public void checkClosed() throws SQLException {
        if (!this.closed) return;
        throw SQLError.createSQLException((String)this.invalidHandleStr, (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public boolean isInGlobalTx() {
        return this.mc.isInGlobalTx();
    }

    @Override
    public void setInGlobalTx(boolean flag) {
        this.mc.setInGlobalTx((boolean)flag);
    }

    @Override
    public void ping() throws SQLException {
        if (this.mc == null) return;
        this.mc.ping();
    }

    @Override
    public void changeUser(String userName, String newPassword) throws SQLException {
        this.checkClosed();
        try {
            this.mc.changeUser((String)userName, (String)newPassword);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Deprecated
    @Override
    public void clearHasTriedMaster() {
        this.mc.clearHasTriedMaster();
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (int)autoGenKeyIndex));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (int[])autoGenKeyIndexes));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.clientPrepareStatement((String)sql, (String[])autoGenKeyColNames));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public int getActiveStatementCount() {
        return this.mc.getActiveStatementCount();
    }

    @Override
    public Log getLog() throws SQLException {
        return this.mc.getLog();
    }

    @Deprecated
    @Override
    public String getServerCharacterEncoding() {
        return this.getServerCharset();
    }

    @Override
    public String getServerCharset() {
        return this.mc.getServerCharset();
    }

    @Override
    public TimeZone getServerTimezoneTZ() {
        return this.mc.getServerTimezoneTZ();
    }

    @Override
    public String getStatementComment() {
        return this.mc.getStatementComment();
    }

    @Deprecated
    @Override
    public boolean hasTriedMaster() {
        return this.mc.hasTriedMaster();
    }

    @Override
    public boolean isAbonormallyLongQuery(long millisOrNanos) {
        return this.mc.isAbonormallyLongQuery((long)millisOrNanos);
    }

    @Override
    public boolean isNoBackslashEscapesSet() {
        return this.mc.isNoBackslashEscapesSet();
    }

    @Override
    public boolean lowerCaseTableNames() {
        return this.mc.lowerCaseTableNames();
    }

    @Override
    public boolean parserKnowsUnicode() {
        return this.mc.parserKnowsUnicode();
    }

    @Override
    public void reportQueryTime(long millisOrNanos) {
        this.mc.reportQueryTime((long)millisOrNanos);
    }

    @Override
    public void resetServerState() throws SQLException {
        this.checkClosed();
        try {
            this.mc.resetServerState();
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
        this.checkClosed();
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql, (int)autoGenKeyIndex));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql, (int[])autoGenKeyIndexes));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        try {
            return PreparedStatementWrapper.getInstance((ConnectionWrapper)this, (MysqlPooledConnection)this.pooledConnection, (PreparedStatement)this.mc.serverPrepareStatement((String)sql, (String[])autoGenKeyColNames));
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public void setFailedOver(boolean flag) {
        this.mc.setFailedOver((boolean)flag);
    }

    @Deprecated
    @Override
    public void setPreferSlaveDuringFailover(boolean flag) {
        this.mc.setPreferSlaveDuringFailover((boolean)flag);
    }

    @Override
    public void setStatementComment(String comment) {
        this.mc.setStatementComment((String)comment);
    }

    @Override
    public void shutdownServer() throws SQLException {
        this.checkClosed();
        try {
            this.mc.shutdownServer();
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public boolean supportsIsolationLevel() {
        return this.mc.supportsIsolationLevel();
    }

    @Override
    public boolean supportsQuotedIdentifiers() {
        return this.mc.supportsQuotedIdentifiers();
    }

    @Override
    public boolean supportsTransactions() {
        return this.mc.supportsTransactions();
    }

    @Override
    public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
        this.checkClosed();
        try {
            return this.mc.versionMeetsMinimum((int)major, (int)minor, (int)subminor);
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return false;
        }
    }

    @Override
    public String exposeAsXml() throws SQLException {
        this.checkClosed();
        try {
            return this.mc.exposeAsXml();
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
            return null;
        }
    }

    @Override
    public boolean getAllowLoadLocalInfile() {
        return this.mc.getAllowLoadLocalInfile();
    }

    @Override
    public boolean getAllowMultiQueries() {
        return this.mc.getAllowMultiQueries();
    }

    @Override
    public boolean getAllowNanAndInf() {
        return this.mc.getAllowNanAndInf();
    }

    @Override
    public boolean getAllowUrlInLocalInfile() {
        return this.mc.getAllowUrlInLocalInfile();
    }

    @Override
    public boolean getAlwaysSendSetIsolation() {
        return this.mc.getAlwaysSendSetIsolation();
    }

    @Override
    public boolean getAutoClosePStmtStreams() {
        return this.mc.getAutoClosePStmtStreams();
    }

    @Override
    public boolean getAutoDeserialize() {
        return this.mc.getAutoDeserialize();
    }

    @Override
    public boolean getAutoGenerateTestcaseScript() {
        return this.mc.getAutoGenerateTestcaseScript();
    }

    @Override
    public boolean getAutoReconnectForPools() {
        return this.mc.getAutoReconnectForPools();
    }

    @Override
    public boolean getAutoSlowLog() {
        return this.mc.getAutoSlowLog();
    }

    @Override
    public int getBlobSendChunkSize() {
        return this.mc.getBlobSendChunkSize();
    }

    @Override
    public boolean getBlobsAreStrings() {
        return this.mc.getBlobsAreStrings();
    }

    @Override
    public boolean getCacheCallableStatements() {
        return this.mc.getCacheCallableStatements();
    }

    @Override
    public boolean getCacheCallableStmts() {
        return this.mc.getCacheCallableStmts();
    }

    @Override
    public boolean getCachePrepStmts() {
        return this.mc.getCachePrepStmts();
    }

    @Override
    public boolean getCachePreparedStatements() {
        return this.mc.getCachePreparedStatements();
    }

    @Override
    public boolean getCacheResultSetMetadata() {
        return this.mc.getCacheResultSetMetadata();
    }

    @Override
    public boolean getCacheServerConfiguration() {
        return this.mc.getCacheServerConfiguration();
    }

    @Override
    public int getCallableStatementCacheSize() {
        return this.mc.getCallableStatementCacheSize();
    }

    @Override
    public int getCallableStmtCacheSize() {
        return this.mc.getCallableStmtCacheSize();
    }

    @Override
    public boolean getCapitalizeTypeNames() {
        return this.mc.getCapitalizeTypeNames();
    }

    @Override
    public String getCharacterSetResults() {
        return this.mc.getCharacterSetResults();
    }

    @Override
    public String getClientCertificateKeyStorePassword() {
        return this.mc.getClientCertificateKeyStorePassword();
    }

    @Override
    public String getClientCertificateKeyStoreType() {
        return this.mc.getClientCertificateKeyStoreType();
    }

    @Override
    public String getClientCertificateKeyStoreUrl() {
        return this.mc.getClientCertificateKeyStoreUrl();
    }

    @Override
    public String getClientInfoProvider() {
        return this.mc.getClientInfoProvider();
    }

    @Override
    public String getClobCharacterEncoding() {
        return this.mc.getClobCharacterEncoding();
    }

    @Override
    public boolean getClobberStreamingResults() {
        return this.mc.getClobberStreamingResults();
    }

    @Override
    public int getConnectTimeout() {
        return this.mc.getConnectTimeout();
    }

    @Override
    public String getConnectionCollation() {
        return this.mc.getConnectionCollation();
    }

    @Override
    public String getConnectionLifecycleInterceptors() {
        return this.mc.getConnectionLifecycleInterceptors();
    }

    @Override
    public boolean getContinueBatchOnError() {
        return this.mc.getContinueBatchOnError();
    }

    @Override
    public boolean getCreateDatabaseIfNotExist() {
        return this.mc.getCreateDatabaseIfNotExist();
    }

    @Override
    public int getDefaultFetchSize() {
        return this.mc.getDefaultFetchSize();
    }

    @Override
    public boolean getDontTrackOpenResources() {
        return this.mc.getDontTrackOpenResources();
    }

    @Override
    public boolean getDumpMetadataOnColumnNotFound() {
        return this.mc.getDumpMetadataOnColumnNotFound();
    }

    @Override
    public boolean getDumpQueriesOnException() {
        return this.mc.getDumpQueriesOnException();
    }

    @Override
    public boolean getDynamicCalendars() {
        return this.mc.getDynamicCalendars();
    }

    @Override
    public boolean getElideSetAutoCommits() {
        return this.mc.getElideSetAutoCommits();
    }

    @Override
    public boolean getEmptyStringsConvertToZero() {
        return this.mc.getEmptyStringsConvertToZero();
    }

    @Override
    public boolean getEmulateLocators() {
        return this.mc.getEmulateLocators();
    }

    @Override
    public boolean getEmulateUnsupportedPstmts() {
        return this.mc.getEmulateUnsupportedPstmts();
    }

    @Override
    public boolean getEnablePacketDebug() {
        return this.mc.getEnablePacketDebug();
    }

    @Override
    public boolean getEnableQueryTimeouts() {
        return this.mc.getEnableQueryTimeouts();
    }

    @Override
    public String getEncoding() {
        return this.mc.getEncoding();
    }

    @Override
    public boolean getExplainSlowQueries() {
        return this.mc.getExplainSlowQueries();
    }

    @Override
    public boolean getFailOverReadOnly() {
        return this.mc.getFailOverReadOnly();
    }

    @Override
    public boolean getFunctionsNeverReturnBlobs() {
        return this.mc.getFunctionsNeverReturnBlobs();
    }

    @Override
    public boolean getGatherPerfMetrics() {
        return this.mc.getGatherPerfMetrics();
    }

    @Override
    public boolean getGatherPerformanceMetrics() {
        return this.mc.getGatherPerformanceMetrics();
    }

    @Override
    public boolean getGenerateSimpleParameterMetadata() {
        return this.mc.getGenerateSimpleParameterMetadata();
    }

    @Override
    public boolean getHoldResultsOpenOverStatementClose() {
        return this.mc.getHoldResultsOpenOverStatementClose();
    }

    @Override
    public boolean getIgnoreNonTxTables() {
        return this.mc.getIgnoreNonTxTables();
    }

    @Override
    public boolean getIncludeInnodbStatusInDeadlockExceptions() {
        return this.mc.getIncludeInnodbStatusInDeadlockExceptions();
    }

    @Override
    public int getInitialTimeout() {
        return this.mc.getInitialTimeout();
    }

    @Override
    public boolean getInteractiveClient() {
        return this.mc.getInteractiveClient();
    }

    @Override
    public boolean getIsInteractiveClient() {
        return this.mc.getIsInteractiveClient();
    }

    @Override
    public boolean getJdbcCompliantTruncation() {
        return this.mc.getJdbcCompliantTruncation();
    }

    @Override
    public boolean getJdbcCompliantTruncationForReads() {
        return this.mc.getJdbcCompliantTruncationForReads();
    }

    @Override
    public String getLargeRowSizeThreshold() {
        return this.mc.getLargeRowSizeThreshold();
    }

    @Override
    public String getLoadBalanceStrategy() {
        return this.mc.getLoadBalanceStrategy();
    }

    @Override
    public String getServerAffinityOrder() {
        return this.mc.getServerAffinityOrder();
    }

    @Override
    public String getLocalSocketAddress() {
        return this.mc.getLocalSocketAddress();
    }

    @Override
    public int getLocatorFetchBufferSize() {
        return this.mc.getLocatorFetchBufferSize();
    }

    @Override
    public boolean getLogSlowQueries() {
        return this.mc.getLogSlowQueries();
    }

    @Override
    public boolean getLogXaCommands() {
        return this.mc.getLogXaCommands();
    }

    @Override
    public String getLogger() {
        return this.mc.getLogger();
    }

    @Override
    public String getLoggerClassName() {
        return this.mc.getLoggerClassName();
    }

    @Override
    public boolean getMaintainTimeStats() {
        return this.mc.getMaintainTimeStats();
    }

    @Override
    public int getMaxQuerySizeToLog() {
        return this.mc.getMaxQuerySizeToLog();
    }

    @Override
    public int getMaxReconnects() {
        return this.mc.getMaxReconnects();
    }

    @Override
    public int getMaxRows() {
        return this.mc.getMaxRows();
    }

    @Override
    public int getMetadataCacheSize() {
        return this.mc.getMetadataCacheSize();
    }

    @Override
    public int getNetTimeoutForStreamingResults() {
        return this.mc.getNetTimeoutForStreamingResults();
    }

    @Override
    public boolean getNoAccessToProcedureBodies() {
        return this.mc.getNoAccessToProcedureBodies();
    }

    @Override
    public boolean getNoDatetimeStringSync() {
        return this.mc.getNoDatetimeStringSync();
    }

    @Override
    public boolean getNoTimezoneConversionForTimeType() {
        return this.mc.getNoTimezoneConversionForTimeType();
    }

    @Override
    public boolean getNoTimezoneConversionForDateType() {
        return this.mc.getNoTimezoneConversionForDateType();
    }

    @Override
    public boolean getCacheDefaultTimezone() {
        return this.mc.getCacheDefaultTimezone();
    }

    @Override
    public boolean getNullCatalogMeansCurrent() {
        return this.mc.getNullCatalogMeansCurrent();
    }

    @Override
    public boolean getNullNamePatternMatchesAll() {
        return this.mc.getNullNamePatternMatchesAll();
    }

    @Override
    public boolean getOverrideSupportsIntegrityEnhancementFacility() {
        return this.mc.getOverrideSupportsIntegrityEnhancementFacility();
    }

    @Override
    public int getPacketDebugBufferSize() {
        return this.mc.getPacketDebugBufferSize();
    }

    @Override
    public boolean getPadCharsWithSpace() {
        return this.mc.getPadCharsWithSpace();
    }

    @Override
    public boolean getParanoid() {
        return this.mc.getParanoid();
    }

    @Override
    public boolean getPedantic() {
        return this.mc.getPedantic();
    }

    @Override
    public boolean getPinGlobalTxToPhysicalConnection() {
        return this.mc.getPinGlobalTxToPhysicalConnection();
    }

    @Override
    public boolean getPopulateInsertRowWithDefaultValues() {
        return this.mc.getPopulateInsertRowWithDefaultValues();
    }

    @Override
    public int getPrepStmtCacheSize() {
        return this.mc.getPrepStmtCacheSize();
    }

    @Override
    public int getPrepStmtCacheSqlLimit() {
        return this.mc.getPrepStmtCacheSqlLimit();
    }

    @Override
    public int getPreparedStatementCacheSize() {
        return this.mc.getPreparedStatementCacheSize();
    }

    @Override
    public int getPreparedStatementCacheSqlLimit() {
        return this.mc.getPreparedStatementCacheSqlLimit();
    }

    @Override
    public boolean getProcessEscapeCodesForPrepStmts() {
        return this.mc.getProcessEscapeCodesForPrepStmts();
    }

    @Override
    public boolean getProfileSQL() {
        return this.mc.getProfileSQL();
    }

    @Override
    public boolean getProfileSql() {
        return this.mc.getProfileSql();
    }

    @Override
    public String getPropertiesTransform() {
        return this.mc.getPropertiesTransform();
    }

    @Override
    public int getQueriesBeforeRetryMaster() {
        return this.mc.getQueriesBeforeRetryMaster();
    }

    @Override
    public boolean getReconnectAtTxEnd() {
        return this.mc.getReconnectAtTxEnd();
    }

    @Override
    public boolean getRelaxAutoCommit() {
        return this.mc.getRelaxAutoCommit();
    }

    @Override
    public int getReportMetricsIntervalMillis() {
        return this.mc.getReportMetricsIntervalMillis();
    }

    @Override
    public boolean getRequireSSL() {
        return this.mc.getRequireSSL();
    }

    @Override
    public String getResourceId() {
        return this.mc.getResourceId();
    }

    @Override
    public int getResultSetSizeThreshold() {
        return this.mc.getResultSetSizeThreshold();
    }

    @Override
    public boolean getRewriteBatchedStatements() {
        return this.mc.getRewriteBatchedStatements();
    }

    @Override
    public boolean getRollbackOnPooledClose() {
        return this.mc.getRollbackOnPooledClose();
    }

    @Override
    public boolean getRoundRobinLoadBalance() {
        return this.mc.getRoundRobinLoadBalance();
    }

    @Override
    public boolean getRunningCTS13() {
        return this.mc.getRunningCTS13();
    }

    @Override
    public int getSecondsBeforeRetryMaster() {
        return this.mc.getSecondsBeforeRetryMaster();
    }

    @Override
    public String getServerTimezone() {
        return this.mc.getServerTimezone();
    }

    @Override
    public String getSessionVariables() {
        return this.mc.getSessionVariables();
    }

    @Override
    public int getSlowQueryThresholdMillis() {
        return this.mc.getSlowQueryThresholdMillis();
    }

    @Override
    public long getSlowQueryThresholdNanos() {
        return this.mc.getSlowQueryThresholdNanos();
    }

    @Override
    public String getSocketFactory() {
        return this.mc.getSocketFactory();
    }

    @Override
    public String getSocketFactoryClassName() {
        return this.mc.getSocketFactoryClassName();
    }

    @Override
    public int getSocketTimeout() {
        return this.mc.getSocketTimeout();
    }

    @Override
    public String getStatementInterceptors() {
        return this.mc.getStatementInterceptors();
    }

    @Override
    public boolean getStrictFloatingPoint() {
        return this.mc.getStrictFloatingPoint();
    }

    @Override
    public boolean getStrictUpdates() {
        return this.mc.getStrictUpdates();
    }

    @Override
    public boolean getTcpKeepAlive() {
        return this.mc.getTcpKeepAlive();
    }

    @Override
    public boolean getTcpNoDelay() {
        return this.mc.getTcpNoDelay();
    }

    @Override
    public int getTcpRcvBuf() {
        return this.mc.getTcpRcvBuf();
    }

    @Override
    public int getTcpSndBuf() {
        return this.mc.getTcpSndBuf();
    }

    @Override
    public int getTcpTrafficClass() {
        return this.mc.getTcpTrafficClass();
    }

    @Override
    public boolean getTinyInt1isBit() {
        return this.mc.getTinyInt1isBit();
    }

    @Override
    public boolean getTraceProtocol() {
        return this.mc.getTraceProtocol();
    }

    @Override
    public boolean getTransformedBitIsBoolean() {
        return this.mc.getTransformedBitIsBoolean();
    }

    @Override
    public boolean getTreatUtilDateAsTimestamp() {
        return this.mc.getTreatUtilDateAsTimestamp();
    }

    @Override
    public String getTrustCertificateKeyStorePassword() {
        return this.mc.getTrustCertificateKeyStorePassword();
    }

    @Override
    public String getTrustCertificateKeyStoreType() {
        return this.mc.getTrustCertificateKeyStoreType();
    }

    @Override
    public String getTrustCertificateKeyStoreUrl() {
        return this.mc.getTrustCertificateKeyStoreUrl();
    }

    @Override
    public boolean getUltraDevHack() {
        return this.mc.getUltraDevHack();
    }

    @Override
    public boolean getUseBlobToStoreUTF8OutsideBMP() {
        return this.mc.getUseBlobToStoreUTF8OutsideBMP();
    }

    @Override
    public boolean getUseCompression() {
        return this.mc.getUseCompression();
    }

    @Override
    public String getUseConfigs() {
        return this.mc.getUseConfigs();
    }

    @Override
    public boolean getUseCursorFetch() {
        return this.mc.getUseCursorFetch();
    }

    @Override
    public boolean getUseDirectRowUnpack() {
        return this.mc.getUseDirectRowUnpack();
    }

    @Override
    public boolean getUseDynamicCharsetInfo() {
        return this.mc.getUseDynamicCharsetInfo();
    }

    @Override
    public boolean getUseFastDateParsing() {
        return this.mc.getUseFastDateParsing();
    }

    @Override
    public boolean getUseFastIntParsing() {
        return this.mc.getUseFastIntParsing();
    }

    @Override
    public boolean getUseGmtMillisForDatetimes() {
        return this.mc.getUseGmtMillisForDatetimes();
    }

    @Override
    public boolean getUseHostsInPrivileges() {
        return this.mc.getUseHostsInPrivileges();
    }

    @Override
    public boolean getUseInformationSchema() {
        return this.mc.getUseInformationSchema();
    }

    @Override
    public boolean getUseJDBCCompliantTimezoneShift() {
        return this.mc.getUseJDBCCompliantTimezoneShift();
    }

    @Override
    public boolean getUseJvmCharsetConverters() {
        return this.mc.getUseJvmCharsetConverters();
    }

    @Override
    public boolean getUseLocalSessionState() {
        return this.mc.getUseLocalSessionState();
    }

    @Override
    public boolean getUseNanosForElapsedTime() {
        return this.mc.getUseNanosForElapsedTime();
    }

    @Override
    public boolean getUseOldAliasMetadataBehavior() {
        return this.mc.getUseOldAliasMetadataBehavior();
    }

    @Override
    public boolean getUseOldUTF8Behavior() {
        return this.mc.getUseOldUTF8Behavior();
    }

    @Override
    public boolean getUseOnlyServerErrorMessages() {
        return this.mc.getUseOnlyServerErrorMessages();
    }

    @Override
    public boolean getUseReadAheadInput() {
        return this.mc.getUseReadAheadInput();
    }

    @Override
    public boolean getUseSSL() {
        return this.mc.getUseSSL();
    }

    @Override
    public boolean getUseSSPSCompatibleTimezoneShift() {
        return this.mc.getUseSSPSCompatibleTimezoneShift();
    }

    @Override
    public boolean getUseServerPrepStmts() {
        return this.mc.getUseServerPrepStmts();
    }

    @Override
    public boolean getUseServerPreparedStmts() {
        return this.mc.getUseServerPreparedStmts();
    }

    @Override
    public boolean getUseSqlStateCodes() {
        return this.mc.getUseSqlStateCodes();
    }

    @Override
    public boolean getUseStreamLengthsInPrepStmts() {
        return this.mc.getUseStreamLengthsInPrepStmts();
    }

    @Override
    public boolean getUseTimezone() {
        return this.mc.getUseTimezone();
    }

    @Override
    public boolean getUseUltraDevWorkAround() {
        return this.mc.getUseUltraDevWorkAround();
    }

    @Override
    public boolean getUseUnbufferedInput() {
        return this.mc.getUseUnbufferedInput();
    }

    @Override
    public boolean getUseUnicode() {
        return this.mc.getUseUnicode();
    }

    @Override
    public boolean getUseUsageAdvisor() {
        return this.mc.getUseUsageAdvisor();
    }

    @Override
    public String getUtf8OutsideBmpExcludedColumnNamePattern() {
        return this.mc.getUtf8OutsideBmpExcludedColumnNamePattern();
    }

    @Override
    public String getUtf8OutsideBmpIncludedColumnNamePattern() {
        return this.mc.getUtf8OutsideBmpIncludedColumnNamePattern();
    }

    @Override
    public boolean getYearIsDateType() {
        return this.mc.getYearIsDateType();
    }

    @Override
    public String getZeroDateTimeBehavior() {
        return this.mc.getZeroDateTimeBehavior();
    }

    @Override
    public void setAllowLoadLocalInfile(boolean property) {
        this.mc.setAllowLoadLocalInfile((boolean)property);
    }

    @Override
    public void setAllowMultiQueries(boolean property) {
        this.mc.setAllowMultiQueries((boolean)property);
    }

    @Override
    public void setAllowNanAndInf(boolean flag) {
        this.mc.setAllowNanAndInf((boolean)flag);
    }

    @Override
    public void setAllowUrlInLocalInfile(boolean flag) {
        this.mc.setAllowUrlInLocalInfile((boolean)flag);
    }

    @Override
    public void setAlwaysSendSetIsolation(boolean flag) {
        this.mc.setAlwaysSendSetIsolation((boolean)flag);
    }

    @Override
    public void setAutoClosePStmtStreams(boolean flag) {
        this.mc.setAutoClosePStmtStreams((boolean)flag);
    }

    @Override
    public void setAutoDeserialize(boolean flag) {
        this.mc.setAutoDeserialize((boolean)flag);
    }

    @Override
    public void setAutoGenerateTestcaseScript(boolean flag) {
        this.mc.setAutoGenerateTestcaseScript((boolean)flag);
    }

    @Override
    public void setAutoReconnect(boolean flag) {
        this.mc.setAutoReconnect((boolean)flag);
    }

    @Override
    public void setAutoReconnectForConnectionPools(boolean property) {
        this.mc.setAutoReconnectForConnectionPools((boolean)property);
    }

    @Override
    public void setAutoReconnectForPools(boolean flag) {
        this.mc.setAutoReconnectForPools((boolean)flag);
    }

    @Override
    public void setAutoSlowLog(boolean flag) {
        this.mc.setAutoSlowLog((boolean)flag);
    }

    @Override
    public void setBlobSendChunkSize(String value) throws SQLException {
        this.mc.setBlobSendChunkSize((String)value);
    }

    @Override
    public void setBlobsAreStrings(boolean flag) {
        this.mc.setBlobsAreStrings((boolean)flag);
    }

    @Override
    public void setCacheCallableStatements(boolean flag) {
        this.mc.setCacheCallableStatements((boolean)flag);
    }

    @Override
    public void setCacheCallableStmts(boolean flag) {
        this.mc.setCacheCallableStmts((boolean)flag);
    }

    @Override
    public void setCachePrepStmts(boolean flag) {
        this.mc.setCachePrepStmts((boolean)flag);
    }

    @Override
    public void setCachePreparedStatements(boolean flag) {
        this.mc.setCachePreparedStatements((boolean)flag);
    }

    @Override
    public void setCacheResultSetMetadata(boolean property) {
        this.mc.setCacheResultSetMetadata((boolean)property);
    }

    @Override
    public void setCacheServerConfiguration(boolean flag) {
        this.mc.setCacheServerConfiguration((boolean)flag);
    }

    @Override
    public void setCallableStatementCacheSize(int size) throws SQLException {
        this.mc.setCallableStatementCacheSize((int)size);
    }

    @Override
    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        this.mc.setCallableStmtCacheSize((int)cacheSize);
    }

    @Override
    public void setCapitalizeDBMDTypes(boolean property) {
        this.mc.setCapitalizeDBMDTypes((boolean)property);
    }

    @Override
    public void setCapitalizeTypeNames(boolean flag) {
        this.mc.setCapitalizeTypeNames((boolean)flag);
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        this.mc.setCharacterEncoding((String)encoding);
    }

    @Override
    public void setCharacterSetResults(String characterSet) {
        this.mc.setCharacterSetResults((String)characterSet);
    }

    @Override
    public void setClientCertificateKeyStorePassword(String value) {
        this.mc.setClientCertificateKeyStorePassword((String)value);
    }

    @Override
    public void setClientCertificateKeyStoreType(String value) {
        this.mc.setClientCertificateKeyStoreType((String)value);
    }

    @Override
    public void setClientCertificateKeyStoreUrl(String value) {
        this.mc.setClientCertificateKeyStoreUrl((String)value);
    }

    @Override
    public void setClientInfoProvider(String classname) {
        this.mc.setClientInfoProvider((String)classname);
    }

    @Override
    public void setClobCharacterEncoding(String encoding) {
        this.mc.setClobCharacterEncoding((String)encoding);
    }

    @Override
    public void setClobberStreamingResults(boolean flag) {
        this.mc.setClobberStreamingResults((boolean)flag);
    }

    @Override
    public void setConnectTimeout(int timeoutMs) throws SQLException {
        this.mc.setConnectTimeout((int)timeoutMs);
    }

    @Override
    public void setConnectionCollation(String collation) {
        this.mc.setConnectionCollation((String)collation);
    }

    @Override
    public void setConnectionLifecycleInterceptors(String interceptors) {
        this.mc.setConnectionLifecycleInterceptors((String)interceptors);
    }

    @Override
    public void setContinueBatchOnError(boolean property) {
        this.mc.setContinueBatchOnError((boolean)property);
    }

    @Override
    public void setCreateDatabaseIfNotExist(boolean flag) {
        this.mc.setCreateDatabaseIfNotExist((boolean)flag);
    }

    @Override
    public void setDefaultFetchSize(int n) throws SQLException {
        this.mc.setDefaultFetchSize((int)n);
    }

    @Override
    public void setDetectServerPreparedStmts(boolean property) {
        this.mc.setDetectServerPreparedStmts((boolean)property);
    }

    @Override
    public void setDontTrackOpenResources(boolean flag) {
        this.mc.setDontTrackOpenResources((boolean)flag);
    }

    @Override
    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        this.mc.setDumpMetadataOnColumnNotFound((boolean)flag);
    }

    @Override
    public void setDumpQueriesOnException(boolean flag) {
        this.mc.setDumpQueriesOnException((boolean)flag);
    }

    @Override
    public void setDynamicCalendars(boolean flag) {
        this.mc.setDynamicCalendars((boolean)flag);
    }

    @Override
    public void setElideSetAutoCommits(boolean flag) {
        this.mc.setElideSetAutoCommits((boolean)flag);
    }

    @Override
    public void setEmptyStringsConvertToZero(boolean flag) {
        this.mc.setEmptyStringsConvertToZero((boolean)flag);
    }

    @Override
    public void setEmulateLocators(boolean property) {
        this.mc.setEmulateLocators((boolean)property);
    }

    @Override
    public void setEmulateUnsupportedPstmts(boolean flag) {
        this.mc.setEmulateUnsupportedPstmts((boolean)flag);
    }

    @Override
    public void setEnablePacketDebug(boolean flag) {
        this.mc.setEnablePacketDebug((boolean)flag);
    }

    @Override
    public void setEnableQueryTimeouts(boolean flag) {
        this.mc.setEnableQueryTimeouts((boolean)flag);
    }

    @Override
    public void setEncoding(String property) {
        this.mc.setEncoding((String)property);
    }

    @Override
    public void setExplainSlowQueries(boolean flag) {
        this.mc.setExplainSlowQueries((boolean)flag);
    }

    @Override
    public void setFailOverReadOnly(boolean flag) {
        this.mc.setFailOverReadOnly((boolean)flag);
    }

    @Override
    public void setFunctionsNeverReturnBlobs(boolean flag) {
        this.mc.setFunctionsNeverReturnBlobs((boolean)flag);
    }

    @Override
    public void setGatherPerfMetrics(boolean flag) {
        this.mc.setGatherPerfMetrics((boolean)flag);
    }

    @Override
    public void setGatherPerformanceMetrics(boolean flag) {
        this.mc.setGatherPerformanceMetrics((boolean)flag);
    }

    @Override
    public void setGenerateSimpleParameterMetadata(boolean flag) {
        this.mc.setGenerateSimpleParameterMetadata((boolean)flag);
    }

    @Override
    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        this.mc.setHoldResultsOpenOverStatementClose((boolean)flag);
    }

    @Override
    public void setIgnoreNonTxTables(boolean property) {
        this.mc.setIgnoreNonTxTables((boolean)property);
    }

    @Override
    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        this.mc.setIncludeInnodbStatusInDeadlockExceptions((boolean)flag);
    }

    @Override
    public void setInitialTimeout(int property) throws SQLException {
        this.mc.setInitialTimeout((int)property);
    }

    @Override
    public void setInteractiveClient(boolean property) {
        this.mc.setInteractiveClient((boolean)property);
    }

    @Override
    public void setIsInteractiveClient(boolean property) {
        this.mc.setIsInteractiveClient((boolean)property);
    }

    @Override
    public void setJdbcCompliantTruncation(boolean flag) {
        this.mc.setJdbcCompliantTruncation((boolean)flag);
    }

    @Override
    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
        this.mc.setJdbcCompliantTruncationForReads((boolean)jdbcCompliantTruncationForReads);
    }

    @Override
    public void setLargeRowSizeThreshold(String value) throws SQLException {
        this.mc.setLargeRowSizeThreshold((String)value);
    }

    @Override
    public void setLoadBalanceStrategy(String strategy) {
        this.mc.setLoadBalanceStrategy((String)strategy);
    }

    @Override
    public void setServerAffinityOrder(String hostsList) {
        this.mc.setServerAffinityOrder((String)hostsList);
    }

    @Override
    public void setLocalSocketAddress(String address) {
        this.mc.setLocalSocketAddress((String)address);
    }

    @Override
    public void setLocatorFetchBufferSize(String value) throws SQLException {
        this.mc.setLocatorFetchBufferSize((String)value);
    }

    @Override
    public void setLogSlowQueries(boolean flag) {
        this.mc.setLogSlowQueries((boolean)flag);
    }

    @Override
    public void setLogXaCommands(boolean flag) {
        this.mc.setLogXaCommands((boolean)flag);
    }

    @Override
    public void setLogger(String property) {
        this.mc.setLogger((String)property);
    }

    @Override
    public void setLoggerClassName(String className) {
        this.mc.setLoggerClassName((String)className);
    }

    @Override
    public void setMaintainTimeStats(boolean flag) {
        this.mc.setMaintainTimeStats((boolean)flag);
    }

    @Override
    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        this.mc.setMaxQuerySizeToLog((int)sizeInBytes);
    }

    @Override
    public void setMaxReconnects(int property) throws SQLException {
        this.mc.setMaxReconnects((int)property);
    }

    @Override
    public void setMaxRows(int property) throws SQLException {
        this.mc.setMaxRows((int)property);
    }

    @Override
    public void setMetadataCacheSize(int value) throws SQLException {
        this.mc.setMetadataCacheSize((int)value);
    }

    @Override
    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        this.mc.setNetTimeoutForStreamingResults((int)value);
    }

    @Override
    public void setNoAccessToProcedureBodies(boolean flag) {
        this.mc.setNoAccessToProcedureBodies((boolean)flag);
    }

    @Override
    public void setNoDatetimeStringSync(boolean flag) {
        this.mc.setNoDatetimeStringSync((boolean)flag);
    }

    @Override
    public void setNoTimezoneConversionForTimeType(boolean flag) {
        this.mc.setNoTimezoneConversionForTimeType((boolean)flag);
    }

    @Override
    public void setNoTimezoneConversionForDateType(boolean flag) {
        this.mc.setNoTimezoneConversionForDateType((boolean)flag);
    }

    @Override
    public void setCacheDefaultTimezone(boolean flag) {
        this.mc.setCacheDefaultTimezone((boolean)flag);
    }

    @Override
    public void setNullCatalogMeansCurrent(boolean value) {
        this.mc.setNullCatalogMeansCurrent((boolean)value);
    }

    @Override
    public void setNullNamePatternMatchesAll(boolean value) {
        this.mc.setNullNamePatternMatchesAll((boolean)value);
    }

    @Override
    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        this.mc.setOverrideSupportsIntegrityEnhancementFacility((boolean)flag);
    }

    @Override
    public void setPacketDebugBufferSize(int size) throws SQLException {
        this.mc.setPacketDebugBufferSize((int)size);
    }

    @Override
    public void setPadCharsWithSpace(boolean flag) {
        this.mc.setPadCharsWithSpace((boolean)flag);
    }

    @Override
    public void setParanoid(boolean property) {
        this.mc.setParanoid((boolean)property);
    }

    @Override
    public void setPedantic(boolean property) {
        this.mc.setPedantic((boolean)property);
    }

    @Override
    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        this.mc.setPinGlobalTxToPhysicalConnection((boolean)flag);
    }

    @Override
    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        this.mc.setPopulateInsertRowWithDefaultValues((boolean)flag);
    }

    @Override
    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        this.mc.setPrepStmtCacheSize((int)cacheSize);
    }

    @Override
    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        this.mc.setPrepStmtCacheSqlLimit((int)sqlLimit);
    }

    @Override
    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        this.mc.setPreparedStatementCacheSize((int)cacheSize);
    }

    @Override
    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        this.mc.setPreparedStatementCacheSqlLimit((int)cacheSqlLimit);
    }

    @Override
    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        this.mc.setProcessEscapeCodesForPrepStmts((boolean)flag);
    }

    @Override
    public void setProfileSQL(boolean flag) {
        this.mc.setProfileSQL((boolean)flag);
    }

    @Override
    public void setProfileSql(boolean property) {
        this.mc.setProfileSql((boolean)property);
    }

    @Override
    public void setPropertiesTransform(String value) {
        this.mc.setPropertiesTransform((String)value);
    }

    @Override
    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        this.mc.setQueriesBeforeRetryMaster((int)property);
    }

    @Override
    public void setReconnectAtTxEnd(boolean property) {
        this.mc.setReconnectAtTxEnd((boolean)property);
    }

    @Override
    public void setRelaxAutoCommit(boolean property) {
        this.mc.setRelaxAutoCommit((boolean)property);
    }

    @Override
    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        this.mc.setReportMetricsIntervalMillis((int)millis);
    }

    @Override
    public void setRequireSSL(boolean property) {
        this.mc.setRequireSSL((boolean)property);
    }

    @Override
    public void setResourceId(String resourceId) {
        this.mc.setResourceId((String)resourceId);
    }

    @Override
    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        this.mc.setResultSetSizeThreshold((int)threshold);
    }

    @Override
    public void setRetainStatementAfterResultSetClose(boolean flag) {
        this.mc.setRetainStatementAfterResultSetClose((boolean)flag);
    }

    @Override
    public void setRewriteBatchedStatements(boolean flag) {
        this.mc.setRewriteBatchedStatements((boolean)flag);
    }

    @Override
    public void setRollbackOnPooledClose(boolean flag) {
        this.mc.setRollbackOnPooledClose((boolean)flag);
    }

    @Override
    public void setRoundRobinLoadBalance(boolean flag) {
        this.mc.setRoundRobinLoadBalance((boolean)flag);
    }

    @Override
    public void setRunningCTS13(boolean flag) {
        this.mc.setRunningCTS13((boolean)flag);
    }

    @Override
    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        this.mc.setSecondsBeforeRetryMaster((int)property);
    }

    @Override
    public void setServerTimezone(String property) {
        this.mc.setServerTimezone((String)property);
    }

    @Override
    public void setSessionVariables(String variables) {
        this.mc.setSessionVariables((String)variables);
    }

    @Override
    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        this.mc.setSlowQueryThresholdMillis((int)millis);
    }

    @Override
    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        this.mc.setSlowQueryThresholdNanos((long)nanos);
    }

    @Override
    public void setSocketFactory(String name) {
        this.mc.setSocketFactory((String)name);
    }

    @Override
    public void setSocketFactoryClassName(String property) {
        this.mc.setSocketFactoryClassName((String)property);
    }

    @Override
    public void setSocketTimeout(int property) throws SQLException {
        this.mc.setSocketTimeout((int)property);
    }

    @Override
    public void setStatementInterceptors(String value) {
        this.mc.setStatementInterceptors((String)value);
    }

    @Override
    public void setStrictFloatingPoint(boolean property) {
        this.mc.setStrictFloatingPoint((boolean)property);
    }

    @Override
    public void setStrictUpdates(boolean property) {
        this.mc.setStrictUpdates((boolean)property);
    }

    @Override
    public void setTcpKeepAlive(boolean flag) {
        this.mc.setTcpKeepAlive((boolean)flag);
    }

    @Override
    public void setTcpNoDelay(boolean flag) {
        this.mc.setTcpNoDelay((boolean)flag);
    }

    @Override
    public void setTcpRcvBuf(int bufSize) throws SQLException {
        this.mc.setTcpRcvBuf((int)bufSize);
    }

    @Override
    public void setTcpSndBuf(int bufSize) throws SQLException {
        this.mc.setTcpSndBuf((int)bufSize);
    }

    @Override
    public void setTcpTrafficClass(int classFlags) throws SQLException {
        this.mc.setTcpTrafficClass((int)classFlags);
    }

    @Override
    public void setTinyInt1isBit(boolean flag) {
        this.mc.setTinyInt1isBit((boolean)flag);
    }

    @Override
    public void setTraceProtocol(boolean flag) {
        this.mc.setTraceProtocol((boolean)flag);
    }

    @Override
    public void setTransformedBitIsBoolean(boolean flag) {
        this.mc.setTransformedBitIsBoolean((boolean)flag);
    }

    @Override
    public void setTreatUtilDateAsTimestamp(boolean flag) {
        this.mc.setTreatUtilDateAsTimestamp((boolean)flag);
    }

    @Override
    public void setTrustCertificateKeyStorePassword(String value) {
        this.mc.setTrustCertificateKeyStorePassword((String)value);
    }

    @Override
    public void setTrustCertificateKeyStoreType(String value) {
        this.mc.setTrustCertificateKeyStoreType((String)value);
    }

    @Override
    public void setTrustCertificateKeyStoreUrl(String value) {
        this.mc.setTrustCertificateKeyStoreUrl((String)value);
    }

    @Override
    public void setUltraDevHack(boolean flag) {
        this.mc.setUltraDevHack((boolean)flag);
    }

    @Override
    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        this.mc.setUseBlobToStoreUTF8OutsideBMP((boolean)flag);
    }

    @Override
    public void setUseCompression(boolean property) {
        this.mc.setUseCompression((boolean)property);
    }

    @Override
    public void setUseConfigs(String configs) {
        this.mc.setUseConfigs((String)configs);
    }

    @Override
    public void setUseCursorFetch(boolean flag) {
        this.mc.setUseCursorFetch((boolean)flag);
    }

    @Override
    public void setUseDirectRowUnpack(boolean flag) {
        this.mc.setUseDirectRowUnpack((boolean)flag);
    }

    @Override
    public void setUseDynamicCharsetInfo(boolean flag) {
        this.mc.setUseDynamicCharsetInfo((boolean)flag);
    }

    @Override
    public void setUseFastDateParsing(boolean flag) {
        this.mc.setUseFastDateParsing((boolean)flag);
    }

    @Override
    public void setUseFastIntParsing(boolean flag) {
        this.mc.setUseFastIntParsing((boolean)flag);
    }

    @Override
    public void setUseGmtMillisForDatetimes(boolean flag) {
        this.mc.setUseGmtMillisForDatetimes((boolean)flag);
    }

    @Override
    public void setUseHostsInPrivileges(boolean property) {
        this.mc.setUseHostsInPrivileges((boolean)property);
    }

    @Override
    public void setUseInformationSchema(boolean flag) {
        this.mc.setUseInformationSchema((boolean)flag);
    }

    @Override
    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        this.mc.setUseJDBCCompliantTimezoneShift((boolean)flag);
    }

    @Override
    public void setUseJvmCharsetConverters(boolean flag) {
        this.mc.setUseJvmCharsetConverters((boolean)flag);
    }

    @Override
    public void setUseLocalSessionState(boolean flag) {
        this.mc.setUseLocalSessionState((boolean)flag);
    }

    @Override
    public void setUseNanosForElapsedTime(boolean flag) {
        this.mc.setUseNanosForElapsedTime((boolean)flag);
    }

    @Override
    public void setUseOldAliasMetadataBehavior(boolean flag) {
        this.mc.setUseOldAliasMetadataBehavior((boolean)flag);
    }

    @Override
    public void setUseOldUTF8Behavior(boolean flag) {
        this.mc.setUseOldUTF8Behavior((boolean)flag);
    }

    @Override
    public void setUseOnlyServerErrorMessages(boolean flag) {
        this.mc.setUseOnlyServerErrorMessages((boolean)flag);
    }

    @Override
    public void setUseReadAheadInput(boolean flag) {
        this.mc.setUseReadAheadInput((boolean)flag);
    }

    @Override
    public void setUseSSL(boolean property) {
        this.mc.setUseSSL((boolean)property);
    }

    @Override
    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        this.mc.setUseSSPSCompatibleTimezoneShift((boolean)flag);
    }

    @Override
    public void setUseServerPrepStmts(boolean flag) {
        this.mc.setUseServerPrepStmts((boolean)flag);
    }

    @Override
    public void setUseServerPreparedStmts(boolean flag) {
        this.mc.setUseServerPreparedStmts((boolean)flag);
    }

    @Override
    public void setUseSqlStateCodes(boolean flag) {
        this.mc.setUseSqlStateCodes((boolean)flag);
    }

    @Override
    public void setUseStreamLengthsInPrepStmts(boolean property) {
        this.mc.setUseStreamLengthsInPrepStmts((boolean)property);
    }

    @Override
    public void setUseTimezone(boolean property) {
        this.mc.setUseTimezone((boolean)property);
    }

    @Override
    public void setUseUltraDevWorkAround(boolean property) {
        this.mc.setUseUltraDevWorkAround((boolean)property);
    }

    @Override
    public void setUseUnbufferedInput(boolean flag) {
        this.mc.setUseUnbufferedInput((boolean)flag);
    }

    @Override
    public void setUseUnicode(boolean flag) {
        this.mc.setUseUnicode((boolean)flag);
    }

    @Override
    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        this.mc.setUseUsageAdvisor((boolean)useUsageAdvisorFlag);
    }

    @Override
    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        this.mc.setUtf8OutsideBmpExcludedColumnNamePattern((String)regexPattern);
    }

    @Override
    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        this.mc.setUtf8OutsideBmpIncludedColumnNamePattern((String)regexPattern);
    }

    @Override
    public void setYearIsDateType(boolean flag) {
        this.mc.setYearIsDateType((boolean)flag);
    }

    @Override
    public void setZeroDateTimeBehavior(String behavior) {
        this.mc.setZeroDateTimeBehavior((String)behavior);
    }

    @Override
    public boolean useUnbufferedInput() {
        return this.mc.useUnbufferedInput();
    }

    @Override
    public void initializeExtension(Extension ex) throws SQLException {
        this.mc.initializeExtension((Extension)ex);
    }

    @Override
    public String getProfilerEventHandler() {
        return this.mc.getProfilerEventHandler();
    }

    @Override
    public void setProfilerEventHandler(String handler) {
        this.mc.setProfilerEventHandler((String)handler);
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return this.mc.getVerifyServerCertificate();
    }

    @Override
    public void setVerifyServerCertificate(boolean flag) {
        this.mc.setVerifyServerCertificate((boolean)flag);
    }

    @Override
    public boolean getUseLegacyDatetimeCode() {
        return this.mc.getUseLegacyDatetimeCode();
    }

    @Override
    public void setUseLegacyDatetimeCode(boolean flag) {
        this.mc.setUseLegacyDatetimeCode((boolean)flag);
    }

    @Override
    public boolean getSendFractionalSeconds() {
        return this.mc.getSendFractionalSeconds();
    }

    @Override
    public void setSendFractionalSeconds(boolean flag) {
        this.mc.setSendFractionalSeconds((boolean)flag);
    }

    @Override
    public int getSelfDestructOnPingMaxOperations() {
        return this.mc.getSelfDestructOnPingMaxOperations();
    }

    @Override
    public int getSelfDestructOnPingSecondsLifetime() {
        return this.mc.getSelfDestructOnPingSecondsLifetime();
    }

    @Override
    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        this.mc.setSelfDestructOnPingMaxOperations((int)maxOperations);
    }

    @Override
    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        this.mc.setSelfDestructOnPingSecondsLifetime((int)seconds);
    }

    @Override
    public boolean getUseColumnNamesInFindColumn() {
        return this.mc.getUseColumnNamesInFindColumn();
    }

    @Override
    public void setUseColumnNamesInFindColumn(boolean flag) {
        this.mc.setUseColumnNamesInFindColumn((boolean)flag);
    }

    @Override
    public boolean getUseLocalTransactionState() {
        return this.mc.getUseLocalTransactionState();
    }

    @Override
    public void setUseLocalTransactionState(boolean flag) {
        this.mc.setUseLocalTransactionState((boolean)flag);
    }

    @Override
    public boolean getCompensateOnDuplicateKeyUpdateCounts() {
        return this.mc.getCompensateOnDuplicateKeyUpdateCounts();
    }

    @Override
    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        this.mc.setCompensateOnDuplicateKeyUpdateCounts((boolean)flag);
    }

    @Override
    public boolean getUseAffectedRows() {
        return this.mc.getUseAffectedRows();
    }

    @Override
    public void setUseAffectedRows(boolean flag) {
        this.mc.setUseAffectedRows((boolean)flag);
    }

    @Override
    public String getPasswordCharacterEncoding() {
        return this.mc.getPasswordCharacterEncoding();
    }

    @Override
    public void setPasswordCharacterEncoding(String characterSet) {
        this.mc.setPasswordCharacterEncoding((String)characterSet);
    }

    @Override
    public int getAutoIncrementIncrement() {
        return this.mc.getAutoIncrementIncrement();
    }

    @Override
    public int getLoadBalanceBlacklistTimeout() {
        return this.mc.getLoadBalanceBlacklistTimeout();
    }

    @Override
    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
        this.mc.setLoadBalanceBlacklistTimeout((int)loadBalanceBlacklistTimeout);
    }

    @Override
    public int getLoadBalancePingTimeout() {
        return this.mc.getLoadBalancePingTimeout();
    }

    @Override
    public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
        this.mc.setLoadBalancePingTimeout((int)loadBalancePingTimeout);
    }

    @Override
    public boolean getLoadBalanceValidateConnectionOnSwapServer() {
        return this.mc.getLoadBalanceValidateConnectionOnSwapServer();
    }

    @Override
    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
        this.mc.setLoadBalanceValidateConnectionOnSwapServer((boolean)loadBalanceValidateConnectionOnSwapServer);
    }

    @Override
    public void setRetriesAllDown(int retriesAllDown) throws SQLException {
        this.mc.setRetriesAllDown((int)retriesAllDown);
    }

    @Override
    public int getRetriesAllDown() {
        return this.mc.getRetriesAllDown();
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.pooledConnection.getExceptionInterceptor();
    }

    @Override
    public String getExceptionInterceptors() {
        return this.mc.getExceptionInterceptors();
    }

    @Override
    public void setExceptionInterceptors(String exceptionInterceptors) {
        this.mc.setExceptionInterceptors((String)exceptionInterceptors);
    }

    @Override
    public boolean getQueryTimeoutKillsConnection() {
        return this.mc.getQueryTimeoutKillsConnection();
    }

    @Override
    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
        this.mc.setQueryTimeoutKillsConnection((boolean)queryTimeoutKillsConnection);
    }

    @Override
    public boolean hasSameProperties(Connection c) {
        return this.mc.hasSameProperties((Connection)c);
    }

    @Override
    public Properties getProperties() {
        return this.mc.getProperties();
    }

    @Override
    public String getHost() {
        return this.mc.getHost();
    }

    @Override
    public void setProxy(MySQLConnection conn) {
        this.mc.setProxy((MySQLConnection)conn);
    }

    @Override
    public boolean getRetainStatementAfterResultSetClose() {
        return this.mc.getRetainStatementAfterResultSetClose();
    }

    @Override
    public int getMaxAllowedPacket() {
        return this.mc.getMaxAllowedPacket();
    }

    @Override
    public String getLoadBalanceConnectionGroup() {
        return this.mc.getLoadBalanceConnectionGroup();
    }

    @Override
    public boolean getLoadBalanceEnableJMX() {
        return this.mc.getLoadBalanceEnableJMX();
    }

    @Override
    public String getLoadBalanceExceptionChecker() {
        return this.mc.getLoadBalanceExceptionChecker();
    }

    @Override
    public String getLoadBalanceSQLExceptionSubclassFailover() {
        return this.mc.getLoadBalanceSQLExceptionSubclassFailover();
    }

    @Override
    public String getLoadBalanceSQLStateFailover() {
        return this.mc.getLoadBalanceSQLStateFailover();
    }

    @Override
    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
        this.mc.setLoadBalanceConnectionGroup((String)loadBalanceConnectionGroup);
    }

    @Override
    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
        this.mc.setLoadBalanceEnableJMX((boolean)loadBalanceEnableJMX);
    }

    @Override
    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
        this.mc.setLoadBalanceExceptionChecker((String)loadBalanceExceptionChecker);
    }

    @Override
    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
        this.mc.setLoadBalanceSQLExceptionSubclassFailover((String)loadBalanceSQLExceptionSubclassFailover);
    }

    @Override
    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
        this.mc.setLoadBalanceSQLStateFailover((String)loadBalanceSQLStateFailover);
    }

    @Override
    public String getLoadBalanceAutoCommitStatementRegex() {
        return this.mc.getLoadBalanceAutoCommitStatementRegex();
    }

    @Override
    public int getLoadBalanceAutoCommitStatementThreshold() {
        return this.mc.getLoadBalanceAutoCommitStatementThreshold();
    }

    @Override
    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
        this.mc.setLoadBalanceAutoCommitStatementRegex((String)loadBalanceAutoCommitStatementRegex);
    }

    @Override
    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
        this.mc.setLoadBalanceAutoCommitStatementThreshold((int)loadBalanceAutoCommitStatementThreshold);
    }

    @Override
    public void setLoadBalanceHostRemovalGracePeriod(int loadBalanceHostRemovalGracePeriod) throws SQLException {
        this.mc.setLoadBalanceHostRemovalGracePeriod((int)loadBalanceHostRemovalGracePeriod);
    }

    @Override
    public int getLoadBalanceHostRemovalGracePeriod() {
        return this.mc.getLoadBalanceHostRemovalGracePeriod();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        try {
            this.mc.setTypeMap(map);
            return;
        }
        catch (SQLException sqlException) {
            this.checkAndFireConnectionError((SQLException)sqlException);
        }
    }

    @Override
    public boolean getIncludeThreadDumpInDeadlockExceptions() {
        return this.mc.getIncludeThreadDumpInDeadlockExceptions();
    }

    @Override
    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        this.mc.setIncludeThreadDumpInDeadlockExceptions((boolean)flag);
    }

    @Override
    public boolean getIncludeThreadNamesAsStatementComment() {
        return this.mc.getIncludeThreadNamesAsStatementComment();
    }

    @Override
    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        this.mc.setIncludeThreadNamesAsStatementComment((boolean)flag);
    }

    @Override
    public boolean isServerLocal() throws SQLException {
        return this.mc.isServerLocal();
    }

    @Override
    public void setAuthenticationPlugins(String authenticationPlugins) {
        this.mc.setAuthenticationPlugins((String)authenticationPlugins);
    }

    @Override
    public String getAuthenticationPlugins() {
        return this.mc.getAuthenticationPlugins();
    }

    @Override
    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
        this.mc.setDisabledAuthenticationPlugins((String)disabledAuthenticationPlugins);
    }

    @Override
    public String getDisabledAuthenticationPlugins() {
        return this.mc.getDisabledAuthenticationPlugins();
    }

    @Override
    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
        this.mc.setDefaultAuthenticationPlugin((String)defaultAuthenticationPlugin);
    }

    @Override
    public String getDefaultAuthenticationPlugin() {
        return this.mc.getDefaultAuthenticationPlugin();
    }

    @Override
    public void setParseInfoCacheFactory(String factoryClassname) {
        this.mc.setParseInfoCacheFactory((String)factoryClassname);
    }

    @Override
    public String getParseInfoCacheFactory() {
        return this.mc.getParseInfoCacheFactory();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.mc.setSchema((String)schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.mc.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.mc.abort((Executor)executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.mc.setNetworkTimeout((Executor)executor, (int)milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.mc.getNetworkTimeout();
    }

    @Override
    public void setServerConfigCacheFactory(String factoryClassname) {
        this.mc.setServerConfigCacheFactory((String)factoryClassname);
    }

    @Override
    public String getServerConfigCacheFactory() {
        return this.mc.getServerConfigCacheFactory();
    }

    @Override
    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
        this.mc.setDisconnectOnExpiredPasswords((boolean)disconnectOnExpiredPasswords);
    }

    @Override
    public boolean getDisconnectOnExpiredPasswords() {
        return this.mc.getDisconnectOnExpiredPasswords();
    }

    @Override
    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        this.mc.setGetProceduresReturnsFunctions((boolean)getProcedureReturnsFunctions);
    }

    @Override
    public boolean getGetProceduresReturnsFunctions() {
        return this.mc.getGetProceduresReturnsFunctions();
    }

    @Override
    public void abortInternal() throws SQLException {
        this.mc.abortInternal();
    }

    @Override
    public Object getConnectionMutex() {
        return this.mc.getConnectionMutex();
    }

    @Override
    public boolean getAllowMasterDownConnections() {
        return this.mc.getAllowMasterDownConnections();
    }

    @Override
    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        this.mc.setAllowMasterDownConnections((boolean)connectIfMasterDown);
    }

    @Override
    public boolean getAllowSlaveDownConnections() {
        return this.mc.getAllowSlaveDownConnections();
    }

    @Override
    public void setAllowSlaveDownConnections(boolean connectIfSlaveDown) {
        this.mc.setAllowSlaveDownConnections((boolean)connectIfSlaveDown);
    }

    @Override
    public boolean getReadFromMasterWhenNoSlaves() {
        return this.mc.getReadFromMasterWhenNoSlaves();
    }

    @Override
    public void setReadFromMasterWhenNoSlaves(boolean useMasterIfSlavesDown) {
        this.mc.setReadFromMasterWhenNoSlaves((boolean)useMasterIfSlavesDown);
    }

    @Override
    public boolean getReplicationEnableJMX() {
        return this.mc.getReplicationEnableJMX();
    }

    @Override
    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        this.mc.setReplicationEnableJMX((boolean)replicationEnableJMX);
    }

    @Override
    public String getConnectionAttributes() throws SQLException {
        return this.mc.getConnectionAttributes();
    }

    @Override
    public void setDetectCustomCollations(boolean detectCustomCollations) {
        this.mc.setDetectCustomCollations((boolean)detectCustomCollations);
    }

    @Override
    public boolean getDetectCustomCollations() {
        return this.mc.getDetectCustomCollations();
    }

    @Override
    public int getSessionMaxRows() {
        return this.mc.getSessionMaxRows();
    }

    @Override
    public void setSessionMaxRows(int max) throws SQLException {
        this.mc.setSessionMaxRows((int)max);
    }

    @Override
    public String getServerRSAPublicKeyFile() {
        return this.mc.getServerRSAPublicKeyFile();
    }

    @Override
    public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile) throws SQLException {
        this.mc.setServerRSAPublicKeyFile((String)serverRSAPublicKeyFile);
    }

    @Override
    public boolean getAllowPublicKeyRetrieval() {
        return this.mc.getAllowPublicKeyRetrieval();
    }

    @Override
    public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) throws SQLException {
        this.mc.setAllowPublicKeyRetrieval((boolean)allowPublicKeyRetrieval);
    }

    @Override
    public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL) {
        this.mc.setDontCheckOnDuplicateKeyUpdateInSQL((boolean)dontCheckOnDuplicateKeyUpdateInSQL);
    }

    @Override
    public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
        return this.mc.getDontCheckOnDuplicateKeyUpdateInSQL();
    }

    @Override
    public void setSocksProxyHost(String socksProxyHost) {
        this.mc.setSocksProxyHost((String)socksProxyHost);
    }

    @Override
    public String getSocksProxyHost() {
        return this.mc.getSocksProxyHost();
    }

    @Override
    public void setSocksProxyPort(int socksProxyPort) throws SQLException {
        this.mc.setSocksProxyPort((int)socksProxyPort);
    }

    @Override
    public int getSocksProxyPort() {
        return this.mc.getSocksProxyPort();
    }

    @Override
    public boolean getReadOnlyPropagatesToServer() {
        return this.mc.getReadOnlyPropagatesToServer();
    }

    @Override
    public void setReadOnlyPropagatesToServer(boolean flag) {
        this.mc.setReadOnlyPropagatesToServer((boolean)flag);
    }

    @Override
    public String getEnabledSSLCipherSuites() {
        return this.mc.getEnabledSSLCipherSuites();
    }

    @Override
    public void setEnabledSSLCipherSuites(String cipherSuites) {
        this.mc.setEnabledSSLCipherSuites((String)cipherSuites);
    }

    @Override
    public String getEnabledTLSProtocols() {
        return this.mc.getEnabledTLSProtocols();
    }

    @Override
    public void setEnabledTLSProtocols(String protocols) {
        this.mc.setEnabledTLSProtocols((String)protocols);
    }

    @Override
    public boolean getEnableEscapeProcessing() {
        return this.mc.getEnableEscapeProcessing();
    }

    @Override
    public void setEnableEscapeProcessing(boolean flag) {
        this.mc.setEnableEscapeProcessing((boolean)flag);
    }

    @Override
    public boolean isUseSSLExplicit() {
        return this.mc.isUseSSLExplicit();
    }

    static {
        if (!Util.isJdbc4()) {
            JDBC_4_CONNECTION_WRAPPER_CTOR = null;
            return;
        }
        try {
            JDBC_4_CONNECTION_WRAPPER_CTOR = Class.forName((String)"com.mysql.jdbc.jdbc2.optional.JDBC4ConnectionWrapper").getConstructor(MysqlPooledConnection.class, Connection.class, Boolean.TYPE);
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

