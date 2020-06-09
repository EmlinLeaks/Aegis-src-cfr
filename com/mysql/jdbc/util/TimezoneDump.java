/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import com.mysql.jdbc.TimeUtil;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TimezoneDump {
    private static final String DEFAULT_URL = "jdbc:mysql:///test";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        String jdbcUrl = DEFAULT_URL;
        if (args.length == 1 && args[0] != null) {
            jdbcUrl = args[0];
        }
        Class.forName((String)"com.mysql.jdbc.Driver").newInstance();
        ResultSet rs = null;
        try {
            rs = DriverManager.getConnection((String)jdbcUrl).createStatement().executeQuery((String)"SHOW VARIABLES LIKE 'timezone'");
            while (rs.next()) {
                String timezoneFromServer = rs.getString((int)2);
                System.out.println((String)("MySQL timezone name: " + timezoneFromServer));
                String canonicalTimezone = TimeUtil.getCanonicalTimezone((String)timezoneFromServer, null);
                System.out.println((String)("Java timezone name: " + canonicalTimezone));
            }
            Object var6_5 = null;
            if (rs == null) return;
            rs.close();
            return;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            if (rs == null) throw throwable;
            rs.close();
            throw throwable;
        }
    }
}

