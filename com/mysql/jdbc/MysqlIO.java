/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AuthenticationPlugin;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.BufferRow;
import com.mysql.jdbc.ByteArrayRow;
import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.CompressedInputStream;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionFeatureNotAvailableException;
import com.mysql.jdbc.Constants;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.ExportControlled;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.NetworkResources;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.PacketTooBigException;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.RowDataCursor;
import com.mysql.jdbc.RowDataDynamic;
import com.mysql.jdbc.RowDataStatic;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import com.mysql.jdbc.ServerPreparedStatement;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.authentication.CachingSha2PasswordPlugin;
import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;
import com.mysql.jdbc.authentication.MysqlNativePasswordPlugin;
import com.mysql.jdbc.authentication.MysqlOldPasswordPlugin;
import com.mysql.jdbc.authentication.Sha256PasswordPlugin;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import com.mysql.jdbc.util.ReadAheadInputStream;
import com.mysql.jdbc.util.ResultSetUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.Deflater;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MysqlIO {
    private static final String CODE_PAGE_1252 = "Cp1252";
    protected static final int NULL_LENGTH = -1;
    protected static final int COMP_HEADER_LENGTH = 3;
    protected static final int MIN_COMPRESS_LEN = 50;
    protected static final int HEADER_LENGTH = 4;
    protected static final int AUTH_411_OVERHEAD = 33;
    public static final int SEED_LENGTH = 20;
    private static int maxBufferSize = 65535;
    private static final String NONE = "none";
    private static final int CLIENT_LONG_PASSWORD = 1;
    private static final int CLIENT_FOUND_ROWS = 2;
    private static final int CLIENT_LONG_FLAG = 4;
    protected static final int CLIENT_CONNECT_WITH_DB = 8;
    private static final int CLIENT_COMPRESS = 32;
    private static final int CLIENT_LOCAL_FILES = 128;
    private static final int CLIENT_PROTOCOL_41 = 512;
    private static final int CLIENT_INTERACTIVE = 1024;
    protected static final int CLIENT_SSL = 2048;
    private static final int CLIENT_TRANSACTIONS = 8192;
    protected static final int CLIENT_RESERVED = 16384;
    protected static final int CLIENT_SECURE_CONNECTION = 32768;
    private static final int CLIENT_MULTI_STATEMENTS = 65536;
    private static final int CLIENT_MULTI_RESULTS = 131072;
    private static final int CLIENT_PLUGIN_AUTH = 524288;
    private static final int CLIENT_CONNECT_ATTRS = 1048576;
    private static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 2097152;
    private static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
    private static final int CLIENT_SESSION_TRACK = 8388608;
    private static final int CLIENT_DEPRECATE_EOF = 16777216;
    private static final int SERVER_STATUS_IN_TRANS = 1;
    private static final int SERVER_STATUS_AUTOCOMMIT = 2;
    static final int SERVER_MORE_RESULTS_EXISTS = 8;
    private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
    private static final int SERVER_QUERY_NO_INDEX_USED = 32;
    private static final int SERVER_QUERY_WAS_SLOW = 2048;
    private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
    private static final String FALSE_SCRAMBLE = "xxxxxxxx";
    protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
    protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
    protected static final int INITIAL_PACKET_SIZE = 1024;
    private static String jvmPlatformCharset = null;
    protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
    protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
    private static final String EXPLAINABLE_STATEMENT = "SELECT";
    private static final String[] EXPLAINABLE_STATEMENT_EXTENSION = new String[]{"INSERT", "UPDATE", "REPLACE", "DELETE"};
    private static final int MAX_PACKET_DUMP_LENGTH = 1024;
    private boolean packetSequenceReset = false;
    protected int serverCharsetIndex;
    private Buffer reusablePacket = null;
    private Buffer sendPacket = null;
    private Buffer sharedSendPacket = null;
    protected BufferedOutputStream mysqlOutput = null;
    protected MySQLConnection connection;
    private Deflater deflater = null;
    protected InputStream mysqlInput = null;
    private LinkedList<StringBuilder> packetDebugRingBuffer = null;
    private RowData streamingData = null;
    public Socket mysqlConnection = null;
    protected SocketFactory socketFactory = null;
    private SoftReference<Buffer> loadFileBufRef;
    private SoftReference<Buffer> splitBufRef;
    private SoftReference<Buffer> compressBufRef;
    protected String host = null;
    protected String seed;
    private String serverVersion = null;
    private String socketFactoryClassName = null;
    private byte[] packetHeaderBuf = new byte[4];
    private boolean colDecimalNeedsBump = false;
    private boolean hadWarnings = false;
    private boolean has41NewNewProt = false;
    private boolean hasLongColumnInfo = false;
    private boolean isInteractiveClient = false;
    private boolean logSlowQueries = false;
    private boolean platformDbCharsetMatches = true;
    private boolean profileSql = false;
    private boolean queryBadIndexUsed = false;
    private boolean queryNoIndexUsed = false;
    private boolean serverQueryWasSlow = false;
    private boolean use41Extensions = false;
    private boolean useCompression = false;
    private boolean useNewLargePackets = false;
    private boolean useNewUpdateCounts = false;
    private byte packetSequence = 0;
    private byte compressedPacketSequence = 0;
    private byte readPacketSequence = (byte)-1;
    private boolean checkPacketSequence = false;
    private byte protocolVersion = 0;
    private int maxAllowedPacket = 1048576;
    protected int maxThreeBytes = 16581375;
    protected int port = 3306;
    protected int serverCapabilities;
    private int serverMajorVersion = 0;
    private int serverMinorVersion = 0;
    private int oldServerStatus = 0;
    private int serverStatus = 0;
    private int serverSubMinorVersion = 0;
    private int warningCount = 0;
    protected long clientParam = 0L;
    protected long lastPacketSentTimeMs = 0L;
    protected long lastPacketReceivedTimeMs = 0L;
    private boolean traceProtocol = false;
    private boolean enablePacketDebug = false;
    private boolean useConnectWithDb;
    private boolean needToGrabQueryFromPacket;
    private boolean autoGenerateTestcaseScript;
    private long threadId;
    private boolean useNanosForElapsedTime;
    private long slowQueryThreshold;
    private String queryTimingUnits;
    private boolean useDirectRowUnpack = true;
    private int useBufferRowSizeThreshold;
    private int commandCount = 0;
    private List<StatementInterceptorV2> statementInterceptors;
    private ExceptionInterceptor exceptionInterceptor;
    private int authPluginDataLength = 0;
    private Map<String, AuthenticationPlugin> authenticationPlugins = null;
    private List<String> disabledAuthenticationPlugins = null;
    private String clientDefaultAuthenticationPlugin = null;
    private String clientDefaultAuthenticationPluginName = null;
    private String serverDefaultAuthenticationPluginName = null;
    private int statementExecutionDepth = 0;
    private boolean useAutoSlowLog;

    public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, MySQLConnection conn, int socketTimeout, int useBufferRowSizeThreshold) throws IOException, SQLException {
        this.connection = conn;
        if (this.connection.getEnablePacketDebug()) {
            this.packetDebugRingBuffer = new LinkedList<E>();
        }
        this.traceProtocol = this.connection.getTraceProtocol();
        this.useAutoSlowLog = this.connection.getAutoSlowLog();
        this.useBufferRowSizeThreshold = useBufferRowSizeThreshold;
        this.useDirectRowUnpack = this.connection.getUseDirectRowUnpack();
        this.logSlowQueries = this.connection.getLogSlowQueries();
        this.reusablePacket = new Buffer((int)1024);
        this.sendPacket = new Buffer((int)1024);
        this.port = port;
        this.host = host;
        this.socketFactoryClassName = socketFactoryClassName;
        this.socketFactory = this.createSocketFactory();
        this.exceptionInterceptor = this.connection.getExceptionInterceptor();
        try {
            this.mysqlConnection = this.socketFactory.connect((String)this.host, (int)this.port, (Properties)props);
            if (socketTimeout != 0) {
                try {
                    this.mysqlConnection.setSoTimeout((int)socketTimeout);
                }
                catch (Exception ex) {
                    // empty catch block
                }
            }
            this.mysqlConnection = this.socketFactory.beforeHandshake();
            this.mysqlInput = this.connection.getUseReadAheadInput() ? new ReadAheadInputStream((InputStream)this.mysqlConnection.getInputStream(), (int)16384, (boolean)this.connection.getTraceProtocol(), (Log)this.connection.getLog()) : (this.connection.useUnbufferedInput() ? this.mysqlConnection.getInputStream() : new BufferedInputStream((InputStream)this.mysqlConnection.getInputStream(), (int)16384));
            this.mysqlOutput = new BufferedOutputStream((OutputStream)this.mysqlConnection.getOutputStream(), (int)16384);
            this.isInteractiveClient = this.connection.getInteractiveClient();
            this.profileSql = this.connection.getProfileSql();
            this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
            this.needToGrabQueryFromPacket = this.profileSql || this.logSlowQueries || this.autoGenerateTestcaseScript;
            this.useNanosForElapsedTime = this.connection.getUseNanosForElapsedTime() && TimeUtil.nanoTimeAvailable();
            this.queryTimingUnits = this.useNanosForElapsedTime ? Messages.getString((String)"Nanoseconds") : Messages.getString((String)"Milliseconds");
            if (!this.connection.getLogSlowQueries()) return;
            this.calculateSlowQueryThreshold();
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)0L, (long)0L, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    public boolean hasLongColumnInfo() {
        return this.hasLongColumnInfo;
    }

    protected boolean isDataAvailable() throws SQLException {
        try {
            if (this.mysqlInput.available() <= 0) return false;
            return true;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    protected long getLastPacketSentTimeMs() {
        return this.lastPacketSentTimeMs;
    }

    protected long getLastPacketReceivedTimeMs() {
        return this.lastPacketReceivedTimeMs;
    }

    protected ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache) throws SQLException {
        int i;
        Field[] fields = null;
        if (metadataFromCache == null) {
            fields = new Field[(int)columnCount];
            i = 0;
            while ((long)i < columnCount) {
                Buffer fieldPacket = null;
                fieldPacket = this.readPacket();
                fields[i] = this.unpackField((Buffer)fieldPacket, (boolean)false);
                ++i;
            }
        } else {
            i = 0;
            while ((long)i < columnCount) {
                this.skipPacket();
                ++i;
            }
        }
        if (!this.isEOFDeprecated() || this.connection.versionMeetsMinimum((int)5, (int)0, (int)2) && callingStatement != null && isBinaryEncoded && callingStatement.isCursorRequired()) {
            Buffer packet = this.reuseAndReadPacket((Buffer)this.reusablePacket);
            this.readServerStatusForResultSets((Buffer)packet);
        }
        if (this.connection.versionMeetsMinimum((int)5, (int)0, (int)2) && this.connection.getUseCursorFetch() && isBinaryEncoded && callingStatement != null && callingStatement.getFetchSize() != 0 && callingStatement.getResultSetType() == 1003) {
            ServerPreparedStatement prepStmt = (ServerPreparedStatement)callingStatement;
            boolean usingCursor = true;
            if (this.connection.versionMeetsMinimum((int)5, (int)0, (int)5)) {
                boolean bl = usingCursor = (this.serverStatus & 64) != 0;
            }
            if (usingCursor) {
                RowDataCursor rows = new RowDataCursor((MysqlIO)this, (ServerPreparedStatement)prepStmt, (Field[])fields);
                ResultSetImpl rs = this.buildResultSetWithRows((StatementImpl)callingStatement, (String)catalog, (Field[])fields, (RowData)rows, (int)resultSetType, (int)resultSetConcurrency, (boolean)isBinaryEncoded);
                if (!usingCursor) return rs;
                rs.setFetchSize((int)callingStatement.getFetchSize());
                return rs;
            }
        }
        RowData rowData = null;
        if (!streamResults) {
            rowData = this.readSingleRowSet((long)columnCount, (int)maxRows, (int)resultSetConcurrency, (boolean)isBinaryEncoded, (Field[])(metadataFromCache == null ? fields : metadataFromCache));
            return this.buildResultSetWithRows((StatementImpl)callingStatement, (String)catalog, (Field[])(metadataFromCache == null ? fields : metadataFromCache), (RowData)rowData, (int)resultSetType, (int)resultSetConcurrency, (boolean)isBinaryEncoded);
        } else {
            this.streamingData = rowData = new RowDataDynamic((MysqlIO)this, (int)((int)columnCount), (Field[])(metadataFromCache == null ? fields : metadataFromCache), (boolean)isBinaryEncoded);
        }
        return this.buildResultSetWithRows((StatementImpl)callingStatement, (String)catalog, (Field[])(metadataFromCache == null ? fields : metadataFromCache), (RowData)rowData, (int)resultSetType, (int)resultSetConcurrency, (boolean)isBinaryEncoded);
    }

    protected NetworkResources getNetworkResources() {
        return new NetworkResources((Socket)this.mysqlConnection, (InputStream)this.mysqlInput, (OutputStream)this.mysqlOutput);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void forceClose() {
        try {
            this.getNetworkResources().forceClose();
            Object var2_1 = null;
            this.mysqlConnection = null;
            this.mysqlInput = null;
            this.mysqlOutput = null;
            return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.mysqlConnection = null;
            this.mysqlInput = null;
            this.mysqlOutput = null;
            throw throwable;
        }
    }

    protected final void skipPacket() throws SQLException {
        try {
            int lengthRead = this.readFully((InputStream)this.mysqlInput, (byte[])this.packetHeaderBuf, (int)0, (int)4);
            if (lengthRead < 4) {
                this.forceClose();
                throw new IOException((String)Messages.getString((String)"MysqlIO.1"));
            }
            int packetLength = (this.packetHeaderBuf[0] & 255) + ((this.packetHeaderBuf[1] & 255) << 8) + ((this.packetHeaderBuf[2] & 255) << 16);
            if (this.traceProtocol) {
                StringBuilder traceMessageBuf = new StringBuilder();
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.2"));
                traceMessageBuf.append((int)packetLength);
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.3"));
                traceMessageBuf.append((String)StringUtils.dumpAsHex((byte[])this.packetHeaderBuf, (int)4));
                this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
            }
            byte multiPacketSeq = this.packetHeaderBuf[3];
            if (!this.packetSequenceReset) {
                if (this.enablePacketDebug && this.checkPacketSequence) {
                    this.checkPacketSequencing((byte)multiPacketSeq);
                }
            } else {
                this.packetSequenceReset = false;
            }
            this.readPacketSequence = multiPacketSeq;
            this.skipFully((InputStream)this.mysqlInput, (long)((long)packetLength));
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (OutOfMemoryError oom) {
            try {
                this.connection.realClose((boolean)false, (boolean)false, (boolean)true, (Throwable)oom);
                throw oom;
            }
            catch (Exception ex) {
                // empty catch block
            }
            throw oom;
        }
    }

    protected final Buffer readPacket() throws SQLException {
        try {
            int lengthRead = this.readFully((InputStream)this.mysqlInput, (byte[])this.packetHeaderBuf, (int)0, (int)4);
            if (lengthRead < 4) {
                this.forceClose();
                throw new IOException((String)Messages.getString((String)"MysqlIO.1"));
            }
            int packetLength = (this.packetHeaderBuf[0] & 255) + ((this.packetHeaderBuf[1] & 255) << 8) + ((this.packetHeaderBuf[2] & 255) << 16);
            if (packetLength > this.maxAllowedPacket) {
                throw new PacketTooBigException((long)((long)packetLength), (long)((long)this.maxAllowedPacket));
            }
            if (this.traceProtocol) {
                StringBuilder traceMessageBuf = new StringBuilder();
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.2"));
                traceMessageBuf.append((int)packetLength);
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.3"));
                traceMessageBuf.append((String)StringUtils.dumpAsHex((byte[])this.packetHeaderBuf, (int)4));
                this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
            }
            byte multiPacketSeq = this.packetHeaderBuf[3];
            if (!this.packetSequenceReset) {
                if (this.enablePacketDebug && this.checkPacketSequence) {
                    this.checkPacketSequencing((byte)multiPacketSeq);
                }
            } else {
                this.packetSequenceReset = false;
            }
            this.readPacketSequence = multiPacketSeq;
            byte[] buffer = new byte[packetLength];
            int numBytesRead = this.readFully((InputStream)this.mysqlInput, (byte[])buffer, (int)0, (int)packetLength);
            if (numBytesRead != packetLength) {
                throw new IOException((String)("Short read, expected " + packetLength + " bytes, only read " + numBytesRead));
            }
            Buffer packet = new Buffer((byte[])buffer);
            if (this.traceProtocol) {
                StringBuilder traceMessageBuf = new StringBuilder();
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.4"));
                traceMessageBuf.append((String)MysqlIO.getPacketDumpToLog((Buffer)packet, (int)packetLength));
                this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
            }
            if (this.enablePacketDebug) {
                this.enqueuePacketForDebugging((boolean)false, (boolean)false, (int)0, (byte[])this.packetHeaderBuf, (Buffer)packet);
            }
            if (!this.connection.getMaintainTimeStats()) return packet;
            this.lastPacketReceivedTimeMs = System.currentTimeMillis();
            return packet;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (OutOfMemoryError oom) {
            try {
                this.connection.realClose((boolean)false, (boolean)false, (boolean)true, (Throwable)oom);
                throw oom;
            }
            catch (Exception ex) {
                // empty catch block
            }
            throw oom;
        }
    }

    protected final Field unpackField(Buffer packet, boolean extractDefaultValues) throws SQLException {
        if (this.use41Extensions) {
            if (this.has41NewNewProt) {
                int catalogNameStart = packet.getPosition() + 1;
                int catalogNameLength = packet.fastSkipLenString();
                catalogNameStart = this.adjustStartForFieldLength((int)catalogNameStart, (int)catalogNameLength);
            }
            int databaseNameStart = packet.getPosition() + 1;
            int databaseNameLength = packet.fastSkipLenString();
            databaseNameStart = this.adjustStartForFieldLength((int)databaseNameStart, (int)databaseNameLength);
            int tableNameStart = packet.getPosition() + 1;
            int tableNameLength = packet.fastSkipLenString();
            tableNameStart = this.adjustStartForFieldLength((int)tableNameStart, (int)tableNameLength);
            int originalTableNameStart = packet.getPosition() + 1;
            int originalTableNameLength = packet.fastSkipLenString();
            originalTableNameStart = this.adjustStartForFieldLength((int)originalTableNameStart, (int)originalTableNameLength);
            int nameStart = packet.getPosition() + 1;
            int nameLength = packet.fastSkipLenString();
            nameStart = this.adjustStartForFieldLength((int)nameStart, (int)nameLength);
            int originalColumnNameStart = packet.getPosition() + 1;
            int originalColumnNameLength = packet.fastSkipLenString();
            originalColumnNameStart = this.adjustStartForFieldLength((int)originalColumnNameStart, (int)originalColumnNameLength);
            packet.readByte();
            short charSetNumber = (short)packet.readInt();
            long colLength = 0L;
            colLength = this.has41NewNewProt ? packet.readLong() : (long)packet.readLongInt();
            int colType = packet.readByte() & 255;
            short colFlag = 0;
            colFlag = this.hasLongColumnInfo ? (short)packet.readInt() : (short)(packet.readByte() & 255);
            int colDecimals = packet.readByte() & 255;
            int defaultValueStart = -1;
            int defaultValueLength = -1;
            if (!extractDefaultValues) return new Field((MySQLConnection)this.connection, (byte[])packet.getByteBuffer(), (int)databaseNameStart, (int)databaseNameLength, (int)tableNameStart, (int)tableNameLength, (int)originalTableNameStart, (int)originalTableNameLength, (int)nameStart, (int)nameLength, (int)originalColumnNameStart, (int)originalColumnNameLength, (long)colLength, (int)colType, (short)colFlag, (int)colDecimals, (int)defaultValueStart, (int)defaultValueLength, (int)charSetNumber);
            defaultValueStart = packet.getPosition() + 1;
            defaultValueLength = packet.fastSkipLenString();
            return new Field((MySQLConnection)this.connection, (byte[])packet.getByteBuffer(), (int)databaseNameStart, (int)databaseNameLength, (int)tableNameStart, (int)tableNameLength, (int)originalTableNameStart, (int)originalTableNameLength, (int)nameStart, (int)nameLength, (int)originalColumnNameStart, (int)originalColumnNameLength, (long)colLength, (int)colType, (short)colFlag, (int)colDecimals, (int)defaultValueStart, (int)defaultValueLength, (int)charSetNumber);
        }
        int tableNameStart = packet.getPosition() + 1;
        int tableNameLength = packet.fastSkipLenString();
        tableNameStart = this.adjustStartForFieldLength((int)tableNameStart, (int)tableNameLength);
        int nameStart = packet.getPosition() + 1;
        int nameLength = packet.fastSkipLenString();
        nameStart = this.adjustStartForFieldLength((int)nameStart, (int)nameLength);
        int colLength = packet.readnBytes();
        int colType = packet.readnBytes();
        packet.readByte();
        short colFlag = 0;
        colFlag = this.hasLongColumnInfo ? (short)packet.readInt() : (short)(packet.readByte() & 255);
        int colDecimals = packet.readByte() & 255;
        if (!this.colDecimalNeedsBump) return new Field((MySQLConnection)this.connection, (byte[])packet.getByteBuffer(), (int)nameStart, (int)nameLength, (int)tableNameStart, (int)tableNameLength, (int)colLength, (int)colType, (short)colFlag, (int)colDecimals);
        ++colDecimals;
        return new Field((MySQLConnection)this.connection, (byte[])packet.getByteBuffer(), (int)nameStart, (int)nameLength, (int)tableNameStart, (int)tableNameLength, (int)colLength, (int)colType, (short)colFlag, (int)colDecimals);
    }

    private int adjustStartForFieldLength(int nameStart, int nameLength) {
        if (nameLength < 251) {
            return nameStart;
        }
        if (nameLength >= 251 && nameLength < 65536) {
            return nameStart + 2;
        }
        if (nameLength < 65536) return nameStart + 8;
        if (nameLength >= 16777216) return nameStart + 8;
        return nameStart + 3;
    }

    protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
        boolean autoCommitModeOnServer;
        if (!this.use41Extensions) return true;
        if (!this.connection.getElideSetAutoCommits()) return true;
        boolean bl = autoCommitModeOnServer = (this.serverStatus & 2) != 0;
        if (!autoCommitFlag && this.versionMeetsMinimum((int)5, (int)0, (int)0)) {
            if (this.inTransactionOnServer()) return false;
            return true;
        }
        if (autoCommitModeOnServer == autoCommitFlag) return false;
        return true;
    }

    protected boolean inTransactionOnServer() {
        if ((this.serverStatus & 1) == 0) return false;
        return true;
    }

    protected void changeUser(String userName, String password, String database) throws SQLException {
        boolean localUseConnectWithDb;
        this.packetSequence = (byte)-1;
        this.compressedPacketSequence = (byte)-1;
        int passwordLength = 16;
        int userLength = userName != null ? userName.length() : 0;
        int databaseLength = database != null ? database.length() : 0;
        int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
        if ((this.serverCapabilities & 524288) != 0) {
            this.proceedHandshakeWithPluggableAuthentication((String)userName, (String)password, (String)database, null);
            return;
        }
        if ((this.serverCapabilities & 32768) != 0) {
            Buffer changeUserPacket = new Buffer((int)(packLength + 1));
            changeUserPacket.writeByte((byte)17);
            if (this.versionMeetsMinimum((int)4, (int)1, (int)1)) {
                this.secureAuth411((Buffer)changeUserPacket, (int)packLength, (String)userName, (String)password, (String)database, (boolean)false, (boolean)true);
                return;
            }
            this.secureAuth((Buffer)changeUserPacket, (int)packLength, (String)userName, (String)password, (String)database, (boolean)false);
            return;
        }
        Buffer packet = new Buffer((int)packLength);
        packet.writeByte((byte)17);
        packet.writeString((String)userName);
        if (this.protocolVersion > 9) {
            packet.writeString((String)Util.newCrypt((String)password, (String)this.seed, (String)this.connection.getPasswordCharacterEncoding()));
        } else {
            packet.writeString((String)Util.oldCrypt((String)password, (String)this.seed));
        }
        boolean bl = localUseConnectWithDb = this.useConnectWithDb && database != null && database.length() > 0;
        if (localUseConnectWithDb) {
            packet.writeString((String)database);
        }
        this.send((Buffer)packet, (int)packet.getPosition());
        this.checkErrorPacket();
        if (localUseConnectWithDb) return;
        this.changeDatabaseTo((String)database);
    }

    protected Buffer checkErrorPacket() throws SQLException {
        return this.checkErrorPacket((int)-1);
    }

    protected void checkForCharsetMismatch() {
        if (!this.connection.getUseUnicode()) return;
        if (this.connection.getEncoding() == null) return;
        String encodingToCheck = jvmPlatformCharset;
        if (encodingToCheck == null) {
            encodingToCheck = System.getProperty((String)"file.encoding");
        }
        if (encodingToCheck == null) {
            this.platformDbCharsetMatches = false;
            return;
        }
        this.platformDbCharsetMatches = encodingToCheck.equals((Object)this.connection.getEncoding());
    }

    protected void clearInputStream() throws SQLException {
        try {
            int len;
            while ((len = this.mysqlInput.available()) > 0) {
                if (this.mysqlInput.skip((long)((long)len)) <= 0L) return;
            }
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    protected void resetReadPacketSequence() {
        this.readPacketSequence = 0;
    }

    protected void dumpPacketRingBuffer() throws SQLException {
        if (this.packetDebugRingBuffer == null) return;
        if (!this.connection.getEnablePacketDebug()) return;
        StringBuilder dumpBuffer = new StringBuilder();
        dumpBuffer.append((String)("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n"));
        dumpBuffer.append((String)"\n");
        Iterator<E> ringBufIter = this.packetDebugRingBuffer.iterator();
        do {
            if (!ringBufIter.hasNext()) {
                this.connection.getLog().logTrace((Object)dumpBuffer.toString());
                return;
            }
            dumpBuffer.append((CharSequence)((CharSequence)ringBufIter.next()));
            dumpBuffer.append((String)"\n");
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void explainSlowQuery(byte[] querySQL, String truncatedQuery) throws SQLException {
        if (!StringUtils.startsWithIgnoreCaseAndWs((String)truncatedQuery, (String)EXPLAINABLE_STATEMENT)) {
            if (!this.versionMeetsMinimum((int)5, (int)6, (int)3)) return;
            if (StringUtils.startsWithIgnoreCaseAndWs((String)truncatedQuery, (String[])EXPLAINABLE_STATEMENT_EXTENSION) == -1) return;
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            try {
                stmt = (PreparedStatement)this.connection.clientPrepareStatement((String)"EXPLAIN ?");
                stmt.setBytesNoEscapeNoQuotes((int)1, (byte[])querySQL);
                rs = stmt.executeQuery();
                StringBuilder explainResults = new StringBuilder((String)(Messages.getString((String)"MysqlIO.8") + truncatedQuery + Messages.getString((String)"MysqlIO.9")));
                ResultSetUtil.appendResultSetSlashGStyle((StringBuilder)explainResults, (ResultSet)rs);
                this.connection.getLog().logWarn((Object)explainResults.toString());
            }
            catch (SQLException sqlEx) {
                Object var7_8 = null;
                if (rs != null) {
                    rs.close();
                }
                if (stmt == null) return;
                stmt.close();
                return;
            }
            Object var7_7 = null;
            if (rs != null) {
                rs.close();
            }
            if (stmt == null) return;
            stmt.close();
            return;
        }
        catch (Throwable throwable) {
            Object var7_9 = null;
            if (rs != null) {
                rs.close();
            }
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    static int getMaxBuf() {
        return maxBufferSize;
    }

    final int getServerMajorVersion() {
        return this.serverMajorVersion;
    }

    final int getServerMinorVersion() {
        return this.serverMinorVersion;
    }

    final int getServerSubMinorVersion() {
        return this.serverSubMinorVersion;
    }

    String getServerVersion() {
        return this.serverVersion;
    }

    void doHandshake(String user, String password, String database) throws SQLException {
        this.checkPacketSequence = false;
        this.readPacketSequence = 0;
        Buffer buf = this.readPacket();
        this.protocolVersion = buf.readByte();
        if (this.protocolVersion == -1) {
            try {
                this.mysqlConnection.close();
            }
            catch (Exception e) {
                // empty catch block
            }
            int errno = 2000;
            errno = buf.readInt();
            String serverErrorMessage = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
            StringBuilder errorBuf = new StringBuilder((String)Messages.getString((String)"MysqlIO.10"));
            errorBuf.append((String)serverErrorMessage);
            errorBuf.append((String)"\"");
            String xOpen = SQLError.mysqlToSqlState((int)errno, (boolean)this.connection.getUseSqlStateCodes());
            throw SQLError.createSQLException((String)(SQLError.get((String)xOpen) + ", " + errorBuf.toString()), (String)xOpen, (int)errno, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.serverVersion = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
        int point = this.serverVersion.indexOf((int)46);
        if (point != -1) {
            try {
                int n;
                this.serverMajorVersion = n = Integer.parseInt((String)this.serverVersion.substring((int)0, (int)point));
            }
            catch (NumberFormatException NFE1) {
                // empty catch block
            }
            String remaining = this.serverVersion.substring((int)(point + 1), (int)this.serverVersion.length());
            point = remaining.indexOf((int)46);
            if (point != -1) {
                int pos;
                try {
                    int n;
                    this.serverMinorVersion = n = Integer.parseInt((String)remaining.substring((int)0, (int)point));
                }
                catch (NumberFormatException nfe) {
                    // empty catch block
                }
                remaining = remaining.substring((int)(point + 1), (int)remaining.length());
                for (pos = 0; pos < remaining.length() && remaining.charAt((int)pos) >= '0' && remaining.charAt((int)pos) <= '9'; ++pos) {
                }
                try {
                    int n;
                    this.serverSubMinorVersion = n = Integer.parseInt((String)remaining.substring((int)0, (int)pos));
                }
                catch (NumberFormatException nfe) {
                    // empty catch block
                }
            }
        }
        if (this.versionMeetsMinimum((int)4, (int)0, (int)8)) {
            this.maxThreeBytes = 16777215;
            this.useNewLargePackets = true;
        } else {
            this.maxThreeBytes = 16581375;
            this.useNewLargePackets = false;
        }
        this.colDecimalNeedsBump = this.versionMeetsMinimum((int)3, (int)23, (int)0);
        this.colDecimalNeedsBump = !this.versionMeetsMinimum((int)3, (int)23, (int)15);
        this.useNewUpdateCounts = this.versionMeetsMinimum((int)3, (int)22, (int)5);
        this.threadId = buf.readLong();
        if (this.protocolVersion > 9) {
            this.seed = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor(), (int)8);
            buf.readByte();
        } else {
            this.seed = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.serverCapabilities = 0;
        if (buf.getPosition() < buf.getBufLength()) {
            this.serverCapabilities = buf.readInt();
        }
        if (this.versionMeetsMinimum((int)4, (int)1, (int)1) || this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0) {
            this.serverCharsetIndex = buf.readByte() & 255;
            this.serverStatus = buf.readInt();
            this.checkTransactionState((int)0);
            this.serverCapabilities |= buf.readInt() << 16;
            if ((this.serverCapabilities & 524288) != 0) {
                this.authPluginDataLength = buf.readByte() & 255;
            } else {
                buf.readByte();
            }
            buf.setPosition((int)(buf.getPosition() + 10));
            if ((this.serverCapabilities & 32768) != 0) {
                StringBuilder newSeed;
                String seedPart2;
                if (this.authPluginDataLength > 0) {
                    seedPart2 = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor(), (int)(this.authPluginDataLength - 8));
                    newSeed = new StringBuilder((int)this.authPluginDataLength);
                } else {
                    seedPart2 = buf.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
                    newSeed = new StringBuilder((int)20);
                }
                newSeed.append((String)this.seed);
                newSeed.append((String)seedPart2);
                this.seed = newSeed.toString();
            }
        }
        if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression()) {
            this.clientParam |= 32L;
        }
        boolean bl = this.useConnectWithDb = database != null && database.length() > 0 && !this.connection.getCreateDatabaseIfNotExist();
        if (this.useConnectWithDb) {
            this.clientParam |= 8L;
        }
        if (this.versionMeetsMinimum((int)5, (int)7, (int)0) && !this.connection.getUseSSL() && !this.connection.isUseSSLExplicit()) {
            this.connection.setUseSSL((boolean)true);
            this.connection.setVerifyServerCertificate((boolean)false);
            this.connection.getLog().logWarn((Object)Messages.getString((String)"MysqlIO.SSLWarning"));
        }
        if ((this.serverCapabilities & 2048) == 0 && this.connection.getUseSSL()) {
            if (this.connection.getRequireSSL()) {
                this.connection.close();
                this.forceClose();
                throw SQLError.createSQLException((String)Messages.getString((String)"MysqlIO.15"), (String)"08001", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            this.connection.setUseSSL((boolean)false);
        }
        if ((this.serverCapabilities & 4) != 0) {
            this.clientParam |= 4L;
            this.hasLongColumnInfo = true;
        }
        if (!this.connection.getUseAffectedRows()) {
            this.clientParam |= 2L;
        }
        if (this.connection.getAllowLoadLocalInfile()) {
            this.clientParam |= 128L;
        }
        if (this.isInteractiveClient) {
            this.clientParam |= 1024L;
        }
        if ((this.serverCapabilities & 8388608) != 0) {
            // empty if block
        }
        if ((this.serverCapabilities & 16777216) != 0) {
            this.clientParam |= 0x1000000L;
        }
        if ((this.serverCapabilities & 524288) != 0) {
            this.proceedHandshakeWithPluggableAuthentication((String)user, (String)password, (String)database, (Buffer)buf);
            return;
        }
        this.clientParam = this.protocolVersion > 9 ? (this.clientParam |= 1L) : (this.clientParam &= -2L);
        if (this.versionMeetsMinimum((int)4, (int)1, (int)0) || this.protocolVersion > 9 && (this.serverCapabilities & 16384) != 0) {
            if (this.versionMeetsMinimum((int)4, (int)1, (int)1) || this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0) {
                this.clientParam |= 512L;
                this.has41NewNewProt = true;
                this.clientParam |= 8192L;
                this.clientParam |= 131072L;
                if (this.connection.getAllowMultiQueries()) {
                    this.clientParam |= 65536L;
                }
            } else {
                this.clientParam |= 16384L;
                this.has41NewNewProt = false;
            }
            this.use41Extensions = true;
        }
        int passwordLength = 16;
        int userLength = user != null ? user.length() : 0;
        int databaseLength = database != null ? database.length() : 0;
        int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
        Buffer packet = null;
        if (!this.connection.getUseSSL()) {
            if ((this.serverCapabilities & 32768) != 0) {
                this.clientParam |= 32768L;
                if (this.versionMeetsMinimum((int)4, (int)1, (int)1) || this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0) {
                    this.secureAuth411(null, (int)packLength, (String)user, (String)password, (String)database, (boolean)true, (boolean)false);
                } else {
                    this.secureAuth(null, (int)packLength, (String)user, (String)password, (String)database, (boolean)true);
                }
            } else {
                packet = new Buffer((int)packLength);
                if ((this.clientParam & 16384L) != 0L) {
                    if (this.versionMeetsMinimum((int)4, (int)1, (int)1) || this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0) {
                        packet.writeLong((long)this.clientParam);
                        packet.writeLong((long)((long)this.maxThreeBytes));
                        packet.writeByte((byte)8);
                        packet.writeBytesNoNull((byte[])new byte[23]);
                    } else {
                        packet.writeLong((long)this.clientParam);
                        packet.writeLong((long)((long)this.maxThreeBytes));
                    }
                } else {
                    packet.writeInt((int)((int)this.clientParam));
                    packet.writeLongInt((int)this.maxThreeBytes);
                }
                packet.writeString((String)user, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
                if (this.protocolVersion > 9) {
                    packet.writeString((String)Util.newCrypt((String)password, (String)this.seed, (String)this.connection.getPasswordCharacterEncoding()), (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
                } else {
                    packet.writeString((String)Util.oldCrypt((String)password, (String)this.seed), (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
                }
                if (this.useConnectWithDb) {
                    packet.writeString((String)database, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
                }
                this.send((Buffer)packet, (int)packet.getPosition());
            }
        } else {
            this.negotiateSSLConnection((String)user, (String)password, (String)database, (int)packLength);
            if ((this.serverCapabilities & 32768) != 0) {
                if (this.versionMeetsMinimum((int)4, (int)1, (int)1)) {
                    this.secureAuth411(null, (int)packLength, (String)user, (String)password, (String)database, (boolean)true, (boolean)false);
                } else {
                    this.secureAuth411(null, (int)packLength, (String)user, (String)password, (String)database, (boolean)true, (boolean)false);
                }
            } else {
                packet = new Buffer((int)packLength);
                if (this.use41Extensions) {
                    packet.writeLong((long)this.clientParam);
                    packet.writeLong((long)((long)this.maxThreeBytes));
                } else {
                    packet.writeInt((int)((int)this.clientParam));
                    packet.writeLongInt((int)this.maxThreeBytes);
                }
                packet.writeString((String)user);
                if (this.protocolVersion > 9) {
                    packet.writeString((String)Util.newCrypt((String)password, (String)this.seed, (String)this.connection.getPasswordCharacterEncoding()));
                } else {
                    packet.writeString((String)Util.oldCrypt((String)password, (String)this.seed));
                }
                if ((this.serverCapabilities & 8) != 0 && database != null && database.length() > 0) {
                    packet.writeString((String)database);
                }
                this.send((Buffer)packet, (int)packet.getPosition());
            }
        }
        if (!this.versionMeetsMinimum((int)4, (int)1, (int)1) || this.protocolVersion <= 9 || (this.serverCapabilities & 512) == 0) {
            this.checkErrorPacket();
        }
        if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression() && !(this.mysqlInput instanceof CompressedInputStream)) {
            this.deflater = new Deflater();
            this.useCompression = true;
            this.mysqlInput = new CompressedInputStream((Connection)this.connection, (InputStream)this.mysqlInput);
        }
        if (!this.useConnectWithDb) {
            this.changeDatabaseTo((String)database);
        }
        try {
            this.mysqlConnection = this.socketFactory.afterHandshake();
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private void loadAuthenticationPlugins() throws SQLException {
        String authenticationPluginClasses;
        this.clientDefaultAuthenticationPlugin = this.connection.getDefaultAuthenticationPlugin();
        if (this.clientDefaultAuthenticationPlugin == null || "".equals((Object)this.clientDefaultAuthenticationPlugin.trim())) {
            throw SQLError.createSQLException((String)Messages.getString((String)"Connection.BadDefaultAuthenticationPlugin", (Object[])new Object[]{this.clientDefaultAuthenticationPlugin}), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String disabledPlugins = this.connection.getDisabledAuthenticationPlugins();
        if (disabledPlugins != null && !"".equals((Object)disabledPlugins)) {
            this.disabledAuthenticationPlugins = new ArrayList<String>();
            List<String> pluginsToDisable = StringUtils.split((String)disabledPlugins, (String)",", (boolean)true);
            Iterator<String> iter = pluginsToDisable.iterator();
            while (iter.hasNext()) {
                this.disabledAuthenticationPlugins.add((String)iter.next());
            }
        }
        this.authenticationPlugins = new HashMap<String, AuthenticationPlugin>();
        AuthenticationPlugin plugin = new MysqlOldPasswordPlugin();
        plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
        boolean defaultIsFound = this.addAuthenticationPlugin((AuthenticationPlugin)plugin);
        plugin = new MysqlNativePasswordPlugin();
        plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
        if (this.addAuthenticationPlugin((AuthenticationPlugin)plugin)) {
            defaultIsFound = true;
        }
        plugin = new MysqlClearPasswordPlugin();
        plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
        if (this.addAuthenticationPlugin((AuthenticationPlugin)plugin)) {
            defaultIsFound = true;
        }
        plugin = new Sha256PasswordPlugin();
        plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
        if (this.addAuthenticationPlugin((AuthenticationPlugin)plugin)) {
            defaultIsFound = true;
        }
        plugin = new CachingSha2PasswordPlugin();
        plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
        if (this.addAuthenticationPlugin((AuthenticationPlugin)plugin)) {
            defaultIsFound = true;
        }
        if ((authenticationPluginClasses = this.connection.getAuthenticationPlugins()) != null && !"".equals((Object)authenticationPluginClasses)) {
            List<Extension> plugins = Util.loadExtensions((Connection)this.connection, (Properties)this.connection.getProperties(), (String)authenticationPluginClasses, (String)"Connection.BadAuthenticationPlugin", (ExceptionInterceptor)this.getExceptionInterceptor());
            for (Extension object : plugins) {
                plugin = (AuthenticationPlugin)object;
                if (!this.addAuthenticationPlugin((AuthenticationPlugin)plugin)) continue;
                defaultIsFound = true;
            }
        }
        if (defaultIsFound) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"Connection.DefaultAuthenticationPluginIsNotListed", (Object[])new Object[]{this.clientDefaultAuthenticationPlugin}), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private boolean addAuthenticationPlugin(AuthenticationPlugin plugin) throws SQLException {
        boolean disabledByMechanism;
        boolean isDefault = false;
        String pluginClassName = plugin.getClass().getName();
        String pluginProtocolName = plugin.getProtocolPluginName();
        boolean disabledByClassName = this.disabledAuthenticationPlugins != null && this.disabledAuthenticationPlugins.contains((Object)pluginClassName);
        boolean bl = disabledByMechanism = this.disabledAuthenticationPlugins != null && this.disabledAuthenticationPlugins.contains((Object)pluginProtocolName);
        if (!disabledByClassName && !disabledByMechanism) {
            this.authenticationPlugins.put((String)pluginProtocolName, (AuthenticationPlugin)plugin);
            if (!this.clientDefaultAuthenticationPlugin.equals((Object)pluginClassName)) return isDefault;
            this.clientDefaultAuthenticationPluginName = pluginProtocolName;
            return true;
        }
        if (!this.clientDefaultAuthenticationPlugin.equals((Object)pluginClassName)) return isDefault;
        throw SQLError.createSQLException((String)Messages.getString((String)"Connection.BadDisabledAuthenticationPlugin", (Object[])new Object[]{disabledByClassName ? pluginClassName : pluginProtocolName}), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private AuthenticationPlugin getAuthenticationPlugin(String pluginName) throws SQLException {
        AuthenticationPlugin plugin = this.authenticationPlugins.get((Object)pluginName);
        if (plugin == null) return plugin;
        if (plugin.isReusable()) return plugin;
        try {
            plugin = (AuthenticationPlugin)plugin.getClass().newInstance();
            plugin.init((Connection)this.connection, (Properties)this.connection.getProperties());
            return plugin;
        }
        catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"Connection.BadAuthenticationPlugin", (Object[])new Object[]{plugin.getClass().getName()}), (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)t);
            throw sqlEx;
        }
    }

    private void checkConfidentiality(AuthenticationPlugin plugin) throws SQLException {
        if (!plugin.requiresConfidentiality()) return;
        if (this.isSSLEstablished()) return;
        throw SQLError.createSQLException((String)Messages.getString((String)"Connection.AuthenticationPluginRequiresSSL", (Object[])new Object[]{plugin.getProtocolPluginName()}), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private void proceedHandshakeWithPluggableAuthentication(String user, String password, String database, Buffer challenge) throws SQLException {
        if (this.authenticationPlugins == null) {
            this.loadAuthenticationPlugins();
        }
        boolean skipPassword = false;
        int passwordLength = 16;
        int userLength = user != null ? user.length() : 0;
        int databaseLength = database != null ? database.length() : 0;
        int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
        AuthenticationPlugin plugin = null;
        Buffer fromServer = null;
        ArrayList<Buffer> toServer = new ArrayList<Buffer>();
        boolean done = false;
        Buffer last_sent = null;
        boolean old_raw_challenge = false;
        int counter = 100;
        while (0 < counter--) {
            String enc;
            String pluginName;
            if (!done) {
                if (challenge != null) {
                    if (challenge.isOKPacket()) {
                        throw SQLError.createSQLException((String)Messages.getString((String)"Connection.UnexpectedAuthenticationApproval", (Object[])new Object[]{plugin.getProtocolPluginName()}), (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    this.clientParam |= 696833L;
                    if (this.connection.getAllowMultiQueries()) {
                        this.clientParam |= 65536L;
                    }
                    if ((this.serverCapabilities & 4194304) != 0 && !this.connection.getDisconnectOnExpiredPasswords()) {
                        this.clientParam |= 0x400000L;
                    }
                    if ((this.serverCapabilities & 1048576) != 0 && !NONE.equals((Object)this.connection.getConnectionAttributes())) {
                        this.clientParam |= 0x100000L;
                    }
                    if ((this.serverCapabilities & 2097152) != 0) {
                        this.clientParam |= 0x200000L;
                    }
                    this.has41NewNewProt = true;
                    this.use41Extensions = true;
                    if (this.connection.getUseSSL()) {
                        this.negotiateSSLConnection((String)user, (String)password, (String)database, (int)packLength);
                    }
                    pluginName = null;
                    if ((this.serverCapabilities & 524288) != 0) {
                        pluginName = !this.versionMeetsMinimum((int)5, (int)5, (int)10) || this.versionMeetsMinimum((int)5, (int)6, (int)0) && !this.versionMeetsMinimum((int)5, (int)6, (int)2) ? challenge.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor(), (int)this.authPluginDataLength) : challenge.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    if ((plugin = this.getAuthenticationPlugin((String)pluginName)) == null) {
                        plugin = this.getAuthenticationPlugin((String)this.clientDefaultAuthenticationPluginName);
                    } else if (pluginName.equals((Object)Sha256PasswordPlugin.PLUGIN_NAME) && !this.isSSLEstablished() && this.connection.getServerRSAPublicKeyFile() == null && !this.connection.getAllowPublicKeyRetrieval()) {
                        plugin = this.getAuthenticationPlugin((String)this.clientDefaultAuthenticationPluginName);
                        skipPassword = !this.clientDefaultAuthenticationPluginName.equals((Object)pluginName);
                    }
                    this.serverDefaultAuthenticationPluginName = plugin.getProtocolPluginName();
                    this.checkConfidentiality((AuthenticationPlugin)plugin);
                    fromServer = new Buffer((byte[])StringUtils.getBytes((String)this.seed));
                } else {
                    plugin = this.getAuthenticationPlugin((String)(this.serverDefaultAuthenticationPluginName == null ? this.clientDefaultAuthenticationPluginName : this.serverDefaultAuthenticationPluginName));
                    this.checkConfidentiality((AuthenticationPlugin)plugin);
                    fromServer = new Buffer((byte[])StringUtils.getBytes((String)this.seed));
                }
            } else {
                challenge = this.checkErrorPacket();
                old_raw_challenge = false;
                this.packetSequence = (byte)(this.packetSequence + 1);
                this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
                if (plugin == null) {
                    plugin = this.getAuthenticationPlugin((String)(this.serverDefaultAuthenticationPluginName != null ? this.serverDefaultAuthenticationPluginName : this.clientDefaultAuthenticationPluginName));
                }
                if (challenge.isOKPacket()) {
                    challenge.newReadLength();
                    challenge.newReadLength();
                    this.oldServerStatus = this.serverStatus;
                    this.serverStatus = challenge.readInt();
                    plugin.destroy();
                    break;
                }
                if (challenge.isAuthMethodSwitchRequestPacket()) {
                    skipPassword = false;
                    pluginName = challenge.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor());
                    if (!plugin.getProtocolPluginName().equals((Object)pluginName)) {
                        plugin.destroy();
                        plugin = this.getAuthenticationPlugin((String)pluginName);
                        if (plugin == null) {
                            throw SQLError.createSQLException((String)Messages.getString((String)"Connection.BadAuthenticationPlugin", (Object[])new Object[]{pluginName}), (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                    } else {
                        plugin.reset();
                    }
                    this.checkConfidentiality((AuthenticationPlugin)plugin);
                    fromServer = new Buffer((byte[])StringUtils.getBytes((String)challenge.readString((String)"ASCII", (ExceptionInterceptor)this.getExceptionInterceptor())));
                } else if (this.versionMeetsMinimum((int)5, (int)5, (int)16)) {
                    fromServer = new Buffer((byte[])challenge.getBytes((int)challenge.getPosition(), (int)(challenge.getBufLength() - challenge.getPosition())));
                } else {
                    old_raw_challenge = true;
                    fromServer = new Buffer((byte[])challenge.getBytes((int)(challenge.getPosition() - 1), (int)(challenge.getBufLength() - challenge.getPosition() + 1)));
                }
            }
            try {
                plugin.setAuthenticationParameters((String)user, (String)(skipPassword ? null : password));
                done = plugin.nextAuthenticationStep((Buffer)fromServer, toServer);
            }
            catch (SQLException e) {
                throw SQLError.createSQLException((String)e.getMessage(), (String)e.getSQLState(), (Throwable)e, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            if (toServer.size() <= 0) continue;
            if (challenge == null) {
                enc = this.getEncodingForHandshake();
                last_sent = new Buffer((int)(packLength + 1));
                last_sent.writeByte((byte)17);
                last_sent.writeString((String)user, (String)enc, (MySQLConnection)this.connection);
                if (toServer.get((int)0).getBufLength() < 256) {
                    last_sent.writeByte((byte)((byte)toServer.get((int)0).getBufLength()));
                    last_sent.writeBytesNoNull((byte[])toServer.get((int)0).getByteBuffer(), (int)0, (int)toServer.get((int)0).getBufLength());
                } else {
                    last_sent.writeByte((byte)0);
                }
                if (this.useConnectWithDb) {
                    last_sent.writeString((String)database, (String)enc, (MySQLConnection)this.connection);
                } else {
                    last_sent.writeByte((byte)0);
                }
                this.appendCharsetByteForHandshake((Buffer)last_sent, (String)enc);
                last_sent.writeByte((byte)0);
                if ((this.serverCapabilities & 524288) != 0) {
                    last_sent.writeString((String)plugin.getProtocolPluginName(), (String)enc, (MySQLConnection)this.connection);
                }
                if ((this.clientParam & 0x100000L) != 0L) {
                    this.sendConnectionAttributes((Buffer)last_sent, (String)enc, (MySQLConnection)this.connection);
                    last_sent.writeByte((byte)0);
                }
                this.send((Buffer)last_sent, (int)last_sent.getPosition());
                continue;
            }
            if (challenge.isAuthMethodSwitchRequestPacket()) {
                last_sent = new Buffer((int)(toServer.get((int)0).getBufLength() + 4));
                last_sent.writeBytesNoNull((byte[])toServer.get((int)0).getByteBuffer(), (int)0, (int)toServer.get((int)0).getBufLength());
                this.send((Buffer)last_sent, (int)last_sent.getPosition());
                continue;
            }
            if (challenge.isRawPacket() || old_raw_challenge) {
                for (Buffer buffer : toServer) {
                    last_sent = new Buffer((int)(buffer.getBufLength() + 4));
                    last_sent.writeBytesNoNull((byte[])buffer.getByteBuffer(), (int)0, (int)toServer.get((int)0).getBufLength());
                    this.send((Buffer)last_sent, (int)last_sent.getPosition());
                }
                continue;
            }
            enc = this.getEncodingForHandshake();
            last_sent = new Buffer((int)packLength);
            last_sent.writeLong((long)this.clientParam);
            last_sent.writeLong((long)((long)this.maxThreeBytes));
            this.appendCharsetByteForHandshake((Buffer)last_sent, (String)enc);
            last_sent.writeBytesNoNull((byte[])new byte[23]);
            last_sent.writeString((String)user, (String)enc, (MySQLConnection)this.connection);
            if ((this.serverCapabilities & 2097152) != 0) {
                last_sent.writeLenBytes((byte[])toServer.get((int)0).getBytes((int)toServer.get((int)0).getBufLength()));
            } else {
                last_sent.writeByte((byte)((byte)toServer.get((int)0).getBufLength()));
                last_sent.writeBytesNoNull((byte[])toServer.get((int)0).getByteBuffer(), (int)0, (int)toServer.get((int)0).getBufLength());
            }
            if (this.useConnectWithDb) {
                last_sent.writeString((String)database, (String)enc, (MySQLConnection)this.connection);
            }
            if ((this.serverCapabilities & 524288) != 0) {
                last_sent.writeString((String)plugin.getProtocolPluginName(), (String)enc, (MySQLConnection)this.connection);
            }
            if ((this.clientParam & 0x100000L) != 0L) {
                this.sendConnectionAttributes((Buffer)last_sent, (String)enc, (MySQLConnection)this.connection);
            }
            this.send((Buffer)last_sent, (int)last_sent.getPosition());
        }
        if (counter == 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"CommunicationsException.TooManyAuthenticationPluginNegotiations"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression() && !(this.mysqlInput instanceof CompressedInputStream)) {
            this.deflater = new Deflater();
            this.useCompression = true;
            this.mysqlInput = new CompressedInputStream((Connection)this.connection, (InputStream)this.mysqlInput);
        }
        if (!this.useConnectWithDb) {
            this.changeDatabaseTo((String)database);
        }
        try {
            this.mysqlConnection = this.socketFactory.afterHandshake();
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private Properties getConnectionAttributesAsProperties(String atts) throws SQLException {
        Properties props = new Properties();
        if (atts != null) {
            String[] pairs;
            for (String pair : pairs = atts.split((String)",")) {
                int keyEnd = pair.indexOf((String)":");
                if (keyEnd <= 0 || keyEnd + 1 >= pair.length()) continue;
                props.setProperty((String)pair.substring((int)0, (int)keyEnd), (String)pair.substring((int)(keyEnd + 1)));
            }
        }
        props.setProperty((String)"_client_name", (String)"MySQL Connector Java");
        props.setProperty((String)"_client_version", (String)"5.1.48");
        props.setProperty((String)"_runtime_vendor", (String)NonRegisteringDriver.RUNTIME_VENDOR);
        props.setProperty((String)"_runtime_version", (String)NonRegisteringDriver.RUNTIME_VERSION);
        props.setProperty((String)"_client_license", (String)"GPL");
        return props;
    }

    private void sendConnectionAttributes(Buffer buf, String enc, MySQLConnection conn) throws SQLException {
        String atts = conn.getConnectionAttributes();
        Buffer lb = new Buffer((int)100);
        try {
            Properties props = this.getConnectionAttributesAsProperties((String)atts);
            for (K key : props.keySet()) {
                lb.writeLenString((String)((String)key), (String)enc, (String)conn.getServerCharset(), null, (boolean)conn.parserKnowsUnicode(), (MySQLConnection)conn);
                lb.writeLenString((String)props.getProperty((String)((String)key)), (String)enc, (String)conn.getServerCharset(), null, (boolean)conn.parserKnowsUnicode(), (MySQLConnection)conn);
            }
        }
        catch (UnsupportedEncodingException e) {
            // empty catch block
        }
        buf.writeByte((byte)((byte)(lb.getPosition() - 4)));
        buf.writeBytesNoNull((byte[])lb.getByteBuffer(), (int)4, (int)(lb.getBufLength() - 4));
    }

    private void changeDatabaseTo(String database) throws SQLException {
        if (database == null) return;
        if (database.length() == 0) {
            return;
        }
        try {
            this.sendCommand((int)2, (String)database, null, (boolean)false, null, (int)0);
            return;
        }
        catch (Exception ex) {
            if (!this.connection.getCreateDatabaseIfNotExist()) throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ex, (ExceptionInterceptor)this.getExceptionInterceptor());
            this.sendCommand((int)3, (String)("CREATE DATABASE IF NOT EXISTS " + database), null, (boolean)false, null, (int)0);
            this.sendCommand((int)2, (String)database, null, (boolean)false, null, (int)0);
            return;
        }
    }

    final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket) throws SQLException {
        if (this.useDirectRowUnpack && existingRowPacket == null && !isBinaryEncoded && !useBufferRowIfPossible && !useBufferRowExplicit) {
            return this.nextRowFast((Field[])fields, (int)columnCount, (boolean)isBinaryEncoded, (int)resultSetConcurrency, (boolean)useBufferRowIfPossible, (boolean)useBufferRowExplicit, (boolean)canReuseRowPacketForBufferRow);
        }
        Buffer rowPacket = null;
        if (existingRowPacket == null) {
            rowPacket = this.checkErrorPacket();
            if (!useBufferRowExplicit && useBufferRowIfPossible && rowPacket.getBufLength() > this.useBufferRowSizeThreshold) {
                useBufferRowExplicit = true;
            }
        } else {
            rowPacket = existingRowPacket;
            this.checkErrorPacket((Buffer)existingRowPacket);
        }
        if (!isBinaryEncoded) {
            rowPacket.setPosition((int)(rowPacket.getPosition() - 1));
            if (!(!this.isEOFDeprecated() && rowPacket.isEOFPacket() || this.isEOFDeprecated() && rowPacket.isResultSetOKPacket())) {
                if (resultSetConcurrency == 1008 || !useBufferRowIfPossible && !useBufferRowExplicit) {
                    byte[][] rowData = new byte[columnCount][];
                    int i = 0;
                    while (i < columnCount) {
                        rowData[i] = rowPacket.readLenByteArray((int)0);
                        ++i;
                    }
                    return new ByteArrayRow((byte[][])rowData, (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                if (canReuseRowPacketForBufferRow) return new BufferRow((Buffer)rowPacket, (Field[])fields, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor());
                this.reusablePacket = new Buffer((int)rowPacket.getBufLength());
                return new BufferRow((Buffer)rowPacket, (Field[])fields, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            this.readServerStatusForResultSets((Buffer)rowPacket);
            return null;
        }
        if (!(!this.isEOFDeprecated() && rowPacket.isEOFPacket() || this.isEOFDeprecated() && rowPacket.isResultSetOKPacket())) {
            if (resultSetConcurrency == 1008) return this.unpackBinaryResultSetRow((Field[])fields, (Buffer)rowPacket, (int)resultSetConcurrency);
            if (!useBufferRowIfPossible && !useBufferRowExplicit) {
                return this.unpackBinaryResultSetRow((Field[])fields, (Buffer)rowPacket, (int)resultSetConcurrency);
            }
            if (canReuseRowPacketForBufferRow) return new BufferRow((Buffer)rowPacket, (Field[])fields, (boolean)true, (ExceptionInterceptor)this.getExceptionInterceptor());
            this.reusablePacket = new Buffer((int)rowPacket.getBufLength());
            return new BufferRow((Buffer)rowPacket, (Field[])fields, (boolean)true, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        rowPacket.setPosition((int)(rowPacket.getPosition() - 1));
        this.readServerStatusForResultSets((Buffer)rowPacket);
        return null;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket) throws SQLException {
        try {
            lengthRead = this.readFully((InputStream)this.mysqlInput, (byte[])this.packetHeaderBuf, (int)0, (int)4);
            if (lengthRead < 4) {
                this.forceClose();
                throw new RuntimeException((String)Messages.getString((String)"MysqlIO.43"));
            }
            packetLength = (this.packetHeaderBuf[0] & 255) + ((this.packetHeaderBuf[1] & 255) << 8) + ((this.packetHeaderBuf[2] & 255) << 16);
            if (packetLength == this.maxThreeBytes) {
                this.reuseAndReadPacket((Buffer)this.reusablePacket, (int)packetLength);
                return this.nextRow((Field[])fields, (int)columnCount, (boolean)isBinaryEncoded, (int)resultSetConcurrency, (boolean)useBufferRowIfPossible, (boolean)useBufferRowExplicit, (boolean)canReuseRowPacket, (Buffer)this.reusablePacket);
            }
            if (packetLength > this.useBufferRowSizeThreshold) {
                this.reuseAndReadPacket((Buffer)this.reusablePacket, (int)packetLength);
                return this.nextRow((Field[])fields, (int)columnCount, (boolean)isBinaryEncoded, (int)resultSetConcurrency, (boolean)true, (boolean)true, (boolean)false, (Buffer)this.reusablePacket);
            }
            remaining = packetLength;
            firstTime = true;
            rowData = (byte[][])null;
            i = 0;
            do {
                if (i >= columnCount) {
                    if (remaining <= 0) return new ByteArrayRow((byte[][])rowData, (ExceptionInterceptor)this.getExceptionInterceptor());
                    this.skipFully((InputStream)this.mysqlInput, (long)((long)remaining));
                    return new ByteArrayRow((byte[][])rowData, (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                sw = this.mysqlInput.read() & 255;
                --remaining;
                if (firstTime) {
                    if (sw == 255) {
                        errorPacket = new Buffer((int)(packetLength + 4));
                        errorPacket.setPosition((int)0);
                        errorPacket.writeByte((byte)this.packetHeaderBuf[0]);
                        errorPacket.writeByte((byte)this.packetHeaderBuf[1]);
                        errorPacket.writeByte((byte)this.packetHeaderBuf[2]);
                        errorPacket.writeByte((byte)1);
                        errorPacket.writeByte((byte)((byte)sw));
                        this.readFully((InputStream)this.mysqlInput, (byte[])errorPacket.getByteBuffer(), (int)5, (int)(packetLength - 1));
                        errorPacket.setPosition((int)4);
                        this.checkErrorPacket((Buffer)errorPacket);
                    }
                    if (sw == 254 && packetLength < 16777215) {
                        if (this.use41Extensions == false) return null;
                        if (this.isEOFDeprecated()) {
                            remaining -= this.skipLengthEncodedInteger((InputStream)this.mysqlInput);
                            remaining -= this.skipLengthEncodedInteger((InputStream)this.mysqlInput);
                            this.oldServerStatus = this.serverStatus;
                            this.serverStatus = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8;
                            this.checkTransactionState((int)this.oldServerStatus);
                            remaining -= 2;
                            this.warningCount = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8;
                            remaining -= 2;
                            if (this.warningCount > 0) {
                                this.hadWarnings = true;
                            }
                        } else {
                            this.warningCount = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8;
                            remaining -= 2;
                            if (this.warningCount > 0) {
                                this.hadWarnings = true;
                            }
                            this.oldServerStatus = this.serverStatus;
                            this.serverStatus = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8;
                            this.checkTransactionState((int)this.oldServerStatus);
                            remaining -= 2;
                        }
                        this.setServerSlowQueryFlags();
                        if (remaining <= 0) return null;
                        this.skipFully((InputStream)this.mysqlInput, (long)((long)remaining));
                        return null;
                    }
                    rowData = new byte[columnCount][];
                    firstTime = false;
                }
                len = 0;
                switch (sw) {
                    case 251: {
                        len = -1;
                        ** break;
                    }
                    case 252: {
                        len = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8;
                        remaining -= 2;
                        ** break;
                    }
                    case 253: {
                        len = this.mysqlInput.read() & 255 | (this.mysqlInput.read() & 255) << 8 | (this.mysqlInput.read() & 255) << 16;
                        remaining -= 3;
                        ** break;
                    }
                    case 254: {
                        len = (int)((long)(this.mysqlInput.read() & 255) | (long)(this.mysqlInput.read() & 255) << 8 | (long)(this.mysqlInput.read() & 255) << 16 | (long)(this.mysqlInput.read() & 255) << 24 | (long)(this.mysqlInput.read() & 255) << 32 | (long)(this.mysqlInput.read() & 255) << 40 | (long)(this.mysqlInput.read() & 255) << 48 | (long)(this.mysqlInput.read() & 255) << 56);
                        remaining -= 8;
                        ** break;
                    }
                }
                len = sw;
lbl87: // 5 sources:
                if (len == -1) {
                    rowData[i] = null;
                } else if (len == 0) {
                    rowData[i] = Constants.EMPTY_BYTE_ARRAY;
                } else {
                    rowData[i] = new byte[len];
                    bytesRead = this.readFully((InputStream)this.mysqlInput, (byte[])rowData[i], (int)0, (int)len);
                    if (bytesRead != len) {
                        throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)new IOException((String)Messages.getString((String)"MysqlIO.43")), (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    remaining -= bytesRead;
                }
                ++i;
            } while (true);
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void quit() throws SQLException {
        try {
            try {
                if (!ExportControlled.isSSLEstablished((Socket)this.mysqlConnection) && !this.mysqlConnection.isClosed()) {
                    try {
                        this.mysqlConnection.shutdownInput();
                    }
                    catch (UnsupportedOperationException e) {}
                }
            }
            catch (IOException e) {
                // empty catch block
            }
            Buffer packet = new Buffer((int)6);
            this.packetSequence = (byte)-1;
            this.compressedPacketSequence = (byte)-1;
            packet.writeByte((byte)1);
            this.send((Buffer)packet, (int)packet.getPosition());
            Object var3_4 = null;
            this.forceClose();
            return;
        }
        catch (Throwable throwable) {
            Object var3_5 = null;
            this.forceClose();
            throw throwable;
        }
    }

    Buffer getSharedSendPacket() {
        if (this.sharedSendPacket != null) return this.sharedSendPacket;
        this.sharedSendPacket = new Buffer((int)1024);
        return this.sharedSendPacket;
    }

    void closeStreamer(RowData streamer) throws SQLException {
        if (this.streamingData == null) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.17") + streamer + Messages.getString((String)"MysqlIO.18")), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (streamer != this.streamingData) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.19") + streamer + Messages.getString((String)"MysqlIO.20") + Messages.getString((String)"MysqlIO.21") + Messages.getString((String)"MysqlIO.22")), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.streamingData = null;
    }

    boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
        if ((this.serverStatus & 8) == 0) return false;
        boolean moreRowSetsExist = true;
        ResultSetImpl currentResultSet = addingTo;
        boolean firstTime = true;
        do {
            if (!moreRowSetsExist) return true;
            if (!firstTime && currentResultSet.reallyResult()) {
                return true;
            }
            firstTime = false;
            Buffer fieldPacket = this.checkErrorPacket();
            fieldPacket.setPosition((int)0);
            java.sql.Statement owningStatement = addingTo.getStatement();
            int maxRows = owningStatement.getMaxRows();
            ResultSetImpl newResultSet = this.readResultsForQueryOrUpdate((StatementImpl)((StatementImpl)owningStatement), (int)maxRows, (int)owningStatement.getResultSetType(), (int)owningStatement.getResultSetConcurrency(), (boolean)true, (String)owningStatement.getConnection().getCatalog(), (Buffer)fieldPacket, (boolean)addingTo.isBinaryEncoded, (long)-1L, null);
            currentResultSet.setNextResultSet((ResultSetInternalMethods)newResultSet);
            currentResultSet = newResultSet;
            boolean bl = moreRowSetsExist = (this.serverStatus & 8) != 0;
        } while (currentResultSet.reallyResult() || moreRowSetsExist);
        return false;
    }

    ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
        ResultSetImpl topLevelResultSet;
        boolean serverHasMoreResults;
        resultPacket.setPosition((int)(resultPacket.getPosition() - 1));
        ResultSetImpl currentResultSet = topLevelResultSet = this.readResultsForQueryOrUpdate((StatementImpl)callingStatement, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Buffer)resultPacket, (boolean)isBinaryEncoded, (long)preSentColumnCount, (Field[])metadataFromCache);
        boolean checkForMoreResults = (this.clientParam & 131072L) != 0L;
        boolean bl = serverHasMoreResults = (this.serverStatus & 8) != 0;
        if (serverHasMoreResults && streamResults) {
            if (topLevelResultSet.getUpdateCount() != -1L) {
                this.tackOnMoreStreamingResults((ResultSetImpl)topLevelResultSet);
            }
            this.reclaimLargeReusablePacket();
            return topLevelResultSet;
        }
        boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
        while (moreRowSetsExist) {
            Buffer fieldPacket = this.checkErrorPacket();
            fieldPacket.setPosition((int)0);
            ResultSetImpl newResultSet = this.readResultsForQueryOrUpdate((StatementImpl)callingStatement, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Buffer)fieldPacket, (boolean)isBinaryEncoded, (long)preSentColumnCount, (Field[])metadataFromCache);
            currentResultSet.setNextResultSet((ResultSetInternalMethods)newResultSet);
            currentResultSet = newResultSet;
            moreRowSetsExist = (this.serverStatus & 8) != 0;
        }
        if (!streamResults) {
            this.clearInputStream();
        }
        this.reclaimLargeReusablePacket();
        return topLevelResultSet;
    }

    void resetMaxBuf() {
        this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
    }

    /*
     * Exception decompiling
     */
    final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding, int timeoutMillis) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[TRYBLOCK]], but top level block is 11[CATCHBLOCK]
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

    protected boolean shouldIntercept() {
        if (this.statementInterceptors == null) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final ResultSetInternalMethods sqlQueryDirect(StatementImpl callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws Exception {
        ++this.statementExecutionDepth;
        try {
            ResultSetInternalMethods interceptedResults;
            if (this.statementInterceptors != null && (interceptedResults = this.invokeStatementInterceptorsPre((String)query, (Statement)callingStatement, (boolean)false)) != null) {
                ResultSetInternalMethods resultSetInternalMethods = interceptedResults;
                Object var29_17 = null;
                --this.statementExecutionDepth;
                return resultSetInternalMethods;
            }
            String statementComment = this.connection.getStatementComment();
            if (this.connection.getIncludeThreadNamesAsStatementComment()) {
                statementComment = (statementComment != null ? statementComment + ", " : "") + "java thread: " + Thread.currentThread().getName();
            }
            if (query != null) {
                int packLength = 5 + query.length() * 3 + 2;
                byte[] commentAsBytes = null;
                if (statementComment != null) {
                    commentAsBytes = StringUtils.getBytes((String)statementComment, null, (String)characterEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor());
                    packLength += commentAsBytes.length;
                    packLength += 6;
                }
                if (this.sendPacket == null) {
                    this.sendPacket = new Buffer((int)packLength);
                } else {
                    this.sendPacket.clear();
                }
                this.sendPacket.writeByte((byte)3);
                if (commentAsBytes != null) {
                    this.sendPacket.writeBytesNoNull((byte[])Constants.SLASH_STAR_SPACE_AS_BYTES);
                    this.sendPacket.writeBytesNoNull((byte[])commentAsBytes);
                    this.sendPacket.writeBytesNoNull((byte[])Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
                }
                if (characterEncoding != null) {
                    if (this.platformDbCharsetMatches) {
                        this.sendPacket.writeStringNoNull((String)query, (String)characterEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (MySQLConnection)this.connection);
                    } else if (StringUtils.startsWithIgnoreCaseAndWs((String)query, (String)"LOAD DATA")) {
                        this.sendPacket.writeBytesNoNull((byte[])StringUtils.getBytes((String)query));
                    } else {
                        this.sendPacket.writeStringNoNull((String)query, (String)characterEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (MySQLConnection)this.connection);
                    }
                } else {
                    this.sendPacket.writeStringNoNull((String)query);
                }
                queryPacket = this.sendPacket;
            }
            byte[] queryBuf = null;
            int oldPacketPosition = 0;
            long queryStartTime = 0L;
            if (this.needToGrabQueryFromPacket) {
                queryBuf = queryPacket.getByteBuffer();
                oldPacketPosition = queryPacket.getPosition();
                queryStartTime = this.getCurrentTimeNanosOrMillis();
            }
            if (this.autoGenerateTestcaseScript) {
                String testcaseQuery = null;
                testcaseQuery = query != null ? (statementComment != null ? "/* " + statementComment + " */ " + query : query) : StringUtils.toString((byte[])queryBuf, (int)5, (int)(oldPacketPosition - 5));
                StringBuilder debugBuf = new StringBuilder((int)(testcaseQuery.length() + 32));
                this.connection.generateConnectionCommentBlock((StringBuilder)debugBuf);
                debugBuf.append((String)testcaseQuery);
                debugBuf.append((char)';');
                this.connection.dumpTestcaseQuery((String)debugBuf.toString());
            }
            Buffer resultPacket = this.sendCommand((int)3, null, (Buffer)queryPacket, (boolean)false, null, (int)0);
            long fetchBeginTime = 0L;
            String profileQueryToLog = null;
            boolean queryWasSlow = false;
            long queryEndTime = 0L;
            if (this.profileSql || this.logSlowQueries) {
                queryEndTime = this.getCurrentTimeNanosOrMillis();
                boolean shouldExtractQuery = false;
                if (this.profileSql) {
                    shouldExtractQuery = true;
                } else if (this.logSlowQueries) {
                    boolean logSlow;
                    long queryTime = queryEndTime - queryStartTime;
                    boolean bl = this.useAutoSlowLog ? this.connection.isAbonormallyLongQuery((long)queryTime) : (logSlow = queryTime > (long)this.connection.getSlowQueryThresholdMillis());
                    if (logSlow) {
                        shouldExtractQuery = true;
                        queryWasSlow = true;
                    }
                }
                if (shouldExtractQuery) {
                    boolean truncated = false;
                    int extractPosition = oldPacketPosition;
                    if (oldPacketPosition > this.connection.getMaxQuerySizeToLog()) {
                        extractPosition = this.connection.getMaxQuerySizeToLog() + 5;
                        truncated = true;
                    }
                    profileQueryToLog = StringUtils.toString((byte[])queryBuf, (int)5, (int)(extractPosition - 5));
                    if (truncated) {
                        profileQueryToLog = profileQueryToLog + Messages.getString((String)"MysqlIO.25");
                    }
                }
                fetchBeginTime = queryEndTime;
            }
            ResultSetInternalMethods rs = this.readAllResults((StatementImpl)callingStatement, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Buffer)resultPacket, (boolean)false, (long)-1L, (Field[])cachedMetadata);
            if (queryWasSlow && !this.serverQueryWasSlow) {
                this.connection.getProfilerEventHandlerInstance().processEvent((byte)6, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)((long)((int)(queryEndTime - queryStartTime))), (Throwable)new Throwable(), (String)Messages.getString((String)"Protocol.SlowQuery", (Object[])new Object[]{this.useAutoSlowLog ? " 95% of all queries " : String.valueOf((long)this.slowQueryThreshold), this.queryTimingUnits, Long.valueOf((long)(queryEndTime - queryStartTime)), profileQueryToLog}));
                if (this.connection.getExplainSlowQueries()) {
                    if (oldPacketPosition < 1048576) {
                        this.explainSlowQuery((byte[])queryPacket.getBytes((int)5, (int)(oldPacketPosition - 5)), (String)profileQueryToLog);
                    } else {
                        this.connection.getLog().logWarn((Object)(Messages.getString((String)"MysqlIO.28") + 1048576 + Messages.getString((String)"MysqlIO.29")));
                    }
                }
            }
            if (this.logSlowQueries) {
                if (this.queryBadIndexUsed && this.profileSql) {
                    this.connection.getProfilerEventHandlerInstance().processEvent((byte)6, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)(queryEndTime - queryStartTime), (Throwable)new Throwable(), (String)(Messages.getString((String)"MysqlIO.33") + profileQueryToLog));
                }
                if (this.queryNoIndexUsed && this.profileSql) {
                    this.connection.getProfilerEventHandlerInstance().processEvent((byte)6, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)(queryEndTime - queryStartTime), (Throwable)new Throwable(), (String)(Messages.getString((String)"MysqlIO.35") + profileQueryToLog));
                }
                if (this.serverQueryWasSlow && this.profileSql) {
                    this.connection.getProfilerEventHandlerInstance().processEvent((byte)6, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)(queryEndTime - queryStartTime), (Throwable)new Throwable(), (String)(Messages.getString((String)"MysqlIO.ServerSlowQuery") + profileQueryToLog));
                }
            }
            if (this.profileSql) {
                this.connection.getProfilerEventHandlerInstance().processEvent((byte)3, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)(queryEndTime - queryStartTime), (Throwable)new Throwable(), (String)profileQueryToLog);
                this.connection.getProfilerEventHandlerInstance().processEvent((byte)5, (MySQLConnection)this.connection, (Statement)callingStatement, (ResultSetInternalMethods)rs, (long)(this.getCurrentTimeNanosOrMillis() - fetchBeginTime), (Throwable)new Throwable(), null);
            }
            if (this.hadWarnings) {
                this.scanForAndThrowDataTruncation();
            }
            if (this.statementInterceptors != null) {
                rs = this.invokeStatementInterceptorsPost((String)query, (Statement)callingStatement, (ResultSetInternalMethods)rs, (boolean)false, null);
            }
            ResultSetImpl truncated = rs;
            Object var29_18 = null;
            --this.statementExecutionDepth;
            return truncated;
        }
        catch (SQLException sqlEx) {
            try {
                if (this.statementInterceptors != null) {
                    this.invokeStatementInterceptorsPost((String)query, (Statement)callingStatement, null, (boolean)false, (SQLException)sqlEx);
                }
                if (callingStatement == null) throw sqlEx;
                Object queryBuf = callingStatement.cancelTimeoutMutex;
                // MONITORENTER : queryBuf
                if (!callingStatement.wasCancelled) {
                    // MONITOREXIT : queryBuf
                    throw sqlEx;
                }
                SQLException cause = null;
                cause = callingStatement.wasCancelledByTimeout ? new MySQLTimeoutException() : new MySQLStatementCancelledException();
                callingStatement.resetCancelledState();
                throw cause;
            }
            catch (Throwable throwable) {
                Object var29_19 = null;
                --this.statementExecutionDepth;
                throw throwable;
            }
        }
    }

    ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement, boolean forceExecute) throws SQLException {
        ResultSetInternalMethods previousResultSet = null;
        int i = 0;
        int s = this.statementInterceptors.size();
        while (i < s) {
            String sqlToInterceptor;
            ResultSetInternalMethods interceptedResultSet;
            boolean shouldExecute;
            StatementInterceptorV2 interceptor = this.statementInterceptors.get((int)i);
            boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
            boolean bl = shouldExecute = executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute) || !executeTopLevelOnly;
            if (shouldExecute && (interceptedResultSet = interceptor.preProcess((String)(sqlToInterceptor = sql), (Statement)interceptedStatement, (Connection)this.connection)) != null) {
                previousResultSet = interceptedResultSet;
            }
            ++i;
        }
        return previousResultSet;
    }

    ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, boolean forceExecute, SQLException statementException) throws SQLException {
        int i = 0;
        int s = this.statementInterceptors.size();
        while (i < s) {
            String sqlToInterceptor;
            ResultSetInternalMethods interceptedResultSet;
            boolean shouldExecute;
            StatementInterceptorV2 interceptor = this.statementInterceptors.get((int)i);
            boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
            boolean bl = shouldExecute = executeTopLevelOnly && (this.statementExecutionDepth == 1 || forceExecute) || !executeTopLevelOnly;
            if (shouldExecute && (interceptedResultSet = interceptor.postProcess((String)(sqlToInterceptor = sql), (Statement)interceptedStatement, (ResultSetInternalMethods)originalResultSet, (Connection)this.connection, (int)this.warningCount, (boolean)this.queryNoIndexUsed, (boolean)this.queryBadIndexUsed, (SQLException)statementException)) != null) {
                originalResultSet = interceptedResultSet;
            }
            ++i;
        }
        return originalResultSet;
    }

    private void calculateSlowQueryThreshold() {
        this.slowQueryThreshold = (long)this.connection.getSlowQueryThresholdMillis();
        if (!this.connection.getUseNanosForElapsedTime()) return;
        long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
        if (nanosThreshold != 0L) {
            this.slowQueryThreshold = nanosThreshold;
            return;
        }
        this.slowQueryThreshold *= 1000000L;
    }

    protected long getCurrentTimeNanosOrMillis() {
        long l;
        if (this.useNanosForElapsedTime) {
            l = TimeUtil.getCurrentTimeNanosOrMillis();
            return l;
        }
        l = System.currentTimeMillis();
        return l;
    }

    String getHost() {
        return this.host;
    }

    boolean isVersion(int major, int minor, int subminor) {
        if (major != this.getServerMajorVersion()) return false;
        if (minor != this.getServerMinorVersion()) return false;
        if (subminor != this.getServerSubMinorVersion()) return false;
        return true;
    }

    boolean versionMeetsMinimum(int major, int minor, int subminor) {
        if (this.getServerMajorVersion() < major) return false;
        if (this.getServerMajorVersion() != major) return true;
        if (this.getServerMinorVersion() < minor) return false;
        if (this.getServerMinorVersion() != minor) return true;
        if (this.getServerSubMinorVersion() < subminor) return false;
        return true;
    }

    private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength) {
        if (packetLength < 1024) {
            return packetToDump.dump((int)packetLength);
        }
        StringBuilder packetDumpBuf = new StringBuilder((int)4096);
        packetDumpBuf.append((String)packetToDump.dump((int)1024));
        packetDumpBuf.append((String)Messages.getString((String)"MysqlIO.36"));
        packetDumpBuf.append((int)1024);
        packetDumpBuf.append((String)Messages.getString((String)"MysqlIO.37"));
        return packetDumpBuf.toString();
    }

    private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = in.read((byte[])b, (int)(off + n), (int)(len - n));
            if (count < 0) {
                throw new EOFException((String)Messages.getString((String)"MysqlIO.EOF", (Object[])new Object[]{Integer.valueOf((int)len), Integer.valueOf((int)n)}));
            }
            n += count;
        }
        return n;
    }

    private final long skipFully(InputStream in, long len) throws IOException {
        if (len < 0L) {
            throw new IOException((String)"Negative skip length not allowed");
        }
        long n = 0L;
        while (n < len) {
            long count = in.skip((long)(len - n));
            if (count < 0L) {
                throw new EOFException((String)Messages.getString((String)"MysqlIO.EOF", (Object[])new Object[]{Long.valueOf((long)len), Long.valueOf((long)n)}));
            }
            n += count;
        }
        return n;
    }

    private final int skipLengthEncodedInteger(InputStream in) throws IOException {
        int sw = in.read() & 255;
        switch (sw) {
            case 252: {
                return (int)this.skipFully((InputStream)in, (long)2L) + 1;
            }
            case 253: {
                return (int)this.skipFully((InputStream)in, (long)3L) + 1;
            }
            case 254: {
                return (int)this.skipFully((InputStream)in, (long)8L) + 1;
            }
        }
        return 1;
    }

    protected final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
        long columnCount = resultPacket.readFieldLength();
        if (columnCount == 0L) {
            return this.buildResultSetWithUpdates((StatementImpl)callingStatement, (Buffer)resultPacket);
        }
        if (columnCount != -1L) {
            return this.getResultSet((StatementImpl)callingStatement, (long)columnCount, (int)maxRows, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (boolean)isBinaryEncoded, (Field[])metadataFromCache);
        }
        String charEncoding = null;
        if (this.connection.getUseUnicode()) {
            charEncoding = this.connection.getEncoding();
        }
        String fileName = null;
        if (!this.platformDbCharsetMatches) {
            fileName = resultPacket.readString();
            return this.sendFileToServer((StatementImpl)callingStatement, (String)fileName);
        }
        fileName = charEncoding != null ? resultPacket.readString((String)charEncoding, (ExceptionInterceptor)this.getExceptionInterceptor()) : resultPacket.readString();
        return this.sendFileToServer((StatementImpl)callingStatement, (String)fileName);
    }

    private int alignPacketSize(int a, int l) {
        return a + l - 1 & ~(l - 1);
    }

    private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded) throws SQLException {
        ResultSetImpl rs = null;
        switch (resultSetConcurrency) {
            case 1007: {
                rs = ResultSetImpl.getInstance((String)catalog, (Field[])fields, (RowData)rows, (MySQLConnection)this.connection, (StatementImpl)callingStatement, (boolean)false);
                if (!isBinaryEncoded) break;
                rs.setBinaryEncoded();
                break;
            }
            case 1008: {
                rs = ResultSetImpl.getInstance((String)catalog, (Field[])fields, (RowData)rows, (MySQLConnection)this.connection, (StatementImpl)callingStatement, (boolean)true);
                break;
            }
            default: {
                return ResultSetImpl.getInstance((String)catalog, (Field[])fields, (RowData)rows, (MySQLConnection)this.connection, (StatementImpl)callingStatement, (boolean)false);
            }
        }
        rs.setResultSetType((int)resultSetType);
        rs.setResultSetConcurrency((int)resultSetConcurrency);
        return rs;
    }

    private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket) throws SQLException {
        long updateCount = -1L;
        long updateID = -1L;
        String info = null;
        try {
            if (this.useNewUpdateCounts) {
                updateCount = resultPacket.newReadLength();
                updateID = resultPacket.newReadLength();
            } else {
                updateCount = resultPacket.readLength();
                updateID = resultPacket.readLength();
            }
            if (this.use41Extensions) {
                this.serverStatus = resultPacket.readInt();
                this.checkTransactionState((int)this.oldServerStatus);
                this.warningCount = resultPacket.readInt();
                if (this.warningCount > 0) {
                    this.hadWarnings = true;
                }
                resultPacket.readByte();
                this.setServerSlowQueryFlags();
            }
            if (this.connection.isReadInfoMsgEnabled()) {
                info = resultPacket.readString((String)this.connection.getErrorMessageEncoding(), (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)SQLError.get((String)"S1000"), (String)"S1000", (int)-1, (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
        ResultSetImpl updateRs = ResultSetImpl.getInstance((long)updateCount, (long)updateID, (MySQLConnection)this.connection, (StatementImpl)callingStatement);
        if (info == null) return updateRs;
        updateRs.setServerInfo((String)info);
        return updateRs;
    }

    private void setServerSlowQueryFlags() {
        this.queryBadIndexUsed = (this.serverStatus & 16) != 0;
        this.queryNoIndexUsed = (this.serverStatus & 32) != 0;
        this.serverQueryWasSlow = (this.serverStatus & 2048) != 0;
    }

    private void checkForOutstandingStreamingData() throws SQLException {
        if (this.streamingData == null) return;
        boolean shouldClobber = this.connection.getClobberStreamingResults();
        if (!shouldClobber) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.39") + this.streamingData + Messages.getString((String)"MysqlIO.40") + Messages.getString((String)"MysqlIO.41") + Messages.getString((String)"MysqlIO.42")), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.streamingData.getOwner().realClose((boolean)false);
        this.clearInputStream();
    }

    private Buffer compressPacket(Buffer packet, int offset, int packetLen) throws SQLException {
        int compressedLength = packetLen;
        int uncompressedLength = 0;
        byte[] compressedBytes = null;
        int offsetWrite = offset;
        if (packetLen < 50) {
            compressedBytes = packet.getByteBuffer();
        } else {
            byte[] bytesToCompress = packet.getByteBuffer();
            compressedBytes = new byte[bytesToCompress.length * 2];
            if (this.deflater == null) {
                this.deflater = new Deflater();
            }
            this.deflater.reset();
            this.deflater.setInput((byte[])bytesToCompress, (int)offset, (int)packetLen);
            this.deflater.finish();
            compressedLength = this.deflater.deflate((byte[])compressedBytes);
            if (compressedLength > packetLen) {
                compressedBytes = packet.getByteBuffer();
                compressedLength = packetLen;
            } else {
                uncompressedLength = packetLen;
                offsetWrite = 0;
            }
        }
        Buffer compressedPacket = new Buffer((int)(7 + compressedLength));
        compressedPacket.setPosition((int)0);
        compressedPacket.writeLongInt((int)compressedLength);
        compressedPacket.writeByte((byte)this.compressedPacketSequence);
        compressedPacket.writeLongInt((int)uncompressedLength);
        compressedPacket.writeBytesNoNull((byte[])compressedBytes, (int)offsetWrite, (int)compressedLength);
        return compressedPacket;
    }

    private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException {
        if (!this.use41Extensions) return;
        rowPacket.readByte();
        if (this.isEOFDeprecated()) {
            rowPacket.newReadLength();
            rowPacket.newReadLength();
            this.oldServerStatus = this.serverStatus;
            this.serverStatus = rowPacket.readInt();
            this.checkTransactionState((int)this.oldServerStatus);
            this.warningCount = rowPacket.readInt();
            if (this.warningCount > 0) {
                this.hadWarnings = true;
            }
            rowPacket.readByte();
            if (this.connection.isReadInfoMsgEnabled()) {
                rowPacket.readString((String)this.connection.getErrorMessageEncoding(), (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        } else {
            this.warningCount = rowPacket.readInt();
            if (this.warningCount > 0) {
                this.hadWarnings = true;
            }
            this.oldServerStatus = this.serverStatus;
            this.serverStatus = rowPacket.readInt();
            this.checkTransactionState((int)this.oldServerStatus);
        }
        this.setServerSlowQueryFlags();
    }

    private SocketFactory createSocketFactory() throws SQLException {
        try {
            if (this.socketFactoryClassName != null) return (SocketFactory)Class.forName((String)this.socketFactoryClassName).newInstance();
            throw SQLError.createSQLException((String)Messages.getString((String)"MysqlIO.75"), (String)"08001", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.76") + this.socketFactoryClassName + Messages.getString((String)"MysqlIO.77")), (String)"08001", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet) throws SQLException {
        if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
            this.packetDebugRingBuffer.removeFirst();
        }
        StringBuilder packetDump = null;
        if (!isPacketBeingSent) {
            int bytesToDump = Math.min((int)1024, (int)packet.getBufLength());
            Buffer packetToDump = new Buffer((int)(4 + bytesToDump));
            packetToDump.setPosition((int)0);
            packetToDump.writeBytesNoNull((byte[])header);
            packetToDump.writeBytesNoNull((byte[])packet.getBytes((int)0, (int)bytesToDump));
            String packetPayload = packetToDump.dump((int)bytesToDump);
            packetDump = new StringBuilder((int)(96 + packetPayload.length()));
            packetDump.append((String)"Server ");
            packetDump.append((String)(isPacketReused ? "(re-used) " : "(new) "));
            packetDump.append((String)packet.toSuperString());
            packetDump.append((String)" --------------------> Client\n");
            packetDump.append((String)"\nPacket payload:\n\n");
            packetDump.append((String)packetPayload);
            if (bytesToDump == 1024) {
                packetDump.append((String)("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n"));
            }
        } else {
            int bytesToDump = Math.min((int)1024, (int)sendLength);
            String packetPayload = packet.dump((int)bytesToDump);
            packetDump = new StringBuilder((int)(68 + packetPayload.length()));
            packetDump.append((String)"Client ");
            packetDump.append((String)packet.toSuperString());
            packetDump.append((String)"--------------------> Server\n");
            packetDump.append((String)"\nPacket payload:\n\n");
            packetDump.append((String)packetPayload);
            if (bytesToDump == 1024) {
                packetDump.append((String)("\nNote: Packet of " + sendLength + " bytes truncated to " + 1024 + " bytes.\n"));
            }
        }
        this.packetDebugRingBuffer.addLast((StringBuilder)packetDump);
    }

    private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields) throws SQLException {
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        boolean useBufferRowExplicit = MysqlIO.useBufferRowExplicit((Field[])fields);
        ResultSetRow row = this.nextRow((Field[])fields, (int)((int)columnCount), (boolean)isBinaryEncoded, (int)resultSetConcurrency, (boolean)false, (boolean)useBufferRowExplicit, (boolean)false, null);
        int rowCount = 0;
        if (row != null) {
            rows.add(row);
            rowCount = 1;
        }
        while (row != null) {
            row = this.nextRow((Field[])fields, (int)((int)columnCount), (boolean)isBinaryEncoded, (int)resultSetConcurrency, (boolean)false, (boolean)useBufferRowExplicit, (boolean)false, null);
            if (row == null || maxRows != -1 && rowCount >= maxRows) continue;
            rows.add((ResultSetRow)row);
            ++rowCount;
        }
        return new RowDataStatic(rows);
    }

    /*
     * Exception decompiling
     */
    public static boolean useBufferRowExplicit(Field[] fields) {
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

    private void reclaimLargeReusablePacket() {
        if (this.reusablePacket == null) return;
        if (this.reusablePacket.getCapacity() <= 1048576) return;
        this.reusablePacket = new Buffer((int)1024);
    }

    private final Buffer reuseAndReadPacket(Buffer reuse) throws SQLException {
        return this.reuseAndReadPacket((Buffer)reuse, (int)-1);
    }

    private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException {
        try {
            reuse.setWasMultiPacket((boolean)false);
            int packetLength = 0;
            if (existingPacketLength == -1) {
                int lengthRead = this.readFully((InputStream)this.mysqlInput, (byte[])this.packetHeaderBuf, (int)0, (int)4);
                if (lengthRead < 4) {
                    this.forceClose();
                    throw new IOException((String)Messages.getString((String)"MysqlIO.43"));
                }
                packetLength = (this.packetHeaderBuf[0] & 255) + ((this.packetHeaderBuf[1] & 255) << 8) + ((this.packetHeaderBuf[2] & 255) << 16);
            } else {
                packetLength = existingPacketLength;
            }
            if (this.traceProtocol) {
                StringBuilder traceMessageBuf = new StringBuilder();
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.44"));
                traceMessageBuf.append((int)packetLength);
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.45"));
                traceMessageBuf.append((String)StringUtils.dumpAsHex((byte[])this.packetHeaderBuf, (int)4));
                this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
            }
            byte multiPacketSeq = this.packetHeaderBuf[3];
            if (!this.packetSequenceReset) {
                if (this.enablePacketDebug && this.checkPacketSequence) {
                    this.checkPacketSequencing((byte)multiPacketSeq);
                }
            } else {
                this.packetSequenceReset = false;
            }
            this.readPacketSequence = multiPacketSeq;
            reuse.setPosition((int)0);
            if (reuse.getByteBuffer().length <= packetLength) {
                reuse.setByteBuffer((byte[])new byte[packetLength + 1]);
            }
            reuse.setBufLength((int)packetLength);
            int numBytesRead = this.readFully((InputStream)this.mysqlInput, (byte[])reuse.getByteBuffer(), (int)0, (int)packetLength);
            if (numBytesRead != packetLength) {
                throw new IOException((String)("Short read, expected " + packetLength + " bytes, only read " + numBytesRead));
            }
            if (this.traceProtocol) {
                StringBuilder traceMessageBuf = new StringBuilder();
                traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.46"));
                traceMessageBuf.append((String)MysqlIO.getPacketDumpToLog((Buffer)reuse, (int)packetLength));
                this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
            }
            if (this.enablePacketDebug) {
                this.enqueuePacketForDebugging((boolean)false, (boolean)true, (int)0, (byte[])this.packetHeaderBuf, (Buffer)reuse);
            }
            boolean isMultiPacket = false;
            if (packetLength == this.maxThreeBytes) {
                reuse.setPosition((int)this.maxThreeBytes);
                isMultiPacket = true;
                packetLength = this.readRemainingMultiPackets((Buffer)reuse, (byte)multiPacketSeq);
            }
            if (!isMultiPacket) {
                reuse.getByteBuffer()[packetLength] = 0;
            }
            if (!this.connection.getMaintainTimeStats()) return reuse;
            this.lastPacketReceivedTimeMs = System.currentTimeMillis();
            return reuse;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (OutOfMemoryError oom) {
            try {
                this.clearInputStream();
            }
            catch (Exception ex) {
                // empty catch block
            }
            try {
                this.connection.realClose((boolean)false, (boolean)false, (boolean)true, (Throwable)oom);
                throw oom;
            }
            catch (Exception ex) {
                // empty catch block
            }
            throw oom;
        }
    }

    private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq) throws IOException, SQLException {
        int packetLength = -1;
        Buffer multiPacket = null;
        do {
            int lengthRead;
            if ((lengthRead = this.readFully((InputStream)this.mysqlInput, (byte[])this.packetHeaderBuf, (int)0, (int)4)) < 4) {
                this.forceClose();
                throw new IOException((String)Messages.getString((String)"MysqlIO.47"));
            }
            packetLength = (this.packetHeaderBuf[0] & 255) + ((this.packetHeaderBuf[1] & 255) << 8) + ((this.packetHeaderBuf[2] & 255) << 16);
            if (multiPacket == null) {
                multiPacket = new Buffer((int)packetLength);
            }
            if (!this.useNewLargePackets && packetLength == 1) {
                this.clearInputStream();
                break;
            }
            if ((multiPacketSeq = (byte)(multiPacketSeq + 1)) != this.packetHeaderBuf[3]) {
                throw new IOException((String)Messages.getString((String)"MysqlIO.49"));
            }
            multiPacket.setPosition((int)0);
            multiPacket.setBufLength((int)packetLength);
            byte[] byteBuf = multiPacket.getByteBuffer();
            int lengthToWrite = packetLength;
            int bytesRead = this.readFully((InputStream)this.mysqlInput, (byte[])byteBuf, (int)0, (int)packetLength);
            if (bytesRead != lengthToWrite) {
                throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.50") + lengthToWrite + Messages.getString((String)"MysqlIO.51") + bytesRead + "."), (ExceptionInterceptor)this.getExceptionInterceptor()), (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            reuse.writeBytesNoNull((byte[])byteBuf, (int)0, (int)lengthToWrite);
        } while (packetLength == this.maxThreeBytes);
        reuse.setPosition((int)0);
        reuse.setWasMultiPacket((boolean)true);
        return packetLength;
    }

    private void checkPacketSequencing(byte multiPacketSeq) throws SQLException {
        if (multiPacketSeq == -128 && this.readPacketSequence != 127) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)new IOException((String)("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq)), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.readPacketSequence == -1 && multiPacketSeq != 0) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)new IOException((String)("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq)), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (multiPacketSeq == -128) return;
        if (this.readPacketSequence == -1) return;
        if (multiPacketSeq == this.readPacketSequence + 1) return;
        throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)new IOException((String)("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq)), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    void enableMultiQueries() throws SQLException {
        Buffer buf = this.getSharedSendPacket();
        buf.clear();
        buf.writeByte((byte)27);
        buf.writeInt((int)0);
        this.sendCommand((int)27, null, (Buffer)buf, (boolean)false, null, (int)0);
        this.preserveOldTransactionState();
    }

    void disableMultiQueries() throws SQLException {
        Buffer buf = this.getSharedSendPacket();
        buf.clear();
        buf.writeByte((byte)27);
        buf.writeInt((int)1);
        this.sendCommand((int)27, null, (Buffer)buf, (boolean)false, null, (int)0);
        this.preserveOldTransactionState();
    }

    private final void send(Buffer packet, int packetLen) throws SQLException {
        try {
            if (this.maxAllowedPacket > 0 && packetLen > this.maxAllowedPacket) {
                throw new PacketTooBigException((long)((long)packetLen), (long)((long)this.maxAllowedPacket));
            }
            if (this.serverMajorVersion >= 4 && (packetLen - 4 >= this.maxThreeBytes || this.useCompression && packetLen - 4 >= this.maxThreeBytes - 3)) {
                this.sendSplitPackets((Buffer)packet, (int)packetLen);
            } else {
                this.packetSequence = (byte)(this.packetSequence + 1);
                Buffer packetToSend = packet;
                packetToSend.setPosition((int)0);
                packetToSend.writeLongInt((int)(packetLen - 4));
                packetToSend.writeByte((byte)this.packetSequence);
                if (this.useCompression) {
                    this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
                    int originalPacketLen = packetLen;
                    packetToSend = this.compressPacket((Buffer)packetToSend, (int)0, (int)packetLen);
                    packetLen = packetToSend.getPosition();
                    if (this.traceProtocol) {
                        StringBuilder traceMessageBuf = new StringBuilder();
                        traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.57"));
                        traceMessageBuf.append((String)MysqlIO.getPacketDumpToLog((Buffer)packetToSend, (int)packetLen));
                        traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.58"));
                        traceMessageBuf.append((String)MysqlIO.getPacketDumpToLog((Buffer)packet, (int)originalPacketLen));
                        this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
                    }
                } else if (this.traceProtocol) {
                    StringBuilder traceMessageBuf = new StringBuilder();
                    traceMessageBuf.append((String)Messages.getString((String)"MysqlIO.59"));
                    traceMessageBuf.append((String)"host: '");
                    traceMessageBuf.append((String)this.host);
                    traceMessageBuf.append((String)"' threadId: '");
                    traceMessageBuf.append((long)this.threadId);
                    traceMessageBuf.append((String)"'\n");
                    traceMessageBuf.append((String)packetToSend.dump((int)packetLen));
                    this.connection.getLog().logTrace((Object)traceMessageBuf.toString());
                }
                this.mysqlOutput.write((byte[])packetToSend.getByteBuffer(), (int)0, (int)packetLen);
                this.mysqlOutput.flush();
            }
            if (this.enablePacketDebug) {
                this.enqueuePacketForDebugging((boolean)true, (boolean)false, (int)(packetLen + 5), (byte[])this.packetHeaderBuf, (Buffer)packet);
            }
            if (packet == this.sharedSendPacket) {
                this.reclaimLargeSharedSendPacket();
            }
            if (!this.connection.getMaintainTimeStats()) return;
            this.lastPacketSentTimeMs = System.currentTimeMillis();
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    /*
     * Exception decompiling
     */
    private final ResultSetImpl sendFileToServer(StatementImpl callingStatement, String fileName) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 4[TRYBLOCK]
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

    private Buffer checkErrorPacket(int command) throws SQLException {
        Buffer resultPacket = null;
        this.serverStatus = 0;
        try {
            resultPacket = this.reuseAndReadPacket((Buffer)this.reusablePacket);
        }
        catch (SQLException sqlEx) {
            throw sqlEx;
        }
        catch (Exception fallThru) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)fallThru, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.checkErrorPacket((Buffer)resultPacket);
        return resultPacket;
    }

    private void checkErrorPacket(Buffer resultPacket) throws SQLException {
        byte statusCode = resultPacket.readByte();
        if (statusCode != -1) return;
        int errno = 2000;
        if (this.protocolVersion > 9) {
            errno = resultPacket.readInt();
            String xOpen = null;
            String serverErrorMessage = resultPacket.readString((String)this.connection.getErrorMessageEncoding(), (ExceptionInterceptor)this.getExceptionInterceptor());
            if (serverErrorMessage.charAt((int)0) == '#') {
                if (serverErrorMessage.length() > 6) {
                    xOpen = serverErrorMessage.substring((int)1, (int)6);
                    serverErrorMessage = serverErrorMessage.substring((int)6);
                    if (xOpen.equals((Object)"HY000")) {
                        xOpen = SQLError.mysqlToSqlState((int)errno, (boolean)this.connection.getUseSqlStateCodes());
                    }
                } else {
                    xOpen = SQLError.mysqlToSqlState((int)errno, (boolean)this.connection.getUseSqlStateCodes());
                }
            } else {
                xOpen = SQLError.mysqlToSqlState((int)errno, (boolean)this.connection.getUseSqlStateCodes());
            }
            this.clearInputStream();
            StringBuilder errorBuf = new StringBuilder();
            String xOpenErrorMessage = SQLError.get((String)xOpen);
            if (!this.connection.getUseOnlyServerErrorMessages() && xOpenErrorMessage != null) {
                errorBuf.append((String)xOpenErrorMessage);
                errorBuf.append((String)Messages.getString((String)"MysqlIO.68"));
            }
            errorBuf.append((String)serverErrorMessage);
            if (!this.connection.getUseOnlyServerErrorMessages() && xOpenErrorMessage != null) {
                errorBuf.append((String)"\"");
            }
            this.appendDeadlockStatusInformation((String)xOpen, (StringBuilder)errorBuf);
            if (xOpen == null) throw SQLError.createSQLException((String)errorBuf.toString(), (String)xOpen, (int)errno, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this.connection);
            if (!xOpen.startsWith((String)"22")) throw SQLError.createSQLException((String)errorBuf.toString(), (String)xOpen, (int)errno, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this.connection);
            throw new MysqlDataTruncation((String)errorBuf.toString(), (int)0, (boolean)true, (boolean)false, (int)0, (int)0, (int)errno);
        }
        String serverErrorMessage = resultPacket.readString((String)this.connection.getErrorMessageEncoding(), (ExceptionInterceptor)this.getExceptionInterceptor());
        this.clearInputStream();
        if (serverErrorMessage.indexOf((String)Messages.getString((String)"MysqlIO.70")) != -1) {
            throw SQLError.createSQLException((String)(SQLError.get((String)"S0022") + ", " + serverErrorMessage), (String)"S0022", (int)-1, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this.connection);
        }
        StringBuilder errorBuf = new StringBuilder((String)Messages.getString((String)"MysqlIO.72"));
        errorBuf.append((String)serverErrorMessage);
        errorBuf.append((String)"\"");
        throw SQLError.createSQLException((String)(SQLError.get((String)"S1000") + ", " + errorBuf.toString()), (String)"S1000", (int)-1, (boolean)false, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this.connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void appendDeadlockStatusInformation(String xOpen, StringBuilder errorBuf) throws SQLException {
        if (this.connection.getIncludeInnodbStatusInDeadlockExceptions() && xOpen != null && (xOpen.startsWith((String)"40") || xOpen.startsWith((String)"41")) && this.streamingData == null) {
            ResultSet rs = null;
            try {
                Object var6_4;
                block16 : {
                    try {
                        rs = this.sqlQueryDirect(null, (String)"SHOW ENGINE INNODB STATUS", (String)this.connection.getEncoding(), null, (int)-1, (int)1003, (int)1007, (boolean)false, (String)this.connection.getCatalog(), null);
                        if (rs.next()) {
                            errorBuf.append((String)"\n\n");
                            errorBuf.append((String)rs.getString((String)"Status"));
                            break block16;
                        }
                        errorBuf.append((String)"\n\n");
                        errorBuf.append((String)Messages.getString((String)"MysqlIO.NoInnoDBStatusFound"));
                    }
                    catch (Exception ex) {
                        errorBuf.append((String)"\n\n");
                        errorBuf.append((String)Messages.getString((String)"MysqlIO.InnoDBStatusFailed"));
                        errorBuf.append((String)"\n\n");
                        errorBuf.append((String)Util.stackTraceToString((Throwable)ex));
                        var6_4 = null;
                        if (rs != null) {
                            rs.close();
                        }
                    }
                }
                var6_4 = null;
                if (rs != null) {
                    rs.close();
                }
            }
            catch (Throwable throwable) {
                Object var6_5 = null;
                if (rs == null) throw throwable;
                rs.close();
                throw throwable;
            }
        }
        if (!this.connection.getIncludeThreadDumpInDeadlockExceptions()) return;
        errorBuf.append((String)"\n\n*** Java threads running at time of deadlock ***\n\n");
        ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMBean.getAllThreadIds();
        ThreadInfo[] threads = threadMBean.getThreadInfo((long[])threadIds, (int)Integer.MAX_VALUE);
        ArrayList<ThreadInfo> activeThreads = new ArrayList<ThreadInfo>();
        for (ThreadInfo info : threads) {
            if (info == null) continue;
            activeThreads.add(info);
        }
        Iterator<E> i$ = activeThreads.iterator();
        block5 : while (i$.hasNext()) {
            StackTraceElement[] stackTrace;
            ThreadInfo threadInfo = (ThreadInfo)i$.next();
            errorBuf.append((char)'\"');
            errorBuf.append((String)threadInfo.getThreadName());
            errorBuf.append((String)"\" tid=");
            errorBuf.append((long)threadInfo.getThreadId());
            errorBuf.append((String)" ");
            errorBuf.append((Object)((Object)threadInfo.getThreadState()));
            if (threadInfo.getLockName() != null) {
                errorBuf.append((String)(" on lock=" + threadInfo.getLockName()));
            }
            if (threadInfo.isSuspended()) {
                errorBuf.append((String)" (suspended)");
            }
            if (threadInfo.isInNative()) {
                errorBuf.append((String)" (running in native)");
            }
            if ((stackTrace = threadInfo.getStackTrace()).length > 0) {
                errorBuf.append((String)" in ");
                errorBuf.append((String)stackTrace[0].getClassName());
                errorBuf.append((String)".");
                errorBuf.append((String)stackTrace[0].getMethodName());
                errorBuf.append((String)"()");
            }
            errorBuf.append((String)"\n");
            if (threadInfo.getLockOwnerName() != null) {
                errorBuf.append((String)("\t owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId()));
                errorBuf.append((String)"\n");
            }
            int j = 0;
            do {
                if (j >= stackTrace.length) continue block5;
                StackTraceElement ste = stackTrace[j];
                errorBuf.append((String)("\tat " + ste.toString()));
                errorBuf.append((String)"\n");
                ++j;
            } while (true);
            break;
        }
        return;
    }

    private final void sendSplitPackets(Buffer packet, int packetLen) throws SQLException {
        try {
            Buffer toCompress;
            int len;
            Buffer packetToSend = this.splitBufRef == null ? null : this.splitBufRef.get();
            Buffer buffer = toCompress = !this.useCompression || this.compressBufRef == null ? null : this.compressBufRef.get();
            if (packetToSend == null) {
                packetToSend = new Buffer((int)(this.maxThreeBytes + 4));
                this.splitBufRef = new SoftReference<Buffer>(packetToSend);
            }
            if (this.useCompression) {
                int cbuflen = packetLen + (packetLen / this.maxThreeBytes + 1) * 4;
                if (toCompress == null) {
                    toCompress = new Buffer((int)cbuflen);
                    this.compressBufRef = new SoftReference<Buffer>(toCompress);
                } else if (toCompress.getBufLength() < cbuflen) {
                    toCompress.setPosition((int)toCompress.getBufLength());
                    toCompress.ensureCapacity((int)(cbuflen - toCompress.getBufLength()));
                }
            }
            int splitSize = this.maxThreeBytes;
            int originalPacketPos = 4;
            byte[] origPacketBytes = packet.getByteBuffer();
            int toCompressPosition = 0;
            for (len = packetLen - 4; len >= 0; originalPacketPos += splitSize, len -= this.maxThreeBytes) {
                this.packetSequence = (byte)(this.packetSequence + 1);
                if (len < splitSize) {
                    splitSize = len;
                }
                packetToSend.setPosition((int)0);
                packetToSend.writeLongInt((int)splitSize);
                packetToSend.writeByte((byte)this.packetSequence);
                if (len > 0) {
                    System.arraycopy((Object)origPacketBytes, (int)originalPacketPos, (Object)packetToSend.getByteBuffer(), (int)4, (int)splitSize);
                }
                if (this.useCompression) {
                    System.arraycopy((Object)packetToSend.getByteBuffer(), (int)0, (Object)toCompress.getByteBuffer(), (int)toCompressPosition, (int)(4 + splitSize));
                    toCompressPosition += 4 + splitSize;
                    continue;
                }
                this.mysqlOutput.write((byte[])packetToSend.getByteBuffer(), (int)0, (int)(4 + splitSize));
                this.mysqlOutput.flush();
            }
            if (!this.useCompression) return;
            len = toCompressPosition;
            toCompressPosition = 0;
            splitSize = this.maxThreeBytes - 3;
            while (len >= 0) {
                this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
                if (len < splitSize) {
                    splitSize = len;
                }
                Buffer compressedPacketToSend = this.compressPacket((Buffer)toCompress, (int)toCompressPosition, (int)splitSize);
                packetLen = compressedPacketToSend.getPosition();
                this.mysqlOutput.write((byte[])compressedPacketToSend.getByteBuffer(), (int)0, (int)packetLen);
                this.mysqlOutput.flush();
                toCompressPosition += splitSize;
                len -= this.maxThreeBytes - 3;
            }
            return;
        }
        catch (IOException ioEx) {
            throw SQLError.createCommunicationsException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, (long)this.lastPacketReceivedTimeMs, (Exception)ioEx, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    private void reclaimLargeSharedSendPacket() {
        if (this.sharedSendPacket == null) return;
        if (this.sharedSendPacket.getCapacity() <= 1048576) return;
        this.sharedSendPacket = new Buffer((int)1024);
    }

    boolean hadWarnings() {
        return this.hadWarnings;
    }

    void scanForAndThrowDataTruncation() throws SQLException {
        if (this.streamingData != null) return;
        if (!this.versionMeetsMinimum((int)4, (int)1, (int)0)) return;
        if (!this.connection.getJdbcCompliantTruncation()) return;
        if (this.warningCount <= 0) return;
        int warningCountOld = this.warningCount;
        SQLError.convertShowWarningsToSQLWarnings((Connection)this.connection, (int)this.warningCount, (boolean)true);
        this.warningCount = warningCountOld;
    }

    private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams) throws SQLException {
        if (packet == null) {
            packet = new Buffer((int)packLength);
        }
        if (writeClientParams) {
            if (this.use41Extensions) {
                if (this.versionMeetsMinimum((int)4, (int)1, (int)1)) {
                    packet.writeLong((long)this.clientParam);
                    packet.writeLong((long)((long)this.maxThreeBytes));
                    packet.writeByte((byte)8);
                    packet.writeBytesNoNull((byte[])new byte[23]);
                } else {
                    packet.writeLong((long)this.clientParam);
                    packet.writeLong((long)((long)this.maxThreeBytes));
                }
            } else {
                packet.writeInt((int)((int)this.clientParam));
                packet.writeLongInt((int)this.maxThreeBytes);
            }
        }
        packet.writeString((String)user, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
        if (password.length() != 0) {
            packet.writeString((String)FALSE_SCRAMBLE, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
        } else {
            packet.writeString((String)"", (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
        }
        if (this.useConnectWithDb) {
            packet.writeString((String)database, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
        }
        this.send((Buffer)packet, (int)packet.getPosition());
        if (password.length() <= 0) return;
        Buffer b = this.readPacket();
        b.setPosition((int)0);
        byte[] replyAsBytes = b.getByteBuffer();
        if (replyAsBytes.length != 24) return;
        if (replyAsBytes[0] == 0) return;
        if (replyAsBytes[0] != 42) {
            try {
                byte[] buff = Security.passwordHashStage1((String)password);
                byte[] passwordHash = new byte[buff.length];
                System.arraycopy((Object)buff, (int)0, (Object)passwordHash, (int)0, (int)buff.length);
                passwordHash = Security.passwordHashStage2((byte[])passwordHash, (byte[])replyAsBytes);
                byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 4];
                System.arraycopy((Object)replyAsBytes, (int)4, (Object)packetDataAfterSalt, (int)0, (int)(replyAsBytes.length - 4));
                byte[] mysqlScrambleBuff = new byte[20];
                Security.xorString((byte[])packetDataAfterSalt, (byte[])mysqlScrambleBuff, (byte[])passwordHash, (int)20);
                Security.xorString((byte[])mysqlScrambleBuff, (byte[])buff, (byte[])buff, (int)20);
                Buffer packet2 = new Buffer((int)25);
                packet2.writeBytesNoNull((byte[])buff);
                this.packetSequence = (byte)(this.packetSequence + 1);
                this.send((Buffer)packet2, (int)24);
                return;
            }
            catch (NoSuchAlgorithmException nse) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.91") + Messages.getString((String)"MysqlIO.92")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        try {
            byte[] passwordHash = Security.createKeyFromOldPassword((String)password);
            byte[] netReadPos4 = new byte[replyAsBytes.length - 4];
            System.arraycopy((Object)replyAsBytes, (int)4, (Object)netReadPos4, (int)0, (int)(replyAsBytes.length - 4));
            byte[] mysqlScrambleBuff = new byte[20];
            Security.xorString((byte[])netReadPos4, (byte[])mysqlScrambleBuff, (byte[])passwordHash, (int)20);
            String scrambledPassword = Util.scramble((String)StringUtils.toString((byte[])mysqlScrambleBuff), (String)password);
            Buffer packet2 = new Buffer((int)packLength);
            packet2.writeString((String)scrambledPassword, (String)CODE_PAGE_1252, (MySQLConnection)this.connection);
            this.packetSequence = (byte)(this.packetSequence + 1);
            this.send((Buffer)packet2, (int)24);
            return;
        }
        catch (NoSuchAlgorithmException nse) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.91") + Messages.getString((String)"MysqlIO.92")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams, boolean forChangeUser) throws SQLException {
        String enc = this.getEncodingForHandshake();
        if (packet == null) {
            packet = new Buffer((int)packLength);
        }
        if (writeClientParams) {
            if (this.use41Extensions) {
                if (this.versionMeetsMinimum((int)4, (int)1, (int)1)) {
                    packet.writeLong((long)this.clientParam);
                    packet.writeLong((long)((long)this.maxThreeBytes));
                    this.appendCharsetByteForHandshake((Buffer)packet, (String)enc);
                    packet.writeBytesNoNull((byte[])new byte[23]);
                } else {
                    packet.writeLong((long)this.clientParam);
                    packet.writeLong((long)((long)this.maxThreeBytes));
                }
            } else {
                packet.writeInt((int)((int)this.clientParam));
                packet.writeLongInt((int)this.maxThreeBytes);
            }
        }
        if (user != null) {
            packet.writeString((String)user, (String)enc, (MySQLConnection)this.connection);
        }
        if (password.length() != 0) {
            packet.writeByte((byte)20);
            try {
                packet.writeBytesNoNull((byte[])Security.scramble411((String)password, (String)this.seed, (String)this.connection.getPasswordCharacterEncoding()));
            }
            catch (NoSuchAlgorithmException nse) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.91") + Messages.getString((String)"MysqlIO.92")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.91") + Messages.getString((String)"MysqlIO.92")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        } else {
            packet.writeByte((byte)0);
        }
        if (this.useConnectWithDb) {
            packet.writeString((String)database, (String)enc, (MySQLConnection)this.connection);
        } else if (forChangeUser) {
            packet.writeByte((byte)0);
        }
        if ((this.serverCapabilities & 1048576) != 0) {
            this.sendConnectionAttributes((Buffer)packet, (String)enc, (MySQLConnection)this.connection);
        }
        this.send((Buffer)packet, (int)packet.getPosition());
        byte by = this.packetSequence;
        this.packetSequence = (byte)(by + 1);
        byte savePacketSequence = by;
        Buffer reply = this.checkErrorPacket();
        if (reply.isAuthMethodSwitchRequestPacket()) {
            this.packetSequence = savePacketSequence = (byte)(savePacketSequence + 1);
            packet.clear();
            String seed323 = this.seed.substring((int)0, (int)8);
            packet.writeString((String)Util.newCrypt((String)password, (String)seed323, (String)this.connection.getPasswordCharacterEncoding()));
            this.send((Buffer)packet, (int)packet.getPosition());
            this.checkErrorPacket();
        }
        if (this.useConnectWithDb) return;
        this.changeDatabaseTo((String)database);
    }

    private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency) throws SQLException {
        int numFields = fields.length;
        byte[][] unpackedRowData = new byte[numFields][];
        int nullCount = (numFields + 9) / 8;
        int nullMaskPos = binaryData.getPosition();
        binaryData.setPosition((int)(nullMaskPos + nullCount));
        int bit = 4;
        int i = 0;
        while (i < numFields) {
            if ((binaryData.readByte((int)nullMaskPos) & bit) != 0) {
                unpackedRowData[i] = null;
            } else if (resultSetConcurrency != 1008) {
                this.extractNativeEncodedColumn((Buffer)binaryData, (Field[])fields, (int)i, (byte[][])unpackedRowData);
            } else {
                this.unpackNativeEncodedColumn((Buffer)binaryData, (Field[])fields, (int)i, (byte[][])unpackedRowData);
            }
            if (((bit <<= 1) & 255) == 0) {
                bit = 1;
                ++nullMaskPos;
            }
            ++i;
        }
        return new ByteArrayRow((byte[][])unpackedRowData, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
        Field curField = fields[columnIndex];
        switch (curField.getMysqlType()) {
            case 6: {
                return;
            }
            case 1: {
                unpackedRowData[columnIndex] = new byte[]{binaryData.readByte()};
                return;
            }
            case 2: 
            case 13: {
                unpackedRowData[columnIndex] = binaryData.getBytes((int)2);
                return;
            }
            case 3: 
            case 9: {
                unpackedRowData[columnIndex] = binaryData.getBytes((int)4);
                return;
            }
            case 8: {
                unpackedRowData[columnIndex] = binaryData.getBytes((int)8);
                return;
            }
            case 4: {
                unpackedRowData[columnIndex] = binaryData.getBytes((int)4);
                return;
            }
            case 5: {
                unpackedRowData[columnIndex] = binaryData.getBytes((int)8);
                return;
            }
            case 11: {
                int length = (int)binaryData.readFieldLength();
                unpackedRowData[columnIndex] = binaryData.getBytes((int)length);
                return;
            }
            case 10: {
                int length = (int)binaryData.readFieldLength();
                unpackedRowData[columnIndex] = binaryData.getBytes((int)length);
                return;
            }
            case 7: 
            case 12: {
                int length = (int)binaryData.readFieldLength();
                unpackedRowData[columnIndex] = binaryData.getBytes((int)length);
                return;
            }
            case 0: 
            case 15: 
            case 16: 
            case 245: 
            case 246: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                unpackedRowData[columnIndex] = binaryData.readLenByteArray((int)0);
                return;
            }
        }
        throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.97") + curField.getMysqlType() + Messages.getString((String)"MysqlIO.98") + columnIndex + Messages.getString((String)"MysqlIO.99") + fields.length + Messages.getString((String)"MysqlIO.100")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
        Field curField = fields[columnIndex];
        switch (curField.getMysqlType()) {
            case 6: {
                return;
            }
            case 1: {
                byte tinyVal = binaryData.readByte();
                if (!curField.isUnsigned()) {
                    unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((int)tinyVal));
                    return;
                }
                short unsignedTinyVal = (short)(tinyVal & 255);
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((int)unsignedTinyVal));
                return;
            }
            case 2: 
            case 13: {
                short shortVal = (short)binaryData.readInt();
                if (!curField.isUnsigned()) {
                    unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((int)shortVal));
                    return;
                }
                int unsignedShortVal = shortVal & 65535;
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((int)unsignedShortVal));
                return;
            }
            case 3: 
            case 9: {
                int intVal = (int)binaryData.readLong();
                if (!curField.isUnsigned()) {
                    unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((int)intVal));
                    return;
                }
                long longVal = (long)intVal & 0xFFFFFFFFL;
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((long)longVal));
                return;
            }
            case 8: {
                long longVal = binaryData.readLongLong();
                if (!curField.isUnsigned()) {
                    unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((long)longVal));
                    return;
                }
                BigInteger asBigInteger = ResultSetImpl.convertLongToUlong((long)longVal);
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)asBigInteger.toString());
                return;
            }
            case 4: {
                float floatVal = Float.intBitsToFloat((int)binaryData.readIntAsLong());
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((float)floatVal));
                return;
            }
            case 5: {
                double doubleVal = Double.longBitsToDouble((long)binaryData.readLongLong());
                unpackedRowData[columnIndex] = StringUtils.getBytes((String)String.valueOf((double)doubleVal));
                return;
            }
            case 11: {
                int length = (int)binaryData.readFieldLength();
                int hour = 0;
                byte minute = 0;
                byte seconds = 0;
                if (length != 0) {
                    binaryData.readByte();
                    binaryData.readLong();
                    hour = (int)binaryData.readByte();
                    minute = binaryData.readByte();
                    seconds = binaryData.readByte();
                    if (length > 8) {
                        binaryData.readLong();
                    }
                }
                byte[] timeAsBytes = new byte[]{(byte)Character.forDigit((int)(hour / 10), (int)10), (byte)Character.forDigit((int)(hour % 10), (int)10), 58, (byte)Character.forDigit((int)(minute / 10), (int)10), (byte)Character.forDigit((int)(minute % 10), (int)10), 58, (byte)Character.forDigit((int)(seconds / 10), (int)10), (byte)Character.forDigit((int)(seconds % 10), (int)10)};
                unpackedRowData[columnIndex] = timeAsBytes;
                return;
            }
            case 10: {
                int length = (int)binaryData.readFieldLength();
                int year = 0;
                byte month = 0;
                byte day = 0;
                boolean hour = false;
                boolean minute = false;
                boolean seconds = false;
                if (length != 0) {
                    year = binaryData.readInt();
                    month = binaryData.readByte();
                    day = binaryData.readByte();
                }
                if (year == 0 && month == 0 && day == 0) {
                    if ("convertToNull".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                        unpackedRowData[columnIndex] = null;
                        return;
                    }
                    if ("exception".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                        throw SQLError.createSQLException((String)"Value '0000-00-00' can not be represented as java.sql.Date", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    year = 1;
                    month = 1;
                    day = 1;
                }
                byte[] dateAsBytes = new byte[10];
                dateAsBytes[0] = (byte)Character.forDigit((int)(year / 1000), (int)10);
                int after1000 = year % 1000;
                dateAsBytes[1] = (byte)Character.forDigit((int)(after1000 / 100), (int)10);
                int after100 = after1000 % 100;
                dateAsBytes[2] = (byte)Character.forDigit((int)(after100 / 10), (int)10);
                dateAsBytes[3] = (byte)Character.forDigit((int)(after100 % 10), (int)10);
                dateAsBytes[4] = 45;
                dateAsBytes[5] = (byte)Character.forDigit((int)(month / 10), (int)10);
                dateAsBytes[6] = (byte)Character.forDigit((int)(month % 10), (int)10);
                dateAsBytes[7] = 45;
                dateAsBytes[8] = (byte)Character.forDigit((int)(day / 10), (int)10);
                dateAsBytes[9] = (byte)Character.forDigit((int)(day % 10), (int)10);
                unpackedRowData[columnIndex] = dateAsBytes;
                return;
            }
            case 7: 
            case 12: {
                int length = (int)binaryData.readFieldLength();
                int year = 0;
                byte month = 0;
                byte day = 0;
                byte hour = 0;
                byte minute = 0;
                byte seconds = 0;
                int nanos = 0;
                if (length != 0) {
                    year = binaryData.readInt();
                    month = binaryData.readByte();
                    day = binaryData.readByte();
                    if (length > 4) {
                        hour = binaryData.readByte();
                        minute = binaryData.readByte();
                        seconds = binaryData.readByte();
                    }
                }
                if (year == 0 && month == 0 && day == 0) {
                    if ("convertToNull".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                        unpackedRowData[columnIndex] = null;
                        return;
                    }
                    if ("exception".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                        throw SQLError.createSQLException((String)"Value '0000-00-00' can not be represented as java.sql.Timestamp", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                    year = 1;
                    month = 1;
                    day = 1;
                }
                int stringLength = 19;
                byte[] nanosAsBytes = StringUtils.getBytes((String)Integer.toString((int)nanos));
                byte[] datetimeAsBytes = new byte[stringLength += 1 + nanosAsBytes.length];
                datetimeAsBytes[0] = (byte)Character.forDigit((int)(year / 1000), (int)10);
                int after1000 = year % 1000;
                datetimeAsBytes[1] = (byte)Character.forDigit((int)(after1000 / 100), (int)10);
                int after100 = after1000 % 100;
                datetimeAsBytes[2] = (byte)Character.forDigit((int)(after100 / 10), (int)10);
                datetimeAsBytes[3] = (byte)Character.forDigit((int)(after100 % 10), (int)10);
                datetimeAsBytes[4] = 45;
                datetimeAsBytes[5] = (byte)Character.forDigit((int)(month / 10), (int)10);
                datetimeAsBytes[6] = (byte)Character.forDigit((int)(month % 10), (int)10);
                datetimeAsBytes[7] = 45;
                datetimeAsBytes[8] = (byte)Character.forDigit((int)(day / 10), (int)10);
                datetimeAsBytes[9] = (byte)Character.forDigit((int)(day % 10), (int)10);
                datetimeAsBytes[10] = 32;
                datetimeAsBytes[11] = (byte)Character.forDigit((int)(hour / 10), (int)10);
                datetimeAsBytes[12] = (byte)Character.forDigit((int)(hour % 10), (int)10);
                datetimeAsBytes[13] = 58;
                datetimeAsBytes[14] = (byte)Character.forDigit((int)(minute / 10), (int)10);
                datetimeAsBytes[15] = (byte)Character.forDigit((int)(minute % 10), (int)10);
                datetimeAsBytes[16] = 58;
                datetimeAsBytes[17] = (byte)Character.forDigit((int)(seconds / 10), (int)10);
                datetimeAsBytes[18] = (byte)Character.forDigit((int)(seconds % 10), (int)10);
                datetimeAsBytes[19] = 46;
                int nanosOffset = 20;
                System.arraycopy((Object)nanosAsBytes, (int)0, (Object)datetimeAsBytes, (int)20, (int)nanosAsBytes.length);
                unpackedRowData[columnIndex] = datetimeAsBytes;
                return;
            }
            case 0: 
            case 15: 
            case 16: 
            case 245: 
            case 246: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: {
                unpackedRowData[columnIndex] = binaryData.readLenByteArray((int)0);
                return;
            }
        }
        throw SQLError.createSQLException((String)(Messages.getString((String)"MysqlIO.97") + curField.getMysqlType() + Messages.getString((String)"MysqlIO.98") + columnIndex + Messages.getString((String)"MysqlIO.99") + fields.length + Messages.getString((String)"MysqlIO.100")), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private void negotiateSSLConnection(String user, String password, String database, int packLength) throws SQLException {
        if (!ExportControlled.enabled()) {
            throw new ConnectionFeatureNotAvailableException((MySQLConnection)this.connection, (long)this.lastPacketSentTimeMs, null);
        }
        if ((this.serverCapabilities & 32768) != 0) {
            this.clientParam |= 32768L;
        }
        this.clientParam |= 2048L;
        Buffer packet = new Buffer((int)packLength);
        if (this.use41Extensions) {
            packet.writeLong((long)this.clientParam);
            packet.writeLong((long)((long)this.maxThreeBytes));
            this.appendCharsetByteForHandshake((Buffer)packet, (String)this.getEncodingForHandshake());
            packet.writeBytesNoNull((byte[])new byte[23]);
        } else {
            packet.writeInt((int)((int)this.clientParam));
        }
        this.send((Buffer)packet, (int)packet.getPosition());
        ExportControlled.transformSocketToSSLSocket((MysqlIO)this);
    }

    public boolean isSSLEstablished() {
        if (!ExportControlled.enabled()) return false;
        if (!ExportControlled.isSSLEstablished((Socket)this.mysqlConnection)) return false;
        return true;
    }

    protected int getServerStatus() {
        return this.serverStatus;
    }

    protected List<ResultSetRow> fetchRowsViaCursor(List<ResultSetRow> fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit) throws SQLException {
        if (fetchedRows == null) {
            fetchedRows = new ArrayList<ResultSetRow>((int)fetchSize);
        } else {
            fetchedRows.clear();
        }
        this.sharedSendPacket.clear();
        this.sharedSendPacket.writeByte((byte)28);
        this.sharedSendPacket.writeLong((long)statementId);
        this.sharedSendPacket.writeLong((long)((long)fetchSize));
        this.sendCommand((int)28, null, (Buffer)this.sharedSendPacket, (boolean)true, null, (int)0);
        ResultSetRow row = null;
        while ((row = this.nextRow((Field[])columnTypes, (int)columnTypes.length, (boolean)true, (int)1007, (boolean)false, (boolean)useBufferRowExplicit, (boolean)false, null)) != null) {
            fetchedRows.add((ResultSetRow)row);
        }
        return fetchedRows;
    }

    protected long getThreadId() {
        return this.threadId;
    }

    protected boolean useNanosForElapsedTime() {
        return this.useNanosForElapsedTime;
    }

    protected long getSlowQueryThreshold() {
        return this.slowQueryThreshold;
    }

    public String getQueryTimingUnits() {
        return this.queryTimingUnits;
    }

    protected int getCommandCount() {
        return this.commandCount;
    }

    private void checkTransactionState(int oldStatus) throws SQLException {
        boolean previouslyInTrans = (oldStatus & 1) != 0;
        boolean currentlyInTrans = this.inTransactionOnServer();
        if (previouslyInTrans && !currentlyInTrans) {
            this.connection.transactionCompleted();
            return;
        }
        if (previouslyInTrans) return;
        if (!currentlyInTrans) return;
        this.connection.transactionBegun();
    }

    private void preserveOldTransactionState() {
        this.serverStatus |= this.oldServerStatus & 1;
    }

    protected void setStatementInterceptors(List<StatementInterceptorV2> statementInterceptors) {
        this.statementInterceptors = statementInterceptors.isEmpty() ? null : statementInterceptors;
    }

    protected ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    protected void setSocketTimeout(int milliseconds) throws SQLException {
        try {
            if (this.mysqlConnection == null) return;
            this.mysqlConnection.setSoTimeout((int)milliseconds);
            return;
        }
        catch (SocketException e) {
            SQLException sqlEx = SQLError.createSQLException((String)"Invalid socket timeout value or state", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    protected void releaseResources() {
        if (this.deflater == null) return;
        this.deflater.end();
        this.deflater = null;
    }

    String getEncodingForHandshake() {
        String enc = this.connection.getEncoding();
        if (enc != null) return enc;
        return "UTF-8";
    }

    private void appendCharsetByteForHandshake(Buffer packet, String enc) throws SQLException {
        int charsetIndex = 0;
        if (enc != null) {
            charsetIndex = CharsetMapping.getCollationIndexForJavaEncoding((String)enc, (java.sql.Connection)this.connection);
        }
        if (charsetIndex == 0) {
            charsetIndex = 33;
        }
        if (charsetIndex > 255) {
            throw SQLError.createSQLException((String)("Invalid character set index for encoding: " + enc), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        packet.writeByte((byte)((byte)charsetIndex));
    }

    public boolean isEOFDeprecated() {
        if ((this.clientParam & 0x1000000L) == 0L) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    static {
        outWriter = null;
        try {
            outWriter = new OutputStreamWriter((OutputStream)new ByteArrayOutputStream());
            MysqlIO.jvmPlatformCharset = outWriter.getEncoding();
            var2_1 = null;
            try {
                if (outWriter == null) return;
                outWriter.close();
                return;
            }
            catch (IOException ioEx) {
                return;
            }
        }
        catch (Throwable var1_5) {
            var2_2 = null;
            ** try [egrp 1[TRYBLOCK] [2 : 78->89)] { 
lbl20: // 1 sources:
            if (outWriter == null) throw var1_5;
            outWriter.close();
            throw var1_5;
lbl23: // 1 sources:
            catch (IOException ioEx) {
                // empty catch block
            }
            throw var1_5;
        }
    }
}

