/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.StringUtil;
import java.util.List;

public final class PooledByteBufAllocatorMetric
implements ByteBufAllocatorMetric {
    private final PooledByteBufAllocator allocator;

    PooledByteBufAllocatorMetric(PooledByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    public int numHeapArenas() {
        return this.allocator.numHeapArenas();
    }

    public int numDirectArenas() {
        return this.allocator.numDirectArenas();
    }

    public List<PoolArenaMetric> heapArenas() {
        return this.allocator.heapArenas();
    }

    public List<PoolArenaMetric> directArenas() {
        return this.allocator.directArenas();
    }

    public int numThreadLocalCaches() {
        return this.allocator.numThreadLocalCaches();
    }

    public int tinyCacheSize() {
        return this.allocator.tinyCacheSize();
    }

    public int smallCacheSize() {
        return this.allocator.smallCacheSize();
    }

    public int normalCacheSize() {
        return this.allocator.normalCacheSize();
    }

    public int chunkSize() {
        return this.allocator.chunkSize();
    }

    @Override
    public long usedHeapMemory() {
        return this.allocator.usedHeapMemory();
    }

    @Override
    public long usedDirectMemory() {
        return this.allocator.usedDirectMemory();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder((int)256);
        sb.append((String)StringUtil.simpleClassName((Object)this)).append((String)"(usedHeapMemory: ").append((long)this.usedHeapMemory()).append((String)"; usedDirectMemory: ").append((long)this.usedDirectMemory()).append((String)"; numHeapArenas: ").append((int)this.numHeapArenas()).append((String)"; numDirectArenas: ").append((int)this.numDirectArenas()).append((String)"; tinyCacheSize: ").append((int)this.tinyCacheSize()).append((String)"; smallCacheSize: ").append((int)this.smallCacheSize()).append((String)"; normalCacheSize: ").append((int)this.normalCacheSize()).append((String)"; numThreadLocalCaches: ").append((int)this.numThreadLocalCaches()).append((String)"; chunkSize: ").append((int)this.chunkSize()).append((char)')');
        return sb.toString();
    }
}

