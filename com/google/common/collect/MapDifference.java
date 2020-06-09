/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.MapDifference;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public interface MapDifference<K, V> {
    public boolean areEqual();

    public Map<K, V> entriesOnlyOnLeft();

    public Map<K, V> entriesOnlyOnRight();

    public Map<K, V> entriesInCommon();

    public Map<K, ValueDifference<V>> entriesDiffering();

    public boolean equals(@Nullable Object var1);

    public int hashCode();
}

