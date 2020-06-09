/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollServerChannel;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.unix.Socket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class AbstractEpollServerChannel
extends AbstractEpollChannel
implements ServerChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false, (int)16);

    protected AbstractEpollServerChannel(int fd) {
        this((LinuxSocket)new LinuxSocket((int)fd), (boolean)false);
    }

    AbstractEpollServerChannel(LinuxSocket fd) {
        this((LinuxSocket)fd, (boolean)AbstractEpollServerChannel.isSoErrorZero((Socket)fd));
    }

    AbstractEpollServerChannel(LinuxSocket fd, boolean active) {
        super(null, (LinuxSocket)fd, (boolean)active);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollServerSocketUnsafe((AbstractEpollServerChannel)this);
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    abstract Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception;

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }
}

