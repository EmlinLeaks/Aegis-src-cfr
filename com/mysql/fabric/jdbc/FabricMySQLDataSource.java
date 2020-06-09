/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.jdbc;

import com.mysql.fabric.jdbc.FabricMySQLConnectionProperties;
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class FabricMySQLDataSource
extends MysqlDataSource
implements FabricMySQLConnectionProperties {
    private static final long serialVersionUID = 1L;
    private static final Driver driver;
    private String fabricShardKey;
    private String fabricShardTable;
    private String fabricServerGroup;
    private String fabricProtocol = "http";
    private String fabricUsername;
    private String fabricPassword;
    private boolean fabricReportErrors = false;

    @Override
    protected Connection getConnection(Properties props) throws SQLException {
        String jdbcUrlToUse = null;
        if (!this.explicitUrl) {
            StringBuilder jdbcUrl = new StringBuilder((String)"jdbc:mysql:fabric://");
            if (this.hostName != null) {
                jdbcUrl.append((String)this.hostName);
            }
            jdbcUrl.append((String)":");
            jdbcUrl.append((int)this.port);
            jdbcUrl.append((String)"/");
            if (this.databaseName != null) {
                jdbcUrl.append((String)this.databaseName);
            }
            jdbcUrlToUse = jdbcUrl.toString();
        } else {
            jdbcUrlToUse = this.url;
        }
        Properties urlProps = ((FabricMySQLDriver)driver).parseFabricURL((String)jdbcUrlToUse, null);
        urlProps.remove((Object)"DBNAME");
        urlProps.remove((Object)"HOST");
        urlProps.remove((Object)"PORT");
        for (String key : urlProps.keySet()) {
            props.setProperty((String)key, (String)urlProps.getProperty((String)key));
        }
        if (this.fabricShardKey != null) {
            props.setProperty((String)"fabricShardKey", (String)this.fabricShardKey);
        }
        if (this.fabricShardTable != null) {
            props.setProperty((String)"fabricShardTable", (String)this.fabricShardTable);
        }
        if (this.fabricServerGroup != null) {
            props.setProperty((String)"fabricServerGroup", (String)this.fabricServerGroup);
        }
        props.setProperty((String)"fabricProtocol", (String)this.fabricProtocol);
        if (this.fabricUsername != null) {
            props.setProperty((String)"fabricUsername", (String)this.fabricUsername);
        }
        if (this.fabricPassword != null) {
            props.setProperty((String)"fabricPassword", (String)this.fabricPassword);
        }
        props.setProperty((String)"fabricReportErrors", (String)Boolean.toString((boolean)this.fabricReportErrors));
        return driver.connect((String)jdbcUrlToUse, (Properties)props);
    }

    @Override
    public void setFabricShardKey(String value) {
        this.fabricShardKey = value;
    }

    @Override
    public String getFabricShardKey() {
        return this.fabricShardKey;
    }

    @Override
    public void setFabricShardTable(String value) {
        this.fabricShardTable = value;
    }

    @Override
    public String getFabricShardTable() {
        return this.fabricShardTable;
    }

    @Override
    public void setFabricServerGroup(String value) {
        this.fabricServerGroup = value;
    }

    @Override
    public String getFabricServerGroup() {
        return this.fabricServerGroup;
    }

    @Override
    public void setFabricProtocol(String value) {
        this.fabricProtocol = value;
    }

    @Override
    public String getFabricProtocol() {
        return this.fabricProtocol;
    }

    @Override
    public void setFabricUsername(String value) {
        this.fabricUsername = value;
    }

    @Override
    public String getFabricUsername() {
        return this.fabricUsername;
    }

    @Override
    public void setFabricPassword(String value) {
        this.fabricPassword = value;
    }

    @Override
    public String getFabricPassword() {
        return this.fabricPassword;
    }

    @Override
    public void setFabricReportErrors(boolean value) {
        this.fabricReportErrors = value;
    }

    @Override
    public boolean getFabricReportErrors() {
        return this.fabricReportErrors;
    }

    static {
        try {
            driver = new FabricMySQLDriver();
            return;
        }
        catch (Exception ex) {
            throw new RuntimeException((String)"Can create driver", (Throwable)ex);
        }
    }
}

