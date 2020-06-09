/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible
public final class CacheStats {
    private final long hitCount;
    private final long missCount;
    private final long loadSuccessCount;
    private final long loadExceptionCount;
    private final long totalLoadTime;
    private final long evictionCount;

    public CacheStats(long hitCount, long missCount, long loadSuccessCount, long loadExceptionCount, long totalLoadTime, long evictionCount) {
        Preconditions.checkArgument((boolean)(hitCount >= 0L));
        Preconditions.checkArgument((boolean)(missCount >= 0L));
        Preconditions.checkArgument((boolean)(loadSuccessCount >= 0L));
        Preconditions.checkArgument((boolean)(loadExceptionCount >= 0L));
        Preconditions.checkArgument((boolean)(totalLoadTime >= 0L));
        Preconditions.checkArgument((boolean)(evictionCount >= 0L));
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.loadSuccessCount = loadSuccessCount;
        this.loadExceptionCount = loadExceptionCount;
        this.totalLoadTime = totalLoadTime;
        this.evictionCount = evictionCount;
    }

    public long requestCount() {
        return this.hitCount + this.missCount;
    }

    public long hitCount() {
        return this.hitCount;
    }

    public double hitRate() {
        long requestCount = this.requestCount();
        if (requestCount == 0L) {
            return 1.0;
        }
        double d = (double)this.hitCount / (double)requestCount;
        return d;
    }

    public long missCount() {
        return this.missCount;
    }

    public double missRate() {
        long requestCount = this.requestCount();
        if (requestCount == 0L) {
            return 0.0;
        }
        double d = (double)this.missCount / (double)requestCount;
        return d;
    }

    public long loadCount() {
        return this.loadSuccessCount + this.loadExceptionCount;
    }

    public long loadSuccessCount() {
        return this.loadSuccessCount;
    }

    public long loadExceptionCount() {
        return this.loadExceptionCount;
    }

    public double loadExceptionRate() {
        long totalLoadCount = this.loadSuccessCount + this.loadExceptionCount;
        if (totalLoadCount == 0L) {
            return 0.0;
        }
        double d = (double)this.loadExceptionCount / (double)totalLoadCount;
        return d;
    }

    public long totalLoadTime() {
        return this.totalLoadTime;
    }

    public double averageLoadPenalty() {
        long totalLoadCount = this.loadSuccessCount + this.loadExceptionCount;
        if (totalLoadCount == 0L) {
            return 0.0;
        }
        double d = (double)this.totalLoadTime / (double)totalLoadCount;
        return d;
    }

    public long evictionCount() {
        return this.evictionCount;
    }

    public CacheStats minus(CacheStats other) {
        return new CacheStats((long)Math.max((long)0L, (long)(this.hitCount - other.hitCount)), (long)Math.max((long)0L, (long)(this.missCount - other.missCount)), (long)Math.max((long)0L, (long)(this.loadSuccessCount - other.loadSuccessCount)), (long)Math.max((long)0L, (long)(this.loadExceptionCount - other.loadExceptionCount)), (long)Math.max((long)0L, (long)(this.totalLoadTime - other.totalLoadTime)), (long)Math.max((long)0L, (long)(this.evictionCount - other.evictionCount)));
    }

    public CacheStats plus(CacheStats other) {
        return new CacheStats((long)(this.hitCount + other.hitCount), (long)(this.missCount + other.missCount), (long)(this.loadSuccessCount + other.loadSuccessCount), (long)(this.loadExceptionCount + other.loadExceptionCount), (long)(this.totalLoadTime + other.totalLoadTime), (long)(this.evictionCount + other.evictionCount));
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{Long.valueOf((long)this.hitCount), Long.valueOf((long)this.missCount), Long.valueOf((long)this.loadSuccessCount), Long.valueOf((long)this.loadExceptionCount), Long.valueOf((long)this.totalLoadTime), Long.valueOf((long)this.evictionCount)});
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof CacheStats)) return false;
        CacheStats other = (CacheStats)object;
        if (this.hitCount != other.hitCount) return false;
        if (this.missCount != other.missCount) return false;
        if (this.loadSuccessCount != other.loadSuccessCount) return false;
        if (this.loadExceptionCount != other.loadExceptionCount) return false;
        if (this.totalLoadTime != other.totalLoadTime) return false;
        if (this.evictionCount != other.evictionCount) return false;
        return true;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add((String)"hitCount", (long)this.hitCount).add((String)"missCount", (long)this.missCount).add((String)"loadSuccessCount", (long)this.loadSuccessCount).add((String)"loadExceptionCount", (long)this.loadExceptionCount).add((String)"totalLoadTime", (long)this.totalLoadTime).add((String)"evictionCount", (long)this.evictionCount).toString();
    }
}

