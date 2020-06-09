/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

public final class Objects {
    private Objects() {
        throw new UnsupportedOperationException();
    }

    public static void ensureNotNull(Object target) {
        if (target != null) return;
        throw new NullPointerException();
    }
}

