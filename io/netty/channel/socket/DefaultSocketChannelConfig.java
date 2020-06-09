/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class DefaultSocketChannelConfig
extends DefaultChannelConfig
implements SocketChannelConfig {
    protected final Socket javaSocket;
    private volatile boolean allowHalfClosure;

    public DefaultSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
        super((Channel)channel);
        if (javaSocket == null) {
            throw new NullPointerException((String)"javaSocket");
        }
        this.javaSocket = javaSocket;
        if (!PlatformDependent.canEnableTcpNoDelayByDefault()) return;
        try {
            this.setTcpNoDelay((boolean)true);
            return;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf((int)this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf((int)this.getSendBufferSize());
        }
        if (option == ChannelOption.TCP_NODELAY) {
            return (T)Boolean.valueOf((boolean)this.isTcpNoDelay());
        }
        if (option == ChannelOption.SO_KEEPALIVE) {
            return (T)Boolean.valueOf((boolean)this.isKeepAlive());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf((boolean)this.isReuseAddress());
        }
        if (option == ChannelOption.SO_LINGER) {
            return (T)Integer.valueOf((int)this.getSoLinger());
        }
        if (option == ChannelOption.IP_TOS) {
            return (T)Integer.valueOf((int)this.getTrafficClass());
        }
        if (option != ChannelOption.ALLOW_HALF_CLOSURE) return (T)super.getOption(option);
        return (T)Boolean.valueOf((boolean)this.isAllowHalfClosure());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.TCP_NODELAY) {
            this.setTcpNoDelay((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_KEEPALIVE) {
            this.setKeepAlive((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_LINGER) {
            this.setSoLinger((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.IP_TOS) {
            this.setTrafficClass((int)((Integer)value).intValue());
            return true;
        }
        if (option != ChannelOption.ALLOW_HALF_CLOSURE) return super.setOption(option, value);
        this.setAllowHalfClosure((boolean)((Boolean)value).booleanValue());
        return true;
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return this.javaSocket.getReceiveBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSendBufferSize() {
        try {
            return this.javaSocket.getSendBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSoLinger() {
        try {
            return this.javaSocket.getSoLinger();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getTrafficClass() {
        try {
            return this.javaSocket.getTrafficClass();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isKeepAlive() {
        try {
            return this.javaSocket.getKeepAlive();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return this.javaSocket.getReuseAddress();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isTcpNoDelay() {
        try {
            return this.javaSocket.getTcpNoDelay();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setKeepAlive(boolean keepAlive) {
        try {
            this.javaSocket.setKeepAlive((boolean)keepAlive);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        this.javaSocket.setPerformancePreferences((int)connectionTime, (int)latency, (int)bandwidth);
        return this;
    }

    @Override
    public SocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.javaSocket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            this.javaSocket.setReuseAddress((boolean)reuseAddress);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            this.javaSocket.setSendBufferSize((int)sendBufferSize);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setSoLinger(int soLinger) {
        try {
            if (soLinger < 0) {
                this.javaSocket.setSoLinger((boolean)false, (int)0);
                return this;
            }
            this.javaSocket.setSoLinger((boolean)true, (int)soLinger);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
        try {
            this.javaSocket.setTcpNoDelay((boolean)tcpNoDelay);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public SocketChannelConfig setTrafficClass(int trafficClass) {
        try {
            this.javaSocket.setTrafficClass((int)trafficClass);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    @Override
    public SocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    @Override
    public SocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public SocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public SocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public SocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public SocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }
}

