/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.GwtTransient;
import com.google.common.collect.Maps;
import com.google.common.collect.StandardTable;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class StandardTable<R, C, V>
extends AbstractTable<R, C, V>
implements Serializable {
    @GwtTransient
    final Map<R, Map<C, V>> backingMap;
    @GwtTransient
    final Supplier<? extends Map<C, V>> factory;
    private transient Set<C> columnKeySet;
    private transient Map<R, Map<C, V>> rowMap;
    private transient StandardTable<R, C, V> columnMap;
    private static final long serialVersionUID = 0L;

    StandardTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
        this.backingMap = backingMap;
        this.factory = factory;
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (rowKey == null) return false;
        if (columnKey == null) return false;
        if (!super.contains((Object)rowKey, (Object)columnKey)) return false;
        return true;
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        Map<C, V> map;
        if (columnKey == null) {
            return false;
        }
        Iterator<Map<C, V>> i$ = this.backingMap.values().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!Maps.safeContainsKey(map = i$.next(), (Object)columnKey));
        return true;
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        if (rowKey == null) return false;
        if (!Maps.safeContainsKey(this.backingMap, (Object)rowKey)) return false;
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        if (value == null) return false;
        if (!super.containsValue((Object)value)) return false;
        return true;
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        V v;
        if (rowKey != null && columnKey != null) {
            v = (V)super.get((Object)rowKey, (Object)columnKey);
            return (V)v;
        }
        v = null;
        return (V)((V)v);
    }

    @Override
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        Iterator<Map<C, V>> i$ = this.backingMap.values().iterator();
        while (i$.hasNext()) {
            Map<C, V> map = i$.next();
            size += map.size();
        }
        return size;
    }

    @Override
    public void clear() {
        this.backingMap.clear();
    }

    private Map<C, V> getOrCreate(R rowKey) {
        Map<C, V> map = this.backingMap.get(rowKey);
        if (map != null) return map;
        map = this.factory.get();
        this.backingMap.put(rowKey, map);
        return map;
    }

    @CanIgnoreReturnValue
    @Override
    public V put(R rowKey, C columnKey, V value) {
        Preconditions.checkNotNull(rowKey);
        Preconditions.checkNotNull(columnKey);
        Preconditions.checkNotNull(value);
        return (V)this.getOrCreate(rowKey).put(columnKey, value);
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (rowKey == null) return (V)null;
        if (columnKey == null) {
            return (V)null;
        }
        Map<C, V> map = Maps.safeGet(this.backingMap, (Object)rowKey);
        if (map == null) {
            return (V)null;
        }
        V value = map.remove((Object)columnKey);
        if (!map.isEmpty()) return (V)value;
        this.backingMap.remove((Object)rowKey);
        return (V)value;
    }

    @CanIgnoreReturnValue
    private Map<R, V> removeColumn(Object column) {
        LinkedHashMap<R, V> output = new LinkedHashMap<R, V>();
        Iterator<Map.Entry<R, Map<C, V>>> iterator = this.backingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<R, Map<C, V>> entry = iterator.next();
            V value = entry.getValue().remove((Object)column);
            if (value == null) continue;
            output.put(entry.getKey(), value);
            if (!entry.getValue().isEmpty()) continue;
            iterator.remove();
        }
        return output;
    }

    private boolean containsMapping(Object rowKey, Object columnKey, Object value) {
        if (value == null) return false;
        if (!value.equals(this.get((Object)rowKey, (Object)columnKey))) return false;
        return true;
    }

    private boolean removeMapping(Object rowKey, Object columnKey, Object value) {
        if (!this.containsMapping((Object)rowKey, (Object)columnKey, (Object)value)) return false;
        this.remove((Object)rowKey, (Object)columnKey);
        return true;
    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        return super.cellSet();
    }

    @Override
    Iterator<Table.Cell<R, C, V>> cellIterator() {
        return new CellIterator((StandardTable)this, null);
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return new Row((StandardTable)this, rowKey);
    }

    @Override
    public Map<R, V> column(C columnKey) {
        return new Column((StandardTable)this, columnKey);
    }

    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        Object object;
        Set<C> result = this.columnKeySet;
        if (result == null) {
            object = this.columnKeySet = new ColumnKeySet((StandardTable)this, null);
            return object;
        }
        object = result;
        return object;
    }

    Iterator<C> createColumnKeyIterator() {
        return new ColumnKeyIterator((StandardTable)this, null);
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        Map<R, Map<C, V>> map;
        Map<R, Map<C, V>> result = this.rowMap;
        if (result == null) {
            map = this.rowMap = this.createRowMap();
            return map;
        }
        map = result;
        return map;
    }

    Map<R, Map<C, V>> createRowMap() {
        return new RowMap((StandardTable)this);
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        Object object;
        StandardTable<R, C, V> result = this.columnMap;
        if (result == null) {
            object = this.columnMap = new ColumnMap((StandardTable)this, null);
            return object;
        }
        object = result;
        return object;
    }

    static /* synthetic */ boolean access$300(StandardTable x0, Object x1, Object x2, Object x3) {
        return x0.containsMapping((Object)x1, (Object)x2, (Object)x3);
    }

    static /* synthetic */ boolean access$400(StandardTable x0, Object x1, Object x2, Object x3) {
        return x0.removeMapping((Object)x1, (Object)x2, (Object)x3);
    }

    static /* synthetic */ Map access$900(StandardTable x0, Object x1) {
        return x0.removeColumn((Object)x1);
    }
}

