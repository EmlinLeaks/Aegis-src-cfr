/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel
extends AttributeMap,
ChannelOutboundInvoker,
Comparable<Channel> {
    public ChannelId id();

    public EventLoop eventLoop();

    public Channel parent();

    public ChannelConfig config();

    public boolean isOpen();

    public boolean isRegistered();

    public boolean isActive();

    public ChannelMetadata metadata();

    public SocketAddress localAddress();

    public SocketAddress remoteAddress();

    public ChannelFuture closeFuture();

    public boolean isWritable();

    public long bytesBeforeUnwritable();

    public long bytesBeforeWritable();

    public Unsafe unsafe();

    public ChannelPipeline pipeline();

    public ByteBufAllocator alloc();

    @Override
    public Channel read();

    @Override
    public Channel flush();
}

