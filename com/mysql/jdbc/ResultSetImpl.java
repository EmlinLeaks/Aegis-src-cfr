/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Blob;
import com.mysql.jdbc.BlobFromLocator;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.Clob;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlDefs;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.RowDataStatic;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.TimeUtil;
import com.mysql.jdbc.UpdatableResultSet;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.LogUtils;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ResultSetImpl
implements ResultSetInternalMethods {
    private static final Constructor<?> JDBC_4_RS_4_ARG_CTOR;
    private static final Constructor<?> JDBC_4_RS_5_ARG_CTOR;
    private static final Constructor<?> JDBC_4_UPD_RS_5_ARG_CTOR;
    protected static final double MIN_DIFF_PREC;
    protected static final double MAX_DIFF_PREC;
    static int resultCounter;
    protected String catalog = null;
    protected Map<String, Integer> columnLabelToIndex = null;
    protected Map<String, Integer> columnToIndexCache = null;
    protected boolean[] columnUsed = null;
    protected volatile MySQLConnection connection;
    protected int currentRow = -1;
    protected boolean doingUpdates = false;
    Calendar fastDefaultCal = null;
    Calendar fastClientCal = null;
    protected int fetchDirection = 1000;
    protected int fetchSize = 0;
    protected Field[] fields;
    protected char firstCharOfQuery;
    protected Map<String, Integer> fullColumnNameToIndex = null;
    protected Map<String, Integer> columnNameToIndex = null;
    protected boolean hasBuiltIndexMapping = false;
    protected boolean isBinaryEncoded = false;
    protected boolean isClosed = false;
    protected ResultSetInternalMethods nextResultSet = null;
    protected boolean onInsertRow = false;
    protected StatementImpl owningStatement;
    protected String pointOfOrigin;
    protected boolean reallyResult = false;
    protected int resultId;
    protected int resultSetConcurrency = 0;
    protected int resultSetType = 0;
    protected RowData rowData;
    protected String serverInfo = null;
    PreparedStatement statementUsedForFetchingRows;
    protected ResultSetRow thisRow = null;
    protected long updateCount;
    protected long updateId = -1L;
    private boolean useStrictFloatingPoint = false;
    protected boolean useUsageAdvisor = false;
    protected SQLWarning warningChain = null;
    protected boolean wasNullFlag = false;
    protected java.sql.Statement wrapperStatement;
    protected boolean retainOwningStatement;
    protected Calendar gmtCalendar = null;
    protected boolean useFastDateParsing = false;
    private boolean padCharsWithSpace = false;
    private boolean jdbcCompliantTruncationForReads;
    private boolean useFastIntParsing = true;
    private boolean useColumnNamesInFindColumn;
    private ExceptionInterceptor exceptionInterceptor;
    static final char[] EMPTY_SPACE;
    private boolean onValidRow = false;
    private String invalidRowReason = null;
    protected boolean useLegacyDatetimeCode;
    private TimeZone serverTimeZoneTz;

    protected static BigInteger convertLongToUlong(long longVal) {
        byte[] asBytes = new byte[8];
        asBytes[7] = (byte)((int)(longVal & 255L));
        asBytes[6] = (byte)((int)(longVal >>> 8));
        asBytes[5] = (byte)((int)(longVal >>> 16));
        asBytes[4] = (byte)((int)(longVal >>> 24));
        asBytes[3] = (byte)((int)(longVal >>> 32));
        asBytes[2] = (byte)((int)(longVal >>> 40));
        asBytes[1] = (byte)((int)(longVal >>> 48));
        asBytes[0] = (byte)((int)(longVal >>> 56));
        return new BigInteger((int)1, (byte[])asBytes);
    }

    protected static ResultSetImpl getInstance(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        if (Util.isJdbc4()) return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, (Object[])new Object[]{Long.valueOf((long)updateCount), Long.valueOf((long)updateID), conn, creatorStmt}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return new ResultSetImpl((long)updateCount, (long)updateID, (MySQLConnection)conn, (StatementImpl)creatorStmt);
    }

    protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt, boolean isUpdatable) throws SQLException {
        if (!Util.isJdbc4()) {
            if (isUpdatable) return new UpdatableResultSet((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
            return new ResultSetImpl((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
        }
        if (isUpdatable) return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_5_ARG_CTOR, (Object[])new Object[]{catalog, fields, tuples, conn, creatorStmt}, (ExceptionInterceptor)conn.getExceptionInterceptor());
        return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_5_ARG_CTOR, (Object[])new Object[]{catalog, fields, tuples, conn, creatorStmt}, (ExceptionInterceptor)conn.getExceptionInterceptor());
    }

    public ResultSetImpl(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt) {
        this.updateCount = updateCount;
        this.updateId = updateID;
        this.reallyResult = false;
        this.fields = new Field[0];
        this.connection = conn;
        this.owningStatement = creatorStmt;
        this.retainOwningStatement = false;
        if (this.connection == null) return;
        this.exceptionInterceptor = this.connection.getExceptionInterceptor();
        this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
        this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
        this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
    }

    public ResultSetImpl(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        this.connection = conn;
        this.retainOwningStatement = false;
        if (this.connection != null) {
            this.exceptionInterceptor = this.connection.getExceptionInterceptor();
            this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
            this.useFastDateParsing = this.connection.getUseFastDateParsing();
            this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
            this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
            this.useFastIntParsing = this.connection.getUseFastIntParsing();
            this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
            this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
        }
        this.owningStatement = creatorStmt;
        this.catalog = catalog;
        this.fields = fields;
        this.rowData = tuples;
        this.updateCount = (long)this.rowData.size();
        this.reallyResult = true;
        if (this.rowData.size() > 0) {
            if (this.updateCount == 1L && this.thisRow == null) {
                this.rowData.close();
                this.updateCount = -1L;
            }
        } else {
            this.thisRow = null;
        }
        this.rowData.setOwner((ResultSetImpl)this);
        if (this.fields != null) {
            this.initializeWithMetadata();
        }
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
        this.setRowPositionValidity();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initializeWithMetadata() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.rowData.setMetadata((Field[])this.fields);
        this.columnToIndexCache = new HashMap<String, Integer>();
        if (this.useUsageAdvisor) {
            this.columnUsed = new boolean[this.fields.length];
            this.pointOfOrigin = LogUtils.findCallingClassAndMethod((Throwable)new Throwable());
            this.resultId = resultCounter++;
        }
        if (this.connection.getGatherPerformanceMetrics()) {
            this.connection.incrementNumberOfResultSetsCreated();
            HashSet<String> tableNamesSet = new HashSet<String>();
            for (int i = 0; i < this.fields.length; ++i) {
                Field f = this.fields[i];
                String tableName = f.getOriginalTableName();
                if (tableName == null) {
                    tableName = f.getTableName();
                }
                if (tableName == null) continue;
                if (this.connection.lowerCaseTableNames()) {
                    tableName = tableName.toLowerCase();
                }
                tableNamesSet.add(tableName);
            }
            this.connection.reportNumberOfTablesAccessed((int)tableNamesSet.size());
        }
        // MONITOREXIT : object
        return;
    }

    private synchronized Calendar getFastDefaultCalendar() {
        if (this.fastDefaultCal != null) return this.fastDefaultCal;
        this.fastDefaultCal = new GregorianCalendar((Locale)Locale.US);
        this.fastDefaultCal.setTimeZone((TimeZone)this.getDefaultTimeZone());
        return this.fastDefaultCal;
    }

    private synchronized Calendar getFastClientCalendar() {
        if (this.fastClientCal != null) return this.fastClientCal;
        this.fastClientCal = new GregorianCalendar((Locale)Locale.US);
        return this.fastClientCal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean absolute(int row) throws SQLException {
        boolean b;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.rowData.size() == 0) {
            b = false;
        } else {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            if (this.thisRow != null) {
                this.thisRow.closeOpenStreams();
            }
            if (row == 0) {
                this.beforeFirst();
                b = false;
            } else if (row == 1) {
                b = this.first();
            } else if (row == -1) {
                b = this.last();
            } else if (row > this.rowData.size()) {
                this.afterLast();
                b = false;
            } else if (row < 0) {
                int newRowPosition = this.rowData.size() + row + 1;
                if (newRowPosition <= 0) {
                    this.beforeFirst();
                    b = false;
                } else {
                    b = this.absolute((int)newRowPosition);
                }
            } else {
                this.rowData.setCurrentRow((int)(--row));
                this.thisRow = this.rowData.getAt((int)row);
                b = true;
            }
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void afterLast() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.onInsertRow = false;
        }
        if (this.doingUpdates) {
            this.doingUpdates = false;
        }
        if (this.thisRow != null) {
            this.thisRow.closeOpenStreams();
        }
        if (this.rowData.size() != 0) {
            this.rowData.afterLast();
            this.thisRow = null;
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void beforeFirst() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.onInsertRow = false;
        }
        if (this.doingUpdates) {
            this.doingUpdates = false;
        }
        if (this.rowData.size() == 0) {
            // MONITOREXIT : object
            return;
        }
        if (this.thisRow != null) {
            this.thisRow.closeOpenStreams();
        }
        this.rowData.beforeFirst();
        this.thisRow = null;
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return;
    }

    @Override
    public void buildIndexMapping() throws SQLException {
        int numFields = this.fields.length;
        this.columnLabelToIndex = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        this.fullColumnNameToIndex = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        this.columnNameToIndex = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        int i = numFields - 1;
        do {
            if (i < 0) {
                this.hasBuiltIndexMapping = true;
                return;
            }
            Integer index = Integer.valueOf((int)i);
            String columnName = this.fields[i].getOriginalName();
            String columnLabel = this.fields[i].getName();
            String fullColumnName = this.fields[i].getFullName();
            if (columnLabel != null) {
                this.columnLabelToIndex.put((String)columnLabel, (Integer)index);
            }
            if (fullColumnName != null) {
                this.fullColumnNameToIndex.put((String)fullColumnName, (Integer)index);
            }
            if (columnName != null) {
                this.columnNameToIndex.put((String)columnName, (Integer)index);
            }
            --i;
        } while (true);
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new NotUpdatable();
    }

    protected final MySQLConnection checkClosed() throws SQLException {
        MySQLConnection c = this.connection;
        if (c != null) return c;
        throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void checkColumnBounds(int columnIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (columnIndex < 1) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Column_Index_out_of_range_low", (Object[])new Object[]{Integer.valueOf((int)columnIndex), Integer.valueOf((int)this.fields.length)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (columnIndex > this.fields.length) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Column_Index_out_of_range_high", (Object[])new Object[]{Integer.valueOf((int)columnIndex), Integer.valueOf((int)this.fields.length)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.useUsageAdvisor) {
            this.columnUsed[columnIndex - 1] = true;
        }
        // MONITOREXIT : object
        return;
    }

    protected void checkRowPos() throws SQLException {
        this.checkClosed();
        if (this.onValidRow) return;
        throw SQLError.createSQLException((String)this.invalidRowReason, (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private void setRowPositionValidity() throws SQLException {
        if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
            this.invalidRowReason = Messages.getString((String)"ResultSet.Illegal_operation_on_empty_result_set");
            this.onValidRow = false;
            return;
        }
        if (this.rowData.isBeforeFirst()) {
            this.invalidRowReason = Messages.getString((String)"ResultSet.Before_start_of_result_set_146");
            this.onValidRow = false;
            return;
        }
        if (this.rowData.isAfterLast()) {
            this.invalidRowReason = Messages.getString((String)"ResultSet.After_end_of_result_set_148");
            this.onValidRow = false;
            return;
        }
        this.onValidRow = true;
        this.invalidRowReason = null;
    }

    @Override
    public synchronized void clearNextResult() {
        this.nextResultSet = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearWarnings() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        this.warningChain = null;
        // MONITOREXIT : object
        return;
    }

    @Override
    public void close() throws SQLException {
        this.realClose((boolean)true);
    }

    private int convertToZeroWithEmptyCheck() throws SQLException {
        if (!this.connection.getEmptyStringsConvertToZero()) throw SQLError.createSQLException((String)"Can't convert empty string ('') to numeric", (String)"22018", (ExceptionInterceptor)this.getExceptionInterceptor());
        return 0;
    }

    private String convertToZeroLiteralStringWithEmptyCheck() throws SQLException {
        if (!this.connection.getEmptyStringsConvertToZero()) throw SQLError.createSQLException((String)"Can't convert empty string ('') to numeric", (String)"22018", (ExceptionInterceptor)this.getExceptionInterceptor());
        return "0";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSetInternalMethods copy() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        ResultSetImpl rs = ResultSetImpl.getInstance((String)this.catalog, (Field[])this.fields, (RowData)this.rowData, (MySQLConnection)this.connection, (StatementImpl)this.owningStatement, (boolean)false);
        if (this.isBinaryEncoded) {
            rs.setBinaryEncoded();
        }
        // MONITOREXIT : object
        return rs;
    }

    @Override
    public void redefineFieldsForDBMD(Field[] f) {
        this.fields = f;
        int i = 0;
        while (i < this.fields.length) {
            this.fields[i].setUseOldNameMetadata((boolean)true);
            this.fields[i].setConnection((MySQLConnection)this.connection);
            ++i;
        }
    }

    @Override
    public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
        cachedMetaData.fields = this.fields;
        cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
        cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
        cachedMetaData.metadata = this.getMetaData();
    }

    @Override
    public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
        this.fields = cachedMetaData.fields;
        this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
        this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
        this.hasBuiltIndexMapping = true;
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new NotUpdatable();
    }

    private String extractStringFromNativeColumn(int columnIndex, int mysqlType) throws SQLException {
        int columnIndexMinusOne = columnIndex - 1;
        this.wasNullFlag = false;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        String encoding = this.fields[columnIndexMinusOne].getCollationIndex() == 63 ? this.connection.getEncoding() : this.fields[columnIndexMinusOne].getEncoding();
        return this.thisRow.getString((int)(columnIndex - 1), (String)encoding, (MySQLConnection)this.connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Date fastDateCreate(Calendar cal, int year, int month, int day) throws SQLException {
        Calendar calendar;
        boolean useGmtMillis;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        Calendar targetCalendar = cal;
        if (cal == null) {
            targetCalendar = this.connection.getNoTimezoneConversionForDateType() ? this.getFastClientCalendar() : this.getFastDefaultCalendar();
        }
        if (!this.useLegacyDatetimeCode) {
            // MONITOREXIT : object
            return TimeUtil.fastDateCreate((int)year, (int)month, (int)day, (Calendar)targetCalendar);
        }
        boolean bl = useGmtMillis = cal == null && !this.connection.getNoTimezoneConversionForDateType() && this.connection.getUseGmtMillisForDatetimes();
        if (useGmtMillis) {
            calendar = this.getGmtCalendar();
            return TimeUtil.fastDateCreate((boolean)useGmtMillis, (Calendar)calendar, (Calendar)targetCalendar, (int)year, (int)month, (int)day);
        }
        calendar = targetCalendar;
        // MONITOREXIT : object
        return TimeUtil.fastDateCreate((boolean)useGmtMillis, (Calendar)calendar, (Calendar)targetCalendar, (int)year, (int)month, (int)day);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Time fastTimeCreate(Calendar cal, int hour, int minute, int second) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.useLegacyDatetimeCode) {
            // MONITOREXIT : object
            return TimeUtil.fastTimeCreate((int)hour, (int)minute, (int)second, (Calendar)cal, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (cal == null) {
            cal = this.getFastDefaultCalendar();
        }
        // MONITOREXIT : object
        return TimeUtil.fastTimeCreate((Calendar)cal, (int)hour, (int)minute, (int)second, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart, boolean useGmtMillis) throws SQLException {
        Calendar calendar;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.useLegacyDatetimeCode) {
            // MONITOREXIT : object
            return TimeUtil.fastTimestampCreate((TimeZone)cal.getTimeZone(), (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)secondsPart);
        }
        if (cal == null) {
            cal = this.getFastDefaultCalendar();
        }
        if (useGmtMillis) {
            calendar = this.getGmtCalendar();
            return TimeUtil.fastTimestampCreate((boolean)useGmtMillis, (Calendar)calendar, (Calendar)cal, (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)secondsPart);
        }
        calendar = null;
        // MONITOREXIT : object
        return TimeUtil.fastTimestampCreate((boolean)useGmtMillis, (Calendar)calendar, (Calendar)cal, (int)year, (int)month, (int)day, (int)hour, (int)minute, (int)seconds, (int)secondsPart);
    }

    @Override
    public int findColumn(String columnName) throws SQLException {
        Integer index;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.hasBuiltIndexMapping) {
            this.buildIndexMapping();
        }
        if ((index = this.columnToIndexCache.get((Object)columnName)) != null) {
            // MONITOREXIT : object
            return index.intValue() + 1;
        }
        index = this.columnLabelToIndex.get((Object)columnName);
        if (index == null && this.useColumnNamesInFindColumn) {
            index = this.columnNameToIndex.get((Object)columnName);
        }
        if (index == null) {
            index = this.fullColumnNameToIndex.get((Object)columnName);
        }
        if (index != null) {
            this.columnToIndexCache.put((String)columnName, (Integer)index);
            // MONITOREXIT : object
            return index.intValue() + 1;
        }
        int i = 0;
        while (i < this.fields.length) {
            if (this.fields[i].getName().equalsIgnoreCase((String)columnName)) {
                // MONITOREXIT : object
                return i + 1;
            }
            if (this.fields[i].getFullName().equalsIgnoreCase((String)columnName)) {
                // MONITOREXIT : object
                return i + 1;
            }
            ++i;
        }
        throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Column____112") + columnName + Messages.getString((String)"ResultSet.___not_found._113")), (String)"S0022", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean first() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        boolean b = true;
        if (this.rowData.isEmpty()) {
            b = false;
        } else {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            this.rowData.beforeFirst();
            this.thisRow = this.rowData.next();
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return b;
    }

    @Override
    public Array getArray(int i) throws SQLException {
        this.checkColumnBounds((int)i);
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public Array getArray(String colName) throws SQLException {
        return this.getArray((int)this.findColumn((String)colName));
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        this.checkRowPos();
        if (this.isBinaryEncoded) return this.getNativeBinaryStream((int)columnIndex);
        return this.getBinaryStream((int)columnIndex);
    }

    @Override
    public InputStream getAsciiStream(String columnName) throws SQLException {
        return this.getAsciiStream((int)this.findColumn((String)columnName));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeBigDecimal((int)columnIndex);
        String stringVal = this.getString((int)columnIndex);
        if (stringVal == null) return null;
        if (stringVal.length() == 0) {
            return new BigDecimal((String)this.convertToZeroLiteralStringWithEmptyCheck());
        }
        try {
            return new BigDecimal((String)stringVal);
        }
        catch (NumberFormatException ex) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal val;
        if (this.isBinaryEncoded) return this.getNativeBigDecimal((int)columnIndex, (int)scale);
        String stringVal = this.getString((int)columnIndex);
        if (stringVal == null) return null;
        if (stringVal.length() == 0) {
            BigDecimal val2 = new BigDecimal((String)this.convertToZeroLiteralStringWithEmptyCheck());
            try {
                return val2.setScale((int)scale);
            }
            catch (ArithmeticException ex) {
                try {
                    return val2.setScale((int)scale, (int)4);
                }
                catch (ArithmeticException arEx) {
                    throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
        }
        try {
            val = new BigDecimal((String)stringVal);
        }
        catch (NumberFormatException ex) {
            if (this.fields[columnIndex - 1].getMysqlType() != 16) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{Integer.valueOf((int)columnIndex), stringVal}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)columnIndex);
            val = new BigDecimal((long)valueAsLong);
        }
        try {
            return val.setScale((int)scale);
        }
        catch (ArithmeticException ex) {
            try {
                return val.setScale((int)scale, (int)4);
            }
            catch (ArithmeticException arithEx) {
                throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{Integer.valueOf((int)columnIndex), stringVal}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return this.getBigDecimal((int)this.findColumn((String)columnName));
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return this.getBigDecimal((int)this.findColumn((String)columnName), (int)scale);
    }

    private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale) throws SQLException {
        if (stringVal == null) return null;
        if (stringVal.length() == 0) {
            BigDecimal bdVal = new BigDecimal((String)this.convertToZeroLiteralStringWithEmptyCheck());
            try {
                return bdVal.setScale((int)scale);
            }
            catch (ArithmeticException ex) {
                try {
                    return bdVal.setScale((int)scale, (int)4);
                }
                catch (ArithmeticException arEx) {
                    throw new SQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009");
                }
            }
        }
        try {
            try {
                return new BigDecimal((String)stringVal).setScale((int)scale);
            }
            catch (ArithmeticException ex) {
                try {
                    return new BigDecimal((String)stringVal).setScale((int)scale, (int)4);
                }
                catch (ArithmeticException arEx) {
                    throw new SQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009");
                }
            }
        }
        catch (NumberFormatException ex) {
            int n;
            if (this.fields[columnIndex - 1].getMysqlType() == 16) {
                long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)columnIndex);
                try {
                    return new BigDecimal((long)valueAsLong).setScale((int)scale);
                }
                catch (ArithmeticException arEx1) {
                    try {
                        return new BigDecimal((long)valueAsLong).setScale((int)scale, (int)4);
                    }
                    catch (ArithmeticException arEx2) {
                        throw new SQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009");
                    }
                }
            }
            if (this.fields[columnIndex - 1].getMysqlType() != 1 || !this.connection.getTinyInt1isBit() || this.fields[columnIndex - 1].getLength() != 1L) throw new SQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009");
            if (stringVal.equalsIgnoreCase((String)"true")) {
                n = 1;
                return new BigDecimal((int)n).setScale((int)scale);
            }
            n = 0;
            return new BigDecimal((int)n).setScale((int)scale);
        }
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        this.checkRowPos();
        if (this.isBinaryEncoded) return this.getNativeBinaryStream((int)columnIndex);
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getBinaryInputStream((int)columnIndexMinusOne);
    }

    @Override
    public InputStream getBinaryStream(String columnName) throws SQLException {
        return this.getBinaryStream((int)this.findColumn((String)columnName));
    }

    @Override
    public java.sql.Blob getBlob(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeBlob((int)columnIndex);
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        this.wasNullFlag = this.thisRow.isNull((int)columnIndexMinusOne);
        if (this.wasNullFlag) {
            return null;
        }
        if (this.connection.getEmulateLocators()) return new BlobFromLocator((ResultSetImpl)this, (int)columnIndex, (ExceptionInterceptor)this.getExceptionInterceptor());
        return new Blob((byte[])this.thisRow.getColumnValue((int)columnIndexMinusOne), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public java.sql.Blob getBlob(String colName) throws SQLException {
        return this.getBlob((int)this.findColumn((String)colName));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        Field field = this.fields[columnIndexMinusOne];
        if (field.getMysqlType() == 16) {
            return this.byteArrayToBoolean((int)columnIndexMinusOne);
        }
        this.wasNullFlag = false;
        int sqlType = field.getSQLType();
        switch (sqlType) {
            case 16: {
                if (field.getMysqlType() == -1) {
                    String stringVal = this.getString((int)columnIndex);
                    return this.getBooleanFromString((String)stringVal);
                }
                long boolVal = this.getLong((int)columnIndex, (boolean)false);
                if (boolVal == -1L) return true;
                if (boolVal > 0L) return true;
                return false;
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
                long boolVal = this.getLong((int)columnIndex, (boolean)false);
                if (boolVal == -1L) return true;
                if (boolVal > 0L) return true;
                return false;
            }
        }
        if (this.connection.getPedantic()) {
            switch (sqlType) {
                case -4: 
                case -3: 
                case -2: 
                case 70: 
                case 91: 
                case 92: 
                case 93: 
                case 2000: 
                case 2002: 
                case 2003: 
                case 2004: 
                case 2005: 
                case 2006: {
                    throw SQLError.createSQLException((String)"Required type conversion not allowed", (String)"22018", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
        }
        if (sqlType == -2) return this.byteArrayToBoolean((int)columnIndexMinusOne);
        if (sqlType == -3) return this.byteArrayToBoolean((int)columnIndexMinusOne);
        if (sqlType == -4) return this.byteArrayToBoolean((int)columnIndexMinusOne);
        if (sqlType == 2004) {
            return this.byteArrayToBoolean((int)columnIndexMinusOne);
        }
        if (this.useUsageAdvisor) {
            this.issueConversionViaParsingWarning((String)"getBoolean()", (int)columnIndex, (Object)this.thisRow.getColumnValue((int)columnIndexMinusOne), (Field)this.fields[columnIndex], (int[])new int[]{16, 5, 1, 2, 3, 8, 4});
        }
        String stringVal = this.getString((int)columnIndex);
        return this.getBooleanFromString((String)stringVal);
    }

    private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException {
        byte[] value = this.thisRow.getColumnValue((int)columnIndexMinusOne);
        if (value == null) {
            this.wasNullFlag = true;
            return false;
        }
        this.wasNullFlag = false;
        if (value.length == 0) {
            return false;
        }
        byte boolVal = value[0];
        if (boolVal == 49) {
            return true;
        }
        if (boolVal == 48) {
            return false;
        }
        if (boolVal == -1) return true;
        if (boolVal > 0) return true;
        return false;
    }

    @Override
    public boolean getBoolean(String columnName) throws SQLException {
        return this.getBoolean((int)this.findColumn((String)columnName));
    }

    private final boolean getBooleanFromString(String stringVal) throws SQLException {
        if (stringVal == null) return false;
        if (stringVal.length() <= 0) return false;
        char c = Character.toLowerCase((char)stringVal.charAt((int)0));
        if (c == 't') return true;
        if (c == 'y') return true;
        if (c == '1') return true;
        if (stringVal.equals((Object)"-1")) return true;
        return false;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeByte((int)columnIndex);
        String stringVal = this.getString((int)columnIndex);
        if (this.wasNullFlag) return 0;
        if (stringVal != null) return this.getByteFromString((String)stringVal, (int)columnIndex);
        return 0;
    }

    @Override
    public byte getByte(String columnName) throws SQLException {
        return this.getByte((int)this.findColumn((String)columnName));
    }

    private final byte getByteFromString(String stringVal, int columnIndex) throws SQLException {
        if (stringVal != null && stringVal.length() == 0) {
            return (byte)this.convertToZeroWithEmptyCheck();
        }
        if (stringVal == null) {
            return 0;
        }
        stringVal = stringVal.trim();
        try {
            int decimalIndex = stringVal.indexOf((String)".");
            if (decimalIndex != -1) {
                double valueAsDouble = Double.parseDouble((String)stringVal);
                if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsDouble);
                if (!(valueAsDouble < -128.0)) {
                    if (!(valueAsDouble > 127.0)) return (byte)((int)valueAsDouble);
                }
                this.throwRangeException((String)stringVal, (int)columnIndex, (int)-6);
                return (byte)((int)valueAsDouble);
            }
            long valueAsLong = Long.parseLong((String)stringVal);
            if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsLong);
            if (valueAsLong >= -128L) {
                if (valueAsLong <= 127L) return (byte)((int)valueAsLong);
            }
            this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)columnIndex, (int)-6);
            return (byte)((int)valueAsLong);
        }
        catch (NumberFormatException NFE) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Value____173") + stringVal + Messages.getString((String)"ResultSet.___is_out_of_range_[-127,127]_174")), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return this.getBytes((int)columnIndex, (boolean)false);
    }

    protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeBytes((int)columnIndex, (boolean)noConversion);
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        this.wasNullFlag = this.thisRow.isNull((int)columnIndexMinusOne);
        if (!this.wasNullFlag) return this.thisRow.getColumnValue((int)columnIndexMinusOne);
        return null;
    }

    @Override
    public byte[] getBytes(String columnName) throws SQLException {
        return this.getBytes((int)this.findColumn((String)columnName));
    }

    private final byte[] getBytesFromString(String stringVal) throws SQLException {
        if (stringVal == null) return null;
        return StringUtils.getBytes((String)stringVal, (String)this.connection.getEncoding(), (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (MySQLConnection)this.connection, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getBytesSize() throws SQLException {
        RowData localRowData = this.rowData;
        this.checkClosed();
        if (!(localRowData instanceof RowDataStatic)) return -1;
        int bytesSize = 0;
        int numRows = localRowData.size();
        int i = 0;
        while (i < numRows) {
            bytesSize += localRowData.getAt((int)i).getBytesSize();
            ++i;
        }
        return bytesSize;
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

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeCharacterStream((int)columnIndex);
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getReader((int)columnIndexMinusOne);
    }

    @Override
    public Reader getCharacterStream(String columnName) throws SQLException {
        return this.getCharacterStream((int)this.findColumn((String)columnName));
    }

    private final Reader getCharacterStreamFromString(String stringVal) throws SQLException {
        if (stringVal == null) return null;
        return new StringReader((String)stringVal);
    }

    @Override
    public java.sql.Clob getClob(int i) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeClob((int)i);
        String asString = this.getStringForClob((int)i);
        if (asString != null) return new Clob((String)asString, (ExceptionInterceptor)this.getExceptionInterceptor());
        return null;
    }

    @Override
    public java.sql.Clob getClob(String colName) throws SQLException {
        return this.getClob((int)this.findColumn((String)colName));
    }

    private final java.sql.Clob getClobFromString(String stringVal) throws SQLException {
        return new Clob((String)stringVal, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public int getConcurrency() throws SQLException {
        return 1007;
    }

    @Override
    public String getCursorName() throws SQLException {
        throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Positioned_Update_not_supported"), (String)"S1C00", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return this.getDate((int)columnIndex, null);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if (this.isBinaryEncoded) {
            return this.getNativeDate((int)columnIndex, (Calendar)cal);
        }
        if (!this.useFastDateParsing) {
            String stringVal = this.getStringInternal((int)columnIndex, (boolean)false);
            if (stringVal != null) return this.getDateFromString((String)stringVal, (int)columnIndex, (Calendar)cal);
            return null;
        }
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        Date tmpDate = this.thisRow.getDateFast((int)columnIndexMinusOne, (MySQLConnection)this.connection, (ResultSetImpl)this, (Calendar)cal);
        if (!this.thisRow.isNull((int)columnIndexMinusOne) && tmpDate != null) {
            this.wasNullFlag = false;
            return tmpDate;
        }
        this.wasNullFlag = true;
        return null;
    }

    @Override
    public Date getDate(String columnName) throws SQLException {
        return this.getDate((int)this.findColumn((String)columnName));
    }

    @Override
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return this.getDate((int)this.findColumn((String)columnName), (Calendar)cal);
    }

    private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException {
        int year = 0;
        int month = 0;
        int day = 0;
        try {
            this.wasNullFlag = false;
            if (stringVal == null) {
                this.wasNullFlag = true;
                return null;
            }
            int dec = (stringVal = stringVal.trim()).indexOf((String)".");
            if (dec > -1) {
                stringVal = stringVal.substring((int)0, (int)dec);
            }
            if (stringVal.equals((Object)"0") || stringVal.equals((Object)"0000-00-00") || stringVal.equals((Object)"0000-00-00 00:00:00") || stringVal.equals((Object)"00000000000000") || stringVal.equals((Object)"0")) {
                if ("convertToNull".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                    this.wasNullFlag = true;
                    return null;
                }
                if (!"exception".equals((Object)this.connection.getZeroDateTimeBehavior())) return this.fastDateCreate((Calendar)targetCalendar, (int)1, (int)1, (int)1);
                throw SQLError.createSQLException((String)("Value '" + stringVal + "' can not be represented as java.sql.Date"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            if (this.fields[columnIndex - 1].getMysqlType() == 7) {
                switch (stringVal.length()) {
                    case 19: 
                    case 21: {
                        year = Integer.parseInt((String)stringVal.substring((int)0, (int)4));
                        month = Integer.parseInt((String)stringVal.substring((int)5, (int)7));
                        day = Integer.parseInt((String)stringVal.substring((int)8, (int)10));
                        return this.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
                    }
                    case 8: 
                    case 14: {
                        year = Integer.parseInt((String)stringVal.substring((int)0, (int)4));
                        month = Integer.parseInt((String)stringVal.substring((int)4, (int)6));
                        day = Integer.parseInt((String)stringVal.substring((int)6, (int)8));
                        return this.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
                    }
                    case 6: 
                    case 10: 
                    case 12: {
                        year = Integer.parseInt((String)stringVal.substring((int)0, (int)2));
                        if (year <= 69) {
                            year += 100;
                        }
                        month = Integer.parseInt((String)stringVal.substring((int)2, (int)4));
                        day = Integer.parseInt((String)stringVal.substring((int)4, (int)6));
                        return this.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)month, (int)day);
                    }
                    case 4: {
                        year = Integer.parseInt((String)stringVal.substring((int)0, (int)4));
                        if (year <= 69) {
                            year += 100;
                        }
                        month = Integer.parseInt((String)stringVal.substring((int)2, (int)4));
                        return this.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)month, (int)1);
                    }
                    case 2: {
                        year = Integer.parseInt((String)stringVal.substring((int)0, (int)2));
                        if (year > 69) return this.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)1, (int)1);
                        year += 100;
                        return this.fastDateCreate((Calendar)targetCalendar, (int)(year + 1900), (int)1, (int)1);
                    }
                }
                throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            if (this.fields[columnIndex - 1].getMysqlType() == 13) {
                if (stringVal.length() != 2 && stringVal.length() != 1) {
                    year = Integer.parseInt((String)stringVal.substring((int)0, (int)4));
                    return this.fastDateCreate((Calendar)targetCalendar, (int)year, (int)1, (int)1);
                }
                year = Integer.parseInt((String)stringVal);
                if (year > 69) return this.fastDateCreate((Calendar)targetCalendar, (int)(year += 1900), (int)1, (int)1);
                year += 100;
                return this.fastDateCreate((Calendar)targetCalendar, (int)(year += 1900), (int)1, (int)1);
            }
            if (this.fields[columnIndex - 1].getMysqlType() == 11) {
                return this.fastDateCreate((Calendar)targetCalendar, (int)1970, (int)1, (int)1);
            }
            if (stringVal.length() < 10) {
                if (stringVal.length() != 8) throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                return this.fastDateCreate((Calendar)targetCalendar, (int)1970, (int)1, (int)1);
            }
            if (stringVal.length() != 18) {
                year = Integer.parseInt((String)stringVal.substring((int)0, (int)4));
                month = Integer.parseInt((String)stringVal.substring((int)5, (int)7));
                day = Integer.parseInt((String)stringVal.substring((int)8, (int)10));
                return this.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
            }
            StringTokenizer st = new StringTokenizer((String)stringVal, (String)"- ");
            year = Integer.parseInt((String)st.nextToken());
            month = Integer.parseInt((String)st.nextToken());
            day = Integer.parseInt((String)st.nextToken());
            return this.fastDateCreate((Calendar)targetCalendar, (int)year, (int)month, (int)day);
        }
        catch (SQLException sqlEx) {
            throw sqlEx;
        }
        catch (Exception e) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_Date", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    private TimeZone getDefaultTimeZone() {
        TimeZone timeZone;
        if (this.useLegacyDatetimeCode) {
            timeZone = this.connection.getDefaultTimeZone();
            return timeZone;
        }
        timeZone = this.serverTimeZoneTz;
        return timeZone;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeDouble((int)columnIndex);
        return this.getDoubleInternal((int)columnIndex);
    }

    @Override
    public double getDouble(String columnName) throws SQLException {
        return this.getDouble((int)this.findColumn((String)columnName));
    }

    private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException {
        return this.getDoubleInternal((String)stringVal, (int)columnIndex);
    }

    protected double getDoubleInternal(int colIndex) throws SQLException {
        return this.getDoubleInternal((String)this.getString((int)colIndex), (int)colIndex);
    }

    protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException {
        try {
            if (stringVal == null) {
                return 0.0;
            }
            if (stringVal.length() == 0) {
                return (double)this.convertToZeroWithEmptyCheck();
            }
            double d = Double.parseDouble((String)stringVal);
            if (!this.useStrictFloatingPoint) return d;
            if (d == 2.147483648E9) {
                return 2.147483647E9;
            }
            if (d == 1.0000000036275E-15) {
                return 1.0E-15;
            }
            if (d == 9.999999869911E14) {
                return 9.99999999999999E14;
            }
            if (d == 1.4012984643248E-45) {
                return 1.4E-45;
            }
            if (d == 1.4013E-45) {
                return 1.4E-45;
            }
            if (d == 3.4028234663853E37) {
                return 3.4028235E37;
            }
            if (d == -2.14748E9) {
                return -2.147483648E9;
            }
            if (d != 3.40282E37) return d;
            return 3.4028235E37;
        }
        catch (NumberFormatException e) {
            if (this.fields[colIndex - 1].getMysqlType() != 16) throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_number", (Object[])new Object[]{stringVal, Integer.valueOf((int)colIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)colIndex);
            return (double)valueAsLong;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getFetchDirection() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.fetchDirection;
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
    public char getFirstCharOfQuery() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            // MONITOREXIT : object
            return this.firstCharOfQuery;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeFloat((int)columnIndex);
        String val = null;
        val = this.getString((int)columnIndex);
        return this.getFloatFromString((String)val, (int)columnIndex);
    }

    @Override
    public float getFloat(String columnName) throws SQLException {
        return this.getFloat((int)this.findColumn((String)columnName));
    }

    private final float getFloatFromString(String val, int columnIndex) throws SQLException {
        try {
            double valAsDouble;
            if (val == null) return 0.0f;
            if (val.length() == 0) {
                return (float)this.convertToZeroWithEmptyCheck();
            }
            float f = Float.parseFloat((String)val);
            if (!this.jdbcCompliantTruncationForReads) return f;
            if (f != Float.MIN_VALUE) {
                if (f != Float.MAX_VALUE) return f;
            }
            if (!((valAsDouble = Double.parseDouble((String)val)) < 1.401298464324817E-45 - MIN_DIFF_PREC)) {
                if (!(valAsDouble > 3.4028234663852886E38 - MAX_DIFF_PREC)) return f;
            }
            this.throwRangeException((String)String.valueOf((double)valAsDouble), (int)columnIndex, (int)6);
            return f;
        }
        catch (NumberFormatException nfe) {
            try {
                Double valueAsDouble = new Double((String)val);
                float valueAsFloat = valueAsDouble.floatValue();
                if (!this.jdbcCompliantTruncationForReads) return valueAsFloat;
                if (!this.jdbcCompliantTruncationForReads || valueAsFloat != Float.NEGATIVE_INFINITY) {
                    if (valueAsFloat != Float.POSITIVE_INFINITY) return valueAsFloat;
                }
                this.throwRangeException((String)valueAsDouble.toString(), (int)columnIndex, (int)6);
                return valueAsFloat;
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString((String)"ResultSet.___in_column__201") + columnIndex), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.isBinaryEncoded) return this.getNativeInt((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
            long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)columnIndex);
            if (!this.jdbcCompliantTruncationForReads) return (int)valueAsLong;
            if (valueAsLong >= Integer.MIN_VALUE) {
                if (valueAsLong <= Integer.MAX_VALUE) return (int)valueAsLong;
            }
            this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)columnIndex, (int)4);
            return (int)valueAsLong;
        }
        if (this.useFastIntParsing) {
            if (this.thisRow.length((int)columnIndexMinusOne) == 0L) {
                return this.convertToZeroWithEmptyCheck();
            }
            boolean needsFullParse = this.thisRow.isFloatingPointNumber((int)columnIndexMinusOne);
            if (!needsFullParse) {
                try {
                    return this.getIntWithOverflowCheck((int)columnIndexMinusOne);
                }
                catch (NumberFormatException nfe) {
                    try {
                        return this.parseIntAsDouble((int)columnIndex, (String)this.thisRow.getString((int)columnIndexMinusOne, (String)this.fields[columnIndexMinusOne].getEncoding(), (MySQLConnection)this.connection));
                    }
                    catch (NumberFormatException newNfe) {
                        throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString((int)columnIndexMinusOne, (String)this.fields[columnIndexMinusOne].getEncoding(), (MySQLConnection)this.connection) + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            }
        }
        String val = null;
        try {
            val = this.getString((int)columnIndex);
            if (val == null) {
                return 0;
            }
            if (val.length() == 0) {
                return this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") == -1 && val.indexOf((String)"E") == -1 && val.indexOf((String)".") == -1) {
                int intVal = Integer.parseInt((String)val);
                this.checkForIntegerTruncation((int)columnIndexMinusOne, null, (int)intVal);
                return intVal;
            }
            int intVal = this.parseIntAsDouble((int)columnIndex, (String)val);
            this.checkForIntegerTruncation((int)columnIndex, null, (int)intVal);
            return intVal;
        }
        catch (NumberFormatException nfe) {
            try {
                return this.parseIntAsDouble((int)columnIndex, (String)val);
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getInt()_-____74") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        return this.getInt((int)this.findColumn((String)columnName));
    }

    private final int getIntFromString(String val, int columnIndex) throws SQLException {
        try {
            if (val == null) return 0;
            if (val.length() == 0) {
                return this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") == -1 && val.indexOf((String)"E") == -1 && val.indexOf((String)".") == -1) {
                long valueAsLong;
                val = val.trim();
                int valueAsInt = Integer.parseInt((String)val);
                if (!this.jdbcCompliantTruncationForReads) return valueAsInt;
                if (valueAsInt != Integer.MIN_VALUE) {
                    if (valueAsInt != Integer.MAX_VALUE) return valueAsInt;
                }
                if ((valueAsLong = Long.parseLong((String)val)) >= Integer.MIN_VALUE) {
                    if (valueAsLong <= Integer.MAX_VALUE) return valueAsInt;
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)columnIndex, (int)4);
                return valueAsInt;
            }
            double valueAsDouble = Double.parseDouble((String)val);
            if (!this.jdbcCompliantTruncationForReads) return (int)valueAsDouble;
            if (!(valueAsDouble < -2.147483648E9)) {
                if (!(valueAsDouble > 2.147483647E9)) return (int)valueAsDouble;
            }
            this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)columnIndex, (int)4);
            return (int)valueAsDouble;
        }
        catch (NumberFormatException nfe) {
            try {
                double valueAsDouble = Double.parseDouble((String)val);
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsDouble;
                if (!(valueAsDouble < -2.147483648E9)) {
                    if (!(valueAsDouble > 2.147483647E9)) return (int)valueAsDouble;
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)columnIndex, (int)4);
                return (int)valueAsDouble;
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString((String)"ResultSet.___in_column__207") + columnIndex), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return this.getLong((int)columnIndex, (boolean)true);
    }

    private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.isBinaryEncoded) return this.getNativeLong((int)columnIndex, (boolean)overflowCheck, (boolean)true);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return 0L;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
            return this.getNumericRepresentationOfSQLBitType((int)columnIndex);
        }
        if (this.useFastIntParsing) {
            if (this.thisRow.length((int)columnIndexMinusOne) == 0L) {
                return (long)this.convertToZeroWithEmptyCheck();
            }
            boolean needsFullParse = this.thisRow.isFloatingPointNumber((int)columnIndexMinusOne);
            if (!needsFullParse) {
                try {
                    return this.getLongWithOverflowCheck((int)columnIndexMinusOne, (boolean)overflowCheck);
                }
                catch (NumberFormatException nfe) {
                    try {
                        return this.parseLongAsDouble((int)columnIndexMinusOne, (String)this.thisRow.getString((int)columnIndexMinusOne, (String)this.fields[columnIndexMinusOne].getEncoding(), (MySQLConnection)this.connection));
                    }
                    catch (NumberFormatException newNfe) {
                        throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString((int)columnIndexMinusOne, (String)this.fields[columnIndexMinusOne].getEncoding(), (MySQLConnection)this.connection) + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            }
        }
        String val = null;
        try {
            val = this.getString((int)columnIndex);
            if (val == null) {
                return 0L;
            }
            if (val.length() == 0) {
                return (long)this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") != -1) return this.parseLongAsDouble((int)columnIndexMinusOne, (String)val);
            if (val.indexOf((String)"E") != -1) return this.parseLongAsDouble((int)columnIndexMinusOne, (String)val);
            return this.parseLongWithOverflowCheck((int)columnIndexMinusOne, null, (String)val, (boolean)overflowCheck);
        }
        catch (NumberFormatException nfe) {
            try {
                return this.parseLongAsDouble((int)columnIndexMinusOne, (String)val);
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getLong()_-____79") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public long getLong(String columnName) throws SQLException {
        return this.getLong((int)this.findColumn((String)columnName));
    }

    private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException {
        try {
            if (val == null) return 0L;
            if (val.length() == 0) {
                return (long)this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") != -1) return this.parseLongAsDouble((int)columnIndexZeroBased, (String)val);
            if (val.indexOf((String)"E") != -1) return this.parseLongAsDouble((int)columnIndexZeroBased, (String)val);
            return this.parseLongWithOverflowCheck((int)columnIndexZeroBased, null, (String)val, (boolean)true);
        }
        catch (NumberFormatException nfe) {
            try {
                return this.parseLongAsDouble((int)columnIndexZeroBased, (String)val);
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString((String)"ResultSet.___in_column__212") + (columnIndexZeroBased + 1)), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public java.sql.ResultSetMetaData getMetaData() throws SQLException {
        this.checkClosed();
        return new ResultSetMetaData((Field[])this.fields, (boolean)this.connection.getUseOldAliasMetadataBehavior(), (boolean)this.connection.getYearIsDateType(), (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    protected Array getNativeArray(int i) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    protected InputStream getNativeAsciiStream(int columnIndex) throws SQLException {
        this.checkRowPos();
        return this.getNativeBinaryStream((int)columnIndex);
    }

    protected BigDecimal getNativeBigDecimal(int columnIndex) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        int scale = this.fields[columnIndex - 1].getDecimals();
        return this.getNativeBigDecimal((int)columnIndex, (int)scale);
    }

    protected BigDecimal getNativeBigDecimal(int columnIndex, int scale) throws SQLException {
        this.checkColumnBounds((int)columnIndex);
        String stringVal = null;
        Field f = this.fields[columnIndex - 1];
        byte[] value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        if (value == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        switch (f.getSQLType()) {
            case 2: 
            case 3: {
                stringVal = StringUtils.toAsciiString((byte[])value);
                return this.getBigDecimalFromString((String)stringVal, (int)columnIndex, (int)scale);
            }
        }
        stringVal = this.getNativeString((int)columnIndex);
        return this.getBigDecimalFromString((String)stringVal, (int)columnIndex, (int)scale);
    }

    protected InputStream getNativeBinaryStream(int columnIndex) throws SQLException {
        this.checkRowPos();
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        switch (this.fields[columnIndexMinusOne].getSQLType()) {
            case -7: 
            case -4: 
            case -3: 
            case -2: 
            case 2004: {
                return this.thisRow.getBinaryInputStream((int)columnIndexMinusOne);
            }
        }
        byte[] b = this.getNativeBytes((int)columnIndex, (boolean)false);
        if (b == null) return null;
        return new ByteArrayInputStream((byte[])b);
    }

    /*
     * Unable to fully structure code
     */
    protected java.sql.Blob getNativeBlob(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        this.wasNullFlag = value == null;
        if (this.wasNullFlag) {
            return null;
        }
        mysqlType = this.fields[columnIndex - 1].getMysqlType();
        dataAsBytes = null;
        switch (mysqlType) {
            case 249: 
            case 250: 
            case 251: 
            case 252: {
                dataAsBytes = value;
                ** break;
            }
        }
        dataAsBytes = this.getNativeBytes((int)columnIndex, (boolean)false);
lbl14: // 2 sources:
        if (this.connection.getEmulateLocators() != false) return new BlobFromLocator((ResultSetImpl)this, (int)columnIndex, (ExceptionInterceptor)this.getExceptionInterceptor());
        return new Blob((byte[])dataAsBytes, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    public static boolean arraysEqual(byte[] left, byte[] right) {
        if (left == null) {
            if (right != null) return false;
            return true;
        }
        if (right == null) {
            return false;
        }
        if (left.length != right.length) {
            return false;
        }
        int i = 0;
        while (i < left.length) {
            if (left[i] != right[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    protected byte getNativeByte(int columnIndex) throws SQLException {
        return this.getNativeByte((int)columnIndex, (boolean)true);
    }

    protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        byte[] value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        if (value == null) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field field = this.fields[--columnIndex];
        switch (field.getMysqlType()) {
            case 16: {
                long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
                if (!overflowCheck) return (byte)((int)valueAsLong);
                if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsLong);
                if (valueAsLong >= -128L) {
                    if (valueAsLong <= 127L) return (byte)((int)valueAsLong);
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)-6);
                return (byte)((int)valueAsLong);
            }
            case 1: {
                byte valueAsByte = value[0];
                if (!field.isUnsigned()) {
                    return valueAsByte;
                }
                short valueAsShort = valueAsByte >= 0 ? (short)valueAsByte : (short)(valueAsByte + 256);
                if (!overflowCheck) return (byte)valueAsShort;
                if (!this.jdbcCompliantTruncationForReads) return (byte)valueAsShort;
                if (valueAsShort <= 127) return (byte)valueAsShort;
                this.throwRangeException((String)String.valueOf((int)valueAsShort), (int)(columnIndex + 1), (int)-6);
                return (byte)valueAsShort;
            }
            case 2: 
            case 13: {
                short valueAsShort = this.getNativeShort((int)(columnIndex + 1));
                if (!overflowCheck) return (byte)valueAsShort;
                if (!this.jdbcCompliantTruncationForReads) return (byte)valueAsShort;
                if (valueAsShort >= -128) {
                    if (valueAsShort <= 127) return (byte)valueAsShort;
                }
                this.throwRangeException((String)String.valueOf((int)valueAsShort), (int)(columnIndex + 1), (int)-6);
                return (byte)valueAsShort;
            }
            case 3: 
            case 9: {
                int valueAsInt = this.getNativeInt((int)(columnIndex + 1), (boolean)false);
                if (!overflowCheck) return (byte)valueAsInt;
                if (!this.jdbcCompliantTruncationForReads) return (byte)valueAsInt;
                if (valueAsInt >= -128) {
                    if (valueAsInt <= 127) return (byte)valueAsInt;
                }
                this.throwRangeException((String)String.valueOf((int)valueAsInt), (int)(columnIndex + 1), (int)-6);
                return (byte)valueAsInt;
            }
            case 4: {
                float valueAsFloat = this.getNativeFloat((int)(columnIndex + 1));
                if (!overflowCheck) return (byte)((int)valueAsFloat);
                if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsFloat);
                if (!(valueAsFloat < -128.0f)) {
                    if (!(valueAsFloat > 127.0f)) return (byte)((int)valueAsFloat);
                }
                this.throwRangeException((String)String.valueOf((float)valueAsFloat), (int)(columnIndex + 1), (int)-6);
                return (byte)((int)valueAsFloat);
            }
            case 5: {
                double valueAsDouble = this.getNativeDouble((int)(columnIndex + 1));
                if (!overflowCheck) return (byte)((int)valueAsDouble);
                if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsDouble);
                if (!(valueAsDouble < -128.0)) {
                    if (!(valueAsDouble > 127.0)) return (byte)((int)valueAsDouble);
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)-6);
                return (byte)((int)valueAsDouble);
            }
            case 8: {
                long valueAsLong = this.getNativeLong((int)(columnIndex + 1), (boolean)false, (boolean)true);
                if (!overflowCheck) return (byte)((int)valueAsLong);
                if (!this.jdbcCompliantTruncationForReads) return (byte)((int)valueAsLong);
                if (valueAsLong >= -128L) {
                    if (valueAsLong <= 127L) return (byte)((int)valueAsLong);
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)-6);
                return (byte)((int)valueAsLong);
            }
        }
        if (!this.useUsageAdvisor) return this.getByteFromString((String)this.getNativeString((int)(columnIndex + 1)), (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getByte()", (int)columnIndex, (Object)this.thisRow.getColumnValue((int)(columnIndex - 1)), (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getByteFromString((String)this.getNativeString((int)(columnIndex + 1)), (int)(columnIndex + 1));
    }

    protected byte[] getNativeBytes(int columnIndex, boolean noConversion) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        byte[] value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        this.wasNullFlag = value == null;
        if (this.wasNullFlag) {
            return null;
        }
        Field field = this.fields[columnIndex - 1];
        int mysqlType = field.getMysqlType();
        if (noConversion) {
            mysqlType = 252;
        }
        switch (mysqlType) {
            case 16: 
            case 249: 
            case 250: 
            case 251: 
            case 252: {
                return value;
            }
            case 15: 
            case 253: 
            case 254: {
                if (!(value instanceof byte[])) break;
                return value;
            }
        }
        int sqlType = field.getSQLType();
        if (sqlType == -3) return value;
        if (sqlType != -2) return this.getBytesFromString((String)this.getNativeString((int)columnIndex));
        return value;
    }

    protected Reader getNativeCharacterStream(int columnIndex) throws SQLException {
        int columnIndexMinusOne = columnIndex - 1;
        switch (this.fields[columnIndexMinusOne].getSQLType()) {
            case -1: 
            case 1: 
            case 12: 
            case 2005: {
                if (this.thisRow.isNull((int)columnIndexMinusOne)) {
                    this.wasNullFlag = true;
                    return null;
                }
                this.wasNullFlag = false;
                return this.thisRow.getReader((int)columnIndexMinusOne);
            }
        }
        String asString = this.getStringForClob((int)columnIndex);
        if (asString != null) return this.getCharacterStreamFromString((String)asString);
        return null;
    }

    protected java.sql.Clob getNativeClob(int columnIndex) throws SQLException {
        String stringVal = this.getStringForClob((int)columnIndex);
        if (stringVal != null) return this.getClobFromString((String)stringVal);
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getNativeConvertToString(int columnIndex, Field field) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int sqlType = field.getSQLType();
        int mysqlType = field.getMysqlType();
        switch (sqlType) {
            case -7: {
                // MONITOREXIT : object
                return String.valueOf((long)this.getNumericRepresentationOfSQLBitType((int)columnIndex));
            }
            case 16: {
                boolean booleanVal = this.getBoolean((int)columnIndex);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((boolean)booleanVal);
            }
            case -6: {
                byte tinyintVal = this.getNativeByte((int)columnIndex, (boolean)false);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                if (field.isUnsigned() && tinyintVal < 0) {
                    short unsignedTinyVal = (short)(tinyintVal & 255);
                    // MONITOREXIT : object
                    return String.valueOf((int)unsignedTinyVal);
                }
                // MONITOREXIT : object
                return String.valueOf((int)tinyintVal);
            }
            case 5: {
                int intVal = this.getNativeInt((int)columnIndex, (boolean)false);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                if (field.isUnsigned() && intVal < 0) {
                    // MONITOREXIT : object
                    return String.valueOf((int)(intVal &= 65535));
                }
                // MONITOREXIT : object
                return String.valueOf((int)intVal);
            }
            case 4: {
                int intVal = this.getNativeInt((int)columnIndex, (boolean)false);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                if (field.isUnsigned() && intVal < 0 && field.getMysqlType() != 9) {
                    long longVal = (long)intVal & 0xFFFFFFFFL;
                    // MONITOREXIT : object
                    return String.valueOf((long)longVal);
                }
                // MONITOREXIT : object
                return String.valueOf((int)intVal);
            }
            case -5: {
                if (!field.isUnsigned()) {
                    long longVal = this.getNativeLong((int)columnIndex, (boolean)false, (boolean)true);
                    if (this.wasNullFlag) {
                        // MONITOREXIT : object
                        return null;
                    }
                    // MONITOREXIT : object
                    return String.valueOf((long)longVal);
                }
                long longVal = this.getNativeLong((int)columnIndex, (boolean)false, (boolean)false);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((Object)ResultSetImpl.convertLongToUlong((long)longVal));
            }
            case 7: {
                float floatVal = this.getNativeFloat((int)columnIndex);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((float)floatVal);
            }
            case 6: 
            case 8: {
                double doubleVal = this.getNativeDouble((int)columnIndex);
                if (this.wasNullFlag) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((double)doubleVal);
            }
            case 2: 
            case 3: {
                String stringVal = StringUtils.toAsciiString((byte[])this.thisRow.getColumnValue((int)(columnIndex - 1)));
                if (stringVal == null) {
                    this.wasNullFlag = true;
                    // MONITOREXIT : object
                    return null;
                }
                this.wasNullFlag = false;
                if (stringVal.length() == 0) {
                    BigDecimal val = new BigDecimal((int)0);
                    // MONITOREXIT : object
                    return val.toString();
                }
                try {
                    BigDecimal val = new BigDecimal((String)stringVal);
                    return val.toString();
                }
                catch (NumberFormatException ex) {
                    throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            case -1: 
            case 1: 
            case 12: {
                // MONITOREXIT : object
                return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
            }
            case -4: 
            case -3: 
            case -2: {
                byte[] data;
                if (!field.isBlob()) {
                    // MONITOREXIT : object
                    return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
                }
                if (!field.isBinary()) {
                    // MONITOREXIT : object
                    return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
                }
                Object obj = data = this.getBytes((int)columnIndex);
                if (this.connection.getAutoDeserialize() && data != null && data.length >= 2) {
                    if (data[0] == -84 && data[1] == -19) {
                        try {
                            ByteArrayInputStream bytesIn = new ByteArrayInputStream((byte[])data);
                            ObjectInputStream objIn = new ObjectInputStream((InputStream)bytesIn);
                            obj = objIn.readObject();
                            objIn.close();
                            bytesIn.close();
                            return obj.toString();
                        }
                        catch (ClassNotFoundException cnfe) {
                            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString((String)"ResultSet._while_reading_serialized_object_92")), (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                        catch (IOException ex) {
                            obj = data;
                        }
                    }
                    // MONITOREXIT : object
                    return obj.toString();
                }
                // MONITOREXIT : object
                return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
            }
            case 91: {
                Date dt;
                if (mysqlType == 13) {
                    short shortVal = this.getNativeShort((int)columnIndex);
                    if (!this.connection.getYearIsDateType()) {
                        if (this.wasNullFlag) {
                            // MONITOREXIT : object
                            return null;
                        }
                        // MONITOREXIT : object
                        return String.valueOf((int)shortVal);
                    }
                    if (field.getLength() == 2L) {
                        if (shortVal <= 69) {
                            shortVal = (short)(shortVal + 100);
                        }
                        shortVal = (short)(shortVal + 1900);
                    }
                    // MONITOREXIT : object
                    return this.fastDateCreate(null, (int)shortVal, (int)1, (int)1).toString();
                }
                if (this.connection.getNoDatetimeStringSync()) {
                    byte[] asBytes = this.getNativeBytes((int)columnIndex, (boolean)true);
                    if (asBytes == null) {
                        // MONITOREXIT : object
                        return null;
                    }
                    if (asBytes.length == 0) {
                        // MONITOREXIT : object
                        return "0000-00-00";
                    }
                    int year = asBytes[0] & 255 | (asBytes[1] & 255) << 8;
                    byte month = asBytes[2];
                    byte day = asBytes[3];
                    if (year == 0 && month == 0 && day == 0) {
                        // MONITOREXIT : object
                        return "0000-00-00";
                    }
                }
                if ((dt = this.getNativeDate((int)columnIndex)) == null) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((Object)dt);
            }
            case 92: {
                Time tm = this.getNativeTime((int)columnIndex, null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false);
                if (tm == null) {
                    // MONITOREXIT : object
                    return null;
                }
                // MONITOREXIT : object
                return String.valueOf((Object)tm);
            }
            case 93: {
                Timestamp tstamp;
                if (this.connection.getNoDatetimeStringSync()) {
                    byte[] asBytes = this.getNativeBytes((int)columnIndex, (boolean)true);
                    if (asBytes == null) {
                        // MONITOREXIT : object
                        return null;
                    }
                    if (asBytes.length == 0) {
                        // MONITOREXIT : object
                        return "0000-00-00 00:00:00";
                    }
                    int year = asBytes[0] & 255 | (asBytes[1] & 255) << 8;
                    byte month = asBytes[2];
                    byte day = asBytes[3];
                    if (year == 0 && month == 0 && day == 0) {
                        // MONITOREXIT : object
                        return "0000-00-00 00:00:00";
                    }
                }
                if ((tstamp = this.getNativeTimestamp((int)columnIndex, null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false)) == null) {
                    // MONITOREXIT : object
                    return null;
                }
                String result = String.valueOf((Object)tstamp);
                if (!this.connection.getNoDatetimeStringSync()) {
                    // MONITOREXIT : object
                    return result;
                }
                if (result.endsWith((String)".0")) {
                    // MONITOREXIT : object
                    return result.substring((int)0, (int)(result.length() - 2));
                }
                // MONITOREXIT : object
                return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
            }
        }
        // MONITOREXIT : object
        return this.extractStringFromNativeColumn((int)columnIndex, (int)mysqlType);
    }

    protected Date getNativeDate(int columnIndex) throws SQLException {
        return this.getNativeDate((int)columnIndex, null);
    }

    protected Date getNativeDate(int columnIndex, Calendar cal) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        Date dateToReturn = null;
        if (mysqlType == 10) {
            dateToReturn = this.thisRow.getNativeDate((int)columnIndexMinusOne, (MySQLConnection)this.connection, (ResultSetImpl)this, (Calendar)cal);
        } else {
            TimeZone tz = cal != null ? cal.getTimeZone() : this.getDefaultTimeZone();
            boolean rollForward = tz != null && !tz.equals((Object)this.getDefaultTimeZone());
            dateToReturn = (Date)this.thisRow.getNativeDateTimeValue((int)columnIndexMinusOne, null, (int)91, (int)mysqlType, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this);
        }
        if (dateToReturn == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return dateToReturn;
    }

    Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
        if (this.useUsageAdvisor) {
            this.issueConversionViaParsingWarning((String)"getDate()", (int)columnIndex, (Object)this.thisRow.getColumnValue((int)(columnIndex - 1)), (Field)this.fields[columnIndex - 1], (int[])new int[]{10});
        }
        String stringVal = this.getNativeString((int)columnIndex);
        return this.getDateFromString((String)stringVal, (int)columnIndex, null);
    }

    protected double getNativeDouble(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.thisRow.isNull((int)(--columnIndex))) {
            this.wasNullFlag = true;
            return 0.0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex];
        switch (f.getMysqlType()) {
            case 5: {
                return this.thisRow.getNativeDouble((int)columnIndex);
            }
            case 1: {
                if (f.isUnsigned()) return (double)this.getNativeShort((int)(columnIndex + 1));
                return (double)this.getNativeByte((int)(columnIndex + 1));
            }
            case 2: 
            case 13: {
                if (f.isUnsigned()) return (double)this.getNativeInt((int)(columnIndex + 1));
                return (double)this.getNativeShort((int)(columnIndex + 1));
            }
            case 3: 
            case 9: {
                if (f.isUnsigned()) return (double)this.getNativeLong((int)(columnIndex + 1));
                return (double)this.getNativeInt((int)(columnIndex + 1));
            }
            case 8: {
                long valueAsLong = this.getNativeLong((int)(columnIndex + 1));
                if (!f.isUnsigned()) {
                    return (double)valueAsLong;
                }
                BigInteger asBigInt = ResultSetImpl.convertLongToUlong((long)valueAsLong);
                return asBigInt.doubleValue();
            }
            case 4: {
                return (double)this.getNativeFloat((int)(columnIndex + 1));
            }
            case 16: {
                return (double)this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
            }
        }
        String stringVal = this.getNativeString((int)(columnIndex + 1));
        if (!this.useUsageAdvisor) return this.getDoubleFromString((String)stringVal, (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getDouble()", (int)columnIndex, (Object)stringVal, (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getDoubleFromString((String)stringVal, (int)(columnIndex + 1));
    }

    protected float getNativeFloat(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.thisRow.isNull((int)(--columnIndex))) {
            this.wasNullFlag = true;
            return 0.0f;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex];
        switch (f.getMysqlType()) {
            case 16: {
                long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
                return (float)valueAsLong;
            }
            case 5: {
                Double valueAsDouble = new Double((double)this.getNativeDouble((int)(columnIndex + 1)));
                float valueAsFloat = valueAsDouble.floatValue();
                if (!this.jdbcCompliantTruncationForReads || valueAsFloat != Float.NEGATIVE_INFINITY) {
                    if (valueAsFloat != Float.POSITIVE_INFINITY) return (float)this.getNativeDouble((int)(columnIndex + 1));
                }
                this.throwRangeException((String)valueAsDouble.toString(), (int)(columnIndex + 1), (int)6);
                return (float)this.getNativeDouble((int)(columnIndex + 1));
            }
            case 1: {
                if (f.isUnsigned()) return (float)this.getNativeShort((int)(columnIndex + 1));
                return (float)this.getNativeByte((int)(columnIndex + 1));
            }
            case 2: 
            case 13: {
                if (f.isUnsigned()) return (float)this.getNativeInt((int)(columnIndex + 1));
                return (float)this.getNativeShort((int)(columnIndex + 1));
            }
            case 3: 
            case 9: {
                if (f.isUnsigned()) return (float)this.getNativeLong((int)(columnIndex + 1));
                return (float)this.getNativeInt((int)(columnIndex + 1));
            }
            case 8: {
                long valueAsLong = this.getNativeLong((int)(columnIndex + 1));
                if (!f.isUnsigned()) {
                    return (float)valueAsLong;
                }
                BigInteger asBigInt = ResultSetImpl.convertLongToUlong((long)valueAsLong);
                return asBigInt.floatValue();
            }
            case 4: {
                return this.thisRow.getNativeFloat((int)columnIndex);
            }
        }
        String stringVal = this.getNativeString((int)(columnIndex + 1));
        if (!this.useUsageAdvisor) return this.getFloatFromString((String)stringVal, (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getFloat()", (int)columnIndex, (Object)stringVal, (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getFloatFromString((String)stringVal, (int)(columnIndex + 1));
    }

    protected int getNativeInt(int columnIndex) throws SQLException {
        return this.getNativeInt((int)columnIndex, (boolean)true);
    }

    protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.thisRow.isNull((int)(--columnIndex))) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex];
        switch (f.getMysqlType()) {
            case 16: {
                long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
                if (!overflowCheck) return (int)valueAsLong;
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsLong;
                if (valueAsLong >= Integer.MIN_VALUE) {
                    if (valueAsLong <= Integer.MAX_VALUE) return (int)valueAsLong;
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)4);
                return (int)valueAsLong;
            }
            case 1: {
                byte tinyintVal = this.getNativeByte((int)(columnIndex + 1), (boolean)false);
                if (!f.isUnsigned()) return tinyintVal;
                if (tinyintVal < 0) return tinyintVal + 256;
                return tinyintVal;
            }
            case 2: 
            case 13: {
                short asShort = this.getNativeShort((int)(columnIndex + 1), (boolean)false);
                if (!f.isUnsigned()) return asShort;
                if (asShort < 0) return asShort + 65536;
                return asShort;
            }
            case 3: 
            case 9: {
                int valueAsInt = this.thisRow.getNativeInt((int)columnIndex);
                if (!f.isUnsigned()) {
                    return valueAsInt;
                }
                long valueAsLong = valueAsInt >= 0 ? (long)valueAsInt : (long)valueAsInt + 0x100000000L;
                if (!overflowCheck) return (int)valueAsLong;
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsLong;
                if (valueAsLong <= Integer.MAX_VALUE) return (int)valueAsLong;
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)4);
                return (int)valueAsLong;
            }
            case 8: {
                long valueAsLong = this.getNativeLong((int)(columnIndex + 1), (boolean)false, (boolean)true);
                if (!overflowCheck) return (int)valueAsLong;
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsLong;
                if (valueAsLong >= Integer.MIN_VALUE) {
                    if (valueAsLong <= Integer.MAX_VALUE) return (int)valueAsLong;
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)4);
                return (int)valueAsLong;
            }
            case 5: {
                double valueAsDouble = this.getNativeDouble((int)(columnIndex + 1));
                if (!overflowCheck) return (int)valueAsDouble;
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsDouble;
                if (!(valueAsDouble < -2.147483648E9)) {
                    if (!(valueAsDouble > 2.147483647E9)) return (int)valueAsDouble;
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)4);
                return (int)valueAsDouble;
            }
            case 4: {
                double valueAsDouble = (double)this.getNativeFloat((int)(columnIndex + 1));
                if (!overflowCheck) return (int)valueAsDouble;
                if (!this.jdbcCompliantTruncationForReads) return (int)valueAsDouble;
                if (!(valueAsDouble < -2.147483648E9)) {
                    if (!(valueAsDouble > 2.147483647E9)) return (int)valueAsDouble;
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)4);
                return (int)valueAsDouble;
            }
        }
        String stringVal = this.getNativeString((int)(columnIndex + 1));
        if (!this.useUsageAdvisor) return this.getIntFromString((String)stringVal, (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getInt()", (int)columnIndex, (Object)stringVal, (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getIntFromString((String)stringVal, (int)(columnIndex + 1));
    }

    protected long getNativeLong(int columnIndex) throws SQLException {
        return this.getNativeLong((int)columnIndex, (boolean)true, (boolean)true);
    }

    protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.thisRow.isNull((int)(--columnIndex))) {
            this.wasNullFlag = true;
            return 0L;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex];
        switch (f.getMysqlType()) {
            case 16: {
                return this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
            }
            case 1: {
                if (f.isUnsigned()) return (long)this.getNativeInt((int)(columnIndex + 1));
                return (long)this.getNativeByte((int)(columnIndex + 1));
            }
            case 2: {
                if (f.isUnsigned()) return (long)this.getNativeInt((int)(columnIndex + 1), (boolean)false);
                return (long)this.getNativeShort((int)(columnIndex + 1));
            }
            case 13: {
                return (long)this.getNativeShort((int)(columnIndex + 1));
            }
            case 3: 
            case 9: {
                int asInt = this.getNativeInt((int)(columnIndex + 1), (boolean)false);
                if (!f.isUnsigned()) return (long)asInt;
                if (asInt < 0) return (long)asInt + 0x100000000L;
                return (long)asInt;
            }
            case 8: {
                long valueAsLong = this.thisRow.getNativeLong((int)columnIndex);
                if (!f.isUnsigned()) return valueAsLong;
                if (!expandUnsignedLong) {
                    return valueAsLong;
                }
                BigInteger asBigInt = ResultSetImpl.convertLongToUlong((long)valueAsLong);
                if (!overflowCheck) return this.getLongFromString((String)asBigInt.toString(), (int)columnIndex);
                if (!this.jdbcCompliantTruncationForReads) return this.getLongFromString((String)asBigInt.toString(), (int)columnIndex);
                if (asBigInt.compareTo((BigInteger)new BigInteger((String)String.valueOf((long)Long.MAX_VALUE))) <= 0) {
                    if (asBigInt.compareTo((BigInteger)new BigInteger((String)String.valueOf((long)Long.MIN_VALUE))) >= 0) return this.getLongFromString((String)asBigInt.toString(), (int)columnIndex);
                }
                this.throwRangeException((String)asBigInt.toString(), (int)(columnIndex + 1), (int)-5);
                return this.getLongFromString((String)asBigInt.toString(), (int)columnIndex);
            }
            case 5: {
                double valueAsDouble = this.getNativeDouble((int)(columnIndex + 1));
                if (!overflowCheck) return (long)valueAsDouble;
                if (!this.jdbcCompliantTruncationForReads) return (long)valueAsDouble;
                if (!(valueAsDouble < -9.223372036854776E18)) {
                    if (!(valueAsDouble > 9.223372036854776E18)) return (long)valueAsDouble;
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)-5);
                return (long)valueAsDouble;
            }
            case 4: {
                double valueAsDouble = (double)this.getNativeFloat((int)(columnIndex + 1));
                if (!overflowCheck) return (long)valueAsDouble;
                if (!this.jdbcCompliantTruncationForReads) return (long)valueAsDouble;
                if (!(valueAsDouble < -9.223372036854776E18)) {
                    if (!(valueAsDouble > 9.223372036854776E18)) return (long)valueAsDouble;
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)-5);
                return (long)valueAsDouble;
            }
        }
        String stringVal = this.getNativeString((int)(columnIndex + 1));
        if (!this.useUsageAdvisor) return this.getLongFromString((String)stringVal, (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getLong()", (int)columnIndex, (Object)stringVal, (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getLongFromString((String)stringVal, (int)(columnIndex + 1));
    }

    protected Ref getNativeRef(int i) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    protected short getNativeShort(int columnIndex) throws SQLException {
        return this.getNativeShort((int)columnIndex, (boolean)true);
    }

    protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.thisRow.isNull((int)(--columnIndex))) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex];
        switch (f.getMysqlType()) {
            case 16: {
                long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)(columnIndex + 1));
                if (!overflowCheck) return (short)((int)valueAsLong);
                if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsLong);
                if (valueAsLong >= -32768L) {
                    if (valueAsLong <= 32767L) return (short)((int)valueAsLong);
                }
                this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)5);
                return (short)((int)valueAsLong);
            }
            case 1: {
                byte tinyintVal = this.getNativeByte((int)(columnIndex + 1), (boolean)false);
                if (!f.isUnsigned()) return (short)tinyintVal;
                if (tinyintVal < 0) return (short)(tinyintVal + 256);
                return (short)tinyintVal;
            }
            case 2: 
            case 13: {
                short asShort = this.thisRow.getNativeShort((int)columnIndex);
                if (!f.isUnsigned()) {
                    return asShort;
                }
                int valueAsInt = asShort & 65535;
                if (!overflowCheck) return (short)valueAsInt;
                if (!this.jdbcCompliantTruncationForReads) return (short)valueAsInt;
                if (valueAsInt <= 32767) return (short)valueAsInt;
                this.throwRangeException((String)String.valueOf((int)valueAsInt), (int)(columnIndex + 1), (int)5);
                return (short)valueAsInt;
            }
            case 3: 
            case 9: {
                if (f.isUnsigned()) {
                    long valueAsLong = this.getNativeLong((int)(columnIndex + 1), (boolean)false, (boolean)true);
                    if (!overflowCheck) return (short)((int)valueAsLong);
                    if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsLong);
                    if (valueAsLong <= 32767L) return (short)((int)valueAsLong);
                    this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)5);
                    return (short)((int)valueAsLong);
                }
                int valueAsInt = this.getNativeInt((int)(columnIndex + 1), (boolean)false);
                if (!overflowCheck || !this.jdbcCompliantTruncationForReads || valueAsInt <= 32767) {
                    if (valueAsInt >= -32768) return (short)valueAsInt;
                }
                this.throwRangeException((String)String.valueOf((int)valueAsInt), (int)(columnIndex + 1), (int)5);
                return (short)valueAsInt;
            }
            case 8: {
                long valueAsLong = this.getNativeLong((int)(columnIndex + 1), (boolean)false, (boolean)false);
                if (!f.isUnsigned()) {
                    if (!overflowCheck) return (short)((int)valueAsLong);
                    if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsLong);
                    if (valueAsLong >= -32768L) {
                        if (valueAsLong <= 32767L) return (short)((int)valueAsLong);
                    }
                    this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)(columnIndex + 1), (int)5);
                    return (short)((int)valueAsLong);
                }
                BigInteger asBigInt = ResultSetImpl.convertLongToUlong((long)valueAsLong);
                if (!overflowCheck) return (short)this.getIntFromString((String)asBigInt.toString(), (int)(columnIndex + 1));
                if (!this.jdbcCompliantTruncationForReads) return (short)this.getIntFromString((String)asBigInt.toString(), (int)(columnIndex + 1));
                if (asBigInt.compareTo((BigInteger)new BigInteger((String)String.valueOf((int)32767))) <= 0) {
                    if (asBigInt.compareTo((BigInteger)new BigInteger((String)String.valueOf((int)-32768))) >= 0) return (short)this.getIntFromString((String)asBigInt.toString(), (int)(columnIndex + 1));
                }
                this.throwRangeException((String)asBigInt.toString(), (int)(columnIndex + 1), (int)5);
                return (short)this.getIntFromString((String)asBigInt.toString(), (int)(columnIndex + 1));
            }
            case 5: {
                double valueAsDouble = this.getNativeDouble((int)(columnIndex + 1));
                if (!overflowCheck) return (short)((int)valueAsDouble);
                if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsDouble);
                if (!(valueAsDouble < -32768.0)) {
                    if (!(valueAsDouble > 32767.0)) return (short)((int)valueAsDouble);
                }
                this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)(columnIndex + 1), (int)5);
                return (short)((int)valueAsDouble);
            }
            case 4: {
                float valueAsFloat = this.getNativeFloat((int)(columnIndex + 1));
                if (!overflowCheck) return (short)((int)valueAsFloat);
                if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsFloat);
                if (!(valueAsFloat < -32768.0f)) {
                    if (!(valueAsFloat > 32767.0f)) return (short)((int)valueAsFloat);
                }
                this.throwRangeException((String)String.valueOf((float)valueAsFloat), (int)(columnIndex + 1), (int)5);
                return (short)((int)valueAsFloat);
            }
        }
        String stringVal = this.getNativeString((int)(columnIndex + 1));
        if (!this.useUsageAdvisor) return this.getShortFromString((String)stringVal, (int)(columnIndex + 1));
        this.issueConversionViaParsingWarning((String)"getShort()", (int)columnIndex, (Object)stringVal, (Field)this.fields[columnIndex], (int[])new int[]{5, 1, 2, 3, 8, 4});
        return this.getShortFromString((String)stringVal, (int)(columnIndex + 1));
    }

    protected String getNativeString(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.fields == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Query_generated_no_fields_for_ResultSet_133"), (String)"S1002", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.thisRow.isNull((int)(columnIndex - 1))) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        String stringVal = null;
        Field field = this.fields[columnIndex - 1];
        stringVal = this.getNativeConvertToString((int)columnIndex, (Field)field);
        int mysqlType = field.getMysqlType();
        if (mysqlType == 7) return stringVal;
        if (mysqlType == 10) return stringVal;
        if (!field.isZeroFill()) return stringVal;
        if (stringVal == null) return stringVal;
        int origLength = stringVal.length();
        StringBuilder zeroFillBuf = new StringBuilder((int)origLength);
        long numZeros = field.getLength() - (long)origLength;
        long i = 0L;
        do {
            if (i >= numZeros) {
                zeroFillBuf.append((String)stringVal);
                return zeroFillBuf.toString();
            }
            zeroFillBuf.append((char)'0');
            ++i;
        } while (true);
    }

    private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        Time timeVal = null;
        timeVal = mysqlType == 11 ? this.thisRow.getNativeTime((int)columnIndexMinusOne, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this) : (Time)this.thisRow.getNativeDateTimeValue((int)columnIndexMinusOne, null, (int)92, (int)mysqlType, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this);
        if (timeVal == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return timeVal;
    }

    Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (this.useUsageAdvisor) {
            this.issueConversionViaParsingWarning((String)"getTime()", (int)columnIndex, (Object)this.thisRow.getColumnValue((int)(columnIndex - 1)), (Field)this.fields[columnIndex - 1], (int[])new int[]{11});
        }
        String strTime = this.getNativeString((int)columnIndex);
        return this.getTimeFromString((String)strTime, (Calendar)targetCalendar, (int)columnIndex, (TimeZone)tz, (boolean)rollForward);
    }

    /*
     * Unable to fully structure code
     */
    private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        columnIndexMinusOne = columnIndex - 1;
        tsVal = null;
        mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        switch (mysqlType) {
            case 7: 
            case 12: {
                tsVal = this.thisRow.getNativeTimestamp((int)columnIndexMinusOne, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this);
                ** break;
            }
        }
        tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue((int)columnIndexMinusOne, null, (int)93, (int)mysqlType, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this);
lbl11: // 2 sources:
        if (tsVal == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return tsVal;
    }

    Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (this.useUsageAdvisor) {
            this.issueConversionViaParsingWarning((String)"getTimestamp()", (int)columnIndex, (Object)this.thisRow.getColumnValue((int)(columnIndex - 1)), (Field)this.fields[columnIndex - 1], (int[])new int[]{7, 12});
        }
        String strTimestamp = this.getNativeString((int)columnIndex);
        return this.getTimestampFromString((int)columnIndex, (Calendar)targetCalendar, (String)strTimestamp, (TimeZone)tz, (boolean)rollForward);
    }

    protected InputStream getNativeUnicodeStream(int columnIndex) throws SQLException {
        this.checkRowPos();
        return this.getBinaryStream((int)columnIndex);
    }

    protected URL getNativeURL(int colIndex) throws SQLException {
        String val = this.getString((int)colIndex);
        if (val == null) {
            return null;
        }
        try {
            return new URL((String)val);
        }
        catch (MalformedURLException mfe) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Malformed_URL____141") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public synchronized ResultSetInternalMethods getNextResultSet() {
        return this.nextResultSet;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        Field field = this.fields[columnIndexMinusOne];
        switch (field.getSQLType()) {
            case -7: {
                if (field.getMysqlType() != 16) return Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
                if (field.isSingleBit()) return Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
                return this.getObjectDeserializingIfNeeded((int)columnIndex);
            }
            case 16: {
                return Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
            }
            case -6: {
                if (field.isUnsigned()) return Integer.valueOf((int)this.getInt((int)columnIndex));
                return Integer.valueOf((int)this.getByte((int)columnIndex));
            }
            case 5: {
                return Integer.valueOf((int)this.getInt((int)columnIndex));
            }
            case 4: {
                if (!field.isUnsigned()) return Integer.valueOf((int)this.getInt((int)columnIndex));
                if (field.getMysqlType() != 9) return Long.valueOf((long)this.getLong((int)columnIndex));
                return Integer.valueOf((int)this.getInt((int)columnIndex));
            }
            case -5: {
                if (!field.isUnsigned()) {
                    return Long.valueOf((long)this.getLong((int)columnIndex));
                }
                String stringVal = this.getString((int)columnIndex);
                if (stringVal == null) {
                    return null;
                }
                try {
                    return new BigInteger((String)stringVal);
                }
                catch (NumberFormatException nfe) {
                    throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigInteger", (Object[])new Object[]{Integer.valueOf((int)columnIndex), stringVal}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            case 2: 
            case 3: {
                String stringVal = this.getString((int)columnIndex);
                if (stringVal == null) return null;
                if (stringVal.length() == 0) {
                    return new BigDecimal((int)0);
                }
                try {
                    return new BigDecimal((String)stringVal);
                }
                catch (NumberFormatException ex) {
                    throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            case 7: {
                return new Float((float)this.getFloat((int)columnIndex));
            }
            case 6: 
            case 8: {
                return new Double((double)this.getDouble((int)columnIndex));
            }
            case 1: 
            case 12: {
                if (field.isOpaqueBinary()) return this.getBytes((int)columnIndex);
                return this.getString((int)columnIndex);
            }
            case -1: {
                if (field.isOpaqueBinary()) return this.getBytes((int)columnIndex);
                return this.getStringForClob((int)columnIndex);
            }
            case -4: 
            case -3: 
            case -2: {
                if (field.getMysqlType() != 255) return this.getObjectDeserializingIfNeeded((int)columnIndex);
                return this.getBytes((int)columnIndex);
            }
            case 91: {
                if (field.getMysqlType() != 13) return this.getDate((int)columnIndex);
                if (this.connection.getYearIsDateType()) return this.getDate((int)columnIndex);
                return Short.valueOf((short)this.getShort((int)columnIndex));
            }
            case 92: {
                return this.getTime((int)columnIndex);
            }
            case 93: {
                return this.getTimestamp((int)columnIndex);
            }
        }
        return this.getString((int)columnIndex);
    }

    private Object getObjectDeserializingIfNeeded(int columnIndex) throws SQLException {
        Field field = this.fields[columnIndex - 1];
        if (!field.isBinary()) {
            if (!field.isBlob()) return this.getBytes((int)columnIndex);
        }
        byte[] data = this.getBytes((int)columnIndex);
        if (!this.connection.getAutoDeserialize()) return data;
        Object obj = data;
        if (data == null) return obj;
        if (data.length < 2) return obj;
        if (data[0] != -84) return this.getString((int)columnIndex);
        if (data[1] != -19) return this.getString((int)columnIndex);
        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream((byte[])data);
            ObjectInputStream objIn = new ObjectInputStream((InputStream)bytesIn);
            obj = objIn.readObject();
            objIn.close();
            bytesIn.close();
            return obj;
        }
        catch (ClassNotFoundException cnfe) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString((String)"ResultSet._while_reading_serialized_object_92")), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        catch (IOException ex) {
            return data;
        }
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (type == null) {
            throw SQLError.createSQLException((String)"Type parameter can not be null", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (type.equals(String.class)) {
            return (T)this.getString((int)columnIndex);
        }
        if (type.equals(BigDecimal.class)) {
            return (T)this.getBigDecimal((int)columnIndex);
        }
        if (type.equals(Boolean.class)) return (T)Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
        if (type.equals(Boolean.TYPE)) {
            return (T)Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
        }
        if (type.equals(Integer.class)) return (T)Integer.valueOf((int)this.getInt((int)columnIndex));
        if (type.equals(Integer.TYPE)) {
            return (T)Integer.valueOf((int)this.getInt((int)columnIndex));
        }
        if (type.equals(Long.class)) return (T)Long.valueOf((long)this.getLong((int)columnIndex));
        if (type.equals(Long.TYPE)) {
            return (T)Long.valueOf((long)this.getLong((int)columnIndex));
        }
        if (type.equals(Float.class)) return (T)Float.valueOf((float)this.getFloat((int)columnIndex));
        if (type.equals(Float.TYPE)) {
            return (T)Float.valueOf((float)this.getFloat((int)columnIndex));
        }
        if (type.equals(Double.class)) return (T)Double.valueOf((double)this.getDouble((int)columnIndex));
        if (type.equals(Double.TYPE)) {
            return (T)Double.valueOf((double)this.getDouble((int)columnIndex));
        }
        if (type.equals(byte[].class)) {
            return (T)this.getBytes((int)columnIndex);
        }
        if (type.equals(Date.class)) {
            return (T)this.getDate((int)columnIndex);
        }
        if (type.equals(Time.class)) {
            return (T)this.getTime((int)columnIndex);
        }
        if (type.equals(Timestamp.class)) {
            return (T)this.getTimestamp((int)columnIndex);
        }
        if (type.equals(Clob.class)) {
            return (T)this.getClob((int)columnIndex);
        }
        if (type.equals(Blob.class)) {
            return (T)this.getBlob((int)columnIndex);
        }
        if (type.equals(Array.class)) {
            return (T)this.getArray((int)columnIndex);
        }
        if (type.equals(Ref.class)) {
            return (T)this.getRef((int)columnIndex);
        }
        if (type.equals(URL.class)) {
            return (T)this.getURL((int)columnIndex);
        }
        if (!this.connection.getAutoDeserialize()) throw SQLError.createSQLException((String)("Conversion not supported for type " + type.getName()), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        try {
            return (T)type.cast((Object)this.getObject((int)columnIndex));
        }
        catch (ClassCastException cce) {
            SQLException sqlEx = SQLError.createSQLException((String)("Conversion not supported for type " + type.getName()), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)cce);
            throw sqlEx;
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return (T)this.getObject((int)this.findColumn((String)columnLabel), type);
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return this.getObject((int)i);
    }

    @Override
    public Object getObject(String columnName) throws SQLException {
        return this.getObject((int)this.findColumn((String)columnName));
    }

    @Override
    public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
        return this.getObject((int)this.findColumn((String)colName), map);
    }

    @Override
    public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        byte[] value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        if (value == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        Field field = this.fields[columnIndex - 1];
        switch (desiredSqlType) {
            case -7: 
            case 16: {
                return Boolean.valueOf((boolean)this.getBoolean((int)columnIndex));
            }
            case -6: {
                return Integer.valueOf((int)this.getInt((int)columnIndex));
            }
            case 5: {
                return Integer.valueOf((int)this.getInt((int)columnIndex));
            }
            case 4: {
                if (!field.isUnsigned()) return Integer.valueOf((int)this.getInt((int)columnIndex));
                if (field.getMysqlType() != 9) return Long.valueOf((long)this.getLong((int)columnIndex));
                return Integer.valueOf((int)this.getInt((int)columnIndex));
            }
            case -5: {
                if (!field.isUnsigned()) return Long.valueOf((long)this.getLong((int)columnIndex));
                return this.getBigDecimal((int)columnIndex);
            }
            case 2: 
            case 3: {
                String stringVal = this.getString((int)columnIndex);
                if (stringVal == null) return null;
                if (stringVal.length() == 0) {
                    return new BigDecimal((int)0);
                }
                try {
                    return new BigDecimal((String)stringVal);
                }
                catch (NumberFormatException ex) {
                    throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Bad_format_for_BigDecimal", (Object[])new Object[]{stringVal, Integer.valueOf((int)columnIndex)}), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
            }
            case 7: {
                return new Float((float)this.getFloat((int)columnIndex));
            }
            case 6: {
                if (this.connection.getRunningCTS13()) return new Float((float)this.getFloat((int)columnIndex));
                return new Double((double)((double)this.getFloat((int)columnIndex)));
            }
            case 8: {
                return new Double((double)this.getDouble((int)columnIndex));
            }
            case 1: 
            case 12: {
                return this.getString((int)columnIndex);
            }
            case -1: {
                return this.getStringForClob((int)columnIndex);
            }
            case -4: 
            case -3: 
            case -2: {
                return this.getBytes((int)columnIndex);
            }
            case 91: {
                if (field.getMysqlType() != 13) return this.getDate((int)columnIndex);
                if (this.connection.getYearIsDateType()) return this.getDate((int)columnIndex);
                return Short.valueOf((short)this.getShort((int)columnIndex));
            }
            case 92: {
                return this.getTime((int)columnIndex);
            }
            case 93: {
                return this.getTimestamp((int)columnIndex);
            }
        }
        return this.getString((int)columnIndex);
    }

    @Override
    public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException {
        return this.getObjectStoredProc((int)i, (int)desiredSqlType);
    }

    @Override
    public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
        return this.getObjectStoredProc((int)this.findColumn((String)columnName), (int)desiredSqlType);
    }

    @Override
    public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
        return this.getObjectStoredProc((int)this.findColumn((String)colName), map, (int)desiredSqlType);
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        this.checkColumnBounds((int)i);
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public Ref getRef(String colName) throws SQLException {
        return this.getRef((int)this.findColumn((String)colName));
    }

    @Override
    public int getRow() throws SQLException {
        this.checkClosed();
        int currentRowNumber = this.rowData.getCurrentRowNumber();
        int row = 0;
        if (this.rowData.isDynamic()) {
            return currentRowNumber + 1;
        }
        if (currentRowNumber < 0) return 0;
        if (this.rowData.isAfterLast()) return 0;
        if (this.rowData.isEmpty()) return 0;
        return currentRowNumber + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getServerInfo() {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            // MONITOREXIT : object
            return this.serverInfo;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    private long getNumericRepresentationOfSQLBitType(int columnIndex) throws SQLException {
        byte[] value = this.thisRow.getColumnValue((int)(columnIndex - 1));
        if (this.fields[columnIndex - 1].isSingleBit()) return (long)value[0];
        if (value.length == 1) {
            return (long)value[0];
        }
        byte[] asBytes = value;
        int shift = 0;
        long[] steps = new long[asBytes.length];
        for (int i = asBytes.length - 1; i >= 0; shift += 8, --i) {
            steps[i] = (long)(asBytes[i] & 255) << shift;
        }
        long valueAsLong = 0L;
        int i = 0;
        while (i < asBytes.length) {
            valueAsLong |= steps[i];
            ++i;
        }
        return valueAsLong;
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.isBinaryEncoded) return this.getNativeShort((int)columnIndex);
        if (this.thisRow.isNull((int)(columnIndex - 1))) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndex - 1].getMysqlType() == 16) {
            long valueAsLong = this.getNumericRepresentationOfSQLBitType((int)columnIndex);
            if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsLong);
            if (valueAsLong >= -32768L) {
                if (valueAsLong <= 32767L) return (short)((int)valueAsLong);
            }
            this.throwRangeException((String)String.valueOf((long)valueAsLong), (int)columnIndex, (int)5);
            return (short)((int)valueAsLong);
        }
        if (this.useFastIntParsing) {
            byte[] shortAsBytes = this.thisRow.getColumnValue((int)(columnIndex - 1));
            if (shortAsBytes.length == 0) {
                return (short)this.convertToZeroWithEmptyCheck();
            }
            boolean needsFullParse = false;
            for (int i = 0; i < shortAsBytes.length; ++i) {
                if ((char)shortAsBytes[i] != 'e' && (char)shortAsBytes[i] != 'E') continue;
                needsFullParse = true;
                break;
            }
            if (!needsFullParse) {
                try {
                    return this.parseShortWithOverflowCheck((int)columnIndex, (byte[])shortAsBytes, null);
                }
                catch (NumberFormatException nfe) {
                    try {
                        return this.parseShortAsDouble((int)columnIndex, (String)StringUtils.toString((byte[])shortAsBytes));
                    }
                    catch (NumberFormatException newNfe) {
                        throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getShort()_-____96") + StringUtils.toString((byte[])shortAsBytes) + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                    }
                }
            }
        }
        String val = null;
        try {
            val = this.getString((int)columnIndex);
            if (val == null) {
                return 0;
            }
            if (val.length() == 0) {
                return (short)this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            if (val.indexOf((String)"E") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            if (val.indexOf((String)".") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            return this.parseShortWithOverflowCheck((int)columnIndex, null, (String)val);
        }
        catch (NumberFormatException nfe) {
            try {
                return this.parseShortAsDouble((int)columnIndex, (String)val);
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getShort()_-____96") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    @Override
    public short getShort(String columnName) throws SQLException {
        return this.getShort((int)this.findColumn((String)columnName));
    }

    private final short getShortFromString(String val, int columnIndex) throws SQLException {
        try {
            if (val == null) return 0;
            if (val.length() == 0) {
                return (short)this.convertToZeroWithEmptyCheck();
            }
            if (val.indexOf((String)"e") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            if (val.indexOf((String)"E") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            if (val.indexOf((String)".") != -1) return this.parseShortAsDouble((int)columnIndex, (String)val);
            return this.parseShortWithOverflowCheck((int)columnIndex, null, (String)val);
        }
        catch (NumberFormatException nfe) {
            try {
                return this.parseShortAsDouble((int)columnIndex, (String)val);
            }
            catch (NumberFormatException newNfe) {
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString((String)"ResultSet.___in_column__218") + columnIndex), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.Statement getStatement() throws SQLException {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            if (this.wrapperStatement != null) {
                // MONITOREXIT : object
                return this.wrapperStatement;
            }
            // MONITOREXIT : object
            return this.owningStatement;
        }
        catch (SQLException sqlEx) {
            if (!this.retainOwningStatement) {
                throw SQLError.createSQLException((String)"Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            if (this.wrapperStatement == null) return this.owningStatement;
            return this.wrapperStatement;
        }
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        String stringVal = this.getStringInternal((int)columnIndex, (boolean)true);
        if (!this.padCharsWithSpace) return stringVal;
        if (stringVal == null) return stringVal;
        Field f = this.fields[columnIndex - 1];
        if (f.getMysqlType() != 254) return stringVal;
        int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
        int currentLength = stringVal.length();
        if (currentLength >= fieldLength) return stringVal;
        StringBuilder paddedBuf = new StringBuilder((int)fieldLength);
        paddedBuf.append((String)stringVal);
        int difference = fieldLength - currentLength;
        paddedBuf.append((char[])EMPTY_SPACE, (int)0, (int)difference);
        return paddedBuf.toString();
    }

    @Override
    public String getString(String columnName) throws SQLException {
        return this.getString((int)this.findColumn((String)columnName));
    }

    private String getStringForClob(int columnIndex) throws SQLException {
        String asString = null;
        String forcedEncoding = this.connection.getClobCharacterEncoding();
        if (forcedEncoding == null) {
            if (this.isBinaryEncoded) return this.getNativeString((int)columnIndex);
            return this.getString((int)columnIndex);
        }
        try {
            byte[] asBytes = null;
            asBytes = !this.isBinaryEncoded ? this.getBytes((int)columnIndex) : this.getNativeBytes((int)columnIndex, (boolean)true);
            if (asBytes == null) return asString;
            return StringUtils.toString((byte[])asBytes, (String)forcedEncoding);
        }
        catch (UnsupportedEncodingException uee) {
            throw SQLError.createSQLException((String)("Unsupported character encoding " + forcedEncoding), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeString((int)columnIndex);
        this.checkRowPos();
        this.checkColumnBounds((int)columnIndex);
        if (this.fields == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Query_generated_no_fields_for_ResultSet_99"), (String)"S1002", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        int internalColumnIndex = columnIndex - 1;
        if (this.thisRow.isNull((int)internalColumnIndex)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        Field metadata = this.fields[internalColumnIndex];
        String stringVal = null;
        if (metadata.getMysqlType() == 16) {
            if (!metadata.isSingleBit()) return String.valueOf((long)this.getNumericRepresentationOfSQLBitType((int)columnIndex));
            byte[] value = this.thisRow.getColumnValue((int)internalColumnIndex);
            if (value.length != 0) return String.valueOf((int)value[0]);
            return String.valueOf((int)this.convertToZeroWithEmptyCheck());
        }
        String encoding = metadata.getCollationIndex() == 63 ? this.connection.getEncoding() : metadata.getEncoding();
        stringVal = this.thisRow.getString((int)internalColumnIndex, (String)encoding, (MySQLConnection)this.connection);
        if (metadata.getMysqlType() == 13) {
            if (!this.connection.getYearIsDateType()) {
                return stringVal;
            }
            Date dt = this.getDateFromString((String)stringVal, (int)columnIndex, null);
            if (dt == null) {
                this.wasNullFlag = true;
                return null;
            }
            this.wasNullFlag = false;
            return dt.toString();
        }
        if (!checkDateTypes) return stringVal;
        if (this.connection.getNoDatetimeStringSync()) return stringVal;
        switch (metadata.getSQLType()) {
            case 92: {
                Time tm = this.getTimeFromString((String)stringVal, null, (int)columnIndex, (TimeZone)this.getDefaultTimeZone(), (boolean)false);
                if (tm == null) {
                    this.wasNullFlag = true;
                    return null;
                }
                this.wasNullFlag = false;
                return tm.toString();
            }
            case 91: {
                Date dt = this.getDateFromString((String)stringVal, (int)columnIndex, null);
                if (dt == null) {
                    this.wasNullFlag = true;
                    return null;
                }
                this.wasNullFlag = false;
                return dt.toString();
            }
            case 93: {
                Timestamp ts = this.getTimestampFromString((int)columnIndex, null, (String)stringVal, (TimeZone)this.getDefaultTimeZone(), (boolean)false);
                if (ts == null) {
                    this.wasNullFlag = true;
                    return null;
                }
                this.wasNullFlag = false;
                return ts.toString();
            }
        }
        return stringVal;
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return this.getTimeInternal((int)columnIndex, null, (TimeZone)this.getDefaultTimeZone(), (boolean)false);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        TimeZone timeZone;
        if (cal != null) {
            timeZone = cal.getTimeZone();
            return this.getTimeInternal((int)columnIndex, (Calendar)cal, (TimeZone)timeZone, (boolean)true);
        }
        timeZone = this.getDefaultTimeZone();
        return this.getTimeInternal((int)columnIndex, (Calendar)cal, (TimeZone)timeZone, (boolean)true);
    }

    @Override
    public Time getTime(String columnName) throws SQLException {
        return this.getTime((int)this.findColumn((String)columnName));
    }

    @Override
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return this.getTime((int)this.findColumn((String)columnName), (Calendar)cal);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException {
        var6_6 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var6_6
        hr = 0;
        min = 0;
        sec = 0;
        try {
            if (timeAsString == null) {
                this.wasNullFlag = true;
                // MONITOREXIT : var6_6
                return null;
            }
            dec = (timeAsString = timeAsString.trim()).indexOf((String)".");
            if (dec > -1) {
                timeAsString = timeAsString.substring((int)0, (int)dec);
            }
            if (timeAsString.equals((Object)"0") || timeAsString.equals((Object)"0000-00-00") || timeAsString.equals((Object)"0000-00-00 00:00:00") || timeAsString.equals((Object)"00000000000000")) {
                if ("convertToNull".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                    this.wasNullFlag = true;
                    // MONITOREXIT : var6_6
                    return null;
                }
                if ("exception".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                    throw SQLError.createSQLException((String)("Value '" + timeAsString + "' can not be represented as java.sql.Time"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                // MONITOREXIT : var6_6
                return this.fastTimeCreate((Calendar)targetCalendar, (int)0, (int)0, (int)0);
            }
            this.wasNullFlag = false;
            timeColField = this.fields[columnIndex - 1];
            if (timeColField.getMysqlType() == 7) {
                length = timeAsString.length();
                switch (length) {
                    case 19: {
                        hr = Integer.parseInt((String)timeAsString.substring((int)(length - 8), (int)(length - 6)));
                        min = Integer.parseInt((String)timeAsString.substring((int)(length - 5), (int)(length - 3)));
                        sec = Integer.parseInt((String)timeAsString.substring((int)(length - 2), (int)length));
                        ** break;
                    }
                    case 12: 
                    case 14: {
                        hr = Integer.parseInt((String)timeAsString.substring((int)(length - 6), (int)(length - 4)));
                        min = Integer.parseInt((String)timeAsString.substring((int)(length - 4), (int)(length - 2)));
                        sec = Integer.parseInt((String)timeAsString.substring((int)(length - 2), (int)length));
                        ** break;
                    }
                    case 10: {
                        hr = Integer.parseInt((String)timeAsString.substring((int)6, (int)8));
                        min = Integer.parseInt((String)timeAsString.substring((int)8, (int)10));
                        sec = 0;
                        ** break;
                    }
                }
                throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[columnIndex - 1] + ")."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
lbl44: // 3 sources:
                precisionLost = new SQLWarning((String)(Messages.getString((String)"ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[columnIndex - 1] + ")."));
                if (this.warningChain == null) {
                    this.warningChain = precisionLost;
                } else {
                    this.warningChain.setNextWarning((SQLWarning)precisionLost);
                }
            } else if (timeColField.getMysqlType() == 12) {
                hr = Integer.parseInt((String)timeAsString.substring((int)11, (int)13));
                min = Integer.parseInt((String)timeAsString.substring((int)14, (int)16));
                sec = Integer.parseInt((String)timeAsString.substring((int)17, (int)19));
                precisionLost = new SQLWarning((String)(Messages.getString((String)"ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[columnIndex - 1] + ")."));
                if (this.warningChain == null) {
                    this.warningChain = precisionLost;
                } else {
                    this.warningChain.setNextWarning((SQLWarning)precisionLost);
                }
            } else {
                if (timeColField.getMysqlType() == 10) {
                    // MONITOREXIT : var6_6
                    return this.fastTimeCreate((Calendar)targetCalendar, (int)0, (int)0, (int)0);
                }
                if (timeAsString.length() != 5 && timeAsString.length() != 8) {
                    throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString((String)"ResultSet.___in_column__268") + columnIndex), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                }
                hr = Integer.parseInt((String)timeAsString.substring((int)0, (int)2));
                min = Integer.parseInt((String)timeAsString.substring((int)3, (int)5));
                sec = timeAsString.length() == 5 ? 0 : Integer.parseInt((String)timeAsString.substring((int)6));
            }
            sessionCalendar = this.getCalendarInstanceForSessionOrNew();
            // MONITOREXIT : var6_6
            return TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Time)this.fastTimeCreate((Calendar)sessionCalendar, (int)hr, (int)min, (int)sec), (TimeZone)this.connection.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
        }
        catch (RuntimeException ex) {
            sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        this.checkRowPos();
        if (this.isBinaryEncoded) {
            return this.getNativeTime((int)columnIndex, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward);
        }
        if (!this.useFastDateParsing) {
            String timeAsString = this.getStringInternal((int)columnIndex, (boolean)false);
            return this.getTimeFromString((String)timeAsString, (Calendar)targetCalendar, (int)columnIndex, (TimeZone)tz, (boolean)rollForward);
        }
        this.checkColumnBounds((int)columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull((int)columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getTimeFast((int)columnIndexMinusOne, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return this.getTimestampInternal((int)columnIndex, null, (TimeZone)this.getDefaultTimeZone(), (boolean)false);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        TimeZone timeZone;
        if (cal != null) {
            timeZone = cal.getTimeZone();
            return this.getTimestampInternal((int)columnIndex, (Calendar)cal, (TimeZone)timeZone, (boolean)true);
        }
        timeZone = this.getDefaultTimeZone();
        return this.getTimestampInternal((int)columnIndex, (Calendar)cal, (TimeZone)timeZone, (boolean)true);
    }

    @Override
    public Timestamp getTimestamp(String columnName) throws SQLException {
        return this.getTimestamp((int)this.findColumn((String)columnName));
    }

    @Override
    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return this.getTimestamp((int)this.findColumn((String)columnName), (Calendar)cal);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException {
        try {
            this.wasNullFlag = false;
            if (timestampValue == null) {
                this.wasNullFlag = true;
                return null;
            }
            timestampValue = timestampValue.trim();
            length = timestampValue.length();
            sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() != false ? this.connection.getUtcCalendar() : this.getCalendarInstanceForSessionOrNew();
            useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
            if (length > 0 && timestampValue.charAt((int)0) == '0' && (timestampValue.equals((Object)"0000-00-00") || timestampValue.equals((Object)"0000-00-00 00:00:00") || timestampValue.equals((Object)"00000000000000") || timestampValue.equals((Object)"0"))) {
                if ("convertToNull".equals((Object)this.connection.getZeroDateTimeBehavior())) {
                    this.wasNullFlag = true;
                    return null;
                }
                if ("exception".equals((Object)this.connection.getZeroDateTimeBehavior()) == false) return this.fastTimestampCreate(null, (int)1, (int)1, (int)1, (int)0, (int)0, (int)0, (int)0, (boolean)useGmtMillis);
                throw SQLError.createSQLException((String)("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            if (this.fields[columnIndex - 1].getMysqlType() == 13) {
                if (this.useLegacyDatetimeCode != false) return TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)this.fastTimestampCreate((Calendar)sessionCalendar, (int)Integer.parseInt((String)timestampValue.substring((int)0, (int)4)), (int)1, (int)1, (int)0, (int)0, (int)0, (int)0, (boolean)useGmtMillis), (TimeZone)this.connection.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
                return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)Integer.parseInt((String)timestampValue.substring((int)0, (int)4)), (int)1, (int)1, (int)0, (int)0, (int)0, (int)0);
            }
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minutes = 0;
            seconds = 0;
            nanos = 0;
            decimalIndex = timestampValue.indexOf((String)".");
            if (decimalIndex == length - 1) {
                --length;
            } else if (decimalIndex != -1) {
                if (decimalIndex + 2 > length) throw new IllegalArgumentException();
                nanos = Integer.parseInt((String)timestampValue.substring((int)(decimalIndex + 1)));
                numDigits = length - (decimalIndex + 1);
                if (numDigits < 9) {
                    factor = (int)Math.pow((double)10.0, (double)((double)(9 - numDigits)));
                    nanos *= factor;
                }
                length = decimalIndex;
            }
            switch (length) {
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)4));
                    month = Integer.parseInt((String)timestampValue.substring((int)5, (int)7));
                    day = Integer.parseInt((String)timestampValue.substring((int)8, (int)10));
                    hour = Integer.parseInt((String)timestampValue.substring((int)11, (int)13));
                    minutes = Integer.parseInt((String)timestampValue.substring((int)14, (int)16));
                    seconds = Integer.parseInt((String)timestampValue.substring((int)17, (int)19));
                    ** break;
                }
                case 14: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)4));
                    month = Integer.parseInt((String)timestampValue.substring((int)4, (int)6));
                    day = Integer.parseInt((String)timestampValue.substring((int)6, (int)8));
                    hour = Integer.parseInt((String)timestampValue.substring((int)8, (int)10));
                    minutes = Integer.parseInt((String)timestampValue.substring((int)10, (int)12));
                    seconds = Integer.parseInt((String)timestampValue.substring((int)12, (int)14));
                    ** break;
                }
                case 12: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = Integer.parseInt((String)timestampValue.substring((int)2, (int)4));
                    day = Integer.parseInt((String)timestampValue.substring((int)4, (int)6));
                    hour = Integer.parseInt((String)timestampValue.substring((int)6, (int)8));
                    minutes = Integer.parseInt((String)timestampValue.substring((int)8, (int)10));
                    seconds = Integer.parseInt((String)timestampValue.substring((int)10, (int)12));
                    ** break;
                }
                case 10: {
                    if (this.fields[columnIndex - 1].getMysqlType() == 10 || timestampValue.indexOf((String)"-") != -1) {
                        year = Integer.parseInt((String)timestampValue.substring((int)0, (int)4));
                        month = Integer.parseInt((String)timestampValue.substring((int)5, (int)7));
                        day = Integer.parseInt((String)timestampValue.substring((int)8, (int)10));
                        hour = 0;
                        minutes = 0;
                        ** break;
                    }
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                    if (year <= 69) {
                        year += 100;
                    }
                    month = Integer.parseInt((String)timestampValue.substring((int)2, (int)4));
                    day = Integer.parseInt((String)timestampValue.substring((int)4, (int)6));
                    hour = Integer.parseInt((String)timestampValue.substring((int)6, (int)8));
                    minutes = Integer.parseInt((String)timestampValue.substring((int)8, (int)10));
                    year += 1900;
                    ** break;
                }
                case 8: {
                    if (timestampValue.indexOf((String)":") != -1) {
                        hour = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                        minutes = Integer.parseInt((String)timestampValue.substring((int)3, (int)5));
                        seconds = Integer.parseInt((String)timestampValue.substring((int)6, (int)8));
                        year = 1970;
                        month = 1;
                        day = 1;
                        ** break;
                    }
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)4));
                    month = Integer.parseInt((String)timestampValue.substring((int)4, (int)6));
                    day = Integer.parseInt((String)timestampValue.substring((int)6, (int)8));
                    year -= 1900;
                    --month;
                    ** break;
                }
                case 6: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = Integer.parseInt((String)timestampValue.substring((int)2, (int)4));
                    day = Integer.parseInt((String)timestampValue.substring((int)4, (int)6));
                    ** break;
                }
                case 4: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = Integer.parseInt((String)timestampValue.substring((int)2, (int)4));
                    day = 1;
                    ** break;
                }
                case 2: {
                    year = Integer.parseInt((String)timestampValue.substring((int)0, (int)2));
                    if (year <= 69) {
                        year += 100;
                    }
                    year += 1900;
                    month = 1;
                    day = 1;
                    ** break;
                }
            }
            throw new SQLException((String)("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + "."), (String)"S1009");
lbl123: // 10 sources:
            if (this.useLegacyDatetimeCode != false) return TimeUtil.changeTimezone((MySQLConnection)this.connection, (Calendar)sessionCalendar, (Calendar)targetCalendar, (Timestamp)this.fastTimestampCreate((Calendar)sessionCalendar, (int)year, (int)month, (int)day, (int)hour, (int)minutes, (int)seconds, (int)nanos, (boolean)useGmtMillis), (TimeZone)this.connection.getServerTimezoneTZ(), (TimeZone)tz, (boolean)rollForward);
            return TimeUtil.fastTimestampCreate((TimeZone)tz, (int)year, (int)month, (int)day, (int)hour, (int)minutes, (int)seconds, (int)nanos);
        }
        catch (RuntimeException e) {
            sqlEx = SQLError.createSQLException((String)("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP."), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            sqlEx.initCause((Throwable)e);
            throw sqlEx;
        }
    }

    private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (this.isBinaryEncoded) {
            return this.getNativeTimestamp((int)columnIndex, (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward);
        }
        Timestamp tsVal = null;
        if (!this.useFastDateParsing) {
            String timestampValue = this.getStringInternal((int)columnIndex, (boolean)false);
            tsVal = this.getTimestampFromString((int)columnIndex, (Calendar)targetCalendar, (String)timestampValue, (TimeZone)tz, (boolean)rollForward);
        } else {
            this.checkClosed();
            this.checkRowPos();
            this.checkColumnBounds((int)columnIndex);
            tsVal = this.thisRow.getTimestampFast((int)(columnIndex - 1), (Calendar)targetCalendar, (TimeZone)tz, (boolean)rollForward, (MySQLConnection)this.connection, (ResultSetImpl)this, (boolean)this.connection.getUseGmtMillisForDatetimes(), (boolean)this.connection.getUseJDBCCompliantTimezoneShift());
        }
        if (tsVal == null) {
            this.wasNullFlag = true;
            return tsVal;
        }
        this.wasNullFlag = false;
        return tsVal;
    }

    @Override
    public int getType() throws SQLException {
        return this.resultSetType;
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) return this.getNativeBinaryStream((int)columnIndex);
        this.checkRowPos();
        return this.getBinaryStream((int)columnIndex);
    }

    @Deprecated
    @Override
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return this.getUnicodeStream((int)this.findColumn((String)columnName));
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount;
    }

    @Override
    public long getUpdateID() {
        return this.updateId;
    }

    @Override
    public URL getURL(int colIndex) throws SQLException {
        String val = this.getString((int)colIndex);
        if (val == null) {
            return null;
        }
        try {
            return new URL((String)val);
        }
        catch (MalformedURLException mfe) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Malformed_URL____104") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    @Override
    public URL getURL(String colName) throws SQLException {
        String val = this.getString((String)colName);
        if (val == null) {
            return null;
        }
        try {
            return new URL((String)val);
        }
        catch (MalformedURLException mfe) {
            throw SQLError.createSQLException((String)(Messages.getString((String)"ResultSet.Malformed_URL____107") + val + "'"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.warningChain;
    }

    @Override
    public void insertRow() throws SQLException {
        throw new NotUpdatable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isAfterLast() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        boolean b = this.rowData.isAfterLast();
        // MONITOREXIT : object
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isBeforeFirst() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.rowData.isBeforeFirst();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isFirst() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.rowData.isFirst();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isLast() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.rowData.isLast();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        StringBuilder originalQueryBuf = new StringBuilder();
        if (this.owningStatement != null && this.owningStatement instanceof PreparedStatement) {
            originalQueryBuf.append((String)Messages.getString((String)"ResultSet.CostlyConversionCreatedFromQuery"));
            originalQueryBuf.append((String)((PreparedStatement)this.owningStatement).originalSql);
            originalQueryBuf.append((String)"\n\n");
        } else {
            originalQueryBuf.append((String)".");
        }
        StringBuilder convertibleTypesBuf = new StringBuilder();
        for (int i = 0; i < typesWithNoParseConversion.length; ++i) {
            convertibleTypesBuf.append((String)MysqlDefs.typeToName((int)typesWithNoParseConversion[i]));
            convertibleTypesBuf.append((String)"\n");
        }
        String message = Messages.getString((String)"ResultSet.CostlyConversion", (Object[])new Object[]{methodName, Integer.valueOf((int)(columnIndex + 1)), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), value != null ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType((int)fieldInfo.getSQLType(), (boolean)fieldInfo.isUnsigned(), (int)fieldInfo.getMysqlType(), (boolean)(fieldInfo.isBinary() || fieldInfo.isBlob()), (boolean)fieldInfo.isOpaqueBinary(), (boolean)this.connection.getYearIsDateType()), MysqlDefs.typeToName((int)fieldInfo.getMysqlType()), convertibleTypesBuf.toString()});
        this.connection.getProfilerEventHandlerInstance().processEvent((byte)0, (MySQLConnection)this.connection, (Statement)this.owningStatement, (ResultSetInternalMethods)this, (long)0L, (Throwable)new Throwable(), (String)message);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean last() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        boolean b = true;
        if (this.rowData.size() == 0) {
            b = false;
        } else {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            if (this.thisRow != null) {
                this.thisRow.closeOpenStreams();
            }
            this.rowData.beforeLast();
            this.thisRow = this.rowData.next();
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return b;
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new NotUpdatable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean next() throws SQLException {
        boolean b;
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.onInsertRow = false;
        }
        if (this.doingUpdates) {
            this.doingUpdates = false;
        }
        if (!this.reallyResult()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.thisRow != null) {
            this.thisRow.closeOpenStreams();
        }
        if (this.rowData.size() == 0) {
            b = false;
        } else {
            this.thisRow = this.rowData.next();
            if (this.thisRow == null) {
                b = false;
            } else {
                this.clearWarnings();
                b = true;
            }
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return b;
    }

    private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0;
        }
        double valueAsDouble = Double.parseDouble((String)val);
        if (!this.jdbcCompliantTruncationForReads) return (int)valueAsDouble;
        if (!(valueAsDouble < -2.147483648E9)) {
            if (!(valueAsDouble > 2.147483647E9)) return (int)valueAsDouble;
        }
        this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)columnIndex, (int)4);
        return (int)valueAsDouble;
    }

    private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
        int intValue = this.thisRow.getInt((int)columnIndex);
        this.checkForIntegerTruncation((int)columnIndex, null, (int)intValue);
        return intValue;
    }

    private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue) throws SQLException {
        long valueAsLong;
        if (!this.jdbcCompliantTruncationForReads) return;
        if (intValue != Integer.MIN_VALUE) {
            if (intValue != Integer.MAX_VALUE) return;
        }
        String valueAsString = null;
        if (valueAsBytes == null) {
            valueAsString = this.thisRow.getString((int)columnIndex, (String)this.fields[columnIndex].getEncoding(), (MySQLConnection)this.connection);
        }
        if ((valueAsLong = Long.parseLong((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString))) >= Integer.MIN_VALUE) {
            if (valueAsLong <= Integer.MAX_VALUE) return;
        }
        this.throwRangeException((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString), (int)(columnIndex + 1), (int)4);
    }

    private long parseLongAsDouble(int columnIndexZeroBased, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0L;
        }
        double valueAsDouble = Double.parseDouble((String)val);
        if (!this.jdbcCompliantTruncationForReads) return (long)valueAsDouble;
        if (!(valueAsDouble < -9.223372036854776E18)) {
            if (!(valueAsDouble > 9.223372036854776E18)) return (long)valueAsDouble;
        }
        this.throwRangeException((String)val, (int)(columnIndexZeroBased + 1), (int)-5);
        return (long)valueAsDouble;
    }

    private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
        long longValue = this.thisRow.getLong((int)columnIndexZeroBased);
        if (!doOverflowCheck) return longValue;
        this.checkForLongTruncation((int)columnIndexZeroBased, null, (long)longValue);
        return longValue;
    }

    private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck) throws NumberFormatException, SQLException {
        long longValue = 0L;
        if (valueAsBytes == null && valueAsString == null) {
            return 0L;
        }
        if (valueAsBytes != null) {
            longValue = StringUtils.getLong((byte[])valueAsBytes);
        } else {
            valueAsString = valueAsString.trim();
            longValue = Long.parseLong((String)valueAsString);
        }
        if (!doCheck) return longValue;
        if (!this.jdbcCompliantTruncationForReads) return longValue;
        this.checkForLongTruncation((int)columnIndexZeroBased, (byte[])valueAsBytes, (long)longValue);
        return longValue;
    }

    private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
        double valueAsDouble;
        if (longValue != Long.MIN_VALUE) {
            if (longValue != Long.MAX_VALUE) return;
        }
        String valueAsString = null;
        if (valueAsBytes == null) {
            valueAsString = this.thisRow.getString((int)columnIndexZeroBased, (String)this.fields[columnIndexZeroBased].getEncoding(), (MySQLConnection)this.connection);
        }
        if (!((valueAsDouble = Double.parseDouble((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString))) < -9.223372036854776E18)) {
            if (!(valueAsDouble > 9.223372036854776E18)) return;
        }
        this.throwRangeException((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString), (int)(columnIndexZeroBased + 1), (int)-5);
    }

    private short parseShortAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0;
        }
        double valueAsDouble = Double.parseDouble((String)val);
        if (!this.jdbcCompliantTruncationForReads) return (short)((int)valueAsDouble);
        if (!(valueAsDouble < -32768.0)) {
            if (!(valueAsDouble > 32767.0)) return (short)((int)valueAsDouble);
        }
        this.throwRangeException((String)String.valueOf((double)valueAsDouble), (int)columnIndex, (int)5);
        return (short)((int)valueAsDouble);
    }

    private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString) throws NumberFormatException, SQLException {
        long valueAsLong;
        short shortValue = 0;
        if (valueAsBytes == null && valueAsString == null) {
            return 0;
        }
        if (valueAsBytes != null) {
            shortValue = StringUtils.getShort((byte[])valueAsBytes);
        } else {
            valueAsString = valueAsString.trim();
            shortValue = Short.parseShort((String)valueAsString);
        }
        if (!this.jdbcCompliantTruncationForReads) return shortValue;
        if (shortValue != -32768) {
            if (shortValue != 32767) return shortValue;
        }
        if ((valueAsLong = Long.parseLong((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString))) >= -32768L) {
            if (valueAsLong <= 32767L) return shortValue;
        }
        this.throwRangeException((String)(valueAsString == null ? StringUtils.toString((byte[])valueAsBytes) : valueAsString), (int)columnIndex, (int)5);
        return shortValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean prev() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        int rowIndex = this.rowData.getCurrentRowNumber();
        if (this.thisRow != null) {
            this.thisRow.closeOpenStreams();
        }
        boolean b = true;
        if (rowIndex - 1 >= 0) {
            this.rowData.setCurrentRow((int)(--rowIndex));
            this.thisRow = this.rowData.getAt((int)rowIndex);
            b = true;
        } else if (rowIndex - 1 == -1) {
            this.rowData.setCurrentRow((int)(--rowIndex));
            this.thisRow = null;
            b = false;
        } else {
            b = false;
        }
        this.setRowPositionValidity();
        // MONITOREXIT : object
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean previous() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.onInsertRow = false;
        }
        if (this.doingUpdates) {
            this.doingUpdates = false;
        }
        // MONITOREXIT : object
        return this.prev();
    }

    /*
     * Exception decompiling
     */
    @Override
    public void realClose(boolean calledExplicitly) throws SQLException {
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

    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed;
    }

    @Override
    public boolean reallyResult() {
        if (this.rowData == null) return this.reallyResult;
        return true;
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new NotUpdatable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean relative(int rows) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.rowData.size() == 0) {
            this.setRowPositionValidity();
            // MONITOREXIT : object
            return false;
        }
        if (this.thisRow != null) {
            this.thisRow.closeOpenStreams();
        }
        this.rowData.moveRowRelative((int)rows);
        this.thisRow = this.rowData.getAt((int)this.rowData.getCurrentRowNumber());
        this.setRowPositionValidity();
        if (!this.rowData.isAfterLast() && !this.rowData.isBeforeFirst()) {
            return true;
        }
        boolean bl = false;
        // MONITOREXIT : object
        return bl;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    protected void setBinaryEncoded() {
        this.isBinaryEncoded = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (direction != 1000 && direction != 1001 && direction != 1002) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Illegal_value_for_fetch_direction_64"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.fetchDirection = direction;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (rows < 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.fetchSize = rows;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFirstCharOfQuery(char c) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.firstCharOfQuery = c;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    protected synchronized void setNextResultSet(ResultSetInternalMethods nextResultSet) {
        this.nextResultSet = nextResultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOwningStatement(StatementImpl owningStatement) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.owningStatement = owningStatement;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void setResultSetConcurrency(int concurrencyFlag) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.resultSetConcurrency = concurrencyFlag;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void setResultSetType(int typeFlag) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.resultSetType = typeFlag;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setServerInfo(String info) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.serverInfo = info;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void setStatementUsedForFetchingRows(PreparedStatement stmt) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.statementUsedForFetchingRows = stmt;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void setWrapperStatement(java.sql.Statement wrapperStatement) {
        try {
            Object object = this.checkClosed().getConnectionMutex();
            // MONITORENTER : object
            this.wrapperStatement = wrapperStatement;
            // MONITOREXIT : object
            return;
        }
        catch (SQLException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException {
        String datatype = null;
        switch (jdbcType) {
            case -6: {
                datatype = "TINYINT";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 5: {
                datatype = "SMALLINT";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 4: {
                datatype = "INTEGER";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case -5: {
                datatype = "BIGINT";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 7: {
                datatype = "REAL";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 6: {
                datatype = "FLOAT";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 8: {
                datatype = "DOUBLE";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
            case 3: {
                datatype = "DECIMAL";
                throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
            }
        }
        datatype = " (JDBC type '" + jdbcType + "')";
        throw SQLError.createSQLException((String)("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + "."), (String)"22003", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    public String toString() {
        if (!this.reallyResult) return "Result set representing update count of " + this.updateCount;
        return super.toString();
    }

    @Override
    public void updateArray(int arg0, Array arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void updateArray(String arg0, Array arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnName), (InputStream)x, (int)length);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        this.updateBigDecimal((int)this.findColumn((String)columnName), (BigDecimal)x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnName), (InputStream)x, (int)length);
    }

    @Override
    public void updateBlob(int arg0, java.sql.Blob arg1) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBlob(String arg0, java.sql.Blob arg1) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        this.updateBoolean((int)this.findColumn((String)columnName), (boolean)x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateByte(String columnName, byte x) throws SQLException {
        this.updateByte((int)this.findColumn((String)columnName), (byte)x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        this.updateBytes((int)this.findColumn((String)columnName), (byte[])x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnName), (Reader)reader, (int)length);
    }

    @Override
    public void updateClob(int arg0, java.sql.Clob arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void updateClob(String columnName, java.sql.Clob clob) throws SQLException {
        this.updateClob((int)this.findColumn((String)columnName), (java.sql.Clob)clob);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateDate(String columnName, Date x) throws SQLException {
        this.updateDate((int)this.findColumn((String)columnName), (Date)x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateDouble(String columnName, double x) throws SQLException {
        this.updateDouble((int)this.findColumn((String)columnName), (double)x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateFloat(String columnName, float x) throws SQLException {
        this.updateFloat((int)this.findColumn((String)columnName), (float)x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateInt(String columnName, int x) throws SQLException {
        this.updateInt((int)this.findColumn((String)columnName), (int)x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateLong(String columnName, long x) throws SQLException {
        this.updateLong((int)this.findColumn((String)columnName), (long)x);
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateNull(String columnName) throws SQLException {
        this.updateNull((int)this.findColumn((String)columnName));
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateObject(String columnName, Object x) throws SQLException {
        this.updateObject((int)this.findColumn((String)columnName), (Object)x);
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        this.updateObject((int)this.findColumn((String)columnName), (Object)x);
    }

    @Override
    public void updateRef(int arg0, Ref arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void updateRef(String arg0, Ref arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void updateRow() throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateShort(String columnName, short x) throws SQLException {
        this.updateShort((int)this.findColumn((String)columnName), (short)x);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateString(String columnName, String x) throws SQLException {
        this.updateString((int)this.findColumn((String)columnName), (String)x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateTime(String columnName, Time x) throws SQLException {
        this.updateTime((int)this.findColumn((String)columnName), (Time)x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new NotUpdatable();
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        this.updateTimestamp((int)this.findColumn((String)columnName), (Timestamp)x);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.wasNullFlag;
    }

    protected Calendar getGmtCalendar() {
        if (this.gmtCalendar != null) return this.gmtCalendar;
        this.gmtCalendar = Calendar.getInstance((TimeZone)TimeZone.getTimeZone((String)"GMT"));
        return this.gmtCalendar;
    }

    protected ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    @Override
    public int getId() {
        return this.resultId;
    }

    static {
        if (Util.isJdbc4()) {
            try {
                String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ResultSet" : "com.mysql.jdbc.JDBC4ResultSet";
                JDBC_4_RS_4_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(Long.TYPE, Long.TYPE, MySQLConnection.class, StatementImpl.class);
                JDBC_4_RS_5_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class);
                jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42UpdatableResultSet" : "com.mysql.jdbc.JDBC4UpdatableResultSet";
                JDBC_4_UPD_RS_5_ARG_CTOR = Class.forName((String)jdbc4ClassName).getConstructor(String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class);
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
            JDBC_4_RS_4_ARG_CTOR = null;
            JDBC_4_RS_5_ARG_CTOR = null;
            JDBC_4_UPD_RS_5_ARG_CTOR = null;
        }
        MIN_DIFF_PREC = (double)Float.parseFloat((String)Float.toString((float)Float.MIN_VALUE)) - Double.parseDouble((String)Float.toString((float)Float.MIN_VALUE));
        MAX_DIFF_PREC = (double)Float.parseFloat((String)Float.toString((float)Float.MAX_VALUE)) - Double.parseDouble((String)Float.toString((float)Float.MAX_VALUE));
        resultCounter = 1;
        EMPTY_SPACE = new char[255];
        int i = 0;
        while (i < EMPTY_SPACE.length) {
            ResultSetImpl.EMPTY_SPACE[i] = 32;
            ++i;
        }
    }
}

