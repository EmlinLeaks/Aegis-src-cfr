/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonParseException;

public final class JsonSyntaxException
extends JsonParseException {
    private static final long serialVersionUID = 1L;

    public JsonSyntaxException(String msg) {
        super((String)msg);
    }

    public JsonSyntaxException(String msg, Throwable cause) {
        super((String)msg, (Throwable)cause);
    }

    public JsonSyntaxException(Throwable cause) {
        super((Throwable)cause);
    }
}

