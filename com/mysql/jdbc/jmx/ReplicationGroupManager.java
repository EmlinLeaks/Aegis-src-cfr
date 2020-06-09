/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jmx;

import com.mysql.jdbc.ReplicationConnectionGroup;
import com.mysql.jdbc.ReplicationConnectionGroupManager;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jmx.ReplicationGroupManagerMBean;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class ReplicationGroupManager
implements ReplicationGroupManagerMBean {
    private boolean isJmxRegistered = false;

    public synchronized void registerJmx() throws SQLException {
        if (this.isJmxRegistered) {
            return;
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName((String)"com.mysql.jdbc.jmx:type=ReplicationGroupManager");
            mbs.registerMBean((Object)this, (ObjectName)name);
            this.isJmxRegistered = true;
            return;
        }
        catch (Exception e) {
            throw SQLError.createSQLException((String)"Unable to register replication host management bean with JMX", null, (Throwable)e, null);
        }
    }

    @Override
    public void addSlaveHost(String groupFilter, String host) throws SQLException {
        ReplicationConnectionGroupManager.addSlaveHost((String)groupFilter, (String)host);
    }

    @Override
    public void removeSlaveHost(String groupFilter, String host) throws SQLException {
        ReplicationConnectionGroupManager.removeSlaveHost((String)groupFilter, (String)host);
    }

    @Override
    public void promoteSlaveToMaster(String groupFilter, String host) throws SQLException {
        ReplicationConnectionGroupManager.promoteSlaveToMaster((String)groupFilter, (String)host);
    }

    @Override
    public void removeMasterHost(String groupFilter, String host) throws SQLException {
        ReplicationConnectionGroupManager.removeMasterHost((String)groupFilter, (String)host);
    }

    @Override
    public String getMasterHostsList(String group) {
        StringBuilder sb = new StringBuilder((String)"");
        boolean found = false;
        Iterator<String> i$ = ReplicationConnectionGroupManager.getMasterHosts((String)group).iterator();
        while (i$.hasNext()) {
            String host = i$.next();
            if (found) {
                sb.append((String)",");
            }
            found = true;
            sb.append((String)host);
        }
        return sb.toString();
    }

    @Override
    public String getSlaveHostsList(String group) {
        StringBuilder sb = new StringBuilder((String)"");
        boolean found = false;
        Iterator<String> i$ = ReplicationConnectionGroupManager.getSlaveHosts((String)group).iterator();
        while (i$.hasNext()) {
            String host = i$.next();
            if (found) {
                sb.append((String)",");
            }
            found = true;
            sb.append((String)host);
        }
        return sb.toString();
    }

    @Override
    public String getRegisteredConnectionGroups() {
        StringBuilder sb = new StringBuilder((String)"");
        boolean found = false;
        Iterator<ReplicationConnectionGroup> i$ = ReplicationConnectionGroupManager.getGroupsMatching(null).iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup group = i$.next();
            if (found) {
                sb.append((String)",");
            }
            found = true;
            sb.append((String)group.getGroupName());
        }
        return sb.toString();
    }

    @Override
    public int getActiveMasterHostCount(String group) {
        return ReplicationConnectionGroupManager.getMasterHosts((String)group).size();
    }

    @Override
    public int getActiveSlaveHostCount(String group) {
        return ReplicationConnectionGroupManager.getSlaveHosts((String)group).size();
    }

    @Override
    public int getSlavePromotionCount(String group) {
        return ReplicationConnectionGroupManager.getNumberOfMasterPromotion((String)group);
    }

    @Override
    public long getTotalLogicalConnectionCount(String group) {
        return ReplicationConnectionGroupManager.getTotalConnectionCount((String)group);
    }

    @Override
    public long getActiveLogicalConnectionCount(String group) {
        return ReplicationConnectionGroupManager.getActiveConnectionCount((String)group);
    }
}

