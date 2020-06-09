/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class PrematureChannelClosureException
extends CodecException {
    private static final long serialVersionUID = 4907642202594703094L;

    public PrematureChannelClosureException() {
    }

    public PrematureChannelClosureException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public PrematureChannelClosureException(String message) {
        super((String)message);
    }

    public PrematureChannelClosureException(Throwable cause) {
        super((Throwable)cause);
    }
}

