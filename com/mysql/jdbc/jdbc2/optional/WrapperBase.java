/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import java.sql.SQLException;
import java.util.Map;

abstract class WrapperBase {
    protected MysqlPooledConnection pooledConnection;
    protected Map<Class<?>, Object> unwrappedInterfaces = null;
    protected ExceptionInterceptor exceptionInterceptor;

    protected void checkAndFireConnectionError(SQLException sqlEx) throws SQLException {
        if (this.pooledConnection == null) throw sqlEx;
        if (!"08S01".equals((Object)sqlEx.getSQLState())) throw sqlEx;
        this.pooledConnection.callConnectionEventListeners((int)1, (SQLException)sqlEx);
        throw sqlEx;
    }

    protected WrapperBase(MysqlPooledConnection pooledConnection) {
        this.pooledConnection = pooledConnection;
        this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
    }
}

