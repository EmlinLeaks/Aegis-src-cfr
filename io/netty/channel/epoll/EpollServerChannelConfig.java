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
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.util.Map;

public class EpollServerChannelConfig
extends EpollChannelConfig
implements ServerSocketChannelConfig {
    private volatile int backlog = NetUtil.SOMAXCONN;
    private volatile int pendingFastOpenRequestsThreshold;

    EpollServerChannelConfig(AbstractEpollChannel channel) {
        super((AbstractEpollChannel)channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, EpollChannelOption.TCP_FASTOPEN);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf((int)this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf((boolean)this.isReuseAddress());
        }
        if (option == ChannelOption.SO_BACKLOG) {
            return (T)Integer.valueOf((int)this.getBacklog());
        }
        if (option != EpollChannelOption.TCP_FASTOPEN) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getTcpFastopen());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_BACKLOG) {
            this.setBacklog((int)((Integer)value).intValue());
            return true;
        }
        if (option != EpollChannelOption.TCP_FASTOPEN) return super.setOption(option, value);
        this.setTcpFastopen((int)((Integer)value).intValue());
        return true;
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return ((AbstractEpollChannel)this.channel).socket.isReuseAddress();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollServerChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            ((AbstractEpollChannel)this.channel).socket.setReuseAddress((boolean)reuseAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return ((AbstractEpollChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollServerChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            ((AbstractEpollChannel)this.channel).socket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getBacklog() {
        return this.backlog;
    }

    @Override
    public EpollServerChannelConfig setBacklog(int backlog) {
        ObjectUtil.checkPositiveOrZero((int)backlog, (String)"backlog");
        this.backlog = backlog;
        return this;
    }

    public int getTcpFastopen() {
        return this.pendingFastOpenRequestsThreshold;
    }

    public EpollServerChannelConfig setTcpFastopen(int pendingFastOpenRequestsThreshold) {
        ObjectUtil.checkPositiveOrZero((int)this.pendingFastOpenRequestsThreshold, (String)"pendingFastOpenRequestsThreshold");
        this.pendingFastOpenRequestsThreshold = pendingFastOpenRequestsThreshold;
        return this;
    }

    @Override
    public EpollServerChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollServerChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollServerChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollServerChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    @Override
    public EpollServerChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode((EpollMode)mode);
        return this;
    }
}

