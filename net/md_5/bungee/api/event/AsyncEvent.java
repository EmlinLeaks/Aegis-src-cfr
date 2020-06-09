/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

public class AsyncEvent<T>
extends Event {
    private final Callback<T> done;
    private final Map<Plugin, AtomicInteger> intents = new ConcurrentHashMap<Plugin, AtomicInteger>();
    private final AtomicBoolean fired = new AtomicBoolean();
    private final AtomicInteger latch = new AtomicInteger();

    @Override
    public void postCall() {
        if (this.latch.get() == 0) {
            this.done.done(this, null);
        }
        this.fired.set((boolean)true);
    }

    public void registerIntent(Plugin plugin) {
        Preconditions.checkState((boolean)(!this.fired.get()), (String)"Event %s has already been fired", (Object)this);
        AtomicInteger intentCount = this.intents.get((Object)plugin);
        if (intentCount == null) {
            this.intents.put((Plugin)plugin, (AtomicInteger)new AtomicInteger((int)1));
        } else {
            intentCount.incrementAndGet();
        }
        this.latch.incrementAndGet();
    }

    public void completeIntent(Plugin plugin) {
        AtomicInteger intentCount = this.intents.get((Object)plugin);
        Preconditions.checkState((boolean)(intentCount != null && intentCount.get() > 0), (String)"Plugin %s has not registered intents for event %s", (Object)plugin, (Object)this);
        intentCount.decrementAndGet();
        if (this.fired.get()) {
            if (this.latch.decrementAndGet() != 0) return;
            this.done.done(this, null);
            return;
        }
        this.latch.decrementAndGet();
    }

    public AsyncEvent(Callback<T> done) {
        this.done = done;
    }

    public String toString() {
        return "AsyncEvent(super=" + Object.super.toString() + ", done=" + this.done + ", intents=" + this.intents + ", fired=" + this.fired + ", latch=" + this.latch + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AsyncEvent)) {
            return false;
        }
        AsyncEvent other = (AsyncEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!Object.super.equals((Object)o)) {
            return false;
        }
        Callback<T> this$done = this.done;
        Callback<T> other$done = other.done;
        if (this$done == null ? other$done != null : !this$done.equals(other$done)) {
            return false;
        }
        Map<Plugin, AtomicInteger> this$intents = this.intents;
        Map<Plugin, AtomicInteger> other$intents = other.intents;
        if (this$intents == null ? other$intents != null : !((Object)this$intents).equals(other$intents)) {
            return false;
        }
        AtomicBoolean this$fired = this.fired;
        AtomicBoolean other$fired = other.fired;
        if (this$fired == null ? other$fired != null : !this$fired.equals((Object)other$fired)) {
            return false;
        }
        AtomicInteger this$latch = this.latch;
        AtomicInteger other$latch = other.latch;
        if (this$latch == null) {
            if (other$latch == null) return true;
            return false;
        }
        if (this$latch.equals((Object)other$latch)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof AsyncEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = Object.super.hashCode();
        Callback<T> $done = this.done;
        result = result * 59 + ($done == null ? 43 : $done.hashCode());
        Map<Plugin, AtomicInteger> $intents = this.intents;
        result = result * 59 + ($intents == null ? 43 : ((Object)$intents).hashCode());
        AtomicBoolean $fired = this.fired;
        result = result * 59 + ($fired == null ? 43 : $fired.hashCode());
        AtomicInteger $latch = this.latch;
        return result * 59 + ($latch == null ? 43 : $latch.hashCode());
    }
}

