/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMapEntry<K, V>
implements Map.Entry<K, V> {
    AbstractMapEntry() {
    }

    @Override
    public abstract K getKey();

    @Override
    public abstract V getValue();

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Map.Entry)) return false;
        Map.Entry that = (Map.Entry)object;
        if (!Objects.equal(this.getKey(), that.getKey())) return false;
        if (!Objects.equal(this.getValue(), that.getValue())) return false;
        return true;
    }

    @Override
    public int hashCode() {
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

    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}

