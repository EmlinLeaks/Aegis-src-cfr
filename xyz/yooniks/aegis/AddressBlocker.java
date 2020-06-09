/*
 * Decompiled with CFR <Could not determine version>.
 */
package xyz.yooniks.aegis;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AddressBlocker {
    private final Set<String> blockedAddresses = Collections.synchronizedSet(new HashSet<E>());

    public boolean isBlocked(InetAddress address) {
        return this.blockedAddresses.contains((Object)address.getHostAddress());
    }

    public void block(InetAddress address) {
        String name = address.getHostAddress();
        if (name.equals((Object)"localhost")) return;
        if (name.equalsIgnoreCase((String)"127.0.0.1")) {
            return;
        }
        this.blockedAddresses.add((String)name);
    }

    public void allow(InetAddress address) {
        this.blockedAddresses.remove((Object)address.getHostAddress());
    }
}

