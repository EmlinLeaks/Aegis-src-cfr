/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

public class BlockingOperationException
extends IllegalStateException {
    private static final long serialVersionUID = 2462223247762460301L;

    public BlockingOperationException() {
    }

    public BlockingOperationException(String s) {
        super((String)s);
    }

    public BlockingOperationException(Throwable cause) {
        super((Throwable)cause);
    }

    public BlockingOperationException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

