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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractSetMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public final class LinkedHashMultimap<K, V>
extends AbstractSetMultimap<K, V> {
    private static final int DEFAULT_KEY_CAPACITY = 16;
    private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
    @VisibleForTesting
    static final double VALUE_SET_LOAD_FACTOR = 1.0;
    @VisibleForTesting
    transient int valueSetCapacity = 2;
    private transient ValueEntry<K, V> multimapHeaderEntry;
    @GwtIncompatible
    private static final long serialVersionUID = 1L;

    public static <K, V> LinkedHashMultimap<K, V> create() {
        return new LinkedHashMultimap<K, V>((int)16, (int)2);
    }

    public static <K, V> LinkedHashMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey) {
        return new LinkedHashMultimap<K, V>((int)Maps.capacity((int)expectedKeys), (int)Maps.capacity((int)expectedValuesPerKey));
    }

    public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        LinkedHashMultimap<K, V> result = LinkedHashMultimap.create((int)multimap.keySet().size(), (int)2);
        result.putAll(multimap);
        return result;
    }

    private static <K, V> void succeedsInValueSet(ValueSetLink<K, V> pred, ValueSetLink<K, V> succ) {
        pred.setSuccessorInValueSet(succ);
        succ.setPredecessorInValueSet(pred);
    }

    private static <K, V> void succeedsInMultimap(ValueEntry<K, V> pred, ValueEntry<K, V> succ) {
        pred.setSuccessorInMultimap(succ);
        succ.setPredecessorInMultimap(pred);
    }

    private static <K, V> void deleteFromValueSet(ValueSetLink<K, V> entry) {
        LinkedHashMultimap.succeedsInValueSet(entry.getPredecessorInValueSet(), entry.getSuccessorInValueSet());
    }

    private static <K, V> void deleteFromMultimap(ValueEntry<K, V> entry) {
        LinkedHashMultimap.succeedsInMultimap(entry.getPredecessorInMultimap(), entry.getSuccessorInMultimap());
    }

    private LinkedHashMultimap(int keyCapacity, int valueSetCapacity) {
        super(new LinkedHashMap<K, V>((int)keyCapacity));
        CollectPreconditions.checkNonnegative((int)valueSetCapacity, (String)"expectedValuesPerKey");
        this.valueSetCapacity = valueSetCapacity;
        this.multimapHeaderEntry = new ValueEntry<Object, Object>(null, null, (int)0, null);
        LinkedHashMultimap.succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    }

    @Override
    Set<V> createCollection() {
        return new LinkedHashSet<E>((int)this.valueSetCapacity);
    }

    @Override
    Collection<V> createCollection(K key) {
        return new ValueSet((LinkedHashMultimap)this, key, (int)this.valueSetCapacity);
    }

    @CanIgnoreReturnValue
    @Override
    public Set<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
        return super.replaceValues(key, values);
    }

    @Override
    public Set<Map.Entry<K, V>> entries() {
        return super.entries();
    }

    @Override
    public Set<K> keySet() {
        return super.keySet();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new Iterator<Map.Entry<K, V>>((LinkedHashMultimap)this){
            ValueEntry<K, V> nextEntry;
            ValueEntry<K, V> toRemove;
            final /* synthetic */ LinkedHashMultimap this$0;
            {
                this.this$0 = linkedHashMultimap;
                this.nextEntry = LinkedHashMultimap.access$300((LinkedHashMultimap)this.this$0).successorInMultimap;
            }

            public boolean hasNext() {
                if (this.nextEntry == LinkedHashMultimap.access$300((LinkedHashMultimap)this.this$0)) return false;
                return true;
            }

            public Map.Entry<K, V> next() {
                if (!this.hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                ValueEntry<K, V> result = this.nextEntry;
                this.toRemove = result;
                this.nextEntry = this.nextEntry.successorInMultimap;
                return result;
            }

            public void remove() {
                CollectPreconditions.checkRemove((boolean)(this.toRemove != null));
                this.this$0.remove(this.toRemove.getKey(), this.toRemove.getValue());
                this.toRemove = null;
            }
        };
    }

    @Override
    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entryIterator());
    }

    @Override
    public void clear() {
        super.clear();
        LinkedHashMultimap.succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt((int)this.keySet().size());
        for (K key : this.keySet()) {
            stream.writeObject(key);
        }
        stream.writeInt((int)this.size());
        Iterator<Object> i$ = this.entries().iterator();
        while (i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.multimapHeaderEntry = new ValueEntry<Object, Object>(null, null, (int)0, null);
        LinkedHashMultimap.succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
        this.valueSetCapacity = 2;
        int distinctKeys = stream.readInt();
        LinkedHashMap<Object, Collection<V>> map = new LinkedHashMap<Object, Collection<V>>();
        for (int i = 0; i < distinctKeys; ++i) {
            Object key = stream.readObject();
            map.put(key, this.createCollection(key));
        }
        int entries = stream.readInt();
        int i = 0;
        do {
            if (i >= entries) {
                this.setMap(map);
                return;
            }
            Object key = stream.readObject();
            Object value = stream.readObject();
            ((Collection)map.get((Object)key)).add(value);
            ++i;
        } while (true);
    }

    static /* synthetic */ void access$200(ValueSetLink x0, ValueSetLink x1) {
        LinkedHashMultimap.succeedsInValueSet(x0, x1);
    }

    static /* synthetic */ ValueEntry access$300(LinkedHashMultimap x0) {
        return x0.multimapHeaderEntry;
    }

    static /* synthetic */ void access$400(ValueEntry x0, ValueEntry x1) {
        LinkedHashMultimap.succeedsInMultimap(x0, x1);
    }

    static /* synthetic */ void access$500(ValueSetLink x0) {
        LinkedHashMultimap.deleteFromValueSet(x0);
    }

    static /* synthetic */ void access$600(ValueEntry x0) {
        LinkedHashMultimap.deleteFromMultimap(x0);
    }
}

