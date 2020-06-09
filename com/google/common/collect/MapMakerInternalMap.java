/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MapMakerInternalMap;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

@GwtIncompatible
class MapMakerInternalMap<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final int DRAIN_THRESHOLD = 63;
    static final int DRAIN_MAX = 16;
    static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
    final transient int segmentMask;
    final transient int segmentShift;
    final transient Segment<K, V, E, S>[] segments;
    final int concurrencyLevel;
    final Equivalence<Object> keyEquivalence;
    final transient InternalEntryHelper<K, V, E, S> entryHelper;
    static final WeakValueReference<Object, Object, DummyInternalEntry> UNSET_WEAK_VALUE_REFERENCE = new WeakValueReference<Object, Object, DummyInternalEntry>(){

        public DummyInternalEntry getEntry() {
            return null;
        }

        public void clear() {
        }

        public Object get() {
            return null;
        }

        public WeakValueReference<Object, Object, DummyInternalEntry> copyFor(java.lang.ref.ReferenceQueue<Object> queue, DummyInternalEntry entry) {
            return this;
        }
    };
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    private static final long serialVersionUID = 5L;

    private MapMakerInternalMap(MapMaker builder, InternalEntryHelper<K, V, E, S> entryHelper) {
        int segmentSize;
        int segmentCount;
        this.concurrencyLevel = Math.min((int)builder.getConcurrencyLevel(), (int)65536);
        this.keyEquivalence = builder.getKeyEquivalence();
        this.entryHelper = entryHelper;
        int initialCapacity = Math.min((int)builder.getInitialCapacity(), (int)1073741824);
        int segmentShift = 0;
        for (segmentCount = 1; segmentCount < this.concurrencyLevel; ++segmentShift, segmentCount <<= 1) {
        }
        this.segmentShift = 32 - segmentShift;
        this.segmentMask = segmentCount - 1;
        this.segments = this.newSegmentArray((int)segmentCount);
        int segmentCapacity = initialCapacity / segmentCount;
        if (segmentCapacity * segmentCount < initialCapacity) {
            ++segmentCapacity;
        }
        for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {
        }
        int i = 0;
        while (i < this.segments.length) {
            this.segments[i] = this.createSegment((int)segmentSize, (int)-1);
            ++i;
        }
    }

    static <K, V> MapMakerInternalMap<K, V, ? extends InternalEntry<K, V, ?>, ?> create(MapMaker builder) {
        if (builder.getKeyStrength() == Strength.STRONG && builder.getValueStrength() == Strength.STRONG) {
            return new MapMakerInternalMap<K, V, E, S>((MapMaker)builder, StrongKeyStrongValueEntry.Helper.<K, V>instance());
        }
        if (builder.getKeyStrength() == Strength.STRONG && builder.getValueStrength() == Strength.WEAK) {
            return new MapMakerInternalMap<K, V, E, S>((MapMaker)builder, StrongKeyWeakValueEntry.Helper.<K, V>instance());
        }
        if (builder.getKeyStrength() == Strength.WEAK && builder.getValueStrength() == Strength.STRONG) {
            return new MapMakerInternalMap<K, V, E, S>((MapMaker)builder, WeakKeyStrongValueEntry.Helper.<K, V>instance());
        }
        if (builder.getKeyStrength() != Strength.WEAK) throw new AssertionError();
        if (builder.getValueStrength() != Strength.WEAK) throw new AssertionError();
        return new MapMakerInternalMap<K, V, E, S>((MapMaker)builder, WeakKeyWeakValueEntry.Helper.<K, V>instance());
    }

    static <K, V, E extends InternalEntry<K, V, E>> WeakValueReference<K, V, E> unsetWeakValueReference() {
        return UNSET_WEAK_VALUE_REFERENCE;
    }

    static int rehash(int h) {
        h += h << 15 ^ -12931;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    @VisibleForTesting
    E copyEntry(E original, E newNext) {
        int hash = original.getHash();
        return (E)this.segmentFor((int)hash).copyEntry(original, newNext);
    }

    int hash(Object key) {
        int h = this.keyEquivalence.hash((Object)key);
        return MapMakerInternalMap.rehash((int)h);
    }

    void reclaimValue(WeakValueReference<K, V, E> valueReference) {
        E entry = valueReference.getEntry();
        int hash = entry.getHash();
        this.segmentFor((int)hash).reclaimValue(entry.getKey(), (int)hash, valueReference);
    }

    void reclaimKey(E entry) {
        int hash = entry.getHash();
        this.segmentFor((int)hash).reclaimKey(entry, (int)hash);
    }

    @VisibleForTesting
    boolean isLiveForTesting(InternalEntry<K, V, ?> entry) {
        if (this.segmentFor((int)entry.getHash()).getLiveValueForTesting(entry) == null) return false;
        return true;
    }

    Segment<K, V, E, S> segmentFor(int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }

    Segment<K, V, E, S> createSegment(int initialCapacity, int maxSegmentSize) {
        return this.entryHelper.newSegment(this, (int)initialCapacity, (int)maxSegmentSize);
    }

    V getLiveValue(E entry) {
        if (entry.getKey() == null) {
            return (V)null;
        }
        V value = entry.getValue();
        if (value != null) return (V)value;
        return (V)null;
    }

    final Segment<K, V, E, S>[] newSegmentArray(int ssize) {
        return new Segment[ssize];
    }

    @VisibleForTesting
    Strength keyStrength() {
        return this.entryHelper.keyStrength();
    }

    @VisibleForTesting
    Strength valueStrength() {
        return this.entryHelper.valueStrength();
    }

    @VisibleForTesting
    Equivalence<Object> valueEquivalence() {
        return this.entryHelper.valueStrength().defaultEquivalence();
    }

    @Override
    public boolean isEmpty() {
        int i;
        long sum = 0L;
        Segment<K, V, E, S>[] segments = this.segments;
        for (i = 0; i < segments.length; sum += (long)segments[i].modCount, ++i) {
            if (segments[i].count == 0) continue;
            return false;
        }
        if (sum == 0L) return true;
        i = 0;
        do {
            if (i >= segments.length) {
                if (sum == 0L) return true;
                return false;
            }
            if (segments[i].count != 0) {
                return false;
            }
            sum -= (long)segments[i].modCount;
            ++i;
        } while (true);
    }

    @Override
    public int size() {
        Segment<K, V, E, S>[] segments = this.segments;
        long sum = 0L;
        int i = 0;
        while (i < segments.length) {
            sum += (long)segments[i].count;
            ++i;
        }
        return Ints.saturatedCast((long)sum);
    }

    @Override
    public V get(@Nullable Object key) {
        if (key == null) {
            return (V)null;
        }
        int hash = this.hash((Object)key);
        return (V)this.segmentFor((int)hash).get((Object)key, (int)hash);
    }

    E getEntry(@Nullable Object key) {
        if (key == null) {
            return (E)null;
        }
        int hash = this.hash((Object)key);
        return (E)this.segmentFor((int)hash).getEntry((Object)key, (int)hash);
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (key == null) {
            return false;
        }
        int hash = this.hash((Object)key);
        return this.segmentFor((int)hash).containsKey((Object)key, (int)hash);
    }

    /*
     * Exception decompiling
     */
    @Override
    public boolean containsValue(@Nullable Object value) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[UNCONDITIONALDOLOOP]], but top level block is 2[UNCONDITIONALDOLOOP]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @CanIgnoreReturnValue
    @Override
    public V put(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = this.hash(key);
        return (V)this.segmentFor((int)hash).put(key, (int)hash, value, (boolean)false);
    }

    @CanIgnoreReturnValue
    @Override
    public V putIfAbsent(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = this.hash(key);
        return (V)this.segmentFor((int)hash).put(key, (int)hash, value, (boolean)true);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<Map.Entry<K, V>> i$ = m.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> e = i$.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object key) {
        if (key == null) {
            return (V)null;
        }
        int hash = this.hash((Object)key);
        return (V)this.segmentFor((int)hash).remove((Object)key, (int)hash);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (key == null) return false;
        if (value == null) {
            return false;
        }
        int hash = this.hash((Object)key);
        return this.segmentFor((int)hash).remove((Object)key, (int)hash, (Object)value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean replace(K key, @Nullable V oldValue, V newValue) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(newValue);
        if (oldValue == null) {
            return false;
        }
        int hash = this.hash(key);
        return this.segmentFor((int)hash).replace(key, (int)hash, oldValue, newValue);
    }

    @CanIgnoreReturnValue
    @Override
    public V replace(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = this.hash(key);
        return (V)this.segmentFor((int)hash).replace(key, (int)hash, value);
    }

    @Override
    public void clear() {
        Segment<K, V, E, S>[] arr$ = this.segments;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Segment<K, V, E, S> segment = arr$[i$];
            segment.clear();
            ++i$;
        }
    }

    @Override
    public Set<K> keySet() {
        KeySet keySet;
        KeySet ks = this.keySet;
        if (ks != null) {
            keySet = ks;
            return keySet;
        }
        keySet = this.keySet = new KeySet((MapMakerInternalMap)this);
        return keySet;
    }

    @Override
    public Collection<V> values() {
        Values values;
        Values vs = this.values;
        if (vs != null) {
            values = vs;
            return values;
        }
        values = this.values = new Values((MapMakerInternalMap)this);
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet entrySet;
        EntrySet es = this.entrySet;
        if (es != null) {
            entrySet = es;
            return entrySet;
        }
        entrySet = this.entrySet = new EntrySet((MapMakerInternalMap)this);
        return entrySet;
    }

    private static <E> ArrayList<E> toArrayList(Collection<E> c) {
        ArrayList<E> result = new ArrayList<E>((int)c.size());
        Iterators.addAll(result, c.iterator());
        return result;
    }

    Object writeReplace() {
        return new SerializationProxy<K, V>((Strength)this.entryHelper.keyStrength(), (Strength)this.entryHelper.valueStrength(), this.keyEquivalence, this.entryHelper.valueStrength().defaultEquivalence(), (int)this.concurrencyLevel, this);
    }

    static /* synthetic */ ArrayList access$800(Collection x0) {
        return MapMakerInternalMap.toArrayList(x0);
    }
}

