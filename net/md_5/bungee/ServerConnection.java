/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import java.util.Objects;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ServerConnection
implements Server {
    private final ChannelWrapper ch;
    private final BungeeServerInfo info;
    private boolean isObsolete;
    private final boolean forgeServer = false;
    private long sentPingId = -1L;
    private final Connection.Unsafe unsafe = new Connection.Unsafe((ServerConnection)this){
        final /* synthetic */ ServerConnection this$0;
        {
            this.this$0 = this$0;
        }

        public void sendPacket(DefinedPacket packet) {
            ServerConnection.access$000((ServerConnection)this.this$0).write((Object)packet);
        }
    };

    @Override
    public void sendData(String channel, byte[] data) {
        this.unsafe().sendPacket((DefinedPacket)new PluginMessage((String)channel, (byte[])data, (boolean)false));
    }

    @Override
    public void disconnect(String reason) {
        this.disconnect((BaseComponent[])new BaseComponent[0]);
    }

    @Override
    public void disconnect(BaseComponent ... reason) {
        Preconditions.checkArgument((boolean)(reason.length == 0), (Object)"Server cannot have disconnect reason");
        this.ch.delayedClose(null);
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.disconnect((BaseComponent[])new BaseComponent[0]);
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.getInfo().getAddress();
    }

    @Override
    public boolean isConnected() {
        if (this.ch.isClosed()) return false;
        return true;
    }

    @Override
    public Connection.Unsafe unsafe() {
        return this.unsafe;
    }

    public ServerConnection(ChannelWrapper ch, BungeeServerInfo info) {
        this.ch = ch;
        this.info = info;
    }

    public ChannelWrapper getCh() {
        return this.ch;
    }

    @Override
    public BungeeServerInfo getInfo() {
        return this.info;
    }

    public boolean isObsolete() {
        return this.isObsolete;
    }

    public void setObsolete(boolean isObsolete) {
        this.isObsolete = isObsolete;
    }

    public boolean isForgeServer() {
        Objects.requireNonNull(this);
        return false;
    }

    public long getSentPingId() {
        return this.sentPingId;
    }

    public void setSentPingId(long sentPingId) {
        this.sentPingId = sentPingId;
    }

    static /* synthetic */ ChannelWrapper access$000(ServerConnection x0) {
        return x0.ch;
    }
}

