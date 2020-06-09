/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.Constants;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.MysqlParameterMetadata;
import com.mysql.jdbc.ParameterBindings;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreparedStatement
extends StatementImpl
implements java.sql.PreparedStatement {
    private static final Constructor<?> JDBC_4_PSTMT_2_ARG_CTOR;
    private static final Constructor<?> JDBC_4_PSTMT_3_ARG_CTOR;
    private static final Constructor<?> JDBC_4_PSTMT_4_ARG_CTOR;
    private static final byte[] HEX_DIGITS;
    protected boolean batchHasPlainStatements = false;
    private DatabaseMetaData dbmd = null;
    protected char firstCharOfStmt = '\u0000';
    protected boolean isLoadDataQuery = false;
    protected boolean[] isNull = null;
    private boolean[] isStream = null;
    protected int numberOfExecutions = 0;
    protected String originalSql = null;
    protected int parameterCount;
    protected MysqlParameterMetadata parameterMetaData;
    private InputStream[] parameterStreams = null;
    private byte[][] parameterValues = (byte[][])null;
    protected int[] parameterTypes = null;
    protected ParseInfo parseInfo;
    private java.sql.ResultSetMetaData pstmtResultMetaData;
    private byte[][] staticSqlStrings = (byte[][])null;
    private byte[] streamConvertBuf = null;
    private int[] streamLengths = null;
    private SimpleDateFormat tsdf = null;
    private SimpleDateFormat ddf;
    private SimpleDateFormat tdf;
    protected boolean useTrueBoolean = false;
    protected boolean usingAnsiMode;
    protected String batchedValuesClause;
    private boolean doPingInstead;
    private boolean compensateForOnDuplicateKeyUpdate = false;
    private CharsetEncoder charsetEncoder;
    protected int batchCommandIndex = -1;
    protected boolean serverSupportsFracSecs;
    protected int rewrittenBatchSize = 0;

    protected static int readFully(Reader reader, char[] buf, int length) throws IOException {
        int numCharsRead = 0;
        while (numCharsRead < length) {
            int count = reader.read((char[])buf, (int)numCharsRead, (int)(length - numCharsRead));
            if (count < 0) {
                return numCharsRead;
            }
            numCharsRead += count;
        }
        return numCharsRead;
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String catalog) throws SQLException {
        if (Util.isJdbc4()) return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, (Object[])new Object[]{conn, catalog}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new PreparedStatement((MySQLConnection)conn, (String)catalog);
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog) throws SQLException {
        if (Util.isJdbc4()) return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, (Object[])new Object[]{conn, sql, catalog}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new PreparedStatement((MySQLConnection)conn, (String)sql, (String)catalog);
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
        if (Util.isJdbc4()) return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, (Object[])new Object[]{conn, sql, catalog, cachedParseInfo}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new PreparedStatement((MySQLConnection)conn, (String)sql, (String)catalog, (ParseInfo)cachedParseInfo);
    }

    public PreparedStatement(MySQLConnection conn, String catalog) throws SQLException {
        super((MySQLConnection)conn, (String)catalog);
        this.detectFractionalSecondsSupport();
        this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
    }

    protected void detectFractionalSecondsSupport() throws SQLException {
        this.serverSupportsFracSecs = this.connection != null && this.connection.versionMeetsMinimum((int)5, (int)6, (int)4);
    }

    public PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException {
        super((MySQLConnection)conn, (String)catalog);
        if (sql == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"PreparedStatement.0"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.detectFractionalSecondsSupport();
        this.originalSql = sql;
        this.doPingInstead = this.originalSql.startsWith((String)"/* ping */");
        this.dbmd = this.connection.getMetaData();
        this.useTrueBoolean = this.connection.versionMeetsMinimum((int)3, (int)21, (int)23);
        this.parseInfo = new ParseInfo((String)sql, (MySQLConnection)this.connection, (DatabaseMetaData)this.dbmd, (String)this.charEncoding, (SingleByteCharsetConverter)this.charConverter);
        this.initializeFromParseInfo();
        this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
        if (!conn.getRequiresEscapingEncoder()) return;
        this.charsetEncoder = Charset.forName((String)conn.getEncoding()).newEncoder();
    }

    public PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
        super((MySQLConnection)conn, (String)catalog);
        if (sql == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"PreparedStatement.1"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.detectFractionalSecondsSupport();
        this.originalSql = sql;
        this.dbmd = this.connection.getMetaData();
        this.useTrueBoolean = this.connection.versionMeetsMinimum((int)3, (int)21, (int)23);
        this.parseInfo = cachedParseInfo;
        this.usingAnsiMode = !this.connection.useAnsiQuotedIdentifiers();
        this.initializeFromParseInfo();
        this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
        if (!conn.getRequiresEscapingEncoder()) return;
        this.charsetEncoder = Charset.forName((String)conn.getEncoding()).newEncoder();
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
        int i = 0;
        do {
            if (i >= this.parameterValues.length) {
                this.batchedArgs.add(new BatchParams((PreparedStatement)this, (byte[][])this.parameterValues, (InputStream[])this.parameterStreams, (boolean[])this.isStream, (int[])this.streamLengths, (boolean[])this.isNull));
                // MONITOREXIT : object
                return;
            }
            this.checkAllParametersSet((byte[])this.parameterValues[i], (InputStream)this.parameterStreams[i], (int)i);
            ++i;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.batchHasPlainStatements = true;
        super.addBatch((String)sql);
        // MONITOREXIT : object
        return;
    }

    public String asSql() throws SQLException {
        return this.asSql((boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder buf = new StringBuilder();
        try {
            int realParameterCount = this.parameterCount + this.getParameterIndexOffset();
            Object batchArg = null;
            if (this.batchCommandIndex != -1) {
                batchArg = this.batchedArgs.get((int)this.batchCommandIndex);
            }
            for (int i = 0; i < realParameterCount; ++i) {
                if (this.charEncoding != null) {
                    buf.append((String)StringUtils.toString((byte[])this.staticSqlStrings[i], (String)this.charEncoding));
                } else {
                    buf.append((String)StringUtils.toString((byte[])this.staticSqlStrings[i]));
                }
                byte[] val = null;
                if (batchArg != null && batchArg instanceof String) {
                    buf.append((String)((String)batchArg));
                    continue;
                }
                val = this.batchCommandIndex == -1 ? this.parameterValues[i] : ((BatchParams)batchArg).parameterStrings[i];
                boolean isStreamParam = false;
                isStreamParam = this.batchCommandIndex == -1 ? this.isStream[i] : ((BatchParams)batchArg).isStream[i];
                if (val == null && !isStreamParam) {
                    if (quoteStreamsAndUnknowns) {
                        buf.append((String)"'");
                    }
                    buf.append((String)"** NOT SPECIFIED **");
                    if (!quoteStreamsAndUnknowns) continue;
                    buf.append((String)"'");
                    continue;
                }
                if (isStreamParam) {
                    if (quoteStreamsAndUnknowns) {
                        buf.append((String)"'");
                    }
                    buf.append((String)"** STREAM DATA **");
                    if (!quoteStreamsAndUnknowns) continue;
                    buf.append((String)"'");
                    continue;
                }
                if (this.charConverter != null) {
                    buf.append((String)this.charConverter.toString((byte[])val));
                    continue;
                }
                if (this.charEncoding != null) {
                    buf.append((String)new String((byte[])val, (String)this.charEncoding));
                    continue;
                }
                buf.append((String)StringUtils.toAsciiString((byte[])val));
            }
            if (this.charEncoding != null) {
                buf.append((String)StringUtils.toString((byte[])this.staticSqlStrings[this.parameterCount + this.getParameterIndexOffset()], (String)this.charEncoding));
                return buf.toString();
            }
            buf.append((String)StringUtils.toAsciiString((byte[])this.staticSqlStrings[this.parameterCount + this.getParameterIndexOffset()]));
            return buf.toString();
        }
        catch (UnsupportedEncodingException uue) {
            throw new RuntimeException((String)(Messages.getString((String)"PreparedStatement.32") + this.charEncoding + Messages.getString((String)"PreparedStatement.33")));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearBatch() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.batchHasPlainStatements = false;
        super.clearBatch();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearParameters() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int i = 0;
        do {
            if (i >= this.parameterValues.length) {
                // MONITOREXIT : object
                return;
            }
            this.parameterValues[i] = null;
            this.parameterStreams[i] = null;
            this.isStream[i] = false;
            this.isNull[i] = false;
            this.parameterTypes[i] = 0;
            ++i;
        } while (true);
    }

    private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException {
        int lastwritten = 0;
        int i = 0;
        do {
            if (i >= size) {
                if (lastwritten >= size) return;
                packet.writeBytesNoNull((byte[])buf, (int)lastwritten, (int)(size - lastwritten));
                return;
            }
            byte b = buf[i];
            if (b == 0) {
                if (i > lastwritten) {
                    packet.writeBytesNoNull((byte[])buf, (int)lastwritten, (int)(i - lastwritten));
                }
                packet.writeByte((byte)92);
                packet.writeByte((byte)48);
                lastwritten = i + 1;
            } else if (b == 92 || b == 39 || !this.usingAnsiMode && b == 34) {
                if (i > lastwritten) {
                    packet.writeBytesNoNull((byte[])buf, (int)lastwritten, (int)(i - lastwritten));
                }
                packet.writeByte((byte)92);
                lastwritten = i;
            }
            ++i;
        } while (true);
    }

    private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size) {
        int lastwritten = 0;
        int i = 0;
        do {
            if (i >= size) {
                if (lastwritten >= size) return;
                bytesOut.write((byte[])buf, (int)lastwritten, (int)(size - lastwritten));
                return;
            }
            byte b = buf[i];
            if (b == 0) {
                if (i > lastwritten) {
                    bytesOut.write((byte[])buf, (int)lastwritten, (int)(i - lastwritten));
                }
                bytesOut.write((int)92);
                bytesOut.write((int)48);
                lastwritten = i + 1;
            } else if (b == 39) {
                if (i > lastwritten) {
                    bytesOut.write((byte[])buf, (int)lastwritten, (int)(i - lastwritten));
                }
                bytesOut.write((int)(this.connection.isNoBackslashEscapesSet() ? 39 : 92));
                lastwritten = i;
            } else if (b == 92 || !this.usingAnsiMode && b == 34) {
                if (i > lastwritten) {
                    bytesOut.write((byte[])buf, (int)lastwritten, (int)(i - lastwritten));
                }
                bytesOut.write((int)92);
                lastwritten = i;
            }
            ++i;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean checkReadOnlySafeStatement() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.firstCharOfStmt == 'S') return true;
        if (!this.connection.isReadOnly()) return true;
        boolean bl = false;
        // MONITOREXIT : object
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean execute() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        if (!this.doPingInstead && !this.checkReadOnlySafeStatement()) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.20") + Messages.getString((String)"PreparedStatement.21")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        ResultSetInternalMethods rs = null;
        this.lastQueryIsOnDupKeyUpdate = false;
        if (this.retrieveGeneratedKeys) {
            this.lastQueryIsOnDupKeyUpdate = this.containsOnDuplicateKeyUpdateInSQL();
        }
        this.batchedGeneratedKeys = null;
        this.resetCancelledState();
        this.implicitlyCloseAllOpenResults();
        this.clearWarnings();
        if (this.doPingInstead) {
            this.doPingInstead();
            // MONITOREXIT : object
            return true;
        }
        this.setupStreamingTimeout((MySQLConnection)locallyScopedConn);
        Buffer sendPacket = this.fillSendPacket();
        String oldCatalog = null;
        if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
            oldCatalog = locallyScopedConn.getCatalog();
            locallyScopedConn.setCatalog((String)this.currentCatalog);
        }
        CachedResultSetMetaData cachedMetadata = null;
        if (locallyScopedConn.getCacheResultSetMetadata()) {
            cachedMetadata = locallyScopedConn.getCachedMetaData((String)this.originalSql);
        }
        Field[] metadataFromCache = null;
        if (cachedMetadata != null) {
            metadataFromCache = cachedMetadata.fields;
        }
        boolean oldInfoMsgState = false;
        if (this.retrieveGeneratedKeys) {
            oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
            locallyScopedConn.setReadInfoMsgEnabled((boolean)true);
        }
        locallyScopedConn.setSessionMaxRows((int)(this.firstCharOfStmt == 'S' ? this.maxRows : -1));
        rs = this.executeInternal((int)this.maxRows, (Buffer)sendPacket, (boolean)this.createStreamingResultSet(), (boolean)(this.firstCharOfStmt == 'S'), (Field[])metadataFromCache, (boolean)false);
        if (cachedMetadata != null) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)this.originalSql, (CachedResultSetMetaData)cachedMetadata, (ResultSetInternalMethods)rs);
        } else if (rs.reallyResult() && locallyScopedConn.getCacheResultSetMetadata()) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)this.originalSql, null, (ResultSetInternalMethods)rs);
        }
        if (this.retrieveGeneratedKeys) {
            locallyScopedConn.setReadInfoMsgEnabled((boolean)oldInfoMsgState);
            rs.setFirstCharOfQuery((char)this.firstCharOfStmt);
        }
        if (oldCatalog != null) {
            locallyScopedConn.setCatalog((String)oldCatalog);
        }
        if (rs != null) {
            this.lastInsertId = rs.getUpdateID();
            this.results = rs;
        }
        if (rs != null && rs.reallyResult()) {
            return true;
        }
        boolean bl = false;
        // MONITOREXIT : object
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    protected long[] executeBatchInternal() throws SQLException {
        int batchTimeout;
        block9 : {
            block10 : {
                long[] arrl;
                Object object = this.checkClosed().getConnectionMutex();
                // MONITORENTER : object
                if (this.connection.isReadOnly()) {
                    throw new SQLException((String)(Messages.getString((String)"PreparedStatement.25") + Messages.getString((String)"PreparedStatement.26")), (String)"S1009");
                }
                if (this.batchedArgs == null || this.batchedArgs.size() == 0) {
                    // MONITOREXIT : object
                    return new long[0];
                }
                batchTimeout = this.timeoutInMillis;
                this.timeoutInMillis = 0;
                this.resetCancelledState();
                try {
                    this.statementBegins();
                    this.clearWarnings();
                    if (this.batchHasPlainStatements || !this.connection.getRewriteBatchedStatements()) break block9;
                    if (!this.canRewriteAsMultiValueInsertAtSqlLevel()) break block10;
                    arrl = this.executeBatchedInserts((int)batchTimeout);
                    Object var5_6 = null;
                    this.statementExecuting.set((boolean)false);
                }
                catch (Throwable throwable) {
                    Object var5_9 = null;
                    this.statementExecuting.set((boolean)false);
                    this.clearBatch();
                    throw throwable;
                }
                this.clearBatch();
                return arrl;
            }
            if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0) || this.batchHasPlainStatements || this.batchedArgs == null || this.batchedArgs.size() <= 3) break block9;
            long[] arrl = this.executePreparedBatchAsMultiStatement((int)batchTimeout);
            Object var5_7 = null;
            this.statementExecuting.set((boolean)false);
            this.clearBatch();
            return arrl;
        }
        long[] arrl = this.executeBatchSerially((int)batchTimeout);
        Object var5_8 = null;
        this.statementExecuting.set((boolean)false);
        this.clearBatch();
        return arrl;
    }

    public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException {
        return this.parseInfo.canRewriteAsMultiValueInsert;
    }

    protected int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
        return this.parseInfo.locationOfOnDuplicateKeyUpdate;
    }

    /*
     * Exception decompiling
     */
    protected long[] executePreparedBatchAsMultiStatement(int batchTimeout) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[TRYBLOCK]], but top level block is 8[TRYBLOCK]
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
    private String generateMultiStatementForBatch(int numBatches) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder newStatementSql = new StringBuilder((int)((this.originalSql.length() + 1) * numBatches));
        newStatementSql.append((String)this.originalSql);
        int i = 0;
        do {
            if (i >= numBatches - 1) {
                // MONITOREXIT : object
                return newStatementSql.toString();
            }
            newStatementSql.append((char)';');
            newStatementSql.append((String)this.originalSql);
            ++i;
        } while (true);
    }

    /*
     * Exception decompiling
     */
    protected long[] executeBatchedInserts(int batchTimeout) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[TRYBLOCK]], but top level block is 8[TRYBLOCK]
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

    protected String getValuesClause() throws SQLException {
        return this.parseInfo.valuesClause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int computeBatchSize(int numBatchedArgs) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        long[] combinedValues = this.computeMaxParameterSetSizeAndBatchSize((int)numBatchedArgs);
        long maxSizeOfParameterSet = combinedValues[0];
        long sizeOfEntireBatch = combinedValues[1];
        int maxAllowedPacket = this.connection.getMaxAllowedPacket();
        if (sizeOfEntireBatch < (long)(maxAllowedPacket - this.originalSql.length())) {
            // MONITOREXIT : object
            return numBatchedArgs;
        }
        // MONITOREXIT : object
        return (int)Math.max((long)1L, (long)((long)(maxAllowedPacket - this.originalSql.length()) / maxSizeOfParameterSet));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        long sizeOfEntireBatch = 0L;
        long maxSizeOfParameterSet = 0L;
        int i = 0;
        do {
            if (i >= numBatchedArgs) {
                // MONITOREXIT : object
                return new long[]{maxSizeOfParameterSet, sizeOfEntireBatch};
            }
            BatchParams paramArg = (BatchParams)this.batchedArgs.get((int)i);
            boolean[] isNullBatch = paramArg.isNull;
            boolean[] isStreamBatch = paramArg.isStream;
            long sizeOfParameterSet = 0L;
            for (int j = 0; j < isNullBatch.length; ++j) {
                if (!isNullBatch[j]) {
                    if (isStreamBatch[j]) {
                        int streamLength = paramArg.streamLengths[j];
                        if (streamLength != -1) {
                            sizeOfParameterSet += (long)(streamLength * 2);
                            continue;
                        }
                        int paramLength = paramArg.parameterStrings[j].length;
                        sizeOfParameterSet += (long)paramLength;
                        continue;
                    }
                    sizeOfParameterSet += (long)paramArg.parameterStrings[j].length;
                    continue;
                }
                sizeOfParameterSet += 4L;
            }
            sizeOfParameterSet = this.getValuesClause() != null ? (sizeOfParameterSet += (long)(this.getValuesClause().length() + 1)) : (sizeOfParameterSet += (long)(this.originalSql.length() + 1));
            sizeOfEntireBatch += sizeOfParameterSet;
            if (sizeOfParameterSet > maxSizeOfParameterSet) {
                maxSizeOfParameterSet = sizeOfParameterSet;
            }
            ++i;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long[] executeBatchSerially(int batchTimeout) throws SQLException {
        long[] arrl;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            this.checkClosed();
        }
        long[] updateCounts = null;
        if (this.batchedArgs != null) {
            int nbrCommands = this.batchedArgs.size();
            updateCounts = new long[nbrCommands];
            for (int i = 0; i < nbrCommands; ++i) {
                updateCounts[i] = -3L;
            }
            SQLException sqlEx = null;
            TimerTask timeoutTask = null;
            try {
                try {
                    if (locallyScopedConn.getEnableQueryTimeouts() && batchTimeout != 0 && locallyScopedConn.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                        timeoutTask = new StatementImpl.CancelTask((StatementImpl)this, (StatementImpl)this);
                        locallyScopedConn.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)batchTimeout));
                    }
                    if (this.retrieveGeneratedKeys) {
                        this.batchedGeneratedKeys = new ArrayList<E>((int)nbrCommands);
                    }
                    this.batchCommandIndex = 0;
                    while (this.batchCommandIndex < nbrCommands) {
                        E arg = this.batchedArgs.get((int)this.batchCommandIndex);
                        try {
                            if (arg instanceof String) {
                                updateCounts[this.batchCommandIndex] = this.executeUpdateInternal((String)((String)arg), (boolean)true, (boolean)this.retrieveGeneratedKeys);
                                this.getBatchedGeneratedKeys((int)(this.results.getFirstCharOfQuery() == 'I' && this.containsOnDuplicateKeyInString((String)((String)arg)) ? 1 : 0));
                            } else {
                                BatchParams paramArg = (BatchParams)arg;
                                updateCounts[this.batchCommandIndex] = this.executeUpdateInternal((byte[][])paramArg.parameterStrings, (InputStream[])paramArg.parameterStreams, (boolean[])paramArg.isStream, (int[])paramArg.streamLengths, (boolean[])paramArg.isNull, (boolean)true);
                                this.getBatchedGeneratedKeys((int)(this.containsOnDuplicateKeyUpdateInSQL() ? 1 : 0));
                            }
                        }
                        catch (SQLException ex) {
                            updateCounts[this.batchCommandIndex] = -3L;
                            if (this.continueBatchOnError && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !this.hasDeadlockOrTimeoutRolledBackTx((SQLException)ex)) {
                                sqlEx = ex;
                            }
                            long[] newUpdateCounts = new long[this.batchCommandIndex];
                            System.arraycopy((Object)updateCounts, (int)0, (Object)newUpdateCounts, (int)0, (int)this.batchCommandIndex);
                            throw SQLError.createBatchUpdateException((SQLException)ex, (long[])newUpdateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                        ++this.batchCommandIndex;
                    }
                    if (sqlEx != null) {
                        throw SQLError.createBatchUpdateException(sqlEx, (long[])updateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    Object var12_16 = null;
                }
                catch (NullPointerException npe) {
                    try {
                        this.checkClosed();
                        throw npe;
                    }
                    catch (SQLException connectionClosedEx) {
                        updateCounts[this.batchCommandIndex] = -3L;
                        long[] newUpdateCounts = new long[this.batchCommandIndex];
                        System.arraycopy((Object)updateCounts, (int)0, (Object)newUpdateCounts, (int)0, (int)this.batchCommandIndex);
                        throw SQLError.createBatchUpdateException((SQLException)connectionClosedEx, (long[])newUpdateCounts, (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
                this.batchCommandIndex = -1;
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                    locallyScopedConn.getCancelTimer().purge();
                }
                this.resetCancelledState();
            }
            catch (Throwable throwable) {
                Object var12_17 = null;
                this.batchCommandIndex = -1;
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                    locallyScopedConn.getCancelTimer().purge();
                }
                this.resetCancelledState();
                throw throwable;
            }
        }
        if (updateCounts != null) {
            arrl = updateCounts;
            return arrl;
        }
        arrl = new long[]{};
        // MONITOREXIT : object
        return arrl;
    }

    public String getDateTime(String pattern) {
        SimpleDateFormat sdf = TimeUtil.getSimpleDateFormat(null, (String)pattern, null, null);
        return sdf.format((Date)new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            MySQLConnection locallyScopedConnection = this.connection;
            ++this.numberOfExecutions;
            TimerTask timeoutTask = null;
            try {
                if (locallyScopedConnection.getEnableQueryTimeouts() && this.timeoutInMillis != 0 && locallyScopedConnection.versionMeetsMinimum((int)5, (int)0, (int)0)) {
                    timeoutTask = new StatementImpl.CancelTask((StatementImpl)this, (StatementImpl)this);
                    locallyScopedConnection.getCancelTimer().schedule((TimerTask)timeoutTask, (long)((long)this.timeoutInMillis));
                }
                if (!isBatch) {
                    this.statementBegins();
                }
                ResultSetInternalMethods rs = locallyScopedConnection.execSQL((StatementImpl)this, null, (int)maxRowsToRetrieve, (Buffer)sendPacket, (int)this.resultSetType, (int)this.resultSetConcurrency, (boolean)createStreamingResultSet, (String)this.currentCatalog, (Field[])metadataFromCache, (boolean)isBatch);
                if (timeoutTask != null) {
                    timeoutTask.cancel();
                    locallyScopedConnection.getCancelTimer().purge();
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
                Object var15_15 = null;
                if (!isBatch) {
                    this.statementExecuting.set((boolean)false);
                }
                if (timeoutTask == null) return rs;
                {
                    timeoutTask.cancel();
                    locallyScopedConnection.getCancelTimer().purge();
                    return rs;
                }
            }
            catch (Throwable throwable) {
                Object var15_16 = null;
                if (!isBatch) {
                    this.statementExecuting.set((boolean)false);
                }
                if (timeoutTask == null) throw throwable;
                timeoutTask.cancel();
                locallyScopedConnection.getCancelTimer().purge();
                throw throwable;
            }
        }
        catch (NullPointerException npe) {
            this.checkClosed();
            throw npe;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        this.checkForDml((String)this.originalSql, (char)this.firstCharOfStmt);
        this.batchedGeneratedKeys = null;
        this.resetCancelledState();
        this.implicitlyCloseAllOpenResults();
        this.clearWarnings();
        if (this.doPingInstead) {
            this.doPingInstead();
            // MONITOREXIT : object
            return this.results;
        }
        this.setupStreamingTimeout((MySQLConnection)locallyScopedConn);
        Buffer sendPacket = this.fillSendPacket();
        String oldCatalog = null;
        if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
            oldCatalog = locallyScopedConn.getCatalog();
            locallyScopedConn.setCatalog((String)this.currentCatalog);
        }
        CachedResultSetMetaData cachedMetadata = null;
        if (locallyScopedConn.getCacheResultSetMetadata()) {
            cachedMetadata = locallyScopedConn.getCachedMetaData((String)this.originalSql);
        }
        Field[] metadataFromCache = null;
        if (cachedMetadata != null) {
            metadataFromCache = cachedMetadata.fields;
        }
        locallyScopedConn.setSessionMaxRows((int)this.maxRows);
        this.results = this.executeInternal((int)this.maxRows, (Buffer)sendPacket, (boolean)this.createStreamingResultSet(), (boolean)true, (Field[])metadataFromCache, (boolean)false);
        if (oldCatalog != null) {
            locallyScopedConn.setCatalog((String)oldCatalog);
        }
        if (cachedMetadata != null) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)this.originalSql, (CachedResultSetMetaData)cachedMetadata, (ResultSetInternalMethods)this.results);
        } else if (locallyScopedConn.getCacheResultSetMetadata()) {
            locallyScopedConn.initializeResultsMetadataFromCache((String)this.originalSql, null, (ResultSetInternalMethods)this.results);
        }
        this.lastInsertId = this.results.getUpdateID();
        // MONITOREXIT : object
        return this.results;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return Util.truncateAndConvertToInt((long)this.executeLargeUpdate());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long executeUpdateInternal(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (clearBatchedGeneratedKeysAndWarnings) {
            this.clearWarnings();
            this.batchedGeneratedKeys = null;
        }
        // MONITOREXIT : object
        return this.executeUpdateInternal((byte[][])this.parameterValues, (InputStream[])this.parameterStreams, (boolean[])this.isStream, (int[])this.streamLengths, (boolean[])this.isNull, (boolean)isBatch);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long executeUpdateInternal(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn.isReadOnly((boolean)false)) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.34") + Messages.getString((String)"PreparedStatement.35")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.firstCharOfStmt == 'S' && this.isSelectQuery()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"PreparedStatement.37"), (String)"01S03", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.resetCancelledState();
        this.implicitlyCloseAllOpenResults();
        ResultSetInternalMethods rs = null;
        Buffer sendPacket = this.fillSendPacket((byte[][])batchedParameterStrings, (InputStream[])batchedParameterStreams, (boolean[])batchedIsStream, (int[])batchedStreamLengths);
        String oldCatalog = null;
        if (!locallyScopedConn.getCatalog().equals((Object)this.currentCatalog)) {
            oldCatalog = locallyScopedConn.getCatalog();
            locallyScopedConn.setCatalog((String)this.currentCatalog);
        }
        locallyScopedConn.setSessionMaxRows((int)-1);
        boolean oldInfoMsgState = false;
        if (this.retrieveGeneratedKeys) {
            oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
            locallyScopedConn.setReadInfoMsgEnabled((boolean)true);
        }
        rs = this.executeInternal((int)-1, (Buffer)sendPacket, (boolean)false, (boolean)false, null, (boolean)isReallyBatch);
        if (this.retrieveGeneratedKeys) {
            locallyScopedConn.setReadInfoMsgEnabled((boolean)oldInfoMsgState);
            rs.setFirstCharOfQuery((char)this.firstCharOfStmt);
        }
        if (oldCatalog != null) {
            locallyScopedConn.setCatalog((String)oldCatalog);
        }
        this.results = rs;
        this.updateCount = rs.getUpdateCount();
        if (this.containsOnDuplicateKeyUpdateInSQL() && this.compensateForOnDuplicateKeyUpdate && (this.updateCount == 2L || this.updateCount == 0L)) {
            this.updateCount = 1L;
        }
        this.lastInsertId = rs.getUpdateID();
        // MONITOREXIT : object
        return this.updateCount;
    }

    protected boolean containsOnDuplicateKeyUpdateInSQL() {
        return this.parseInfo.isOnDuplicateKeyUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Buffer fillSendPacket() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.fillSendPacket((byte[][])this.parameterValues, (InputStream[])this.parameterStreams, (boolean[])this.isStream, (int[])this.streamLengths);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
        int i;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
        sendPacket.clear();
        sendPacket.writeByte((byte)3);
        boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
        int ensurePacketSize = 0;
        String statementComment = this.connection.getStatementComment();
        byte[] commentAsBytes = null;
        if (statementComment != null) {
            commentAsBytes = this.charConverter != null ? this.charConverter.toBytes((String)statementComment) : StringUtils.getBytes((String)statementComment, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor());
            ensurePacketSize += commentAsBytes.length;
            ensurePacketSize += 6;
        }
        for (i = 0; i < batchedParameterStrings.length; ++i) {
            if (!batchedIsStream[i] || !useStreamLengths) continue;
            ensurePacketSize += batchedStreamLengths[i];
        }
        if (ensurePacketSize != 0) {
            sendPacket.ensureCapacity((int)ensurePacketSize);
        }
        if (commentAsBytes != null) {
            sendPacket.writeBytesNoNull((byte[])Constants.SLASH_STAR_SPACE_AS_BYTES);
            sendPacket.writeBytesNoNull((byte[])commentAsBytes);
            sendPacket.writeBytesNoNull((byte[])Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
        }
        i = 0;
        do {
            if (i >= batchedParameterStrings.length) {
                sendPacket.writeBytesNoNull((byte[])this.staticSqlStrings[batchedParameterStrings.length]);
                // MONITOREXIT : object
                return sendPacket;
            }
            this.checkAllParametersSet((byte[])batchedParameterStrings[i], (InputStream)batchedParameterStreams[i], (int)i);
            sendPacket.writeBytesNoNull((byte[])this.staticSqlStrings[i]);
            if (batchedIsStream[i]) {
                this.streamToBytes((Buffer)sendPacket, (InputStream)batchedParameterStreams[i], (boolean)true, (int)batchedStreamLengths[i], (boolean)useStreamLengths);
            } else {
                sendPacket.writeBytesNoNull((byte[])batchedParameterStrings[i]);
            }
            ++i;
        } while (true);
    }

    private void checkAllParametersSet(byte[] parameterString, InputStream parameterStream, int columnIndex) throws SQLException {
        if (parameterString != null) return;
        if (parameterStream != null) return;
        throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.40") + (columnIndex + 1)), (String)"07001", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        PreparedStatement pstmt = new PreparedStatement((MySQLConnection)localConn, (String)("Rewritten batch of: " + this.originalSql), (String)this.currentCatalog, (ParseInfo)this.parseInfo.getParseInfoForBatch((int)numBatches));
        pstmt.setRetrieveGeneratedKeys((boolean)this.retrieveGeneratedKeys);
        pstmt.rewrittenBatchSize = numBatches;
        // MONITOREXIT : object
        return pstmt;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.retrieveGeneratedKeys = flag;
        // MONITOREXIT : object
        return;
    }

    public int getRewrittenBatchSize() {
        return this.rewrittenBatchSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getNonRewrittenSql() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int indexOfBatch = this.originalSql.indexOf((String)" of: ");
        if (indexOfBatch != -1) {
            // MONITOREXIT : object
            return this.originalSql.substring((int)(indexOfBatch + 5));
        }
        // MONITOREXIT : object
        return this.originalSql;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getBytesRepresentation(int parameterIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.isStream[parameterIndex]) {
            // MONITOREXIT : object
            return this.streamToBytes((InputStream)this.parameterStreams[parameterIndex], (boolean)false, (int)this.streamLengths[parameterIndex], (boolean)this.connection.getUseStreamLengthsInPrepStmts());
        }
        byte[] parameterVal = this.parameterValues[parameterIndex];
        if (parameterVal == null) {
            // MONITOREXIT : object
            return null;
        }
        if (parameterVal[0] == 39 && parameterVal[parameterVal.length - 1] == 39) {
            byte[] valNoQuotes = new byte[parameterVal.length - 2];
            System.arraycopy((Object)parameterVal, (int)1, (Object)valNoQuotes, (int)0, (int)(parameterVal.length - 2));
            // MONITOREXIT : object
            return valNoQuotes;
        }
        // MONITOREXIT : object
        return parameterVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] getBytesRepresentationForBatch(int parameterIndex, int commandIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        E batchedArg = this.batchedArgs.get((int)commandIndex);
        if (batchedArg instanceof String) {
            try {
                // MONITOREXIT : object
                return StringUtils.getBytes((String)((String)batchedArg), (String)this.charEncoding);
            }
            catch (UnsupportedEncodingException uue) {
                throw new RuntimeException((String)(Messages.getString((String)"PreparedStatement.32") + this.charEncoding + Messages.getString((String)"PreparedStatement.33")));
            }
        }
        BatchParams params = (BatchParams)batchedArg;
        if (params.isStream[parameterIndex]) {
            // MONITOREXIT : object
            return this.streamToBytes((InputStream)params.parameterStreams[parameterIndex], (boolean)false, (int)params.streamLengths[parameterIndex], (boolean)this.connection.getUseStreamLengthsInPrepStmts());
        }
        byte[] parameterVal = params.parameterStrings[parameterIndex];
        if (parameterVal == null) {
            // MONITOREXIT : object
            return null;
        }
        if (parameterVal[0] == 39 && parameterVal[parameterVal.length - 1] == 39) {
            byte[] valNoQuotes = new byte[parameterVal.length - 2];
            System.arraycopy((Object)parameterVal, (int)1, (Object)valNoQuotes, (int)0, (int)(parameterVal.length - 2));
            // MONITOREXIT : object
            return valNoQuotes;
        }
        // MONITOREXIT : object
        return parameterVal;
    }

    private final String getDateTimePattern(String dt, boolean toTime) throws Exception {
        int n;
        int size;
        int dtLength;
        char c;
        int i;
        Object[] v;
        int n2 = dtLength = dt != null ? dt.length() : 0;
        if (dtLength >= 8 && dtLength <= 10) {
            int dashCount = 0;
            boolean isDateOnly = true;
            for (int i2 = 0; i2 < dtLength; ++i2) {
                char c2 = dt.charAt((int)i2);
                if (!Character.isDigit((char)c2) && c2 != '-') {
                    isDateOnly = false;
                    break;
                }
                if (c2 != '-') continue;
                ++dashCount;
            }
            if (isDateOnly && dashCount == 2) {
                return "yyyy-MM-dd";
            }
        }
        boolean colonsOnly = true;
        for (int i3 = 0; i3 < dtLength; ++i3) {
            char c3 = dt.charAt((int)i3);
            if (Character.isDigit((char)c3) || c3 == ':') continue;
            colonsOnly = false;
            break;
        }
        if (colonsOnly) {
            return "HH:mm:ss";
        }
        StringReader reader = new StringReader((String)(dt + " "));
        ArrayList<Object[]> vec = new ArrayList<Object[]>();
        ArrayList<Object[]> vecRemovelist = new ArrayList<Object[]>();
        Object[] nv = new Object[]{Character.valueOf((char)'y'), new StringBuilder(), Integer.valueOf((int)0)};
        vec.add(nv);
        if (toTime) {
            nv = new Object[]{Character.valueOf((char)'h'), new StringBuilder(), Integer.valueOf((int)0)};
            vec.add(nv);
        }
        do {
            int z;
            int maxvecs;
            char separator;
            if ((z = reader.read()) != -1) {
                separator = (char)z;
                maxvecs = vec.size();
            } else {
                size = vec.size();
                break;
            }
            for (int count = 0; count < maxvecs; ++count) {
                v = (Object[])vec.get((int)count);
                n = ((Integer)v[2]).intValue();
                c = this.getSuccessor((char)((Character)v[0]).charValue(), (int)n);
                if (!Character.isLetterOrDigit((char)separator)) {
                    if (c == ((Character)v[0]).charValue() && c != 'S') {
                        vecRemovelist.add(v);
                        continue;
                    }
                    ((StringBuilder)v[1]).append((char)separator);
                    if (c != 'X' && c != 89) continue;
                    v[2] = Integer.valueOf((int)4);
                    continue;
                }
                if (c == 'X') {
                    c = 'y';
                    nv = new Object[3];
                    nv[1] = new StringBuilder((String)((StringBuilder)v[1]).toString()).append((char)'M');
                    nv[0] = Character.valueOf((char)'M');
                    nv[2] = Integer.valueOf((int)1);
                    vec.add(nv);
                } else if (c == 'Y') {
                    c = 'M';
                    nv = new Object[3];
                    nv[1] = new StringBuilder((String)((StringBuilder)v[1]).toString()).append((char)'d');
                    nv[0] = Character.valueOf((char)'d');
                    nv[2] = Integer.valueOf((int)1);
                    vec.add(nv);
                }
                ((StringBuilder)v[1]).append((char)c);
                if (c == ((Character)v[0]).charValue()) {
                    v[2] = Integer.valueOf((int)(n + 1));
                    continue;
                }
                v[0] = Character.valueOf((char)c);
                v[2] = Integer.valueOf((int)1);
            }
            size = vecRemovelist.size();
            for (i = 0; i < size; ++i) {
                v = (Object[])vecRemovelist.get((int)i);
                vec.remove((Object)v);
            }
            vecRemovelist.clear();
        } while (true);
        for (i = 0; i < size; ++i) {
            boolean containsEnd;
            v = (Object[])vec.get((int)i);
            c = ((Character)v[0]).charValue();
            boolean bk = this.getSuccessor((char)c, (int)(n = ((Integer)v[2]).intValue())) != c;
            boolean atEnd = (c == 's' || c == 'm' || c == 'h' && toTime) && bk;
            boolean finishesAtDate = bk && c == 'd' && !toTime;
            boolean bl = containsEnd = ((StringBuilder)v[1]).toString().indexOf((int)87) != -1;
            if ((atEnd || finishesAtDate) && !containsEnd) continue;
            vecRemovelist.add(v);
        }
        size = vecRemovelist.size();
        i = 0;
        do {
            if (i >= size) {
                vecRemovelist.clear();
                v = (Object[])vec.get((int)0);
                StringBuilder format = (StringBuilder)v[1];
                format.setLength((int)(format.length() - 1));
                return format.toString();
            }
            vec.remove(vecRemovelist.get((int)i));
            ++i;
        } while (true);
    }

    /*
     * Exception decompiling
     */
    @Override
    public java.sql.ResultSetMetaData getMetaData() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 7[CATCHBLOCK]
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
    protected boolean isSelectQuery() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return StringUtils.startsWithIgnoreCaseAndWs((String)StringUtils.stripComments((String)this.originalSql, (String)"'\"", (String)"'\"", (boolean)true, (boolean)false, (boolean)true, (boolean)true), (String)"SELECT");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.parameterMetaData == null) {
            if (this.connection.getGenerateSimpleParameterMetadata()) {
                this.parameterMetaData = new MysqlParameterMetadata((int)this.parameterCount);
                return this.parameterMetaData;
            }
            this.parameterMetaData = new MysqlParameterMetadata(null, (int)this.parameterCount, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return this.parameterMetaData;
    }

    ParseInfo getParseInfo() {
        return this.parseInfo;
    }

    private final char getSuccessor(char c, int n) {
        if (c == 'y' && n == 2) {
            return (char)88;
        }
        if (c == 'y' && n < 4) {
            return (char)121;
        }
        if (c == 'y') {
            return (char)77;
        }
        if (c == 'M' && n == 2) {
            return (char)89;
        }
        if (c == 'M' && n < 3) {
            return (char)77;
        }
        if (c == 'M') {
            return (char)100;
        }
        if (c == 'd' && n < 2) {
            return (char)100;
        }
        if (c == 'd') {
            return (char)72;
        }
        if (c == 'H' && n < 2) {
            return (char)72;
        }
        if (c == 'H') {
            return (char)109;
        }
        if (c == 'm' && n < 2) {
            return (char)109;
        }
        if (c == 'm') {
            return (char)115;
        }
        if (c != 's') return (char)87;
        if (n >= 2) return (char)87;
        return (char)115;
    }

    private final void hexEscapeBlock(byte[] buf, Buffer packet, int size) throws SQLException {
        int i = 0;
        while (i < size) {
            byte b = buf[i];
            int lowBits = (b & 255) / 16;
            int highBits = (b & 255) % 16;
            packet.writeByte((byte)HEX_DIGITS[lowBits]);
            packet.writeByte((byte)HEX_DIGITS[highBits]);
            ++i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeFromParseInfo() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.staticSqlStrings = this.parseInfo.staticSql;
        this.isLoadDataQuery = this.parseInfo.foundLoadData;
        this.firstCharOfStmt = this.parseInfo.firstStmtChar;
        this.parameterCount = this.staticSqlStrings.length - 1;
        this.parameterValues = new byte[this.parameterCount][];
        this.parameterStreams = new InputStream[this.parameterCount];
        this.isStream = new boolean[this.parameterCount];
        this.streamLengths = new int[this.parameterCount];
        this.isNull = new boolean[this.parameterCount];
        this.parameterTypes = new int[this.parameterCount];
        this.clearParameters();
        int j = 0;
        do {
            if (j >= this.parameterCount) {
                // MONITOREXIT : object
                return;
            }
            this.isStream[j] = false;
            ++j;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean isNull(int paramIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.isNull[paramIndex];
    }

    private final int readblock(InputStream i, byte[] b) throws SQLException {
        try {
            return i.read((byte[])b);
        }
        catch (Throwable ex) {
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.56") + ex.getClass().getName()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
        try {
            int lengthToRead = length;
            if (lengthToRead <= b.length) return i.read((byte[])b, (int)0, (int)lengthToRead);
            lengthToRead = b.length;
            return i.read((byte[])b, (int)0, (int)lengthToRead);
        }
        catch (Throwable ex) {
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.56") + ex.getClass().getName()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
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
        if (this.isClosed) {
            // MONITOREXIT : object
            return;
        }
        if (this.useUsageAdvisor && this.numberOfExecutions <= 1) {
            this.connection.getProfilerEventHandlerInstance().processEvent((byte)0, (MySQLConnection)this.connection, (Statement)this, null, (long)0L, (Throwable)new Throwable(), (String)Messages.getString((String)"PreparedStatement.43"));
        }
        super.realClose((boolean)calledExplicitly, (boolean)closeOpenResults);
        this.dbmd = null;
        this.originalSql = null;
        this.staticSqlStrings = (byte[][])null;
        this.parameterValues = (byte[][])null;
        this.parameterStreams = null;
        this.isStream = null;
        this.streamLengths = null;
        this.isNull = null;
        this.streamConvertBuf = null;
        this.parameterTypes = null;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setArray(int i, Array x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)12);
            return;
        }
        this.setBinaryStream((int)parameterIndex, (InputStream)x, (int)length);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)3);
            return;
        }
        this.setInternal((int)parameterIndex, (String)StringUtils.fixDecimalExponent((String)StringUtils.consistentToString((BigDecimal)x)));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 3;
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
        int parameterIndexOffset = this.getParameterIndexOffset();
        if (parameterIndex < 1) throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.2") + parameterIndex + Messages.getString((String)"PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString((String)"PreparedStatement.4")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        if (parameterIndex > this.staticSqlStrings.length) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.2") + parameterIndex + Messages.getString((String)"PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString((String)"PreparedStatement.4")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (parameterIndexOffset == -1 && parameterIndex == 1) {
            throw SQLError.createSQLException((String)"Can't set IN parameter for return value of stored function call.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.parameterStreams[parameterIndex - 1 + parameterIndexOffset] = x;
        this.isStream[parameterIndex - 1 + parameterIndexOffset] = true;
        this.streamLengths[parameterIndex - 1 + parameterIndexOffset] = length;
        this.isNull[parameterIndex - 1 + parameterIndexOffset] = false;
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 2004;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.setBinaryStream((int)parameterIndex, (InputStream)inputStream, (int)((int)length));
    }

    @Override
    public void setBlob(int i, Blob x) throws SQLException {
        if (x == null) {
            this.setNull((int)i, (int)2004);
            return;
        }
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        bytesOut.write((int)39);
        this.escapeblockFast((byte[])x.getBytes((long)1L, (int)((int)x.length())), (ByteArrayOutputStream)bytesOut, (int)((int)x.length()));
        bytesOut.write((int)39);
        this.setInternal((int)i, (byte[])bytesOut.toByteArray());
        this.parameterTypes[i - 1 + this.getParameterIndexOffset()] = 2004;
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        if (this.useTrueBoolean) {
            this.setInternal((int)parameterIndex, (String)(x ? "1" : "0"));
            return;
        }
        this.setInternal((int)parameterIndex, (String)(x ? "'t'" : "'f'"));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 16;
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.setInternal((int)parameterIndex, (String)String.valueOf((int)x));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = -6;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.setBytes((int)parameterIndex, (byte[])x, (boolean)true, (boolean)true);
        if (x == null) return;
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = -2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) throws SQLException {
        var5_5 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var5_5
        if (x == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        connectionEncoding = this.connection.getEncoding();
        try {
            if (this.connection.isNoBackslashEscapesSet() || escapeForMBChars && this.connection.getUseUnicode() && connectionEncoding != null && CharsetMapping.isMultibyteCharset((String)connectionEncoding)) {
                bOut = new ByteArrayOutputStream((int)(x.length * 2 + 3));
                bOut.write((int)120);
                bOut.write((int)39);
                i = 0;
                do {
                    if (i >= x.length) {
                        bOut.write((int)39);
                        this.setInternal((int)parameterIndex, (byte[])bOut.toByteArray());
                        // MONITOREXIT : var5_5
                        return;
                    }
                    lowBits = (x[i] & 255) / 16;
                    highBits = (x[i] & 255) % 16;
                    bOut.write((int)PreparedStatement.HEX_DIGITS[lowBits]);
                    bOut.write((int)PreparedStatement.HEX_DIGITS[highBits]);
                    ++i;
                } while (true);
            }
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        numBytes = x.length;
        pad = 2;
        v0 = needsIntroducer = checkForIntroducer != false && this.connection.versionMeetsMinimum((int)4, (int)1, (int)0) != false;
        if (needsIntroducer) {
            pad += 7;
        }
        bOut = new ByteArrayOutputStream((int)(numBytes + pad));
        if (needsIntroducer) {
            bOut.write((int)95);
            bOut.write((int)98);
            bOut.write((int)105);
            bOut.write((int)110);
            bOut.write((int)97);
            bOut.write((int)114);
            bOut.write((int)121);
        }
        bOut.write((int)39);
        i = 0;
        do {
            if (i >= numBytes) {
                bOut.write((int)39);
                this.setInternal((int)parameterIndex, (byte[])bOut.toByteArray());
                // MONITOREXIT : var5_5
                return;
            }
            b = x[i];
            switch (b) {
                case 0: {
                    bOut.write((int)92);
                    bOut.write((int)48);
                    ** break;
                }
                case 10: {
                    bOut.write((int)92);
                    bOut.write((int)110);
                    ** break;
                }
                case 13: {
                    bOut.write((int)92);
                    bOut.write((int)114);
                    ** break;
                }
                case 92: {
                    bOut.write((int)92);
                    bOut.write((int)92);
                    ** break;
                }
                case 39: {
                    bOut.write((int)92);
                    bOut.write((int)39);
                    ** break;
                }
                case 34: {
                    bOut.write((int)92);
                    bOut.write((int)34);
                    ** break;
                }
                case 26: {
                    bOut.write((int)92);
                    bOut.write((int)90);
                    ** break;
                }
            }
            bOut.write((int)b);
lbl86: // 8 sources:
            ++i;
        } while (true);
    }

    protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
        byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
        parameterWithQuotes[0] = 39;
        System.arraycopy((Object)parameterAsBytes, (int)0, (Object)parameterWithQuotes, (int)1, (int)parameterAsBytes.length);
        parameterWithQuotes[parameterAsBytes.length + 1] = 39;
        this.setInternal((int)parameterIndex, (byte[])parameterWithQuotes);
    }

    protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
        this.setInternal((int)parameterIndex, (byte[])parameterAsBytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            if (reader == null) {
                this.setNull((int)parameterIndex, (int)-1);
                return;
            }
            char[] c = null;
            int len = 0;
            boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
            String forcedEncoding = this.connection.getClobCharacterEncoding();
            if (useLength && length != -1) {
                c = new char[length];
                int numCharsRead = PreparedStatement.readFully((Reader)reader, (char[])c, (int)length);
                if (forcedEncoding == null) {
                    this.setString((int)parameterIndex, (String)new String((char[])c, (int)0, (int)numCharsRead));
                } else {
                    try {
                        this.setBytes((int)parameterIndex, (byte[])StringUtils.getBytes((String)new String((char[])c, (int)0, (int)numCharsRead), (String)forcedEncoding));
                    }
                    catch (UnsupportedEncodingException uee) {
                        throw SQLError.createSQLException((String)("Unsupported character encoding " + forcedEncoding), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            } else {
                c = new char[4096];
                StringBuilder buf = new StringBuilder();
                while ((len = reader.read((char[])c)) != -1) {
                    buf.append((char[])c, (int)0, (int)len);
                }
                if (forcedEncoding == null) {
                    this.setString((int)parameterIndex, (String)buf.toString());
                } else {
                    try {
                        this.setBytes((int)parameterIndex, (byte[])StringUtils.getBytes((String)buf.toString(), (String)forcedEncoding));
                    }
                    catch (UnsupportedEncodingException uee) {
                        throw SQLError.createSQLException((String)("Unsupported character encoding " + forcedEncoding), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            }
            this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 2005;
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createSQLException((String)ioEx.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setClob(int i, Clob x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)i, (int)2005);
            return;
        }
        String forcedEncoding = this.connection.getClobCharacterEncoding();
        if (forcedEncoding == null) {
            this.setString((int)i, (String)x.getSubString((long)1L, (int)((int)x.length())));
        } else {
            try {
                this.setBytes((int)i, (byte[])StringUtils.getBytes((String)x.getSubString((long)1L, (int)((int)x.length())), (String)forcedEncoding));
            }
            catch (UnsupportedEncodingException uee) {
                throw SQLError.createSQLException((String)("Unsupported character encoding " + forcedEncoding), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        this.parameterTypes[i - 1 + this.getParameterIndexOffset()] = 2005;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        this.setDate((int)parameterIndex, (java.sql.Date)x, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)91);
            return;
        }
        if (!this.useLegacyDatetimeCode) {
            this.newSetDateInternal((int)parameterIndex, (java.sql.Date)x, (Calendar)cal);
            return;
        }
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.ddf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)this.ddf, (String)"''yyyy-MM-dd''", (Calendar)cal, null);
        this.setInternal((int)parameterIndex, (String)this.ddf.format((Date)x));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 91;
        // MONITOREXIT : object
        return;
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
        this.setInternal((int)parameterIndex, (String)StringUtils.fixDecimalExponent((String)String.valueOf((double)x)));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 8;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.setInternal((int)parameterIndex, (String)StringUtils.fixDecimalExponent((String)String.valueOf((float)x)));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 6;
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.setInternal((int)parameterIndex, (String)String.valueOf((int)x));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 4;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void setInternal(int paramIndex, byte[] val) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int parameterIndexOffset = this.getParameterIndexOffset();
        this.checkBounds((int)paramIndex, (int)parameterIndexOffset);
        this.isStream[paramIndex - 1 + parameterIndexOffset] = false;
        this.isNull[paramIndex - 1 + parameterIndexOffset] = false;
        this.parameterStreams[paramIndex - 1 + parameterIndexOffset] = null;
        this.parameterValues[paramIndex - 1 + parameterIndexOffset] = val;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (paramIndex < 1) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.49") + paramIndex + Messages.getString((String)"PreparedStatement.50")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (paramIndex > this.parameterCount) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.51") + paramIndex + Messages.getString((String)"PreparedStatement.52") + this.parameterValues.length + Messages.getString((String)"PreparedStatement.53")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (parameterIndexOffset == -1 && paramIndex == 1) {
            throw SQLError.createSQLException((String)"Can't set IN parameter for return value of stored function call.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void setInternal(int paramIndex, String val) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        byte[] parameterAsBytes = null;
        parameterAsBytes = this.charConverter != null ? this.charConverter.toBytes((String)val) : StringUtils.getBytes((String)val, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor());
        this.setInternal((int)paramIndex, (byte[])parameterAsBytes);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.setInternal((int)parameterIndex, (String)String.valueOf((long)x));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = -5;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.setInternal((int)parameterIndex, (String)"null");
        this.isNull[parameterIndex - 1 + this.getParameterIndexOffset()] = true;
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 0;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String arg) throws SQLException {
        this.setNull((int)parameterIndex, (int)sqlType);
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 0;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
        if (parameterObj instanceof Boolean) {
            parameterAsNum = ((Boolean)parameterObj).booleanValue() != false ? Integer.valueOf((int)1) : Integer.valueOf((int)0);
        } else if (parameterObj instanceof String) {
            switch (targetSqlType) {
                case -7: {
                    if ("1".equals((Object)parameterObj) || "0".equals((Object)parameterObj)) {
                        parameterAsNum = Integer.valueOf((String)((String)parameterObj));
                        ** break;
                    }
                    parameterAsBoolean = "true".equalsIgnoreCase((String)((String)parameterObj));
                    parameterAsNum = parameterAsBoolean != false ? Integer.valueOf((int)1) : Integer.valueOf((int)0);
                    ** break;
                }
                case -6: 
                case 4: 
                case 5: {
                    parameterAsNum = Integer.valueOf((String)((String)parameterObj));
                    ** break;
                }
                case -5: {
                    parameterAsNum = Long.valueOf((String)((String)parameterObj));
                    ** break;
                }
                case 7: {
                    parameterAsNum = Float.valueOf((String)((String)parameterObj));
                    ** break;
                }
                case 6: 
                case 8: {
                    parameterAsNum = Double.valueOf((String)((String)parameterObj));
                    ** break;
                }
            }
            parameterAsNum = new BigDecimal((String)((String)parameterObj));
            ** break;
lbl27: // 7 sources:
        } else {
            parameterAsNum = (Number)parameterObj;
        }
        switch (targetSqlType) {
            case -7: 
            case -6: 
            case 4: 
            case 5: {
                this.setInt((int)parameterIndex, (int)parameterAsNum.intValue());
                return;
            }
            case -5: {
                this.setLong((int)parameterIndex, (long)parameterAsNum.longValue());
                return;
            }
            case 7: {
                this.setFloat((int)parameterIndex, (float)parameterAsNum.floatValue());
                return;
            }
            case 6: 
            case 8: {
                this.setDouble((int)parameterIndex, (double)parameterAsNum.doubleValue());
                return;
            }
            case 2: 
            case 3: {
                if (parameterAsNum instanceof BigDecimal) {
                    scaledBigDecimal = null;
                    try {
                        scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale((int)scale);
                    }
                    catch (ArithmeticException ex) {
                        try {
                            scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale((int)scale, (int)4);
                        }
                        catch (ArithmeticException arEx) {
                            throw SQLError.createSQLException((String)("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                    }
                    this.setBigDecimal((int)parameterIndex, (BigDecimal)scaledBigDecimal);
                    return;
                }
                if (parameterAsNum instanceof BigInteger) {
                    this.setBigDecimal((int)parameterIndex, (BigDecimal)new BigDecimal((BigInteger)((BigInteger)parameterAsNum), (int)scale));
                    return;
                }
                this.setBigDecimal((int)parameterIndex, (BigDecimal)new BigDecimal((double)parameterAsNum.doubleValue()));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (parameterObj == null) {
            this.setNull((int)parameterIndex, (int)1111);
            return;
        }
        if (parameterObj instanceof Byte) {
            this.setInt((int)parameterIndex, (int)((Byte)parameterObj).intValue());
            return;
        }
        if (parameterObj instanceof String) {
            this.setString((int)parameterIndex, (String)((String)parameterObj));
            return;
        }
        if (parameterObj instanceof BigDecimal) {
            this.setBigDecimal((int)parameterIndex, (BigDecimal)((BigDecimal)parameterObj));
            return;
        }
        if (parameterObj instanceof Short) {
            this.setShort((int)parameterIndex, (short)((Short)parameterObj).shortValue());
            return;
        }
        if (parameterObj instanceof Integer) {
            this.setInt((int)parameterIndex, (int)((Integer)parameterObj).intValue());
            return;
        }
        if (parameterObj instanceof Long) {
            this.setLong((int)parameterIndex, (long)((Long)parameterObj).longValue());
            return;
        }
        if (parameterObj instanceof Float) {
            this.setFloat((int)parameterIndex, (float)((Float)parameterObj).floatValue());
            return;
        }
        if (parameterObj instanceof Double) {
            this.setDouble((int)parameterIndex, (double)((Double)parameterObj).doubleValue());
            return;
        }
        if (parameterObj instanceof byte[]) {
            this.setBytes((int)parameterIndex, (byte[])((byte[])parameterObj));
            return;
        }
        if (parameterObj instanceof java.sql.Date) {
            this.setDate((int)parameterIndex, (java.sql.Date)((java.sql.Date)parameterObj));
            return;
        }
        if (parameterObj instanceof Time) {
            this.setTime((int)parameterIndex, (Time)((Time)parameterObj));
            return;
        }
        if (parameterObj instanceof Timestamp) {
            this.setTimestamp((int)parameterIndex, (Timestamp)((Timestamp)parameterObj));
            return;
        }
        if (parameterObj instanceof Boolean) {
            this.setBoolean((int)parameterIndex, (boolean)((Boolean)parameterObj).booleanValue());
            return;
        }
        if (parameterObj instanceof InputStream) {
            this.setBinaryStream((int)parameterIndex, (InputStream)((InputStream)parameterObj), (int)-1);
            return;
        }
        if (parameterObj instanceof Blob) {
            this.setBlob((int)parameterIndex, (Blob)((Blob)parameterObj));
            return;
        }
        if (parameterObj instanceof Clob) {
            this.setClob((int)parameterIndex, (Clob)((Clob)parameterObj));
            return;
        }
        if (this.connection.getTreatUtilDateAsTimestamp() && parameterObj instanceof Date) {
            this.setTimestamp((int)parameterIndex, (Timestamp)new Timestamp((long)((Date)parameterObj).getTime()));
            return;
        }
        if (parameterObj instanceof BigInteger) {
            this.setString((int)parameterIndex, (String)parameterObj.toString());
            return;
        }
        this.setSerializableObject((int)parameterIndex, (Object)parameterObj);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void setObject(int parameterIndex, Object parameterObj, int targetSqlType) throws SQLException {
        if (!(parameterObj instanceof BigDecimal)) {
            this.setObject((int)parameterIndex, (Object)parameterObj, (int)targetSqlType, (int)0);
            return;
        }
        this.setObject((int)parameterIndex, (Object)parameterObj, (int)targetSqlType, (int)((BigDecimal)parameterObj).scale());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (parameterObj == null) {
            this.setNull((int)parameterIndex, (int)1111);
            return;
        }
        try {
            switch (targetSqlType) {
                case 16: {
                    if (parameterObj instanceof Boolean) {
                        this.setBoolean((int)parameterIndex, (boolean)((Boolean)parameterObj).booleanValue());
                        return;
                    }
                    if (parameterObj instanceof String) {
                        this.setBoolean((int)parameterIndex, (boolean)("true".equalsIgnoreCase((String)((String)parameterObj)) || !"0".equalsIgnoreCase((String)((String)parameterObj))));
                        return;
                    }
                    if (!(parameterObj instanceof Number)) throw SQLError.createSQLException((String)("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    int intValue = ((Number)parameterObj).intValue();
                    this.setBoolean((int)parameterIndex, (boolean)(intValue != 0));
                    return;
                }
                case -7: 
                case -6: 
                case -5: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: {
                    this.setNumericObject((int)parameterIndex, (Object)parameterObj, (int)targetSqlType, (int)scale);
                    return;
                }
                case -1: 
                case 1: 
                case 12: {
                    if (parameterObj instanceof BigDecimal) {
                        this.setString((int)parameterIndex, (String)StringUtils.fixDecimalExponent((String)StringUtils.consistentToString((BigDecimal)((BigDecimal)parameterObj))));
                        return;
                    }
                    this.setString((int)parameterIndex, (String)parameterObj.toString());
                    return;
                }
                case 2005: {
                    if (parameterObj instanceof Clob) {
                        this.setClob((int)parameterIndex, (Clob)((Clob)parameterObj));
                        return;
                    }
                    this.setString((int)parameterIndex, (String)parameterObj.toString());
                    return;
                }
                case -4: 
                case -3: 
                case -2: 
                case 2004: {
                    if (parameterObj instanceof byte[]) {
                        this.setBytes((int)parameterIndex, (byte[])((byte[])parameterObj));
                        return;
                    }
                    if (parameterObj instanceof Blob) {
                        this.setBlob((int)parameterIndex, (Blob)((Blob)parameterObj));
                        return;
                    }
                    this.setBytes((int)parameterIndex, (byte[])StringUtils.getBytes((String)parameterObj.toString(), (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()));
                    return;
                }
                case 91: 
                case 93: {
                    Date parameterAsDate;
                    if (parameterObj instanceof String) {
                        ParsePosition pp = new ParsePosition((int)0);
                        SimpleDateFormat sdf = TimeUtil.getSimpleDateFormat(null, (String)this.getDateTimePattern((String)((String)parameterObj), (boolean)false), null, null);
                        parameterAsDate = ((DateFormat)sdf).parse((String)((String)parameterObj), (ParsePosition)pp);
                    } else {
                        parameterAsDate = (Date)parameterObj;
                    }
                    switch (targetSqlType) {
                        case 91: {
                            if (parameterAsDate instanceof java.sql.Date) {
                                this.setDate((int)parameterIndex, (java.sql.Date)((java.sql.Date)parameterAsDate));
                                return;
                            }
                            this.setDate((int)parameterIndex, (java.sql.Date)new java.sql.Date((long)parameterAsDate.getTime()));
                            return;
                        }
                        case 93: {
                            if (parameterAsDate instanceof Timestamp) {
                                this.setTimestamp((int)parameterIndex, (Timestamp)((Timestamp)parameterAsDate));
                                return;
                            }
                            this.setTimestamp((int)parameterIndex, (Timestamp)new Timestamp((long)parameterAsDate.getTime()));
                        }
                    }
                    return;
                }
                case 92: {
                    if (parameterObj instanceof String) {
                        SimpleDateFormat sdf = TimeUtil.getSimpleDateFormat(null, (String)this.getDateTimePattern((String)((String)parameterObj), (boolean)true), null, null);
                        this.setTime((int)parameterIndex, (Time)new Time((long)sdf.parse((String)((String)parameterObj)).getTime()));
                        return;
                    }
                    if (parameterObj instanceof Timestamp) {
                        Timestamp xT = (Timestamp)parameterObj;
                        this.setTime((int)parameterIndex, (Time)new Time((long)xT.getTime()));
                        return;
                    }
                    this.setTime((int)parameterIndex, (Time)((Time)parameterObj));
                    return;
                }
                case 1111: {
                    this.setSerializableObject((int)parameterIndex, (Object)parameterObj);
                    return;
                }
            }
            throw SQLError.createSQLException((String)Messages.getString((String)"PreparedStatement.16"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (Exception ex) {
            if (ex instanceof SQLException) {
                throw (SQLException)ex;
            }
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString((String)"PreparedStatement.18") + ex.getClass().getName() + Messages.getString((String)"PreparedStatement.19") + ex.getMessage()), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
        BatchParams paramArg = (BatchParams)paramSet;
        boolean[] isNullBatch = paramArg.isNull;
        boolean[] isStreamBatch = paramArg.isStream;
        int j = 0;
        while (j < isNullBatch.length) {
            if (isNullBatch[j]) {
                batchedStatement.setNull((int)batchedParamIndex++, (int)0);
            } else if (isStreamBatch[j]) {
                batchedStatement.setBinaryStream((int)batchedParamIndex++, (InputStream)paramArg.parameterStreams[j], (int)paramArg.streamLengths[j]);
            } else {
                ((PreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes((int)batchedParamIndex++, (byte[])paramArg.parameterStrings[j]);
            }
            ++j;
        }
        return batchedParamIndex;
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    private final void setSerializableObject(int parameterIndex, Object parameterObj) throws SQLException {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream((OutputStream)bytesOut);
            objectOut.writeObject((Object)parameterObj);
            objectOut.flush();
            objectOut.close();
            bytesOut.flush();
            bytesOut.close();
            byte[] buf = bytesOut.toByteArray();
            ByteArrayInputStream bytesIn = new ByteArrayInputStream((byte[])buf);
            this.setBinaryStream((int)parameterIndex, (InputStream)bytesIn, (int)buf.length);
            this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = -2;
            return;
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"PreparedStatement.54") + ex.getClass().getName()), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.setInternal((int)parameterIndex, (String)String.valueOf((int)x));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 5;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (x == null) {
            this.setNull((int)parameterIndex, (int)1);
            return;
        }
        this.checkClosed();
        int stringLength = x.length();
        if (this.connection.isNoBackslashEscapesSet()) {
            boolean needsHexEscape = this.isEscapeNeededForString((String)x, (int)stringLength);
            if (!needsHexEscape) {
                byte[] parameterAsBytes = null;
                StringBuilder quotedString = new StringBuilder((int)(x.length() + 2));
                quotedString.append((char)'\'');
                quotedString.append((String)x);
                quotedString.append((char)'\'');
                parameterAsBytes = !this.isLoadDataQuery ? StringUtils.getBytes((String)quotedString.toString(), (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()) : StringUtils.getBytes((String)quotedString.toString());
                this.setInternal((int)parameterIndex, (byte[])parameterAsBytes);
                return;
            }
            byte[] parameterAsBytes = null;
            parameterAsBytes = !this.isLoadDataQuery ? StringUtils.getBytes((String)x, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()) : StringUtils.getBytes((String)x);
            this.setBytes((int)parameterIndex, (byte[])parameterAsBytes);
            // MONITOREXIT : object
            return;
        }
        String parameterAsString = x;
        boolean needsQuoted = true;
        if (this.isLoadDataQuery || this.isEscapeNeededForString((String)x, (int)stringLength)) {
            needsQuoted = false;
            StringBuilder buf = new StringBuilder((int)((int)((double)x.length() * 1.1)));
            buf.append((char)'\'');
            block13 : for (int i = 0; i < stringLength; ++i) {
                char c = x.charAt((int)i);
                switch (c) {
                    case '\u0000': {
                        buf.append((char)'\\');
                        buf.append((char)'0');
                        continue block13;
                    }
                    case '\n': {
                        buf.append((char)'\\');
                        buf.append((char)'n');
                        continue block13;
                    }
                    case '\r': {
                        buf.append((char)'\\');
                        buf.append((char)'r');
                        continue block13;
                    }
                    case '\\': {
                        buf.append((char)'\\');
                        buf.append((char)'\\');
                        continue block13;
                    }
                    case '\'': {
                        buf.append((char)'\\');
                        buf.append((char)'\'');
                        continue block13;
                    }
                    case '\"': {
                        if (this.usingAnsiMode) {
                            buf.append((char)'\\');
                        }
                        buf.append((char)'\"');
                        continue block13;
                    }
                    case '\u001a': {
                        buf.append((char)'\\');
                        buf.append((char)'Z');
                        continue block13;
                    }
                    case '\u00a5': 
                    case '\u20a9': {
                        if (this.charsetEncoder != null) {
                            CharBuffer cbuf = CharBuffer.allocate((int)1);
                            ByteBuffer bbuf = ByteBuffer.allocate((int)1);
                            cbuf.put((char)c);
                            cbuf.position((int)0);
                            this.charsetEncoder.encode((CharBuffer)cbuf, (ByteBuffer)bbuf, (boolean)true);
                            if (bbuf.get((int)0) == 92) {
                                buf.append((char)'\\');
                            }
                        }
                        buf.append((char)c);
                        continue block13;
                    }
                }
                buf.append((char)c);
            }
            buf.append((char)'\'');
            parameterAsString = buf.toString();
        }
        byte[] parameterAsBytes = null;
        parameterAsBytes = !this.isLoadDataQuery ? (needsQuoted ? StringUtils.getBytesWrapped((String)parameterAsString, (char)'\'', (char)'\'', (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()) : StringUtils.getBytes((String)parameterAsString, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor())) : StringUtils.getBytes((String)parameterAsString);
        this.setInternal((int)parameterIndex, (byte[])parameterAsBytes);
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 12;
        // MONITOREXIT : object
        return;
    }

    private boolean isEscapeNeededForString(String x, int stringLength) {
        boolean needsHexEscape = false;
        int i = 0;
        while (i < stringLength) {
            char c = x.charAt((int)i);
            switch (c) {
                case '\u0000': {
                    return true;
                }
                case '\n': {
                    return true;
                }
                case '\r': {
                    return true;
                }
                case '\\': {
                    return true;
                }
                case '\'': {
                    return true;
                }
                case '\"': {
                    return true;
                }
                case '\u001a': {
                    needsHexEscape = true;
                }
            }
            if (needsHexEscape) {
                return needsHexEscape;
            }
            ++i;
        }
        return needsHexEscape;
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

    private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)92);
            return;
        }
        this.checkClosed();
        if (!this.useLegacyDatetimeCode) {
            this.newSetTimeInternal((int)parameterIndex, (Time)x, (Calendar)targetCalendar);
        } else {
            Calendar sessionCalendar = this.getCalendarInstanceForSessionOrNew();
            x = TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)x, (TimeZone)tz, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)rollForward);
            this.setInternal((int)parameterIndex, (String)("'" + x.toString() + "'"));
        }
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 92;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int fractLen = -1;
        if (!this.sendFractionalSeconds || !this.serverSupportsFracSecs) {
            fractLen = 0;
        } else if (this.parameterMetaData != null && this.parameterMetaData.metadata != null && this.parameterMetaData.metadata.fields != null && parameterIndex <= this.parameterMetaData.metadata.fields.length && parameterIndex >= 0 && this.parameterMetaData.metadata.getField((int)parameterIndex).getDecimals() > 0) {
            fractLen = this.parameterMetaData.metadata.getField((int)parameterIndex).getDecimals();
        }
        this.setTimestampInternal((int)parameterIndex, (Timestamp)x, (Calendar)cal, (TimeZone)cal.getTimeZone(), (boolean)true, (int)fractLen, (boolean)this.connection.getUseSSPSCompatibleTimezoneShift());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int fractLen = -1;
        if (!this.sendFractionalSeconds || !this.serverSupportsFracSecs) {
            fractLen = 0;
        } else if (this.parameterMetaData != null && this.parameterMetaData.metadata != null && this.parameterMetaData.metadata.fields != null && parameterIndex <= this.parameterMetaData.metadata.fields.length && parameterIndex >= 0) {
            fractLen = this.parameterMetaData.metadata.getField((int)parameterIndex).getDecimals();
        }
        this.setTimestampInternal((int)parameterIndex, (Timestamp)x, null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false, (int)fractLen, (boolean)this.connection.getUseSSPSCompatibleTimezoneShift());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward, int fractionalLength, boolean useSSPSCompatibleTimezoneShift) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)93);
            return;
        }
        this.checkClosed();
        x = (Timestamp)x.clone();
        if (!this.serverSupportsFracSecs || !this.sendFractionalSeconds && fractionalLength == 0) {
            x = TimeUtil.truncateFractionalSeconds((Timestamp)x);
        }
        if (fractionalLength < 0) {
            fractionalLength = 6;
        }
        x = TimeUtil.adjustTimestampNanosPrecision((Timestamp)x, (int)fractionalLength, (boolean)(!this.connection.isServerTruncatesFracSecs()));
        if (!this.useLegacyDatetimeCode) {
            this.newSetTimestampInternal((int)parameterIndex, (Timestamp)x, (Calendar)targetCalendar);
        } else {
            Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew();
            sessionCalendar = TimeUtil.setProlepticIfNeeded((Calendar)sessionCalendar, (Calendar)targetCalendar);
            x = TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)x, (TimeZone)tz, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)rollForward);
            if (useSSPSCompatibleTimezoneShift) {
                this.doSSPSCompatibleTimezoneShift((int)parameterIndex, (Timestamp)x, (int)fractionalLength, (Calendar)targetCalendar);
            } else {
                int nanos;
                PreparedStatement preparedStatement = this;
                // MONITORENTER : preparedStatement
                this.tsdf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)this.tsdf, (String)"''yyyy-MM-dd HH:mm:ss", null, null);
                Calendar adjCal = TimeUtil.setProlepticIfNeeded((Calendar)this.tsdf.getCalendar(), (Calendar)targetCalendar);
                if (this.tsdf.getCalendar() != adjCal) {
                    this.tsdf.setCalendar((Calendar)adjCal);
                }
                StringBuffer buf = new StringBuffer();
                buf.append((String)this.tsdf.format((Date)x));
                if (fractionalLength > 0 && (nanos = x.getNanos()) != 0) {
                    buf.append((char)'.');
                    buf.append((String)TimeUtil.formatNanos((int)nanos, (boolean)this.serverSupportsFracSecs, (int)fractionalLength));
                }
                buf.append((char)'\'');
                this.setInternal((int)parameterIndex, (String)buf.toString());
                // MONITOREXIT : preparedStatement
            }
        }
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 93;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.tsdf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)this.tsdf, (String)"''yyyy-MM-dd HH:mm:ss", (Calendar)targetCalendar, targetCalendar != null ? null : this.connection.getServerTimezoneTZ());
        StringBuffer buf = new StringBuffer();
        buf.append((String)this.tsdf.format((Date)x));
        buf.append((char)'.');
        buf.append((String)TimeUtil.formatNanos((int)x.getNanos(), (boolean)this.serverSupportsFracSecs, (int)6));
        buf.append((char)'\'');
        this.setInternal((int)parameterIndex, (String)buf.toString());
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.tdf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)this.tdf, (String)"''HH:mm:ss''", (Calendar)targetCalendar, targetCalendar != null ? null : this.connection.getServerTimezoneTZ());
        this.setInternal((int)parameterIndex, (String)this.tdf.format((Date)x));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void newSetDateInternal(int parameterIndex, java.sql.Date x, Calendar targetCalendar) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.ddf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)this.ddf, (String)"''yyyy-MM-dd''", (Calendar)targetCalendar, targetCalendar != null ? null : (this.connection.getNoTimezoneConversionForDateType() ? this.connection.getDefaultTimeZone() : this.connection.getServerTimezoneTZ()));
        this.setInternal((int)parameterIndex, (String)this.ddf.format((Date)x));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doSSPSCompatibleTimezoneShift(int parameterIndex, Timestamp x, int fractionalLength, Calendar targetCalendar) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Calendar sessionCalendar2 = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew();
        Calendar calendar = sessionCalendar2 = TimeUtil.setProlepticIfNeeded((Calendar)sessionCalendar2, (Calendar)targetCalendar);
        // MONITORENTER : calendar
        Date oldTime = sessionCalendar2.getTime();
        try {
            sessionCalendar2.setTime((Date)x);
            int year = sessionCalendar2.get((int)1);
            int month = sessionCalendar2.get((int)2) + 1;
            int date = sessionCalendar2.get((int)5);
            int hour = sessionCalendar2.get((int)11);
            int minute = sessionCalendar2.get((int)12);
            int seconds = sessionCalendar2.get((int)13);
            StringBuilder tsBuf = new StringBuilder();
            tsBuf.append((char)'\'');
            tsBuf.append((int)year);
            tsBuf.append((String)"-");
            if (month < 10) {
                tsBuf.append((char)'0');
            }
            tsBuf.append((int)month);
            tsBuf.append((char)'-');
            if (date < 10) {
                tsBuf.append((char)'0');
            }
            tsBuf.append((int)date);
            tsBuf.append((char)' ');
            if (hour < 10) {
                tsBuf.append((char)'0');
            }
            tsBuf.append((int)hour);
            tsBuf.append((char)':');
            if (minute < 10) {
                tsBuf.append((char)'0');
            }
            tsBuf.append((int)minute);
            tsBuf.append((char)':');
            if (seconds < 10) {
                tsBuf.append((char)'0');
            }
            tsBuf.append((int)seconds);
            tsBuf.append((char)'.');
            tsBuf.append((String)TimeUtil.formatNanos((int)x.getNanos(), (boolean)this.serverSupportsFracSecs, (int)fractionalLength));
            tsBuf.append((char)'\'');
            this.setInternal((int)parameterIndex, (String)tsBuf.toString());
            Object var17_16 = null;
            sessionCalendar2.setTime((Date)oldTime);
            return;
        }
        catch (Throwable throwable) {
            Object var17_17 = null;
            sessionCalendar2.setTime((Date)oldTime);
            throw throwable;
        }
    }

    @Deprecated
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (x == null) {
            this.setNull((int)parameterIndex, (int)12);
            return;
        }
        this.setBinaryStream((int)parameterIndex, (InputStream)x, (int)length);
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 2005;
    }

    @Override
    public void setURL(int parameterIndex, URL arg) throws SQLException {
        if (arg != null) {
            this.setString((int)parameterIndex, (String)arg.toString());
            this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 70;
            return;
        }
        this.setNull((int)parameterIndex, (int)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
        var6_6 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var6_6
        try {
            if (this.streamConvertBuf == null) {
                this.streamConvertBuf = new byte[4096];
            }
            connectionEncoding = this.connection.getEncoding();
            hexEscape = false;
            try {
                if (this.connection.isNoBackslashEscapesSet() || this.connection.getUseUnicode() && connectionEncoding != null && CharsetMapping.isMultibyteCharset((String)connectionEncoding) && !this.connection.parserKnowsUnicode()) {
                    hexEscape = true;
                }
            }
            catch (RuntimeException ex) {
                sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
                sqlEx.initCause((Throwable)ex);
                throw sqlEx;
            }
            if (streamLength == -1) {
                useLength = false;
            }
            bc = -1;
            bc = useLength != false ? this.readblock((InputStream)in, (byte[])this.streamConvertBuf, (int)streamLength) : this.readblock((InputStream)in, (byte[])this.streamConvertBuf);
            lengthLeftToRead = streamLength - bc;
            if (hexEscape) {
                packet.writeStringNoNull((String)"x");
            } else if (this.connection.getIO().versionMeetsMinimum((int)4, (int)1, (int)0)) {
                packet.writeStringNoNull((String)"_binary");
            }
            if (escape) {
                packet.writeByte((byte)39);
            }
            while (bc > 0) {
                if (hexEscape) {
                    this.hexEscapeBlock((byte[])this.streamConvertBuf, (Buffer)packet, (int)bc);
                } else if (escape) {
                    this.escapeblockFast((byte[])this.streamConvertBuf, (Buffer)packet, (int)bc);
                } else {
                    packet.writeBytesNoNull((byte[])this.streamConvertBuf, (int)0, (int)bc);
                }
                if (useLength) {
                    bc = this.readblock((InputStream)in, (byte[])this.streamConvertBuf, (int)lengthLeftToRead);
                    if (bc <= 0) continue;
                    lengthLeftToRead -= bc;
                    continue;
                }
                bc = this.readblock((InputStream)in, (byte[])this.streamConvertBuf);
            }
            if (escape) {
                packet.writeByte((byte)39);
            }
            var12_13 = null;
            if (!this.connection.getAutoClosePStmtStreams()) {
                // MONITOREXIT : var6_6
                return;
            }
            try {
                in.close();
                return;
            }
            catch (IOException ioEx) {
                // empty catch block
            }
            return;
        }
        catch (Throwable var11_17) {
            block24 : {
                var12_14 = null;
                if (this.connection.getAutoClosePStmtStreams() == false) throw var11_17;
                ** try [egrp 3[TRYBLOCK] [3 : 363->370)] { 
lbl62: // 1 sources:
                in.close();
                break block24;
lbl64: // 1 sources:
                catch (IOException ioEx) {
                    // empty catch block
                }
            }
            in = null;
            throw var11_17;
        }
    }

    /*
     * Exception decompiling
     */
    private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [3[TRYBLOCK]], but top level block is 8[CATCHBLOCK]
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

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append((String)Object.super.toString());
        buf.append((String)": ");
        try {
            buf.append((String)this.asSql());
            return buf.toString();
        }
        catch (SQLException sqlEx) {
            buf.append((String)("EXCEPTION: " + sqlEx.toString()));
        }
        return buf.toString();
    }

    protected int getParameterIndexOffset() {
        return 0;
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        this.setAsciiStream((int)parameterIndex, (InputStream)x, (int)-1);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.setAsciiStream((int)parameterIndex, (InputStream)x, (int)((int)length));
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 2005;
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        this.setBinaryStream((int)parameterIndex, (InputStream)x, (int)-1);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.setBinaryStream((int)parameterIndex, (InputStream)x, (int)((int)length));
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        this.setBinaryStream((int)parameterIndex, (InputStream)inputStream);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        this.setCharacterStream((int)parameterIndex, (Reader)reader, (int)-1);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        this.setCharacterStream((int)parameterIndex, (Reader)reader, (int)((int)length));
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        this.setCharacterStream((int)parameterIndex, (Reader)reader);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.setCharacterStream((int)parameterIndex, (Reader)reader, (long)length);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        this.setNCharacterStream((int)parameterIndex, (Reader)value, (long)-1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.charEncoding.equalsIgnoreCase((String)"UTF-8") || this.charEncoding.equalsIgnoreCase((String)"utf8")) {
            this.setString((int)parameterIndex, (String)x);
            // MONITOREXIT : object
            return;
        }
        if (x == null) {
            this.setNull((int)parameterIndex, (int)1);
            return;
        }
        int stringLength = x.length();
        StringBuilder buf = new StringBuilder((int)((int)((double)x.length() * 1.1 + 4.0)));
        buf.append((String)"_utf8");
        buf.append((char)'\'');
        block12 : for (int i = 0; i < stringLength; ++i) {
            char c = x.charAt((int)i);
            switch (c) {
                case '\u0000': {
                    buf.append((char)'\\');
                    buf.append((char)'0');
                    continue block12;
                }
                case '\n': {
                    buf.append((char)'\\');
                    buf.append((char)'n');
                    continue block12;
                }
                case '\r': {
                    buf.append((char)'\\');
                    buf.append((char)'r');
                    continue block12;
                }
                case '\\': {
                    buf.append((char)'\\');
                    buf.append((char)'\\');
                    continue block12;
                }
                case '\'': {
                    buf.append((char)'\\');
                    buf.append((char)'\'');
                    continue block12;
                }
                case '\"': {
                    if (this.usingAnsiMode) {
                        buf.append((char)'\\');
                    }
                    buf.append((char)'\"');
                    continue block12;
                }
                case '\u001a': {
                    buf.append((char)'\\');
                    buf.append((char)'Z');
                    continue block12;
                }
            }
            buf.append((char)c);
        }
        buf.append((char)'\'');
        String parameterAsString = buf.toString();
        byte[] parameterAsBytes = null;
        parameterAsBytes = !this.isLoadDataQuery ? StringUtils.getBytes((String)parameterAsString, (SingleByteCharsetConverter)this.connection.getCharsetConverter((String)"UTF-8"), (String)"UTF-8", (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()) : StringUtils.getBytes((String)parameterAsString);
        this.setInternal((int)parameterIndex, (byte[])parameterAsBytes);
        this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = -9;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        try {
            if (reader == null) {
                this.setNull((int)parameterIndex, (int)-1);
                return;
            }
            char[] c = null;
            int len = 0;
            boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
            if (useLength && length != -1L) {
                c = new char[(int)length];
                int numCharsRead = PreparedStatement.readFully((Reader)reader, (char[])c, (int)((int)length));
                this.setNString((int)parameterIndex, (String)new String((char[])c, (int)0, (int)numCharsRead));
            } else {
                c = new char[4096];
                StringBuilder buf = new StringBuilder();
                while ((len = reader.read((char[])c)) != -1) {
                    buf.append((char[])c, (int)0, (int)len);
                }
                this.setNString((int)parameterIndex, (String)buf.toString());
            }
            this.parameterTypes[parameterIndex - 1 + this.getParameterIndexOffset()] = 2011;
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createSQLException((String)ioEx.toString(), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        this.setNCharacterStream((int)parameterIndex, (Reader)reader);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (reader == null) {
            this.setNull((int)parameterIndex, (int)-1);
            return;
        }
        this.setNCharacterStream((int)parameterIndex, (Reader)reader, (long)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ParameterBindings getParameterBindings() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return new EmulatedPreparedStatementBindings((PreparedStatement)this);
    }

    public String getPreparedSql() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (this.rewrittenBatchSize == 0) {
                // MONITOREXIT : object
                return this.originalSql;
            }
            try {
                // MONITOREXIT : object
                return this.parseInfo.getSqlForBatch((ParseInfo)this.parseInfo);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException((Throwable)e);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public int getUpdateCount() throws SQLException {
        int count = super.getUpdateCount();
        if (!this.containsOnDuplicateKeyUpdateInSQL()) return count;
        if (!this.compensateForOnDuplicateKeyUpdate) return count;
        if (count == 2) return 1;
        if (count != 0) return count;
        return 1;
    }

    protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos) {
        if (StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"INSERT", (int)statementStartPos)) {
            if (StringUtils.indexOfIgnoreCase((int)statementStartPos, (String)sql, (String)"SELECT", (String)"\"'`", (String)"\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) {
                return false;
            }
            if (!isOnDuplicateKeyUpdate) return true;
            int updateClausePos = StringUtils.indexOfIgnoreCase((int)locationOfOnDuplicateKeyUpdate, (String)sql, (String)" UPDATE ");
            if (updateClausePos == -1) return true;
            if (StringUtils.indexOfIgnoreCase((int)updateClausePos, (String)sql, (String)"LAST_INSERT_ID", (String)"\"'`", (String)"\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) return false;
            return true;
        }
        if (!StringUtils.startsWithIgnoreCaseAndWs((String)sql, (String)"REPLACE", (int)statementStartPos)) return false;
        if (StringUtils.indexOfIgnoreCase((int)statementStartPos, (String)sql, (String)"SELECT", (String)"\"'`", (String)"\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) return false;
        return true;
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        return this.executeUpdateInternal((boolean)true, (boolean)false);
    }

    static {
        if (Util.isJdbc4()) {
            try {
                String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42PreparedStatement" : "com.mysql.jdbc.JDBC4PreparedStatement";
                JDBC_4_PSTMT_2_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, String.class);
                JDBC_4_PSTMT_3_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, String.class, String.class);
                JDBC_4_PSTMT_4_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(MySQLConnection.class, String.class, String.class, ParseInfo.class);
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
            JDBC_4_PSTMT_2_ARG_CTOR = null;
            JDBC_4_PSTMT_3_ARG_CTOR = null;
            JDBC_4_PSTMT_4_ARG_CTOR = null;
        }
        HEX_DIGITS = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    }
}

