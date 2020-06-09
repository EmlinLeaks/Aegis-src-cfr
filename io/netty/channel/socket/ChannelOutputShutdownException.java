/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket;

import java.io.IOException;

public final class ChannelOutputShutdownException
extends IOException {
    private static final long serialVersionUID = 6712549938359321378L;

    public ChannelOutputShutdownException(String msg) {
        super((String)msg);
    }

    public ChannelOutputShutdownException(String msg, Throwable cause) {
        super((String)msg, (Throwable)cause);
    }
}

