/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectedEvent
extends Event {
    private final ProxiedPlayer player;
    private final Server server;

    public ServerConnectedEvent(ProxiedPlayer player, Server server) {
        this.player = player;
        this.server = server;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public Server getServer() {
        return this.server;
    }

    public String toString() {
        return "ServerConnectedEvent(player=" + this.getPlayer() + ", server=" + this.getServer() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerConnectedEvent)) {
            return false;
        }
        ServerConnectedEvent other = (ServerConnectedEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        ProxiedPlayer this$player = this.getPlayer();
        ProxiedPlayer other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals((Object)other$player)) {
            return false;
        }
        Server this$server = this.getServer();
        Server other$server = other.getServer();
        if (this$server == null) {
            if (other$server == null) return true;
            return false;
        }
        if (this$server.equals((Object)other$server)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerConnectedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ProxiedPlayer $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        Server $server = this.getServer();
        return result * 59 + ($server == null ? 43 : $server.hashCode());
    }
}

