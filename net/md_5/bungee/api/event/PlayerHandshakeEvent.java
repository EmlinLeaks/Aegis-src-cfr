/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.packet.Handshake;

public class PlayerHandshakeEvent
extends Event {
    private final PendingConnection connection;
    private final Handshake handshake;

    public PlayerHandshakeEvent(PendingConnection connection, Handshake handshake) {
        this.connection = connection;
        this.handshake = handshake;
    }

    public PendingConnection getConnection() {
        return this.connection;
    }

    public Handshake getHandshake() {
        return this.handshake;
    }

    public String toString() {
        return "PlayerHandshakeEvent(connection=" + this.getConnection() + ", handshake=" + this.getHandshake() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerHandshakeEvent)) {
            return false;
        }
        PlayerHandshakeEvent other = (PlayerHandshakeEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        PendingConnection this$connection = this.getConnection();
        PendingConnection other$connection = other.getConnection();
        if (this$connection == null ? other$connection != null : !this$connection.equals((Object)other$connection)) {
            return false;
        }
        Handshake this$handshake = this.getHandshake();
        Handshake other$handshake = other.getHandshake();
        if (this$handshake == null) {
            if (other$handshake == null) return true;
            return false;
        }
        if (((Object)this$handshake).equals((Object)other$handshake)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerHandshakeEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        PendingConnection $connection = this.getConnection();
        result = result * 59 + ($connection == null ? 43 : $connection.hashCode());
        Handshake $handshake = this.getHandshake();
        return result * 59 + ($handshake == null ? 43 : ((Object)$handshake).hashCode());
    }
}

