/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class BungeeServerInfo
implements ServerInfo {
    private final String name;
    private final InetSocketAddress address;
    private final Collection<ProxiedPlayer> players = new ArrayList<ProxiedPlayer>();
    private final String motd;
    private final boolean restricted;
    private final Queue<DefinedPacket> packetQueue = new LinkedList<DefinedPacket>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPlayer(ProxiedPlayer player) {
        Collection<ProxiedPlayer> collection = this.players;
        // MONITORENTER : collection
        this.players.add((ProxiedPlayer)player);
        // MONITOREXIT : collection
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePlayer(ProxiedPlayer player) {
        Collection<ProxiedPlayer> collection = this.players;
        // MONITORENTER : collection
        this.players.remove((Object)player);
        // MONITOREXIT : collection
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        Collection<ProxiedPlayer> collection = this.players;
        // MONITORENTER : collection
        // MONITOREXIT : collection
        return Collections.unmodifiableCollection(new HashSet<ProxiedPlayer>(this.players));
    }

    @Override
    public String getPermission() {
        return "bungeecord.server." + this.name;
    }

    @Override
    public boolean canAccess(CommandSender player) {
        Preconditions.checkNotNull(player, (Object)"player");
        if (!this.restricted) return true;
        if (player.hasPermission((String)this.getPermission())) return true;
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ServerInfo)) return false;
        if (!Objects.equals((Object)this.getAddress(), (Object)((ServerInfo)obj).getAddress())) return false;
        return true;
    }

    public int hashCode() {
        return this.address.hashCode();
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.sendData((String)channel, (byte[])data, (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean sendData(String channel, byte[] data, boolean queue) {
        Server server;
        Preconditions.checkNotNull(channel, (Object)"channel");
        Preconditions.checkNotNull(data, (Object)"data");
        Queue<DefinedPacket> queue2 = this.packetQueue;
        // MONITORENTER : queue2
        Server server2 = server = this.players.isEmpty() ? null : this.players.iterator().next().getServer();
        if (server != null) {
            server.sendData((String)channel, (byte[])data);
            // MONITOREXIT : queue2
            return true;
        }
        if (queue) {
            this.packetQueue.add((DefinedPacket)new PluginMessage((String)channel, (byte[])data, (boolean)false));
        }
        // MONITOREXIT : queue2
        return false;
    }

    @Override
    public void ping(Callback<ServerPing> callback) {
        this.ping(callback, (int)ProxyServer.getInstance().getProtocolVersion());
    }

    public void ping(Callback<ServerPing> callback, int protocolVersion) {
        Preconditions.checkNotNull(callback, (Object)"callback");
        ChannelFutureListener listener = new ChannelFutureListener((BungeeServerInfo)this, callback, (int)protocolVersion){
            final /* synthetic */ Callback val$callback;
            final /* synthetic */ int val$protocolVersion;
            final /* synthetic */ BungeeServerInfo this$0;
            {
                this.this$0 = this$0;
                this.val$callback = callback;
                this.val$protocolVersion = n;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (future.isSuccess()) {
                    future.channel().pipeline().get(net.md_5.bungee.netty.HandlerBoss.class).setHandler((net.md_5.bungee.netty.PacketHandler)new net.md_5.bungee.connection.PingHandler((ServerInfo)this.this$0, (Callback<ServerPing>)this.val$callback, (int)this.val$protocolVersion));
                    return;
                }
                this.val$callback.done(null, (java.lang.Throwable)future.cause());
            }
        };
        ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(PipelineUtils.getChannel())).group((EventLoopGroup)BungeeCord.getInstance().eventLoops)).handler((ChannelHandler)PipelineUtils.BASE)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf((int)5000))).remoteAddress((SocketAddress)this.getAddress()).connect().addListener((GenericFutureListener<? extends Future<? super Void>>)listener);
    }

    public BungeeServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        this.name = name;
        this.address = address;
        this.motd = motd;
        this.restricted = restricted;
    }

    public String toString() {
        return "BungeeServerInfo(name=" + this.getName() + ", address=" + this.getAddress() + ", restricted=" + this.isRestricted() + ")";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public String getMotd() {
        return this.motd;
    }

    @Override
    public boolean isRestricted() {
        return this.restricted;
    }

    public Queue<DefinedPacket> getPacketQueue() {
        return this.packetQueue;
    }
}

