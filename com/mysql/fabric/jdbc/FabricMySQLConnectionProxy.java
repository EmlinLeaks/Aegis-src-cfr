/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.jdbc;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.FabricConnection;
import com.mysql.fabric.Server;
import com.mysql.fabric.ServerGroup;
import com.mysql.fabric.ShardMapping;
import com.mysql.fabric.jdbc.FabricMySQLConnection;
import com.mysql.fabric.jdbc.FabricMySQLConnectionProperties;
import com.mysql.fabric.proto.xmlrpc.XmlRpcClient;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.CachedResultSetMetaData;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.Field;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.ReplicationConnection;
import com.mysql.jdbc.ReplicationConnectionGroup;
import com.mysql.jdbc.ReplicationConnectionGroupManager;
import com.mysql.jdbc.ReplicationConnectionProxy;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.ServerPreparedStatement;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StatementInterceptorV2;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogFactory;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.Executor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FabricMySQLConnectionProxy
extends ConnectionPropertiesImpl
implements FabricMySQLConnection,
FabricMySQLConnectionProperties {
    private static final long serialVersionUID = 5845485979107347258L;
    private Log log;
    protected FabricConnection fabricConnection;
    protected boolean closed = false;
    protected boolean transactionInProgress = false;
    protected Map<ServerGroup, ReplicationConnection> serverConnections = new HashMap<ServerGroup, ReplicationConnection>();
    protected ReplicationConnection currentConnection;
    protected String shardKey;
    protected String shardTable;
    protected String serverGroupName;
    protected Set<String> queryTables = new HashSet<String>();
    protected ServerGroup serverGroup;
    protected String host;
    protected String port;
    protected String username;
    protected String password;
    protected String database;
    protected ShardMapping shardMapping;
    protected boolean readOnly = false;
    protected boolean autoCommit = true;
    protected int transactionIsolation = 4;
    private String fabricShardKey;
    private String fabricShardTable;
    private String fabricServerGroup;
    private String fabricProtocol;
    private String fabricUsername;
    private String fabricPassword;
    private boolean reportErrors = false;
    private static final Set<String> replConnGroupLocks = Collections.synchronizedSet(new HashSet<E>());
    private static final Class<?> JDBC4_NON_TRANSIENT_CONN_EXCEPTION;

    public FabricMySQLConnectionProxy(Properties props) throws SQLException {
        String exceptionInterceptors;
        this.fabricShardKey = props.getProperty((String)"fabricShardKey");
        this.fabricShardTable = props.getProperty((String)"fabricShardTable");
        this.fabricServerGroup = props.getProperty((String)"fabricServerGroup");
        this.fabricProtocol = props.getProperty((String)"fabricProtocol");
        this.fabricUsername = props.getProperty((String)"fabricUsername");
        this.fabricPassword = props.getProperty((String)"fabricPassword");
        this.reportErrors = Boolean.valueOf((String)props.getProperty((String)"fabricReportErrors")).booleanValue();
        props.remove((Object)"fabricShardKey");
        props.remove((Object)"fabricShardTable");
        props.remove((Object)"fabricServerGroup");
        props.remove((Object)"fabricProtocol");
        props.remove((Object)"fabricUsername");
        props.remove((Object)"fabricPassword");
        props.remove((Object)"fabricReportErrors");
        this.host = props.getProperty((String)"HOST");
        this.port = props.getProperty((String)"PORT");
        this.username = props.getProperty((String)"user");
        this.password = props.getProperty((String)"password");
        this.database = props.getProperty((String)"DBNAME");
        if (this.username == null) {
            this.username = "";
        }
        if (this.password == null) {
            this.password = "";
        }
        exceptionInterceptors = (exceptionInterceptors = props.getProperty((String)"exceptionInterceptors")) == null || "null".equals((Object)"exceptionInterceptors") ? "" : exceptionInterceptors + ",";
        exceptionInterceptors = exceptionInterceptors + "com.mysql.fabric.jdbc.ErrorReportingExceptionInterceptor";
        props.setProperty((String)"exceptionInterceptors", (String)exceptionInterceptors);
        this.initializeProperties((Properties)props);
        if (this.fabricServerGroup != null && this.fabricShardTable != null) {
            throw SQLError.createSQLException((String)"Server group and shard table are mutually exclusive. Only one may be provided.", (String)"08004", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        try {
            String url = this.fabricProtocol + "://" + this.host + ":" + this.port;
            this.fabricConnection = new FabricConnection((String)url, (String)this.fabricUsername, (String)this.fabricPassword);
        }
        catch (FabricCommunicationException ex) {
            throw SQLError.createSQLException((String)"Unable to establish connection to the Fabric server", (String)"08004", (Throwable)ex, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        this.log = LogFactory.getLogger((String)this.getLogger(), (String)"FabricMySQLConnectionProxy", null);
        this.setShardTable((String)this.fabricShardTable);
        this.setShardKey((String)this.fabricShardKey);
        this.setServerGroupName((String)this.fabricServerGroup);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized SQLException interceptException(SQLException sqlEx, Connection conn, String groupName, String hostname, String portNumber) throws FabricCommunicationException {
        if (!(sqlEx.getSQLState() != null && sqlEx.getSQLState().startsWith((String)"08") || MySQLNonTransientConnectionException.class.isAssignableFrom(sqlEx.getClass()))) {
            if (JDBC4_NON_TRANSIENT_CONN_EXCEPTION == null) return null;
            if (!JDBC4_NON_TRANSIENT_CONN_EXCEPTION.isAssignableFrom(sqlEx.getClass())) return null;
        }
        if (sqlEx.getCause() != null && FabricCommunicationException.class.isAssignableFrom(sqlEx.getCause().getClass())) {
            return null;
        }
        Server currentServer = this.serverGroup.getServer((String)(hostname + ":" + portNumber));
        if (currentServer == null) {
            return null;
        }
        if (this.reportErrors) {
            this.fabricConnection.getClient().reportServerError((Server)currentServer, (String)sqlEx.toString(), (boolean)true);
        }
        if (!replConnGroupLocks.add((String)this.serverGroup.getName())) return SQLError.createSQLException((String)"Fabric state syncing already in progress in another thread.", (String)"08006", (Throwable)sqlEx, null);
        try {
            try {
                this.fabricConnection.refreshStatePassive();
                this.setCurrentServerGroup((String)this.serverGroup.getName());
            }
            catch (SQLException ex) {
                SQLException sQLException = SQLError.createSQLException((String)"Unable to refresh Fabric state. Failover impossible", (String)"08006", (Throwable)ex, null);
                Object var10_11 = null;
                replConnGroupLocks.remove((Object)this.serverGroup.getName());
                return sQLException;
            }
            try {
                this.syncGroupServersToReplicationConnectionGroup((ReplicationConnectionGroup)ReplicationConnectionGroupManager.getConnectionGroup((String)groupName));
            }
            catch (SQLException ex) {
                SQLException sQLException = ex;
                Object var10_12 = null;
                replConnGroupLocks.remove((Object)this.serverGroup.getName());
                return sQLException;
            }
            Object var10_13 = null;
            replConnGroupLocks.remove((Object)this.serverGroup.getName());
            return null;
        }
        catch (Throwable throwable) {
            Object var10_14 = null;
            replConnGroupLocks.remove((Object)this.serverGroup.getName());
            throw throwable;
        }
    }

    private void refreshStateIfNecessary() throws SQLException {
        if (!this.fabricConnection.isStateExpired()) return;
        this.fabricConnection.refreshStatePassive();
        if (this.serverGroup == null) return;
        this.setCurrentServerGroup((String)this.serverGroup.getName());
    }

    @Override
    public void setShardKey(String shardKey) throws SQLException {
        this.ensureNoTransactionInProgress();
        this.currentConnection = null;
        if (shardKey != null) {
            if (this.serverGroupName != null) {
                throw SQLError.createSQLException((String)"Shard key cannot be provided when server group is chosen directly.", (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
            }
            if (this.shardTable == null) {
                throw SQLError.createSQLException((String)"Shard key cannot be provided without a shard table.", (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
            }
            this.setCurrentServerGroup((String)this.shardMapping.getGroupNameForKey((String)shardKey));
        } else if (this.shardTable != null) {
            this.setCurrentServerGroup((String)this.shardMapping.getGlobalGroupName());
        }
        this.shardKey = shardKey;
    }

    @Override
    public String getShardKey() {
        return this.shardKey;
    }

    @Override
    public void setShardTable(String shardTable) throws SQLException {
        this.ensureNoTransactionInProgress();
        this.currentConnection = null;
        if (this.serverGroupName != null) {
            throw SQLError.createSQLException((String)"Server group and shard table are mutually exclusive. Only one may be provided.", (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        this.shardKey = null;
        this.serverGroup = null;
        this.shardTable = shardTable;
        if (shardTable == null) {
            this.shardMapping = null;
            return;
        }
        String table = shardTable;
        String db = this.database;
        if (shardTable.contains((CharSequence)".")) {
            String[] pair = shardTable.split((String)"\\.");
            db = pair[0];
            table = pair[1];
        }
        this.shardMapping = this.fabricConnection.getShardMapping((String)db, (String)table);
        if (this.shardMapping == null) {
            throw SQLError.createSQLException((String)("Shard mapping not found for table `" + shardTable + "'"), (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        this.setCurrentServerGroup((String)this.shardMapping.getGlobalGroupName());
    }

    @Override
    public String getShardTable() {
        return this.shardTable;
    }

    @Override
    public void setServerGroupName(String serverGroupName) throws SQLException {
        this.ensureNoTransactionInProgress();
        this.currentConnection = null;
        if (serverGroupName != null) {
            this.setCurrentServerGroup((String)serverGroupName);
        }
        this.serverGroupName = serverGroupName;
    }

    @Override
    public String getServerGroupName() {
        return this.serverGroupName;
    }

    @Override
    public void clearServerSelectionCriteria() throws SQLException {
        this.ensureNoTransactionInProgress();
        this.shardTable = null;
        this.shardKey = null;
        this.serverGroupName = null;
        this.serverGroup = null;
        this.queryTables.clear();
        this.currentConnection = null;
    }

    @Override
    public ServerGroup getCurrentServerGroup() {
        return this.serverGroup;
    }

    @Override
    public void clearQueryTables() throws SQLException {
        this.ensureNoTransactionInProgress();
        this.currentConnection = null;
        this.queryTables.clear();
        this.setShardTable(null);
    }

    @Override
    public void addQueryTable(String tableName) throws SQLException {
        this.ensureNoTransactionInProgress();
        this.currentConnection = null;
        if (this.shardMapping == null) {
            if (this.fabricConnection.getShardMapping((String)this.database, (String)tableName) != null) {
                this.setShardTable((String)tableName);
            }
        } else {
            ShardMapping mappingForTableName = this.fabricConnection.getShardMapping((String)this.database, (String)tableName);
            if (mappingForTableName != null && !mappingForTableName.equals((Object)this.shardMapping)) {
                throw SQLError.createSQLException((String)"Cross-shard query not allowed", (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
            }
        }
        this.queryTables.add((String)tableName);
    }

    @Override
    public Set<String> getQueryTables() {
        return this.queryTables;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setCurrentServerGroup(String serverGroupName) throws SQLException {
        this.serverGroup = this.fabricConnection.getServerGroup((String)serverGroupName);
        if (this.serverGroup == null) {
            throw SQLError.createSQLException((String)("Cannot find server group: `" + serverGroupName + "'"), (String)"S1009", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        ReplicationConnectionGroup replConnGroup = ReplicationConnectionGroupManager.getConnectionGroup((String)serverGroupName);
        if (replConnGroup == null) return;
        if (!replConnGroupLocks.add((String)this.serverGroup.getName())) return;
        try {
            this.syncGroupServersToReplicationConnectionGroup((ReplicationConnectionGroup)replConnGroup);
            Object var4_3 = null;
            replConnGroupLocks.remove((Object)this.serverGroup.getName());
            return;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            replConnGroupLocks.remove((Object)this.serverGroup.getName());
            throw throwable;
        }
    }

    protected MySQLConnection getActiveMySQLConnectionChecked() throws SQLException {
        ReplicationConnection c = (ReplicationConnection)this.getActiveConnection();
        return (MySQLConnection)c.getCurrentConnection();
    }

    @Override
    public MySQLConnection getActiveMySQLConnection() {
        try {
            return this.getActiveMySQLConnectionChecked();
        }
        catch (SQLException ex) {
            throw new IllegalStateException((String)"Unable to determine active connection", (Throwable)ex);
        }
    }

    protected Connection getActiveConnectionPassive() {
        try {
            return this.getActiveConnection();
        }
        catch (SQLException ex) {
            throw new IllegalStateException((String)"Unable to determine active connection", (Throwable)ex);
        }
    }

    private void syncGroupServersToReplicationConnectionGroup(ReplicationConnectionGroup replConnGroup) throws SQLException {
        Server newMaster;
        String currentMasterString = null;
        if (replConnGroup.getMasterHosts().size() == 1) {
            currentMasterString = replConnGroup.getMasterHosts().iterator().next();
        }
        if (!(currentMasterString == null || this.serverGroup.getMaster() != null && currentMasterString.equals((Object)this.serverGroup.getMaster().getHostPortString()))) {
            try {
                replConnGroup.removeMasterHost((String)currentMasterString, (boolean)false);
            }
            catch (SQLException ex) {
                this.getLog().logWarn((Object)("Unable to remove master: " + currentMasterString), (Throwable)ex);
            }
        }
        if ((newMaster = this.serverGroup.getMaster()) != null && replConnGroup.getMasterHosts().size() == 0) {
            this.getLog().logInfo((Object)("Changing master for group '" + replConnGroup.getGroupName() + "' to: " + newMaster));
            try {
                if (!replConnGroup.getSlaveHosts().contains((Object)newMaster.getHostPortString())) {
                    replConnGroup.addSlaveHost((String)newMaster.getHostPortString());
                }
                replConnGroup.promoteSlaveToMaster((String)newMaster.getHostPortString());
            }
            catch (SQLException ex) {
                throw SQLError.createSQLException((String)("Unable to promote new master '" + newMaster.toString() + "'"), (String)ex.getSQLState(), (Throwable)ex, null);
            }
        }
        for (Server s : this.serverGroup.getServers()) {
            if (!s.isSlave()) continue;
            try {
                replConnGroup.addSlaveHost((String)s.getHostPortString());
            }
            catch (SQLException ex) {
                this.getLog().logWarn((Object)("Unable to add slave: " + s.toString()), (Throwable)ex);
            }
        }
        Iterator<Object> i$ = replConnGroup.getSlaveHosts().iterator();
        while (i$.hasNext()) {
            String hostPortString = (String)i$.next();
            Server fabServer = this.serverGroup.getServer((String)hostPortString);
            if (fabServer != null && fabServer.isSlave()) continue;
            try {
                replConnGroup.removeSlaveHost((String)hostPortString, (boolean)true);
            }
            catch (SQLException ex) {
                this.getLog().logWarn((Object)("Unable to remove slave: " + hostPortString), (Throwable)ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Connection getActiveConnection() throws SQLException {
        if (!this.transactionInProgress) {
            this.refreshStateIfNecessary();
        }
        if (this.currentConnection != null) {
            return this.currentConnection;
        }
        if (this.getCurrentServerGroup() == null) {
            throw SQLError.createSQLException((String)"No server group selected.", (String)"08004", null, (ExceptionInterceptor)this.getExceptionInterceptor(), (Connection)this);
        }
        this.currentConnection = this.serverConnections.get((Object)this.serverGroup);
        if (this.currentConnection != null) {
            return this.currentConnection;
        }
        ArrayList<String> masterHost = new ArrayList<String>();
        ArrayList<String> slaveHosts = new ArrayList<String>();
        for (Server s : this.serverGroup.getServers()) {
            if (s.isMaster()) {
                masterHost.add(s.getHostPortString());
                continue;
            }
            if (!s.isSlave()) continue;
            slaveHosts.add(s.getHostPortString());
        }
        Properties info = this.exposeAsProperties(null);
        ReplicationConnectionGroup replConnGroup = ReplicationConnectionGroupManager.getConnectionGroup((String)this.serverGroup.getName());
        if (replConnGroup != null && replConnGroupLocks.add((String)this.serverGroup.getName())) {
            try {
                this.syncGroupServersToReplicationConnectionGroup((ReplicationConnectionGroup)replConnGroup);
                Object var6_5 = null;
                replConnGroupLocks.remove((Object)this.serverGroup.getName());
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                replConnGroupLocks.remove((Object)this.serverGroup.getName());
                throw throwable;
            }
        }
        info.put("replicationConnectionGroup", this.serverGroup.getName());
        info.setProperty((String)"user", (String)this.username);
        info.setProperty((String)"password", (String)this.password);
        info.setProperty((String)"DBNAME", (String)this.getCatalog());
        info.setProperty((String)"connectionAttributes", (String)("fabricHaGroup:" + this.serverGroup.getName()));
        info.setProperty((String)"retriesAllDown", (String)"1");
        info.setProperty((String)"allowMasterDownConnections", (String)"true");
        info.setProperty((String)"allowSlaveDownConnections", (String)"true");
        info.setProperty((String)"readFromMasterWhenNoSlaves", (String)"true");
        this.currentConnection = ReplicationConnectionProxy.createProxyInstance(masterHost, (Properties)info, slaveHosts, (Properties)info);
        this.serverConnections.put((ServerGroup)this.serverGroup, (ReplicationConnection)this.currentConnection);
        this.currentConnection.setProxy((MySQLConnection)this);
        this.currentConnection.setAutoCommit((boolean)this.autoCommit);
        this.currentConnection.setReadOnly((boolean)this.readOnly);
        this.currentConnection.setTransactionIsolation((int)this.transactionIsolation);
        return this.currentConnection;
    }

    private void ensureOpen() throws SQLException {
        if (!this.closed) return;
        throw SQLError.createSQLException((String)"No operations allowed after connection closed.", (String)"08003", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    private void ensureNoTransactionInProgress() throws SQLException {
        this.ensureOpen();
        if (!this.transactionInProgress) return;
        if (this.autoCommit) return;
        throw SQLError.createSQLException((String)"Not allow while a transaction is active.", (String)"25000", (ExceptionInterceptor)this.getExceptionInterceptor());
    }

    @Override
    public void close() throws SQLException {
        this.closed = true;
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            try {
                c.close();
            }
            catch (SQLException ex) {}
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (this.closed) return false;
        return true;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection conn = i$.next();
            conn.setReadOnly((boolean)readOnly);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.readOnly;
    }

    @Override
    public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
        return this.readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.database = catalog;
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setCatalog((String)catalog);
        }
    }

    @Override
    public String getCatalog() {
        return this.database;
    }

    @Override
    public void rollback() throws SQLException {
        this.getActiveConnection().rollback();
        this.transactionCompleted();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.getActiveConnection().rollback();
        this.transactionCompleted();
    }

    @Override
    public void commit() throws SQLException {
        this.getActiveConnection().commit();
        this.transactionCompleted();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setAutoCommit((boolean)this.autoCommit);
        }
    }

    @Override
    public void transactionBegun() throws SQLException {
        if (this.autoCommit) return;
        this.transactionInProgress = true;
    }

    @Override
    public void transactionCompleted() throws SQLException {
        this.transactionInProgress = false;
        this.refreshStateIfNecessary();
    }

    @Override
    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    @Deprecated
    @Override
    public MySQLConnection getLoadBalanceSafeProxy() {
        return this.getMultiHostSafeProxy();
    }

    @Override
    public MySQLConnection getMultiHostSafeProxy() {
        return this.getActiveMySQLConnection();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.transactionIsolation = level;
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setTransactionIsolation((int)level);
        }
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setTypeMap(map);
        }
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setHoldability((int)holdability);
        }
    }

    @Override
    public void setProxy(MySQLConnection proxy) {
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.getActiveConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        this.transactionInProgress = true;
        return this.getActiveConnection().setSavepoint((String)name);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) {
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareCall((String)sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareCall((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql, (int)autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql, (int[])columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().prepareStatement((String)sql, (String[])columnNames);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql, (int)autoGenKeyIndex);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql, (int[])autoGenKeyIndexes);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().clientPrepareStatement((String)sql, (String[])autoGenKeyColNames);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql, (int)autoGenKeyIndex);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql, (int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql, (int[])autoGenKeyIndexes);
    }

    @Override
    public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().serverPrepareStatement((String)sql, (String[])autoGenKeyColNames);
    }

    @Override
    public java.sql.Statement createStatement() throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().createStatement();
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().createStatement((int)resultSetType, (int)resultSetConcurrency);
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.transactionBegun();
        return this.getActiveConnection().createStatement((int)resultSetType, (int)resultSetConcurrency, (int)resultSetHoldability);
    }

    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException {
        return this.getActiveMySQLConnectionChecked().execSQL((StatementImpl)callingStatement, (String)sql, (int)maxRows, (Buffer)packet, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata);
    }

    @Override
    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException {
        return this.getActiveMySQLConnectionChecked().execSQL((StatementImpl)callingStatement, (String)sql, (int)maxRows, (Buffer)packet, (int)resultSetType, (int)resultSetConcurrency, (boolean)streamResults, (String)catalog, (Field[])cachedMetadata, (boolean)isBatch);
    }

    @Override
    public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException {
        return this.getActiveMySQLConnectionChecked().extractSqlFromPacket((String)possibleSqlQuery, (Buffer)queryPacket, (int)endOfQueryPacketPosition);
    }

    @Override
    public StringBuilder generateConnectionCommentBlock(StringBuilder buf) {
        return this.getActiveMySQLConnection().generateConnectionCommentBlock((StringBuilder)buf);
    }

    @Override
    public MysqlIO getIO() throws SQLException {
        return this.getActiveMySQLConnectionChecked().getIO();
    }

    @Override
    public Calendar getCalendarInstanceForSessionOrNew() {
        return this.getActiveMySQLConnection().getCalendarInstanceForSessionOrNew();
    }

    @Deprecated
    @Override
    public String getServerCharacterEncoding() {
        return this.getServerCharset();
    }

    @Override
    public String getServerCharset() {
        return this.getActiveMySQLConnection().getServerCharset();
    }

    @Override
    public TimeZone getServerTimezoneTZ() {
        return this.getActiveMySQLConnection().getServerTimezoneTZ();
    }

    @Override
    public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
        return this.getActiveConnection().versionMeetsMinimum((int)major, (int)minor, (int)subminor);
    }

    @Override
    public boolean supportsIsolationLevel() {
        return this.getActiveConnectionPassive().supportsIsolationLevel();
    }

    @Override
    public boolean supportsQuotedIdentifiers() {
        return this.getActiveConnectionPassive().supportsQuotedIdentifiers();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.getActiveConnection().getMetaData();
    }

    @Override
    public String getCharacterSetMetadata() {
        return this.getActiveMySQLConnection().getCharacterSetMetadata();
    }

    @Override
    public java.sql.Statement getMetadataSafeStatement() throws SQLException {
        return this.getActiveMySQLConnectionChecked().getMetadataSafeStatement();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return (T)null;
    }

    @Override
    public void unSafeStatementInterceptors() throws SQLException {
    }

    @Override
    public boolean supportsTransactions() {
        return true;
    }

    @Override
    public boolean isRunningOnJDK13() {
        return false;
    }

    @Override
    public void createNewIO(boolean isForReconnect) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Override
    public void dumpTestcaseQuery(String query) {
    }

    @Override
    public void abortInternal() throws SQLException {
    }

    @Override
    public boolean isServerLocal() throws SQLException {
        return false;
    }

    @Override
    public void shutdownServer() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    @Deprecated
    @Override
    public void clearHasTriedMaster() {
    }

    @Deprecated
    @Override
    public boolean hasTriedMaster() {
        return false;
    }

    @Override
    public boolean isInGlobalTx() {
        return false;
    }

    @Override
    public void setInGlobalTx(boolean flag) {
        throw new RuntimeException((String)"Global transactions not supported.");
    }

    @Override
    public void changeUser(String userName, String newPassword) throws SQLException {
        throw SQLError.createSQLException((String)"User change not allowed.", (ExceptionInterceptor)this.getExceptionInterceptor());
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
        this.reportErrors = value;
    }

    @Override
    public boolean getFabricReportErrors() {
        return this.reportErrors;
    }

    @Override
    public void setAllowLoadLocalInfile(boolean property) {
        super.setAllowLoadLocalInfile((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAllowLoadLocalInfile((boolean)property);
        }
    }

    @Override
    public void setAllowMultiQueries(boolean property) {
        super.setAllowMultiQueries((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAllowMultiQueries((boolean)property);
        }
    }

    @Override
    public void setAllowNanAndInf(boolean flag) {
        super.setAllowNanAndInf((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAllowNanAndInf((boolean)flag);
        }
    }

    @Override
    public void setAllowUrlInLocalInfile(boolean flag) {
        super.setAllowUrlInLocalInfile((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAllowUrlInLocalInfile((boolean)flag);
        }
    }

    @Override
    public void setAlwaysSendSetIsolation(boolean flag) {
        super.setAlwaysSendSetIsolation((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAlwaysSendSetIsolation((boolean)flag);
        }
    }

    @Override
    public void setAutoDeserialize(boolean flag) {
        super.setAutoDeserialize((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoDeserialize((boolean)flag);
        }
    }

    @Override
    public void setAutoGenerateTestcaseScript(boolean flag) {
        super.setAutoGenerateTestcaseScript((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoGenerateTestcaseScript((boolean)flag);
        }
    }

    @Override
    public void setAutoReconnect(boolean flag) {
        super.setAutoReconnect((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoReconnect((boolean)flag);
        }
    }

    @Override
    public void setAutoReconnectForConnectionPools(boolean property) {
        super.setAutoReconnectForConnectionPools((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoReconnectForConnectionPools((boolean)property);
        }
    }

    @Override
    public void setAutoReconnectForPools(boolean flag) {
        super.setAutoReconnectForPools((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoReconnectForPools((boolean)flag);
        }
    }

    @Override
    public void setBlobSendChunkSize(String value) throws SQLException {
        super.setBlobSendChunkSize((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setBlobSendChunkSize((String)value);
        }
    }

    @Override
    public void setCacheCallableStatements(boolean flag) {
        super.setCacheCallableStatements((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCacheCallableStatements((boolean)flag);
        }
    }

    @Override
    public void setCachePreparedStatements(boolean flag) {
        super.setCachePreparedStatements((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCachePreparedStatements((boolean)flag);
        }
    }

    @Override
    public void setCacheResultSetMetadata(boolean property) {
        super.setCacheResultSetMetadata((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCacheResultSetMetadata((boolean)property);
        }
    }

    @Override
    public void setCacheServerConfiguration(boolean flag) {
        super.setCacheServerConfiguration((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCacheServerConfiguration((boolean)flag);
        }
    }

    @Override
    public void setCallableStatementCacheSize(int size) throws SQLException {
        super.setCallableStatementCacheSize((int)size);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCallableStatementCacheSize((int)size);
        }
    }

    @Override
    public void setCapitalizeDBMDTypes(boolean property) {
        super.setCapitalizeDBMDTypes((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCapitalizeDBMDTypes((boolean)property);
        }
    }

    @Override
    public void setCapitalizeTypeNames(boolean flag) {
        super.setCapitalizeTypeNames((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCapitalizeTypeNames((boolean)flag);
        }
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        super.setCharacterEncoding((String)encoding);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCharacterEncoding((String)encoding);
        }
    }

    @Override
    public void setCharacterSetResults(String characterSet) {
        super.setCharacterSetResults((String)characterSet);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCharacterSetResults((String)characterSet);
        }
    }

    @Override
    public void setClobberStreamingResults(boolean flag) {
        super.setClobberStreamingResults((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClobberStreamingResults((boolean)flag);
        }
    }

    @Override
    public void setClobCharacterEncoding(String encoding) {
        super.setClobCharacterEncoding((String)encoding);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClobCharacterEncoding((String)encoding);
        }
    }

    @Override
    public void setConnectionCollation(String collation) {
        super.setConnectionCollation((String)collation);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setConnectionCollation((String)collation);
        }
    }

    @Override
    public void setConnectTimeout(int timeoutMs) throws SQLException {
        super.setConnectTimeout((int)timeoutMs);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setConnectTimeout((int)timeoutMs);
        }
    }

    @Override
    public void setContinueBatchOnError(boolean property) {
        super.setContinueBatchOnError((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setContinueBatchOnError((boolean)property);
        }
    }

    @Override
    public void setCreateDatabaseIfNotExist(boolean flag) {
        super.setCreateDatabaseIfNotExist((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCreateDatabaseIfNotExist((boolean)flag);
        }
    }

    @Override
    public void setDefaultFetchSize(int n) throws SQLException {
        super.setDefaultFetchSize((int)n);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDefaultFetchSize((int)n);
        }
    }

    @Override
    public void setDetectServerPreparedStmts(boolean property) {
        super.setDetectServerPreparedStmts((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDetectServerPreparedStmts((boolean)property);
        }
    }

    @Override
    public void setDontTrackOpenResources(boolean flag) {
        super.setDontTrackOpenResources((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDontTrackOpenResources((boolean)flag);
        }
    }

    @Override
    public void setDumpQueriesOnException(boolean flag) {
        super.setDumpQueriesOnException((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDumpQueriesOnException((boolean)flag);
        }
    }

    @Override
    public void setDynamicCalendars(boolean flag) {
        super.setDynamicCalendars((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDynamicCalendars((boolean)flag);
        }
    }

    @Override
    public void setElideSetAutoCommits(boolean flag) {
        super.setElideSetAutoCommits((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setElideSetAutoCommits((boolean)flag);
        }
    }

    @Override
    public void setEmptyStringsConvertToZero(boolean flag) {
        super.setEmptyStringsConvertToZero((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEmptyStringsConvertToZero((boolean)flag);
        }
    }

    @Override
    public void setEmulateLocators(boolean property) {
        super.setEmulateLocators((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEmulateLocators((boolean)property);
        }
    }

    @Override
    public void setEmulateUnsupportedPstmts(boolean flag) {
        super.setEmulateUnsupportedPstmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEmulateUnsupportedPstmts((boolean)flag);
        }
    }

    @Override
    public void setEnablePacketDebug(boolean flag) {
        super.setEnablePacketDebug((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEnablePacketDebug((boolean)flag);
        }
    }

    @Override
    public void setEncoding(String property) {
        super.setEncoding((String)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEncoding((String)property);
        }
    }

    @Override
    public void setExplainSlowQueries(boolean flag) {
        super.setExplainSlowQueries((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setExplainSlowQueries((boolean)flag);
        }
    }

    @Override
    public void setFailOverReadOnly(boolean flag) {
        super.setFailOverReadOnly((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setFailOverReadOnly((boolean)flag);
        }
    }

    @Override
    public void setGatherPerformanceMetrics(boolean flag) {
        super.setGatherPerformanceMetrics((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setGatherPerformanceMetrics((boolean)flag);
        }
    }

    @Override
    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        super.setHoldResultsOpenOverStatementClose((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setHoldResultsOpenOverStatementClose((boolean)flag);
        }
    }

    @Override
    public void setIgnoreNonTxTables(boolean property) {
        super.setIgnoreNonTxTables((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setIgnoreNonTxTables((boolean)property);
        }
    }

    @Override
    public void setInitialTimeout(int property) throws SQLException {
        super.setInitialTimeout((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setInitialTimeout((int)property);
        }
    }

    @Override
    public void setIsInteractiveClient(boolean property) {
        super.setIsInteractiveClient((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setIsInteractiveClient((boolean)property);
        }
    }

    @Override
    public void setJdbcCompliantTruncation(boolean flag) {
        super.setJdbcCompliantTruncation((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setJdbcCompliantTruncation((boolean)flag);
        }
    }

    @Override
    public void setLocatorFetchBufferSize(String value) throws SQLException {
        super.setLocatorFetchBufferSize((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLocatorFetchBufferSize((String)value);
        }
    }

    @Override
    public void setLogger(String property) {
        super.setLogger((String)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLogger((String)property);
        }
    }

    @Override
    public void setLoggerClassName(String className) {
        super.setLoggerClassName((String)className);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoggerClassName((String)className);
        }
    }

    @Override
    public void setLogSlowQueries(boolean flag) {
        super.setLogSlowQueries((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLogSlowQueries((boolean)flag);
        }
    }

    @Override
    public void setMaintainTimeStats(boolean flag) {
        super.setMaintainTimeStats((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setMaintainTimeStats((boolean)flag);
        }
    }

    @Override
    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        super.setMaxQuerySizeToLog((int)sizeInBytes);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setMaxQuerySizeToLog((int)sizeInBytes);
        }
    }

    @Override
    public void setMaxReconnects(int property) throws SQLException {
        super.setMaxReconnects((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setMaxReconnects((int)property);
        }
    }

    @Override
    public void setMaxRows(int property) throws SQLException {
        super.setMaxRows((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setMaxRows((int)property);
        }
    }

    @Override
    public void setMetadataCacheSize(int value) throws SQLException {
        super.setMetadataCacheSize((int)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setMetadataCacheSize((int)value);
        }
    }

    @Override
    public void setNoDatetimeStringSync(boolean flag) {
        super.setNoDatetimeStringSync((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNoDatetimeStringSync((boolean)flag);
        }
    }

    @Override
    public void setNullCatalogMeansCurrent(boolean value) {
        super.setNullCatalogMeansCurrent((boolean)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNullCatalogMeansCurrent((boolean)value);
        }
    }

    @Override
    public void setNullNamePatternMatchesAll(boolean value) {
        super.setNullNamePatternMatchesAll((boolean)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNullNamePatternMatchesAll((boolean)value);
        }
    }

    @Override
    public void setPacketDebugBufferSize(int size) throws SQLException {
        super.setPacketDebugBufferSize((int)size);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPacketDebugBufferSize((int)size);
        }
    }

    @Override
    public void setParanoid(boolean property) {
        super.setParanoid((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setParanoid((boolean)property);
        }
    }

    @Override
    public void setPedantic(boolean property) {
        super.setPedantic((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPedantic((boolean)property);
        }
    }

    @Override
    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        super.setPreparedStatementCacheSize((int)cacheSize);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPreparedStatementCacheSize((int)cacheSize);
        }
    }

    @Override
    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        super.setPreparedStatementCacheSqlLimit((int)cacheSqlLimit);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPreparedStatementCacheSqlLimit((int)cacheSqlLimit);
        }
    }

    @Override
    public void setProfileSql(boolean property) {
        super.setProfileSql((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setProfileSql((boolean)property);
        }
    }

    @Override
    public void setProfileSQL(boolean flag) {
        super.setProfileSQL((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setProfileSQL((boolean)flag);
        }
    }

    @Override
    public void setPropertiesTransform(String value) {
        super.setPropertiesTransform((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPropertiesTransform((String)value);
        }
    }

    @Override
    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        super.setQueriesBeforeRetryMaster((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setQueriesBeforeRetryMaster((int)property);
        }
    }

    @Override
    public void setReconnectAtTxEnd(boolean property) {
        super.setReconnectAtTxEnd((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setReconnectAtTxEnd((boolean)property);
        }
    }

    @Override
    public void setRelaxAutoCommit(boolean property) {
        super.setRelaxAutoCommit((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRelaxAutoCommit((boolean)property);
        }
    }

    @Override
    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        super.setReportMetricsIntervalMillis((int)millis);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setReportMetricsIntervalMillis((int)millis);
        }
    }

    @Override
    public void setRequireSSL(boolean property) {
        super.setRequireSSL((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRequireSSL((boolean)property);
        }
    }

    @Override
    public void setRetainStatementAfterResultSetClose(boolean flag) {
        super.setRetainStatementAfterResultSetClose((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRetainStatementAfterResultSetClose((boolean)flag);
        }
    }

    @Override
    public void setRollbackOnPooledClose(boolean flag) {
        super.setRollbackOnPooledClose((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRollbackOnPooledClose((boolean)flag);
        }
    }

    @Override
    public void setRoundRobinLoadBalance(boolean flag) {
        super.setRoundRobinLoadBalance((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRoundRobinLoadBalance((boolean)flag);
        }
    }

    @Override
    public void setRunningCTS13(boolean flag) {
        super.setRunningCTS13((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRunningCTS13((boolean)flag);
        }
    }

    @Override
    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        super.setSecondsBeforeRetryMaster((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSecondsBeforeRetryMaster((int)property);
        }
    }

    @Override
    public void setServerTimezone(String property) {
        super.setServerTimezone((String)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setServerTimezone((String)property);
        }
    }

    @Override
    public void setSessionVariables(String variables) {
        super.setSessionVariables((String)variables);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSessionVariables((String)variables);
        }
    }

    @Override
    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        super.setSlowQueryThresholdMillis((int)millis);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSlowQueryThresholdMillis((int)millis);
        }
    }

    @Override
    public void setSocketFactoryClassName(String property) {
        super.setSocketFactoryClassName((String)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSocketFactoryClassName((String)property);
        }
    }

    @Override
    public void setSocketTimeout(int property) throws SQLException {
        super.setSocketTimeout((int)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSocketTimeout((int)property);
        }
    }

    @Override
    public void setStrictFloatingPoint(boolean property) {
        super.setStrictFloatingPoint((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setStrictFloatingPoint((boolean)property);
        }
    }

    @Override
    public void setStrictUpdates(boolean property) {
        super.setStrictUpdates((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setStrictUpdates((boolean)property);
        }
    }

    @Override
    public void setTinyInt1isBit(boolean flag) {
        super.setTinyInt1isBit((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTinyInt1isBit((boolean)flag);
        }
    }

    @Override
    public void setTraceProtocol(boolean flag) {
        super.setTraceProtocol((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTraceProtocol((boolean)flag);
        }
    }

    @Override
    public void setTransformedBitIsBoolean(boolean flag) {
        super.setTransformedBitIsBoolean((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTransformedBitIsBoolean((boolean)flag);
        }
    }

    @Override
    public void setUseCompression(boolean property) {
        super.setUseCompression((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseCompression((boolean)property);
        }
    }

    @Override
    public void setUseFastIntParsing(boolean flag) {
        super.setUseFastIntParsing((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseFastIntParsing((boolean)flag);
        }
    }

    @Override
    public void setUseHostsInPrivileges(boolean property) {
        super.setUseHostsInPrivileges((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseHostsInPrivileges((boolean)property);
        }
    }

    @Override
    public void setUseInformationSchema(boolean flag) {
        super.setUseInformationSchema((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseInformationSchema((boolean)flag);
        }
    }

    @Override
    public void setUseLocalSessionState(boolean flag) {
        super.setUseLocalSessionState((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseLocalSessionState((boolean)flag);
        }
    }

    @Override
    public void setUseOldUTF8Behavior(boolean flag) {
        super.setUseOldUTF8Behavior((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseOldUTF8Behavior((boolean)flag);
        }
    }

    @Override
    public void setUseOnlyServerErrorMessages(boolean flag) {
        super.setUseOnlyServerErrorMessages((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseOnlyServerErrorMessages((boolean)flag);
        }
    }

    @Override
    public void setUseReadAheadInput(boolean flag) {
        super.setUseReadAheadInput((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseReadAheadInput((boolean)flag);
        }
    }

    @Override
    public void setUseServerPreparedStmts(boolean flag) {
        super.setUseServerPreparedStmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseServerPreparedStmts((boolean)flag);
        }
    }

    @Override
    public void setUseSqlStateCodes(boolean flag) {
        super.setUseSqlStateCodes((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseSqlStateCodes((boolean)flag);
        }
    }

    @Override
    public void setUseSSL(boolean property) {
        super.setUseSSL((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseSSL((boolean)property);
        }
    }

    @Override
    public void setUseStreamLengthsInPrepStmts(boolean property) {
        super.setUseStreamLengthsInPrepStmts((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseStreamLengthsInPrepStmts((boolean)property);
        }
    }

    @Override
    public void setUseTimezone(boolean property) {
        super.setUseTimezone((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseTimezone((boolean)property);
        }
    }

    @Override
    public void setUseUltraDevWorkAround(boolean property) {
        super.setUseUltraDevWorkAround((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseUltraDevWorkAround((boolean)property);
        }
    }

    @Override
    public void setUseUnbufferedInput(boolean flag) {
        super.setUseUnbufferedInput((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseUnbufferedInput((boolean)flag);
        }
    }

    @Override
    public void setUseUnicode(boolean flag) {
        super.setUseUnicode((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseUnicode((boolean)flag);
        }
    }

    @Override
    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        super.setUseUsageAdvisor((boolean)useUsageAdvisorFlag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseUsageAdvisor((boolean)useUsageAdvisorFlag);
        }
    }

    @Override
    public void setYearIsDateType(boolean flag) {
        super.setYearIsDateType((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setYearIsDateType((boolean)flag);
        }
    }

    @Override
    public void setZeroDateTimeBehavior(String behavior) {
        super.setZeroDateTimeBehavior((String)behavior);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setZeroDateTimeBehavior((String)behavior);
        }
    }

    @Override
    public void setUseCursorFetch(boolean flag) {
        super.setUseCursorFetch((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseCursorFetch((boolean)flag);
        }
    }

    @Override
    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        super.setOverrideSupportsIntegrityEnhancementFacility((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setOverrideSupportsIntegrityEnhancementFacility((boolean)flag);
        }
    }

    @Override
    public void setNoTimezoneConversionForTimeType(boolean flag) {
        super.setNoTimezoneConversionForTimeType((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNoTimezoneConversionForTimeType((boolean)flag);
        }
    }

    @Override
    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        super.setUseJDBCCompliantTimezoneShift((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseJDBCCompliantTimezoneShift((boolean)flag);
        }
    }

    @Override
    public void setAutoClosePStmtStreams(boolean flag) {
        super.setAutoClosePStmtStreams((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoClosePStmtStreams((boolean)flag);
        }
    }

    @Override
    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        super.setProcessEscapeCodesForPrepStmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setProcessEscapeCodesForPrepStmts((boolean)flag);
        }
    }

    @Override
    public void setUseGmtMillisForDatetimes(boolean flag) {
        super.setUseGmtMillisForDatetimes((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseGmtMillisForDatetimes((boolean)flag);
        }
    }

    @Override
    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        super.setDumpMetadataOnColumnNotFound((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDumpMetadataOnColumnNotFound((boolean)flag);
        }
    }

    @Override
    public void setResourceId(String resourceId) {
        super.setResourceId((String)resourceId);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setResourceId((String)resourceId);
        }
    }

    @Override
    public void setRewriteBatchedStatements(boolean flag) {
        super.setRewriteBatchedStatements((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRewriteBatchedStatements((boolean)flag);
        }
    }

    @Override
    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads) {
        super.setJdbcCompliantTruncationForReads((boolean)jdbcCompliantTruncationForReads);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setJdbcCompliantTruncationForReads((boolean)jdbcCompliantTruncationForReads);
        }
    }

    @Override
    public void setUseJvmCharsetConverters(boolean flag) {
        super.setUseJvmCharsetConverters((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseJvmCharsetConverters((boolean)flag);
        }
    }

    @Override
    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        super.setPinGlobalTxToPhysicalConnection((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPinGlobalTxToPhysicalConnection((boolean)flag);
        }
    }

    @Override
    public void setGatherPerfMetrics(boolean flag) {
        super.setGatherPerfMetrics((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setGatherPerfMetrics((boolean)flag);
        }
    }

    @Override
    public void setUltraDevHack(boolean flag) {
        super.setUltraDevHack((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUltraDevHack((boolean)flag);
        }
    }

    @Override
    public void setInteractiveClient(boolean property) {
        super.setInteractiveClient((boolean)property);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setInteractiveClient((boolean)property);
        }
    }

    @Override
    public void setSocketFactory(String name) {
        super.setSocketFactory((String)name);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSocketFactory((String)name);
        }
    }

    @Override
    public void setUseServerPrepStmts(boolean flag) {
        super.setUseServerPrepStmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseServerPrepStmts((boolean)flag);
        }
    }

    @Override
    public void setCacheCallableStmts(boolean flag) {
        super.setCacheCallableStmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCacheCallableStmts((boolean)flag);
        }
    }

    @Override
    public void setCachePrepStmts(boolean flag) {
        super.setCachePrepStmts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCachePrepStmts((boolean)flag);
        }
    }

    @Override
    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        super.setCallableStmtCacheSize((int)cacheSize);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCallableStmtCacheSize((int)cacheSize);
        }
    }

    @Override
    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        super.setPrepStmtCacheSize((int)cacheSize);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPrepStmtCacheSize((int)cacheSize);
        }
    }

    @Override
    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        super.setPrepStmtCacheSqlLimit((int)sqlLimit);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPrepStmtCacheSqlLimit((int)sqlLimit);
        }
    }

    @Override
    public void setNoAccessToProcedureBodies(boolean flag) {
        super.setNoAccessToProcedureBodies((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNoAccessToProcedureBodies((boolean)flag);
        }
    }

    @Override
    public void setUseOldAliasMetadataBehavior(boolean flag) {
        super.setUseOldAliasMetadataBehavior((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseOldAliasMetadataBehavior((boolean)flag);
        }
    }

    @Override
    public void setClientCertificateKeyStorePassword(String value) {
        super.setClientCertificateKeyStorePassword((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClientCertificateKeyStorePassword((String)value);
        }
    }

    @Override
    public void setClientCertificateKeyStoreType(String value) {
        super.setClientCertificateKeyStoreType((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClientCertificateKeyStoreType((String)value);
        }
    }

    @Override
    public void setClientCertificateKeyStoreUrl(String value) {
        super.setClientCertificateKeyStoreUrl((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClientCertificateKeyStoreUrl((String)value);
        }
    }

    @Override
    public void setTrustCertificateKeyStorePassword(String value) {
        super.setTrustCertificateKeyStorePassword((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTrustCertificateKeyStorePassword((String)value);
        }
    }

    @Override
    public void setTrustCertificateKeyStoreType(String value) {
        super.setTrustCertificateKeyStoreType((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTrustCertificateKeyStoreType((String)value);
        }
    }

    @Override
    public void setTrustCertificateKeyStoreUrl(String value) {
        super.setTrustCertificateKeyStoreUrl((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTrustCertificateKeyStoreUrl((String)value);
        }
    }

    @Override
    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        super.setUseSSPSCompatibleTimezoneShift((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseSSPSCompatibleTimezoneShift((boolean)flag);
        }
    }

    @Override
    public void setTreatUtilDateAsTimestamp(boolean flag) {
        super.setTreatUtilDateAsTimestamp((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTreatUtilDateAsTimestamp((boolean)flag);
        }
    }

    @Override
    public void setUseFastDateParsing(boolean flag) {
        super.setUseFastDateParsing((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseFastDateParsing((boolean)flag);
        }
    }

    @Override
    public void setLocalSocketAddress(String address) {
        super.setLocalSocketAddress((String)address);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLocalSocketAddress((String)address);
        }
    }

    @Override
    public void setUseConfigs(String configs) {
        super.setUseConfigs((String)configs);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseConfigs((String)configs);
        }
    }

    @Override
    public void setGenerateSimpleParameterMetadata(boolean flag) {
        super.setGenerateSimpleParameterMetadata((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setGenerateSimpleParameterMetadata((boolean)flag);
        }
    }

    @Override
    public void setLogXaCommands(boolean flag) {
        super.setLogXaCommands((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLogXaCommands((boolean)flag);
        }
    }

    @Override
    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        super.setResultSetSizeThreshold((int)threshold);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setResultSetSizeThreshold((int)threshold);
        }
    }

    @Override
    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        super.setNetTimeoutForStreamingResults((int)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setNetTimeoutForStreamingResults((int)value);
        }
    }

    @Override
    public void setEnableQueryTimeouts(boolean flag) {
        super.setEnableQueryTimeouts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setEnableQueryTimeouts((boolean)flag);
        }
    }

    @Override
    public void setPadCharsWithSpace(boolean flag) {
        super.setPadCharsWithSpace((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPadCharsWithSpace((boolean)flag);
        }
    }

    @Override
    public void setUseDynamicCharsetInfo(boolean flag) {
        super.setUseDynamicCharsetInfo((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseDynamicCharsetInfo((boolean)flag);
        }
    }

    @Override
    public void setClientInfoProvider(String classname) {
        super.setClientInfoProvider((String)classname);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setClientInfoProvider((String)classname);
        }
    }

    @Override
    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        super.setPopulateInsertRowWithDefaultValues((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPopulateInsertRowWithDefaultValues((boolean)flag);
        }
    }

    @Override
    public void setLoadBalanceStrategy(String strategy) {
        super.setLoadBalanceStrategy((String)strategy);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceStrategy((String)strategy);
        }
    }

    @Override
    public void setTcpNoDelay(boolean flag) {
        super.setTcpNoDelay((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTcpNoDelay((boolean)flag);
        }
    }

    @Override
    public void setTcpKeepAlive(boolean flag) {
        super.setTcpKeepAlive((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTcpKeepAlive((boolean)flag);
        }
    }

    @Override
    public void setTcpRcvBuf(int bufSize) throws SQLException {
        super.setTcpRcvBuf((int)bufSize);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTcpRcvBuf((int)bufSize);
        }
    }

    @Override
    public void setTcpSndBuf(int bufSize) throws SQLException {
        super.setTcpSndBuf((int)bufSize);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTcpSndBuf((int)bufSize);
        }
    }

    @Override
    public void setTcpTrafficClass(int classFlags) throws SQLException {
        super.setTcpTrafficClass((int)classFlags);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setTcpTrafficClass((int)classFlags);
        }
    }

    @Override
    public void setUseNanosForElapsedTime(boolean flag) {
        super.setUseNanosForElapsedTime((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseNanosForElapsedTime((boolean)flag);
        }
    }

    @Override
    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        super.setSlowQueryThresholdNanos((long)nanos);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSlowQueryThresholdNanos((long)nanos);
        }
    }

    @Override
    public void setStatementInterceptors(String value) {
        super.setStatementInterceptors((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setStatementInterceptors((String)value);
        }
    }

    @Override
    public void setUseDirectRowUnpack(boolean flag) {
        super.setUseDirectRowUnpack((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseDirectRowUnpack((boolean)flag);
        }
    }

    @Override
    public void setLargeRowSizeThreshold(String value) throws SQLException {
        super.setLargeRowSizeThreshold((String)value);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLargeRowSizeThreshold((String)value);
        }
    }

    @Override
    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        super.setUseBlobToStoreUTF8OutsideBMP((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseBlobToStoreUTF8OutsideBMP((boolean)flag);
        }
    }

    @Override
    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        super.setUtf8OutsideBmpExcludedColumnNamePattern((String)regexPattern);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUtf8OutsideBmpExcludedColumnNamePattern((String)regexPattern);
        }
    }

    @Override
    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        super.setUtf8OutsideBmpIncludedColumnNamePattern((String)regexPattern);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUtf8OutsideBmpIncludedColumnNamePattern((String)regexPattern);
        }
    }

    @Override
    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        super.setIncludeInnodbStatusInDeadlockExceptions((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setIncludeInnodbStatusInDeadlockExceptions((boolean)flag);
        }
    }

    @Override
    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        super.setIncludeThreadDumpInDeadlockExceptions((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setIncludeThreadDumpInDeadlockExceptions((boolean)flag);
        }
    }

    @Override
    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        super.setIncludeThreadNamesAsStatementComment((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setIncludeThreadNamesAsStatementComment((boolean)flag);
        }
    }

    @Override
    public void setBlobsAreStrings(boolean flag) {
        super.setBlobsAreStrings((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setBlobsAreStrings((boolean)flag);
        }
    }

    @Override
    public void setFunctionsNeverReturnBlobs(boolean flag) {
        super.setFunctionsNeverReturnBlobs((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setFunctionsNeverReturnBlobs((boolean)flag);
        }
    }

    @Override
    public void setAutoSlowLog(boolean flag) {
        super.setAutoSlowLog((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAutoSlowLog((boolean)flag);
        }
    }

    @Override
    public void setConnectionLifecycleInterceptors(String interceptors) {
        super.setConnectionLifecycleInterceptors((String)interceptors);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setConnectionLifecycleInterceptors((String)interceptors);
        }
    }

    @Override
    public void setProfilerEventHandler(String handler) {
        super.setProfilerEventHandler((String)handler);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setProfilerEventHandler((String)handler);
        }
    }

    @Override
    public void setVerifyServerCertificate(boolean flag) {
        super.setVerifyServerCertificate((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setVerifyServerCertificate((boolean)flag);
        }
    }

    @Override
    public void setUseLegacyDatetimeCode(boolean flag) {
        super.setUseLegacyDatetimeCode((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseLegacyDatetimeCode((boolean)flag);
        }
    }

    @Override
    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        super.setSelfDestructOnPingSecondsLifetime((int)seconds);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSelfDestructOnPingSecondsLifetime((int)seconds);
        }
    }

    @Override
    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        super.setSelfDestructOnPingMaxOperations((int)maxOperations);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setSelfDestructOnPingMaxOperations((int)maxOperations);
        }
    }

    @Override
    public void setUseColumnNamesInFindColumn(boolean flag) {
        super.setUseColumnNamesInFindColumn((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseColumnNamesInFindColumn((boolean)flag);
        }
    }

    @Override
    public void setUseLocalTransactionState(boolean flag) {
        super.setUseLocalTransactionState((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseLocalTransactionState((boolean)flag);
        }
    }

    @Override
    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        super.setCompensateOnDuplicateKeyUpdateCounts((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setCompensateOnDuplicateKeyUpdateCounts((boolean)flag);
        }
    }

    @Override
    public void setUseAffectedRows(boolean flag) {
        super.setUseAffectedRows((boolean)flag);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setUseAffectedRows((boolean)flag);
        }
    }

    @Override
    public void setPasswordCharacterEncoding(String characterSet) {
        super.setPasswordCharacterEncoding((String)characterSet);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setPasswordCharacterEncoding((String)characterSet);
        }
    }

    @Override
    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
        super.setLoadBalanceBlacklistTimeout((int)loadBalanceBlacklistTimeout);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceBlacklistTimeout((int)loadBalanceBlacklistTimeout);
        }
    }

    @Override
    public void setRetriesAllDown(int retriesAllDown) throws SQLException {
        super.setRetriesAllDown((int)retriesAllDown);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setRetriesAllDown((int)retriesAllDown);
        }
    }

    @Override
    public void setExceptionInterceptors(String exceptionInterceptors) {
        super.setExceptionInterceptors((String)exceptionInterceptors);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setExceptionInterceptors((String)exceptionInterceptors);
        }
    }

    @Override
    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
        super.setQueryTimeoutKillsConnection((boolean)queryTimeoutKillsConnection);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setQueryTimeoutKillsConnection((boolean)queryTimeoutKillsConnection);
        }
    }

    @Override
    public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
        super.setLoadBalancePingTimeout((int)loadBalancePingTimeout);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalancePingTimeout((int)loadBalancePingTimeout);
        }
    }

    @Override
    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
        super.setLoadBalanceValidateConnectionOnSwapServer((boolean)loadBalanceValidateConnectionOnSwapServer);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceValidateConnectionOnSwapServer((boolean)loadBalanceValidateConnectionOnSwapServer);
        }
    }

    @Override
    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
        super.setLoadBalanceConnectionGroup((String)loadBalanceConnectionGroup);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceConnectionGroup((String)loadBalanceConnectionGroup);
        }
    }

    @Override
    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
        super.setLoadBalanceExceptionChecker((String)loadBalanceExceptionChecker);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceExceptionChecker((String)loadBalanceExceptionChecker);
        }
    }

    @Override
    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
        super.setLoadBalanceSQLStateFailover((String)loadBalanceSQLStateFailover);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceSQLStateFailover((String)loadBalanceSQLStateFailover);
        }
    }

    @Override
    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
        super.setLoadBalanceSQLExceptionSubclassFailover((String)loadBalanceSQLExceptionSubclassFailover);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceSQLExceptionSubclassFailover((String)loadBalanceSQLExceptionSubclassFailover);
        }
    }

    @Override
    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
        super.setLoadBalanceEnableJMX((boolean)loadBalanceEnableJMX);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceEnableJMX((boolean)loadBalanceEnableJMX);
        }
    }

    @Override
    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
        super.setLoadBalanceAutoCommitStatementThreshold((int)loadBalanceAutoCommitStatementThreshold);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceAutoCommitStatementThreshold((int)loadBalanceAutoCommitStatementThreshold);
        }
    }

    @Override
    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
        super.setLoadBalanceAutoCommitStatementRegex((String)loadBalanceAutoCommitStatementRegex);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setLoadBalanceAutoCommitStatementRegex((String)loadBalanceAutoCommitStatementRegex);
        }
    }

    @Override
    public void setAuthenticationPlugins(String authenticationPlugins) {
        super.setAuthenticationPlugins((String)authenticationPlugins);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setAuthenticationPlugins((String)authenticationPlugins);
        }
    }

    @Override
    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
        super.setDisabledAuthenticationPlugins((String)disabledAuthenticationPlugins);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDisabledAuthenticationPlugins((String)disabledAuthenticationPlugins);
        }
    }

    @Override
    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
        super.setDefaultAuthenticationPlugin((String)defaultAuthenticationPlugin);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDefaultAuthenticationPlugin((String)defaultAuthenticationPlugin);
        }
    }

    @Override
    public void setParseInfoCacheFactory(String factoryClassname) {
        super.setParseInfoCacheFactory((String)factoryClassname);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setParseInfoCacheFactory((String)factoryClassname);
        }
    }

    @Override
    public void setServerConfigCacheFactory(String factoryClassname) {
        super.setServerConfigCacheFactory((String)factoryClassname);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setServerConfigCacheFactory((String)factoryClassname);
        }
    }

    @Override
    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
        super.setDisconnectOnExpiredPasswords((boolean)disconnectOnExpiredPasswords);
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection cp = i$.next();
            cp.setDisconnectOnExpiredPasswords((boolean)disconnectOnExpiredPasswords);
        }
    }

    @Override
    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        super.setGetProceduresReturnsFunctions((boolean)getProcedureReturnsFunctions);
    }

    @Override
    public int getActiveStatementCount() {
        return -1;
    }

    @Override
    public long getIdleFor() {
        return -1L;
    }

    @Override
    public Log getLog() {
        return this.log;
    }

    @Override
    public boolean isMasterConnection() {
        return false;
    }

    @Override
    public boolean isNoBackslashEscapesSet() {
        return false;
    }

    @Override
    public boolean isSameResource(Connection c) {
        return false;
    }

    @Override
    public boolean parserKnowsUnicode() {
        return false;
    }

    @Override
    public void ping() throws SQLException {
    }

    @Override
    public void resetServerState() throws SQLException {
    }

    @Override
    public void setFailedOver(boolean flag) {
    }

    @Deprecated
    @Override
    public void setPreferSlaveDuringFailover(boolean flag) {
    }

    @Override
    public void setStatementComment(String comment) {
    }

    @Override
    public void reportQueryTime(long millisOrNanos) {
    }

    @Override
    public boolean isAbonormallyLongQuery(long millisOrNanos) {
        return false;
    }

    @Override
    public void initializeExtension(Extension ex) throws SQLException {
    }

    @Override
    public int getAutoIncrementIncrement() {
        return -1;
    }

    @Override
    public boolean hasSameProperties(Connection c) {
        return false;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return -1;
    }

    @Override
    public void checkClosed() throws SQLException {
    }

    @Override
    public Object getConnectionMutex() {
        return this;
    }

    @Override
    public void setSessionMaxRows(int max) throws SQLException {
        Iterator<ReplicationConnection> i$ = this.serverConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.setSessionMaxRows((int)max);
        }
    }

    @Override
    public int getSessionMaxRows() {
        return this.getActiveConnectionPassive().getSessionMaxRows();
    }

    @Override
    public boolean isProxySet() {
        return false;
    }

    @Override
    public Connection duplicate() throws SQLException {
        return null;
    }

    @Override
    public CachedResultSetMetaData getCachedMetaData(String sql) {
        return null;
    }

    @Override
    public Timer getCancelTimer() {
        return null;
    }

    @Override
    public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
        return null;
    }

    @Deprecated
    @Override
    public String getCharsetNameForIndex(int charsetIndex) throws SQLException {
        return this.getEncodingForIndex((int)charsetIndex);
    }

    @Override
    public String getEncodingForIndex(int charsetIndex) throws SQLException {
        return null;
    }

    @Override
    public TimeZone getDefaultTimeZone() {
        return null;
    }

    @Override
    public String getErrorMessageEncoding() {
        return null;
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        if (this.currentConnection != null) return this.currentConnection.getExceptionInterceptor();
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public String getHostPortPair() {
        return this.getActiveMySQLConnection().getHostPortPair();
    }

    @Override
    public long getId() {
        return -1L;
    }

    @Override
    public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
        return -1;
    }

    @Override
    public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException {
        return -1;
    }

    @Override
    public int getNetBufferLength() {
        return -1;
    }

    @Override
    public boolean getRequiresEscapingEncoder() {
        return false;
    }

    @Override
    public int getServerMajorVersion() {
        return -1;
    }

    @Override
    public int getServerMinorVersion() {
        return -1;
    }

    @Override
    public int getServerSubMinorVersion() {
        return -1;
    }

    @Override
    public String getServerVariable(String variableName) {
        return null;
    }

    @Override
    public String getServerVersion() {
        return null;
    }

    @Override
    public Calendar getSessionLockedCalendar() {
        return null;
    }

    @Override
    public String getStatementComment() {
        return null;
    }

    @Override
    public List<StatementInterceptorV2> getStatementInterceptorsInstances() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public Calendar getUtcCalendar() {
        return null;
    }

    @Override
    public void incrementNumberOfPreparedExecutes() {
    }

    @Override
    public void incrementNumberOfPrepares() {
    }

    @Override
    public void incrementNumberOfResultSetsCreated() {
    }

    @Override
    public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
    }

    @Override
    public void initializeSafeStatementInterceptors() throws SQLException {
    }

    @Override
    public boolean isClientTzUTC() {
        return false;
    }

    @Override
    public boolean isCursorFetchEnabled() throws SQLException {
        return false;
    }

    @Override
    public boolean isReadInfoMsgEnabled() {
        return false;
    }

    @Override
    public boolean isServerTzUTC() {
        return false;
    }

    @Override
    public boolean lowerCaseTableNames() {
        return this.getActiveMySQLConnection().lowerCaseTableNames();
    }

    public void maxRowsChanged(Statement stmt) {
    }

    @Override
    public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
    }

    @Override
    public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
    }

    @Override
    public void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
    }

    @Override
    public void registerQueryExecutionTime(long queryTimeMs) {
    }

    @Override
    public void registerStatement(Statement stmt) {
    }

    @Override
    public void reportNumberOfTablesAccessed(int numTablesAccessed) {
    }

    @Override
    public boolean serverSupportsConvertFn() throws SQLException {
        return this.getActiveMySQLConnectionChecked().serverSupportsConvertFn();
    }

    @Override
    public void setReadInfoMsgEnabled(boolean flag) {
    }

    @Override
    public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
    }

    @Override
    public boolean storesLowerCaseTableName() {
        return this.getActiveMySQLConnection().storesLowerCaseTableName();
    }

    @Override
    public void throwConnectionClosedException() throws SQLException {
    }

    @Override
    public void unregisterStatement(Statement stmt) {
    }

    public void unsetMaxRows(Statement stmt) throws SQLException {
    }

    @Override
    public boolean useAnsiQuotedIdentifiers() {
        return false;
    }

    public boolean useMaxRows() {
        return false;
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public Properties getClientInfo() {
        return null;
    }

    @Override
    public String getClientInfo(String name) {
        return null;
    }

    @Override
    public int getHoldability() {
        return -1;
    }

    @Override
    public int getTransactionIsolation() {
        return -1;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getActiveMySQLConnectionChecked().getWarnings();
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.getActiveMySQLConnectionChecked().nativeSQL((String)sql);
    }

    @Override
    public ProfilerEventHandler getProfilerEventHandlerInstance() {
        return null;
    }

    @Override
    public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {
    }

    @Override
    public void decachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
    }

    @Override
    public boolean isServerTruncatesFracSecs() {
        return this.getActiveMySQLConnection().isServerTruncatesFracSecs();
    }

    @Override
    public String getQueryTimingUnits() {
        return this.getActiveMySQLConnection().getQueryTimingUnits();
    }

    static {
        Class<?> clazz = null;
        try {
            if (Util.isJdbc4()) {
                clazz = Class.forName((String)"com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException");
            }
        }
        catch (ClassNotFoundException e) {
            // empty catch block
        }
        JDBC4_NON_TRANSIENT_CONN_EXCEPTION = clazz;
    }
}

