/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.LoadBalancedMySQLConnection;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.StringUtils;
import java.sql.SQLException;
import java.util.Properties;

public class LoadBalancedAutoCommitInterceptor
implements StatementInterceptorV2 {
    private int matchingAfterStatementCount = 0;
    private int matchingAfterStatementThreshold = 0;
    private String matchingAfterStatementRegex;
    private ConnectionImpl conn;
    private LoadBalancedConnectionProxy proxy = null;
    private boolean countStatements = false;

    @Override
    public void destroy() {
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void init(Connection connection, Properties props) throws SQLException {
        this.conn = (ConnectionImpl)connection;
        String autoCommitSwapThresholdAsString = props.getProperty((String)"loadBalanceAutoCommitStatementThreshold", (String)"0");
        try {
            this.matchingAfterStatementThreshold = Integer.parseInt((String)autoCommitSwapThresholdAsString);
        }
        catch (NumberFormatException nfe) {
            // empty catch block
        }
        String autoCommitSwapRegex = props.getProperty((String)"loadBalanceAutoCommitStatementRegex", (String)"");
        if ("".equals((Object)autoCommitSwapRegex)) {
            return;
        }
        this.matchingAfterStatementRegex = autoCommitSwapRegex;
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException {
        if (!this.countStatements) return originalResultSet;
        if (StringUtils.startsWithIgnoreCase((String)sql, (String)"SET")) return originalResultSet;
        if (StringUtils.startsWithIgnoreCase((String)sql, (String)"SHOW")) {
            return originalResultSet;
        }
        if (!this.conn.getAutoCommit()) {
            this.matchingAfterStatementCount = 0;
            return originalResultSet;
        }
        if (this.proxy == null && this.conn.isProxySet()) {
            MySQLConnection lcl_proxy;
            for (lcl_proxy = this.conn.getMultiHostSafeProxy(); lcl_proxy != null && !(lcl_proxy instanceof LoadBalancedMySQLConnection); lcl_proxy = lcl_proxy.getMultiHostSafeProxy()) {
            }
            if (lcl_proxy != null) {
                this.proxy = ((LoadBalancedMySQLConnection)lcl_proxy).getThisAsProxy();
            }
        }
        if (this.proxy == null) {
            return originalResultSet;
        }
        if (this.matchingAfterStatementRegex == null || sql.matches((String)this.matchingAfterStatementRegex)) {
            ++this.matchingAfterStatementCount;
        }
        if (this.matchingAfterStatementCount < this.matchingAfterStatementThreshold) return originalResultSet;
        this.matchingAfterStatementCount = 0;
        try {
            this.proxy.pickNewConnection();
            return originalResultSet;
        }
        catch (SQLException e) {
            // empty catch block
        }
        return originalResultSet;
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        return null;
    }

    void pauseCounters() {
        this.countStatements = false;
    }

    void resumeCounters() {
        this.countStatements = true;
    }
}

