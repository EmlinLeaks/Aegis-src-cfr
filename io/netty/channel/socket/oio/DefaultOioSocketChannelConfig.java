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
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannelConfig;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

@Deprecated
public class DefaultOioSocketChannelConfig
extends DefaultSocketChannelConfig
implements OioSocketChannelConfig {
    @Deprecated
    public DefaultOioSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
        super((SocketChannel)channel, (Socket)javaSocket);
        this.setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.getAllocator()));
    }

    DefaultOioSocketChannelConfig(OioSocketChannel channel, Socket javaSocket) {
        super((SocketChannel)channel, (Socket)javaSocket);
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
    public OioSocketChannelConfig setSoTimeout(int timeout) {
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
    public OioSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
        super.setTcpNoDelay((boolean)tcpNoDelay);
        return this;
    }

    @Override
    public OioSocketChannelConfig setSoLinger(int soLinger) {
        super.setSoLinger((int)soLinger);
        return this;
    }

    @Override
    public OioSocketChannelConfig setSendBufferSize(int sendBufferSize) {
        super.setSendBufferSize((int)sendBufferSize);
        return this;
    }

    @Override
    public OioSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize((int)receiveBufferSize);
        return this;
    }

    @Override
    public OioSocketChannelConfig setKeepAlive(boolean keepAlive) {
        super.setKeepAlive((boolean)keepAlive);
        return this;
    }

    @Override
    public OioSocketChannelConfig setTrafficClass(int trafficClass) {
        super.setTrafficClass((int)trafficClass);
        return this;
    }

    @Override
    public OioSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress((boolean)reuseAddress);
        return this;
    }

    @Override
    public OioSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        super.setPerformancePreferences((int)connectionTime, (int)latency, (int)bandwidth);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        super.setAllowHalfClosure((boolean)allowHalfClosure);
        return this;
    }

    @Override
    public OioSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public OioSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        if (!(this.channel instanceof OioSocketChannel)) return;
        ((OioSocketChannel)this.channel).clearReadPending0();
    }

    @Override
    public OioSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }
}

