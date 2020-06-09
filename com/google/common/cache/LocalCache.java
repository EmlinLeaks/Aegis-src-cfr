/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Ticker;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LocalCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
class LocalCache<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V> {
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final int DRAIN_THRESHOLD = 63;
    static final int DRAIN_MAX = 16;
    static final Logger logger = Logger.getLogger((String)LocalCache.class.getName());
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    final int concurrencyLevel;
    final Equivalence<Object> keyEquivalence;
    final Equivalence<Object> valueEquivalence;
    final Strength keyStrength;
    final Strength valueStrength;
    final long maxWeight;
    final Weigher<K, V> weigher;
    final long expireAfterAccessNanos;
    final long expireAfterWriteNanos;
    final long refreshNanos;
    final Queue<RemovalNotification<K, V>> removalNotificationQueue;
    final RemovalListener<K, V> removalListener;
    final Ticker ticker;
    final EntryFactory entryFactory;
    final AbstractCache.StatsCounter globalStatsCounter;
    @Nullable
    final CacheLoader<? super K, V> defaultLoader;
    static final ValueReference<Object, Object> UNSET = new ValueReference<Object, Object>(){

        public Object get() {
            return null;
        }

        public int getWeight() {
            return 0;
        }

        public ReferenceEntry<Object, Object> getEntry() {
            return null;
        }

        public ValueReference<Object, Object> copyFor(java.lang.ref.ReferenceQueue<Object> queue, @Nullable Object value, ReferenceEntry<Object, Object> entry) {
            return this;
        }

        public boolean isLoading() {
            return false;
        }

        public boolean isActive() {
            return false;
        }

        public Object waitForValue() {
            return null;
        }

        public void notifyNewValue(Object newValue) {
        }
    };
    static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue<Object>(){

        public boolean offer(Object o) {
            return true;
        }

        public Object peek() {
            return null;
        }

        public Object poll() {
            return null;
        }

        public int size() {
            return 0;
        }

        public Iterator<Object> iterator() {
            return com.google.common.collect.ImmutableSet.of().iterator();
        }
    };
    Set<K> keySet;
    Collection<V> values;
    Set<Map.Entry<K, V>> entrySet;

    LocalCache(CacheBuilder<? super K, ? super V> builder, @Nullable CacheLoader<? super K, V> loader) {
        int segmentSize;
        int segmentCount;
        this.concurrencyLevel = Math.min((int)builder.getConcurrencyLevel(), (int)65536);
        this.keyStrength = builder.getKeyStrength();
        this.valueStrength = builder.getValueStrength();
        this.keyEquivalence = builder.getKeyEquivalence();
        this.valueEquivalence = builder.getValueEquivalence();
        this.maxWeight = builder.getMaximumWeight();
        this.weigher = builder.getWeigher();
        this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
        this.refreshNanos = builder.getRefreshNanos();
        this.removalListener = builder.getRemovalListener();
        this.removalNotificationQueue = this.removalListener == CacheBuilder.NullListener.INSTANCE ? LocalCache.discardingQueue() : new ConcurrentLinkedQueue<E>();
        this.ticker = builder.getTicker((boolean)this.recordsTime());
        this.entryFactory = EntryFactory.getFactory((Strength)this.keyStrength, (boolean)this.usesAccessEntries(), (boolean)this.usesWriteEntries());
        this.globalStatsCounter = builder.getStatsCounterSupplier().get();
        this.defaultLoader = loader;
        int initialCapacity = Math.min((int)builder.getInitialCapacity(), (int)1073741824);
        if (this.evictsBySize() && !this.customWeigher()) {
            initialCapacity = Math.min((int)initialCapacity, (int)((int)this.maxWeight));
        }
        int segmentShift = 0;
        for (segmentCount = 1; !(segmentCount >= this.concurrencyLevel || this.evictsBySize() && (long)(segmentCount * 20) > this.maxWeight); ++segmentShift, segmentCount <<= 1) {
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
        if (this.evictsBySize()) {
            long maxSegmentWeight = this.maxWeight / (long)segmentCount + 1L;
            long remainder = this.maxWeight % (long)segmentCount;
            int i = 0;
            while (i < this.segments.length) {
                if ((long)i == remainder) {
                    --maxSegmentWeight;
                }
                this.segments[i] = this.createSegment((int)segmentSize, (long)maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
                ++i;
            }
            return;
        }
        int i = 0;
        while (i < this.segments.length) {
            this.segments[i] = this.createSegment((int)segmentSize, (long)-1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
            ++i;
        }
    }

    boolean evictsBySize() {
        if (this.maxWeight < 0L) return false;
        return true;
    }

    boolean customWeigher() {
        if (this.weigher == CacheBuilder.OneWeigher.INSTANCE) return false;
        return true;
    }

    boolean expires() {
        if (this.expiresAfterWrite()) return true;
        if (this.expiresAfterAccess()) return true;
        return false;
    }

    boolean expiresAfterWrite() {
        if (this.expireAfterWriteNanos <= 0L) return false;
        return true;
    }

    boolean expiresAfterAccess() {
        if (this.expireAfterAccessNanos <= 0L) return false;
        return true;
    }

    boolean refreshes() {
        if (this.refreshNanos <= 0L) return false;
        return true;
    }

    boolean usesAccessQueue() {
        if (this.expiresAfterAccess()) return true;
        if (this.evictsBySize()) return true;
        return false;
    }

    boolean usesWriteQueue() {
        return this.expiresAfterWrite();
    }

    boolean recordsWrite() {
        if (this.expiresAfterWrite()) return true;
        if (this.refreshes()) return true;
        return false;
    }

    boolean recordsAccess() {
        return this.expiresAfterAccess();
    }

    boolean recordsTime() {
        if (this.recordsWrite()) return true;
        if (this.recordsAccess()) return true;
        return false;
    }

    boolean usesWriteEntries() {
        if (this.usesWriteQueue()) return true;
        if (this.recordsWrite()) return true;
        return false;
    }

    boolean usesAccessEntries() {
        if (this.usesAccessQueue()) return true;
        if (this.recordsAccess()) return true;
        return false;
    }

    boolean usesKeyReferences() {
        if (this.keyStrength == Strength.STRONG) return false;
        return true;
    }

    boolean usesValueReferences() {
        if (this.valueStrength == Strength.STRONG) return false;
        return true;
    }

    static <K, V> ValueReference<K, V> unset() {
        return UNSET;
    }

    static <K, V> ReferenceEntry<K, V> nullEntry() {
        return NullEntry.INSTANCE;
    }

    static <E> Queue<E> discardingQueue() {
        return DISCARDING_QUEUE;
    }

    static int rehash(int h) {
        h += h << 15 ^ -12931;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next) {
        Segment<K, V> segment = this.segmentFor((int)hash);
        segment.lock();
        try {
            ReferenceEntry<K, V> referenceEntry = segment.newEntry(key, (int)hash, next);
            return referenceEntry;
        }
        finally {
            segment.unlock();
        }
    }

    @VisibleForTesting
    ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
        int hash = original.getHash();
        return this.segmentFor((int)hash).copyEntry(original, newNext);
    }

    @VisibleForTesting
    ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight) {
        int hash = entry.getHash();
        return this.valueStrength.referenceValue(this.segmentFor((int)hash), entry, Preconditions.checkNotNull(value), (int)weight);
    }

    int hash(@Nullable Object key) {
        int h = this.keyEquivalence.hash((Object)key);
        return LocalCache.rehash((int)h);
    }

    void reclaimValue(ValueReference<K, V> valueReference) {
        ReferenceEntry<K, V> entry = valueReference.getEntry();
        int hash = entry.getHash();
        this.segmentFor((int)hash).reclaimValue(entry.getKey(), (int)hash, valueReference);
    }

    void reclaimKey(ReferenceEntry<K, V> entry) {
        int hash = entry.getHash();
        this.segmentFor((int)hash).reclaimKey(entry, (int)hash);
    }

    @VisibleForTesting
    boolean isLive(ReferenceEntry<K, V> entry, long now) {
        if (this.segmentFor((int)entry.getHash()).getLiveValue(entry, (long)now) == null) return false;
        return true;
    }

    Segment<K, V> segmentFor(int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }

    Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter) {
        return new Segment<K, V>(this, (int)initialCapacity, (long)maxSegmentWeight, (AbstractCache.StatsCounter)statsCounter);
    }

    @Nullable
    V getLiveValue(ReferenceEntry<K, V> entry, long now) {
        if (entry.getKey() == null) {
            return (V)null;
        }
        V value = entry.getValueReference().get();
        if (value == null) {
            return (V)null;
        }
        if (!this.isExpired(entry, (long)now)) return (V)value;
        return (V)null;
    }

    boolean isExpired(ReferenceEntry<K, V> entry, long now) {
        Preconditions.checkNotNull(entry);
        if (this.expiresAfterAccess() && now - entry.getAccessTime() >= this.expireAfterAccessNanos) {
            return true;
        }
        if (!this.expiresAfterWrite()) return false;
        if (now - entry.getWriteTime() < this.expireAfterWriteNanos) return false;
        return true;
    }

    static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
        previous.setNextInAccessQueue(next);
        next.setPreviousInAccessQueue(previous);
    }

    static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled) {
        ReferenceEntry<K, V> nullEntry = LocalCache.nullEntry();
        nulled.setNextInAccessQueue(nullEntry);
        nulled.setPreviousInAccessQueue(nullEntry);
    }

    static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
        previous.setNextInWriteQueue(next);
        next.setPreviousInWriteQueue(previous);
    }

    static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled) {
        ReferenceEntry<K, V> nullEntry = LocalCache.nullEntry();
        nulled.setNextInWriteQueue(nullEntry);
        nulled.setPreviousInWriteQueue(nullEntry);
    }

    void processPendingNotifications() {
        RemovalNotification<K, V> notification;
        while ((notification = this.removalNotificationQueue.poll()) != null) {
            try {
                this.removalListener.onRemoval(notification);
            }
            catch (Throwable e) {
                logger.log((Level)Level.WARNING, (String)"Exception thrown by removal listener", (Throwable)e);
            }
        }
    }

    final Segment<K, V>[] newSegmentArray(int ssize) {
        return new Segment[ssize];
    }

    public void cleanUp() {
        Segment<K, V>[] arr$ = this.segments;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Segment<K, V> segment = arr$[i$];
            segment.cleanUp();
            ++i$;
        }
    }

    @Override
    public boolean isEmpty() {
        int i;
        long sum = 0L;
        Segment<K, V>[] segments = this.segments;
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

    long longSize() {
        Segment<K, V>[] segments = this.segments;
        long sum = 0L;
        int i = 0;
        while (i < segments.length) {
            sum += (long)Math.max((int)0, (int)segments[i].count);
            ++i;
        }
        return sum;
    }

    @Override
    public int size() {
        return Ints.saturatedCast((long)this.longSize());
    }

    @Nullable
    @Override
    public V get(@Nullable Object key) {
        if (key == null) {
            return (V)null;
        }
        int hash = this.hash((Object)key);
        return (V)this.segmentFor((int)hash).get((Object)key, (int)hash);
    }

    @Nullable
    public V getIfPresent(Object key) {
        int hash = this.hash((Object)Preconditions.checkNotNull(key));
        V value = this.segmentFor((int)hash).get((Object)key, (int)hash);
        if (value == null) {
            this.globalStatsCounter.recordMisses((int)1);
            return (V)((V)value);
        }
        this.globalStatsCounter.recordHits((int)1);
        return (V)value;
    }

    @Nullable
    @Override
    public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        V v;
        V result = this.get((Object)key);
        if (result != null) {
            v = result;
            return (V)((V)v);
        }
        v = defaultValue;
        return (V)v;
    }

    V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
        int hash = this.hash(Preconditions.checkNotNull(key));
        return (V)this.segmentFor((int)hash).get(key, (int)hash, loader);
    }

    V getOrLoad(K key) throws ExecutionException {
        return (V)this.get(key, this.defaultLoader);
    }

    ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
        int hits = 0;
        int misses = 0;
        LinkedHashMap<?, V> result = Maps.newLinkedHashMap();
        Iterator<?> i$ = keys.iterator();
        do {
            if (!i$.hasNext()) {
                this.globalStatsCounter.recordHits((int)hits);
                this.globalStatsCounter.recordMisses((int)misses);
                return ImmutableMap.copyOf(result);
            }
            ? key = i$.next();
            V value = this.get(key);
            if (value == null) {
                ++misses;
                continue;
            }
            ? castKey = key;
            result.put(castKey, value);
            ++hits;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
        int hits = 0;
        int misses = 0;
        LinkedHashMap<Object, V> result = Maps.newLinkedHashMap();
        LinkedHashSet<K> keysToLoad = Sets.newLinkedHashSet();
        for (K key : keys) {
            V value = this.get(key);
            if (result.containsKey(key)) continue;
            result.put(key, value);
            if (value == null) {
                ++misses;
                keysToLoad.add(key);
                continue;
            }
            ++hits;
        }
        try {
            if (!keysToLoad.isEmpty()) {
                try {
                    Map<K, V> newEntries = this.loadAll(keysToLoad, this.defaultLoader);
                    for (E key : keysToLoad) {
                        V value = newEntries.get(key);
                        if (value == null) {
                            throw new CacheLoader.InvalidCacheLoadException((String)("loadAll failed to return a value for " + key));
                        }
                        result.put(key, value);
                    }
                }
                catch (CacheLoader.UnsupportedLoadingOperationException e) {
                    for (E key : keysToLoad) {
                        --misses;
                        result.put(key, this.get(key, this.defaultLoader));
                    }
                }
            }
            ImmutableMap<K, V> e = ImmutableMap.copyOf(result);
            return e;
        }
        finally {
            this.globalStatsCounter.recordHits((int)hits);
            this.globalStatsCounter.recordMisses((int)misses);
        }
    }

    @Nullable
    Map<K, V> loadAll(Set<? extends K> keys, CacheLoader<? super K, V> loader) throws ExecutionException {
        Map<K, V> result;
        Preconditions.checkNotNull(loader);
        Preconditions.checkNotNull(keys);
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean success = false;
        try {
            Map<K, V> map;
            result = map = loader.loadAll(keys);
            success = true;
        }
        catch (CacheLoader.UnsupportedLoadingOperationException e) {
            success = true;
            throw e;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionException((Throwable)e);
        }
        catch (RuntimeException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
        catch (Exception e) {
            throw new ExecutionException((Throwable)e);
        }
        catch (Error e) {
            throw new ExecutionError((Error)e);
        }
        finally {
            if (!success) {
                this.globalStatsCounter.recordLoadException((long)stopwatch.elapsed((TimeUnit)TimeUnit.NANOSECONDS));
            }
        }
        if (result == null) {
            this.globalStatsCounter.recordLoadException((long)stopwatch.elapsed((TimeUnit)TimeUnit.NANOSECONDS));
            throw new CacheLoader.InvalidCacheLoadException((String)(loader + " returned null map from loadAll"));
        }
        stopwatch.stop();
        boolean nullsPresent = false;
        for (Map.Entry<K, V> entry : result.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (key == null || value == null) {
                nullsPresent = true;
                continue;
            }
            this.put(key, value);
        }
        if (nullsPresent) {
            this.globalStatsCounter.recordLoadException((long)stopwatch.elapsed((TimeUnit)TimeUnit.NANOSECONDS));
            throw new CacheLoader.InvalidCacheLoadException((String)(loader + " returned null keys or values from loadAll"));
        }
        this.globalStatsCounter.recordLoadSuccess((long)stopwatch.elapsed((TimeUnit)TimeUnit.NANOSECONDS));
        return result;
    }

    ReferenceEntry<K, V> getEntry(@Nullable Object key) {
        if (key == null) {
            return null;
        }
        int hash = this.hash((Object)key);
        return this.segmentFor((int)hash).getEntry((Object)key, (int)hash);
    }

    void refresh(K key) {
        int hash = this.hash(Preconditions.checkNotNull(key));
        this.segmentFor((int)hash).refresh(key, (int)hash, this.defaultLoader, (boolean)false);
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

    @Override
    public V put(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = this.hash(key);
        return (V)this.segmentFor((int)hash).put(key, (int)hash, value, (boolean)false);
    }

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

    @Override
    public V remove(@Nullable Object key) {
        if (key == null) {
            return (V)null;
        }
        int hash = this.hash((Object)key);
        return (V)this.segmentFor((int)hash).remove((Object)key, (int)hash);
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        if (key == null) return false;
        if (value == null) {
            return false;
        }
        int hash = this.hash((Object)key);
        return this.segmentFor((int)hash).remove((Object)key, (int)hash, (Object)value);
    }

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

    @Override
    public V replace(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        int hash = this.hash(key);
        return (V)this.segmentFor((int)hash).replace(key, (int)hash, value);
    }

    @Override
    public void clear() {
        Segment<K, V>[] arr$ = this.segments;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Segment<K, V> segment = arr$[i$];
            segment.clear();
            ++i$;
        }
    }

    void invalidateAll(Iterable<?> keys) {
        Iterator<?> i$ = keys.iterator();
        while (i$.hasNext()) {
            ? key = i$.next();
            this.remove(key);
        }
    }

    @Override
    public Set<K> keySet() {
        Object object;
        Set<K> ks = this.keySet;
        if (ks != null) {
            object = ks;
            return object;
        }
        object = this.keySet = new KeySet((LocalCache)this, this);
        return object;
    }

    @Override
    public Collection<V> values() {
        Values values;
        Values vs = this.values;
        if (vs != null) {
            values = vs;
            return values;
        }
        values = this.values = new Values((LocalCache)this, this);
        return values;
    }

    @GwtIncompatible
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Object object;
        Set<Map.Entry<K, V>> es = this.entrySet;
        if (es != null) {
            object = es;
            return object;
        }
        object = this.entrySet = new EntrySet((LocalCache)this, this);
        return object;
    }

    private static <E> ArrayList<E> toArrayList(Collection<E> c) {
        ArrayList<E> result = new ArrayList<E>((int)c.size());
        Iterators.addAll(result, c.iterator());
        return result;
    }

    static /* synthetic */ ArrayList access$200(Collection x0) {
        return LocalCache.toArrayList(x0);
    }
}

