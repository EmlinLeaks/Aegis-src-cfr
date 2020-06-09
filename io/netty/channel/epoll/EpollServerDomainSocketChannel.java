/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollServerChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollServerChannelConfig;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class EpollServerDomainSocketChannel
extends AbstractEpollServerChannel
implements ServerDomainSocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollServerDomainSocketChannel.class);
    private final EpollServerChannelConfig config = new EpollServerChannelConfig((AbstractEpollChannel)this);
    private volatile DomainSocketAddress local;

    public EpollServerDomainSocketChannel() {
        super((LinuxSocket)LinuxSocket.newSocketDomain(), (boolean)false);
    }

    public EpollServerDomainSocketChannel(int fd) {
        super((int)fd);
    }

    EpollServerDomainSocketChannel(LinuxSocket fd) {
        super((LinuxSocket)fd);
    }

    EpollServerDomainSocketChannel(LinuxSocket fd, boolean active) {
        super((LinuxSocket)fd, (boolean)active);
    }

    @Override
    protected Channel newChildChannel(int fd, byte[] addr, int offset, int len) throws Exception {
        return new EpollDomainSocketChannel((Channel)this, (FileDescriptor)new Socket((int)fd));
    }

    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind((SocketAddress)localAddress);
        this.socket.listen((int)this.config.getBacklog());
        this.local = (DomainSocketAddress)localAddress;
        this.active = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
            return;
        }
        finally {
            File socketFile;
            boolean success;
            DomainSocketAddress local = this.local;
            if (local != null && !(success = (socketFile = new File((String)local.path())).delete()) && logger.isDebugEnabled()) {
                logger.debug((String)"Failed to delete a domain socket file: {}", (Object)local.path());
            }
        }
    }

    @Override
    public EpollServerChannelConfig config() {
        return this.config;
    }

    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }

    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }
}

