/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.EpollTcpInfo;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.TcpMd5Util;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class EpollSocketChannel
extends AbstractEpollStreamChannel
implements SocketChannel {
    private final EpollSocketChannelConfig config = new EpollSocketChannelConfig((EpollSocketChannel)this);
    private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

    public EpollSocketChannel() {
        super((LinuxSocket)LinuxSocket.newSocketStream(), (boolean)false);
    }

    public EpollSocketChannel(int fd) {
        super((int)fd);
    }

    EpollSocketChannel(LinuxSocket fd, boolean active) {
        super((LinuxSocket)fd, (boolean)active);
    }

    EpollSocketChannel(Channel parent, LinuxSocket fd, InetSocketAddress remoteAddress) {
        super((Channel)parent, (LinuxSocket)fd, (SocketAddress)remoteAddress);
        if (!(parent instanceof EpollServerSocketChannel)) return;
        this.tcpMd5SigAddresses = ((EpollServerSocketChannel)parent).tcpMd5SigAddresses();
    }

    public EpollTcpInfo tcpInfo() {
        return this.tcpInfo((EpollTcpInfo)new EpollTcpInfo());
    }

    public EpollTcpInfo tcpInfo(EpollTcpInfo info) {
        try {
            this.socket.getTcpInfo((EpollTcpInfo)info);
            return info;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public EpollSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollSocketChannelUnsafe((EpollSocketChannel)this, null);
    }

    void setTcpMd5Sig(Map<InetAddress, byte[]> keys) throws IOException {
        this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs((AbstractEpollChannel)this, this.tcpMd5SigAddresses, keys);
    }
}

