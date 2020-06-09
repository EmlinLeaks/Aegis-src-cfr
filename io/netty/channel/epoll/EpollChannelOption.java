/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.unix.UnixChannelOption;
import java.net.InetAddress;
import java.util.Map;

public final class EpollChannelOption<T>
extends UnixChannelOption<T> {
    public static final ChannelOption<Boolean> TCP_CORK = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_CORK");
    public static final ChannelOption<Long> TCP_NOTSENT_LOWAT = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_NOTSENT_LOWAT");
    public static final ChannelOption<Integer> TCP_KEEPIDLE = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_KEEPIDLE");
    public static final ChannelOption<Integer> TCP_KEEPINTVL = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_KEEPINTVL");
    public static final ChannelOption<Integer> TCP_KEEPCNT = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_KEEPCNT");
    public static final ChannelOption<Integer> TCP_USER_TIMEOUT = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_USER_TIMEOUT");
    public static final ChannelOption<Boolean> IP_FREEBIND = EpollChannelOption.valueOf((String)"IP_FREEBIND");
    public static final ChannelOption<Boolean> IP_TRANSPARENT = EpollChannelOption.valueOf((String)"IP_TRANSPARENT");
    public static final ChannelOption<Boolean> IP_RECVORIGDSTADDR = EpollChannelOption.valueOf((String)"IP_RECVORIGDSTADDR");
    public static final ChannelOption<Integer> TCP_FASTOPEN = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_FASTOPEN");
    public static final ChannelOption<Boolean> TCP_FASTOPEN_CONNECT = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_FASTOPEN_CONNECT");
    public static final ChannelOption<Integer> TCP_DEFER_ACCEPT = ChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_DEFER_ACCEPT");
    public static final ChannelOption<Boolean> TCP_QUICKACK = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"TCP_QUICKACK");
    public static final ChannelOption<Integer> SO_BUSY_POLL = EpollChannelOption.valueOf(EpollChannelOption.class, (String)"SO_BUSY_POLL");
    public static final ChannelOption<EpollMode> EPOLL_MODE = ChannelOption.valueOf(EpollChannelOption.class, (String)"EPOLL_MODE");
    public static final ChannelOption<Map<InetAddress, byte[]>> TCP_MD5SIG = EpollChannelOption.valueOf((String)"TCP_MD5SIG");
    public static final ChannelOption<Integer> MAX_DATAGRAM_PAYLOAD_SIZE = EpollChannelOption.valueOf((String)"MAX_DATAGRAM_PAYLOAD_SIZE");

    private EpollChannelOption() {
    }
}

