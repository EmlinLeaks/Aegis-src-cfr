/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee.api;

import lombok.NonNull;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class ServerConnectRequest {
    @NonNull
    private final ServerInfo target;
    @NonNull
    private final ServerConnectEvent.Reason reason;
    private final Callback<Result> callback;
    private final int connectTimeout;
    private final boolean retry;

    ServerConnectRequest(@NonNull ServerInfo target, @NonNull ServerConnectEvent.Reason reason, Callback<Result> callback, int connectTimeout, boolean retry) {
        if (target == null) {
            throw new NullPointerException((String)"target is marked non-null but is null");
        }
        if (reason == null) {
            throw new NullPointerException((String)"reason is marked non-null but is null");
        }
        this.target = target;
        this.reason = reason;
        this.callback = callback;
        this.connectTimeout = connectTimeout;
        this.retry = retry;
    }

    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public ServerInfo getTarget() {
        return this.target;
    }

    @NonNull
    public ServerConnectEvent.Reason getReason() {
        return this.reason;
    }

    public Callback<Result> getCallback() {
        return this.callback;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public boolean isRetry() {
        return this.retry;
    }
}

