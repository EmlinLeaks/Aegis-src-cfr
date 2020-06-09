/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface Extension {
    public void init(Connection var1, Properties var2) throws SQLException;

    public void destroy();
}

