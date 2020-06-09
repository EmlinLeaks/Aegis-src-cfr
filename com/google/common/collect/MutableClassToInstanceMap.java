/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MapConstraint;
import com.google.common.collect.MapConstraints;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@GwtIncompatible
public final class MutableClassToInstanceMap<B>
extends MapConstraints.ConstrainedMap<Class<? extends B>, B>
implements ClassToInstanceMap<B>,
Serializable {
    private static final MapConstraint<Class<?>, Object> VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint<Class<?>, Object>(){

        public void checkKeyValue(Class<?> key, Object value) {
            MutableClassToInstanceMap.access$000(key, (Object)value);
        }
    };

    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap<V>(new HashMap<K, V>());
    }

    public static <B> MutableClassToInstanceMap<B> create(Map<Class<? extends B>, B> backingMap) {
        return new MutableClassToInstanceMap<B>(backingMap);
    }

    private MutableClassToInstanceMap(Map<Class<? extends B>, B> delegate) {
        super(delegate, VALUE_CAN_BE_CAST_TO_KEY);
    }

    @CanIgnoreReturnValue
    @Override
    public <T extends B> T putInstance(Class<T> type, T value) {
        return (T)MutableClassToInstanceMap.cast(type, this.put(type, value));
    }

    @Override
    public <T extends B> T getInstance(Class<T> type) {
        return (T)MutableClassToInstanceMap.cast(type, this.get(type));
    }

    @CanIgnoreReturnValue
    private static <B, T extends B> T cast(Class<T> type, B value) {
        return (T)Primitives.wrap(type).cast(value);
    }

    private Object writeReplace() {
        return new SerializedForm<B>(this.delegate());
    }

    static /* synthetic */ Object access$000(Class x0, Object x1) {
        return MutableClassToInstanceMap.cast(x0, x1);
    }
}

