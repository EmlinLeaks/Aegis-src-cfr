/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class DecoderException
extends CodecException {
    private static final long serialVersionUID = 6926716840699621852L;

    public DecoderException() {
    }

    public DecoderException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public DecoderException(String message) {
        super((String)message);
    }

    public DecoderException(Throwable cause) {
        super((Throwable)cause);
    }
}

