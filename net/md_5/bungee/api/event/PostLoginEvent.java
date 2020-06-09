/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PostLoginEvent
extends Event {
    private final ProxiedPlayer player;

    public PostLoginEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String toString() {
        return "PostLoginEvent(player=" + this.getPlayer() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PostLoginEvent)) {
            return false;
        }
        PostLoginEvent other = (PostLoginEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        ProxiedPlayer this$player = this.getPlayer();
        ProxiedPlayer other$player = other.getPlayer();
        if (this$player == null) {
            if (other$player == null) return true;
            return false;
        }
        if (this$player.equals((Object)other$player)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PostLoginEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ProxiedPlayer $player = this.getPlayer();
        return result * 59 + ($player == null ? 43 : $player.hashCode());
    }
}

