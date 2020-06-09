/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public interface ConnectionPropertiesTransform {
    public Properties transformProperties(Properties var1) throws SQLException;
}

