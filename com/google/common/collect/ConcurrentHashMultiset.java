/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Serialization;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@GwtIncompatible
public final class ConcurrentHashMultiset<E>
extends AbstractMultiset<E>
implements Serializable {
    private final transient ConcurrentMap<E, AtomicInteger> countMap;
    private static final long serialVersionUID = 1L;

    public static <E> ConcurrentHashMultiset<E> create() {
        return new ConcurrentHashMultiset<K>(new ConcurrentHashMap<K, V>());
    }

    public static <E> ConcurrentHashMultiset<E> create(Iterable<? extends E> elements) {
        ConcurrentHashMultiset<E> multiset = ConcurrentHashMultiset.create();
        Iterables.addAll(multiset, elements);
        return multiset;
    }

    @Deprecated
    @Beta
    public static <E> ConcurrentHashMultiset<E> create(MapMaker mapMaker) {
        return ConcurrentHashMultiset.create(mapMaker.makeMap());
    }

    @Beta
    public static <E> ConcurrentHashMultiset<E> create(ConcurrentMap<E, AtomicInteger> countMap) {
        return new ConcurrentHashMultiset<E>(countMap);
    }

    @VisibleForTesting
    ConcurrentHashMultiset(ConcurrentMap<E, AtomicInteger> countMap) {
        Preconditions.checkArgument((boolean)countMap.isEmpty(), (String)"the backing map (%s) must be empty", countMap);
        this.countMap = countMap;
    }

    @Override
    public int count(@Nullable Object element) {
        AtomicInteger existingCounter = Maps.safeGet(this.countMap, (Object)element);
        if (existingCounter == null) {
            return 0;
        }
        int n = existingCounter.get();
        return n;
    }

    @Override
    public int size() {
        long sum = 0L;
        Iterator<V> i$ = this.countMap.values().iterator();
        while (i$.hasNext()) {
            AtomicInteger value = (AtomicInteger)i$.next();
            sum += (long)value.get();
        }
        return Ints.saturatedCast((long)sum);
    }

    @Override
    public Object[] toArray() {
        return this.snapshot().toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return this.snapshot().toArray(array);
    }

    private List<E> snapshot() {
        ArrayList<E> list = Lists.newArrayListWithExpectedSize((int)this.size());
        Iterator<E> i$ = this.entrySet().iterator();
        block0 : while (i$.hasNext()) {
            Multiset.Entry entry = (Multiset.Entry)i$.next();
            E element = entry.getElement();
            int i = entry.getCount();
            do {
                if (i <= 0) continue block0;
                list.add(element);
                --i;
            } while (true);
            break;
        }
        return list;
    }

    @CanIgnoreReturnValue
    @Override
    public int add(E element, int occurrences) {
        AtomicInteger newCounter;
        AtomicInteger existingCounter;
        Preconditions.checkNotNull(element);
        if (occurrences == 0) {
            return this.count(element);
        }
        CollectPreconditions.checkPositive((int)occurrences, (String)"occurences");
        do {
            int oldValue;
            if ((existingCounter = Maps.safeGet(this.countMap, element)) == null && (existingCounter = this.countMap.putIfAbsent(element, (AtomicInteger)new AtomicInteger((int)occurrences))) == null) {
                return 0;
            }
            while ((oldValue = existingCounter.get()) != 0) {
                try {
                    int newValue = IntMath.checkedAdd((int)oldValue, (int)occurrences);
                    if (!existingCounter.compareAndSet((int)oldValue, (int)newValue)) continue;
                    return oldValue;
                }
                catch (ArithmeticException overflow) {
                    throw new IllegalArgumentException((String)("Overflow adding " + occurrences + " occurrences to a count of " + oldValue));
                }
            }
            newCounter = new AtomicInteger((int)occurrences);
            if (this.countMap.putIfAbsent(element, (AtomicInteger)newCounter) == null) return 0;
        } while (!this.countMap.replace(element, (AtomicInteger)existingCounter, (AtomicInteger)newCounter));
        return 0;
    }

    @CanIgnoreReturnValue
    @Override
    public int remove(@Nullable Object element, int occurrences) {
        int oldValue;
        int newValue;
        if (occurrences == 0) {
            return this.count((Object)element);
        }
        CollectPreconditions.checkPositive((int)occurrences, (String)"occurences");
        AtomicInteger existingCounter = Maps.safeGet(this.countMap, (Object)element);
        if (existingCounter == null) {
            return 0;
        }
        do {
            if ((oldValue = existingCounter.get()) == 0) return 0;
        } while (!existingCounter.compareAndSet((int)oldValue, (int)(newValue = Math.max((int)0, (int)(oldValue - occurrences)))));
        if (newValue != 0) return oldValue;
        this.countMap.remove((Object)element, (Object)existingCounter);
        return oldValue;
    }

    @CanIgnoreReturnValue
    public boolean removeExactly(@Nullable Object element, int occurrences) {
        int oldValue;
        int newValue;
        if (occurrences == 0) {
            return true;
        }
        CollectPreconditions.checkPositive((int)occurrences, (String)"occurences");
        AtomicInteger existingCounter = Maps.safeGet(this.countMap, (Object)element);
        if (existingCounter == null) {
            return false;
        }
        do {
            if ((oldValue = existingCounter.get()) >= occurrences) continue;
            return false;
        } while (!existingCounter.compareAndSet((int)oldValue, (int)(newValue = oldValue - occurrences)));
        if (newValue != 0) return true;
        this.countMap.remove((Object)element, (Object)existingCounter);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public int setCount(E element, int count) {
        int oldValue;
        AtomicInteger existingCounter;
        Preconditions.checkNotNull(element);
        CollectPreconditions.checkNonnegative((int)count, (String)"count");
        block0 : do {
            if ((existingCounter = Maps.safeGet(this.countMap, element)) == null) {
                if (count == 0) {
                    return 0;
                }
                existingCounter = this.countMap.putIfAbsent(element, (AtomicInteger)new AtomicInteger((int)count));
                if (existingCounter == null) {
                    return 0;
                }
            }
            do {
                if ((oldValue = existingCounter.get()) != 0) continue;
                if (count == 0) {
                    return 0;
                }
                AtomicInteger newCounter = new AtomicInteger((int)count);
                if (this.countMap.putIfAbsent(element, (AtomicInteger)newCounter) == null) return 0;
                if (!this.countMap.replace(element, (AtomicInteger)existingCounter, (AtomicInteger)newCounter)) continue block0;
                return 0;
            } while (!existingCounter.compareAndSet((int)oldValue, (int)count));
            break;
        } while (true);
        if (count != 0) return oldValue;
        this.countMap.remove(element, (Object)existingCounter);
        return oldValue;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean setCount(E element, int expectedOldCount, int newCount) {
        Preconditions.checkNotNull(element);
        CollectPreconditions.checkNonnegative((int)expectedOldCount, (String)"oldCount");
        CollectPreconditions.checkNonnegative((int)newCount, (String)"newCount");
        AtomicInteger existingCounter = Maps.safeGet(this.countMap, element);
        if (existingCounter == null) {
            if (expectedOldCount != 0) {
                return false;
            }
            if (newCount == 0) {
                return true;
            }
            if (this.countMap.putIfAbsent(element, (AtomicInteger)new AtomicInteger((int)newCount)) != null) return false;
            return true;
        }
        int oldValue = existingCounter.get();
        if (oldValue != expectedOldCount) return false;
        if (oldValue != 0) {
            if (!existingCounter.compareAndSet((int)oldValue, (int)newCount)) return false;
            if (newCount != 0) return true;
            this.countMap.remove(element, (Object)existingCounter);
            return true;
        }
        if (newCount == 0) {
            this.countMap.remove(element, (Object)existingCounter);
            return true;
        }
        AtomicInteger newCounter = new AtomicInteger((int)newCount);
        if (this.countMap.putIfAbsent(element, (AtomicInteger)newCounter) == null) return true;
        if (this.countMap.replace(element, (AtomicInteger)existingCounter, (AtomicInteger)newCounter)) return true;
        return false;
    }

    @Override
    Set<E> createElementSet() {
        Set<K> delegate = this.countMap.keySet();
        return new ForwardingSet<E>((ConcurrentHashMultiset)this, delegate){
            final /* synthetic */ Set val$delegate;
            final /* synthetic */ ConcurrentHashMultiset this$0;
            {
                this.this$0 = concurrentHashMultiset;
                this.val$delegate = set;
            }

            protected Set<E> delegate() {
                return this.val$delegate;
            }

            public boolean contains(@Nullable Object object) {
                if (object == null) return false;
                if (!com.google.common.collect.Collections2.safeContains(this.val$delegate, (Object)object)) return false;
                return true;
            }

            public boolean containsAll(Collection<?> collection) {
                return this.standardContainsAll(collection);
            }

            public boolean remove(Object object) {
                if (object == null) return false;
                if (!com.google.common.collect.Collections2.safeRemove(this.val$delegate, (Object)object)) return false;
                return true;
            }

            public boolean removeAll(Collection<?> c) {
                return this.standardRemoveAll(c);
            }
        };
    }

    @Override
    public Set<Multiset.Entry<E>> createEntrySet() {
        return new EntrySet((ConcurrentHashMultiset)this, null);
    }

    @Override
    int distinctElements() {
        return this.countMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.countMap.isEmpty();
    }

    @Override
    Iterator<Multiset.Entry<E>> entryIterator() {
        AbstractIterator<Multiset.Entry<E>> readOnlyIterator = new AbstractIterator<Multiset.Entry<E>>((ConcurrentHashMultiset)this){
            private final Iterator<java.util.Map$Entry<E, AtomicInteger>> mapEntries;
            final /* synthetic */ ConcurrentHashMultiset this$0;
            {
                this.this$0 = concurrentHashMultiset;
                this.mapEntries = ConcurrentHashMultiset.access$100((ConcurrentHashMultiset)this.this$0).entrySet().iterator();
            }

            protected Multiset.Entry<E> computeNext() {
                java.util.Map$Entry<E, AtomicInteger> mapEntry;
                int count;
                do {
                    if (this.mapEntries.hasNext()) continue;
                    return (Multiset.Entry)this.endOfData();
                } while ((count = (mapEntry = this.mapEntries.next()).getValue().get()) == 0);
                return com.google.common.collect.Multisets.immutableEntry(mapEntry.getKey(), (int)count);
            }
        };
        return new ForwardingIterator<Multiset.Entry<E>>((ConcurrentHashMultiset)this, (Iterator)readOnlyIterator){
            private Multiset.Entry<E> last;
            final /* synthetic */ Iterator val$readOnlyIterator;
            final /* synthetic */ ConcurrentHashMultiset this$0;
            {
                this.this$0 = concurrentHashMultiset;
                this.val$readOnlyIterator = iterator;
            }

            protected Iterator<Multiset.Entry<E>> delegate() {
                return this.val$readOnlyIterator;
            }

            public Multiset.Entry<E> next() {
                this.last = (Multiset.Entry)super.next();
                return this.last;
            }

            public void remove() {
                CollectPreconditions.checkRemove((boolean)(this.last != null));
                this.this$0.setCount(this.last.getElement(), (int)0);
                this.last = null;
            }
        };
    }

    @Override
    public void clear() {
        this.countMap.clear();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.countMap);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ConcurrentMap deserializedCountMap = (ConcurrentMap)stream.readObject();
        FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set((ConcurrentHashMultiset)this, (Object)deserializedCountMap);
    }

    static /* synthetic */ ConcurrentMap access$100(ConcurrentHashMultiset x0) {
        return x0.countMap;
    }
}

