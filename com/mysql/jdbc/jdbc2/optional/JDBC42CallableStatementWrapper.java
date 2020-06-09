/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;
import com.mysql.jdbc.jdbc2.optional.JDBC4CallableStatementWrapper;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;

public class JDBC42CallableStatementWrapper
extends JDBC4CallableStatementWrapper {
    public JDBC42CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) {
        super((ConnectionWrapper)c, (MysqlPooledConnection)conn, (CallableStatement)toWrap);
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((int)parameterIndex, (SQLType)sqlType);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((int)parameterIndex, (SQLType)sqlType, (int)scale);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((int)parameterIndex, (SQLType)sqlType, (String)typeName);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((String)parameterName, (SQLType)sqlType);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((String)parameterName, (SQLType)sqlType, (int)scale);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).registerOutParameter((String)parameterName, (SQLType)sqlType, (String)typeName);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setObject((int)parameterIndex, (Object)x, (SQLType)targetSqlType);
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
            ((CallableStatement)this.wrappedStmt).setObject((int)parameterIndex, (Object)x, (SQLType)targetSqlType, (int)scaleOrLength);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setObject((String)parameterName, (Object)x, (SQLType)targetSqlType);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        try {
            if (this.wrappedStmt == null) throw SQLError.createSQLException((String)"No operations allowed after statement closed", (String)"S1000", (ExceptionInterceptor)this.exceptionInterceptor);
            ((CallableStatement)this.wrappedStmt).setObject((String)parameterName, (Object)x, (SQLType)targetSqlType, (int)scaleOrLength);
            return;
        }
        catch (SQLException sqlEx) {
            this.checkAndFireConnectionError((SQLException)sqlEx);
        }
    }
}

