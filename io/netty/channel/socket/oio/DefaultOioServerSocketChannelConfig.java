/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannelConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

@Deprecated
public class DefaultOioServerSocketChannelConfig
extends DefaultServerSocketChannelConfig
implements OioServerSocketChannelConfig {
    @Deprecated
    public DefaultOioServerSocketChannelConfig(ServerSocketChannel channel, ServerSocket javaSocket) {
        super((ServerSocketChannel)channel, (ServerSocket)javaSocket);
        this.setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.getAllocator()));
    }

    DefaultOioServerSocketChannelConfig(OioServerSocketChannel channel, ServerSocket javaSocket) {
        super((ServerSocketChannel)channel, (ServerSocket)javaSocket);
        this.setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.getAllocator()));
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_TIMEOUT);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option != ChannelOption.SO_TIMEOUT) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getSoTimeout());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != ChannelOption.SO_TIMEOUT) return super.setOption(option, value);
        this.setSoTimeout((int)((Integer)value).intValue());
        return true;
    }

    @Override
    public OioServerSocketChannelConfig setSoTimeout(int timeout) {
        try {
            this.javaSocket.setSoTimeout((int)timeout);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSoTimeout() {
        try {
            return this.javaSocket.getSoTimeout();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public OioServerSocketChannelConfig setBacklog(int backlog) {
        super.setBacklog((int)backlog);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress((boolean)reuseAddress);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize((int)receiveBufferSize);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        super.setPerformancePreferences((int)connectionTime, (int)latency, (int)bandwidth);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public OioServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        if (!(this.channel instanceof OioServerSocketChannel)) return;
        ((OioServerSocketChannel)this.channel).clearReadPending0();
    }

    @Override
    public OioServerSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }
}

