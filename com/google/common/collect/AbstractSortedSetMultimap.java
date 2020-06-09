/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SortedSetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V>
extends AbstractSetMultimap<K, V>
implements SortedSetMultimap<K, V> {
    private static final long serialVersionUID = 430848587173315748L;

    protected AbstractSortedSetMultimap(Map<K, Collection<V>> map) {
        super(map);
    }

    @Override
    abstract SortedSet<V> createCollection();

    @Override
    SortedSet<V> createUnmodifiableEmptyCollection() {
        Comparator<V> comparator = this.valueComparator();
        if (comparator != null) return ImmutableSortedSet.emptySet(this.valueComparator());
        return Collections.unmodifiableSortedSet(this.createCollection());
    }

    @Override
    public SortedSet<V> get(@Nullable K key) {
        return (SortedSet)super.get(key);
    }

    @CanIgnoreReturnValue
    @Override
    public SortedSet<V> removeAll(@Nullable Object key) {
        return (SortedSet)super.removeAll((Object)key);
    }

    @CanIgnoreReturnValue
    @Override
    public SortedSet<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
        return (SortedSet)super.replaceValues(key, values);
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }
}

