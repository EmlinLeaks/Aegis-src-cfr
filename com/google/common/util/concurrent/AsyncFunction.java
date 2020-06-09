/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

@GwtCompatible
public interface AsyncFunction<I, O> {
    public ListenableFuture<O> apply(@Nullable I var1) throws Exception;
}

