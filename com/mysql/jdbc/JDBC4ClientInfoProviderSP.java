/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.JDBC4ClientInfoProvider;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class JDBC4ClientInfoProviderSP
implements JDBC4ClientInfoProvider {
    PreparedStatement setClientInfoSp;
    PreparedStatement getClientInfoSp;
    PreparedStatement getClientInfoBulkSp;

    @Override
    public synchronized void initialize(java.sql.Connection conn, Properties configurationProps) throws SQLException {
        String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
        String setClientInfoSpName = configurationProps.getProperty((String)"clientInfoSetSPName", (String)"setClientInfo");
        String getClientInfoSpName = configurationProps.getProperty((String)"clientInfoGetSPName", (String)"getClientInfo");
        String getClientInfoBulkSpName = configurationProps.getProperty((String)"clientInfoGetBulkSPName", (String)"getClientInfoBulk");
        String clientInfoCatalog = configurationProps.getProperty((String)"clientInfoCatalog", (String)"");
        String catalog = "".equals((Object)clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
        this.setClientInfoSp = ((Connection)conn).clientPrepareStatement((String)("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)"));
        this.getClientInfoSp = ((Connection)conn).clientPrepareStatement((String)("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)"));
        this.getClientInfoBulkSp = ((Connection)conn).clientPrepareStatement((String)("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()"));
    }

    @Override
    public synchronized void destroy() throws SQLException {
        if (this.setClientInfoSp != null) {
            this.setClientInfoSp.close();
            this.setClientInfoSp = null;
        }
        if (this.getClientInfoSp != null) {
            this.getClientInfoSp.close();
            this.getClientInfoSp = null;
        }
        if (this.getClientInfoBulkSp == null) return;
        this.getClientInfoBulkSp.close();
        this.getClientInfoBulkSp = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException {
        ResultSet rs = null;
        Properties props = new Properties();
        try {
            this.getClientInfoBulkSp.execute();
            rs = this.getClientInfoBulkSp.getResultSet();
            while (rs.next()) {
                props.setProperty((String)rs.getString((int)1), (String)rs.getString((int)2));
            }
            return props;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException {
        ResultSet rs = null;
        String clientInfo = null;
        try {
            this.getClientInfoSp.setString((int)1, (String)name);
            this.getClientInfoSp.execute();
            rs = this.getClientInfoSp.getResultSet();
            if (!rs.next()) return clientInfo;
            clientInfo = rs.getString((int)1);
            return clientInfo;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException {
        try {
            Enumeration<?> propNames = properties.propertyNames();
            while (propNames.hasMoreElements()) {
                String name = (String)propNames.nextElement();
                String value = properties.getProperty((String)name);
                this.setClientInfo((java.sql.Connection)conn, (String)name, (String)value);
            }
            return;
        }
        catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause((Throwable)sqlEx);
            throw clientInfoEx;
        }
    }

    @Override
    public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException {
        try {
            this.setClientInfoSp.setString((int)1, (String)name);
            this.setClientInfoSp.setString((int)2, (String)value);
            this.setClientInfoSp.execute();
            return;
        }
        catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause((Throwable)sqlEx);
            throw clientInfoEx;
        }
    }
}

