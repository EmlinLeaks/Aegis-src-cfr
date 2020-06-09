/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;
import java.sql.SQLException;
import java.util.Properties;

public class NoSubInterceptorWrapper
implements StatementInterceptorV2 {
    private final StatementInterceptorV2 underlyingInterceptor;

    public NoSubInterceptorWrapper(StatementInterceptorV2 underlyingInterceptor) {
        if (underlyingInterceptor == null) {
            throw new RuntimeException((String)"Interceptor to be wrapped can not be NULL");
        }
        this.underlyingInterceptor = underlyingInterceptor;
    }

    @Override
    public void destroy() {
        this.underlyingInterceptor.destroy();
    }

    @Override
    public boolean executeTopLevelOnly() {
        return this.underlyingInterceptor.executeTopLevelOnly();
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.underlyingInterceptor.init((Connection)conn, (Properties)props);
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException {
        this.underlyingInterceptor.postProcess((String)sql, (Statement)interceptedStatement, (ResultSetInternalMethods)originalResultSet, (Connection)connection, (int)warningCount, (boolean)noIndexUsed, (boolean)noGoodIndexUsed, (SQLException)statementException);
        return null;
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        this.underlyingInterceptor.preProcess((String)sql, (Statement)interceptedStatement, (Connection)connection);
        return null;
    }

    public StatementInterceptorV2 getUnderlyingInterceptor() {
        return this.underlyingInterceptor;
    }
}

