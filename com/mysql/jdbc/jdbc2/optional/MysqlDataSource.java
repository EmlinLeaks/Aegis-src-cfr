/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.SQLError;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

public class MysqlDataSource
extends ConnectionPropertiesImpl
implements DataSource,
Referenceable,
Serializable {
    static final long serialVersionUID = -5515846944416881264L;
    protected static final NonRegisteringDriver mysqlDriver;
    protected transient PrintWriter logWriter = null;
    protected String databaseName = null;
    protected String encoding = null;
    protected String hostName = null;
    protected String password = null;
    protected String profileSql = "false";
    protected String url = null;
    protected String user = null;
    protected boolean explicitUrl = false;
    protected int port = 3306;

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection((String)this.user, (String)this.password);
    }

    @Override
    public Connection getConnection(String userID, String pass) throws SQLException {
        Properties props = new Properties();
        if (userID != null) {
            props.setProperty((String)"user", (String)userID);
        }
        if (pass != null) {
            props.setProperty((String)"password", (String)pass);
        }
        this.exposeAsProperties((Properties)props);
        return this.getConnection((Properties)props);
    }

    public void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }

    public String getDatabaseName() {
        if (this.databaseName == null) return "";
        String string = this.databaseName;
        return string;
    }

    @Override
    public void setLogWriter(PrintWriter output) throws SQLException {
        this.logWriter = output;
    }

    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public void setPort(int p) {
        this.port = p;
    }

    public int getPort() {
        return this.port;
    }

    public void setPortNumber(int p) {
        this.setPort((int)p);
    }

    public int getPortNumber() {
        return this.getPort();
    }

    public void setPropertiesViaRef(Reference ref) throws SQLException {
        super.initializeFromRef((Reference)ref);
    }

    @Override
    public Reference getReference() throws NamingException {
        String factoryName = "com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory";
        Reference ref = new Reference((String)this.getClass().getName(), (String)factoryName, null);
        ref.add((RefAddr)new StringRefAddr((String)"user", (String)this.getUser()));
        ref.add((RefAddr)new StringRefAddr((String)"password", (String)this.password));
        ref.add((RefAddr)new StringRefAddr((String)"serverName", (String)this.getServerName()));
        ref.add((RefAddr)new StringRefAddr((String)"port", (String)("" + this.getPort())));
        ref.add((RefAddr)new StringRefAddr((String)"databaseName", (String)this.getDatabaseName()));
        ref.add((RefAddr)new StringRefAddr((String)"url", (String)this.getUrl()));
        ref.add((RefAddr)new StringRefAddr((String)"explicitUrl", (String)String.valueOf((boolean)this.explicitUrl)));
        try {
            this.storeToRef((Reference)ref);
            return ref;
        }
        catch (SQLException sqlEx) {
            throw new NamingException((String)sqlEx.getMessage());
        }
    }

    public void setServerName(String serverName) {
        this.hostName = serverName;
    }

    public String getServerName() {
        if (this.hostName == null) return "";
        String string = this.hostName;
        return string;
    }

    public void setURL(String url) {
        this.setUrl((String)url);
    }

    public String getURL() {
        return this.getUrl();
    }

    public void setUrl(String url) {
        this.url = url;
        this.explicitUrl = true;
    }

    public String getUrl() {
        if (this.explicitUrl) return this.url;
        String builtUrl = "jdbc:mysql://";
        return builtUrl + this.getServerName() + ":" + this.getPort() + "/" + this.getDatabaseName();
    }

    public void setUser(String userID) {
        this.user = userID;
    }

    public String getUser() {
        return this.user;
    }

    protected Connection getConnection(Properties props) throws SQLException {
        String jdbcUrlToUse = null;
        if (!this.explicitUrl) {
            StringBuilder jdbcUrl = new StringBuilder((String)"jdbc:mysql://");
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
        Properties urlProps = mysqlDriver.parseURL((String)jdbcUrlToUse, null);
        if (urlProps == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"MysqlDataSource.BadUrl", (Object[])new Object[]{jdbcUrlToUse}), (String)"08006", null);
        }
        urlProps.remove((Object)"DBNAME");
        urlProps.remove((Object)"HOST");
        urlProps.remove((Object)"PORT");
        Iterator<K> keys = urlProps.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            props.setProperty((String)key, (String)urlProps.getProperty((String)key));
        }
        return mysqlDriver.connect((String)jdbcUrlToUse, (Properties)props);
    }

    @Override
    public Properties exposeAsProperties(Properties props) throws SQLException {
        return this.exposeAsProperties((Properties)props, (boolean)true);
    }

    static {
        try {
            mysqlDriver = new NonRegisteringDriver();
            return;
        }
        catch (Exception E) {
            throw new RuntimeException((String)"Can not load Driver class com.mysql.jdbc.Driver");
        }
    }
}

