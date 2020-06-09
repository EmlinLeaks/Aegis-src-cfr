/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.local;

import io.netty.channel.Channel;
import java.net.SocketAddress;

public final class LocalAddress
extends SocketAddress
implements Comparable<LocalAddress> {
    private static final long serialVersionUID = 4644331421130916435L;
    public static final LocalAddress ANY = new LocalAddress((String)"ANY");
    private final String id;
    private final String strVal;

    LocalAddress(Channel channel) {
        StringBuilder buf = new StringBuilder((int)16);
        buf.append((String)"local:E");
        buf.append((String)Long.toHexString((long)((long)channel.hashCode() & 0xFFFFFFFFL | 0x100000000L)));
        buf.setCharAt((int)7, (char)':');
        this.id = buf.substring((int)6);
        this.strVal = buf.toString();
    }

    public LocalAddress(String id) {
        if (id == null) {
            throw new NullPointerException((String)"id");
        }
        if ((id = id.trim().toLowerCase()).isEmpty()) {
            throw new IllegalArgumentException((String)"empty id");
        }
        this.id = id;
        this.strVal = "local:" + id;
    }

    public String id() {
        return this.id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof LocalAddress) return this.id.equals((Object)((LocalAddress)o).id);
        return false;
    }

    @Override
    public int compareTo(LocalAddress o) {
        return this.id.compareTo((String)o.id);
    }

    public String toString() {
        return this.strVal;
    }
}

