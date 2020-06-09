/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DecoderException;

public class TooLongFrameException
extends DecoderException {
    private static final long serialVersionUID = -1995801950698951640L;

    public TooLongFrameException() {
    }

    public TooLongFrameException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public TooLongFrameException(String message) {
        super((String)message);
    }

    public TooLongFrameException(Throwable cause) {
        super((Throwable)cause);
    }
}

