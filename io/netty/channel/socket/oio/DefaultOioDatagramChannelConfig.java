/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.oio.OioDatagramChannelConfig;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

final class DefaultOioDatagramChannelConfig
extends DefaultDatagramChannelConfig
implements OioDatagramChannelConfig {
    DefaultOioDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
        super((DatagramChannel)channel, (DatagramSocket)javaSocket);
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
    public OioDatagramChannelConfig setSoTimeout(int timeout) {
        try {
            this.javaSocket().setSoTimeout((int)timeout);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSoTimeout() {
        try {
            return this.javaSocket().getSoTimeout();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public OioDatagramChannelConfig setBroadcast(boolean broadcast) {
        super.setBroadcast((boolean)broadcast);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        super.setInterface((InetAddress)interfaceAddress);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        super.setLoopbackModeDisabled((boolean)loopbackModeDisabled);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        super.setNetworkInterface((NetworkInterface)networkInterface);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
        super.setReuseAddress((boolean)reuseAddress);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        super.setReceiveBufferSize((int)receiveBufferSize);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
        super.setSendBufferSize((int)sendBufferSize);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setTimeToLive(int ttl) {
        super.setTimeToLive((int)ttl);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setTrafficClass(int trafficClass) {
        super.setTrafficClass((int)trafficClass);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public OioDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }
}

