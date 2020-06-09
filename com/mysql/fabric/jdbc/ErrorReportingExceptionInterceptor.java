/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.jdbc;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.jdbc.FabricMySQLConnectionProxy;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;
import java.util.Properties;

public class ErrorReportingExceptionInterceptor
implements ExceptionInterceptor {
    private String hostname;
    private String port;
    private String fabricHaGroup;

    @Override
    public SQLException interceptException(SQLException sqlEx, Connection conn) {
        MySQLConnection mysqlConn = (MySQLConnection)conn;
        if (ConnectionImpl.class.isAssignableFrom(mysqlConn.getMultiHostSafeProxy().getClass())) {
            return null;
        }
        FabricMySQLConnectionProxy fabricProxy = (FabricMySQLConnectionProxy)mysqlConn.getMultiHostSafeProxy();
        try {
            return fabricProxy.interceptException((SQLException)sqlEx, (Connection)conn, (String)this.fabricHaGroup, (String)this.hostname, (String)this.port);
        }
        catch (FabricCommunicationException ex) {
            return SQLError.createSQLException((String)"Failed to report error to Fabric.", (String)"08S01", (Throwable)ex, null);
        }
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.hostname = props.getProperty((String)"HOST");
        this.port = props.getProperty((String)"PORT");
        String connectionAttributes = props.getProperty((String)"connectionAttributes");
        this.fabricHaGroup = connectionAttributes.replaceAll((String)"^.*\\bfabricHaGroup:(.+)\\b.*$", (String)"$1");
    }

    @Override
    public void destroy() {
    }
}

