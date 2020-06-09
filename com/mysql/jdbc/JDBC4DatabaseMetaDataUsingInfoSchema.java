/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.JDBC4DatabaseMetaData;
import com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class JDBC4DatabaseMetaDataUsingInfoSchema
extends DatabaseMetaDataUsingInfoSchema {
    public JDBC4DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet) throws SQLException {
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
    protected ResultSet getProcedureColumnsNoISParametersView(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        Field[] fields = this.createProcedureColumnsFields();
        return this.getProcedureOrFunctionColumns((Field[])fields, (String)catalog, (String)schemaPattern, (String)procedureNamePattern, (String)columnNamePattern, (boolean)true, (boolean)this.conn.getGetProceduresReturnsFunctions());
    }

    @Override
    protected String getRoutineTypeConditionForGetProcedures() {
        if (!this.conn.getGetProceduresReturnsFunctions()) return "ROUTINE_TYPE = 'PROCEDURE' AND ";
        return "";
    }

    @Override
    protected String getRoutineTypeConditionForGetProcedureColumns() {
        if (!this.conn.getGetProceduresReturnsFunctions()) return "ROUTINE_TYPE = 'PROCEDURE' AND ";
        return "";
    }

    @Override
    protected int getJDBC4FunctionConstant(DatabaseMetaDataUsingInfoSchema.JDBC4FunctionConstant constant) {
        switch (1.$SwitchMap$com$mysql$jdbc$DatabaseMetaDataUsingInfoSchema$JDBC4FunctionConstant[constant.ordinal()]) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 4;
            }
            case 5: {
                return 5;
            }
            case 6: {
                return 0;
            }
            case 7: {
                return 0;
            }
            case 8: {
                return 1;
            }
            case 9: {
                return 2;
            }
        }
        return -1;
    }

    @Override
    protected int getJDBC4FunctionNoTableConstant() {
        return 1;
    }

    @Override
    protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
        return JDBC4DatabaseMetaData.getProcedureOrFunctionColumnType((boolean)isOutParam, (boolean)isInParam, (boolean)isReturnParam, (boolean)forGetFunctionColumns);
    }
}

