/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.sql.SQLException;

public class ProfilerEventHandlerFactory {
    private Connection ownerConnection = null;
    protected Log log = null;

    public static synchronized ProfilerEventHandler getInstance(MySQLConnection conn) throws SQLException {
        ProfilerEventHandler handler = conn.getProfilerEventHandlerInstance();
        if (handler != null) return handler;
        handler = (ProfilerEventHandler)Util.getInstance((String)conn.getProfilerEventHandler(), new Class[0], (Object[])new Object[0], (ExceptionInterceptor)conn.getExceptionInterceptor());
        conn.initializeExtension((Extension)handler);
        conn.setProfilerEventHandlerInstance((ProfilerEventHandler)handler);
        return handler;
    }

    public static synchronized void removeInstance(MySQLConnection conn) {
        ProfilerEventHandler handler = conn.getProfilerEventHandlerInstance();
        if (handler == null) return;
        handler.destroy();
    }

    private ProfilerEventHandlerFactory(Connection conn) {
        this.ownerConnection = conn;
        try {
            this.log = this.ownerConnection.getLog();
            return;
        }
        catch (SQLException sqlEx) {
            throw new RuntimeException((String)"Unable to get logger from connection");
        }
    }
}

