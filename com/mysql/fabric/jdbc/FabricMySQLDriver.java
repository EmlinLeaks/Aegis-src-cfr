/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.jdbc;

import com.mysql.fabric.jdbc.FabricMySQLConnectionProxy;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class FabricMySQLDriver
extends NonRegisteringDriver
implements Driver {
    public static final String FABRIC_URL_PREFIX = "jdbc:mysql:fabric://";
    public static final String FABRIC_SHARD_KEY_PROPERTY_KEY = "fabricShardKey";
    public static final String FABRIC_SHARD_TABLE_PROPERTY_KEY = "fabricShardTable";
    public static final String FABRIC_SERVER_GROUP_PROPERTY_KEY = "fabricServerGroup";
    public static final String FABRIC_PROTOCOL_PROPERTY_KEY = "fabricProtocol";
    public static final String FABRIC_USERNAME_PROPERTY_KEY = "fabricUsername";
    public static final String FABRIC_PASSWORD_PROPERTY_KEY = "fabricPassword";
    public static final String FABRIC_REPORT_ERRORS_PROPERTY_KEY = "fabricReportErrors";

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Properties parsedProps = this.parseFabricURL((String)url, (Properties)info);
        if (parsedProps == null) {
            return null;
        }
        parsedProps.setProperty((String)FABRIC_PROTOCOL_PROPERTY_KEY, (String)"http");
        if (!Util.isJdbc4()) return new FabricMySQLConnectionProxy((Properties)parsedProps);
        try {
            Constructor<?> jdbc4proxy = Class.forName((String)"com.mysql.fabric.jdbc.JDBC4FabricMySQLConnectionProxy").getConstructor(Properties.class);
            return (Connection)Util.handleNewInstance(jdbc4proxy, (Object[])new Object[]{parsedProps}, null);
        }
        catch (Exception e) {
            throw (SQLException)new SQLException((String)e.getMessage()).initCause((Throwable)e);
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (this.parseFabricURL((String)url, null) == null) return false;
        return true;
    }

    Properties parseFabricURL(String url, Properties defaults) throws SQLException {
        if (url.startsWith((String)FABRIC_URL_PREFIX)) return super.parseURL((String)url.replaceAll((String)"fabric:", (String)""), (Properties)defaults);
        return null;
    }

    @Override
    public Logger getParentLogger() throws SQLException {
        throw new SQLException((String)"no logging");
    }

    static {
        try {
            DriverManager.registerDriver((Driver)new FabricMySQLDriver());
            return;
        }
        catch (SQLException ex) {
            throw new RuntimeException((String)"Can't register driver", (Throwable)ex);
        }
    }
}

