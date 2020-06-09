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
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractBiMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Serialization;
import com.google.common.collect.WellBehavedMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class EnumHashBiMap<K extends Enum<K>, V>
extends AbstractBiMap<K, V> {
    private transient Class<K> keyType;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Class<K> keyType) {
        return new EnumHashBiMap<K, V>(keyType);
    }

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Map<K, ? extends V> map) {
        EnumHashBiMap<K, V> bimap = EnumHashBiMap.create(EnumBiMap.inferKeyType(map));
        bimap.putAll(map);
        return bimap;
    }

    private EnumHashBiMap(Class<K> keyType) {
        super(WellBehavedMap.wrap(new EnumMap<K, V>(keyType)), Maps.newHashMapWithExpectedSize((int)((Enum[])keyType.getEnumConstants()).length));
        this.keyType = keyType;
    }

    @Override
    K checkKey(K key) {
        return (K)((Enum)Preconditions.checkNotNull(key));
    }

    @CanIgnoreReturnValue
    @Override
    public V put(K key, @Nullable V value) {
        return (V)super.put(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public V forcePut(K key, @Nullable V value) {
        return (V)super.forcePut(key, value);
    }

    public Class<K> keyType() {
        return this.keyType;
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.keyType);
        Serialization.writeMap(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.keyType = (Class)stream.readObject();
        this.setDelegates(WellBehavedMap.wrap(new EnumMap<K, V>(this.keyType)), new HashMap<K, V>((int)(((Enum[])this.keyType.getEnumConstants()).length * 3 / 2)));
        Serialization.populateMap(this, (ObjectInputStream)stream);
    }
}

