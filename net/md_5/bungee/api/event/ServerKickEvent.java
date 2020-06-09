/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerKickEvent
extends Event
implements Cancellable {
    private boolean cancelled;
    private final ProxiedPlayer player;
    private final ServerInfo kickedFrom;
    private BaseComponent[] kickReasonComponent;
    private ServerInfo cancelServer;
    private State state;

    @Deprecated
    public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer) {
        this((ProxiedPlayer)player, (BaseComponent[])kickReasonComponent, (ServerInfo)cancelServer, (State)State.UNKNOWN);
    }

    @Deprecated
    public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state) {
        this((ProxiedPlayer)player, (ServerInfo)player.getServer().getInfo(), (BaseComponent[])kickReasonComponent, (ServerInfo)cancelServer, (State)state);
    }

    public ServerKickEvent(ProxiedPlayer player, ServerInfo kickedFrom, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state) {
        this.player = player;
        this.kickedFrom = kickedFrom;
        this.kickReasonComponent = kickReasonComponent;
        this.cancelServer = cancelServer;
        this.state = state;
    }

    @Deprecated
    public String getKickReason() {
        return BaseComponent.toLegacyText((BaseComponent[])this.kickReasonComponent);
    }

    @Deprecated
    public void setKickReason(String reason) {
        this.kickReasonComponent = TextComponent.fromLegacyText((String)reason);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public ServerInfo getKickedFrom() {
        return this.kickedFrom;
    }

    public BaseComponent[] getKickReasonComponent() {
        return this.kickReasonComponent;
    }

    public ServerInfo getCancelServer() {
        return this.cancelServer;
    }

    public State getState() {
        return this.state;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setKickReasonComponent(BaseComponent[] kickReasonComponent) {
        this.kickReasonComponent = kickReasonComponent;
    }

    public void setCancelServer(ServerInfo cancelServer) {
        this.cancelServer = cancelServer;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String toString() {
        return "ServerKickEvent(cancelled=" + this.isCancelled() + ", player=" + this.getPlayer() + ", kickedFrom=" + this.getKickedFrom() + ", kickReasonComponent=" + Arrays.deepToString((Object[])this.getKickReasonComponent()) + ", cancelServer=" + this.getCancelServer() + ", state=" + (Object)((Object)this.getState()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerKickEvent)) {
            return false;
        }
        ServerKickEvent other = (ServerKickEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        ProxiedPlayer this$player = this.getPlayer();
        ProxiedPlayer other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals((Object)other$player)) {
            return false;
        }
        ServerInfo this$kickedFrom = this.getKickedFrom();
        ServerInfo other$kickedFrom = other.getKickedFrom();
        if (this$kickedFrom == null ? other$kickedFrom != null : !this$kickedFrom.equals((Object)other$kickedFrom)) {
            return false;
        }
        if (!Arrays.deepEquals((Object[])this.getKickReasonComponent(), (Object[])other.getKickReasonComponent())) {
            return false;
        }
        ServerInfo this$cancelServer = this.getCancelServer();
        ServerInfo other$cancelServer = other.getCancelServer();
        if (this$cancelServer == null ? other$cancelServer != null : !this$cancelServer.equals((Object)other$cancelServer)) {
            return false;
        }
        State this$state = this.getState();
        State other$state = other.getState();
        if (this$state == null) {
            if (other$state == null) return true;
            return false;
        }
        if (((Object)((Object)this$state)).equals((Object)((Object)other$state))) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerKickEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        ProxiedPlayer $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        ServerInfo $kickedFrom = this.getKickedFrom();
        result = result * 59 + ($kickedFrom == null ? 43 : $kickedFrom.hashCode());
        result = result * 59 + Arrays.deepHashCode((Object[])this.getKickReasonComponent());
        ServerInfo $cancelServer = this.getCancelServer();
        result = result * 59 + ($cancelServer == null ? 43 : $cancelServer.hashCode());
        State $state = this.getState();
        return result * 59 + ($state == null ? 43 : ((Object)((Object)$state)).hashCode());
    }
}

