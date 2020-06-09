/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import java.nio.channels.ClosedChannelException;

final class ExtendedClosedChannelException
extends ClosedChannelException {
    ExtendedClosedChannelException(Throwable cause) {
        if (cause == null) return;
        this.initCause((Throwable)cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

