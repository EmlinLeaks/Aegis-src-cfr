/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.BoundType;
import com.google.common.collect.Collections2;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.EmptyContiguousSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RegularContiguousSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class RegularContiguousSet<C extends Comparable>
extends ContiguousSet<C> {
    private final Range<C> range;
    private static final long serialVersionUID = 0L;

    RegularContiguousSet(Range<C> range, DiscreteDomain<C> domain) {
        super(domain);
        this.range = range;
    }

    private ContiguousSet<C> intersectionInCurrentDomain(Range<C> other) {
        ContiguousSet contiguousSet;
        if (this.range.isConnected(other)) {
            contiguousSet = ContiguousSet.create(this.range.intersection(other), this.domain);
            return contiguousSet;
        }
        contiguousSet = new EmptyContiguousSet<C>(this.domain);
        return contiguousSet;
    }

    @Override
    ContiguousSet<C> headSetImpl(C toElement, boolean inclusive) {
        return this.intersectionInCurrentDomain(Range.upTo(toElement, (BoundType)BoundType.forBoolean((boolean)inclusive)));
    }

    @Override
    ContiguousSet<C> subSetImpl(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive) {
        if (fromElement.compareTo(toElement) != 0) return this.intersectionInCurrentDomain(Range.range(fromElement, (BoundType)BoundType.forBoolean((boolean)fromInclusive), toElement, (BoundType)BoundType.forBoolean((boolean)toInclusive)));
        if (fromInclusive) return this.intersectionInCurrentDomain(Range.range(fromElement, (BoundType)BoundType.forBoolean((boolean)fromInclusive), toElement, (BoundType)BoundType.forBoolean((boolean)toInclusive)));
        if (toInclusive) return this.intersectionInCurrentDomain(Range.range(fromElement, (BoundType)BoundType.forBoolean((boolean)fromInclusive), toElement, (BoundType)BoundType.forBoolean((boolean)toInclusive)));
        return new EmptyContiguousSet<C>(this.domain);
    }

    @Override
    ContiguousSet<C> tailSetImpl(C fromElement, boolean inclusive) {
        return this.intersectionInCurrentDomain(Range.downTo(fromElement, (BoundType)BoundType.forBoolean((boolean)inclusive)));
    }

    @GwtIncompatible
    @Override
    int indexOf(Object target) {
        if (!this.contains((Object)target)) return -1;
        int n = (int)this.domain.distance(this.first(), (Comparable)target);
        return n;
    }

    @Override
    public UnmodifiableIterator<C> iterator() {
        return new AbstractSequentialIterator<C>((RegularContiguousSet)this, (Comparable)this.first()){
            final C last;
            final /* synthetic */ RegularContiguousSet this$0;
            {
                this.this$0 = regularContiguousSet;
                super(x0);
                this.last = this.this$0.last();
            }

            protected C computeNext(C previous) {
                C c;
                if (RegularContiguousSet.access$000(previous, this.last)) {
                    c = null;
                    return (C)((C)c);
                }
                c = (C)this.this$0.domain.next(previous);
                return (C)c;
            }
        };
    }

    @GwtIncompatible
    @Override
    public UnmodifiableIterator<C> descendingIterator() {
        return new AbstractSequentialIterator<C>((RegularContiguousSet)this, (Comparable)this.last()){
            final C first;
            final /* synthetic */ RegularContiguousSet this$0;
            {
                this.this$0 = regularContiguousSet;
                super(x0);
                this.first = this.this$0.first();
            }

            protected C computeNext(C previous) {
                C c;
                if (RegularContiguousSet.access$000(previous, this.first)) {
                    c = null;
                    return (C)((C)c);
                }
                c = (C)this.this$0.domain.previous(previous);
                return (C)c;
            }
        };
    }

    private static boolean equalsOrThrow(Comparable<?> left, @Nullable Comparable<?> right) {
        if (right == null) return false;
        if (Range.compareOrThrow(left, right) != 0) return false;
        return true;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public C first() {
        return (C)this.range.lowerBound.leastValueAbove(this.domain);
    }

    @Override
    public C last() {
        return (C)this.range.upperBound.greatestValueBelow(this.domain);
    }

    @Override
    public int size() {
        long distance = this.domain.distance(this.first(), this.last());
        if (distance >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        int n = (int)distance + 1;
        return n;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        try {
            return this.range.contains((Comparable)object);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> targets) {
        return Collections2.containsAllImpl(this, targets);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ContiguousSet<C> intersection(ContiguousSet<C> other) {
        Comparable upperEndpoint;
        ContiguousSet contiguousSet;
        Preconditions.checkNotNull(other);
        Preconditions.checkArgument((boolean)this.domain.equals(other.domain));
        if (other.isEmpty()) {
            return other;
        }
        Comparable lowerEndpoint = (Comparable)Ordering.natural().max(this.first(), other.first());
        if (lowerEndpoint.compareTo(upperEndpoint = (Comparable)Ordering.natural().min(this.last(), other.last())) <= 0) {
            contiguousSet = ContiguousSet.create(Range.closed(lowerEndpoint, upperEndpoint), this.domain);
            return contiguousSet;
        }
        contiguousSet = new EmptyContiguousSet<C>(this.domain);
        return contiguousSet;
    }

    @Override
    public Range<C> range() {
        return this.range((BoundType)BoundType.CLOSED, (BoundType)BoundType.CLOSED);
    }

    @Override
    public Range<C> range(BoundType lowerBoundType, BoundType upperBoundType) {
        return Range.create(this.range.lowerBound.withLowerBoundType((BoundType)lowerBoundType, this.domain), this.range.upperBound.withUpperBoundType((BoundType)upperBoundType, this.domain));
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof RegularContiguousSet)) return super.equals((Object)object);
        RegularContiguousSet that = (RegularContiguousSet)object;
        if (!this.domain.equals((Object)that.domain)) return super.equals((Object)object);
        if (!this.first().equals((Object)that.first())) return false;
        if (!this.last().equals((Object)that.last())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }

    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new SerializedForm<C>(this.range, (DiscreteDomain)this.domain, null);
    }

    static /* synthetic */ boolean access$000(Comparable x0, Comparable x1) {
        return RegularContiguousSet.equalsOrThrow(x0, x1);
    }
}

