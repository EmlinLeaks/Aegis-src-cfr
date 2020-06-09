/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(emulated=true)
public final class ArrayTable<R, C, V>
extends AbstractTable<R, C, V>
implements Serializable {
    private final ImmutableList<R> rowList;
    private final ImmutableList<C> columnList;
    private final ImmutableMap<R, Integer> rowKeyToIndex;
    private final ImmutableMap<C, Integer> columnKeyToIndex;
    private final V[][] array;
    private transient ArrayTable<R, C, V> columnMap;
    private transient ArrayTable<R, C, V> rowMap;
    private static final long serialVersionUID = 0L;

    public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys) {
        return new ArrayTable<R, C, V>(rowKeys, columnKeys);
    }

    public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, V> table) {
        ArrayTable<R, C, V> arrayTable;
        if (table instanceof ArrayTable) {
            arrayTable = new ArrayTable<R, C, V>((ArrayTable)table);
            return arrayTable;
        }
        arrayTable = new ArrayTable<R, C, V>(table);
        return arrayTable;
    }

    private ArrayTable(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys) {
        this.rowList = ImmutableList.copyOf(rowKeys);
        this.columnList = ImmutableList.copyOf(columnKeys);
        Preconditions.checkArgument((boolean)(!this.rowList.isEmpty()));
        Preconditions.checkArgument((boolean)(!this.columnList.isEmpty()));
        this.rowKeyToIndex = Maps.indexMap(this.rowList);
        this.columnKeyToIndex = Maps.indexMap(this.columnList);
        Object[][] tmpArray = new Object[this.rowList.size()][this.columnList.size()];
        this.array = tmpArray;
        this.eraseAll();
    }

    private ArrayTable(Table<R, C, V> table) {
        this(table.rowKeySet(), table.columnKeySet());
        this.putAll(table);
    }

    private ArrayTable(ArrayTable<R, C, V> table) {
        this.rowList = table.rowList;
        this.columnList = table.columnList;
        this.rowKeyToIndex = table.rowKeyToIndex;
        this.columnKeyToIndex = table.columnKeyToIndex;
        Object[][] copy = new Object[this.rowList.size()][this.columnList.size()];
        this.array = copy;
        this.eraseAll();
        int i = 0;
        while (i < this.rowList.size()) {
            System.arraycopy(table.array[i], (int)0, (Object)copy[i], (int)0, (int)table.array[i].length);
            ++i;
        }
    }

    public ImmutableList<R> rowKeyList() {
        return this.rowList;
    }

    public ImmutableList<C> columnKeyList() {
        return this.columnList;
    }

    public V at(int rowIndex, int columnIndex) {
        Preconditions.checkElementIndex((int)rowIndex, (int)this.rowList.size());
        Preconditions.checkElementIndex((int)columnIndex, (int)this.columnList.size());
        return (V)this.array[rowIndex][columnIndex];
    }

    @CanIgnoreReturnValue
    public V set(int rowIndex, int columnIndex, @Nullable V value) {
        Preconditions.checkElementIndex((int)rowIndex, (int)this.rowList.size());
        Preconditions.checkElementIndex((int)columnIndex, (int)this.columnList.size());
        V oldValue = this.array[rowIndex][columnIndex];
        this.array[rowIndex][columnIndex] = value;
        return (V)oldValue;
    }

    @GwtIncompatible
    public V[][] toArray(Class<V> valueClass) {
        Object[][] copy = (Object[][])Array.newInstance(valueClass, (int[])new int[]{this.rowList.size(), this.columnList.size()});
        int i = 0;
        while (i < this.rowList.size()) {
            System.arraycopy(this.array[i], (int)0, (Object)copy[i], (int)0, (int)this.array[i].length);
            ++i;
        }
        return copy;
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void eraseAll() {
        V[][] arr$ = this.array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Object[] row = arr$[i$];
            Arrays.fill((Object[])row, null);
            ++i$;
        }
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        if (!this.containsRow((Object)rowKey)) return false;
        if (!this.containsColumn((Object)columnKey)) return false;
        return true;
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        return this.columnKeyToIndex.containsKey((Object)columnKey);
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        return this.rowKeyToIndex.containsKey((Object)rowKey);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        V[][] arr$ = this.array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            V[] row;
            for (V element : row = arr$[i$]) {
                if (!Objects.equal((Object)value, element)) continue;
                return true;
            }
            ++i$;
        }
        return false;
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        V v;
        Integer rowIndex = this.rowKeyToIndex.get((Object)rowKey);
        Integer columnIndex = this.columnKeyToIndex.get((Object)columnKey);
        if (rowIndex != null && columnIndex != null) {
            v = (V)this.at((int)rowIndex.intValue(), (int)columnIndex.intValue());
            return (V)v;
        }
        v = null;
        return (V)((V)v);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @CanIgnoreReturnValue
    @Override
    public V put(R rowKey, C columnKey, @Nullable V value) {
        Preconditions.checkNotNull(rowKey);
        Preconditions.checkNotNull(columnKey);
        Integer rowIndex = this.rowKeyToIndex.get(rowKey);
        Preconditions.checkArgument((boolean)(rowIndex != null), (String)"Row %s not in %s", rowKey, this.rowList);
        Integer columnIndex = this.columnKeyToIndex.get(columnKey);
        Preconditions.checkArgument((boolean)(columnIndex != null), (String)"Column %s not in %s", columnKey, this.columnList);
        return (V)this.set((int)rowIndex.intValue(), (int)columnIndex.intValue(), value);
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        super.putAll(table);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public V remove(Object rowKey, Object columnKey) {
        throw new UnsupportedOperationException();
    }

    @CanIgnoreReturnValue
    public V erase(@Nullable Object rowKey, @Nullable Object columnKey) {
        Integer rowIndex = this.rowKeyToIndex.get((Object)rowKey);
        Integer columnIndex = this.columnKeyToIndex.get((Object)columnKey);
        if (rowIndex == null) return (V)null;
        if (columnIndex != null) return (V)this.set((int)rowIndex.intValue(), (int)columnIndex.intValue(), null);
        return (V)null;
    }

    @Override
    public int size() {
        return this.rowList.size() * this.columnList.size();
    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        return super.cellSet();
    }

    @Override
    Iterator<Table.Cell<R, C, V>> cellIterator() {
        return new AbstractIndexedListIterator<Table.Cell<R, C, V>>((ArrayTable)this, (int)this.size()){
            final /* synthetic */ ArrayTable this$0;
            {
                this.this$0 = arrayTable;
                super((int)x0);
            }

            protected Table.Cell<R, C, V> get(int index) {
                return new com.google.common.collect.Tables$AbstractCell<R, C, V>(this, (int)index){
                    final int rowIndex;
                    final int columnIndex;
                    final /* synthetic */ int val$index;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$index = n;
                        this.rowIndex = this.val$index / ArrayTable.access$000((ArrayTable)this.this$1.this$0).size();
                        this.columnIndex = this.val$index % ArrayTable.access$000((ArrayTable)this.this$1.this$0).size();
                    }

                    public R getRowKey() {
                        return (R)ArrayTable.access$100((ArrayTable)this.this$1.this$0).get((int)this.rowIndex);
                    }

                    public C getColumnKey() {
                        return (C)ArrayTable.access$000((ArrayTable)this.this$1.this$0).get((int)this.columnIndex);
                    }

                    public V getValue() {
                        return (V)this.this$1.this$0.at((int)this.rowIndex, (int)this.columnIndex);
                    }
                };
            }
        };
    }

    @Override
    public Map<R, V> column(C columnKey) {
        Map<K, V> map;
        Preconditions.checkNotNull(columnKey);
        Integer columnIndex = this.columnKeyToIndex.get(columnKey);
        if (columnIndex == null) {
            map = ImmutableMap.of();
            return map;
        }
        map = new Column((ArrayTable)this, (int)columnIndex.intValue());
        return map;
    }

    @Override
    public ImmutableSet<C> columnKeySet() {
        return this.columnKeyToIndex.keySet();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        Object object;
        ArrayTable<R, C, V> map = this.columnMap;
        if (map == null) {
            object = this.columnMap = new ColumnMap((ArrayTable)this, null);
            return object;
        }
        object = map;
        return object;
    }

    @Override
    public Map<C, V> row(R rowKey) {
        Map<K, V> map;
        Preconditions.checkNotNull(rowKey);
        Integer rowIndex = this.rowKeyToIndex.get(rowKey);
        if (rowIndex == null) {
            map = ImmutableMap.of();
            return map;
        }
        map = new Row((ArrayTable)this, (int)rowIndex.intValue());
        return map;
    }

    @Override
    public ImmutableSet<R> rowKeySet() {
        return this.rowKeyToIndex.keySet();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        Object object;
        ArrayTable<R, C, V> map = this.rowMap;
        if (map == null) {
            object = this.rowMap = new RowMap((ArrayTable)this, null);
            return object;
        }
        object = map;
        return object;
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    static /* synthetic */ ImmutableList access$000(ArrayTable x0) {
        return x0.columnList;
    }

    static /* synthetic */ ImmutableList access$100(ArrayTable x0) {
        return x0.rowList;
    }

    static /* synthetic */ ImmutableMap access$200(ArrayTable x0) {
        return x0.rowKeyToIndex;
    }

    static /* synthetic */ ImmutableMap access$500(ArrayTable x0) {
        return x0.columnKeyToIndex;
    }
}

