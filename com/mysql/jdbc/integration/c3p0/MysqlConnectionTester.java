/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.C3P0ProxyConnection
 *  com.mchange.v2.c3p0.QueryConnectionTester
 */
package com.mysql.jdbc.integration.c3p0;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.QueryConnectionTester;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class MysqlConnectionTester
implements QueryConnectionTester {
    private static final long serialVersionUID = 3256444690067896368L;
    private static final Object[] NO_ARGS_ARRAY = new Object[0];
    private transient Method pingMethod;

    public MysqlConnectionTester() {
        try {
            this.pingMethod = Connection.class.getMethod((String)"ping", (Class[])null);
            return;
        }
        catch (Exception ex) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int activeCheckConnection(java.sql.Connection con) {
        try {
            if (this.pingMethod != null) {
                if (con instanceof Connection) {
                    ((Connection)con).ping();
                    return 0;
                }
                C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
                castCon.rawConnectionOperation((Method)this.pingMethod, (Object)C3P0ProxyConnection.RAW_CONNECTION, (Object[])NO_ARGS_ARRAY);
                return 0;
            }
            Statement pingStatement = null;
            try {
                pingStatement = con.createStatement();
                pingStatement.executeQuery((String)"SELECT 1").close();
                return 0;
            }
            finally {
                if (pingStatement != null) {
                    pingStatement.close();
                }
            }
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public int statusOnException(java.sql.Connection arg0, Throwable throwable) {
        if (throwable instanceof CommunicationsException) return -1;
        if ("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals((Object)throwable.getClass().getName())) {
            return -1;
        }
        if (!(throwable instanceof SQLException)) return -1;
        String sqlState = ((SQLException)throwable).getSQLState();
        if (sqlState == null) return 0;
        if (!sqlState.startsWith((String)"08")) return 0;
        return -1;
    }

    public int activeCheckConnection(java.sql.Connection arg0, String arg1) {
        return 0;
    }
}

