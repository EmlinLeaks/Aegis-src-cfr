/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class AbstractMapBasedMultimap<K, V>
extends AbstractMultimap<K, V>
implements Serializable {
    private transient Map<K, Collection<V>> map;
    private transient int totalSize;
    private static final long serialVersionUID = 2447537837011683357L;

    protected AbstractMapBasedMultimap(Map<K, Collection<V>> map) {
        Preconditions.checkArgument((boolean)map.isEmpty());
        this.map = map;
    }

    final void setMap(Map<K, Collection<V>> map) {
        this.map = map;
        this.totalSize = 0;
        Iterator<Collection<Collection<V>>> i$ = map.values().iterator();
        while (i$.hasNext()) {
            Collection<Collection<V>> values = i$.next();
            Preconditions.checkArgument((boolean)(!values.isEmpty()));
            this.totalSize += values.size();
        }
    }

    Collection<V> createUnmodifiableEmptyCollection() {
        return this.unmodifiableCollectionSubclass(this.createCollection());
    }

    abstract Collection<V> createCollection();

    Collection<V> createCollection(@Nullable K key) {
        return this.createCollection();
    }

    Map<K, Collection<V>> backingMap() {
        return this.map;
    }

    @Override
    public int size() {
        return this.totalSize;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.map.containsKey((Object)key);
    }

    @Override
    public boolean put(@Nullable K key, @Nullable V value) {
        Collection<V> collection = this.map.get(key);
        if (collection == null) {
            collection = this.createCollection(key);
            if (!collection.add(value)) throw new AssertionError((Object)"New Collection violated the Collection spec");
            ++this.totalSize;
            this.map.put(key, collection);
            return true;
        }
        if (!collection.add(value)) return false;
        ++this.totalSize;
        return true;
    }

    private Collection<V> getOrCreateCollection(@Nullable K key) {
        Collection<V> collection = this.map.get(key);
        if (collection != null) return collection;
        collection = this.createCollection(key);
        this.map.put(key, collection);
        return collection;
    }

    @Override
    public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
        Iterator<V> iterator = values.iterator();
        if (!iterator.hasNext()) {
            return this.removeAll(key);
        }
        Collection<V> collection = this.getOrCreateCollection(key);
        Collection<V> oldValues = this.createCollection();
        oldValues.addAll(collection);
        this.totalSize -= collection.size();
        collection.clear();
        while (iterator.hasNext()) {
            if (!collection.add(iterator.next())) continue;
            ++this.totalSize;
        }
        return this.unmodifiableCollectionSubclass(oldValues);
    }

    @Override
    public Collection<V> removeAll(@Nullable Object key) {
        Collection<V> collection = this.map.remove((Object)key);
        if (collection == null) {
            return this.createUnmodifiableEmptyCollection();
        }
        Collection<V> output = this.createCollection();
        output.addAll(collection);
        this.totalSize -= collection.size();
        collection.clear();
        return this.unmodifiableCollectionSubclass(output);
    }

    Collection<V> unmodifiableCollectionSubclass(Collection<V> collection) {
        if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet)collection);
        }
        if (collection instanceof Set) {
            return Collections.unmodifiableSet((Set)collection);
        }
        if (!(collection instanceof List)) return Collections.unmodifiableCollection(collection);
        return Collections.unmodifiableList((List)collection);
    }

    @Override
    public void clear() {
        Iterator<Collection<V>> i$ = this.map.values().iterator();
        do {
            if (!i$.hasNext()) {
                this.map.clear();
                this.totalSize = 0;
                return;
            }
            Collection<V> collection = i$.next();
            collection.clear();
        } while (true);
    }

    @Override
    public Collection<V> get(@Nullable K key) {
        Collection<V> collection = this.map.get(key);
        if (collection != null) return this.wrapCollection(key, collection);
        collection = this.createCollection(key);
        return this.wrapCollection(key, collection);
    }

    Collection<V> wrapCollection(@Nullable K key, Collection<V> collection) {
        if (collection instanceof SortedSet) {
            return new WrappedSortedSet((AbstractMapBasedMultimap)this, key, (SortedSet)((SortedSet)collection), null);
        }
        if (collection instanceof Set) {
            return new WrappedSet((AbstractMapBasedMultimap)this, key, (Set)collection);
        }
        if (!(collection instanceof List)) return new WrappedCollection((AbstractMapBasedMultimap)this, key, collection, null);
        return this.wrapList(key, (List)((List)collection), null);
    }

    private List<V> wrapList(@Nullable K key, List<V> list, @Nullable AbstractMapBasedMultimap<K, V> ancestor) {
        AbstractMapBasedMultimap abstractMapBasedMultimap;
        if (list instanceof RandomAccess) {
            abstractMapBasedMultimap = new RandomAccessWrappedList((AbstractMapBasedMultimap)this, key, list, ancestor);
            return abstractMapBasedMultimap;
        }
        abstractMapBasedMultimap = new WrappedList((AbstractMapBasedMultimap)this, key, list, ancestor);
        return abstractMapBasedMultimap;
    }

    private Iterator<V> iteratorOrListIterator(Collection<V> collection) {
        Iterator<V> iterator;
        if (collection instanceof List) {
            iterator = ((List)collection).listIterator();
            return iterator;
        }
        iterator = collection.iterator();
        return iterator;
    }

    @Override
    Set<K> createKeySet() {
        Set<K> set;
        if (this.map instanceof SortedMap) {
            set = new SortedKeySet((AbstractMapBasedMultimap)this, (SortedMap)this.map);
            return set;
        }
        set = new KeySet((AbstractMapBasedMultimap)this, this.map);
        return set;
    }

    private void removeValuesForKey(Object key) {
        Collection<V> collection = Maps.safeRemove(this.map, (Object)key);
        if (collection == null) return;
        int count = collection.size();
        collection.clear();
        this.totalSize -= count;
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    Iterator<V> valueIterator() {
        return new AbstractMapBasedMultimap<K, V>((AbstractMapBasedMultimap)this){
            final /* synthetic */ AbstractMapBasedMultimap this$0;
            {
                this.this$0 = abstractMapBasedMultimap;
                super((AbstractMapBasedMultimap)abstractMapBasedMultimap);
            }

            V output(K key, V value) {
                return (V)value;
            }
        };
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return super.entries();
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new AbstractMapBasedMultimap<K, V>((AbstractMapBasedMultimap)this){
            final /* synthetic */ AbstractMapBasedMultimap this$0;
            {
                this.this$0 = abstractMapBasedMultimap;
                super((AbstractMapBasedMultimap)abstractMapBasedMultimap);
            }

            Map.Entry<K, V> output(K key, V value) {
                return Maps.immutableEntry(key, value);
            }
        };
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        Map<K, Collection<V>> map;
        if (this.map instanceof SortedMap) {
            map = new SortedAsMap((AbstractMapBasedMultimap)this, (SortedMap)this.map);
            return map;
        }
        map = new AsMap((AbstractMapBasedMultimap)this, this.map);
        return map;
    }

    static /* synthetic */ Map access$000(AbstractMapBasedMultimap x0) {
        return x0.map;
    }

    static /* synthetic */ Iterator access$100(AbstractMapBasedMultimap x0, Collection x1) {
        return x0.iteratorOrListIterator(x1);
    }

    static /* synthetic */ int access$210(AbstractMapBasedMultimap x0) {
        return x0.totalSize--;
    }

    static /* synthetic */ int access$208(AbstractMapBasedMultimap x0) {
        return x0.totalSize++;
    }

    static /* synthetic */ int access$212(AbstractMapBasedMultimap x0, int x1) {
        return x0.totalSize += x1;
    }

    static /* synthetic */ int access$220(AbstractMapBasedMultimap x0, int x1) {
        return x0.totalSize -= x1;
    }

    static /* synthetic */ List access$300(AbstractMapBasedMultimap x0, Object x1, List x2, WrappedCollection x3) {
        return x0.wrapList((Object)x1, (List)x2, (WrappedCollection)x3);
    }

    static /* synthetic */ void access$400(AbstractMapBasedMultimap x0, Object x1) {
        x0.removeValuesForKey((Object)x1);
    }
}

