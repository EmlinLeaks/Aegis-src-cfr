/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.RegularImmutableMultiset;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
class RegularImmutableMultiset<E>
extends ImmutableMultiset<E> {
    static final RegularImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset<E>(ImmutableList.<E>of());
    private final transient Multisets.ImmutableEntry<E>[] entries;
    private final transient Multisets.ImmutableEntry<E>[] hashTable;
    private final transient int size;
    private final transient int hashCode;
    @LazyInit
    private transient ImmutableSet<E> elementSet;

    RegularImmutableMultiset(Collection<? extends Multiset.Entry<? extends E>> entries) {
        int distinct = entries.size();
        Multisets.ImmutableEntry[] entryArray = new Multisets.ImmutableEntry[distinct];
        if (distinct == 0) {
            this.entries = entryArray;
            this.hashTable = null;
            this.size = 0;
            this.hashCode = 0;
            this.elementSet = ImmutableSet.of();
            return;
        }
        int tableSize = Hashing.closedTableSize((int)distinct, (double)1.0);
        int mask = tableSize - 1;
        Multisets.ImmutableEntry[] hashTable = new Multisets.ImmutableEntry[tableSize];
        int index = 0;
        int hashCode = 0;
        long size = 0L;
        Iterator<Multiset.Entry<E>> i$ = entries.iterator();
        do {
            Multisets.ImmutableEntry newEntry;
            if (!i$.hasNext()) {
                this.entries = entryArray;
                this.hashTable = hashTable;
                this.size = Ints.saturatedCast((long)size);
                this.hashCode = hashCode;
                return;
            }
            Multiset.Entry<E> entry = i$.next();
            E element = Preconditions.checkNotNull(entry.getElement());
            int count = entry.getCount();
            int hash = element.hashCode();
            int bucket = Hashing.smear((int)hash) & mask;
            Multisets.ImmutableEntry bucketHead = hashTable[bucket];
            if (bucketHead == null) {
                boolean canReuseEntry = entry instanceof Multisets.ImmutableEntry && !(entry instanceof NonTerminalEntry);
                newEntry = canReuseEntry ? (Multisets.ImmutableEntry<E>)entry : new Multisets.ImmutableEntry<E>(element, (int)count);
            } else {
                newEntry = new NonTerminalEntry<E>(element, (int)count, bucketHead);
            }
            hashCode += hash ^ count;
            entryArray[index++] = newEntry;
            hashTable[bucket] = newEntry;
            size += (long)count;
        } while (true);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int count(@Nullable Object element) {
        Multisets.ImmutableEntry<E>[] hashTable = this.hashTable;
        if (element == null) return 0;
        if (hashTable == null) {
            return 0;
        }
        int hash = Hashing.smearedHash((Object)element);
        int mask = hashTable.length - 1;
        Multisets.ImmutableEntry<E> entry = hashTable[hash & mask];
        while (entry != null) {
            if (Objects.equal((Object)element, entry.getElement())) {
                return entry.getCount();
            }
            entry = entry.nextInBucket();
        }
        return 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ImmutableSet<E> elementSet() {
        ElementSet elementSet;
        ElementSet result = this.elementSet;
        if (result == null) {
            elementSet = this.elementSet = new ElementSet((RegularImmutableMultiset)this, null);
            return elementSet;
        }
        elementSet = result;
        return elementSet;
    }

    @Override
    Multiset.Entry<E> getEntry(int index) {
        return this.entries[index];
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    static /* synthetic */ Multisets.ImmutableEntry[] access$100(RegularImmutableMultiset x0) {
        return x0.entries;
    }
}

