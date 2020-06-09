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
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Count;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class AbstractMapBasedMultiset<E>
extends AbstractMultiset<E>
implements Serializable {
    private transient Map<E, Count> backingMap;
    private transient long size;
    @GwtIncompatible
    private static final long serialVersionUID = -2250766705698539974L;

    protected AbstractMapBasedMultiset(Map<E, Count> backingMap) {
        this.backingMap = Preconditions.checkNotNull(backingMap);
        this.size = (long)super.size();
    }

    void setBackingMap(Map<E, Count> backingMap) {
        this.backingMap = backingMap;
    }

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        return super.entrySet();
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        Iterator<Map.Entry<E, Count>> backingEntries = this.backingMap.entrySet().iterator();
        return new Iterator<Multiset.Entry<E>>((AbstractMapBasedMultiset)this, backingEntries){
            Map.Entry<E, Count> toRemove;
            final /* synthetic */ Iterator val$backingEntries;
            final /* synthetic */ AbstractMapBasedMultiset this$0;
            {
                this.this$0 = abstractMapBasedMultiset;
                this.val$backingEntries = iterator;
            }

            public boolean hasNext() {
                return this.val$backingEntries.hasNext();
            }

            public Multiset.Entry<E> next() {
                Map.Entry mapEntry;
                this.toRemove = mapEntry = (Map.Entry)this.val$backingEntries.next();
                return new com.google.common.collect.Multisets$AbstractEntry<E>(this, (Map.Entry)mapEntry){
                    final /* synthetic */ Map.Entry val$mapEntry;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$mapEntry = entry;
                    }

                    public E getElement() {
                        return (E)this.val$mapEntry.getKey();
                    }

                    public int getCount() {
                        Count frequency;
                        Count count = (Count)this.val$mapEntry.getValue();
                        if ((count == null || count.get() == 0) && (frequency = (Count)AbstractMapBasedMultiset.access$000((AbstractMapBasedMultiset)this.this$1.this$0).get(this.getElement())) != null) {
                            return frequency.get();
                        }
                        if (count == null) {
                            return 0;
                        }
                        int n = count.get();
                        return n;
                    }
                };
            }

            public void remove() {
                CollectPreconditions.checkRemove((boolean)(this.toRemove != null));
                AbstractMapBasedMultiset.access$122((AbstractMapBasedMultiset)this.this$0, (long)((long)this.toRemove.getValue().getAndSet((int)0)));
                this.val$backingEntries.remove();
                this.toRemove = null;
            }
        };
    }

    @Override
    public void clear() {
        Iterator<Count> i$ = this.backingMap.values().iterator();
        do {
            if (!i$.hasNext()) {
                this.backingMap.clear();
                this.size = 0L;
                return;
            }
            Count frequency = i$.next();
            frequency.set((int)0);
        } while (true);
    }

    @Override
    int distinctElements() {
        return this.backingMap.size();
    }

    @Override
    public int size() {
        return Ints.saturatedCast((long)this.size);
    }

    @Override
    public Iterator<E> iterator() {
        return new MapBasedMultisetIterator((AbstractMapBasedMultiset)this);
    }

    @Override
    public int count(@Nullable Object element) {
        Count frequency = Maps.safeGet(this.backingMap, (Object)element);
        if (frequency == null) {
            return 0;
        }
        int n = frequency.get();
        return n;
    }

    @CanIgnoreReturnValue
    @Override
    public int add(@Nullable E element, int occurrences) {
        int oldCount;
        if (occurrences == 0) {
            return this.count(element);
        }
        Preconditions.checkArgument((boolean)(occurrences > 0), (String)"occurrences cannot be negative: %s", (int)occurrences);
        Count frequency = this.backingMap.get(element);
        if (frequency == null) {
            oldCount = 0;
            this.backingMap.put(element, (Count)new Count((int)occurrences));
        } else {
            oldCount = frequency.get();
            long newCount = (long)oldCount + (long)occurrences;
            Preconditions.checkArgument((boolean)(newCount <= Integer.MAX_VALUE), (String)"too many occurrences: %s", (long)newCount);
            frequency.add((int)occurrences);
        }
        this.size += (long)occurrences;
        return oldCount;
    }

    @CanIgnoreReturnValue
    @Override
    public int remove(@Nullable Object element, int occurrences) {
        int numberRemoved;
        if (occurrences == 0) {
            return this.count((Object)element);
        }
        Preconditions.checkArgument((boolean)(occurrences > 0), (String)"occurrences cannot be negative: %s", (int)occurrences);
        Count frequency = this.backingMap.get((Object)element);
        if (frequency == null) {
            return 0;
        }
        int oldCount = frequency.get();
        if (oldCount > occurrences) {
            numberRemoved = occurrences;
        } else {
            numberRemoved = oldCount;
            this.backingMap.remove((Object)element);
        }
        frequency.add((int)(-numberRemoved));
        this.size -= (long)numberRemoved;
        return oldCount;
    }

    @CanIgnoreReturnValue
    @Override
    public int setCount(@Nullable E element, int count) {
        int oldCount;
        CollectPreconditions.checkNonnegative((int)count, (String)"count");
        if (count == 0) {
            Count existingCounter = this.backingMap.remove(element);
            oldCount = AbstractMapBasedMultiset.getAndSet((Count)existingCounter, (int)count);
        } else {
            Count existingCounter = this.backingMap.get(element);
            oldCount = AbstractMapBasedMultiset.getAndSet((Count)existingCounter, (int)count);
            if (existingCounter == null) {
                this.backingMap.put(element, (Count)new Count((int)count));
            }
        }
        this.size += (long)(count - oldCount);
        return oldCount;
    }

    private static int getAndSet(@Nullable Count i, int count) {
        if (i != null) return i.getAndSet((int)count);
        return 0;
    }

    @GwtIncompatible
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException((String)"Stream data required");
    }

    static /* synthetic */ Map access$000(AbstractMapBasedMultiset x0) {
        return x0.backingMap;
    }

    static /* synthetic */ long access$122(AbstractMapBasedMultiset x0, long x1) {
        return x0.size -= x1;
    }

    static /* synthetic */ long access$110(AbstractMapBasedMultiset x0) {
        return x0.size--;
    }
}

