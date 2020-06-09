/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;
import java.util.Map;

public abstract class AbstractBootstrapConfig<B extends AbstractBootstrap<B, C>, C extends Channel> {
    protected final B bootstrap;

    protected AbstractBootstrapConfig(B bootstrap) {
        this.bootstrap = (AbstractBootstrap)ObjectUtil.checkNotNull(bootstrap, (String)"bootstrap");
    }

    public final SocketAddress localAddress() {
        return ((AbstractBootstrap)this.bootstrap).localAddress();
    }

    public final ChannelFactory<? extends C> channelFactory() {
        return ((AbstractBootstrap)this.bootstrap).channelFactory();
    }

    public final ChannelHandler handler() {
        return ((AbstractBootstrap)this.bootstrap).handler();
    }

    public final Map<ChannelOption<?>, Object> options() {
        return ((AbstractBootstrap)this.bootstrap).options();
    }

    public final Map<AttributeKey<?>, Object> attrs() {
        return ((AbstractBootstrap)this.bootstrap).attrs();
    }

    public final EventLoopGroup group() {
        return ((AbstractBootstrap)this.bootstrap).group();
    }

    public String toString() {
        Map<ChannelOption<?>, Object> options;
        ChannelFactory<C> factory;
        Map<AttributeKey<?>, Object> attrs;
        SocketAddress localAddress;
        ChannelHandler handler;
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((char)'(');
        EventLoopGroup group = this.group();
        if (group != null) {
            buf.append((String)"group: ").append((String)StringUtil.simpleClassName((Object)group)).append((String)", ");
        }
        if ((factory = this.channelFactory()) != null) {
            buf.append((String)"channelFactory: ").append(factory).append((String)", ");
        }
        if ((localAddress = this.localAddress()) != null) {
            buf.append((String)"localAddress: ").append((Object)localAddress).append((String)", ");
        }
        if (!(options = this.options()).isEmpty()) {
            buf.append((String)"options: ").append(options).append((String)", ");
        }
        if (!(attrs = this.attrs()).isEmpty()) {
            buf.append((String)"attrs: ").append(attrs).append((String)", ");
        }
        if ((handler = this.handler()) != null) {
            buf.append((String)"handler: ").append((Object)handler).append((String)", ");
        }
        if (buf.charAt((int)(buf.length() - 1)) == '(') {
            buf.append((char)')');
            return buf.toString();
        }
        buf.setCharAt((int)(buf.length() - 2), (char)')');
        buf.setLength((int)(buf.length() - 1));
        return buf.toString();
    }
}

