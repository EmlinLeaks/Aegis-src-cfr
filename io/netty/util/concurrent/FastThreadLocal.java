/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class FastThreadLocal<V> {
    private static final int variablesToRemoveIndex = InternalThreadLocalMap.nextVariableIndex();
    private final int index = InternalThreadLocalMap.nextVariableIndex();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void removeAll() {
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
        if (threadLocalMap == null) {
            return;
        }
        try {
            FastThreadLocal[] variablesToRemoveArray;
            Object v = threadLocalMap.indexedVariable((int)variablesToRemoveIndex);
            if (v == null) return;
            if (v == InternalThreadLocalMap.UNSET) return;
            Set variablesToRemove = (Set)v;
            FastThreadLocal[] arrfastThreadLocal = variablesToRemoveArray = variablesToRemove.toArray(new FastThreadLocal[0]);
            int n = arrfastThreadLocal.length;
            int n2 = 0;
            while (n2 < n) {
                FastThreadLocal tlv = arrfastThreadLocal[n2];
                tlv.remove((InternalThreadLocalMap)threadLocalMap);
                ++n2;
            }
            return;
        }
        finally {
            InternalThreadLocalMap.remove();
        }
    }

    public static int size() {
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
        if (threadLocalMap != null) return threadLocalMap.size();
        return 0;
    }

    public static void destroy() {
        InternalThreadLocalMap.destroy();
    }

    private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
        Set<K> variablesToRemove;
        Object v = threadLocalMap.indexedVariable((int)variablesToRemoveIndex);
        if (v == InternalThreadLocalMap.UNSET || v == null) {
            variablesToRemove = Collections.newSetFromMap(new IdentityHashMap<K, V>());
            threadLocalMap.setIndexedVariable((int)variablesToRemoveIndex, variablesToRemove);
        } else {
            variablesToRemove = (Set<K>)v;
        }
        variablesToRemove.add(variable);
    }

    private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
        Object v = threadLocalMap.indexedVariable((int)variablesToRemoveIndex);
        if (v == InternalThreadLocalMap.UNSET) return;
        if (v == null) {
            return;
        }
        Set variablesToRemove = (Set)v;
        variablesToRemove.remove(variable);
    }

    public final V get() {
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
        Object v = threadLocalMap.indexedVariable((int)this.index);
        if (v == InternalThreadLocalMap.UNSET) return (V)this.initialize((InternalThreadLocalMap)threadLocalMap);
        return (V)v;
    }

    public final V getIfExists() {
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
        if (threadLocalMap == null) return (V)null;
        Object v = threadLocalMap.indexedVariable((int)this.index);
        if (v == InternalThreadLocalMap.UNSET) return (V)null;
        return (V)v;
    }

    public final V get(InternalThreadLocalMap threadLocalMap) {
        Object v = threadLocalMap.indexedVariable((int)this.index);
        if (v == InternalThreadLocalMap.UNSET) return (V)this.initialize((InternalThreadLocalMap)threadLocalMap);
        return (V)v;
    }

    private V initialize(InternalThreadLocalMap threadLocalMap) {
        V v = null;
        try {
            v = (V)this.initialValue();
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
        }
        threadLocalMap.setIndexedVariable((int)this.index, v);
        FastThreadLocal.addToVariablesToRemove((InternalThreadLocalMap)threadLocalMap, this);
        return (V)v;
    }

    public final void set(V value) {
        if (value != InternalThreadLocalMap.UNSET) {
            InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
            this.setKnownNotUnset((InternalThreadLocalMap)threadLocalMap, value);
            return;
        }
        this.remove();
    }

    public final void set(InternalThreadLocalMap threadLocalMap, V value) {
        if (value != InternalThreadLocalMap.UNSET) {
            this.setKnownNotUnset((InternalThreadLocalMap)threadLocalMap, value);
            return;
        }
        this.remove((InternalThreadLocalMap)threadLocalMap);
    }

    private void setKnownNotUnset(InternalThreadLocalMap threadLocalMap, V value) {
        if (!threadLocalMap.setIndexedVariable((int)this.index, value)) return;
        FastThreadLocal.addToVariablesToRemove((InternalThreadLocalMap)threadLocalMap, this);
    }

    public final boolean isSet() {
        return this.isSet((InternalThreadLocalMap)InternalThreadLocalMap.getIfSet());
    }

    public final boolean isSet(InternalThreadLocalMap threadLocalMap) {
        if (threadLocalMap == null) return false;
        if (!threadLocalMap.isIndexedVariableSet((int)this.index)) return false;
        return true;
    }

    public final void remove() {
        this.remove((InternalThreadLocalMap)InternalThreadLocalMap.getIfSet());
    }

    public final void remove(InternalThreadLocalMap threadLocalMap) {
        if (threadLocalMap == null) {
            return;
        }
        Object v = threadLocalMap.removeIndexedVariable((int)this.index);
        FastThreadLocal.removeFromVariablesToRemove((InternalThreadLocalMap)threadLocalMap, this);
        if (v == InternalThreadLocalMap.UNSET) return;
        try {
            this.onRemoval(v);
            return;
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
        }
    }

    protected V initialValue() throws Exception {
        return (V)null;
    }

    protected void onRemoval(V value) throws Exception {
    }
}

