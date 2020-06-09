/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.AbstractConstant;
import io.netty.util.ConstantPool;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class ChannelOption<T>
extends AbstractConstant<ChannelOption<T>> {
    private static final ConstantPool<ChannelOption<Object>> pool = new ConstantPool<ChannelOption<Object>>(){

        protected ChannelOption<Object> newConstant(int id, String name) {
            return new ChannelOption<Object>((int)id, (String)name);
        }
    };
    public static final ChannelOption<ByteBufAllocator> ALLOCATOR = ChannelOption.valueOf((String)"ALLOCATOR");
    public static final ChannelOption<RecvByteBufAllocator> RCVBUF_ALLOCATOR = ChannelOption.valueOf((String)"RCVBUF_ALLOCATOR");
    public static final ChannelOption<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = ChannelOption.valueOf((String)"MESSAGE_SIZE_ESTIMATOR");
    public static final ChannelOption<Integer> CONNECT_TIMEOUT_MILLIS = ChannelOption.valueOf((String)"CONNECT_TIMEOUT_MILLIS");
    @Deprecated
    public static final ChannelOption<Integer> MAX_MESSAGES_PER_READ = ChannelOption.valueOf((String)"MAX_MESSAGES_PER_READ");
    public static final ChannelOption<Integer> WRITE_SPIN_COUNT = ChannelOption.valueOf((String)"WRITE_SPIN_COUNT");
    @Deprecated
    public static final ChannelOption<Integer> WRITE_BUFFER_HIGH_WATER_MARK = ChannelOption.valueOf((String)"WRITE_BUFFER_HIGH_WATER_MARK");
    @Deprecated
    public static final ChannelOption<Integer> WRITE_BUFFER_LOW_WATER_MARK = ChannelOption.valueOf((String)"WRITE_BUFFER_LOW_WATER_MARK");
    public static final ChannelOption<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK = ChannelOption.valueOf((String)"WRITE_BUFFER_WATER_MARK");
    public static final ChannelOption<Boolean> ALLOW_HALF_CLOSURE = ChannelOption.valueOf((String)"ALLOW_HALF_CLOSURE");
    public static final ChannelOption<Boolean> AUTO_READ = ChannelOption.valueOf((String)"AUTO_READ");
    public static final ChannelOption<Boolean> AUTO_CLOSE = ChannelOption.valueOf((String)"AUTO_CLOSE");
    public static final ChannelOption<Boolean> SO_BROADCAST = ChannelOption.valueOf((String)"SO_BROADCAST");
    public static final ChannelOption<Boolean> SO_KEEPALIVE = ChannelOption.valueOf((String)"SO_KEEPALIVE");
    public static final ChannelOption<Integer> SO_SNDBUF = ChannelOption.valueOf((String)"SO_SNDBUF");
    public static final ChannelOption<Integer> SO_RCVBUF = ChannelOption.valueOf((String)"SO_RCVBUF");
    public static final ChannelOption<Boolean> SO_REUSEADDR = ChannelOption.valueOf((String)"SO_REUSEADDR");
    public static final ChannelOption<Integer> SO_LINGER = ChannelOption.valueOf((String)"SO_LINGER");
    public static final ChannelOption<Integer> SO_BACKLOG = ChannelOption.valueOf((String)"SO_BACKLOG");
    public static final ChannelOption<Integer> SO_TIMEOUT = ChannelOption.valueOf((String)"SO_TIMEOUT");
    public static final ChannelOption<Integer> IP_TOS = ChannelOption.valueOf((String)"IP_TOS");
    public static final ChannelOption<InetAddress> IP_MULTICAST_ADDR = ChannelOption.valueOf((String)"IP_MULTICAST_ADDR");
    public static final ChannelOption<NetworkInterface> IP_MULTICAST_IF = ChannelOption.valueOf((String)"IP_MULTICAST_IF");
    public static final ChannelOption<Integer> IP_MULTICAST_TTL = ChannelOption.valueOf((String)"IP_MULTICAST_TTL");
    public static final ChannelOption<Boolean> IP_MULTICAST_LOOP_DISABLED = ChannelOption.valueOf((String)"IP_MULTICAST_LOOP_DISABLED");
    public static final ChannelOption<Boolean> TCP_NODELAY = ChannelOption.valueOf((String)"TCP_NODELAY");
    @Deprecated
    public static final ChannelOption<Boolean> DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION = ChannelOption.valueOf((String)"DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION");
    public static final ChannelOption<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = ChannelOption.valueOf((String)"SINGLE_EVENTEXECUTOR_PER_GROUP");

    public static <T> ChannelOption<T> valueOf(String name) {
        return pool.valueOf((String)name);
    }

    public static <T> ChannelOption<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return pool.valueOf(firstNameComponent, (String)secondNameComponent);
    }

    public static boolean exists(String name) {
        return pool.exists((String)name);
    }

    @Deprecated
    public static <T> ChannelOption<T> newInstance(String name) {
        return pool.newInstance((String)name);
    }

    private ChannelOption(int id, String name) {
        super((int)id, (String)name);
    }

    @Deprecated
    protected ChannelOption(String name) {
        this((int)pool.nextId(), (String)name);
    }

    public void validate(T value) {
        if (value != null) return;
        throw new NullPointerException((String)"value");
    }
}

