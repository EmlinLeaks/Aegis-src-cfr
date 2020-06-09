/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider
 */
package com.mysql.fabric.hibernate;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.FabricConnection;
import com.mysql.fabric.Server;
import com.mysql.fabric.ServerGroup;
import com.mysql.fabric.ServerMode;
import com.mysql.fabric.ShardMapping;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;

public class FabricMultiTenantConnectionProvider
implements MultiTenantConnectionProvider {
    private static final long serialVersionUID = 1L;
    private FabricConnection fabricConnection;
    private String database;
    private String table;
    private String user;
    private String password;
    private ShardMapping shardMapping;
    private ServerGroup globalGroup;

    public FabricMultiTenantConnectionProvider(String fabricUrl, String database, String table, String user, String password, String fabricUser, String fabricPassword) {
        try {
            this.fabricConnection = new FabricConnection((String)fabricUrl, (String)fabricUser, (String)fabricPassword);
            this.database = database;
            this.table = table;
            this.user = user;
            this.password = password;
            this.shardMapping = this.fabricConnection.getShardMapping((String)this.database, (String)this.table);
            this.globalGroup = this.fabricConnection.getServerGroup((String)this.shardMapping.getGlobalGroupName());
            return;
        }
        catch (FabricCommunicationException ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }

    private Connection getReadWriteConnectionFromServerGroup(ServerGroup serverGroup) throws SQLException {
        Server s;
        Iterator<Server> iterator = serverGroup.getServers().iterator();
        do {
            if (!iterator.hasNext()) throw new SQLException((String)("Unable to find r/w server for chosen shard mapping in group " + serverGroup.getName()));
        } while (!ServerMode.READ_WRITE.equals((Object)((Object)(s = iterator.next()).getMode())));
        String jdbcUrl = String.format((String)"jdbc:mysql://%s:%s/%s", (Object[])new Object[]{s.getHostname(), Integer.valueOf((int)s.getPort()), this.database});
        return DriverManager.getConnection((String)jdbcUrl, (String)this.user, (String)this.password);
    }

    public Connection getAnyConnection() throws SQLException {
        return this.getReadWriteConnectionFromServerGroup((ServerGroup)this.globalGroup);
    }

    public Connection getConnection(String tenantIdentifier) throws SQLException {
        String serverGroupName = this.shardMapping.getGroupNameForKey((String)tenantIdentifier);
        ServerGroup serverGroup = this.fabricConnection.getServerGroup((String)serverGroupName);
        return this.getReadWriteConnectionFromServerGroup((ServerGroup)serverGroup);
    }

    public void releaseAnyConnection(Connection connection) throws SQLException {
        try {
            connection.close();
            return;
        }
        catch (Exception ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }

    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        this.releaseAnyConnection((Connection)connection);
    }

    public boolean supportsAggressiveRelease() {
        return false;
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    public <T> T unwrap(Class<T> unwrapType) {
        return (T)null;
    }
}

