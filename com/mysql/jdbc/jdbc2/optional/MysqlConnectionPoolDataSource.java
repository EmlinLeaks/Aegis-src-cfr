/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class MysqlConnectionPoolDataSource
extends MysqlDataSource
implements ConnectionPoolDataSource {
    static final long serialVersionUID = -7767325445592304961L;

    @Override
    public synchronized PooledConnection getPooledConnection() throws SQLException {
        java.sql.Connection connection = this.getConnection();
        return MysqlPooledConnection.getInstance((Connection)((Connection)connection));
    }

    @Override
    public synchronized PooledConnection getPooledConnection(String s, String s1) throws SQLException {
        java.sql.Connection connection = this.getConnection((String)s, (String)s1);
        return MysqlPooledConnection.getInstance((Connection)((Connection)connection));
    }
}

