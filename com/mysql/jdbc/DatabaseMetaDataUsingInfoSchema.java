/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlDefs;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StringUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseMetaDataUsingInfoSchema
extends DatabaseMetaData {
    private boolean hasReferentialConstraintsView;
    private final boolean hasParametersView;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet) throws SQLException {
        super((MySQLConnection)connToSet, (String)databaseToSet);
        this.hasReferentialConstraintsView = this.conn.versionMeetsMinimum((int)5, (int)1, (int)10);
        ResultSet rs = null;
        try {
            rs = super.getTables((String)"INFORMATION_SCHEMA", null, (String)"PARAMETERS", (String[])new String[0]);
            this.hasParametersView = rs.next();
            java.lang.Object var5_4 = null;
            if (rs == null) return;
            rs.close();
            return;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_5 = null;
            if (rs == null) throw throwable;
            rs.close();
            throw throwable;
        }
    }

    protected ResultSet executeMetadataQuery(PreparedStatement pStmt) throws SQLException {
        ResultSet rs = pStmt.executeQuery();
        ((ResultSetInternalMethods)rs).setOwningStatement(null);
        return rs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        ResultSet resultSet;
        if (columnNamePattern == null) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Column name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            columnNamePattern = "%";
        }
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME,COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME =? AND COLUMN_NAME LIKE ? ORDER BY COLUMN_NAME, PRIVILEGE_TYPE";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            pStmt.setString((int)3, (String)columnNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)64), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)1), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)64), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)64), new Field((String)"", (String)"GRANTOR", (int)1, (int)77), new Field((String)"", (String)"GRANTEE", (int)1, (int)77), new Field((String)"", (String)"PRIVILEGE", (int)1, (int)64), new Field((String)"", (String)"IS_GRANTABLE", (int)1, (int)3)});
            resultSet = rs;
            java.lang.Object var10_9 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_10 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern) throws SQLException {
        ResultSet resultSet;
        if (columnNamePattern == null) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Column name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            columnNamePattern = "%";
        }
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        StringBuilder sqlBuf = new StringBuilder((String)"SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME,");
        MysqlDefs.appendJdbcTypeMappingQuery((StringBuilder)sqlBuf, (String)"DATA_TYPE");
        sqlBuf.append((String)" AS DATA_TYPE, ");
        if (this.conn.getCapitalizeTypeNames()) {
            sqlBuf.append((String)"UPPER(CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS TYPE_NAME,");
        } else {
            sqlBuf.append((String)"CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS TYPE_NAME,");
        }
        sqlBuf.append((String)("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, " + MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS," + "10 AS NUM_PREC_RADIX," + "CASE WHEN IS_NULLABLE='NO' THEN " + 0 + " ELSE CASE WHEN IS_NULLABLE='YES' THEN " + 1 + " ELSE " + 2 + " END END AS NULLABLE," + "COLUMN_COMMENT AS REMARKS," + "COLUMN_DEFAULT AS COLUMN_DEF," + "0 AS SQL_DATA_TYPE," + "0 AS SQL_DATETIME_SUB," + "CASE WHEN CHARACTER_OCTET_LENGTH > " + Integer.MAX_VALUE + " THEN " + Integer.MAX_VALUE + " ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH," + "ORDINAL_POSITION," + "IS_NULLABLE," + "NULL AS SCOPE_CATALOG," + "NULL AS SCOPE_SCHEMA," + "NULL AS SCOPE_TABLE," + "NULL AS SOURCE_DATA_TYPE," + "IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT, " + "IF (EXTRA LIKE '%GENERATED%','YES','NO') AS IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE "));
        boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase((String)catalog);
        if (catalog != null) {
            if (operatingOnInformationSchema || StringUtils.indexOfIgnoreCase((int)0, (String)catalog, (String)"%") == -1 && StringUtils.indexOfIgnoreCase((int)0, (String)catalog, (String)"_") == -1) {
                sqlBuf.append((String)"TABLE_SCHEMA = ? AND ");
            } else {
                sqlBuf.append((String)"TABLE_SCHEMA LIKE ? AND ");
            }
        } else {
            sqlBuf.append((String)"TABLE_SCHEMA LIKE ? AND ");
        }
        if (tableName != null) {
            if (StringUtils.indexOfIgnoreCase((int)0, (String)tableName, (String)"%") == -1 && StringUtils.indexOfIgnoreCase((int)0, (String)tableName, (String)"_") == -1) {
                sqlBuf.append((String)"TABLE_NAME = ? AND ");
            } else {
                sqlBuf.append((String)"TABLE_NAME LIKE ? AND ");
            }
        } else {
            sqlBuf.append((String)"TABLE_NAME LIKE ? AND ");
        }
        if (StringUtils.indexOfIgnoreCase((int)0, (String)columnNamePattern, (String)"%") == -1 && StringUtils.indexOfIgnoreCase((int)0, (String)columnNamePattern, (String)"_") == -1) {
            sqlBuf.append((String)"COLUMN_NAME = ? ");
        } else {
            sqlBuf.append((String)"COLUMN_NAME LIKE ? ");
        }
        sqlBuf.append((String)"ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sqlBuf.toString());
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)tableName);
            pStmt.setString((int)3, (String)columnNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createColumnsFields());
            resultSet = rs;
            java.lang.Object var11_10 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_11 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        ResultSet resultSet;
        if (primaryTable == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (primaryCatalog == null && this.conn.getNullCatalogMeansCurrent()) {
            primaryCatalog = this.database;
        }
        if (foreignCatalog == null && this.conn.getNullCatalogMeansCurrent()) {
            foreignCatalog = this.database;
        }
        String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + this.generateUpdateRuleClause() + " AS UPDATE_RULE," + this.generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + this.generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "AND A.TABLE_SCHEMA LIKE ? AND A.TABLE_NAME=? ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (primaryCatalog != null) {
                pStmt.setString((int)1, (String)primaryCatalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)primaryTable);
            if (foreignCatalog != null) {
                pStmt.setString((int)3, (String)foreignCatalog);
            } else {
                pStmt.setString((int)3, (String)"%");
            }
            pStmt.setString((int)4, (String)foreignTable);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createFkMetadataFields());
            resultSet = rs;
            java.lang.Object var12_11 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var12_12 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        ResultSet resultSet;
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT, NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME,A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + this.generateUpdateRuleClause() + " AS UPDATE_RULE," + this.generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + this.generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createFkMetadataFields());
            resultSet = rs;
            java.lang.Object var9_8 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_9 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    private String generateOptionalRefContraintsJoin() {
        if (!this.hasReferentialConstraintsView) return "";
        return "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) ";
    }

    private String generateDeleteRuleClause() {
        String string;
        if (this.hasReferentialConstraintsView) {
            string = "CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf((int)0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + String.valueOf((int)2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf((int)4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + String.valueOf((int)1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + String.valueOf((int)3) + " ELSE " + String.valueOf((int)3) + " END ";
            return string;
        }
        string = String.valueOf((int)1);
        return string;
    }

    private String generateUpdateRuleClause() {
        String string;
        if (this.hasReferentialConstraintsView) {
            string = "CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf((int)0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + String.valueOf((int)2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf((int)4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + String.valueOf((int)1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + String.valueOf((int)3) + " ELSE " + String.valueOf((int)3) + " END ";
            return string;
        }
        string = String.valueOf((int)1);
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        ResultSet resultSet;
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT, NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + this.generateUpdateRuleClause() + " AS UPDATE_RULE," + this.generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A " + "JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING " + "(CONSTRAINT_NAME, TABLE_NAME) " + this.generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.TABLE_SCHEMA LIKE ? " + "AND A.TABLE_NAME=? " + "AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL " + "ORDER BY A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, A.ORDINAL_POSITION";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createFkMetadataFields());
            resultSet = rs;
            java.lang.Object var9_8 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_9 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        ResultSet resultSet;
        StringBuilder sqlBuf = new StringBuilder((String)"SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, NON_UNIQUE,");
        sqlBuf.append((String)"TABLE_SCHEMA AS INDEX_QUALIFIER, INDEX_NAME,3 AS TYPE, SEQ_IN_INDEX AS ORDINAL_POSITION, COLUMN_NAME,");
        sqlBuf.append((String)"COLLATION AS ASC_OR_DESC, CARDINALITY, NULL AS PAGES, NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE ");
        sqlBuf.append((String)"TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?");
        if (unique) {
            sqlBuf.append((String)" AND NON_UNIQUE=0 ");
        }
        sqlBuf.append((String)"ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
        PreparedStatement pStmt = null;
        try {
            if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
                catalog = this.database;
            }
            pStmt = this.prepareMetaDataSafeStatement((String)sqlBuf.toString());
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createIndexInfoFields());
            resultSet = rs;
            java.lang.Object var11_10 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_11 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        ResultSet resultSet;
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])new Field[]{new Field((String)"", (String)"TABLE_CAT", (int)1, (int)255), new Field((String)"", (String)"TABLE_SCHEM", (int)1, (int)0), new Field((String)"", (String)"TABLE_NAME", (int)1, (int)255), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"KEY_SEQ", (int)5, (int)5), new Field((String)"", (String)"PK_NAME", (int)1, (int)32)});
            resultSet = rs;
            java.lang.Object var9_8 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_9 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        ResultSet resultSet;
        if (procedureNamePattern == null || procedureNamePattern.length() == 0) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Procedure name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            procedureNamePattern = "%";
        }
        String db = null;
        if (catalog == null) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                db = this.database;
            }
        } else {
            db = catalog;
        }
        String sql = "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM, ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN 1 WHEN ROUTINE_TYPE='FUNCTION' THEN 2 ELSE 0 END AS PROCEDURE_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE " + this.getRoutineTypeConditionForGetProcedures() + "ROUTINE_SCHEMA LIKE ? AND ROUTINE_NAME LIKE ? ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_TYPE";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (db != null) {
                pStmt.setString((int)1, (String)db);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)procedureNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createFieldMetadataForGetProcedures());
            resultSet = rs;
            java.lang.Object var10_9 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_10 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    protected String getRoutineTypeConditionForGetProcedures() {
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        ResultSet resultSet;
        if (!this.hasParametersView) {
            return this.getProcedureColumnsNoISParametersView((String)catalog, (String)schemaPattern, (String)procedureNamePattern, (String)columnNamePattern);
        }
        if (procedureNamePattern == null || procedureNamePattern.length() == 0) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Procedure name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            procedureNamePattern = "%";
        }
        String db = null;
        if (catalog == null) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                db = this.database;
            }
        } else {
            db = catalog;
        }
        StringBuilder sqlBuf = new StringBuilder((String)"SELECT SPECIFIC_SCHEMA AS PROCEDURE_CAT, NULL AS `PROCEDURE_SCHEM`, SPECIFIC_NAME AS `PROCEDURE_NAME`, IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE = 'OUT' THEN 4 WHEN PARAMETER_MODE = 'INOUT' THEN 2 WHEN ORDINAL_POSITION = 0 THEN 5 ELSE 0 END AS `COLUMN_TYPE`, ");
        MysqlDefs.appendJdbcTypeMappingQuery((StringBuilder)sqlBuf, (String)"DATA_TYPE");
        sqlBuf.append((String)" AS `DATA_TYPE`, ");
        if (this.conn.getCapitalizeTypeNames()) {
            sqlBuf.append((String)"UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
        } else {
            sqlBuf.append((String)"CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
        }
        sqlBuf.append((String)"NUMERIC_PRECISION AS `PRECISION`, ");
        sqlBuf.append((String)"CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
        sqlBuf.append((String)"NUMERIC_SCALE AS `SCALE`, ");
        sqlBuf.append((String)"10 AS RADIX,");
        sqlBuf.append((String)("1 AS `NULLABLE`, NULL AS `REMARKS`, NULL AS `COLUMN_DEF`, NULL AS `SQL_DATA_TYPE`, NULL AS `SQL_DATETIME_SUB`, CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`, ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`, SPECIFIC_NAME FROM INFORMATION_SCHEMA.PARAMETERS WHERE " + this.getRoutineTypeConditionForGetProcedureColumns() + "SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) " + "ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ROUTINE_TYPE, ORDINAL_POSITION"));
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sqlBuf.toString());
            if (db != null) {
                pStmt.setString((int)1, (String)db);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)procedureNamePattern);
            pStmt.setString((int)3, (String)columnNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createProcedureColumnsFields());
            resultSet = rs;
            java.lang.Object var11_10 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_11 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    protected ResultSet getProcedureColumnsNoISParametersView(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        return super.getProcedureColumns((String)catalog, (String)schemaPattern, (String)procedureNamePattern, (String)columnNamePattern);
    }

    protected String getRoutineTypeConditionForGetProcedureColumns() {
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        ResultSet i;
        List<String> parseList;
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        if (tableNamePattern == null) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Table name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            tableNamePattern = "%";
        }
        String tmpCat = "";
        if (catalog == null || catalog.length() == 0) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                tmpCat = this.database;
            }
        } else {
            tmpCat = catalog;
        }
        String tableNamePat = (parseList = StringUtils.splitDBdotName((String)tableNamePattern, (String)tmpCat, (String)this.quotedId, (boolean)this.conn.isNoBackslashEscapesSet())).size() == 2 ? parseList.get((int)1) : tableNamePattern;
        PreparedStatement pStmt = null;
        String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN CASE WHEN TABLE_SCHEMA = 'mysql' OR TABLE_SCHEMA = 'performance_schema' THEN 'SYSTEM TABLE' ELSE 'TABLE' END WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, TABLE_COMMENT AS REMARKS, NULL AS TYPE_CAT, NULL AS TYPE_SCHEM, NULL AS TYPE_NAME, NULL AS SELF_REFERENCING_COL_NAME, NULL AS REF_GENERATION FROM INFORMATION_SCHEMA.TABLES WHERE ";
        boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase((String)catalog);
        sql = catalog != null ? (operatingOnInformationSchema || StringUtils.indexOfIgnoreCase((int)0, (String)catalog, (String)"%") == -1 && StringUtils.indexOfIgnoreCase((int)0, (String)catalog, (String)"_") == -1 ? sql + "TABLE_SCHEMA = ? " : sql + "TABLE_SCHEMA LIKE ? ") : sql + "TABLE_SCHEMA LIKE ? ";
        sql = tableNamePat != null ? (StringUtils.indexOfIgnoreCase((int)0, (String)tableNamePat, (String)"%") == -1 && StringUtils.indexOfIgnoreCase((int)0, (String)tableNamePat, (String)"_") == -1 ? sql + "AND TABLE_NAME = ? " : sql + "AND TABLE_NAME LIKE ? ") : sql + "AND TABLE_NAME LIKE ? ";
        sql = sql + "HAVING TABLE_TYPE IN (?,?,?,?,?) ";
        sql = sql + "ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME";
        try {
            int i2;
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)tableNamePat);
            if (types == null || types.length == 0) {
                DatabaseMetaData.TableType[] tableTypes = DatabaseMetaData.TableType.values();
                for (i2 = 0; i2 < 5; ++i2) {
                    pStmt.setString((int)(3 + i2), (String)tableTypes[i2].getName());
                }
            } else {
                for (int i3 = 0; i3 < 5; ++i3) {
                    pStmt.setNull((int)(3 + i3), (int)12);
                }
                int idx = 3;
                for (i2 = 0; i2 < types.length; ++i2) {
                    DatabaseMetaData.TableType tableType = DatabaseMetaData.TableType.getTableTypeEqualTo((String)types[i2]);
                    if (tableType == DatabaseMetaData.TableType.UNKNOWN) continue;
                    pStmt.setString((int)idx++, (String)tableType.getName());
                }
            }
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createTablesFields());
            i = rs;
            java.lang.Object var15_16 = null;
            if (pStmt == null) return i;
        }
        catch (Throwable throwable) {
            java.lang.Object var15_17 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return i;
    }

    public boolean gethasParametersView() {
        return this.hasParametersView;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        ResultSet resultSet;
        if (catalog == null && this.conn.getNullCatalogMeansCurrent()) {
            catalog = this.database;
        }
        if (table == null) {
            throw SQLError.createSQLException((String)"Table not specified.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        StringBuilder sqlBuf = new StringBuilder((String)"SELECT NULL AS SCOPE, COLUMN_NAME, ");
        MysqlDefs.appendJdbcTypeMappingQuery((StringBuilder)sqlBuf, (String)"DATA_TYPE");
        sqlBuf.append((String)" AS DATA_TYPE, ");
        sqlBuf.append((String)"COLUMN_TYPE AS TYPE_NAME, ");
        sqlBuf.append((String)"CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, ");
        sqlBuf.append((String)(MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH,NUMERIC_SCALE AS DECIMAL_DIGITS, " + Integer.toString((int)1) + " AS PSEUDO_COLUMN FROM INFORMATION_SCHEMA.COLUMNS " + "WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND EXTRA LIKE '%on update CURRENT_TIMESTAMP%'"));
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sqlBuf.toString());
            if (catalog != null) {
                pStmt.setString((int)1, (String)catalog);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)table);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])new Field[]{new Field((String)"", (String)"SCOPE", (int)5, (int)5), new Field((String)"", (String)"COLUMN_NAME", (int)1, (int)32), new Field((String)"", (String)"DATA_TYPE", (int)4, (int)5), new Field((String)"", (String)"TYPE_NAME", (int)1, (int)16), new Field((String)"", (String)"COLUMN_SIZE", (int)4, (int)16), new Field((String)"", (String)"BUFFER_LENGTH", (int)4, (int)16), new Field((String)"", (String)"DECIMAL_DIGITS", (int)5, (int)16), new Field((String)"", (String)"PSEUDO_COLUMN", (int)5, (int)5)});
            resultSet = rs;
            java.lang.Object var9_8 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_9 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        ResultSet resultSet;
        if (!this.hasParametersView) {
            return super.getFunctionColumns((String)catalog, (String)schemaPattern, (String)functionNamePattern, (String)columnNamePattern);
        }
        if (functionNamePattern == null || functionNamePattern.length() == 0) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Procedure name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            functionNamePattern = "%";
        }
        String db = null;
        if (catalog == null) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                db = this.database;
            }
        } else {
            db = catalog;
        }
        StringBuilder sqlBuf = new StringBuilder((String)"SELECT SPECIFIC_SCHEMA AS FUNCTION_CAT, NULL AS `FUNCTION_SCHEM`, SPECIFIC_NAME AS `FUNCTION_NAME`, ");
        sqlBuf.append((String)"IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN ");
        sqlBuf.append((int)this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_COLUMN_IN));
        sqlBuf.append((String)" WHEN PARAMETER_MODE = 'OUT' THEN ");
        sqlBuf.append((int)this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_COLUMN_OUT));
        sqlBuf.append((String)" WHEN PARAMETER_MODE = 'INOUT' THEN ");
        sqlBuf.append((int)this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_COLUMN_INOUT));
        sqlBuf.append((String)" WHEN ORDINAL_POSITION = 0 THEN ");
        sqlBuf.append((int)this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_COLUMN_RETURN));
        sqlBuf.append((String)" ELSE ");
        sqlBuf.append((int)this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_COLUMN_UNKNOWN));
        sqlBuf.append((String)" END AS `COLUMN_TYPE`, ");
        MysqlDefs.appendJdbcTypeMappingQuery((StringBuilder)sqlBuf, (String)"DATA_TYPE");
        sqlBuf.append((String)" AS `DATA_TYPE`, ");
        if (this.conn.getCapitalizeTypeNames()) {
            sqlBuf.append((String)"UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
        } else {
            sqlBuf.append((String)"CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
        }
        sqlBuf.append((String)"NUMERIC_PRECISION AS `PRECISION`, ");
        sqlBuf.append((String)"CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
        sqlBuf.append((String)"NUMERIC_SCALE AS `SCALE`, ");
        sqlBuf.append((String)"10 AS RADIX,");
        sqlBuf.append((String)(this.getJDBC4FunctionConstant((JDBC4FunctionConstant)JDBC4FunctionConstant.FUNCTION_NULLABLE) + " AS `NULLABLE`,  NULL AS `REMARKS`, " + "CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`,  ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`, SPECIFIC_NAME " + "FROM INFORMATION_SCHEMA.PARAMETERS WHERE " + "SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) " + "AND ROUTINE_TYPE='FUNCTION' ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION"));
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sqlBuf.toString());
            if (db != null) {
                pStmt.setString((int)1, (String)db);
            } else {
                pStmt.setString((int)1, (String)"%");
            }
            pStmt.setString((int)2, (String)functionNamePattern);
            pStmt.setString((int)3, (String)columnNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])this.createFunctionColumnsFields());
            resultSet = rs;
            java.lang.Object var11_10 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_11 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    protected int getJDBC4FunctionConstant(JDBC4FunctionConstant constant) {
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        ResultSet resultSet;
        if (functionNamePattern == null || functionNamePattern.length() == 0) {
            if (!this.conn.getNullNamePatternMatchesAll()) throw SQLError.createSQLException((String)"Function name pattern can not be NULL or empty.", (String)"S1009", (ExceptionInterceptor)this.getExceptionInterceptor());
            functionNamePattern = "%";
        }
        String db = null;
        if (catalog == null) {
            if (this.conn.getNullCatalogMeansCurrent()) {
                db = this.database;
            }
        } else {
            db = catalog;
        }
        String sql = "SELECT ROUTINE_SCHEMA AS FUNCTION_CAT, NULL AS FUNCTION_SCHEM, ROUTINE_NAME AS FUNCTION_NAME, ROUTINE_COMMENT AS REMARKS, " + this.getJDBC4FunctionNoTableConstant() + " AS FUNCTION_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES " + "WHERE ROUTINE_TYPE LIKE 'FUNCTION' AND ROUTINE_SCHEMA LIKE ? AND " + "ROUTINE_NAME LIKE ? ORDER BY FUNCTION_CAT, FUNCTION_SCHEM, FUNCTION_NAME, SPECIFIC_NAME";
        PreparedStatement pStmt = null;
        try {
            pStmt = this.prepareMetaDataSafeStatement((String)sql);
            pStmt.setString((int)1, (String)(db != null ? db : "%"));
            pStmt.setString((int)2, (String)functionNamePattern);
            ResultSet rs = this.executeMetadataQuery((PreparedStatement)pStmt);
            ((ResultSetInternalMethods)rs).redefineFieldsForDBMD((Field[])new Field[]{new Field((String)"", (String)"FUNCTION_CAT", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_SCHEM", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_NAME", (int)1, (int)255), new Field((String)"", (String)"REMARKS", (int)1, (int)255), new Field((String)"", (String)"FUNCTION_TYPE", (int)5, (int)6), new Field((String)"", (String)"SPECIFIC_NAME", (int)1, (int)255)});
            resultSet = rs;
            java.lang.Object var10_9 = null;
            if (pStmt == null) return resultSet;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_10 = null;
            if (pStmt == null) throw throwable;
            pStmt.close();
            throw throwable;
        }
        pStmt.close();
        return resultSet;
    }

    @Override
    protected int getJDBC4FunctionNoTableConstant() {
        return 0;
    }
}

