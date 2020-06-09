/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.JDBC4PreparedStatementHelper;
import com.mysql.jdbc.JDBC4ResultSet;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import java.io.Reader;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class JDBC4CallableStatement
extends CallableStatement {
    public JDBC4CallableStatement(MySQLConnection conn, CallableStatement.CallableStatementParamInfo paramInfo) throws SQLException {
        super((MySQLConnection)conn, (CallableStatement.CallableStatementParamInfo)paramInfo);
    }

    public JDBC4CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog, (boolean)isFunctionCall);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ResultSet getParamTypes(String catalog, String routineName) throws SQLException {
        DatabaseMetaData dbmd = this.connection.getMetaData();
        if (this.callingStoredFunction) {
            return dbmd.getFunctionColumns((String)catalog, null, (String)routineName, (String)"%");
        }
        boolean getProcRetFuncsCurrentValue = this.connection.getGetProceduresReturnsFunctions();
        try {
            this.connection.setGetProceduresReturnsFunctions((boolean)false);
            ResultSet resultSet = dbmd.getProcedureColumns((String)catalog, null, (String)routineName, (String)"%");
            return resultSet;
        }
        finally {
            this.connection.setGetProceduresReturnsFunctions((boolean)getProcRetFuncsCurrentValue);
        }
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        JDBC4PreparedStatementHelper.setRowId((PreparedStatement)this, (int)parameterIndex, (RowId)x);
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        JDBC4PreparedStatementHelper.setRowId((PreparedStatement)this, (int)this.getNamedParamIndex((String)parameterName, (boolean)false), (RowId)x);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        JDBC4PreparedStatementHelper.setSQLXML((PreparedStatement)this, (int)parameterIndex, (SQLXML)xmlObject);
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        JDBC4PreparedStatementHelper.setSQLXML((PreparedStatement)this, (int)this.getNamedParamIndex((String)parameterName, (boolean)false), (SQLXML)xmlObject);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        RowId retValue = ((JDBC4ResultSet)rs).getRowId((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        RowId retValue = ((JDBC4ResultSet)rs).getRowId((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        JDBC4PreparedStatementHelper.setNClob((PreparedStatement)this, (int)parameterIndex, (NClob)value);
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        JDBC4PreparedStatementHelper.setNClob((PreparedStatement)this, (int)this.getNamedParamIndex((String)parameterName, (boolean)false), (NClob)value);
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        this.setNClob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader);
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        this.setNClob((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (Reader)reader, (long)length);
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        this.setNString((int)this.getNamedParamIndex((String)parameterName, (boolean)false), (String)value);
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Reader retValue = rs.getCharacterStream((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Reader retValue = rs.getCharacterStream((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        NClob retValue = ((JDBC4ResultSet)rs).getNClob((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        NClob retValue = ((JDBC4ResultSet)rs).getNClob((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)parameterIndex);
        String retValue = ((JDBC4ResultSet)rs).getNString((int)this.mapOutputParameterIndexToRsIndex((int)parameterIndex));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        ResultSetInternalMethods rs = this.getOutputParameters((int)0);
        String retValue = ((JDBC4ResultSet)rs).getNString((String)this.fixParameterName((String)parameterName));
        this.outputParamWasNull = rs.wasNull();
        return retValue;
    }
}

