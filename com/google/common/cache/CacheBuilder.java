/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Ticker;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.LocalCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.Weigher;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckReturnValue;

@GwtCompatible(emulated=true)
public final class CacheBuilder<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private static final int DEFAULT_EXPIRATION_NANOS = 0;
    private static final int DEFAULT_REFRESH_NANOS = 0;
    static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter(){

        public void recordHits(int count) {
        }

        public void recordMisses(int count) {
        }

        public void recordLoadSuccess(long loadTime) {
        }

        public void recordLoadException(long loadTime) {
        }

        public void recordEviction() {
        }

        public CacheStats snapshot() {
            return EMPTY_STATS;
        }
    });
    static final CacheStats EMPTY_STATS = new CacheStats((long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L);
    static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER = new Supplier<AbstractCache.StatsCounter>(){

        public AbstractCache.StatsCounter get() {
            return new com.google.common.cache.AbstractCache$SimpleStatsCounter();
        }
    };
    static final Ticker NULL_TICKER = new Ticker(){

        public long read() {
            return 0L;
        }
    };
    private static final Logger logger = Logger.getLogger((String)CacheBuilder.class.getName());
    static final int UNSET_INT = -1;
    boolean strictParsing = true;
    int initialCapacity = -1;
    int concurrencyLevel = -1;
    long maximumSize = -1L;
    long maximumWeight = -1L;
    Weigher<? super K, ? super V> weigher;
    LocalCache.Strength keyStrength;
    LocalCache.Strength valueStrength;
    long expireAfterWriteNanos = -1L;
    long expireAfterAccessNanos = -1L;
    long refreshNanos = -1L;
    Equivalence<Object> keyEquivalence;
    Equivalence<Object> valueEquivalence;
    RemovalListener<? super K, ? super V> removalListener;
    Ticker ticker;
    Supplier<? extends AbstractCache.StatsCounter> statsCounterSupplier = NULL_STATS_COUNTER;

    CacheBuilder() {
    }

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<Object, Object>();
    }

    @GwtIncompatible
    public static CacheBuilder<Object, Object> from(CacheBuilderSpec spec) {
        return spec.toCacheBuilder().lenientParsing();
    }

    @GwtIncompatible
    public static CacheBuilder<Object, Object> from(String spec) {
        return CacheBuilder.from((CacheBuilderSpec)CacheBuilderSpec.parse((String)spec));
    }

    @GwtIncompatible
    CacheBuilder<K, V> lenientParsing() {
        this.strictParsing = false;
        return this;
    }

    @GwtIncompatible
    CacheBuilder<K, V> keyEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState((boolean)(this.keyEquivalence == null), (String)"key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = Preconditions.checkNotNull(equivalence);
        return this;
    }

    Equivalence<Object> getKeyEquivalence() {
        return MoreObjects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }

    @GwtIncompatible
    CacheBuilder<K, V> valueEquivalence(Equivalence<Object> equivalence) {
        Preconditions.checkState((boolean)(this.valueEquivalence == null), (String)"value equivalence was already set to %s", this.valueEquivalence);
        this.valueEquivalence = Preconditions.checkNotNull(equivalence);
        return this;
    }

    Equivalence<Object> getValueEquivalence() {
        return MoreObjects.firstNonNull(this.valueEquivalence, this.getValueStrength().defaultEquivalence());
    }

    public CacheBuilder<K, V> initialCapacity(int initialCapacity) {
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

    public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
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

    public CacheBuilder<K, V> maximumSize(long size) {
        Preconditions.checkState((boolean)(this.maximumSize == -1L), (String)"maximum size was already set to %s", (long)this.maximumSize);
        Preconditions.checkState((boolean)(this.maximumWeight == -1L), (String)"maximum weight was already set to %s", (long)this.maximumWeight);
        Preconditions.checkState((boolean)(this.weigher == null), (Object)"maximum size can not be combined with weigher");
        Preconditions.checkArgument((boolean)(size >= 0L), (Object)"maximum size must not be negative");
        this.maximumSize = size;
        return this;
    }

    @GwtIncompatible
    public CacheBuilder<K, V> maximumWeight(long weight) {
        Preconditions.checkState((boolean)(this.maximumWeight == -1L), (String)"maximum weight was already set to %s", (long)this.maximumWeight);
        Preconditions.checkState((boolean)(this.maximumSize == -1L), (String)"maximum size was already set to %s", (long)this.maximumSize);
        this.maximumWeight = weight;
        Preconditions.checkArgument((boolean)(weight >= 0L), (Object)"maximum weight must not be negative");
        return this;
    }

    @GwtIncompatible
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(Weigher<? super K1, ? super V1> weigher) {
        Preconditions.checkState((boolean)(this.weigher == null));
        if (this.strictParsing) {
            Preconditions.checkState((boolean)(this.maximumSize == -1L), (String)"weigher can not be combined with maximum size", (long)this.maximumSize);
        }
        CacheBuilder me = this;
        me.weigher = Preconditions.checkNotNull(weigher);
        return me;
    }

    long getMaximumWeight() {
        long l;
        if (this.expireAfterWriteNanos == 0L) return 0L;
        if (this.expireAfterAccessNanos == 0L) {
            return 0L;
        }
        if (this.weigher == null) {
            l = this.maximumSize;
            return l;
        }
        l = this.maximumWeight;
        return l;
    }

    <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher() {
        return (Weigher)MoreObjects.firstNonNull(this.weigher, OneWeigher.INSTANCE);
    }

    @GwtIncompatible
    public CacheBuilder<K, V> weakKeys() {
        return this.setKeyStrength((LocalCache.Strength)LocalCache.Strength.WEAK);
    }

    CacheBuilder<K, V> setKeyStrength(LocalCache.Strength strength) {
        Preconditions.checkState((boolean)(this.keyStrength == null), (String)"Key strength was already set to %s", (Object)((Object)this.keyStrength));
        this.keyStrength = Preconditions.checkNotNull(strength);
        return this;
    }

    LocalCache.Strength getKeyStrength() {
        return MoreObjects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
    }

    @GwtIncompatible
    public CacheBuilder<K, V> weakValues() {
        return this.setValueStrength((LocalCache.Strength)LocalCache.Strength.WEAK);
    }

    @GwtIncompatible
    public CacheBuilder<K, V> softValues() {
        return this.setValueStrength((LocalCache.Strength)LocalCache.Strength.SOFT);
    }

    CacheBuilder<K, V> setValueStrength(LocalCache.Strength strength) {
        Preconditions.checkState((boolean)(this.valueStrength == null), (String)"Value strength was already set to %s", (Object)((Object)this.valueStrength));
        this.valueStrength = Preconditions.checkNotNull(strength);
        return this;
    }

    LocalCache.Strength getValueStrength() {
        return MoreObjects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
    }

    public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit) {
        Preconditions.checkState((boolean)(this.expireAfterWriteNanos == -1L), (String)"expireAfterWrite was already set to %s ns", (long)this.expireAfterWriteNanos);
        Preconditions.checkArgument((boolean)(duration >= 0L), (String)"duration cannot be negative: %s %s", (long)duration, (Object)((Object)unit));
        this.expireAfterWriteNanos = unit.toNanos((long)duration);
        return this;
    }

    long getExpireAfterWriteNanos() {
        if (this.expireAfterWriteNanos == -1L) {
            return 0L;
        }
        long l = this.expireAfterWriteNanos;
        return l;
    }

    public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
        Preconditions.checkState((boolean)(this.expireAfterAccessNanos == -1L), (String)"expireAfterAccess was already set to %s ns", (long)this.expireAfterAccessNanos);
        Preconditions.checkArgument((boolean)(duration >= 0L), (String)"duration cannot be negative: %s %s", (long)duration, (Object)((Object)unit));
        this.expireAfterAccessNanos = unit.toNanos((long)duration);
        return this;
    }

    long getExpireAfterAccessNanos() {
        if (this.expireAfterAccessNanos == -1L) {
            return 0L;
        }
        long l = this.expireAfterAccessNanos;
        return l;
    }

    @GwtIncompatible
    public CacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit) {
        Preconditions.checkNotNull(unit);
        Preconditions.checkState((boolean)(this.refreshNanos == -1L), (String)"refresh was already set to %s ns", (long)this.refreshNanos);
        Preconditions.checkArgument((boolean)(duration > 0L), (String)"duration must be positive: %s %s", (long)duration, (Object)((Object)unit));
        this.refreshNanos = unit.toNanos((long)duration);
        return this;
    }

    long getRefreshNanos() {
        if (this.refreshNanos == -1L) {
            return 0L;
        }
        long l = this.refreshNanos;
        return l;
    }

    public CacheBuilder<K, V> ticker(Ticker ticker) {
        Preconditions.checkState((boolean)(this.ticker == null));
        this.ticker = Preconditions.checkNotNull(ticker);
        return this;
    }

    Ticker getTicker(boolean recordsTime) {
        Ticker ticker;
        if (this.ticker != null) {
            return this.ticker;
        }
        if (recordsTime) {
            ticker = Ticker.systemTicker();
            return ticker;
        }
        ticker = NULL_TICKER;
        return ticker;
    }

    @CheckReturnValue
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> listener) {
        Preconditions.checkState((boolean)(this.removalListener == null));
        CacheBuilder me = this;
        me.removalListener = Preconditions.checkNotNull(listener);
        return me;
    }

    <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener() {
        return (RemovalListener)MoreObjects.firstNonNull(this.removalListener, NullListener.INSTANCE);
    }

    public CacheBuilder<K, V> recordStats() {
        this.statsCounterSupplier = CACHE_STATS_COUNTER;
        return this;
    }

    boolean isRecordingStats() {
        if (this.statsCounterSupplier != CACHE_STATS_COUNTER) return false;
        return true;
    }

    Supplier<? extends AbstractCache.StatsCounter> getStatsCounterSupplier() {
        return this.statsCounterSupplier;
    }

    public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> loader) {
        this.checkWeightWithWeigher();
        return new LocalCache.LocalLoadingCache<K1, V1>(this, loader);
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        this.checkWeightWithWeigher();
        this.checkNonLoadingCache();
        return new LocalCache.LocalManualCache<K, V>(this);
    }

    private void checkNonLoadingCache() {
        Preconditions.checkState((boolean)(this.refreshNanos == -1L), (Object)"refreshAfterWrite requires a LoadingCache");
    }

    private void checkWeightWithWeigher() {
        if (this.weigher == null) {
            Preconditions.checkState((boolean)(this.maximumWeight == -1L), (Object)"maximumWeight requires weigher");
            return;
        }
        if (!this.strictParsing) {
            if (this.maximumWeight != -1L) return;
            logger.log((Level)Level.WARNING, (String)"ignoring weigher specified without maximumWeight");
            return;
        }
        Preconditions.checkState((boolean)(this.maximumWeight != -1L), (Object)"weigher requires maximumWeight");
    }

    public String toString() {
        MoreObjects.ToStringHelper s = MoreObjects.toStringHelper((Object)this);
        if (this.initialCapacity != -1) {
            s.add((String)"initialCapacity", (int)this.initialCapacity);
        }
        if (this.concurrencyLevel != -1) {
            s.add((String)"concurrencyLevel", (int)this.concurrencyLevel);
        }
        if (this.maximumSize != -1L) {
            s.add((String)"maximumSize", (long)this.maximumSize);
        }
        if (this.maximumWeight != -1L) {
            s.add((String)"maximumWeight", (long)this.maximumWeight);
        }
        if (this.expireAfterWriteNanos != -1L) {
            s.add((String)"expireAfterWrite", (Object)(this.expireAfterWriteNanos + "ns"));
        }
        if (this.expireAfterAccessNanos != -1L) {
            s.add((String)"expireAfterAccess", (Object)(this.expireAfterAccessNanos + "ns"));
        }
        if (this.keyStrength != null) {
            s.add((String)"keyStrength", (Object)Ascii.toLowerCase((String)this.keyStrength.toString()));
        }
        if (this.valueStrength != null) {
            s.add((String)"valueStrength", (Object)Ascii.toLowerCase((String)this.valueStrength.toString()));
        }
        if (this.keyEquivalence != null) {
            s.addValue((Object)"keyEquivalence");
        }
        if (this.valueEquivalence != null) {
            s.addValue((Object)"valueEquivalence");
        }
        if (this.removalListener == null) return s.toString();
        s.addValue((Object)"removalListener");
        return s.toString();
    }
}

