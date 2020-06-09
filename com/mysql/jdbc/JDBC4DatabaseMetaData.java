/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class JDBC4DatabaseMetaData
extends DatabaseMetaData {
    public JDBC4DatabaseMetaData(MySQLConnection connToSet, String databaseToSet) {
        super((MySQLConnection)connToSet, (String)databaseToSet);
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance((Object)this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T)iface.cast((Object)this);
        }
        catch (ClassCastException cce) {
            throw SQLError.createSQLException((String)("Unable to unwrap to " + iface.toString()), (String)"S1009", (ExceptionInterceptor)this.conn.getExceptionInterceptor());
        }
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        Field[] fields = this.createProcedureColumnsFields();
        return this.getProcedureOrFunctionColumns((Field[])fields, (String)catalog, (String)schemaPattern, (String)procedureNamePattern, (String)columnNamePattern, (boolean)true, (boolean)this.conn.getGetProceduresReturnsFunctions());
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        Field[] fields = this.createFieldMetadataForGetProcedures();
        return this.getProceduresAndOrFunctions((Field[])fields, (String)catalog, (String)schemaPattern, (String)procedureNamePattern, (boolean)true, (boolean)this.conn.getGetProceduresReturnsFunctions());
    }

    @Override
    protected int getJDBC4FunctionNoTableConstant() {
        return 1;
    }

    @Override
    protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
        return JDBC4DatabaseMetaData.getProcedureOrFunctionColumnType((boolean)isOutParam, (boolean)isInParam, (boolean)isReturnParam, (boolean)forGetFunctionColumns);
    }

    protected static int getProcedureOrFunctionColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
        if (isInParam && isOutParam) {
            if (!forGetFunctionColumns) return 2;
            return 2;
        }
        if (isInParam) {
            if (!forGetFunctionColumns) return 1;
            return 1;
        }
        if (isOutParam) {
            if (!forGetFunctionColumns) return 4;
            return 3;
        }
        if (isReturnParam) {
            if (!forGetFunctionColumns) return 5;
            return 4;
        }
        if (!forGetFunctionColumns) return 0;
        return 0;
    }
}

