/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

public final class $Gson$Preconditions {
    private $Gson$Preconditions() {
        throw new UnsupportedOperationException();
    }

    public static <T> T checkNotNull(T obj) {
        if (obj != null) return (T)obj;
        throw new NullPointerException();
    }

    public static void checkArgument(boolean condition) {
        if (condition) return;
        throw new IllegalArgumentException();
    }
}

