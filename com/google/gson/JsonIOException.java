/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonParseException;

public final class JsonIOException
extends JsonParseException {
    private static final long serialVersionUID = 1L;

    public JsonIOException(String msg) {
        super((String)msg);
    }

    public JsonIOException(String msg, Throwable cause) {
        super((String)msg, (Throwable)cause);
    }

    public JsonIOException(Throwable cause) {
        super((Throwable)cause);
    }
}

