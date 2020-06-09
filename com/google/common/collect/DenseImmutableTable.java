/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.DenseImmutableTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.Table;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class DenseImmutableTable<R, C, V>
extends RegularImmutableTable<R, C, V> {
    private final ImmutableMap<R, Integer> rowKeyToIndex;
    private final ImmutableMap<C, Integer> columnKeyToIndex;
    private final ImmutableMap<R, Map<C, V>> rowMap;
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] rowCounts;
    private final int[] columnCounts;
    private final V[][] values;
    private final int[] cellRowIndices;
    private final int[] cellColumnIndices;

    DenseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace) {
        Object[][] array = new Object[rowSpace.size()][columnSpace.size()];
        this.values = array;
        this.rowKeyToIndex = Maps.indexMap(rowSpace);
        this.columnKeyToIndex = Maps.indexMap(columnSpace);
        this.rowCounts = new int[this.rowKeyToIndex.size()];
        this.columnCounts = new int[this.columnKeyToIndex.size()];
        int[] cellRowIndices = new int[cellList.size()];
        int[] cellColumnIndices = new int[cellList.size()];
        int i = 0;
        do {
            int columnIndex;
            if (i >= cellList.size()) {
                this.cellRowIndices = cellRowIndices;
                this.cellColumnIndices = cellColumnIndices;
                this.rowMap = new RowMap((DenseImmutableTable)this, null);
                this.columnMap = new ColumnMap((DenseImmutableTable)this, null);
                return;
            }
            Table.Cell cell = (Table.Cell)cellList.get((int)i);
            R rowKey = cell.getRowKey();
            C columnKey = cell.getColumnKey();
            int rowIndex = this.rowKeyToIndex.get(rowKey).intValue();
            V existingValue = this.values[rowIndex][columnIndex = this.columnKeyToIndex.get(columnKey).intValue()];
            Preconditions.checkArgument((boolean)(existingValue == null), (String)"duplicate key: (%s, %s)", rowKey, columnKey);
            this.values[rowIndex][columnIndex] = cell.getValue();
            int[] arrn = this.rowCounts;
            int n = rowIndex;
            arrn[n] = arrn[n] + 1;
            int[] arrn2 = this.columnCounts;
            int n2 = columnIndex;
            arrn2[n2] = arrn2[n2] + 1;
            cellRowIndices[i] = rowIndex;
            cellColumnIndices[i] = columnIndex;
            ++i;
        } while (true);
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        V v;
        Integer rowIndex = this.rowKeyToIndex.get((Object)rowKey);
        Integer columnIndex = this.columnKeyToIndex.get((Object)columnKey);
        if (rowIndex != null && columnIndex != null) {
            v = (V)this.values[rowIndex.intValue()][columnIndex.intValue()];
            return (V)v;
        }
        v = null;
        return (V)((V)v);
    }

    @Override
    public int size() {
        return this.cellRowIndices.length;
    }

    @Override
    Table.Cell<R, C, V> getCell(int index) {
        int rowIndex = this.cellRowIndices[index];
        int columnIndex = this.cellColumnIndices[index];
        E rowKey = ((ImmutableSet)this.rowKeySet()).asList().get((int)rowIndex);
        E columnKey = ((ImmutableSet)this.columnKeySet()).asList().get((int)columnIndex);
        V value = this.values[rowIndex][columnIndex];
        return DenseImmutableTable.cellOf(rowKey, columnKey, value);
    }

    @Override
    V getValue(int index) {
        return (V)this.values[this.cellRowIndices[index]][this.cellColumnIndices[index]];
    }

    @Override
    ImmutableTable.SerializedForm createSerializedForm() {
        return ImmutableTable.SerializedForm.create(this, (int[])this.cellRowIndices, (int[])this.cellColumnIndices);
    }

    static /* synthetic */ int[] access$200(DenseImmutableTable x0) {
        return x0.rowCounts;
    }

    static /* synthetic */ ImmutableMap access$300(DenseImmutableTable x0) {
        return x0.columnKeyToIndex;
    }

    static /* synthetic */ Object[][] access$400(DenseImmutableTable x0) {
        return x0.values;
    }

    static /* synthetic */ int[] access$500(DenseImmutableTable x0) {
        return x0.columnCounts;
    }

    static /* synthetic */ ImmutableMap access$600(DenseImmutableTable x0) {
        return x0.rowKeyToIndex;
    }
}

