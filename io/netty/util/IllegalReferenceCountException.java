/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

public class IllegalReferenceCountException
extends IllegalStateException {
    private static final long serialVersionUID = -2507492394288153468L;

    public IllegalReferenceCountException() {
    }

    public IllegalReferenceCountException(int refCnt) {
        this((String)("refCnt: " + refCnt));
    }

    public IllegalReferenceCountException(int refCnt, int increment) {
        this((String)("refCnt: " + refCnt + ", " + (increment > 0 ? "increment: " + increment : "decrement: " + -increment)));
    }

    public IllegalReferenceCountException(String message) {
        super((String)message);
    }

    public IllegalReferenceCountException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public IllegalReferenceCountException(Throwable cause) {
        super((Throwable)cause);
    }
}

