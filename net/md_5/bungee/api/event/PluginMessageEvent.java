/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class PluginMessageEvent
extends TargetedEvent
implements Cancellable {
    private boolean cancelled;
    private final String tag;
    private final byte[] data;

    public PluginMessageEvent(Connection sender, Connection receiver, String tag, byte[] data) {
        super((Connection)sender, (Connection)receiver);
        this.tag = tag;
        this.data = data;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getTag() {
        return this.tag;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "PluginMessageEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", tag=" + this.getTag() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginMessageEvent)) {
            return false;
        }
        PluginMessageEvent other = (PluginMessageEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        String this$tag = this.getTag();
        String other$tag = other.getTag();
        if (this$tag == null ? other$tag != null : !this$tag.equals((Object)other$tag)) {
            return false;
        }
        if (Arrays.equals((byte[])this.getData(), (byte[])other.getData())) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof PluginMessageEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        String $tag = this.getTag();
        result = result * 59 + ($tag == null ? 43 : $tag.hashCode());
        return result * 59 + Arrays.hashCode((byte[])this.getData());
    }
}

