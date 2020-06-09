/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee.api.event;

import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectEvent
extends Event
implements Cancellable {
    private final ProxiedPlayer player;
    @NonNull
    private ServerInfo target;
    private boolean cancelled;
    private final Reason reason;

    @Deprecated
    public ServerConnectEvent(ProxiedPlayer player, ServerInfo target) {
        this((ProxiedPlayer)player, (ServerInfo)target, (Reason)Reason.UNKNOWN);
    }

    public ServerConnectEvent(ProxiedPlayer player, ServerInfo target, Reason reason) {
        this.player = player;
        this.target = target;
        this.reason = reason;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    @NonNull
    public ServerInfo getTarget() {
        return this.target;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Reason getReason() {
        return this.reason;
    }

    public void setTarget(@NonNull ServerInfo target) {
        if (target == null) {
            throw new NullPointerException((String)"target is marked non-null but is null");
        }
        this.target = target;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String toString() {
        return "ServerConnectEvent(player=" + this.getPlayer() + ", target=" + this.getTarget() + ", cancelled=" + this.isCancelled() + ", reason=" + (Object)((Object)this.getReason()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerConnectEvent)) {
            return false;
        }
        ServerConnectEvent other = (ServerConnectEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        ProxiedPlayer this$player = this.getPlayer();
        ProxiedPlayer other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals((Object)other$player)) {
            return false;
        }
        ServerInfo this$target = this.getTarget();
        ServerInfo other$target = other.getTarget();
        if (this$target == null ? other$target != null : !this$target.equals((Object)other$target)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        Reason this$reason = this.getReason();
        Reason other$reason = other.getReason();
        if (this$reason == null) {
            if (other$reason == null) return true;
            return false;
        }
        if (((Object)((Object)this$reason)).equals((Object)((Object)other$reason))) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerConnectEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ProxiedPlayer $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        ServerInfo $target = this.getTarget();
        result = result * 59 + ($target == null ? 43 : $target.hashCode());
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        Reason $reason = this.getReason();
        return result * 59 + ($reason == null ? 43 : ((Object)((Object)$reason)).hashCode());
    }
}

