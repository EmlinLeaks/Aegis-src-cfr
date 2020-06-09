/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

final class PoolThreadCache {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;
    private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
    private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
    private final int numShiftsNormalDirect;
    private final int numShiftsNormalHeap;
    private final int freeSweepAllocationThreshold;
    private final AtomicBoolean freed = new AtomicBoolean();
    private int allocations;

    PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
        ObjectUtil.checkPositiveOrZero((int)maxCachedBufferCapacity, (String)"maxCachedBufferCapacity");
        this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
        this.heapArena = heapArena;
        this.directArena = directArena;
        if (directArena != null) {
            this.tinySubPageDirectCaches = PoolThreadCache.createSubPageCaches((int)tinyCacheSize, (int)32, (PoolArena.SizeClass)PoolArena.SizeClass.Tiny);
            this.smallSubPageDirectCaches = PoolThreadCache.createSubPageCaches((int)smallCacheSize, (int)directArena.numSmallSubpagePools, (PoolArena.SizeClass)PoolArena.SizeClass.Small);
            this.numShiftsNormalDirect = PoolThreadCache.log2((int)directArena.pageSize);
            this.normalDirectCaches = PoolThreadCache.createNormalCaches((int)normalCacheSize, (int)maxCachedBufferCapacity, directArena);
            directArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageDirectCaches = null;
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
            this.numShiftsNormalDirect = -1;
        }
        if (heapArena != null) {
            this.tinySubPageHeapCaches = PoolThreadCache.createSubPageCaches((int)tinyCacheSize, (int)32, (PoolArena.SizeClass)PoolArena.SizeClass.Tiny);
            this.smallSubPageHeapCaches = PoolThreadCache.createSubPageCaches((int)smallCacheSize, (int)heapArena.numSmallSubpagePools, (PoolArena.SizeClass)PoolArena.SizeClass.Small);
            this.numShiftsNormalHeap = PoolThreadCache.log2((int)heapArena.pageSize);
            this.normalHeapCaches = PoolThreadCache.createNormalCaches((int)normalCacheSize, (int)maxCachedBufferCapacity, heapArena);
            heapArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageHeapCaches = null;
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
            this.numShiftsNormalHeap = -1;
        }
        if (this.tinySubPageDirectCaches == null && this.smallSubPageDirectCaches == null && this.normalDirectCaches == null && this.tinySubPageHeapCaches == null && this.smallSubPageHeapCaches == null) {
            if (this.normalHeapCaches == null) return;
        }
        if (freeSweepAllocationThreshold >= 1) return;
        throw new IllegalArgumentException((String)("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)"));
    }

    private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches, PoolArena.SizeClass sizeClass) {
        if (cacheSize <= 0) return null;
        if (numCaches <= 0) return null;
        MemoryRegionCache[] cache = new MemoryRegionCache[numCaches];
        int i = 0;
        while (i < cache.length) {
            cache[i] = new SubPageMemoryRegionCache<T>((int)cacheSize, (PoolArena.SizeClass)sizeClass);
            ++i;
        }
        return cache;
    }

    private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
        if (cacheSize <= 0) return null;
        if (maxCachedBufferCapacity <= 0) return null;
        int max = Math.min((int)area.chunkSize, (int)maxCachedBufferCapacity);
        int arraySize = Math.max((int)1, (int)(PoolThreadCache.log2((int)(max / area.pageSize)) + 1));
        MemoryRegionCache[] cache = new MemoryRegionCache[arraySize];
        int i = 0;
        while (i < cache.length) {
            cache[i] = new NormalMemoryRegionCache<T>((int)cacheSize);
            ++i;
        }
        return cache;
    }

    private static int log2(int val) {
        int res = 0;
        while (val > 1) {
            val >>= 1;
            ++res;
        }
        return res;
    }

    boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForTiny(area, (int)normCapacity), buf, (int)reqCapacity);
    }

    boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForSmall(area, (int)normCapacity), buf, (int)reqCapacity);
    }

    boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForNormal(area, (int)normCapacity), buf, (int)reqCapacity);
    }

    private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity) {
        if (cache == null) {
            return false;
        }
        boolean allocated = cache.allocate(buf, (int)reqCapacity);
        if (++this.allocations < this.freeSweepAllocationThreshold) return allocated;
        this.allocations = 0;
        this.trim();
        return allocated;
    }

    boolean add(PoolArena<?> area, PoolChunk chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
        MemoryRegionCache<?> cache = this.cache(area, (int)normCapacity, (PoolArena.SizeClass)sizeClass);
        if (cache != null) return cache.add(chunk, (ByteBuffer)nioBuffer, (long)handle);
        return false;
    }

    private MemoryRegionCache<?> cache(PoolArena<?> area, int normCapacity, PoolArena.SizeClass sizeClass) {
        switch (1.$SwitchMap$io$netty$buffer$PoolArena$SizeClass[sizeClass.ordinal()]) {
            case 1: {
                return this.cacheForNormal(area, (int)normCapacity);
            }
            case 2: {
                return this.cacheForSmall(area, (int)normCapacity);
            }
            case 3: {
                return this.cacheForTiny(area, (int)normCapacity);
            }
        }
        throw new Error();
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
            return;
        }
        finally {
            this.free((boolean)true);
        }
    }

    void free(boolean finalizer) {
        if (!this.freed.compareAndSet((boolean)false, (boolean)true)) return;
        int numFreed = PoolThreadCache.free(this.tinySubPageDirectCaches, (boolean)finalizer) + PoolThreadCache.free(this.smallSubPageDirectCaches, (boolean)finalizer) + PoolThreadCache.free(this.normalDirectCaches, (boolean)finalizer) + PoolThreadCache.free(this.tinySubPageHeapCaches, (boolean)finalizer) + PoolThreadCache.free(this.smallSubPageHeapCaches, (boolean)finalizer) + PoolThreadCache.free(this.normalHeapCaches, (boolean)finalizer);
        if (numFreed > 0 && logger.isDebugEnabled()) {
            logger.debug((String)"Freed {} thread-local buffer(s) from thread: {}", (Object)Integer.valueOf((int)numFreed), (Object)Thread.currentThread().getName());
        }
        if (this.directArena != null) {
            this.directArena.numThreadCaches.getAndDecrement();
        }
        if (this.heapArena == null) return;
        this.heapArena.numThreadCaches.getAndDecrement();
    }

    private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
        if (caches == null) {
            return 0;
        }
        int numFreed = 0;
        MemoryRegionCache<?>[] arrmemoryRegionCache = caches;
        int n = arrmemoryRegionCache.length;
        int n2 = 0;
        while (n2 < n) {
            MemoryRegionCache<?> c = arrmemoryRegionCache[n2];
            numFreed += PoolThreadCache.free(c, (boolean)finalizer);
            ++n2;
        }
        return numFreed;
    }

    private static int free(MemoryRegionCache<?> cache, boolean finalizer) {
        if (cache != null) return cache.free((boolean)finalizer);
        return 0;
    }

    void trim() {
        PoolThreadCache.trim(this.tinySubPageDirectCaches);
        PoolThreadCache.trim(this.smallSubPageDirectCaches);
        PoolThreadCache.trim(this.normalDirectCaches);
        PoolThreadCache.trim(this.tinySubPageHeapCaches);
        PoolThreadCache.trim(this.smallSubPageHeapCaches);
        PoolThreadCache.trim(this.normalHeapCaches);
    }

    private static void trim(MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return;
        }
        MemoryRegionCache<?>[] arrmemoryRegionCache = caches;
        int n = arrmemoryRegionCache.length;
        int n2 = 0;
        while (n2 < n) {
            MemoryRegionCache<?> c = arrmemoryRegionCache[n2];
            PoolThreadCache.trim(c);
            ++n2;
        }
    }

    private static void trim(MemoryRegionCache<?> cache) {
        if (cache == null) {
            return;
        }
        cache.trim();
    }

    private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.tinyIdx((int)normCapacity);
        if (!area.isDirect()) return PoolThreadCache.cache(this.tinySubPageHeapCaches, (int)idx);
        return PoolThreadCache.cache(this.tinySubPageDirectCaches, (int)idx);
    }

    private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.smallIdx((int)normCapacity);
        if (!area.isDirect()) return PoolThreadCache.cache(this.smallSubPageHeapCaches, (int)idx);
        return PoolThreadCache.cache(this.smallSubPageDirectCaches, (int)idx);
    }

    private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int normCapacity) {
        if (area.isDirect()) {
            int idx = PoolThreadCache.log2((int)(normCapacity >> this.numShiftsNormalDirect));
            return PoolThreadCache.cache(this.normalDirectCaches, (int)idx);
        }
        int idx = PoolThreadCache.log2((int)(normCapacity >> this.numShiftsNormalHeap));
        return PoolThreadCache.cache(this.normalHeapCaches, (int)idx);
    }

    private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int idx) {
        if (cache == null) return null;
        if (idx <= cache.length - 1) return cache[idx];
        return null;
    }
}

