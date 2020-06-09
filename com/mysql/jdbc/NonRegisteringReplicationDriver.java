/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.NonRegisteringDriver;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class NonRegisteringReplicationDriver
extends NonRegisteringDriver {
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.connectReplicationConnection((String)url, (Properties)info);
    }
}

