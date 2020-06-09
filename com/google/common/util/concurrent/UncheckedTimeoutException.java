/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
public class UncheckedTimeoutException
extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public UncheckedTimeoutException() {
    }

    public UncheckedTimeoutException(@Nullable String message) {
        super((String)message);
    }

    public UncheckedTimeoutException(@Nullable Throwable cause) {
        super((Throwable)cause);
    }

    public UncheckedTimeoutException(@Nullable String message, @Nullable Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

