/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.NonRegisteringReplicationDriver;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ReplicationDriver
extends NonRegisteringReplicationDriver
implements Driver {
    static {
        try {
            DriverManager.registerDriver((Driver)new NonRegisteringReplicationDriver());
            return;
        }
        catch (SQLException E) {
            throw new RuntimeException((String)"Can't register driver!");
        }
    }
}

