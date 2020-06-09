/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.LocalCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtIncompatible
public final class CacheBuilderSpec {
    private static final Splitter KEYS_SPLITTER = Splitter.on((char)',').trimResults();
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on((char)'=').trimResults();
    private static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap.builder().put("initialCapacity", new InitialCapacityParser()).put((String)"maximumSize", (InitialCapacityParser)((Object)new MaximumSizeParser())).put((String)"maximumWeight", (InitialCapacityParser)((Object)new MaximumWeightParser())).put((String)"concurrencyLevel", (InitialCapacityParser)((Object)new ConcurrencyLevelParser())).put((String)"weakKeys", (InitialCapacityParser)((Object)new KeyStrengthParser((LocalCache.Strength)LocalCache.Strength.WEAK))).put((String)"softValues", (InitialCapacityParser)((Object)new ValueStrengthParser((LocalCache.Strength)LocalCache.Strength.SOFT))).put((String)"weakValues", (InitialCapacityParser)((Object)new ValueStrengthParser((LocalCache.Strength)LocalCache.Strength.WEAK))).put((String)"recordStats", (InitialCapacityParser)((Object)new RecordStatsParser())).put((String)"expireAfterAccess", (InitialCapacityParser)((Object)new AccessDurationParser())).put((String)"expireAfterWrite", (InitialCapacityParser)((Object)new WriteDurationParser())).put((String)"refreshAfterWrite", (InitialCapacityParser)((Object)new RefreshDurationParser())).put((String)"refreshInterval", (InitialCapacityParser)((Object)new RefreshDurationParser())).build();
    @VisibleForTesting
    Integer initialCapacity;
    @VisibleForTesting
    Long maximumSize;
    @VisibleForTesting
    Long maximumWeight;
    @VisibleForTesting
    Integer concurrencyLevel;
    @VisibleForTesting
    LocalCache.Strength keyStrength;
    @VisibleForTesting
    LocalCache.Strength valueStrength;
    @VisibleForTesting
    Boolean recordStats;
    @VisibleForTesting
    long writeExpirationDuration;
    @VisibleForTesting
    TimeUnit writeExpirationTimeUnit;
    @VisibleForTesting
    long accessExpirationDuration;
    @VisibleForTesting
    TimeUnit accessExpirationTimeUnit;
    @VisibleForTesting
    long refreshDuration;
    @VisibleForTesting
    TimeUnit refreshTimeUnit;
    private final String specification;

    private CacheBuilderSpec(String specification) {
        this.specification = specification;
    }

    public static CacheBuilderSpec parse(String cacheBuilderSpecification) {
        CacheBuilderSpec spec = new CacheBuilderSpec((String)cacheBuilderSpecification);
        if (cacheBuilderSpecification.isEmpty()) return spec;
        Iterator<String> i$ = KEYS_SPLITTER.split((CharSequence)cacheBuilderSpecification).iterator();
        while (i$.hasNext()) {
            String keyValuePair = i$.next();
            ImmutableList<String> keyAndValue = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split((CharSequence)keyValuePair));
            Preconditions.checkArgument((boolean)(!keyAndValue.isEmpty()), (Object)"blank key-value pair");
            Preconditions.checkArgument((boolean)(keyAndValue.size() <= 2), (String)"key-value pair %s with more than one equals sign", (Object)keyValuePair);
            String key = (String)keyAndValue.get((int)0);
            ValueParser valueParser = VALUE_PARSERS.get((Object)key);
            Preconditions.checkArgument((boolean)(valueParser != null), (String)"unknown key %s", (Object)key);
            String value = keyAndValue.size() == 1 ? null : (String)keyAndValue.get((int)1);
            valueParser.parse((CacheBuilderSpec)spec, (String)key, (String)value);
        }
        return spec;
    }

    public static CacheBuilderSpec disableCaching() {
        return CacheBuilderSpec.parse((String)"maximumSize=0");
    }

    /*
     * Unable to fully structure code
     */
    CacheBuilder<Object, Object> toCacheBuilder() {
        builder = CacheBuilder.newBuilder();
        if (this.initialCapacity != null) {
            builder.initialCapacity((int)this.initialCapacity.intValue());
        }
        if (this.maximumSize != null) {
            builder.maximumSize((long)this.maximumSize.longValue());
        }
        if (this.maximumWeight != null) {
            builder.maximumWeight((long)this.maximumWeight.longValue());
        }
        if (this.concurrencyLevel != null) {
            builder.concurrencyLevel((int)this.concurrencyLevel.intValue());
        }
        if (this.keyStrength != null) {
            switch (1.$SwitchMap$com$google$common$cache$LocalCache$Strength[this.keyStrength.ordinal()]) {
                case 1: {
                    builder.weakKeys();
                    ** break;
                }
            }
            throw new AssertionError();
        }
lbl21: // 3 sources:
        if (this.valueStrength != null) {
            switch (1.$SwitchMap$com$google$common$cache$LocalCache$Strength[this.valueStrength.ordinal()]) {
                case 2: {
                    builder.softValues();
                    ** break;
                }
                case 1: {
                    builder.weakValues();
                    ** break;
                }
            }
            throw new AssertionError();
        }
lbl32: // 4 sources:
        if (this.recordStats != null && this.recordStats.booleanValue()) {
            builder.recordStats();
        }
        if (this.writeExpirationTimeUnit != null) {
            builder.expireAfterWrite((long)this.writeExpirationDuration, (TimeUnit)this.writeExpirationTimeUnit);
        }
        if (this.accessExpirationTimeUnit != null) {
            builder.expireAfterAccess((long)this.accessExpirationDuration, (TimeUnit)this.accessExpirationTimeUnit);
        }
        if (this.refreshTimeUnit == null) return builder;
        builder.refreshAfterWrite((long)this.refreshDuration, (TimeUnit)this.refreshTimeUnit);
        return builder;
    }

    public String toParsableString() {
        return this.specification;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).addValue((Object)this.toParsableString()).toString();
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, this.recordStats, CacheBuilderSpec.durationInNanos((long)this.writeExpirationDuration, (TimeUnit)this.writeExpirationTimeUnit), CacheBuilderSpec.durationInNanos((long)this.accessExpirationDuration, (TimeUnit)this.accessExpirationTimeUnit), CacheBuilderSpec.durationInNanos((long)this.refreshDuration, (TimeUnit)this.refreshTimeUnit)});
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CacheBuilderSpec)) {
            return false;
        }
        CacheBuilderSpec that = (CacheBuilderSpec)obj;
        if (!Objects.equal((Object)this.initialCapacity, (Object)that.initialCapacity)) return false;
        if (!Objects.equal((Object)this.maximumSize, (Object)that.maximumSize)) return false;
        if (!Objects.equal((Object)this.maximumWeight, (Object)that.maximumWeight)) return false;
        if (!Objects.equal((Object)this.concurrencyLevel, (Object)that.concurrencyLevel)) return false;
        if (!Objects.equal((Object)((Object)this.keyStrength), (Object)((Object)that.keyStrength))) return false;
        if (!Objects.equal((Object)((Object)this.valueStrength), (Object)((Object)that.valueStrength))) return false;
        if (!Objects.equal((Object)this.recordStats, (Object)that.recordStats)) return false;
        if (!Objects.equal((Object)CacheBuilderSpec.durationInNanos((long)this.writeExpirationDuration, (TimeUnit)this.writeExpirationTimeUnit), (Object)CacheBuilderSpec.durationInNanos((long)that.writeExpirationDuration, (TimeUnit)that.writeExpirationTimeUnit))) return false;
        if (!Objects.equal((Object)CacheBuilderSpec.durationInNanos((long)this.accessExpirationDuration, (TimeUnit)this.accessExpirationTimeUnit), (Object)CacheBuilderSpec.durationInNanos((long)that.accessExpirationDuration, (TimeUnit)that.accessExpirationTimeUnit))) return false;
        if (!Objects.equal((Object)CacheBuilderSpec.durationInNanos((long)this.refreshDuration, (TimeUnit)this.refreshTimeUnit), (Object)CacheBuilderSpec.durationInNanos((long)that.refreshDuration, (TimeUnit)that.refreshTimeUnit))) return false;
        return true;
    }

    @Nullable
    private static Long durationInNanos(long duration, @Nullable TimeUnit unit) {
        if (unit == null) {
            return null;
        }
        Long l = Long.valueOf((long)unit.toNanos((long)duration));
        return l;
    }

    private static String format(String format, Object ... args) {
        return String.format((Locale)Locale.ROOT, (String)format, (Object[])args);
    }

    static /* synthetic */ String access$000(String x0, Object[] x1) {
        return CacheBuilderSpec.format((String)x0, (Object[])x1);
    }
}

