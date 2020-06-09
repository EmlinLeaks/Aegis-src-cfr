/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import net.md_5.bungee.api.CommandSender;

public abstract class Command {
    private final String name;
    private final String permission;
    private final String[] aliases;

    public Command(String name) {
        this((String)name, null, (String[])new String[0]);
    }

    public Command(String name, String permission, String ... aliases) {
        Preconditions.checkArgument((boolean)(name != null), (Object)"name");
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public abstract void execute(CommandSender var1, String[] var2);

    public boolean hasPermission(CommandSender sender) {
        if (this.permission == null) return true;
        if (this.permission.isEmpty()) return true;
        if (sender.hasPermission((String)this.permission)) return true;
        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Command)) {
            return false;
        }
        Command other = (Command)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$permission = this.getPermission();
        String other$permission = other.getPermission();
        if (this$permission == null ? other$permission != null : !this$permission.equals((Object)other$permission)) {
            return false;
        }
        if (Arrays.deepEquals((Object[])this.getAliases(), (Object[])other.getAliases())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Command;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $permission = this.getPermission();
        result = result * 59 + ($permission == null ? 43 : $permission.hashCode());
        return result * 59 + Arrays.deepHashCode((Object[])this.getAliases());
    }

    public String toString() {
        return "Command(name=" + this.getName() + ", permission=" + this.getPermission() + ", aliases=" + Arrays.deepToString((Object[])this.getAliases()) + ")";
    }
}

