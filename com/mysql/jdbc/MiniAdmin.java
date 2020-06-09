/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;
import java.util.Properties;

public class MiniAdmin {
    private Connection conn;

    public MiniAdmin(java.sql.Connection conn) throws SQLException {
        if (conn == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"MiniAdmin.0"), (String)"S1000", null);
        }
        if (!(conn instanceof Connection)) {
            throw SQLError.createSQLException((String)Messages.getString((String)"MiniAdmin.1"), (String)"S1000", (ExceptionInterceptor)((ConnectionImpl)conn).getExceptionInterceptor());
        }
        this.conn = (Connection)conn;
    }

    public MiniAdmin(String jdbcUrl) throws SQLException {
        this((String)jdbcUrl, (Properties)new Properties());
    }

    public MiniAdmin(String jdbcUrl, Properties props) throws SQLException {
        this.conn = (Connection)new Driver().connect((String)jdbcUrl, (Properties)props);
    }

    public void shutdown() throws SQLException {
        this.conn.shutdownServer();
    }
}

