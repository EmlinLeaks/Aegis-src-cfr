/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Table<R, C, V> {
    public boolean contains(@Nullable Object var1, @Nullable Object var2);

    public boolean containsRow(@Nullable Object var1);

    public boolean containsColumn(@Nullable Object var1);

    public boolean containsValue(@Nullable Object var1);

    public V get(@Nullable Object var1, @Nullable Object var2);

    public boolean isEmpty();

    public int size();

    public boolean equals(@Nullable Object var1);

    public int hashCode();

    public void clear();

    @Nullable
    @CanIgnoreReturnValue
    public V put(R var1, C var2, V var3);

    public void putAll(Table<? extends R, ? extends C, ? extends V> var1);

    @Nullable
    @CanIgnoreReturnValue
    public V remove(@Nullable Object var1, @Nullable Object var2);

    public Map<C, V> row(R var1);

    public Map<R, V> column(C var1);

    public Set<Cell<R, C, V>> cellSet();

    public Set<R> rowKeySet();

    public Set<C> columnKeySet();

    public Collection<V> values();

    public Map<R, Map<C, V>> rowMap();

    public Map<C, Map<R, V>> columnMap();
}

