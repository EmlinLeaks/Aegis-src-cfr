/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.sql.StatementEvent;
import javax.sql.StatementEventListener;

public class JDBC4MysqlPooledConnection
extends MysqlPooledConnection {
    private final Map<StatementEventListener, StatementEventListener> statementEventListeners = new HashMap<StatementEventListener, StatementEventListener>();

    public JDBC4MysqlPooledConnection(Connection connection) {
        super((Connection)connection);
    }

    @Override
    public synchronized void close() throws SQLException {
        super.close();
        this.statementEventListeners.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        Map<StatementEventListener, StatementEventListener> map = this.statementEventListeners;
        // MONITORENTER : map
        this.statementEventListeners.put((StatementEventListener)listener, (StatementEventListener)listener);
        // MONITOREXIT : map
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        Map<StatementEventListener, StatementEventListener> map = this.statementEventListeners;
        // MONITORENTER : map
        this.statementEventListeners.remove((Object)listener);
        // MONITOREXIT : map
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void fireStatementEvent(StatementEvent event) throws SQLException {
        Map<StatementEventListener, StatementEventListener> map = this.statementEventListeners;
        // MONITORENTER : map
        Iterator<StatementEventListener> iterator = this.statementEventListeners.keySet().iterator();
        do {
            if (!iterator.hasNext()) {
                // MONITOREXIT : map
                return;
            }
            StatementEventListener listener = iterator.next();
            listener.statementClosed((StatementEvent)event);
        } while (true);
    }
}

