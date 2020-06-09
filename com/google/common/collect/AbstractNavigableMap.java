/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.AbstractNavigableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtIncompatible
abstract class AbstractNavigableMap<K, V>
extends Maps.IteratorBasedAbstractMap<K, V>
implements NavigableMap<K, V> {
    AbstractNavigableMap() {
    }

    @Nullable
    @Override
    public abstract V get(@Nullable Object var1);

    @Nullable
    @Override
    public Map.Entry<K, V> firstEntry() {
        return (Map.Entry)Iterators.getNext(this.entryIterator(), null);
    }

    @Nullable
    @Override
    public Map.Entry<K, V> lastEntry() {
        return (Map.Entry)Iterators.getNext(this.descendingEntryIterator(), null);
    }

    @Nullable
    @Override
    public Map.Entry<K, V> pollFirstEntry() {
        return Iterators.pollNext(this.entryIterator());
    }

    @Nullable
    @Override
    public Map.Entry<K, V> pollLastEntry() {
        return Iterators.pollNext(this.descendingEntryIterator());
    }

    @Override
    public K firstKey() {
        Map.Entry<K, V> entry = this.firstEntry();
        if (entry != null) return (K)entry.getKey();
        throw new NoSuchElementException();
    }

    @Override
    public K lastKey() {
        Map.Entry<K, V> entry = this.lastEntry();
        if (entry != null) return (K)entry.getKey();
        throw new NoSuchElementException();
    }

    @Nullable
    @Override
    public Map.Entry<K, V> lowerEntry(K key) {
        return this.headMap(key, (boolean)false).lastEntry();
    }

    @Nullable
    @Override
    public Map.Entry<K, V> floorEntry(K key) {
        return this.headMap(key, (boolean)true).lastEntry();
    }

    @Nullable
    @Override
    public Map.Entry<K, V> ceilingEntry(K key) {
        return this.tailMap(key, (boolean)true).firstEntry();
    }

    @Nullable
    @Override
    public Map.Entry<K, V> higherEntry(K key) {
        return this.tailMap(key, (boolean)false).firstEntry();
    }

    @Override
    public K lowerKey(K key) {
        return (K)Maps.keyOrNull(this.lowerEntry(key));
    }

    @Override
    public K floorKey(K key) {
        return (K)Maps.keyOrNull(this.floorEntry(key));
    }

    @Override
    public K ceilingKey(K key) {
        return (K)Maps.keyOrNull(this.ceilingEntry(key));
    }

    @Override
    public K higherKey(K key) {
        return (K)Maps.keyOrNull(this.higherEntry(key));
    }

    abstract Iterator<Map.Entry<K, V>> descendingEntryIterator();

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return this.subMap(fromKey, (boolean)true, toKey, (boolean)false);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return this.headMap(toKey, (boolean)false);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return this.tailMap(fromKey, (boolean)true);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return new Maps.NavigableKeySet<K, V>(this);
    }

    @Override
    public Set<K> keySet() {
        return this.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new DescendingMap((AbstractNavigableMap)this, null);
    }
}

