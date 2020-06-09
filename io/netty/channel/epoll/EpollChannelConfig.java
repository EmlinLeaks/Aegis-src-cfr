/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.Limits;
import java.util.Map;

public class EpollChannelConfig
extends DefaultChannelConfig {
    private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

    EpollChannelConfig(AbstractEpollChannel channel) {
        super((Channel)channel);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.EPOLL_MODE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option != EpollChannelOption.EPOLL_MODE) return (T)super.getOption(option);
        return (T)((Object)this.getEpollMode());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != EpollChannelOption.EPOLL_MODE) return super.setOption(option, value);
        this.setEpollMode((EpollMode)((EpollMode)((Object)value)));
        return true;
    }

    @Override
    public EpollChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
            throw new IllegalArgumentException((String)("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class));
        }
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Deprecated
    @Override
    public EpollChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    public EpollMode getEpollMode() {
        EpollMode epollMode;
        if (((AbstractEpollChannel)this.channel).isFlagSet((int)Native.EPOLLET)) {
            epollMode = EpollMode.EDGE_TRIGGERED;
            return epollMode;
        }
        epollMode = EpollMode.LEVEL_TRIGGERED;
        return epollMode;
    }

    public EpollChannelConfig setEpollMode(EpollMode mode) {
        if (mode == null) {
            throw new NullPointerException((String)"mode");
        }
        switch (1.$SwitchMap$io$netty$channel$epoll$EpollMode[mode.ordinal()]) {
            case 1: {
                this.checkChannelNotRegistered();
                ((AbstractEpollChannel)this.channel).setFlag((int)Native.EPOLLET);
                return this;
            }
            case 2: {
                this.checkChannelNotRegistered();
                ((AbstractEpollChannel)this.channel).clearFlag((int)Native.EPOLLET);
                return this;
            }
        }
        throw new Error();
    }

    private void checkChannelNotRegistered() {
        if (!this.channel.isRegistered()) return;
        throw new IllegalStateException((String)"EpollMode can only be changed before channel is registered");
    }

    @Override
    protected final void autoReadCleared() {
        ((AbstractEpollChannel)this.channel).clearEpollIn();
    }

    final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
        this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
    }

    final long getMaxBytesPerGatheringWrite() {
        return this.maxBytesPerGatheringWrite;
    }
}

