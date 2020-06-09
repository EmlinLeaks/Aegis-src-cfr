/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.CacheAdapter;
import com.mysql.jdbc.CacheAdapterFactory;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.Constants;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.EscapeProcessor;
import com.mysql.jdbc.EscapeProcessorResult;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.IterateBlock;
import com.mysql.jdbc.LicenseConfiguration;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.MultiHostMySQLConnection;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlCharset;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.MysqlSavepoint;
import com.mysql.jdbc.NamedPipeSocketFactory;
import com.mysql.jdbc.NetworkResources;
import com.mysql.jdbc.NoSubInterceptorWrapper;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ProfilerEventHandlerFactory;
import com.mysql.jdbc.ReflectiveStatementInterceptorAdapter;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ServerPreparedStatement;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.SocketMetadata;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StatementInterceptor;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import com.mysql.jdbc.UpdatableResultSet;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.V1toV2StatementInterceptorAdapter;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogFactory;
import com.mysql.jdbc.log.NullLogger;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import com.mysql.jdbc.util.LRUCache;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import java.security.Permission;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConnectionImpl
extends ConnectionPropertiesImpl
implements MySQLConnection {
    private static final long serialVersionUID = 2877471301981509474L;
    private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission((String)"setNetworkTimeout");
    private static final SQLPermission ABORT_PERM = new SQLPermission((String)"abort");
    public static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
    private MySQLConnection proxy = null;
    private InvocationHandler realProxy = null;
    private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
    public static Map<?, ?> charsetMap;
    protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
    private static final int HISTOGRAM_BUCKETS = 20;
    private static final String LOGGER_INSTANCE_NAME = "MySQL";
    private static Map<String, Integer> mapTransIsolationNameToValue;
    private static final Log NULL_LOGGER;
    protected static Map<?, ?> roundRobinStatsMap;
    private static final Map<String, Map<Integer, String>> customIndexToCharsetMapByUrl;
    private static final Map<String, Map<String, Integer>> customCharsetToMblenMapByUrl;
    private CacheAdapter<String, Map<String, String>> serverConfigCache;
    private long queryTimeCount;
    private double queryTimeSum;
    private double queryTimeSumSquares;
    private double queryTimeMean;
    private transient Timer cancelTimer;
    private List<Extension> connectionLifecycleInterceptors;
    private static final Constructor<?> JDBC_4_CONNECTION_CTOR;
    private static final int DEFAULT_RESULT_SET_TYPE = 1003;
    private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
    private static final Random random;
    private boolean autoCommit = true;
    private CacheAdapter<String, PreparedStatement.ParseInfo> cachedPreparedStatementParams;
    private String characterSetMetadata = null;
    private String characterSetResultsOnServer = null;
    private final Map<String, Object> charsetConverterMap = new HashMap<String, Object>((int)CharsetMapping.getNumberOfCharsetsConfigured());
    private long connectionCreationTimeMillis = 0L;
    private long connectionId;
    private String database = null;
    private java.sql.DatabaseMetaData dbmd = null;
    private TimeZone defaultTimeZone;
    private ProfilerEventHandler eventSink;
    private Throwable forceClosedReason;
    private boolean hasIsolationLevels = false;
    private boolean hasQuotedIdentifiers = false;
    private String host = null;
    public Map<Integer, String> indexToCustomMysqlCharset = null;
    private Map<String, Integer> mysqlCharsetToCustomMblen = null;
    private transient MysqlIO io = null;
    private boolean isClientTzUTC = false;
    private boolean isClosed = true;
    private boolean isInGlobalTx = false;
    private boolean isRunningOnJDK13 = false;
    private int isolationLevel = 2;
    private boolean isServerTzUTC = false;
    private long lastQueryFinishedTime = 0L;
    private transient Log log = NULL_LOGGER;
    private long longestQueryTimeMs = 0L;
    private boolean lowerCaseTableNames = false;
    private long maximumNumberTablesAccessed = 0L;
    private int sessionMaxRows = -1;
    private long metricsLastReportedMs;
    private long minimumNumberTablesAccessed = Long.MAX_VALUE;
    private String myURL = null;
    private boolean needsPing = false;
    private int netBufferLength = 16384;
    private boolean noBackslashEscapes = false;
    private boolean serverTruncatesFracSecs = false;
    private long numberOfPreparedExecutes = 0L;
    private long numberOfPrepares = 0L;
    private long numberOfQueriesIssued = 0L;
    private long numberOfResultSetsCreated = 0L;
    private long[] numTablesMetricsHistBreakpoints;
    private int[] numTablesMetricsHistCounts;
    private long[] oldHistBreakpoints = null;
    private int[] oldHistCounts = null;
    private final CopyOnWriteArrayList<Statement> openStatements = new CopyOnWriteArrayList<E>();
    private LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> parsedCallableStatementCache;
    private boolean parserKnowsUnicode = false;
    private String password = null;
    private long[] perfMetricsHistBreakpoints;
    private int[] perfMetricsHistCounts;
    private int port = 3306;
    protected Properties props = null;
    private boolean readInfoMsg = false;
    private boolean readOnly = false;
    protected LRUCache<String, CachedResultSetMetaData> resultSetMetadataCache;
    private TimeZone serverTimezoneTZ = null;
    private Map<String, String> serverVariables = null;
    private long shortestQueryTimeMs = Long.MAX_VALUE;
    private double totalQueryTimeMs = 0.0;
    private boolean transactionsSupported = false;
    private Map<String, Class<?>> typeMap;
    private boolean useAnsiQuotes = false;
    private String user = null;
    private boolean useServerPreparedStmts = false;
    private LRUCache<String, Boolean> serverSideStatementCheckCache;
    private LRUCache<CompoundCacheKey, ServerPreparedStatement> serverSideStatementCache;
    private Calendar sessionCalendar;
    private Calendar utcCalendar;
    private String origHostToConnectTo;
    private int origPortToConnectTo;
    private String origDatabaseToConnectTo;
    private String errorMessageEncoding = "Cp1252";
    private boolean usePlatformCharsetConverters;
    private boolean hasTriedMasterFlag = false;
    private String statementComment = null;
    private boolean storesLowerCaseTableName;
    private List<StatementInterceptorV2> statementInterceptors;
    private boolean requiresEscapingEncoder;
    private String hostPortPair;
    private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";
    private int autoIncrementIncrement = 0;
    private ExceptionInterceptor exceptionInterceptor;

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getHostPortPair() {
        String string;
        if (this.hostPortPair != null) {
            string = this.hostPortPair;
            return string;
        }
        string = this.host + ":" + this.port;
        return string;
    }

    @Override
    public boolean isProxySet() {
        if (this.proxy == null) return false;
        return true;
    }

    @Override
    public void setProxy(MySQLConnection proxy) {
        this.proxy = proxy;
        this.realProxy = this.proxy instanceof MultiHostMySQLConnection ? ((MultiHostMySQLConnection)proxy).getThisAsProxy() : null;
    }

    private MySQLConnection getProxy() {
        MySQLConnection mySQLConnection;
        if (this.proxy != null) {
            mySQLConnection = this.proxy;
            return mySQLConnection;
        }
        mySQLConnection = this;
        return mySQLConnection;
    }

    @Deprecated
    @Override
    public MySQLConnection getLoadBalanceSafeProxy() {
        return this.getMultiHostSafeProxy();
    }

    @Override
    public MySQLConnection getMultiHostSafeProxy() {
        return this.getProxy();
    }

    @Override
    public MySQLConnection getActiveMySQLConnection() {
        return this;
    }

    @Override
    public Object getConnectionMutex() {
        Object object;
        if (this.realProxy != null) {
            object = this.realProxy;
            return object;
        }
        object = this.getProxy();
        return object;
    }

    protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor) {
        String origMessage = sqlEx.getMessage();
        String sqlState = sqlEx.getSQLState();
        int vendorErrorCode = sqlEx.getErrorCode();
        StringBuilder messageBuf = new StringBuilder((int)(origMessage.length() + messageToAppend.length()));
        messageBuf.append((String)origMessage);
        messageBuf.append((String)messageToAppend);
        SQLException sqlExceptionWithNewMessage = SQLError.createSQLException((String)messageBuf.toString(), (String)sqlState, (int)vendorErrorCode, (ExceptionInterceptor)interceptor);
        try {
            Method getStackTraceMethod = null;
            Method setStackTraceMethod = null;
            Object theStackTraceAsObject = null;
            Class<?> stackTraceElementClass = Class.forName((String)"java.lang.StackTraceElement");
            Class<?> stackTraceElementArrayClass = Array.newInstance(stackTraceElementClass, (int[])new int[]{0}).getClass();
            getStackTraceMethod = Throwable.class.getMethod((String)"getStackTrace", new Class[0]);
            setStackTraceMethod = Throwable.class.getMethod((String)"setStackTrace", stackTraceElementArrayClass);
            if (getStackTraceMethod == null) return sqlExceptionWithNewMessage;
            if (setStackTraceMethod == null) return sqlExceptionWithNewMessage;
            theStackTraceAsObject = getStackTraceMethod.invoke((Object)sqlEx, (Object[])new Object[0]);
            setStackTraceMethod.invoke((Object)sqlExceptionWithNewMessage, (Object[])new Object[]{theStackTraceAsObject});
            return sqlExceptionWithNewMessage;
        }
        catch (NoClassDefFoundError noClassDefFound) {
            return sqlExceptionWithNewMessage;
        }
        catch (NoSuchMethodException noSuchMethodEx) {
            return sqlExceptionWithNewMessage;
        }
        catch (Throwable catchAll) {
            // empty catch block
        }
        return sqlExceptionWithNewMessage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Timer getCancelTimer() {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.cancelTimer == null) {
            this.cancelTimer = new Timer((String)"MySQL Statement Cancellation Timer", (boolean)true);
        }
        // MONITOREXIT : object
        return this.cancelTimer;
    }

    protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
        if (Util.isJdbc4()) return (Connection)Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, (Object[])new Object[]{hostToConnectTo, Integer.valueOf((int)portToConnectTo), info, databaseToConnectTo, url}, null);
        return new ConnectionImpl((String)hostToConnectTo, (int)portToConnectTo, (Properties)info, (String)databaseToConnectTo, (String)url);
    }

    protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList) {
        int indexRange = hostList.size();
        return random.nextInt((int)indexRange);
    }

    private static boolean nullSafeCompare(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null && s2 != null) {
            return false;
        }
        if (s1 == null) return false;
        if (!s1.equals((Object)s2)) return false;
        return true;
    }

    protected ConnectionImpl() {
    }

    public ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
        this.connectionCreationTimeMillis = System.currentTimeMillis();
        if (databaseToConnectTo == null) {
            databaseToConnectTo = "";
        }
        this.origHostToConnectTo = hostToConnectTo;
        this.origPortToConnectTo = portToConnectTo;
        this.origDatabaseToConnectTo = databaseToConnectTo;
        try {
            Blob.class.getMethod((String)"truncate", Long.TYPE);
            this.isRunningOnJDK13 = false;
        }
        catch (NoSuchMethodException nsme) {
            this.isRunningOnJDK13 = true;
        }
        this.sessionCalendar = new GregorianCalendar();
        this.utcCalendar = new GregorianCalendar();
        this.utcCalendar.setTimeZone((TimeZone)TimeZone.getTimeZone((String)"GMT"));
        this.log = LogFactory.getLogger((String)this.getLogger(), (String)LOGGER_INSTANCE_NAME, (ExceptionInterceptor)this.getExceptionInterceptor());
        if (NonRegisteringDriver.isHostPropertiesList((String)hostToConnectTo)) {
            Properties hostSpecificProps = NonRegisteringDriver.expandHostKeyValues((String)hostToConnectTo);
            Enumeration<?> propertyNames = hostSpecificProps.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String propertyName = propertyNames.nextElement().toString();
                String propertyValue = hostSpecificProps.getProperty((String)propertyName);
                info.setProperty((String)propertyName, (String)propertyValue);
            }
        } else if (hostToConnectTo == null) {
            this.host = "localhost";
            this.hostPortPair = this.host + ":" + portToConnectTo;
        } else {
            this.host = hostToConnectTo;
            this.hostPortPair = hostToConnectTo.indexOf((String)":") == -1 ? this.host + ":" + portToConnectTo : this.host;
        }
        this.port = portToConnectTo;
        this.database = databaseToConnectTo;
        this.myURL = url;
        this.user = info.getProperty((String)"user");
        this.password = info.getProperty((String)"password");
        if (this.user == null || this.user.equals((Object)"")) {
            this.user = "";
        }
        if (this.password == null) {
            this.password = "";
        }
        this.props = info;
        this.initializeDriverProperties((Properties)info);
        this.defaultTimeZone = TimeUtil.getDefaultTimeZone((boolean)this.getCacheDefaultTimezone());
        this.isClientTzUTC = !this.defaultTimeZone.useDaylightTime() && this.defaultTimeZone.getRawOffset() == 0;
        try {
            this.dbmd = this.getMetaData((boolean)false, (boolean)false);
            this.initializeSafeStatementInterceptors();
            this.createNewIO((boolean)false);
            this.unSafeStatementInterceptors();
        }
        catch (SQLException ex) {
            this.cleanup((Throwable)ex);
            throw ex;
        }
        catch (Exception ex) {
            this.cleanup((Throwable)ex);
            StringBuilder mesg = new StringBuilder((int)128);
            if (!this.getParanoid()) {
                mesg.append((String)"Cannot connect to MySQL server on ");
                mesg.append((String)this.host);
                mesg.append((String)":");
                mesg.append((int)this.port);
                mesg.append((String)".\n\n");
                mesg.append((String)"Make sure that there is a MySQL server ");
                mesg.append((String)"running on the machine/port you are trying ");
                mesg.append((String)"to connect to and that the machine this software is running on ");
                mesg.append((String)"is able to connect to this host/port (i.e. not firewalled). ");
                mesg.append((String)"Also make sure that the server has not been started with the --skip-networking ");
                mesg.append((String)"flag.\n\n");
            } else {
                mesg.append((String)"Unable to connect to database.");
            }
            SQLException sqlEx = SQLError.createSQLException((String)mesg.toString(), (String)"08S01", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        AbandonedConnectionCleanupThread.trackConnection((MySQLConnection)this, (NetworkResources)this.io.getNetworkResources());
    }

    @Override
    public void unSafeStatementInterceptors() throws SQLException {
        ArrayList<StatementInterceptorV2> unSafedStatementInterceptors = new ArrayList<StatementInterceptorV2>((int)this.statementInterceptors.size());
        int i = 0;
        do {
            if (i >= this.statementInterceptors.size()) {
                this.statementInterceptors = unSafedStatementInterceptors;
                if (this.io == null) return;
                this.io.setStatementInterceptors(this.statementInterceptors);
                return;
            }
            NoSubInterceptorWrapper wrappedInterceptor = (NoSubInterceptorWrapper)this.statementInterceptors.get((int)i);
            unSafedStatementInterceptors.add((StatementInterceptorV2)wrappedInterceptor.getUnderlyingInterceptor());
            ++i;
        } while (true);
    }

    @Override
    public void initializeSafeStatementInterceptors() throws SQLException {
        this.isClosed = false;
        List<Extension> unwrappedInterceptors = Util.loadExtensions((Connection)this, (Properties)this.props, (String)this.getStatementInterceptors(), (String)"MysqlIo.BadStatementInterceptor", (ExceptionInterceptor)this.getExceptionInterceptor());
        this.statementInterceptors = new ArrayList<StatementInterceptorV2>((int)unwrappedInterceptors.size());
        int i = 0;
        while (i < unwrappedInterceptors.size()) {
            Extension interceptor = unwrappedInterceptors.get((int)i);
            if (interceptor instanceof StatementInterceptor) {
                if (ReflectiveStatementInterceptorAdapter.getV2PostProcessMethod(interceptor.getClass()) != null) {
                    this.statementInterceptors.add((StatementInterceptorV2)new NoSubInterceptorWrapper((StatementInterceptorV2)new ReflectiveStatementInterceptorAdapter((StatementInterceptor)((StatementInterceptor)interceptor))));
                } else {
                    this.statementInterceptors.add((StatementInterceptorV2)new NoSubInterceptorWrapper((StatementInterceptorV2)new V1toV2StatementInterceptorAdapter((StatementInterceptor)((StatementInterceptor)interceptor))));
                }
            } else {
                this.statementInterceptors.add((StatementInterceptorV2)new NoSubInterceptorWrapper((StatementInterceptorV2)((StatementInterceptorV2)interceptor)));
            }
            ++i;
        }
    }

    @Override
    public List<StatementInterceptorV2> getStatementInterceptorsInstances() {
        return this.statementInterceptors;
    }

    private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound) {
        if (histogramCounts == null) {
            this.createInitialHistogram((long[])histogramBreakpoints, (long)currentLowerBound, (long)currentUpperBound);
            return;
        }
        int i = 0;
        while (i < 20) {
            if (histogramBreakpoints[i] >= value) {
                int[] arrn = histogramCounts;
                int n = i;
                arrn[n] = arrn[n] + numberOfTimes;
                return;
            }
            ++i;
        }
    }

    private void addToPerformanceHistogram(long value, int numberOfTimes) {
        this.checkAndCreatePerformanceHistogram();
        this.addToHistogram((int[])this.perfMetricsHistCounts, (long[])this.perfMetricsHistBreakpoints, (long)value, (int)numberOfTimes, (long)(this.shortestQueryTimeMs == Long.MAX_VALUE ? 0L : this.shortestQueryTimeMs), (long)this.longestQueryTimeMs);
    }

    private void addToTablesAccessedHistogram(long value, int numberOfTimes) {
        this.checkAndCreateTablesAccessedHistogram();
        this.addToHistogram((int[])this.numTablesMetricsHistCounts, (long[])this.numTablesMetricsHistBreakpoints, (long)value, (int)numberOfTimes, (long)(this.minimumNumberTablesAccessed == Long.MAX_VALUE ? 0L : this.minimumNumberTablesAccessed), (long)this.maximumNumberTablesAccessed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void buildCollationMapping() throws SQLException {
        block32 : {
            customCharset = null;
            customMblen = null;
            if (this.getCacheServerConfiguration()) {
                var3_3 = ConnectionImpl.customIndexToCharsetMapByUrl;
                // MONITORENTER : var3_3
                customCharset = ConnectionImpl.customIndexToCharsetMapByUrl.get((Object)this.getURL());
                customMblen = ConnectionImpl.customCharsetToMblenMapByUrl.get((Object)this.getURL());
                // MONITOREXIT : var3_3
            }
            if (customCharset != null || !this.getDetectCustomCollations() || !this.versionMeetsMinimum((int)4, (int)1, (int)0)) break block32;
            stmt = null;
            results = null;
            try {
                block30 : {
                    block29 : {
                        block28 : {
                            block27 : {
                                customCharset = new HashMap<Integer, String>();
                                customMblen = new HashMap<String, Integer>();
                                stmt = this.getMetadataSafeStatement();
                                try {
                                    results = stmt.executeQuery((String)"SHOW COLLATION");
                                    while (results.next()) {
                                        collationIndex = ((Number)results.getObject((int)3)).intValue();
                                        charsetName = results.getString((int)2);
                                        if (collationIndex >= 2048 || !charsetName.equals((Object)CharsetMapping.getMysqlCharsetNameForCollationIndex((Integer)Integer.valueOf((int)collationIndex)))) {
                                            customCharset.put((Integer)Integer.valueOf((int)collationIndex), (String)charsetName);
                                        }
                                        if (CharsetMapping.CHARSET_NAME_TO_CHARSET.containsKey((Object)charsetName)) continue;
                                        customMblen.put((String)charsetName, null);
                                    }
                                }
                                catch (SQLException ex) {
                                    if (ex.getErrorCode() != 1820) throw ex;
                                    if (!this.getDisconnectOnExpiredPasswords()) break block27;
                                    throw ex;
                                }
                            }
                            if (customMblen.size() > 0) {
                                try {
                                    results = stmt.executeQuery((String)"SHOW CHARACTER SET");
                                    while (results.next()) {
                                        charsetName = results.getString((String)"Charset");
                                        if (!customMblen.containsKey((Object)charsetName)) continue;
                                        customMblen.put((String)charsetName, (Integer)Integer.valueOf((int)results.getInt((String)"Maxlen")));
                                    }
                                }
                                catch (SQLException ex) {
                                    if (ex.getErrorCode() != 1820) throw ex;
                                    if (!this.getDisconnectOnExpiredPasswords()) break block28;
                                    throw ex;
                                }
                            }
                        }
                        if (!this.getCacheServerConfiguration()) break block29;
                        ex = ConnectionImpl.customIndexToCharsetMapByUrl;
                        // MONITORENTER : ex
                        ConnectionImpl.customIndexToCharsetMapByUrl.put((String)this.getURL(), customCharset);
                        ConnectionImpl.customCharsetToMblenMapByUrl.put((String)this.getURL(), customMblen);
                        // MONITOREXIT : ex
                    }
                    var9_12 = null;
                    if (results == null) break block30;
                    results.close();
                    break block30;
                    catch (SQLException ex) {
                        throw ex;
                    }
                    catch (RuntimeException ex) {
                        sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
                        sqlEx.initCause((Throwable)ex);
                        throw sqlEx;
                    }
                    catch (SQLException sqlE) {
                        // empty catch block
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlE) {}
                }
            }
            catch (Throwable var8_16) {
                block31 : {
                    var9_13 = null;
                    if (results != null) {
                        ** try [egrp 6[TRYBLOCK] [10 : 458->468)] { 
lbl86: // 1 sources:
                        results.close();
                        break block31;
lbl88: // 1 sources:
                        catch (SQLException sqlE) {
                            // empty catch block
                        }
                    }
                }
                if (stmt == null) throw var8_16;
                ** try [egrp 7[TRYBLOCK] [11 : 474->483)] { 
lbl93: // 1 sources:
                stmt.close();
                throw var8_16;
lbl95: // 1 sources:
                catch (SQLException sqlE) {
                    // empty catch block
                }
                throw var8_16;
            }
        }
        if (customCharset != null) {
            this.indexToCustomMysqlCharset = Collections.unmodifiableMap(customCharset);
        }
        if (customMblen == null) return;
        this.mysqlCharsetToCustomMblen = Collections.unmodifiableMap(customMblen);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
        if (sql == null) return true;
        if (sql.length() == 0) {
            return true;
        }
        if (!this.useServerPreparedStmts) {
            return false;
        }
        if (!this.getCachePreparedStatements()) return this.canHandleAsServerPreparedStatementNoCache((String)sql);
        LRUCache<String, Boolean> lRUCache = this.serverSideStatementCheckCache;
        // MONITORENTER : lRUCache
        Boolean flag = (Boolean)this.serverSideStatementCheckCache.get((Object)sql);
        if (flag != null) {
            // MONITOREXIT : lRUCache
            return flag.booleanValue();
        }
        boolean canHandle = this.canHandleAsServerPreparedStatementNoCache((String)sql);
        if (sql.length() < this.getPreparedStatementCacheSqlLimit()) {
            this.serverSideStatementCheckCache.put((String)sql, (Boolean)(canHandle ? Boolean.TRUE : Boolean.FALSE));
        }
        // MONITOREXIT : lRUCache
        return canHandle;
    }

    private boolean canHandleAsServerPreparedStatementNoCache(String sql) throws SQLException {
        String quoteChar;
        if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"CALL")) {
            return false;
        }
        boolean canHandleAsStatement = true;
        boolean allowBackslashEscapes = !this.noBackslashEscapes;
        String string = quoteChar = this.useAnsiQuotes ? "\"" : "'";
        if (this.getAllowMultiQueries()) {
            if (StringUtils.indexOfIgnoreCase((int)0, (String)sql, (String)";", (String)quoteChar, (String)quoteChar, allowBackslashEscapes ? StringUtils.SEARCH_MODE__ALL : StringUtils.SEARCH_MODE__MRK_COM_WS) == -1) return canHandleAsStatement;
            return false;
        }
        if (this.versionMeetsMinimum((int)5, (int)0, (int)7) || !StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"SELECT") && !StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"DELETE") && !StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"INSERT") && !StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"UPDATE") && !StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric((String)sql, (String)"REPLACE")) {
            if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"XA ")) {
                return false;
            }
            if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"CREATE TABLE")) {
                return false;
            }
            if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"DO")) {
                return false;
            }
            if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"SET")) {
                return false;
            }
            if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"SHOW WARNINGS") && this.versionMeetsMinimum((int)5, (int)7, (int)2)) {
                return false;
            }
            if (!sql.startsWith((String)"/* ping */")) return canHandleAsStatement;
            return false;
        }
        int currentPos = 0;
        int statementLength = sql.length();
        int lastPosToLook = statementLength - 7;
        boolean foundLimitWithPlaceholder = false;
        block0 : do {
            int limitStart;
            if (currentPos >= lastPosToLook || (limitStart = StringUtils.indexOfIgnoreCase((int)currentPos, (String)sql, (String)"LIMIT ", (String)quoteChar, (String)quoteChar, allowBackslashEscapes ? StringUtils.SEARCH_MODE__ALL : StringUtils.SEARCH_MODE__MRK_COM_WS)) == -1) {
                if (foundLimitWithPlaceholder) return false;
                return true;
            }
            currentPos = limitStart + 7;
            do {
                char c;
                if (currentPos >= statementLength || !Character.isDigit((char)(c = sql.charAt((int)currentPos))) && !Character.isWhitespace((char)c) && c != ',' && c != '?') continue block0;
                if (c == '?') {
                    foundLimitWithPlaceholder = true;
                    continue block0;
                }
                ++currentPos;
            } while (true);
            break;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void changeUser(String userName, String newPassword) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        if (userName == null || userName.equals((Object)"")) {
            userName = "";
        }
        if (newPassword == null) {
            newPassword = "";
        }
        this.sessionMaxRows = -1;
        try {
            this.io.changeUser((String)userName, (String)newPassword, (String)this.database);
        }
        catch (SQLException ex) {
            if (!this.versionMeetsMinimum((int)5, (int)6, (int)13)) throw ex;
            if (!"28000".equals((Object)ex.getSQLState())) throw ex;
            this.cleanup((Throwable)ex);
            throw ex;
        }
        this.user = userName;
        this.password = newPassword;
        if (this.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            this.configureClientCharacterSet((boolean)true);
        }
        this.setSessionVariables();
        this.setupServerForTruncationChecks();
        // MONITOREXIT : object
        return;
    }

    private boolean characterSetNamesMatches(String mysqlEncodingName) {
        if (mysqlEncodingName == null) return false;
        if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get((Object)"character_set_client"))) return false;
        if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get((Object)"character_set_connection"))) return false;
        return true;
    }

    private void checkAndCreatePerformanceHistogram() {
        if (this.perfMetricsHistCounts == null) {
            this.perfMetricsHistCounts = new int[20];
        }
        if (this.perfMetricsHistBreakpoints != null) return;
        this.perfMetricsHistBreakpoints = new long[20];
    }

    private void checkAndCreateTablesAccessedHistogram() {
        if (this.numTablesMetricsHistCounts == null) {
            this.numTablesMetricsHistCounts = new int[20];
        }
        if (this.numTablesMetricsHistBreakpoints != null) return;
        this.numTablesMetricsHistBreakpoints = new long[20];
    }

    @Override
    public void checkClosed() throws SQLException {
        if (!this.isClosed) return;
        this.throwConnectionClosedException();
    }

    @Override
    public void throwConnectionClosedException() throws SQLException {
        SQLException ex = SQLError.createSQLException((String)"No operations allowed after connection closed.", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (this.forceClosedReason == null) throw ex;
        ex.initCause((Throwable)this.forceClosedReason);
        throw ex;
    }

    private void checkServerEncoding() throws SQLException {
        SingleByteCharsetConverter converter;
        if (this.getUseUnicode() && this.getEncoding() != null) {
            return;
        }
        String serverCharset = this.serverVariables.get((Object)"character_set");
        if (serverCharset == null) {
            serverCharset = this.serverVariables.get((Object)"character_set_server");
        }
        String mappedServerEncoding = null;
        if (serverCharset != null) {
            try {
                mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlCharset((String)serverCharset);
            }
            catch (RuntimeException ex) {
                SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
                sqlEx.initCause((Throwable)ex);
                throw sqlEx;
            }
        }
        if (!this.getUseUnicode() && mappedServerEncoding != null && (converter = this.getCharsetConverter((String)mappedServerEncoding)) != null) {
            this.setUseUnicode((boolean)true);
            this.setEncoding((String)mappedServerEncoding);
            return;
        }
        if (serverCharset == null) return;
        if (mappedServerEncoding == null && Character.isLowerCase((char)serverCharset.charAt((int)0))) {
            char[] ach = serverCharset.toCharArray();
            ach[0] = Character.toUpperCase((char)serverCharset.charAt((int)0));
            this.setEncoding((String)new String((char[])ach));
        }
        if (mappedServerEncoding == null) {
            throw SQLError.createSQLException((String)("Unknown character encoding on server '" + serverCharset + "', use 'characterEncoding=' property " + " to provide correct mapping"), (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        try {
            StringUtils.getBytes((String)"abc", (String)mappedServerEncoding);
            this.setEncoding((String)mappedServerEncoding);
            this.setUseUnicode((boolean)true);
            return;
        }
        catch (UnsupportedEncodingException UE) {
            throw SQLError.createSQLException((String)("The driver can not map the character encoding '" + this.getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" to your JDBC URL."), (String)"0S100", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private void checkTransactionIsolationLevel() throws SQLException {
        String s = this.serverVariables.get((Object)"transaction_isolation");
        if (s == null) {
            s = this.serverVariables.get((Object)"tx_isolation");
        }
        if (s == null) return;
        Integer intTI = mapTransIsolationNameToValue.get((Object)s);
        if (intTI == null) return;
        this.isolationLevel = intTI.intValue();
    }

    @Override
    public void abortInternal() throws SQLException {
        if (this.io != null) {
            try {
                this.io.forceClose();
                this.io.releaseResources();
            }
            catch (Throwable t) {
                // empty catch block
            }
            this.io = null;
        }
        this.isClosed = true;
    }

    private void cleanup(Throwable whyCleanedUp) {
        try {
            if (this.io != null) {
                if (this.isClosed()) {
                    this.io.forceClose();
                } else {
                    this.realClose((boolean)false, (boolean)false, (boolean)false, (Throwable)whyCleanedUp);
                }
            }
        }
        catch (SQLException sqlEx) {
            // empty catch block
        }
        this.isClosed = true;
    }

    @Deprecated
    @Override
    public void clearHasTriedMaster() {
        this.hasTriedMasterFlag = false;
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql) throws SQLException {
        return this.clientPrepareStatement((String)sql, (int)1003, (int)1007);
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        java.sql.PreparedStatement pStmt = this.clientPrepareStatement((String)sql);
        ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((boolean)(autoGenKeyIndex == 1));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (boolean)true);
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
        this.checkClosed();
        String nativeSql = processEscapeCodesIfNeeded && this.getProcessEscapeCodesForPrepStmts() ? this.nativeSQL((String)sql) : sql;
        PreparedStatement pStmt = null;
        if (this.getCachePreparedStatements()) {
            PreparedStatement.ParseInfo pStmtInfo = this.cachedPreparedStatementParams.get((String)nativeSql);
            if (pStmtInfo == null) {
                pStmt = PreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.database);
                this.cachedPreparedStatementParams.put((String)nativeSql, (PreparedStatement.ParseInfo)pStmt.getParseInfo());
            } else {
                pStmt = PreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.database, (PreparedStatement.ParseInfo)pStmtInfo);
            }
        } else {
            pStmt = PreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.database);
        }
        pStmt.setResultSetType((int)resultSetType);
        pStmt.setResultSetConcurrency((int)resultSetConcurrency);
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement)this.clientPrepareStatement((String)sql);
        pStmt.setRetrieveGeneratedKeys((boolean)(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement)this.clientPrepareStatement((String)sql);
        pStmt.setRetrieveGeneratedKeys((boolean)(autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.connectionLifecycleInterceptors != null) {
            new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator()){
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    super(x0);
                }

                void forEach(Extension each) throws SQLException {
                    ((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).close();
                }
            }.doForAll();
        }
        this.realClose((boolean)true, (boolean)true, (boolean)false, null);
        // MONITOREXIT : object
        return;
    }

    private void closeAllOpenStatements() throws SQLException {
        SQLException postponedException = null;
        Iterator<Statement> i$ = this.openStatements.iterator();
        do {
            if (!i$.hasNext()) {
                if (postponedException == null) return;
                throw postponedException;
            }
            Statement stmt = i$.next();
            try {
                ((StatementImpl)stmt).realClose((boolean)false, (boolean)true);
            }
            catch (SQLException sqlEx) {
                postponedException = sqlEx;
                continue;
            }
            break;
        } while (true);
    }

    private void closeStatement(java.sql.Statement stmt) {
        if (stmt == null) return;
        try {
            stmt.close();
            return;
        }
        catch (SQLException sqlEx) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void commit() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        try {
            block11 : {
                try {
                    if (this.connectionLifecycleInterceptors != null) {
                        IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator()){
                            final /* synthetic */ ConnectionImpl this$0;
                            {
                                this.this$0 = connectionImpl;
                                super(x0);
                            }

                            void forEach(Extension each) throws SQLException {
                                if (((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).commit()) return;
                                this.stopIterating = true;
                            }
                        };
                        iter.doForAll();
                        if (!iter.fullIteration()) {
                            Object var4_4 = null;
                            this.needsPing = this.getReconnectAtTxEnd();
                            // MONITOREXIT : object
                            return;
                        }
                    }
                    if (this.autoCommit && !this.getRelaxAutoCommit()) {
                        throw SQLError.createSQLException((String)"Can't call commit when autocommit=true", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    if (!this.transactionsSupported) break block11;
                    if (this.getUseLocalTransactionState() && this.versionMeetsMinimum((int)5, (int)0, (int)0) && !this.io.inTransactionOnServer()) {
                        Object var4_5 = null;
                        this.needsPing = this.getReconnectAtTxEnd();
                        // MONITOREXIT : object
                        return;
                    }
                    this.execSQL(null, (String)"commit", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                }
                catch (SQLException sqlException) {
                    if (!"08S01".equals((Object)sqlException.getSQLState())) throw sqlException;
                    throw SQLError.createSQLException((String)"Communications link failure during commit(). Transaction resolution unknown.", (String)"08007", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            Object var4_6 = null;
            this.needsPing = this.getReconnectAtTxEnd();
            return;
        }
        catch (Throwable throwable) {
            Object var4_7 = null;
            this.needsPing = this.getReconnectAtTxEnd();
            throw throwable;
        }
    }

    private void configureCharsetProperties() throws SQLException {
        if (this.getEncoding() == null) return;
        try {
            String testString = "abc";
            StringUtils.getBytes((String)testString, (String)this.getEncoding());
            return;
        }
        catch (UnsupportedEncodingException UE) {
            String oldEncoding = this.getEncoding();
            try {
                this.setEncoding((String)CharsetMapping.getJavaEncodingForMysqlCharset((String)oldEncoding));
            }
            catch (RuntimeException ex) {
                SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
                sqlEx.initCause((Throwable)ex);
                throw sqlEx;
            }
            if (this.getEncoding() == null) {
                throw SQLError.createSQLException((String)("Java does not support the MySQL character encoding '" + oldEncoding + "'."), (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            try {
                String testString = "abc";
                StringUtils.getBytes((String)testString, (String)this.getEncoding());
                return;
            }
            catch (UnsupportedEncodingException encodingEx) {
                throw SQLError.createSQLException((String)("Unsupported character encoding '" + this.getEncoding() + "'."), (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean configureClientCharacterSet(boolean dontCheckServerMatch) throws SQLException {
        String realJavaEncoding = this.getEncoding();
        boolean characterSetAlreadyConfigured = false;
        try {
            if (this.versionMeetsMinimum((int)4, (int)1, (int)0)) {
                characterSetAlreadyConfigured = true;
                this.setUseUnicode((boolean)true);
                this.configureCharsetProperties();
                realJavaEncoding = this.getEncoding();
                String connectionCollationSuffix = "";
                String connectionCollationCharset = "";
                if (!this.getUseOldUTF8Behavior() && !StringUtils.isNullOrEmpty((String)this.getConnectionCollation())) {
                    for (int i = 1; i < CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME.length; ++i) {
                        if (!CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[i].equals((Object)this.getConnectionCollation())) continue;
                        connectionCollationSuffix = " COLLATE " + CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[i];
                        connectionCollationCharset = CharsetMapping.COLLATION_INDEX_TO_CHARSET[i].charsetName;
                        realJavaEncoding = CharsetMapping.getJavaEncodingForCollationIndex((Integer)Integer.valueOf((int)i));
                    }
                }
                try {
                    String serverEncodingToSet;
                    if (this.props != null && this.props.getProperty((String)"com.mysql.jdbc.faultInjection.serverCharsetIndex") != null) {
                        this.io.serverCharsetIndex = Integer.parseInt((String)this.props.getProperty((String)"com.mysql.jdbc.faultInjection.serverCharsetIndex"));
                    }
                    if ((serverEncodingToSet = CharsetMapping.getJavaEncodingForCollationIndex((Integer)Integer.valueOf((int)this.io.serverCharsetIndex))) == null || serverEncodingToSet.length() == 0) {
                        if (realJavaEncoding == null) throw SQLError.createSQLException((String)("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property."), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                        this.setEncoding((String)realJavaEncoding);
                    }
                    if (this.versionMeetsMinimum((int)4, (int)1, (int)0) && "ISO8859_1".equalsIgnoreCase((String)serverEncodingToSet)) {
                        serverEncodingToSet = "Cp1252";
                    }
                    if ("UnicodeBig".equalsIgnoreCase((String)serverEncodingToSet) || "UTF-16".equalsIgnoreCase((String)serverEncodingToSet) || "UTF-16LE".equalsIgnoreCase((String)serverEncodingToSet) || "UTF-32".equalsIgnoreCase((String)serverEncodingToSet)) {
                        serverEncodingToSet = "UTF-8";
                    }
                    this.setEncoding((String)serverEncodingToSet);
                }
                catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
                    if (realJavaEncoding == null) throw SQLError.createSQLException((String)("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property."), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                    this.setEncoding((String)realJavaEncoding);
                }
                catch (SQLException ex) {
                    throw ex;
                }
                catch (RuntimeException ex) {
                    SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
                    sqlEx.initCause((Throwable)ex);
                    throw sqlEx;
                }
                if (this.getEncoding() == null) {
                    this.setEncoding((String)"ISO8859_1");
                }
                if (this.getUseUnicode()) {
                    String mysqlCharsetName;
                    if (realJavaEncoding != null) {
                        if (realJavaEncoding.equalsIgnoreCase((String)"UTF-8") || realJavaEncoding.equalsIgnoreCase((String)"UTF8")) {
                            String utf8CharsetName;
                            boolean utf8mb4Supported = this.versionMeetsMinimum((int)5, (int)5, (int)2);
                            String string = connectionCollationSuffix.length() > 0 ? connectionCollationCharset : (utf8CharsetName = utf8mb4Supported ? "utf8mb4" : "utf8");
                            if (!this.getUseOldUTF8Behavior()) {
                                if (dontCheckServerMatch || !this.characterSetNamesMatches((String)"utf8") || utf8mb4Supported && !this.characterSetNamesMatches((String)"utf8mb4") || connectionCollationSuffix.length() > 0 && !this.getConnectionCollation().equalsIgnoreCase((String)this.serverVariables.get((Object)"collation_server"))) {
                                    this.execSQL(null, (String)("SET NAMES " + utf8CharsetName + connectionCollationSuffix), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                                    this.serverVariables.put((String)"character_set_client", (String)utf8CharsetName);
                                    this.serverVariables.put((String)"character_set_connection", (String)utf8CharsetName);
                                }
                            } else {
                                this.execSQL(null, (String)"SET NAMES latin1", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                                this.serverVariables.put((String)"character_set_client", (String)"latin1");
                                this.serverVariables.put((String)"character_set_connection", (String)"latin1");
                            }
                            this.setEncoding((String)realJavaEncoding);
                        } else {
                            String string = mysqlCharsetName = connectionCollationSuffix.length() > 0 ? connectionCollationCharset : CharsetMapping.getMysqlCharsetForJavaEncoding((String)realJavaEncoding.toUpperCase((Locale)Locale.ENGLISH), (Connection)this);
                            if (mysqlCharsetName != null && (dontCheckServerMatch || !this.characterSetNamesMatches((String)mysqlCharsetName))) {
                                this.execSQL(null, (String)("SET NAMES " + mysqlCharsetName + connectionCollationSuffix), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                                this.serverVariables.put((String)"character_set_client", (String)mysqlCharsetName);
                                this.serverVariables.put((String)"character_set_connection", (String)mysqlCharsetName);
                            }
                            this.setEncoding((String)realJavaEncoding);
                        }
                    } else if (this.getEncoding() != null) {
                        block52 : {
                            mysqlCharsetName = connectionCollationSuffix.length() > 0 ? connectionCollationCharset : (this.getUseOldUTF8Behavior() ? "latin1" : this.getServerCharset());
                            boolean ucs2 = false;
                            if ("ucs2".equalsIgnoreCase((String)mysqlCharsetName) || "utf16".equalsIgnoreCase((String)mysqlCharsetName) || "utf16le".equalsIgnoreCase((String)mysqlCharsetName) || "utf32".equalsIgnoreCase((String)mysqlCharsetName)) {
                                mysqlCharsetName = "utf8";
                                ucs2 = true;
                                if (this.getCharacterSetResults() == null) {
                                    this.setCharacterSetResults((String)"UTF-8");
                                }
                            }
                            if (dontCheckServerMatch || !this.characterSetNamesMatches((String)mysqlCharsetName) || ucs2) {
                                try {
                                    this.execSQL(null, (String)("SET NAMES " + mysqlCharsetName + connectionCollationSuffix), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                                    this.serverVariables.put((String)"character_set_client", (String)mysqlCharsetName);
                                    this.serverVariables.put((String)"character_set_connection", (String)mysqlCharsetName);
                                }
                                catch (SQLException ex) {
                                    if (ex.getErrorCode() != 1820) throw ex;
                                    if (!this.getDisconnectOnExpiredPasswords()) break block52;
                                    throw ex;
                                }
                            }
                        }
                        realJavaEncoding = this.getEncoding();
                    }
                }
                String onServer = null;
                boolean isNullOnServer = false;
                if (this.serverVariables != null) {
                    onServer = this.serverVariables.get((Object)"character_set_results");
                    boolean bl = isNullOnServer = onServer == null || "NULL".equalsIgnoreCase((String)onServer) || onServer.length() == 0;
                }
                if (this.getCharacterSetResults() == null) {
                    if (!isNullOnServer) {
                        block53 : {
                            try {
                                this.execSQL(null, (String)"SET character_set_results = NULL", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                            }
                            catch (SQLException ex) {
                                if (ex.getErrorCode() != 1820) throw ex;
                                if (!this.getDisconnectOnExpiredPasswords()) break block53;
                                throw ex;
                            }
                        }
                        this.serverVariables.put((String)JDBC_LOCAL_CHARACTER_SET_RESULTS, null);
                    } else {
                        this.serverVariables.put((String)JDBC_LOCAL_CHARACTER_SET_RESULTS, (String)onServer);
                    }
                } else {
                    block54 : {
                        if (this.getUseOldUTF8Behavior()) {
                            try {
                                this.execSQL(null, (String)"SET NAMES latin1", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                                this.serverVariables.put((String)"character_set_client", (String)"latin1");
                                this.serverVariables.put((String)"character_set_connection", (String)"latin1");
                            }
                            catch (SQLException ex) {
                                if (ex.getErrorCode() != 1820) throw ex;
                                if (!this.getDisconnectOnExpiredPasswords()) break block54;
                                throw ex;
                            }
                        }
                    }
                    String charsetResults = this.getCharacterSetResults();
                    String mysqlEncodingName = null;
                    mysqlEncodingName = "UTF-8".equalsIgnoreCase((String)charsetResults) || "UTF8".equalsIgnoreCase((String)charsetResults) ? "utf8" : ("null".equalsIgnoreCase((String)charsetResults) ? "NULL" : CharsetMapping.getMysqlCharsetForJavaEncoding((String)charsetResults.toUpperCase((Locale)Locale.ENGLISH), (Connection)this));
                    if (mysqlEncodingName == null) {
                        throw SQLError.createSQLException((String)("Can't map " + charsetResults + " given for characterSetResults to a supported MySQL encoding."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get((Object)"character_set_results"))) {
                        block55 : {
                            StringBuilder setBuf = new StringBuilder((int)("SET character_set_results = ".length() + mysqlEncodingName.length()));
                            setBuf.append((String)"SET character_set_results = ").append((String)mysqlEncodingName);
                            try {
                                this.execSQL(null, (String)setBuf.toString(), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                            }
                            catch (SQLException ex) {
                                if (ex.getErrorCode() != 1820) throw ex;
                                if (!this.getDisconnectOnExpiredPasswords()) break block55;
                                throw ex;
                            }
                        }
                        this.serverVariables.put((String)JDBC_LOCAL_CHARACTER_SET_RESULTS, (String)mysqlEncodingName);
                        if (this.versionMeetsMinimum((int)5, (int)5, (int)0)) {
                            this.errorMessageEncoding = charsetResults;
                        }
                    } else {
                        this.serverVariables.put((String)JDBC_LOCAL_CHARACTER_SET_RESULTS, (String)onServer);
                    }
                }
            } else {
                realJavaEncoding = this.getEncoding();
            }
            Object var13_25 = null;
            this.setEncoding((String)realJavaEncoding);
        }
        catch (Throwable throwable) {
            Object var13_26 = null;
            this.setEncoding((String)realJavaEncoding);
            throw throwable;
        }
        try {
            CharsetEncoder enc = Charset.forName((String)this.getEncoding()).newEncoder();
            CharBuffer cbuf = CharBuffer.allocate((int)1);
            ByteBuffer bbuf = ByteBuffer.allocate((int)1);
            cbuf.put((String)"\u00a5");
            cbuf.position((int)0);
            enc.encode((CharBuffer)cbuf, (ByteBuffer)bbuf, (boolean)true);
            if (bbuf.get((int)0) == 92) {
                this.requiresEscapingEncoder = true;
                return characterSetAlreadyConfigured;
            }
            cbuf.clear();
            bbuf.clear();
            cbuf.put((String)"\u20a9");
            cbuf.position((int)0);
            enc.encode((CharBuffer)cbuf, (ByteBuffer)bbuf, (boolean)true);
            if (bbuf.get((int)0) != 92) return characterSetAlreadyConfigured;
            this.requiresEscapingEncoder = true;
            return characterSetAlreadyConfigured;
        }
        catch (UnsupportedCharsetException ucex) {
            try {
                byte[] bbuf = StringUtils.getBytes((String)"\u00a5", (String)this.getEncoding());
                if (bbuf[0] == 92) {
                    this.requiresEscapingEncoder = true;
                    return characterSetAlreadyConfigured;
                }
                bbuf = StringUtils.getBytes((String)"\u20a9", (String)this.getEncoding());
                if (bbuf[0] != 92) return characterSetAlreadyConfigured;
                this.requiresEscapingEncoder = true;
                return characterSetAlreadyConfigured;
            }
            catch (UnsupportedEncodingException ueex) {
                throw SQLError.createSQLException((String)("Unable to use encoding: " + this.getEncoding()), (String)"S1000", (Throwable)ueex, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    private void configureTimezone() throws SQLException {
        String configuredTimeZoneOnServer = this.serverVariables.get((Object)"timezone");
        if (configuredTimeZoneOnServer == null && "SYSTEM".equalsIgnoreCase((String)(configuredTimeZoneOnServer = this.serverVariables.get((Object)"time_zone")))) {
            configuredTimeZoneOnServer = this.serverVariables.get((Object)"system_time_zone");
        }
        String canonicalTimezone = this.getServerTimezone();
        if (!(!this.getUseTimezone() && this.getUseLegacyDatetimeCode() || configuredTimeZoneOnServer == null || canonicalTimezone != null && !StringUtils.isEmptyOrWhitespaceOnly((String)canonicalTimezone))) {
            try {
                canonicalTimezone = TimeUtil.getCanonicalTimezone((String)configuredTimeZoneOnServer, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            catch (IllegalArgumentException iae) {
                throw SQLError.createSQLException((String)iae.getMessage(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        if (canonicalTimezone == null) return;
        if (canonicalTimezone.length() <= 0) return;
        this.serverTimezoneTZ = TimeZone.getTimeZone((String)canonicalTimezone);
        if (!canonicalTimezone.equalsIgnoreCase((String)"GMT") && this.serverTimezoneTZ.getID().equals((Object)"GMT")) {
            throw SQLError.createSQLException((String)("No timezone mapping entry for '" + canonicalTimezone + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.isServerTzUTC = !this.serverTimezoneTZ.useDaylightTime() && this.serverTimezoneTZ.getRawOffset() == 0;
    }

    private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound) {
        double bucketSize = ((double)upperBound - (double)lowerBound) / 20.0 * 1.25;
        if (bucketSize < 1.0) {
            bucketSize = 1.0;
        }
        int i = 0;
        while (i < 20) {
            breakpoints[i] = lowerBound;
            lowerBound = (long)((double)lowerBound + bucketSize);
            ++i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void createNewIO(boolean isForReconnect) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        Properties mergedProps = this.exposeAsProperties((Properties)this.props);
        if (!this.getHighAvailability()) {
            this.connectOneTryOnly((boolean)isForReconnect, (Properties)mergedProps);
            // MONITOREXIT : object
            return;
        }
        this.connectWithRetries((boolean)isForReconnect, (Properties)mergedProps);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void connectWithRetries(boolean isForReconnect, Properties mergedProps) throws SQLException {
        double timeout = (double)this.getInitialTimeout();
        boolean connectionGood = false;
        Exception connectionException = null;
        for (int attemptCount = 0; attemptCount < this.getMaxReconnects() && !connectionGood; ++attemptCount) {
            try {
                if (this.io != null) {
                    this.io.forceClose();
                }
                this.coreConnect((Properties)mergedProps);
                this.pingInternal((boolean)false, (int)0);
                Object object = this.getConnectionMutex();
                // MONITORENTER : object
                this.connectionId = this.io.getThreadId();
                this.isClosed = false;
                boolean oldAutoCommit = this.getAutoCommit();
                int oldIsolationLevel = this.isolationLevel;
                boolean oldReadOnly = this.isReadOnly((boolean)false);
                String oldCatalog = this.getCatalog();
                this.io.setStatementInterceptors(this.statementInterceptors);
                // MONITOREXIT : object
                this.initializePropsFromServer();
                if (isForReconnect) {
                    this.setAutoCommit((boolean)oldAutoCommit);
                    if (this.hasIsolationLevels) {
                        this.setTransactionIsolation((int)oldIsolationLevel);
                    }
                    this.setCatalog((String)oldCatalog);
                    this.setReadOnly((boolean)oldReadOnly);
                }
                connectionGood = true;
                break;
            }
            catch (Exception EEE) {
                connectionException = EEE;
                connectionGood = false;
                if (connectionGood) break;
                if (attemptCount <= 0) continue;
                try {
                    Thread.sleep((long)((long)timeout * 1000L));
                }
                catch (InterruptedException IE) {
                    // empty catch block
                }
                continue;
            }
        }
        if (!connectionGood) {
            SQLException chainedEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.UnableToConnectWithRetries", (Object[])new Object[]{Integer.valueOf((int)this.getMaxReconnects())}), (String)"08001", (ExceptionInterceptor)this.getExceptionInterceptor());
            chainedEx.initCause((Throwable)connectionException);
            throw chainedEx;
        }
        if (this.getParanoid() && !this.getHighAvailability()) {
            this.password = null;
            this.user = null;
        }
        if (!isForReconnect) return;
        Iterator<Statement> statementIter = this.openStatements.iterator();
        Stack<Statement> serverPreparedStatements = null;
        while (statementIter.hasNext()) {
            Statement statementObj = statementIter.next();
            if (!(statementObj instanceof ServerPreparedStatement)) continue;
            if (serverPreparedStatements == null) {
                serverPreparedStatements = new Stack<Statement>();
            }
            serverPreparedStatements.add(statementObj);
        }
        if (serverPreparedStatements == null) return;
        while (!serverPreparedStatements.isEmpty()) {
            ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
        }
    }

    private void coreConnect(Properties mergedProps) throws SQLException, IOException {
        int newPort = 3306;
        String newHost = "localhost";
        String protocol = mergedProps.getProperty((String)"PROTOCOL");
        if (protocol != null) {
            if ("tcp".equalsIgnoreCase((String)protocol)) {
                newHost = this.normalizeHost((String)mergedProps.getProperty((String)"HOST"));
                newPort = this.parsePortNumber((String)mergedProps.getProperty((String)"PORT", (String)"3306"));
            } else if ("pipe".equalsIgnoreCase((String)protocol)) {
                this.setSocketFactoryClassName((String)NamedPipeSocketFactory.class.getName());
                String path = mergedProps.getProperty((String)"PATH");
                if (path != null) {
                    mergedProps.setProperty((String)"namedPipePath", (String)path);
                }
            } else {
                newHost = this.normalizeHost((String)mergedProps.getProperty((String)"HOST"));
                newPort = this.parsePortNumber((String)mergedProps.getProperty((String)"PORT", (String)"3306"));
            }
        } else {
            String[] parsedHostPortPair = NonRegisteringDriver.parseHostPortPair((String)this.hostPortPair);
            newHost = parsedHostPortPair[0];
            newHost = this.normalizeHost((String)newHost);
            if (parsedHostPortPair[1] != null) {
                newPort = this.parsePortNumber((String)parsedHostPortPair[1]);
            }
        }
        this.port = newPort;
        this.host = newHost;
        this.sessionMaxRows = -1;
        this.serverVariables = new HashMap<String, String>();
        this.serverVariables.put((String)"character_set_server", (String)"utf8");
        this.io = new MysqlIO((String)newHost, (int)newPort, (Properties)mergedProps, (String)this.getSocketFactoryClassName(), (MySQLConnection)this.getProxy(), (int)this.getSocketTimeout(), (int)this.largeRowSizeThreshold.getValueAsInt());
        this.io.doHandshake((String)this.user, (String)this.password, (String)this.database);
        if (!this.versionMeetsMinimum((int)5, (int)5, (int)0)) return;
        this.errorMessageEncoding = this.io.getEncodingForHandshake();
    }

    private String normalizeHost(String hostname) {
        if (hostname == null) return "localhost";
        if (!StringUtils.isEmptyOrWhitespaceOnly((String)hostname)) return hostname;
        return "localhost";
    }

    private int parsePortNumber(String portAsString) throws SQLException {
        int portNumber = 3306;
        try {
            return Integer.parseInt((String)portAsString);
        }
        catch (NumberFormatException nfe) {
            throw SQLError.createSQLException((String)("Illegal connection port value '" + portAsString + "'"), (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private void connectOneTryOnly(boolean isForReconnect, Properties mergedProps) throws SQLException {
        Exception connectionNotEstablishedBecause = null;
        try {
            this.coreConnect((Properties)mergedProps);
            this.connectionId = this.io.getThreadId();
            this.isClosed = false;
            boolean oldAutoCommit = this.getAutoCommit();
            int oldIsolationLevel = this.isolationLevel;
            boolean oldReadOnly = this.isReadOnly((boolean)false);
            String oldCatalog = this.getCatalog();
            this.io.setStatementInterceptors(this.statementInterceptors);
            this.initializePropsFromServer();
            if (!isForReconnect) return;
            this.setAutoCommit((boolean)oldAutoCommit);
            if (this.hasIsolationLevels) {
                this.setTransactionIsolation((int)oldIsolationLevel);
            }
            this.setCatalog((String)oldCatalog);
            this.setReadOnly((boolean)oldReadOnly);
            return;
        }
        catch (Exception EEE) {
            if (EEE instanceof SQLException && ((SQLException)EEE).getErrorCode() == 1820 && !this.getDisconnectOnExpiredPasswords()) {
                return;
            }
            if (this.io != null) {
                this.io.forceClose();
            }
            connectionNotEstablishedBecause = EEE;
            if (EEE instanceof SQLException) {
                throw (SQLException)EEE;
            }
            SQLException chainedEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.UnableToConnect"), (String)"08001", (ExceptionInterceptor)this.getExceptionInterceptor());
            chainedEx.initCause((Throwable)connectionNotEstablishedBecause);
            throw chainedEx;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createPreparedStatementCaches() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        int cacheSize = this.getPreparedStatementCacheSize();
        try {
            Class<?> factoryClass = Class.forName((String)this.getParseInfoCacheFactory());
            CacheAdapterFactory cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
            this.cachedPreparedStatementParams = cacheFactory.getInstance((Connection)this, (String)this.myURL, (int)this.getPreparedStatementCacheSize(), (int)this.getPreparedStatementCacheSqlLimit(), (Properties)this.props);
        }
        catch (ClassNotFoundException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantFindCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
        catch (InstantiationException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantLoadCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
        catch (IllegalAccessException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantLoadCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
        if (this.getUseServerPreparedStmts()) {
            this.serverSideStatementCheckCache = new LRUCache<K, V>((int)cacheSize);
            this.serverSideStatementCache = new LRUCache<CompoundCacheKey, ServerPreparedStatement>((ConnectionImpl)this, (int)cacheSize){
                private static final long serialVersionUID = 7692318650375988114L;
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    super((int)x0);
                }

                protected boolean removeEldestEntry(java.util.Map$Entry<CompoundCacheKey, ServerPreparedStatement> eldest) {
                    if (this.maxElements <= 1) {
                        return false;
                    }
                    boolean removeIt = super.removeEldestEntry(eldest);
                    if (!removeIt) return removeIt;
                    ServerPreparedStatement ps = eldest.getValue();
                    ps.isCached = false;
                    ps.setClosed((boolean)false);
                    try {
                        ps.close();
                        return removeIt;
                    }
                    catch (SQLException sqlEx) {
                        // empty catch block
                    }
                    return removeIt;
                }
            };
        }
        // MONITOREXIT : object
        return;
    }

    @Override
    public java.sql.Statement createStatement() throws SQLException {
        return this.createStatement((int)1003, (int)1007);
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        StatementImpl stmt = new StatementImpl((MySQLConnection)this.getMultiHostSafeProxy(), (String)this.database);
        stmt.setResultSetType((int)resultSetType);
        stmt.setResultSetConcurrency((int)resultSetConcurrency);
        return stmt;
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!this.getPedantic()) return this.createStatement((int)resultSetType, (int)resultSetConcurrency);
        if (resultSetHoldability == 1) return this.createStatement((int)resultSetType, (int)resultSetConcurrency);
        throw SQLError.createSQLException((String)"HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void dumpTestcaseQuery(String query) {
        System.err.println((String)query);
    }

    @Override
    public Connection duplicate() throws SQLException {
        return new ConnectionImpl((String)this.origHostToConnectTo, (int)this.origPortToConnectTo, (Properties)this.props, (String)this.origDatabaseToConnectTo, (String)this.myURL);
    }

    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException {
        return this.execSQL((StatementImpl)callingStatement, (String)sql, (int)maxRows, (Buffer)packet, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata, (boolean)false);
    }

    /*
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException {
        ResultSetInternalMethods Ex2222;
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        long queryStartTime = this.getGatherPerformanceMetrics() ? System.currentTimeMillis() : 0L;
        int endOfQueryPacketPosition = packet != null ? packet.getPosition() : 0;
        this.lastQueryFinishedTime = 0L;
        if (this.getHighAvailability() && (this.autoCommit || this.getAutoReconnectForPools()) && this.needsPing && !isBatch) {
            try {
                this.pingInternal((boolean)false, (int)0);
                this.needsPing = false;
            }
            catch (Exception Ex2222) {
                this.createNewIO((boolean)true);
            }
        }
        try {
            Ex2222 = packet == null ? this.io.sqlQueryDirect((StatementImpl)callingStatement, (String)sql, (String)(this.getUseUnicode() ? this.getEncoding() : null), null, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata) : this.io.sqlQueryDirect((StatementImpl)callingStatement, null, null, (Buffer)packet, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata);
            Object var19_19 = null;
        }
        catch (SQLException sqlE) {
            try {
                if (this.getDumpQueriesOnException()) {
                    String extractedSql = this.extractSqlFromPacket((String)sql, (Buffer)packet, (int)endOfQueryPacketPosition);
                    StringBuilder messageBuf = new StringBuilder((int)(extractedSql.length() + 32));
                    messageBuf.append((String)"\n\nQuery being executed when exception was thrown:\n");
                    messageBuf.append((String)extractedSql);
                    messageBuf.append((String)"\n\n");
                    sqlE = ConnectionImpl.appendMessageToException((SQLException)sqlE, (String)messageBuf.toString(), (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                if (!this.getHighAvailability()) {
                    if (!"08S01".equals((Object)sqlE.getSQLState())) throw sqlE;
                    this.cleanup((Throwable)sqlE);
                    throw sqlE;
                }
                if ("08S01".equals((Object)sqlE.getSQLState())) {
                    this.io.forceClose();
                }
                this.needsPing = true;
                throw sqlE;
                catch (Exception ex) {
                    if (this.getHighAvailability()) {
                        if (ex instanceof IOException) {
                            this.io.forceClose();
                        }
                        this.needsPing = true;
                    } else if (ex instanceof IOException) {
                        this.cleanup((Throwable)ex);
                    }
                    SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.UnexpectedException"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                    sqlEx.initCause((Throwable)ex);
                    throw sqlEx;
                }
            }
            catch (Throwable throwable) {
                Object var19_20 = null;
                if (this.getMaintainTimeStats()) {
                    this.lastQueryFinishedTime = System.currentTimeMillis();
                }
                if (!this.getGatherPerformanceMetrics()) throw throwable;
                this.registerQueryExecutionTime((long)(System.currentTimeMillis() - queryStartTime));
                throw throwable;
            }
        }
        if (this.getMaintainTimeStats()) {
            this.lastQueryFinishedTime = System.currentTimeMillis();
        }
        if (!this.getGatherPerformanceMetrics()) return Ex2222;
        this.registerQueryExecutionTime((long)(System.currentTimeMillis() - queryStartTime));
        return Ex2222;
    }

    @Override
    public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException {
        String extractedSql = null;
        if (possibleSqlQuery != null) {
            if (possibleSqlQuery.length() > this.getMaxQuerySizeToLog()) {
                StringBuilder truncatedQueryBuf = new StringBuilder((String)possibleSqlQuery.substring((int)0, (int)this.getMaxQuerySizeToLog()));
                truncatedQueryBuf.append((String)Messages.getString((String)"MysqlIO.25"));
                extractedSql = truncatedQueryBuf.toString();
            } else {
                extractedSql = possibleSqlQuery;
            }
        }
        if (extractedSql != null) return extractedSql;
        int extractPosition = endOfQueryPacketPosition;
        boolean truncated = false;
        if (endOfQueryPacketPosition > this.getMaxQuerySizeToLog()) {
            extractPosition = this.getMaxQuerySizeToLog();
            truncated = true;
        }
        extractedSql = StringUtils.toString((byte[])queryPacket.getByteBuffer(), (int)5, (int)(extractPosition - 5));
        if (!truncated) return extractedSql;
        return extractedSql + Messages.getString((String)"MysqlIO.25");
    }

    @Override
    public StringBuilder generateConnectionCommentBlock(StringBuilder buf) {
        buf.append((String)"/* conn id ");
        buf.append((long)this.getId());
        buf.append((String)" clock: ");
        buf.append((long)System.currentTimeMillis());
        buf.append((String)" */ ");
        return buf;
    }

    @Override
    public int getActiveStatementCount() {
        return this.openStatements.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.autoCommit;
    }

    @Override
    public Calendar getCalendarInstanceForSessionOrNew() {
        if (!this.getDynamicCalendars()) return this.getSessionLockedCalendar();
        return Calendar.getInstance();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getCatalog() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.database;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getCharacterSetMetadata() {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.characterSetMetadata;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
        if (javaEncodingName == null) {
            return null;
        }
        if (this.usePlatformCharsetConverters) {
            return null;
        }
        SingleByteCharsetConverter converter = null;
        Map<String, Object> map = this.charsetConverterMap;
        // MONITORENTER : map
        Object asObject = this.charsetConverterMap.get((Object)javaEncodingName);
        if (asObject == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
            // MONITOREXIT : map
            return null;
        }
        converter = (SingleByteCharsetConverter)asObject;
        if (converter == null) {
            try {
                converter = SingleByteCharsetConverter.getInstance((String)javaEncodingName, (Connection)this);
                if (converter == null) {
                    this.charsetConverterMap.put((String)javaEncodingName, (Object)CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
                    return converter;
                }
                this.charsetConverterMap.put((String)javaEncodingName, (Object)converter);
                return converter;
            }
            catch (UnsupportedEncodingException unsupEncEx) {
                this.charsetConverterMap.put((String)javaEncodingName, (Object)CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
                converter = null;
            }
        }
        // MONITOREXIT : map
        return converter;
    }

    @Deprecated
    @Override
    public String getCharsetNameForIndex(int charsetIndex) throws SQLException {
        return this.getEncodingForIndex((int)charsetIndex);
    }

    @Override
    public String getEncodingForIndex(int charsetIndex) throws SQLException {
        String javaEncoding = null;
        if (this.getUseOldUTF8Behavior()) {
            return this.getEncoding();
        }
        if (charsetIndex == -1) {
            return this.getEncoding();
        }
        try {
            String cs;
            if (this.indexToCustomMysqlCharset != null && (cs = this.indexToCustomMysqlCharset.get((Object)Integer.valueOf((int)charsetIndex))) != null) {
                javaEncoding = CharsetMapping.getJavaEncodingForMysqlCharset((String)cs, (String)this.getEncoding());
            }
            if (javaEncoding == null) {
                javaEncoding = CharsetMapping.getJavaEncodingForCollationIndex((Integer)Integer.valueOf((int)charsetIndex), (String)this.getEncoding());
            }
        }
        catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
            throw SQLError.createSQLException((String)("Unknown character set index for field '" + charsetIndex + "' received from server."), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        if (javaEncoding != null) return javaEncoding;
        return this.getEncoding();
    }

    @Override
    public TimeZone getDefaultTimeZone() {
        TimeZone timeZone;
        if (this.getCacheDefaultTimezone()) {
            timeZone = this.defaultTimeZone;
            return timeZone;
        }
        timeZone = TimeUtil.getDefaultTimeZone((boolean)false);
        return timeZone;
    }

    @Override
    public String getErrorMessageEncoding() {
        return this.errorMessageEncoding;
    }

    @Override
    public int getHoldability() throws SQLException {
        return 2;
    }

    @Override
    public long getId() {
        return this.connectionId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getIdleFor() {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.lastQueryFinishedTime == 0L) {
            return 0L;
        }
        long l = System.currentTimeMillis() - this.lastQueryFinishedTime;
        // MONITOREXIT : object
        return l;
    }

    @Override
    public MysqlIO getIO() throws SQLException {
        if (this.io == null) throw SQLError.createSQLException((String)"Operation not allowed on closed connection", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (!this.isClosed) return this.io;
        throw SQLError.createSQLException((String)"Operation not allowed on closed connection", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public Log getLog() throws SQLException {
        return this.log;
    }

    @Override
    public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
        return this.getMaxBytesPerChar(null, (String)javaCharsetName);
    }

    @Override
    public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException {
        String charset = null;
        int res = 1;
        try {
            if (this.indexToCustomMysqlCharset != null) {
                charset = this.indexToCustomMysqlCharset.get((Object)charsetIndex);
            }
            if (charset == null) {
                charset = CharsetMapping.getMysqlCharsetNameForCollationIndex((Integer)charsetIndex);
            }
            if (charset == null) {
                charset = CharsetMapping.getMysqlCharsetForJavaEncoding((String)javaCharsetName, (Connection)this);
            }
            Integer mblen = null;
            if (this.mysqlCharsetToCustomMblen != null) {
                mblen = this.mysqlCharsetToCustomMblen.get((Object)charset);
            }
            if (mblen == null) {
                mblen = Integer.valueOf((int)CharsetMapping.getMblen((String)charset));
            }
            if (mblen == null) return res;
            return mblen.intValue();
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    @Override
    public java.sql.DatabaseMetaData getMetaData() throws SQLException {
        return this.getMetaData((boolean)true, (boolean)true);
    }

    private java.sql.DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
        if (!checkClosed) return DatabaseMetaData.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)this.database, (boolean)checkForInfoSchema);
        this.checkClosed();
        return DatabaseMetaData.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)this.database, (boolean)checkForInfoSchema);
    }

    @Override
    public java.sql.Statement getMetadataSafeStatement() throws SQLException {
        return this.getMetadataSafeStatement((int)0);
    }

    public java.sql.Statement getMetadataSafeStatement(int maxRows) throws SQLException {
        java.sql.Statement stmt = this.createStatement();
        stmt.setMaxRows((int)(maxRows == -1 ? 0 : maxRows));
        stmt.setEscapeProcessing((boolean)false);
        if (stmt.getFetchSize() == 0) return stmt;
        stmt.setFetchSize((int)0);
        return stmt;
    }

    @Override
    public int getNetBufferLength() {
        return this.netBufferLength;
    }

    @Deprecated
    @Override
    public String getServerCharacterEncoding() {
        return this.getServerCharset();
    }

    @Override
    public String getServerCharset() {
        String string;
        if (!this.io.versionMeetsMinimum((int)4, (int)1, (int)0)) return this.serverVariables.get((Object)"character_set");
        String charset = null;
        if (this.indexToCustomMysqlCharset != null) {
            charset = this.indexToCustomMysqlCharset.get((Object)Integer.valueOf((int)this.io.serverCharsetIndex));
        }
        if (charset == null) {
            charset = CharsetMapping.getMysqlCharsetNameForCollationIndex((Integer)Integer.valueOf((int)this.io.serverCharsetIndex));
        }
        if (charset != null) {
            string = charset;
            return string;
        }
        string = this.serverVariables.get((Object)"character_set_server");
        return string;
    }

    @Override
    public int getServerMajorVersion() {
        return this.io.getServerMajorVersion();
    }

    @Override
    public int getServerMinorVersion() {
        return this.io.getServerMinorVersion();
    }

    @Override
    public int getServerSubMinorVersion() {
        return this.io.getServerSubMinorVersion();
    }

    @Override
    public TimeZone getServerTimezoneTZ() {
        return this.serverTimezoneTZ;
    }

    @Override
    public String getServerVariable(String variableName) {
        if (this.serverVariables == null) return null;
        return this.serverVariables.get((Object)variableName);
    }

    @Override
    public String getServerVersion() {
        return this.io.getServerVersion();
    }

    @Override
    public Calendar getSessionLockedCalendar() {
        return this.sessionCalendar;
    }

    /*
     * Exception decompiling
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[TRYBLOCK]], but top level block is 9[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.typeMap == null) {
            this.typeMap = new HashMap<String, Class<?>>();
        }
        // MONITOREXIT : object
        return this.typeMap;
    }

    @Override
    public String getURL() {
        return this.myURL;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public Calendar getUtcCalendar() {
        return this.utcCalendar;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public boolean hasSameProperties(Connection c) {
        return this.props.equals((Object)c.getProperties());
    }

    @Override
    public Properties getProperties() {
        return this.props;
    }

    @Deprecated
    @Override
    public boolean hasTriedMaster() {
        return this.hasTriedMasterFlag;
    }

    @Override
    public void incrementNumberOfPreparedExecutes() {
        if (!this.getGatherPerformanceMetrics()) return;
        ++this.numberOfPreparedExecutes;
        ++this.numberOfQueriesIssued;
    }

    @Override
    public void incrementNumberOfPrepares() {
        if (!this.getGatherPerformanceMetrics()) return;
        ++this.numberOfPrepares;
    }

    @Override
    public void incrementNumberOfResultSetsCreated() {
        if (!this.getGatherPerformanceMetrics()) return;
        ++this.numberOfResultSetsCreated;
    }

    private void initializeDriverProperties(Properties info) throws SQLException {
        this.initializeProperties((Properties)info);
        String exceptionInterceptorClasses = this.getExceptionInterceptors();
        if (exceptionInterceptorClasses != null && !"".equals((Object)exceptionInterceptorClasses)) {
            this.exceptionInterceptor = new ExceptionInterceptorChain((ConnectionImpl)this, (String)exceptionInterceptorClasses);
        }
        this.usePlatformCharsetConverters = this.getUseJvmCharsetConverters();
        this.log = LogFactory.getLogger((String)this.getLogger(), (String)LOGGER_INSTANCE_NAME, (ExceptionInterceptor)this.getExceptionInterceptor());
        if (this.getProfileSql() || this.getLogSlowQueries() || this.getUseUsageAdvisor()) {
            this.eventSink = ProfilerEventHandlerFactory.getInstance((MySQLConnection)this.getMultiHostSafeProxy());
        }
        if (this.getCachePreparedStatements()) {
            this.createPreparedStatementCaches();
        }
        if (this.getNoDatetimeStringSync() && this.getUseTimezone()) {
            throw SQLError.createSQLException((String)"Can't enable noDatetimeStringSync and useTimezone configuration properties at the same time", (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.getCacheCallableStatements()) {
            this.parsedCallableStatementCache = new LRUCache<K, V>((int)this.getCallableStatementCacheSize());
        }
        if (this.getAllowMultiQueries()) {
            this.setCacheResultSetMetadata((boolean)false);
        }
        if (this.getCacheResultSetMetadata()) {
            this.resultSetMetadataCache = new LRUCache<K, V>((int)this.getMetadataCacheSize());
        }
        if (this.getSocksProxyHost() == null) return;
        this.setSocketFactoryClassName((String)"com.mysql.jdbc.SocksProxySocketFactory");
    }

    private void initializePropsFromServer() throws SQLException {
        String connectionInterceptorClasses = this.getConnectionLifecycleInterceptors();
        this.connectionLifecycleInterceptors = null;
        if (connectionInterceptorClasses != null) {
            this.connectionLifecycleInterceptors = Util.loadExtensions((Connection)this, (Properties)this.props, (String)connectionInterceptorClasses, (String)"Connection.badLifecycleInterceptor", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.setSessionVariables();
        if (!this.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            this.setTransformedBitIsBoolean((boolean)false);
        }
        this.parserKnowsUnicode = this.versionMeetsMinimum((int)4, (int)1, (int)0);
        if (this.getUseServerPreparedStmts() && this.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            this.useServerPreparedStmts = true;
            if (this.versionMeetsMinimum((int)5, (int)0, (int)0) && !this.versionMeetsMinimum((int)5, (int)0, (int)3)) {
                this.useServerPreparedStmts = false;
            }
        }
        if (this.versionMeetsMinimum((int)3, (int)21, (int)22)) {
            this.loadServerVariables();
            this.autoIncrementIncrement = this.versionMeetsMinimum((int)5, (int)0, (int)2) ? this.getServerVariableAsInt((String)"auto_increment_increment", (int)1) : 1;
            this.buildCollationMapping();
            if (this.io.serverCharsetIndex == 0) {
                String collationServer = this.serverVariables.get((Object)"collation_server");
                if (collationServer == null) {
                    this.io.serverCharsetIndex = 45;
                } else {
                    for (int i = 1; i < CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME.length; ++i) {
                        if (!CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[i].equals((Object)collationServer)) continue;
                        this.io.serverCharsetIndex = i;
                        break;
                    }
                }
            }
            LicenseConfiguration.checkLicenseType(this.serverVariables);
            String lowerCaseTables = this.serverVariables.get((Object)"lower_case_table_names");
            this.lowerCaseTableNames = "on".equalsIgnoreCase((String)lowerCaseTables) || "1".equalsIgnoreCase((String)lowerCaseTables) || "2".equalsIgnoreCase((String)lowerCaseTables);
            this.storesLowerCaseTableName = "1".equalsIgnoreCase((String)lowerCaseTables) || "on".equalsIgnoreCase((String)lowerCaseTables);
            this.configureTimezone();
            if (this.serverVariables.containsKey((Object)"max_allowed_packet")) {
                int serverMaxAllowedPacket = this.getServerVariableAsInt((String)"max_allowed_packet", (int)-1);
                if (serverMaxAllowedPacket != -1 && (serverMaxAllowedPacket < this.getMaxAllowedPacket() || this.getMaxAllowedPacket() <= 0)) {
                    this.setMaxAllowedPacket((int)serverMaxAllowedPacket);
                } else if (serverMaxAllowedPacket == -1 && this.getMaxAllowedPacket() == -1) {
                    this.setMaxAllowedPacket((int)65535);
                }
                if (this.getUseServerPrepStmts()) {
                    int preferredBlobSendChunkSize = this.getBlobSendChunkSize();
                    int packetHeaderSize = 8203;
                    int allowedBlobSendChunkSize = Math.min((int)preferredBlobSendChunkSize, (int)this.getMaxAllowedPacket()) - packetHeaderSize;
                    if (allowedBlobSendChunkSize <= 0) {
                        throw SQLError.createSQLException((String)("Connection setting too low for 'maxAllowedPacket'. When 'useServerPrepStmts=true', 'maxAllowedPacket' must be higher than " + packetHeaderSize + ". Check also 'max_allowed_packet' in MySQL configuration files."), (String)"01S00", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    this.setBlobSendChunkSize((String)String.valueOf((int)allowedBlobSendChunkSize));
                }
            }
            if (this.serverVariables.containsKey((Object)"net_buffer_length")) {
                this.netBufferLength = this.getServerVariableAsInt((String)"net_buffer_length", (int)16384);
            }
            this.checkTransactionIsolationLevel();
            if (!this.versionMeetsMinimum((int)4, (int)1, (int)0)) {
                this.checkServerEncoding();
            }
            this.io.checkForCharsetMismatch();
            if (this.serverVariables.containsKey((Object)"sql_mode")) {
                String sqlModeAsString = this.serverVariables.get((Object)"sql_mode");
                if (StringUtils.isStrictlyNumeric((CharSequence)sqlModeAsString)) {
                    this.useAnsiQuotes = (Integer.parseInt((String)sqlModeAsString) & 4) > 0;
                } else if (sqlModeAsString != null) {
                    this.useAnsiQuotes = sqlModeAsString.indexOf((String)"ANSI_QUOTES") != -1;
                    this.noBackslashEscapes = sqlModeAsString.indexOf((String)"NO_BACKSLASH_ESCAPES") != -1;
                    this.serverTruncatesFracSecs = sqlModeAsString.indexOf((String)"TIME_TRUNCATE_FRACTIONAL") != -1;
                }
            }
        }
        this.configureClientCharacterSet((boolean)false);
        try {
            this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages((ConnectionImpl)this);
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        if (this.versionMeetsMinimum((int)3, (int)23, (int)15)) {
            this.transactionsSupported = true;
            this.handleAutoCommitDefaults();
        } else {
            this.transactionsSupported = false;
        }
        this.hasIsolationLevels = this.versionMeetsMinimum((int)3, (int)23, (int)36);
        this.hasQuotedIdentifiers = this.versionMeetsMinimum((int)3, (int)23, (int)6);
        this.io.resetMaxBuf();
        if (this.io.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            String characterSetResultsOnServerMysql = this.serverVariables.get((Object)JDBC_LOCAL_CHARACTER_SET_RESULTS);
            if (characterSetResultsOnServerMysql == null || StringUtils.startsWithIgnoreCaseAndWs((String)characterSetResultsOnServerMysql, (String)"NULL") || characterSetResultsOnServerMysql.length() == 0) {
                String defaultMetadataCharsetMysql = this.serverVariables.get((Object)"character_set_system");
                String defaultMetadataCharset = null;
                defaultMetadataCharset = defaultMetadataCharsetMysql != null ? CharsetMapping.getJavaEncodingForMysqlCharset((String)defaultMetadataCharsetMysql) : "UTF-8";
                this.characterSetMetadata = defaultMetadataCharset;
            } else {
                this.characterSetMetadata = this.characterSetResultsOnServer = CharsetMapping.getJavaEncodingForMysqlCharset((String)characterSetResultsOnServerMysql);
            }
        } else {
            this.characterSetMetadata = this.getEncoding();
        }
        if (this.versionMeetsMinimum((int)4, (int)1, (int)0) && !this.versionMeetsMinimum((int)4, (int)1, (int)10) && this.getAllowMultiQueries() && this.isQueryCacheEnabled()) {
            this.setAllowMultiQueries((boolean)false);
        }
        if (this.versionMeetsMinimum((int)5, (int)0, (int)0) && (this.getUseLocalTransactionState() || this.getElideSetAutoCommits()) && this.isQueryCacheEnabled() && !this.versionMeetsMinimum((int)5, (int)1, (int)32)) {
            this.setUseLocalTransactionState((boolean)false);
            this.setElideSetAutoCommits((boolean)false);
        }
        this.setupServerForTruncationChecks();
    }

    public boolean isQueryCacheEnabled() {
        if (!"ON".equalsIgnoreCase((String)this.serverVariables.get((Object)"query_cache_type"))) return false;
        if ("0".equalsIgnoreCase((String)this.serverVariables.get((Object)"query_cache_size"))) return false;
        return true;
    }

    private int getServerVariableAsInt(String variableName, int fallbackValue) throws SQLException {
        try {
            return Integer.parseInt((String)this.serverVariables.get((Object)variableName));
        }
        catch (NumberFormatException nfe) {
            this.getLog().logWarn((Object)Messages.getString((String)"Connection.BadValueInServerVariables", (Object[])new Object[]{variableName, this.serverVariables.get((Object)variableName), Integer.valueOf((int)fallbackValue)}));
            return fallbackValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void handleAutoCommitDefaults() throws SQLException {
        block15 : {
            resetAutoCommitDefault = false;
            if (this.getElideSetAutoCommits()) ** GOTO lbl21
            initConnectValue = this.serverVariables.get((Object)"init_connect");
            if (!this.versionMeetsMinimum((int)4, (int)1, (int)2) || initConnectValue == null || initConnectValue.length() <= 0) ** GOTO lbl19
            rs = null;
            stmt = null;
            try {
                block14 : {
                    stmt = this.getMetadataSafeStatement();
                    rs = stmt.executeQuery((String)"SELECT @@session.autocommit");
                    if (rs.next()) {
                        this.autoCommit = rs.getBoolean((int)1);
                        resetAutoCommitDefault = this.autoCommit == false;
                    }
                    var6_6 = null;
                    if (rs == null) break block14;
                    try {
                        rs.close();
                        break block14;
                    }
                    catch (SQLException sqlEx) {
                        // empty catch block
                    }
lbl19: // 1 sources:
                    resetAutoCommitDefault = true;
                    break block15;
lbl21: // 1 sources:
                    if (this.getIO().isSetNeededForAutoCommitMode((boolean)true)) {
                        this.autoCommit = false;
                        resetAutoCommitDefault = true;
                    }
                    break block15;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {}
                }
            }
            catch (Throwable var5_10) {
                block16 : {
                    var6_7 = null;
                    if (rs != null) {
                        ** try [egrp 1[TRYBLOCK] [2 : 121->130)] { 
lbl39: // 1 sources:
                        rs.close();
                        break block16;
lbl41: // 1 sources:
                        catch (SQLException sqlEx) {
                            // empty catch block
                        }
                    }
                }
                if (stmt == null) throw var5_10;
                ** try [egrp 2[TRYBLOCK] [3 : 137->147)] { 
lbl46: // 1 sources:
                stmt.close();
                throw var5_10;
lbl48: // 1 sources:
                catch (SQLException sqlEx) {
                    // empty catch block
                }
                throw var5_10;
            }
        }
        if (resetAutoCommitDefault == false) return;
        try {
            this.setAutoCommit((boolean)true);
            return;
        }
        catch (SQLException ex) {
            if (ex.getErrorCode() != 1820) throw ex;
            if (this.getDisconnectOnExpiredPasswords() == false) return;
            throw ex;
        }
    }

    @Override
    public boolean isClientTzUTC() {
        return this.isClientTzUTC;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public boolean isCursorFetchEnabled() throws SQLException {
        if (!this.versionMeetsMinimum((int)5, (int)0, (int)2)) return false;
        if (!this.getUseCursorFetch()) return false;
        return true;
    }

    @Override
    public boolean isInGlobalTx() {
        return this.isInGlobalTx;
    }

    @Override
    public boolean isMasterConnection() {
        return false;
    }

    @Override
    public boolean isNoBackslashEscapesSet() {
        return this.noBackslashEscapes;
    }

    @Override
    public boolean isReadInfoMsgEnabled() {
        return this.readInfoMsg;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.isReadOnly((boolean)true);
    }

    /*
     * Exception decompiling
     */
    @Override
    public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:404)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:482)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public boolean isRunningOnJDK13() {
        return this.isRunningOnJDK13;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSameResource(Connection otherConnection) {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (otherConnection == null) {
            // MONITOREXIT : object
            return false;
        }
        boolean directCompare = true;
        String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
        String otherOrigDatabase = ((ConnectionImpl)otherConnection).origDatabaseToConnectTo;
        String otherCurrentCatalog = ((ConnectionImpl)otherConnection).database;
        if (!ConnectionImpl.nullSafeCompare((String)otherHost, (String)this.origHostToConnectTo)) {
            directCompare = false;
        } else if (otherHost != null && otherHost.indexOf((int)44) == -1 && otherHost.indexOf((int)58) == -1) {
            boolean bl = directCompare = ((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo;
        }
        if (!(!directCompare || ConnectionImpl.nullSafeCompare((String)otherOrigDatabase, (String)this.origDatabaseToConnectTo) && ConnectionImpl.nullSafeCompare((String)otherCurrentCatalog, (String)this.database))) {
            directCompare = false;
        }
        if (directCompare) {
            // MONITOREXIT : object
            return true;
        }
        String otherResourceId = ((ConnectionImpl)otherConnection).getResourceId();
        String myResourceId = this.getResourceId();
        if ((otherResourceId != null || myResourceId != null) && (directCompare = ConnectionImpl.nullSafeCompare((String)otherResourceId, (String)myResourceId))) {
            // MONITOREXIT : object
            return true;
        }
        // MONITOREXIT : object
        return false;
    }

    @Override
    public boolean isServerTzUTC() {
        return this.isServerTzUTC;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createConfigCacheIfNeeded() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.serverConfigCache != null) {
            // MONITOREXIT : object
            return;
        }
        try {
            Class<?> factoryClass = Class.forName((String)this.getServerConfigCacheFactory());
            CacheAdapterFactory cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
            this.serverConfigCache = cacheFactory.getInstance((Connection)this, (String)this.myURL, (int)Integer.MAX_VALUE, (int)Integer.MAX_VALUE, (Properties)this.props);
            ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor((ConnectionImpl)this){
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                }

                public void init(Connection conn, Properties config) throws SQLException {
                }

                public void destroy() {
                }

                public SQLException interceptException(SQLException sqlEx, Connection conn) {
                    if (sqlEx.getSQLState() == null) return null;
                    if (!sqlEx.getSQLState().startsWith((String)"08")) return null;
                    ConnectionImpl.access$000((ConnectionImpl)this.this$0).invalidate(this.this$0.getURL());
                    return null;
                }
            };
            if (this.exceptionInterceptor == null) {
                this.exceptionInterceptor = evictOnCommsError;
                return;
            }
            ((ExceptionInterceptorChain)this.exceptionInterceptor).addRingZero((ExceptionInterceptor)evictOnCommsError);
            return;
        }
        catch (ClassNotFoundException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantFindCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
        catch (InstantiationException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantLoadCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
        catch (IllegalAccessException e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.CantLoadCacheFactory", (Object[])new Object[]{this.getParseInfoCacheFactory(), "parseInfoCacheFactory"}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void loadServerVariables() throws SQLException {
        if (this.getCacheServerConfiguration()) {
            this.createConfigCacheIfNeeded();
            cachedVariableMap = this.serverConfigCache.get((String)this.getURL());
            if (cachedVariableMap != null) {
                cachedServerVersion = cachedVariableMap.get((Object)"server_version_string");
                if (cachedServerVersion != null && this.io.getServerVersion() != null && cachedServerVersion.equals((Object)this.io.getServerVersion())) {
                    this.serverVariables = cachedVariableMap;
                    return;
                }
                this.serverConfigCache.invalidate((String)this.getURL());
            }
        }
        stmt = null;
        results = null;
        try {
            block34 : {
                block33 : {
                    stmt = this.getMetadataSafeStatement();
                    version = this.dbmd.getDriverVersion();
                    if (version != null && version.indexOf((int)42) != -1) {
                        buf = new StringBuilder((int)(version.length() + 10));
                        for (i = 0; i < version.length(); ++i) {
                            c = version.charAt((int)i);
                            if (c == '*') {
                                buf.append((String)"[star]");
                                continue;
                            }
                            buf.append((char)c);
                        }
                        version = buf.toString();
                    }
                    versionComment = this.getParanoid() != false || version == null ? "" : "/* " + version + " */";
                    this.serverVariables = new HashMap<String, String>();
                    currentJdbcComplTrunc = this.getJdbcCompliantTruncation();
                    this.setJdbcCompliantTruncation((boolean)false);
                    try {
                        try {
                            if (this.versionMeetsMinimum((int)5, (int)1, (int)0)) {
                                queryBuf = new StringBuilder((String)versionComment).append((String)"SELECT");
                                queryBuf.append((String)"  @@session.auto_increment_increment AS auto_increment_increment");
                                queryBuf.append((String)", @@character_set_client AS character_set_client");
                                queryBuf.append((String)", @@character_set_connection AS character_set_connection");
                                queryBuf.append((String)", @@character_set_results AS character_set_results");
                                queryBuf.append((String)", @@character_set_server AS character_set_server");
                                queryBuf.append((String)", @@collation_server AS collation_server");
                                queryBuf.append((String)", @@collation_connection AS collation_connection");
                                queryBuf.append((String)", @@init_connect AS init_connect");
                                queryBuf.append((String)", @@interactive_timeout AS interactive_timeout");
                                if (!this.versionMeetsMinimum((int)5, (int)5, (int)0)) {
                                    queryBuf.append((String)", @@language AS language");
                                }
                                queryBuf.append((String)", @@license AS license");
                                queryBuf.append((String)", @@lower_case_table_names AS lower_case_table_names");
                                queryBuf.append((String)", @@max_allowed_packet AS max_allowed_packet");
                                queryBuf.append((String)", @@net_buffer_length AS net_buffer_length");
                                queryBuf.append((String)", @@net_write_timeout AS net_write_timeout");
                                if (this.versionMeetsMinimum((int)5, (int)5, (int)0)) {
                                    queryBuf.append((String)", @@performance_schema AS performance_schema");
                                }
                                if (!this.versionMeetsMinimum((int)8, (int)0, (int)3)) {
                                    queryBuf.append((String)", @@query_cache_size AS query_cache_size");
                                    queryBuf.append((String)", @@query_cache_type AS query_cache_type");
                                }
                                queryBuf.append((String)", @@sql_mode AS sql_mode");
                                queryBuf.append((String)", @@system_time_zone AS system_time_zone");
                                queryBuf.append((String)", @@time_zone AS time_zone");
                                if (this.versionMeetsMinimum((int)8, (int)0, (int)3) || this.versionMeetsMinimum((int)5, (int)7, (int)20) && !this.versionMeetsMinimum((int)8, (int)0, (int)0)) {
                                    queryBuf.append((String)", @@transaction_isolation AS transaction_isolation");
                                } else {
                                    queryBuf.append((String)", @@tx_isolation AS transaction_isolation");
                                }
                                queryBuf.append((String)", @@wait_timeout AS wait_timeout");
                                results = stmt.executeQuery((String)queryBuf.toString());
                                if (results.next()) {
                                    rsmd = results.getMetaData();
                                    for (i = 1; i <= rsmd.getColumnCount(); ++i) {
                                        this.serverVariables.put((String)rsmd.getColumnLabel((int)i), (String)results.getString((int)i));
                                    }
                                }
                            } else {
                                results = stmt.executeQuery((String)(versionComment + "SHOW VARIABLES"));
                                while (results.next()) {
                                    this.serverVariables.put((String)results.getString((int)1), (String)results.getString((int)2));
                                }
                            }
                            results.close();
                            results = null;
                        }
                        catch (SQLException ex) {
                            if (ex.getErrorCode() != 1820) throw ex;
                            if (this.getDisconnectOnExpiredPasswords()) {
                                throw ex;
                            }
                            var10_13 = null;
                            this.setJdbcCompliantTruncation((boolean)currentJdbcComplTrunc);
                        }
                        var10_12 = null;
                        this.setJdbcCompliantTruncation((boolean)currentJdbcComplTrunc);
                    }
                    catch (Throwable var9_15) {
                        var10_14 = null;
                        this.setJdbcCompliantTruncation((boolean)currentJdbcComplTrunc);
                        throw var9_15;
                    }
                    if (!this.getCacheServerConfiguration()) break block33;
                    this.serverVariables.put((String)"server_version_string", (String)this.io.getServerVersion());
                    this.serverConfigCache.put((String)this.getURL(), this.serverVariables);
                }
                var12_16 = null;
                if (results == null) break block34;
                results.close();
                break block34;
                catch (SQLException sqlE) {
                    // empty catch block
                }
            }
            if (stmt == null) return;
            stmt.close();
            return;
            catch (SQLException sqlE) {
                return;
            }
        }
        catch (Throwable var11_20) {
            block35 : {
                var12_17 = null;
                if (results != null) {
                    ** try [egrp 4[TRYBLOCK] [7 : 848->857)] { 
lbl149: // 1 sources:
                    results.close();
                    break block35;
lbl151: // 1 sources:
                    catch (SQLException sqlE) {
                        // empty catch block
                    }
                }
            }
            if (stmt == null) throw var11_20;
            ** try [egrp 5[TRYBLOCK] [8 : 863->872)] { 
lbl156: // 1 sources:
            stmt.close();
            throw var11_20;
lbl158: // 1 sources:
            catch (SQLException sqlE) {
                // empty catch block
            }
            throw var11_20;
        }
    }

    @Override
    public int getAutoIncrementIncrement() {
        return this.autoIncrementIncrement;
    }

    @Override
    public boolean lowerCaseTableNames() {
        return this.lowerCaseTableNames;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        if (sql == null) {
            return null;
        }
        Object escapedSqlResult = EscapeProcessor.escapeSQL((String)sql, (boolean)this.serverSupportsConvertFn(), (MySQLConnection)this.getMultiHostSafeProxy());
        if (!(escapedSqlResult instanceof String)) return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
        return (String)escapedSqlResult;
    }

    private CallableStatement parseCallableStatement(String sql) throws SQLException {
        Object escapedSqlResult = EscapeProcessor.escapeSQL((String)sql, (boolean)this.serverSupportsConvertFn(), (MySQLConnection)this.getMultiHostSafeProxy());
        boolean isFunctionCall = false;
        String parsedSql = null;
        if (escapedSqlResult instanceof EscapeProcessorResult) {
            parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
            isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
            return CallableStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)parsedSql, (String)this.database, (boolean)isFunctionCall);
        }
        parsedSql = (String)escapedSqlResult;
        isFunctionCall = false;
        return CallableStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)parsedSql, (String)this.database, (boolean)isFunctionCall);
    }

    @Override
    public boolean parserKnowsUnicode() {
        return this.parserKnowsUnicode;
    }

    @Override
    public void ping() throws SQLException {
        this.pingInternal((boolean)true, (int)0);
    }

    @Override
    public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
        if (checkForClosedConnection) {
            this.checkClosed();
        }
        long pingMillisLifetime = (long)this.getSelfDestructOnPingSecondsLifetime();
        int pingMaxOperations = this.getSelfDestructOnPingMaxOperations();
        if (pingMillisLifetime > 0L && System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime || pingMaxOperations > 0 && pingMaxOperations <= this.io.getCommandCount()) {
            this.close();
            throw SQLError.createSQLException((String)Messages.getString((String)"Connection.exceededConnectionLifetime"), (String)"08S01", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.io.sendCommand((int)14, null, null, (boolean)false, null, (int)timeoutMillis);
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
        return this.prepareCall((String)sql, (int)1003, (int)1007);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (!this.versionMeetsMinimum((int)5, (int)0, (int)0)) throw SQLError.createSQLException((String)"Callable statements not supported.", (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
        CallableStatement cStmt = null;
        if (!this.getCacheCallableStatements()) {
            cStmt = this.parseCallableStatement((String)sql);
        } else {
            LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> lRUCache = this.parsedCallableStatementCache;
            // MONITORENTER : lRUCache
            CompoundCacheKey key = new CompoundCacheKey((String)this.getCatalog(), (String)sql);
            CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get((Object)key);
            if (cachedParamInfo != null) {
                cStmt = CallableStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (CallableStatement.CallableStatementParamInfo)cachedParamInfo);
            } else {
                CallableStatement callableStatement = cStmt = this.parseCallableStatement((String)sql);
                // MONITORENTER : callableStatement
                cachedParamInfo = cStmt.paramInfo;
                // MONITOREXIT : callableStatement
                this.parsedCallableStatementCache.put((CompoundCacheKey)key, (CallableStatement.CallableStatementParamInfo)cachedParamInfo);
            }
            // MONITOREXIT : lRUCache
        }
        cStmt.setResultSetType((int)resultSetType);
        cStmt.setResultSetConcurrency((int)resultSetConcurrency);
        return cStmt;
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!this.getPedantic()) return (CallableStatement)this.prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        if (resultSetHoldability == 1) return (CallableStatement)this.prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        throw SQLError.createSQLException((String)"HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.prepareStatement((String)sql, (int)1003, (int)1007);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        java.sql.PreparedStatement pStmt = this.prepareStatement((String)sql);
        ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((boolean)(autoGenKeyIndex == 1));
        return pStmt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String nativeSql;
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        PreparedStatement pStmt = null;
        boolean canServerPrepare = true;
        String string = nativeSql = this.getProcessEscapeCodesForPrepStmts() ? this.nativeSQL((String)sql) : sql;
        if (this.useServerPreparedStmts && this.getEmulateUnsupportedPstmts()) {
            canServerPrepare = this.canHandleAsServerPreparedStatement((String)nativeSql);
        }
        if (!this.useServerPreparedStmts) return (PreparedStatement)this.clientPrepareStatement((String)nativeSql, (int)resultSetType, (int)resultSetConcurrency, (boolean)false);
        if (!canServerPrepare) return (PreparedStatement)this.clientPrepareStatement((String)nativeSql, (int)resultSetType, (int)resultSetConcurrency, (boolean)false);
        if (this.getCachePreparedStatements()) {
            block16 : {
                LRUCache<CompoundCacheKey, ServerPreparedStatement> lRUCache = this.serverSideStatementCache;
                // MONITORENTER : lRUCache
                pStmt = (PreparedStatement)this.serverSideStatementCache.remove((Object)new CompoundCacheKey((String)this.database, (String)sql));
                if (pStmt != null) {
                    ((ServerPreparedStatement)pStmt).setClosed((boolean)false);
                    pStmt.clearParameters();
                }
                if (pStmt == null) {
                    try {
                        pStmt = ServerPreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.database, (int)resultSetType, (int)resultSetConcurrency);
                        if (sql.length() < this.getPreparedStatementCacheSqlLimit()) {
                            ((ServerPreparedStatement)pStmt).isCached = true;
                        }
                        pStmt.setResultSetType((int)resultSetType);
                        pStmt.setResultSetConcurrency((int)resultSetConcurrency);
                        return pStmt;
                    }
                    catch (SQLException sqlEx) {
                        if (!this.getEmulateUnsupportedPstmts()) throw sqlEx;
                        pStmt = (PreparedStatement)this.clientPrepareStatement((String)nativeSql, (int)resultSetType, (int)resultSetConcurrency, (boolean)false);
                        if (sql.length() >= this.getPreparedStatementCacheSqlLimit()) break block16;
                        this.serverSideStatementCheckCache.put((String)sql, (Boolean)Boolean.FALSE);
                        return pStmt;
                    }
                }
            }
            // MONITOREXIT : lRUCache
            return pStmt;
        }
        try {
            pStmt = ServerPreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.database, (int)resultSetType, (int)resultSetConcurrency);
            pStmt.setResultSetType((int)resultSetType);
            pStmt.setResultSetConcurrency((int)resultSetConcurrency);
            return pStmt;
        }
        catch (SQLException sqlEx) {
            if (!this.getEmulateUnsupportedPstmts()) throw sqlEx;
            return (PreparedStatement)this.clientPrepareStatement((String)nativeSql, (int)resultSetType, (int)resultSetConcurrency, (boolean)false);
        }
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!this.getPedantic()) return this.prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        if (resultSetHoldability == 1) return this.prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        throw SQLError.createSQLException((String)"HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        java.sql.PreparedStatement pStmt = this.prepareStatement((String)sql);
        ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((boolean)(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        java.sql.PreparedStatement pStmt = this.prepareStatement((String)sql);
        ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((boolean)(autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
        return pStmt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
        SQLException sqlEx = null;
        if (this.isClosed()) {
            return;
        }
        this.forceClosedReason = reason;
        try {
            if (!skipLocalTeardown) {
                if (!this.getAutoCommit() && issueRollback) {
                    try {
                        this.rollback();
                    }
                    catch (SQLException ex) {
                        sqlEx = ex;
                    }
                }
                if (this.getGatherPerfMetrics()) {
                    this.reportMetrics();
                }
                if (this.getUseUsageAdvisor()) {
                    String message;
                    if (!calledExplicitly) {
                        message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
                        this.eventSink.processEvent((byte)0, (MySQLConnection)this, null, null, (long)0L, (Throwable)new Throwable(), (String)message);
                    }
                    if (System.currentTimeMillis() - this.connectionCreationTimeMillis < 500L) {
                        message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
                        this.eventSink.processEvent((byte)0, (MySQLConnection)this, null, null, (long)0L, (Throwable)new Throwable(), (String)message);
                    }
                }
                try {
                    this.closeAllOpenStatements();
                }
                catch (SQLException ex) {
                    sqlEx = ex;
                }
                if (this.io != null) {
                    try {
                        this.io.quit();
                    }
                    catch (Exception e) {}
                }
            } else {
                this.io.forceClose();
            }
            if (this.statementInterceptors != null) {
                for (int i = 0; i < this.statementInterceptors.size(); ++i) {
                    this.statementInterceptors.get((int)i).destroy();
                }
            }
            if (this.exceptionInterceptor != null) {
                this.exceptionInterceptor.destroy();
            }
            Object var8_11 = null;
            this.openStatements.clear();
            if (this.io != null) {
                this.io.releaseResources();
                this.io = null;
            }
            this.statementInterceptors = null;
            this.exceptionInterceptor = null;
            ProfilerEventHandlerFactory.removeInstance((MySQLConnection)this);
            this.eventSink = null;
            Object object = this.getConnectionMutex();
            // MONITORENTER : object
            if (this.cancelTimer != null) {
                this.cancelTimer.cancel();
            }
            // MONITOREXIT : object
            this.isClosed = true;
        }
        catch (Throwable throwable) {
            Object var8_12 = null;
            this.openStatements.clear();
            if (this.io != null) {
                this.io.releaseResources();
                this.io = null;
            }
            this.statementInterceptors = null;
            this.exceptionInterceptor = null;
            ProfilerEventHandlerFactory.removeInstance((MySQLConnection)this);
            this.eventSink = null;
            Object object = this.getConnectionMutex();
            // MONITORENTER : object
            if (this.cancelTimer != null) {
                this.cancelTimer.cancel();
            }
            // MONITOREXIT : object
            this.isClosed = true;
            throw throwable;
        }
        if (sqlEx == null) return;
        throw sqlEx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (!this.getCachePreparedStatements()) return;
        if (!pstmt.isPoolable()) return;
        LRUCache<CompoundCacheKey, ServerPreparedStatement> lRUCache = this.serverSideStatementCache;
        // MONITORENTER : lRUCache
        ServerPreparedStatement oldServerPrepStmt = this.serverSideStatementCache.put((CompoundCacheKey)new CompoundCacheKey((String)pstmt.currentCatalog, (String)pstmt.originalSql), (ServerPreparedStatement)pstmt);
        if (oldServerPrepStmt != null && oldServerPrepStmt != pstmt) {
            oldServerPrepStmt.isCached = false;
            oldServerPrepStmt.setClosed((boolean)false);
            oldServerPrepStmt.realClose((boolean)true, (boolean)true);
        }
        // MONITOREXIT : lRUCache
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void decachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (!this.getCachePreparedStatements()) return;
        if (!pstmt.isPoolable()) return;
        LRUCache<CompoundCacheKey, ServerPreparedStatement> lRUCache = this.serverSideStatementCache;
        // MONITORENTER : lRUCache
        this.serverSideStatementCache.remove((Object)new CompoundCacheKey((String)pstmt.currentCatalog, (String)pstmt.originalSql));
        // MONITOREXIT : lRUCache
        return;
    }

    @Override
    public void registerQueryExecutionTime(long queryTimeMs) {
        if (queryTimeMs > this.longestQueryTimeMs) {
            this.longestQueryTimeMs = queryTimeMs;
            this.repartitionPerformanceHistogram();
        }
        this.addToPerformanceHistogram((long)queryTimeMs, (int)1);
        if (queryTimeMs < this.shortestQueryTimeMs) {
            this.shortestQueryTimeMs = queryTimeMs == 0L ? 1L : queryTimeMs;
        }
        ++this.numberOfQueriesIssued;
        this.totalQueryTimeMs += (double)queryTimeMs;
    }

    @Override
    public void registerStatement(Statement stmt) {
        this.openStatements.addIfAbsent((Statement)stmt);
    }

    @Override
    public void releaseSavepoint(Savepoint arg0) throws SQLException {
    }

    private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound) {
        if (this.oldHistCounts == null) {
            this.oldHistCounts = new int[histCounts.length];
            this.oldHistBreakpoints = new long[histBreakpoints.length];
        }
        System.arraycopy((Object)histCounts, (int)0, (Object)this.oldHistCounts, (int)0, (int)histCounts.length);
        System.arraycopy((Object)histBreakpoints, (int)0, (Object)this.oldHistBreakpoints, (int)0, (int)histBreakpoints.length);
        this.createInitialHistogram((long[])histBreakpoints, (long)currentLowerBound, (long)currentUpperBound);
        int i = 0;
        while (i < 20) {
            this.addToHistogram((int[])histCounts, (long[])histBreakpoints, (long)this.oldHistBreakpoints[i], (int)this.oldHistCounts[i], (long)currentLowerBound, (long)currentUpperBound);
            ++i;
        }
    }

    private void repartitionPerformanceHistogram() {
        this.checkAndCreatePerformanceHistogram();
        this.repartitionHistogram((int[])this.perfMetricsHistCounts, (long[])this.perfMetricsHistBreakpoints, (long)(this.shortestQueryTimeMs == Long.MAX_VALUE ? 0L : this.shortestQueryTimeMs), (long)this.longestQueryTimeMs);
    }

    private void repartitionTablesAccessedHistogram() {
        this.checkAndCreateTablesAccessedHistogram();
        this.repartitionHistogram((int[])this.numTablesMetricsHistCounts, (long[])this.numTablesMetricsHistBreakpoints, (long)(this.minimumNumberTablesAccessed == Long.MAX_VALUE ? 0L : this.minimumNumberTablesAccessed), (long)this.maximumNumberTablesAccessed);
    }

    private void reportMetrics() {
        int numPointsToGraph;
        int j;
        int i;
        int maxNumPoints;
        int highestCount;
        if (!this.getGatherPerformanceMetrics()) return;
        StringBuilder logMessage = new StringBuilder((int)256);
        logMessage.append((String)"** Performance Metrics Report **\n");
        logMessage.append((String)("\nLongest reported query: " + this.longestQueryTimeMs + " ms"));
        logMessage.append((String)("\nShortest reported query: " + this.shortestQueryTimeMs + " ms"));
        logMessage.append((String)("\nAverage query execution time: " + this.totalQueryTimeMs / (double)this.numberOfQueriesIssued + " ms"));
        logMessage.append((String)("\nNumber of statements executed: " + this.numberOfQueriesIssued));
        logMessage.append((String)("\nNumber of result sets created: " + this.numberOfResultSetsCreated));
        logMessage.append((String)("\nNumber of statements prepared: " + this.numberOfPrepares));
        logMessage.append((String)("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes));
        if (this.perfMetricsHistBreakpoints != null) {
            logMessage.append((String)"\n\n\tTiming Histogram:\n");
            maxNumPoints = 20;
            highestCount = Integer.MIN_VALUE;
            for (i = 0; i < 20; ++i) {
                if (this.perfMetricsHistCounts[i] <= highestCount) continue;
                highestCount = this.perfMetricsHistCounts[i];
            }
            if (highestCount == 0) {
                highestCount = 1;
            }
            for (i = 0; i < 19; ++i) {
                if (i == 0) {
                    logMessage.append((String)("\n\tless than " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]));
                } else {
                    logMessage.append((String)("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]));
                }
                logMessage.append((String)"\t");
                numPointsToGraph = (int)((double)maxNumPoints * ((double)this.perfMetricsHistCounts[i] / (double)highestCount));
                for (j = 0; j < numPointsToGraph; ++j) {
                    logMessage.append((String)"*");
                }
                if (this.longestQueryTimeMs < (long)this.perfMetricsHistCounts[i + 1]) break;
            }
            if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
                logMessage.append((String)"\n\tbetween ");
                logMessage.append((long)this.perfMetricsHistBreakpoints[18]);
                logMessage.append((String)" and ");
                logMessage.append((long)this.perfMetricsHistBreakpoints[19]);
                logMessage.append((String)" ms: \t");
                logMessage.append((int)this.perfMetricsHistCounts[19]);
            }
        }
        if (this.numTablesMetricsHistBreakpoints != null) {
            logMessage.append((String)"\n\n\tTable Join Histogram:\n");
            maxNumPoints = 20;
            highestCount = Integer.MIN_VALUE;
            for (i = 0; i < 20; ++i) {
                if (this.numTablesMetricsHistCounts[i] <= highestCount) continue;
                highestCount = this.numTablesMetricsHistCounts[i];
            }
            if (highestCount == 0) {
                highestCount = 1;
            }
            for (i = 0; i < 19; ++i) {
                if (i == 0) {
                    logMessage.append((String)("\n\t" + this.numTablesMetricsHistBreakpoints[i + 1] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]));
                } else {
                    logMessage.append((String)("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[i + 1] + " tables: \t" + this.numTablesMetricsHistCounts[i]));
                }
                logMessage.append((String)"\t");
                numPointsToGraph = (int)((double)maxNumPoints * ((double)this.numTablesMetricsHistCounts[i] / (double)highestCount));
                for (j = 0; j < numPointsToGraph; ++j) {
                    logMessage.append((String)"*");
                }
                if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[i + 1]) break;
            }
            if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
                logMessage.append((String)"\n\tbetween ");
                logMessage.append((long)this.numTablesMetricsHistBreakpoints[18]);
                logMessage.append((String)" and ");
                logMessage.append((long)this.numTablesMetricsHistBreakpoints[19]);
                logMessage.append((String)" tables: ");
                logMessage.append((int)this.numTablesMetricsHistCounts[19]);
            }
        }
        this.log.logInfo((Object)logMessage);
        this.metricsLastReportedMs = System.currentTimeMillis();
    }

    protected void reportMetricsIfNeeded() {
        if (!this.getGatherPerformanceMetrics()) return;
        if (System.currentTimeMillis() - this.metricsLastReportedMs <= (long)this.getReportMetricsIntervalMillis()) return;
        this.reportMetrics();
    }

    @Override
    public void reportNumberOfTablesAccessed(int numTablesAccessed) {
        if ((long)numTablesAccessed < this.minimumNumberTablesAccessed) {
            this.minimumNumberTablesAccessed = (long)numTablesAccessed;
        }
        if ((long)numTablesAccessed > this.maximumNumberTablesAccessed) {
            this.maximumNumberTablesAccessed = (long)numTablesAccessed;
            this.repartitionTablesAccessedHistogram();
        }
        this.addToTablesAccessedHistogram((long)((long)numTablesAccessed), (int)1);
    }

    @Override
    public void resetServerState() throws SQLException {
        if (this.getParanoid()) return;
        if (this.io == null) return;
        if (!this.versionMeetsMinimum((int)4, (int)0, (int)6)) return;
        this.changeUser((String)this.user, (String)this.password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rollback() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        try {
            try {
                if (this.connectionLifecycleInterceptors != null) {
                    IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator()){
                        final /* synthetic */ ConnectionImpl this$0;
                        {
                            this.this$0 = connectionImpl;
                            super(x0);
                        }

                        void forEach(Extension each) throws SQLException {
                            if (((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).rollback()) return;
                            this.stopIterating = true;
                        }
                    };
                    iter.doForAll();
                    if (!iter.fullIteration()) {
                        Object var4_5 = null;
                        this.needsPing = this.getReconnectAtTxEnd();
                        // MONITOREXIT : object
                        return;
                    }
                }
                if (this.autoCommit && !this.getRelaxAutoCommit()) {
                    throw SQLError.createSQLException((String)"Can't call rollback when autocommit=true", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                if (this.transactionsSupported) {
                    try {
                        this.rollbackNoChecks();
                    }
                    catch (SQLException sqlEx) {
                        if (!this.getIgnoreNonTxTables()) throw sqlEx;
                        if (sqlEx.getErrorCode() != 1196) throw sqlEx;
                        Object var4_6 = null;
                        this.needsPing = this.getReconnectAtTxEnd();
                        // MONITOREXIT : object
                        return;
                    }
                }
                Object var4_7 = null;
            }
            catch (SQLException sqlException) {
                if (!"08S01".equals((Object)sqlException.getSQLState())) throw sqlException;
                throw SQLError.createSQLException((String)"Communications link failure during rollback(). Transaction resolution unknown.", (String)"08007", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            this.needsPing = this.getReconnectAtTxEnd();
            return;
        }
        catch (Throwable throwable) {
            Object var4_8 = null;
            this.needsPing = this.getReconnectAtTxEnd();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (!this.versionMeetsMinimum((int)4, (int)0, (int)14)) {
            if (!this.versionMeetsMinimum((int)4, (int)1, (int)1)) throw SQLError.createSQLFeatureNotSupportedException();
        }
        this.checkClosed();
        try {
            if (this.connectionLifecycleInterceptors != null) {
                IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator(), (Savepoint)savepoint){
                    final /* synthetic */ Savepoint val$savepoint;
                    final /* synthetic */ ConnectionImpl this$0;
                    {
                        this.this$0 = connectionImpl;
                        this.val$savepoint = savepoint;
                        super(x0);
                    }

                    void forEach(Extension each) throws SQLException {
                        if (((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).rollback((Savepoint)this.val$savepoint)) return;
                        this.stopIterating = true;
                    }
                };
                iter.doForAll();
                if (!iter.fullIteration()) {
                    Object var12_4 = null;
                    this.needsPing = this.getReconnectAtTxEnd();
                    // MONITOREXIT : object
                    return;
                }
            }
            StringBuilder rollbackQuery = new StringBuilder((String)"ROLLBACK TO SAVEPOINT ");
            rollbackQuery.append((char)'`');
            rollbackQuery.append((String)savepoint.getSavepointName());
            rollbackQuery.append((char)'`');
            java.sql.Statement stmt = null;
            try {
                try {
                    stmt = this.getMetadataSafeStatement();
                    stmt.executeUpdate((String)rollbackQuery.toString());
                }
                catch (SQLException sqlEx) {
                    String msg;
                    int indexOfError153;
                    int errno = sqlEx.getErrorCode();
                    if (errno == 1181 && (msg = sqlEx.getMessage()) != null && (indexOfError153 = msg.indexOf((String)"153")) != -1) {
                        throw SQLError.createSQLException((String)("Savepoint '" + savepoint.getSavepointName() + "' does not exist"), (String)"S1009", (int)errno, (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    if (this.getIgnoreNonTxTables() && sqlEx.getErrorCode() != 1196) {
                        throw sqlEx;
                    }
                    if (!"08S01".equals((Object)sqlEx.getSQLState())) throw sqlEx;
                    throw SQLError.createSQLException((String)"Communications link failure during rollback(). Transaction resolution unknown.", (String)"08007", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                Object var10_8 = null;
                this.closeStatement((java.sql.Statement)stmt);
            }
            catch (Throwable throwable) {
                Object var10_9 = null;
                this.closeStatement((java.sql.Statement)stmt);
                throw throwable;
            }
            Object var12_5 = null;
            this.needsPing = this.getReconnectAtTxEnd();
            return;
        }
        catch (Throwable throwable) {
            Object var12_6 = null;
            this.needsPing = this.getReconnectAtTxEnd();
            throw throwable;
        }
    }

    private void rollbackNoChecks() throws SQLException {
        if (this.getUseLocalTransactionState() && this.versionMeetsMinimum((int)5, (int)0, (int)0) && !this.io.inTransactionOnServer()) {
            return;
        }
        this.execSQL(null, (String)"rollback", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql) throws SQLException {
        String nativeSql = this.getProcessEscapeCodesForPrepStmts() ? this.nativeSQL((String)sql) : sql;
        return ServerPreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.getCatalog(), (int)1003, (int)1007);
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        String nativeSql = this.getProcessEscapeCodesForPrepStmts() ? this.nativeSQL((String)sql) : sql;
        ServerPreparedStatement pStmt = ServerPreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.getCatalog(), (int)1003, (int)1007);
        pStmt.setRetrieveGeneratedKeys((boolean)(autoGenKeyIndex == 1));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String nativeSql = this.getProcessEscapeCodesForPrepStmts() ? this.nativeSQL((String)sql) : sql;
        return ServerPreparedStatement.getInstance((MySQLConnection)this.getMultiHostSafeProxy(), (String)nativeSql, (String)this.getCatalog(), (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!this.getPedantic()) return this.serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        if (resultSetHoldability == 1) return this.serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
        throw SQLError.createSQLException((String)"HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement)this.serverPrepareStatement((String)sql);
        pStmt.setRetrieveGeneratedKeys((boolean)(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0));
        return pStmt;
    }

    @Override
    public java.sql.PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement)this.serverPrepareStatement((String)sql);
        pStmt.setRetrieveGeneratedKeys((boolean)(autoGenKeyColNames != null && autoGenKeyColNames.length > 0));
        return pStmt;
    }

    @Override
    public boolean serverSupportsConvertFn() throws SQLException {
        return this.versionMeetsMinimum((int)4, (int)0, (int)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoCommit(boolean autoCommitFlag) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator(), (boolean)autoCommitFlag){
                final /* synthetic */ boolean val$autoCommitFlag;
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    this.val$autoCommitFlag = bl;
                    super(x0);
                }

                void forEach(Extension each) throws SQLException {
                    if (((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).setAutoCommit((boolean)this.val$autoCommitFlag)) return;
                    this.stopIterating = true;
                }
            };
            iter.doForAll();
            if (!iter.fullIteration()) {
                // MONITOREXIT : object
                return;
            }
        }
        if (this.getAutoReconnectForPools()) {
            this.setHighAvailability((boolean)true);
        }
        try {
            if (this.transactionsSupported) {
                boolean needsSetOnServer = true;
                if (this.getUseLocalSessionState() && this.autoCommit == autoCommitFlag) {
                    needsSetOnServer = false;
                } else if (!this.getHighAvailability()) {
                    needsSetOnServer = this.getIO().isSetNeededForAutoCommitMode((boolean)autoCommitFlag);
                }
                this.autoCommit = autoCommitFlag;
                if (needsSetOnServer) {
                    this.execSQL(null, (String)(autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0"), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                }
            } else {
                if (!autoCommitFlag && !this.getRelaxAutoCommit()) {
                    throw SQLError.createSQLException((String)"MySQL Versions Older than 3.23.15 do not support transactions", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                this.autoCommit = autoCommitFlag;
            }
            Object var5_5 = null;
            if (!this.getAutoReconnectForPools()) {
                // MONITOREXIT : object
                return;
            }
            this.setHighAvailability((boolean)false);
            return;
        }
        catch (Throwable throwable) {
            Object var5_6 = null;
            if (!this.getAutoReconnectForPools()) throw throwable;
            this.setHighAvailability((boolean)false);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        String quotedId;
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        if (catalog == null) {
            throw SQLError.createSQLException((String)"Catalog can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator(), (String)catalog){
                final /* synthetic */ String val$catalog;
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    this.val$catalog = string;
                    super(x0);
                }

                void forEach(Extension each) throws SQLException {
                    if (((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).setCatalog((String)this.val$catalog)) return;
                    this.stopIterating = true;
                }
            };
            iter.doForAll();
            if (!iter.fullIteration()) {
                // MONITOREXIT : object
                return;
            }
        }
        if (this.getUseLocalSessionState()) {
            if (this.lowerCaseTableNames) {
                if (this.database.equalsIgnoreCase((String)catalog)) {
                    // MONITOREXIT : object
                    return;
                }
            } else if (this.database.equals((Object)catalog)) {
                // MONITOREXIT : object
                return;
            }
        }
        if ((quotedId = this.dbmd.getIdentifierQuoteString()) == null || quotedId.equals((Object)" ")) {
            quotedId = "";
        }
        StringBuilder query = new StringBuilder((String)"USE ");
        query.append((String)StringUtils.quoteIdentifier((String)catalog, (String)quotedId, (boolean)this.getPedantic()));
        this.execSQL(null, (String)query.toString(), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
        this.database = catalog;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setFailedOver(boolean flag) {
    }

    @Override
    public void setHoldability(int arg0) throws SQLException {
    }

    @Override
    public void setInGlobalTx(boolean flag) {
        this.isInGlobalTx = flag;
    }

    @Deprecated
    @Override
    public void setPreferSlaveDuringFailover(boolean flag) {
    }

    @Override
    public void setReadInfoMsgEnabled(boolean flag) {
        this.readInfoMsg = flag;
    }

    @Override
    public void setReadOnly(boolean readOnlyFlag) throws SQLException {
        this.checkClosed();
        this.setReadOnlyInternal((boolean)readOnlyFlag);
    }

    @Override
    public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
        if (this.getReadOnlyPropagatesToServer() && this.versionMeetsMinimum((int)5, (int)6, (int)5) && (!this.getUseLocalSessionState() || readOnlyFlag != this.readOnly)) {
            this.execSQL(null, (String)("set session transaction " + (readOnlyFlag ? "read only" : "read write")), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
        }
        this.readOnly = readOnlyFlag;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        MysqlSavepoint savepoint = new MysqlSavepoint((ExceptionInterceptor)this.getExceptionInterceptor());
        this.setSavepoint((MysqlSavepoint)savepoint);
        return savepoint;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (!this.versionMeetsMinimum((int)4, (int)0, (int)14)) {
            if (!this.versionMeetsMinimum((int)4, (int)1, (int)1)) throw SQLError.createSQLFeatureNotSupportedException();
        }
        this.checkClosed();
        StringBuilder savePointQuery = new StringBuilder((String)"SAVEPOINT ");
        savePointQuery.append((char)'`');
        savePointQuery.append((String)savepoint.getSavepointName());
        savePointQuery.append((char)'`');
        java.sql.Statement stmt = null;
        try {
            stmt = this.getMetadataSafeStatement();
            stmt.executeUpdate((String)savePointQuery.toString());
            Object var6_5 = null;
            this.closeStatement((java.sql.Statement)stmt);
            return;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.closeStatement((java.sql.Statement)stmt);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        MysqlSavepoint savepoint = new MysqlSavepoint((String)name, (ExceptionInterceptor)this.getExceptionInterceptor());
        this.setSavepoint((MysqlSavepoint)savepoint);
        // MONITOREXIT : object
        return savepoint;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setSessionVariables() throws SQLException {
        if (!this.versionMeetsMinimum((int)4, (int)0, (int)0)) return;
        if (this.getSessionVariables() == null) return;
        ArrayList<String> variablesToSet = new ArrayList<String>();
        for (String part : StringUtils.split((String)this.getSessionVariables(), (String)",", (String)"\"'(", (String)"\"')", (String)"\"'", (boolean)true)) {
            variablesToSet.addAll(StringUtils.split((String)part, (String)";", (String)"\"'(", (String)"\"')", (String)"\"'", (boolean)true));
        }
        if (variablesToSet.isEmpty()) return;
        java.sql.Statement stmt = null;
        try {
            stmt = this.getMetadataSafeStatement();
            StringBuilder query = new StringBuilder((String)"SET ");
            String separator = "";
            for (String variableToSet : variablesToSet) {
                if (variableToSet.length() <= 0) continue;
                query.append((String)separator);
                if (!variableToSet.startsWith((String)"@")) {
                    query.append((String)"SESSION ");
                }
                query.append((String)variableToSet);
                separator = ",";
            }
            stmt.executeUpdate((String)query.toString());
            Object var8_7 = null;
            if (stmt == null) return;
            stmt.close();
            return;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        var2_2 = this.getConnectionMutex();
        // MONITORENTER : var2_2
        this.checkClosed();
        if (this.hasIsolationLevels == false) throw SQLError.createSQLException((String)"Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
        sql = null;
        shouldSendSet = false;
        if (this.getAlwaysSendSetIsolation()) {
            shouldSendSet = true;
        } else if (level != this.isolationLevel) {
            shouldSendSet = true;
        }
        if (this.getUseLocalSessionState()) {
            v0 = shouldSendSet = this.isolationLevel != level;
        }
        if (!shouldSendSet) {
            // MONITOREXIT : var2_2
            return;
        }
        switch (level) {
            case 0: {
                throw SQLError.createSQLException((String)"Transaction isolation level NONE not supported by MySQL", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 2: {
                sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
                ** break;
            }
            case 1: {
                sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
                ** break;
            }
            case 4: {
                sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
                ** break;
            }
            case 8: {
                sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
                ** break;
            }
        }
        throw SQLError.createSQLException((String)("Unsupported transaction isolation level '" + level + "'"), (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
lbl33: // 4 sources:
        this.execSQL(null, (String)sql, (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
        this.isolationLevel = level;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.typeMap = map;
        // MONITOREXIT : object
        return;
    }

    private void setupServerForTruncationChecks() throws SQLException {
        boolean strictTransTablesIsSet;
        if (!this.getJdbcCompliantTruncation()) return;
        if (!this.versionMeetsMinimum((int)5, (int)0, (int)2)) return;
        String currentSqlMode = this.serverVariables.get((Object)"sql_mode");
        boolean bl = strictTransTablesIsSet = StringUtils.indexOfIgnoreCase((String)currentSqlMode, (String)"STRICT_TRANS_TABLES") != -1;
        if (currentSqlMode != null && currentSqlMode.length() != 0 && strictTransTablesIsSet) {
            if (!strictTransTablesIsSet) return;
            this.setJdbcCompliantTruncation((boolean)false);
            return;
        }
        StringBuilder commandBuf = new StringBuilder((String)"SET sql_mode='");
        if (currentSqlMode != null && currentSqlMode.length() > 0) {
            commandBuf.append((String)currentSqlMode);
            commandBuf.append((String)",");
        }
        commandBuf.append((String)"STRICT_TRANS_TABLES'");
        this.execSQL(null, (String)commandBuf.toString(), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
        this.setJdbcCompliantTruncation((boolean)false);
    }

    @Override
    public void shutdownServer() throws SQLException {
        try {
            if (this.versionMeetsMinimum((int)5, (int)7, (int)9)) {
                this.execSQL(null, (String)"SHUTDOWN", (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
                return;
            }
            this.io.sendCommand((int)8, null, null, (boolean)false, null, (int)0);
            return;
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.UnhandledExceptionDuringShutdown"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    @Override
    public boolean supportsIsolationLevel() {
        return this.hasIsolationLevels;
    }

    @Override
    public boolean supportsQuotedIdentifiers() {
        return this.hasQuotedIdentifiers;
    }

    @Override
    public boolean supportsTransactions() {
        return this.transactionsSupported;
    }

    @Override
    public void unregisterStatement(Statement stmt) {
        this.openStatements.remove((Object)stmt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean useAnsiQuotedIdentifiers() {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.useAnsiQuotes;
    }

    @Override
    public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
        this.checkClosed();
        return this.io.versionMeetsMinimum((int)major, (int)minor, (int)subminor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CachedResultSetMetaData getCachedMetaData(String sql) {
        if (this.resultSetMetadataCache == null) return null;
        LRUCache<String, CachedResultSetMetaData> lRUCache = this.resultSetMetadataCache;
        // MONITORENTER : lRUCache
        // MONITOREXIT : lRUCache
        return (CachedResultSetMetaData)this.resultSetMetadataCache.get((Object)sql);
    }

    @Override
    public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
        if (cachedMetaData != null) {
            resultSet.initializeFromCachedMetaData((CachedResultSetMetaData)cachedMetaData);
            resultSet.initializeWithMetadata();
            if (!(resultSet instanceof UpdatableResultSet)) return;
            ((UpdatableResultSet)resultSet).checkUpdatability();
            return;
        }
        cachedMetaData = new CachedResultSetMetaData();
        resultSet.buildIndexMapping();
        resultSet.initializeWithMetadata();
        if (resultSet instanceof UpdatableResultSet) {
            ((UpdatableResultSet)resultSet).checkUpdatability();
        }
        resultSet.populateCachedMetaData((CachedResultSetMetaData)cachedMetaData);
        this.resultSetMetadataCache.put((String)sql, (CachedResultSetMetaData)cachedMetaData);
    }

    @Override
    public String getStatementComment() {
        return this.statementComment;
    }

    @Override
    public void setStatementComment(String comment) {
        this.statementComment = comment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reportQueryTime(long millisOrNanos) {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        ++this.queryTimeCount;
        this.queryTimeSum += (double)millisOrNanos;
        this.queryTimeSumSquares += (double)(millisOrNanos * millisOrNanos);
        this.queryTimeMean = (this.queryTimeMean * (double)(this.queryTimeCount - 1L) + (double)millisOrNanos) / (double)this.queryTimeCount;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isAbonormallyLongQuery(long millisOrNanos) {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        boolean res = false;
        if (this.queryTimeCount > 14L) {
            double stddev = Math.sqrt((double)((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / (double)this.queryTimeCount) / (double)(this.queryTimeCount - 1L)));
            res = (double)millisOrNanos > this.queryTimeMean + 5.0 * stddev;
        }
        this.reportQueryTime((long)millisOrNanos);
        // MONITOREXIT : object
        return res;
    }

    @Override
    public void initializeExtension(Extension ex) throws SQLException {
        ex.init((Connection)this, (Properties)this.props);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transactionBegun() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator()){
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    super(x0);
                }

                void forEach(Extension each) throws SQLException {
                    ((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).transactionBegun();
                }
            };
            iter.doForAll();
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transactionCompleted() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.connectionLifecycleInterceptors != null) {
            IterateBlock<Extension> iter = new IterateBlock<Extension>((ConnectionImpl)this, this.connectionLifecycleInterceptors.iterator()){
                final /* synthetic */ ConnectionImpl this$0;
                {
                    this.this$0 = connectionImpl;
                    super(x0);
                }

                void forEach(Extension each) throws SQLException {
                    ((com.mysql.jdbc.ConnectionLifecycleInterceptor)each).transactionCompleted();
                }
            };
            iter.doForAll();
        }
        // MONITOREXIT : object
        return;
    }

    @Override
    public boolean storesLowerCaseTableName() {
        return this.storesLowerCaseTableName;
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    @Override
    public boolean getRequiresEscapingEncoder() {
        return this.requiresEscapingEncoder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isServerLocal() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        SocketFactory factory = this.getIO().socketFactory;
        if (factory instanceof SocketMetadata) {
            // MONITOREXIT : object
            return ((SocketMetadata)((Object)factory)).isLocallyConnected((ConnectionImpl)this);
        }
        this.getLog().logWarn((Object)Messages.getString((String)"Connection.NoMetadataOnSocketFactory"));
        // MONITOREXIT : object
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSessionMaxRows() {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.sessionMaxRows;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSessionMaxRows(int max) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        if (this.sessionMaxRows != max) {
            this.sessionMaxRows = max;
            this.execSQL(null, (String)("SET SQL_SELECT_LIMIT=" + (this.sessionMaxRows == -1 ? "DEFAULT" : Integer.valueOf((int)this.sessionMaxRows))), (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.database, null, (boolean)false);
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSchema(String schema) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSchema() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        // MONITOREXIT : object
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission((Permission)ABORT_PERM);
        }
        if (executor == null) {
            throw SQLError.createSQLException((String)"Executor can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        executor.execute((Runnable)new Runnable((ConnectionImpl)this){
            final /* synthetic */ ConnectionImpl this$0;
            {
                this.this$0 = connectionImpl;
            }

            public void run() {
                try {
                    this.this$0.abortInternal();
                    return;
                }
                catch (SQLException e) {
                    throw new RuntimeException((Throwable)e);
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission((Permission)SET_NETWORK_TIMEOUT_PERM);
        }
        if (executor == null) {
            throw SQLError.createSQLException((String)"Executor can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.checkClosed();
        executor.execute((Runnable)new NetworkTimeoutSetter((ConnectionImpl)this, (MysqlIO)this.io, (int)milliseconds));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        Object object = this.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        // MONITOREXIT : object
        return this.getSocketTimeout();
    }

    @Override
    public ProfilerEventHandler getProfilerEventHandlerInstance() {
        return this.eventSink;
    }

    @Override
    public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {
        this.eventSink = h;
    }

    @Override
    public boolean isServerTruncatesFracSecs() {
        return this.serverTruncatesFracSecs;
    }

    @Override
    public String getQueryTimingUnits() {
        String string;
        if (this.io != null) {
            string = this.io.getQueryTimingUnits();
            return string;
        }
        string = Constants.MILLIS_I18N;
        return string;
    }

    static /* synthetic */ CacheAdapter access$000(ConnectionImpl x0) {
        return x0.serverConfigCache;
    }

    static {
        mapTransIsolationNameToValue = null;
        NULL_LOGGER = new NullLogger((String)LOGGER_INSTANCE_NAME);
        customIndexToCharsetMapByUrl = new HashMap<String, Map<Integer, String>>();
        customCharsetToMblenMapByUrl = new HashMap<String, Map<String, Integer>>();
        mapTransIsolationNameToValue = new HashMap<String, Integer>((int)8);
        mapTransIsolationNameToValue.put((String)"READ-UNCOMMITED", (Integer)Integer.valueOf((int)1));
        mapTransIsolationNameToValue.put((String)"READ-UNCOMMITTED", (Integer)Integer.valueOf((int)1));
        mapTransIsolationNameToValue.put((String)"READ-COMMITTED", (Integer)Integer.valueOf((int)2));
        mapTransIsolationNameToValue.put((String)"REPEATABLE-READ", (Integer)Integer.valueOf((int)4));
        mapTransIsolationNameToValue.put((String)"SERIALIZABLE", (Integer)Integer.valueOf((int)8));
        if (Util.isJdbc4()) {
            try {
                JDBC_4_CONNECTION_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4Connection").getConstructor(String.class, Integer.TYPE, Properties.class, String.class, String.class);
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
        } else {
            JDBC_4_CONNECTION_CTOR = null;
        }
        random = new Random();
    }
}

