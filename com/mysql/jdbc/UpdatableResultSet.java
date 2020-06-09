/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AssertionFailedException;
import com.mysql.jdbc.ByteArrayRow;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.MysqlParameterMetadata;
import com.mysql.jdbc.NotUpdatable;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UpdatableResultSet
extends ResultSetImpl {
    static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes((String)"** STREAM DATA **");
    protected SingleByteCharsetConverter charConverter;
    private String charEncoding;
    private byte[][] defaultColumnValue;
    private PreparedStatement deleter = null;
    private String deleteSQL = null;
    private boolean initializedCharConverter = false;
    protected PreparedStatement inserter = null;
    private String insertSQL = null;
    private boolean isUpdatable = false;
    private String notUpdatableReason = null;
    private List<Integer> primaryKeyIndicies = null;
    private String qualifiedAndQuotedTableName;
    private String quotedIdChar = null;
    private PreparedStatement refresher;
    private String refreshSQL = null;
    private ResultSetRow savedCurrentRow;
    protected PreparedStatement updater = null;
    private String updateSQL = null;
    private boolean populateInserterWithDefaultValues = false;
    private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;

    protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super((String)catalog, (Field[])fields, (RowData)tuples, (MySQLConnection)conn, (StatementImpl)creatorStmt);
        this.checkUpdatability();
        this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return super.absolute((int)row);
    }

    @Override
    public void afterLast() throws SQLException {
        super.afterLast();
    }

    @Override
    public void beforeFirst() throws SQLException {
        super.beforeFirst();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancelRowUpdates() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.doingUpdates) {
            this.doingUpdates = false;
            this.updater.clearParameters();
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void checkRowPos() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            super.checkRowPos();
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected void checkUpdatability() throws SQLException {
        try {
            block28 : {
                block26 : {
                    if (this.fields == null) {
                        return;
                    }
                    singleTableName = null;
                    catalogName = null;
                    primaryKeyCount = 0;
                    if (this.catalog == null || this.catalog.length() == 0) {
                        this.catalog = this.fields[0].getDatabaseName();
                        if (this.catalog == null) throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.43"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                        if (this.catalog.length() == 0) {
                            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.43"), (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                        }
                    }
                    if (this.fields.length <= 0) {
                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.3");
                        return;
                    }
                    singleTableName = this.fields[0].getOriginalTableName();
                    catalogName = this.fields[0].getDatabaseName();
                    if (singleTableName == null) {
                        singleTableName = this.fields[0].getTableName();
                        catalogName = this.catalog;
                    }
                    if (singleTableName != null && singleTableName.length() == 0) {
                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.3");
                        return;
                    }
                    if (this.fields[0].isPrimaryKey()) {
                        ++primaryKeyCount;
                    }
                    for (i = 1; i < this.fields.length; ++i) {
                        otherTableName = this.fields[i].getOriginalTableName();
                        otherCatalogName = this.fields[i].getDatabaseName();
                        if (otherTableName == null) {
                            otherTableName = this.fields[i].getTableName();
                            otherCatalogName = this.catalog;
                        }
                        if (otherTableName != null && otherTableName.length() == 0) {
                            this.isUpdatable = false;
                            this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.3");
                            return;
                        }
                        if (singleTableName == null || !otherTableName.equals((Object)singleTableName)) {
                            this.isUpdatable = false;
                            this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.0");
                            return;
                        }
                        if (catalogName == null || !otherCatalogName.equals((Object)catalogName)) {
                            this.isUpdatable = false;
                            this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.1");
                            return;
                        }
                        if (!this.fields[i].isPrimaryKey()) continue;
                        ++primaryKeyCount;
                    }
                    if (singleTableName == null || singleTableName.length() == 0) {
                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.2");
                        return;
                    }
                    if (!this.connection.getStrictUpdates()) break block28;
                    dbmd = this.connection.getMetaData();
                    rs = null;
                    primaryKeyNames = new HashMap<String, String>();
                    try {
                        rs = dbmd.getPrimaryKeys((String)catalogName, null, (String)singleTableName);
                        while (rs.next()) {
                            keyName = rs.getString((int)4);
                            keyName = keyName.toUpperCase();
                            primaryKeyNames.put(keyName, keyName);
                        }
                        var9_11 = null;
                        if (rs == null) break block26;
                        try {
                            rs.close();
                        }
                        catch (Exception ex) {
                            AssertionFailedException.shouldNotHappen((Exception)ex);
                        }
                        rs = null;
                    }
                    catch (Throwable var8_15) {
                        block29 : {
                            var9_12 = null;
                            if (rs == null) throw var8_15;
                            ** try [egrp 2[TRYBLOCK] [2 : 479->489)] { 
lbl79: // 1 sources:
                            rs.close();
                            break block29;
lbl81: // 1 sources:
                            catch (Exception ex) {
                                AssertionFailedException.shouldNotHappen((Exception)ex);
                            }
                        }
                        rs = null;
                        throw var8_15;
                    }
                }
                existingPrimaryKeysCount = primaryKeyNames.size();
                if (existingPrimaryKeysCount == 0) {
                    this.isUpdatable = false;
                    this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.5");
                    return;
                }
                for (i = 0; i < this.fields.length; ++i) {
                    if (!this.fields[i].isPrimaryKey() || primaryKeyNames.remove((Object)(columnNameUC = this.fields[i].getName().toUpperCase())) != null || (originalName = this.fields[i].getOriginalName()) == null || primaryKeyNames.remove((Object)originalName.toUpperCase()) != null) continue;
                    this.isUpdatable = false;
                    this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.6", (Object[])new Object[]{originalName});
                    return;
                }
                this.isUpdatable = primaryKeyNames.isEmpty();
                if (!this.isUpdatable) {
                    if (existingPrimaryKeysCount > 1) {
                        this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.7");
                        return;
                    }
                    this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.4");
                    return;
                }
            }
            if (primaryKeyCount == 0) {
                this.isUpdatable = false;
                this.notUpdatableReason = Messages.getString((String)"NotUpdatableReason.4");
                return;
            }
            this.isUpdatable = true;
            this.notUpdatableReason = null;
            return;
        }
        catch (SQLException sqlEx) {
            this.isUpdatable = false;
            this.notUpdatableReason = sqlEx.getMessage();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteRow() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.isUpdatable) {
            throw new NotUpdatable((String)this.notUpdatableReason);
        }
        if (this.onInsertRow) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.1"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.rowData.size() == 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.2"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.isBeforeFirst()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.3"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.isAfterLast()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.4"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.deleter == null) {
            if (this.deleteSQL == null) {
                this.generateStatements();
            }
            this.deleter = (PreparedStatement)this.connection.clientPrepareStatement((String)this.deleteSQL);
        }
        this.deleter.clearParameters();
        int numKeys = this.primaryKeyIndicies.size();
        int i = 0;
        do {
            if (i >= numKeys) {
                this.deleter.executeUpdate();
                this.rowData.removeRow((int)this.rowData.getCurrentRowNumber());
                this.previous();
                // MONITOREXIT : object
                return;
            }
            int index = this.primaryKeyIndicies.get((int)i).intValue();
            this.setParamValue((PreparedStatement)this.deleter, (int)(i + 1), (ResultSetRow)this.thisRow, (int)index, (Field)this.fields[index]);
            ++i;
        } while (true);
    }

    private void setParamValue(PreparedStatement ps, int psIdx, ResultSetRow row, int rsIdx, Field field) throws SQLException {
        byte[] val = row.getColumnValue((int)rsIdx);
        if (val == null) {
            ps.setNull((int)psIdx, (int)0);
            return;
        }
        switch (field.getSQLType()) {
            case 0: {
                ps.setNull((int)psIdx, (int)0);
                return;
            }
            case -6: 
            case 4: 
            case 5: {
                ps.setInt((int)psIdx, (int)row.getInt((int)rsIdx));
                return;
            }
            case -5: {
                ps.setLong((int)psIdx, (long)row.getLong((int)rsIdx));
                return;
            }
            case -1: 
            case 1: 
            case 2: 
            case 3: 
            case 12: {
                Field f = this.fields[rsIdx];
                ps.setString((int)psIdx, (String)row.getString((int)rsIdx, (String)f.getEncoding(), (MySQLConnection)this.connection));
                return;
            }
            case 91: {
                ps.setDate((int)psIdx, (Date)row.getDateFast((int)rsIdx, (MySQLConnection)this.connection, (ResultSetImpl)this, (Calendar)this.fastDefaultCal), (Calendar)this.fastDefaultCal);
                return;
            }
            case 93: {
                boolean useGmtMillis = false;
                boolean useJdbcCompliantTimezoneShift = false;
                ps.setTimestampInternal((int)psIdx, (Timestamp)row.getTimestampFast((int)rsIdx, (Calendar)this.fastDefaultCal, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)false, (MySQLConnection)this.connection, (ResultSetImpl)this, (boolean)useGmtMillis, (boolean)useJdbcCompliantTimezoneShift), null, (TimeZone)this.connection.getDefaultTimeZone(), (boolean)false, (int)field.getDecimals(), (boolean)false);
                return;
            }
            case 92: {
                ps.setTime((int)psIdx, (Time)row.getTimeFast((int)rsIdx, (Calendar)this.fastDefaultCal, (TimeZone)this.connection.getServerTimezoneTZ(), (boolean)false, (MySQLConnection)this.connection, (ResultSetImpl)this));
                return;
            }
            case 6: 
            case 7: 
            case 8: 
            case 16: {
                ps.setBytesNoEscapeNoQuotes((int)psIdx, (byte[])val);
                return;
            }
        }
        ps.setBytes((int)psIdx, (byte[])val);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void extractDefaultValues() throws SQLException {
        DatabaseMetaData dbmd = this.connection.getMetaData();
        this.defaultColumnValue = new byte[this.fields.length][];
        ResultSet columnsResultSet = null;
        Iterator<Map.Entry<String, Map<String, Map<String, Integer>>>> i$ = this.databasesUsedToTablesUsed.entrySet().iterator();
        block2 : while (i$.hasNext()) {
            Map.Entry<String, Map<String, Map<String, Integer>>> dbEntry = i$.next();
            Iterator<Map.Entry<String, Map<String, Integer>>> i$2 = dbEntry.getValue().entrySet().iterator();
            do {
                Object var13_12;
                if (!i$2.hasNext()) continue block2;
                Map.Entry<String, Map<String, Integer>> tableEntry = i$2.next();
                String tableName = tableEntry.getKey();
                Map<String, Integer> columnNamesToIndices = tableEntry.getValue();
                try {
                    columnsResultSet = dbmd.getColumns((String)this.catalog, null, (String)tableName, (String)"%");
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString((String)"COLUMN_NAME");
                        byte[] defaultValue = columnsResultSet.getBytes((String)"COLUMN_DEF");
                        if (!columnNamesToIndices.containsKey((Object)columnName)) continue;
                        int localColumnIndex = columnNamesToIndices.get((Object)columnName).intValue();
                        this.defaultColumnValue[localColumnIndex] = defaultValue;
                    }
                    var13_12 = null;
                    if (columnsResultSet == null) continue;
                    columnsResultSet.close();
                    columnsResultSet = null;
                }
                catch (Throwable throwable) {
                    var13_12 = null;
                    if (columnsResultSet == null) throw throwable;
                    columnsResultSet.close();
                    columnsResultSet = null;
                    throw throwable;
                }
            } while (true);
            break;
        }
        return;
    }

    @Override
    public boolean first() throws SQLException {
        return super.first();
    }

    protected void generateStatements() throws SQLException {
        if (!this.isUpdatable) {
            this.doingUpdates = false;
            this.onInsertRow = false;
            throw new NotUpdatable((String)this.notUpdatableReason);
        }
        String quotedId = this.getQuotedIdChar();
        TreeMap<String, String> tableNamesSoFar = null;
        if (this.connection.lowerCaseTableNames()) {
            tableNamesSoFar = new TreeMap<K, V>(String.CASE_INSENSITIVE_ORDER);
            this.databasesUsedToTablesUsed = new TreeMap<String, Map<String, Map<String, Integer>>>(String.CASE_INSENSITIVE_ORDER);
        } else {
            tableNamesSoFar = new TreeMap<String, String>();
            this.databasesUsedToTablesUsed = new TreeMap<String, Map<String, Map<String, Integer>>>();
        }
        this.primaryKeyIndicies = new ArrayList<Integer>();
        StringBuilder fieldValues = new StringBuilder();
        StringBuilder keyValues = new StringBuilder();
        StringBuilder columnNames = new StringBuilder();
        StringBuilder insertPlaceHolders = new StringBuilder();
        StringBuilder allTablesBuf = new StringBuilder();
        HashMap<Integer, String> columnIndicesToTable = new HashMap<Integer, String>();
        boolean firstTime = true;
        boolean keysFirstTime = true;
        String equalsStr = this.connection.versionMeetsMinimum((int)3, (int)23, (int)0) ? "<=>" : "=";
        int i = 0;
        do {
            if (i >= this.fields.length) {
                this.qualifiedAndQuotedTableName = allTablesBuf.toString();
                this.updateSQL = "UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString();
                this.insertSQL = "INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")";
                this.refreshSQL = "SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
                this.deleteSQL = "DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
                return;
            }
            StringBuilder tableNameBuffer = new StringBuilder();
            Map<String, Integer> updColumnNameToIndex = null;
            if (this.fields[i].getOriginalTableName() != null) {
                String databaseName = this.fields[i].getDatabaseName();
                if (databaseName != null && databaseName.length() > 0) {
                    tableNameBuffer.append((String)quotedId);
                    tableNameBuffer.append((String)databaseName);
                    tableNameBuffer.append((String)quotedId);
                    tableNameBuffer.append((char)'.');
                }
                String tableOnlyName = this.fields[i].getOriginalTableName();
                tableNameBuffer.append((String)quotedId);
                tableNameBuffer.append((String)tableOnlyName);
                tableNameBuffer.append((String)quotedId);
                String fqTableName = tableNameBuffer.toString();
                if (!tableNamesSoFar.containsKey((Object)fqTableName)) {
                    if (!tableNamesSoFar.isEmpty()) {
                        allTablesBuf.append((char)',');
                    }
                    allTablesBuf.append((String)fqTableName);
                    tableNamesSoFar.put(fqTableName, fqTableName);
                }
                columnIndicesToTable.put(Integer.valueOf((int)i), fqTableName);
                updColumnNameToIndex = this.getColumnsToIndexMapForTableAndDB((String)databaseName, (String)tableOnlyName);
            } else {
                String tableOnlyName = this.fields[i].getTableName();
                if (tableOnlyName != null) {
                    tableNameBuffer.append((String)quotedId);
                    tableNameBuffer.append((String)tableOnlyName);
                    tableNameBuffer.append((String)quotedId);
                    String fqTableName = tableNameBuffer.toString();
                    if (!tableNamesSoFar.containsKey((Object)fqTableName)) {
                        if (!tableNamesSoFar.isEmpty()) {
                            allTablesBuf.append((char)',');
                        }
                        allTablesBuf.append((String)fqTableName);
                        tableNamesSoFar.put(fqTableName, fqTableName);
                    }
                    columnIndicesToTable.put(Integer.valueOf((int)i), fqTableName);
                    updColumnNameToIndex = this.getColumnsToIndexMapForTableAndDB((String)this.catalog, (String)tableOnlyName);
                }
            }
            String originalColumnName = this.fields[i].getOriginalName();
            String columnName = null;
            columnName = this.connection.getIO().hasLongColumnInfo() && originalColumnName != null && originalColumnName.length() > 0 ? originalColumnName : this.fields[i].getName();
            if (updColumnNameToIndex != null && columnName != null) {
                updColumnNameToIndex.put(columnName, Integer.valueOf((int)i));
            }
            String originalTableName = this.fields[i].getOriginalTableName();
            String tableName = null;
            tableName = this.connection.getIO().hasLongColumnInfo() && originalTableName != null && originalTableName.length() > 0 ? originalTableName : this.fields[i].getTableName();
            StringBuilder fqcnBuf = new StringBuilder();
            String databaseName = this.fields[i].getDatabaseName();
            if (databaseName != null && databaseName.length() > 0) {
                fqcnBuf.append((String)quotedId);
                fqcnBuf.append((String)databaseName);
                fqcnBuf.append((String)quotedId);
                fqcnBuf.append((char)'.');
            }
            fqcnBuf.append((String)quotedId);
            fqcnBuf.append((String)tableName);
            fqcnBuf.append((String)quotedId);
            fqcnBuf.append((char)'.');
            fqcnBuf.append((String)quotedId);
            fqcnBuf.append((String)columnName);
            fqcnBuf.append((String)quotedId);
            String qualifiedColumnName = fqcnBuf.toString();
            if (this.fields[i].isPrimaryKey()) {
                this.primaryKeyIndicies.add((Integer)Integer.valueOf((int)i));
                if (!keysFirstTime) {
                    keyValues.append((String)" AND ");
                } else {
                    keysFirstTime = false;
                }
                keyValues.append((String)qualifiedColumnName);
                keyValues.append((String)equalsStr);
                keyValues.append((String)"?");
            }
            if (firstTime) {
                firstTime = false;
                fieldValues.append((String)"SET ");
            } else {
                fieldValues.append((String)",");
                columnNames.append((String)",");
                insertPlaceHolders.append((String)",");
            }
            insertPlaceHolders.append((String)"?");
            columnNames.append((String)qualifiedColumnName);
            fieldValues.append((String)qualifiedColumnName);
            fieldValues.append((String)"=?");
            ++i;
        } while (true);
    }

    private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName) {
        Map<String, Integer> nameToIndex;
        Map<String, Map<String, Integer>> tablesUsedToColumnsMap = this.databasesUsedToTablesUsed.get((Object)databaseName);
        if (tablesUsedToColumnsMap == null) {
            tablesUsedToColumnsMap = this.connection.lowerCaseTableNames() ? new TreeMap<String, Map<String, Integer>>(String.CASE_INSENSITIVE_ORDER) : new TreeMap<String, Map<String, Integer>>();
            this.databasesUsedToTablesUsed.put((String)databaseName, tablesUsedToColumnsMap);
        }
        if ((nameToIndex = tablesUsedToColumnsMap.get((Object)tableName)) != null) return nameToIndex;
        nameToIndex = new HashMap<String, Integer>();
        tablesUsedToColumnsMap.put((String)tableName, nameToIndex);
        return nameToIndex;
    }

    private SingleByteCharsetConverter getCharConverter() throws SQLException {
        if (this.initializedCharConverter) return this.charConverter;
        this.initializedCharConverter = true;
        if (!this.connection.getUseUnicode()) return this.charConverter;
        this.charEncoding = this.connection.getEncoding();
        this.charConverter = this.connection.getCharsetConverter((String)this.charEncoding);
        return this.charConverter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getConcurrency() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.isUpdatable) {
            return 1008;
        }
        int n = 1007;
        // MONITOREXIT : object
        return n;
    }

    private String getQuotedIdChar() throws SQLException {
        if (this.quotedIdChar != null) return this.quotedIdChar;
        boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
        if (useQuotedIdentifiers) {
            DatabaseMetaData dbmd = this.connection.getMetaData();
            this.quotedIdChar = dbmd.getIdentifierQuoteString();
            return this.quotedIdChar;
        }
        this.quotedIdChar = "";
        return this.quotedIdChar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertRow() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.7"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.inserter.executeUpdate();
        long autoIncrementId = this.inserter.getLastInsertID();
        int numFields = this.fields.length;
        byte[][] newRow = new byte[numFields][];
        int i = 0;
        do {
            if (i >= numFields) {
                ByteArrayRow resultSetRow = new ByteArrayRow((byte[][])newRow, (ExceptionInterceptor)this.getExceptionInterceptor());
                this.refreshRow((PreparedStatement)this.inserter, (ResultSetRow)resultSetRow);
                this.rowData.addRow((ResultSetRow)resultSetRow);
                this.resetInserter();
                // MONITOREXIT : object
                return;
            }
            newRow[i] = this.inserter.isNull((int)i) ? null : this.inserter.getBytesRepresentation((int)i);
            if (this.fields[i].isAutoIncrement() && autoIncrementId > 0L) {
                newRow[i] = StringUtils.getBytes((String)String.valueOf((long)autoIncrementId));
                this.inserter.setBytesNoEscapeNoQuotes((int)(i + 1), (byte[])newRow[i]);
            }
            ++i;
        } while (true);
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return super.isAfterLast();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return super.isBeforeFirst();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return super.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return super.isLast();
    }

    boolean isUpdatable() {
        return this.isUpdatable;
    }

    @Override
    public boolean last() throws SQLException {
        return super.last();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void moveToCurrentRow() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.isUpdatable) {
            throw new NotUpdatable((String)this.notUpdatableReason);
        }
        if (this.onInsertRow) {
            this.onInsertRow = false;
            this.thisRow = this.savedCurrentRow;
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public void moveToInsertRow() throws SQLException {
        var1_1 = this.checkClosed().getConnectionMutex();
        // MONITORENTER : var1_1
        if (!this.isUpdatable) {
            throw new NotUpdatable((String)this.notUpdatableReason);
        }
        if (this.inserter == null) {
            if (this.insertSQL == null) {
                this.generateStatements();
            }
            this.inserter = (PreparedStatement)this.connection.clientPrepareStatement((String)this.insertSQL);
            this.inserter.parameterMetaData = new MysqlParameterMetadata((Field[])this.fields, (int)this.fields.length, (ExceptionInterceptor)this.getExceptionInterceptor());
            if (this.populateInserterWithDefaultValues) {
                this.extractDefaultValues();
            }
            this.resetInserter();
        } else {
            this.resetInserter();
        }
        numFields = this.fields.length;
        this.onInsertRow = true;
        this.doingUpdates = false;
        this.savedCurrentRow = this.thisRow;
        newRowData = new byte[numFields][];
        this.thisRow = new ByteArrayRow((byte[][])newRowData, (ExceptionInterceptor)this.getExceptionInterceptor());
        this.thisRow.setMetadata((Field[])this.fields);
        i = 0;
        do {
            if (i >= numFields) {
                // MONITOREXIT : var1_1
                return;
            }
            if (!this.populateInserterWithDefaultValues) {
                this.inserter.setBytesNoEscapeNoQuotes((int)(i + 1), (byte[])StringUtils.getBytes((String)"DEFAULT"));
                newRowData = (byte[][])null;
            } else if (this.defaultColumnValue[i] != null) {
                f = this.fields[i];
                switch (f.getMysqlType()) {
                    case 7: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 14: {
                        if (this.defaultColumnValue[i].length > 7 && this.defaultColumnValue[i][0] == 67 && this.defaultColumnValue[i][1] == 85 && this.defaultColumnValue[i][2] == 82 && this.defaultColumnValue[i][3] == 82 && this.defaultColumnValue[i][4] == 69 && this.defaultColumnValue[i][5] == 78 && this.defaultColumnValue[i][6] == 84 && this.defaultColumnValue[i][7] == 95) {
                            this.inserter.setBytesNoEscapeNoQuotes((int)(i + 1), (byte[])this.defaultColumnValue[i]);
                            ** break;
                        }
                        this.inserter.setBytes((int)(i + 1), (byte[])this.defaultColumnValue[i], (boolean)false, (boolean)false);
                        ** break;
                    }
                }
                this.inserter.setBytes((int)(i + 1), (byte[])this.defaultColumnValue[i], (boolean)false, (boolean)false);
lbl42: // 3 sources:
                defaultValueCopy = new byte[this.defaultColumnValue[i].length];
                System.arraycopy((Object)this.defaultColumnValue[i], (int)0, (Object)defaultValueCopy, (int)0, (int)defaultValueCopy.length);
                newRowData[i] = defaultValueCopy;
            } else {
                this.inserter.setNull((int)(i + 1), (int)0);
                newRowData[i] = null;
            }
            ++i;
        } while (true);
    }

    @Override
    public boolean next() throws SQLException {
        return super.next();
    }

    @Override
    public boolean prev() throws SQLException {
        return super.prev();
    }

    @Override
    public boolean previous() throws SQLException {
        return super.previous();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void realClose(boolean calledExplicitly) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return;
        }
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        SQLException sqlEx = null;
        if (this.useUsageAdvisor && this.deleter == null && this.inserter == null && this.refresher == null && this.updater == null) {
            this.connection.getProfilerEventHandlerInstance().processEvent((byte)0, (MySQLConnection)this.connection, (Statement)this.owningStatement, (ResultSetInternalMethods)this, (long)0L, (Throwable)new Throwable(), (String)Messages.getString((String)"UpdatableResultSet.34"));
        }
        try {
            if (this.deleter != null) {
                this.deleter.close();
            }
        }
        catch (SQLException ex) {
            sqlEx = ex;
        }
        try {
            if (this.inserter != null) {
                this.inserter.close();
            }
        }
        catch (SQLException ex) {
            sqlEx = ex;
        }
        try {
            if (this.refresher != null) {
                this.refresher.close();
            }
        }
        catch (SQLException ex) {
            sqlEx = ex;
        }
        try {
            if (this.updater != null) {
                this.updater.close();
            }
        }
        catch (SQLException ex) {
            sqlEx = ex;
        }
        super.realClose((boolean)calledExplicitly);
        if (sqlEx != null) {
            throw sqlEx;
        }
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void refreshRow() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.isUpdatable) {
            throw new NotUpdatable();
        }
        if (this.onInsertRow) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.8"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.rowData.size() == 0) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.9"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.isBeforeFirst()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.10"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (this.isAfterLast()) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.11"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.refreshRow((PreparedStatement)this.updater, (ResultSetRow)this.thisRow);
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException {
        if (this.refresher == null) {
            if (this.refreshSQL == null) {
                this.generateStatements();
            }
            this.refresher = (PreparedStatement)this.connection.clientPrepareStatement((String)this.refreshSQL);
            this.refresher.parameterMetaData = new MysqlParameterMetadata((Field[])this.fields, (int)this.fields.length, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.refresher.clearParameters();
        numKeys = this.primaryKeyIndicies.size();
        if (numKeys == 1) {
            dataFrom = null;
            index = this.primaryKeyIndicies.get((int)0).intValue();
            if (!this.doingUpdates && !this.onInsertRow) {
                dataFrom = rowToRefresh.getColumnValue((int)index);
            } else {
                dataFrom = updateInsertStmt.getBytesRepresentation((int)index);
                dataFrom = updateInsertStmt.isNull((int)index) != false || dataFrom.length == 0 ? rowToRefresh.getColumnValue((int)index) : this.stripBinaryPrefix((byte[])dataFrom);
            }
            if (this.fields[index].getvalueNeedsQuoting() && !this.connection.isNoBackslashEscapesSet()) {
                this.refresher.setBytesNoEscape((int)1, (byte[])dataFrom);
            } else {
                this.refresher.setBytesNoEscapeNoQuotes((int)1, (byte[])dataFrom);
            }
        } else {
            for (i = 0; i < numKeys; ++i) {
                dataFrom = null;
                index = this.primaryKeyIndicies.get((int)i).intValue();
                if (!this.doingUpdates && !this.onInsertRow) {
                    dataFrom = rowToRefresh.getColumnValue((int)index);
                } else {
                    dataFrom = updateInsertStmt.getBytesRepresentation((int)index);
                    dataFrom = updateInsertStmt.isNull((int)index) != false || dataFrom.length == 0 ? rowToRefresh.getColumnValue((int)index) : this.stripBinaryPrefix((byte[])dataFrom);
                }
                this.refresher.setBytesNoEscape((int)(i + 1), (byte[])dataFrom);
            }
        }
        rs = null;
        try {
            rs = this.refresher.executeQuery();
            numCols = rs.getMetaData().getColumnCount();
            if (rs.next() == false) throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.12"), (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            for (i = 0; i < numCols; ++i) {
                val = rs.getBytes((int)(i + 1));
                if (val == null || rs.wasNull()) {
                    rowToRefresh.setColumnValue((int)i, null);
                    continue;
                }
                rowToRefresh.setColumnValue((int)i, (byte[])rs.getBytes((int)(i + 1)));
            }
            var9_10 = null;
            if (rs == null) return;
            try {
                rs.close();
                return;
            }
            catch (SQLException ex) {
                return;
            }
        }
        catch (Throwable var8_14) {
            var9_11 = null;
            if (rs == null) throw var8_14;
            ** try [egrp 1[TRYBLOCK] [2 : 491->501)] { 
lbl55: // 1 sources:
            rs.close();
            throw var8_14;
lbl57: // 1 sources:
            catch (SQLException ex) {
                // empty catch block
            }
            throw var8_14;
        }
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return super.relative((int)rows);
    }

    private void resetInserter() throws SQLException {
        this.inserter.clearParameters();
        int i = 0;
        while (i < this.fields.length) {
            this.inserter.setNull((int)(i + 1), (int)0);
            ++i;
        }
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

    @Override
    protected void setResultSetConcurrency(int concurrencyFlag) {
        super.setResultSetConcurrency((int)concurrencyFlag);
    }

    private byte[] stripBinaryPrefix(byte[] dataFrom) {
        return StringUtils.stripEnclosure((byte[])dataFrom, (String)"_binary'", (String)"'");
    }

    protected void syncUpdate() throws SQLException {
        if (this.updater == null) {
            if (this.updateSQL == null) {
                this.generateStatements();
            }
            this.updater = (PreparedStatement)this.connection.clientPrepareStatement((String)this.updateSQL);
            this.updater.parameterMetaData = new MysqlParameterMetadata((Field[])this.fields, (int)this.fields.length, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        int numFields = this.fields.length;
        this.updater.clearParameters();
        for (int i = 0; i < numFields; ++i) {
            if (this.thisRow.getColumnValue((int)i) != null) {
                if (this.fields[i].getvalueNeedsQuoting()) {
                    if (this.fields[i].isCharsetApplicableType() && !this.fields[i].getEncoding().equals((Object)this.connection.getEncoding())) {
                        this.updater.setString((int)(i + 1), (String)this.thisRow.getString((int)i, (String)this.fields[i].getEncoding(), (MySQLConnection)this.connection));
                        continue;
                    }
                    this.updater.setBytes((int)(i + 1), (byte[])this.thisRow.getColumnValue((int)i), (boolean)this.fields[i].isBinary(), (boolean)false);
                    continue;
                }
                this.updater.setBytesNoEscapeNoQuotes((int)(i + 1), (byte[])this.thisRow.getColumnValue((int)i));
                continue;
            }
            this.updater.setNull((int)(i + 1), (int)0);
        }
        int numKeys = this.primaryKeyIndicies.size();
        int i = 0;
        while (i < numKeys) {
            int idx = this.primaryKeyIndicies.get((int)i).intValue();
            this.setParamValue((PreparedStatement)this.updater, (int)(numFields + i + 1), (ResultSetRow)this.thisRow, (int)idx, (Field)this.fields[idx]);
            ++i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setAsciiStream((int)columnIndex, (InputStream)x, (int)length);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])STREAM_DATA_MARKER);
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setAsciiStream((int)columnIndex, (InputStream)x, (int)length);
        return;
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateAsciiStream((int)this.findColumn((String)columnName), (InputStream)x, (int)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            this.updater.setBigDecimal((int)columnIndex, (BigDecimal)x);
            return;
        }
        this.inserter.setBigDecimal((int)columnIndex, (BigDecimal)x);
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])StringUtils.getBytes((String)x.toString()));
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        this.updateBigDecimal((int)this.findColumn((String)columnName), (BigDecimal)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            this.updater.setBinaryStream((int)columnIndex, (InputStream)x, (int)length);
            return;
        }
        this.inserter.setBinaryStream((int)columnIndex, (InputStream)x, (int)length);
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])STREAM_DATA_MARKER);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        this.updateBinaryStream((int)this.findColumn((String)columnName), (InputStream)x, (int)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateBlob(int columnIndex, Blob blob) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            this.updater.setBlob((int)columnIndex, (Blob)blob);
            return;
        }
        this.inserter.setBlob((int)columnIndex, (Blob)blob);
        if (blob == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])STREAM_DATA_MARKER);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateBlob(String columnName, Blob blob) throws SQLException {
        this.updateBlob((int)this.findColumn((String)columnName), (Blob)blob);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setBoolean((int)columnIndex, (boolean)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setBoolean((int)columnIndex, (boolean)x);
        return;
    }

    @Override
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        this.updateBoolean((int)this.findColumn((String)columnName), (boolean)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setByte((int)columnIndex, (byte)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setByte((int)columnIndex, (byte)x);
        return;
    }

    @Override
    public void updateByte(String columnName, byte x) throws SQLException {
        this.updateByte((int)this.findColumn((String)columnName), (byte)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setBytes((int)columnIndex, (byte[])x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])x);
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setBytes((int)columnIndex, (byte[])x);
        return;
    }

    @Override
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        this.updateBytes((int)this.findColumn((String)columnName), (byte[])x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            this.updater.setCharacterStream((int)columnIndex, (Reader)x, (int)length);
            return;
        }
        this.inserter.setCharacterStream((int)columnIndex, (Reader)x, (int)length);
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])STREAM_DATA_MARKER);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        this.updateCharacterStream((int)this.findColumn((String)columnName), (Reader)reader, (int)length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateClob(int columnIndex, Clob clob) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (clob == null) {
            this.updateNull((int)columnIndex);
            return;
        }
        this.updateCharacterStream((int)columnIndex, (Reader)clob.getCharacterStream(), (int)((int)clob.length()));
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setDate((int)columnIndex, (Date)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setDate((int)columnIndex, (Date)x);
        return;
    }

    @Override
    public void updateDate(String columnName, Date x) throws SQLException {
        this.updateDate((int)this.findColumn((String)columnName), (Date)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setDouble((int)columnIndex, (double)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setDouble((int)columnIndex, (double)x);
        return;
    }

    @Override
    public void updateDouble(String columnName, double x) throws SQLException {
        this.updateDouble((int)this.findColumn((String)columnName), (double)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setFloat((int)columnIndex, (float)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setFloat((int)columnIndex, (float)x);
        return;
    }

    @Override
    public void updateFloat(String columnName, float x) throws SQLException {
        this.updateFloat((int)this.findColumn((String)columnName), (float)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setInt((int)columnIndex, (int)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setInt((int)columnIndex, (int)x);
        return;
    }

    @Override
    public void updateInt(String columnName, int x) throws SQLException {
        this.updateInt((int)this.findColumn((String)columnName), (int)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setLong((int)columnIndex, (long)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setLong((int)columnIndex, (long)x);
        return;
    }

    @Override
    public void updateLong(String columnName, long x) throws SQLException {
        this.updateLong((int)this.findColumn((String)columnName), (long)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateNull(int columnIndex) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setNull((int)columnIndex, (int)0);
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setNull((int)columnIndex, (int)0);
        return;
    }

    @Override
    public void updateNull(String columnName) throws SQLException {
        this.updateNull((int)this.findColumn((String)columnName));
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        this.updateObjectInternal((int)columnIndex, (Object)x, null, (int)0);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        this.updateObjectInternal((int)columnIndex, (Object)x, null, (int)scale);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void updateObjectInternal(int columnIndex, Object x, Integer targetType, int scaleOrLength) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            if (targetType == null) {
                this.updater.setObject((int)columnIndex, (Object)x);
                return;
            }
            this.updater.setObject((int)columnIndex, (Object)x, (int)targetType.intValue());
            return;
        }
        if (targetType == null) {
            this.inserter.setObject((int)columnIndex, (Object)x);
        } else {
            this.inserter.setObject((int)columnIndex, (Object)x, (int)targetType.intValue());
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateObject(String columnName, Object x) throws SQLException {
        this.updateObject((int)this.findColumn((String)columnName), (Object)x);
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        this.updateObject((int)this.findColumn((String)columnName), (Object)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateRow() throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.isUpdatable) {
            throw new NotUpdatable((String)this.notUpdatableReason);
        }
        if (this.doingUpdates) {
            this.updater.executeUpdate();
            this.refreshRow();
            this.doingUpdates = false;
        } else if (this.onInsertRow) {
            throw SQLError.createSQLException((String)Messages.getString((String)"UpdatableResultSet.44"), (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.syncUpdate();
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setShort((int)columnIndex, (short)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setShort((int)columnIndex, (short)x);
        return;
    }

    @Override
    public void updateShort(String columnName, short x) throws SQLException {
        this.updateShort((int)this.findColumn((String)columnName), (short)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (!this.onInsertRow) {
            if (!this.doingUpdates) {
                this.doingUpdates = true;
                this.syncUpdate();
            }
            this.updater.setString((int)columnIndex, (String)x);
            return;
        }
        this.inserter.setString((int)columnIndex, (String)x);
        if (x == null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), null);
            return;
        }
        if (this.getCharConverter() != null) {
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])StringUtils.getBytes((String)x, (SingleByteCharsetConverter)this.charConverter, (String)this.charEncoding, (String)this.connection.getServerCharset(), (boolean)this.connection.parserKnowsUnicode(), (ExceptionInterceptor)this.getExceptionInterceptor()));
            return;
        }
        this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])StringUtils.getBytes((String)x));
        // MONITOREXIT : object
        return;
    }

    @Override
    public void updateString(String columnName, String x) throws SQLException {
        this.updateString((int)this.findColumn((String)columnName), (String)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setTime((int)columnIndex, (Time)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setTime((int)columnIndex, (Time)x);
        return;
    }

    @Override
    public void updateTime(String columnName, Time x) throws SQLException {
        this.updateTime((int)this.findColumn((String)columnName), (Time)x);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        Object object = this.checkClosed().getConnectionMutex();
        // MONITORENTER : object
        if (this.onInsertRow) {
            this.inserter.setTimestamp((int)columnIndex, (Timestamp)x);
            this.thisRow.setColumnValue((int)(columnIndex - 1), (byte[])this.inserter.getBytesRepresentation((int)(columnIndex - 1)));
            // MONITOREXIT : object
            return;
        }
        if (!this.doingUpdates) {
            this.doingUpdates = true;
            this.syncUpdate();
        }
        this.updater.setTimestamp((int)columnIndex, (Timestamp)x);
        return;
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        this.updateTimestamp((int)this.findColumn((String)columnName), (Timestamp)x);
    }
}

