/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.PluginManager;

public class ChatEvent
extends TargetedEvent
implements Cancellable {
    private boolean cancelled;
    private String message;

    public ChatEvent(Connection sender, Connection receiver, String message) {
        super((Connection)sender, (Connection)receiver);
        this.message = message;
    }

    public boolean isCommand() {
        if (this.message.length() <= 0) return false;
        if (this.message.charAt((int)0) != '/') return false;
        return true;
    }

    public boolean isProxyCommand() {
        if (!this.isCommand()) {
            return false;
        }
        int index = this.message.indexOf((String)" ");
        String commandName = index == -1 ? this.message.substring((int)1) : this.message.substring((int)1, (int)index);
        CommandSender sender = this.getSender() instanceof CommandSender ? (CommandSender)((Object)this.getSender()) : null;
        return ProxyServer.getInstance().getPluginManager().isExecutableCommand((String)commandName, (CommandSender)sender);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", message=" + this.getMessage() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChatEvent)) {
            return false;
        }
        ChatEvent other = (ChatEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null) {
            if (other$message == null) return true;
            return false;
        }
        if (this$message.equals((Object)other$message)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ChatEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        String $message = this.getMessage();
        return result * 59 + ($message == null ? 43 : $message.hashCode());
    }
}

