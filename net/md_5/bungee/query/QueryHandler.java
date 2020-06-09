/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.query;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.query.QueryHandler;

public class QueryHandler
extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ProxyServer bungee;
    private final ListenerInfo listener;
    private final Random random = new Random();
    private final Cache<InetAddress, QuerySession> sessions = CacheBuilder.newBuilder().expireAfterWrite((long)30L, (TimeUnit)TimeUnit.SECONDS).build();

    private void writeShort(ByteBuf buf, int s) {
        buf.writeShortLE((int)s);
    }

    private void writeNumber(ByteBuf buf, int i) {
        this.writeString((ByteBuf)buf, (String)Integer.toString((int)i));
    }

    private void writeString(ByteBuf buf, String s) {
        char[] arrc = s.toCharArray();
        int n = arrc.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                buf.writeByte((int)0);
                return;
            }
            char c = arrc[n2];
            buf.writeByte((int)c);
            ++n2;
        } while (true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        try {
            this.handleMessage((ChannelHandlerContext)ctx, (DatagramPacket)msg);
            return;
        }
        catch (Throwable t) {
            this.bungee.getLogger().log((Level)Level.WARNING, (String)("Error whilst handling query packet from " + msg.sender()), (Throwable)t);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, DatagramPacket msg) {
        int challengeToken;
        ByteBuf in = (ByteBuf)msg.content();
        if (in.readUnsignedByte() != 254 || in.readUnsignedByte() != 253) {
            this.bungee.getLogger().log((Level)Level.WARNING, (String)"Query - Incorrect magic!: {0}", msg.sender());
            return;
        }
        ByteBuf out = ctx.alloc().buffer();
        DatagramPacket response = new DatagramPacket((ByteBuf)out, (InetSocketAddress)((InetSocketAddress)msg.sender()));
        byte type = in.readByte();
        int sessionId = in.readInt();
        if (type == 9) {
            out.writeByte((int)9);
            out.writeInt((int)sessionId);
            challengeToken = this.random.nextInt();
            this.sessions.put((InetAddress)((InetSocketAddress)msg.sender()).getAddress(), (QuerySession)new QuerySession((int)challengeToken, (long)System.currentTimeMillis()));
            this.writeNumber((ByteBuf)out, (int)challengeToken);
        }
        if (type == 0) {
            challengeToken = in.readInt();
            QuerySession session = this.sessions.getIfPresent((Object)((InetSocketAddress)msg.sender()).getAddress());
            if (session == null) throw new IllegalStateException((String)"No session!");
            if (session.getToken() != challengeToken) {
                throw new IllegalStateException((String)"No session!");
            }
            out.writeByte((int)0);
            out.writeInt((int)sessionId);
            if (in.readableBytes() == 0) {
                this.writeString((ByteBuf)out, (String)this.listener.getMotd());
                this.writeString((ByteBuf)out, (String)"SMP");
                this.writeString((ByteBuf)out, (String)"BungeeCord_Proxy");
                this.writeNumber((ByteBuf)out, (int)this.bungee.getOnlineCount());
                this.writeNumber((ByteBuf)out, (int)this.listener.getMaxPlayers());
                this.writeShort((ByteBuf)out, (int)this.listener.getHost().getPort());
                this.writeString((ByteBuf)out, (String)this.listener.getHost().getHostString());
            } else {
                if (in.readableBytes() != 4) throw new IllegalStateException((String)"Invalid data request packet");
                out.writeBytes((byte[])new byte[]{115, 112, 108, 105, 116, 110, 117, 109, 0, -128, 0});
                LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
                data.put("hostname", this.listener.getMotd());
                data.put("gametype", "SMP");
                data.put("game_id", "MINECRAFT");
                data.put("version", this.bungee.getGameVersion());
                data.put("plugins", "");
                data.put("map", "BungeeCord_Proxy");
                data.put("numplayers", Integer.toString((int)this.bungee.getOnlineCount()));
                data.put("maxplayers", Integer.toString((int)this.listener.getMaxPlayers()));
                data.put("hostport", Integer.toString((int)this.listener.getHost().getPort()));
                data.put("hostip", this.listener.getHost().getHostString());
                for (Map.Entry<K, V> entry : data.entrySet()) {
                    this.writeString((ByteBuf)out, (String)((String)entry.getKey()));
                    this.writeString((ByteBuf)out, (String)((String)entry.getValue()));
                }
                out.writeByte((int)0);
                this.writeString((ByteBuf)out, (String)"\u0001player_\u0000");
                for (ProxiedPlayer p : this.bungee.getPlayers()) {
                    this.writeString((ByteBuf)out, (String)p.getName());
                }
                out.writeByte((int)0);
            }
        }
        ctx.writeAndFlush((Object)response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.bungee.getLogger().log((Level)Level.WARNING, (String)("Error whilst handling query packet from " + ctx.channel().remoteAddress()), (Throwable)cause);
    }

    public QueryHandler(ProxyServer bungee, ListenerInfo listener) {
        this.bungee = bungee;
        this.listener = listener;
    }
}

