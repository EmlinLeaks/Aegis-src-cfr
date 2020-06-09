/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public final class ServerBootstrapConfig
extends AbstractBootstrapConfig<ServerBootstrap, ServerChannel> {
    ServerBootstrapConfig(ServerBootstrap bootstrap) {
        super(bootstrap);
    }

    public EventLoopGroup childGroup() {
        return ((ServerBootstrap)this.bootstrap).childGroup();
    }

    public ChannelHandler childHandler() {
        return ((ServerBootstrap)this.bootstrap).childHandler();
    }

    public Map<ChannelOption<?>, Object> childOptions() {
        return ((ServerBootstrap)this.bootstrap).childOptions();
    }

    public Map<AttributeKey<?>, Object> childAttrs() {
        return ((ServerBootstrap)this.bootstrap).childAttrs();
    }

    @Override
    public String toString() {
        Map<AttributeKey<?>, Object> childAttrs;
        ChannelHandler childHandler;
        Map<ChannelOption<?>, Object> childOptions;
        StringBuilder buf = new StringBuilder((String)super.toString());
        buf.setLength((int)(buf.length() - 1));
        buf.append((String)", ");
        EventLoopGroup childGroup = this.childGroup();
        if (childGroup != null) {
            buf.append((String)"childGroup: ");
            buf.append((String)StringUtil.simpleClassName((Object)childGroup));
            buf.append((String)", ");
        }
        if (!(childOptions = this.childOptions()).isEmpty()) {
            buf.append((String)"childOptions: ");
            buf.append(childOptions);
            buf.append((String)", ");
        }
        if (!(childAttrs = this.childAttrs()).isEmpty()) {
            buf.append((String)"childAttrs: ");
            buf.append(childAttrs);
            buf.append((String)", ");
        }
        if ((childHandler = this.childHandler()) != null) {
            buf.append((String)"childHandler: ");
            buf.append((Object)childHandler);
            buf.append((String)", ");
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

