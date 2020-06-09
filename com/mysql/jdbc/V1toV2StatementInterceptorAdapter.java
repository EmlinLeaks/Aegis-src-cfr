/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptor;
import com.mysql.jdbc.StatementInterceptorV2;
import java.sql.SQLException;
import java.util.Properties;

public class V1toV2StatementInterceptorAdapter
implements StatementInterceptorV2 {
    private final StatementInterceptor toProxy;

    public V1toV2StatementInterceptorAdapter(StatementInterceptor toProxy) {
        this.toProxy = toProxy;
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException {
        return this.toProxy.postProcess((String)sql, (Statement)interceptedStatement, (ResultSetInternalMethods)originalResultSet, (Connection)connection);
    }

    @Override
    public void destroy() {
        this.toProxy.destroy();
    }

    @Override
    public boolean executeTopLevelOnly() {
        return this.toProxy.executeTopLevelOnly();
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.toProxy.init((Connection)conn, (Properties)props);
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        return this.toProxy.preProcess((String)sql, (Statement)interceptedStatement, (Connection)connection);
    }
}

