/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.StandardRowSortedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

@GwtCompatible(serializable=true)
public class TreeBasedTable<R, C, V>
extends StandardRowSortedTable<R, C, V> {
    private final Comparator<? super C> columnComparator;
    private static final long serialVersionUID = 0L;

    public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create() {
        return new TreeBasedTable<C, C, V>(Ordering.<C>natural(), Ordering.<C>natural());
    }

    public static <R, C, V> TreeBasedTable<R, C, V> create(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
        Preconditions.checkNotNull(rowComparator);
        Preconditions.checkNotNull(columnComparator);
        return new TreeBasedTable<R, C, V>(rowComparator, columnComparator);
    }

    public static <R, C, V> TreeBasedTable<R, C, V> create(TreeBasedTable<R, C, ? extends V> table) {
        TreeBasedTable<R, C, V> result = new TreeBasedTable<R, C, V>(table.rowComparator(), table.columnComparator());
        result.putAll(table);
        return result;
    }

    TreeBasedTable(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
        super(new TreeMap<R, V>(rowComparator), new Factory<C, V>(columnComparator));
        this.columnComparator = columnComparator;
    }

    public Comparator<? super R> rowComparator() {
        return this.rowKeySet().comparator();
    }

    public Comparator<? super C> columnComparator() {
        return this.columnComparator;
    }

    @Override
    public SortedMap<C, V> row(R rowKey) {
        return new TreeRow((TreeBasedTable)this, rowKey);
    }

    @Override
    public SortedSet<R> rowKeySet() {
        return super.rowKeySet();
    }

    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return super.rowMap();
    }

    @Override
    Iterator<C> createColumnKeyIterator() {
        Comparator<C> comparator = this.columnComparator();
        UnmodifiableIterator<C> merged = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function<Map<C, V>, Iterator<C>>((TreeBasedTable)this){
            final /* synthetic */ TreeBasedTable this$0;
            {
                this.this$0 = treeBasedTable;
            }

            public Iterator<C> apply(Map<C, V> input) {
                return input.keySet().iterator();
            }
        }), comparator);
        return new AbstractIterator<C>((TreeBasedTable)this, merged, comparator){
            C lastValue;
            final /* synthetic */ Iterator val$merged;
            final /* synthetic */ Comparator val$comparator;
            final /* synthetic */ TreeBasedTable this$0;
            {
                this.this$0 = treeBasedTable;
                this.val$merged = iterator;
                this.val$comparator = comparator;
            }

            protected C computeNext() {
                E next;
                boolean duplicate;
                do {
                    if (!this.val$merged.hasNext()) {
                        this.lastValue = null;
                        return (C)this.endOfData();
                    }
                    next = this.val$merged.next();
                } while (duplicate = this.lastValue != null && this.val$comparator.compare(next, this.lastValue) == 0);
                this.lastValue = next;
                return (C)this.lastValue;
            }
        };
    }
}

