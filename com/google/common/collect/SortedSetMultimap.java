/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public interface SortedSetMultimap<K, V>
extends SetMultimap<K, V> {
    @Override
    public SortedSet<V> get(@Nullable K var1);

    @CanIgnoreReturnValue
    @Override
    public SortedSet<V> removeAll(@Nullable Object var1);

    @CanIgnoreReturnValue
    @Override
    public SortedSet<V> replaceValues(K var1, Iterable<? extends V> var2);

    @Override
    public Map<K, Collection<V>> asMap();

    public Comparator<? super V> valueComparator();
}

