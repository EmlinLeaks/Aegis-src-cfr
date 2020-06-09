/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.stream;

import java.io.IOException;

public final class MalformedJsonException
extends IOException {
    private static final long serialVersionUID = 1L;

    public MalformedJsonException(String msg) {
        super((String)msg);
    }

    public MalformedJsonException(String msg, Throwable throwable) {
        super((String)msg);
        this.initCause((Throwable)throwable);
    }

    public MalformedJsonException(Throwable throwable) {
        this.initCause((Throwable)throwable);
    }
}

