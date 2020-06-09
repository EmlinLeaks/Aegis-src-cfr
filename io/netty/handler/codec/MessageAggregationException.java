/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

public class MessageAggregationException
extends IllegalStateException {
    private static final long serialVersionUID = -1995826182950310255L;

    public MessageAggregationException() {
    }

    public MessageAggregationException(String s) {
        super((String)s);
    }

    public MessageAggregationException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public MessageAggregationException(Throwable cause) {
        super((Throwable)cause);
    }
}

