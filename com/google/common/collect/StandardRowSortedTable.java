/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.StandardRowSortedTable;
import com.google.common.collect.StandardTable;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
class StandardRowSortedTable<R, C, V>
extends StandardTable<R, C, V>
implements RowSortedTable<R, C, V> {
    private static final long serialVersionUID = 0L;

    StandardRowSortedTable(SortedMap<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
        super(backingMap, factory);
    }

    private SortedMap<R, Map<C, V>> sortedBackingMap() {
        return (SortedMap)this.backingMap;
    }

    @Override
    public SortedSet<R> rowKeySet() {
        return (SortedSet)this.rowMap().keySet();
    }

    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return (SortedMap)super.rowMap();
    }

    @Override
    SortedMap<R, Map<C, V>> createRowMap() {
        return new RowSortedMap((StandardRowSortedTable)this, null);
    }

    static /* synthetic */ SortedMap access$100(StandardRowSortedTable x0) {
        return x0.sortedBackingMap();
    }
}

