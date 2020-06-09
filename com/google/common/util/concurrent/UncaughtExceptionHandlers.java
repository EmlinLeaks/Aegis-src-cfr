/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.UncaughtExceptionHandlers;

@GwtIncompatible
public final class UncaughtExceptionHandlers {
    private UncaughtExceptionHandlers() {
    }

    public static Thread.UncaughtExceptionHandler systemExit() {
        return new Exiter((Runtime)Runtime.getRuntime());
    }
}

