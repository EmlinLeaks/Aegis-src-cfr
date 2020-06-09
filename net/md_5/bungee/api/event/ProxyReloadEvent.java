/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class ProxyReloadEvent
extends Event {
    private final CommandSender sender;

    public CommandSender getSender() {
        return this.sender;
    }

    public ProxyReloadEvent(CommandSender sender) {
        this.sender = sender;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProxyReloadEvent)) {
            return false;
        }
        ProxyReloadEvent other = (ProxyReloadEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        CommandSender this$sender = this.getSender();
        CommandSender other$sender = other.getSender();
        if (this$sender == null) {
            if (other$sender == null) return true;
            return false;
        }
        if (this$sender.equals((Object)other$sender)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ProxyReloadEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        CommandSender $sender = this.getSender();
        return result * 59 + ($sender == null ? 43 : $sender.hashCode());
    }
}

