/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableAsList;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
final class RegularImmutableSet<E>
extends ImmutableSet.Indexed<E> {
    static final RegularImmutableSet<Object> EMPTY = new RegularImmutableSet<E>((Object[])ObjectArrays.EMPTY_ARRAY, (int)0, null, (int)0);
    private final transient Object[] elements;
    @VisibleForTesting
    final transient Object[] table;
    private final transient int mask;
    private final transient int hashCode;

    RegularImmutableSet(Object[] elements, int hashCode, Object[] table, int mask) {
        this.elements = elements;
        this.table = table;
        this.mask = mask;
        this.hashCode = hashCode;
    }

    @Override
    public boolean contains(@Nullable Object target) {
        Object[] table = this.table;
        if (target == null) return false;
        if (table == null) {
            return false;
        }
        int i = Hashing.smearedHash((Object)target);
        Object candidate;
        while ((candidate = table[i &= this.mask]) != null) {
            if (candidate.equals((Object)target)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public int size() {
        return this.elements.length;
    }

    @Override
    E get(int i) {
        return (E)this.elements[i];
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        System.arraycopy((Object)this.elements, (int)0, (Object)dst, (int)offset, (int)this.elements.length);
        return offset + this.elements.length;
    }

    @Override
    ImmutableList<E> createAsList() {
        RegularImmutableAsList<E> regularImmutableAsList;
        if (this.table == null) {
            regularImmutableAsList = ImmutableList.of();
            return regularImmutableAsList;
        }
        regularImmutableAsList = new RegularImmutableAsList<E>(this, (Object[])this.elements);
        return regularImmutableAsList;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }
}

