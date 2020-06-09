/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jmx;

import com.mysql.jdbc.ConnectionGroupManager;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManagerMBean;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class LoadBalanceConnectionGroupManager
implements LoadBalanceConnectionGroupManagerMBean {
    private boolean isJmxRegistered = false;

    public synchronized void registerJmx() throws SQLException {
        if (this.isJmxRegistered) {
            return;
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName((String)"com.mysql.jdbc.jmx:type=LoadBalanceConnectionGroupManager");
            mbs.registerMBean((Object)this, (ObjectName)name);
            this.isJmxRegistered = true;
            return;
        }
        catch (Exception e) {
            throw SQLError.createSQLException((String)"Unable to register load-balance management bean with JMX", null, (Throwable)e, null);
        }
    }

    @Override
    public void addHost(String group, String host, boolean forExisting) {
        try {
            ConnectionGroupManager.addHost((String)group, (String)host, (boolean)forExisting);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getActiveHostCount(String group) {
        return ConnectionGroupManager.getActiveHostCount((String)group);
    }

    @Override
    public long getActiveLogicalConnectionCount(String group) {
        return ConnectionGroupManager.getActiveLogicalConnectionCount((String)group);
    }

    @Override
    public long getActivePhysicalConnectionCount(String group) {
        return ConnectionGroupManager.getActivePhysicalConnectionCount((String)group);
    }

    @Override
    public int getTotalHostCount(String group) {
        return ConnectionGroupManager.getTotalHostCount((String)group);
    }

    @Override
    public long getTotalLogicalConnectionCount(String group) {
        return ConnectionGroupManager.getTotalLogicalConnectionCount((String)group);
    }

    @Override
    public long getTotalPhysicalConnectionCount(String group) {
        return ConnectionGroupManager.getTotalPhysicalConnectionCount((String)group);
    }

    @Override
    public long getTotalTransactionCount(String group) {
        return ConnectionGroupManager.getTotalTransactionCount((String)group);
    }

    @Override
    public void removeHost(String group, String host) throws SQLException {
        ConnectionGroupManager.removeHost((String)group, (String)host);
    }

    @Override
    public String getActiveHostsList(String group) {
        return ConnectionGroupManager.getActiveHostLists((String)group);
    }

    @Override
    public String getRegisteredConnectionGroups() {
        return ConnectionGroupManager.getRegisteredConnectionGroups();
    }

    @Override
    public void stopNewConnectionsToHost(String group, String host) throws SQLException {
        ConnectionGroupManager.removeHost((String)group, (String)host);
    }
}

