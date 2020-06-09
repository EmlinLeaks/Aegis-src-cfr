/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractSortedMultiset;
import com.google.common.collect.BoundType;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.GeneralRange;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Serialization;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class TreeMultiset<E>
extends AbstractSortedMultiset<E>
implements Serializable {
    private final transient Reference<AvlNode<E>> rootReference;
    private final transient GeneralRange<E> range;
    private final transient AvlNode<E> header;
    @GwtIncompatible
    private static final long serialVersionUID = 1L;

    public static <E extends Comparable> TreeMultiset<E> create() {
        return new TreeMultiset<C>(Ordering.<C>natural());
    }

    public static <E> TreeMultiset<E> create(@Nullable Comparator<? super E> comparator) {
        TreeMultiset<C> treeMultiset;
        if (comparator == null) {
            treeMultiset = new TreeMultiset<C>(Ordering.<C>natural());
            return treeMultiset;
        }
        treeMultiset = new TreeMultiset<E>(comparator);
        return treeMultiset;
    }

    public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> elements) {
        TreeMultiset<E> multiset = TreeMultiset.create();
        Iterables.addAll(multiset, elements);
        return multiset;
    }

    TreeMultiset(Reference<AvlNode<E>> rootReference, GeneralRange<E> range, AvlNode<E> endLink) {
        super(range.comparator());
        this.rootReference = rootReference;
        this.range = range;
        this.header = endLink;
    }

    TreeMultiset(Comparator<? super E> comparator) {
        super(comparator);
        this.range = GeneralRange.all(comparator);
        this.header = new AvlNode<Object>(null, (int)1);
        TreeMultiset.successor(this.header, this.header);
        this.rootReference = new Reference<T>(null);
    }

    private long aggregateForEntries(Aggregate aggr) {
        AvlNode<E> root = this.rootReference.get();
        long total = aggr.treeAggregate(root);
        if (this.range.hasLowerBound()) {
            total -= this.aggregateBelowRange((Aggregate)aggr, root);
        }
        if (!this.range.hasUpperBound()) return total;
        total -= this.aggregateAboveRange((Aggregate)aggr, root);
        return total;
    }

    private long aggregateBelowRange(Aggregate aggr, @Nullable AvlNode<E> node) {
        if (node == null) {
            return 0L;
        }
        int cmp = this.comparator().compare(this.range.getLowerEndpoint(), node.elem);
        if (cmp < 0) {
            return this.aggregateBelowRange((Aggregate)aggr, node.left);
        }
        if (cmp != 0) return aggr.treeAggregate(node.left) + (long)aggr.nodeAggregate(node) + this.aggregateBelowRange((Aggregate)aggr, node.right);
        switch (4.$SwitchMap$com$google$common$collect$BoundType[this.range.getLowerBoundType().ordinal()]) {
            case 1: {
                return (long)aggr.nodeAggregate(node) + aggr.treeAggregate(node.left);
            }
            case 2: {
                return aggr.treeAggregate(node.left);
            }
        }
        throw new AssertionError();
    }

    private long aggregateAboveRange(Aggregate aggr, @Nullable AvlNode<E> node) {
        if (node == null) {
            return 0L;
        }
        int cmp = this.comparator().compare(this.range.getUpperEndpoint(), node.elem);
        if (cmp > 0) {
            return this.aggregateAboveRange((Aggregate)aggr, node.right);
        }
        if (cmp != 0) return aggr.treeAggregate(node.right) + (long)aggr.nodeAggregate(node) + this.aggregateAboveRange((Aggregate)aggr, node.left);
        switch (4.$SwitchMap$com$google$common$collect$BoundType[this.range.getUpperBoundType().ordinal()]) {
            case 1: {
                return (long)aggr.nodeAggregate(node) + aggr.treeAggregate(node.right);
            }
            case 2: {
                return aggr.treeAggregate(node.right);
            }
        }
        throw new AssertionError();
    }

    @Override
    public int size() {
        return Ints.saturatedCast((long)this.aggregateForEntries((Aggregate)Aggregate.SIZE));
    }

    @Override
    int distinctElements() {
        return Ints.saturatedCast((long)this.aggregateForEntries((Aggregate)Aggregate.DISTINCT));
    }

    @Override
    public int count(@Nullable Object element) {
        try {
            Object e = element;
            AvlNode<Object> root = this.rootReference.get();
            if (!this.range.contains(e)) return 0;
            if (root != null) return root.count(this.comparator(), e);
            return 0;
        }
        catch (ClassCastException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    @CanIgnoreReturnValue
    @Override
    public int add(@Nullable E element, int occurrences) {
        CollectPreconditions.checkNonnegative((int)occurrences, (String)"occurrences");
        if (occurrences == 0) {
            return this.count(element);
        }
        Preconditions.checkArgument((boolean)this.range.contains(element));
        AvlNode<E> root = this.rootReference.get();
        if (root == null) {
            this.comparator().compare(element, element);
            AvlNode<E> newRoot = new AvlNode<E>(element, (int)occurrences);
            TreeMultiset.successor(this.header, newRoot, this.header);
            this.rootReference.checkAndSet(root, newRoot);
            return 0;
        }
        int[] result = new int[1];
        AvlNode<E> newRoot = root.add(this.comparator(), element, (int)occurrences, (int[])result);
        this.rootReference.checkAndSet(root, newRoot);
        return result[0];
    }

    @CanIgnoreReturnValue
    @Override
    public int remove(@Nullable Object element, int occurrences) {
        AvlNode<Object> newRoot;
        CollectPreconditions.checkNonnegative((int)occurrences, (String)"occurrences");
        if (occurrences == 0) {
            return this.count((Object)element);
        }
        AvlNode<Object> root = this.rootReference.get();
        int[] result = new int[1];
        try {
            Object e = element;
            if (!this.range.contains(e)) return 0;
            if (root == null) {
                return 0;
            }
            newRoot = root.remove(this.comparator(), e, (int)occurrences, (int[])result);
        }
        catch (ClassCastException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
        this.rootReference.checkAndSet(root, newRoot);
        return result[0];
    }

    @CanIgnoreReturnValue
    @Override
    public int setCount(@Nullable E element, int count) {
        CollectPreconditions.checkNonnegative((int)count, (String)"count");
        if (!this.range.contains(element)) {
            Preconditions.checkArgument((boolean)(count == 0));
            return 0;
        }
        AvlNode<E> root = this.rootReference.get();
        if (root == null) {
            if (count <= 0) return 0;
            this.add(element, (int)count);
            return 0;
        }
        int[] result = new int[1];
        AvlNode<E> newRoot = root.setCount(this.comparator(), element, (int)count, (int[])result);
        this.rootReference.checkAndSet(root, newRoot);
        return result[0];
    }

    @CanIgnoreReturnValue
    @Override
    public boolean setCount(@Nullable E element, int oldCount, int newCount) {
        CollectPreconditions.checkNonnegative((int)newCount, (String)"newCount");
        CollectPreconditions.checkNonnegative((int)oldCount, (String)"oldCount");
        Preconditions.checkArgument((boolean)this.range.contains(element));
        AvlNode<E> root = this.rootReference.get();
        if (root == null) {
            if (oldCount != 0) return false;
            if (newCount <= 0) return true;
            this.add(element, (int)newCount);
            return true;
        }
        int[] result = new int[1];
        AvlNode<E> newRoot = root.setCount(this.comparator(), element, (int)oldCount, (int)newCount, (int[])result);
        this.rootReference.checkAndSet(root, newRoot);
        if (result[0] != oldCount) return false;
        return true;
    }

    private Multiset.Entry<E> wrapEntry(AvlNode<E> baseEntry) {
        return new Multisets.AbstractEntry<E>((TreeMultiset)this, baseEntry){
            final /* synthetic */ AvlNode val$baseEntry;
            final /* synthetic */ TreeMultiset this$0;
            {
                this.this$0 = treeMultiset;
                this.val$baseEntry = avlNode;
            }

            public E getElement() {
                return (E)this.val$baseEntry.getElement();
            }

            public int getCount() {
                int result = this.val$baseEntry.getCount();
                if (result != 0) return result;
                return this.this$0.count(this.getElement());
            }
        };
    }

    @Nullable
    private AvlNode<E> firstNode() {
        AvlNode node;
        AvlNode<E> root = this.rootReference.get();
        if (root == null) {
            return null;
        }
        if (this.range.hasLowerBound()) {
            E endpoint = this.range.getLowerEndpoint();
            node = this.rootReference.get().ceiling((Comparator)this.comparator(), endpoint);
            if (node == null) {
                return null;
            }
            if (this.range.getLowerBoundType() == BoundType.OPEN && this.comparator().compare(endpoint, node.getElement()) == 0) {
                node = ((AvlNode)node).succ;
            }
        } else {
            node = this.header.succ;
        }
        if (node == this.header) return null;
        if (!this.range.contains(node.getElement())) return null;
        AvlNode avlNode = node;
        return avlNode;
    }

    @Nullable
    private AvlNode<E> lastNode() {
        AvlNode node;
        AvlNode<E> root = this.rootReference.get();
        if (root == null) {
            return null;
        }
        if (this.range.hasUpperBound()) {
            E endpoint = this.range.getUpperEndpoint();
            node = this.rootReference.get().floor((Comparator)this.comparator(), endpoint);
            if (node == null) {
                return null;
            }
            if (this.range.getUpperBoundType() == BoundType.OPEN && this.comparator().compare(endpoint, node.getElement()) == 0) {
                node = ((AvlNode)node).pred;
            }
        } else {
            node = this.header.pred;
        }
        if (node == this.header) return null;
        if (!this.range.contains(node.getElement())) return null;
        AvlNode avlNode = node;
        return avlNode;
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        return new Iterator<Multiset.Entry<E>>((TreeMultiset)this){
            AvlNode<E> current;
            Multiset.Entry<E> prevEntry;
            final /* synthetic */ TreeMultiset this$0;
            {
                this.this$0 = treeMultiset;
                this.current = TreeMultiset.access$1200((TreeMultiset)this.this$0);
            }

            public boolean hasNext() {
                if (this.current == null) {
                    return false;
                }
                if (!TreeMultiset.access$1300((TreeMultiset)this.this$0).tooHigh(this.current.getElement())) return true;
                this.current = null;
                return false;
            }

            public Multiset.Entry<E> next() {
                Multiset.Entry result;
                if (!this.hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                this.prevEntry = result = TreeMultiset.access$1400((TreeMultiset)this.this$0, this.current);
                if (AvlNode.access$900(this.current) == TreeMultiset.access$1500((TreeMultiset)this.this$0)) {
                    this.current = null;
                    return result;
                }
                this.current = AvlNode.access$900(this.current);
                return result;
            }

            public void remove() {
                CollectPreconditions.checkRemove((boolean)(this.prevEntry != null));
                this.this$0.setCount(this.prevEntry.getElement(), (int)0);
                this.prevEntry = null;
            }
        };
    }

    @Override
    Iterator<Multiset.Entry<E>> descendingEntryIterator() {
        return new Iterator<Multiset.Entry<E>>((TreeMultiset)this){
            AvlNode<E> current;
            Multiset.Entry<E> prevEntry;
            final /* synthetic */ TreeMultiset this$0;
            {
                this.this$0 = treeMultiset;
                this.current = TreeMultiset.access$1600((TreeMultiset)this.this$0);
                this.prevEntry = null;
            }

            public boolean hasNext() {
                if (this.current == null) {
                    return false;
                }
                if (!TreeMultiset.access$1300((TreeMultiset)this.this$0).tooLow(this.current.getElement())) return true;
                this.current = null;
                return false;
            }

            public Multiset.Entry<E> next() {
                Multiset.Entry result;
                if (!this.hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                this.prevEntry = result = TreeMultiset.access$1400((TreeMultiset)this.this$0, this.current);
                if (AvlNode.access$1100(this.current) == TreeMultiset.access$1500((TreeMultiset)this.this$0)) {
                    this.current = null;
                    return result;
                }
                this.current = AvlNode.access$1100(this.current);
                return result;
            }

            public void remove() {
                CollectPreconditions.checkRemove((boolean)(this.prevEntry != null));
                this.this$0.setCount(this.prevEntry.getElement(), (int)0);
                this.prevEntry = null;
            }
        };
    }

    @Override
    public SortedMultiset<E> headMultiset(@Nullable E upperBound, BoundType boundType) {
        return new TreeMultiset<E>(this.rootReference, this.range.intersect(GeneralRange.upTo(this.comparator(), upperBound, (BoundType)boundType)), this.header);
    }

    @Override
    public SortedMultiset<E> tailMultiset(@Nullable E lowerBound, BoundType boundType) {
        return new TreeMultiset<E>(this.rootReference, this.range.intersect(GeneralRange.downTo(this.comparator(), lowerBound, (BoundType)boundType)), this.header);
    }

    static int distinctElements(@Nullable AvlNode<?> node) {
        if (node == null) {
            return 0;
        }
        int n = node.distinctElements;
        return n;
    }

    private static <T> void successor(AvlNode<T> a, AvlNode<T> b) {
        a.succ = b;
        b.pred = a;
    }

    private static <T> void successor(AvlNode<T> a, AvlNode<T> b, AvlNode<T> c) {
        TreeMultiset.successor(a, b);
        TreeMultiset.successor(b, c);
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.elementSet().comparator());
        Serialization.writeMultiset(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        Comparator comparator = (Comparator)stream.readObject();
        Serialization.getFieldSetter(AbstractSortedMultiset.class, (String)"comparator").set((AbstractSortedMultiset)this, (Object)comparator);
        Serialization.getFieldSetter(TreeMultiset.class, (String)"range").set((TreeMultiset)this, GeneralRange.all(comparator));
        Serialization.getFieldSetter(TreeMultiset.class, (String)"rootReference").set((TreeMultiset)this, new Reference<T>(null));
        AvlNode<Object> header = new AvlNode<Object>(null, (int)1);
        Serialization.getFieldSetter(TreeMultiset.class, (String)"header").set((TreeMultiset)this, header);
        TreeMultiset.successor(header, header);
        Serialization.populateMultiset(this, (ObjectInputStream)stream);
    }

    static /* synthetic */ AvlNode access$1200(TreeMultiset x0) {
        return x0.firstNode();
    }

    static /* synthetic */ GeneralRange access$1300(TreeMultiset x0) {
        return x0.range;
    }

    static /* synthetic */ Multiset.Entry access$1400(TreeMultiset x0, AvlNode x1) {
        return x0.wrapEntry(x1);
    }

    static /* synthetic */ AvlNode access$1500(TreeMultiset x0) {
        return x0.header;
    }

    static /* synthetic */ AvlNode access$1600(TreeMultiset x0) {
        return x0.lastNode();
    }

    static /* synthetic */ void access$1700(AvlNode x0, AvlNode x1, AvlNode x2) {
        TreeMultiset.successor(x0, x1, x2);
    }

    static /* synthetic */ void access$1800(AvlNode x0, AvlNode x1) {
        TreeMultiset.successor(x0, x1);
    }
}

