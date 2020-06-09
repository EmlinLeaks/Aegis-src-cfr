/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.JDBC4MysqlSQLXML;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.SQLError;
import java.io.Reader;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class JDBC4PreparedStatementHelper {
    private JDBC4PreparedStatementHelper() {
    }

    static void setRowId(PreparedStatement pstmt, int parameterIndex, RowId x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    static void setNClob(PreparedStatement pstmt, int parameterIndex, NClob value) throws SQLException {
        if (value == null) {
            pstmt.setNull((int)parameterIndex, (int)2011);
            return;
        }
        pstmt.setNCharacterStream((int)parameterIndex, (Reader)value.getCharacterStream(), (long)value.length());
    }

    static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader) throws SQLException {
        pstmt.setNCharacterStream((int)parameterIndex, (Reader)reader);
    }

    static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader, long length) throws SQLException {
        if (reader == null) {
            pstmt.setNull((int)parameterIndex, (int)2011);
            return;
        }
        pstmt.setNCharacterStream((int)parameterIndex, (Reader)reader, (long)length);
    }

    static void setSQLXML(PreparedStatement pstmt, int parameterIndex, SQLXML xmlObject) throws SQLException {
        if (xmlObject == null) {
            pstmt.setNull((int)parameterIndex, (int)2009);
            return;
        }
        pstmt.setCharacterStream((int)parameterIndex, (Reader)((JDBC4MysqlSQLXML)xmlObject).serializeAsCharacterStream());
    }
}

