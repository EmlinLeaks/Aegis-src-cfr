/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public class MysqlXADataSource
extends MysqlDataSource
implements XADataSource {
    static final long serialVersionUID = 7911390333152247455L;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        java.sql.Connection conn = this.getConnection();
        return this.wrapConnection((java.sql.Connection)conn);
    }

    @Override
    public XAConnection getXAConnection(String u, String p) throws SQLException {
        java.sql.Connection conn = this.getConnection((String)u, (String)p);
        return this.wrapConnection((java.sql.Connection)conn);
    }

    private XAConnection wrapConnection(java.sql.Connection conn) throws SQLException {
        if (this.getPinGlobalTxToPhysicalConnection()) return SuspendableXAConnection.getInstance((Connection)((Connection)conn));
        if (!((Connection)conn).getPinGlobalTxToPhysicalConnection()) return MysqlXAConnection.getInstance((Connection)((Connection)conn), (boolean)this.getLogXaCommands());
        return SuspendableXAConnection.getInstance((Connection)((Connection)conn));
    }
}

