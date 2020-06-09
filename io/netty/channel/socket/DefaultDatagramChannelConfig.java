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
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Map;

public class DefaultDatagramChannelConfig
extends DefaultChannelConfig
implements DatagramChannelConfig {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDatagramChannelConfig.class);
    private final DatagramSocket javaSocket;
    private volatile boolean activeOnOpen;

    public DefaultDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
        super((Channel)channel, (RecvByteBufAllocator)new FixedRecvByteBufAllocator((int)2048));
        if (javaSocket == null) {
            throw new NullPointerException((String)"javaSocket");
        }
        this.javaSocket = javaSocket;
    }

    protected final DatagramSocket javaSocket() {
        return this.javaSocket;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION);
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
        if (option != ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) return (T)super.getOption(option);
        return (T)Boolean.valueOf((boolean)this.activeOnOpen);
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
        if (option != ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) return super.setOption(option, value);
        this.setActiveOnOpen((boolean)((Boolean)value).booleanValue());
        return true;
    }

    private void setActiveOnOpen(boolean activeOnOpen) {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException((String)"Can only changed before channel was registered");
        }
        this.activeOnOpen = activeOnOpen;
    }

    @Override
    public boolean isBroadcast() {
        try {
            return this.javaSocket.getBroadcast();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setBroadcast(boolean broadcast) {
        try {
            if (broadcast && !this.javaSocket.getLocalAddress().isAnyLocalAddress() && !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser()) {
                logger.warn((String)("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; setting the SO_BROADCAST flag anyway as requested on the socket which is bound to " + this.javaSocket.getLocalSocketAddress() + '.'));
            }
            this.javaSocket.setBroadcast((boolean)broadcast);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public InetAddress getInterface() {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            return ((MulticastSocket)this.javaSocket).getInterface();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            ((MulticastSocket)this.javaSocket).setInterface((InetAddress)interfaceAddress);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            return ((MulticastSocket)this.javaSocket).getLoopbackMode();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            ((MulticastSocket)this.javaSocket).setLoopbackMode((boolean)loopbackModeDisabled);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            return ((MulticastSocket)this.javaSocket).getNetworkInterface();
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            ((MulticastSocket)this.javaSocket).setNetworkInterface((NetworkInterface)networkInterface);
            return this;
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
    public DatagramChannelConfig setReuseAddress(boolean reuseAddress) {
        try {
            this.javaSocket.setReuseAddress((boolean)reuseAddress);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
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
    public DatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
        try {
            this.javaSocket.setReceiveBufferSize((int)receiveBufferSize);
            return this;
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
    public DatagramChannelConfig setSendBufferSize(int sendBufferSize) {
        try {
            this.javaSocket.setSendBufferSize((int)sendBufferSize);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public int getTimeToLive() {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            return ((MulticastSocket)this.javaSocket).getTimeToLive();
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int ttl) {
        if (!(this.javaSocket instanceof MulticastSocket)) throw new UnsupportedOperationException();
        try {
            ((MulticastSocket)this.javaSocket).setTimeToLive((int)ttl);
            return this;
        }
        catch (IOException e) {
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
    public DatagramChannelConfig setTrafficClass(int trafficClass) {
        try {
            this.javaSocket.setTrafficClass((int)trafficClass);
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public DatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount((int)writeSpinCount);
        return this;
    }

    @Override
    public DatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis((int)connectTimeoutMillis);
        return this;
    }

    @Deprecated
    @Override
    public DatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead((int)maxMessagesPerRead);
        return this;
    }

    @Override
    public DatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator((ByteBufAllocator)allocator);
        return this;
    }

    @Override
    public DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoClose(boolean autoClose) {
        super.setAutoClose((boolean)autoClose);
        return this;
    }

    @Override
    public DatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark((int)writeBufferHighWaterMark);
        return this;
    }

    @Override
    public DatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark((int)writeBufferLowWaterMark);
        return this;
    }

    @Override
    public DatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark((WriteBufferWaterMark)writeBufferWaterMark);
        return this;
    }

    @Override
    public DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator((MessageSizeEstimator)estimator);
        return this;
    }
}

