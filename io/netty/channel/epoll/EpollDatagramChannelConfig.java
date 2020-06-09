/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig
extends EpollChannelConfig
implements DatagramChannelConfig {
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator((int)2048);
    private boolean activeOnOpen;
    private volatile int maxDatagramSize;

    EpollDatagramChannelConfig(EpollDatagramChannel channel) {
        super((AbstractEpollChannel)channel);
        this.setRecvByteBufAllocator((RecvByteBufAllocator)DEFAULT_RCVBUF_ALLOCATOR);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.IP_RECVORIGDSTADDR, EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == ChannelOption.SO_BROADCAST) {
            return (T)Boolean.valueOf((boolean)this.isBroadcast());
        }
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf((int)this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf((int)this.getSendBufferSize());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf((boolean)this.isReuseAddress());
        }
        if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            return (T)Boolean.valueOf((boolean)this.isLoopbackModeDisabled());
        }
        if (option == ChannelOption.IP_MULTICAST_ADDR) {
            return (T)this.getInterface();
        }
        if (option == ChannelOption.IP_MULTICAST_IF) {
            return (T)this.getNetworkInterface();
        }
        if (option == ChannelOption.IP_MULTICAST_TTL) {
            return (T)Integer.valueOf((int)this.getTimeToLive());
        }
        if (option == ChannelOption.IP_TOS) {
            return (T)Integer.valueOf((int)this.getTrafficClass());
        }
        if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            return (T)Boolean.valueOf((boolean)this.activeOnOpen);
        }
        if (option == EpollChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf((boolean)this.isReusePort());
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return (T)Boolean.valueOf((boolean)this.isIpTransparent());
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            return (T)Boolean.valueOf((boolean)this.isFreeBind());
        }
        if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
            return (T)Boolean.valueOf((boolean)this.isIpRecvOrigDestAddr());
        }
        if (option != EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) return (T)super.getOption(option);
        return (T)Integer.valueOf((int)this.getMaxDatagramPayloadSize());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_BROADCAST) {
            this.setBroadcast((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            this.setLoopbackModeDisabled((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.IP_MULTICAST_ADDR) {
            this.setInterface((InetAddress)((InetAddress)value));
            return true;
        }
        if (option == ChannelOption.IP_MULTICAST_IF) {
            this.setNetworkInterface((NetworkInterface)((NetworkInterface)value));
            return true;
        }
        if (option == ChannelOption.IP_MULTICAST_TTL) {
            this.setTimeToLive((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.IP_TOS) {
            this.setTrafficClass((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            this.setActiveOnOpen((boolean)((Boolean)value).booleanValue());
            return true;
        }
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
        if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
            this.setIpRecvOrigDestAddr((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option != EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) return super.setOption(option, value);
        this.setMaxDatagramPayloadSize((int)((Integer)value).intValue());
        return true;
    }

    private void setActiveOnOpen(boolean activeOnOpen) {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException((String)"Can only changed before channel was registered");
        }
        this.activeOnOpen = activeOnOpen;
    }

    boolean getActiveOnOpen() {
        return this.activeOnOpen;
    }

    @Override
    public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }

    @Deprecated
    @Override
    public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Deprecated
    @Override
    public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public EpollDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public EpollDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public int getSendBufferSize() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getSendBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setSendBufferSize((int)sendBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getTrafficClass() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getTrafficClass();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setTrafficClass(int trafficClass) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setTrafficClass((int)trafficClass);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isReuseAddress();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReuseAddress((boolean)reuseAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isBroadcast() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isBroadcast();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setBroadcast(boolean broadcast) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setBroadcast((boolean)broadcast);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isLoopbackModeDisabled();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setLoopbackModeDisabled((boolean)loopbackModeDisabled);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getTimeToLive() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getTimeToLive();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setTimeToLive(int ttl) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setTimeToLive((int)ttl);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public InetAddress getInterface() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getInterface();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setInterface((InetAddress)interfaceAddress);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getNetworkInterface();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        try {
            EpollDatagramChannel datagramChannel = (EpollDatagramChannel)this.channel;
            datagramChannel.socket.setNetworkInterface((NetworkInterface)networkInterface);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public EpollDatagramChannelConfig setEpollMode(EpollMode mode) {
        super.setEpollMode((EpollMode)mode);
        return this;
    }

    public boolean isReusePort() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isReusePort();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollDatagramChannelConfig setReusePort(boolean reusePort) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReusePort((boolean)reusePort);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isIpTransparent() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpTransparent();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollDatagramChannelConfig setIpTransparent(boolean ipTransparent) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpTransparent((boolean)ipTransparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isFreeBind() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpFreeBind();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollDatagramChannelConfig setFreeBind(boolean freeBind) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpFreeBind((boolean)freeBind);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public boolean isIpRecvOrigDestAddr() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpRecvOrigDestAddr();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollDatagramChannelConfig setIpRecvOrigDestAddr(boolean ipTransparent) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpRecvOrigDestAddr((boolean)ipTransparent);
            return this;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    public EpollDatagramChannelConfig setMaxDatagramPayloadSize(int maxDatagramSize) {
        this.maxDatagramSize = ObjectUtil.checkPositiveOrZero((int)maxDatagramSize, (String)"maxDatagramSize");
        return this;
    }

    public int getMaxDatagramPayloadSize() {
        return this.maxDatagramSize;
    }
}

