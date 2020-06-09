/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.interceptors;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptor;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class SessionAssociationInterceptor
implements StatementInterceptor {
    protected String currentSessionKey;
    protected static final ThreadLocal<String> sessionLocal = new ThreadLocal<T>();

    public static final void setSessionKey(String key) {
        sessionLocal.set((String)key);
    }

    public static final void resetSessionKey() {
        sessionLocal.set(null);
    }

    public static final String getSessionKey() {
        return sessionLocal.get();
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        String key = SessionAssociationInterceptor.getSessionKey();
        if (key == null) return null;
        if (key.equals((Object)this.currentSessionKey)) return null;
        PreparedStatement pstmt = connection.clientPrepareStatement((String)"SET @mysql_proxy_session=?");
        try {
            pstmt.setString((int)1, (String)key);
            pstmt.execute();
            Object var7_6 = null;
            pstmt.close();
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            pstmt.close();
            throw throwable;
        }
        this.currentSessionKey = key;
        return null;
    }

    @Override
    public void destroy() {
    }
}

