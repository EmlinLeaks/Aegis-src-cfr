/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class SuspendableXAConnection
extends MysqlPooledConnection
implements XAConnection,
XAResource {
    private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
    private static final Map<Xid, XAConnection> XIDS_TO_PHYSICAL_CONNECTIONS;
    private Xid currentXid;
    private XAConnection currentXAConnection;
    private XAResource currentXAResource;
    private Connection underlyingConnection;

    protected static SuspendableXAConnection getInstance(Connection mysqlConnection) throws SQLException {
        if (Util.isJdbc4()) return (SuspendableXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, (Object[])new Object[]{mysqlConnection}, (ExceptionInterceptor)mysqlConnection.getExceptionInterceptor());
        return new SuspendableXAConnection((Connection)mysqlConnection);
    }

    public SuspendableXAConnection(Connection connection) {
        super((Connection)connection);
        this.underlyingConnection = connection;
    }

    private static synchronized XAConnection findConnectionForXid(Connection connectionToWrap, Xid xid) throws SQLException {
        XAConnection conn = XIDS_TO_PHYSICAL_CONNECTIONS.get((Object)xid);
        if (conn != null) return conn;
        conn = new MysqlXAConnection((Connection)connectionToWrap, (boolean)connectionToWrap.getLogXaCommands());
        XIDS_TO_PHYSICAL_CONNECTIONS.put((Xid)xid, (XAConnection)conn);
        return conn;
    }

    private static synchronized void removeXAConnectionMapping(Xid xid) {
        XIDS_TO_PHYSICAL_CONNECTIONS.remove((Object)xid);
    }

    private synchronized void switchToXid(Xid xid) throws XAException {
        if (xid == null) {
            throw new XAException();
        }
        try {
            XAConnection toSwitchTo;
            if (xid.equals((Object)this.currentXid)) return;
            this.currentXAConnection = toSwitchTo = SuspendableXAConnection.findConnectionForXid((Connection)this.underlyingConnection, (Xid)xid);
            this.currentXid = xid;
            this.currentXAResource = toSwitchTo.getXAResource();
            return;
        }
        catch (SQLException sqlEx) {
            throw new XAException();
        }
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return this;
    }

    @Override
    public void commit(Xid xid, boolean arg1) throws XAException {
        this.switchToXid((Xid)xid);
        this.currentXAResource.commit((Xid)xid, (boolean)arg1);
        SuspendableXAConnection.removeXAConnectionMapping((Xid)xid);
    }

    @Override
    public void end(Xid xid, int arg1) throws XAException {
        this.switchToXid((Xid)xid);
        this.currentXAResource.end((Xid)xid, (int)arg1);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        this.switchToXid((Xid)xid);
        this.currentXAResource.forget((Xid)xid);
        SuspendableXAConnection.removeXAConnectionMapping((Xid)xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xaRes) throws XAException {
        if (xaRes != this) return false;
        return true;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        this.switchToXid((Xid)xid);
        return this.currentXAResource.prepare((Xid)xid);
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return MysqlXAConnection.recover((java.sql.Connection)this.underlyingConnection, (int)flag);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        this.switchToXid((Xid)xid);
        this.currentXAResource.rollback((Xid)xid);
        SuspendableXAConnection.removeXAConnectionMapping((Xid)xid);
    }

    @Override
    public boolean setTransactionTimeout(int arg0) throws XAException {
        return false;
    }

    @Override
    public void start(Xid xid, int arg1) throws XAException {
        this.switchToXid((Xid)xid);
        if (arg1 != 2097152) {
            this.currentXAResource.start((Xid)xid, (int)arg1);
            return;
        }
        this.currentXAResource.start((Xid)xid, (int)134217728);
    }

    @Override
    public synchronized java.sql.Connection getConnection() throws SQLException {
        if (this.currentXAConnection != null) return this.currentXAConnection.getConnection();
        return this.getConnection((boolean)false, (boolean)true);
    }

    @Override
    public void close() throws SQLException {
        if (this.currentXAConnection == null) {
            super.close();
            return;
        }
        SuspendableXAConnection.removeXAConnectionMapping((Xid)this.currentXid);
        this.currentXAConnection.close();
    }

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName((String)"com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection").getConstructor(Connection.class);
            }
            catch (SecurityException e) {
                throw new RuntimeException((Throwable)e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException((Throwable)e);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException((Throwable)e);
            }
        } else {
            JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
        }
        XIDS_TO_PHYSICAL_CONNECTIONS = new HashMap<Xid, XAConnection>();
    }
}

