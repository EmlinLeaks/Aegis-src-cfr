/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class Platform {
    static boolean isInstanceOfThrowableClass(@Nullable Throwable t, Class<? extends Throwable> expectedClass) {
        return expectedClass.isInstance((Object)t);
    }

    private Platform() {
    }
}

