/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.SingletonImmutableTable;
import com.google.common.collect.SparseImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ImmutableTable<R, C, V>
extends AbstractTable<R, C, V>
implements Serializable {
    public static <R, C, V> ImmutableTable<R, C, V> of() {
        return SparseImmutableTable.EMPTY;
    }

    public static <R, C, V> ImmutableTable<R, C, V> of(R rowKey, C columnKey, V value) {
        return new SingletonImmutableTable<R, C, V>(rowKey, columnKey, value);
    }

    public static <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> table) {
        if (table instanceof ImmutableTable) {
            return (ImmutableTable)table;
        }
        int size = table.size();
        switch (size) {
            case 0: {
                return ImmutableTable.of();
            }
            case 1: {
                Table.Cell<R, C, V> onlyCell = Iterables.getOnlyElement(table.cellSet());
                return ImmutableTable.of(onlyCell.getRowKey(), onlyCell.getColumnKey(), onlyCell.getValue());
            }
        }
        ImmutableSet.Builder<E> cellSetBuilder = new ImmutableSet.Builder<E>((int)size);
        Iterator<Table.Cell<R, C, V>> i$ = table.cellSet().iterator();
        while (i$.hasNext()) {
            Table.Cell<R, C, V> cell = i$.next();
            cellSetBuilder.add(ImmutableTable.cellOf(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
        }
        return RegularImmutableTable.forCells(cellSetBuilder.build());
    }

    public static <R, C, V> Builder<R, C, V> builder() {
        return new Builder<R, C, V>();
    }

    static <R, C, V> Table.Cell<R, C, V> cellOf(R rowKey, C columnKey, V value) {
        return Tables.immutableCell(Preconditions.checkNotNull(rowKey), Preconditions.checkNotNull(columnKey), Preconditions.checkNotNull(value));
    }

    ImmutableTable() {
    }

    @Override
    public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
        return (ImmutableSet)super.cellSet();
    }

    @Override
    abstract ImmutableSet<Table.Cell<R, C, V>> createCellSet();

    @Override
    final UnmodifiableIterator<Table.Cell<R, C, V>> cellIterator() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection)super.values();
    }

    @Override
    abstract ImmutableCollection<V> createValues();

    @Override
    final Iterator<V> valuesIterator() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    public ImmutableMap<R, V> column(C columnKey) {
        Preconditions.checkNotNull(columnKey);
        return MoreObjects.firstNonNull((ImmutableMap)((ImmutableMap)this.columnMap()).get(columnKey), ImmutableMap.<K, V>of());
    }

    @Override
    public ImmutableSet<C> columnKeySet() {
        return ((ImmutableMap)this.columnMap()).keySet();
    }

    @Override
    public abstract ImmutableMap<C, Map<R, V>> columnMap();

    @Override
    public ImmutableMap<C, V> row(R rowKey) {
        Preconditions.checkNotNull(rowKey);
        return MoreObjects.firstNonNull((ImmutableMap)((ImmutableMap)this.rowMap()).get(rowKey), ImmutableMap.<K, V>of());
    }

    @Override
    public ImmutableSet<R> rowKeySet() {
        return ((ImmutableMap)this.rowMap()).keySet();
    }

    @Override
    public abstract ImmutableMap<R, Map<C, V>> rowMap();

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (this.get((Object)rowKey, (Object)columnKey) == null) return false;
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return ((ImmutableCollection)this.values()).contains((Object)value);
    }

    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V put(R rowKey, C columnKey, V value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V remove(Object rowKey, Object columnKey) {
        throw new UnsupportedOperationException();
    }

    abstract SerializedForm createSerializedForm();

    final Object writeReplace() {
        return this.createSerializedForm();
    }
}

