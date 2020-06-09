/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionImpl;
import java.sql.SQLException;

public interface SocketMetadata {
    public boolean isLocallyConnected(ConnectionImpl var1) throws SQLException;
}

