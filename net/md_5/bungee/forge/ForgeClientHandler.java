/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee.forge;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Map;
import lombok.NonNull;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.forge.ForgeClientHandshakeState;
import net.md_5.bungee.forge.ForgeConstants;
import net.md_5.bungee.forge.ForgeLogger;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeClientHandler {
    @NonNull
    private final UserConnection con;
    private Map<String, String> clientModList = null;
    private final ArrayDeque<PluginMessage> packetQueue = new ArrayDeque<E>();
    @NonNull
    private ForgeClientHandshakeState state = ForgeClientHandshakeState.HELLO;
    private PluginMessage serverModList = null;
    private PluginMessage serverIdList = null;
    private boolean fmlTokenInHandshake = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handle(PluginMessage message) throws IllegalArgumentException {
        if (!message.getTag().equalsIgnoreCase((String)"FML|HS")) {
            throw new IllegalArgumentException((String)"Expecting a Forge Handshake packet.");
        }
        message.setAllowExtendedPacket((boolean)true);
        ForgeClientHandshakeState prevState = this.state;
        Preconditions.checkState((boolean)(this.packetQueue.size() < 128), (Object)"Forge packet queue too big!");
        this.packetQueue.add((PluginMessage)message);
        this.state = (ForgeClientHandshakeState)this.state.send((PluginMessage)message, (UserConnection)this.con);
        if (this.state == prevState) return;
        ArrayDeque<PluginMessage> arrayDeque = this.packetQueue;
        // MONITORENTER : arrayDeque
        do {
            if (this.packetQueue.isEmpty()) {
                // MONITOREXIT : arrayDeque
                return;
            }
            ForgeLogger.logClient((ForgeLogger.LogDirection)ForgeLogger.LogDirection.SENDING, (String)prevState.name(), (PluginMessage)this.packetQueue.getFirst());
            this.con.getForgeServerHandler().receive((PluginMessage)this.packetQueue.removeFirst());
        } while (true);
    }

    public void receive(PluginMessage message) throws IllegalArgumentException {
        this.state = (ForgeClientHandshakeState)this.state.handle((PluginMessage)message, (UserConnection)this.con);
    }

    public void resetHandshake() {
        this.state = ForgeClientHandshakeState.HELLO;
        this.con.unsafe().sendPacket((DefinedPacket)ForgeConstants.FML_RESET_HANDSHAKE);
    }

    public void setServerModList(PluginMessage modList) throws IllegalArgumentException {
        if (!modList.getTag().equalsIgnoreCase((String)"FML|HS")) throw new IllegalArgumentException((String)"modList");
        if (modList.getData()[0] != 2) {
            throw new IllegalArgumentException((String)"modList");
        }
        this.serverModList = modList;
    }

    public void setServerIdList(PluginMessage idList) throws IllegalArgumentException {
        if (!idList.getTag().equalsIgnoreCase((String)"FML|HS")) throw new IllegalArgumentException((String)"idList");
        if (idList.getData()[0] != 3) {
            throw new IllegalArgumentException((String)"idList");
        }
        this.serverIdList = idList;
    }

    public boolean isHandshakeComplete() {
        if (this.state != ForgeClientHandshakeState.DONE) return false;
        return true;
    }

    public void setHandshakeComplete() {
        this.state = ForgeClientHandshakeState.DONE;
    }

    public boolean isForgeUser() {
        if (this.fmlTokenInHandshake) return true;
        if (this.clientModList != null) return true;
        return false;
    }

    public ForgeClientHandler(@NonNull UserConnection con) {
        if (con == null) {
            throw new NullPointerException((String)"con is marked non-null but is null");
        }
        this.con = con;
    }

    public Map<String, String> getClientModList() {
        return this.clientModList;
    }

    void setClientModList(Map<String, String> clientModList) {
        this.clientModList = clientModList;
    }

    void setState(@NonNull ForgeClientHandshakeState state) {
        if (state == null) {
            throw new NullPointerException((String)"state is marked non-null but is null");
        }
        this.state = state;
    }

    public boolean isFmlTokenInHandshake() {
        return this.fmlTokenInHandshake;
    }

    public void setFmlTokenInHandshake(boolean fmlTokenInHandshake) {
        this.fmlTokenInHandshake = fmlTokenInHandshake;
    }
}

