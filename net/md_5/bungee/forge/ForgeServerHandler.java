/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.forge;

import java.util.ArrayDeque;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.forge.ForgeLogger;
import net.md_5.bungee.forge.ForgeServerHandshakeState;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeServerHandler {
    private final UserConnection con;
    private final ChannelWrapper ch;
    private final ServerInfo serverInfo;
    private ForgeServerHandshakeState state = ForgeServerHandshakeState.START;
    private boolean serverForge = false;
    private final ArrayDeque<PluginMessage> packetQueue = new ArrayDeque<E>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handle(PluginMessage message) throws IllegalArgumentException {
        if (!message.getTag().equalsIgnoreCase((String)"FML|HS") && !message.getTag().equalsIgnoreCase((String)"FORGE")) {
            throw new IllegalArgumentException((String)"Expecting a Forge REGISTER or FML Handshake packet.");
        }
        message.setAllowExtendedPacket((boolean)true);
        ForgeServerHandshakeState prevState = this.state;
        this.packetQueue.add((PluginMessage)message);
        this.state = (ForgeServerHandshakeState)this.state.send((PluginMessage)message, (UserConnection)this.con);
        if (this.state == prevState) return;
        ArrayDeque<PluginMessage> arrayDeque = this.packetQueue;
        // MONITORENTER : arrayDeque
        do {
            if (this.packetQueue.isEmpty()) {
                // MONITOREXIT : arrayDeque
                return;
            }
            ForgeLogger.logServer((ForgeLogger.LogDirection)ForgeLogger.LogDirection.SENDING, (String)prevState.name(), (PluginMessage)this.packetQueue.getFirst());
            this.con.getForgeClientHandler().receive((PluginMessage)this.packetQueue.removeFirst());
        } while (true);
    }

    public void receive(PluginMessage message) throws IllegalArgumentException {
        this.state = (ForgeServerHandshakeState)this.state.handle((PluginMessage)message, (ChannelWrapper)this.ch);
    }

    public void setServerAsForgeServer() {
        this.serverForge = true;
    }

    public ForgeServerHandler(UserConnection con, ChannelWrapper ch, ServerInfo serverInfo) {
        this.con = con;
        this.ch = ch;
        this.serverInfo = serverInfo;
    }

    public ChannelWrapper getCh() {
        return this.ch;
    }

    ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public ForgeServerHandshakeState getState() {
        return this.state;
    }

    public boolean isServerForge() {
        return this.serverForge;
    }
}

