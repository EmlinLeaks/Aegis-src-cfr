/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CacheAdapter;
import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface CacheAdapterFactory<K, V> {
    public CacheAdapter<K, V> getInstance(Connection var1, String var2, int var3, int var4, Properties var5) throws SQLException;
}

