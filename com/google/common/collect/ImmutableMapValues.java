/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapValues;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.j2objc.annotations.Weak;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class ImmutableMapValues<K, V>
extends ImmutableCollection<V> {
    @Weak
    private final ImmutableMap<K, V> map;

    ImmutableMapValues(ImmutableMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public UnmodifiableIterator<V> iterator() {
        return new UnmodifiableIterator<V>((ImmutableMapValues)this){
            final UnmodifiableIterator<java.util.Map$Entry<K, V>> entryItr;
            final /* synthetic */ ImmutableMapValues this$0;
            {
                this.this$0 = immutableMapValues;
                this.entryItr = ((ImmutableSet)ImmutableMapValues.access$000((ImmutableMapValues)this.this$0).entrySet()).iterator();
            }

            public boolean hasNext() {
                return this.entryItr.hasNext();
            }

            public V next() {
                return (V)((java.util.Map$Entry)this.entryItr.next()).getValue();
            }
        };
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (object == null) return false;
        if (!Iterators.contains(this.iterator(), (Object)object)) return false;
        return true;
    }

    @Override
    boolean isPartialView() {
        return true;
    }

    @Override
    public ImmutableList<V> asList() {
        ImmutableList<E> entryList = ((ImmutableSet)this.map.entrySet()).asList();
        return new ImmutableAsList<V>((ImmutableMapValues)this, entryList){
            final /* synthetic */ ImmutableList val$entryList;
            final /* synthetic */ ImmutableMapValues this$0;
            {
                this.this$0 = immutableMapValues;
                this.val$entryList = immutableList;
            }

            public V get(int index) {
                return (V)((java.util.Map$Entry)this.val$entryList.get((int)index)).getValue();
            }

            ImmutableCollection<V> delegateCollection() {
                return this.this$0;
            }
        };
    }

    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new SerializedForm<V>(this.map);
    }

    static /* synthetic */ ImmutableMap access$000(ImmutableMapValues x0) {
        return x0.map;
    }
}

