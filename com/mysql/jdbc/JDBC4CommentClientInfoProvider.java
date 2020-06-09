/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.JDBC4ClientInfoProvider;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JDBC4CommentClientInfoProvider
implements JDBC4ClientInfoProvider {
    private Properties clientInfo;

    @Override
    public synchronized void initialize(java.sql.Connection conn, Properties configurationProps) throws SQLException {
        this.clientInfo = new Properties();
    }

    @Override
    public synchronized void destroy() throws SQLException {
        this.clientInfo = null;
    }

    @Override
    public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException {
        return this.clientInfo;
    }

    @Override
    public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException {
        return this.clientInfo.getProperty((String)name);
    }

    @Override
    public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException {
        this.clientInfo = new Properties();
        Enumeration<?> propNames = properties.propertyNames();
        do {
            if (!propNames.hasMoreElements()) {
                this.setComment((java.sql.Connection)conn);
                return;
            }
            String name = (String)propNames.nextElement();
            this.clientInfo.put(name, properties.getProperty((String)name));
        } while (true);
    }

    @Override
    public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException {
        this.clientInfo.setProperty((String)name, (String)value);
        this.setComment((java.sql.Connection)conn);
    }

    private synchronized void setComment(java.sql.Connection conn) {
        StringBuilder commentBuf = new StringBuilder();
        Iterator<Map.Entry<K, V>> elements = this.clientInfo.entrySet().iterator();
        do {
            if (!elements.hasNext()) {
                ((Connection)conn).setStatementComment((String)commentBuf.toString());
                return;
            }
            if (commentBuf.length() > 0) {
                commentBuf.append((String)", ");
            }
            Map.Entry<K, V> entry = elements.next();
            commentBuf.append((String)("" + entry.getKey()));
            commentBuf.append((String)"=");
            commentBuf.append((String)("" + entry.getValue()));
        } while (true);
    }
}

