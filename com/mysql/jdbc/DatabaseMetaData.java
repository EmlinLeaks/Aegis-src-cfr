/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AssertionFailedException;
import com.mysql.jdbc.ByteArrayRow;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.IterateBlock;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import com.mysql.jdbc.RowDataStatic;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DatabaseMetaData
implements java.sql.DatabaseMetaData {
    protected static final int MAX_IDENTIFIER_LENGTH = 64;
    private static final int DEFERRABILITY = 13;
    private static final int DELETE_RULE = 10;
    private static final int FK_NAME = 11;
    private static final int FKCOLUMN_NAME = 7;
    private static final int FKTABLE_CAT = 4;
    private static final int FKTABLE_NAME = 6;
    private static final int FKTABLE_SCHEM = 5;
    private static final int KEY_SEQ = 8;
    private static final int PK_NAME = 12;
    private static final int PKCOLUMN_NAME = 3;
    private static final int PKTABLE_CAT = 0;
    private static final int PKTABLE_NAME = 2;
    private static final int PKTABLE_SCHEM = 1;
    private static final String SUPPORTS_FK = "SUPPORTS_FK";
    protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
    protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
    private static final int UPDATE_RULE = 9;
    protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
    private static final Constructor<?> JDBC_4_DBMD_SHOW_CTOR;
    private static final Constructor<?> JDBC_4_DBMD_IS_CTOR;
    private static final String[] MYSQL_KEYWORDS;
    private static final String[] SQL92_KEYWORDS;
    private static final String[] SQL2003_KEYWORDS;
    private static volatile String mysqlKeywords;
    protected MySQLConnection conn;
    protected String database = null;
    protected final String quotedId;
    private ExceptionInterceptor exceptionInterceptor;

    protected static DatabaseMetaData getInstance(MySQLConnection connToSet, String databaseToSet, boolean checkForInfoSchema) throws SQLException {
        if (!Util.isJdbc4()) {
            if (!checkForInfoSchema) return new DatabaseMetaData((MySQLConnection)connToSet, (String)databaseToSet);
            if (!connToSet.getUseInformationSchema()) return new DatabaseMetaData((MySQLConnection)connToSet, (String)databaseToSet);
            if (!connToSet.versionMeetsMinimum((int)5, (int)0, (int)7)) return new DatabaseMetaData((MySQLConnection)connToSet, (String)databaseToSet);
            return new DatabaseMetaDataUsingInfoSchema((MySQLConnection)connToSet, (String)databaseToSet);
        }
        if (!checkForInfoSchema || !connToSet.getUseInformationSchema() || !connToSet.versionMeetsMinimum((int)5, (int)0, (int)7)) return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, (Object[])new Object[]{connToSet, databaseToSet}, (ExceptionInterceptor)connToSet.getExceptionInterceptor());
        return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, (Object[])new Object[]{connToSet, databaseToSet}, (ExceptionInterceptor)connToSet.getExceptionInterceptor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DatabaseMetaData(MySQLConnection connToSet, String databaseToSet) {
        this.conn = connToSet;
        this.database = databaseToSet;
        this.exceptionInterceptor = this.conn.getExceptionInterceptor();
        String identifierQuote = null;
        try {
            try {
                identifierQuote = this.getIdentifierQuoteString();
            }
            catch (SQLException sqlEx) {
                AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                Object var6_5 = null;
                this.quotedId = identifierQuote;
                return;
            }
            Object var6_4 = null;
            this.quotedId = identifierQuote;
            return;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            this.quotedId = identifierQuote;
            throw throwable;
        }
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return false;
    }

    private ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows) throws SQLException {
        return DatabaseMetaData.buildResultSet((Field[])fields, rows, (MySQLConnection)this.conn);
    }

    static ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows, MySQLConnection c) throws SQLException {
        int fieldsLength = fields.length;
        int i = 0;
        while (i < fieldsLength) {
            int jdbcType = fields[i].getSQLType();
            switch (jdbcType) {
                case -1: 
                case 1: 
                case 12: {
                    fields[i].setEncoding((String)c.getCharacterSetMetadata(), (Connection)c);
                    break;
                }
            }
            fields[i].setConnection((MySQLConnection)c);
            fields[i].setUseOldNameMetadata((boolean)true);
            ++i;
        }
        return ResultSetImpl.getInstance((String)c.getCatalog(), (Field[])fields, (RowData)new RowDataStatic(rows), (MySQLConnection)c, null, (boolean)false);
    }

    protected void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex, Field[] fields) throws SQLException {
        while (proceduresRs.next()) {
            boolean shouldAdd = true;
            if (needsClientFiltering) {
                shouldAdd = false;
                String procDb = proceduresRs.getString((int)1);
                if (db == null && procDb == null) {
                    shouldAdd = true;
                } else if (db != null && db.equals((Object)procDb)) {
                    shouldAdd = true;
                }
            }
            if (!shouldAdd) continue;
            String functionName = proceduresRs.getString((int)nameIndex);
            byte[][] rowData = (byte[][])null;
            rowData = fields != null && fields.length == 9 ? new byte[][]{catalog == null ? null : this.s2b((String)catalog), null, this.s2b((String)functionName), null, null, null, this.s2b((String)proceduresRs.getString((String)"comment")), this.s2b((String)Integer.toString((int)2)), this.s2b((String)functionName)} : new byte[][]{catalog == null ? null : this.s2b((String)catalog), null, this.s2b((String)functionName), this.s2b((String)proceduresRs.getString((String)"comment")), this.s2b((String)Integer.toString((int)this.getJDBC4FunctionNoTableConstant())), this.s2b((String)functionName)};
            procedureRows.add(new ComparableWrapper<String, ByteArrayRow>((DatabaseMetaData)this, this.getFullyQualifiedName((String)catalog, (String)functionName), new ByteArrayRow((byte[][])rowData, (ExceptionInterceptor)this.getExceptionInterceptor())));
        }
    }

    protected String getFullyQualifiedName(String catalog, String entity) {
        StringBuilder fullyQualifiedName = new StringBuilder((String)StringUtils.quoteIdentifier((String)(catalog == null ? "" : catalog), (String)this.quotedId, (boolean)this.conn.getPedantic()));
        fullyQualifiedName.append((char)'.');
        fullyQualifiedName.append((String)StringUtils.quoteIdentifier((String)entity, (String)this.quotedId, (boolean)this.conn.getPedantic()));
        return fullyQualifiedName.toString();
    }

    protected int getJDBC4FunctionNoTableConstant() {
        return 0;
    }

    protected void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex) throws SQLException {
        while (proceduresRs.next()) {
            boolean shouldAdd = true;
            if (needsClientFiltering) {
                shouldAdd = false;
                String procDb = proceduresRs.getString((int)1);
                if (db == null && procDb == null) {
                    shouldAdd = true;
                } else if (db != null && db.equals((Object)procDb)) {
                    shouldAdd = true;
                }
            }
            if (!shouldAdd) continue;
            String procedureName = proceduresRs.getString((int)nameIndex);
            byte[][] rowData = new byte[9][];
            rowData[0] = catalog == null ? null : this.s2b((String)catalog);
            rowData[1] = null;
            rowData[2] = this.s2b((String)procedureName);
            rowData[3] = null;
            rowData[4] = null;
            rowData[5] = null;
            rowData[6] = this.s2b((String)proceduresRs.getString((String)"comment"));
            boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase((String)proceduresRs.getString((String)"type")) : false;
            rowData[7] = this.s2b((String)(isFunction ? Integer.toString((int)2) : Integer.toString((int)1)));
            rowData[8] = this.s2b((String)procedureName);
            procedureRows.add(new ComparableWrapper<String, ByteArrayRow>((DatabaseMetaData)this, this.getFullyQualifiedName((String)catalog, (String)procedureName), new ByteArrayRow((byte[][])rowData, (ExceptionInterceptor)this.getExceptionInterceptor())));
        }
    }

    /*
     * Unable to fully structure code
     */
    private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal) throws SQLException {
        row = forGetFunctionColumns != false ? new byte[17][] : new byte[20][];
        row[0] = procCatAsBytes;
        row[1] = null;
        row[2] = procNameAsBytes;
        row[3] = this.s2b((String)paramName);
        row[4] = this.s2b((String)String.valueOf((int)this.getColumnType((boolean)isOutParam, (boolean)isInParam, (boolean)isReturnParam, (boolean)forGetFunctionColumns)));
        row[5] = this.s2b((String)Short.toString((short)typeDesc.dataType));
        row[6] = this.s2b((String)typeDesc.typeName);
        row[7] = typeDesc.columnSize == null ? null : this.s2b((String)typeDesc.columnSize.toString());
        row[8] = row[7];
        row[9] = typeDesc.decimalDigits == null ? null : this.s2b((String)typeDesc.decimalDigits.toString());
        row[10] = this.s2b((String)Integer.toString((int)typeDesc.numPrecRadix));
        switch (typeDesc.nullability) {
            case 0: {
                row[11] = this.s2b((String)String.valueOf((int)0));
                ** break;
            }
            case 1: {
                row[11] = this.s2b((String)String.valueOf((int)1));
                ** break;
            }
            case 2: {
                row[11] = this.s2b((String)String.valueOf((int)2));
                ** break;
            }
        }
        throw SQLError.createSQLException((String)"Internal error while parsing callable statement metadata (unknown nullability value fount)", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
lbl24: // 3 sources:
        row[12] = null;
        if (forGetFunctionColumns) {
            row[13] = null;
            row[14] = this.s2b((String)String.valueOf((int)ordinal));
            row[15] = this.s2b((String)typeDesc.isNullable);
            row[16] = procNameAsBytes;
            return new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        row[13] = null;
        row[14] = null;
        row[15] = null;
        row[16] = null;
        row[17] = this.s2b((String)String.valueOf((int)ordinal));
        row[18] = this.s2b((String)typeDesc.isNullable);
        row[19] = procNameAsBytes;
        return new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
        if (isInParam && isOutParam) {
            return 2;
        }
        if (isInParam) {
            return 1;
        }
        if (isOutParam) {
            return 4;
        }
        if (!isReturnParam) return 0;
        return 5;
    }

    protected ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return true;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    public List<ResultSetRow> extractForeignKeyForTable(ArrayList<ResultSetRow> rows, ResultSet rs, String catalog) throws SQLException {
        byte[][] row = new byte[3][];
        row[0] = rs.getBytes((int)1);
        row[1] = this.s2b((String)SUPPORTS_FK);
        String createTableString = rs.getString((int)2);
        StringTokenizer lineTokenizer = new StringTokenizer((String)createTableString, (String)"\n");
        StringBuilder commentBuf = new StringBuilder((String)"comment; ");
        boolean firstTime = true;
        do {
            int afterFk;
            int indexOfRef;
            if (!lineTokenizer.hasMoreTokens()) {
                row[2] = this.s2b((String)commentBuf.toString());
                rows.add((ResultSetRow)new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.getExceptionInterceptor()));
                return rows;
            }
            String line = lineTokenizer.nextToken().trim();
            String constraintName = null;
            if (StringUtils.startsWithIgnoreCase((String)line, (String)"CONSTRAINT")) {
                boolean usingBackTicks = true;
                int beginPos = StringUtils.indexOfQuoteDoubleAware((String)line, (String)this.quotedId, (int)0);
                if (beginPos == -1) {
                    beginPos = line.indexOf((String)"\"");
                    usingBackTicks = false;
                }
                if (beginPos != -1) {
                    int endPos = -1;
                    endPos = usingBackTicks ? StringUtils.indexOfQuoteDoubleAware((String)line, (String)this.quotedId, (int)(beginPos + 1)) : StringUtils.indexOfQuoteDoubleAware((String)line, (String)"\"", (int)(beginPos + 1));
                    if (endPos != -1) {
                        constraintName = line.substring((int)(beginPos + 1), (int)endPos);
                        line = line.substring((int)(endPos + 1), (int)line.length()).trim();
                    }
                }
            }
            if (!line.startsWith((String)"FOREIGN KEY")) continue;
            if (line.endsWith((String)",")) {
                line = line.substring((int)0, (int)(line.length() - 1));
            }
            int indexOfFK = line.indexOf((String)"FOREIGN KEY");
            String localColumnName = null;
            String referencedCatalogName = StringUtils.quoteIdentifier((String)catalog, (String)this.quotedId, (boolean)this.conn.getPedantic());
            String referencedTableName = null;
            String referencedColumnName = null;
            if (indexOfFK != -1 && (indexOfRef = StringUtils.indexOfIgnoreCase((int)(afterFk = indexOfFK + "FOREIGN KEY".length()), (String)line, (String)"REFERENCES", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL)) != -1) {
                int indexOfParenOpen = line.indexOf((int)40, (int)afterFk);
                int indexOfParenClose = StringUtils.indexOfIgnoreCase((int)indexOfParenOpen, (String)line, (String)")", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
                if (indexOfParenOpen == -1 || indexOfParenClose == -1) {
                    // empty if block
                }
                localColumnName = line.substring((int)(indexOfParenOpen + 1), (int)indexOfParenClose);
                int afterRef = indexOfRef + "REFERENCES".length();
                int referencedColumnBegin = StringUtils.indexOfIgnoreCase((int)afterRef, (String)line, (String)"(", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
                if (referencedColumnBegin != -1) {
                    int indexOfCatalogSep;
                    referencedTableName = line.substring((int)afterRef, (int)referencedColumnBegin);
                    int referencedColumnEnd = StringUtils.indexOfIgnoreCase((int)(referencedColumnBegin + 1), (String)line, (String)")", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
                    if (referencedColumnEnd != -1) {
                        referencedColumnName = line.substring((int)(referencedColumnBegin + 1), (int)referencedColumnEnd);
                    }
                    if ((indexOfCatalogSep = StringUtils.indexOfIgnoreCase((int)0, (String)referencedTableName, (String)".", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL)) != -1) {
                        referencedCatalogName = referencedTableName.substring((int)0, (int)indexOfCatalogSep);
                        referencedTableName = referencedTableName.substring((int)(indexOfCatalogSep + 1));
                    }
                }
            }
            if (!firstTime) {
                commentBuf.append((String)"; ");
            } else {
                firstTime = false;
            }
            if (constraintName != null) {
                commentBuf.append((String)constraintName);
            } else {
                commentBuf.append((String)"not_available");
            }
            commentBuf.append((String)"(");
            commentBuf.append(localColumnName);
            commentBuf.append((String)") REFER ");
            commentBuf.append((String)referencedCatalogName);
            commentBuf.append((String)"/");
            commentBuf.append(referencedTableName);
            commentBuf.append((String)"(");
            commentBuf.append(referencedColumnName);
            commentBuf.append((String)")");
            int lastParenIndex = line.lastIndexOf((String)")");
            if (lastParenIndex == line.length() - 1) continue;
            String cascadeOptions = line.substring((int)(lastParenIndex + 1));
            commentBuf.append((String)" ");
            commentBuf.append((String)cascadeOptions);
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName) throws SQLException {
        ArrayList<String> tableList = new ArrayList<String>();
        ResultSet rs = null;
        java.sql.Statement stmt = null;
        if (tableName != null) {
            tableList.add(tableName);
        } else {
            try {
                rs = this.getTables((String)catalog, (String)"", (String)"%", (String[])new String[]{"TABLE"});
                while (rs.next()) {
                    tableList.add(rs.getString((String)"TABLE_NAME"));
                }
                Object var7_6 = null;
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                if (rs != null) {
                    rs.close();
                }
                rs = null;
                throw throwable;
            }
        }
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        Field[] fields = new Field[]{new Field((String)"", (String)"Name", (int)1, (int)Integer.MAX_VALUE), new Field((String)"", (String)"Type", (int)1, (int)255), new Field((String)"", (String)"Comment", (int)1, (int)Integer.MAX_VALUE)};
        int numTables = tableList.size();
        stmt = this.conn.getMetadataSafeStatement();
        try {
            for (int i = 0; i < numTables; ++i) {
                String tableToExtract = (String)tableList.get((int)i);
                String query = "SHOW CREATE TABLE " + this.getFullyQualifiedName((String)catalog, (String)tableToExtract);
                try {
                    rs = stmt.executeQuery((String)query);
                }
                catch (SQLException sqlEx) {
                    String sqlState = sqlEx.getSQLState();
                    if ("42S02".equals((Object)sqlState) || sqlEx.getErrorCode() == 1146) continue;
                    throw sqlEx;
                }
                while (rs.next()) {
                    this.extractForeignKeyForTable(rows, (ResultSet)rs, (String)catalog);
                }
            }
            Object var15_16 = null;
            if (rs != null) {
                rs.close();
            }
            rs = null;
            if (stmt != null) {
                stmt.close();
            }
            stmt = null;
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var15_17 = null;
            if (rs != null) {
                rs.close();
            }
            rs = null;
            if (stmt != null) {
                stmt.close();
            }
            stmt = null;
            throw throwable;
        }
    }

    @Override
    public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TYPE_CAT", (int)1, (int)32), new Field((String)"", (String)"TYPE_SCHEM", (int)1, (int)32), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"ATTR_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)5, (int)32), new Field((String)"", (String)"ATTR_TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"ATTR_SIZE", (int)4, (int)32), new Field((String)"", (String)"DECIMAL_DIGITS", (int)4, (int)32), new Field((String)"", (String)"NUM_PREC_RADIX", (int)4, (int)32), new Field((String)"", (String)"NULLABLE ", (int)4, (int)32), new Field((String)"", (String)"REMARKS", (int)1, (int)32), new Field((String)"", (String)"ATTR_DEF", (int)1, (int)32), new Field((String)"", (String)"SQL_DATA_TYPE", (int)4, (int)32), new Field((String)"", (String)"SQL_DATETIME_SUB", (int)4, (int)32), new Field((String)"", (String)"CHAR_OCTET_LENGTH", (int)4, (int)32), new Field((String)"", (String)"ORDINAL_POSITION", (int)4, (int)32), new Field((String)"", (String)"IS_NULLABLE", (int)1, (int)32), new Field((String)"", (String)"SCOPE_CATALOG", (int)1, (int)32), new Field((String)"", (String)"SCOPE_SCHEMA", (int)1, (int)32), new Field((String)"", (String)"SCOPE_TABLE", (int)1, (int)32), new Field((String)"", (String)"SOURCE_DATA_TYPE", (int)5, (int)32)};
        return this.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Field[] fields = new Field[]{new Field((String)"", (String)"SCOPE", (int)5, (int)5), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)32), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"COLUMN_SIZE", (int)4, (int)10), new Field((String)"", (String)"BUFFER_LENGTH", (int)4, (int)10), new Field((String)"", (String)"DECIMAL_DIGITS", (int)5, (int)10), new Field((String)"", (String)"PSEUDO_COLUMN", (int)5, (int)5)};
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)table, (java.sql.Statement)stmt, rows){
                final /* synthetic */ String val$table;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$table = string;
                    this.val$stmt = statement;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * Exception decompiling
                 */
                void forEach(String catalogStr) throws SQLException {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 4[CATCHBLOCK]
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:475)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:73)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.MemberFunctionInvokation.dumpInner(MemberFunctionInvokation.java:51)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:74)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.dump(StructuredExpressionStatement.java:27)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredTry.dump(StructuredTry.java:72)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:141)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:494)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:87)
                    // org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1016)
                    // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:231)
                    // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                    // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                    // org.benf.cfr.reader.Main.main(Main.java:48)
                    // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
                    // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
                    throw new IllegalStateException("Decompilation failed");
                }
            }.doForAll();
            Object var10_9 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    /*
     * Exception decompiling
     */
    private void getCallStmtParameterTypes(String catalog, String quotedProcName, ProcedureType procType, String parameterNamePattern, List<ResultSetRow> resultRows, boolean forGetFunctionColumns) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[TRYBLOCK]], but top level block is 13[CATCHBLOCK]
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

    private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar) throws SQLException {
        int currentPos = beginIndex + 1;
        int parenDepth = 1;
        while (parenDepth > 0) {
            if (currentPos >= procedureDef.length()) return currentPos;
            int closedParenIndex = StringUtils.indexOfIgnoreCase((int)currentPos, (String)procedureDef, (String)")", (String)quoteChar, (String)quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            if (closedParenIndex == -1) throw SQLError.createSQLException((String)"Internal error when parsing callable statement metadata", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
            int nextOpenParenIndex = StringUtils.indexOfIgnoreCase((int)currentPos, (String)procedureDef, (String)"(", (String)quoteChar, (String)quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            if (nextOpenParenIndex != -1 && nextOpenParenIndex < closedParenIndex) {
                ++parenDepth;
                currentPos = closedParenIndex + 1;
                continue;
            }
            --parenDepth;
            currentPos = closedParenIndex;
        }
        return currentPos;
    }

    private int findEndOfReturnsClause(String procedureDefn, int positionOfReturnKeyword) throws SQLException {
        int i;
        String openingMarkers = this.quotedId + "(";
        String closingMarkers = this.quotedId + ")";
        String[] tokens = new String[]{"LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN"};
        int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
        int endOfReturn = -1;
        for (i = 0; i < tokens.length; ++i) {
            int nextEndOfReturn = StringUtils.indexOfIgnoreCase((int)startLookingAt, (String)procedureDefn, (String)tokens[i], (String)openingMarkers, (String)closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            if (nextEndOfReturn == -1 || endOfReturn != -1 && nextEndOfReturn >= endOfReturn) continue;
            endOfReturn = nextEndOfReturn;
        }
        if (endOfReturn != -1) {
            return endOfReturn;
        }
        endOfReturn = StringUtils.indexOfIgnoreCase((int)startLookingAt, (String)procedureDefn, (String)":", (String)openingMarkers, (String)closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
        if (endOfReturn == -1) throw SQLError.createSQLException((String)"Internal error when parsing callable statement metadata", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        i = endOfReturn;
        while (i > 0) {
            if (Character.isWhitespace((char)procedureDefn.charAt((int)i))) {
                return i;
            }
            --i;
        }
        throw SQLError.createSQLException((String)"Internal error when parsing callable statement metadata", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private int getCascadeDeleteOption(String cascadeOptions) {
        int onDeletePos = cascadeOptions.indexOf((String)"ON DELETE");
        if (onDeletePos == -1) return 3;
        String deleteOptions = cascadeOptions.substring((int)onDeletePos, (int)cascadeOptions.length());
        if (deleteOptions.startsWith((String)"ON DELETE CASCADE")) {
            return 0;
        }
        if (deleteOptions.startsWith((String)"ON DELETE SET NULL")) {
            return 2;
        }
        if (deleteOptions.startsWith((String)"ON DELETE RESTRICT")) {
            return 1;
        }
        if (!deleteOptions.startsWith((String)"ON DELETE NO ACTION")) return 3;
        return 3;
    }

    private int getCascadeUpdateOption(String cascadeOptions) {
        int onUpdatePos = cascadeOptions.indexOf((String)"ON UPDATE");
        if (onUpdatePos == -1) return 3;
        String updateOptions = cascadeOptions.substring((int)onUpdatePos, (int)cascadeOptions.length());
        if (updateOptions.startsWith((String)"ON UPDATE CASCADE")) {
            return 0;
        }
        if (updateOptions.startsWith((String)"ON UPDATE SET NULL")) {
            return 2;
        }
        if (updateOptions.startsWith((String)"ON UPDATE RESTRICT")) {
            return 1;
        }
        if (!updateOptions.startsWith((String)"ON UPDATE NO ACTION")) return 3;
        return 3;
    }

    protected IteratorWithCleanup<String> getCatalogIterator(String catalogSpec) throws SQLException {
        if (catalogSpec != null) {
            if (catalogSpec.equals((Object)"")) {
                return new SingleStringIterator((DatabaseMetaData)this, (String)this.database);
            }
            if (!this.conn.getPedantic()) return new SingleStringIterator((DatabaseMetaData)this, (String)StringUtils.unQuoteIdentifier((String)catalogSpec, (String)this.quotedId));
            return new SingleStringIterator((DatabaseMetaData)this, (String)catalogSpec);
        }
        if (!this.conn.getNullCatalogMeansCurrent()) return new ResultSetIterator((DatabaseMetaData)this, (ResultSet)this.getCatalogs(), (int)1);
        return new SingleStringIterator((DatabaseMetaData)this, (String)this.database);
    }

    /*
     * Exception decompiling
     */
    @Override
    public ResultSet getCatalogs() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
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
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)64), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)1), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)64), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)64), new Field((String)"", (String)"GRANTOR", (int)1, (int)77), new Field((String)"", (String)"GRANTEE", (int)1, (int)77), new Field((String)"", (String)"PRIVILEGE", (int)1, (int)64), new Field((String)"", (String)"IS_GRANTABLE", (int)1, (int)3)};
        grantQuery = "SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv FROM mysql.columns_priv c, mysql.tables_priv t WHERE c.host = t.host AND c.db = t.db AND c.table_name = t.table_name AND c.db LIKE ? AND c.table_name = ? AND c.column_name LIKE ?";
        pStmt = null;
        results = null;
        grantRows = new ArrayList<ResultSetRow>();
        try {
            block12 : {
                block13 : {
                    pStmt = this.prepareMetaDataSafeStatement((String)grantQuery);
                    pStmt.setString((int)1, (String)(catalog != null && catalog.length() != 0 ? catalog : "%"));
                    pStmt.setString((int)2, (String)table);
                    pStmt.setString((int)3, (String)columnNamePattern);
                    results = pStmt.executeQuery();
                    block8 : do lbl-1000: // 3 sources:
                    {
                        block16 : {
                            block15 : {
                                if (!results.next()) break block15;
                                host = results.getString((int)1);
                                db = results.getString((int)2);
                                grantor = results.getString((int)3);
                                user = results.getString((int)4);
                                if (user == null || user.length() == 0) {
                                    user = "%";
                                }
                                fullUser = new StringBuilder((String)user);
                                if (host != null && this.conn.getUseHostsInPrivileges()) {
                                    fullUser.append((String)"@");
                                    fullUser.append((String)host);
                                }
                                columnName = results.getString((int)6);
                                allPrivileges = results.getString((int)7);
                                if (allPrivileges == null) ** GOTO lbl-1000
                                break block16;
                            }
                            var21_20 = null;
                            if (results == null) break block12;
                            results.close();
                            break block13;
                        }
                        allPrivileges = allPrivileges.toUpperCase((Locale)Locale.ENGLISH);
                        st = new StringTokenizer((String)allPrivileges, (String)",");
                        do {
                            if (!st.hasMoreTokens()) continue block8;
                            privilege = st.nextToken().trim();
                            tuple = new byte[][]{this.s2b((String)db), null, this.s2b((String)table), this.s2b((String)columnName), grantor != null ? this.s2b((String)grantor) : null, this.s2b((String)fullUser.toString()), this.s2b((String)privilege), null};
                            grantRows.add((ResultSetRow)new ByteArrayRow((byte[][])tuple, (ExceptionInterceptor)this.getExceptionInterceptor()));
                        } while (true);
                        break;
                    } while (true);
                    catch (Exception ex) {
                        // empty catch block
                    }
                }
                results = null;
            }
            if (pStmt == null) return this.buildResultSet((Field[])fields, grantRows);
            try {
                pStmt.close();
            }
            catch (Exception ex) {
                // empty catch block
            }
            pStmt = null;
            return this.buildResultSet((Field[])fields, grantRows);
        }
        catch (Throwable var20_24) {
            block19 : {
                block17 : {
                    block18 : {
                        var21_21 = null;
                        if (results == null) break block17;
                        ** try [egrp 1[TRYBLOCK] [2 : 571->581)] { 
lbl65: // 1 sources:
                        results.close();
                        break block18;
lbl67: // 1 sources:
                        catch (Exception ex) {
                            // empty catch block
                        }
                    }
                    results = null;
                }
                if (pStmt == null) throw var20_24;
                ** try [egrp 2[TRYBLOCK] [3 : 591->601)] { 
lbl74: // 1 sources:
                pStmt.close();
                break block19;
lbl76: // 1 sources:
                catch (Exception ex) {
                    // empty catch block
                }
            }
            pStmt = null;
            throw var20_24;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        if (columnNamePattern == null) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Column name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            columnNamePattern = "%";
        }
        String colPattern = columnNamePattern;
        Field[] fields = this.createColumnsFields();
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)tableNamePattern, (String)schemaPattern, (String)colPattern, (java.sql.Statement)stmt, rows){
                final /* synthetic */ String val$tableNamePattern;
                final /* synthetic */ String val$schemaPattern;
                final /* synthetic */ String val$colPattern;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$tableNamePattern = string;
                    this.val$schemaPattern = string2;
                    this.val$colPattern = string3;
                    this.val$stmt = statement;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * Exception decompiling
                 */
                void forEach(String catalogStr) throws SQLException {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 2[TRYBLOCK]
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:475)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:73)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.MemberFunctionInvokation.dumpInner(MemberFunctionInvokation.java:51)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:74)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.dump(StructuredExpressionStatement.java:27)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredTry.dump(StructuredTry.java:72)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:141)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:494)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:87)
                    // org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1016)
                    // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:231)
                    // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                    // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                    // org.benf.cfr.reader.Main.main(Main.java:48)
                    // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
                    // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
                    throw new IllegalStateException("Decompilation failed");
                }
            }.doForAll();
            Object var10_9 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    protected Field[] createColumnsFields() {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)255), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)255), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)5), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)16), new Field((String)"", (String)"COLUMN_SIZE", (int)4, (int)Integer.toString((int)Integer.MAX_VALUE).length()), new Field((String)"", (String)"BUFFER_LENGTH", (int)4, (int)10), new Field((String)"", (String)"DECIMAL_DIGITS", (int)4, (int)10), new Field((String)"", (String)"NUM_PREC_RADIX", (int)4, (int)10), new Field((String)"", (String)"NULLABLE", (int)4, (int)10), new Field((String)"", (String)"REMARKS", (int)1, (int)0), new Field((String)"", (String)"COLUMN_DEF", (int)1, (int)0), new Field((String)"", (String)"SQL_DATA_TYPE", (int)4, (int)10), new Field((String)"", (String)"SQL_DATETIME_SUB", (int)4, (int)10), new Field((String)"", (String)"CHAR_OCTET_LENGTH", (int)4, (int)Integer.toString((int)Integer.MAX_VALUE).length()), new Field((String)"", (String)"ORDINAL_POSITION", (int)4, (int)10), new Field((String)"", (String)"IS_NULLABLE", (int)1, (int)3), new Field((String)"", (String)"SCOPE_CATALOG", (int)1, (int)255), new Field((String)"", (String)"SCOPE_SCHEMA", (int)1, (int)255), new Field((String)"", (String)"SCOPE_TABLE", (int)1, (int)255), new Field((String)"", (String)"SOURCE_DATA_TYPE", (int)5, (int)10), new Field((String)"", (String)"IS_AUTOINCREMENT", (int)1, (int)3), new Field((String)"", (String)"IS_GENERATEDCOLUMN", (int)1, (int)3)};
        return fields;
    }

    @Override
    public java.sql.Connection getConnection() throws SQLException {
        return this.conn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        if (primaryTable == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Field[] fields = this.createFkMetadataFields();
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        if (!this.conn.versionMeetsMinimum((int)3, (int)23, (int)0)) return this.buildResultSet((Field[])fields, tuples);
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)foreignCatalog), (java.sql.Statement)stmt, (String)foreignTable, (String)primaryTable, (String)foreignCatalog, (String)foreignSchema, (String)primaryCatalog, (String)primarySchema, tuples){
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ String val$foreignTable;
                final /* synthetic */ String val$primaryTable;
                final /* synthetic */ String val$foreignCatalog;
                final /* synthetic */ String val$foreignSchema;
                final /* synthetic */ String val$primaryCatalog;
                final /* synthetic */ String val$primarySchema;
                final /* synthetic */ ArrayList val$tuples;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$stmt = statement;
                    this.val$foreignTable = string;
                    this.val$primaryTable = string2;
                    this.val$foreignCatalog = string3;
                    this.val$foreignSchema = string4;
                    this.val$primaryCatalog = string5;
                    this.val$primarySchema = string6;
                    this.val$tuples = arrayList;
                    super(x0);
                }

                /*
                 * Exception decompiling
                 */
                void forEach(String catalogStr) throws SQLException {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[DOLOOP]], but top level block is 7[UNCONDITIONALDOLOOP]
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:475)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:73)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.MemberFunctionInvokation.dumpInner(MemberFunctionInvokation.java:51)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:74)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.dump(StructuredExpressionStatement.java:27)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredTry.dump(StructuredTry.java:72)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:141)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:494)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:87)
                    // org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1016)
                    // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:231)
                    // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                    // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                    // org.benf.cfr.reader.Main.main(Main.java:48)
                    // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
                    // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
                    throw new IllegalStateException("Decompilation failed");
                }
            }.doForAll();
            Object var11_10 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, tuples);
            stmt.close();
            return this.buildResultSet((Field[])fields, tuples);
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    protected Field[] createFkMetadataFields() {
        Field[] fields = new Field[]{new Field((String)"", (String)"PKTABLE_CAT", (int)1, (int)255), new Field((String)"", (String)"PKTABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"PKTABLE_NAME", (int)1, (int)255), new Field((String)"", (String)"PKCOLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"FKTABLE_CAT", (int)1, (int)255), new Field((String)"", (String)"FKTABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"FKTABLE_NAME", (int)1, (int)255), new Field((String)"", (String)"FKCOLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"KEY_SEQ", (int)5, (int)2), new Field((String)"", (String)"UPDATE_RULE", (int)5, (int)2), new Field((String)"", (String)"DELETE_RULE", (int)5, (int)2), new Field((String)"", (String)"FK_NAME", (int)1, (int)0), new Field((String)"", (String)"PK_NAME", (int)1, (int)0), new Field((String)"", (String)"DEFERRABILITY", (int)5, (int)2)};
        return fields;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return this.conn.getServerMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return this.conn.getServerMinorVersion();
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return "MySQL";
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return this.conn.getServerVersion();
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        if (!this.conn.supportsIsolationLevel()) return 0;
        return 2;
    }

    @Override
    public int getDriverMajorVersion() {
        return NonRegisteringDriver.getMajorVersionInternal();
    }

    @Override
    public int getDriverMinorVersion() {
        return NonRegisteringDriver.getMinorVersionInternal();
    }

    @Override
    public String getDriverName() throws SQLException {
        return "MySQL Connector Java";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return "mysql-connector-java-5.1.48 ( Revision: 29734982609c32d3ab7e5cac2e6acee69ff6b4aa )";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Field[] fields = this.createFkMetadataFields();
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        if (!this.conn.versionMeetsMinimum((int)3, (int)23, (int)0)) return this.buildResultSet((Field[])fields, rows);
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (java.sql.Statement)stmt, (String)table, rows){
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ String val$table;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$stmt = statement;
                    this.val$table = string;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 * Enabled unnecessary exception pruning
                 */
                void forEach(String catalogStr) throws SQLException {
                    fkresults = null;
                    try {
                        if (this.this$0.conn.versionMeetsMinimum((int)3, (int)23, (int)50)) {
                            fkresults = this.this$0.extractForeignKeyFromCreateTable((String)catalogStr, null);
                        } else {
                            queryBuf = new StringBuilder((String)"SHOW TABLE STATUS FROM ");
                            queryBuf.append((String)StringUtils.quoteIdentifier((String)catalogStr, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                            fkresults = this.val$stmt.executeQuery((String)queryBuf.toString());
                        }
                        tableNameWithCase = this.this$0.getTableNameWithCase((String)this.val$table);
                        while (fkresults.next()) {
                            tableType = fkresults.getString((String)"Type");
                            if (tableType == null || !tableType.equalsIgnoreCase((String)"innodb") && !tableType.equalsIgnoreCase((String)"SUPPORTS_FK") || (comment = fkresults.getString((String)"Comment").trim()) == null || !(commentTokens = new StringTokenizer((String)comment, (String)";", (boolean)false)).hasMoreTokens()) continue;
                            commentTokens.nextToken();
                            while (commentTokens.hasMoreTokens()) {
                                keys = commentTokens.nextToken();
                                this.this$0.getExportKeyResults((String)catalogStr, (String)tableNameWithCase, (String)keys, (List<ResultSetRow>)this.val$rows, (String)fkresults.getString((String)"Name"));
                            }
                        }
                        var9_8 = null;
                        if (fkresults == null) return;
                        try {
                            fkresults.close();
                            return;
                        }
                        catch (SQLException sqlEx) {
                            AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                        }
                        return;
                    }
                    catch (Throwable var8_12) {
                        block9 : {
                            var9_9 = null;
                            if (fkresults == null) throw var8_12;
                            ** try [egrp 1[TRYBLOCK] [2 : 253->262)] { 
lbl34: // 1 sources:
                            fkresults.close();
                            break block9;
lbl36: // 1 sources:
                            catch (SQLException sqlEx) {
                                AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                            }
                        }
                        fkresults = null;
                        throw var8_12;
                    }
                }
            }.doForAll();
            Object var8_7 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    protected void getExportKeyResults(String catalog, String exportingTable, String keysComment, List<ResultSetRow> tuples, String fkTableName) throws SQLException {
        this.getResultsImpl((String)catalog, (String)exportingTable, (String)keysComment, tuples, (String)fkTableName, (boolean)true);
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "#@";
    }

    protected int[] getForeignKeyActions(String commentString) {
        int[] actions = new int[]{3, 3};
        int lastParenIndex = commentString.lastIndexOf((String)")");
        if (lastParenIndex == commentString.length() - 1) return actions;
        String cascadeOptions = commentString.substring((int)(lastParenIndex + 1)).trim().toUpperCase((Locale)Locale.ENGLISH);
        actions[0] = this.getCascadeDeleteOption((String)cascadeOptions);
        actions[1] = this.getCascadeUpdateOption((String)cascadeOptions);
        return actions;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        if (!this.conn.supportsQuotedIdentifiers()) return " ";
        if (!this.conn.useAnsiQuotedIdentifiers()) return "`";
        return "\"";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Field[] fields = this.createFkMetadataFields();
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        if (!this.conn.versionMeetsMinimum((int)3, (int)23, (int)0)) return this.buildResultSet((Field[])fields, rows);
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)table, (java.sql.Statement)stmt, rows){
                final /* synthetic */ String val$table;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$table = string;
                    this.val$stmt = statement;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 * Enabled unnecessary exception pruning
                 */
                void forEach(String catalogStr) throws SQLException {
                    fkresults = null;
                    try {
                        if (this.this$0.conn.versionMeetsMinimum((int)3, (int)23, (int)50)) {
                            fkresults = this.this$0.extractForeignKeyFromCreateTable((String)catalogStr, (String)this.val$table);
                        } else {
                            queryBuf = new StringBuilder((String)"SHOW TABLE STATUS ");
                            queryBuf.append((String)" FROM ");
                            queryBuf.append((String)StringUtils.quoteIdentifier((String)catalogStr, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                            queryBuf.append((String)" LIKE ");
                            queryBuf.append((String)StringUtils.quoteIdentifier((String)this.val$table, (String)"'", (boolean)true));
                            fkresults = this.val$stmt.executeQuery((String)queryBuf.toString());
                        }
                        while (fkresults.next()) {
                            tableType = fkresults.getString((String)"Type");
                            if (tableType == null || !tableType.equalsIgnoreCase((String)"innodb") && !tableType.equalsIgnoreCase((String)"SUPPORTS_FK") || (comment = fkresults.getString((String)"Comment").trim()) == null || !(commentTokens = new StringTokenizer((String)comment, (String)";", (boolean)false)).hasMoreTokens()) continue;
                            commentTokens.nextToken();
                            while (commentTokens.hasMoreTokens()) {
                                keys = commentTokens.nextToken();
                                this.this$0.getImportKeyResults((String)catalogStr, (String)this.val$table, (String)keys, (List<ResultSetRow>)this.val$rows);
                            }
                        }
                        var8_7 = null;
                        if (fkresults == null) return;
                        try {
                            fkresults.close();
                            return;
                        }
                        catch (SQLException sqlEx) {
                            AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                        }
                        return;
                    }
                    catch (Throwable var7_11) {
                        block9 : {
                            var8_8 = null;
                            if (fkresults == null) throw var7_11;
                            ** try [egrp 1[TRYBLOCK] [2 : 264->273)] { 
lbl39: // 1 sources:
                            fkresults.close();
                            break block9;
lbl41: // 1 sources:
                            catch (SQLException sqlEx) {
                                AssertionFailedException.shouldNotHappen((Exception)sqlEx);
                            }
                        }
                        fkresults = null;
                        throw var7_11;
                    }
                }
            }.doForAll();
            Object var8_7 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    protected void getImportKeyResults(String catalog, String importingTable, String keysComment, List<ResultSetRow> tuples) throws SQLException {
        this.getResultsImpl((String)catalog, (String)importingTable, (String)keysComment, tuples, null, (boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        ResultSet resultSet;
        Field[] fields = this.createIndexInfoFields();
        TreeMap<K, V> sortedRows = new TreeMap<K, V>();
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            ResultSet indexInfo;
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)table, (java.sql.Statement)stmt, (boolean)unique, sortedRows){
                final /* synthetic */ String val$table;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ boolean val$unique;
                final /* synthetic */ SortedMap val$sortedRows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$table = string;
                    this.val$stmt = statement;
                    this.val$unique = bl;
                    this.val$sortedRows = sortedMap;
                    super(x0);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 * Enabled unnecessary exception pruning
                 */
                void forEach(String catalogStr) throws SQLException {
                    results = null;
                    try {
                        block10 : {
                            queryBuf = new StringBuilder((String)"SHOW INDEX FROM ");
                            queryBuf.append((String)StringUtils.quoteIdentifier((String)this.val$table, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                            queryBuf.append((String)" FROM ");
                            queryBuf.append((String)StringUtils.quoteIdentifier((String)catalogStr, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                            try {
                                results = this.val$stmt.executeQuery((String)queryBuf.toString());
                            }
                            catch (SQLException sqlEx) {
                                errorCode = sqlEx.getErrorCode();
                                if ("42S02".equals((Object)sqlEx.getSQLState()) || errorCode == 1146) break block10;
                                throw sqlEx;
                            }
                        }
                        while (results != null && results.next()) {
                            row = new byte[14][];
                            row[0] = catalogStr == null ? new byte[0] : this.this$0.s2b((String)catalogStr);
                            row[1] = null;
                            row[2] = results.getBytes((String)"Table");
                            indexIsUnique = results.getInt((String)"Non_unique") == 0;
                            row[3] = indexIsUnique == false ? this.this$0.s2b((String)"true") : this.this$0.s2b((String)"false");
                            row[4] = new byte[0];
                            row[5] = results.getBytes((String)"Key_name");
                            indexType = 3;
                            row[6] = Integer.toString((int)indexType).getBytes();
                            row[7] = results.getBytes((String)"Seq_in_index");
                            row[8] = results.getBytes((String)"Column_name");
                            row[9] = results.getBytes((String)"Collation");
                            cardinality = results.getLong((String)"Cardinality");
                            if (!Util.isJdbc42() && cardinality > Integer.MAX_VALUE) {
                                cardinality = Integer.MAX_VALUE;
                            }
                            row[10] = this.this$0.s2b((String)String.valueOf((long)cardinality));
                            row[11] = this.this$0.s2b((String)"0");
                            row[12] = null;
                            indexInfoKey = new com.mysql.jdbc.DatabaseMetaData$IndexMetaDataKey((DatabaseMetaData)this.this$0, (boolean)(indexIsUnique == false), (short)indexType, (String)results.getString((String)"Key_name").toLowerCase(), (short)results.getShort((String)"Seq_in_index"));
                            if (this.val$unique) {
                                if (!indexIsUnique) continue;
                                this.val$sortedRows.put(indexInfoKey, new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                continue;
                            }
                            this.val$sortedRows.put(indexInfoKey, new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                        }
                        var11_10 = null;
                        if (results == null) return;
                        try {
                            results.close();
                            return;
                        }
                        catch (Exception ex) {
                            // empty catch block
                        }
                        return;
                    }
                    catch (Throwable var10_14) {
                        block11 : {
                            var11_11 = null;
                            if (results == null) throw var10_14;
                            ** try [egrp 2[TRYBLOCK] [3 : 514->523)] { 
lbl60: // 1 sources:
                            results.close();
                            break block11;
lbl62: // 1 sources:
                            catch (Exception ex) {
                                // empty catch block
                            }
                        }
                        results = null;
                        throw var10_14;
                    }
                }
            }.doForAll();
            Iterator<V> sortedRowsIterator = sortedRows.values().iterator();
            while (sortedRowsIterator.hasNext()) {
                rows.add(sortedRowsIterator.next());
            }
            resultSet = indexInfo = this.buildResultSet((Field[])fields, rows);
            Object var14_13 = null;
            if (stmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            Object var14_14 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
        stmt.close();
        return resultSet;
    }

    protected Field[] createIndexInfoFields() {
        Field[] fields = new Field[13];
        fields[0] = new Field((String)"", (String)"TABLE_CAT", (int)1, (int)255);
        fields[1] = new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)0);
        fields[2] = new Field((String)"", (String)"TABLE_NAME", (int)1, (int)255);
        fields[3] = new Field((String)"", (String)"NON_UNIQUE", (int)16, (int)4);
        fields[4] = new Field((String)"", (String)"INDEX_QUALIFIER", (int)1, (int)1);
        fields[5] = new Field((String)"", (String)"INDEX_NAME", (int)1, (int)32);
        fields[6] = new Field((String)"", (String)"TYPE", (int)5, (int)32);
        fields[7] = new Field((String)"", (String)"ORDINAL_POSITION", (int)5, (int)5);
        fields[8] = new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32);
        fields[9] = new Field((String)"", (String)"ASC_OR_DESC", (int)1, (int)1);
        if (Util.isJdbc42()) {
            fields[10] = new Field((String)"", (String)"CARDINALITY", (int)-5, (int)20);
            fields[11] = new Field((String)"", (String)"PAGES", (int)-5, (int)20);
        } else {
            fields[10] = new Field((String)"", (String)"CARDINALITY", (int)4, (int)20);
            fields[11] = new Field((String)"", (String)"PAGES", (int)4, (int)10);
        }
        fields[12] = new Field((String)"", (String)"FILTER_CONDITION", (int)1, (int)32);
        return fields;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 16777208;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 32;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 16777208;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 64;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 64;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 64;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 256;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 512;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 64;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 256;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 2147483639;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return MysqlIO.getMaxBuf() - 4;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 64;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 256;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 16;
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)255), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)255), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"KEY_SEQ", (int)5, (int)5), new Field((String)"", (String)"PK_NAME", (int)1, (int)32)};
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)table, (java.sql.Statement)stmt, rows){
                final /* synthetic */ String val$table;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$table = string;
                    this.val$stmt = statement;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 * Enabled unnecessary exception pruning
                 */
                void forEach(String catalogStr) throws SQLException {
                    rs = null;
                    try {
                        queryBuf = new StringBuilder((String)"SHOW KEYS FROM ");
                        queryBuf.append((String)StringUtils.quoteIdentifier((String)this.val$table, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                        queryBuf.append((String)" FROM ");
                        queryBuf.append((String)StringUtils.quoteIdentifier((String)catalogStr, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()));
                        rs = this.val$stmt.executeQuery((String)queryBuf.toString());
                        sortMap = new TreeMap<String, byte[][]>();
                        while (rs.next()) {
                            keyType = rs.getString((String)"Key_name");
                            if (keyType == null || !keyType.equalsIgnoreCase((String)"PRIMARY") && !keyType.equalsIgnoreCase((String)"PRI")) continue;
                            tuple = new byte[6][];
                            tuple[0] = catalogStr == null ? new byte[0] : this.this$0.s2b((String)catalogStr);
                            tuple[1] = null;
                            tuple[2] = this.this$0.s2b((String)this.val$table);
                            columnName = rs.getString((String)"Column_name");
                            tuple[3] = this.this$0.s2b((String)columnName);
                            tuple[4] = this.this$0.s2b((String)rs.getString((String)"Seq_in_index"));
                            tuple[5] = this.this$0.s2b((String)keyType);
                            sortMap.put(columnName, tuple);
                        }
                        sortedIterator = sortMap.values().iterator();
                        while (sortedIterator.hasNext()) {
                            this.val$rows.add(new ByteArrayRow((byte[][])((byte[][])sortedIterator.next()), (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                        }
                        var9_8 = null;
                        if (rs == null) return;
                        try {
                            rs.close();
                            return;
                        }
                        catch (Exception ex) {
                            // empty catch block
                        }
                        return;
                    }
                    catch (Throwable var8_12) {
                        block7 : {
                            var9_9 = null;
                            if (rs == null) throw var8_12;
                            ** try [egrp 1[TRYBLOCK] [2 : 339->348)] { 
lbl44: // 1 sources:
                            rs.close();
                            break block7;
lbl46: // 1 sources:
                            catch (Exception ex) {
                                // empty catch block
                            }
                        }
                        rs = null;
                        throw var8_12;
                    }
                }
            }.doForAll();
            Object var8_7 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        Field[] fields = this.createProcedureColumnsFields();
        return this.getProcedureOrFunctionColumns((Field[])fields, (String)catalog, (String)schemaPattern, (String)procedureNamePattern, (String)columnNamePattern, (boolean)true, (boolean)true);
    }

    protected Field[] createProcedureColumnsFields() {
        Field[] fields = new Field[]{new Field((String)"", (String)"PROCEDURE_CAT", (int)1, (int)512), new Field((String)"", (String)"PROCEDURE_SCHEM", (int)1, (int)512), new Field((String)"", (String)"PROCEDURE_NAME", (int)1, (int)512), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)512), new Field((String)"", (String)"COLUMN_TYPE", (int)1, (int)64), new Field((String)"", (String)"DATA_TYPE", (int)5, (int)6), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)64), new Field((String)"", (String)"PRECISION", (int)4, (int)12), new Field((String)"", (String)"LENGTH", (int)4, (int)12), new Field((String)"", (String)"SCALE", (int)5, (int)12), new Field((String)"", (String)"RADIX", (int)5, (int)6), new Field((String)"", (String)"NULLABLE", (int)5, (int)6), new Field((String)"", (String)"REMARKS", (int)1, (int)512), new Field((String)"", (String)"COLUMN_DEF", (int)1, (int)512), new Field((String)"", (String)"SQL_DATA_TYPE", (int)4, (int)12), new Field((String)"", (String)"SQL_DATETIME_SUB", (int)4, (int)12), new Field((String)"", (String)"CHAR_OCTET_LENGTH", (int)4, (int)12), new Field((String)"", (String)"ORDINAL_POSITION", (int)4, (int)12), new Field((String)"", (String)"IS_NULLABLE", (int)1, (int)512), new Field((String)"", (String)"SPECIFIC_NAME", (int)1, (int)512)};
        return fields;
    }

    /*
     * Exception decompiling
     */
    protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
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
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        Field[] fields = this.createFieldMetadataForGetProcedures();
        return this.getProceduresAndOrFunctions((Field[])fields, (String)catalog, (String)schemaPattern, (String)procedureNamePattern, (boolean)true, (boolean)true);
    }

    protected Field[] createFieldMetadataForGetProcedures() {
        Field[] fields = new Field[]{new Field((String)"", (String)"PROCEDURE_CAT", (int)1, (int)255), new Field((String)"", (String)"PROCEDURE_SCHEM", (int)1, (int)255), new Field((String)"", (String)"PROCEDURE_NAME", (int)1, (int)255), new Field((String)"", (String)"reserved1", (int)1, (int)0), new Field((String)"", (String)"reserved2", (int)1, (int)0), new Field((String)"", (String)"reserved3", (int)1, (int)0), new Field((String)"", (String)"REMARKS", (int)1, (int)255), new Field((String)"", (String)"PROCEDURE_TYPE", (int)5, (int)6), new Field((String)"", (String)"SPECIFIC_NAME", (int)1, (int)255)};
        return fields;
    }

    protected ResultSet getProceduresAndOrFunctions(Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
        if (procedureNamePattern == null || procedureNamePattern.length() == 0) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Procedure name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            procedureNamePattern = "%";
        }
        ArrayList<ResultSetRow> procedureRows = new ArrayList<ResultSetRow>();
        if (!this.supportsStoredProcedures()) return this.buildResultSet((Field[])fields, procedureRows);
        String procNamePattern = procedureNamePattern;
        ArrayList<E> procedureRowsToSort = new ArrayList<E>();
        new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (boolean)returnProcedures, (boolean)returnFunctions, (String)procNamePattern, procedureRowsToSort, (Field[])fields){
            final /* synthetic */ boolean val$returnProcedures;
            final /* synthetic */ boolean val$returnFunctions;
            final /* synthetic */ String val$procNamePattern;
            final /* synthetic */ List val$procedureRowsToSort;
            final /* synthetic */ Field[] val$fields;
            final /* synthetic */ DatabaseMetaData this$0;
            {
                this.this$0 = databaseMetaData;
                this.val$returnProcedures = bl;
                this.val$returnFunctions = bl2;
                this.val$procNamePattern = string;
                this.val$procedureRowsToSort = list;
                this.val$fields = arrfield;
                super(x0);
            }

            /*
             * Exception decompiling
             */
            void forEach(String catalogStr) throws SQLException {
                // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 7[CATCHBLOCK]
                // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
                // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
                // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
                // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
                // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                // org.benf.cfr.reader.entities.Method.dump(Method.java:475)
                // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                // org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:73)
                // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                // org.benf.cfr.reader.bytecode.analysis.parse.expression.MemberFunctionInvokation.dumpInner(MemberFunctionInvokation.java:51)
                // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:74)
                // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.dump(StructuredExpressionStatement.java:27)
                // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                // org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:141)
                // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                // org.benf.cfr.reader.entities.Method.dump(Method.java:494)
                // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:87)
                // org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1016)
                // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:231)
                // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                // org.benf.cfr.reader.Main.main(Main.java:48)
                // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
                // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
                throw new IllegalStateException("Decompilation failed");
            }
        }.doForAll();
        Collections.sort(procedureRowsToSort);
        Iterator<E> i$ = procedureRowsToSort.iterator();
        while (i$.hasNext()) {
            ComparableWrapper procRow = (ComparableWrapper)i$.next();
            procedureRows.add(procRow.getValue());
        }
        return this.buildResultSet((Field[])fields, procedureRows);
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE";
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    private void getResultsImpl(String catalog, String table, String keysComment, List<ResultSetRow> tuples, String fkTableName, boolean isExport) throws SQLException {
        LocalAndReferencedColumns parsedInfo = this.parseTableStatusIntoLocalAndReferencedColumns((String)keysComment);
        if (isExport && !parsedInfo.referencedTable.equals((Object)table)) {
            return;
        }
        if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size()) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, number of local and referenced columns is not the same.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Iterator<String> localColumnNames = parsedInfo.localColumnsList.iterator();
        Iterator<String> referColumnNames = parsedInfo.referencedColumnsList.iterator();
        int keySeqIndex = 1;
        while (localColumnNames.hasNext()) {
            byte[][] tuple = new byte[14][];
            String lColumnName = StringUtils.unQuoteIdentifier((String)localColumnNames.next(), (String)this.quotedId);
            String rColumnName = StringUtils.unQuoteIdentifier((String)referColumnNames.next(), (String)this.quotedId);
            tuple[4] = catalog == null ? new byte[0] : this.s2b((String)catalog);
            tuple[5] = null;
            tuple[6] = this.s2b((String)(isExport ? fkTableName : table));
            tuple[7] = this.s2b((String)lColumnName);
            tuple[0] = this.s2b((String)parsedInfo.referencedCatalog);
            tuple[1] = null;
            tuple[2] = this.s2b((String)(isExport ? table : parsedInfo.referencedTable));
            tuple[3] = this.s2b((String)rColumnName);
            tuple[8] = this.s2b((String)Integer.toString((int)keySeqIndex++));
            int[] actions = this.getForeignKeyActions((String)keysComment);
            tuple[9] = this.s2b((String)Integer.toString((int)actions[1]));
            tuple[10] = this.s2b((String)Integer.toString((int)actions[0]));
            tuple[11] = this.s2b((String)parsedInfo.constraintName);
            tuple[12] = null;
            tuple[13] = this.s2b((String)Integer.toString((int)7));
            tuples.add((ResultSetRow)new ByteArrayRow((byte[][])tuple, (ExceptionInterceptor)this.getExceptionInterceptor()));
        }
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"TABLE_CATALOG", (int)1, (int)0)};
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        return this.buildResultSet((Field[])fields, tuples);
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return "";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSQLKeywords() throws SQLException {
        if (mysqlKeywords != null) {
            return mysqlKeywords;
        }
        Class<DatabaseMetaData> class_ = DatabaseMetaData.class;
        // MONITORENTER : com.mysql.jdbc.DatabaseMetaData.class
        if (mysqlKeywords != null) {
            // MONITOREXIT : class_
            return mysqlKeywords;
        }
        TreeSet<E> mysqlKeywordSet = new TreeSet<E>();
        StringBuilder mysqlKeywordsBuffer = new StringBuilder();
        Collections.addAll(mysqlKeywordSet, MYSQL_KEYWORDS);
        mysqlKeywordSet.removeAll(Arrays.asList(Util.isJdbc4() ? SQL2003_KEYWORDS : SQL92_KEYWORDS));
        Iterator<E> i$ = mysqlKeywordSet.iterator();
        do {
            if (!i$.hasNext()) {
                mysqlKeywords = mysqlKeywordsBuffer.substring((int)1);
                // MONITOREXIT : class_
                return mysqlKeywords;
            }
            String keyword = (String)i$.next();
            mysqlKeywordsBuffer.append((String)",").append((String)keyword);
        } while (true);
    }

    @Override
    public int getSQLStateType() throws SQLException {
        if (this.conn.versionMeetsMinimum((int)4, (int)1, (int)0)) {
            return 2;
        }
        if (!this.conn.getUseSqlStateCodes()) return 1;
        return 2;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
    }

    @Override
    public ResultSet getSuperTables(String arg0, String arg1, String arg2) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)32), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)32), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)32), new Field((String)"", (String)"SUPERTABLE_NAME", (int)1, (int)32)};
        return this.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>());
    }

    @Override
    public ResultSet getSuperTypes(String arg0, String arg1, String arg2) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TYPE_CAT", (int)1, (int)32), new Field((String)"", (String)"TYPE_SCHEM", (int)1, (int)32), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"SUPERTYPE_CAT", (int)1, (int)32), new Field((String)"", (String)"SUPERTYPE_SCHEM", (int)1, (int)32), new Field((String)"", (String)"SUPERTYPE_NAME", (int)1, (int)32)};
        return this.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>());
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
    }

    protected String getTableNameWithCase(String table) {
        return this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        if (tableNamePattern == null) {
            if (this.conn.getNullNamePatternMatchesAll() == false) throw SQLError.createSQLException((String)"Table name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            tableNamePattern = "%";
        }
        fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)64), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)1), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)64), new Field((String)"", (String)"GRANTOR", (int)1, (int)77), new Field((String)"", (String)"GRANTEE", (int)1, (int)77), new Field((String)"", (String)"PRIVILEGE", (int)1, (int)64), new Field((String)"", (String)"IS_GRANTABLE", (int)1, (int)3)};
        grantQuery = "SELECT host,db,table_name,grantor,user,table_priv FROM mysql.tables_priv WHERE db LIKE ? AND table_name LIKE ?";
        results = null;
        grantRows = new ArrayList<ResultSetRow>();
        pStmt = null;
        try {
            block19 : {
                block20 : {
                    pStmt = this.prepareMetaDataSafeStatement((String)grantQuery);
                    pStmt.setString((int)1, (String)(catalog != null && catalog.length() != 0 ? catalog : "%"));
                    pStmt.setString((int)2, (String)tableNamePattern);
                    results = pStmt.executeQuery();
                    block13 : do lbl-1000: // 3 sources:
                    {
                        block23 : {
                            block22 : {
                                if (!results.next()) break block22;
                                host = results.getString((int)1);
                                db = results.getString((int)2);
                                table = results.getString((int)3);
                                grantor = results.getString((int)4);
                                user = results.getString((int)5);
                                if (user == null || user.length() == 0) {
                                    user = "%";
                                }
                                fullUser = new StringBuilder((String)user);
                                if (host != null && this.conn.getUseHostsInPrivileges()) {
                                    fullUser.append((String)"@");
                                    fullUser.append((String)host);
                                }
                                if ((allPrivileges = results.getString((int)6)) == null) ** GOTO lbl-1000
                                break block23;
                            }
                            var24_23 = null;
                            if (results == null) break block19;
                            results.close();
                            break block20;
                        }
                        allPrivileges = allPrivileges.toUpperCase((Locale)Locale.ENGLISH);
                        st = new StringTokenizer((String)allPrivileges, (String)",");
                        do {
                            if (!st.hasMoreTokens()) continue block13;
                            privilege = st.nextToken().trim();
                            columnResults = null;
                            try {
                                columnResults = this.getColumns((String)catalog, (String)schemaPattern, (String)table, (String)"%");
                                while (columnResults.next()) {
                                    tuple = new byte[8][];
                                    tuple[0] = this.s2b((String)db);
                                    tuple[1] = null;
                                    tuple[2] = this.s2b((String)table);
                                    tuple[3] = grantor != null ? this.s2b((String)grantor) : null;
                                    tuple[4] = this.s2b((String)fullUser.toString());
                                    tuple[5] = this.s2b((String)privilege);
                                    tuple[6] = null;
                                    grantRows.add((ResultSetRow)new ByteArrayRow((byte[][])tuple, (ExceptionInterceptor)this.getExceptionInterceptor()));
                                }
                                var21_20 = null;
                                if (columnResults == null) continue;
                                try {
                                    columnResults.close();
                                }
                                catch (Exception ex) {}
                            }
                            catch (Throwable var20_22) {
                                var21_20 = null;
                                if (columnResults == null) throw var20_22;
                                ** try [egrp 2[TRYBLOCK] [2 : 589->599)] { 
lbl70: // 1 sources:
                                columnResults.close();
                                throw var20_22;
lbl72: // 1 sources:
                                catch (Exception ex) {
                                    // empty catch block
                                }
                                throw var20_22;
                            }
                        } while (true);
                        break;
                    } while (true);
                    catch (Exception ex) {
                        // empty catch block
                    }
                }
                results = null;
            }
            if (pStmt == null) return this.buildResultSet((Field[])fields, grantRows);
            try {
                pStmt.close();
            }
            catch (Exception ex) {
                // empty catch block
            }
            pStmt = null;
            return this.buildResultSet((Field[])fields, grantRows);
        }
        catch (Throwable var23_27) {
            block26 : {
                block24 : {
                    block25 : {
                        var24_24 = null;
                        if (results == null) break block24;
                        ** try [egrp 3[TRYBLOCK] [5 : 630->640)] { 
lbl95: // 1 sources:
                        results.close();
                        break block25;
lbl97: // 1 sources:
                        catch (Exception ex) {
                            // empty catch block
                        }
                    }
                    results = null;
                }
                if (pStmt == null) throw var23_27;
                ** try [egrp 4[TRYBLOCK] [6 : 650->660)] { 
lbl104: // 1 sources:
                pStmt.close();
                break block26;
lbl106: // 1 sources:
                catch (Exception ex) {
                    // empty catch block
                }
            }
            pStmt = null;
            throw var23_27;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        List<String> parseList;
        if (tableNamePattern == null) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Table name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            tableNamePattern = "%";
        }
        TreeMap<K, V> sortedRows = new TreeMap<K, V>();
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        String tmpCat = "";
        if (catalog == null || catalog.length() == 0) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                tmpCat = this.database;
            }
        } else {
            tmpCat = catalog;
        }
        String tableNamePat = (parseList = StringUtils.splitDBdotName((String)tableNamePattern, (String)tmpCat, (String)this.quotedId, (boolean)this.conn.isNoBackslashEscapesSet())).size() == 2 ? parseList.get((int)1) : tableNamePattern;
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (java.sql.Statement)stmt, (String)tableNamePat, (String[])types, sortedRows){
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ String val$tableNamePat;
                final /* synthetic */ String[] val$types;
                final /* synthetic */ SortedMap val$sortedRows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$stmt = statement;
                    this.val$tableNamePat = string;
                    this.val$types = arrstring;
                    this.val$sortedRows = sortedMap;
                    super(x0);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 * Enabled unnecessary exception pruning
                 */
                void forEach(String catalogStr) throws SQLException {
                    operatingOnSystemDB = "information_schema".equalsIgnoreCase((String)catalogStr) != false || "mysql".equalsIgnoreCase((String)catalogStr) != false || "performance_schema".equalsIgnoreCase((String)catalogStr) != false;
                    results = null;
                    try {
                        block32 : {
                            block31 : {
                                try {
                                    results = this.val$stmt.executeQuery((String)((this.this$0.conn.versionMeetsMinimum((int)5, (int)0, (int)2) == false ? "SHOW TABLES FROM " : "SHOW FULL TABLES FROM ") + StringUtils.quoteIdentifier((String)catalogStr, (String)this.this$0.quotedId, (boolean)this.this$0.conn.getPedantic()) + " LIKE " + StringUtils.quoteIdentifier((String)this.val$tableNamePat, (String)"'", (boolean)true)));
                                    ** GOTO lbl15
                                }
                                catch (SQLException sqlEx) {
                                    if ("08S01".equals((Object)sqlEx.getSQLState())) {
                                        throw sqlEx;
                                    }
                                    var16_6 = null;
                                    if (results == null) return;
                                    try {
                                        results.close();
                                        return;
                                    }
                                    catch (Exception ex) {
                                        // empty catch block
                                    }
lbl15: // 1 sources:
                                    shouldReportTables = false;
                                    shouldReportViews = false;
                                    shouldReportSystemTables = false;
                                    shouldReportSystemViews = false;
                                    shouldReportLocalTemporaries = false;
                                    if (this.val$types != null && this.val$types.length != 0) break block31;
                                    shouldReportTables = true;
                                    shouldReportViews = true;
                                    shouldReportSystemTables = true;
                                    shouldReportSystemViews = true;
                                    shouldReportLocalTemporaries = true;
                                    break block32;
                                    return;
                                }
                            }
                            for (i = 0; i < this.val$types.length; ++i) {
                                if (TableType.TABLE.equalsTo((String)this.val$types[i])) {
                                    shouldReportTables = true;
                                    continue;
                                }
                                if (TableType.VIEW.equalsTo((String)this.val$types[i])) {
                                    shouldReportViews = true;
                                    continue;
                                }
                                if (TableType.SYSTEM_TABLE.equalsTo((String)this.val$types[i])) {
                                    shouldReportSystemTables = true;
                                    continue;
                                }
                                if (TableType.SYSTEM_VIEW.equalsTo((String)this.val$types[i])) {
                                    shouldReportSystemViews = true;
                                    continue;
                                }
                                if (!TableType.LOCAL_TEMPORARY.equalsTo((String)this.val$types[i])) continue;
                                shouldReportLocalTemporaries = true;
                            }
                        }
                        typeColumnIndex = 1;
                        hasTableTypes = false;
                        if (this.this$0.conn.versionMeetsMinimum((int)5, (int)0, (int)2)) {
                            try {
                                typeColumnIndex = results.findColumn((String)"table_type");
                                hasTableTypes = true;
                            }
                            catch (SQLException sqlEx) {
                                try {
                                    typeColumnIndex = results.findColumn((String)"Type");
                                    hasTableTypes = true;
                                }
                                catch (SQLException sqlEx2) {
                                    hasTableTypes = false;
                                }
                            }
                        }
                        block20 : while (results.next()) {
                            row = new byte[10][];
                            row[0] = catalogStr == null ? null : this.this$0.s2b((String)catalogStr);
                            row[1] = null;
                            row[2] = results.getBytes((int)1);
                            row[4] = new byte[0];
                            row[5] = null;
                            row[6] = null;
                            row[7] = null;
                            row[8] = null;
                            row[9] = null;
                            if (hasTableTypes) {
                                tableType = results.getString((int)typeColumnIndex);
                                switch (com.mysql.jdbc.DatabaseMetaData$11.$SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[TableType.getTableTypeCompliantWith((String)tableType).ordinal()]) {
                                    case 1: {
                                        reportTable = false;
                                        tablesKey = null;
                                        if (operatingOnSystemDB && shouldReportSystemTables) {
                                            row[3] = TableType.SYSTEM_TABLE.asBytes();
                                            tablesKey = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.SYSTEM_TABLE.getName(), (String)catalogStr, null, (String)results.getString((int)1));
                                            reportTable = true;
                                        } else if (!operatingOnSystemDB && shouldReportTables) {
                                            row[3] = TableType.TABLE.asBytes();
                                            tablesKey = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.TABLE.getName(), (String)catalogStr, null, (String)results.getString((int)1));
                                            reportTable = true;
                                        }
                                        if (!reportTable) continue block20;
                                        this.val$sortedRows.put(tablesKey, new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                        ** break;
                                    }
                                    case 2: {
                                        if (!shouldReportViews) continue block20;
                                        row[3] = TableType.VIEW.asBytes();
                                        this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.VIEW.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                        ** break;
                                    }
                                    case 3: {
                                        if (!shouldReportSystemTables) continue block20;
                                        row[3] = TableType.SYSTEM_TABLE.asBytes();
                                        this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.SYSTEM_TABLE.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                        ** break;
                                    }
                                    case 4: {
                                        if (!shouldReportSystemViews) continue block20;
                                        row[3] = TableType.SYSTEM_VIEW.asBytes();
                                        this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.SYSTEM_VIEW.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                        ** break;
                                    }
                                    case 5: {
                                        if (!shouldReportLocalTemporaries) continue block20;
                                        row[3] = TableType.LOCAL_TEMPORARY.asBytes();
                                        this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.LOCAL_TEMPORARY.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                        ** break;
                                    }
                                }
                                row[3] = TableType.TABLE.asBytes();
                                this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.TABLE.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                                ** break;
lbl120: // 6 sources:
                                continue;
                            }
                            if (!shouldReportTables) continue;
                            row[3] = TableType.TABLE.asBytes();
                            this.val$sortedRows.put(new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey((DatabaseMetaData)this.this$0, (String)TableType.TABLE.getName(), (String)catalogStr, null, (String)results.getString((int)1)), new ByteArrayRow((byte[][])row, (ExceptionInterceptor)this.this$0.getExceptionInterceptor()));
                        }
                        var16_7 = null;
                        if (results == null) return;
                        ** try [egrp 4[TRYBLOCK] [6 : 1120->1129)] { 
lbl129: // 1 sources:
                        results.close();
                        return;
lbl131: // 1 sources:
                        catch (Exception ex) {
                            // empty catch block
                        }
                        return;
                    }
                    catch (Throwable var15_24) {
                        block33 : {
                            var16_8 = null;
                            if (results == null) throw var15_24;
                            ** try [egrp 4[TRYBLOCK] [6 : 1120->1129)] { 
lbl139: // 1 sources:
                            results.close();
                            break block33;
lbl141: // 1 sources:
                            catch (Exception ex) {
                                // empty catch block
                            }
                        }
                        results = null;
                        throw var15_24;
                    }
                }
            }.doForAll();
            Object var12_11 = null;
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (Throwable throwable) {
            Object var12_12 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
        tuples.addAll(sortedRows.values());
        return this.buildResultSet((Field[])this.createTablesFields(), tuples);
    }

    protected Field[] createTablesFields() {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)12, (int)255), new Field((String)"", (String)"TABLE_SCHEM", (int)12, (int)0), new Field((String)"", (String)"TABLE_NAME", (int)12, (int)255), new Field((String)"", (String)"TABLE_TYPE", (int)12, (int)5), new Field((String)"", (String)"REMARKS", (int)12, (int)0), new Field((String)"", (String)"TYPE_CAT", (int)12, (int)0), new Field((String)"", (String)"TYPE_SCHEM", (int)12, (int)0), new Field((String)"", (String)"TYPE_NAME", (int)12, (int)0), new Field((String)"", (String)"SELF_REFERENCING_COL_NAME", (int)12, (int)0), new Field((String)"", (String)"REF_GENERATION", (int)12, (int)0)};
        return fields;
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_TYPE", (int)12, (int)256)};
        boolean minVersion5_0_1 = this.conn.versionMeetsMinimum((int)5, (int)0, (int)1);
        tuples.add(new ByteArrayRow((byte[][])new byte[][]{TableType.LOCAL_TEMPORARY.asBytes()}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        tuples.add(new ByteArrayRow((byte[][])new byte[][]{TableType.SYSTEM_TABLE.asBytes()}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        if (minVersion5_0_1) {
            tuples.add(new ByteArrayRow((byte[][])new byte[][]{TableType.SYSTEM_VIEW.asBytes()}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        }
        tuples.add(new ByteArrayRow((byte[][])new byte[][]{TableType.TABLE.asBytes()}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        if (!minVersion5_0_1) return this.buildResultSet((Field[])fields, tuples);
        tuples.add((ResultSetRow)new ByteArrayRow((byte[][])new byte[][]{TableType.VIEW.asBytes()}, (ExceptionInterceptor)this.getExceptionInterceptor()));
        return this.buildResultSet((Field[])fields, tuples);
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)5), new Field((String)"", (String)"PRECISION", (int)4, (int)10), new Field((String)"", (String)"LITERAL_PREFIX", (int)1, (int)4), new Field((String)"", (String)"LITERAL_SUFFIX", (int)1, (int)4), new Field((String)"", (String)"CREATE_PARAMS", (int)1, (int)32), new Field((String)"", (String)"NULLABLE", (int)5, (int)5), new Field((String)"", (String)"CASE_SENSITIVE", (int)16, (int)3), new Field((String)"", (String)"SEARCHABLE", (int)5, (int)3), new Field((String)"", (String)"UNSIGNED_ATTRIBUTE", (int)16, (int)3), new Field((String)"", (String)"FIXED_PREC_SCALE", (int)16, (int)3), new Field((String)"", (String)"AUTO_INCREMENT", (int)16, (int)3), new Field((String)"", (String)"LOCAL_TYPE_NAME", (int)1, (int)32), new Field((String)"", (String)"MINIMUM_SCALE", (int)5, (int)5), new Field((String)"", (String)"MAXIMUM_SCALE", (int)5, (int)5), new Field((String)"", (String)"SQL_DATA_TYPE", (int)4, (int)10), new Field((String)"", (String)"SQL_DATETIME_SUB", (int)4, (int)10), new Field((String)"", (String)"NUM_PREC_RADIX", (int)4, (int)10)};
        byte[][] rowVal = (byte[][])null;
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        rowVal = new byte[][]{this.s2b((String)"BIT"), Integer.toString((int)-7).getBytes(), this.s2b((String)"1"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"BIT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"BOOL"), Integer.toString((int)-7).getBytes(), this.s2b((String)"1"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"BOOL"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TINYINT"), Integer.toString((int)-6).getBytes(), this.s2b((String)"3"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"TINYINT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TINYINT UNSIGNED"), Integer.toString((int)-6).getBytes(), this.s2b((String)"3"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"TINYINT UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"BIGINT"), Integer.toString((int)-5).getBytes(), this.s2b((String)"19"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"BIGINT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"BIGINT UNSIGNED"), Integer.toString((int)-5).getBytes(), this.s2b((String)"20"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"BIGINT UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"LONG VARBINARY"), Integer.toString((int)-4).getBytes(), this.s2b((String)"16777215"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"LONG VARBINARY"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"MEDIUMBLOB"), Integer.toString((int)-4).getBytes(), this.s2b((String)"16777215"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"MEDIUMBLOB"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"LONGBLOB"), Integer.toString((int)-4).getBytes(), Integer.toString((int)Integer.MAX_VALUE).getBytes(), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"LONGBLOB"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"BLOB"), Integer.toString((int)-4).getBytes(), this.s2b((String)"65535"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"BLOB"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TINYBLOB"), Integer.toString((int)-4).getBytes(), this.s2b((String)"255"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"TINYBLOB"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"VARBINARY"), Integer.toString((int)-3).getBytes(), this.s2b((String)(this.conn.versionMeetsMinimum((int)5, (int)0, (int)3) ? "65535" : "255")), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)"(M)"), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"VARBINARY"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"BINARY"), Integer.toString((int)-2).getBytes(), this.s2b((String)"255"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)"(M)"), Integer.toString((int)1).getBytes(), this.s2b((String)"true"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"BINARY"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"LONG VARCHAR"), Integer.toString((int)-1).getBytes(), this.s2b((String)"16777215"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"LONG VARCHAR"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"MEDIUMTEXT"), Integer.toString((int)-1).getBytes(), this.s2b((String)"16777215"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"MEDIUMTEXT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"LONGTEXT"), Integer.toString((int)-1).getBytes(), Integer.toString((int)Integer.MAX_VALUE).getBytes(), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"LONGTEXT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TEXT"), Integer.toString((int)-1).getBytes(), this.s2b((String)"65535"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"TEXT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TINYTEXT"), Integer.toString((int)-1).getBytes(), this.s2b((String)"255"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"TINYTEXT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"CHAR"), Integer.toString((int)1).getBytes(), this.s2b((String)"255"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)"(M)"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"CHAR"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        int decimalPrecision = 254;
        if (this.conn.versionMeetsMinimum((int)5, (int)0, (int)3)) {
            decimalPrecision = this.conn.versionMeetsMinimum((int)5, (int)0, (int)6) ? 65 : 64;
        }
        rowVal = new byte[][]{this.s2b((String)"NUMERIC"), Integer.toString((int)2).getBytes(), this.s2b((String)String.valueOf((int)decimalPrecision)), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M[,D])] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"NUMERIC"), this.s2b((String)"-308"), this.s2b((String)"308"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"DECIMAL"), Integer.toString((int)3).getBytes(), this.s2b((String)String.valueOf((int)decimalPrecision)), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M[,D])] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"DECIMAL"), this.s2b((String)"-308"), this.s2b((String)"308"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"INTEGER"), Integer.toString((int)4).getBytes(), this.s2b((String)"10"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"INTEGER"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"INTEGER UNSIGNED"), Integer.toString((int)4).getBytes(), this.s2b((String)"10"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"INTEGER UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"INT"), Integer.toString((int)4).getBytes(), this.s2b((String)"10"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"INT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"INT UNSIGNED"), Integer.toString((int)4).getBytes(), this.s2b((String)"10"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"INT UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"MEDIUMINT"), Integer.toString((int)4).getBytes(), this.s2b((String)"7"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"MEDIUMINT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"MEDIUMINT UNSIGNED"), Integer.toString((int)4).getBytes(), this.s2b((String)"8"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"MEDIUMINT UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"SMALLINT"), Integer.toString((int)5).getBytes(), this.s2b((String)"5"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"SMALLINT"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"SMALLINT UNSIGNED"), Integer.toString((int)5).getBytes(), this.s2b((String)"5"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"true"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"SMALLINT UNSIGNED"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"FLOAT"), Integer.toString((int)7).getBytes(), this.s2b((String)"10"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M,D)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"FLOAT"), this.s2b((String)"-38"), this.s2b((String)"38"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"DOUBLE"), Integer.toString((int)8).getBytes(), this.s2b((String)"17"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M,D)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"DOUBLE"), this.s2b((String)"-308"), this.s2b((String)"308"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"DOUBLE PRECISION"), Integer.toString((int)8).getBytes(), this.s2b((String)"17"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M,D)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"DOUBLE PRECISION"), this.s2b((String)"-308"), this.s2b((String)"308"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"REAL"), Integer.toString((int)8).getBytes(), this.s2b((String)"17"), this.s2b((String)""), this.s2b((String)""), this.s2b((String)"[(M,D)] [ZEROFILL]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"true"), this.s2b((String)"REAL"), this.s2b((String)"-308"), this.s2b((String)"308"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"VARCHAR"), Integer.toString((int)12).getBytes(), this.s2b((String)(this.conn.versionMeetsMinimum((int)5, (int)0, (int)3) ? "65535" : "255")), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)"(M)"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"VARCHAR"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"ENUM"), Integer.toString((int)12).getBytes(), this.s2b((String)"65535"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"ENUM"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"SET"), Integer.toString((int)12).getBytes(), this.s2b((String)"64"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"SET"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"DATE"), Integer.toString((int)91).getBytes(), this.s2b((String)"0"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"DATE"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TIME"), Integer.toString((int)92).getBytes(), this.s2b((String)"0"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"TIME"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"DATETIME"), Integer.toString((int)93).getBytes(), this.s2b((String)"0"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)""), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"DATETIME"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        rowVal = new byte[][]{this.s2b((String)"TIMESTAMP"), Integer.toString((int)93).getBytes(), this.s2b((String)"0"), this.s2b((String)"'"), this.s2b((String)"'"), this.s2b((String)"[(M)]"), Integer.toString((int)1).getBytes(), this.s2b((String)"false"), Integer.toString((int)3).getBytes(), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"false"), this.s2b((String)"TIMESTAMP"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"0"), this.s2b((String)"10")};
        tuples.add(new ByteArrayRow((byte[][])rowVal, (ExceptionInterceptor)this.getExceptionInterceptor()));
        return this.buildResultSet((Field[])fields, tuples);
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TYPE_CAT", (int)12, (int)32), new Field((String)"", (String)"TYPE_SCHEM", (int)12, (int)32), new Field((String)"", (String)"TYPE_NAME", (int)12, (int)32), new Field((String)"", (String)"CLASS_NAME", (int)12, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)10), new Field((String)"", (String)"REMARKS", (int)12, (int)32), new Field((String)"", (String)"BASE_TYPE", (int)5, (int)10)};
        ArrayList<ResultSetRow> tuples = new ArrayList<ResultSetRow>();
        return this.buildResultSet((Field[])fields, tuples);
    }

    @Override
    public String getURL() throws SQLException {
        return this.conn.getURL();
    }

    /*
     * Exception decompiling
     */
    @Override
    public String getUserName() throws SQLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
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
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        Field[] fields = new Field[]{new Field((String)"", (String)"SCOPE", (int)5, (int)5), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)5), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)16), new Field((String)"", (String)"COLUMN_SIZE", (int)4, (int)16), new Field((String)"", (String)"BUFFER_LENGTH", (int)4, (int)16), new Field((String)"", (String)"DECIMAL_DIGITS", (int)5, (int)16), new Field((String)"", (String)"PSEUDO_COLUMN", (int)5, (int)5)};
        ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
        java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            new IterateBlock<String>((DatabaseMetaData)this, this.getCatalogIterator((String)catalog), (String)table, (java.sql.Statement)stmt, rows){
                final /* synthetic */ String val$table;
                final /* synthetic */ java.sql.Statement val$stmt;
                final /* synthetic */ ArrayList val$rows;
                final /* synthetic */ DatabaseMetaData this$0;
                {
                    this.this$0 = databaseMetaData;
                    this.val$table = string;
                    this.val$stmt = statement;
                    this.val$rows = arrayList;
                    super(x0);
                }

                /*
                 * Exception decompiling
                 */
                void forEach(String catalogStr) throws SQLException {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 4[CATCHBLOCK]
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
                    // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:475)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:73)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.MemberFunctionInvokation.dumpInner(MemberFunctionInvokation.java:51)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:113)
                    // org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:74)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.dump(StructuredExpressionStatement.java:27)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredTry.dump(StructuredTry.java:72)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:559)
                    // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:204)
                    // org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:141)
                    // org.benf.cfr.reader.util.output.StreamDumper.dump(StreamDumper.java:146)
                    // org.benf.cfr.reader.entities.Method.dump(Method.java:494)
                    // org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:87)
                    // org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1016)
                    // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:231)
                    // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
                    // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
                    // org.benf.cfr.reader.Main.main(Main.java:48)
                    // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
                    // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
                    throw new IllegalStateException("Decompilation failed");
                }
            }.doForAll();
            Object var8_7 = null;
            if (stmt == null) return this.buildResultSet((Field[])fields, rows);
            stmt.close();
            return this.buildResultSet((Field[])fields, rows);
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (stmt == null) throw throwable;
            stmt.close();
            throw throwable;
        }
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        if (this.conn.getEmulateLocators()) return false;
        return true;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        if (!this.conn.versionMeetsMinimum((int)4, (int)0, (int)2)) return false;
        if (this.conn.versionMeetsMinimum((int)4, (int)0, (int)11)) return false;
        return true;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        if (this.nullsAreSortedHigh()) return false;
        return true;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    protected LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment) throws SQLException {
        String columnsDelimitter = ",";
        int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCase((int)0, (String)keysComment, (String)"(", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
        if (indexOfOpenParenLocalColumns == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find start of local columns list.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String constraintName = StringUtils.unQuoteIdentifier((String)keysComment.substring((int)0, (int)indexOfOpenParenLocalColumns).trim(), (String)this.quotedId);
        String keysCommentTrimmed = (keysComment = keysComment.substring((int)indexOfOpenParenLocalColumns, (int)keysComment.length())).trim();
        int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCase((int)0, (String)keysCommentTrimmed, (String)")", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
        if (indexOfCloseParenLocalColumns == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find end of local columns list.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String localColumnNamesString = keysCommentTrimmed.substring((int)1, (int)indexOfCloseParenLocalColumns);
        int indexOfRefer = StringUtils.indexOfIgnoreCase((int)0, (String)keysCommentTrimmed, (String)"REFER ", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
        if (indexOfRefer == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find start of referenced tables list.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCase((int)indexOfRefer, (String)keysCommentTrimmed, (String)"(", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__MRK_COM_WS);
        if (indexOfOpenParenReferCol == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find start of referenced columns list.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String referCatalogTableString = keysCommentTrimmed.substring((int)(indexOfRefer + "REFER ".length()), (int)indexOfOpenParenReferCol);
        int indexOfSlash = StringUtils.indexOfIgnoreCase((int)0, (String)referCatalogTableString, (String)"/", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__MRK_COM_WS);
        if (indexOfSlash == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find name of referenced catalog.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String referCatalog = StringUtils.unQuoteIdentifier((String)referCatalogTableString.substring((int)0, (int)indexOfSlash), (String)this.quotedId);
        String referTable = StringUtils.unQuoteIdentifier((String)referCatalogTableString.substring((int)(indexOfSlash + 1)).trim(), (String)this.quotedId);
        int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCase((int)indexOfOpenParenReferCol, (String)keysCommentTrimmed, (String)")", (String)this.quotedId, (String)this.quotedId, StringUtils.SEARCH_MODE__ALL);
        if (indexOfCloseParenRefer == -1) {
            throw SQLError.createSQLException((String)"Error parsing foreign keys definition, couldn't find end of referenced columns list.", (String)"S1000", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String referColumnNamesString = keysCommentTrimmed.substring((int)(indexOfOpenParenReferCol + 1), (int)indexOfCloseParenRefer);
        List<String> referColumnsList = StringUtils.split((String)referColumnNamesString, (String)columnsDelimitter, (String)this.quotedId, (String)this.quotedId, (boolean)false);
        List<String> localColumnsList = StringUtils.split((String)localColumnNamesString, (String)columnsDelimitter, (String)this.quotedId, (String)this.quotedId, (boolean)false);
        return new LocalAndReferencedColumns((DatabaseMetaData)this, localColumnsList, referColumnsList, (String)constraintName, (String)referCatalog, (String)referTable);
    }

    protected byte[] s2b(String s) throws SQLException {
        if (s != null) return StringUtils.getBytes((String)s, (String)this.conn.getCharacterSetMetadata(), (String)this.conn.getServerCharset(), (boolean)this.conn.parserKnowsUnicode(), (MySQLConnection)this.conn, (ExceptionInterceptor)this.getExceptionInterceptor());
        return null;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return this.conn.storesLowerCaseTableName();
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return this.conn.storesLowerCaseTableName();
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        if (this.conn.storesLowerCaseTableName()) return false;
        return true;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        if (this.conn.storesLowerCaseTableName()) return false;
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return this.conn.versionMeetsMinimum((int)3, (int)22, (int)0);
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum((int)3, (int)22, (int)0);
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum((int)3, (int)22, (int)0);
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return this.conn.versionMeetsMinimum((int)3, (int)22, (int)0);
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum((int)3, (int)22, (int)0);
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        switch (fromType) {
            case -4: 
            case -3: 
            case -2: 
            case -1: 
            case 1: 
            case 12: {
                switch (toType) {
                    case -6: 
                    case -5: 
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 12: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 1111: {
                        return true;
                    }
                }
                return false;
            }
            case -7: {
                return false;
            }
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                switch (toType) {
                    case -6: 
                    case -5: 
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 12: {
                        return true;
                    }
                }
                return false;
            }
            case 0: {
                return false;
            }
            case 1111: {
                switch (toType) {
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 12: {
                        return true;
                    }
                }
                return false;
            }
            case 91: {
                switch (toType) {
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 12: {
                        return true;
                    }
                }
                return false;
            }
            case 92: {
                switch (toType) {
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 12: {
                        return true;
                    }
                }
                return false;
            }
            case 93: {
                switch (toType) {
                    case -4: 
                    case -3: 
                    case -2: 
                    case -1: 
                    case 1: 
                    case 12: 
                    case 91: 
                    case 92: {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() {
        return true;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        if (this.conn.getOverrideSupportsIntegrityEnhancementFacility()) return true;
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        if (this.conn.lowerCaseTableNames()) return false;
        return true;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        if (this.conn.lowerCaseTableNames()) return false;
        return true;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        switch (type) {
            case 1004: {
                if (concurrency == 1007) return true;
                if (concurrency != 1008) throw SQLError.createSQLException((String)"Illegal arguments to supportsResultSetConcurrency()", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                return true;
            }
            case 1003: {
                if (concurrency == 1007) return true;
                if (concurrency != 1008) throw SQLError.createSQLException((String)"Illegal arguments to supportsResultSetConcurrency()", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
                return true;
            }
            case 1005: {
                return false;
            }
        }
        throw SQLError.createSQLException((String)"Illegal arguments to supportsResultSetConcurrency()", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        if (holdability != 1) return false;
        return true;
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        if (type != 1004) return false;
        return true;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        if (this.conn.versionMeetsMinimum((int)4, (int)0, (int)14)) return true;
        if (this.conn.versionMeetsMinimum((int)4, (int)1, (int)1)) return true;
        return false;
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)0, (int)0);
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return this.conn.versionMeetsMinimum((int)5, (int)0, (int)0);
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)1, (int)0);
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        if (!this.conn.supportsIsolationLevel()) return false;
        switch (level) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return this.conn.supportsTransactions();
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)0, (int)0);
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return this.conn.versionMeetsMinimum((int)4, (int)0, (int)0);
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"NAME", (int)12, (int)255), new Field((String)"", (String)"MAX_LEN", (int)4, (int)10), new Field((String)"", (String)"DEFAULT_VALUE", (int)12, (int)255), new Field((String)"", (String)"DESCRIPTION", (int)12, (int)255)};
        return DatabaseMetaData.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>(), (MySQLConnection)this.conn);
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        Field[] fields = this.createFunctionColumnsFields();
        return this.getProcedureOrFunctionColumns((Field[])fields, (String)catalog, (String)schemaPattern, (String)functionNamePattern, (String)columnNamePattern, (boolean)false, (boolean)true);
    }

    protected Field[] createFunctionColumnsFields() {
        return new Field[]{new Field((String)"", (String)"FUNCTION_CAT", (int)12, (int)512), new Field((String)"", (String)"FUNCTION_SCHEM", (int)12, (int)512), new Field((String)"", (String)"FUNCTION_NAME", (int)12, (int)512), new Field((String)"", (String)"COLUMN_NAME", (int)12, (int)512), new Field((String)"", (String)"COLUMN_TYPE", (int)12, (int)64), new Field((String)"", (String)"DATA_TYPE", (int)5, (int)6), new Field((String)"", (String)"TYPE_NAME", (int)12, (int)64), new Field((String)"", (String)"PRECISION", (int)4, (int)12), new Field((String)"", (String)"LENGTH", (int)4, (int)12), new Field((String)"", (String)"SCALE", (int)5, (int)12), new Field((String)"", (String)"RADIX", (int)5, (int)6), new Field((String)"", (String)"NULLABLE", (int)5, (int)6), new Field((String)"", (String)"REMARKS", (int)12, (int)512), new Field((String)"", (String)"CHAR_OCTET_LENGTH", (int)4, (int)32), new Field((String)"", (String)"ORDINAL_POSITION", (int)4, (int)32), new Field((String)"", (String)"IS_NULLABLE", (int)12, (int)12), new Field((String)"", (String)"SPECIFIC_NAME", (int)12, (int)64)};
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"FUNCTION_CAT", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_SCHEM", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_NAME", (int)1, (int)255), new Field((String)"", (String)"REMARKS", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_TYPE", (int)5, (int)6), new Field((String)"", (String)"SPECIFIC_NAME", (int)1, (int)255)};
        return this.getProceduresAndOrFunctions((Field[])fields, (String)catalog, (String)schemaPattern, (String)functionNamePattern, (boolean)false, (boolean)true);
    }

    public boolean providesQueryObjectGenerator() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_SCHEM", (int)12, (int)255), new Field((String)"", (String)"TABLE_CATALOG", (int)12, (int)255)};
        return this.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>());
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return true;
    }

    protected PreparedStatement prepareMetaDataSafeStatement(String sql) throws SQLException {
        PreparedStatement pStmt = this.conn.clientPrepareStatement((String)sql);
        if (pStmt.getMaxRows() != 0) {
            pStmt.setMaxRows((int)0);
        }
        ((Statement)((Object)pStmt)).setHoldResultsOpenOverClose((boolean)true);
        return pStmt;
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        Field[] fields = new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)12, (int)512), new Field((String)"", (String)"TABLE_SCHEM", (int)12, (int)512), new Field((String)"", (String)"TABLE_NAME", (int)12, (int)512), new Field((String)"", (String)"COLUMN_NAME", (int)12, (int)512), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)12), new Field((String)"", (String)"COLUMN_SIZE", (int)4, (int)12), new Field((String)"", (String)"DECIMAL_DIGITS", (int)4, (int)12), new Field((String)"", (String)"NUM_PREC_RADIX", (int)4, (int)12), new Field((String)"", (String)"COLUMN_USAGE", (int)12, (int)512), new Field((String)"", (String)"REMARKS", (int)12, (int)512), new Field((String)"", (String)"CHAR_OCTET_LENGTH", (int)4, (int)12), new Field((String)"", (String)"IS_NULLABLE", (int)12, (int)512)};
        return this.buildResultSet((Field[])fields, new ArrayList<ResultSetRow>());
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return true;
    }

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_DBMD_SHOW_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(MySQLConnection.class, String.class);
                JDBC_4_DBMD_IS_CTOR = Class.forName((String)"com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(MySQLConnection.class, String.class);
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
            JDBC_4_DBMD_IS_CTOR = null;
            JDBC_4_DBMD_SHOW_CTOR = null;
        }
        MYSQL_KEYWORDS = new String[]{"ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "ARRAY", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DENSE_RANK", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "EMPTY", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FIRST_VALUE", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "FUNCTION", "GENERATED", "GET", "GRANT", "GROUP", "GROUPING", "GROUPS", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "JSON_TABLE", "KEY", "KEYS", "KILL", "LAG", "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MEMBER", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NUMERIC", "OF", "ON", "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "OVER", "PARTITION", "PERCENT_RANK", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "RANK", "READ", "READS", "READ_WRITE", "REAL", "RECURSIVE", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "SYSTEM", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL"};
        SQL92_KEYWORDS = new String[]{"ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BIT_LENGTH", "BOTH", "BY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP", "ELSE", "END", "END-EXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LAST", "LEADING", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOWER", "MATCH", "MAX", "MIN", "MINUTE", "MODULE", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "SCHEMA", "SCROLL", "SECOND", "SECTION", "SELECT", "SESSION", "SESSION_USER", "SET", "SIZE", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM", "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE", "YEAR", "ZONE"};
        SQL2003_KEYWORDS = new String[]{"ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADED", "CASE", "CAST", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEFT", "LIKE", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "ROW_NUMBER", "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VAR_POP", "VAR_SAMP", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR"};
        mysqlKeywords = null;
    }
}

