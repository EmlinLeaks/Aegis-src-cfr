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
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollSocketChannelConfig
extends EpollChannelConfig
implements SocketChannelConfig {
    private volatile boolean allowHalfClosure;

    EpollSocketChannelConfig(EpollSocketChannel channel) {
        super((AbstractEpollChannel)channel);
        if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
            this.setTcpNoDelay((boolean)true);
        }
        this.calculateMaxBytesPerGatheringWrite();
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_NOTSENT_LOWAT, EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL, EpollChannelOption.TCP_MD5SIG, EpollChannelOption.TCP_QUICKACK, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_FASTOPEN_CONNECT, EpollChannelOption.SO_BUSY_POLL);
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
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return (T)Boolean.valueOf((boolean)this.isAllowHalfClosure());
        }
        if (option == EpollChannelOption.TCP_CORK) {
            return (T)Boolean.valueOf((boolean)this.isTcpCork());
        }
        if (option == EpollChannelOption.TCP_NOTSENT_LOWAT) {
            return (T)Long.valueOf((long)this.getTcpNotSentLowAt());
        }
        if (option == EpollChannelOption.TCP_KEEPIDLE) {
            return (T)Integer.valueOf((int)this.getTcpKeepIdle());
        }
        if (option == EpollChannelOption.TCP_KEEPINTVL) {
            return (T)Integer.valueOf((int)this.getTcpKeepIntvl());
        }
        if (option == EpollChannelOption.TCP_KEEPCNT) {
            return (T)Integer.valueOf((int)this.getTcpKeepCnt());
        }
        if (option == EpollChannelOption.TCP_USER_TIMEOUT) {
            return (T)Integer.valueOf((int)this.getTcpUserTimeout());
        }
        if (option == EpollChannelOption.TCP_QUICKACK) {
            return (T)Boolean.valueOf((boolean)this.isTcpQuickAck());
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return (T)Boolean.valueOf((boolean)this.isIpTransparent());
        }
        if (option == EpollChannelOption.TCP_FASTOPEN_CONNECT) {
            return (T)Boolean.valueOf((boolean)this.isTcpFastOpenConnect());
        }
        if (option != EpollChannelOption.SO_BUSY_POLL) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getSoBusyPoll());
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
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            this.setAllowHalfClosure((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_CORK) {
            this.setTcpCork((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_NOTSENT_LOWAT) {
            this.setTcpNotSentLowAt((long)((Long)value).longValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_KEEPIDLE) {
            this.setTcpKeepIdle((int)((Integer)value).intValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_KEEPCNT) {
            this.setTcpKeepCnt((int)((Integer)value).intValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_KEEPINTVL) {
            this.setTcpKeepIntvl((int)((Integer)value).intValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_USER_TIMEOUT) {
            this.setTcpUserTimeout((int)((Integer)value).intValue());
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
        if (option == EpollChannelOption.TCP_QUICKACK) {
            this.setTcpQuickAck((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == EpollChannelOption.TCP_FASTOPEN_CONNECT) {
            this.setTcpFastOpenConnect((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option != EpollChannelOption.SO_BUSY_POLL) return super.setOption(option, value);
        this.setSoBusyPoll((int)((Integer)value).intValue());
        return true;
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSendBufferSize() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getSendBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getSoLinger() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getSoLinger();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getTrafficClass() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTrafficClass();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isKeepAlive() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isKeepAlive();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isReuseAddress();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isTcpNoDelay() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isTcpNoDelay();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isTcpCork() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isTcpCork();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getSoBusyPoll() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getSoBusyPoll();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public long getTcpNotSentLowAt() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTcpNotSentLowAt();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getTcpKeepIdle() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTcpKeepIdle();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getTcpKeepIntvl() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTcpKeepIntvl();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getTcpKeepCnt() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTcpKeepCnt();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public int getTcpUserTimeout() {
        try {
            return ((EpollSocketChannel)this.channel).socket.getTcpUserTimeout();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setKeepAlive(boolean keepAlive) {
        try {
            ((EpollSocketChannel)this.channel).socket.setKeepAlive((boolean)keepAlive);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        return this;
    }

    @Override
    public EpollSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            ((EpollSocketChannel)this.channel).socket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            ((EpollSocketChannel)this.channel).socket.setReuseAddress((boolean)reuseAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            ((EpollSocketChannel)this.channel).socket.setSendBufferSize((int)sendBufferSize);
            this.calculateMaxBytesPerGatheringWrite();
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setSoLinger(int soLinger) {
        try {
            ((EpollSocketChannel)this.channel).socket.setSoLinger((int)soLinger);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpNoDelay((boolean)tcpNoDelay);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpCork(boolean tcpCork) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpCork((boolean)tcpCork);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setSoBusyPoll(int loopMicros) {
        try {
            ((EpollSocketChannel)this.channel).socket.setSoBusyPoll((int)loopMicros);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpNotSentLowAt(long tcpNotSentLowAt) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpNotSentLowAt((long)tcpNotSentLowAt);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollSocketChannelConfig setTrafficClass(int trafficClass) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTrafficClass((int)trafficClass);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpKeepIdle(int seconds) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpKeepIdle((int)seconds);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpKeepIntvl(int seconds) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpKeepIntvl((int)seconds);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Deprecated
    public EpollSocketChannelConfig setTcpKeepCntl(int probes) {
        return this.setTcpKeepCnt((int)probes);
    }

    public EpollSocketChannelConfig setTcpKeepCnt(int probes) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpKeepCnt((int)probes);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpUserTimeout(int milliseconds) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpUserTimeout((int)milliseconds);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isIpTransparent() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isIpTransparent();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setIpTransparent(boolean transparent) {
        try {
            ((EpollSocketChannel)this.channel).socket.setIpTransparent((boolean)transparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
        try {
            ((EpollSocketChannel)this.channel).setTcpMd5Sig(keys);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpQuickAck(boolean quickAck) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpQuickAck((boolean)quickAck);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isTcpQuickAck() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isTcpQuickAck();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollSocketChannelConfig setTcpFastOpenConnect(boolean fastOpenConnect) {
        try {
            ((EpollSocketChannel)this.channel).socket.setTcpFastOpenConnect((boolean)fastOpenConnect);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isTcpFastOpenConnect() {
        try {
            return ((EpollSocketChannel)this.channel).socket.isTcpFastOpenConnect();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    @Override
    public EpollSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }

    @Override
    public EpollSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    @Override
    public EpollSocketChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode((EpollMode)mode);
        return this;
    }

    private void calculateMaxBytesPerGatheringWrite() {
        int newSendBufferSize = this.getSendBufferSize() << 1;
        if (newSendBufferSize <= 0) return;
        this.setMaxBytesPerGatheringWrite((long)((long)(this.getSendBufferSize() << 1)));
    }
}

