/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.jdbc;

import com.mysql.fabric.ServerGroup;
import com.mysql.jdbc.MySQLConnection;
import java.sql.SQLException;
import java.util.Set;

public interface FabricMySQLConnection
extends MySQLConnection {
    public void clearServerSelectionCriteria() throws SQLException;

    public void setShardKey(String var1) throws SQLException;

    public String getShardKey();

    public void setShardTable(String var1) throws SQLException;

    public String getShardTable();

    public void setServerGroupName(String var1) throws SQLException;

    public String getServerGroupName();

    public ServerGroup getCurrentServerGroup();

    public void clearQueryTables() throws SQLException;

    public void addQueryTable(String var1) throws SQLException;

    public Set<String> getQueryTables();
}

