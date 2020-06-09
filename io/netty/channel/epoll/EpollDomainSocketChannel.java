/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollDomainSocketChannelConfig;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import java.net.SocketAddress;

public final class EpollDomainSocketChannel
extends AbstractEpollStreamChannel
implements DomainSocketChannel {
    private final EpollDomainSocketChannelConfig config = new EpollDomainSocketChannelConfig((AbstractEpollChannel)this);
    private volatile DomainSocketAddress local;
    private volatile DomainSocketAddress remote;

    public EpollDomainSocketChannel() {
        super((LinuxSocket)LinuxSocket.newSocketDomain(), (boolean)false);
    }

    EpollDomainSocketChannel(Channel parent, FileDescriptor fd) {
        super((Channel)parent, (LinuxSocket)new LinuxSocket((int)fd.intValue()));
    }

    public EpollDomainSocketChannel(int fd) {
        super((int)fd);
    }

    public EpollDomainSocketChannel(Channel parent, LinuxSocket fd) {
        super((Channel)parent, (LinuxSocket)fd);
    }

    public EpollDomainSocketChannel(int fd, boolean active) {
        super((LinuxSocket)new LinuxSocket((int)fd), (boolean)active);
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDomainUnsafe((EpollDomainSocketChannel)this, null);
    }

    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected DomainSocketAddress remoteAddress0() {
        return this.remote;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind((SocketAddress)localAddress);
        this.local = (DomainSocketAddress)localAddress;
    }

    @Override
    public EpollDomainSocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (!super.doConnect((SocketAddress)remoteAddress, (SocketAddress)localAddress)) return false;
        this.local = (DomainSocketAddress)localAddress;
        this.remote = (DomainSocketAddress)remoteAddress;
        return true;
    }

    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }

    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }

    @Override
    protected int doWriteSingle(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (!(msg instanceof FileDescriptor)) return super.doWriteSingle((ChannelOutboundBuffer)in);
        if (this.socket.sendFd((int)((FileDescriptor)msg).intValue()) <= 0) return super.doWriteSingle((ChannelOutboundBuffer)in);
        in.remove();
        return 1;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (!(msg instanceof FileDescriptor)) return super.filterOutboundMessage((Object)msg);
        return msg;
    }

    public PeerCredentials peerCredentials() throws IOException {
        return this.socket.getPeerCredentials();
    }
}

