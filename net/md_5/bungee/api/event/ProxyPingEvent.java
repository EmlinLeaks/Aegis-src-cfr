/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.event;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.AsyncEvent;

public class ProxyPingEvent
extends AsyncEvent<ProxyPingEvent> {
    private final PendingConnection connection;
    private ServerPing response;

    public ProxyPingEvent(PendingConnection connection, ServerPing response, Callback<ProxyPingEvent> done) {
        super(done);
        this.connection = connection;
        this.response = response;
    }

    public PendingConnection getConnection() {
        return this.connection;
    }

    public ServerPing getResponse() {
        return this.response;
    }

    public void setResponse(ServerPing response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ProxyPingEvent(connection=" + this.getConnection() + ", response=" + this.getResponse() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProxyPingEvent)) {
            return false;
        }
        ProxyPingEvent other = (ProxyPingEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        PendingConnection this$connection = this.getConnection();
        PendingConnection other$connection = other.getConnection();
        if (this$connection == null ? other$connection != null : !this$connection.equals((Object)other$connection)) {
            return false;
        }
        ServerPing this$response = this.getResponse();
        ServerPing other$response = other.getResponse();
        if (this$response == null) {
            if (other$response == null) return true;
            return false;
        }
        if (((Object)this$response).equals((Object)other$response)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ProxyPingEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        PendingConnection $connection = this.getConnection();
        result = result * 59 + ($connection == null ? 43 : $connection.hashCode());
        ServerPing $response = this.getResponse();
        return result * 59 + ($response == null ? 43 : ((Object)$response).hashCode());
    }
}

