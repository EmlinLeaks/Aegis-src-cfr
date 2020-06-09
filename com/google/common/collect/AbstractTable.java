/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TransformedIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTable<R, C, V>
implements Table<R, C, V> {
    private transient Set<Table.Cell<R, C, V>> cellSet;
    private transient Collection<V> values;

    AbstractTable() {
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        return Maps.safeContainsKey(this.rowMap(), (Object)rowKey);
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        return Maps.safeContainsKey(this.columnMap(), (Object)columnKey);
    }

    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return this.columnMap().keySet();
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        Map<C, V> row;
        Iterator<Map<C, V>> i$ = this.rowMap().values().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!(row = i$.next()).containsValue((Object)value));
        return true;
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        Map<C, V> row = Maps.safeGet(this.rowMap(), (Object)rowKey);
        if (row == null) return false;
        if (!Maps.safeContainsKey(row, (Object)columnKey)) return false;
        return true;
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        V v;
        Map<C, V> row = Maps.safeGet(this.rowMap(), (Object)rowKey);
        if (row == null) {
            v = null;
            return (V)((V)v);
        }
        v = (V)Maps.safeGet(row, (Object)columnKey);
        return (V)v;
    }

    @Override
    public boolean isEmpty() {
        if (this.size() != 0) return false;
        return true;
    }

    @Override
    public void clear() {
        Iterators.clear(this.cellSet().iterator());
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        V v;
        Map<C, V> row = Maps.safeGet(this.rowMap(), (Object)rowKey);
        if (row == null) {
            v = null;
            return (V)((V)v);
        }
        v = (V)Maps.safeRemove(row, (Object)columnKey);
        return (V)v;
    }

    @CanIgnoreReturnValue
    @Override
    public V put(R rowKey, C columnKey, V value) {
        return (V)this.row(rowKey).put(columnKey, value);
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        Iterator<Table.Cell<R, C, V>> i$ = table.cellSet().iterator();
        while (i$.hasNext()) {
            Table.Cell<R, C, V> cell = i$.next();
            this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        Set<Table.Cell<R, C, V>> set;
        Set<Table.Cell<R, C, V>> result = this.cellSet;
        if (result == null) {
            set = this.cellSet = this.createCellSet();
            return set;
        }
        set = result;
        return set;
    }

    Set<Table.Cell<R, C, V>> createCellSet() {
        return new CellSet((AbstractTable)this);
    }

    abstract Iterator<Table.Cell<R, C, V>> cellIterator();

    @Override
    public Collection<V> values() {
        Collection<V> collection;
        Collection<V> result = this.values;
        if (result == null) {
            collection = this.values = this.createValues();
            return collection;
        }
        collection = result;
        return collection;
    }

    Collection<V> createValues() {
        return new Values((AbstractTable)this);
    }

    Iterator<V> valuesIterator() {
        return new TransformedIterator<Table.Cell<R, C, V>, V>((AbstractTable)this, this.cellSet().iterator()){
            final /* synthetic */ AbstractTable this$0;
            {
                this.this$0 = abstractTable;
                super(x0);
            }

            V transform(Table.Cell<R, C, V> cell) {
                return (V)cell.getValue();
            }
        };
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return Tables.equalsImpl(this, (Object)obj);
    }

    @Override
    public int hashCode() {
        return this.cellSet().hashCode();
    }

    public String toString() {
        return this.rowMap().toString();
    }
}

