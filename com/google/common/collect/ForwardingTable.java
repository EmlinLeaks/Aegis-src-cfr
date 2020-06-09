/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingTable<R, C, V>
extends ForwardingObject
implements Table<R, C, V> {
    protected ForwardingTable() {
    }

    @Override
    protected abstract Table<R, C, V> delegate();

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        return this.delegate().cellSet();
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public Map<R, V> column(C columnKey) {
        return this.delegate().column(columnKey);
    }

    @Override
    public Set<C> columnKeySet() {
        return this.delegate().columnKeySet();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        return this.delegate().columnMap();
    }

    @Override
    public boolean contains(Object rowKey, Object columnKey) {
        return this.delegate().contains((Object)rowKey, (Object)columnKey);
    }

    @Override
    public boolean containsColumn(Object columnKey) {
        return this.delegate().containsColumn((Object)columnKey);
    }

    @Override
    public boolean containsRow(Object rowKey) {
        return this.delegate().containsRow((Object)rowKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate().containsValue((Object)value);
    }

    @Override
    public V get(Object rowKey, Object columnKey) {
        return (V)this.delegate().get((Object)rowKey, (Object)columnKey);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @CanIgnoreReturnValue
    @Override
    public V put(R rowKey, C columnKey, V value) {
        return (V)this.delegate().put(rowKey, columnKey, value);
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        this.delegate().putAll(table);
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(Object rowKey, Object columnKey) {
        return (V)this.delegate().remove((Object)rowKey, (Object)columnKey);
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return this.delegate().row(rowKey);
    }

    @Override
    public Set<R> rowKeySet() {
        return this.delegate().rowKeySet();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return this.delegate().rowMap();
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (this.delegate().equals((Object)obj)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
}

