/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.connection;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import xyz.yooniks.aegis.AddressBlocker;
import xyz.yooniks.aegis.Aegis;

public class PingHandler
extends PacketHandler {
    private static final AddressBlocker ADDRESS_BLOCKER = Aegis.getInstance().getAddressBlocker();
    private final ServerInfo target;
    private final Callback<ServerPing> callback;
    private final int protocol;
    private ChannelWrapper channel;

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.channel = channel;
        MinecraftEncoder encoder = new MinecraftEncoder((Protocol)Protocol.HANDSHAKE, (boolean)false, (int)this.protocol);
        channel.getHandle().pipeline().addAfter((String)"frame-decoder", (String)"packet-decoder", (ChannelHandler)new MinecraftDecoder((Protocol)Protocol.STATUS, (boolean)false, (int)ProxyServer.getInstance().getProtocolVersion()));
        channel.getHandle().pipeline().addAfter((String)"frame-prepender", (String)"packet-encoder", (ChannelHandler)encoder);
        channel.write((Object)new Handshake((int)this.protocol, (String)this.target.getAddress().getHostString(), (int)this.target.getAddress().getPort(), (int)1));
        encoder.setProtocol((Protocol)Protocol.STATUS);
        channel.write((Object)new StatusRequest());
    }

    @Override
    public void exception(Throwable t) {
        this.callback.done(null, (Throwable)t);
    }

    @Override
    public void handle(PacketWrapper packet) {
        if (packet != null) {
            if (packet.packet != null) return;
        }
        if (this.channel == null) {
            return;
        }
        this.channel.close();
        if (this.channel.getRemoteAddress() == null) return;
        if (this.channel.getRemoteAddress().getAddress() == null) return;
        System.out.println((String)("{CasualProtector} " + this.channel.getRemoteAddress().getAddress().getHostAddress() + " null ping packet!"));
    }

    @SuppressFBWarnings(value={"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"})
    @Override
    public void handle(StatusResponse statusResponse) throws Exception {
        Gson gson = BungeeCord.getInstance().gson;
        this.callback.done((ServerPing)gson.fromJson((String)statusResponse.getResponse(), ServerPing.class), null);
        this.channel.close();
    }

    @Override
    public String toString() {
        return "[Ping Handler] -> " + this.target.getName();
    }

    public PingHandler(ServerInfo target, Callback<ServerPing> callback, int protocol) {
        this.target = target;
        this.callback = callback;
        this.protocol = protocol;
    }
}

