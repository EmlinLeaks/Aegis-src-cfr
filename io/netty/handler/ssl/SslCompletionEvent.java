/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;

public abstract class SslCompletionEvent {
    private final Throwable cause;

    SslCompletionEvent() {
        this.cause = null;
    }

    SslCompletionEvent(Throwable cause) {
        this.cause = ObjectUtil.checkNotNull(cause, (String)"cause");
    }

    public final boolean isSuccess() {
        if (this.cause != null) return false;
        return true;
    }

    public final Throwable cause() {
        return this.cause;
    }

    public String toString() {
        String string;
        Throwable cause = this.cause();
        if (cause == null) {
            string = this.getClass().getSimpleName() + "(SUCCESS)";
            return string;
        }
        string = this.getClass().getSimpleName() + '(' + cause + ')';
        return string;
    }
}

