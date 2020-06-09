/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.escape.Platform;

@GwtCompatible(emulated=true)
final class Platform {
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>(){

        protected char[] initialValue() {
            return new char[1024];
        }
    };

    private Platform() {
    }

    static char[] charBufferFromThreadLocal() {
        return DEST_TL.get();
    }
}

