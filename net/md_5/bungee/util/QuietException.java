/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

public class QuietException
extends RuntimeException {
    public QuietException(String message) {
        super((String)message);
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

