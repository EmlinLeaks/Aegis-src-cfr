/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteEvent
extends TargetedEvent
implements Cancellable {
    private boolean cancelled;
    private final String cursor;
    private final List<String> suggestions;

    public TabCompleteEvent(Connection sender, Connection receiver, String cursor, List<String> suggestions) {
        super((Connection)sender, (Connection)receiver);
        this.cursor = cursor;
        this.suggestions = suggestions;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getCursor() {
        return this.cursor;
    }

    public List<String> getSuggestions() {
        return this.suggestions;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "TabCompleteEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", cursor=" + this.getCursor() + ", suggestions=" + this.getSuggestions() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabCompleteEvent)) {
            return false;
        }
        TabCompleteEvent other = (TabCompleteEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        String this$cursor = this.getCursor();
        String other$cursor = other.getCursor();
        if (this$cursor == null ? other$cursor != null : !this$cursor.equals((Object)other$cursor)) {
            return false;
        }
        List<String> this$suggestions = this.getSuggestions();
        List<String> other$suggestions = other.getSuggestions();
        if (this$suggestions == null) {
            if (other$suggestions == null) return true;
            return false;
        }
        if (((Object)this$suggestions).equals(other$suggestions)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof TabCompleteEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        String $cursor = this.getCursor();
        result = result * 59 + ($cursor == null ? 43 : $cursor.hashCode());
        List<String> $suggestions = this.getSuggestions();
        return result * 59 + ($suggestions == null ? 43 : ((Object)$suggestions).hashCode());
    }
}

