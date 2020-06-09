/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class PreLoginEvent
extends AsyncEvent<PreLoginEvent>
implements Cancellable {
    private boolean cancelled;
    private BaseComponent[] cancelReasonComponents;
    private final PendingConnection connection;

    public PreLoginEvent(PendingConnection connection, Callback<PreLoginEvent> done) {
        super(done);
        this.connection = connection;
    }

    @Deprecated
    public String getCancelReason() {
        return BaseComponent.toLegacyText((BaseComponent[])this.getCancelReasonComponents());
    }

    @Deprecated
    public void setCancelReason(String cancelReason) {
        this.setCancelReason((BaseComponent[])TextComponent.fromLegacyText((String)cancelReason));
    }

    public void setCancelReason(BaseComponent ... cancelReason) {
        this.cancelReasonComponents = cancelReason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public BaseComponent[] getCancelReasonComponents() {
        return this.cancelReasonComponents;
    }

    public PendingConnection getConnection() {
        return this.connection;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "PreLoginEvent(cancelled=" + this.isCancelled() + ", cancelReasonComponents=" + Arrays.deepToString((Object[])this.getCancelReasonComponents()) + ", connection=" + this.getConnection() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PreLoginEvent)) {
            return false;
        }
        PreLoginEvent other = (PreLoginEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isCancelled() != other.isCancelled()) {
            return false;
        }
        if (!Arrays.deepEquals((Object[])this.getCancelReasonComponents(), (Object[])other.getCancelReasonComponents())) {
            return false;
        }
        PendingConnection this$connection = this.getConnection();
        PendingConnection other$connection = other.getConnection();
        if (this$connection == null) {
            if (other$connection == null) return true;
            return false;
        }
        if (this$connection.equals((Object)other$connection)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof PreLoginEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isCancelled() ? 79 : 97);
        result = result * 59 + Arrays.deepHashCode((Object[])this.getCancelReasonComponents());
        PendingConnection $connection = this.getConnection();
        return result * 59 + ($connection == null ? 43 : $connection.hashCode());
    }
}

