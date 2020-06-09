/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteResponseEvent
extends TargetedEvent
implements Cancellable {
    private boolean cancelled;
    private final List<String> suggestions;

    public TabCompleteResponseEvent(Connection sender, Connection receiver, List<String> suggestions) {
        super((Connection)sender, (Connection)receiver);
        this.suggestions = suggestions;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
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
        return "TabCompleteResponseEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", suggestions=" + this.getSuggestions() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabCompleteResponseEvent)) {
            return false;
        }
        TabCompleteResponseEvent other = (TabCompleteResponseEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
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
        return other instanceof TabCompleteResponseEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        List<String> $suggestions = this.getSuggestions();
        return result * 59 + ($suggestions == null ? 43 : ((Object)$suggestions).hashCode());
    }
}

