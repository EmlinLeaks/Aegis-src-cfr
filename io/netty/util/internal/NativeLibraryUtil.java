/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

final class NativeLibraryUtil {
    public static void loadLibrary(String libName, boolean absolute) {
        if (absolute) {
            System.load((String)libName);
            return;
        }
        System.loadLibrary((String)libName);
    }

    private NativeLibraryUtil() {
    }
}

