/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

import com.mysql.fabric.Server;
import com.mysql.fabric.ServerRole;
import java.util.Iterator;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ServerGroup {
    private String name;
    private Set<Server> servers;

    public ServerGroup(String name, Set<Server> servers) {
        this.name = name;
        this.servers = servers;
    }

    public String getName() {
        return this.name;
    }

    public Set<Server> getServers() {
        return this.servers;
    }

    public Server getMaster() {
        Server s;
        Iterator<Server> i$ = this.servers.iterator();
        do {
            if (!i$.hasNext()) return null;
        } while ((s = i$.next()).getRole() != ServerRole.PRIMARY);
        return s;
    }

    public Server getServer(String hostPortString) {
        Server s;
        Iterator<Server> i$ = this.servers.iterator();
        do {
            if (!i$.hasNext()) return null;
        } while (!(s = i$.next()).getHostPortString().equals((Object)hostPortString));
        return s;
    }

    public String toString() {
        return String.format((String)"Group[name=%s, servers=%s]", (Object[])new Object[]{this.name, this.servers});
    }
}

