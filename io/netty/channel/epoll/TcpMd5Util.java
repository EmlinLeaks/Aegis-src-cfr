/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class TcpMd5Util {
    static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel channel, Collection<InetAddress> current, Map<InetAddress, byte[]> newKeys) throws IOException {
        ObjectUtil.checkNotNull(channel, (String)"channel");
        ObjectUtil.checkNotNull(current, (String)"current");
        ObjectUtil.checkNotNull(newKeys, (String)"newKeys");
        for (Map.Entry<InetAddress, byte[]> e : newKeys.entrySet()) {
            byte[] key = e.getValue();
            if (e.getKey() == null) {
                throw new IllegalArgumentException((String)("newKeys contains an entry with null address: " + newKeys));
            }
            if (key == null) {
                throw new NullPointerException((String)("newKeys[" + e.getKey() + ']'));
            }
            if (key.length == 0) {
                throw new IllegalArgumentException((String)("newKeys[" + e.getKey() + "] has an empty key."));
            }
            if (key.length <= Native.TCP_MD5SIG_MAXKEYLEN) continue;
            throw new IllegalArgumentException((String)("newKeys[" + e.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')'));
        }
        for (InetAddress addr2 : current) {
            if (newKeys.containsKey((Object)addr2)) continue;
            channel.socket.setTcpMd5Sig((InetAddress)addr2, null);
        }
        if (newKeys.isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList<InetAddress> addresses = new ArrayList<InetAddress>((int)newKeys.size());
        Iterator<Map.Entry<InetAddress, byte[]>> addr2 = newKeys.entrySet().iterator();
        while (addr2.hasNext()) {
            Map.Entry<InetAddress, byte[]> e = addr2.next();
            channel.socket.setTcpMd5Sig((InetAddress)e.getKey(), (byte[])e.getValue());
            addresses.add((InetAddress)e.getKey());
        }
        return addresses;
    }

    private TcpMd5Util() {
    }
}

