/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BalanceStrategy
extends Extension {
    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy var1, List<String> var2, Map<String, ConnectionImpl> var3, long[] var4, int var5) throws SQLException;
}

