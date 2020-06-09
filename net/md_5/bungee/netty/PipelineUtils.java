/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.KickStringWriter;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;

public class PipelineUtils {
    public static final AttributeKey<ListenerInfo> LISTENER = AttributeKey.valueOf((String)"ListerInfo");
    public static final AttributeKey<UserConnection> USER = AttributeKey.valueOf((String)"User");
    public static final AttributeKey<BungeeServerInfo> TARGET = AttributeKey.valueOf((String)"Target");
    public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>(){

        protected void initChannel(Channel ch) throws java.lang.Exception {
            if (net.md_5.bungee.BungeeCord.getInstance().getConnectionThrottle() != null && net.md_5.bungee.BungeeCord.getInstance().getConnectionThrottle().throttle((java.net.InetAddress)((java.net.InetSocketAddress)ch.remoteAddress()).getAddress())) {
                ch.close();
                return;
            }
            ListenerInfo listener = ch.attr(LISTENER).get();
            BASE.initChannel((Channel)ch);
            ch.pipeline().addBefore((String)"frame-decoder", (String)"legacy-decoder", (io.netty.channel.ChannelHandler)new net.md_5.bungee.protocol.LegacyDecoder());
            ch.pipeline().addAfter((String)"frame-decoder", (String)"packet-decoder", (io.netty.channel.ChannelHandler)new net.md_5.bungee.protocol.MinecraftDecoder((net.md_5.bungee.protocol.Protocol)net.md_5.bungee.protocol.Protocol.HANDSHAKE, (boolean)true, (int)ProxyServer.getInstance().getProtocolVersion()));
            ch.pipeline().addAfter((String)"frame-prepender", (String)"packet-encoder", (io.netty.channel.ChannelHandler)new net.md_5.bungee.protocol.MinecraftEncoder((net.md_5.bungee.protocol.Protocol)net.md_5.bungee.protocol.Protocol.HANDSHAKE, (boolean)true, (int)ProxyServer.getInstance().getProtocolVersion()));
            ch.pipeline().addBefore((String)"frame-prepender", (String)"legacy-kick", (io.netty.channel.ChannelHandler)PipelineUtils.access$000());
            ch.pipeline().get(net.md_5.bungee.netty.HandlerBoss.class).setHandler((net.md_5.bungee.netty.PacketHandler)new net.md_5.bungee.connection.InitialHandler((net.md_5.bungee.BungeeCord)net.md_5.bungee.BungeeCord.getInstance(), (ListenerInfo)listener));
            if (!listener.isProxyProtocol()) return;
            ch.pipeline().addFirst((io.netty.channel.ChannelHandler[])new io.netty.channel.ChannelHandler[]{new io.netty.handler.codec.haproxy.HAProxyMessageDecoder()});
        }
    };
    public static final Base BASE = new Base();
    private static final KickStringWriter legacyKicker = new KickStringWriter();
    private static final Varint21LengthFieldPrepender framePrepender = new Varint21LengthFieldPrepender();
    public static final String TIMEOUT_HANDLER = "timeout";
    public static final String PACKET_DECODER = "packet-decoder";
    public static final String PACKET_ENCODER = "packet-encoder";
    public static final String BOSS_HANDLER = "inbound-boss";
    public static final String ENCRYPT_HANDLER = "encrypt";
    public static final String DECRYPT_HANDLER = "decrypt";
    public static final String FRAME_DECODER = "frame-decoder";
    public static final String FRAME_PREPENDER = "frame-prepender";
    public static final String LEGACY_DECODER = "legacy-decoder";
    public static final String LEGACY_KICKER = "legacy-kick";
    private static boolean epoll;
    private static final int LOW_MARK;
    private static final int HIGH_MARK;
    private static final WriteBufferWaterMark MARK;

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {
        MultithreadEventLoopGroup multithreadEventLoopGroup;
        if (epoll) {
            multithreadEventLoopGroup = new EpollEventLoopGroup((int)threads, (ThreadFactory)factory);
            return multithreadEventLoopGroup;
        }
        multithreadEventLoopGroup = new NioEventLoopGroup((int)threads, (ThreadFactory)factory);
        return multithreadEventLoopGroup;
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        if (!epoll) return NioServerSocketChannel.class;
        return EpollServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {
        if (!epoll) return NioSocketChannel.class;
        return EpollSocketChannel.class;
    }

    public static Class<? extends Channel> getDatagramChannel() {
        if (!epoll) return NioDatagramChannel.class;
        return EpollDatagramChannel.class;
    }

    static /* synthetic */ KickStringWriter access$000() {
        return legacyKicker;
    }

    static /* synthetic */ WriteBufferWaterMark access$100() {
        return MARK;
    }

    static /* synthetic */ Varint21LengthFieldPrepender access$200() {
        return framePrepender;
    }

    static {
        if (!PlatformDependent.isWindows() && Boolean.parseBoolean((String)System.getProperty((String)"bungee.epoll", (String)"true"))) {
            ProxyServer.getInstance().getLogger().info((String)"Not on Windows, attempting to use enhanced EpollEventLoop");
            epoll = Epoll.isAvailable();
            if (epoll) {
                ProxyServer.getInstance().getLogger().info((String)"Epoll is working, utilising it!");
            } else {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Epoll is not working, falling back to NIO: {0}", (Object)Util.exception((Throwable)Epoll.unavailabilityCause()));
            }
        }
        LOW_MARK = Integer.getInteger((String)"net.md_5.bungee.low_mark", (int)524288).intValue();
        HIGH_MARK = Integer.getInteger((String)"net.md_5.bungee.high_mark", (int)2097152).intValue();
        MARK = new WriteBufferWaterMark((int)LOW_MARK, (int)HIGH_MARK);
    }
}

