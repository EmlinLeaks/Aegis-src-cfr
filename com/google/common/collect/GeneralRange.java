/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
final class GeneralRange<T>
implements Serializable {
    private final Comparator<? super T> comparator;
    private final boolean hasLowerBound;
    @Nullable
    private final T lowerEndpoint;
    private final BoundType lowerBoundType;
    private final boolean hasUpperBound;
    @Nullable
    private final T upperEndpoint;
    private final BoundType upperBoundType;
    private transient GeneralRange<T> reverse;

    static <T extends Comparable> GeneralRange<T> from(Range<T> range) {
        T lowerEndpoint = range.hasLowerBound() ? (T)range.lowerEndpoint() : null;
        BoundType lowerBoundType = range.hasLowerBound() ? range.lowerBoundType() : BoundType.OPEN;
        T upperEndpoint = range.hasUpperBound() ? (T)range.upperEndpoint() : null;
        BoundType upperBoundType = range.hasUpperBound() ? range.upperBoundType() : BoundType.OPEN;
        return new GeneralRange<Object>(Ordering.<C>natural(), (boolean)range.hasLowerBound(), lowerEndpoint, (BoundType)lowerBoundType, (boolean)range.hasUpperBound(), upperEndpoint, (BoundType)upperBoundType);
    }

    static <T> GeneralRange<T> all(Comparator<? super T> comparator) {
        return new GeneralRange<Object>(comparator, (boolean)false, null, (BoundType)BoundType.OPEN, (boolean)false, null, (BoundType)BoundType.OPEN);
    }

    static <T> GeneralRange<T> downTo(Comparator<? super T> comparator, @Nullable T endpoint, BoundType boundType) {
        return new GeneralRange<Object>(comparator, (boolean)true, endpoint, (BoundType)boundType, (boolean)false, null, (BoundType)BoundType.OPEN);
    }

    static <T> GeneralRange<T> upTo(Comparator<? super T> comparator, @Nullable T endpoint, BoundType boundType) {
        return new GeneralRange<Object>(comparator, (boolean)false, null, (BoundType)BoundType.OPEN, (boolean)true, endpoint, (BoundType)boundType);
    }

    static <T> GeneralRange<T> range(Comparator<? super T> comparator, @Nullable T lower, BoundType lowerType, @Nullable T upper, BoundType upperType) {
        return new GeneralRange<T>(comparator, (boolean)true, lower, (BoundType)lowerType, (boolean)true, upper, (BoundType)upperType);
    }

    private GeneralRange(Comparator<? super T> comparator, boolean hasLowerBound, @Nullable T lowerEndpoint, BoundType lowerBoundType, boolean hasUpperBound, @Nullable T upperEndpoint, BoundType upperBoundType) {
        this.comparator = Preconditions.checkNotNull(comparator);
        this.hasLowerBound = hasLowerBound;
        this.hasUpperBound = hasUpperBound;
        this.lowerEndpoint = lowerEndpoint;
        this.lowerBoundType = Preconditions.checkNotNull(lowerBoundType);
        this.upperEndpoint = upperEndpoint;
        this.upperBoundType = Preconditions.checkNotNull(upperBoundType);
        if (hasLowerBound) {
            comparator.compare(lowerEndpoint, lowerEndpoint);
        }
        if (hasUpperBound) {
            comparator.compare(upperEndpoint, upperEndpoint);
        }
        if (!hasLowerBound) return;
        if (!hasUpperBound) return;
        int cmp = comparator.compare(lowerEndpoint, upperEndpoint);
        Preconditions.checkArgument((boolean)(cmp <= 0), (String)"lowerEndpoint (%s) > upperEndpoint (%s)", lowerEndpoint, upperEndpoint);
        if (cmp != 0) return;
        Preconditions.checkArgument((boolean)(lowerBoundType != BoundType.OPEN | upperBoundType != BoundType.OPEN));
    }

    Comparator<? super T> comparator() {
        return this.comparator;
    }

    boolean hasLowerBound() {
        return this.hasLowerBound;
    }

    boolean hasUpperBound() {
        return this.hasUpperBound;
    }

    boolean isEmpty() {
        if (this.hasUpperBound()) {
            if (this.tooLow(this.getUpperEndpoint())) return true;
        }
        if (!this.hasLowerBound()) return false;
        if (!this.tooHigh(this.getLowerEndpoint())) return false;
        return true;
    }

    boolean tooLow(@Nullable T t) {
        boolean bl;
        if (!this.hasLowerBound()) {
            return false;
        }
        T lbound = this.getLowerEndpoint();
        int cmp = this.comparator.compare(t, lbound);
        boolean bl2 = cmp < 0;
        boolean bl3 = cmp == 0;
        if (this.getLowerBoundType() == BoundType.OPEN) {
            bl = true;
            return bl2 | bl3 & bl;
        }
        bl = false;
        return bl2 | bl3 & bl;
    }

    boolean tooHigh(@Nullable T t) {
        boolean bl;
        if (!this.hasUpperBound()) {
            return false;
        }
        T ubound = this.getUpperEndpoint();
        int cmp = this.comparator.compare(t, ubound);
        boolean bl2 = cmp > 0;
        boolean bl3 = cmp == 0;
        if (this.getUpperBoundType() == BoundType.OPEN) {
            bl = true;
            return bl2 | bl3 & bl;
        }
        bl = false;
        return bl2 | bl3 & bl;
    }

    boolean contains(@Nullable T t) {
        if (this.tooLow(t)) return false;
        if (this.tooHigh(t)) return false;
        return true;
    }

    GeneralRange<T> intersect(GeneralRange<T> other) {
        int cmp;
        int cmp2;
        Preconditions.checkNotNull(other);
        Preconditions.checkArgument((boolean)this.comparator.equals(other.comparator));
        boolean hasLowBound = this.hasLowerBound;
        T lowEnd = this.getLowerEndpoint();
        BoundType lowType = this.getLowerBoundType();
        if (!this.hasLowerBound()) {
            hasLowBound = other.hasLowerBound;
            lowEnd = other.getLowerEndpoint();
            lowType = other.getLowerBoundType();
        } else if (other.hasLowerBound() && ((cmp = this.comparator.compare(this.getLowerEndpoint(), other.getLowerEndpoint())) < 0 || cmp == 0 && other.getLowerBoundType() == BoundType.OPEN)) {
            lowEnd = other.getLowerEndpoint();
            lowType = other.getLowerBoundType();
        }
        boolean hasUpBound = this.hasUpperBound;
        T upEnd = this.getUpperEndpoint();
        BoundType upType = this.getUpperBoundType();
        if (!this.hasUpperBound()) {
            hasUpBound = other.hasUpperBound;
            upEnd = other.getUpperEndpoint();
            upType = other.getUpperBoundType();
        } else if (other.hasUpperBound() && ((cmp2 = this.comparator.compare(this.getUpperEndpoint(), other.getUpperEndpoint())) > 0 || cmp2 == 0 && other.getUpperBoundType() == BoundType.OPEN)) {
            upEnd = other.getUpperEndpoint();
            upType = other.getUpperBoundType();
        }
        if (!hasLowBound) return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
        if (!hasUpBound) return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
        cmp2 = this.comparator.compare(lowEnd, upEnd);
        if (cmp2 <= 0) {
            if (cmp2 != 0) return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
            if (lowType != BoundType.OPEN) return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
            if (upType != BoundType.OPEN) return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
        }
        lowEnd = upEnd;
        lowType = BoundType.OPEN;
        upType = BoundType.CLOSED;
        return new GeneralRange<T>(this.comparator, (boolean)hasLowBound, lowEnd, (BoundType)lowType, (boolean)hasUpBound, upEnd, (BoundType)upType);
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof GeneralRange)) return false;
        GeneralRange r = (GeneralRange)obj;
        if (!this.comparator.equals(r.comparator)) return false;
        if (this.hasLowerBound != r.hasLowerBound) return false;
        if (this.hasUpperBound != r.hasUpperBound) return false;
        if (!this.getLowerBoundType().equals((Object)((Object)r.getLowerBoundType()))) return false;
        if (!this.getUpperBoundType().equals((Object)((Object)r.getUpperBoundType()))) return false;
        if (!Objects.equal(this.getLowerEndpoint(), r.getLowerEndpoint())) return false;
        if (!Objects.equal(this.getUpperEndpoint(), r.getUpperEndpoint())) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.comparator, this.getLowerEndpoint(), this.getLowerBoundType(), this.getUpperEndpoint(), this.getUpperBoundType()});
    }

    GeneralRange<T> reverse() {
        GeneralRange<Object> result = this.reverse;
        if (result != null) return result;
        result = new GeneralRange<S>(Ordering.from(this.comparator).reverse(), (boolean)this.hasUpperBound, this.getUpperEndpoint(), (BoundType)this.getUpperBoundType(), (boolean)this.hasLowerBound, this.getLowerEndpoint(), (BoundType)this.getLowerBoundType());
        result.reverse = this;
        this.reverse = result;
        return this.reverse;
    }

    public String toString() {
        char c;
        char c2 = this.lowerBoundType == BoundType.CLOSED ? (char)'[' : '(';
        String string = this.hasLowerBound ? this.lowerEndpoint : "-\u221e";
        String string2 = this.hasUpperBound ? this.upperEndpoint : "\u221e";
        if (this.upperBoundType == BoundType.CLOSED) {
            c = ']';
            return this.comparator + ":" + c2 + string + ',' + string2 + c;
        }
        c = ')';
        return this.comparator + ":" + c2 + string + ',' + string2 + c;
    }

    T getLowerEndpoint() {
        return (T)this.lowerEndpoint;
    }

    BoundType getLowerBoundType() {
        return this.lowerBoundType;
    }

    T getUpperEndpoint() {
        return (T)this.upperEndpoint;
    }

    BoundType getUpperBoundType() {
        return this.upperBoundType;
    }
}

