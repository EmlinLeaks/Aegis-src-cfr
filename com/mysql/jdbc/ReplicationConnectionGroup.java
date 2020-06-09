/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ReplicationConnection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReplicationConnectionGroup {
    private String groupName;
    private long connections = 0L;
    private long slavesAdded = 0L;
    private long slavesRemoved = 0L;
    private long slavesPromoted = 0L;
    private long activeConnections = 0L;
    private HashMap<Long, ReplicationConnection> replicationConnections = new HashMap<K, V>();
    private Set<String> slaveHostList = new CopyOnWriteArraySet<String>();
    private boolean isInitialized = false;
    private Set<String> masterHostList = new CopyOnWriteArraySet<String>();

    ReplicationConnectionGroup(String groupName) {
        this.groupName = groupName;
    }

    public long getConnectionCount() {
        return this.connections;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long registerReplicationConnection(ReplicationConnection conn, List<String> localMasterList, List<String> localSlaveList) {
        ReplicationConnectionGroup replicationConnectionGroup = this;
        // MONITORENTER : replicationConnectionGroup
        if (!this.isInitialized) {
            if (localMasterList != null) {
                this.masterHostList.addAll(localMasterList);
            }
            if (localSlaveList != null) {
                this.slaveHostList.addAll(localSlaveList);
            }
            this.isInitialized = true;
        }
        long currentConnectionId = ++this.connections;
        this.replicationConnections.put((Long)Long.valueOf((long)currentConnectionId), (ReplicationConnection)conn);
        // MONITOREXIT : replicationConnectionGroup
        ++this.activeConnections;
        return currentConnectionId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Collection<String> getMasterHosts() {
        return this.masterHostList;
    }

    public Collection<String> getSlaveHosts() {
        return this.slaveHostList;
    }

    public void addSlaveHost(String hostPortPair) throws SQLException {
        if (!this.slaveHostList.add((String)hostPortPair)) return;
        ++this.slavesAdded;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.addSlaveHost((String)hostPortPair);
        }
    }

    public void handleCloseConnection(ReplicationConnection conn) {
        this.replicationConnections.remove((Object)Long.valueOf((long)conn.getConnectionGroupId()));
        --this.activeConnections;
    }

    public void removeSlaveHost(String hostPortPair, boolean closeGently) throws SQLException {
        if (!this.slaveHostList.remove((Object)hostPortPair)) return;
        ++this.slavesRemoved;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.removeSlave((String)hostPortPair, (boolean)closeGently);
        }
    }

    public void promoteSlaveToMaster(String hostPortPair) throws SQLException {
        if (!(this.slaveHostList.remove((Object)hostPortPair) | this.masterHostList.add((String)hostPortPair))) return;
        ++this.slavesPromoted;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.promoteSlaveToMaster((String)hostPortPair);
        }
    }

    public void removeMasterHost(String hostPortPair) throws SQLException {
        this.removeMasterHost((String)hostPortPair, (boolean)true);
    }

    public void removeMasterHost(String hostPortPair, boolean closeGently) throws SQLException {
        if (!this.masterHostList.remove((Object)hostPortPair)) return;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            c.removeMasterHost((String)hostPortPair, (boolean)closeGently);
        }
    }

    public int getConnectionCountWithHostAsSlave(String hostPortPair) {
        int matched = 0;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            if (!c.isHostSlave((String)hostPortPair)) continue;
            ++matched;
        }
        return matched;
    }

    public int getConnectionCountWithHostAsMaster(String hostPortPair) {
        int matched = 0;
        Iterator<ReplicationConnection> i$ = this.replicationConnections.values().iterator();
        while (i$.hasNext()) {
            ReplicationConnection c = i$.next();
            if (!c.isHostMaster((String)hostPortPair)) continue;
            ++matched;
        }
        return matched;
    }

    public long getNumberOfSlavesAdded() {
        return this.slavesAdded;
    }

    public long getNumberOfSlavesRemoved() {
        return this.slavesRemoved;
    }

    public long getNumberOfSlavePromotions() {
        return this.slavesPromoted;
    }

    public long getTotalConnectionCount() {
        return this.connections;
    }

    public long getActiveConnectionCount() {
        return this.activeConnections;
    }

    public String toString() {
        return "ReplicationConnectionGroup[groupName=" + this.groupName + ",masterHostList=" + this.masterHostList + ",slaveHostList=" + this.slaveHostList + "]";
    }
}

