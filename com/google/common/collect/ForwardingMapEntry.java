/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingObject;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMapEntry<K, V>
extends ForwardingObject
implements Map.Entry<K, V> {
    protected ForwardingMapEntry() {
    }

    @Override
    protected abstract Map.Entry<K, V> delegate();

    @Override
    public K getKey() {
        return (K)this.delegate().getKey();
    }

    @Override
    public V getValue() {
        return (V)this.delegate().getValue();
    }

    @Override
    public V setValue(V value) {
        return (V)this.delegate().setValue(value);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return this.delegate().equals((Object)object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected boolean standardEquals(@Nullable Object object) {
        if (!(object instanceof Map.Entry)) return false;
        Map.Entry that = (Map.Entry)object;
        if (!Objects.equal(this.getKey(), that.getKey())) return false;
        if (!Objects.equal(this.getValue(), that.getValue())) return false;
        return true;
    }

    protected int standardHashCode() {
        int n;
        K k = this.getKey();
        V v = this.getValue();
        int n2 = k == null ? 0 : k.hashCode();
        if (v == null) {
            n = 0;
            return n2 ^ n;
        }
        n = v.hashCode();
        return n2 ^ n;
    }

    @Beta
    protected String standardToString() {
        return this.getKey() + "=" + this.getValue();
    }
}

