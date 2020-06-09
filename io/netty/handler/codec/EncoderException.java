/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class EncoderException
extends CodecException {
    private static final long serialVersionUID = -5086121160476476774L;

    public EncoderException() {
    }

    public EncoderException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public EncoderException(String message) {
        super((String)message);
    }

    public EncoderException(Throwable cause) {
        super((Throwable)cause);
    }
}

