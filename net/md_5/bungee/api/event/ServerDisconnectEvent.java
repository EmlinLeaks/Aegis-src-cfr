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
import net.md_5.bungee.api.plugin.Event;

public class ServerDisconnectEvent
extends Event {
    @NonNull
    private final ProxiedPlayer player;
    @NonNull
    private final ServerInfo target;

    @NonNull
    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    @NonNull
    public ServerInfo getTarget() {
        return this.target;
    }

    public ServerDisconnectEvent(@NonNull ProxiedPlayer player, @NonNull ServerInfo target) {
        if (player == null) {
            throw new NullPointerException((String)"player is marked non-null but is null");
        }
        if (target == null) {
            throw new NullPointerException((String)"target is marked non-null but is null");
        }
        this.player = player;
        this.target = target;
    }

    public String toString() {
        return "ServerDisconnectEvent(player=" + this.getPlayer() + ", target=" + this.getTarget() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerDisconnectEvent)) {
            return false;
        }
        ServerDisconnectEvent other = (ServerDisconnectEvent)o;
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
        if (this$target == null) {
            if (other$target == null) return true;
            return false;
        }
        if (this$target.equals((Object)other$target)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerDisconnectEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ProxiedPlayer $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        ServerInfo $target = this.getTarget();
        return result * 59 + ($target == null ? 43 : $target.hashCode());
    }
}

