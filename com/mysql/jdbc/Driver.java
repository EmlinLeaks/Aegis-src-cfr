/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.NonRegisteringDriver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver
extends NonRegisteringDriver
implements java.sql.Driver {
    static {
        try {
            DriverManager.registerDriver((java.sql.Driver)new Driver());
            return;
        }
        catch (SQLException E) {
            throw new RuntimeException((String)"Can't register driver!");
        }
    }
}

