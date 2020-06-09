/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerChannelConfig;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.ServerSocketChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollServerSocketChannelConfig
extends EpollServerChannelConfig
implements ServerSocketChannelConfig {
    EpollServerSocketChannelConfig(EpollServerSocketChannel channel) {
        super((AbstractEpollChannel)channel);
        this.setReuseAddress((boolean)true);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_DEFER_ACCEPT);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == EpollChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf((boolean)this.isReusePort());
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            return (T)Boolean.valueOf((boolean)this.isFreeBind());
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return (T)Boolean.valueOf((boolean)this.isIpTransparent());
        }
        if (option != EpollChannelOption.TCP_DEFER_ACCEPT) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getTcpDeferAccept());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == EpollChannelOption.SO_REUSEPORT) {
            this.setReusePort((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            this.setFreeBind((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            this.setIpTransparent((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_MD5SIG) {
            Map m = (Map)value;
            this.setTcpMd5Sig((Map<InetAddress, byte[]>)m);
            return true;
        }
        if (option != EpollChannelOption.TCP_DEFER_ACCEPT) return super.setOption(option, value);
        this.setTcpDeferAccept((int)((Integer)value).intValue());
        return true;
    }

    @Override
    public EpollServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress((boolean)reuseAddress);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize((int)receiveBufferSize);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setBacklog(int backlog) {
        super.setBacklog((int)backlog);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    public EpollServerSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
        try {
            ((EpollServerSocketChannel)this.channel).setTcpMd5Sig(keys);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isReusePort() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isReusePort();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollServerSocketChannelConfig setReusePort(boolean reusePort) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setReusePort((boolean)reusePort);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isFreeBind() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isIpFreeBind();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollServerSocketChannelConfig setFreeBind(boolean freeBind) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setIpFreeBind((boolean)freeBind);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isIpTransparent() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isIpTransparent();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollServerSocketChannelConfig setIpTransparent(boolean transparent) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setIpTransparent((boolean)transparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollServerSocketChannelConfig setTcpDeferAccept(int deferAccept) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setTcpDeferAccept((int)deferAccept);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getTcpDeferAccept() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.getTcpDeferAccept();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }
}

