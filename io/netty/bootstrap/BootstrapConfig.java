/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.resolver.AddressResolverGroup;
import java.net.SocketAddress;

public final class BootstrapConfig
extends AbstractBootstrapConfig<Bootstrap, Channel> {
    BootstrapConfig(Bootstrap bootstrap) {
        super(bootstrap);
    }

    public SocketAddress remoteAddress() {
        return ((Bootstrap)this.bootstrap).remoteAddress();
    }

    public AddressResolverGroup<?> resolver() {
        return ((Bootstrap)this.bootstrap).resolver();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder((String)super.toString());
        buf.setLength((int)(buf.length() - 1));
        buf.append((String)", resolver: ").append(this.resolver());
        SocketAddress remoteAddress = this.remoteAddress();
        if (remoteAddress == null) return buf.append((char)')').toString();
        buf.append((String)", remoteAddress: ").append((Object)remoteAddress);
        return buf.append((char)')').toString();
    }
}

