/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.MysqlParameterMetadata;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ServerPreparedStatement;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.Wrapper;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerPreparedStatement
extends PreparedStatement {
    private static final Constructor<?> JDBC_4_SPS_CTOR;
    protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
    private boolean hasOnDuplicateKeyUpdate = false;
    private boolean detectedLongParameterSwitch = false;
    private int fieldCount;
    private boolean invalid = false;
    private SQLException invalidationException;
    private Buffer outByteBuffer;
    private BindValue[] parameterBindings;
    private Field[] parameterFields;
    private Field[] resultFields;
    private boolean sendTypesToServer = false;
    private long serverStatementId;
    private int stringTypeCode = 254;
    private boolean serverNeedsResetBeforeEachExecution;
    protected boolean isCached = false;
    private boolean useAutoSlowLog;
    private Calendar serverTzCalendar;
    private Calendar defaultTzCalendar;
    private boolean hasCheckedRewrite = false;
    private boolean canRewrite = false;
    private int locationOfOnDuplicateKeyUpdate = -2;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeTime(Buffer intoBuf, Time tm) throws SQLException {
        Calendar sessionCalendar;
        intoBuf.ensureCapacity((int)9);
        intoBuf.writeByte((byte)8);
        intoBuf.writeByte((byte)0);
        intoBuf.writeLong((long)0L);
        Calendar calendar = sessionCalendar = this.getCalendarInstanceForSessionOrNew();
        // MONITORENTER : calendar
        java.util.Date oldTime = sessionCalendar.getTime();
        try {
            sessionCalendar.setTime((java.util.Date)tm);
            intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)11)));
            intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)12)));
            intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)13)));
            Object var7_6 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            return;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            throw throwable;
        }
    }

    protected static ServerPreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (!Util.isJdbc4()) {
            return new ServerPreparedStatement((MySQLConnection)conn, (String)sql, (String)catalog, (int)resultSetType, (int)resultSetConcurrency);
        }
        try {
            return (ServerPreparedStatement)JDBC_4_SPS_CTOR.newInstance((Object[])new Object[]{conn, sql, catalog, Integer.valueOf((int)resultSetType), Integer.valueOf((int)resultSetConcurrency)});
        }
        catch (IllegalArgumentException e) {
            throw new SQLException((String)e.toString(), (String)"S1000");
        }
        catch (InstantiationException e) {
            throw new SQLException((String)e.toString(), (String)"S1000");
        }
        catch (IllegalAccessException e) {
            throw new SQLException((String)e.toString(), (String)"S1000");
        }
        catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (!(target instanceof SQLException)) throw new SQLException((String)target.toString(), (String)"S1000");
            throw (SQLException)target;
        }
    }

    protected ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        super((MySQLConnection)conn, (String)catalog);
        this.checkNullOrEmptyQuery((String)sql);
        int startOfStatement = ServerPreparedStatement.findStartOfStatement((String)sql);
        this.firstCharOfStmt = StringUtils.firstAlphaCharUc((String)sql, (int)startOfStatement);
        boolean bl = this.hasOnDuplicateKeyUpdate = this.firstCharOfStmt == 'I' && this.containsOnDuplicateKeyInString((String)sql);
        this.serverNeedsResetBeforeEachExecution = this.connection.versionMeetsMinimum((int)5, (int)0, (int)0) ? !this.connection.versionMeetsMinimum((int)5, (int)0, (int)3) : !this.connection.versionMeetsMinimum((int)4, (int)1, (int)10);
        this.useAutoSlowLog = this.connection.getAutoSlowLog();
        this.useTrueBoolean = this.connection.versionMeetsMinimum((int)3, (int)21, (int)23);
        String statementComment = this.connection.getStatementComment();
        this.originalSql = statementComment == null ? sql : "/* " + statementComment + " */ " + sql;
        this.stringTypeCode = this.connection.versionMeetsMinimum((int)4, (int)1, (int)2) ? 253 : 254;
        try {
            this.serverPrepare((String)sql);
        }
        catch (SQLException sqlEx) {
            this.realClose((boolean)false, (boolean)true);
            throw sqlEx;
        }
        catch (Exception ex) {
            this.realClose((boolean)false, (boolean)true);
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        this.setResultSetType((int)resultSetType);
        this.setResultSetConcurrency((int)resultSetConcurrency);
        this.parameterTypes = new int[this.parameterCount];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addBatch() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.batchedArgs == null) {
            this.batchedArgs = new ArrayList<E>();
        }
        this.batchedArgs.add(new BatchedBindValues((BindValue[])this.parameterBindings));
        // MONITOREXIT : object
        return;
    }

    /*
     * Exception decompiling
     */
    @Override
    public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [3[TRYBLOCK]], but top level block is 14[CATCHBLOCK]
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

    @Override
    protected MySQLConnection checkClosed() throws SQLException {
        if (!this.invalid) return super.checkClosed();
        throw this.invalidationException;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearParameters() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.clearParametersInternal((boolean)true);
        // MONITOREXIT : object
        return;
    }

    private void clearParametersInternal(boolean clearServerParameters) throws SQLException {
        boolean hadLongData = false;
        if (this.parameterBindings != null) {
            for (int i = 0; i < this.parameterCount; ++i) {
                if (this.parameterBindings[i] != null && this.parameterBindings[i].isLongData) {
                    hadLongData = true;
                }
                this.parameterBindings[i].reset();
            }
        }
        if (!clearServerParameters) return;
        if (!hadLongData) return;
        this.serverResetStatement();
        this.detectedLongParameterSwitch = false;
    }

    protected void setClosed(boolean flag) {
        this.isClosed = flag;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return;
        }
        Object object = locallyScopedConn.getConnectionMutex();
        // MONITORENTER : object
        if (this.isCached && this.isPoolable() && !this.isClosed) {
            this.clearParameters();
            this.isClosed = true;
            this.connection.recachePreparedStatement((ServerPreparedStatement)this);
            // MONITOREXIT : object
            return;
        }
        this.isClosed = false;
        this.realClose((boolean)true, (boolean)true);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dumpCloseForTestcase() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder buf = new StringBuilder();
        this.connection.generateConnectionCommentBlock((StringBuilder)buf);
        buf.append((String)"DEALLOCATE PREPARE debug_stmt_");
        buf.append((int)this.statementId);
        buf.append((String)";\n");
        this.connection.dumpTestcaseQuery((String)buf.toString());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dumpExecuteForTestcase() throws SQLException {
        int i;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder buf = new StringBuilder();
        for (i = 0; i < this.parameterCount; ++i) {
            this.connection.generateConnectionCommentBlock((StringBuilder)buf);
            buf.append((String)"SET @debug_stmt_param");
            buf.append((int)this.statementId);
            buf.append((String)"_");
            buf.append((int)i);
            buf.append((String)"=");
            if (this.parameterBindings[i].isNull) {
                buf.append((String)"NULL");
            } else {
                buf.append((String)this.parameterBindings[i].toString((boolean)true));
            }
            buf.append((String)";\n");
        }
        this.connection.generateConnectionCommentBlock((StringBuilder)buf);
        buf.append((String)"EXECUTE debug_stmt_");
        buf.append((int)this.statementId);
        if (this.parameterCount > 0) {
            buf.append((String)" USING ");
            for (i = 0; i < this.parameterCount; ++i) {
                if (i > 0) {
                    buf.append((String)", ");
                }
                buf.append((String)"@debug_stmt_param");
                buf.append((int)this.statementId);
                buf.append((String)"_");
                buf.append((int)i);
            }
        }
        buf.append((String)";\n");
        this.connection.dumpTestcaseQuery((String)buf.toString());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dumpPrepareForTestcase() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder buf = new StringBuilder((int)(this.originalSql.length() + 64));
        this.connection.generateConnectionCommentBlock((StringBuilder)buf);
        buf.append((String)"PREPARE debug_stmt_");
        buf.append((int)this.statementId);
        buf.append((String)" FROM \"");
        buf.append((String)this.originalSql);
        buf.append((String)"\";\n");
        this.connection.dumpTestcaseQuery((String)buf.toString());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
        long[] nbrCommands2;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn.isReadOnly()) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.2") + Messages.getString((String)"ServerPreparedStatement.3")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.clearWarnings();
        BindValue[] oldBindValues = this.parameterBindings;
        try {
            long[] updateCounts = null;
            if (this.batchedArgs != null) {
                int nbrCommands2 = this.batchedArgs.size();
                updateCounts = new long[nbrCommands2];
                if (this.retrieveGeneratedKeys) {
                    this.batchedGeneratedKeys = new ArrayList<E>((int)nbrCommands2);
                }
                for (int i = 0; i < nbrCommands2; ++i) {
                    updateCounts[i] = -3L;
                }
                SQLException sqlEx = null;
                int commandIndex = 0;
                BindValue[] previousBindValuesForBatch = null;
                TimerTask timeoutTask = null;
                try {
                    if (locallyScopedConn.getEnableQueryTimeouts() && batchTimeout != 0 && locallyScopedConn.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                        timeoutTask = new StatementImpl.CancelTask((StatementImpl)this, (StatementImpl)this);
                        locallyScopedConn.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)batchTimeout));
                    }
                    for (commandIndex = 0; commandIndex < nbrCommands2; ++commandIndex) {
                        E arg = this.batchedArgs.get((int)commandIndex);
                        try {
                            Object var14_16;
                            if (arg instanceof String) {
                                updateCounts[commandIndex] = this.executeUpdateInternal((String)((String)arg), (boolean)true, (boolean)this.retrieveGeneratedKeys);
                                this.getBatchedGeneratedKeys((int)(this.results.getFirstCharOfQuery() == 'I' && this.containsOnDuplicateKeyInString((String)((String)arg)) ? 1 : 0));
                                continue;
                            }
                            this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
                            if (previousBindValuesForBatch != null) {
                                for (int j = 0; j < this.parameterBindings.length; ++j) {
                                    if (this.parameterBindings[j].bufferType == previousBindValuesForBatch[j].bufferType) continue;
                                    this.sendTypesToServer = true;
                                    break;
                                }
                            }
                            try {
                                updateCounts[commandIndex] = this.executeUpdateInternal((boolean)false, (boolean)true);
                                var14_16 = null;
                                previousBindValuesForBatch = this.parameterBindings;
                            }
                            catch (Throwable throwable) {
                                var14_16 = null;
                                previousBindValuesForBatch = this.parameterBindings;
                                throw throwable;
                            }
                            this.getBatchedGeneratedKeys((int)(this.containsOnDuplicateKeyUpdateInSQL() ? 1 : 0));
                            continue;
                        }
                        catch (SQLException ex) {
                            updateCounts[commandIndex] = -3L;
                            if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !this.hasDeadlockOrTimeoutRolledBackTx((SQLException)ex)) {
                                sqlEx = ex;
                                continue;
                            }
                            long[] newUpdateCounts = new long[commandIndex];
                            System.arraycopy((Object)updateCounts, (int)0, (Object)newUpdateCounts, (int)0, (int)commandIndex);
                            throw SQLError.createBatchUpdateException((SQLException)ex, (long[])newUpdateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                    }
                    Object var16_19 = null;
                    if (timeoutTask != null) {
                        timeoutTask.cancel();
                        locallyScopedConn.getCancelTimer().purge();
                    }
                    this.resetCancelledState();
                }
                catch (Throwable throwable) {
                    Object var16_20 = null;
                    if (timeoutTask != null) {
                        timeoutTask.cancel();
                        locallyScopedConn.getCancelTimer().purge();
                    }
                    this.resetCancelledState();
                    throw throwable;
                }
                if (sqlEx != null) {
                    throw SQLError.createBatchUpdateException(sqlEx, (long[])updateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            nbrCommands2 = updateCounts != null ? updateCounts : new long[0];
            Object var18_22 = null;
            this.parameterBindings = oldBindValues;
            this.sendTypesToServer = true;
        }
        catch (Throwable throwable) {
            Object var18_23 = null;
            this.parameterBindings = oldBindValues;
            this.sendTypesToServer = true;
            this.clearBatch();
            throw throwable;
        }
        this.clearBatch();
        return nbrCommands2;
    }

    @Override
    protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ++this.numberOfExecutions;
        try {
            // MONITOREXIT : object
            return this.serverExecute((int)maxRowsToRetrieve, (boolean)createStreamingResultSet, (Field[])metadataFromCache);
        }
        catch (SQLException sqlEx) {
            if (this.connection.getEnablePacketDebug()) {
                this.connection.getIO().dumpPacketRingBuffer();
            }
            if (!this.connection.getDumpQueriesOnException()) throw sqlEx;
            String extractedSql = this.toString();
            StringBuilder messageBuf = new StringBuilder((int)(extractedSql.length() + 32));
            messageBuf.append((String)"\n\nQuery being executed when exception was thrown:\n");
            messageBuf.append((String)extractedSql);
            messageBuf.append((String)"\n\n");
            sqlEx = ConnectionImpl.appendMessageToException((SQLException)sqlEx, (String)messageBuf.toString(), (ExceptionInterceptor)this.getExceptionInterceptor());
            throw sqlEx;
        }
        catch (Exception ex) {
            if (this.connection.getEnablePacketDebug()) {
                this.connection.getIO().dumpPacketRingBuffer();
            }
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            if (this.connection.getDumpQueriesOnException()) {
                String extractedSql = this.toString();
                StringBuilder messageBuf = new StringBuilder((int)(extractedSql.length() + 32));
                messageBuf.append((String)"\n\nQuery being executed when exception was thrown:\n");
                messageBuf.append((String)extractedSql);
                messageBuf.append((String)"\n\n");
                sqlEx = ConnectionImpl.appendMessageToException((SQLException)sqlEx, (String)messageBuf.toString(), (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    @Override
    protected Buffer fillSendPacket() throws SQLException {
        return null;
    }

    @Override
    protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected BindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.parameterBindings.length == 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ServerPreparedStatement.8"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (--parameterIndex < 0) throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString((String)"ServerPreparedStatement.10") + this.parameterBindings.length), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (parameterIndex >= this.parameterBindings.length) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString((String)"ServerPreparedStatement.10") + this.parameterBindings.length), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.parameterBindings[parameterIndex] == null) {
            this.parameterBindings[parameterIndex] = new BindValue();
            return this.parameterBindings[parameterIndex];
        }
        if (this.parameterBindings[parameterIndex].isLongData && !forLongData) {
            this.detectedLongParameterSwitch = true;
        }
        // MONITOREXIT : object
        return this.parameterBindings[parameterIndex];
    }

    public BindValue[] getParameterBindValues() {
        return this.parameterBindings;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] getBytes(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        BindValue bindValue = this.getBinding((int)parameterIndex, (boolean)false);
        if (bindValue.isNull) {
            // MONITOREXIT : object
            return null;
        }
        if (bindValue.isLongData) {
            throw SQLError.createSQLFeatureNotSupportedException();
        }
        if (this.outByteBuffer == null) {
            this.outByteBuffer = new Buffer((int)this.connection.getNetBufferLength());
        }
        this.outByteBuffer.clear();
        int originalPosition = this.outByteBuffer.getPosition();
        this.storeBinding((Buffer)this.outByteBuffer, (BindValue)bindValue, (MysqlIO)this.connection.getIO());
        int newPosition = this.outByteBuffer.getPosition();
        int length = newPosition - originalPosition;
        byte[] valueAsBytes = new byte[length];
        System.arraycopy((Object)this.outByteBuffer.getByteBuffer(), (int)originalPosition, (Object)valueAsBytes, (int)0, (int)length);
        // MONITOREXIT : object
        return valueAsBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.ResultSetMetaData getMetaData() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.resultFields == null) {
            // MONITOREXIT : object
            return null;
        }
        // MONITOREXIT : object
        return new ResultSetMetaData((Field[])this.resultFields, (boolean)this.connection.getUseOldAliasMetadataBehavior(), (boolean)this.connection.getYearIsDateType(), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.parameterMetaData == null) {
            this.parameterMetaData = new MysqlParameterMetadata((Field[])this.parameterFields, (int)this.parameterCount, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return this.parameterMetaData;
    }

    @Override
    boolean isNull(int paramIndex) {
        throw new IllegalArgumentException((String)Messages.getString((String)"ServerPreparedStatement.7"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return;
        }
        Object object = locallyScopedConn.getConnectionMutex();
        // MONITORENTER : object
        if (this.connection != null) {
            if (this.connection.getAutoGenerateTestcaseScript()) {
                this.dumpCloseForTestcase();
            }
            SQLException exceptionDuringClose = null;
            if (calledExplicitly && !this.connection.isClosed()) {
                Object object2 = this.connection.getConnectionMutex();
                // MONITORENTER : object2
                try {
                    MysqlIO mysql = this.connection.getIO();
                    Buffer packet = mysql.getSharedSendPacket();
                    packet.writeByte((byte)25);
                    packet.writeLong((long)this.serverStatementId);
                    mysql.sendCommand((int)25, null, (Buffer)packet, (boolean)true, null, (int)0);
                }
                catch (SQLException sqlEx) {
                    exceptionDuringClose = sqlEx;
                }
            }
            if (this.isCached) {
                this.connection.decachePreparedStatement((ServerPreparedStatement)this);
                this.isCached = false;
            }
            super.realClose((boolean)calledExplicitly, (boolean)closeOpenResults);
            this.clearParametersInternal((boolean)false);
            this.parameterBindings = null;
            this.parameterFields = null;
            this.resultFields = null;
            if (exceptionDuringClose != null) {
                throw exceptionDuringClose;
            }
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void rePrepare() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.invalidationException = null;
        try {
            this.serverPrepare((String)this.originalSql);
        }
        catch (SQLException sqlEx) {
            this.invalidationException = sqlEx;
        }
        catch (Exception ex) {
            this.invalidationException = SQLError.createSQLException((String)ex.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            this.invalidationException.initCause((Throwable)ex);
        }
        if (this.invalidationException != null) {
            this.invalid = true;
            this.parameterBindings = null;
            this.parameterFields = null;
            this.resultFields = null;
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
            try {
                this.closeAllOpenResults();
            }
            catch (Exception e) {
                // empty catch block
            }
            if (this.connection != null && !this.connection.getDontTrackOpenResources()) {
                this.connection.unregisterStatement((Statement)this);
            }
        }
        // MONITOREXIT : object
        return;
    }

    @Override
    boolean isCursorRequired() throws SQLException {
        if (this.resultFields == null) return false;
        if (!this.connection.isCursorFetchEnabled()) return false;
        if (this.getResultSetType() != 1003) return false;
        if (this.getResultSetConcurrency() != 1007) return false;
        if (this.getFetchSize() <= 0) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    private ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, Field[] metadataFromCache) throws SQLException {
        int i;
        int i2;
        ResultSetInternalMethods interceptedResults;
        ResultSetInternalMethods interceptedResults2;
        int i3;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MysqlIO mysql = this.connection.getIO();
        if (mysql.shouldIntercept() && (interceptedResults = mysql.invokeStatementInterceptorsPre((String)this.originalSql, (Statement)this, (boolean)true)) != null) {
            // MONITOREXIT : object
            return interceptedResults;
        }
        if (this.detectedLongParameterSwitch) {
            boolean firstFound = false;
            long boundTimeToCheck = 0L;
            for (i3 = 0; i3 < this.parameterCount - 1; ++i3) {
                if (!this.parameterBindings[i3].isLongData) continue;
                if (firstFound && boundTimeToCheck != this.parameterBindings[i3].boundBeforeExecutionNum) {
                    throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.11") + Messages.getString((String)"ServerPreparedStatement.12")), (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                firstFound = true;
                boundTimeToCheck = this.parameterBindings[i3].boundBeforeExecutionNum;
            }
            this.serverResetStatement();
        }
        for (i2 = 0; i2 < this.parameterCount; ++i2) {
            if (this.parameterBindings[i2].isSet) continue;
            throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.13") + (i2 + 1) + Messages.getString((String)"ServerPreparedStatement.14")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        for (i2 = 0; i2 < this.parameterCount; ++i2) {
            if (!this.parameterBindings[i2].isLongData) continue;
            this.serverLongData((int)i2, (BindValue)this.parameterBindings[i2]);
        }
        if (this.connection.getAutoGenerateTestcaseScript()) {
            this.dumpExecuteForTestcase();
        }
        Buffer packet = mysql.getSharedSendPacket();
        packet.clear();
        packet.writeByte((byte)23);
        packet.writeLong((long)this.serverStatementId);
        if (this.connection.versionMeetsMinimum((int)4, (int)1, (int)2)) {
            if (this.isCursorRequired()) {
                packet.writeByte((byte)1);
            } else {
                packet.writeByte((byte)0);
            }
            packet.writeLong((long)1L);
        }
        int nullCount = (this.parameterCount + 7) / 8;
        int nullBitsPosition = packet.getPosition();
        for (i3 = 0; i3 < nullCount; ++i3) {
            packet.writeByte((byte)0);
        }
        byte[] nullBitsBuffer = new byte[nullCount];
        packet.writeByte((byte)(this.sendTypesToServer ? (byte)1 : 0));
        if (this.sendTypesToServer) {
            for (i = 0; i < this.parameterCount; ++i) {
                packet.writeInt((int)this.parameterBindings[i].bufferType);
            }
        }
        for (i = 0; i < this.parameterCount; ++i) {
            if (this.parameterBindings[i].isLongData) continue;
            if (!this.parameterBindings[i].isNull) {
                this.storeBinding((Buffer)packet, (BindValue)this.parameterBindings[i], (MysqlIO)mysql);
                continue;
            }
            byte[] arrby = nullBitsBuffer;
            int n = i / 8;
            arrby[n] = (byte)(arrby[n] | 1 << (i & 7));
        }
        int endPosition = packet.getPosition();
        packet.setPosition((int)nullBitsPosition);
        packet.writeBytesNoNull((byte[])nullBitsBuffer);
        packet.setPosition((int)endPosition);
        boolean logSlowQueries = this.connection.getLogSlowQueries();
        boolean gatherPerformanceMetrics = this.connection.getGatherPerformanceMetrics();
        boolean countDuration = this.profileSQL || logSlowQueries || gatherPerformanceMetrics;
        long begin = countDuration ? mysql.getCurrentTimeNanosOrMillis() : 0L;
        this.resetCancelledState();
        TimerTask timeoutTask = null;
        try {
            long queryEndTime;
            String queryAsString;
            String string = queryAsString = countDuration ? this.asSql((boolean)true) : "";
            if (this.connection.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && this.connection.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                timeoutTask = new StatementImpl.CancelTask((StatementImpl)this, (StatementImpl)this);
                this.connection.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)this.timeoutInMillis));
            }
            this.statementBegins();
            Buffer resultPacket = mysql.sendCommand((int)23, null, (Buffer)packet, (boolean)false, null, (int)0);
            long l = queryEndTime = countDuration ? (queryEndTime = mysql.getCurrentTimeNanosOrMillis()) : 0L;
            if (timeoutTask != null) {
                timeoutTask.cancel();
                this.connection.getCancelTimer().purge();
                if (((StatementImpl.CancelTask)timeoutTask).caughtWhileCancelling != null) {
                    throw ((StatementImpl.CancelTask)timeoutTask).caughtWhileCancelling;
                }
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
            long elapsedTime = countDuration ? queryEndTime - begin : 0L;
            boolean queryWasSlow = false;
            if (logSlowQueries) {
                boolean bl = this.useAutoSlowLog ? this.connection.isAbonormallyLongQuery((long)elapsedTime) : (queryWasSlow = elapsedTime > (long)this.connection.getSlowQueryThresholdMillis());
                if (queryWasSlow) {
                    this.connection.getProfilerEventHandlerInstance().processEvent((byte)6, (MySQLConnection)this.connection, (Statement)this, null, (long)elapsedTime, (Throwable)new Throwable(), (String)Messages.getString((String)"ServerPreparedStatement.15", (Object[])new String[]{String.valueOf((long)mysql.getSlowQueryThreshold()), String.valueOf((long)elapsedTime), this.originalSql, queryAsString}));
                }
            }
            if (gatherPerformanceMetrics) {
                this.connection.registerQueryExecutionTime((long)elapsedTime);
                this.connection.incrementNumberOfPreparedExecutes();
            }
            if (this.profileSQL) {
                this.connection.getProfilerEventHandlerInstance().processEvent((byte)4, (MySQLConnection)this.connection, (Statement)this, null, (long)(mysql.getCurrentTimeNanosOrMillis() - begin), (Throwable)new Throwable(), (String)this.truncateQueryToLog((String)queryAsString));
            }
            ResultSetInternalMethods rs = mysql.readAllResults((StatementImpl)this, (int)maxRowsToRetrieve, (int)this.resultSetType, (int)this.resultSetConcurrency, (boolean)createStreamingResultSet, (String)this.currentCatalog, (Buffer)resultPacket, (boolean)true, (long)((long)this.fieldCount), (Field[])metadataFromCache);
            if (mysql.shouldIntercept() && (interceptedResults2 = mysql.invokeStatementInterceptorsPost((String)this.originalSql, (Statement)this, (ResultSetInternalMethods)rs, (boolean)true, null)) != null) {
                rs = interceptedResults2;
            }
            if (this.profileSQL) {
                this.connection.getProfilerEventHandlerInstance().processEvent((byte)5, (MySQLConnection)this.connection, (Statement)this, null, (long)(mysql.getCurrentTimeNanosOrMillis() - queryEndTime), (Throwable)new Throwable(), null);
            }
            if (queryWasSlow && this.connection.getExplainSlowQueries()) {
                mysql.explainSlowQuery((byte[])StringUtils.getBytes((String)queryAsString), (String)queryAsString);
            }
            if (!createStreamingResultSet && this.serverNeedsResetBeforeEachExecution) {
                this.serverResetStatement();
            }
            this.sendTypesToServer = false;
            this.results = rs;
            if (mysql.hadWarnings()) {
                mysql.scanForAndThrowDataTruncation();
            }
            interceptedResults2 = rs;
            Object var27_31 = null;
            this.statementExecuting.set((boolean)false);
            if (timeoutTask == null) return interceptedResults2;
        }
        catch (SQLException sqlEx) {
            try {
                if (!mysql.shouldIntercept()) throw sqlEx;
                mysql.invokeStatementInterceptorsPost((String)this.originalSql, (Statement)this, null, (boolean)true, (SQLException)sqlEx);
                throw sqlEx;
            }
            catch (Throwable throwable) {
                Object var27_32 = null;
                this.statementExecuting.set((boolean)false);
                if (timeoutTask == null) throw throwable;
                timeoutTask.cancel();
                this.connection.getCancelTimer().purge();
                throw throwable;
            }
        }
        timeoutTask.cancel();
        this.connection.getCancelTimer().purge();
        return interceptedResults2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serverLongData(int parameterIndex, BindValue longData) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MysqlIO mysql = this.connection.getIO();
        Buffer packet = mysql.getSharedSendPacket();
        Object value = longData.value;
        if (value instanceof byte[]) {
            packet.clear();
            packet.writeByte((byte)24);
            packet.writeLong((long)this.serverStatementId);
            packet.writeInt((int)parameterIndex);
            packet.writeBytesNoNull((byte[])((byte[])longData.value));
            mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
            return;
        }
        if (value instanceof InputStream) {
            this.storeStream((MysqlIO)mysql, (int)parameterIndex, (Buffer)packet, (InputStream)((InputStream)value));
            return;
        }
        if (value instanceof Blob) {
            this.storeStream((MysqlIO)mysql, (int)parameterIndex, (Buffer)packet, (InputStream)((Blob)value).getBinaryStream());
            return;
        }
        if (!(value instanceof Reader)) throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.18") + value.getClass().getName() + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        this.storeReader((MysqlIO)mysql, (int)parameterIndex, (Buffer)packet, (Reader)((Reader)value));
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serverPrepare(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MysqlIO mysql = this.connection.getIO();
        if (this.connection.getAutoGenerateTestcaseScript()) {
            this.dumpPrepareForTestcase();
        }
        try {
            block17 : {
                try {
                    boolean checkEOF;
                    int i;
                    long begin;
                    begin = this.connection.getProfileSql() ? (begin = System.currentTimeMillis()) : 0L;
                    this.isLoadDataQuery = StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"LOAD DATA");
                    String characterEncoding = null;
                    String connectionEncoding = this.connection.getEncoding();
                    if (!this.isLoadDataQuery && this.connection.getUseUnicode() && connectionEncoding != null) {
                        characterEncoding = connectionEncoding;
                    }
                    Buffer prepareResultPacket = mysql.sendCommand((int)22, (String)sql, null, (boolean)false, (String)characterEncoding, (int)0);
                    if (this.connection.versionMeetsMinimum((int)4, (int)1, (int)1)) {
                        prepareResultPacket.setPosition((int)1);
                    } else {
                        prepareResultPacket.setPosition((int)0);
                    }
                    this.serverStatementId = prepareResultPacket.readLong();
                    this.fieldCount = prepareResultPacket.readInt();
                    this.parameterCount = prepareResultPacket.readInt();
                    this.parameterBindings = new BindValue[this.parameterCount];
                    for (int i2 = 0; i2 < this.parameterCount; ++i2) {
                        this.parameterBindings[i2] = new BindValue();
                    }
                    this.connection.incrementNumberOfPrepares();
                    if (this.profileSQL) {
                        this.connection.getProfilerEventHandlerInstance().processEvent((byte)2, (MySQLConnection)this.connection, (Statement)this, null, (long)(mysql.getCurrentTimeNanosOrMillis() - begin), (Throwable)new Throwable(), (String)this.truncateQueryToLog((String)sql));
                    }
                    boolean bl = checkEOF = !mysql.isEOFDeprecated();
                    if (this.parameterCount > 0 && this.connection.versionMeetsMinimum((int)4, (int)1, (int)2) && !mysql.isVersion((int)5, (int)0, (int)0)) {
                        this.parameterFields = new Field[this.parameterCount];
                        for (i = 0; i < this.parameterCount; ++i) {
                            Buffer metaDataPacket = mysql.readPacket();
                            this.parameterFields[i] = mysql.unpackField((Buffer)metaDataPacket, (boolean)false);
                        }
                        if (checkEOF) {
                            mysql.readPacket();
                        }
                    }
                    if (this.fieldCount <= 0) break block17;
                    this.resultFields = new Field[this.fieldCount];
                    for (i = 0; i < this.fieldCount; ++i) {
                        Buffer fieldPacket = mysql.readPacket();
                        this.resultFields[i] = mysql.unpackField((Buffer)fieldPacket, (boolean)false);
                    }
                    if (!checkEOF) break block17;
                    mysql.readPacket();
                }
                catch (SQLException sqlEx) {
                    if (!this.connection.getDumpQueriesOnException()) throw sqlEx;
                    StringBuilder messageBuf = new StringBuilder((int)(this.originalSql.length() + 32));
                    messageBuf.append((String)"\n\nQuery being prepared when exception was thrown:\n\n");
                    messageBuf.append((String)this.originalSql);
                    sqlEx = ConnectionImpl.appendMessageToException((SQLException)sqlEx, (String)messageBuf.toString(), (ExceptionInterceptor)this.getExceptionInterceptor());
                    throw sqlEx;
                }
            }
            Object var13_13 = null;
            this.connection.getIO().clearInputStream();
            return;
        }
        catch (Throwable throwable) {
            Object var13_14 = null;
            this.connection.getIO().clearInputStream();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String truncateQueryToLog(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        String query = null;
        if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
            StringBuilder queryBuf = new StringBuilder((int)(this.connection.getMaxQuerySizeToLog() + 12));
            queryBuf.append((String)sql.substring((int)0, (int)this.connection.getMaxQuerySizeToLog()));
            queryBuf.append((String)Messages.getString((String)"MysqlIO.25"));
            return queryBuf.toString();
        }
        query = sql;
        // MONITOREXIT : object
        return query;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serverResetStatement() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MysqlIO mysql = this.connection.getIO();
        Buffer packet = mysql.getSharedSendPacket();
        packet.clear();
        packet.writeByte((byte)26);
        packet.writeLong((long)this.serverStatementId);
        try {
            try {
                mysql.sendCommand((int)26, null, (Buffer)packet, (boolean)(!this.connection.versionMeetsMinimum((int)4, (int)1, (int)2)), null, (int)0);
            }
            catch (SQLException sqlEx) {
                throw sqlEx;
            }
            catch (Exception ex) {
                SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                sqlEx.initCause((Throwable)ex);
                throw sqlEx;
            }
            Object var7_4 = null;
            mysql.clearInputStream();
            return;
        }
        catch (Throwable throwable) {
            Object var7_5 = null;
            mysql.clearInputStream();
            throw throwable;
        }
    }

    @Override
    public void setArray(int i, Array x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((BindValue)binding, (int)252);
        binding.value = x;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = (long)length;
            return;
        }
        binding.bindLength = -1L;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)3);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        if (this.connection.versionMeetsMinimum((int)5, (int)0, (int)3)) {
            this.resetToType((BindValue)binding, (int)246);
        } else {
            this.resetToType((BindValue)binding, (int)this.stringTypeCode);
        }
        binding.value = StringUtils.fixDecimalExponent((String)StringUtils.consistentToString((BigDecimal)x));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((BindValue)binding, (int)252);
        binding.value = x;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = (long)length;
            return;
        }
        binding.bindLength = -1L;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((BindValue)binding, (int)252);
        binding.value = x;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = x.length();
            return;
        }
        binding.bindLength = -1L;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.setByte((int)parameterIndex, (byte)(x ? (byte)1 : 0));
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)1);
        binding.longBinding = (long)x;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.checkClosed();
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)253);
        binding.value = x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (reader == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((BindValue)binding, (int)252);
        binding.value = reader;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = (long)length;
            return;
        }
        binding.bindLength = -1L;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((BindValue)binding, (int)252);
        binding.value = x.getCharacterStream();
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = x.length();
            return;
        }
        binding.bindLength = -1L;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.setDate((int)parameterIndex, (Date)x, null);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)91);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)10);
        binding.value = x;
        if (cal == null) return;
        binding.calendar = (Calendar)cal.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.connection.getAllowNanAndInf()) {
            if (x == Double.POSITIVE_INFINITY) throw SQLError.createSQLException((String)("'" + x + "' is not a valid numeric or approximate numeric value"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            if (x == Double.NEGATIVE_INFINITY) throw SQLError.createSQLException((String)("'" + x + "' is not a valid numeric or approximate numeric value"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            if (Double.isNaN((double)x)) {
                throw SQLError.createSQLException((String)("'" + x + "' is not a valid numeric or approximate numeric value"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)5);
        binding.doubleBinding = x;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)4);
        binding.floatBinding = x;
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)3);
        binding.longBinding = (long)x;
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)8);
        binding.longBinding = x;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)6);
        binding.isNull = true;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)6);
        binding.isNull = true;
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.checkClosed();
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)2);
        binding.longBinding = (long)x;
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.checkClosed();
        if (x == null) {
            this.setNull((int)parameterIndex, (int)1);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)this.stringTypeCode);
        binding.value = x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.setTimeInternal((int)parameterIndex, (Time)x, null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.setTimeInternal((int)parameterIndex, (Time)x, (Calendar)cal, (TimeZone)cal.getTimeZone(), (boolean)true);
        // MONITOREXIT : object
        return;
    }

    private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)92);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)11);
        if (!this.useLegacyDatetimeCode) {
            binding.value = x;
            if (targetCalendar == null) return;
            binding.calendar = (Calendar)targetCalendar.clone();
            return;
        }
        Calendar sessionCalendar = this.getCalendarInstanceForSessionOrNew();
        binding.value = TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)x, (TimeZone)tz, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)rollForward);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.setTimestampInternal((int)parameterIndex, (Timestamp)x, null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.setTimestampInternal((int)parameterIndex, (Timestamp)x, (Calendar)cal, (TimeZone)cal.getTimeZone(), (boolean)true);
        // MONITOREXIT : object
        return;
    }

    private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)93);
            return;
        }
        BindValue binding = this.getBinding((int)parameterIndex, (boolean)false);
        this.resetToType((BindValue)binding, (int)12);
        if (!this.sendFractionalSeconds) {
            x = TimeUtil.truncateFractionalSeconds((Timestamp)x);
        }
        if (!this.useLegacyDatetimeCode) {
            binding.value = x;
        } else {
            Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew();
            sessionCalendar = TimeUtil.setProlepticIfNeeded((Calendar)sessionCalendar, (Calendar)targetCalendar);
            binding.value = TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)x, (TimeZone)tz, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)rollForward);
        }
        if (targetCalendar == null) return;
        binding.calendar = (Calendar)targetCalendar.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void resetToType(BindValue oldValue, int bufferType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        oldValue.reset();
        if ((bufferType != 6 || oldValue.bufferType == 0) && oldValue.bufferType != bufferType) {
            this.sendTypesToServer = true;
            oldValue.bufferType = bufferType;
        }
        oldValue.isSet = true;
        oldValue.boundBeforeExecutionNum = (long)this.numberOfExecutions;
        // MONITOREXIT : object
        return;
    }

    @Deprecated
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.checkClosed();
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.checkClosed();
        this.setString((int)parameterIndex, (String)x.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            Object value = bindValue.value;
            switch (bindValue.bufferType) {
                case 1: {
                    packet.writeByte((byte)((byte)((int)bindValue.longBinding)));
                    // MONITOREXIT : object
                    return;
                }
                case 2: {
                    packet.ensureCapacity((int)2);
                    packet.writeInt((int)((int)bindValue.longBinding));
                    // MONITOREXIT : object
                    return;
                }
                case 3: {
                    packet.ensureCapacity((int)4);
                    packet.writeLong((long)((long)((int)bindValue.longBinding)));
                    // MONITOREXIT : object
                    return;
                }
                case 8: {
                    packet.ensureCapacity((int)8);
                    packet.writeLongLong((long)bindValue.longBinding);
                    // MONITOREXIT : object
                    return;
                }
                case 4: {
                    packet.ensureCapacity((int)4);
                    packet.writeFloat((float)bindValue.floatBinding);
                    // MONITOREXIT : object
                    return;
                }
                case 5: {
                    packet.ensureCapacity((int)8);
                    packet.writeDouble((double)bindValue.doubleBinding);
                    // MONITOREXIT : object
                    return;
                }
                case 11: {
                    this.storeTime((Buffer)packet, (Time)((Time)value));
                    // MONITOREXIT : object
                    return;
                }
                case 7: 
                case 10: 
                case 12: {
                    this.storeDateTime((Buffer)packet, (java.util.Date)((java.util.Date)value), (MysqlIO)mysql, (int)bindValue.bufferType, (Calendar)bindValue.calendar);
                    // MONITOREXIT : object
                    return;
                }
                case 0: 
                case 15: 
                case 246: 
                case 253: 
                case 254: {
                    if (value instanceof byte[]) {
                        packet.writeLenBytes((byte[])((byte[])value));
                        return;
                    }
                    if (!this.isLoadDataQuery) {
                        packet.writeLenString((String)((String)value), (String)this.charEncoding, (String)this.connection.getServerCharset(), (SingleByteCharsetConverter)this.charConverter, (boolean)this.connection.parserKnowsUnicode(), (MySQLConnection)this.connection);
                        return;
                    }
                    packet.writeLenBytes((byte[])StringUtils.getBytes((String)((String)value)));
                    // MONITOREXIT : object
                    return;
                }
            }
            return;
        }
        catch (UnsupportedEncodingException uEE) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.22") + this.connection.getEncoding() + "'"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeDateTime412AndOlder(Buffer intoBuf, java.util.Date dt, int bufferType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Calendar sessionCalendar = null;
        sessionCalendar = !this.useLegacyDatetimeCode ? (bufferType == 10 ? this.getDefaultTzCalendar() : this.getServerTzCalendar()) : (dt instanceof Timestamp && this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew());
        java.util.Date oldTime = sessionCalendar.getTime();
        try {
            intoBuf.ensureCapacity((int)8);
            intoBuf.writeByte((byte)7);
            sessionCalendar.setTime((java.util.Date)dt);
            int year = sessionCalendar.get((int)1);
            int month = sessionCalendar.get((int)2) + 1;
            int date = sessionCalendar.get((int)5);
            intoBuf.writeInt((int)year);
            intoBuf.writeByte((byte)((byte)month));
            intoBuf.writeByte((byte)((byte)date));
            if (dt instanceof Date) {
                intoBuf.writeByte((byte)0);
                intoBuf.writeByte((byte)0);
                intoBuf.writeByte((byte)0);
            } else {
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)11)));
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)12)));
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)13)));
            }
            Object var11_10 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            return;
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql, int bufferType, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.connection.versionMeetsMinimum((int)4, (int)1, (int)3)) {
            this.storeDateTime413AndNewer((Buffer)intoBuf, (java.util.Date)dt, (int)bufferType, (Calendar)cal);
            return;
        }
        this.storeDateTime412AndOlder((Buffer)intoBuf, (java.util.Date)dt, (int)bufferType);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt, int bufferType, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Calendar sessionCalendar = cal;
        if (cal == null) {
            sessionCalendar = !this.useLegacyDatetimeCode ? (bufferType == 10 ? this.getDefaultTzCalendar() : this.getServerTzCalendar()) : (dt instanceof Timestamp && this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew());
        }
        java.util.Date oldTime = sessionCalendar.getTime();
        try {
            sessionCalendar.setTime((java.util.Date)dt);
            if (dt instanceof Date) {
                sessionCalendar.set((int)11, (int)0);
                sessionCalendar.set((int)12, (int)0);
                sessionCalendar.set((int)13, (int)0);
            }
            byte length = 7;
            if (dt instanceof Timestamp) {
                length = 11;
            }
            intoBuf.ensureCapacity((int)length);
            intoBuf.writeByte((byte)length);
            int year = sessionCalendar.get((int)1);
            int month = sessionCalendar.get((int)2) + 1;
            int date = sessionCalendar.get((int)5);
            intoBuf.writeInt((int)year);
            intoBuf.writeByte((byte)((byte)month));
            intoBuf.writeByte((byte)((byte)date));
            if (dt instanceof Date) {
                intoBuf.writeByte((byte)0);
                intoBuf.writeByte((byte)0);
                intoBuf.writeByte((byte)0);
            } else {
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)11)));
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)12)));
                intoBuf.writeByte((byte)((byte)sessionCalendar.get((int)13)));
            }
            if (length == 11) {
                intoBuf.writeLong((long)((long)(((Timestamp)dt).getNanos() / 1000)));
            }
            Object var13_12 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            return;
        }
        catch (Throwable throwable) {
            Object var13_13 = null;
            sessionCalendar.setTime((java.util.Date)oldTime);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Calendar getServerTzCalendar() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.serverTzCalendar == null) {
            this.serverTzCalendar = new GregorianCalendar((TimeZone)this.connection.getServerTimezoneTZ());
        }
        // MONITOREXIT : object
        return this.serverTzCalendar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Calendar getDefaultTzCalendar() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.defaultTzCalendar == null) {
            this.defaultTzCalendar = new GregorianCalendar((TimeZone)TimeZone.getDefault());
        }
        // MONITOREXIT : object
        return this.defaultTzCalendar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream) throws SQLException {
        var5_5 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var5_5
        forcedEncoding = this.connection.getClobCharacterEncoding();
        clobEncoding = forcedEncoding == null ? this.connection.getEncoding() : forcedEncoding;
        maxBytesChar = 2;
        if (clobEncoding != null) {
            if (!clobEncoding.equals((Object)"UTF-16")) {
                maxBytesChar = this.connection.getMaxBytesPerChar((String)clobEncoding);
                if (maxBytesChar == 1) {
                    maxBytesChar = 2;
                }
            } else {
                maxBytesChar = 4;
            }
        }
        buf = new char[8192 / maxBytesChar];
        numRead = 0;
        bytesInPacket = 0;
        totalBytesRead = 0;
        bytesReadAtLastSend = 0;
        packetIsFullAt = this.connection.getBlobSendChunkSize();
        try {
            block16 : {
                block15 : {
                    try {
                        packet.clear();
                        packet.writeByte((byte)24);
                        packet.writeLong((long)this.serverStatementId);
                        packet.writeInt((int)parameterIndex);
                        readAny = false;
                        while ((numRead = inStream.read((char[])buf)) != -1) {
                            readAny = true;
                            valueAsBytes = StringUtils.getBytes((char[])buf, null, (String)clobEncoding, (String)this.connection.getServerCharset(), (int)0, (int)numRead, (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor());
                            packet.writeBytesNoNull((byte[])valueAsBytes, (int)0, (int)valueAsBytes.length);
                            totalBytesRead += valueAsBytes.length;
                            if ((bytesInPacket += valueAsBytes.length) < packetIsFullAt) continue;
                            bytesReadAtLastSend = totalBytesRead;
                            mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                            bytesInPacket = 0;
                            packet.clear();
                            packet.writeByte((byte)24);
                            packet.writeLong((long)this.serverStatementId);
                            packet.writeInt((int)parameterIndex);
                        }
                        if (totalBytesRead != bytesReadAtLastSend) {
                            mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                        }
                        if (readAny) break block15;
                        mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                    }
                    catch (IOException ioEx) {
                        sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.24") + ioEx.toString()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                        sqlEx.initCause((Throwable)ioEx);
                        throw sqlEx;
                    }
                }
                var18_19 = null;
                if (!this.connection.getAutoClosePStmtStreams() || inStream == null) break block16;
                inStream.close();
                return;
                catch (IOException ioEx) {
                    return;
                }
            }
            // MONITOREXIT : var5_5
            return;
        }
        catch (Throwable var17_23) {
            var18_20 = null;
            if (this.connection.getAutoClosePStmtStreams() == false) throw var17_23;
            if (inStream == null) throw var17_23;
            ** try [egrp 3[TRYBLOCK] [3 : 401->409)] { 
lbl70: // 1 sources:
            inStream.close();
            throw var17_23;
lbl72: // 1 sources:
            catch (IOException ioEx) {
                // empty catch block
            }
            throw var17_23;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream) throws SQLException {
        var5_5 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var5_5
        buf = new byte[8192];
        numRead = 0;
        try {
            block12 : {
                block11 : {
                    try {
                        bytesInPacket = 0;
                        totalBytesRead = 0;
                        bytesReadAtLastSend = 0;
                        packetIsFullAt = this.connection.getBlobSendChunkSize();
                        packet.clear();
                        packet.writeByte((byte)24);
                        packet.writeLong((long)this.serverStatementId);
                        packet.writeInt((int)parameterIndex);
                        readAny = false;
                        while ((numRead = inStream.read((byte[])buf)) != -1) {
                            readAny = true;
                            packet.writeBytesNoNull((byte[])buf, (int)0, (int)numRead);
                            totalBytesRead += numRead;
                            if ((bytesInPacket += numRead) < packetIsFullAt) continue;
                            bytesReadAtLastSend = totalBytesRead;
                            mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                            bytesInPacket = 0;
                            packet.clear();
                            packet.writeByte((byte)24);
                            packet.writeLong((long)this.serverStatementId);
                            packet.writeInt((int)parameterIndex);
                        }
                        if (totalBytesRead != bytesReadAtLastSend) {
                            mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                        }
                        if (readAny) break block11;
                        mysql.sendCommand((int)24, null, (Buffer)packet, (boolean)true, null, (int)0);
                    }
                    catch (IOException ioEx) {
                        sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"ServerPreparedStatement.25") + ioEx.toString()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
                        sqlEx.initCause((Throwable)ioEx);
                        throw sqlEx;
                    }
                }
                var14_15 = null;
                if (!this.connection.getAutoClosePStmtStreams() || inStream == null) break block12;
                inStream.close();
                return;
                catch (IOException ioEx) {
                    return;
                }
            }
            // MONITOREXIT : var5_5
            return;
        }
        catch (Throwable var13_19) {
            var14_16 = null;
            if (this.connection.getAutoClosePStmtStreams() == false) throw var13_19;
            if (inStream == null) throw var13_19;
            ** try [egrp 3[TRYBLOCK] [3 : 281->289)] { 
lbl59: // 1 sources:
            inStream.close();
            throw var13_19;
lbl61: // 1 sources:
            catch (IOException ioEx) {
                // empty catch block
            }
            throw var13_19;
        }
    }

    @Override
    public String toString() {
        StringBuilder toStringBuf = new StringBuilder();
        toStringBuf.append((String)"com.mysql.jdbc.ServerPreparedStatement[");
        toStringBuf.append((long)this.serverStatementId);
        toStringBuf.append((String)"] - ");
        try {
            toStringBuf.append((String)this.asSql());
            return toStringBuf.toString();
        }
        catch (SQLException sqlEx) {
            toStringBuf.append((String)Messages.getString((String)"ServerPreparedStatement.6"));
            toStringBuf.append((Object)sqlEx);
        }
        return toStringBuf.toString();
    }

    protected long getServerStatementId() {
        return this.serverStatementId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.hasCheckedRewrite) {
            this.hasCheckedRewrite = true;
            this.canRewrite = ServerPreparedStatement.canRewrite((String)this.originalSql, (boolean)this.isOnDuplicateKeyUpdate(), (int)this.getLocationOfOnDuplicateKeyUpdate(), (int)0);
            this.parseInfo = new PreparedStatement.ParseInfo((String)this.originalSql, (MySQLConnection)this.connection, (DatabaseMetaData)this.connection.getMetaData(), (String)this.charEncoding, (SingleByteCharsetConverter)this.charConverter);
        }
        // MONITOREXIT : object
        return this.canRewrite;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.locationOfOnDuplicateKeyUpdate == -2) {
            this.locationOfOnDuplicateKeyUpdate = ServerPreparedStatement.getOnDuplicateKeyLocation((String)this.originalSql, (boolean)this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), (boolean)this.connection.getRewriteBatchedStatements(), (boolean)this.connection.isNoBackslashEscapesSet());
        }
        // MONITOREXIT : object
        return this.locationOfOnDuplicateKeyUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isOnDuplicateKeyUpdate() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.getLocationOfOnDuplicateKeyUpdate() != -1) {
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
    protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        long sizeOfEntireBatch = 10L;
        long maxSizeOfParameterSet = 0L;
        int i = 0;
        do {
            if (i >= numBatchedArgs) {
                // MONITOREXIT : object
                return new long[]{maxSizeOfParameterSet, sizeOfEntireBatch};
            }
            BindValue[] paramArg = ((BatchedBindValues)this.batchedArgs.get((int)i)).batchedParameterValues;
            long sizeOfParameterSet = 0L;
            sizeOfParameterSet += (long)((this.parameterCount + 7) / 8);
            sizeOfParameterSet += (long)(this.parameterCount * 2);
            for (int j = 0; j < this.parameterBindings.length; ++j) {
                if (paramArg[j].isNull) continue;
                long size = paramArg[j].getBoundLength();
                if (paramArg[j].isLongData) {
                    if (size == -1L) continue;
                    sizeOfParameterSet += size;
                    continue;
                }
                sizeOfParameterSet += size;
            }
            sizeOfEntireBatch += sizeOfParameterSet;
            if (sizeOfParameterSet > maxSizeOfParameterSet) {
                maxSizeOfParameterSet = sizeOfParameterSet;
            }
            ++i;
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
        paramArg = ((BatchedBindValues)paramSet).batchedParameterValues;
        j = 0;
        while (j < paramArg.length) {
            if (paramArg[j].isNull) {
                batchedStatement.setNull((int)batchedParamIndex++, (int)0);
            } else if (paramArg[j].isLongData) {
                value = paramArg[j].value;
                if (value instanceof InputStream) {
                    batchedStatement.setBinaryStream((int)batchedParamIndex++, (InputStream)((InputStream)value), (int)((int)paramArg[j].bindLength));
                } else {
                    batchedStatement.setCharacterStream((int)batchedParamIndex++, (Reader)((Reader)value), (int)((int)paramArg[j].bindLength));
                }
            } else {
                switch (paramArg[j].bufferType) {
                    case 1: {
                        batchedStatement.setByte((int)batchedParamIndex++, (byte)((byte)((int)paramArg[j].longBinding)));
                        ** break;
                    }
                    case 2: {
                        batchedStatement.setShort((int)batchedParamIndex++, (short)((short)((int)paramArg[j].longBinding)));
                        ** break;
                    }
                    case 3: {
                        batchedStatement.setInt((int)batchedParamIndex++, (int)((int)paramArg[j].longBinding));
                        ** break;
                    }
                    case 8: {
                        batchedStatement.setLong((int)batchedParamIndex++, (long)paramArg[j].longBinding);
                        ** break;
                    }
                    case 4: {
                        batchedStatement.setFloat((int)batchedParamIndex++, (float)paramArg[j].floatBinding);
                        ** break;
                    }
                    case 5: {
                        batchedStatement.setDouble((int)batchedParamIndex++, (double)paramArg[j].doubleBinding);
                        ** break;
                    }
                    case 11: {
                        batchedStatement.setTime((int)batchedParamIndex++, (Time)((Time)paramArg[j].value));
                        ** break;
                    }
                    case 10: {
                        batchedStatement.setDate((int)batchedParamIndex++, (Date)((Date)paramArg[j].value));
                        ** break;
                    }
                    case 7: 
                    case 12: {
                        batchedStatement.setTimestamp((int)batchedParamIndex++, (Timestamp)((Timestamp)paramArg[j].value));
                        ** break;
                    }
                    case 0: 
                    case 15: 
                    case 246: 
                    case 253: 
                    case 254: {
                        value = paramArg[j].value;
                        if (value instanceof byte[]) {
                            batchedStatement.setBytes((int)batchedParamIndex, (byte[])((byte[])value));
                        } else {
                            batchedStatement.setString((int)batchedParamIndex, (String)((String)value));
                        }
                        if (batchedStatement instanceof ServerPreparedStatement) {
                            asBound = ((ServerPreparedStatement)batchedStatement).getBinding((int)batchedParamIndex, (boolean)false);
                            asBound.bufferType = paramArg[j].bufferType;
                        }
                        ++batchedParamIndex;
                        ** break;
                    }
                }
                throw new IllegalArgumentException((String)("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex));
            }
lbl54: // 13 sources:
            ++j;
        }
        return batchedParamIndex;
    }

    @Override
    protected boolean containsOnDuplicateKeyUpdateInSQL() {
        return this.hasOnDuplicateKeyUpdate;
    }

    @Override
    protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            PreparedStatement pstmt = ((Wrapper)((Object)localConn.prepareStatement((String)this.parseInfo.getSqlForBatch((int)numBatches), (int)this.resultSetType, (int)this.resultSetConcurrency))).unwrap(PreparedStatement.class);
            pstmt.setRetrieveGeneratedKeys((boolean)this.retrieveGeneratedKeys);
            // MONITOREXIT : object
            return pstmt;
        }
        catch (UnsupportedEncodingException e) {
            SQLException sqlEx = SQLError.createSQLException((String)"Unable to prepare batch statement", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        if (!poolable) {
            this.connection.decachePreparedStatement((ServerPreparedStatement)this);
        }
        super.setPoolable((boolean)poolable);
    }

    static {
        if (!Util.isJdbc4()) {
            JDBC_4_SPS_CTOR = null;
            return;
        }
        try {
            String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ServerPreparedStatement" : "com.mysql.jdbc.JDBC4ServerPreparedStatement";
            JDBC_4_SPS_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, String.class, String.class, Integer.TYPE, Integer.TYPE);
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

