/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.DenseImmutableTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.SparseImmutableTable;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class RegularImmutableTable<R, C, V>
extends ImmutableTable<R, C, V> {
    RegularImmutableTable() {
    }

    abstract Table.Cell<R, C, V> getCell(int var1);

    @Override
    final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
        CellSet cellSet;
        if (this.isEmpty()) {
            cellSet = ImmutableSet.of();
            return cellSet;
        }
        cellSet = new CellSet((RegularImmutableTable)this, null);
        return cellSet;
    }

    abstract V getValue(int var1);

    @Override
    final ImmutableCollection<V> createValues() {
        Values values;
        if (this.isEmpty()) {
            values = ImmutableList.of();
            return values;
        }
        values = new Values((RegularImmutableTable)this, null);
        return values;
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator) {
        Preconditions.checkNotNull(cells);
        if (rowComparator == null) {
            if (columnComparator == null) return RegularImmutableTable.forCellsInternal(cells, rowComparator, columnComparator);
        }
        Comparator<Table.Cell<R, C, V>> comparator = new Comparator<Table.Cell<R, C, V>>(rowComparator, columnComparator){
            final /* synthetic */ Comparator val$rowComparator;
            final /* synthetic */ Comparator val$columnComparator;
            {
                this.val$rowComparator = comparator;
                this.val$columnComparator = comparator2;
            }

            public int compare(Table.Cell<R, C, V> cell1, Table.Cell<R, C, V> cell2) {
                int rowCompare;
                int n = rowCompare = this.val$rowComparator == null ? 0 : this.val$rowComparator.compare(cell1.getRowKey(), cell2.getRowKey());
                if (rowCompare != 0) {
                    return rowCompare;
                }
                if (this.val$columnComparator == null) {
                    return 0;
                }
                int n2 = this.val$columnComparator.compare(cell1.getColumnKey(), cell2.getColumnKey());
                return n2;
            }
        };
        Collections.sort(cells, comparator);
        return RegularImmutableTable.forCellsInternal(cells, rowComparator, columnComparator);
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> cells) {
        return RegularImmutableTable.forCellsInternal(cells, null, null);
    }

    private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator) {
        LinkedHashSet<R> rowSpaceBuilder = new LinkedHashSet<R>();
        LinkedHashSet<C> columnSpaceBuilder = new LinkedHashSet<C>();
        ImmutableList<Table.Cell<R, C, V>> cellList = ImmutableList.copyOf(cells);
        for (Table.Cell<R, C, V> cell : cells) {
            rowSpaceBuilder.add(cell.getRowKey());
            columnSpaceBuilder.add(cell.getColumnKey());
        }
        ImmutableSet<E> rowSpace = rowComparator == null ? ImmutableSet.copyOf(rowSpaceBuilder) : ImmutableSet.copyOf(Ordering.from(rowComparator).immutableSortedCopy(rowSpaceBuilder));
        ImmutableSet<E> columnSpace = columnComparator == null ? ImmutableSet.copyOf(columnSpaceBuilder) : ImmutableSet.copyOf(Ordering.from(columnComparator).immutableSortedCopy(columnSpaceBuilder));
        return RegularImmutableTable.forOrderedComponents(cellList, rowSpace, columnSpace);
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forOrderedComponents(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace) {
        RegularImmutableTable regularImmutableTable;
        if ((long)cellList.size() > (long)rowSpace.size() * (long)columnSpace.size() / 2L) {
            regularImmutableTable = new DenseImmutableTable<R, C, V>(cellList, rowSpace, columnSpace);
            return regularImmutableTable;
        }
        regularImmutableTable = new SparseImmutableTable<R, C, V>(cellList, rowSpace, columnSpace);
        return regularImmutableTable;
    }
}

