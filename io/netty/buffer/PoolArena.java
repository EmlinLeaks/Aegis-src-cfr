/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolSubpageMetric;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

abstract class PoolArena<T>
implements PoolArenaMetric {
    static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
    static final int numTinySubpagePools = 32;
    final PooledByteBufAllocator parent;
    private final int maxOrder;
    final int pageSize;
    final int pageShifts;
    final int chunkSize;
    final int subpageOverflowMask;
    final int numSmallSubpagePools;
    final int directMemoryCacheAlignment;
    final int directMemoryCacheAlignmentMask;
    private final PoolSubpage<T>[] tinySubpagePools;
    private final PoolSubpage<T>[] smallSubpagePools;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> qInit;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;
    private final List<PoolChunkListMetric> chunkListMetrics;
    private long allocationsNormal;
    private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
    private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
    private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
    private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
    private long deallocationsTiny;
    private long deallocationsSmall;
    private long deallocationsNormal;
    private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
    final AtomicInteger numThreadCaches = new AtomicInteger();

    protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int cacheAlignment) {
        int i;
        this.parent = parent;
        this.pageSize = pageSize;
        this.maxOrder = maxOrder;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        this.directMemoryCacheAlignment = cacheAlignment;
        this.directMemoryCacheAlignmentMask = cacheAlignment - 1;
        this.subpageOverflowMask = ~(pageSize - 1);
        this.tinySubpagePools = this.newSubpagePoolArray((int)32);
        for (i = 0; i < this.tinySubpagePools.length; ++i) {
            this.tinySubpagePools[i] = this.newSubpagePoolHead((int)pageSize);
        }
        this.numSmallSubpagePools = pageShifts - 9;
        this.smallSubpagePools = this.newSubpagePoolArray((int)this.numSmallSubpagePools);
        i = 0;
        do {
            if (i >= this.smallSubpagePools.length) {
                this.q100 = new PoolChunkList<T>(this, null, (int)100, (int)Integer.MAX_VALUE, (int)chunkSize);
                this.q075 = new PoolChunkList<T>(this, this.q100, (int)75, (int)100, (int)chunkSize);
                this.q050 = new PoolChunkList<T>(this, this.q075, (int)50, (int)100, (int)chunkSize);
                this.q025 = new PoolChunkList<T>(this, this.q050, (int)25, (int)75, (int)chunkSize);
                this.q000 = new PoolChunkList<T>(this, this.q025, (int)1, (int)50, (int)chunkSize);
                this.qInit = new PoolChunkList<T>(this, this.q000, (int)Integer.MIN_VALUE, (int)25, (int)chunkSize);
                this.q100.prevList(this.q075);
                this.q075.prevList(this.q050);
                this.q050.prevList(this.q025);
                this.q025.prevList(this.q000);
                this.q000.prevList(null);
                this.qInit.prevList(this.qInit);
                ArrayList<PoolChunkList<T>> metrics = new ArrayList<PoolChunkList<T>>((int)6);
                metrics.add(this.qInit);
                metrics.add(this.q000);
                metrics.add(this.q025);
                metrics.add(this.q050);
                metrics.add(this.q075);
                metrics.add(this.q100);
                this.chunkListMetrics = Collections.unmodifiableList(metrics);
                return;
            }
            this.smallSubpagePools[i] = this.newSubpagePoolHead((int)pageSize);
            ++i;
        } while (true);
    }

    private PoolSubpage<T> newSubpagePoolHead(int pageSize) {
        PoolSubpage<T> head = new PoolSubpage<T>((int)pageSize);
        head.prev = head;
        head.next = head;
        return head;
    }

    private PoolSubpage<T>[] newSubpagePoolArray(int size) {
        return new PoolSubpage[size];
    }

    abstract boolean isDirect();

    PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
        PooledByteBuf<T> buf = this.newByteBuf((int)maxCapacity);
        this.allocate((PoolThreadCache)cache, buf, (int)reqCapacity);
        return buf;
    }

    static int tinyIdx(int normCapacity) {
        return normCapacity >>> 4;
    }

    static int smallIdx(int normCapacity) {
        int tableIdx = 0;
        int i = normCapacity >>> 10;
        while (i != 0) {
            i >>>= 1;
            ++tableIdx;
        }
        return tableIdx;
    }

    boolean isTinyOrSmall(int normCapacity) {
        if ((normCapacity & this.subpageOverflowMask) != 0) return false;
        return true;
    }

    static boolean isTiny(int normCapacity) {
        if ((normCapacity & -512) != 0) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
        int normCapacity = this.normalizeCapacity((int)reqCapacity);
        if (this.isTinyOrSmall((int)normCapacity)) {
            PoolSubpage<T>[] table;
            int tableIdx;
            boolean tiny = PoolArena.isTiny((int)normCapacity);
            if (tiny) {
                if (cache.allocateTiny(this, buf, (int)reqCapacity, (int)normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.tinyIdx((int)normCapacity);
                table = this.tinySubpagePools;
            } else {
                if (cache.allocateSmall(this, buf, (int)reqCapacity, (int)normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.smallIdx((int)normCapacity);
                table = this.smallSubpagePools;
            }
            PoolSubpage<T> head = table[tableIdx];
            Object object = head;
            // MONITORENTER : object
            PoolSubpage<T> s = head.next;
            if (s != head) {
                if (!$assertionsDisabled) {
                    if (!s.doNotDestroy) throw new AssertionError();
                    if (s.elemSize != normCapacity) {
                        throw new AssertionError();
                    }
                }
                long handle = s.allocate();
                assert (handle >= 0L);
                s.chunk.initBufWithSubpage(buf, null, (long)handle, (int)reqCapacity);
                this.incTinySmallAllocation((boolean)tiny);
                // MONITOREXIT : object
                return;
            }
            // MONITOREXIT : object
            object = this;
            // MONITORENTER : object
            this.allocateNormal(buf, (int)reqCapacity, (int)normCapacity);
            // MONITOREXIT : object
            this.incTinySmallAllocation((boolean)tiny);
            return;
        }
        if (normCapacity > this.chunkSize) {
            this.allocateHuge(buf, (int)reqCapacity);
            return;
        }
        if (cache.allocateNormal(this, buf, (int)reqCapacity, (int)normCapacity)) {
            return;
        }
        PoolArena tableIdx = this;
        // MONITORENTER : tableIdx
        this.allocateNormal(buf, (int)reqCapacity, (int)normCapacity);
        ++this.allocationsNormal;
        // MONITOREXIT : tableIdx
        return;
    }

    private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        if (this.q050.allocate(buf, (int)reqCapacity, (int)normCapacity)) return;
        if (this.q025.allocate(buf, (int)reqCapacity, (int)normCapacity)) return;
        if (this.q000.allocate(buf, (int)reqCapacity, (int)normCapacity)) return;
        if (this.qInit.allocate(buf, (int)reqCapacity, (int)normCapacity)) return;
        if (this.q075.allocate(buf, (int)reqCapacity, (int)normCapacity)) {
            return;
        }
        PoolChunk<T> c = this.newChunk((int)this.pageSize, (int)this.maxOrder, (int)this.pageShifts, (int)this.chunkSize);
        boolean success = c.allocate(buf, (int)reqCapacity, (int)normCapacity);
        assert (success);
        this.qInit.add(c);
    }

    private void incTinySmallAllocation(boolean tiny) {
        if (tiny) {
            this.allocationsTiny.increment();
            return;
        }
        this.allocationsSmall.increment();
    }

    private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
        PoolChunk<T> chunk = this.newUnpooledChunk((int)reqCapacity);
        this.activeBytesHuge.add((long)((long)chunk.chunkSize()));
        buf.initUnpooled(chunk, (int)reqCapacity);
        this.allocationsHuge.increment();
    }

    void free(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolThreadCache cache) {
        if (chunk.unpooled) {
            int size = chunk.chunkSize();
            this.destroyChunk(chunk);
            this.activeBytesHuge.add((long)((long)(-size)));
            this.deallocationsHuge.increment();
            return;
        }
        SizeClass sizeClass = this.sizeClass((int)normCapacity);
        if (cache != null && cache.add(this, chunk, (ByteBuffer)nioBuffer, (long)handle, (int)normCapacity, (SizeClass)sizeClass)) {
            return;
        }
        this.freeChunk(chunk, (long)handle, (SizeClass)sizeClass, (ByteBuffer)nioBuffer, (boolean)false);
    }

    private SizeClass sizeClass(int normCapacity) {
        SizeClass sizeClass;
        if (!this.isTinyOrSmall((int)normCapacity)) {
            return SizeClass.Normal;
        }
        if (PoolArena.isTiny((int)normCapacity)) {
            sizeClass = SizeClass.Tiny;
            return sizeClass;
        }
        sizeClass = SizeClass.Small;
        return sizeClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    void freeChunk(PoolChunk<T> chunk, long handle, SizeClass sizeClass, ByteBuffer nioBuffer, boolean finalizer) {
        var8_6 = this;
        // MONITORENTER : var8_6
        if (!finalizer) {
            switch (1.$SwitchMap$io$netty$buffer$PoolArena$SizeClass[sizeClass.ordinal()]) {
                case 1: {
                    ++this.deallocationsNormal;
                    ** break;
                }
                case 2: {
                    ++this.deallocationsSmall;
                    ** break;
                }
                case 3: {
                    ++this.deallocationsTiny;
                    ** break;
                }
            }
            throw new Error();
        }
lbl15: // 5 sources:
        destroyChunk = chunk.parent.free(chunk, (long)handle, (ByteBuffer)nioBuffer) == false;
        // MONITOREXIT : var8_6
        if (destroyChunk == false) return;
        this.destroyChunk(chunk);
    }

    PoolSubpage<T> findSubpagePoolHead(int elemSize) {
        PoolSubpage<T>[] table;
        int tableIdx;
        if (PoolArena.isTiny((int)elemSize)) {
            tableIdx = elemSize >>> 4;
            table = this.tinySubpagePools;
            return table[tableIdx];
        }
        tableIdx = 0;
        elemSize >>>= 10;
        do {
            if (elemSize == 0) {
                table = this.smallSubpagePools;
                return table[tableIdx];
            }
            elemSize >>>= 1;
            ++tableIdx;
        } while (true);
    }

    int normalizeCapacity(int reqCapacity) {
        ObjectUtil.checkPositiveOrZero((int)reqCapacity, (String)"reqCapacity");
        if (reqCapacity >= this.chunkSize) {
            int n;
            if (this.directMemoryCacheAlignment == 0) {
                n = reqCapacity;
                return n;
            }
            n = this.alignCapacity((int)reqCapacity);
            return n;
        }
        if (!PoolArena.isTiny((int)reqCapacity)) {
            int normalizedCapacity = reqCapacity;
            --normalizedCapacity;
            normalizedCapacity |= normalizedCapacity >>> 1;
            normalizedCapacity |= normalizedCapacity >>> 2;
            normalizedCapacity |= normalizedCapacity >>> 4;
            normalizedCapacity |= normalizedCapacity >>> 8;
            normalizedCapacity |= normalizedCapacity >>> 16;
            if (++normalizedCapacity < 0) {
                normalizedCapacity >>>= 1;
            }
            if ($assertionsDisabled) return normalizedCapacity;
            if (this.directMemoryCacheAlignment == 0) return normalizedCapacity;
            if ((normalizedCapacity & this.directMemoryCacheAlignmentMask) == 0) return normalizedCapacity;
            throw new AssertionError();
        }
        if (this.directMemoryCacheAlignment > 0) {
            return this.alignCapacity((int)reqCapacity);
        }
        if ((reqCapacity & 15) != 0) return (reqCapacity & -16) + 16;
        return reqCapacity;
    }

    int alignCapacity(int reqCapacity) {
        int n;
        int delta = reqCapacity & this.directMemoryCacheAlignmentMask;
        if (delta == 0) {
            n = reqCapacity;
            return n;
        }
        n = reqCapacity + this.directMemoryCacheAlignment - delta;
        return n;
    }

    void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
        int bytesToCopy;
        int oldCapacity;
        if (!$assertionsDisabled) {
            if (newCapacity < 0) throw new AssertionError();
            if (newCapacity > buf.maxCapacity()) {
                throw new AssertionError();
            }
        }
        if ((oldCapacity = buf.length) == newCapacity) {
            return;
        }
        PoolChunk<T> oldChunk = buf.chunk;
        ByteBuffer oldNioBuffer = buf.tmpNioBuf;
        long oldHandle = buf.handle;
        T oldMemory = buf.memory;
        int oldOffset = buf.offset;
        int oldMaxLength = buf.maxLength;
        this.allocate((PoolThreadCache)this.parent.threadCache(), buf, (int)newCapacity);
        if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
        } else {
            buf.trimIndicesToCapacity((int)newCapacity);
            bytesToCopy = newCapacity;
        }
        this.memoryCopy(oldMemory, (int)oldOffset, buf.memory, (int)buf.offset, (int)bytesToCopy);
        if (!freeOldMemory) return;
        this.free(oldChunk, (ByteBuffer)oldNioBuffer, (long)oldHandle, (int)oldMaxLength, (PoolThreadCache)buf.cache);
    }

    @Override
    public int numThreadCaches() {
        return this.numThreadCaches.get();
    }

    @Override
    public int numTinySubpages() {
        return this.tinySubpagePools.length;
    }

    @Override
    public int numSmallSubpages() {
        return this.smallSubpagePools.length;
    }

    @Override
    public int numChunkLists() {
        return this.chunkListMetrics.size();
    }

    @Override
    public List<PoolSubpageMetric> tinySubpages() {
        return PoolArena.subPageMetricList(this.tinySubpagePools);
    }

    @Override
    public List<PoolSubpageMetric> smallSubpages() {
        return PoolArena.subPageMetricList(this.smallSubpagePools);
    }

    @Override
    public List<PoolChunkListMetric> chunkLists() {
        return this.chunkListMetrics;
    }

    private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
        ArrayList<PoolSubpageMetric> metrics = new ArrayList<PoolSubpageMetric>();
        PoolSubpage<?>[] arrpoolSubpage = pages;
        int n = arrpoolSubpage.length;
        int n2 = 0;
        while (n2 < n) {
            PoolSubpage<?> head = arrpoolSubpage[n2];
            if (head.next != head) {
                PoolSubpage<T> s = head.next;
                do {
                    metrics.add(s);
                } while ((s = s.next) != head);
            }
            ++n2;
        }
        return metrics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numAllocations() {
        PoolArena poolArena = this;
        // MONITORENTER : poolArena
        long allocsNormal = this.allocationsNormal;
        // MONITOREXIT : poolArena
        return this.allocationsTiny.value() + this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
    }

    @Override
    public long numTinyAllocations() {
        return this.allocationsTiny.value();
    }

    @Override
    public long numSmallAllocations() {
        return this.allocationsSmall.value();
    }

    @Override
    public synchronized long numNormalAllocations() {
        return this.allocationsNormal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numDeallocations() {
        PoolArena poolArena = this;
        // MONITORENTER : poolArena
        long deallocs = this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal;
        // MONITOREXIT : poolArena
        return deallocs + this.deallocationsHuge.value();
    }

    @Override
    public synchronized long numTinyDeallocations() {
        return this.deallocationsTiny;
    }

    @Override
    public synchronized long numSmallDeallocations() {
        return this.deallocationsSmall;
    }

    @Override
    public synchronized long numNormalDeallocations() {
        return this.deallocationsNormal;
    }

    @Override
    public long numHugeAllocations() {
        return this.allocationsHuge.value();
    }

    @Override
    public long numHugeDeallocations() {
        return this.deallocationsHuge.value();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveAllocations() {
        long val = this.allocationsTiny.value() + this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
        PoolArena poolArena = this;
        // MONITORENTER : poolArena
        // MONITOREXIT : poolArena
        return Math.max((long)(val += this.allocationsNormal - (this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal)), (long)0L);
    }

    @Override
    public long numActiveTinyAllocations() {
        return Math.max((long)(this.numTinyAllocations() - this.numTinyDeallocations()), (long)0L);
    }

    @Override
    public long numActiveSmallAllocations() {
        return Math.max((long)(this.numSmallAllocations() - this.numSmallDeallocations()), (long)0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveNormalAllocations() {
        PoolArena poolArena = this;
        // MONITORENTER : poolArena
        long val = this.allocationsNormal - this.deallocationsNormal;
        // MONITOREXIT : poolArena
        return Math.max((long)val, (long)0L);
    }

    @Override
    public long numActiveHugeAllocations() {
        return Math.max((long)(this.numHugeAllocations() - this.numHugeDeallocations()), (long)0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveBytes() {
        long val = this.activeBytesHuge.value();
        PoolArena poolArena = this;
        // MONITORENTER : poolArena
        int i = 0;
        do {
            if (i >= this.chunkListMetrics.size()) {
                // MONITOREXIT : poolArena
                return Math.max((long)0L, (long)val);
            }
            for (PoolChunkMetric m : this.chunkListMetrics.get((int)i)) {
                val += (long)m.chunkSize();
            }
            ++i;
        } while (true);
    }

    protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

    protected abstract PoolChunk<T> newUnpooledChunk(int var1);

    protected abstract PooledByteBuf<T> newByteBuf(int var1);

    protected abstract void memoryCopy(T var1, int var2, T var3, int var4, int var5);

    protected abstract void destroyChunk(PoolChunk<T> var1);

    public synchronized String toString() {
        StringBuilder buf = new StringBuilder().append((String)"Chunk(s) at 0~25%:").append((String)StringUtil.NEWLINE).append(this.qInit).append((String)StringUtil.NEWLINE).append((String)"Chunk(s) at 0~50%:").append((String)StringUtil.NEWLINE).append(this.q000).append((String)StringUtil.NEWLINE).append((String)"Chunk(s) at 25~75%:").append((String)StringUtil.NEWLINE).append(this.q025).append((String)StringUtil.NEWLINE).append((String)"Chunk(s) at 50~100%:").append((String)StringUtil.NEWLINE).append(this.q050).append((String)StringUtil.NEWLINE).append((String)"Chunk(s) at 75~100%:").append((String)StringUtil.NEWLINE).append(this.q075).append((String)StringUtil.NEWLINE).append((String)"Chunk(s) at 100%:").append((String)StringUtil.NEWLINE).append(this.q100).append((String)StringUtil.NEWLINE).append((String)"tiny subpages:");
        PoolArena.appendPoolSubPages((StringBuilder)buf, this.tinySubpagePools);
        buf.append((String)StringUtil.NEWLINE).append((String)"small subpages:");
        PoolArena.appendPoolSubPages((StringBuilder)buf, this.smallSubpagePools);
        buf.append((String)StringUtil.NEWLINE);
        return buf.toString();
    }

    private static void appendPoolSubPages(StringBuilder buf, PoolSubpage<?>[] subpages) {
        int i = 0;
        while (i < subpages.length) {
            PoolSubpage<?> head = subpages[i];
            if (head.next != head) {
                buf.append((String)StringUtil.NEWLINE).append((int)i).append((String)": ");
                PoolSubpage<T> s = head.next;
                do {
                    buf.append(s);
                } while ((s = s.next) != head);
            }
            ++i;
        }
    }

    protected final void finalize() throws Throwable {
        try {
            super.finalize();
        }
        catch (Throwable throwable) {
            PoolArena.destroyPoolSubPages(this.smallSubpagePools);
            PoolArena.destroyPoolSubPages(this.tinySubpagePools);
            this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
            throw throwable;
        }
        PoolArena.destroyPoolSubPages(this.smallSubpagePools);
        PoolArena.destroyPoolSubPages(this.tinySubpagePools);
        this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
    }

    private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
        PoolSubpage<?>[] arrpoolSubpage = pages;
        int n = arrpoolSubpage.length;
        int n2 = 0;
        while (n2 < n) {
            PoolSubpage<?> page = arrpoolSubpage[n2];
            page.destroy();
            ++n2;
        }
    }

    private void destroyPoolChunkLists(PoolChunkList<T> ... chunkLists) {
        PoolChunkList<T>[] arrpoolChunkList = chunkLists;
        int n = arrpoolChunkList.length;
        int n2 = 0;
        while (n2 < n) {
            PoolChunkList<T> chunkList = arrpoolChunkList[n2];
            chunkList.destroy(this);
            ++n2;
        }
    }
}

