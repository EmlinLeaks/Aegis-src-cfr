/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

@GwtIncompatible
public final class ImmutableClassToInstanceMap<B>
extends ForwardingMap<Class<? extends B>, B>
implements ClassToInstanceMap<B>,
Serializable {
    private static final ImmutableClassToInstanceMap<Object> EMPTY = new ImmutableClassToInstanceMap<V>(ImmutableMap.<K, V>of());
    private final ImmutableMap<Class<? extends B>, B> delegate;

    public static <B> ImmutableClassToInstanceMap<B> of() {
        return EMPTY;
    }

    public static <B, T extends B> ImmutableClassToInstanceMap<B> of(Class<T> type, T value) {
        ImmutableMap<Class<T>, T> map = ImmutableMap.of(type, value);
        return new ImmutableClassToInstanceMap<T>(map);
    }

    public static <B> Builder<B> builder() {
        return new Builder<B>();
    }

    public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> map) {
        if (!(map instanceof ImmutableClassToInstanceMap)) return new Builder<B>().putAll(map).build();
        return (ImmutableClassToInstanceMap)map;
    }

    private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Map<Class<? extends B>, B> delegate() {
        return this.delegate;
    }

    @Nullable
    @Override
    public <T extends B> T getInstance(Class<T> type) {
        return (T)this.delegate.get(Preconditions.checkNotNull(type));
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public <T extends B> T putInstance(Class<T> type, T value) {
        throw new UnsupportedOperationException();
    }

    Object readResolve() {
        ImmutableClassToInstanceMap immutableClassToInstanceMap;
        if (this.isEmpty()) {
            immutableClassToInstanceMap = ImmutableClassToInstanceMap.of();
            return immutableClassToInstanceMap;
        }
        immutableClassToInstanceMap = this;
        return immutableClassToInstanceMap;
    }
}

