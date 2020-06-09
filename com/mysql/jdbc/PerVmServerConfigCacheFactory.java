/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CacheAdapter;
import com.mysql.jdbc.CacheAdapterFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PerVmServerConfigCacheFactory;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PerVmServerConfigCacheFactory
implements CacheAdapterFactory<String, Map<String, String>> {
    static final ConcurrentHashMap<String, Map<String, String>> serverConfigByUrl = new ConcurrentHashMap<K, V>();
    private static final CacheAdapter<String, Map<String, String>> serverConfigCache = new CacheAdapter<String, Map<String, String>>(){

        public Map<String, String> get(String key) {
            return serverConfigByUrl.get((Object)key);
        }

        public void put(String key, Map<String, String> value) {
            serverConfigByUrl.putIfAbsent((String)key, value);
        }

        public void invalidate(String key) {
            serverConfigByUrl.remove((Object)key);
        }

        public void invalidateAll(java.util.Set<String> keys) {
            java.util.Iterator<String> i$ = keys.iterator();
            while (i$.hasNext()) {
                String key = i$.next();
                serverConfigByUrl.remove((Object)key);
            }
        }

        public void invalidateAll() {
            serverConfigByUrl.clear();
        }
    };

    @Override
    public CacheAdapter<String, Map<String, String>> getInstance(Connection forConn, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties) throws SQLException {
        return serverConfigCache;
    }
}

