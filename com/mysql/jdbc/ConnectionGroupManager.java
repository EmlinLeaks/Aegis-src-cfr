/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionGroup;
import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConnectionGroupManager {
    private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap<K, V>();
    private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();
    private static boolean hasRegisteredJmx = false;

    public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName) {
        if (GROUP_MAP.containsKey((Object)groupName)) {
            return GROUP_MAP.get((Object)groupName);
        }
        ConnectionGroup group = new ConnectionGroup((String)groupName);
        GROUP_MAP.put((String)groupName, (ConnectionGroup)group);
        return group;
    }

    public static void registerJmx() throws SQLException {
        if (hasRegisteredJmx) {
            return;
        }
        mbean.registerJmx();
        hasRegisteredJmx = true;
    }

    public static ConnectionGroup getConnectionGroup(String groupName) {
        return GROUP_MAP.get((Object)groupName);
    }

    private static Collection<ConnectionGroup> getGroupsMatching(String group) {
        if (group != null && !group.equals((Object)"")) {
            HashSet<ConnectionGroup> s = new HashSet<ConnectionGroup>();
            ConnectionGroup o = GROUP_MAP.get((Object)group);
            if (o == null) return s;
            s.add((ConnectionGroup)o);
            return s;
        }
        HashSet<ConnectionGroup> s = new HashSet<ConnectionGroup>();
        s.addAll(GROUP_MAP.values());
        return s;
    }

    public static void addHost(String group, String hostPortPair, boolean forExisting) {
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            cg.addHost((String)hostPortPair, (boolean)forExisting);
        }
    }

    public static int getActiveHostCount(String group) {
        HashSet<String> active = new HashSet<String>();
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            active.addAll(cg.getInitialHosts());
        }
        return active.size();
    }

    public static long getActiveLogicalConnectionCount(String group) {
        int count = 0;
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            count = (int)((long)count + cg.getActiveLogicalConnectionCount());
        }
        return (long)count;
    }

    public static long getActivePhysicalConnectionCount(String group) {
        int count = 0;
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            count = (int)((long)count + cg.getActivePhysicalConnectionCount());
        }
        return (long)count;
    }

    public static int getTotalHostCount(String group) {
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        HashSet<String> hosts = new HashSet<String>();
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            hosts.addAll(cg.getInitialHosts());
            hosts.addAll(cg.getClosedHosts());
        }
        return hosts.size();
    }

    public static long getTotalLogicalConnectionCount(String group) {
        long count = 0L;
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            count += cg.getTotalLogicalConnectionCount();
        }
        return count;
    }

    public static long getTotalPhysicalConnectionCount(String group) {
        long count = 0L;
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            count += cg.getTotalPhysicalConnectionCount();
        }
        return count;
    }

    public static long getTotalTransactionCount(String group) {
        long count = 0L;
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            count += cg.getTotalTransactionCount();
        }
        return count;
    }

    public static void removeHost(String group, String hostPortPair) throws SQLException {
        ConnectionGroupManager.removeHost((String)group, (String)hostPortPair, (boolean)false);
    }

    public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            cg.removeHost((String)host, (boolean)removeExisting);
        }
    }

    public static String getActiveHostLists(String group) {
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching((String)group);
        HashMap<String, Integer> hosts = new HashMap<String, Integer>();
        for (ConnectionGroup cg : s) {
            Collection<String> l = cg.getInitialHosts();
            for (String host : l) {
                Integer o = (Integer)hosts.get((Object)host);
                o = o == null ? Integer.valueOf((int)1) : Integer.valueOf((int)(o.intValue() + 1));
                hosts.put(host, o);
            }
        }
        StringBuilder sb = new StringBuilder();
        String sep = "";
        Iterator<K> i$ = hosts.keySet().iterator();
        while (i$.hasNext()) {
            String host = (String)i$.next();
            sb.append((String)sep);
            sb.append((String)host);
            sb.append((char)'(');
            sb.append(hosts.get((Object)host));
            sb.append((char)')');
            sep = ",";
        }
        return sb.toString();
    }

    public static String getRegisteredConnectionGroups() {
        Collection<ConnectionGroup> s = ConnectionGroupManager.getGroupsMatching(null);
        StringBuilder sb = new StringBuilder();
        String sep = "";
        Iterator<ConnectionGroup> i$ = s.iterator();
        while (i$.hasNext()) {
            ConnectionGroup cg = i$.next();
            String group = cg.getGroupName();
            sb.append((String)sep);
            sb.append((String)group);
            sep = ",";
        }
        return sb.toString();
    }
}

