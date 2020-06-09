/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC4PreparedStatementHelper;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ServerPreparedStatement;
import java.io.Reader;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class JDBC4ServerPreparedStatement
extends ServerPreparedStatement {
    public JDBC4ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        super((MySQLConnection)conn, (String)sql, (String)catalog, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        if (!this.charEncoding.equalsIgnoreCase((String)"UTF-8") && !this.charEncoding.equalsIgnoreCase((String)"utf8")) {
            throw SQLError.createSQLException((String)"Can not call setNCharacterStream() when connection character set isn't UTF-8", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.checkClosed();
        if (reader == null) {
            this.setNull((int)parameterIndex, (int)-2);
            return;
        }
        ServerPreparedStatement.BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((ServerPreparedStatement.BindValue)binding, (int)252);
        binding.value = reader;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = length;
            return;
        }
        binding.bindLength = -1L;
    }

    @Override
    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        this.setNClob((int)parameterIndex, (Reader)x.getCharacterStream(), (long)(this.connection.getUseStreamLengthsInPrepStmts() ? x.length() : -1L));
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (!this.charEncoding.equalsIgnoreCase((String)"UTF-8") && !this.charEncoding.equalsIgnoreCase((String)"utf8")) {
            throw SQLError.createSQLException((String)"Can not call setNClob() when connection character set isn't UTF-8", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.checkClosed();
        if (reader == null) {
            this.setNull((int)parameterIndex, (int)2011);
            return;
        }
        ServerPreparedStatement.BindValue binding = this.getBinding((int)parameterIndex, (boolean)true);
        this.resetToType((ServerPreparedStatement.BindValue)binding, (int)252);
        binding.value = reader;
        binding.isLongData = true;
        if (this.connection.getUseStreamLengthsInPrepStmts()) {
            binding.bindLength = length;
            return;
        }
        binding.bindLength = -1L;
    }

    @Override
    public void setNString(int parameterIndex, String x) throws SQLException {
        if (!this.charEncoding.equalsIgnoreCase((String)"UTF-8")) {
            if (!this.charEncoding.equalsIgnoreCase((String)"utf8")) throw SQLError.createSQLException((String)"Can not call setNString() when connection character set isn't UTF-8", (ExceptionInterceptor)this.getExceptionInterceptor());
        }
        this.setString((int)parameterIndex, (String)x);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        JDBC4PreparedStatementHelper.setRowId((PreparedStatement)this, (int)parameterIndex, (RowId)x);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        JDBC4PreparedStatementHelper.setSQLXML((PreparedStatement)this, (int)parameterIndex, (SQLXML)xmlObject);
    }
}

