/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractBiMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class AbstractBiMap<K, V>
extends ForwardingMap<K, V>
implements BiMap<K, V>,
Serializable {
    private transient Map<K, V> delegate;
    @RetainedWith
    transient AbstractBiMap<V, K> inverse;
    private transient Set<K> keySet;
    private transient Set<V> valueSet;
    private transient Set<Map.Entry<K, V>> entrySet;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    AbstractBiMap(Map<K, V> forward, Map<V, K> backward) {
        this.setDelegates(forward, backward);
    }

    private AbstractBiMap(Map<K, V> backward, AbstractBiMap<V, K> forward) {
        this.delegate = backward;
        this.inverse = forward;
    }

    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }

    @CanIgnoreReturnValue
    K checkKey(@Nullable K key) {
        return (K)key;
    }

    @CanIgnoreReturnValue
    V checkValue(@Nullable V value) {
        return (V)value;
    }

    void setDelegates(Map<K, V> forward, Map<V, K> backward) {
        Preconditions.checkState((boolean)(this.delegate == null));
        Preconditions.checkState((boolean)(this.inverse == null));
        Preconditions.checkArgument((boolean)forward.isEmpty());
        Preconditions.checkArgument((boolean)backward.isEmpty());
        Preconditions.checkArgument((boolean)(forward != backward));
        this.delegate = forward;
        this.inverse = this.makeInverse(backward);
    }

    AbstractBiMap<V, K> makeInverse(Map<V, K> backward) {
        return new Inverse<V, K>(backward, this);
    }

    void setInverse(AbstractBiMap<V, K> inverse) {
        this.inverse = inverse;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return this.inverse.containsKey((Object)value);
    }

    @CanIgnoreReturnValue
    @Override
    public V put(@Nullable K key, @Nullable V value) {
        return (V)this.putInBothMaps(key, value, (boolean)false);
    }

    @CanIgnoreReturnValue
    @Override
    public V forcePut(@Nullable K key, @Nullable V value) {
        return (V)this.putInBothMaps(key, value, (boolean)true);
    }

    private V putInBothMaps(@Nullable K key, @Nullable V value, boolean force) {
        this.checkKey(key);
        this.checkValue(value);
        boolean containedKey = this.containsKey(key);
        if (containedKey && Objects.equal(value, this.get(key))) {
            return (V)value;
        }
        if (force) {
            this.inverse().remove(value);
        } else {
            Preconditions.checkArgument((boolean)(!this.containsValue(value)), (String)"value already present: %s", value);
        }
        V oldValue = this.delegate.put(key, value);
        this.updateInverseMap(key, (boolean)containedKey, oldValue, value);
        return (V)oldValue;
    }

    private void updateInverseMap(K key, boolean containedKey, V oldValue, V newValue) {
        if (containedKey) {
            this.removeFromInverseMap(oldValue);
        }
        this.inverse.delegate.put(newValue, key);
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object key) {
        V v;
        if (this.containsKey((Object)key)) {
            v = (V)this.removeFromBothMaps((Object)key);
            return (V)((V)v);
        }
        v = null;
        return (V)v;
    }

    @CanIgnoreReturnValue
    private V removeFromBothMaps(Object key) {
        V oldValue = this.delegate.remove((Object)key);
        this.removeFromInverseMap(oldValue);
        return (V)oldValue;
    }

    private void removeFromInverseMap(V oldValue) {
        this.inverse.delegate.remove(oldValue);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Iterator<Map.Entry<K, V>> i$ = map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
        this.inverse.delegate.clear();
    }

    @Override
    public BiMap<V, K> inverse() {
        return this.inverse;
    }

    @Override
    public Set<K> keySet() {
        KeySet keySet;
        KeySet result = this.keySet;
        if (result == null) {
            keySet = this.keySet = new KeySet((AbstractBiMap)this, null);
            return keySet;
        }
        keySet = result;
        return keySet;
    }

    @Override
    public Set<V> values() {
        ValueSet valueSet;
        ValueSet result = this.valueSet;
        if (result == null) {
            valueSet = this.valueSet = new ValueSet((AbstractBiMap)this, null);
            return valueSet;
        }
        valueSet = result;
        return valueSet;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet entrySet;
        EntrySet result = this.entrySet;
        if (result == null) {
            entrySet = this.entrySet = new EntrySet((AbstractBiMap)this, null);
            return entrySet;
        }
        entrySet = result;
        return entrySet;
    }

    Iterator<Map.Entry<K, V>> entrySetIterator() {
        Iterator<Map.Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        return new Iterator<Map.Entry<K, V>>((AbstractBiMap)this, iterator){
            Map.Entry<K, V> entry;
            final /* synthetic */ Iterator val$iterator;
            final /* synthetic */ AbstractBiMap this$0;
            {
                this.this$0 = abstractBiMap;
                this.val$iterator = iterator;
            }

            public boolean hasNext() {
                return this.val$iterator.hasNext();
            }

            public Map.Entry<K, V> next() {
                this.entry = (Map.Entry)this.val$iterator.next();
                return new com.google.common.collect.AbstractBiMap$BiMapEntry((AbstractBiMap)this.this$0, this.entry);
            }

            public void remove() {
                com.google.common.collect.CollectPreconditions.checkRemove((boolean)(this.entry != null));
                V value = this.entry.getValue();
                this.val$iterator.remove();
                AbstractBiMap.access$600((AbstractBiMap)this.this$0, value);
            }
        };
    }

    static /* synthetic */ Map access$100(AbstractBiMap x0) {
        return x0.delegate;
    }

    static /* synthetic */ Object access$200(AbstractBiMap x0, Object x1) {
        return x0.removeFromBothMaps((Object)x1);
    }

    static /* synthetic */ void access$500(AbstractBiMap x0, Object x1, boolean x2, Object x3, Object x4) {
        x0.updateInverseMap(x1, (boolean)x2, x3, x4);
    }

    static /* synthetic */ void access$600(AbstractBiMap x0, Object x1) {
        x0.removeFromInverseMap(x1);
    }
}

