/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CacheAdapter;
import com.mysql.jdbc.CacheAdapterFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PerConnectionLRUFactory;
import com.mysql.jdbc.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PerConnectionLRUFactory
implements CacheAdapterFactory<String, PreparedStatement.ParseInfo> {
    @Override
    public CacheAdapter<String, PreparedStatement.ParseInfo> getInstance(Connection forConnection, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties) throws SQLException {
        return new PerConnectionLRU((PerConnectionLRUFactory)this, (Connection)forConnection, (int)cacheMaxSize, (int)maxKeySize);
    }
}

