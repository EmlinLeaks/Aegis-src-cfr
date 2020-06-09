/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.graph.ElementOrder;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
public final class ElementOrder<T> {
    private final Type type;
    @Nullable
    private final Comparator<T> comparator;

    private ElementOrder(Type type, @Nullable Comparator<T> comparator) {
        this.type = Preconditions.checkNotNull(type);
        this.comparator = comparator;
        Preconditions.checkState((boolean)(type == Type.SORTED == (comparator != null)));
    }

    public static <S> ElementOrder<S> unordered() {
        return new ElementOrder<T>((Type)Type.UNORDERED, null);
    }

    public static <S> ElementOrder<S> insertion() {
        return new ElementOrder<T>((Type)Type.INSERTION, null);
    }

    public static <S extends Comparable<? super S>> ElementOrder<S> natural() {
        return new ElementOrder<C>((Type)Type.SORTED, Ordering.<C>natural());
    }

    public static <S> ElementOrder<S> sorted(Comparator<S> comparator) {
        return new ElementOrder<S>((Type)Type.SORTED, comparator);
    }

    public Type type() {
        return this.type;
    }

    public Comparator<T> comparator() {
        if (this.comparator == null) throw new UnsupportedOperationException((String)"This ordering does not define a comparator.");
        return this.comparator;
    }

    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ElementOrder)) {
            return false;
        }
        ElementOrder other = (ElementOrder)obj;
        if (this.type != other.type) return false;
        if (!Objects.equal(this.comparator, other.comparator)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.type, this.comparator});
    }

    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper((Object)this).add((String)"type", (Object)((Object)this.type));
        if (this.comparator == null) return helper.toString();
        helper.add((String)"comparator", this.comparator);
        return helper.toString();
    }

    <K extends T, V> Map<K, V> createMap(int expectedSize) {
        switch (1.$SwitchMap$com$google$common$graph$ElementOrder$Type[this.type.ordinal()]) {
            case 1: {
                return Maps.newHashMapWithExpectedSize((int)expectedSize);
            }
            case 2: {
                return Maps.newLinkedHashMapWithExpectedSize((int)expectedSize);
            }
            case 3: {
                return Maps.newTreeMap(this.comparator());
            }
        }
        throw new AssertionError();
    }

    <T1 extends T> ElementOrder<T1> cast() {
        return this;
    }
}

