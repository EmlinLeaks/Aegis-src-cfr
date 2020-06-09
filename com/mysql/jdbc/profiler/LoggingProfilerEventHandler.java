/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Constants;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.profiler.ProfilerEvent;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import java.sql.SQLException;
import java.util.Properties;

public class LoggingProfilerEventHandler
implements ProfilerEventHandler {
    private Log log;

    @Override
    public void consumeEvent(ProfilerEvent evt) {
        switch (evt.getEventType()) {
            case 0: {
                this.log.logWarn((Object)evt);
                return;
            }
        }
        this.log.logInfo((Object)evt);
    }

    @Override
    public void destroy() {
        this.log = null;
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.log = conn.getLog();
    }

    @Override
    public void processEvent(byte eventType, MySQLConnection conn, Statement stmt, ResultSetInternalMethods resultSet, long eventDuration, Throwable eventCreationPoint, String message) {
        String catalog = "";
        try {
            if (conn != null) {
                catalog = conn.getCatalog();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        this.consumeEvent((ProfilerEvent)new ProfilerEvent((byte)eventType, (String)(conn == null ? "" : conn.getHost()), (String)catalog, (long)(conn == null ? -1L : conn.getId()), (int)(stmt == null ? -1 : stmt.getId()), (int)(resultSet == null ? -1 : resultSet.getId()), (long)eventDuration, (String)(conn == null ? Constants.MILLIS_I18N : conn.getQueryTimingUnits()), (Throwable)eventCreationPoint, (String)message));
    }
}

