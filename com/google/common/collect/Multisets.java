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
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.UnmodifiableSortedMultiset;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public final class Multisets {
    private static final Ordering<Multiset.Entry<?>> DECREASING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>(){

        public int compare(Multiset.Entry<?> entry1, Multiset.Entry<?> entry2) {
            return Ints.compare((int)entry2.getCount(), (int)entry1.getCount());
        }
    };

    private Multisets() {
    }

    public static <E> Multiset<E> unmodifiableMultiset(Multiset<? extends E> multiset) {
        if (multiset instanceof UnmodifiableMultiset) return multiset;
        if (!(multiset instanceof ImmutableMultiset)) return new UnmodifiableMultiset<E>(Preconditions.checkNotNull(multiset));
        return multiset;
    }

    @Deprecated
    public static <E> Multiset<E> unmodifiableMultiset(ImmutableMultiset<E> multiset) {
        return (Multiset)Preconditions.checkNotNull(multiset);
    }

    @Beta
    public static <E> SortedMultiset<E> unmodifiableSortedMultiset(SortedMultiset<E> sortedMultiset) {
        return new UnmodifiableSortedMultiset<E>(Preconditions.checkNotNull(sortedMultiset));
    }

    public static <E> Multiset.Entry<E> immutableEntry(@Nullable E e, int n) {
        return new ImmutableEntry<E>(e, (int)n);
    }

    @Beta
    public static <E> Multiset<E> filter(Multiset<E> unfiltered, Predicate<? super E> predicate) {
        if (!(unfiltered instanceof FilteredMultiset)) return new FilteredMultiset<E>(unfiltered, predicate);
        FilteredMultiset filtered = (FilteredMultiset)unfiltered;
        Predicate<? super E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
        return new FilteredMultiset<E>(filtered.unfiltered, combinedPredicate);
    }

    static int inferDistinctElements(Iterable<?> elements) {
        if (!(elements instanceof Multiset)) return 11;
        return ((Multiset)elements).elementSet().size();
    }

    @Beta
    public static <E> Multiset<E> union(Multiset<? extends E> multiset1, Multiset<? extends E> multiset2) {
        Preconditions.checkNotNull(multiset1);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>(multiset1, multiset2){
            final /* synthetic */ Multiset val$multiset1;
            final /* synthetic */ Multiset val$multiset2;
            {
                this.val$multiset1 = multiset;
                this.val$multiset2 = multiset2;
            }

            public boolean contains(@Nullable Object element) {
                if (this.val$multiset1.contains((Object)element)) return true;
                if (this.val$multiset2.contains((Object)element)) return true;
                return false;
            }

            public boolean isEmpty() {
                if (!this.val$multiset1.isEmpty()) return false;
                if (!this.val$multiset2.isEmpty()) return false;
                return true;
            }

            public int count(Object element) {
                return java.lang.Math.max((int)this.val$multiset1.count((Object)element), (int)this.val$multiset2.count((Object)element));
            }

            Set<E> createElementSet() {
                return com.google.common.collect.Sets.union(this.val$multiset1.elementSet(), this.val$multiset2.elementSet());
            }

            Iterator<Multiset.Entry<E>> entryIterator() {
                Iterator<Multiset.Entry<E>> iterator1 = this.val$multiset1.entrySet().iterator();
                Iterator<Multiset.Entry<E>> iterator2 = this.val$multiset2.entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<Multiset.Entry<E>>(this, iterator1, iterator2){
                    final /* synthetic */ Iterator val$iterator1;
                    final /* synthetic */ Iterator val$iterator2;
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$iterator1 = iterator;
                        this.val$iterator2 = iterator2;
                    }

                    protected Multiset.Entry<E> computeNext() {
                        E element;
                        Multiset.Entry entry2;
                        if (this.val$iterator1.hasNext()) {
                            Multiset.Entry entry1 = (Multiset.Entry)this.val$iterator1.next();
                            E element2 = entry1.getElement();
                            int count = java.lang.Math.max((int)entry1.getCount(), (int)this.this$0.val$multiset2.count(element2));
                            return Multisets.immutableEntry(element2, (int)count);
                        }
                        do {
                            if (!this.val$iterator2.hasNext()) return (Multiset.Entry)this.endOfData();
                        } while (this.this$0.val$multiset1.contains(element = (entry2 = (Multiset.Entry)this.val$iterator2.next()).getElement()));
                        return Multisets.immutableEntry(element, (int)entry2.getCount());
                    }
                };
            }

            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    public static <E> Multiset<E> intersection(Multiset<E> multiset1, Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset1);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>(multiset1, multiset2){
            final /* synthetic */ Multiset val$multiset1;
            final /* synthetic */ Multiset val$multiset2;
            {
                this.val$multiset1 = multiset;
                this.val$multiset2 = multiset2;
            }

            public int count(Object element) {
                int count1 = this.val$multiset1.count((Object)element);
                if (count1 == 0) {
                    return 0;
                }
                int n = java.lang.Math.min((int)count1, (int)this.val$multiset2.count((Object)element));
                return n;
            }

            Set<E> createElementSet() {
                return com.google.common.collect.Sets.intersection(this.val$multiset1.elementSet(), this.val$multiset2.elementSet());
            }

            Iterator<Multiset.Entry<E>> entryIterator() {
                Iterator<Multiset.Entry<E>> iterator1 = this.val$multiset1.entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<Multiset.Entry<E>>(this, iterator1){
                    final /* synthetic */ Iterator val$iterator1;
                    final /* synthetic */ 2 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$iterator1 = iterator;
                    }

                    protected Multiset.Entry<E> computeNext() {
                        E element;
                        int count;
                        Multiset.Entry entry1;
                        do {
                            if (!this.val$iterator1.hasNext()) return (Multiset.Entry)this.endOfData();
                            entry1 = (Multiset.Entry)this.val$iterator1.next();
                            element = entry1.getElement();
                        } while ((count = java.lang.Math.min((int)entry1.getCount(), (int)this.this$0.val$multiset2.count(element))) <= 0);
                        return Multisets.immutableEntry(element, (int)count);
                    }
                };
            }

            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    @Beta
    public static <E> Multiset<E> sum(Multiset<? extends E> multiset1, Multiset<? extends E> multiset2) {
        Preconditions.checkNotNull(multiset1);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>(multiset1, multiset2){
            final /* synthetic */ Multiset val$multiset1;
            final /* synthetic */ Multiset val$multiset2;
            {
                this.val$multiset1 = multiset;
                this.val$multiset2 = multiset2;
            }

            public boolean contains(@Nullable Object element) {
                if (this.val$multiset1.contains((Object)element)) return true;
                if (this.val$multiset2.contains((Object)element)) return true;
                return false;
            }

            public boolean isEmpty() {
                if (!this.val$multiset1.isEmpty()) return false;
                if (!this.val$multiset2.isEmpty()) return false;
                return true;
            }

            public int size() {
                return com.google.common.math.IntMath.saturatedAdd((int)this.val$multiset1.size(), (int)this.val$multiset2.size());
            }

            public int count(Object element) {
                return this.val$multiset1.count((Object)element) + this.val$multiset2.count((Object)element);
            }

            Set<E> createElementSet() {
                return com.google.common.collect.Sets.union(this.val$multiset1.elementSet(), this.val$multiset2.elementSet());
            }

            Iterator<Multiset.Entry<E>> entryIterator() {
                Iterator<Multiset.Entry<E>> iterator1 = this.val$multiset1.entrySet().iterator();
                Iterator<Multiset.Entry<E>> iterator2 = this.val$multiset2.entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<Multiset.Entry<E>>(this, iterator1, iterator2){
                    final /* synthetic */ Iterator val$iterator1;
                    final /* synthetic */ Iterator val$iterator2;
                    final /* synthetic */ 3 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$iterator1 = iterator;
                        this.val$iterator2 = iterator2;
                    }

                    protected Multiset.Entry<E> computeNext() {
                        E element;
                        Multiset.Entry entry2;
                        if (this.val$iterator1.hasNext()) {
                            Multiset.Entry entry1 = (Multiset.Entry)this.val$iterator1.next();
                            E element2 = entry1.getElement();
                            int count = entry1.getCount() + this.this$0.val$multiset2.count(element2);
                            return Multisets.immutableEntry(element2, (int)count);
                        }
                        do {
                            if (!this.val$iterator2.hasNext()) return (Multiset.Entry)this.endOfData();
                        } while (this.this$0.val$multiset1.contains(element = (entry2 = (Multiset.Entry)this.val$iterator2.next()).getElement()));
                        return Multisets.immutableEntry(element, (int)entry2.getCount());
                    }
                };
            }

            int distinctElements() {
                return this.elementSet().size();
            }
        };
    }

    @Beta
    public static <E> Multiset<E> difference(Multiset<E> multiset1, Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset1);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>(multiset1, multiset2){
            final /* synthetic */ Multiset val$multiset1;
            final /* synthetic */ Multiset val$multiset2;
            {
                this.val$multiset1 = multiset;
                this.val$multiset2 = multiset2;
            }

            public int count(@Nullable Object element) {
                int count1 = this.val$multiset1.count((Object)element);
                if (count1 == 0) {
                    return 0;
                }
                int n = java.lang.Math.max((int)0, (int)(count1 - this.val$multiset2.count((Object)element)));
                return n;
            }

            Iterator<Multiset.Entry<E>> entryIterator() {
                Iterator<Multiset.Entry<E>> iterator1 = this.val$multiset1.entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<Multiset.Entry<E>>(this, iterator1){
                    final /* synthetic */ Iterator val$iterator1;
                    final /* synthetic */ 4 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$iterator1 = iterator;
                    }

                    protected Multiset.Entry<E> computeNext() {
                        E element;
                        int count;
                        Multiset.Entry entry1;
                        do {
                            if (!this.val$iterator1.hasNext()) return (Multiset.Entry)this.endOfData();
                            entry1 = (Multiset.Entry)this.val$iterator1.next();
                            element = entry1.getElement();
                        } while ((count = entry1.getCount() - this.this$0.val$multiset2.count(element)) <= 0);
                        return Multisets.immutableEntry(element, (int)count);
                    }
                };
            }

            int distinctElements() {
                return Iterators.size(this.entryIterator());
            }
        };
    }

    @CanIgnoreReturnValue
    public static boolean containsOccurrences(Multiset<?> superMultiset, Multiset<?> subMultiset) {
        Multiset.Entry<?> entry;
        int superCount;
        Preconditions.checkNotNull(superMultiset);
        Preconditions.checkNotNull(subMultiset);
        Iterator<Multiset.Entry<?>> i$ = subMultiset.entrySet().iterator();
        do {
            if (!i$.hasNext()) return true;
        } while ((superCount = superMultiset.count((entry = i$.next()).getElement())) >= entry.getCount());
        return false;
    }

    @CanIgnoreReturnValue
    public static boolean retainOccurrences(Multiset<?> multisetToModify, Multiset<?> multisetToRetain) {
        return Multisets.retainOccurrencesImpl(multisetToModify, multisetToRetain);
    }

    private static <E> boolean retainOccurrencesImpl(Multiset<E> multisetToModify, Multiset<?> occurrencesToRetain) {
        Preconditions.checkNotNull(multisetToModify);
        Preconditions.checkNotNull(occurrencesToRetain);
        Iterator<Multiset.Entry<E>> entryIterator = multisetToModify.entrySet().iterator();
        boolean changed = false;
        while (entryIterator.hasNext()) {
            Multiset.Entry<E> entry = entryIterator.next();
            int retainCount = occurrencesToRetain.count(entry.getElement());
            if (retainCount == 0) {
                entryIterator.remove();
                changed = true;
                continue;
            }
            if (retainCount >= entry.getCount()) continue;
            multisetToModify.setCount(entry.getElement(), (int)retainCount);
            changed = true;
        }
        return changed;
    }

    @CanIgnoreReturnValue
    public static boolean removeOccurrences(Multiset<?> multisetToModify, Iterable<?> occurrencesToRemove) {
        if (occurrencesToRemove instanceof Multiset) {
            return Multisets.removeOccurrences(multisetToModify, (Multiset)occurrencesToRemove);
        }
        Preconditions.checkNotNull(multisetToModify);
        Preconditions.checkNotNull(occurrencesToRemove);
        boolean changed = false;
        Iterator<?> i$ = occurrencesToRemove.iterator();
        while (i$.hasNext()) {
            ? o = i$.next();
            changed |= multisetToModify.remove(o);
        }
        return changed;
    }

    @CanIgnoreReturnValue
    public static boolean removeOccurrences(Multiset<?> multisetToModify, Multiset<?> occurrencesToRemove) {
        Preconditions.checkNotNull(multisetToModify);
        Preconditions.checkNotNull(occurrencesToRemove);
        boolean changed = false;
        Iterator<Multiset.Entry<?>> entryIterator = multisetToModify.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Multiset.Entry<?> entry = entryIterator.next();
            int removeCount = occurrencesToRemove.count(entry.getElement());
            if (removeCount >= entry.getCount()) {
                entryIterator.remove();
                changed = true;
                continue;
            }
            if (removeCount <= 0) continue;
            multisetToModify.remove(entry.getElement(), (int)removeCount);
            changed = true;
        }
        return changed;
    }

    static boolean equalsImpl(Multiset<?> multiset, @Nullable Object object) {
        Multiset.Entry<E> entry;
        if (object == multiset) {
            return true;
        }
        if (!(object instanceof Multiset)) return false;
        Multiset that = (Multiset)object;
        if (multiset.size() != that.size()) return false;
        if (multiset.entrySet().size() != that.entrySet().size()) {
            return false;
        }
        Iterator<Multiset.Entry<E>> i$ = that.entrySet().iterator();
        do {
            if (!i$.hasNext()) return true;
        } while (multiset.count((entry = i$.next()).getElement()) == entry.getCount());
        return false;
    }

    static <E> boolean addAllImpl(Multiset<E> self, Collection<? extends E> elements) {
        if (elements.isEmpty()) {
            return false;
        }
        if (!(elements instanceof Multiset)) {
            Iterators.addAll(self, elements.iterator());
            return true;
        }
        Multiset<E> that = Multisets.cast(elements);
        Iterator<Multiset.Entry<E>> i$ = that.entrySet().iterator();
        while (i$.hasNext()) {
            Multiset.Entry<E> entry = i$.next();
            self.add(entry.getElement(), (int)entry.getCount());
        }
        return true;
    }

    static boolean removeAllImpl(Multiset<?> self, Collection<?> elementsToRemove) {
        Collection<?> collection = elementsToRemove instanceof Multiset ? ((Multiset)elementsToRemove).elementSet() : elementsToRemove;
        return self.elementSet().removeAll(collection);
    }

    static boolean retainAllImpl(Multiset<?> self, Collection<?> elementsToRetain) {
        Preconditions.checkNotNull(elementsToRetain);
        Collection<?> collection = elementsToRetain instanceof Multiset ? ((Multiset)elementsToRetain).elementSet() : elementsToRetain;
        return self.elementSet().retainAll(collection);
    }

    static <E> int setCountImpl(Multiset<E> self, E element, int count) {
        CollectPreconditions.checkNonnegative((int)count, (String)"count");
        int oldCount = self.count(element);
        int delta = count - oldCount;
        if (delta > 0) {
            self.add(element, (int)delta);
            return oldCount;
        }
        if (delta >= 0) return oldCount;
        self.remove(element, (int)(-delta));
        return oldCount;
    }

    static <E> boolean setCountImpl(Multiset<E> self, E element, int oldCount, int newCount) {
        CollectPreconditions.checkNonnegative((int)oldCount, (String)"oldCount");
        CollectPreconditions.checkNonnegative((int)newCount, (String)"newCount");
        if (self.count(element) != oldCount) return false;
        self.setCount(element, (int)newCount);
        return true;
    }

    static <E> Iterator<E> iteratorImpl(Multiset<E> multiset) {
        return new MultisetIteratorImpl<E>(multiset, multiset.entrySet().iterator());
    }

    static int sizeImpl(Multiset<?> multiset) {
        long size = 0L;
        Iterator<Multiset.Entry<?>> i$ = multiset.entrySet().iterator();
        while (i$.hasNext()) {
            Multiset.Entry<?> entry = i$.next();
            size += (long)entry.getCount();
        }
        return Ints.saturatedCast((long)size);
    }

    static <T> Multiset<T> cast(Iterable<T> iterable) {
        return (Multiset)iterable;
    }

    @Beta
    public static <E> ImmutableMultiset<E> copyHighestCountFirst(Multiset<E> multiset) {
        ImmutableList<Multiset.Entry<E>> sortedEntries = DECREASING_COUNT_ORDERING.immutableSortedCopy(multiset.entrySet());
        return ImmutableMultiset.copyFromEntries(sortedEntries);
    }
}

