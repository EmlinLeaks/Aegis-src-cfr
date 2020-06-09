/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionProperties;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.PerConnectionLRUFactory;
import com.mysql.jdbc.PerVmServerConfigCacheFactory;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SocksProxySocketFactory;
import com.mysql.jdbc.StandardSocketFactory;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.StandardLogger;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.naming.Reference;

public class ConnectionPropertiesImpl
implements Serializable,
ConnectionProperties {
    private static final long serialVersionUID = 4257801713007640580L;
    private static final String CONNECTION_AND_AUTH_CATEGORY = Messages.getString((String)"ConnectionProperties.categoryConnectionAuthentication");
    private static final String NETWORK_CATEGORY = Messages.getString((String)"ConnectionProperties.categoryNetworking");
    private static final String DEBUGING_PROFILING_CATEGORY = Messages.getString((String)"ConnectionProperties.categoryDebuggingProfiling");
    private static final String HA_CATEGORY = Messages.getString((String)"ConnectionProperties.categorryHA");
    private static final String MISC_CATEGORY = Messages.getString((String)"ConnectionProperties.categoryMisc");
    private static final String PERFORMANCE_CATEGORY = Messages.getString((String)"ConnectionProperties.categoryPerformance");
    private static final String SECURITY_CATEGORY = Messages.getString((String)"ConnectionProperties.categorySecurity");
    private static final String[] PROPERTY_CATEGORIES = new String[]{CONNECTION_AND_AUTH_CATEGORY, NETWORK_CATEGORY, HA_CATEGORY, SECURITY_CATEGORY, PERFORMANCE_CATEGORY, DEBUGING_PROFILING_CATEGORY, MISC_CATEGORY};
    private static final ArrayList<Field> PROPERTY_LIST = new ArrayList<E>();
    private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
    protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
    protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
    protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
    private BooleanConnectionProperty allowLoadLocalInfile = new BooleanConnectionProperty((String)"allowLoadLocalInfile", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.loadDataLocal"), (String)"3.0.3", (String)SECURITY_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty allowMultiQueries = new BooleanConnectionProperty((String)"allowMultiQueries", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowMultiQueries"), (String)"3.1.1", (String)SECURITY_CATEGORY, (int)1);
    private BooleanConnectionProperty allowNanAndInf = new BooleanConnectionProperty((String)"allowNanAndInf", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowNANandINF"), (String)"3.1.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty allowUrlInLocalInfile = new BooleanConnectionProperty((String)"allowUrlInLocalInfile", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowUrlInLoadLocal"), (String)"3.1.4", (String)SECURITY_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty alwaysSendSetIsolation = new BooleanConnectionProperty((String)"alwaysSendSetIsolation", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.alwaysSendSetIsolation"), (String)"3.1.7", (String)PERFORMANCE_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty autoClosePStmtStreams = new BooleanConnectionProperty((String)"autoClosePStmtStreams", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.autoClosePstmtStreams"), (String)"3.1.12", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty replicationConnectionGroup = new StringConnectionProperty((String)"replicationConnectionGroup", null, (String)Messages.getString((String)"ConnectionProperties.replicationConnectionGroup"), (String)"5.1.27", (String)HA_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty allowMasterDownConnections = new BooleanConnectionProperty((String)"allowMasterDownConnections", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowMasterDownConnections"), (String)"5.1.27", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty allowSlaveDownConnections = new BooleanConnectionProperty((String)"allowSlaveDownConnections", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowSlaveDownConnections"), (String)"5.1.38", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty readFromMasterWhenNoSlaves = new BooleanConnectionProperty((String)"readFromMasterWhenNoSlaves", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.readFromMasterWhenNoSlaves"), (String)"5.1.38", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty autoDeserialize = new BooleanConnectionProperty((String)"autoDeserialize", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.autoDeserialize"), (String)"3.1.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty autoGenerateTestcaseScript = new BooleanConnectionProperty((String)"autoGenerateTestcaseScript", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.autoGenerateTestcaseScript"), (String)"3.1.9", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private boolean autoGenerateTestcaseScriptAsBoolean = false;
    private BooleanConnectionProperty autoReconnect = new BooleanConnectionProperty((String)"autoReconnect", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.autoReconnect"), (String)"1.1", (String)HA_CATEGORY, (int)0);
    private BooleanConnectionProperty autoReconnectForPools = new BooleanConnectionProperty((String)"autoReconnectForPools", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.autoReconnectForPools"), (String)"3.1.3", (String)HA_CATEGORY, (int)1);
    private boolean autoReconnectForPoolsAsBoolean = false;
    private MemorySizeConnectionProperty blobSendChunkSize = new MemorySizeConnectionProperty((String)"blobSendChunkSize", (int)1048576, (int)0, (int)0, (String)Messages.getString((String)"ConnectionProperties.blobSendChunkSize"), (String)"3.1.9", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty autoSlowLog = new BooleanConnectionProperty((String)"autoSlowLog", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.autoSlowLog"), (String)"5.1.4", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty blobsAreStrings = new BooleanConnectionProperty((String)"blobsAreStrings", (boolean)false, (String)"Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", (String)"5.0.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty functionsNeverReturnBlobs = new BooleanConnectionProperty((String)"functionsNeverReturnBlobs", (boolean)false, (String)"Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", (String)"5.0.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty cacheCallableStatements = new BooleanConnectionProperty((String)"cacheCallableStmts", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.cacheCallableStatements"), (String)"3.1.2", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty cachePreparedStatements = new BooleanConnectionProperty((String)"cachePrepStmts", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.cachePrepStmts"), (String)"3.0.10", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty cacheResultSetMetadata = new BooleanConnectionProperty((String)"cacheResultSetMetadata", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.cacheRSMetadata"), (String)"3.1.1", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private boolean cacheResultSetMetaDataAsBoolean;
    private StringConnectionProperty serverConfigCacheFactory = new StringConnectionProperty((String)"serverConfigCacheFactory", (String)PerVmServerConfigCacheFactory.class.getName(), (String)Messages.getString((String)"ConnectionProperties.serverConfigCacheFactory"), (String)"5.1.1", (String)PERFORMANCE_CATEGORY, (int)12);
    private BooleanConnectionProperty cacheServerConfiguration = new BooleanConnectionProperty((String)"cacheServerConfiguration", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.cacheServerConfiguration"), (String)"3.1.5", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty callableStatementCacheSize = new IntegerConnectionProperty((String)"callableStmtCacheSize", (int)100, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.callableStmtCacheSize"), (String)"3.1.2", (String)PERFORMANCE_CATEGORY, (int)5);
    private BooleanConnectionProperty capitalizeTypeNames = new BooleanConnectionProperty((String)"capitalizeTypeNames", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.capitalizeTypeNames"), (String)"2.0.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty characterEncoding = new StringConnectionProperty((String)"characterEncoding", null, (String)Messages.getString((String)"ConnectionProperties.characterEncoding"), (String)"1.1g", (String)MISC_CATEGORY, (int)5);
    private String characterEncodingAsString = null;
    protected boolean characterEncodingIsAliasForSjis = false;
    private StringConnectionProperty characterSetResults = new StringConnectionProperty((String)"characterSetResults", null, (String)Messages.getString((String)"ConnectionProperties.characterSetResults"), (String)"3.0.13", (String)MISC_CATEGORY, (int)6);
    private StringConnectionProperty connectionAttributes = new StringConnectionProperty((String)"connectionAttributes", null, (String)Messages.getString((String)"ConnectionProperties.connectionAttributes"), (String)"5.1.25", (String)MISC_CATEGORY, (int)7);
    private StringConnectionProperty clientInfoProvider = new StringConnectionProperty((String)"clientInfoProvider", (String)"com.mysql.jdbc.JDBC4CommentClientInfoProvider", (String)Messages.getString((String)"ConnectionProperties.clientInfoProvider"), (String)"5.1.0", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty clobberStreamingResults = new BooleanConnectionProperty((String)"clobberStreamingResults", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.clobberStreamingResults"), (String)"3.0.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty clobCharacterEncoding = new StringConnectionProperty((String)"clobCharacterEncoding", null, (String)Messages.getString((String)"ConnectionProperties.clobCharacterEncoding"), (String)"5.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty compensateOnDuplicateKeyUpdateCounts = new BooleanConnectionProperty((String)"compensateOnDuplicateKeyUpdateCounts", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), (String)"5.1.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty connectionCollation = new StringConnectionProperty((String)"connectionCollation", null, (String)Messages.getString((String)"ConnectionProperties.connectionCollation"), (String)"3.0.13", (String)MISC_CATEGORY, (int)7);
    private StringConnectionProperty connectionLifecycleInterceptors = new StringConnectionProperty((String)"connectionLifecycleInterceptors", null, (String)Messages.getString((String)"ConnectionProperties.connectionLifecycleInterceptors"), (String)"5.1.4", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MAX_VALUE);
    private IntegerConnectionProperty connectTimeout = new IntegerConnectionProperty((String)"connectTimeout", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.connectTimeout"), (String)"3.0.1", (String)CONNECTION_AND_AUTH_CATEGORY, (int)9);
    private BooleanConnectionProperty continueBatchOnError = new BooleanConnectionProperty((String)"continueBatchOnError", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.continueBatchOnError"), (String)"3.0.3", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty createDatabaseIfNotExist = new BooleanConnectionProperty((String)"createDatabaseIfNotExist", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.createDatabaseIfNotExist"), (String)"3.1.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty defaultFetchSize = new IntegerConnectionProperty((String)"defaultFetchSize", (int)0, (String)Messages.getString((String)"ConnectionProperties.defaultFetchSize"), (String)"3.1.9", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty detectServerPreparedStmts = new BooleanConnectionProperty((String)"useServerPrepStmts", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useServerPrepStmts"), (String)"3.1.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty dontTrackOpenResources = new BooleanConnectionProperty((String)"dontTrackOpenResources", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.dontTrackOpenResources"), (String)"3.1.7", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty dumpQueriesOnException = new BooleanConnectionProperty((String)"dumpQueriesOnException", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.dumpQueriesOnException"), (String)"3.1.3", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty dynamicCalendars = new BooleanConnectionProperty((String)"dynamicCalendars", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.dynamicCalendars"), (String)"3.1.5", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty elideSetAutoCommits = new BooleanConnectionProperty((String)"elideSetAutoCommits", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.eliseSetAutoCommit"), (String)"3.1.3", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty emptyStringsConvertToZero = new BooleanConnectionProperty((String)"emptyStringsConvertToZero", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.emptyStringsConvertToZero"), (String)"3.1.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty emulateLocators = new BooleanConnectionProperty((String)"emulateLocators", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.emulateLocators"), (String)"3.1.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty emulateUnsupportedPstmts = new BooleanConnectionProperty((String)"emulateUnsupportedPstmts", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.emulateUnsupportedPstmts"), (String)"3.1.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty enablePacketDebug = new BooleanConnectionProperty((String)"enablePacketDebug", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.enablePacketDebug"), (String)"3.1.3", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty enableQueryTimeouts = new BooleanConnectionProperty((String)"enableQueryTimeouts", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.enableQueryTimeouts"), (String)"5.0.6", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty explainSlowQueries = new BooleanConnectionProperty((String)"explainSlowQueries", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.explainSlowQueries"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty exceptionInterceptors = new StringConnectionProperty((String)"exceptionInterceptors", null, (String)Messages.getString((String)"ConnectionProperties.exceptionInterceptors"), (String)"5.1.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty failOverReadOnly = new BooleanConnectionProperty((String)"failOverReadOnly", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.failoverReadOnly"), (String)"3.0.12", (String)HA_CATEGORY, (int)2);
    private BooleanConnectionProperty gatherPerformanceMetrics = new BooleanConnectionProperty((String)"gatherPerfMetrics", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.gatherPerfMetrics"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)1);
    private BooleanConnectionProperty generateSimpleParameterMetadata = new BooleanConnectionProperty((String)"generateSimpleParameterMetadata", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.generateSimpleParameterMetadata"), (String)"5.0.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private boolean highAvailabilityAsBoolean = false;
    private BooleanConnectionProperty holdResultsOpenOverStatementClose = new BooleanConnectionProperty((String)"holdResultsOpenOverStatementClose", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.holdRSOpenOverStmtClose"), (String)"3.1.7", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty((String)"includeInnodbStatusInDeadlockExceptions", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.includeInnodbStatusInDeadlockExceptions"), (String)"5.0.7", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty includeThreadDumpInDeadlockExceptions = new BooleanConnectionProperty((String)"includeThreadDumpInDeadlockExceptions", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.includeThreadDumpInDeadlockExceptions"), (String)"5.1.15", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty includeThreadNamesAsStatementComment = new BooleanConnectionProperty((String)"includeThreadNamesAsStatementComment", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.includeThreadNamesAsStatementComment"), (String)"5.1.15", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty ignoreNonTxTables = new BooleanConnectionProperty((String)"ignoreNonTxTables", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.ignoreNonTxTables"), (String)"3.0.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty initialTimeout = new IntegerConnectionProperty((String)"initialTimeout", (int)2, (int)1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.initialTimeout"), (String)"1.1", (String)HA_CATEGORY, (int)5);
    private BooleanConnectionProperty isInteractiveClient = new BooleanConnectionProperty((String)"interactiveClient", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.interactiveClient"), (String)"3.1.0", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty jdbcCompliantTruncation = new BooleanConnectionProperty((String)"jdbcCompliantTruncation", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.jdbcCompliantTruncation"), (String)"3.1.2", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private boolean jdbcCompliantTruncationForReads = this.jdbcCompliantTruncation.getValueAsBoolean();
    protected MemorySizeConnectionProperty largeRowSizeThreshold = new MemorySizeConnectionProperty((String)"largeRowSizeThreshold", (int)2048, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.largeRowSizeThreshold"), (String)"5.1.1", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loadBalanceStrategy = new StringConnectionProperty((String)"loadBalanceStrategy", (String)"random", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceStrategy"), (String)"5.0.6", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty serverAffinityOrder = new StringConnectionProperty((String)"serverAffinityOrder", (String)"", null, (String)Messages.getString((String)"ConnectionProperties.serverAffinityOrder"), (String)"5.1.43", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty loadBalanceBlacklistTimeout = new IntegerConnectionProperty((String)"loadBalanceBlacklistTimeout", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.loadBalanceBlacklistTimeout"), (String)"5.1.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty loadBalancePingTimeout = new IntegerConnectionProperty((String)"loadBalancePingTimeout", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.loadBalancePingTimeout"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty loadBalanceValidateConnectionOnSwapServer = new BooleanConnectionProperty((String)"loadBalanceValidateConnectionOnSwapServer", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.loadBalanceValidateConnectionOnSwapServer"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loadBalanceConnectionGroup = new StringConnectionProperty((String)"loadBalanceConnectionGroup", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceConnectionGroup"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loadBalanceExceptionChecker = new StringConnectionProperty((String)"loadBalanceExceptionChecker", (String)"com.mysql.jdbc.StandardLoadBalanceExceptionChecker", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceExceptionChecker"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loadBalanceSQLStateFailover = new StringConnectionProperty((String)"loadBalanceSQLStateFailover", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceSQLStateFailover"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loadBalanceSQLExceptionSubclassFailover = new StringConnectionProperty((String)"loadBalanceSQLExceptionSubclassFailover", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceSQLExceptionSubclassFailover"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty loadBalanceEnableJMX = new BooleanConnectionProperty((String)"loadBalanceEnableJMX", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.loadBalanceEnableJMX"), (String)"5.1.13", (String)MISC_CATEGORY, (int)Integer.MAX_VALUE);
    private IntegerConnectionProperty loadBalanceHostRemovalGracePeriod = new IntegerConnectionProperty((String)"loadBalanceHostRemovalGracePeriod", (int)15000, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.loadBalanceHostRemovalGracePeriod"), (String)"5.1.39", (String)MISC_CATEGORY, (int)Integer.MAX_VALUE);
    private StringConnectionProperty loadBalanceAutoCommitStatementRegex = new StringConnectionProperty((String)"loadBalanceAutoCommitStatementRegex", null, (String)Messages.getString((String)"ConnectionProperties.loadBalanceAutoCommitStatementRegex"), (String)"5.1.15", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty loadBalanceAutoCommitStatementThreshold = new IntegerConnectionProperty((String)"loadBalanceAutoCommitStatementThreshold", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.loadBalanceAutoCommitStatementThreshold"), (String)"5.1.15", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty localSocketAddress = new StringConnectionProperty((String)"localSocketAddress", null, (String)Messages.getString((String)"ConnectionProperties.localSocketAddress"), (String)"5.0.5", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private MemorySizeConnectionProperty locatorFetchBufferSize = new MemorySizeConnectionProperty((String)"locatorFetchBufferSize", (int)1048576, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.locatorFetchBufferSize"), (String)"3.2.1", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty loggerClassName = new StringConnectionProperty((String)"logger", (String)STANDARD_LOGGER_NAME, (String)Messages.getString((String)"ConnectionProperties.logger", (Object[])new Object[]{Log.class.getName(), STANDARD_LOGGER_NAME}), (String)"3.1.1", (String)DEBUGING_PROFILING_CATEGORY, (int)0);
    private BooleanConnectionProperty logSlowQueries = new BooleanConnectionProperty((String)"logSlowQueries", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.logSlowQueries"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty logXaCommands = new BooleanConnectionProperty((String)"logXaCommands", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.logXaCommands"), (String)"5.0.5", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty maintainTimeStats = new BooleanConnectionProperty((String)"maintainTimeStats", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.maintainTimeStats"), (String)"3.1.9", (String)PERFORMANCE_CATEGORY, (int)Integer.MAX_VALUE);
    private boolean maintainTimeStatsAsBoolean = true;
    private IntegerConnectionProperty maxQuerySizeToLog = new IntegerConnectionProperty((String)"maxQuerySizeToLog", (int)2048, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.maxQuerySizeToLog"), (String)"3.1.3", (String)DEBUGING_PROFILING_CATEGORY, (int)4);
    private IntegerConnectionProperty maxReconnects = new IntegerConnectionProperty((String)"maxReconnects", (int)3, (int)1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.maxReconnects"), (String)"1.1", (String)HA_CATEGORY, (int)4);
    private IntegerConnectionProperty retriesAllDown = new IntegerConnectionProperty((String)"retriesAllDown", (int)120, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.retriesAllDown"), (String)"5.1.6", (String)HA_CATEGORY, (int)4);
    private IntegerConnectionProperty maxRows = new IntegerConnectionProperty((String)"maxRows", (int)-1, (int)-1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.maxRows"), (String)Messages.getString((String)"ConnectionProperties.allVersions"), (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private int maxRowsAsInt = -1;
    private IntegerConnectionProperty metadataCacheSize = new IntegerConnectionProperty((String)"metadataCacheSize", (int)50, (int)1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.metadataCacheSize"), (String)"3.1.1", (String)PERFORMANCE_CATEGORY, (int)5);
    private IntegerConnectionProperty netTimeoutForStreamingResults = new IntegerConnectionProperty((String)"netTimeoutForStreamingResults", (int)600, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.netTimeoutForStreamingResults"), (String)"5.1.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty noAccessToProcedureBodies = new BooleanConnectionProperty((String)"noAccessToProcedureBodies", (boolean)false, (String)"When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead of throwing an exception?", (String)"5.0.3", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty noDatetimeStringSync = new BooleanConnectionProperty((String)"noDatetimeStringSync", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.noDatetimeStringSync"), (String)"3.1.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty noTimezoneConversionForTimeType = new BooleanConnectionProperty((String)"noTimezoneConversionForTimeType", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.noTzConversionForTimeType"), (String)"5.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty noTimezoneConversionForDateType = new BooleanConnectionProperty((String)"noTimezoneConversionForDateType", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.noTzConversionForDateType"), (String)"5.1.35", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty cacheDefaultTimezone = new BooleanConnectionProperty((String)"cacheDefaultTimezone", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.cacheDefaultTimezone"), (String)"5.1.35", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty nullCatalogMeansCurrent = new BooleanConnectionProperty((String)"nullCatalogMeansCurrent", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.nullCatalogMeansCurrent"), (String)"3.1.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty nullNamePatternMatchesAll = new BooleanConnectionProperty((String)"nullNamePatternMatchesAll", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.nullNamePatternMatchesAll"), (String)"3.1.8", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty packetDebugBufferSize = new IntegerConnectionProperty((String)"packetDebugBufferSize", (int)20, (int)1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.packetDebugBufferSize"), (String)"3.1.3", (String)DEBUGING_PROFILING_CATEGORY, (int)7);
    private BooleanConnectionProperty padCharsWithSpace = new BooleanConnectionProperty((String)"padCharsWithSpace", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.padCharsWithSpace"), (String)"5.0.6", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty paranoid = new BooleanConnectionProperty((String)"paranoid", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.paranoid"), (String)"3.0.1", (String)SECURITY_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty pedantic = new BooleanConnectionProperty((String)"pedantic", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.pedantic"), (String)"3.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty((String)"pinGlobalTxToPhysicalConnection", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.pinGlobalTxToPhysicalConnection"), (String)"5.0.1", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty populateInsertRowWithDefaultValues = new BooleanConnectionProperty((String)"populateInsertRowWithDefaultValues", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.populateInsertRowWithDefaultValues"), (String)"5.0.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty preparedStatementCacheSize = new IntegerConnectionProperty((String)"prepStmtCacheSize", (int)25, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.prepStmtCacheSize"), (String)"3.0.10", (String)PERFORMANCE_CATEGORY, (int)10);
    private IntegerConnectionProperty preparedStatementCacheSqlLimit = new IntegerConnectionProperty((String)"prepStmtCacheSqlLimit", (int)256, (int)1, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.prepStmtCacheSqlLimit"), (String)"3.0.10", (String)PERFORMANCE_CATEGORY, (int)11);
    private StringConnectionProperty parseInfoCacheFactory = new StringConnectionProperty((String)"parseInfoCacheFactory", (String)PerConnectionLRUFactory.class.getName(), (String)Messages.getString((String)"ConnectionProperties.parseInfoCacheFactory"), (String)"5.1.1", (String)PERFORMANCE_CATEGORY, (int)12);
    private BooleanConnectionProperty processEscapeCodesForPrepStmts = new BooleanConnectionProperty((String)"processEscapeCodesForPrepStmts", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.processEscapeCodesForPrepStmts"), (String)"3.1.12", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty profilerEventHandler = new StringConnectionProperty((String)"profilerEventHandler", (String)"com.mysql.jdbc.profiler.LoggingProfilerEventHandler", (String)Messages.getString((String)"ConnectionProperties.profilerEventHandler"), (String)"5.1.6", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty profileSql = new StringConnectionProperty((String)"profileSql", null, (String)Messages.getString((String)"ConnectionProperties.profileSqlDeprecated"), (String)"2.0.14", (String)DEBUGING_PROFILING_CATEGORY, (int)3);
    private BooleanConnectionProperty profileSQL = new BooleanConnectionProperty((String)"profileSQL", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.profileSQL"), (String)"3.1.0", (String)DEBUGING_PROFILING_CATEGORY, (int)1);
    private boolean profileSQLAsBoolean = false;
    private StringConnectionProperty propertiesTransform = new StringConnectionProperty((String)"propertiesTransform", null, (String)Messages.getString((String)"ConnectionProperties.connectionPropertiesTransform"), (String)"3.1.4", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty queriesBeforeRetryMaster = new IntegerConnectionProperty((String)"queriesBeforeRetryMaster", (int)50, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.queriesBeforeRetryMaster"), (String)"3.0.2", (String)HA_CATEGORY, (int)7);
    private BooleanConnectionProperty queryTimeoutKillsConnection = new BooleanConnectionProperty((String)"queryTimeoutKillsConnection", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.queryTimeoutKillsConnection"), (String)"5.1.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty reconnectAtTxEnd = new BooleanConnectionProperty((String)"reconnectAtTxEnd", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.reconnectAtTxEnd"), (String)"3.0.10", (String)HA_CATEGORY, (int)4);
    private boolean reconnectTxAtEndAsBoolean = false;
    private BooleanConnectionProperty relaxAutoCommit = new BooleanConnectionProperty((String)"relaxAutoCommit", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.relaxAutoCommit"), (String)"2.0.13", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty reportMetricsIntervalMillis = new IntegerConnectionProperty((String)"reportMetricsIntervalMillis", (int)30000, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.reportMetricsIntervalMillis"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)3);
    private BooleanConnectionProperty requireSSL = new BooleanConnectionProperty((String)"requireSSL", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.requireSSL"), (String)"3.1.0", (String)SECURITY_CATEGORY, (int)3);
    private StringConnectionProperty resourceId = new StringConnectionProperty((String)"resourceId", null, (String)Messages.getString((String)"ConnectionProperties.resourceId"), (String)"5.0.1", (String)HA_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty resultSetSizeThreshold = new IntegerConnectionProperty((String)"resultSetSizeThreshold", (int)100, (String)Messages.getString((String)"ConnectionProperties.resultSetSizeThreshold"), (String)"5.0.5", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty retainStatementAfterResultSetClose = new BooleanConnectionProperty((String)"retainStatementAfterResultSetClose", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.retainStatementAfterResultSetClose"), (String)"3.1.11", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty rewriteBatchedStatements = new BooleanConnectionProperty((String)"rewriteBatchedStatements", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.rewriteBatchedStatements"), (String)"3.1.13", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty rollbackOnPooledClose = new BooleanConnectionProperty((String)"rollbackOnPooledClose", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.rollbackOnPooledClose"), (String)"3.0.15", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty roundRobinLoadBalance = new BooleanConnectionProperty((String)"roundRobinLoadBalance", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.roundRobinLoadBalance"), (String)"3.1.2", (String)HA_CATEGORY, (int)5);
    private BooleanConnectionProperty runningCTS13 = new BooleanConnectionProperty((String)"runningCTS13", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.runningCTS13"), (String)"3.1.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty secondsBeforeRetryMaster = new IntegerConnectionProperty((String)"secondsBeforeRetryMaster", (int)30, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.secondsBeforeRetryMaster"), (String)"3.0.2", (String)HA_CATEGORY, (int)8);
    private IntegerConnectionProperty selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty((String)"selfDestructOnPingSecondsLifetime", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.selfDestructOnPingSecondsLifetime"), (String)"5.1.6", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private IntegerConnectionProperty selfDestructOnPingMaxOperations = new IntegerConnectionProperty((String)"selfDestructOnPingMaxOperations", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.selfDestructOnPingMaxOperations"), (String)"5.1.6", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty replicationEnableJMX = new BooleanConnectionProperty((String)"replicationEnableJMX", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.loadBalanceEnableJMX"), (String)"5.1.27", (String)HA_CATEGORY, (int)Integer.MAX_VALUE);
    private StringConnectionProperty serverTimezone = new StringConnectionProperty((String)"serverTimezone", null, (String)Messages.getString((String)"ConnectionProperties.serverTimezone"), (String)"3.0.2", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty sessionVariables = new StringConnectionProperty((String)"sessionVariables", null, (String)Messages.getString((String)"ConnectionProperties.sessionVariables"), (String)"3.1.8", (String)MISC_CATEGORY, (int)Integer.MAX_VALUE);
    private IntegerConnectionProperty slowQueryThresholdMillis = new IntegerConnectionProperty((String)"slowQueryThresholdMillis", (int)2000, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.slowQueryThresholdMillis"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)9);
    private LongConnectionProperty slowQueryThresholdNanos = new LongConnectionProperty((String)"slowQueryThresholdNanos", (long)0L, (String)Messages.getString((String)"ConnectionProperties.slowQueryThresholdNanos"), (String)"5.0.7", (String)DEBUGING_PROFILING_CATEGORY, (int)10);
    private StringConnectionProperty socketFactoryClassName = new StringConnectionProperty((String)"socketFactory", (String)StandardSocketFactory.class.getName(), (String)Messages.getString((String)"ConnectionProperties.socketFactory"), (String)"3.0.3", (String)CONNECTION_AND_AUTH_CATEGORY, (int)4);
    private StringConnectionProperty socksProxyHost = new StringConnectionProperty((String)"socksProxyHost", null, (String)Messages.getString((String)"ConnectionProperties.socksProxyHost"), (String)"5.1.34", (String)NETWORK_CATEGORY, (int)1);
    private IntegerConnectionProperty socksProxyPort = new IntegerConnectionProperty((String)"socksProxyPort", (int)SocksProxySocketFactory.SOCKS_DEFAULT_PORT, (int)0, (int)65535, (String)Messages.getString((String)"ConnectionProperties.socksProxyPort"), (String)"5.1.34", (String)NETWORK_CATEGORY, (int)2);
    private IntegerConnectionProperty socketTimeout = new IntegerConnectionProperty((String)"socketTimeout", (int)0, (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.socketTimeout"), (String)"3.0.1", (String)CONNECTION_AND_AUTH_CATEGORY, (int)10);
    private StringConnectionProperty statementInterceptors = new StringConnectionProperty((String)"statementInterceptors", null, (String)Messages.getString((String)"ConnectionProperties.statementInterceptors"), (String)"5.1.1", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty strictFloatingPoint = new BooleanConnectionProperty((String)"strictFloatingPoint", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.strictFloatingPoint"), (String)"3.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty strictUpdates = new BooleanConnectionProperty((String)"strictUpdates", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.strictUpdates"), (String)"3.0.4", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty((String)"overrideSupportsIntegrityEnhancementFacility", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.overrideSupportsIEF"), (String)"3.1.12", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty tcpNoDelay = new BooleanConnectionProperty((String)"tcpNoDelay", (boolean)Boolean.valueOf((String)"true").booleanValue(), (String)Messages.getString((String)"ConnectionProperties.tcpNoDelay"), (String)"5.0.7", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty tcpKeepAlive = new BooleanConnectionProperty((String)"tcpKeepAlive", (boolean)Boolean.valueOf((String)"true").booleanValue(), (String)Messages.getString((String)"ConnectionProperties.tcpKeepAlive"), (String)"5.0.7", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty tcpRcvBuf = new IntegerConnectionProperty((String)"tcpRcvBuf", (int)Integer.parseInt((String)"0"), (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.tcpSoRcvBuf"), (String)"5.0.7", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty tcpSndBuf = new IntegerConnectionProperty((String)"tcpSndBuf", (int)Integer.parseInt((String)"0"), (int)0, (int)Integer.MAX_VALUE, (String)Messages.getString((String)"ConnectionProperties.tcpSoSndBuf"), (String)"5.0.7", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty tcpTrafficClass = new IntegerConnectionProperty((String)"tcpTrafficClass", (int)Integer.parseInt((String)"0"), (int)0, (int)255, (String)Messages.getString((String)"ConnectionProperties.tcpTrafficClass"), (String)"5.0.7", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty tinyInt1isBit = new BooleanConnectionProperty((String)"tinyInt1isBit", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.tinyInt1isBit"), (String)"3.0.16", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    protected BooleanConnectionProperty traceProtocol = new BooleanConnectionProperty((String)"traceProtocol", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.traceProtocol"), (String)"3.1.2", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty treatUtilDateAsTimestamp = new BooleanConnectionProperty((String)"treatUtilDateAsTimestamp", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.treatUtilDateAsTimestamp"), (String)"5.0.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty transformedBitIsBoolean = new BooleanConnectionProperty((String)"transformedBitIsBoolean", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.transformedBitIsBoolean"), (String)"3.1.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty((String)"useBlobToStoreUTF8OutsideBMP", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), (String)"5.1.3", (String)MISC_CATEGORY, (int)128);
    private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty((String)"utf8OutsideBmpExcludedColumnNamePattern", null, (String)Messages.getString((String)"ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), (String)"5.1.3", (String)MISC_CATEGORY, (int)129);
    private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty((String)"utf8OutsideBmpIncludedColumnNamePattern", null, (String)Messages.getString((String)"ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), (String)"5.1.3", (String)MISC_CATEGORY, (int)129);
    private BooleanConnectionProperty useCompression = new BooleanConnectionProperty((String)"useCompression", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useCompression"), (String)"3.0.17", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useColumnNamesInFindColumn = new BooleanConnectionProperty((String)"useColumnNamesInFindColumn", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useColumnNamesInFindColumn"), (String)"5.1.7", (String)MISC_CATEGORY, (int)Integer.MAX_VALUE);
    private StringConnectionProperty useConfigs = new StringConnectionProperty((String)"useConfigs", null, (String)Messages.getString((String)"ConnectionProperties.useConfigs"), (String)"3.1.5", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty useCursorFetch = new BooleanConnectionProperty((String)"useCursorFetch", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useCursorFetch"), (String)"5.0.0", (String)PERFORMANCE_CATEGORY, (int)Integer.MAX_VALUE);
    private BooleanConnectionProperty useDynamicCharsetInfo = new BooleanConnectionProperty((String)"useDynamicCharsetInfo", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useDynamicCharsetInfo"), (String)"5.0.6", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useDirectRowUnpack = new BooleanConnectionProperty((String)"useDirectRowUnpack", (boolean)true, (String)"Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", (String)"5.1.1", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useFastIntParsing = new BooleanConnectionProperty((String)"useFastIntParsing", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useFastIntParsing"), (String)"3.1.4", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useFastDateParsing = new BooleanConnectionProperty((String)"useFastDateParsing", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useFastDateParsing"), (String)"5.0.5", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useHostsInPrivileges = new BooleanConnectionProperty((String)"useHostsInPrivileges", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useHostsInPrivileges"), (String)"3.0.2", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useInformationSchema = new BooleanConnectionProperty((String)"useInformationSchema", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useInformationSchema"), (String)"5.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useJDBCCompliantTimezoneShift = new BooleanConnectionProperty((String)"useJDBCCompliantTimezoneShift", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useJDBCCompliantTimezoneShift"), (String)"5.0.0", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useLocalSessionState = new BooleanConnectionProperty((String)"useLocalSessionState", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useLocalSessionState"), (String)"3.1.7", (String)PERFORMANCE_CATEGORY, (int)5);
    private BooleanConnectionProperty useLocalTransactionState = new BooleanConnectionProperty((String)"useLocalTransactionState", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useLocalTransactionState"), (String)"5.1.7", (String)PERFORMANCE_CATEGORY, (int)6);
    private BooleanConnectionProperty useLegacyDatetimeCode = new BooleanConnectionProperty((String)"useLegacyDatetimeCode", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useLegacyDatetimeCode"), (String)"5.1.6", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty sendFractionalSeconds = new BooleanConnectionProperty((String)"sendFractionalSeconds", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.sendFractionalSeconds"), (String)"5.1.37", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useNanosForElapsedTime = new BooleanConnectionProperty((String)"useNanosForElapsedTime", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useNanosForElapsedTime"), (String)"5.0.7", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useOldAliasMetadataBehavior = new BooleanConnectionProperty((String)"useOldAliasMetadataBehavior", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useOldAliasMetadataBehavior"), (String)"5.0.4", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useOldUTF8Behavior = new BooleanConnectionProperty((String)"useOldUTF8Behavior", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useOldUtf8Behavior"), (String)"3.1.6", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private boolean useOldUTF8BehaviorAsBoolean = false;
    private BooleanConnectionProperty useOnlyServerErrorMessages = new BooleanConnectionProperty((String)"useOnlyServerErrorMessages", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useOnlyServerErrorMessages"), (String)"3.0.15", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useReadAheadInput = new BooleanConnectionProperty((String)"useReadAheadInput", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useReadAheadInput"), (String)"3.1.5", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useSqlStateCodes = new BooleanConnectionProperty((String)"useSqlStateCodes", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useSqlStateCodes"), (String)"3.1.3", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useSSL = new BooleanConnectionProperty((String)"useSSL", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useSSL"), (String)"3.0.2", (String)SECURITY_CATEGORY, (int)2);
    private BooleanConnectionProperty useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty((String)"useSSPSCompatibleTimezoneShift", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useSSPSCompatibleTimezoneShift"), (String)"5.0.5", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useStreamLengthsInPrepStmts = new BooleanConnectionProperty((String)"useStreamLengthsInPrepStmts", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useStreamLengthsInPrepStmts"), (String)"3.0.2", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useTimezone = new BooleanConnectionProperty((String)"useTimezone", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useTimezone"), (String)"3.0.2", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useUltraDevWorkAround = new BooleanConnectionProperty((String)"ultraDevHack", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.ultraDevHack"), (String)"2.0.3", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useUnbufferedInput = new BooleanConnectionProperty((String)"useUnbufferedInput", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useUnbufferedInput"), (String)"3.0.11", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useUnicode = new BooleanConnectionProperty((String)"useUnicode", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.useUnicode"), (String)"1.1g", (String)MISC_CATEGORY, (int)0);
    private boolean useUnicodeAsBoolean = true;
    private BooleanConnectionProperty useUsageAdvisor = new BooleanConnectionProperty((String)"useUsageAdvisor", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useUsageAdvisor"), (String)"3.1.1", (String)DEBUGING_PROFILING_CATEGORY, (int)10);
    private boolean useUsageAdvisorAsBoolean = false;
    private BooleanConnectionProperty yearIsDateType = new BooleanConnectionProperty((String)"yearIsDateType", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.yearIsDateType"), (String)"3.1.9", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty zeroDateTimeBehavior = new StringConnectionProperty((String)"zeroDateTimeBehavior", (String)"exception", (String[])new String[]{"exception", "round", "convertToNull"}, (String)Messages.getString((String)"ConnectionProperties.zeroDateTimeBehavior", (Object[])new Object[]{"exception", "round", "convertToNull"}), (String)"3.1.4", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useJvmCharsetConverters = new BooleanConnectionProperty((String)"useJvmCharsetConverters", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useJvmCharsetConverters"), (String)"5.0.1", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty useGmtMillisForDatetimes = new BooleanConnectionProperty((String)"useGmtMillisForDatetimes", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useGmtMillisForDatetimes"), (String)"3.1.12", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty dumpMetadataOnColumnNotFound = new BooleanConnectionProperty((String)"dumpMetadataOnColumnNotFound", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.dumpMetadataOnColumnNotFound"), (String)"3.1.13", (String)DEBUGING_PROFILING_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty clientCertificateKeyStoreUrl = new StringConnectionProperty((String)"clientCertificateKeyStoreUrl", null, (String)Messages.getString((String)"ConnectionProperties.clientCertificateKeyStoreUrl"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)5);
    private StringConnectionProperty trustCertificateKeyStoreUrl = new StringConnectionProperty((String)"trustCertificateKeyStoreUrl", null, (String)Messages.getString((String)"ConnectionProperties.trustCertificateKeyStoreUrl"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)8);
    private StringConnectionProperty clientCertificateKeyStoreType = new StringConnectionProperty((String)"clientCertificateKeyStoreType", (String)"JKS", (String)Messages.getString((String)"ConnectionProperties.clientCertificateKeyStoreType"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)6);
    private StringConnectionProperty clientCertificateKeyStorePassword = new StringConnectionProperty((String)"clientCertificateKeyStorePassword", null, (String)Messages.getString((String)"ConnectionProperties.clientCertificateKeyStorePassword"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)7);
    private StringConnectionProperty trustCertificateKeyStoreType = new StringConnectionProperty((String)"trustCertificateKeyStoreType", (String)"JKS", (String)Messages.getString((String)"ConnectionProperties.trustCertificateKeyStoreType"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)9);
    private StringConnectionProperty trustCertificateKeyStorePassword = new StringConnectionProperty((String)"trustCertificateKeyStorePassword", null, (String)Messages.getString((String)"ConnectionProperties.trustCertificateKeyStorePassword"), (String)"5.1.0", (String)SECURITY_CATEGORY, (int)10);
    private BooleanConnectionProperty verifyServerCertificate = new BooleanConnectionProperty((String)"verifyServerCertificate", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.verifyServerCertificate"), (String)"5.1.6", (String)SECURITY_CATEGORY, (int)4);
    private BooleanConnectionProperty useAffectedRows = new BooleanConnectionProperty((String)"useAffectedRows", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.useAffectedRows"), (String)"5.1.7", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty passwordCharacterEncoding = new StringConnectionProperty((String)"passwordCharacterEncoding", null, (String)Messages.getString((String)"ConnectionProperties.passwordCharacterEncoding"), (String)"5.1.7", (String)SECURITY_CATEGORY, (int)Integer.MIN_VALUE);
    private IntegerConnectionProperty maxAllowedPacket = new IntegerConnectionProperty((String)"maxAllowedPacket", (int)-1, (String)Messages.getString((String)"ConnectionProperties.maxAllowedPacket"), (String)"5.1.8", (String)NETWORK_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty authenticationPlugins = new StringConnectionProperty((String)"authenticationPlugins", null, (String)Messages.getString((String)"ConnectionProperties.authenticationPlugins"), (String)"5.1.19", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty disabledAuthenticationPlugins = new StringConnectionProperty((String)"disabledAuthenticationPlugins", null, (String)Messages.getString((String)"ConnectionProperties.disabledAuthenticationPlugins"), (String)"5.1.19", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty defaultAuthenticationPlugin = new StringConnectionProperty((String)"defaultAuthenticationPlugin", (String)"com.mysql.jdbc.authentication.MysqlNativePasswordPlugin", (String)Messages.getString((String)"ConnectionProperties.defaultAuthenticationPlugin"), (String)"5.1.19", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty disconnectOnExpiredPasswords = new BooleanConnectionProperty((String)"disconnectOnExpiredPasswords", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.disconnectOnExpiredPasswords"), (String)"5.1.23", (String)CONNECTION_AND_AUTH_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty getProceduresReturnsFunctions = new BooleanConnectionProperty((String)"getProceduresReturnsFunctions", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.getProceduresReturnsFunctions"), (String)"5.1.26", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty detectCustomCollations = new BooleanConnectionProperty((String)"detectCustomCollations", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.detectCustomCollations"), (String)"5.1.29", (String)MISC_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty serverRSAPublicKeyFile = new StringConnectionProperty((String)"serverRSAPublicKeyFile", null, (String)Messages.getString((String)"ConnectionProperties.serverRSAPublicKeyFile"), (String)"5.1.31", (String)SECURITY_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty allowPublicKeyRetrieval = new BooleanConnectionProperty((String)"allowPublicKeyRetrieval", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.allowPublicKeyRetrieval"), (String)"5.1.31", (String)SECURITY_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty dontCheckOnDuplicateKeyUpdateInSQL = new BooleanConnectionProperty((String)"dontCheckOnDuplicateKeyUpdateInSQL", (boolean)false, (String)Messages.getString((String)"ConnectionProperties.dontCheckOnDuplicateKeyUpdateInSQL"), (String)"5.1.32", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private BooleanConnectionProperty readOnlyPropagatesToServer = new BooleanConnectionProperty((String)"readOnlyPropagatesToServer", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.readOnlyPropagatesToServer"), (String)"5.1.35", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);
    private StringConnectionProperty enabledSSLCipherSuites = new StringConnectionProperty((String)"enabledSSLCipherSuites", null, (String)Messages.getString((String)"ConnectionProperties.enabledSSLCipherSuites"), (String)"5.1.35", (String)SECURITY_CATEGORY, (int)11);
    private StringConnectionProperty enabledTLSProtocols = new StringConnectionProperty((String)"enabledTLSProtocols", null, (String)Messages.getString((String)"ConnectionProperties.enabledTLSProtocols"), (String)"5.1.44", (String)SECURITY_CATEGORY, (int)12);
    private BooleanConnectionProperty enableEscapeProcessing = new BooleanConnectionProperty((String)"enableEscapeProcessing", (boolean)true, (String)Messages.getString((String)"ConnectionProperties.enableEscapeProcessing"), (String)"5.1.37", (String)PERFORMANCE_CATEGORY, (int)Integer.MIN_VALUE);

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return null;
    }

    protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve) throws SQLException {
        return new ConnectionPropertiesImpl(){
            private static final long serialVersionUID = 4257801713007640581L;
        }.exposeAsDriverPropertyInfoInternal((Properties)info, (int)slotsToReserve);
    }

    protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve) throws SQLException {
        this.initializeProperties((Properties)info);
        int numProperties = PROPERTY_LIST.size();
        int listSize = numProperties + slotsToReserve;
        DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
        int i = slotsToReserve;
        while (i < listSize) {
            Field propertyField = PROPERTY_LIST.get((int)(i - slotsToReserve));
            try {
                ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get((Object)this);
                if (info != null) {
                    propToExpose.initializeFrom((Properties)info, (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ConnectionProperties.InternalPropertiesFailure"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        }
        return driverProperties;
    }

    protected Properties exposeAsProperties(Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            Field propertyField = PROPERTY_LIST.get((int)i);
            try {
                ConnectionProperty propToGet = (ConnectionProperty)propertyField.get((Object)this);
                Object propValue = propToGet.getValueAsObject();
                if (propValue != null) {
                    info.setProperty((String)propToGet.getPropertyName(), (String)propValue.toString());
                }
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)"Internal properties failure", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        }
        return info;
    }

    public Properties exposeAsProperties(Properties props, boolean explicitOnly) throws SQLException {
        if (props == null) {
            props = new Properties();
        }
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            Field propertyField = PROPERTY_LIST.get((int)i);
            try {
                ConnectionProperty propToGet = (ConnectionProperty)propertyField.get((Object)this);
                Object propValue = propToGet.getValueAsObject();
                if (propValue != null && (!explicitOnly || propToGet.isExplicitlySet())) {
                    props.setProperty((String)propToGet.getPropertyName(), (String)propValue.toString());
                }
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)"Internal properties failure", (String)"S1000", (Throwable)iae, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        }
        return props;
    }

    @Override
    public String exposeAsXml() throws SQLException {
        StringBuilder xmlBuf = new StringBuilder();
        xmlBuf.append((String)"<ConnectionProperties>");
        int numPropertiesToSet = PROPERTY_LIST.size();
        int numCategories = PROPERTY_CATEGORIES.length;
        HashMap<String, XmlMap> propertyListByCategory = new HashMap<String, XmlMap>();
        for (int i = 0; i < numCategories; ++i) {
            propertyListByCategory.put(PROPERTY_CATEGORIES[i], new XmlMap((ConnectionPropertiesImpl)this));
        }
        StringConnectionProperty userProp = new StringConnectionProperty((String)"user", null, (String)Messages.getString((String)"ConnectionProperties.Username"), (String)Messages.getString((String)"ConnectionProperties.allVersions"), (String)CONNECTION_AND_AUTH_CATEGORY, (int)-2147483647);
        StringConnectionProperty passwordProp = new StringConnectionProperty((String)"password", null, (String)Messages.getString((String)"ConnectionProperties.Password"), (String)Messages.getString((String)"ConnectionProperties.allVersions"), (String)CONNECTION_AND_AUTH_CATEGORY, (int)-2147483646);
        XmlMap connectionSortMaps = (XmlMap)propertyListByCategory.get((Object)CONNECTION_AND_AUTH_CATEGORY);
        TreeMap<String, StringConnectionProperty> userMap = new TreeMap<String, StringConnectionProperty>();
        userMap.put(userProp.getPropertyName(), userProp);
        connectionSortMaps.ordered.put((Integer)Integer.valueOf((int)userProp.getOrder()), userMap);
        TreeMap<String, StringConnectionProperty> passwordMap = new TreeMap<String, StringConnectionProperty>();
        passwordMap.put(passwordProp.getPropertyName(), passwordProp);
        connectionSortMaps.ordered.put((Integer)new Integer((int)passwordProp.getOrder()), passwordMap);
        try {
            for (int i = 0; i < numPropertiesToSet; ++i) {
                Field propertyField = PROPERTY_LIST.get((int)i);
                ConnectionProperty propToGet = (ConnectionProperty)propertyField.get((Object)this);
                XmlMap sortMaps = (XmlMap)propertyListByCategory.get((Object)propToGet.getCategoryName());
                int orderInCategory = propToGet.getOrder();
                if (orderInCategory == Integer.MIN_VALUE) {
                    sortMaps.alpha.put((String)propToGet.getPropertyName(), (ConnectionProperty)propToGet);
                    continue;
                }
                Integer order = Integer.valueOf((int)orderInCategory);
                Map<String, ConnectionProperty> orderMap = sortMaps.ordered.get((Object)order);
                if (orderMap == null) {
                    orderMap = new TreeMap<String, ConnectionProperty>();
                    sortMaps.ordered.put((Integer)order, orderMap);
                }
                orderMap.put((String)propToGet.getPropertyName(), (ConnectionProperty)propToGet);
            }
            for (int j = 0; j < numCategories; ++j) {
                XmlMap sortMaps = (XmlMap)propertyListByCategory.get((Object)PROPERTY_CATEGORIES[j]);
                xmlBuf.append((String)"\n <PropertyCategory name=\"");
                xmlBuf.append((String)PROPERTY_CATEGORIES[j]);
                xmlBuf.append((String)"\">");
                Iterator<Object> i$ = sortMaps.ordered.values().iterator();
                block5 : do {
                    if (!i$.hasNext()) {
                        i$ = sortMaps.alpha.values().iterator();
                        break;
                    }
                    Map<String, ConnectionProperty> orderedEl = i$.next();
                    Iterator<ConnectionProperty> i$2 = orderedEl.values().iterator();
                    do {
                        if (!i$2.hasNext()) continue block5;
                        ConnectionProperty propToGet = i$2.next();
                        xmlBuf.append((String)"\n  <Property name=\"");
                        xmlBuf.append((String)propToGet.getPropertyName());
                        xmlBuf.append((String)"\" required=\"");
                        xmlBuf.append((String)(propToGet.required ? "Yes" : "No"));
                        xmlBuf.append((String)"\" default=\"");
                        if (propToGet.getDefaultValue() != null) {
                            xmlBuf.append((Object)propToGet.getDefaultValue());
                        }
                        xmlBuf.append((String)"\" sortOrder=\"");
                        xmlBuf.append((int)propToGet.getOrder());
                        xmlBuf.append((String)"\" since=\"");
                        xmlBuf.append((String)propToGet.sinceVersion);
                        xmlBuf.append((String)"\">\n");
                        xmlBuf.append((String)"    ");
                        String escapedDescription = propToGet.description;
                        escapedDescription = escapedDescription.replace((CharSequence)"&", (CharSequence)"&amp;").replace((CharSequence)"<", (CharSequence)"&lt;").replace((CharSequence)">", (CharSequence)"&gt;");
                        xmlBuf.append((String)escapedDescription);
                        xmlBuf.append((String)"\n  </Property>");
                    } while (true);
                    break;
                } while (true);
                while (i$.hasNext()) {
                    ConnectionProperty propToGet = (ConnectionProperty)i$.next();
                    xmlBuf.append((String)"\n  <Property name=\"");
                    xmlBuf.append((String)propToGet.getPropertyName());
                    xmlBuf.append((String)"\" required=\"");
                    xmlBuf.append((String)(propToGet.required ? "Yes" : "No"));
                    xmlBuf.append((String)"\" default=\"");
                    if (propToGet.getDefaultValue() != null) {
                        xmlBuf.append((Object)propToGet.getDefaultValue());
                    }
                    xmlBuf.append((String)"\" sortOrder=\"alpha\" since=\"");
                    xmlBuf.append((String)propToGet.sinceVersion);
                    xmlBuf.append((String)"\">\n");
                    xmlBuf.append((String)"    ");
                    xmlBuf.append((String)propToGet.description);
                    xmlBuf.append((String)"\n  </Property>");
                }
                xmlBuf.append((String)"\n </PropertyCategory>");
            }
        }
        catch (IllegalAccessException iae) {
            throw SQLError.createSQLException((String)"Internal properties failure", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        xmlBuf.append((String)"\n</ConnectionProperties>");
        return xmlBuf.toString();
    }

    @Override
    public boolean getAllowLoadLocalInfile() {
        return this.allowLoadLocalInfile.getValueAsBoolean();
    }

    @Override
    public boolean getAllowMultiQueries() {
        return this.allowMultiQueries.getValueAsBoolean();
    }

    @Override
    public boolean getAllowNanAndInf() {
        return this.allowNanAndInf.getValueAsBoolean();
    }

    @Override
    public boolean getAllowUrlInLocalInfile() {
        return this.allowUrlInLocalInfile.getValueAsBoolean();
    }

    @Override
    public boolean getAlwaysSendSetIsolation() {
        return this.alwaysSendSetIsolation.getValueAsBoolean();
    }

    @Override
    public boolean getAutoDeserialize() {
        return this.autoDeserialize.getValueAsBoolean();
    }

    @Override
    public boolean getAutoGenerateTestcaseScript() {
        return this.autoGenerateTestcaseScriptAsBoolean;
    }

    @Override
    public boolean getAutoReconnectForPools() {
        return this.autoReconnectForPoolsAsBoolean;
    }

    @Override
    public int getBlobSendChunkSize() {
        return this.blobSendChunkSize.getValueAsInt();
    }

    @Override
    public boolean getCacheCallableStatements() {
        return this.cacheCallableStatements.getValueAsBoolean();
    }

    @Override
    public boolean getCachePreparedStatements() {
        return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
    }

    @Override
    public boolean getCacheResultSetMetadata() {
        return this.cacheResultSetMetaDataAsBoolean;
    }

    @Override
    public boolean getCacheServerConfiguration() {
        return this.cacheServerConfiguration.getValueAsBoolean();
    }

    @Override
    public int getCallableStatementCacheSize() {
        return this.callableStatementCacheSize.getValueAsInt();
    }

    @Override
    public boolean getCapitalizeTypeNames() {
        return this.capitalizeTypeNames.getValueAsBoolean();
    }

    @Override
    public String getCharacterSetResults() {
        return this.characterSetResults.getValueAsString();
    }

    @Override
    public String getConnectionAttributes() {
        return this.connectionAttributes.getValueAsString();
    }

    public void setConnectionAttributes(String val) {
        this.connectionAttributes.setValue((String)val);
    }

    @Override
    public boolean getClobberStreamingResults() {
        return this.clobberStreamingResults.getValueAsBoolean();
    }

    @Override
    public String getClobCharacterEncoding() {
        return this.clobCharacterEncoding.getValueAsString();
    }

    @Override
    public String getConnectionCollation() {
        return this.connectionCollation.getValueAsString();
    }

    @Override
    public int getConnectTimeout() {
        return this.connectTimeout.getValueAsInt();
    }

    @Override
    public boolean getContinueBatchOnError() {
        return this.continueBatchOnError.getValueAsBoolean();
    }

    @Override
    public boolean getCreateDatabaseIfNotExist() {
        return this.createDatabaseIfNotExist.getValueAsBoolean();
    }

    @Override
    public int getDefaultFetchSize() {
        return this.defaultFetchSize.getValueAsInt();
    }

    @Override
    public boolean getDontTrackOpenResources() {
        return this.dontTrackOpenResources.getValueAsBoolean();
    }

    @Override
    public boolean getDumpQueriesOnException() {
        return this.dumpQueriesOnException.getValueAsBoolean();
    }

    @Override
    public boolean getDynamicCalendars() {
        return this.dynamicCalendars.getValueAsBoolean();
    }

    @Override
    public boolean getElideSetAutoCommits() {
        return false;
    }

    @Override
    public boolean getEmptyStringsConvertToZero() {
        return this.emptyStringsConvertToZero.getValueAsBoolean();
    }

    @Override
    public boolean getEmulateLocators() {
        return this.emulateLocators.getValueAsBoolean();
    }

    @Override
    public boolean getEmulateUnsupportedPstmts() {
        return this.emulateUnsupportedPstmts.getValueAsBoolean();
    }

    @Override
    public boolean getEnablePacketDebug() {
        return this.enablePacketDebug.getValueAsBoolean();
    }

    @Override
    public String getEncoding() {
        return this.characterEncodingAsString;
    }

    @Override
    public boolean getExplainSlowQueries() {
        return this.explainSlowQueries.getValueAsBoolean();
    }

    @Override
    public boolean getFailOverReadOnly() {
        return this.failOverReadOnly.getValueAsBoolean();
    }

    @Override
    public boolean getGatherPerformanceMetrics() {
        return this.gatherPerformanceMetrics.getValueAsBoolean();
    }

    protected boolean getHighAvailability() {
        return this.highAvailabilityAsBoolean;
    }

    @Override
    public boolean getHoldResultsOpenOverStatementClose() {
        return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
    }

    @Override
    public boolean getIgnoreNonTxTables() {
        return this.ignoreNonTxTables.getValueAsBoolean();
    }

    @Override
    public int getInitialTimeout() {
        return this.initialTimeout.getValueAsInt();
    }

    @Override
    public boolean getInteractiveClient() {
        return this.isInteractiveClient.getValueAsBoolean();
    }

    @Override
    public boolean getIsInteractiveClient() {
        return this.isInteractiveClient.getValueAsBoolean();
    }

    @Override
    public boolean getJdbcCompliantTruncation() {
        return this.jdbcCompliantTruncation.getValueAsBoolean();
    }

    @Override
    public int getLocatorFetchBufferSize() {
        return this.locatorFetchBufferSize.getValueAsInt();
    }

    @Override
    public String getLogger() {
        return this.loggerClassName.getValueAsString();
    }

    @Override
    public String getLoggerClassName() {
        return this.loggerClassName.getValueAsString();
    }

    @Override
    public boolean getLogSlowQueries() {
        return this.logSlowQueries.getValueAsBoolean();
    }

    @Override
    public boolean getMaintainTimeStats() {
        return this.maintainTimeStatsAsBoolean;
    }

    @Override
    public int getMaxQuerySizeToLog() {
        return this.maxQuerySizeToLog.getValueAsInt();
    }

    @Override
    public int getMaxReconnects() {
        return this.maxReconnects.getValueAsInt();
    }

    @Override
    public int getMaxRows() {
        return this.maxRowsAsInt;
    }

    @Override
    public int getMetadataCacheSize() {
        return this.metadataCacheSize.getValueAsInt();
    }

    @Override
    public boolean getNoDatetimeStringSync() {
        return this.noDatetimeStringSync.getValueAsBoolean();
    }

    @Override
    public boolean getNullCatalogMeansCurrent() {
        return this.nullCatalogMeansCurrent.getValueAsBoolean();
    }

    @Override
    public boolean getNullNamePatternMatchesAll() {
        return this.nullNamePatternMatchesAll.getValueAsBoolean();
    }

    @Override
    public int getPacketDebugBufferSize() {
        return this.packetDebugBufferSize.getValueAsInt();
    }

    @Override
    public boolean getParanoid() {
        return this.paranoid.getValueAsBoolean();
    }

    @Override
    public boolean getPedantic() {
        return this.pedantic.getValueAsBoolean();
    }

    @Override
    public int getPreparedStatementCacheSize() {
        return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
    }

    @Override
    public int getPreparedStatementCacheSqlLimit() {
        return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
    }

    @Override
    public boolean getProfileSql() {
        return this.profileSQLAsBoolean;
    }

    @Override
    public boolean getProfileSQL() {
        return this.profileSQL.getValueAsBoolean();
    }

    @Override
    public String getPropertiesTransform() {
        return this.propertiesTransform.getValueAsString();
    }

    @Override
    public int getQueriesBeforeRetryMaster() {
        return this.queriesBeforeRetryMaster.getValueAsInt();
    }

    @Override
    public boolean getReconnectAtTxEnd() {
        return this.reconnectTxAtEndAsBoolean;
    }

    @Override
    public boolean getRelaxAutoCommit() {
        return this.relaxAutoCommit.getValueAsBoolean();
    }

    @Override
    public int getReportMetricsIntervalMillis() {
        return this.reportMetricsIntervalMillis.getValueAsInt();
    }

    @Override
    public boolean getRequireSSL() {
        return this.requireSSL.getValueAsBoolean();
    }

    @Override
    public boolean getRetainStatementAfterResultSetClose() {
        return this.retainStatementAfterResultSetClose.getValueAsBoolean();
    }

    @Override
    public boolean getRollbackOnPooledClose() {
        return this.rollbackOnPooledClose.getValueAsBoolean();
    }

    @Override
    public boolean getRoundRobinLoadBalance() {
        return this.roundRobinLoadBalance.getValueAsBoolean();
    }

    @Override
    public boolean getRunningCTS13() {
        return this.runningCTS13.getValueAsBoolean();
    }

    @Override
    public int getSecondsBeforeRetryMaster() {
        return this.secondsBeforeRetryMaster.getValueAsInt();
    }

    @Override
    public String getServerTimezone() {
        return this.serverTimezone.getValueAsString();
    }

    @Override
    public String getSessionVariables() {
        return this.sessionVariables.getValueAsString();
    }

    @Override
    public int getSlowQueryThresholdMillis() {
        return this.slowQueryThresholdMillis.getValueAsInt();
    }

    @Override
    public String getSocketFactoryClassName() {
        return this.socketFactoryClassName.getValueAsString();
    }

    @Override
    public int getSocketTimeout() {
        return this.socketTimeout.getValueAsInt();
    }

    @Override
    public boolean getStrictFloatingPoint() {
        return this.strictFloatingPoint.getValueAsBoolean();
    }

    @Override
    public boolean getStrictUpdates() {
        return this.strictUpdates.getValueAsBoolean();
    }

    @Override
    public boolean getTinyInt1isBit() {
        return this.tinyInt1isBit.getValueAsBoolean();
    }

    @Override
    public boolean getTraceProtocol() {
        return this.traceProtocol.getValueAsBoolean();
    }

    @Override
    public boolean getTransformedBitIsBoolean() {
        return this.transformedBitIsBoolean.getValueAsBoolean();
    }

    @Override
    public boolean getUseCompression() {
        return this.useCompression.getValueAsBoolean();
    }

    @Override
    public boolean getUseFastIntParsing() {
        return this.useFastIntParsing.getValueAsBoolean();
    }

    @Override
    public boolean getUseHostsInPrivileges() {
        return this.useHostsInPrivileges.getValueAsBoolean();
    }

    @Override
    public boolean getUseInformationSchema() {
        return this.useInformationSchema.getValueAsBoolean();
    }

    @Override
    public boolean getUseLocalSessionState() {
        return this.useLocalSessionState.getValueAsBoolean();
    }

    @Override
    public boolean getUseOldUTF8Behavior() {
        return this.useOldUTF8BehaviorAsBoolean;
    }

    @Override
    public boolean getUseOnlyServerErrorMessages() {
        return this.useOnlyServerErrorMessages.getValueAsBoolean();
    }

    @Override
    public boolean getUseReadAheadInput() {
        return this.useReadAheadInput.getValueAsBoolean();
    }

    @Override
    public boolean getUseServerPreparedStmts() {
        return this.detectServerPreparedStmts.getValueAsBoolean();
    }

    @Override
    public boolean getUseSqlStateCodes() {
        return this.useSqlStateCodes.getValueAsBoolean();
    }

    @Override
    public boolean getUseSSL() {
        return this.useSSL.getValueAsBoolean();
    }

    @Override
    public boolean isUseSSLExplicit() {
        return this.useSSL.wasExplicitlySet;
    }

    @Override
    public boolean getUseStreamLengthsInPrepStmts() {
        return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
    }

    @Override
    public boolean getUseTimezone() {
        return this.useTimezone.getValueAsBoolean();
    }

    @Override
    public boolean getUseUltraDevWorkAround() {
        return this.useUltraDevWorkAround.getValueAsBoolean();
    }

    @Override
    public boolean getUseUnbufferedInput() {
        return this.useUnbufferedInput.getValueAsBoolean();
    }

    @Override
    public boolean getUseUnicode() {
        return this.useUnicodeAsBoolean;
    }

    @Override
    public boolean getUseUsageAdvisor() {
        return this.useUsageAdvisorAsBoolean;
    }

    @Override
    public boolean getYearIsDateType() {
        return this.yearIsDateType.getValueAsBoolean();
    }

    @Override
    public String getZeroDateTimeBehavior() {
        return this.zeroDateTimeBehavior.getValueAsString();
    }

    protected void initializeFromRef(Reference ref) throws SQLException {
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        do {
            if (i >= numPropertiesToSet) {
                this.postInitialization();
                return;
            }
            Field propertyField = PROPERTY_LIST.get((int)i);
            try {
                ConnectionProperty propToSet = (ConnectionProperty)propertyField.get((Object)this);
                if (ref != null) {
                    propToSet.initializeFrom((Reference)ref, (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)"Internal properties failure", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        } while (true);
    }

    protected void initializeProperties(Properties info) throws SQLException {
        if (info == null) return;
        String profileSqlLc = info.getProperty((String)"profileSql");
        if (profileSqlLc != null) {
            info.put("profileSQL", profileSqlLc);
        }
        Properties infoCopy = (Properties)info.clone();
        infoCopy.remove((Object)"HOST");
        infoCopy.remove((Object)"user");
        infoCopy.remove((Object)"password");
        infoCopy.remove((Object)"DBNAME");
        infoCopy.remove((Object)"PORT");
        infoCopy.remove((Object)"profileSql");
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        do {
            if (i >= numPropertiesToSet) {
                this.postInitialization();
                return;
            }
            Field propertyField = PROPERTY_LIST.get((int)i);
            try {
                ConnectionProperty propToSet = (ConnectionProperty)propertyField.get((Object)this);
                propToSet.initializeFrom((Properties)infoCopy, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ConnectionProperties.unableToInitDriverProperties") + iae.toString()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        } while (true);
    }

    protected void postInitialization() throws SQLException {
        String testEncoding;
        if (this.profileSql.getValueAsObject() != null) {
            this.profileSQL.initializeFrom((String)this.profileSql.getValueAsObject().toString(), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
        if (this.getMaxRows() == 0) {
            this.maxRows.setValueAsObject((Object)Integer.valueOf((int)-1));
        }
        if ((testEncoding = (String)this.characterEncoding.getValueAsObject()) != null) {
            try {
                String testString = "abc";
                StringUtils.getBytes((String)testString, (String)testEncoding);
            }
            catch (UnsupportedEncodingException UE) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ConnectionProperties.unsupportedCharacterEncoding", (Object[])new Object[]{testEncoding}), (String)"0S100", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
            try {
                Class.forName((String)"java.util.LinkedHashMap");
            }
            catch (ClassNotFoundException cnfe) {
                this.cacheResultSetMetadata.setValue((boolean)false);
            }
        }
        this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
        this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
        this.characterEncodingAsString = (String)this.characterEncoding.getValueAsObject();
        this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
        this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
        this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
        this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
        this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
        this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
        this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
        this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
        this.jdbcCompliantTruncationForReads = this.getJdbcCompliantTruncation();
        if (!this.getUseCursorFetch()) return;
        this.setDetectServerPreparedStmts((boolean)true);
    }

    @Override
    public void setAllowLoadLocalInfile(boolean property) {
        this.allowLoadLocalInfile.setValue((boolean)property);
    }

    @Override
    public void setAllowMultiQueries(boolean property) {
        this.allowMultiQueries.setValue((boolean)property);
    }

    @Override
    public void setAllowNanAndInf(boolean flag) {
        this.allowNanAndInf.setValue((boolean)flag);
    }

    @Override
    public void setAllowUrlInLocalInfile(boolean flag) {
        this.allowUrlInLocalInfile.setValue((boolean)flag);
    }

    @Override
    public void setAlwaysSendSetIsolation(boolean flag) {
        this.alwaysSendSetIsolation.setValue((boolean)flag);
    }

    @Override
    public void setAutoDeserialize(boolean flag) {
        this.autoDeserialize.setValue((boolean)flag);
    }

    @Override
    public void setAutoGenerateTestcaseScript(boolean flag) {
        this.autoGenerateTestcaseScript.setValue((boolean)flag);
        this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
    }

    @Override
    public void setAutoReconnect(boolean flag) {
        this.autoReconnect.setValue((boolean)flag);
    }

    @Override
    public void setAutoReconnectForConnectionPools(boolean property) {
        this.autoReconnectForPools.setValue((boolean)property);
        this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
    }

    @Override
    public void setAutoReconnectForPools(boolean flag) {
        this.autoReconnectForPools.setValue((boolean)flag);
    }

    @Override
    public void setBlobSendChunkSize(String value) throws SQLException {
        this.blobSendChunkSize.setValue((String)value, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setCacheCallableStatements(boolean flag) {
        this.cacheCallableStatements.setValue((boolean)flag);
    }

    @Override
    public void setCachePreparedStatements(boolean flag) {
        this.cachePreparedStatements.setValue((boolean)flag);
    }

    @Override
    public void setCacheResultSetMetadata(boolean property) {
        this.cacheResultSetMetadata.setValue((boolean)property);
        this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
    }

    @Override
    public void setCacheServerConfiguration(boolean flag) {
        this.cacheServerConfiguration.setValue((boolean)flag);
    }

    @Override
    public void setCallableStatementCacheSize(int size) throws SQLException {
        this.callableStatementCacheSize.setValue((int)size, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setCapitalizeDBMDTypes(boolean property) {
        this.capitalizeTypeNames.setValue((boolean)property);
    }

    @Override
    public void setCapitalizeTypeNames(boolean flag) {
        this.capitalizeTypeNames.setValue((boolean)flag);
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        this.characterEncoding.setValue((String)encoding);
    }

    @Override
    public void setCharacterSetResults(String characterSet) {
        this.characterSetResults.setValue((String)characterSet);
    }

    @Override
    public void setClobberStreamingResults(boolean flag) {
        this.clobberStreamingResults.setValue((boolean)flag);
    }

    @Override
    public void setClobCharacterEncoding(String encoding) {
        this.clobCharacterEncoding.setValue((String)encoding);
    }

    @Override
    public void setConnectionCollation(String collation) {
        this.connectionCollation.setValue((String)collation);
    }

    @Override
    public void setConnectTimeout(int timeoutMs) throws SQLException {
        this.connectTimeout.setValue((int)timeoutMs, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setContinueBatchOnError(boolean property) {
        this.continueBatchOnError.setValue((boolean)property);
    }

    @Override
    public void setCreateDatabaseIfNotExist(boolean flag) {
        this.createDatabaseIfNotExist.setValue((boolean)flag);
    }

    @Override
    public void setDefaultFetchSize(int n) throws SQLException {
        this.defaultFetchSize.setValue((int)n, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setDetectServerPreparedStmts(boolean property) {
        this.detectServerPreparedStmts.setValue((boolean)property);
    }

    @Override
    public void setDontTrackOpenResources(boolean flag) {
        this.dontTrackOpenResources.setValue((boolean)flag);
    }

    @Override
    public void setDumpQueriesOnException(boolean flag) {
        this.dumpQueriesOnException.setValue((boolean)flag);
    }

    @Override
    public void setDynamicCalendars(boolean flag) {
        this.dynamicCalendars.setValue((boolean)flag);
    }

    @Override
    public void setElideSetAutoCommits(boolean flag) {
        this.elideSetAutoCommits.setValue((boolean)flag);
    }

    @Override
    public void setEmptyStringsConvertToZero(boolean flag) {
        this.emptyStringsConvertToZero.setValue((boolean)flag);
    }

    @Override
    public void setEmulateLocators(boolean property) {
        this.emulateLocators.setValue((boolean)property);
    }

    @Override
    public void setEmulateUnsupportedPstmts(boolean flag) {
        this.emulateUnsupportedPstmts.setValue((boolean)flag);
    }

    @Override
    public void setEnablePacketDebug(boolean flag) {
        this.enablePacketDebug.setValue((boolean)flag);
    }

    @Override
    public void setEncoding(String property) {
        this.characterEncoding.setValue((String)property);
        this.characterEncodingAsString = this.characterEncoding.getValueAsString();
    }

    @Override
    public void setExplainSlowQueries(boolean flag) {
        this.explainSlowQueries.setValue((boolean)flag);
    }

    @Override
    public void setFailOverReadOnly(boolean flag) {
        this.failOverReadOnly.setValue((boolean)flag);
    }

    @Override
    public void setGatherPerformanceMetrics(boolean flag) {
        this.gatherPerformanceMetrics.setValue((boolean)flag);
    }

    protected void setHighAvailability(boolean property) {
        this.autoReconnect.setValue((boolean)property);
        this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
    }

    @Override
    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        this.holdResultsOpenOverStatementClose.setValue((boolean)flag);
    }

    @Override
    public void setIgnoreNonTxTables(boolean property) {
        this.ignoreNonTxTables.setValue((boolean)property);
    }

    @Override
    public void setInitialTimeout(int property) throws SQLException {
        this.initialTimeout.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setIsInteractiveClient(boolean property) {
        this.isInteractiveClient.setValue((boolean)property);
    }

    @Override
    public void setJdbcCompliantTruncation(boolean flag) {
        this.jdbcCompliantTruncation.setValue((boolean)flag);
    }

    @Override
    public void setLocatorFetchBufferSize(String value) throws SQLException {
        this.locatorFetchBufferSize.setValue((String)value, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setLogger(String property) {
        this.loggerClassName.setValueAsObject((Object)property);
    }

    @Override
    public void setLoggerClassName(String className) {
        this.loggerClassName.setValue((String)className);
    }

    @Override
    public void setLogSlowQueries(boolean flag) {
        this.logSlowQueries.setValue((boolean)flag);
    }

    @Override
    public void setMaintainTimeStats(boolean flag) {
        this.maintainTimeStats.setValue((boolean)flag);
        this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
    }

    @Override
    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        this.maxQuerySizeToLog.setValue((int)sizeInBytes, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setMaxReconnects(int property) throws SQLException {
        this.maxReconnects.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setMaxRows(int property) throws SQLException {
        this.maxRows.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
        this.maxRowsAsInt = this.maxRows.getValueAsInt();
    }

    @Override
    public void setMetadataCacheSize(int value) throws SQLException {
        this.metadataCacheSize.setValue((int)value, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setNoDatetimeStringSync(boolean flag) {
        this.noDatetimeStringSync.setValue((boolean)flag);
    }

    @Override
    public void setNullCatalogMeansCurrent(boolean value) {
        this.nullCatalogMeansCurrent.setValue((boolean)value);
    }

    @Override
    public void setNullNamePatternMatchesAll(boolean value) {
        this.nullNamePatternMatchesAll.setValue((boolean)value);
    }

    @Override
    public void setPacketDebugBufferSize(int size) throws SQLException {
        this.packetDebugBufferSize.setValue((int)size, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setParanoid(boolean property) {
        this.paranoid.setValue((boolean)property);
    }

    @Override
    public void setPedantic(boolean property) {
        this.pedantic.setValue((boolean)property);
    }

    @Override
    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        this.preparedStatementCacheSize.setValue((int)cacheSize, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        this.preparedStatementCacheSqlLimit.setValue((int)cacheSqlLimit, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setProfileSql(boolean property) {
        this.profileSQL.setValue((boolean)property);
        this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
    }

    @Override
    public void setProfileSQL(boolean flag) {
        this.profileSQL.setValue((boolean)flag);
    }

    @Override
    public void setPropertiesTransform(String value) {
        this.propertiesTransform.setValue((String)value);
    }

    @Override
    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        this.queriesBeforeRetryMaster.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setReconnectAtTxEnd(boolean property) {
        this.reconnectAtTxEnd.setValue((boolean)property);
        this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
    }

    @Override
    public void setRelaxAutoCommit(boolean property) {
        this.relaxAutoCommit.setValue((boolean)property);
    }

    @Override
    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        this.reportMetricsIntervalMillis.setValue((int)millis, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setRequireSSL(boolean property) {
        this.requireSSL.setValue((boolean)property);
    }

    @Override
    public void setRetainStatementAfterResultSetClose(boolean flag) {
        this.retainStatementAfterResultSetClose.setValue((boolean)flag);
    }

    @Override
    public void setRollbackOnPooledClose(boolean flag) {
        this.rollbackOnPooledClose.setValue((boolean)flag);
    }

    @Override
    public void setRoundRobinLoadBalance(boolean flag) {
        this.roundRobinLoadBalance.setValue((boolean)flag);
    }

    @Override
    public void setRunningCTS13(boolean flag) {
        this.runningCTS13.setValue((boolean)flag);
    }

    @Override
    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        this.secondsBeforeRetryMaster.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setServerTimezone(String property) {
        this.serverTimezone.setValue((String)property);
    }

    @Override
    public void setSessionVariables(String variables) {
        this.sessionVariables.setValue((String)variables);
    }

    @Override
    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        this.slowQueryThresholdMillis.setValue((int)millis, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setSocketFactoryClassName(String property) {
        this.socketFactoryClassName.setValue((String)property);
    }

    @Override
    public void setSocketTimeout(int property) throws SQLException {
        this.socketTimeout.setValue((int)property, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setStrictFloatingPoint(boolean property) {
        this.strictFloatingPoint.setValue((boolean)property);
    }

    @Override
    public void setStrictUpdates(boolean property) {
        this.strictUpdates.setValue((boolean)property);
    }

    @Override
    public void setTinyInt1isBit(boolean flag) {
        this.tinyInt1isBit.setValue((boolean)flag);
    }

    @Override
    public void setTraceProtocol(boolean flag) {
        this.traceProtocol.setValue((boolean)flag);
    }

    @Override
    public void setTransformedBitIsBoolean(boolean flag) {
        this.transformedBitIsBoolean.setValue((boolean)flag);
    }

    @Override
    public void setUseCompression(boolean property) {
        this.useCompression.setValue((boolean)property);
    }

    @Override
    public void setUseFastIntParsing(boolean flag) {
        this.useFastIntParsing.setValue((boolean)flag);
    }

    @Override
    public void setUseHostsInPrivileges(boolean property) {
        this.useHostsInPrivileges.setValue((boolean)property);
    }

    @Override
    public void setUseInformationSchema(boolean flag) {
        this.useInformationSchema.setValue((boolean)flag);
    }

    @Override
    public void setUseLocalSessionState(boolean flag) {
        this.useLocalSessionState.setValue((boolean)flag);
    }

    @Override
    public void setUseOldUTF8Behavior(boolean flag) {
        this.useOldUTF8Behavior.setValue((boolean)flag);
        this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
    }

    @Override
    public void setUseOnlyServerErrorMessages(boolean flag) {
        this.useOnlyServerErrorMessages.setValue((boolean)flag);
    }

    @Override
    public void setUseReadAheadInput(boolean flag) {
        this.useReadAheadInput.setValue((boolean)flag);
    }

    @Override
    public void setUseServerPreparedStmts(boolean flag) {
        this.detectServerPreparedStmts.setValue((boolean)flag);
    }

    @Override
    public void setUseSqlStateCodes(boolean flag) {
        this.useSqlStateCodes.setValue((boolean)flag);
    }

    @Override
    public void setUseSSL(boolean property) {
        this.useSSL.setValue((boolean)property);
    }

    @Override
    public void setUseStreamLengthsInPrepStmts(boolean property) {
        this.useStreamLengthsInPrepStmts.setValue((boolean)property);
    }

    @Override
    public void setUseTimezone(boolean property) {
        this.useTimezone.setValue((boolean)property);
    }

    @Override
    public void setUseUltraDevWorkAround(boolean property) {
        this.useUltraDevWorkAround.setValue((boolean)property);
    }

    @Override
    public void setUseUnbufferedInput(boolean flag) {
        this.useUnbufferedInput.setValue((boolean)flag);
    }

    @Override
    public void setUseUnicode(boolean flag) {
        this.useUnicode.setValue((boolean)flag);
        this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
    }

    @Override
    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        this.useUsageAdvisor.setValue((boolean)useUsageAdvisorFlag);
        this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
    }

    @Override
    public void setYearIsDateType(boolean flag) {
        this.yearIsDateType.setValue((boolean)flag);
    }

    @Override
    public void setZeroDateTimeBehavior(String behavior) {
        this.zeroDateTimeBehavior.setValue((String)behavior);
    }

    protected void storeToRef(Reference ref) throws SQLException {
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            Field propertyField = PROPERTY_LIST.get((int)i);
            try {
                ConnectionProperty propToStore = (ConnectionProperty)propertyField.get((Object)this);
                if (ref != null) {
                    propToStore.storeTo((Reference)ref);
                }
            }
            catch (IllegalAccessException iae) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ConnectionProperties.errorNotExpected"), (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            ++i;
        }
    }

    @Override
    public boolean useUnbufferedInput() {
        return this.useUnbufferedInput.getValueAsBoolean();
    }

    @Override
    public boolean getUseCursorFetch() {
        return this.useCursorFetch.getValueAsBoolean();
    }

    @Override
    public void setUseCursorFetch(boolean flag) {
        this.useCursorFetch.setValue((boolean)flag);
    }

    @Override
    public boolean getOverrideSupportsIntegrityEnhancementFacility() {
        return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
    }

    @Override
    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        this.overrideSupportsIntegrityEnhancementFacility.setValue((boolean)flag);
    }

    @Override
    public boolean getNoTimezoneConversionForTimeType() {
        return this.noTimezoneConversionForTimeType.getValueAsBoolean();
    }

    @Override
    public void setNoTimezoneConversionForTimeType(boolean flag) {
        this.noTimezoneConversionForTimeType.setValue((boolean)flag);
    }

    @Override
    public boolean getNoTimezoneConversionForDateType() {
        return this.noTimezoneConversionForDateType.getValueAsBoolean();
    }

    @Override
    public void setNoTimezoneConversionForDateType(boolean flag) {
        this.noTimezoneConversionForDateType.setValue((boolean)flag);
    }

    @Override
    public boolean getCacheDefaultTimezone() {
        return this.cacheDefaultTimezone.getValueAsBoolean();
    }

    @Override
    public void setCacheDefaultTimezone(boolean flag) {
        this.cacheDefaultTimezone.setValue((boolean)flag);
    }

    @Override
    public boolean getUseJDBCCompliantTimezoneShift() {
        return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
    }

    @Override
    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        this.useJDBCCompliantTimezoneShift.setValue((boolean)flag);
    }

    @Override
    public boolean getAutoClosePStmtStreams() {
        return this.autoClosePStmtStreams.getValueAsBoolean();
    }

    @Override
    public void setAutoClosePStmtStreams(boolean flag) {
        this.autoClosePStmtStreams.setValue((boolean)flag);
    }

    @Override
    public boolean getProcessEscapeCodesForPrepStmts() {
        return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
    }

    @Override
    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        this.processEscapeCodesForPrepStmts.setValue((boolean)flag);
    }

    @Override
    public boolean getUseGmtMillisForDatetimes() {
        return this.useGmtMillisForDatetimes.getValueAsBoolean();
    }

    @Override
    public void setUseGmtMillisForDatetimes(boolean flag) {
        this.useGmtMillisForDatetimes.setValue((boolean)flag);
    }

    @Override
    public boolean getDumpMetadataOnColumnNotFound() {
        return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
    }

    @Override
    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        this.dumpMetadataOnColumnNotFound.setValue((boolean)flag);
    }

    @Override
    public String getResourceId() {
        return this.resourceId.getValueAsString();
    }

    @Override
    public void setResourceId(String resourceId) {
        this.resourceId.setValue((String)resourceId);
    }

    @Override
    public boolean getRewriteBatchedStatements() {
        return this.rewriteBatchedStatements.getValueAsBoolean();
    }

    @Override
    public void setRewriteBatchedStatements(boolean flag) {
        this.rewriteBatchedStatements.setValue((boolean)flag);
    }

    @Override
    public boolean getJdbcCompliantTruncationForReads() {
        return this.jdbcCompliantTruncationForReads;
    }

    @Override
    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
        this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
    }

    @Override
    public boolean getUseJvmCharsetConverters() {
        return this.useJvmCharsetConverters.getValueAsBoolean();
    }

    @Override
    public void setUseJvmCharsetConverters(boolean flag) {
        this.useJvmCharsetConverters.setValue((boolean)flag);
    }

    @Override
    public boolean getPinGlobalTxToPhysicalConnection() {
        return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean();
    }

    @Override
    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        this.pinGlobalTxToPhysicalConnection.setValue((boolean)flag);
    }

    @Override
    public void setGatherPerfMetrics(boolean flag) {
        this.setGatherPerformanceMetrics((boolean)flag);
    }

    @Override
    public boolean getGatherPerfMetrics() {
        return this.getGatherPerformanceMetrics();
    }

    @Override
    public void setUltraDevHack(boolean flag) {
        this.setUseUltraDevWorkAround((boolean)flag);
    }

    @Override
    public boolean getUltraDevHack() {
        return this.getUseUltraDevWorkAround();
    }

    @Override
    public void setInteractiveClient(boolean property) {
        this.setIsInteractiveClient((boolean)property);
    }

    @Override
    public void setSocketFactory(String name) {
        this.setSocketFactoryClassName((String)name);
    }

    @Override
    public String getSocketFactory() {
        return this.getSocketFactoryClassName();
    }

    @Override
    public void setUseServerPrepStmts(boolean flag) {
        this.setUseServerPreparedStmts((boolean)flag);
    }

    @Override
    public boolean getUseServerPrepStmts() {
        return this.getUseServerPreparedStmts();
    }

    @Override
    public void setCacheCallableStmts(boolean flag) {
        this.setCacheCallableStatements((boolean)flag);
    }

    @Override
    public boolean getCacheCallableStmts() {
        return this.getCacheCallableStatements();
    }

    @Override
    public void setCachePrepStmts(boolean flag) {
        this.setCachePreparedStatements((boolean)flag);
    }

    @Override
    public boolean getCachePrepStmts() {
        return this.getCachePreparedStatements();
    }

    @Override
    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        this.setCallableStatementCacheSize((int)cacheSize);
    }

    @Override
    public int getCallableStmtCacheSize() {
        return this.getCallableStatementCacheSize();
    }

    @Override
    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        this.setPreparedStatementCacheSize((int)cacheSize);
    }

    @Override
    public int getPrepStmtCacheSize() {
        return this.getPreparedStatementCacheSize();
    }

    @Override
    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        this.setPreparedStatementCacheSqlLimit((int)sqlLimit);
    }

    @Override
    public int getPrepStmtCacheSqlLimit() {
        return this.getPreparedStatementCacheSqlLimit();
    }

    @Override
    public boolean getNoAccessToProcedureBodies() {
        return this.noAccessToProcedureBodies.getValueAsBoolean();
    }

    @Override
    public void setNoAccessToProcedureBodies(boolean flag) {
        this.noAccessToProcedureBodies.setValue((boolean)flag);
    }

    @Override
    public boolean getUseOldAliasMetadataBehavior() {
        return this.useOldAliasMetadataBehavior.getValueAsBoolean();
    }

    @Override
    public void setUseOldAliasMetadataBehavior(boolean flag) {
        this.useOldAliasMetadataBehavior.setValue((boolean)flag);
    }

    @Override
    public String getClientCertificateKeyStorePassword() {
        return this.clientCertificateKeyStorePassword.getValueAsString();
    }

    @Override
    public void setClientCertificateKeyStorePassword(String value) {
        this.clientCertificateKeyStorePassword.setValue((String)value);
    }

    @Override
    public String getClientCertificateKeyStoreType() {
        return this.clientCertificateKeyStoreType.getValueAsString();
    }

    @Override
    public void setClientCertificateKeyStoreType(String value) {
        this.clientCertificateKeyStoreType.setValue((String)value);
    }

    @Override
    public String getClientCertificateKeyStoreUrl() {
        return this.clientCertificateKeyStoreUrl.getValueAsString();
    }

    @Override
    public void setClientCertificateKeyStoreUrl(String value) {
        this.clientCertificateKeyStoreUrl.setValue((String)value);
    }

    @Override
    public String getTrustCertificateKeyStorePassword() {
        return this.trustCertificateKeyStorePassword.getValueAsString();
    }

    @Override
    public void setTrustCertificateKeyStorePassword(String value) {
        this.trustCertificateKeyStorePassword.setValue((String)value);
    }

    @Override
    public String getTrustCertificateKeyStoreType() {
        return this.trustCertificateKeyStoreType.getValueAsString();
    }

    @Override
    public void setTrustCertificateKeyStoreType(String value) {
        this.trustCertificateKeyStoreType.setValue((String)value);
    }

    @Override
    public String getTrustCertificateKeyStoreUrl() {
        return this.trustCertificateKeyStoreUrl.getValueAsString();
    }

    @Override
    public void setTrustCertificateKeyStoreUrl(String value) {
        this.trustCertificateKeyStoreUrl.setValue((String)value);
    }

    @Override
    public boolean getUseSSPSCompatibleTimezoneShift() {
        return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
    }

    @Override
    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        this.useSSPSCompatibleTimezoneShift.setValue((boolean)flag);
    }

    @Override
    public boolean getTreatUtilDateAsTimestamp() {
        return this.treatUtilDateAsTimestamp.getValueAsBoolean();
    }

    @Override
    public void setTreatUtilDateAsTimestamp(boolean flag) {
        this.treatUtilDateAsTimestamp.setValue((boolean)flag);
    }

    @Override
    public boolean getUseFastDateParsing() {
        return this.useFastDateParsing.getValueAsBoolean();
    }

    @Override
    public void setUseFastDateParsing(boolean flag) {
        this.useFastDateParsing.setValue((boolean)flag);
    }

    @Override
    public String getLocalSocketAddress() {
        return this.localSocketAddress.getValueAsString();
    }

    @Override
    public void setLocalSocketAddress(String address) {
        this.localSocketAddress.setValue((String)address);
    }

    @Override
    public void setUseConfigs(String configs) {
        this.useConfigs.setValue((String)configs);
    }

    @Override
    public String getUseConfigs() {
        return this.useConfigs.getValueAsString();
    }

    @Override
    public boolean getGenerateSimpleParameterMetadata() {
        return this.generateSimpleParameterMetadata.getValueAsBoolean();
    }

    @Override
    public void setGenerateSimpleParameterMetadata(boolean flag) {
        this.generateSimpleParameterMetadata.setValue((boolean)flag);
    }

    @Override
    public boolean getLogXaCommands() {
        return this.logXaCommands.getValueAsBoolean();
    }

    @Override
    public void setLogXaCommands(boolean flag) {
        this.logXaCommands.setValue((boolean)flag);
    }

    @Override
    public int getResultSetSizeThreshold() {
        return this.resultSetSizeThreshold.getValueAsInt();
    }

    @Override
    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        this.resultSetSizeThreshold.setValue((int)threshold, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getNetTimeoutForStreamingResults() {
        return this.netTimeoutForStreamingResults.getValueAsInt();
    }

    @Override
    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        this.netTimeoutForStreamingResults.setValue((int)value, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public boolean getEnableQueryTimeouts() {
        return this.enableQueryTimeouts.getValueAsBoolean();
    }

    @Override
    public void setEnableQueryTimeouts(boolean flag) {
        this.enableQueryTimeouts.setValue((boolean)flag);
    }

    @Override
    public boolean getPadCharsWithSpace() {
        return this.padCharsWithSpace.getValueAsBoolean();
    }

    @Override
    public void setPadCharsWithSpace(boolean flag) {
        this.padCharsWithSpace.setValue((boolean)flag);
    }

    @Override
    public boolean getUseDynamicCharsetInfo() {
        return this.useDynamicCharsetInfo.getValueAsBoolean();
    }

    @Override
    public void setUseDynamicCharsetInfo(boolean flag) {
        this.useDynamicCharsetInfo.setValue((boolean)flag);
    }

    @Override
    public String getClientInfoProvider() {
        return this.clientInfoProvider.getValueAsString();
    }

    @Override
    public void setClientInfoProvider(String classname) {
        this.clientInfoProvider.setValue((String)classname);
    }

    @Override
    public boolean getPopulateInsertRowWithDefaultValues() {
        return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
    }

    @Override
    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        this.populateInsertRowWithDefaultValues.setValue((boolean)flag);
    }

    @Override
    public String getLoadBalanceStrategy() {
        return this.loadBalanceStrategy.getValueAsString();
    }

    @Override
    public void setLoadBalanceStrategy(String strategy) {
        this.loadBalanceStrategy.setValue((String)strategy);
    }

    @Override
    public String getServerAffinityOrder() {
        return this.serverAffinityOrder.getValueAsString();
    }

    @Override
    public void setServerAffinityOrder(String hostsList) {
        this.serverAffinityOrder.setValue((String)hostsList);
    }

    @Override
    public boolean getTcpNoDelay() {
        return this.tcpNoDelay.getValueAsBoolean();
    }

    @Override
    public void setTcpNoDelay(boolean flag) {
        this.tcpNoDelay.setValue((boolean)flag);
    }

    @Override
    public boolean getTcpKeepAlive() {
        return this.tcpKeepAlive.getValueAsBoolean();
    }

    @Override
    public void setTcpKeepAlive(boolean flag) {
        this.tcpKeepAlive.setValue((boolean)flag);
    }

    @Override
    public int getTcpRcvBuf() {
        return this.tcpRcvBuf.getValueAsInt();
    }

    @Override
    public void setTcpRcvBuf(int bufSize) throws SQLException {
        this.tcpRcvBuf.setValue((int)bufSize, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getTcpSndBuf() {
        return this.tcpSndBuf.getValueAsInt();
    }

    @Override
    public void setTcpSndBuf(int bufSize) throws SQLException {
        this.tcpSndBuf.setValue((int)bufSize, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getTcpTrafficClass() {
        return this.tcpTrafficClass.getValueAsInt();
    }

    @Override
    public void setTcpTrafficClass(int classFlags) throws SQLException {
        this.tcpTrafficClass.setValue((int)classFlags, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public boolean getUseNanosForElapsedTime() {
        return this.useNanosForElapsedTime.getValueAsBoolean();
    }

    @Override
    public void setUseNanosForElapsedTime(boolean flag) {
        this.useNanosForElapsedTime.setValue((boolean)flag);
    }

    @Override
    public long getSlowQueryThresholdNanos() {
        return this.slowQueryThresholdNanos.getValueAsLong();
    }

    @Override
    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        this.slowQueryThresholdNanos.setValue((long)nanos, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public String getStatementInterceptors() {
        return this.statementInterceptors.getValueAsString();
    }

    @Override
    public void setStatementInterceptors(String value) {
        this.statementInterceptors.setValue((String)value);
    }

    @Override
    public boolean getUseDirectRowUnpack() {
        return this.useDirectRowUnpack.getValueAsBoolean();
    }

    @Override
    public void setUseDirectRowUnpack(boolean flag) {
        this.useDirectRowUnpack.setValue((boolean)flag);
    }

    @Override
    public String getLargeRowSizeThreshold() {
        return this.largeRowSizeThreshold.getValueAsString();
    }

    @Override
    public void setLargeRowSizeThreshold(String value) throws SQLException {
        this.largeRowSizeThreshold.setValue((String)value, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public boolean getUseBlobToStoreUTF8OutsideBMP() {
        return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
    }

    @Override
    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        this.useBlobToStoreUTF8OutsideBMP.setValue((boolean)flag);
    }

    @Override
    public String getUtf8OutsideBmpExcludedColumnNamePattern() {
        return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
    }

    @Override
    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        this.utf8OutsideBmpExcludedColumnNamePattern.setValue((String)regexPattern);
    }

    @Override
    public String getUtf8OutsideBmpIncludedColumnNamePattern() {
        return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
    }

    @Override
    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        this.utf8OutsideBmpIncludedColumnNamePattern.setValue((String)regexPattern);
    }

    @Override
    public boolean getIncludeInnodbStatusInDeadlockExceptions() {
        return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
    }

    @Override
    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        this.includeInnodbStatusInDeadlockExceptions.setValue((boolean)flag);
    }

    @Override
    public boolean getBlobsAreStrings() {
        return this.blobsAreStrings.getValueAsBoolean();
    }

    @Override
    public void setBlobsAreStrings(boolean flag) {
        this.blobsAreStrings.setValue((boolean)flag);
    }

    @Override
    public boolean getFunctionsNeverReturnBlobs() {
        return this.functionsNeverReturnBlobs.getValueAsBoolean();
    }

    @Override
    public void setFunctionsNeverReturnBlobs(boolean flag) {
        this.functionsNeverReturnBlobs.setValue((boolean)flag);
    }

    @Override
    public boolean getAutoSlowLog() {
        return this.autoSlowLog.getValueAsBoolean();
    }

    @Override
    public void setAutoSlowLog(boolean flag) {
        this.autoSlowLog.setValue((boolean)flag);
    }

    @Override
    public String getConnectionLifecycleInterceptors() {
        return this.connectionLifecycleInterceptors.getValueAsString();
    }

    @Override
    public void setConnectionLifecycleInterceptors(String interceptors) {
        this.connectionLifecycleInterceptors.setValue((String)interceptors);
    }

    @Override
    public String getProfilerEventHandler() {
        return this.profilerEventHandler.getValueAsString();
    }

    @Override
    public void setProfilerEventHandler(String handler) {
        this.profilerEventHandler.setValue((String)handler);
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return this.verifyServerCertificate.getValueAsBoolean();
    }

    @Override
    public void setVerifyServerCertificate(boolean flag) {
        this.verifyServerCertificate.setValue((boolean)flag);
    }

    @Override
    public boolean getUseLegacyDatetimeCode() {
        return this.useLegacyDatetimeCode.getValueAsBoolean();
    }

    @Override
    public void setUseLegacyDatetimeCode(boolean flag) {
        this.useLegacyDatetimeCode.setValue((boolean)flag);
    }

    @Override
    public boolean getSendFractionalSeconds() {
        return this.sendFractionalSeconds.getValueAsBoolean();
    }

    @Override
    public void setSendFractionalSeconds(boolean flag) {
        this.sendFractionalSeconds.setValue((boolean)flag);
    }

    @Override
    public int getSelfDestructOnPingSecondsLifetime() {
        return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
    }

    @Override
    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        this.selfDestructOnPingSecondsLifetime.setValue((int)seconds, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getSelfDestructOnPingMaxOperations() {
        return this.selfDestructOnPingMaxOperations.getValueAsInt();
    }

    @Override
    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        this.selfDestructOnPingMaxOperations.setValue((int)maxOperations, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public boolean getUseColumnNamesInFindColumn() {
        return this.useColumnNamesInFindColumn.getValueAsBoolean();
    }

    @Override
    public void setUseColumnNamesInFindColumn(boolean flag) {
        this.useColumnNamesInFindColumn.setValue((boolean)flag);
    }

    @Override
    public boolean getUseLocalTransactionState() {
        return this.useLocalTransactionState.getValueAsBoolean();
    }

    @Override
    public void setUseLocalTransactionState(boolean flag) {
        this.useLocalTransactionState.setValue((boolean)flag);
    }

    @Override
    public boolean getCompensateOnDuplicateKeyUpdateCounts() {
        return this.compensateOnDuplicateKeyUpdateCounts.getValueAsBoolean();
    }

    @Override
    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        this.compensateOnDuplicateKeyUpdateCounts.setValue((boolean)flag);
    }

    @Override
    public int getLoadBalanceBlacklistTimeout() {
        return this.loadBalanceBlacklistTimeout.getValueAsInt();
    }

    @Override
    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
        this.loadBalanceBlacklistTimeout.setValue((int)loadBalanceBlacklistTimeout, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getLoadBalancePingTimeout() {
        return this.loadBalancePingTimeout.getValueAsInt();
    }

    @Override
    public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
        this.loadBalancePingTimeout.setValue((int)loadBalancePingTimeout, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void setRetriesAllDown(int retriesAllDown) throws SQLException {
        this.retriesAllDown.setValue((int)retriesAllDown, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getRetriesAllDown() {
        return this.retriesAllDown.getValueAsInt();
    }

    @Override
    public void setUseAffectedRows(boolean flag) {
        this.useAffectedRows.setValue((boolean)flag);
    }

    @Override
    public boolean getUseAffectedRows() {
        return this.useAffectedRows.getValueAsBoolean();
    }

    @Override
    public void setPasswordCharacterEncoding(String characterSet) {
        this.passwordCharacterEncoding.setValue((String)characterSet);
    }

    @Override
    public String getPasswordCharacterEncoding() {
        String encoding = this.passwordCharacterEncoding.getValueAsString();
        if (encoding != null) {
            return encoding;
        }
        if (!this.getUseUnicode()) return "UTF-8";
        encoding = this.getEncoding();
        if (encoding == null) return "UTF-8";
        return encoding;
    }

    @Override
    public void setExceptionInterceptors(String exceptionInterceptors) {
        this.exceptionInterceptors.setValue((String)exceptionInterceptors);
    }

    @Override
    public String getExceptionInterceptors() {
        return this.exceptionInterceptors.getValueAsString();
    }

    public void setMaxAllowedPacket(int max) throws SQLException {
        this.maxAllowedPacket.setValue((int)max, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getMaxAllowedPacket() {
        return this.maxAllowedPacket.getValueAsInt();
    }

    @Override
    public boolean getQueryTimeoutKillsConnection() {
        return this.queryTimeoutKillsConnection.getValueAsBoolean();
    }

    @Override
    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
        this.queryTimeoutKillsConnection.setValue((boolean)queryTimeoutKillsConnection);
    }

    @Override
    public boolean getLoadBalanceValidateConnectionOnSwapServer() {
        return this.loadBalanceValidateConnectionOnSwapServer.getValueAsBoolean();
    }

    @Override
    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
        this.loadBalanceValidateConnectionOnSwapServer.setValue((boolean)loadBalanceValidateConnectionOnSwapServer);
    }

    @Override
    public String getLoadBalanceConnectionGroup() {
        return this.loadBalanceConnectionGroup.getValueAsString();
    }

    @Override
    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
        this.loadBalanceConnectionGroup.setValue((String)loadBalanceConnectionGroup);
    }

    @Override
    public String getLoadBalanceExceptionChecker() {
        return this.loadBalanceExceptionChecker.getValueAsString();
    }

    @Override
    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
        this.loadBalanceExceptionChecker.setValue((String)loadBalanceExceptionChecker);
    }

    @Override
    public String getLoadBalanceSQLStateFailover() {
        return this.loadBalanceSQLStateFailover.getValueAsString();
    }

    @Override
    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
        this.loadBalanceSQLStateFailover.setValue((String)loadBalanceSQLStateFailover);
    }

    @Override
    public String getLoadBalanceSQLExceptionSubclassFailover() {
        return this.loadBalanceSQLExceptionSubclassFailover.getValueAsString();
    }

    @Override
    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
        this.loadBalanceSQLExceptionSubclassFailover.setValue((String)loadBalanceSQLExceptionSubclassFailover);
    }

    @Override
    public boolean getLoadBalanceEnableJMX() {
        return this.loadBalanceEnableJMX.getValueAsBoolean();
    }

    @Override
    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
        this.loadBalanceEnableJMX.setValue((boolean)loadBalanceEnableJMX);
    }

    @Override
    public void setLoadBalanceHostRemovalGracePeriod(int loadBalanceHostRemovalGracePeriod) throws SQLException {
        this.loadBalanceHostRemovalGracePeriod.setValue((int)loadBalanceHostRemovalGracePeriod, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getLoadBalanceHostRemovalGracePeriod() {
        return this.loadBalanceHostRemovalGracePeriod.getValueAsInt();
    }

    @Override
    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
        this.loadBalanceAutoCommitStatementThreshold.setValue((int)loadBalanceAutoCommitStatementThreshold, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getLoadBalanceAutoCommitStatementThreshold() {
        return this.loadBalanceAutoCommitStatementThreshold.getValueAsInt();
    }

    @Override
    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
        this.loadBalanceAutoCommitStatementRegex.setValue((String)loadBalanceAutoCommitStatementRegex);
    }

    @Override
    public String getLoadBalanceAutoCommitStatementRegex() {
        return this.loadBalanceAutoCommitStatementRegex.getValueAsString();
    }

    @Override
    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        this.includeThreadDumpInDeadlockExceptions.setValue((boolean)flag);
    }

    @Override
    public boolean getIncludeThreadDumpInDeadlockExceptions() {
        return this.includeThreadDumpInDeadlockExceptions.getValueAsBoolean();
    }

    @Override
    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        this.includeThreadNamesAsStatementComment.setValue((boolean)flag);
    }

    @Override
    public boolean getIncludeThreadNamesAsStatementComment() {
        return this.includeThreadNamesAsStatementComment.getValueAsBoolean();
    }

    @Override
    public void setAuthenticationPlugins(String authenticationPlugins) {
        this.authenticationPlugins.setValue((String)authenticationPlugins);
    }

    @Override
    public String getAuthenticationPlugins() {
        return this.authenticationPlugins.getValueAsString();
    }

    @Override
    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
        this.disabledAuthenticationPlugins.setValue((String)disabledAuthenticationPlugins);
    }

    @Override
    public String getDisabledAuthenticationPlugins() {
        return this.disabledAuthenticationPlugins.getValueAsString();
    }

    @Override
    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
        this.defaultAuthenticationPlugin.setValue((String)defaultAuthenticationPlugin);
    }

    @Override
    public String getDefaultAuthenticationPlugin() {
        return this.defaultAuthenticationPlugin.getValueAsString();
    }

    @Override
    public void setParseInfoCacheFactory(String factoryClassname) {
        this.parseInfoCacheFactory.setValue((String)factoryClassname);
    }

    @Override
    public String getParseInfoCacheFactory() {
        return this.parseInfoCacheFactory.getValueAsString();
    }

    @Override
    public void setServerConfigCacheFactory(String factoryClassname) {
        this.serverConfigCacheFactory.setValue((String)factoryClassname);
    }

    @Override
    public String getServerConfigCacheFactory() {
        return this.serverConfigCacheFactory.getValueAsString();
    }

    @Override
    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
        this.disconnectOnExpiredPasswords.setValue((boolean)disconnectOnExpiredPasswords);
    }

    @Override
    public boolean getDisconnectOnExpiredPasswords() {
        return this.disconnectOnExpiredPasswords.getValueAsBoolean();
    }

    public String getReplicationConnectionGroup() {
        return this.replicationConnectionGroup.getValueAsString();
    }

    public void setReplicationConnectionGroup(String replicationConnectionGroup) {
        this.replicationConnectionGroup.setValue((String)replicationConnectionGroup);
    }

    @Override
    public boolean getAllowMasterDownConnections() {
        return this.allowMasterDownConnections.getValueAsBoolean();
    }

    @Override
    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        this.allowMasterDownConnections.setValue((boolean)connectIfMasterDown);
    }

    @Override
    public boolean getAllowSlaveDownConnections() {
        return this.allowSlaveDownConnections.getValueAsBoolean();
    }

    @Override
    public void setAllowSlaveDownConnections(boolean connectIfSlaveDown) {
        this.allowSlaveDownConnections.setValue((boolean)connectIfSlaveDown);
    }

    @Override
    public boolean getReadFromMasterWhenNoSlaves() {
        return this.readFromMasterWhenNoSlaves.getValueAsBoolean();
    }

    @Override
    public void setReadFromMasterWhenNoSlaves(boolean useMasterIfSlavesDown) {
        this.readFromMasterWhenNoSlaves.setValue((boolean)useMasterIfSlavesDown);
    }

    @Override
    public boolean getReplicationEnableJMX() {
        return this.replicationEnableJMX.getValueAsBoolean();
    }

    @Override
    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        this.replicationEnableJMX.setValue((boolean)replicationEnableJMX);
    }

    @Override
    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        this.getProceduresReturnsFunctions.setValue((boolean)getProcedureReturnsFunctions);
    }

    @Override
    public boolean getGetProceduresReturnsFunctions() {
        return this.getProceduresReturnsFunctions.getValueAsBoolean();
    }

    @Override
    public void setDetectCustomCollations(boolean detectCustomCollations) {
        this.detectCustomCollations.setValue((boolean)detectCustomCollations);
    }

    @Override
    public boolean getDetectCustomCollations() {
        return this.detectCustomCollations.getValueAsBoolean();
    }

    @Override
    public String getServerRSAPublicKeyFile() {
        return this.serverRSAPublicKeyFile.getValueAsString();
    }

    @Override
    public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile) throws SQLException {
        if (this.serverRSAPublicKeyFile.getUpdateCount() > 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ConnectionProperties.dynamicChangeIsNotAllowed", (Object[])new Object[]{"'serverRSAPublicKeyFile'"}), (String)"S1009", null);
        }
        this.serverRSAPublicKeyFile.setValue((String)serverRSAPublicKeyFile);
    }

    @Override
    public boolean getAllowPublicKeyRetrieval() {
        return this.allowPublicKeyRetrieval.getValueAsBoolean();
    }

    @Override
    public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) throws SQLException {
        if (this.allowPublicKeyRetrieval.getUpdateCount() > 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ConnectionProperties.dynamicChangeIsNotAllowed", (Object[])new Object[]{"'allowPublicKeyRetrieval'"}), (String)"S1009", null);
        }
        this.allowPublicKeyRetrieval.setValue((boolean)allowPublicKeyRetrieval);
    }

    @Override
    public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL) {
        this.dontCheckOnDuplicateKeyUpdateInSQL.setValue((boolean)dontCheckOnDuplicateKeyUpdateInSQL);
    }

    @Override
    public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
        return this.dontCheckOnDuplicateKeyUpdateInSQL.getValueAsBoolean();
    }

    @Override
    public void setSocksProxyHost(String socksProxyHost) {
        this.socksProxyHost.setValue((String)socksProxyHost);
    }

    @Override
    public String getSocksProxyHost() {
        return this.socksProxyHost.getValueAsString();
    }

    @Override
    public void setSocksProxyPort(int socksProxyPort) throws SQLException {
        this.socksProxyPort.setValue((int)socksProxyPort, null);
    }

    @Override
    public int getSocksProxyPort() {
        return this.socksProxyPort.getValueAsInt();
    }

    @Override
    public boolean getReadOnlyPropagatesToServer() {
        return this.readOnlyPropagatesToServer.getValueAsBoolean();
    }

    @Override
    public void setReadOnlyPropagatesToServer(boolean flag) {
        this.readOnlyPropagatesToServer.setValue((boolean)flag);
    }

    @Override
    public String getEnabledSSLCipherSuites() {
        return this.enabledSSLCipherSuites.getValueAsString();
    }

    @Override
    public void setEnabledSSLCipherSuites(String cipherSuites) {
        this.enabledSSLCipherSuites.setValue((String)cipherSuites);
    }

    @Override
    public String getEnabledTLSProtocols() {
        return this.enabledTLSProtocols.getValueAsString();
    }

    @Override
    public void setEnabledTLSProtocols(String protocols) {
        this.enabledTLSProtocols.setValue((String)protocols);
    }

    @Override
    public boolean getEnableEscapeProcessing() {
        return this.enableEscapeProcessing.getValueAsBoolean();
    }

    @Override
    public void setEnableEscapeProcessing(boolean flag) {
        this.enableEscapeProcessing.setValue((boolean)flag);
    }

    static {
        try {
            Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
            int i = 0;
            while (i < declaredFields.length) {
                if (ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType())) {
                    PROPERTY_LIST.add((Field)declaredFields[i]);
                }
                ++i;
            }
            return;
        }
        catch (Exception ex) {
            RuntimeException rtEx = new RuntimeException();
            rtEx.initCause((Throwable)ex);
            throw rtEx;
        }
    }
}

