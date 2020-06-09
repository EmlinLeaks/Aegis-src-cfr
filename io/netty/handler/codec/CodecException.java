/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

public class CodecException
extends RuntimeException {
    private static final long serialVersionUID = -1464830400709348473L;

    public CodecException() {
    }

    public CodecException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public CodecException(String message) {
        super((String)message);
    }

    public CodecException(Throwable cause) {
        super((Throwable)cause);
    }
}

