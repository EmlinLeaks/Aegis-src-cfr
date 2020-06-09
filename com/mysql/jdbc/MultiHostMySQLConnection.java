/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ServerPreparedStatement;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiHostMySQLConnection
implements MySQLConnection {
    protected MultiHostConnectionProxy thisAsProxy;

    public MultiHostMySQLConnection(MultiHostConnectionProxy proxy) {
        this.thisAsProxy = proxy;
    }

    protected MultiHostConnectionProxy getThisAsProxy() {
        return this.thisAsProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MySQLConnection getActiveMySQLConnection() {
        MultiHostConnectionProxy multiHostConnectionProxy = this.thisAsProxy;
        // MONITORENTER : multiHostConnectionProxy
        // MONITOREXIT : multiHostConnectionProxy
        return this.thisAsProxy.currentConnection;
    }

    @Override
    public void abortInternal() throws SQLException {
        this.getActiveMySQLConnection().abortInternal();
    }

    @Override
    public void changeUser(String userName, String newPassword) throws SQLException {
        this.getActiveMySQLConnection().changeUser((String)userName, (String)newPassword);
    }

    @Override
    public void checkClosed() throws SQLException {
        this.getActiveMySQLConnection().checkClosed();
    }

    @Deprecated
    @Override
    public void clearHasTriedMaster() {
        this.getActiveMySQLConnection().clearHasTriedMaster();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.getActiveMySQLConnection().clearWarnings();
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql, (int)autoGenKeyIndex);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql, (int[])autoGenKeyIndexes);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql, (String[])autoGenKeyColNames);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
        return this.getActiveMySQLConnection().clientPrepareStatement((String)sql);
    }

    @Override
    public void close() throws SQLException {
        this.getActiveMySQLConnection().close();
    }

    @Override
    public void commit() throws SQLException {
        this.getActiveMySQLConnection().commit();
    }

    @Override
    public void createNewIO(boolean isForReconnect) throws SQLException {
        this.getActiveMySQLConnection().createNewIO((boolean)isForReconnect);
    }

    @Override
    public java.sql.Statement createStatement() throws SQLException {
        return this.getActiveMySQLConnection().createStatement();
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getActiveMySQLConnection().createStatement((int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getActiveMySQLConnection().createStatement((int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public void dumpTestcaseQuery(String query) {
        this.getActiveMySQLConnection().dumpTestcaseQuery((String)query);
    }

    @Override
    public Connection duplicate() throws SQLException {
        return this.getActiveMySQLConnection().duplicate();
    }

    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException {
        return this.getActiveMySQLConnection().execSQL((StatementImpl)callingStatement, (String)sql, (int)maxRows, (Buffer)packet, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata, (boolean)isBatch);
    }

    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException {
        return this.getActiveMySQLConnection().execSQL((StatementImpl)callingStatement, (String)sql, (int)maxRows, (Buffer)packet, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata);
    }

    @Override
    public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException {
        return this.getActiveMySQLConnection().extractSqlFromPacket((String)possibleSqlQuery, (Buffer)queryPacket, (int)endOfQueryPacketPosition);
    }

    @Override
    public String exposeAsXml() throws SQLException {
        return this.getActiveMySQLConnection().exposeAsXml();
    }

    @Override
    public boolean getAllowLoadLocalInfile() {
        return this.getActiveMySQLConnection().getAllowLoadLocalInfile();
    }

    @Override
    public boolean getAllowMultiQueries() {
        return this.getActiveMySQLConnection().getAllowMultiQueries();
    }

    @Override
    public boolean getAllowNanAndInf() {
        return this.getActiveMySQLConnection().getAllowNanAndInf();
    }

    @Override
    public boolean getAllowUrlInLocalInfile() {
        return this.getActiveMySQLConnection().getAllowUrlInLocalInfile();
    }

    @Override
    public boolean getAlwaysSendSetIsolation() {
        return this.getActiveMySQLConnection().getAlwaysSendSetIsolation();
    }

    @Override
    public boolean getAutoClosePStmtStreams() {
        return this.getActiveMySQLConnection().getAutoClosePStmtStreams();
    }

    @Override
    public boolean getAutoDeserialize() {
        return this.getActiveMySQLConnection().getAutoDeserialize();
    }

    @Override
    public boolean getAutoGenerateTestcaseScript() {
        return this.getActiveMySQLConnection().getAutoGenerateTestcaseScript();
    }

    @Override
    public boolean getAutoReconnectForPools() {
        return this.getActiveMySQLConnection().getAutoReconnectForPools();
    }

    @Override
    public boolean getAutoSlowLog() {
        return this.getActiveMySQLConnection().getAutoSlowLog();
    }

    @Override
    public int getBlobSendChunkSize() {
        return this.getActiveMySQLConnection().getBlobSendChunkSize();
    }

    @Override
    public boolean getBlobsAreStrings() {
        return this.getActiveMySQLConnection().getBlobsAreStrings();
    }

    @Override
    public boolean getCacheCallableStatements() {
        return this.getActiveMySQLConnection().getCacheCallableStatements();
    }

    @Override
    public boolean getCacheCallableStmts() {
        return this.getActiveMySQLConnection().getCacheCallableStmts();
    }

    @Override
    public boolean getCachePrepStmts() {
        return this.getActiveMySQLConnection().getCachePrepStmts();
    }

    @Override
    public boolean getCachePreparedStatements() {
        return this.getActiveMySQLConnection().getCachePreparedStatements();
    }

    @Override
    public boolean getCacheResultSetMetadata() {
        return this.getActiveMySQLConnection().getCacheResultSetMetadata();
    }

    @Override
    public boolean getCacheServerConfiguration() {
        return this.getActiveMySQLConnection().getCacheServerConfiguration();
    }

    @Override
    public int getCallableStatementCacheSize() {
        return this.getActiveMySQLConnection().getCallableStatementCacheSize();
    }

    @Override
    public int getCallableStmtCacheSize() {
        return this.getActiveMySQLConnection().getCallableStmtCacheSize();
    }

    @Override
    public boolean getCapitalizeTypeNames() {
        return this.getActiveMySQLConnection().getCapitalizeTypeNames();
    }

    @Override
    public String getCharacterSetResults() {
        return this.getActiveMySQLConnection().getCharacterSetResults();
    }

    @Override
    public String getClientCertificateKeyStorePassword() {
        return this.getActiveMySQLConnection().getClientCertificateKeyStorePassword();
    }

    @Override
    public String getClientCertificateKeyStoreType() {
        return this.getActiveMySQLConnection().getClientCertificateKeyStoreType();
    }

    @Override
    public String getClientCertificateKeyStoreUrl() {
        return this.getActiveMySQLConnection().getClientCertificateKeyStoreUrl();
    }

    @Override
    public String getClientInfoProvider() {
        return this.getActiveMySQLConnection().getClientInfoProvider();
    }

    @Override
    public String getClobCharacterEncoding() {
        return this.getActiveMySQLConnection().getClobCharacterEncoding();
    }

    @Override
    public boolean getClobberStreamingResults() {
        return this.getActiveMySQLConnection().getClobberStreamingResults();
    }

    @Override
    public boolean getCompensateOnDuplicateKeyUpdateCounts() {
        return this.getActiveMySQLConnection().getCompensateOnDuplicateKeyUpdateCounts();
    }

    @Override
    public int getConnectTimeout() {
        return this.getActiveMySQLConnection().getConnectTimeout();
    }

    @Override
    public String getConnectionCollation() {
        return this.getActiveMySQLConnection().getConnectionCollation();
    }

    @Override
    public String getConnectionLifecycleInterceptors() {
        return this.getActiveMySQLConnection().getConnectionLifecycleInterceptors();
    }

    @Override
    public boolean getContinueBatchOnError() {
        return this.getActiveMySQLConnection().getContinueBatchOnError();
    }

    @Override
    public boolean getCreateDatabaseIfNotExist() {
        return this.getActiveMySQLConnection().getCreateDatabaseIfNotExist();
    }

    @Override
    public int getDefaultFetchSize() {
        return this.getActiveMySQLConnection().getDefaultFetchSize();
    }

    @Override
    public boolean getDontTrackOpenResources() {
        return this.getActiveMySQLConnection().getDontTrackOpenResources();
    }

    @Override
    public boolean getDumpMetadataOnColumnNotFound() {
        return this.getActiveMySQLConnection().getDumpMetadataOnColumnNotFound();
    }

    @Override
    public boolean getDumpQueriesOnException() {
        return this.getActiveMySQLConnection().getDumpQueriesOnException();
    }

    @Override
    public boolean getDynamicCalendars() {
        return this.getActiveMySQLConnection().getDynamicCalendars();
    }

    @Override
    public boolean getElideSetAutoCommits() {
        return this.getActiveMySQLConnection().getElideSetAutoCommits();
    }

    @Override
    public boolean getEmptyStringsConvertToZero() {
        return this.getActiveMySQLConnection().getEmptyStringsConvertToZero();
    }

    @Override
    public boolean getEmulateLocators() {
        return this.getActiveMySQLConnection().getEmulateLocators();
    }

    @Override
    public boolean getEmulateUnsupportedPstmts() {
        return this.getActiveMySQLConnection().getEmulateUnsupportedPstmts();
    }

    @Override
    public boolean getEnablePacketDebug() {
        return this.getActiveMySQLConnection().getEnablePacketDebug();
    }

    @Override
    public boolean getEnableQueryTimeouts() {
        return this.getActiveMySQLConnection().getEnableQueryTimeouts();
    }

    @Override
    public String getEncoding() {
        return this.getActiveMySQLConnection().getEncoding();
    }

    @Override
    public String getExceptionInterceptors() {
        return this.getActiveMySQLConnection().getExceptionInterceptors();
    }

    @Override
    public boolean getExplainSlowQueries() {
        return this.getActiveMySQLConnection().getExplainSlowQueries();
    }

    @Override
    public boolean getFailOverReadOnly() {
        return this.getActiveMySQLConnection().getFailOverReadOnly();
    }

    @Override
    public boolean getFunctionsNeverReturnBlobs() {
        return this.getActiveMySQLConnection().getFunctionsNeverReturnBlobs();
    }

    @Override
    public boolean getGatherPerfMetrics() {
        return this.getActiveMySQLConnection().getGatherPerfMetrics();
    }

    @Override
    public boolean getGatherPerformanceMetrics() {
        return this.getActiveMySQLConnection().getGatherPerformanceMetrics();
    }

    @Override
    public boolean getGenerateSimpleParameterMetadata() {
        return this.getActiveMySQLConnection().getGenerateSimpleParameterMetadata();
    }

    @Override
    public boolean getIgnoreNonTxTables() {
        return this.getActiveMySQLConnection().getIgnoreNonTxTables();
    }

    @Override
    public boolean getIncludeInnodbStatusInDeadlockExceptions() {
        return this.getActiveMySQLConnection().getIncludeInnodbStatusInDeadlockExceptions();
    }

    @Override
    public int getInitialTimeout() {
        return this.getActiveMySQLConnection().getInitialTimeout();
    }

    @Override
    public boolean getInteractiveClient() {
        return this.getActiveMySQLConnection().getInteractiveClient();
    }

    @Override
    public boolean getIsInteractiveClient() {
        return this.getActiveMySQLConnection().getIsInteractiveClient();
    }

    @Override
    public boolean getJdbcCompliantTruncation() {
        return this.getActiveMySQLConnection().getJdbcCompliantTruncation();
    }

    @Override
    public boolean getJdbcCompliantTruncationForReads() {
        return this.getActiveMySQLConnection().getJdbcCompliantTruncationForReads();
    }

    @Override
    public String getLargeRowSizeThreshold() {
        return this.getActiveMySQLConnection().getLargeRowSizeThreshold();
    }

    @Override
    public int getLoadBalanceBlacklistTimeout() {
        return this.getActiveMySQLConnection().getLoadBalanceBlacklistTimeout();
    }

    @Override
    public int getLoadBalancePingTimeout() {
        return this.getActiveMySQLConnection().getLoadBalancePingTimeout();
    }

    @Override
    public String getLoadBalanceStrategy() {
        return this.getActiveMySQLConnection().getLoadBalanceStrategy();
    }

    @Override
    public String getServerAffinityOrder() {
        return this.getActiveMySQLConnection().getServerAffinityOrder();
    }

    @Override
    public boolean getLoadBalanceValidateConnectionOnSwapServer() {
        return this.getActiveMySQLConnection().getLoadBalanceValidateConnectionOnSwapServer();
    }

    @Override
    public String getLocalSocketAddress() {
        return this.getActiveMySQLConnection().getLocalSocketAddress();
    }

    @Override
    public int getLocatorFetchBufferSize() {
        return this.getActiveMySQLConnection().getLocatorFetchBufferSize();
    }

    @Override
    public boolean getLogSlowQueries() {
        return this.getActiveMySQLConnection().getLogSlowQueries();
    }

    @Override
    public boolean getLogXaCommands() {
        return this.getActiveMySQLConnection().getLogXaCommands();
    }

    @Override
    public String getLogger() {
        return this.getActiveMySQLConnection().getLogger();
    }

    @Override
    public String getLoggerClassName() {
        return this.getActiveMySQLConnection().getLoggerClassName();
    }

    @Override
    public boolean getMaintainTimeStats() {
        return this.getActiveMySQLConnection().getMaintainTimeStats();
    }

    @Override
    public int getMaxAllowedPacket() {
        return this.getActiveMySQLConnection().getMaxAllowedPacket();
    }

    @Override
    public int getMaxQuerySizeToLog() {
        return this.getActiveMySQLConnection().getMaxQuerySizeToLog();
    }

    @Override
    public int getMaxReconnects() {
        return this.getActiveMySQLConnection().getMaxReconnects();
    }

    @Override
    public int getMaxRows() {
        return this.getActiveMySQLConnection().getMaxRows();
    }

    @Override
    public int getMetadataCacheSize() {
        return this.getActiveMySQLConnection().getMetadataCacheSize();
    }

    @Override
    public int getNetTimeoutForStreamingResults() {
        return this.getActiveMySQLConnection().getNetTimeoutForStreamingResults();
    }

    @Override
    public boolean getNoAccessToProcedureBodies() {
        return this.getActiveMySQLConnection().getNoAccessToProcedureBodies();
    }

    @Override
    public boolean getNoDatetimeStringSync() {
        return this.getActiveMySQLConnection().getNoDatetimeStringSync();
    }

    @Override
    public boolean getNoTimezoneConversionForTimeType() {
        return this.getActiveMySQLConnection().getNoTimezoneConversionForTimeType();
    }

    @Override
    public boolean getNoTimezoneConversionForDateType() {
        return this.getActiveMySQLConnection().getNoTimezoneConversionForDateType();
    }

    @Override
    public boolean getCacheDefaultTimezone() {
        return this.getActiveMySQLConnection().getCacheDefaultTimezone();
    }

    @Override
    public boolean getNullCatalogMeansCurrent() {
        return this.getActiveMySQLConnection().getNullCatalogMeansCurrent();
    }

    @Override
    public boolean getNullNamePatternMatchesAll() {
        return this.getActiveMySQLConnection().getNullNamePatternMatchesAll();
    }

    @Override
    public boolean getOverrideSupportsIntegrityEnhancementFacility() {
        return this.getActiveMySQLConnection().getOverrideSupportsIntegrityEnhancementFacility();
    }

    @Override
    public int getPacketDebugBufferSize() {
        return this.getActiveMySQLConnection().getPacketDebugBufferSize();
    }

    @Override
    public boolean getPadCharsWithSpace() {
        return this.getActiveMySQLConnection().getPadCharsWithSpace();
    }

    @Override
    public boolean getParanoid() {
        return this.getActiveMySQLConnection().getParanoid();
    }

    @Override
    public String getPasswordCharacterEncoding() {
        return this.getActiveMySQLConnection().getPasswordCharacterEncoding();
    }

    @Override
    public boolean getPedantic() {
        return this.getActiveMySQLConnection().getPedantic();
    }

    @Override
    public boolean getPinGlobalTxToPhysicalConnection() {
        return this.getActiveMySQLConnection().getPinGlobalTxToPhysicalConnection();
    }

    @Override
    public boolean getPopulateInsertRowWithDefaultValues() {
        return this.getActiveMySQLConnection().getPopulateInsertRowWithDefaultValues();
    }

    @Override
    public int getPrepStmtCacheSize() {
        return this.getActiveMySQLConnection().getPrepStmtCacheSize();
    }

    @Override
    public int getPrepStmtCacheSqlLimit() {
        return this.getActiveMySQLConnection().getPrepStmtCacheSqlLimit();
    }

    @Override
    public int getPreparedStatementCacheSize() {
        return this.getActiveMySQLConnection().getPreparedStatementCacheSize();
    }

    @Override
    public int getPreparedStatementCacheSqlLimit() {
        return this.getActiveMySQLConnection().getPreparedStatementCacheSqlLimit();
    }

    @Override
    public boolean getProcessEscapeCodesForPrepStmts() {
        return this.getActiveMySQLConnection().getProcessEscapeCodesForPrepStmts();
    }

    @Override
    public boolean getProfileSQL() {
        return this.getActiveMySQLConnection().getProfileSQL();
    }

    @Override
    public boolean getProfileSql() {
        return this.getActiveMySQLConnection().getProfileSql();
    }

    @Override
    public String getProfilerEventHandler() {
        return this.getActiveMySQLConnection().getProfilerEventHandler();
    }

    @Override
    public String getPropertiesTransform() {
        return this.getActiveMySQLConnection().getPropertiesTransform();
    }

    @Override
    public int getQueriesBeforeRetryMaster() {
        return this.getActiveMySQLConnection().getQueriesBeforeRetryMaster();
    }

    @Override
    public boolean getQueryTimeoutKillsConnection() {
        return this.getActiveMySQLConnection().getQueryTimeoutKillsConnection();
    }

    @Override
    public boolean getReconnectAtTxEnd() {
        return this.getActiveMySQLConnection().getReconnectAtTxEnd();
    }

    @Override
    public boolean getRelaxAutoCommit() {
        return this.getActiveMySQLConnection().getRelaxAutoCommit();
    }

    @Override
    public int getReportMetricsIntervalMillis() {
        return this.getActiveMySQLConnection().getReportMetricsIntervalMillis();
    }

    @Override
    public boolean getRequireSSL() {
        return this.getActiveMySQLConnection().getRequireSSL();
    }

    @Override
    public String getResourceId() {
        return this.getActiveMySQLConnection().getResourceId();
    }

    @Override
    public int getResultSetSizeThreshold() {
        return this.getActiveMySQLConnection().getResultSetSizeThreshold();
    }

    @Override
    public boolean getRetainStatementAfterResultSetClose() {
        return this.getActiveMySQLConnection().getRetainStatementAfterResultSetClose();
    }

    @Override
    public int getRetriesAllDown() {
        return this.getActiveMySQLConnection().getRetriesAllDown();
    }

    @Override
    public boolean getRewriteBatchedStatements() {
        return this.getActiveMySQLConnection().getRewriteBatchedStatements();
    }

    @Override
    public boolean getRollbackOnPooledClose() {
        return this.getActiveMySQLConnection().getRollbackOnPooledClose();
    }

    @Override
    public boolean getRoundRobinLoadBalance() {
        return this.getActiveMySQLConnection().getRoundRobinLoadBalance();
    }

    @Override
    public boolean getRunningCTS13() {
        return this.getActiveMySQLConnection().getRunningCTS13();
    }

    @Override
    public int getSecondsBeforeRetryMaster() {
        return this.getActiveMySQLConnection().getSecondsBeforeRetryMaster();
    }

    @Override
    public int getSelfDestructOnPingMaxOperations() {
        return this.getActiveMySQLConnection().getSelfDestructOnPingMaxOperations();
    }

    @Override
    public int getSelfDestructOnPingSecondsLifetime() {
        return this.getActiveMySQLConnection().getSelfDestructOnPingSecondsLifetime();
    }

    @Override
    public String getServerTimezone() {
        return this.getActiveMySQLConnection().getServerTimezone();
    }

    @Override
    public String getSessionVariables() {
        return this.getActiveMySQLConnection().getSessionVariables();
    }

    @Override
    public int getSlowQueryThresholdMillis() {
        return this.getActiveMySQLConnection().getSlowQueryThresholdMillis();
    }

    @Override
    public long getSlowQueryThresholdNanos() {
        return this.getActiveMySQLConnection().getSlowQueryThresholdNanos();
    }

    @Override
    public String getSocketFactory() {
        return this.getActiveMySQLConnection().getSocketFactory();
    }

    @Override
    public String getSocketFactoryClassName() {
        return this.getActiveMySQLConnection().getSocketFactoryClassName();
    }

    @Override
    public int getSocketTimeout() {
        return this.getActiveMySQLConnection().getSocketTimeout();
    }

    @Override
    public String getStatementInterceptors() {
        return this.getActiveMySQLConnection().getStatementInterceptors();
    }

    @Override
    public boolean getStrictFloatingPoint() {
        return this.getActiveMySQLConnection().getStrictFloatingPoint();
    }

    @Override
    public boolean getStrictUpdates() {
        return this.getActiveMySQLConnection().getStrictUpdates();
    }

    @Override
    public boolean getTcpKeepAlive() {
        return this.getActiveMySQLConnection().getTcpKeepAlive();
    }

    @Override
    public boolean getTcpNoDelay() {
        return this.getActiveMySQLConnection().getTcpNoDelay();
    }

    @Override
    public int getTcpRcvBuf() {
        return this.getActiveMySQLConnection().getTcpRcvBuf();
    }

    @Override
    public int getTcpSndBuf() {
        return this.getActiveMySQLConnection().getTcpSndBuf();
    }

    @Override
    public int getTcpTrafficClass() {
        return this.getActiveMySQLConnection().getTcpTrafficClass();
    }

    @Override
    public boolean getTinyInt1isBit() {
        return this.getActiveMySQLConnection().getTinyInt1isBit();
    }

    @Override
    public boolean getTraceProtocol() {
        return this.getActiveMySQLConnection().getTraceProtocol();
    }

    @Override
    public boolean getTransformedBitIsBoolean() {
        return this.getActiveMySQLConnection().getTransformedBitIsBoolean();
    }

    @Override
    public boolean getTreatUtilDateAsTimestamp() {
        return this.getActiveMySQLConnection().getTreatUtilDateAsTimestamp();
    }

    @Override
    public String getTrustCertificateKeyStorePassword() {
        return this.getActiveMySQLConnection().getTrustCertificateKeyStorePassword();
    }

    @Override
    public String getTrustCertificateKeyStoreType() {
        return this.getActiveMySQLConnection().getTrustCertificateKeyStoreType();
    }

    @Override
    public String getTrustCertificateKeyStoreUrl() {
        return this.getActiveMySQLConnection().getTrustCertificateKeyStoreUrl();
    }

    @Override
    public boolean getUltraDevHack() {
        return this.getActiveMySQLConnection().getUltraDevHack();
    }

    @Override
    public boolean getUseAffectedRows() {
        return this.getActiveMySQLConnection().getUseAffectedRows();
    }

    @Override
    public boolean getUseBlobToStoreUTF8OutsideBMP() {
        return this.getActiveMySQLConnection().getUseBlobToStoreUTF8OutsideBMP();
    }

    @Override
    public boolean getUseColumnNamesInFindColumn() {
        return this.getActiveMySQLConnection().getUseColumnNamesInFindColumn();
    }

    @Override
    public boolean getUseCompression() {
        return this.getActiveMySQLConnection().getUseCompression();
    }

    @Override
    public String getUseConfigs() {
        return this.getActiveMySQLConnection().getUseConfigs();
    }

    @Override
    public boolean getUseCursorFetch() {
        return this.getActiveMySQLConnection().getUseCursorFetch();
    }

    @Override
    public boolean getUseDirectRowUnpack() {
        return this.getActiveMySQLConnection().getUseDirectRowUnpack();
    }

    @Override
    public boolean getUseDynamicCharsetInfo() {
        return this.getActiveMySQLConnection().getUseDynamicCharsetInfo();
    }

    @Override
    public boolean getUseFastDateParsing() {
        return this.getActiveMySQLConnection().getUseFastDateParsing();
    }

    @Override
    public boolean getUseFastIntParsing() {
        return this.getActiveMySQLConnection().getUseFastIntParsing();
    }

    @Override
    public boolean getUseGmtMillisForDatetimes() {
        return this.getActiveMySQLConnection().getUseGmtMillisForDatetimes();
    }

    @Override
    public boolean getUseHostsInPrivileges() {
        return this.getActiveMySQLConnection().getUseHostsInPrivileges();
    }

    @Override
    public boolean getUseInformationSchema() {
        return this.getActiveMySQLConnection().getUseInformationSchema();
    }

    @Override
    public boolean getUseJDBCCompliantTimezoneShift() {
        return this.getActiveMySQLConnection().getUseJDBCCompliantTimezoneShift();
    }

    @Override
    public boolean getUseJvmCharsetConverters() {
        return this.getActiveMySQLConnection().getUseJvmCharsetConverters();
    }

    @Override
    public boolean getUseLegacyDatetimeCode() {
        return this.getActiveMySQLConnection().getUseLegacyDatetimeCode();
    }

    @Override
    public boolean getSendFractionalSeconds() {
        return this.getActiveMySQLConnection().getSendFractionalSeconds();
    }

    @Override
    public boolean getUseLocalSessionState() {
        return this.getActiveMySQLConnection().getUseLocalSessionState();
    }

    @Override
    public boolean getUseLocalTransactionState() {
        return this.getActiveMySQLConnection().getUseLocalTransactionState();
    }

    @Override
    public boolean getUseNanosForElapsedTime() {
        return this.getActiveMySQLConnection().getUseNanosForElapsedTime();
    }

    @Override
    public boolean getUseOldAliasMetadataBehavior() {
        return this.getActiveMySQLConnection().getUseOldAliasMetadataBehavior();
    }

    @Override
    public boolean getUseOldUTF8Behavior() {
        return this.getActiveMySQLConnection().getUseOldUTF8Behavior();
    }

    @Override
    public boolean getUseOnlyServerErrorMessages() {
        return this.getActiveMySQLConnection().getUseOnlyServerErrorMessages();
    }

    @Override
    public boolean getUseReadAheadInput() {
        return this.getActiveMySQLConnection().getUseReadAheadInput();
    }

    @Override
    public boolean getUseSSL() {
        return this.getActiveMySQLConnection().getUseSSL();
    }

    @Override
    public boolean getUseSSPSCompatibleTimezoneShift() {
        return this.getActiveMySQLConnection().getUseSSPSCompatibleTimezoneShift();
    }

    @Override
    public boolean getUseServerPrepStmts() {
        return this.getActiveMySQLConnection().getUseServerPrepStmts();
    }

    @Override
    public boolean getUseServerPreparedStmts() {
        return this.getActiveMySQLConnection().getUseServerPreparedStmts();
    }

    @Override
    public boolean getUseSqlStateCodes() {
        return this.getActiveMySQLConnection().getUseSqlStateCodes();
    }

    @Override
    public boolean getUseStreamLengthsInPrepStmts() {
        return this.getActiveMySQLConnection().getUseStreamLengthsInPrepStmts();
    }

    @Override
    public boolean getUseTimezone() {
        return this.getActiveMySQLConnection().getUseTimezone();
    }

    @Override
    public boolean getUseUltraDevWorkAround() {
        return this.getActiveMySQLConnection().getUseUltraDevWorkAround();
    }

    @Override
    public boolean getUseUnbufferedInput() {
        return this.getActiveMySQLConnection().getUseUnbufferedInput();
    }

    @Override
    public boolean getUseUnicode() {
        return this.getActiveMySQLConnection().getUseUnicode();
    }

    @Override
    public boolean getUseUsageAdvisor() {
        return this.getActiveMySQLConnection().getUseUsageAdvisor();
    }

    @Override
    public String getUtf8OutsideBmpExcludedColumnNamePattern() {
        return this.getActiveMySQLConnection().getUtf8OutsideBmpExcludedColumnNamePattern();
    }

    @Override
    public String getUtf8OutsideBmpIncludedColumnNamePattern() {
        return this.getActiveMySQLConnection().getUtf8OutsideBmpIncludedColumnNamePattern();
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return this.getActiveMySQLConnection().getVerifyServerCertificate();
    }

    @Override
    public boolean getYearIsDateType() {
        return this.getActiveMySQLConnection().getYearIsDateType();
    }

    @Override
    public String getZeroDateTimeBehavior() {
        return this.getActiveMySQLConnection().getZeroDateTimeBehavior();
    }

    @Override
    public void setAllowLoadLocalInfile(boolean property) {
        this.getActiveMySQLConnection().setAllowLoadLocalInfile((boolean)property);
    }

    @Override
    public void setAllowMultiQueries(boolean property) {
        this.getActiveMySQLConnection().setAllowMultiQueries((boolean)property);
    }

    @Override
    public void setAllowNanAndInf(boolean flag) {
        this.getActiveMySQLConnection().setAllowNanAndInf((boolean)flag);
    }

    @Override
    public void setAllowUrlInLocalInfile(boolean flag) {
        this.getActiveMySQLConnection().setAllowUrlInLocalInfile((boolean)flag);
    }

    @Override
    public void setAlwaysSendSetIsolation(boolean flag) {
        this.getActiveMySQLConnection().setAlwaysSendSetIsolation((boolean)flag);
    }

    @Override
    public void setAutoClosePStmtStreams(boolean flag) {
        this.getActiveMySQLConnection().setAutoClosePStmtStreams((boolean)flag);
    }

    @Override
    public void setAutoDeserialize(boolean flag) {
        this.getActiveMySQLConnection().setAutoDeserialize((boolean)flag);
    }

    @Override
    public void setAutoGenerateTestcaseScript(boolean flag) {
        this.getActiveMySQLConnection().setAutoGenerateTestcaseScript((boolean)flag);
    }

    @Override
    public void setAutoReconnect(boolean flag) {
        this.getActiveMySQLConnection().setAutoReconnect((boolean)flag);
    }

    @Override
    public void setAutoReconnectForConnectionPools(boolean property) {
        this.getActiveMySQLConnection().setAutoReconnectForConnectionPools((boolean)property);
    }

    @Override
    public void setAutoReconnectForPools(boolean flag) {
        this.getActiveMySQLConnection().setAutoReconnectForPools((boolean)flag);
    }

    @Override
    public void setAutoSlowLog(boolean flag) {
        this.getActiveMySQLConnection().setAutoSlowLog((boolean)flag);
    }

    @Override
    public void setBlobSendChunkSize(String value) throws SQLException {
        this.getActiveMySQLConnection().setBlobSendChunkSize((String)value);
    }

    @Override
    public void setBlobsAreStrings(boolean flag) {
        this.getActiveMySQLConnection().setBlobsAreStrings((boolean)flag);
    }

    @Override
    public void setCacheCallableStatements(boolean flag) {
        this.getActiveMySQLConnection().setCacheCallableStatements((boolean)flag);
    }

    @Override
    public void setCacheCallableStmts(boolean flag) {
        this.getActiveMySQLConnection().setCacheCallableStmts((boolean)flag);
    }

    @Override
    public void setCachePrepStmts(boolean flag) {
        this.getActiveMySQLConnection().setCachePrepStmts((boolean)flag);
    }

    @Override
    public void setCachePreparedStatements(boolean flag) {
        this.getActiveMySQLConnection().setCachePreparedStatements((boolean)flag);
    }

    @Override
    public void setCacheResultSetMetadata(boolean property) {
        this.getActiveMySQLConnection().setCacheResultSetMetadata((boolean)property);
    }

    @Override
    public void setCacheServerConfiguration(boolean flag) {
        this.getActiveMySQLConnection().setCacheServerConfiguration((boolean)flag);
    }

    @Override
    public void setCallableStatementCacheSize(int size) throws SQLException {
        this.getActiveMySQLConnection().setCallableStatementCacheSize((int)size);
    }

    @Override
    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        this.getActiveMySQLConnection().setCallableStmtCacheSize((int)cacheSize);
    }

    @Override
    public void setCapitalizeDBMDTypes(boolean property) {
        this.getActiveMySQLConnection().setCapitalizeDBMDTypes((boolean)property);
    }

    @Override
    public void setCapitalizeTypeNames(boolean flag) {
        this.getActiveMySQLConnection().setCapitalizeTypeNames((boolean)flag);
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        this.getActiveMySQLConnection().setCharacterEncoding((String)encoding);
    }

    @Override
    public void setCharacterSetResults(String characterSet) {
        this.getActiveMySQLConnection().setCharacterSetResults((String)characterSet);
    }

    @Override
    public void setClientCertificateKeyStorePassword(String value) {
        this.getActiveMySQLConnection().setClientCertificateKeyStorePassword((String)value);
    }

    @Override
    public void setClientCertificateKeyStoreType(String value) {
        this.getActiveMySQLConnection().setClientCertificateKeyStoreType((String)value);
    }

    @Override
    public void setClientCertificateKeyStoreUrl(String value) {
        this.getActiveMySQLConnection().setClientCertificateKeyStoreUrl((String)value);
    }

    @Override
    public void setClientInfoProvider(String classname) {
        this.getActiveMySQLConnection().setClientInfoProvider((String)classname);
    }

    @Override
    public void setClobCharacterEncoding(String encoding) {
        this.getActiveMySQLConnection().setClobCharacterEncoding((String)encoding);
    }

    @Override
    public void setClobberStreamingResults(boolean flag) {
        this.getActiveMySQLConnection().setClobberStreamingResults((boolean)flag);
    }

    @Override
    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        this.getActiveMySQLConnection().setCompensateOnDuplicateKeyUpdateCounts((boolean)flag);
    }

    @Override
    public void setConnectTimeout(int timeoutMs) throws SQLException {
        this.getActiveMySQLConnection().setConnectTimeout((int)timeoutMs);
    }

    @Override
    public void setConnectionCollation(String collation) {
        this.getActiveMySQLConnection().setConnectionCollation((String)collation);
    }

    @Override
    public void setConnectionLifecycleInterceptors(String interceptors) {
        this.getActiveMySQLConnection().setConnectionLifecycleInterceptors((String)interceptors);
    }

    @Override
    public void setContinueBatchOnError(boolean property) {
        this.getActiveMySQLConnection().setContinueBatchOnError((boolean)property);
    }

    @Override
    public void setCreateDatabaseIfNotExist(boolean flag) {
        this.getActiveMySQLConnection().setCreateDatabaseIfNotExist((boolean)flag);
    }

    @Override
    public void setDefaultFetchSize(int n) throws SQLException {
        this.getActiveMySQLConnection().setDefaultFetchSize((int)n);
    }

    @Override
    public void setDetectServerPreparedStmts(boolean property) {
        this.getActiveMySQLConnection().setDetectServerPreparedStmts((boolean)property);
    }

    @Override
    public void setDontTrackOpenResources(boolean flag) {
        this.getActiveMySQLConnection().setDontTrackOpenResources((boolean)flag);
    }

    @Override
    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        this.getActiveMySQLConnection().setDumpMetadataOnColumnNotFound((boolean)flag);
    }

    @Override
    public void setDumpQueriesOnException(boolean flag) {
        this.getActiveMySQLConnection().setDumpQueriesOnException((boolean)flag);
    }

    @Override
    public void setDynamicCalendars(boolean flag) {
        this.getActiveMySQLConnection().setDynamicCalendars((boolean)flag);
    }

    @Override
    public void setElideSetAutoCommits(boolean flag) {
        this.getActiveMySQLConnection().setElideSetAutoCommits((boolean)flag);
    }

    @Override
    public void setEmptyStringsConvertToZero(boolean flag) {
        this.getActiveMySQLConnection().setEmptyStringsConvertToZero((boolean)flag);
    }

    @Override
    public void setEmulateLocators(boolean property) {
        this.getActiveMySQLConnection().setEmulateLocators((boolean)property);
    }

    @Override
    public void setEmulateUnsupportedPstmts(boolean flag) {
        this.getActiveMySQLConnection().setEmulateUnsupportedPstmts((boolean)flag);
    }

    @Override
    public void setEnablePacketDebug(boolean flag) {
        this.getActiveMySQLConnection().setEnablePacketDebug((boolean)flag);
    }

    @Override
    public void setEnableQueryTimeouts(boolean flag) {
        this.getActiveMySQLConnection().setEnableQueryTimeouts((boolean)flag);
    }

    @Override
    public void setEncoding(String property) {
        this.getActiveMySQLConnection().setEncoding((String)property);
    }

    @Override
    public void setExceptionInterceptors(String exceptionInterceptors) {
        this.getActiveMySQLConnection().setExceptionInterceptors((String)exceptionInterceptors);
    }

    @Override
    public void setExplainSlowQueries(boolean flag) {
        this.getActiveMySQLConnection().setExplainSlowQueries((boolean)flag);
    }

    @Override
    public void setFailOverReadOnly(boolean flag) {
        this.getActiveMySQLConnection().setFailOverReadOnly((boolean)flag);
    }

    @Override
    public void setFunctionsNeverReturnBlobs(boolean flag) {
        this.getActiveMySQLConnection().setFunctionsNeverReturnBlobs((boolean)flag);
    }

    @Override
    public void setGatherPerfMetrics(boolean flag) {
        this.getActiveMySQLConnection().setGatherPerfMetrics((boolean)flag);
    }

    @Override
    public void setGatherPerformanceMetrics(boolean flag) {
        this.getActiveMySQLConnection().setGatherPerformanceMetrics((boolean)flag);
    }

    @Override
    public void setGenerateSimpleParameterMetadata(boolean flag) {
        this.getActiveMySQLConnection().setGenerateSimpleParameterMetadata((boolean)flag);
    }

    @Override
    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        this.getActiveMySQLConnection().setHoldResultsOpenOverStatementClose((boolean)flag);
    }

    @Override
    public void setIgnoreNonTxTables(boolean property) {
        this.getActiveMySQLConnection().setIgnoreNonTxTables((boolean)property);
    }

    @Override
    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        this.getActiveMySQLConnection().setIncludeInnodbStatusInDeadlockExceptions((boolean)flag);
    }

    @Override
    public void setInitialTimeout(int property) throws SQLException {
        this.getActiveMySQLConnection().setInitialTimeout((int)property);
    }

    @Override
    public void setInteractiveClient(boolean property) {
        this.getActiveMySQLConnection().setInteractiveClient((boolean)property);
    }

    @Override
    public void setIsInteractiveClient(boolean property) {
        this.getActiveMySQLConnection().setIsInteractiveClient((boolean)property);
    }

    @Override
    public void setJdbcCompliantTruncation(boolean flag) {
        this.getActiveMySQLConnection().setJdbcCompliantTruncation((boolean)flag);
    }

    @Override
    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
        this.getActiveMySQLConnection().setJdbcCompliantTruncationForReads((boolean)jdbcCompliantTruncationForReads);
    }

    @Override
    public void setLargeRowSizeThreshold(String value) throws SQLException {
        this.getActiveMySQLConnection().setLargeRowSizeThreshold((String)value);
    }

    @Override
    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
        this.getActiveMySQLConnection().setLoadBalanceBlacklistTimeout((int)loadBalanceBlacklistTimeout);
    }

    @Override
    public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
        this.getActiveMySQLConnection().setLoadBalancePingTimeout((int)loadBalancePingTimeout);
    }

    @Override
    public void setLoadBalanceStrategy(String strategy) {
        this.getActiveMySQLConnection().setLoadBalanceStrategy((String)strategy);
    }

    @Override
    public void setServerAffinityOrder(String hostsList) {
        this.getActiveMySQLConnection().setServerAffinityOrder((String)hostsList);
    }

    @Override
    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
        this.getActiveMySQLConnection().setLoadBalanceValidateConnectionOnSwapServer((boolean)loadBalanceValidateConnectionOnSwapServer);
    }

    @Override
    public void setLocalSocketAddress(String address) {
        this.getActiveMySQLConnection().setLocalSocketAddress((String)address);
    }

    @Override
    public void setLocatorFetchBufferSize(String value) throws SQLException {
        this.getActiveMySQLConnection().setLocatorFetchBufferSize((String)value);
    }

    @Override
    public void setLogSlowQueries(boolean flag) {
        this.getActiveMySQLConnection().setLogSlowQueries((boolean)flag);
    }

    @Override
    public void setLogXaCommands(boolean flag) {
        this.getActiveMySQLConnection().setLogXaCommands((boolean)flag);
    }

    @Override
    public void setLogger(String property) {
        this.getActiveMySQLConnection().setLogger((String)property);
    }

    @Override
    public void setLoggerClassName(String className) {
        this.getActiveMySQLConnection().setLoggerClassName((String)className);
    }

    @Override
    public void setMaintainTimeStats(boolean flag) {
        this.getActiveMySQLConnection().setMaintainTimeStats((boolean)flag);
    }

    @Override
    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        this.getActiveMySQLConnection().setMaxQuerySizeToLog((int)sizeInBytes);
    }

    @Override
    public void setMaxReconnects(int property) throws SQLException {
        this.getActiveMySQLConnection().setMaxReconnects((int)property);
    }

    @Override
    public void setMaxRows(int property) throws SQLException {
        this.getActiveMySQLConnection().setMaxRows((int)property);
    }

    @Override
    public void setMetadataCacheSize(int value) throws SQLException {
        this.getActiveMySQLConnection().setMetadataCacheSize((int)value);
    }

    @Override
    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        this.getActiveMySQLConnection().setNetTimeoutForStreamingResults((int)value);
    }

    @Override
    public void setNoAccessToProcedureBodies(boolean flag) {
        this.getActiveMySQLConnection().setNoAccessToProcedureBodies((boolean)flag);
    }

    @Override
    public void setNoDatetimeStringSync(boolean flag) {
        this.getActiveMySQLConnection().setNoDatetimeStringSync((boolean)flag);
    }

    @Override
    public void setNoTimezoneConversionForTimeType(boolean flag) {
        this.getActiveMySQLConnection().setNoTimezoneConversionForTimeType((boolean)flag);
    }

    @Override
    public void setNoTimezoneConversionForDateType(boolean flag) {
        this.getActiveMySQLConnection().setNoTimezoneConversionForDateType((boolean)flag);
    }

    @Override
    public void setCacheDefaultTimezone(boolean flag) {
        this.getActiveMySQLConnection().setCacheDefaultTimezone((boolean)flag);
    }

    @Override
    public void setNullCatalogMeansCurrent(boolean value) {
        this.getActiveMySQLConnection().setNullCatalogMeansCurrent((boolean)value);
    }

    @Override
    public void setNullNamePatternMatchesAll(boolean value) {
        this.getActiveMySQLConnection().setNullNamePatternMatchesAll((boolean)value);
    }

    @Override
    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        this.getActiveMySQLConnection().setOverrideSupportsIntegrityEnhancementFacility((boolean)flag);
    }

    @Override
    public void setPacketDebugBufferSize(int size) throws SQLException {
        this.getActiveMySQLConnection().setPacketDebugBufferSize((int)size);
    }

    @Override
    public void setPadCharsWithSpace(boolean flag) {
        this.getActiveMySQLConnection().setPadCharsWithSpace((boolean)flag);
    }

    @Override
    public void setParanoid(boolean property) {
        this.getActiveMySQLConnection().setParanoid((boolean)property);
    }

    @Override
    public void setPasswordCharacterEncoding(String characterSet) {
        this.getActiveMySQLConnection().setPasswordCharacterEncoding((String)characterSet);
    }

    @Override
    public void setPedantic(boolean property) {
        this.getActiveMySQLConnection().setPedantic((boolean)property);
    }

    @Override
    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        this.getActiveMySQLConnection().setPinGlobalTxToPhysicalConnection((boolean)flag);
    }

    @Override
    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        this.getActiveMySQLConnection().setPopulateInsertRowWithDefaultValues((boolean)flag);
    }

    @Override
    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        this.getActiveMySQLConnection().setPrepStmtCacheSize((int)cacheSize);
    }

    @Override
    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        this.getActiveMySQLConnection().setPrepStmtCacheSqlLimit((int)sqlLimit);
    }

    @Override
    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        this.getActiveMySQLConnection().setPreparedStatementCacheSize((int)cacheSize);
    }

    @Override
    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        this.getActiveMySQLConnection().setPreparedStatementCacheSqlLimit((int)cacheSqlLimit);
    }

    @Override
    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        this.getActiveMySQLConnection().setProcessEscapeCodesForPrepStmts((boolean)flag);
    }

    @Override
    public void setProfileSQL(boolean flag) {
        this.getActiveMySQLConnection().setProfileSQL((boolean)flag);
    }

    @Override
    public void setProfileSql(boolean property) {
        this.getActiveMySQLConnection().setProfileSql((boolean)property);
    }

    @Override
    public void setProfilerEventHandler(String handler) {
        this.getActiveMySQLConnection().setProfilerEventHandler((String)handler);
    }

    @Override
    public void setPropertiesTransform(String value) {
        this.getActiveMySQLConnection().setPropertiesTransform((String)value);
    }

    @Override
    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        this.getActiveMySQLConnection().setQueriesBeforeRetryMaster((int)property);
    }

    @Override
    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
        this.getActiveMySQLConnection().setQueryTimeoutKillsConnection((boolean)queryTimeoutKillsConnection);
    }

    @Override
    public void setReconnectAtTxEnd(boolean property) {
        this.getActiveMySQLConnection().setReconnectAtTxEnd((boolean)property);
    }

    @Override
    public void setRelaxAutoCommit(boolean property) {
        this.getActiveMySQLConnection().setRelaxAutoCommit((boolean)property);
    }

    @Override
    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        this.getActiveMySQLConnection().setReportMetricsIntervalMillis((int)millis);
    }

    @Override
    public void setRequireSSL(boolean property) {
        this.getActiveMySQLConnection().setRequireSSL((boolean)property);
    }

    @Override
    public void setResourceId(String resourceId) {
        this.getActiveMySQLConnection().setResourceId((String)resourceId);
    }

    @Override
    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        this.getActiveMySQLConnection().setResultSetSizeThreshold((int)threshold);
    }

    @Override
    public void setRetainStatementAfterResultSetClose(boolean flag) {
        this.getActiveMySQLConnection().setRetainStatementAfterResultSetClose((boolean)flag);
    }

    @Override
    public void setRetriesAllDown(int retriesAllDown) throws SQLException {
        this.getActiveMySQLConnection().setRetriesAllDown((int)retriesAllDown);
    }

    @Override
    public void setRewriteBatchedStatements(boolean flag) {
        this.getActiveMySQLConnection().setRewriteBatchedStatements((boolean)flag);
    }

    @Override
    public void setRollbackOnPooledClose(boolean flag) {
        this.getActiveMySQLConnection().setRollbackOnPooledClose((boolean)flag);
    }

    @Override
    public void setRoundRobinLoadBalance(boolean flag) {
        this.getActiveMySQLConnection().setRoundRobinLoadBalance((boolean)flag);
    }

    @Override
    public void setRunningCTS13(boolean flag) {
        this.getActiveMySQLConnection().setRunningCTS13((boolean)flag);
    }

    @Override
    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        this.getActiveMySQLConnection().setSecondsBeforeRetryMaster((int)property);
    }

    @Override
    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        this.getActiveMySQLConnection().setSelfDestructOnPingMaxOperations((int)maxOperations);
    }

    @Override
    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        this.getActiveMySQLConnection().setSelfDestructOnPingSecondsLifetime((int)seconds);
    }

    @Override
    public void setServerTimezone(String property) {
        this.getActiveMySQLConnection().setServerTimezone((String)property);
    }

    @Override
    public void setSessionVariables(String variables) {
        this.getActiveMySQLConnection().setSessionVariables((String)variables);
    }

    @Override
    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        this.getActiveMySQLConnection().setSlowQueryThresholdMillis((int)millis);
    }

    @Override
    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        this.getActiveMySQLConnection().setSlowQueryThresholdNanos((long)nanos);
    }

    @Override
    public void setSocketFactory(String name) {
        this.getActiveMySQLConnection().setSocketFactory((String)name);
    }

    @Override
    public void setSocketFactoryClassName(String property) {
        this.getActiveMySQLConnection().setSocketFactoryClassName((String)property);
    }

    @Override
    public void setSocketTimeout(int property) throws SQLException {
        this.getActiveMySQLConnection().setSocketTimeout((int)property);
    }

    @Override
    public void setStatementInterceptors(String value) {
        this.getActiveMySQLConnection().setStatementInterceptors((String)value);
    }

    @Override
    public void setStrictFloatingPoint(boolean property) {
        this.getActiveMySQLConnection().setStrictFloatingPoint((boolean)property);
    }

    @Override
    public void setStrictUpdates(boolean property) {
        this.getActiveMySQLConnection().setStrictUpdates((boolean)property);
    }

    @Override
    public void setTcpKeepAlive(boolean flag) {
        this.getActiveMySQLConnection().setTcpKeepAlive((boolean)flag);
    }

    @Override
    public void setTcpNoDelay(boolean flag) {
        this.getActiveMySQLConnection().setTcpNoDelay((boolean)flag);
    }

    @Override
    public void setTcpRcvBuf(int bufSize) throws SQLException {
        this.getActiveMySQLConnection().setTcpRcvBuf((int)bufSize);
    }

    @Override
    public void setTcpSndBuf(int bufSize) throws SQLException {
        this.getActiveMySQLConnection().setTcpSndBuf((int)bufSize);
    }

    @Override
    public void setTcpTrafficClass(int classFlags) throws SQLException {
        this.getActiveMySQLConnection().setTcpTrafficClass((int)classFlags);
    }

    @Override
    public void setTinyInt1isBit(boolean flag) {
        this.getActiveMySQLConnection().setTinyInt1isBit((boolean)flag);
    }

    @Override
    public void setTraceProtocol(boolean flag) {
        this.getActiveMySQLConnection().setTraceProtocol((boolean)flag);
    }

    @Override
    public void setTransformedBitIsBoolean(boolean flag) {
        this.getActiveMySQLConnection().setTransformedBitIsBoolean((boolean)flag);
    }

    @Override
    public void setTreatUtilDateAsTimestamp(boolean flag) {
        this.getActiveMySQLConnection().setTreatUtilDateAsTimestamp((boolean)flag);
    }

    @Override
    public void setTrustCertificateKeyStorePassword(String value) {
        this.getActiveMySQLConnection().setTrustCertificateKeyStorePassword((String)value);
    }

    @Override
    public void setTrustCertificateKeyStoreType(String value) {
        this.getActiveMySQLConnection().setTrustCertificateKeyStoreType((String)value);
    }

    @Override
    public void setTrustCertificateKeyStoreUrl(String value) {
        this.getActiveMySQLConnection().setTrustCertificateKeyStoreUrl((String)value);
    }

    @Override
    public void setUltraDevHack(boolean flag) {
        this.getActiveMySQLConnection().setUltraDevHack((boolean)flag);
    }

    @Override
    public void setUseAffectedRows(boolean flag) {
        this.getActiveMySQLConnection().setUseAffectedRows((boolean)flag);
    }

    @Override
    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        this.getActiveMySQLConnection().setUseBlobToStoreUTF8OutsideBMP((boolean)flag);
    }

    @Override
    public void setUseColumnNamesInFindColumn(boolean flag) {
        this.getActiveMySQLConnection().setUseColumnNamesInFindColumn((boolean)flag);
    }

    @Override
    public void setUseCompression(boolean property) {
        this.getActiveMySQLConnection().setUseCompression((boolean)property);
    }

    @Override
    public void setUseConfigs(String configs) {
        this.getActiveMySQLConnection().setUseConfigs((String)configs);
    }

    @Override
    public void setUseCursorFetch(boolean flag) {
        this.getActiveMySQLConnection().setUseCursorFetch((boolean)flag);
    }

    @Override
    public void setUseDirectRowUnpack(boolean flag) {
        this.getActiveMySQLConnection().setUseDirectRowUnpack((boolean)flag);
    }

    @Override
    public void setUseDynamicCharsetInfo(boolean flag) {
        this.getActiveMySQLConnection().setUseDynamicCharsetInfo((boolean)flag);
    }

    @Override
    public void setUseFastDateParsing(boolean flag) {
        this.getActiveMySQLConnection().setUseFastDateParsing((boolean)flag);
    }

    @Override
    public void setUseFastIntParsing(boolean flag) {
        this.getActiveMySQLConnection().setUseFastIntParsing((boolean)flag);
    }

    @Override
    public void setUseGmtMillisForDatetimes(boolean flag) {
        this.getActiveMySQLConnection().setUseGmtMillisForDatetimes((boolean)flag);
    }

    @Override
    public void setUseHostsInPrivileges(boolean property) {
        this.getActiveMySQLConnection().setUseHostsInPrivileges((boolean)property);
    }

    @Override
    public void setUseInformationSchema(boolean flag) {
        this.getActiveMySQLConnection().setUseInformationSchema((boolean)flag);
    }

    @Override
    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        this.getActiveMySQLConnection().setUseJDBCCompliantTimezoneShift((boolean)flag);
    }

    @Override
    public void setUseJvmCharsetConverters(boolean flag) {
        this.getActiveMySQLConnection().setUseJvmCharsetConverters((boolean)flag);
    }

    @Override
    public void setUseLegacyDatetimeCode(boolean flag) {
        this.getActiveMySQLConnection().setUseLegacyDatetimeCode((boolean)flag);
    }

    @Override
    public void setSendFractionalSeconds(boolean flag) {
        this.getActiveMySQLConnection().setSendFractionalSeconds((boolean)flag);
    }

    @Override
    public void setUseLocalSessionState(boolean flag) {
        this.getActiveMySQLConnection().setUseLocalSessionState((boolean)flag);
    }

    @Override
    public void setUseLocalTransactionState(boolean flag) {
        this.getActiveMySQLConnection().setUseLocalTransactionState((boolean)flag);
    }

    @Override
    public void setUseNanosForElapsedTime(boolean flag) {
        this.getActiveMySQLConnection().setUseNanosForElapsedTime((boolean)flag);
    }

    @Override
    public void setUseOldAliasMetadataBehavior(boolean flag) {
        this.getActiveMySQLConnection().setUseOldAliasMetadataBehavior((boolean)flag);
    }

    @Override
    public void setUseOldUTF8Behavior(boolean flag) {
        this.getActiveMySQLConnection().setUseOldUTF8Behavior((boolean)flag);
    }

    @Override
    public void setUseOnlyServerErrorMessages(boolean flag) {
        this.getActiveMySQLConnection().setUseOnlyServerErrorMessages((boolean)flag);
    }

    @Override
    public void setUseReadAheadInput(boolean flag) {
        this.getActiveMySQLConnection().setUseReadAheadInput((boolean)flag);
    }

    @Override
    public void setUseSSL(boolean property) {
        this.getActiveMySQLConnection().setUseSSL((boolean)property);
    }

    @Override
    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        this.getActiveMySQLConnection().setUseSSPSCompatibleTimezoneShift((boolean)flag);
    }

    @Override
    public void setUseServerPrepStmts(boolean flag) {
        this.getActiveMySQLConnection().setUseServerPrepStmts((boolean)flag);
    }

    @Override
    public void setUseServerPreparedStmts(boolean flag) {
        this.getActiveMySQLConnection().setUseServerPreparedStmts((boolean)flag);
    }

    @Override
    public void setUseSqlStateCodes(boolean flag) {
        this.getActiveMySQLConnection().setUseSqlStateCodes((boolean)flag);
    }

    @Override
    public void setUseStreamLengthsInPrepStmts(boolean property) {
        this.getActiveMySQLConnection().setUseStreamLengthsInPrepStmts((boolean)property);
    }

    @Override
    public void setUseTimezone(boolean property) {
        this.getActiveMySQLConnection().setUseTimezone((boolean)property);
    }

    @Override
    public void setUseUltraDevWorkAround(boolean property) {
        this.getActiveMySQLConnection().setUseUltraDevWorkAround((boolean)property);
    }

    @Override
    public void setUseUnbufferedInput(boolean flag) {
        this.getActiveMySQLConnection().setUseUnbufferedInput((boolean)flag);
    }

    @Override
    public void setUseUnicode(boolean flag) {
        this.getActiveMySQLConnection().setUseUnicode((boolean)flag);
    }

    @Override
    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        this.getActiveMySQLConnection().setUseUsageAdvisor((boolean)useUsageAdvisorFlag);
    }

    @Override
    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        this.getActiveMySQLConnection().setUtf8OutsideBmpExcludedColumnNamePattern((String)regexPattern);
    }

    @Override
    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        this.getActiveMySQLConnection().setUtf8OutsideBmpIncludedColumnNamePattern((String)regexPattern);
    }

    @Override
    public void setVerifyServerCertificate(boolean flag) {
        this.getActiveMySQLConnection().setVerifyServerCertificate((boolean)flag);
    }

    @Override
    public void setYearIsDateType(boolean flag) {
        this.getActiveMySQLConnection().setYearIsDateType((boolean)flag);
    }

    @Override
    public void setZeroDateTimeBehavior(String behavior) {
        this.getActiveMySQLConnection().setZeroDateTimeBehavior((String)behavior);
    }

    @Override
    public boolean useUnbufferedInput() {
        return this.getActiveMySQLConnection().useUnbufferedInput();
    }

    @Override
    public StringBuilder generateConnectionCommentBlock(StringBuilder buf) {
        return this.getActiveMySQLConnection().generateConnectionCommentBlock((StringBuilder)buf);
    }

    @Override
    public int getActiveStatementCount() {
        return this.getActiveMySQLConnection().getActiveStatementCount();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.getActiveMySQLConnection().getAutoCommit();
    }

    @Override
    public int getAutoIncrementIncrement() {
        return this.getActiveMySQLConnection().getAutoIncrementIncrement();
    }

    @Override
    public CachedResultSetMetaData getCachedMetaData(String sql) {
        return this.getActiveMySQLConnection().getCachedMetaData((String)sql);
    }

    @Override
    public Calendar getCalendarInstanceForSessionOrNew() {
        return this.getActiveMySQLConnection().getCalendarInstanceForSessionOrNew();
    }

    @Override
    public Timer getCancelTimer() {
        return this.getActiveMySQLConnection().getCancelTimer();
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.getActiveMySQLConnection().getCatalog();
    }

    @Override
    public String getCharacterSetMetadata() {
        return this.getActiveMySQLConnection().getCharacterSetMetadata();
    }

    @Override
    public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
        return this.getActiveMySQLConnection().getCharsetConverter((String)javaEncodingName);
    }

    @Deprecated
    @Override
    public String getCharsetNameForIndex(int charsetIndex) throws SQLException {
        return this.getEncodingForIndex((int)charsetIndex);
    }

    @Override
    public String getEncodingForIndex(int collationIndex) throws SQLException {
        return this.getActiveMySQLConnection().getEncodingForIndex((int)collationIndex);
    }

    @Override
    public TimeZone getDefaultTimeZone() {
        return this.getActiveMySQLConnection().getDefaultTimeZone();
    }

    @Override
    public String getErrorMessageEncoding() {
        return this.getActiveMySQLConnection().getErrorMessageEncoding();
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.getActiveMySQLConnection().getExceptionInterceptor();
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.getActiveMySQLConnection().getHoldability();
    }

    @Override
    public String getHost() {
        return this.getActiveMySQLConnection().getHost();
    }

    @Override
    public String getHostPortPair() {
        return this.getActiveMySQLConnection().getHostPortPair();
    }

    @Override
    public long getId() {
        return this.getActiveMySQLConnection().getId();
    }

    @Override
    public long getIdleFor() {
        return this.getActiveMySQLConnection().getIdleFor();
    }

    @Override
    public MysqlIO getIO() throws SQLException {
        return this.getActiveMySQLConnection().getIO();
    }

    @Deprecated
    @Override
    public MySQLConnection getLoadBalanceSafeProxy() {
        return this.getMultiHostSafeProxy();
    }

    @Override
    public MySQLConnection getMultiHostSafeProxy() {
        return this.getThisAsProxy().getProxy();
    }

    @Override
    public Log getLog() throws SQLException {
        return this.getActiveMySQLConnection().getLog();
    }

    @Override
    public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
        return this.getActiveMySQLConnection().getMaxBytesPerChar((String)javaCharsetName);
    }

    @Override
    public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException {
        return this.getActiveMySQLConnection().getMaxBytesPerChar((Integer)charsetIndex, (String)javaCharsetName);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.getActiveMySQLConnection().getMetaData();
    }

    @Override
    public java.sql.Statement getMetadataSafeStatement() throws SQLException {
        return this.getActiveMySQLConnection().getMetadataSafeStatement();
    }

    @Override
    public int getNetBufferLength() {
        return this.getActiveMySQLConnection().getNetBufferLength();
    }

    @Override
    public Properties getProperties() {
        return this.getActiveMySQLConnection().getProperties();
    }

    @Override
    public boolean getRequiresEscapingEncoder() {
        return this.getActiveMySQLConnection().getRequiresEscapingEncoder();
    }

    @Deprecated
    @Override
    public String getServerCharacterEncoding() {
        return this.getServerCharset();
    }

    @Override
    public String getServerCharset() {
        return this.getActiveMySQLConnection().getServerCharset();
    }

    @Override
    public int getServerMajorVersion() {
        return this.getActiveMySQLConnection().getServerMajorVersion();
    }

    @Override
    public int getServerMinorVersion() {
        return this.getActiveMySQLConnection().getServerMinorVersion();
    }

    @Override
    public int getServerSubMinorVersion() {
        return this.getActiveMySQLConnection().getServerSubMinorVersion();
    }

    @Override
    public TimeZone getServerTimezoneTZ() {
        return this.getActiveMySQLConnection().getServerTimezoneTZ();
    }

    @Override
    public String getServerVariable(String variableName) {
        return this.getActiveMySQLConnection().getServerVariable((String)variableName);
    }

    @Override
    public String getServerVersion() {
        return this.getActiveMySQLConnection().getServerVersion();
    }

    @Override
    public Calendar getSessionLockedCalendar() {
        return this.getActiveMySQLConnection().getSessionLockedCalendar();
    }

    @Override
    public String getStatementComment() {
        return this.getActiveMySQLConnection().getStatementComment();
    }

    @Override
    public List<StatementInterceptorV2> getStatementInterceptorsInstances() {
        return this.getActiveMySQLConnection().getStatementInterceptorsInstances();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.getActiveMySQLConnection().getTransactionIsolation();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.getActiveMySQLConnection().getTypeMap();
    }

    @Override
    public String getURL() {
        return this.getActiveMySQLConnection().getURL();
    }

    @Override
    public String getUser() {
        return this.getActiveMySQLConnection().getUser();
    }

    @Override
    public Calendar getUtcCalendar() {
        return this.getActiveMySQLConnection().getUtcCalendar();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getActiveMySQLConnection().getWarnings();
    }

    @Override
    public boolean hasSameProperties(Connection c) {
        return this.getActiveMySQLConnection().hasSameProperties((Connection)c);
    }

    @Deprecated
    @Override
    public boolean hasTriedMaster() {
        return this.getActiveMySQLConnection().hasTriedMaster();
    }

    @Override
    public void incrementNumberOfPreparedExecutes() {
        this.getActiveMySQLConnection().incrementNumberOfPreparedExecutes();
    }

    @Override
    public void incrementNumberOfPrepares() {
        this.getActiveMySQLConnection().incrementNumberOfPrepares();
    }

    @Override
    public void incrementNumberOfResultSetsCreated() {
        this.getActiveMySQLConnection().incrementNumberOfResultSetsCreated();
    }

    @Override
    public void initializeExtension(Extension ex) throws SQLException {
        this.getActiveMySQLConnection().initializeExtension((Extension)ex);
    }

    @Override
    public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
        this.getActiveMySQLConnection().initializeResultsMetadataFromCache((String)sql, (CachedResultSetMetaData)cachedMetaData, (ResultSetInternalMethods)resultSet);
    }

    @Override
    public void initializeSafeStatementInterceptors() throws SQLException {
        this.getActiveMySQLConnection().initializeSafeStatementInterceptors();
    }

    @Override
    public boolean isAbonormallyLongQuery(long millisOrNanos) {
        return this.getActiveMySQLConnection().isAbonormallyLongQuery((long)millisOrNanos);
    }

    @Override
    public boolean isClientTzUTC() {
        return this.getActiveMySQLConnection().isClientTzUTC();
    }

    @Override
    public boolean isCursorFetchEnabled() throws SQLException {
        return this.getActiveMySQLConnection().isCursorFetchEnabled();
    }

    @Override
    public boolean isInGlobalTx() {
        return this.getActiveMySQLConnection().isInGlobalTx();
    }

    @Override
    public boolean isMasterConnection() {
        return this.getThisAsProxy().isMasterConnection();
    }

    @Override
    public boolean isNoBackslashEscapesSet() {
        return this.getActiveMySQLConnection().isNoBackslashEscapesSet();
    }

    @Override
    public boolean isReadInfoMsgEnabled() {
        return this.getActiveMySQLConnection().isReadInfoMsgEnabled();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.getActiveMySQLConnection().isReadOnly();
    }

    @Override
    public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
        return this.getActiveMySQLConnection().isReadOnly((boolean)useSessionStatus);
    }

    @Override
    public boolean isRunningOnJDK13() {
        return this.getActiveMySQLConnection().isRunningOnJDK13();
    }

    @Override
    public boolean isSameResource(Connection otherConnection) {
        return this.getActiveMySQLConnection().isSameResource((Connection)otherConnection);
    }

    @Override
    public boolean isServerTzUTC() {
        return this.getActiveMySQLConnection().isServerTzUTC();
    }

    @Override
    public boolean lowerCaseTableNames() {
        return this.getActiveMySQLConnection().lowerCaseTableNames();
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.getActiveMySQLConnection().nativeSQL((String)sql);
    }

    @Override
    public boolean parserKnowsUnicode() {
        return this.getActiveMySQLConnection().parserKnowsUnicode();
    }

    @Override
    public void ping() throws SQLException {
        this.getActiveMySQLConnection().ping();
    }

    @Override
    public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
        this.getActiveMySQLConnection().pingInternal((boolean)checkForClosedConnection, (int)timeoutMillis);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getActiveMySQLConnection().prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getActiveMySQLConnection().prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.getActiveMySQLConnection().prepareCall((String)sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql, (int)autoGenKeyIndex);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql, (int[])autoGenKeyIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql, (String[])autoGenKeyColNames);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.getActiveMySQLConnection().prepareStatement((String)sql);
    }

    @Override
    public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
        this.getActiveMySQLConnection().realClose((boolean)calledExplicitly, (boolean)issueRollback, (boolean)skipLocalTeardown, (Throwable)reason);
    }

    @Override
    public void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        this.getActiveMySQLConnection().recachePreparedStatement((ServerPreparedStatement)pstmt);
    }

    @Override
    public void decachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        this.getActiveMySQLConnection().decachePreparedStatement((ServerPreparedStatement)pstmt);
    }

    @Override
    public void registerQueryExecutionTime(long queryTimeMs) {
        this.getActiveMySQLConnection().registerQueryExecutionTime((long)queryTimeMs);
    }

    @Override
    public void registerStatement(Statement stmt) {
        this.getActiveMySQLConnection().registerStatement((Statement)stmt);
    }

    @Override
    public void releaseSavepoint(Savepoint arg0) throws SQLException {
        this.getActiveMySQLConnection().releaseSavepoint((Savepoint)arg0);
    }

    @Override
    public void reportNumberOfTablesAccessed(int numTablesAccessed) {
        this.getActiveMySQLConnection().reportNumberOfTablesAccessed((int)numTablesAccessed);
    }

    @Override
    public void reportQueryTime(long millisOrNanos) {
        this.getActiveMySQLConnection().reportQueryTime((long)millisOrNanos);
    }

    @Override
    public void resetServerState() throws SQLException {
        this.getActiveMySQLConnection().resetServerState();
    }

    @Override
    public void rollback() throws SQLException {
        this.getActiveMySQLConnection().rollback();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.getActiveMySQLConnection().rollback((Savepoint)savepoint);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql, (int)autoGenKeyIndex);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql, (int[])autoGenKeyIndexes);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql, (String[])autoGenKeyColNames);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
        return this.getActiveMySQLConnection().serverPrepareStatement((String)sql);
    }

    @Override
    public boolean serverSupportsConvertFn() throws SQLException {
        return this.getActiveMySQLConnection().serverSupportsConvertFn();
    }

    @Override
    public void setAutoCommit(boolean autoCommitFlag) throws SQLException {
        this.getActiveMySQLConnection().setAutoCommit((boolean)autoCommitFlag);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.getActiveMySQLConnection().setCatalog((String)catalog);
    }

    @Override
    public void setFailedOver(boolean flag) {
        this.getActiveMySQLConnection().setFailedOver((boolean)flag);
    }

    @Override
    public void setHoldability(int arg0) throws SQLException {
        this.getActiveMySQLConnection().setHoldability((int)arg0);
    }

    @Override
    public void setInGlobalTx(boolean flag) {
        this.getActiveMySQLConnection().setInGlobalTx((boolean)flag);
    }

    @Deprecated
    @Override
    public void setPreferSlaveDuringFailover(boolean flag) {
        this.getActiveMySQLConnection().setPreferSlaveDuringFailover((boolean)flag);
    }

    @Override
    public void setProxy(MySQLConnection proxy) {
        this.getThisAsProxy().setProxy((MySQLConnection)proxy);
    }

    @Override
    public void setReadInfoMsgEnabled(boolean flag) {
        this.getActiveMySQLConnection().setReadInfoMsgEnabled((boolean)flag);
    }

    @Override
    public void setReadOnly(boolean readOnlyFlag) throws SQLException {
        this.getActiveMySQLConnection().setReadOnly((boolean)readOnlyFlag);
    }

    @Override
    public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
        this.getActiveMySQLConnection().setReadOnlyInternal((boolean)readOnlyFlag);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.getActiveMySQLConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.getActiveMySQLConnection().setSavepoint((String)name);
    }

    @Override
    public void setStatementComment(String comment) {
        this.getActiveMySQLConnection().setStatementComment((String)comment);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.getActiveMySQLConnection().setTransactionIsolation((int)level);
    }

    @Override
    public void shutdownServer() throws SQLException {
        this.getActiveMySQLConnection().shutdownServer();
    }

    @Override
    public boolean storesLowerCaseTableName() {
        return this.getActiveMySQLConnection().storesLowerCaseTableName();
    }

    @Override
    public boolean supportsIsolationLevel() {
        return this.getActiveMySQLConnection().supportsIsolationLevel();
    }

    @Override
    public boolean supportsQuotedIdentifiers() {
        return this.getActiveMySQLConnection().supportsQuotedIdentifiers();
    }

    @Override
    public boolean supportsTransactions() {
        return this.getActiveMySQLConnection().supportsTransactions();
    }

    @Override
    public void throwConnectionClosedException() throws SQLException {
        this.getActiveMySQLConnection().throwConnectionClosedException();
    }

    @Override
    public void transactionBegun() throws SQLException {
        this.getActiveMySQLConnection().transactionBegun();
    }

    @Override
    public void transactionCompleted() throws SQLException {
        this.getActiveMySQLConnection().transactionCompleted();
    }

    @Override
    public void unregisterStatement(Statement stmt) {
        this.getActiveMySQLConnection().unregisterStatement((Statement)stmt);
    }

    @Override
    public void unSafeStatementInterceptors() throws SQLException {
        this.getActiveMySQLConnection().unSafeStatementInterceptors();
    }

    @Override
    public boolean useAnsiQuotedIdentifiers() {
        return this.getActiveMySQLConnection().useAnsiQuotedIdentifiers();
    }

    @Override
    public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
        return this.getActiveMySQLConnection().versionMeetsMinimum((int)major, (int)minor, (int)subminor);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.getThisAsProxy().isClosed;
    }

    @Override
    public boolean getHoldResultsOpenOverStatementClose() {
        return this.getActiveMySQLConnection().getHoldResultsOpenOverStatementClose();
    }

    @Override
    public String getLoadBalanceConnectionGroup() {
        return this.getActiveMySQLConnection().getLoadBalanceConnectionGroup();
    }

    @Override
    public boolean getLoadBalanceEnableJMX() {
        return this.getActiveMySQLConnection().getLoadBalanceEnableJMX();
    }

    @Override
    public String getLoadBalanceExceptionChecker() {
        return this.getActiveMySQLConnection().getLoadBalanceExceptionChecker();
    }

    @Override
    public String getLoadBalanceSQLExceptionSubclassFailover() {
        return this.getActiveMySQLConnection().getLoadBalanceSQLExceptionSubclassFailover();
    }

    @Override
    public String getLoadBalanceSQLStateFailover() {
        return this.getActiveMySQLConnection().getLoadBalanceSQLStateFailover();
    }

    @Override
    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
        this.getActiveMySQLConnection().setLoadBalanceConnectionGroup((String)loadBalanceConnectionGroup);
    }

    @Override
    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
        this.getActiveMySQLConnection().setLoadBalanceEnableJMX((boolean)loadBalanceEnableJMX);
    }

    @Override
    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
        this.getActiveMySQLConnection().setLoadBalanceExceptionChecker((String)loadBalanceExceptionChecker);
    }

    @Override
    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
        this.getActiveMySQLConnection().setLoadBalanceSQLExceptionSubclassFailover((String)loadBalanceSQLExceptionSubclassFailover);
    }

    @Override
    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
        this.getActiveMySQLConnection().setLoadBalanceSQLStateFailover((String)loadBalanceSQLStateFailover);
    }

    @Override
    public void setLoadBalanceHostRemovalGracePeriod(int loadBalanceHostRemovalGracePeriod) throws SQLException {
        this.getActiveMySQLConnection().setLoadBalanceHostRemovalGracePeriod((int)loadBalanceHostRemovalGracePeriod);
    }

    @Override
    public int getLoadBalanceHostRemovalGracePeriod() {
        return this.getActiveMySQLConnection().getLoadBalanceHostRemovalGracePeriod();
    }

    @Override
    public boolean isProxySet() {
        return this.getActiveMySQLConnection().isProxySet();
    }

    @Override
    public String getLoadBalanceAutoCommitStatementRegex() {
        return this.getActiveMySQLConnection().getLoadBalanceAutoCommitStatementRegex();
    }

    @Override
    public int getLoadBalanceAutoCommitStatementThreshold() {
        return this.getActiveMySQLConnection().getLoadBalanceAutoCommitStatementThreshold();
    }

    @Override
    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
        this.getActiveMySQLConnection().setLoadBalanceAutoCommitStatementRegex((String)loadBalanceAutoCommitStatementRegex);
    }

    @Override
    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
        this.getActiveMySQLConnection().setLoadBalanceAutoCommitStatementThreshold((int)loadBalanceAutoCommitStatementThreshold);
    }

    @Override
    public boolean getIncludeThreadDumpInDeadlockExceptions() {
        return this.getActiveMySQLConnection().getIncludeThreadDumpInDeadlockExceptions();
    }

    @Override
    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        this.getActiveMySQLConnection().setIncludeThreadDumpInDeadlockExceptions((boolean)flag);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.getActiveMySQLConnection().setTypeMap(map);
    }

    @Override
    public boolean getIncludeThreadNamesAsStatementComment() {
        return this.getActiveMySQLConnection().getIncludeThreadNamesAsStatementComment();
    }

    @Override
    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        this.getActiveMySQLConnection().setIncludeThreadNamesAsStatementComment((boolean)flag);
    }

    @Override
    public boolean isServerLocal() throws SQLException {
        return this.getActiveMySQLConnection().isServerLocal();
    }

    @Override
    public void setAuthenticationPlugins(String authenticationPlugins) {
        this.getActiveMySQLConnection().setAuthenticationPlugins((String)authenticationPlugins);
    }

    @Override
    public String getAuthenticationPlugins() {
        return this.getActiveMySQLConnection().getAuthenticationPlugins();
    }

    @Override
    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
        this.getActiveMySQLConnection().setDisabledAuthenticationPlugins((String)disabledAuthenticationPlugins);
    }

    @Override
    public String getDisabledAuthenticationPlugins() {
        return this.getActiveMySQLConnection().getDisabledAuthenticationPlugins();
    }

    @Override
    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
        this.getActiveMySQLConnection().setDefaultAuthenticationPlugin((String)defaultAuthenticationPlugin);
    }

    @Override
    public String getDefaultAuthenticationPlugin() {
        return this.getActiveMySQLConnection().getDefaultAuthenticationPlugin();
    }

    @Override
    public void setParseInfoCacheFactory(String factoryClassname) {
        this.getActiveMySQLConnection().setParseInfoCacheFactory((String)factoryClassname);
    }

    @Override
    public String getParseInfoCacheFactory() {
        return this.getActiveMySQLConnection().getParseInfoCacheFactory();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.getActiveMySQLConnection().setSchema((String)schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.getActiveMySQLConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.getActiveMySQLConnection().abort((Executor)executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.getActiveMySQLConnection().setNetworkTimeout((Executor)executor, (int)milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.getActiveMySQLConnection().getNetworkTimeout();
    }

    @Override
    public void setServerConfigCacheFactory(String factoryClassname) {
        this.getActiveMySQLConnection().setServerConfigCacheFactory((String)factoryClassname);
    }

    @Override
    public String getServerConfigCacheFactory() {
        return this.getActiveMySQLConnection().getServerConfigCacheFactory();
    }

    @Override
    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
        this.getActiveMySQLConnection().setDisconnectOnExpiredPasswords((boolean)disconnectOnExpiredPasswords);
    }

    @Override
    public boolean getDisconnectOnExpiredPasswords() {
        return this.getActiveMySQLConnection().getDisconnectOnExpiredPasswords();
    }

    @Override
    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        this.getActiveMySQLConnection().setGetProceduresReturnsFunctions((boolean)getProcedureReturnsFunctions);
    }

    @Override
    public boolean getGetProceduresReturnsFunctions() {
        return this.getActiveMySQLConnection().getGetProceduresReturnsFunctions();
    }

    @Override
    public Object getConnectionMutex() {
        return this.getActiveMySQLConnection().getConnectionMutex();
    }

    @Override
    public String getConnectionAttributes() throws SQLException {
        return this.getActiveMySQLConnection().getConnectionAttributes();
    }

    @Override
    public boolean getAllowMasterDownConnections() {
        return this.getActiveMySQLConnection().getAllowMasterDownConnections();
    }

    @Override
    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        this.getActiveMySQLConnection().setAllowMasterDownConnections((boolean)connectIfMasterDown);
    }

    @Override
    public boolean getAllowSlaveDownConnections() {
        return this.getActiveMySQLConnection().getAllowSlaveDownConnections();
    }

    @Override
    public void setAllowSlaveDownConnections(boolean connectIfSlaveDown) {
        this.getActiveMySQLConnection().setAllowSlaveDownConnections((boolean)connectIfSlaveDown);
    }

    @Override
    public boolean getReadFromMasterWhenNoSlaves() {
        return this.getActiveMySQLConnection().getReadFromMasterWhenNoSlaves();
    }

    @Override
    public void setReadFromMasterWhenNoSlaves(boolean useMasterIfSlavesDown) {
        this.getActiveMySQLConnection().setReadFromMasterWhenNoSlaves((boolean)useMasterIfSlavesDown);
    }

    @Override
    public boolean getReplicationEnableJMX() {
        return this.getActiveMySQLConnection().getReplicationEnableJMX();
    }

    @Override
    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        this.getActiveMySQLConnection().setReplicationEnableJMX((boolean)replicationEnableJMX);
    }

    @Override
    public void setDetectCustomCollations(boolean detectCustomCollations) {
        this.getActiveMySQLConnection().setDetectCustomCollations((boolean)detectCustomCollations);
    }

    @Override
    public boolean getDetectCustomCollations() {
        return this.getActiveMySQLConnection().getDetectCustomCollations();
    }

    @Override
    public int getSessionMaxRows() {
        return this.getActiveMySQLConnection().getSessionMaxRows();
    }

    @Override
    public void setSessionMaxRows(int max) throws SQLException {
        this.getActiveMySQLConnection().setSessionMaxRows((int)max);
    }

    @Override
    public ProfilerEventHandler getProfilerEventHandlerInstance() {
        return this.getActiveMySQLConnection().getProfilerEventHandlerInstance();
    }

    @Override
    public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {
        this.getActiveMySQLConnection().setProfilerEventHandlerInstance((ProfilerEventHandler)h);
    }

    @Override
    public String getServerRSAPublicKeyFile() {
        return this.getActiveMySQLConnection().getServerRSAPublicKeyFile();
    }

    @Override
    public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile) throws SQLException {
        this.getActiveMySQLConnection().setServerRSAPublicKeyFile((String)serverRSAPublicKeyFile);
    }

    @Override
    public boolean getAllowPublicKeyRetrieval() {
        return this.getActiveMySQLConnection().getAllowPublicKeyRetrieval();
    }

    @Override
    public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) throws SQLException {
        this.getActiveMySQLConnection().setAllowPublicKeyRetrieval((boolean)allowPublicKeyRetrieval);
    }

    @Override
    public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL) {
        this.getActiveMySQLConnection().setDontCheckOnDuplicateKeyUpdateInSQL((boolean)dontCheckOnDuplicateKeyUpdateInSQL);
    }

    @Override
    public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
        return this.getActiveMySQLConnection().getDontCheckOnDuplicateKeyUpdateInSQL();
    }

    @Override
    public void setSocksProxyHost(String socksProxyHost) {
        this.getActiveMySQLConnection().setSocksProxyHost((String)socksProxyHost);
    }

    @Override
    public String getSocksProxyHost() {
        return this.getActiveMySQLConnection().getSocksProxyHost();
    }

    @Override
    public void setSocksProxyPort(int socksProxyPort) throws SQLException {
        this.getActiveMySQLConnection().setSocksProxyPort((int)socksProxyPort);
    }

    @Override
    public int getSocksProxyPort() {
        return this.getActiveMySQLConnection().getSocksProxyPort();
    }

    @Override
    public boolean getReadOnlyPropagatesToServer() {
        return this.getActiveMySQLConnection().getReadOnlyPropagatesToServer();
    }

    @Override
    public void setReadOnlyPropagatesToServer(boolean flag) {
        this.getActiveMySQLConnection().setReadOnlyPropagatesToServer((boolean)flag);
    }

    @Override
    public String getEnabledSSLCipherSuites() {
        return this.getActiveMySQLConnection().getEnabledSSLCipherSuites();
    }

    @Override
    public void setEnabledSSLCipherSuites(String cipherSuites) {
        this.getActiveMySQLConnection().setEnabledSSLCipherSuites((String)cipherSuites);
    }

    @Override
    public String getEnabledTLSProtocols() {
        return this.getActiveMySQLConnection().getEnabledTLSProtocols();
    }

    @Override
    public void setEnabledTLSProtocols(String protocols) {
        this.getActiveMySQLConnection().setEnabledTLSProtocols((String)protocols);
    }

    @Override
    public boolean getEnableEscapeProcessing() {
        return this.getActiveMySQLConnection().getEnableEscapeProcessing();
    }

    @Override
    public void setEnableEscapeProcessing(boolean flag) {
        this.getActiveMySQLConnection().setEnableEscapeProcessing((boolean)flag);
    }

    @Override
    public boolean isUseSSLExplicit() {
        return this.getActiveMySQLConnection().isUseSSLExplicit();
    }

    @Override
    public boolean isServerTruncatesFracSecs() {
        return this.getActiveMySQLConnection().isServerTruncatesFracSecs();
    }

    @Override
    public String getQueryTimingUnits() {
        return this.getActiveMySQLConnection().getQueryTimingUnits();
    }
}

