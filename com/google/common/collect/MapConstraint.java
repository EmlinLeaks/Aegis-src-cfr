/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@Deprecated
@GwtCompatible
@Beta
public interface MapConstraint<K, V> {
    public void checkKeyValue(@Nullable K var1, @Nullable V var2);

    public String toString();
}

