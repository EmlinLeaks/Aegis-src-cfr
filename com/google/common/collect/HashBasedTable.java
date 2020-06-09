/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.StandardTable;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
public class HashBasedTable<R, C, V>
extends StandardTable<R, C, V> {
    private static final long serialVersionUID = 0L;

    public static <R, C, V> HashBasedTable<R, C, V> create() {
        return new HashBasedTable<K, C, V>(new LinkedHashMap<K, V>(), new Factory<C, V>((int)0));
    }

    public static <R, C, V> HashBasedTable<R, C, V> create(int expectedRows, int expectedCellsPerRow) {
        CollectPreconditions.checkNonnegative((int)expectedCellsPerRow, (String)"expectedCellsPerRow");
        LinkedHashMap<K, V> backingMap = Maps.newLinkedHashMapWithExpectedSize((int)expectedRows);
        return new HashBasedTable<K, C, V>(backingMap, new Factory<C, V>((int)expectedCellsPerRow));
    }

    public static <R, C, V> HashBasedTable<R, C, V> create(Table<? extends R, ? extends C, ? extends V> table) {
        HashBasedTable<R, C, V> result = HashBasedTable.create();
        result.putAll(table);
        return result;
    }

    HashBasedTable(Map<R, Map<C, V>> backingMap, Factory<C, V> factory) {
        super(backingMap, factory);
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        return super.contains((Object)rowKey, (Object)columnKey);
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        return super.containsColumn((Object)columnKey);
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        return super.containsRow((Object)rowKey);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return super.containsValue((Object)value);
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        return (V)super.get((Object)rowKey, (Object)columnKey);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals((Object)obj);
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        return (V)super.remove((Object)rowKey, (Object)columnKey);
    }
}

