/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ReplicationConnectionGroup;
import com.mysql.jdbc.jmx.ReplicationGroupManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReplicationConnectionGroupManager {
    private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap<K, V>();
    private static ReplicationGroupManager mbean = new ReplicationGroupManager();
    private static boolean hasRegisteredJmx = false;

    public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
        if (GROUP_MAP.containsKey((Object)groupName)) {
            return GROUP_MAP.get((Object)groupName);
        }
        ReplicationConnectionGroup group = new ReplicationConnectionGroup((String)groupName);
        GROUP_MAP.put((String)groupName, (ReplicationConnectionGroup)group);
        return group;
    }

    public static void registerJmx() throws SQLException {
        if (hasRegisteredJmx) {
            return;
        }
        mbean.registerJmx();
        hasRegisteredJmx = true;
    }

    public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
        return GROUP_MAP.get((Object)groupName);
    }

    public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
        if (group != null && !group.equals((Object)"")) {
            HashSet<ReplicationConnectionGroup> s = new HashSet<ReplicationConnectionGroup>();
            ReplicationConnectionGroup o = GROUP_MAP.get((Object)group);
            if (o == null) return s;
            s.add((ReplicationConnectionGroup)o);
            return s;
        }
        HashSet<ReplicationConnectionGroup> s = new HashSet<ReplicationConnectionGroup>();
        s.addAll(GROUP_MAP.values());
        return s;
    }

    public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            cg.addSlaveHost((String)hostPortPair);
        }
    }

    public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
        ReplicationConnectionGroupManager.removeSlaveHost((String)group, (String)hostPortPair, (boolean)true);
    }

    public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            cg.removeSlaveHost((String)hostPortPair, (boolean)closeGently);
        }
    }

    public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            cg.promoteSlaveToMaster((String)hostPortPair);
        }
    }

    public static long getSlavePromotionCount(String group) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        long promoted = 0L;
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            long tmp = cg.getNumberOfSlavePromotions();
            if (tmp <= promoted) continue;
            promoted = tmp;
        }
        return promoted;
    }

    public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
        ReplicationConnectionGroupManager.removeMasterHost((String)group, (String)hostPortPair, (boolean)true);
    }

    public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            cg.removeMasterHost((String)hostPortPair, (boolean)closeGently);
        }
    }

    public static String getRegisteredReplicationConnectionGroups() {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(null);
        StringBuilder sb = new StringBuilder();
        String sep = "";
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            String group = cg.getGroupName();
            sb.append((String)sep);
            sb.append((String)group);
            sep = ",";
        }
        return sb.toString();
    }

    public static int getNumberOfMasterPromotion(String groupFilter) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)groupFilter);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            total = (int)((long)total + cg.getNumberOfSlavePromotions());
        }
        return total;
    }

    public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)groupFilter);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            total += cg.getConnectionCountWithHostAsSlave((String)hostPortPair);
        }
        return total;
    }

    public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)groupFilter);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            total += cg.getConnectionCountWithHostAsMaster((String)hostPortPair);
        }
        return total;
    }

    public static Collection<String> getSlaveHosts(String groupFilter) {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)groupFilter);
        ArrayList<String> hosts = new ArrayList<String>();
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            hosts.addAll(cg.getSlaveHosts());
        }
        return hosts;
    }

    public static Collection<String> getMasterHosts(String groupFilter) {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)groupFilter);
        ArrayList<String> hosts = new ArrayList<String>();
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            hosts.addAll(cg.getMasterHosts());
        }
        return hosts;
    }

    public static long getTotalConnectionCount(String group) {
        long connections = 0L;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            connections += cg.getTotalConnectionCount();
        }
        return connections;
    }

    public static long getActiveConnectionCount(String group) {
        long connections = 0L;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ReplicationConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ReplicationConnectionGroup cg = i$.next();
            connections += cg.getActiveConnectionCount();
        }
        return connections;
    }
}

