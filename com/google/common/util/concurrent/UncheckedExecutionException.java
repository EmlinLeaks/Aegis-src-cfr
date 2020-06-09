/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class UncheckedExecutionException
extends RuntimeException {
    private static final long serialVersionUID = 0L;

    protected UncheckedExecutionException() {
    }

    protected UncheckedExecutionException(@Nullable String message) {
        super((String)message);
    }

    public UncheckedExecutionException(@Nullable String message, @Nullable Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public UncheckedExecutionException(@Nullable Throwable cause) {
        super((Throwable)cause);
    }
}

