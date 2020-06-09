/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMakerInternalMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible(emulated=true)
public final class MapMaker {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    static final int UNSET_INT = -1;
    boolean useCustomMap;
    int initialCapacity = -1;
    int concurrencyLevel = -1;
    MapMakerInternalMap.Strength keyStrength;
    MapMakerInternalMap.Strength valueStrength;
    Equivalence<Object> keyEquivalence;

    @CanIgnoreReturnValue
    @GwtIncompatible
    MapMaker keyEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState((boolean)(this.keyEquivalence == null), (String)"key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = Preconditions.checkNotNull(equivalence);
        this.useCustomMap = true;
        return this;
    }

    Equivalence<Object> getKeyEquivalence() {
        return MoreObjects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }

    @CanIgnoreReturnValue
    public MapMaker initialCapacity(int initialCapacity) {
        Preconditions.checkState((boolean)(this.initialCapacity == -1), (String)"initial capacity was already set to %s", (int)this.initialCapacity);
        Preconditions.checkArgument((boolean)(initialCapacity >= 0));
        this.initialCapacity = initialCapacity;
        return this;
    }

    int getInitialCapacity() {
        if (this.initialCapacity == -1) {
            return 16;
        }
        int n = this.initialCapacity;
        return n;
    }

    @CanIgnoreReturnValue
    public MapMaker concurrencyLevel(int concurrencyLevel) {
        Preconditions.checkState((boolean)(this.concurrencyLevel == -1), (String)"concurrency level was already set to %s", (int)this.concurrencyLevel);
        Preconditions.checkArgument((boolean)(concurrencyLevel > 0));
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    int getConcurrencyLevel() {
        if (this.concurrencyLevel == -1) {
            return 4;
        }
        int n = this.concurrencyLevel;
        return n;
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public MapMaker weakKeys() {
        return this.setKeyStrength((MapMakerInternalMap.Strength)MapMakerInternalMap.Strength.WEAK);
    }

    MapMaker setKeyStrength(MapMakerInternalMap.Strength strength) {
        Preconditions.checkState((boolean)(this.keyStrength == null), (String)"Key strength was already set to %s", (Object)((Object)this.keyStrength));
        this.keyStrength = Preconditions.checkNotNull(strength);
        if (strength == MapMakerInternalMap.Strength.STRONG) return this;
        this.useCustomMap = true;
        return this;
    }

    MapMakerInternalMap.Strength getKeyStrength() {
        return MoreObjects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public MapMaker weakValues() {
        return this.setValueStrength((MapMakerInternalMap.Strength)MapMakerInternalMap.Strength.WEAK);
    }

    MapMaker setValueStrength(MapMakerInternalMap.Strength strength) {
        Preconditions.checkState((boolean)(this.valueStrength == null), (String)"Value strength was already set to %s", (Object)((Object)this.valueStrength));
        this.valueStrength = Preconditions.checkNotNull(strength);
        if (strength == MapMakerInternalMap.Strength.STRONG) return this;
        this.useCustomMap = true;
        return this;
    }

    MapMakerInternalMap.Strength getValueStrength() {
        return MoreObjects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
    }

    public <K, V> ConcurrentMap<K, V> makeMap() {
        if (this.useCustomMap) return MapMakerInternalMap.create((MapMaker)this);
        return new ConcurrentHashMap<K, V>((int)this.getInitialCapacity(), (float)0.75f, (int)this.getConcurrencyLevel());
    }

    @GwtIncompatible
    <K, V> MapMakerInternalMap<K, V, ?, ?> makeCustomMap() {
        return MapMakerInternalMap.create((MapMaker)this);
    }

    public String toString() {
        MoreObjects.ToStringHelper s = MoreObjects.toStringHelper((Object)this);
        if (this.initialCapacity != -1) {
            s.add((String)"initialCapacity", (int)this.initialCapacity);
        }
        if (this.concurrencyLevel != -1) {
            s.add((String)"concurrencyLevel", (int)this.concurrencyLevel);
        }
        if (this.keyStrength != null) {
            s.add((String)"keyStrength", (Object)Ascii.toLowerCase((String)this.keyStrength.toString()));
        }
        if (this.valueStrength != null) {
            s.add((String)"valueStrength", (Object)Ascii.toLowerCase((String)this.valueStrength.toString()));
        }
        if (this.keyEquivalence == null) return s.toString();
        s.addValue((Object)"keyEquivalence");
        return s.toString();
    }
}

