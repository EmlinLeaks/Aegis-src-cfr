/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.DecoderException;

public class DecompressionException
extends DecoderException {
    private static final long serialVersionUID = 3546272712208105199L;

    public DecompressionException() {
    }

    public DecompressionException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public DecompressionException(String message) {
        super((String)message);
    }

    public DecompressionException(Throwable cause) {
        super((Throwable)cause);
    }
}

