/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public class VerifyException
extends RuntimeException {
    public VerifyException() {
    }

    public VerifyException(@Nullable String message) {
        super((String)message);
    }

    public VerifyException(@Nullable Throwable cause) {
        super((Throwable)cause);
    }

    public VerifyException(@Nullable String message, @Nullable Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

