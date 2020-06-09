/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelException;

public class EventLoopException
extends ChannelException {
    private static final long serialVersionUID = -8969100344583703616L;

    public EventLoopException() {
    }

    public EventLoopException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public EventLoopException(String message) {
        super((String)message);
    }

    public EventLoopException(Throwable cause) {
        super((Throwable)cause);
    }
}

