/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.local.LocalAddress;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

final class LocalChannelRegistry {
    private static final ConcurrentMap<LocalAddress, Channel> boundChannels = PlatformDependent.newConcurrentHashMap();

    static LocalAddress register(Channel channel, LocalAddress oldLocalAddress, SocketAddress localAddress) {
        Channel boundChannel;
        if (oldLocalAddress != null) {
            throw new ChannelException((String)"already bound");
        }
        if (!(localAddress instanceof LocalAddress)) {
            throw new ChannelException((String)("unsupported address type: " + StringUtil.simpleClassName((Object)localAddress)));
        }
        LocalAddress addr = (LocalAddress)localAddress;
        if (LocalAddress.ANY.equals((Object)addr)) {
            addr = new LocalAddress((Channel)channel);
        }
        if ((boundChannel = boundChannels.putIfAbsent((LocalAddress)addr, (Channel)channel)) == null) return addr;
        throw new ChannelException((String)("address already in use by: " + boundChannel));
    }

    static Channel get(SocketAddress localAddress) {
        return (Channel)boundChannels.get((Object)localAddress);
    }

    static void unregister(LocalAddress localAddress) {
        boundChannels.remove((Object)localAddress);
    }

    private LocalChannelRegistry() {
    }
}

