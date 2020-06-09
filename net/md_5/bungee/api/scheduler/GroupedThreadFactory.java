/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.scheduler;

import java.util.concurrent.ThreadFactory;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;

@Deprecated
public class GroupedThreadFactory
implements ThreadFactory {
    private final ThreadGroup group;

    public GroupedThreadFactory(Plugin plugin, String name) {
        this.group = new BungeeGroup((String)name, null);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread((ThreadGroup)this.group, (Runnable)r);
    }

    public ThreadGroup getGroup() {
        return this.group;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroupedThreadFactory)) {
            return false;
        }
        GroupedThreadFactory other = (GroupedThreadFactory)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        ThreadGroup this$group = this.getGroup();
        ThreadGroup other$group = other.getGroup();
        if (this$group == null) {
            if (other$group == null) return true;
            return false;
        }
        if (this$group.equals((Object)other$group)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof GroupedThreadFactory;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ThreadGroup $group = this.getGroup();
        return result * 59 + ($group == null ? 43 : $group.hashCode());
    }

    public String toString() {
        return "GroupedThreadFactory(group=" + this.getGroup() + ")";
    }
}

