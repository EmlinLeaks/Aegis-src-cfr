/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.EncoderException;

public class CompressionException
extends EncoderException {
    private static final long serialVersionUID = 5603413481274811897L;

    public CompressionException() {
    }

    public CompressionException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public CompressionException(String message) {
        super((String)message);
    }

    public CompressionException(Throwable cause) {
        super((Throwable)cause);
    }
}

