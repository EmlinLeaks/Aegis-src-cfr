/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Event;

public abstract class TargetedEvent
extends Event {
    private final Connection sender;
    private final Connection receiver;

    public Connection getSender() {
        return this.sender;
    }

    public Connection getReceiver() {
        return this.receiver;
    }

    public String toString() {
        return "TargetedEvent(sender=" + this.getSender() + ", receiver=" + this.getReceiver() + ")";
    }

    public TargetedEvent(Connection sender, Connection receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TargetedEvent)) {
            return false;
        }
        TargetedEvent other = (TargetedEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Connection this$sender = this.getSender();
        Connection other$sender = other.getSender();
        if (this$sender == null ? other$sender != null : !this$sender.equals((Object)other$sender)) {
            return false;
        }
        Connection this$receiver = this.getReceiver();
        Connection other$receiver = other.getReceiver();
        if (this$receiver == null) {
            if (other$receiver == null) return true;
            return false;
        }
        if (this$receiver.equals((Object)other$receiver)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TargetedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Connection $sender = this.getSender();
        result = result * 59 + ($sender == null ? 43 : $sender.hashCode());
        Connection $receiver = this.getReceiver();
        return result * 59 + ($receiver == null ? 43 : $receiver.hashCode());
    }
}

