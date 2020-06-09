/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class PermissionCheckEvent
extends Event {
    private final CommandSender sender;
    private final String permission;
    private boolean hasPermission;

    public boolean hasPermission() {
        return this.hasPermission;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public PermissionCheckEvent(CommandSender sender, String permission, boolean hasPermission) {
        this.sender = sender;
        this.permission = permission;
        this.hasPermission = hasPermission;
    }

    public String toString() {
        return "PermissionCheckEvent(sender=" + this.getSender() + ", permission=" + this.getPermission() + ", hasPermission=" + this.hasPermission + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PermissionCheckEvent)) {
            return false;
        }
        PermissionCheckEvent other = (PermissionCheckEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        CommandSender this$sender = this.getSender();
        CommandSender other$sender = other.getSender();
        if (this$sender == null ? other$sender != null : !this$sender.equals((Object)other$sender)) {
            return false;
        }
        String this$permission = this.getPermission();
        String other$permission = other.getPermission();
        if (this$permission == null ? other$permission != null : !this$permission.equals((Object)other$permission)) {
            return false;
        }
        if (this.hasPermission == other.hasPermission) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PermissionCheckEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        CommandSender $sender = this.getSender();
        result = result * 59 + ($sender == null ? 43 : $sender.hashCode());
        String $permission = this.getPermission();
        result = result * 59 + ($permission == null ? 43 : $permission.hashCode());
        return result * 59 + (this.hasPermission ? 79 : 97);
    }
}

