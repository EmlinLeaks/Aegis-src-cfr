/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.SslCompletionEvent;

public final class SniCompletionEvent
extends SslCompletionEvent {
    private final String hostname;

    SniCompletionEvent(String hostname) {
        this.hostname = hostname;
    }

    SniCompletionEvent(String hostname, Throwable cause) {
        super((Throwable)cause);
        this.hostname = hostname;
    }

    SniCompletionEvent(Throwable cause) {
        this(null, (Throwable)cause);
    }

    public String hostname() {
        return this.hostname;
    }

    @Override
    public String toString() {
        String string;
        Throwable cause = this.cause();
        if (cause == null) {
            string = this.getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")";
            return string;
        }
        string = this.getClass().getSimpleName() + '(' + cause + ')';
        return string;
    }
}

