/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;

public class JDBC42PreparedStatementWrapper
extends JDBC4PreparedStatementWrapper {
    public JDBC42PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
        super((ConnectionWrapper)c, (MysqlPooledConnection)conn, (PreparedStatement)toWrap);
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setObject((int)parameterIndex, (Object)x, (SQLType)targetSqlType);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((PreparedStatement)this.wrappedStmt).setObject((int)parameterIndex, (Object)x, (SQLType)targetSqlType, (int)scaleOrLength);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }
}

