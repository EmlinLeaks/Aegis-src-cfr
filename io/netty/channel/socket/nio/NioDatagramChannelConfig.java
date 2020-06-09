/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

class NioDatagramChannelConfig
extends DefaultDatagramChannelConfig {
    private static final Object IP_MULTICAST_TTL;
    private static final Object IP_MULTICAST_IF;
    private static final Object IP_MULTICAST_LOOP;
    private static final Method GET_OPTION;
    private static final Method SET_OPTION;
    private final java.nio.channels.DatagramChannel javaChannel;

    NioDatagramChannelConfig(NioDatagramChannel channel, java.nio.channels.DatagramChannel javaChannel) {
        super((DatagramChannel)channel, (DatagramSocket)javaChannel.socket());
        this.javaChannel = javaChannel;
    }

    @Override
    public int getTimeToLive() {
        return ((Integer)this.getOption0((Object)IP_MULTICAST_TTL)).intValue();
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int ttl) {
        this.setOption0((Object)IP_MULTICAST_TTL, (Object)Integer.valueOf((int)ttl));
        return this;
    }

    @Override
    public InetAddress getInterface() {
        NetworkInterface inf = this.getNetworkInterface();
        if (inf == null) return null;
        Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface((NetworkInterface)inf);
        if (!addresses.hasMoreElements()) return null;
        return addresses.nextElement();
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        try {
            this.setNetworkInterface((NetworkInterface)NetworkInterface.getByInetAddress((InetAddress)interfaceAddress));
            return this;
        }
        catch (SocketException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return (NetworkInterface)this.getOption0((Object)IP_MULTICAST_IF);
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        this.setOption0((Object)IP_MULTICAST_IF, (Object)networkInterface);
        return this;
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        return ((Boolean)this.getOption0((Object)IP_MULTICAST_LOOP)).booleanValue();
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        this.setOption0((Object)IP_MULTICAST_LOOP, (Object)Boolean.valueOf((boolean)loopbackModeDisabled));
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead((boolean)autoRead);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        ((NioDatagramChannel)this.channel).clearReadPending0();
    }

    private Object getOption0(Object option) {
        if (GET_OPTION == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return GET_OPTION.invoke((Object)this.javaChannel, (Object[])new Object[]{option});
        }
        catch (Exception e) {
            throw new ChannelException((Throwable)e);
        }
    }

    private void setOption0(Object option, Object value) {
        if (SET_OPTION == null) {
            throw new UnsupportedOperationException();
        }
        try {
            SET_OPTION.invoke((Object)this.javaChannel, (Object[])new Object[]{option, value});
            return;
        }
        catch (Exception e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        if (PlatformDependent.javaVersion() < 7) return super.setOption(option, value);
        if (!(option instanceof NioChannelOption)) return super.setOption(option, value);
        return NioChannelOption.setOption((java.nio.channels.Channel)this.javaChannel, (NioChannelOption)option, value);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (PlatformDependent.javaVersion() < 7) return (T)super.getOption(option);
        if (!(option instanceof NioChannelOption)) return (T)super.getOption(option);
        return (T)NioChannelOption.getOption((java.nio.channels.Channel)this.javaChannel, (NioChannelOption)option);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        if (PlatformDependent.javaVersion() < 7) return super.getOptions();
        return this.getOptions(super.getOptions(), NioChannelOption.getOptions((java.nio.channels.Channel)this.javaChannel));
    }

    static {
        ClassLoader classLoader = PlatformDependent.getClassLoader(java.nio.channels.DatagramChannel.class);
        Class<?> socketOptionType = null;
        try {
            socketOptionType = Class.forName((String)"java.net.SocketOption", (boolean)true, (ClassLoader)classLoader);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Class<?> stdSocketOptionType = null;
        try {
            stdSocketOptionType = Class.forName((String)"java.net.StandardSocketOptions", (boolean)true, (ClassLoader)classLoader);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Object ipMulticastTtl = null;
        Object ipMulticastIf = null;
        Object ipMulticastLoop = null;
        Method getOption = null;
        Method setOption = null;
        if (socketOptionType != null) {
            try {
                ipMulticastTtl = stdSocketOptionType.getDeclaredField((String)"IP_MULTICAST_TTL").get(null);
            }
            catch (Exception e) {
                throw new Error((String)"cannot locate the IP_MULTICAST_TTL field", (Throwable)e);
            }
            try {
                ipMulticastIf = stdSocketOptionType.getDeclaredField((String)"IP_MULTICAST_IF").get(null);
            }
            catch (Exception e) {
                throw new Error((String)"cannot locate the IP_MULTICAST_IF field", (Throwable)e);
            }
            try {
                ipMulticastLoop = stdSocketOptionType.getDeclaredField((String)"IP_MULTICAST_LOOP").get(null);
            }
            catch (Exception e) {
                throw new Error((String)"cannot locate the IP_MULTICAST_LOOP field", (Throwable)e);
            }
            Class<?> networkChannelClass = null;
            try {
                networkChannelClass = Class.forName((String)"java.nio.channels.NetworkChannel", (boolean)true, (ClassLoader)classLoader);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (networkChannelClass == null) {
                getOption = null;
                setOption = null;
            } else {
                try {
                    getOption = networkChannelClass.getDeclaredMethod((String)"getOption", socketOptionType);
                }
                catch (Exception e) {
                    throw new Error((String)"cannot locate the getOption() method", (Throwable)e);
                }
                try {
                    setOption = networkChannelClass.getDeclaredMethod((String)"setOption", socketOptionType, Object.class);
                }
                catch (Exception e) {
                    throw new Error((String)"cannot locate the setOption() method", (Throwable)e);
                }
            }
        }
        IP_MULTICAST_TTL = ipMulticastTtl;
        IP_MULTICAST_IF = ipMulticastIf;
        IP_MULTICAST_LOOP = ipMulticastLoop;
        GET_OPTION = getOption;
        SET_OPTION = setOption;
    }
}

