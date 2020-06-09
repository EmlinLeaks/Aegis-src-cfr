/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DecoderException;

public class CorruptedFrameException
extends DecoderException {
    private static final long serialVersionUID = 3918052232492988408L;

    public CorruptedFrameException() {
    }

    public CorruptedFrameException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public CorruptedFrameException(String message) {
        super((String)message);
    }

    public CorruptedFrameException(Throwable cause) {
        super((Throwable)cause);
    }
}

