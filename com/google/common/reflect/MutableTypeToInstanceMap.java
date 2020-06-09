/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.MutableTypeToInstanceMap;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public final class MutableTypeToInstanceMap<B>
extends ForwardingMap<TypeToken<? extends B>, B>
implements TypeToInstanceMap<B> {
    private final Map<TypeToken<? extends B>, B> backingMap = Maps.newHashMap();

    @Nullable
    @Override
    public <T extends B> T getInstance(Class<T> type) {
        return (T)this.trustedGet(TypeToken.of(type));
    }

    @Nullable
    @CanIgnoreReturnValue
    @Override
    public <T extends B> T putInstance(Class<T> type, @Nullable T value) {
        return (T)this.trustedPut(TypeToken.of(type), value);
    }

    @Nullable
    @Override
    public <T extends B> T getInstance(TypeToken<T> type) {
        return (T)this.trustedGet(type.rejectTypeVariables());
    }

    @Nullable
    @CanIgnoreReturnValue
    @Override
    public <T extends B> T putInstance(TypeToken<T> type, @Nullable T value) {
        return (T)this.trustedPut(type.rejectTypeVariables(), value);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public B put(TypeToken<? extends B> key, B value) {
        throw new UnsupportedOperationException((String)"Please use putInstance() instead.");
    }

    @Deprecated
    @Override
    public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map) {
        throw new UnsupportedOperationException((String)"Please use putInstance() instead.");
    }

    @Override
    public Set<Map.Entry<TypeToken<? extends B>, B>> entrySet() {
        return UnmodifiableEntry.transformEntries(super.entrySet());
    }

    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.backingMap;
    }

    @Nullable
    private <T extends B> T trustedPut(TypeToken<T> type, @Nullable T value) {
        return (T)this.backingMap.put(type, value);
    }

    @Nullable
    private <T extends B> T trustedGet(TypeToken<T> type) {
        return (T)this.backingMap.get(type);
    }
}

