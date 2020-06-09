/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

@GwtIncompatible
public abstract class ForwardingNavigableMap<K, V>
extends ForwardingSortedMap<K, V>
implements NavigableMap<K, V> {
    protected ForwardingNavigableMap() {
    }

    @Override
    protected abstract NavigableMap<K, V> delegate();

    @Override
    public Map.Entry<K, V> lowerEntry(K key) {
        return this.delegate().lowerEntry(key);
    }

    protected Map.Entry<K, V> standardLowerEntry(K key) {
        return this.headMap(key, (boolean)false).lastEntry();
    }

    @Override
    public K lowerKey(K key) {
        return (K)this.delegate().lowerKey(key);
    }

    protected K standardLowerKey(K key) {
        return (K)Maps.keyOrNull(this.lowerEntry(key));
    }

    @Override
    public Map.Entry<K, V> floorEntry(K key) {
        return this.delegate().floorEntry(key);
    }

    protected Map.Entry<K, V> standardFloorEntry(K key) {
        return this.headMap(key, (boolean)true).lastEntry();
    }

    @Override
    public K floorKey(K key) {
        return (K)this.delegate().floorKey(key);
    }

    protected K standardFloorKey(K key) {
        return (K)Maps.keyOrNull(this.floorEntry(key));
    }

    @Override
    public Map.Entry<K, V> ceilingEntry(K key) {
        return this.delegate().ceilingEntry(key);
    }

    protected Map.Entry<K, V> standardCeilingEntry(K key) {
        return this.tailMap(key, (boolean)true).firstEntry();
    }

    @Override
    public K ceilingKey(K key) {
        return (K)this.delegate().ceilingKey(key);
    }

    protected K standardCeilingKey(K key) {
        return (K)Maps.keyOrNull(this.ceilingEntry(key));
    }

    @Override
    public Map.Entry<K, V> higherEntry(K key) {
        return this.delegate().higherEntry(key);
    }

    protected Map.Entry<K, V> standardHigherEntry(K key) {
        return this.tailMap(key, (boolean)false).firstEntry();
    }

    @Override
    public K higherKey(K key) {
        return (K)this.delegate().higherKey(key);
    }

    protected K standardHigherKey(K key) {
        return (K)Maps.keyOrNull(this.higherEntry(key));
    }

    @Override
    public Map.Entry<K, V> firstEntry() {
        return this.delegate().firstEntry();
    }

    protected Map.Entry<K, V> standardFirstEntry() {
        return (Map.Entry)Iterables.getFirst(this.entrySet(), null);
    }

    protected K standardFirstKey() {
        Map.Entry<K, V> entry = this.firstEntry();
        if (entry != null) return (K)entry.getKey();
        throw new NoSuchElementException();
    }

    @Override
    public Map.Entry<K, V> lastEntry() {
        return this.delegate().lastEntry();
    }

    protected Map.Entry<K, V> standardLastEntry() {
        return (Map.Entry)Iterables.getFirst(this.descendingMap().entrySet(), null);
    }

    protected K standardLastKey() {
        Map.Entry<K, V> entry = this.lastEntry();
        if (entry != null) return (K)entry.getKey();
        throw new NoSuchElementException();
    }

    @Override
    public Map.Entry<K, V> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }

    protected Map.Entry<K, V> standardPollFirstEntry() {
        return Iterators.pollNext(this.entrySet().iterator());
    }

    @Override
    public Map.Entry<K, V> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }

    protected Map.Entry<K, V> standardPollLastEntry() {
        return Iterators.pollNext(this.descendingMap().entrySet().iterator());
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return this.delegate().descendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return this.delegate().navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.delegate().descendingKeySet();
    }

    @Beta
    protected NavigableSet<K> standardDescendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }

    @Override
    protected SortedMap<K, V> standardSubMap(K fromKey, K toKey) {
        return this.subMap(fromKey, (boolean)true, toKey, (boolean)false);
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return this.delegate().subMap(fromKey, (boolean)fromInclusive, toKey, (boolean)toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return this.delegate().headMap(toKey, (boolean)inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return this.delegate().tailMap(fromKey, (boolean)inclusive);
    }

    protected SortedMap<K, V> standardHeadMap(K toKey) {
        return this.headMap(toKey, (boolean)false);
    }

    protected SortedMap<K, V> standardTailMap(K fromKey) {
        return this.tailMap(fromKey, (boolean)true);
    }
}

