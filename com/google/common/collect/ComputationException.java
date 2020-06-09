/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public class ComputationException
extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public ComputationException(@Nullable Throwable cause) {
        super((Throwable)cause);
    }
}

