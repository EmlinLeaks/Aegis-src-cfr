/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;
import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeHeapByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PooledByteBufAllocator
extends AbstractByteBufAllocator
implements ByteBufAllocatorMetricProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
    private static final int DEFAULT_NUM_HEAP_ARENA;
    private static final int DEFAULT_NUM_DIRECT_ARENA;
    private static final int DEFAULT_PAGE_SIZE;
    private static final int DEFAULT_MAX_ORDER;
    private static final int DEFAULT_TINY_CACHE_SIZE;
    private static final int DEFAULT_SMALL_CACHE_SIZE;
    private static final int DEFAULT_NORMAL_CACHE_SIZE;
    private static final int DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
    private static final int DEFAULT_CACHE_TRIM_INTERVAL;
    private static final long DEFAULT_CACHE_TRIM_INTERVAL_MILLIS;
    private static final boolean DEFAULT_USE_CACHE_FOR_ALL_THREADS;
    private static final int DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT;
    static final int DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK;
    private static final int MIN_PAGE_SIZE = 4096;
    private static final int MAX_CHUNK_SIZE = 1073741824;
    private final Runnable trimTask = new Runnable((PooledByteBufAllocator)this){
        final /* synthetic */ PooledByteBufAllocator this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.trimCurrentThreadCache();
        }
    };
    public static final PooledByteBufAllocator DEFAULT;
    private final PoolArena<byte[]>[] heapArenas;
    private final PoolArena<ByteBuffer>[] directArenas;
    private final int tinyCacheSize;
    private final int smallCacheSize;
    private final int normalCacheSize;
    private final List<PoolArenaMetric> heapArenaMetrics;
    private final List<PoolArenaMetric> directArenaMetrics;
    private final PoolThreadLocalCache threadCache;
    private final int chunkSize;
    private final PooledByteBufAllocatorMetric metric;

    public PooledByteBufAllocator() {
        this((boolean)false);
    }

    public PooledByteBufAllocator(boolean preferDirect) {
        this((boolean)preferDirect, (int)DEFAULT_NUM_HEAP_ARENA, (int)DEFAULT_NUM_DIRECT_ARENA, (int)DEFAULT_PAGE_SIZE, (int)DEFAULT_MAX_ORDER);
    }

    public PooledByteBufAllocator(int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
        this((boolean)false, (int)nHeapArena, (int)nDirectArena, (int)pageSize, (int)maxOrder);
    }

    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
        this((boolean)preferDirect, (int)nHeapArena, (int)nDirectArena, (int)pageSize, (int)maxOrder, (int)DEFAULT_TINY_CACHE_SIZE, (int)DEFAULT_SMALL_CACHE_SIZE, (int)DEFAULT_NORMAL_CACHE_SIZE);
    }

    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize) {
        this((boolean)preferDirect, (int)nHeapArena, (int)nDirectArena, (int)pageSize, (int)maxOrder, (int)tinyCacheSize, (int)smallCacheSize, (int)normalCacheSize, (boolean)DEFAULT_USE_CACHE_FOR_ALL_THREADS, (int)DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
    }

    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads) {
        this((boolean)preferDirect, (int)nHeapArena, (int)nDirectArena, (int)pageSize, (int)maxOrder, (int)tinyCacheSize, (int)smallCacheSize, (int)normalCacheSize, (boolean)useCacheForAllThreads, (int)DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
    }

    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads, int directMemoryCacheAlignment) {
        super((boolean)preferDirect);
        ArrayList<PoolArena.HeapArena> metrics;
        PoolArena arena;
        int i;
        this.threadCache = new PoolThreadLocalCache((PooledByteBufAllocator)this, (boolean)useCacheForAllThreads);
        this.tinyCacheSize = tinyCacheSize;
        this.smallCacheSize = smallCacheSize;
        this.normalCacheSize = normalCacheSize;
        this.chunkSize = PooledByteBufAllocator.validateAndCalculateChunkSize((int)pageSize, (int)maxOrder);
        ObjectUtil.checkPositiveOrZero((int)nHeapArena, (String)"nHeapArena");
        ObjectUtil.checkPositiveOrZero((int)nDirectArena, (String)"nDirectArena");
        ObjectUtil.checkPositiveOrZero((int)directMemoryCacheAlignment, (String)"directMemoryCacheAlignment");
        if (directMemoryCacheAlignment > 0 && !PooledByteBufAllocator.isDirectMemoryCacheAlignmentSupported()) {
            throw new IllegalArgumentException((String)"directMemoryCacheAlignment is not supported");
        }
        if ((directMemoryCacheAlignment & -directMemoryCacheAlignment) != directMemoryCacheAlignment) {
            throw new IllegalArgumentException((String)("directMemoryCacheAlignment: " + directMemoryCacheAlignment + " (expected: power of two)"));
        }
        int pageShifts = PooledByteBufAllocator.validateAndCalculatePageShifts((int)pageSize);
        if (nHeapArena > 0) {
            this.heapArenas = PooledByteBufAllocator.newArenaArray((int)nHeapArena);
            metrics = new ArrayList<PoolArena.HeapArena>((int)this.heapArenas.length);
            for (i = 0; i < this.heapArenas.length; ++i) {
                this.heapArenas[i] = arena = new PoolArena.HeapArena((PooledByteBufAllocator)this, (int)pageSize, (int)maxOrder, (int)pageShifts, (int)this.chunkSize, (int)directMemoryCacheAlignment);
                metrics.add(arena);
            }
            this.heapArenaMetrics = Collections.unmodifiableList(metrics);
        } else {
            this.heapArenas = null;
            this.heapArenaMetrics = Collections.emptyList();
        }
        if (nDirectArena > 0) {
            this.directArenas = PooledByteBufAllocator.newArenaArray((int)nDirectArena);
            metrics = new ArrayList<E>((int)this.directArenas.length);
            for (i = 0; i < this.directArenas.length; ++i) {
                this.directArenas[i] = arena = new PoolArena.DirectArena((PooledByteBufAllocator)this, (int)pageSize, (int)maxOrder, (int)pageShifts, (int)this.chunkSize, (int)directMemoryCacheAlignment);
                metrics.add(arena);
            }
            this.directArenaMetrics = Collections.unmodifiableList(metrics);
        } else {
            this.directArenas = null;
            this.directArenaMetrics = Collections.emptyList();
        }
        this.metric = new PooledByteBufAllocatorMetric((PooledByteBufAllocator)this);
    }

    private static <T> PoolArena<T>[] newArenaArray(int size) {
        return new PoolArena[size];
    }

    private static int validateAndCalculatePageShifts(int pageSize) {
        if (pageSize < 4096) {
            throw new IllegalArgumentException((String)("pageSize: " + pageSize + " (expected: " + 4096 + ")"));
        }
        if ((pageSize & pageSize - 1) == 0) return 31 - Integer.numberOfLeadingZeros((int)pageSize);
        throw new IllegalArgumentException((String)("pageSize: " + pageSize + " (expected: power of 2)"));
    }

    private static int validateAndCalculateChunkSize(int pageSize, int maxOrder) {
        if (maxOrder > 14) {
            throw new IllegalArgumentException((String)("maxOrder: " + maxOrder + " (expected: 0-14)"));
        }
        int chunkSize = pageSize;
        int i = maxOrder;
        while (i > 0) {
            if (chunkSize > 536870912) {
                throw new IllegalArgumentException((String)String.format((String)"pageSize (%d) << maxOrder (%d) must not exceed %d", (Object[])new Object[]{Integer.valueOf((int)pageSize), Integer.valueOf((int)maxOrder), Integer.valueOf((int)1073741824)}));
            }
            chunkSize <<= 1;
            --i;
        }
        return chunkSize;
    }

    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        AbstractReferenceCountedByteBuf buf;
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
        PoolArena<byte[]> heapArena = cache.heapArena;
        if (heapArena != null) {
            buf = heapArena.allocate((PoolThreadCache)cache, (int)initialCapacity, (int)maxCapacity);
            return PooledByteBufAllocator.toLeakAwareBuffer(buf);
        }
        buf = PlatformDependent.hasUnsafe() ? new UnpooledUnsafeHeapByteBuf((ByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity) : new UnpooledHeapByteBuf((ByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity);
        return PooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    @Override
    protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
        AbstractReferenceCountedByteBuf buf;
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
        PoolArena<ByteBuffer> directArena = cache.directArena;
        if (directArena != null) {
            buf = directArena.allocate((PoolThreadCache)cache, (int)initialCapacity, (int)maxCapacity);
            return PooledByteBufAllocator.toLeakAwareBuffer(buf);
        }
        buf = PlatformDependent.hasUnsafe() ? UnsafeByteBufUtil.newUnsafeDirectByteBuf((ByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity) : new UnpooledDirectByteBuf((ByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity);
        return PooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    public static int defaultNumHeapArena() {
        return DEFAULT_NUM_HEAP_ARENA;
    }

    public static int defaultNumDirectArena() {
        return DEFAULT_NUM_DIRECT_ARENA;
    }

    public static int defaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public static int defaultMaxOrder() {
        return DEFAULT_MAX_ORDER;
    }

    public static boolean defaultUseCacheForAllThreads() {
        return DEFAULT_USE_CACHE_FOR_ALL_THREADS;
    }

    public static boolean defaultPreferDirect() {
        return PlatformDependent.directBufferPreferred();
    }

    public static int defaultTinyCacheSize() {
        return DEFAULT_TINY_CACHE_SIZE;
    }

    public static int defaultSmallCacheSize() {
        return DEFAULT_SMALL_CACHE_SIZE;
    }

    public static int defaultNormalCacheSize() {
        return DEFAULT_NORMAL_CACHE_SIZE;
    }

    public static boolean isDirectMemoryCacheAlignmentSupported() {
        return PlatformDependent.hasUnsafe();
    }

    @Override
    public boolean isDirectBufferPooled() {
        if (this.directArenas == null) return false;
        return true;
    }

    @Deprecated
    public boolean hasThreadLocalCache() {
        return this.threadCache.isSet();
    }

    @Deprecated
    public void freeThreadLocalCache() {
        this.threadCache.remove();
    }

    @Override
    public PooledByteBufAllocatorMetric metric() {
        return this.metric;
    }

    @Deprecated
    public int numHeapArenas() {
        return this.heapArenaMetrics.size();
    }

    @Deprecated
    public int numDirectArenas() {
        return this.directArenaMetrics.size();
    }

    @Deprecated
    public List<PoolArenaMetric> heapArenas() {
        return this.heapArenaMetrics;
    }

    @Deprecated
    public List<PoolArenaMetric> directArenas() {
        return this.directArenaMetrics;
    }

    @Deprecated
    public int numThreadLocalCaches() {
        PoolArena<Object>[] arenas;
        PoolArena<Object>[] arrpoolArena = arenas = this.heapArenas != null ? this.heapArenas : this.directArenas;
        if (arenas == null) {
            return 0;
        }
        int total = 0;
        PoolArena<Object>[] arrpoolArena2 = arenas;
        int n = arrpoolArena2.length;
        int n2 = 0;
        while (n2 < n) {
            PoolArena<Object> arena = arrpoolArena2[n2];
            total += arena.numThreadCaches.get();
            ++n2;
        }
        return total;
    }

    @Deprecated
    public int tinyCacheSize() {
        return this.tinyCacheSize;
    }

    @Deprecated
    public int smallCacheSize() {
        return this.smallCacheSize;
    }

    @Deprecated
    public int normalCacheSize() {
        return this.normalCacheSize;
    }

    @Deprecated
    public final int chunkSize() {
        return this.chunkSize;
    }

    final long usedHeapMemory() {
        return PooledByteBufAllocator.usedMemory(this.heapArenas);
    }

    final long usedDirectMemory() {
        return PooledByteBufAllocator.usedMemory(this.directArenas);
    }

    private static long usedMemory(PoolArena<?>[] arenas) {
        if (arenas == null) {
            return -1L;
        }
        long used = 0L;
        PoolArena<?>[] arrpoolArena = arenas;
        int n = arrpoolArena.length;
        int n2 = 0;
        while (n2 < n) {
            PoolArena<?> arena = arrpoolArena[n2];
            if ((used += arena.numActiveBytes()) < 0L) {
                return Long.MAX_VALUE;
            }
            ++n2;
        }
        return used;
    }

    final PoolThreadCache threadCache() {
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
        if ($assertionsDisabled) return cache;
        if (cache != null) return cache;
        throw new AssertionError();
    }

    public boolean trimCurrentThreadCache() {
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.getIfExists();
        if (cache == null) return false;
        cache.trim();
        return true;
    }

    public String dumpStats() {
        int heapArenasLen = this.heapArenas == null ? 0 : this.heapArenas.length;
        StringBuilder buf = new StringBuilder((int)512).append((int)heapArenasLen).append((String)" heap arena(s):").append((String)StringUtil.NEWLINE);
        if (heapArenasLen > 0) {
            for (PoolArena<byte[]> a : this.heapArenas) {
                buf.append(a);
            }
        }
        int directArenasLen = this.directArenas == null ? 0 : this.directArenas.length;
        buf.append((int)directArenasLen).append((String)" direct arena(s):").append((String)StringUtil.NEWLINE);
        if (directArenasLen <= 0) return buf.toString();
        PoolArena<ByteBuffer>[] arrpoolArena = this.directArenas;
        int n = arrpoolArena.length;
        int a = 0;
        while (a < n) {
            PoolArena<ByteBuffer> a2 = arrpoolArena[a];
            buf.append(a2);
            ++a;
        }
        return buf.toString();
    }

    static /* synthetic */ PoolArena[] access$000(PooledByteBufAllocator x0) {
        return x0.heapArenas;
    }

    static /* synthetic */ PoolArena[] access$100(PooledByteBufAllocator x0) {
        return x0.directArenas;
    }

    static /* synthetic */ int access$200(PooledByteBufAllocator x0) {
        return x0.tinyCacheSize;
    }

    static /* synthetic */ int access$300(PooledByteBufAllocator x0) {
        return x0.smallCacheSize;
    }

    static /* synthetic */ int access$400(PooledByteBufAllocator x0) {
        return x0.normalCacheSize;
    }

    static /* synthetic */ int access$500() {
        return DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
    }

    static /* synthetic */ int access$600() {
        return DEFAULT_CACHE_TRIM_INTERVAL;
    }

    static /* synthetic */ long access$700() {
        return DEFAULT_CACHE_TRIM_INTERVAL_MILLIS;
    }

    static /* synthetic */ Runnable access$800(PooledByteBufAllocator x0) {
        return x0.trimTask;
    }

    static {
        int defaultPageSize = SystemPropertyUtil.getInt((String)"io.netty.allocator.pageSize", (int)8192);
        Throwable pageSizeFallbackCause = null;
        try {
            PooledByteBufAllocator.validateAndCalculatePageShifts((int)defaultPageSize);
        }
        catch (Throwable t) {
            pageSizeFallbackCause = t;
            defaultPageSize = 8192;
        }
        DEFAULT_PAGE_SIZE = defaultPageSize;
        int defaultMaxOrder = SystemPropertyUtil.getInt((String)"io.netty.allocator.maxOrder", (int)11);
        Throwable maxOrderFallbackCause = null;
        try {
            PooledByteBufAllocator.validateAndCalculateChunkSize((int)DEFAULT_PAGE_SIZE, (int)defaultMaxOrder);
        }
        catch (Throwable t) {
            maxOrderFallbackCause = t;
            defaultMaxOrder = 11;
        }
        DEFAULT_MAX_ORDER = defaultMaxOrder;
        Runtime runtime = Runtime.getRuntime();
        int defaultMinNumArena = NettyRuntime.availableProcessors() * 2;
        int defaultChunkSize = DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER;
        DEFAULT_NUM_HEAP_ARENA = Math.max((int)0, (int)SystemPropertyUtil.getInt((String)"io.netty.allocator.numHeapArenas", (int)((int)Math.min((long)((long)defaultMinNumArena), (long)(runtime.maxMemory() / (long)defaultChunkSize / 2L / 3L)))));
        DEFAULT_NUM_DIRECT_ARENA = Math.max((int)0, (int)SystemPropertyUtil.getInt((String)"io.netty.allocator.numDirectArenas", (int)((int)Math.min((long)((long)defaultMinNumArena), (long)(PlatformDependent.maxDirectMemory() / (long)defaultChunkSize / 2L / 3L)))));
        DEFAULT_TINY_CACHE_SIZE = SystemPropertyUtil.getInt((String)"io.netty.allocator.tinyCacheSize", (int)512);
        DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt((String)"io.netty.allocator.smallCacheSize", (int)256);
        DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt((String)"io.netty.allocator.normalCacheSize", (int)64);
        DEFAULT_MAX_CACHED_BUFFER_CAPACITY = SystemPropertyUtil.getInt((String)"io.netty.allocator.maxCachedBufferCapacity", (int)32768);
        DEFAULT_CACHE_TRIM_INTERVAL = SystemPropertyUtil.getInt((String)"io.netty.allocator.cacheTrimInterval", (int)8192);
        DEFAULT_CACHE_TRIM_INTERVAL_MILLIS = SystemPropertyUtil.getLong((String)"io.netty.allocation.cacheTrimIntervalMillis", (long)0L);
        DEFAULT_USE_CACHE_FOR_ALL_THREADS = SystemPropertyUtil.getBoolean((String)"io.netty.allocator.useCacheForAllThreads", (boolean)true);
        DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT = SystemPropertyUtil.getInt((String)"io.netty.allocator.directMemoryCacheAlignment", (int)0);
        DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK = SystemPropertyUtil.getInt((String)"io.netty.allocator.maxCachedByteBuffersPerChunk", (int)1023);
        if (logger.isDebugEnabled()) {
            logger.debug((String)"-Dio.netty.allocator.numHeapArenas: {}", (Object)Integer.valueOf((int)DEFAULT_NUM_HEAP_ARENA));
            logger.debug((String)"-Dio.netty.allocator.numDirectArenas: {}", (Object)Integer.valueOf((int)DEFAULT_NUM_DIRECT_ARENA));
            if (pageSizeFallbackCause == null) {
                logger.debug((String)"-Dio.netty.allocator.pageSize: {}", (Object)Integer.valueOf((int)DEFAULT_PAGE_SIZE));
            } else {
                logger.debug((String)"-Dio.netty.allocator.pageSize: {}", (Object)Integer.valueOf((int)DEFAULT_PAGE_SIZE), (Object)pageSizeFallbackCause);
            }
            if (maxOrderFallbackCause == null) {
                logger.debug((String)"-Dio.netty.allocator.maxOrder: {}", (Object)Integer.valueOf((int)DEFAULT_MAX_ORDER));
            } else {
                logger.debug((String)"-Dio.netty.allocator.maxOrder: {}", (Object)Integer.valueOf((int)DEFAULT_MAX_ORDER), (Object)maxOrderFallbackCause);
            }
            logger.debug((String)"-Dio.netty.allocator.chunkSize: {}", (Object)Integer.valueOf((int)(DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER)));
            logger.debug((String)"-Dio.netty.allocator.tinyCacheSize: {}", (Object)Integer.valueOf((int)DEFAULT_TINY_CACHE_SIZE));
            logger.debug((String)"-Dio.netty.allocator.smallCacheSize: {}", (Object)Integer.valueOf((int)DEFAULT_SMALL_CACHE_SIZE));
            logger.debug((String)"-Dio.netty.allocator.normalCacheSize: {}", (Object)Integer.valueOf((int)DEFAULT_NORMAL_CACHE_SIZE));
            logger.debug((String)"-Dio.netty.allocator.maxCachedBufferCapacity: {}", (Object)Integer.valueOf((int)DEFAULT_MAX_CACHED_BUFFER_CAPACITY));
            logger.debug((String)"-Dio.netty.allocator.cacheTrimInterval: {}", (Object)Integer.valueOf((int)DEFAULT_CACHE_TRIM_INTERVAL));
            logger.debug((String)"-Dio.netty.allocator.cacheTrimIntervalMillis: {}", (Object)Long.valueOf((long)DEFAULT_CACHE_TRIM_INTERVAL_MILLIS));
            logger.debug((String)"-Dio.netty.allocator.useCacheForAllThreads: {}", (Object)Boolean.valueOf((boolean)DEFAULT_USE_CACHE_FOR_ALL_THREADS));
            logger.debug((String)"-Dio.netty.allocator.maxCachedByteBuffersPerChunk: {}", (Object)Integer.valueOf((int)DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK));
        }
        DEFAULT = new PooledByteBufAllocator((boolean)PlatformDependent.directBufferPreferred());
    }
}

