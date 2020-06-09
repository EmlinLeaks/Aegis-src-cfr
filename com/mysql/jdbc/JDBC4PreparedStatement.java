/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.JDBC4PreparedStatementHelper;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class JDBC4PreparedStatement
extends PreparedStatement {
    public JDBC4PreparedStatement(MySQLConnection conn, String catalog) throws SQLException {
        super((MySQLConnection)conn, (String)catalog);
    }

    public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog);
    }

    public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog, PreparedStatement.ParseInfo cachedParseInfo) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog, (PreparedStatement.ParseInfo)cachedParseInfo);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        JDBC4PreparedStatementHelper.setRowId((PreparedStatement)this, (int)parameterIndex, (RowId)x);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        JDBC4PreparedStatementHelper.setNClob((PreparedStatement)this, (int)parameterIndex, (NClob)value);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        JDBC4PreparedStatementHelper.setSQLXML((PreparedStatement)this, (int)parameterIndex, (SQLXML)xmlObject);
    }
}

