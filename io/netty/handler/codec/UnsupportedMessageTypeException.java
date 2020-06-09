/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.CodecException;

public class UnsupportedMessageTypeException
extends CodecException {
    private static final long serialVersionUID = 2799598826487038726L;

    public UnsupportedMessageTypeException(Object message, Class<?> ... expectedTypes) {
        super((String)UnsupportedMessageTypeException.message((String)(message == null ? "null" : message.getClass().getName()), expectedTypes));
    }

    public UnsupportedMessageTypeException() {
    }

    public UnsupportedMessageTypeException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public UnsupportedMessageTypeException(String s) {
        super((String)s);
    }

    public UnsupportedMessageTypeException(Throwable cause) {
        super((Throwable)cause);
    }

    private static String message(String actualType, Class<?> ... expectedTypes) {
        Class<?> t;
        StringBuilder buf = new StringBuilder((String)actualType);
        if (expectedTypes == null) return buf.toString();
        if (expectedTypes.length <= 0) return buf.toString();
        buf.append((String)" (expected: ").append((String)expectedTypes[0].getName());
        for (int i = 1; i < expectedTypes.length && (t = expectedTypes[i]) != null; ++i) {
            buf.append((String)", ").append((String)t.getName());
        }
        buf.append((char)')');
        return buf.toString();
    }
}

