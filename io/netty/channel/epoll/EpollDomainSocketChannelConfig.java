/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.unix.UnixChannelOption;
import java.io.IOException;
import java.util.Map;

public final class EpollDomainSocketChannelConfig
extends EpollChannelConfig
implements DomainSocketChannelConfig {
    private volatile DomainSocketReadMode mode = DomainSocketReadMode.BYTES;
    private volatile boolean allowHalfClosure;

    EpollDomainSocketChannelConfig(AbstractEpollChannel channel) {
        super((AbstractEpollChannel)channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), UnixChannelOption.DOMAIN_SOCKET_READ_MODE, ChannelOption.ALLOW_HALF_CLOSURE, ChannelOption.SO_SNDBUF, ChannelOption.SO_RCVBUF);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return (T)((Object)this.getReadMode());
        }
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return (T)Boolean.valueOf((boolean)this.isAllowHalfClosure());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf((int)this.getSendBufferSize());
        }
        if (option != ChannelOption.SO_RCVBUF) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getReceiveBufferSize());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            this.setReadMode((DomainSocketReadMode)((DomainSocketReadMode)((Object)value)));
            return true;
        }
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            this.setAllowHalfClosure((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option != ChannelOption.SO_RCVBUF) return super.setOption(option, value);
        this.setReceiveBufferSize((int)((Integer)value).intValue());
        return true;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode((EpollMode)mode);
        return this;
    }

    @Override
    public EpollDomainSocketChannelConfig setReadMode(DomainSocketReadMode mode) {
        if (mode == null) {
            throw new NullPointerException((String)"mode");
        }
        this.mode = mode;
        return this;
    }

    @Override
    public DomainSocketReadMode getReadMode() {
        return this.mode;
    }

    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    public EpollDomainSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    public int getSendBufferSize() {
        try {
            return ((EpollDomainSocketChannel)this.channel).socket.getSendBufferSize();
        }
        catch (IOException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    public EpollDomainSocketChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            ((EpollDomainSocketChannel)this.channel).socket.setSendBufferSize((int)sendBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    public int getReceiveBufferSize() {
        try {
            return ((EpollDomainSocketChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (IOException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    public EpollDomainSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            ((EpollDomainSocketChannel)this.channel).socket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

