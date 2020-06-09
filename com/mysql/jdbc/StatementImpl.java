/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AssertionFailedException;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ByteArrayRow;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.EscapeProcessor;
import com.mysql.jdbc.EscapeProcessorResult;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.PingTarget;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.RowDataStatic;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StatementImpl
implements Statement {
    protected static final String PING_MARKER = "/* ping */";
    protected static final String[] ON_DUPLICATE_KEY_UPDATE_CLAUSE = new String[]{"ON", "DUPLICATE", "KEY", "UPDATE"};
    protected Object cancelTimeoutMutex = new Object();
    static int statementCounter = 1;
    public static final byte USES_VARIABLES_FALSE = 0;
    public static final byte USES_VARIABLES_TRUE = 1;
    public static final byte USES_VARIABLES_UNKNOWN = -1;
    protected boolean wasCancelled = false;
    protected boolean wasCancelledByTimeout = false;
    protected List<Object> batchedArgs;
    protected SingleByteCharsetConverter charConverter = null;
    protected String charEncoding = null;
    protected volatile MySQLConnection connection = null;
    protected Reference<MySQLConnection> physicalConnection = null;
    protected String currentCatalog = null;
    protected boolean doEscapeProcessing = true;
    private int fetchSize = 0;
    protected boolean isClosed = false;
    protected long lastInsertId = -1L;
    protected int maxFieldSize = MysqlIO.getMaxBuf();
    protected int maxRows = -1;
    protected Set<ResultSetInternalMethods> openResults = new HashSet<ResultSetInternalMethods>();
    protected boolean pedantic = false;
    protected boolean profileSQL = false;
    protected ResultSetInternalMethods results = null;
    protected ResultSetInternalMethods generatedKeysResults = null;
    protected int resultSetConcurrency = 0;
    protected int resultSetType = 0;
    protected int statementId;
    protected int timeoutInMillis = 0;
    protected long updateCount = -1L;
    protected boolean useUsageAdvisor = false;
    protected SQLWarning warningChain = null;
    protected boolean clearWarningsCalled = false;
    protected boolean holdResultsOpenOverClose = false;
    protected ArrayList<ResultSetRow> batchedGeneratedKeys = null;
    protected boolean retrieveGeneratedKeys = false;
    protected boolean continueBatchOnError = false;
    protected PingTarget pingTarget = null;
    protected boolean useLegacyDatetimeCode;
    protected boolean sendFractionalSeconds;
    private ExceptionInterceptor exceptionInterceptor;
    protected boolean lastQueryIsOnDupKeyUpdate = false;
    protected final AtomicBoolean statementExecuting = new AtomicBoolean((boolean)false);
    private boolean isImplicitlyClosingResults = false;
    private int originalResultSetType = 0;
    private int originalFetchSize = 0;
    private boolean isPoolable = true;
    private InputStream localInfileInputStream;
    protected final boolean version5013OrNewer;
    private boolean closeOnCompletion = false;

    public StatementImpl(MySQLConnection c, String catalog) throws SQLException {
        int maxRowsConn;
        boolean profiling;
        if (c == null) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.0"), (String)"08003", null);
        if (c.isClosed()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.0"), (String)"08003", null);
        }
        this.connection = c;
        this.exceptionInterceptor = this.connection.getExceptionInterceptor();
        this.currentCatalog = catalog;
        this.pedantic = this.connection.getPedantic();
        this.continueBatchOnError = this.connection.getContinueBatchOnError();
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        this.sendFractionalSeconds = this.connection.getSendFractionalSeconds();
        this.doEscapeProcessing = this.connection.getEnableEscapeProcessing();
        if (!this.connection.getDontTrackOpenResources()) {
            this.connection.registerStatement((Statement)this);
        }
        this.maxFieldSize = this.connection.getMaxAllowedPacket();
        int defaultFetchSize = this.connection.getDefaultFetchSize();
        if (defaultFetchSize != 0) {
            this.setFetchSize((int)defaultFetchSize);
        }
        if (this.connection.getUseUnicode()) {
            this.charEncoding = this.connection.getEncoding();
            this.charConverter = this.connection.getCharsetConverter((String)this.charEncoding);
        }
        boolean bl = profiling = this.connection.getProfileSql() || this.connection.getUseUsageAdvisor() || this.connection.getLogSlowQueries();
        if (this.connection.getAutoGenerateTestcaseScript() || profiling) {
            this.statementId = statementCounter++;
        }
        if (profiling) {
            this.profileSQL = this.connection.getProfileSql();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
        }
        if ((maxRowsConn = this.connection.getMaxRows()) != -1) {
            this.setMaxRows((int)maxRowsConn);
        }
        this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
        this.version5013OrNewer = this.connection.versionMeetsMinimum((int)5, (int)0, (int)13);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.batchedArgs == null) {
            this.batchedArgs = new ArrayList<Object>();
        }
        if (sql != null) {
            this.batchedArgs.add((Object)sql);
        }
        // MONITOREXIT : object
        return;
    }

    public List<Object> getBatchedArgs() {
        if (this.batchedArgs == null) {
            return null;
        }
        List<Object> list = Collections.unmodifiableList(this.batchedArgs);
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancel() throws SQLException {
        if (!this.statementExecuting.get()) {
            return;
        }
        if (this.isClosed) return;
        if (this.connection == null) return;
        if (!this.connection.versionMeetsMinimum((int)5, (int)0, (int)0)) return;
        Connection cancelConn = null;
        java.sql.Statement cancelStmt = null;
        try {
            cancelConn = this.connection.duplicate();
            cancelStmt = cancelConn.createStatement();
            cancelStmt.execute((String)("KILL QUERY " + this.connection.getIO().getThreadId()));
            this.wasCancelled = true;
            Object var4_3 = null;
            if (cancelStmt != null) {
                cancelStmt.close();
            }
            if (cancelConn == null) return;
            cancelConn.close();
            return;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            if (cancelStmt != null) {
                cancelStmt.close();
            }
            if (cancelConn == null) throw throwable;
            cancelConn.close();
            throw throwable;
        }
    }

    protected MySQLConnection checkClosed() throws SQLException {
        MySQLConnection c = this.connection;
        if (c != null) return c;
        throw SQLError.createSQLException((String)Messages.getString((String)"Statement.49"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    protected void checkForDml(String sql, char firstStatementChar) throws SQLException {
        String noCommentSql;
        if (firstStatementChar != 'I' && firstStatementChar != 'U' && firstStatementChar != 'D' && firstStatementChar != 'A' && firstStatementChar != 'C' && firstStatementChar != 'T') {
            if (firstStatementChar != 'R') return;
        }
        if (StringUtils.startsWithIgnoreCaseAndWs((String)(noCommentSql = StringUtils.stripComments((String)sql, (String)"'\"", (String)"'\"", (boolean)true, (boolean)false, (boolean)true, (boolean)true)), (String)"INSERT")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"UPDATE")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"DELETE")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"DROP")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"CREATE")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"ALTER")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"TRUNCATE")) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (!StringUtils.startsWithIgnoreCaseAndWs((String)noCommentSql, (String)"RENAME")) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"Statement.57"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    protected void checkNullOrEmptyQuery(String sql) throws SQLException {
        if (sql == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.59"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (sql.length() != 0) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"Statement.61"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearBatch() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.batchedArgs != null) {
            this.batchedArgs.clear();
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearWarnings() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.clearWarningsCalled = true;
        this.warningChain = null;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void close() throws SQLException {
        this.realClose((boolean)true, (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeAllOpenResults() throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return;
        }
        Object object = locallyScopedConn.getConnectionMutex();
        // MONITORENTER : object
        if (this.openResults != null) {
            for (ResultSetInternalMethods element : this.openResults) {
                try {
                    element.realClose((boolean)false);
                }
                catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                }
            }
            this.openResults.clear();
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void implicitlyCloseAllOpenResults() throws SQLException {
        this.isImplicitlyClosingResults = true;
        try {
            if (!(this.connection.getHoldResultsOpenOverStatementClose() || this.connection.getDontTrackOpenResources() || this.holdResultsOpenOverClose)) {
                if (this.results != null) {
                    this.results.realClose((boolean)false);
                }
                if (this.generatedKeysResults != null) {
                    this.generatedKeysResults.realClose((boolean)false);
                }
                this.closeAllOpenResults();
            }
            Object var2_1 = null;
            this.isImplicitlyClosingResults = false;
            return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.isImplicitlyClosingResults = false;
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOpenResultSet(ResultSetInternalMethods rs) {
        try {
            boolean hasMoreResults;
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (this.openResults != null) {
                this.openResults.remove((Object)rs);
            }
            boolean bl = hasMoreResults = rs.getNextResultSet() != null;
            if (this.results == rs && !hasMoreResults) {
                this.results = null;
            }
            if (this.generatedKeysResults == rs) {
                this.generatedKeysResults = null;
            }
            if (!this.isImplicitlyClosingResults && !hasMoreResults) {
                this.checkAndPerformCloseOnCompletionAction();
            }
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getOpenResultSetCount() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (this.openResults != null) {
                // MONITOREXIT : object
                return this.openResults.size();
            }
            // MONITOREXIT : object
            return 0;
        }
        catch (SQLException e) {
            return 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkAndPerformCloseOnCompletionAction() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (!(!this.isCloseOnCompletion() || this.connection.getDontTrackOpenResources() || this.getOpenResultSetCount() != 0 || this.results != null && this.results.reallyResult() && !this.results.isClosed() || this.generatedKeysResults != null && this.generatedKeysResults.reallyResult() && !this.generatedKeysResults.isClosed())) {
                this.realClose((boolean)false, (boolean)false);
            }
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ResultSetInternalMethods createResultSetUsingServerFetch(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        java.sql.PreparedStatement pStmt = this.connection.prepareStatement((String)sql, (int)this.resultSetType, (int)this.resultSetConcurrency);
        pStmt.setFetchSize((int)this.fetchSize);
        if (this.maxRows > -1) {
            pStmt.setMaxRows((int)this.maxRows);
        }
        this.statementBegins();
        pStmt.execute();
        ResultSetInternalMethods rs = ((StatementImpl)((Object)pStmt)).getResultSetInternal();
        rs.setStatementUsedForFetchingRows((PreparedStatement)((PreparedStatement)pStmt));
        this.results = rs;
        // MONITOREXIT : object
        return rs;
    }

    protected boolean createStreamingResultSet() {
        if (this.resultSetType != 1003) return false;
        if (this.resultSetConcurrency != 1007) return false;
        if (this.fetchSize != Integer.MIN_VALUE) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void enableStreamingResults() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.originalResultSetType = this.resultSetType;
        this.originalFetchSize = this.fetchSize;
        this.setFetchSize((int)Integer.MIN_VALUE);
        this.setResultSetType((int)1003);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void disableStreamingResults() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.fetchSize == Integer.MIN_VALUE && this.resultSetType == 1003) {
            this.setFetchSize((int)this.originalFetchSize);
            this.setResultSetType((int)this.originalResultSetType);
        }
        // MONITOREXIT : object
        return;
    }

    protected void setupStreamingTimeout(MySQLConnection con) throws SQLException {
        if (!this.createStreamingResultSet()) return;
        if (con.getNetTimeoutForStreamingResults() <= 0) return;
        this.executeSimpleNonQuery((MySQLConnection)con, (String)("SET net_write_timeout=" + con.getNetTimeoutForStreamingResults()));
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return this.executeInternal((String)sql, (boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    private boolean executeInternal(String sql, boolean returnGeneratedKeys) throws SQLException {
        MySQLConnection locallyScopedConn = this.checkClosed();
        Object object = locallyScopedConn.getConnectionMutex();
        // MONITORENTER : object
        this.checkClosed();
        this.checkNullOrEmptyQuery((String)sql);
        this.resetCancelledState();
        this.implicitlyCloseAllOpenResults();
        if (sql.charAt((int)0) == '/' && sql.startsWith((String)PING_MARKER)) {
            this.doPingInstead();
            // MONITOREXIT : object
            return true;
        }
        char firstNonWsChar = StringUtils.firstAlphaCharUc((String)sql, (int)StatementImpl.findStartOfStatement((String)sql));
        boolean maybeSelect = firstNonWsChar == 'S';
        this.retrieveGeneratedKeys = returnGeneratedKeys;
        boolean bl = this.lastQueryIsOnDupKeyUpdate = returnGeneratedKeys && firstNonWsChar == 'I' && this.containsOnDuplicateKeyInString((String)sql);
        if (!maybeSelect && locallyScopedConn.isReadOnly()) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"Statement.27") + Messages.getString((String)"Statement.28")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
        if (returnGeneratedKeys && firstNonWsChar == 'R') {
            locallyScopedConn.setReadInfoMsgEnabled((boolean)true);
        }
        try {
            this.setupStreamingTimeout((MySQLConnection)locallyScopedConn);
            if (this.doEscapeProcessing) {
                Object escapedSqlResult = EscapeProcessor.escapeSQL((String)sql, (boolean)locallyScopedConn.serverSupportsConvertFn(), (MySQLConnection)locallyScopedConn);
                sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
            }
            CachedResultSetMetaData cachedMetaData = null;
            ResultSetInternalMethods rs = null;
            this.batchedGeneratedKeys = null;
            if (this.useServerFetch()) {
                rs = this.createResultSetUsingServerFetch((String)sql);
            } else {
                TimerTask timeoutTask = null;
                String oldCatalog = null;
                try {
                    if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                        timeoutTask = new CancelTask((StatementImpl)this, (StatementImpl)this);
                        locallyScopedConn.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)this.timeoutInMillis));
                    }
                    if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
                        oldCatalog = locallyScopedConn.getCatalog();
                        locallyScopedConn.setCatalog((String)this.currentCatalog);
                    }
                    Field[] cachedFields = null;
                    if (locallyScopedConn.getCacheResultSetMetadata() && (cachedMetaData = locallyScopedConn.getCachedMetaData((String)sql)) != null) {
                        cachedFields = cachedMetaData.fields;
                    }
                    locallyScopedConn.setSessionMaxRows((int)(maybeSelect ? this.maxRows : -1));
                    this.statementBegins();
                    rs = locallyScopedConn.execSQL((StatementImpl)this, (String)sql, (int)this.maxRows, null, (int)this.resultSetType, (int)this.resultSetConcurrency, (boolean)this.createStreamingResultSet(), (String)this.currentCatalog, (Field[])cachedFields);
                    if (timeoutTask != null) {
                        if (((CancelTask)timeoutTask).caughtWhileCancelling != null) {
                            throw ((CancelTask)timeoutTask).caughtWhileCancelling;
                        }
                        timeoutTask.cancel();
                        timeoutTask = null;
                    }
                    Object object2 = this.cancelTimeoutMutex;
                    // MONITORENTER : object2
                    if (this.wasCancelled) {
                        SQLException cause = null;
                        cause = this.wasCancelledByTimeout ? new MySQLTimeoutException() : new MySQLStatementCancelledException();
                        this.resetCancelledState();
                        throw cause;
                    }
                    // MONITOREXIT : object2
                    Object var17_16 = null;
                    if (timeoutTask != null) {
                        timeoutTask.cancel();
                        locallyScopedConn.getCancelTimer().purge();
                    }
                    if (oldCatalog != null) {
                        locallyScopedConn.setCatalog((String)oldCatalog);
                    }
                }
                catch (Throwable throwable) {
                    Object var17_17 = null;
                    if (timeoutTask != null) {
                        timeoutTask.cancel();
                        locallyScopedConn.getCancelTimer().purge();
                    }
                    if (oldCatalog == null) throw throwable;
                    locallyScopedConn.setCatalog((String)oldCatalog);
                    throw throwable;
                }
            }
            if (rs != null) {
                this.lastInsertId = rs.getUpdateID();
                this.results = rs;
                rs.setFirstCharOfQuery((char)firstNonWsChar);
                if (rs.reallyResult()) {
                    if (cachedMetaData != null) {
                        locallyScopedConn.initializeResultsMetadataFromCache((String)sql, (CachedResultSetMetaData)cachedMetaData, (ResultSetInternalMethods)this.results);
                    } else if (this.connection.getCacheResultSetMetadata()) {
                        locallyScopedConn.initializeResultsMetadataFromCache((String)sql, null, (ResultSetInternalMethods)this.results);
                    }
                }
            }
            boolean timeoutTask = rs != null && rs.reallyResult();
            Object var19_19 = null;
            locallyScopedConn.setReadInfoMsgEnabled((boolean)readInfoMsgState);
            this.statementExecuting.set((boolean)false);
            return timeoutTask;
        }
        catch (Throwable throwable) {
            Object var19_20 = null;
            locallyScopedConn.setReadInfoMsgEnabled((boolean)readInfoMsgState);
            this.statementExecuting.set((boolean)false);
            throw throwable;
        }
    }

    protected void statementBegins() {
        this.clearWarningsCalled = false;
        this.statementExecuting.set((boolean)true);
        MySQLConnection physicalConn = this.connection.getMultiHostSafeProxy().getActiveMySQLConnection();
        do {
            if (physicalConn instanceof ConnectionImpl) {
                this.physicalConnection = new WeakReference<MySQLConnection>(physicalConn);
                return;
            }
            physicalConn = physicalConn.getMultiHostSafeProxy().getActiveMySQLConnection();
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void resetCancelledState() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.cancelTimeoutMutex == null) {
            // MONITOREXIT : object
            return;
        }
        Object object2 = this.cancelTimeoutMutex;
        // MONITORENTER : object2
        this.wasCancelled = false;
        this.wasCancelledByTimeout = false;
        // MONITOREXIT : object2
        return;
    }

    @Override
    public boolean execute(String sql, int returnGeneratedKeys) throws SQLException {
        boolean bl;
        if (returnGeneratedKeys == 1) {
            bl = true;
            return this.executeInternal((String)sql, (boolean)bl);
        }
        bl = false;
        return this.executeInternal((String)sql, (boolean)bl);
    }

    @Override
    public boolean execute(String sql, int[] generatedKeyIndices) throws SQLException {
        boolean bl;
        if (generatedKeyIndices != null && generatedKeyIndices.length > 0) {
            bl = true;
            return this.executeInternal((String)sql, (boolean)bl);
        }
        bl = false;
        return this.executeInternal((String)sql, (boolean)bl);
    }

    @Override
    public boolean execute(String sql, String[] generatedKeyNames) throws SQLException {
        boolean bl;
        if (generatedKeyNames != null && generatedKeyNames.length > 0) {
            bl = true;
            return this.executeInternal((String)sql, (boolean)bl);
        }
        bl = false;
        return this.executeInternal((String)sql, (boolean)bl);
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return Util.truncateAndConvertToInt((long[])this.executeBatchInternal());
    }

    /*
     * Exception decompiling
     */
    protected long[] executeBatchInternal() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 7[TRYBLOCK]
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

    protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex) {
        int vendorCode = ex.getErrorCode();
        switch (vendorCode) {
            case 1206: 
            case 1213: {
                return true;
            }
            case 1205: {
                if (this.version5013OrNewer) return false;
                return true;
            }
        }
        return false;
    }

    /*
     * Exception decompiling
     */
    private long[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[TRYBLOCK]], but top level block is 10[CATCHBLOCK]
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
    protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, long[] updateCounts) throws SQLException {
        long generatedKey;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount();
        boolean doGenKeys = this.batchedGeneratedKeys != null;
        byte[][] row = (byte[][])null;
        if (doGenKeys) {
            generatedKey = batchedStatement.getLastInsertID();
            row = new byte[][]{StringUtils.getBytes((String)Long.toString((long)generatedKey))};
            this.batchedGeneratedKeys.add((ResultSetRow)new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor()));
        }
        do {
            if (!batchedStatement.getMoreResults() && batchedStatement.getLargeUpdateCount() == -1L) {
                // MONITOREXIT : object
                return updateCountCounter;
            }
            updateCounts[updateCountCounter++] = batchedStatement.getLargeUpdateCount();
            if (!doGenKeys) continue;
            generatedKey = batchedStatement.getLastInsertID();
            row = new byte[][]{StringUtils.getBytes((String)Long.toString((long)generatedKey))};
            this.batchedGeneratedKeys.add((ResultSetRow)new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor()));
        } while (true);
    }

    protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, long[] updateCounts, SQLException ex) throws BatchUpdateException, SQLException {
        for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; --j) {
            updateCounts[j] = -3L;
        }
        if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !this.hasDeadlockOrTimeoutRolledBackTx((SQLException)ex)) {
            return ex;
        }
        long[] newUpdateCounts = new long[endOfBatchIndex];
        System.arraycopy((Object)updateCounts, (int)0, (Object)newUpdateCounts, (int)0, (int)endOfBatchIndex);
        throw SQLError.createBatchUpdateException((SQLException)ex, (long[])newUpdateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        this.retrieveGeneratedKeys = false;
        this.checkNullOrEmptyQuery((String)sql);
        this.resetCancelledState();
        this.implicitlyCloseAllOpenResults();
        if (sql.charAt((int)0) == '/' && sql.startsWith((String)PING_MARKER)) {
            this.doPingInstead();
            // MONITOREXIT : object
            return this.results;
        }
        this.setupStreamingTimeout((MySQLConnection)locallyScopedConn);
        if (this.doEscapeProcessing) {
            Object escapedSqlResult = EscapeProcessor.escapeSQL((String)sql, (boolean)locallyScopedConn.serverSupportsConvertFn(), (MySQLConnection)this.connection);
            sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
        }
        char firstStatementChar = StringUtils.firstAlphaCharUc((String)sql, (int)StatementImpl.findStartOfStatement((String)sql));
        this.checkForDml((String)sql, (char)firstStatementChar);
        CachedResultSetMetaData cachedMetaData = null;
        if (this.useServerFetch()) {
            this.results = this.createResultSetUsingServerFetch((String)sql);
            // MONITOREXIT : object
            return this.results;
        }
        TimerTask timeoutTask = null;
        String oldCatalog = null;
        try {
            if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                timeoutTask = new CancelTask((StatementImpl)this, (StatementImpl)this);
                locallyScopedConn.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)this.timeoutInMillis));
            }
            if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
                oldCatalog = locallyScopedConn.getCatalog();
                locallyScopedConn.setCatalog((String)this.currentCatalog);
            }
            Field[] cachedFields = null;
            if (locallyScopedConn.getCacheResultSetMetadata() && (cachedMetaData = locallyScopedConn.getCachedMetaData((String)sql)) != null) {
                cachedFields = cachedMetaData.fields;
            }
            locallyScopedConn.setSessionMaxRows((int)this.maxRows);
            this.statementBegins();
            this.results = locallyScopedConn.execSQL((StatementImpl)this, (String)sql, (int)this.maxRows, null, (int)this.resultSetType, (int)this.resultSetConcurrency, (boolean)this.createStreamingResultSet(), (String)this.currentCatalog, (Field[])cachedFields);
            if (timeoutTask != null) {
                if (((CancelTask)timeoutTask).caughtWhileCancelling != null) {
                    throw ((CancelTask)timeoutTask).caughtWhileCancelling;
                }
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
                timeoutTask = null;
            }
            Object object2 = this.cancelTimeoutMutex;
            // MONITORENTER : object2
            if (this.wasCancelled) {
                SQLException cause = null;
                cause = this.wasCancelledByTimeout ? new MySQLTimeoutException() : new MySQLStatementCancelledException();
                this.resetCancelledState();
                throw cause;
            }
            // MONITOREXIT : object2
            Object var13_13 = null;
            this.statementExecuting.set((boolean)false);
            if (timeoutTask != null) {
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
            }
            if (oldCatalog != null) {
                locallyScopedConn.setCatalog((String)oldCatalog);
            }
        }
        catch (Throwable throwable) {
            Object var13_14 = null;
            this.statementExecuting.set((boolean)false);
            if (timeoutTask != null) {
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
            }
            if (oldCatalog == null) throw throwable;
            locallyScopedConn.setCatalog((String)oldCatalog);
            throw throwable;
        }
        this.lastInsertId = this.results.getUpdateID();
        if (cachedMetaData != null) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)sql, (CachedResultSetMetaData)cachedMetaData, (ResultSetInternalMethods)this.results);
            return this.results;
        }
        if (this.connection.getCacheResultSetMetadata()) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)sql, null, (ResultSetInternalMethods)this.results);
        }
        // MONITOREXIT : object
        return this.results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doPingInstead() throws SQLException {
        ResultSetInternalMethods fakeSelectOneResultSet;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.pingTarget != null) {
            this.pingTarget.doPing();
        } else {
            this.connection.ping();
        }
        this.results = fakeSelectOneResultSet = this.generatePingResultSet();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Field[] fields = new Field[]{new Field(null, (String)"1", (int)-5, (int)1)};
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        byte[] colVal = new byte[]{49};
        rows.add(new ByteArrayRow((byte[][])new byte[][]{colVal}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        // MONITOREXIT : object
        return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet((Field[])fields, rows, (MySQLConnection)this.connection);
    }

    protected void executeSimpleNonQuery(MySQLConnection c, String nonQuery) throws SQLException {
        c.execSQL((StatementImpl)this, (String)nonQuery, (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.currentCatalog, null, (boolean)false).close();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate((String)sql));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long executeUpdateInternal(String sql, boolean isBatch, boolean returnGeneratedKeys) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        this.checkNullOrEmptyQuery((String)sql);
        this.resetCancelledState();
        char firstStatementChar = StringUtils.firstAlphaCharUc((String)sql, (int)StatementImpl.findStartOfStatement((String)sql));
        this.retrieveGeneratedKeys = returnGeneratedKeys;
        this.lastQueryIsOnDupKeyUpdate = returnGeneratedKeys && firstStatementChar == 'I' && this.containsOnDuplicateKeyInString((String)sql);
        ResultSetInternalMethods rs = null;
        if (this.doEscapeProcessing) {
            Object escapedSqlResult = EscapeProcessor.escapeSQL((String)sql, (boolean)this.connection.serverSupportsConvertFn(), (MySQLConnection)this.connection);
            sql = escapedSqlResult instanceof String ? (String)escapedSqlResult : ((EscapeProcessorResult)escapedSqlResult).escapedSql;
        }
        if (locallyScopedConn.isReadOnly((boolean)false)) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"Statement.42") + Messages.getString((String)"Statement.43")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"select")) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.46"), (String)"01S03", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.implicitlyCloseAllOpenResults();
        TimerTask timeoutTask = null;
        String oldCatalog = null;
        boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
        if (returnGeneratedKeys && firstStatementChar == 'R') {
            locallyScopedConn.setReadInfoMsgEnabled((boolean)true);
        }
        try {
            if (locallyScopedConn.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConn.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                timeoutTask = new CancelTask((StatementImpl)this, (StatementImpl)this);
                locallyScopedConn.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)this.timeoutInMillis));
            }
            if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
                oldCatalog = locallyScopedConn.getCatalog();
                locallyScopedConn.setCatalog((String)this.currentCatalog);
            }
            locallyScopedConn.setSessionMaxRows((int)-1);
            this.statementBegins();
            rs = locallyScopedConn.execSQL((StatementImpl)this, (String)sql, (int)-1, null, (int)1003, (int)1007, (boolean)false, (String)this.currentCatalog, null, (boolean)isBatch);
            if (timeoutTask != null) {
                if (((CancelTask)timeoutTask).caughtWhileCancelling != null) {
                    throw ((CancelTask)timeoutTask).caughtWhileCancelling;
                }
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
                timeoutTask = null;
            }
            Object object2 = this.cancelTimeoutMutex;
            // MONITORENTER : object2
            if (this.wasCancelled) {
                SQLException cause = null;
                cause = this.wasCancelledByTimeout ? new MySQLTimeoutException() : new MySQLStatementCancelledException();
                this.resetCancelledState();
                throw cause;
            }
            // MONITOREXIT : object2
            Object var15_14 = null;
            locallyScopedConn.setReadInfoMsgEnabled((boolean)readInfoMsgState);
            if (timeoutTask != null) {
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
            }
            if (oldCatalog != null) {
                locallyScopedConn.setCatalog((String)oldCatalog);
            }
            if (!isBatch) {
                this.statementExecuting.set((boolean)false);
            }
        }
        catch (Throwable throwable) {
            Object var15_15 = null;
            locallyScopedConn.setReadInfoMsgEnabled((boolean)readInfoMsgState);
            if (timeoutTask != null) {
                timeoutTask.cancel();
                locallyScopedConn.getCancelTimer().purge();
            }
            if (oldCatalog != null) {
                locallyScopedConn.setCatalog((String)oldCatalog);
            }
            if (isBatch) throw throwable;
            this.statementExecuting.set((boolean)false);
            throw throwable;
        }
        this.results = rs;
        rs.setFirstCharOfQuery((char)firstStatementChar);
        this.updateCount = rs.getUpdateCount();
        this.lastInsertId = rs.getUpdateID();
        // MONITOREXIT : object
        return this.updateCount;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate((String)sql, (int)autoGeneratedKeys));
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate((String)sql, (int[])columnIndexes));
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate((String)sql, (String[])columnNames));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Calendar getCalendarInstanceForSessionOrNew() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.connection != null) {
            // MONITOREXIT : object
            return this.connection.getCalendarInstanceForSessionOrNew();
        }
        // MONITOREXIT : object
        return new GregorianCalendar();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.Connection getConnection() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.connection;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 1000;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getFetchSize() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.fetchSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.retrieveGeneratedKeys) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.GeneratedKeysNotRequested"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.batchedGeneratedKeys != null) {
            Field[] fields = new Field[]{new Field((String)"", (String)"GENERATED_KEY", (int)-5, (int)20)};
            fields[0].setConnection((MySQLConnection)this.connection);
            this.generatedKeysResults = ResultSetImpl.getInstance((String)this.currentCatalog, (Field[])fields, (RowData)new RowDataStatic(this.batchedGeneratedKeys), (MySQLConnection)this.connection, (StatementImpl)this, (boolean)false);
            // MONITOREXIT : object
            return this.generatedKeysResults;
        }
        if (this.lastQueryIsOnDupKeyUpdate) {
            this.generatedKeysResults = this.getGeneratedKeysInternal((long)1L);
            // MONITOREXIT : object
            return this.generatedKeysResults;
        }
        this.generatedKeysResults = this.getGeneratedKeysInternal();
        // MONITOREXIT : object
        return this.generatedKeysResults;
    }

    protected ResultSetInternalMethods getGeneratedKeysInternal() throws SQLException {
        long numKeys = this.getLargeUpdateCount();
        return this.getGeneratedKeysInternal((long)numKeys);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResultSetInternalMethods getGeneratedKeysInternal(long numKeys) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Field[] fields = new Field[]{new Field((String)"", (String)"GENERATED_KEY", (int)-5, (int)20)};
        fields[0].setConnection((MySQLConnection)this.connection);
        fields[0].setUseOldNameMetadata((boolean)true);
        ArrayList<ResultSetRow> rowSet = new ArrayList<ResultSetRow>();
        long beginAt = this.getLastInsertID();
        if (beginAt < 0L) {
            fields[0].setUnsigned();
        }
        if (this.results != null) {
            String serverInfo = this.results.getServerInfo();
            if (numKeys > 0L && this.results.getFirstCharOfQuery() == 'R' && serverInfo != null && serverInfo.length() > 0) {
                numKeys = this.getRecordCountFromInfo((String)serverInfo);
            }
            if (beginAt != 0L && numKeys > 0L) {
                int i = 0;
                while ((long)i < numKeys) {
                    byte[][] row = new byte[1][];
                    if (beginAt > 0L) {
                        row[0] = StringUtils.getBytes((String)Long.toString((long)beginAt));
                    } else {
                        byte[] asBytes = new byte[8];
                        asBytes[7] = (byte)((int)(beginAt & 255L));
                        asBytes[6] = (byte)((int)(beginAt >>> 8));
                        asBytes[5] = (byte)((int)(beginAt >>> 16));
                        asBytes[4] = (byte)((int)(beginAt >>> 24));
                        asBytes[3] = (byte)((int)(beginAt >>> 32));
                        asBytes[2] = (byte)((int)(beginAt >>> 40));
                        asBytes[1] = (byte)((int)(beginAt >>> 48));
                        asBytes[0] = (byte)((int)(beginAt >>> 56));
                        BigInteger val = new BigInteger((int)1, (byte[])asBytes);
                        row[0] = val.toString().getBytes();
                    }
                    rowSet.add((ResultSetRow)new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor()));
                    beginAt += (long)this.connection.getAutoIncrementIncrement();
                    ++i;
                }
            }
        }
        ResultSetImpl gkRs = ResultSetImpl.getInstance((String)this.currentCatalog, (Field[])fields, (RowData)new RowDataStatic(rowSet), (MySQLConnection)this.connection, (StatementImpl)this, (boolean)false);
        // MONITOREXIT : object
        return gkRs;
    }

    @Override
    public int getId() {
        return this.statementId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getLastInsertID() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            // MONITOREXIT : object
            return this.lastInsertId;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getLongUpdateCount() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (this.results == null) {
                // MONITOREXIT : object
                return -1L;
            }
            if (this.results.reallyResult()) {
                // MONITOREXIT : object
                return -1L;
            }
            // MONITOREXIT : object
            return this.updateCount;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getMaxFieldSize() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.maxFieldSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getMaxRows() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.maxRows <= 0) {
            // MONITOREXIT : object
            return 0;
        }
        // MONITOREXIT : object
        return this.maxRows;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.getMoreResults((int)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        boolean moreResults;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.results == null) {
            // MONITOREXIT : object
            return false;
        }
        boolean streamingMode = this.createStreamingResultSet();
        if (streamingMode && this.results.reallyResult()) {
            while (this.results.next()) {
            }
        }
        ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
        switch (current) {
            case 1: {
                if (this.results == null) break;
                if (!streamingMode && !this.connection.getDontTrackOpenResources()) {
                    this.results.realClose((boolean)false);
                }
                this.results.clearNextResult();
                break;
            }
            case 3: {
                if (this.results != null) {
                    if (!streamingMode && !this.connection.getDontTrackOpenResources()) {
                        this.results.realClose((boolean)false);
                    }
                    this.results.clearNextResult();
                }
                this.closeAllOpenResults();
                break;
            }
            case 2: {
                if (!this.connection.getDontTrackOpenResources()) {
                    this.openResults.add((ResultSetInternalMethods)this.results);
                }
                this.results.clearNextResult();
                break;
            }
            default: {
                throw SQLError.createSQLException((String)Messages.getString((String)"Statement.19"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        this.results = nextResultSet;
        if (this.results == null) {
            this.updateCount = -1L;
            this.lastInsertId = -1L;
        } else if (this.results.reallyResult()) {
            this.updateCount = -1L;
            this.lastInsertId = -1L;
        } else {
            this.updateCount = this.results.getUpdateCount();
            this.lastInsertId = this.results.getUpdateID();
        }
        boolean bl = moreResults = this.results != null && this.results.reallyResult();
        if (!moreResults) {
            this.checkAndPerformCloseOnCompletionAction();
        }
        // MONITOREXIT : object
        return moreResults;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getQueryTimeout() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.timeoutInMillis / 1000;
    }

    private long getRecordCountFromInfo(String serverInfo) {
        int i;
        StringBuilder recordsBuf = new StringBuilder();
        long recordsCount = 0L;
        long duplicatesCount = 0L;
        char c = '\u0000';
        int length = serverInfo.length();
        for (i = 0; i < length && !Character.isDigit((char)(c = serverInfo.charAt((int)i))); ++i) {
        }
        recordsBuf.append((char)c);
        ++i;
        while (i < length && Character.isDigit((char)(c = serverInfo.charAt((int)i)))) {
            recordsBuf.append((char)c);
            ++i;
        }
        recordsCount = Long.parseLong((String)recordsBuf.toString());
        StringBuilder duplicatesBuf = new StringBuilder();
        while (i < length && !Character.isDigit((char)(c = serverInfo.charAt((int)i)))) {
            ++i;
        }
        duplicatesBuf.append((char)c);
        ++i;
        while (i < length && Character.isDigit((char)(c = serverInfo.charAt((int)i)))) {
            duplicatesBuf.append((char)c);
            ++i;
        }
        duplicatesCount = Long.parseLong((String)duplicatesBuf.toString());
        return recordsCount - duplicatesCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getResultSet() throws SQLException {
        ResultSetInternalMethods resultSetInternalMethods;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.results != null && this.results.reallyResult()) {
            resultSetInternalMethods = this.results;
            return resultSetInternalMethods;
        }
        resultSetInternalMethods = null;
        // MONITOREXIT : object
        return resultSetInternalMethods;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getResultSetConcurrency() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.resultSetConcurrency;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResultSetInternalMethods getResultSetInternal() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            // MONITOREXIT : object
            return this.results;
        }
        catch (SQLException e) {
            return this.results;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getResultSetType() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.resultSetType;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return Util.truncateAndConvertToInt((long)this.getLargeUpdateCount());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.clearWarningsCalled) {
            // MONITOREXIT : object
            return null;
        }
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            // MONITOREXIT : object
            return this.warningChain;
        }
        SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings((Connection)this.connection);
        if (this.warningChain != null) {
            this.warningChain.setNextWarning((SQLWarning)pendingWarningsFromServer);
            return this.warningChain;
        }
        this.warningChain = pendingWarningsFromServer;
        // MONITOREXIT : object
        return this.warningChain;
    }

    protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) return;
        if (this.isClosed) {
            return;
        }
        if (!locallyScopedConn.getDontTrackOpenResources()) {
            locallyScopedConn.unregisterStatement((Statement)this);
        }
        if (this.useUsageAdvisor && !calledExplicitly) {
            this.connection.getProfilerEventHandlerInstance().processEvent((byte)0, (MySQLConnection)this.connection, (Statement)this, null, (long)0L, (Throwable)new Throwable(), (String)Messages.getString((String)"Statement.63"));
        }
        if (closeOpenResults) {
            boolean bl = closeOpenResults = !this.holdResultsOpenOverClose && !this.connection.getDontTrackOpenResources();
        }
        if (closeOpenResults) {
            if (this.results != null) {
                try {
                    this.results.close();
                }
                catch (Exception ex) {
                    // empty catch block
                }
            }
            if (this.generatedKeysResults != null) {
                try {
                    this.generatedKeysResults.close();
                }
                catch (Exception ex) {
                    // empty catch block
                }
            }
            this.closeAllOpenResults();
        }
        this.isClosed = true;
        this.results = null;
        this.generatedKeysResults = null;
        this.connection = null;
        this.warningChain = null;
        this.openResults = null;
        this.batchedGeneratedKeys = null;
        this.localInfileInputStream = null;
        this.pingTarget = null;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.doEscapeProcessing = enable;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        switch (direction) {
            case 1000: 
            case 1001: 
            case 1002: {
                return;
            }
        }
        throw SQLError.createSQLException((String)Messages.getString((String)"Statement.5"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (rows < 0) {
            if (rows != Integer.MIN_VALUE) throw SQLError.createSQLException((String)Messages.getString((String)"Statement.7"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.maxRows > 0 && rows > this.getMaxRows()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.7"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.fetchSize = rows;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.holdResultsOpenOverClose = holdResultsOpenOverClose;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        int maxBuf;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (max < 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.11"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        int n = maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
        if (max > maxBuf) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.13", (Object[])new Object[]{Long.valueOf((long)((long)maxBuf))}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.maxFieldSize = max;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.setLargeMaxRows((long)((long)max));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (seconds < 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Statement.21"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.timeoutInMillis = seconds * 1000;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setResultSetConcurrency(int concurrencyFlag) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.resultSetConcurrency = concurrencyFlag;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setResultSetType(int typeFlag) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.resultSetType = typeFlag;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void getBatchedGeneratedKeys(java.sql.Statement batchedStatement) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.retrieveGeneratedKeys) {
            ResultSet rs = null;
            try {
                rs = batchedStatement.getGeneratedKeys();
                while (rs.next()) {
                    this.batchedGeneratedKeys.add((ResultSetRow)new ByteArrayRow((byte[][])new byte[][]{rs.getBytes((int)1)}, (ExceptionInterceptor)this.getExceptionInterceptor()));
                }
                Object var5_4 = null;
                if (rs == null) return;
                {
                    rs.close();
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var5_5 = null;
                if (rs == null) throw throwable;
                rs.close();
                throw throwable;
            }
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException {
        var2_2 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var2_2
        if (!this.retrieveGeneratedKeys) {
            // MONITOREXIT : var2_2
            return;
        }
        rs = null;
        try {
            rs = maxKeys == 0 ? this.getGeneratedKeysInternal() : this.getGeneratedKeysInternal((long)((long)maxKeys));
            while (rs.next()) {
                this.batchedGeneratedKeys.add((ResultSetRow)new ByteArrayRow((byte[][])new byte[][]{rs.getBytes((int)1)}, (ExceptionInterceptor)this.getExceptionInterceptor()));
            }
            var5_4 = null;
            this.isImplicitlyClosingResults = true;
            try {
                if (rs != null) {
                    rs.close();
                }
                v0 = null;
            }
            catch (Throwable var6_6) {
                v0 = null;
            }
            var7_8 = v0;
            this.isImplicitlyClosingResults = false;
            return;
        }
        catch (Throwable var4_10) {
            block12 : {
                var5_5 = null;
                this.isImplicitlyClosingResults = true;
                ** try [egrp 2[TRYBLOCK] [2 : 106->122)] { 
lbl31: // 1 sources:
                if (rs != null) {
                    rs.close();
                }
                v1 = null;
                break block12;
lbl35: // 1 sources:
                catch (Throwable var6_7) {
                    v1 = null;
                }
            }
            var7_9 = v1;
            this.isImplicitlyClosingResults = false;
            throw var4_10;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean useServerFetch() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.connection.isCursorFetchEnabled() && this.fetchSize > 0 && this.resultSetConcurrency == 1007 && this.resultSetType == 1003) {
            return true;
        }
        boolean bl = false;
        // MONITOREXIT : object
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isClosed() throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return true;
        }
        Object object = locallyScopedConn.getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.isClosed;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.isPoolable;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.isPoolable = poolable;
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

    protected static int findStartOfStatement(String sql) {
        int statementStartPos = 0;
        if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"/*")) {
            statementStartPos = sql.indexOf((String)"*/");
            if (statementStartPos == -1) {
                return 0;
            }
            return statementStartPos += 2;
        }
        if (!StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"--")) {
            if (!StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"#")) return statementStartPos;
        }
        if ((statementStartPos = sql.indexOf((int)10)) != -1) return statementStartPos;
        statementStartPos = sql.indexOf((int)13);
        if (statementStartPos != -1) return statementStartPos;
        return 0;
    }

    @Override
    public InputStream getLocalInfileInputStream() {
        return this.localInfileInputStream;
    }

    @Override
    public void setLocalInfileInputStream(InputStream stream) {
        this.localInfileInputStream = stream;
    }

    @Override
    public void setPingTarget(PingTarget pingTarget) {
        this.pingTarget = pingTarget;
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    protected boolean containsOnDuplicateKeyInString(String sql) {
        if (StatementImpl.getOnDuplicateKeyLocation((String)sql, (boolean)this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), (boolean)this.connection.getRewriteBatchedStatements(), (boolean)this.connection.isNoBackslashEscapesSet()) == -1) return false;
        return true;
    }

    protected static int getOnDuplicateKeyLocation(String sql, boolean dontCheckOnDuplicateKeyUpdateInSQL, boolean rewriteBatchedStatements, boolean noBackslashEscapes) {
        if (dontCheckOnDuplicateKeyUpdateInSQL && !rewriteBatchedStatements) {
            return -1;
        }
        int n = StringUtils.indexOfIgnoreCase((int)0, (String)sql, (String[])ON_DUPLICATE_KEY_UPDATE_CLAUSE, (String)"\"'`", (String)"\"'`", noBackslashEscapes ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void closeOnCompletion() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.closeOnCompletion = true;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.closeOnCompletion;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return this.executeBatchInternal();
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)false);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        boolean bl;
        if (autoGeneratedKeys == 1) {
            bl = true;
            return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
        }
        bl = false;
        return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        boolean bl;
        if (columnIndexes != null && columnIndexes.length > 0) {
            bl = true;
            return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
        }
        bl = false;
        return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        boolean bl;
        if (columnNames != null && columnNames.length > 0) {
            bl = true;
            return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
        }
        bl = false;
        return this.executeUpdateInternal((String)sql, (boolean)false, (boolean)bl);
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return (long)this.getMaxRows();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getLargeUpdateCount() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.results == null) {
            // MONITOREXIT : object
            return -1L;
        }
        if (this.results.reallyResult()) {
            // MONITOREXIT : object
            return -1L;
        }
        // MONITOREXIT : object
        return this.results.getUpdateCount();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (max > 50000000L) throw SQLError.createSQLException((String)(Messages.getString((String)"Statement.15") + max + " > " + 50000000 + "."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (max < 0L) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"Statement.15") + max + " > " + 50000000 + "."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (max == 0L) {
            max = -1L;
        }
        this.maxRows = (int)max;
        // MONITOREXIT : object
        return;
    }

    boolean isCursorRequired() throws SQLException {
        return false;
    }
}

