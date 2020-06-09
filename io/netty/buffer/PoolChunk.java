/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

final class PoolChunk<T>
implements PoolChunkMetric {
    private static final int INTEGER_SIZE_MINUS_ONE = 31;
    final PoolArena<T> arena;
    final T memory;
    final boolean unpooled;
    final int offset;
    private final byte[] memoryMap;
    private final byte[] depthMap;
    private final PoolSubpage<T>[] subpages;
    private final int subpageOverflowMask;
    private final int pageSize;
    private final int pageShifts;
    private final int maxOrder;
    private final int chunkSize;
    private final int log2ChunkSize;
    private final int maxSubpageAllocs;
    private final byte unusable;
    private final Deque<ByteBuffer> cachedNioBuffers;
    private int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;

    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize, int offset) {
        this.unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        this.offset = offset;
        this.unusable = (byte)(maxOrder + 1);
        this.log2ChunkSize = PoolChunk.log2((int)chunkSize);
        this.subpageOverflowMask = ~(pageSize - 1);
        this.freeBytes = chunkSize;
        assert (maxOrder < 30) : "maxOrder should be < 30, but is: " + maxOrder;
        this.maxSubpageAllocs = 1 << maxOrder;
        this.memoryMap = new byte[this.maxSubpageAllocs << 1];
        this.depthMap = new byte[this.memoryMap.length];
        int memoryMapIndex = 1;
        int d = 0;
        do {
            if (d > maxOrder) {
                this.subpages = this.newSubpageArray((int)this.maxSubpageAllocs);
                this.cachedNioBuffers = new ArrayDeque<ByteBuffer>((int)8);
                return;
            }
            int depth = 1 << d;
            for (int p = 0; p < depth; ++memoryMapIndex, ++p) {
                this.memoryMap[memoryMapIndex] = (byte)d;
                this.depthMap[memoryMapIndex] = (byte)d;
            }
            ++d;
        } while (true);
    }

    PoolChunk(PoolArena<T> arena, T memory, int size, int offset) {
        this.unpooled = true;
        this.arena = arena;
        this.memory = memory;
        this.offset = offset;
        this.memoryMap = null;
        this.depthMap = null;
        this.subpages = null;
        this.subpageOverflowMask = 0;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.maxOrder = 0;
        this.unusable = (byte)(this.maxOrder + 1);
        this.chunkSize = size;
        this.log2ChunkSize = PoolChunk.log2((int)this.chunkSize);
        this.maxSubpageAllocs = 0;
        this.cachedNioBuffers = null;
    }

    private PoolSubpage<T>[] newSubpageArray(int size) {
        return new PoolSubpage[size];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int usage() {
        PoolArena<T> poolArena = this.arena;
        // MONITORENTER : poolArena
        int freeBytes = this.freeBytes;
        // MONITOREXIT : poolArena
        return this.usage((int)freeBytes);
    }

    private int usage(int freeBytes) {
        if (freeBytes == 0) {
            return 100;
        }
        int freePercentage = (int)((long)freeBytes * 100L / (long)this.chunkSize);
        if (freePercentage != 0) return 100 - freePercentage;
        return 99;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        long handle = (normCapacity & this.subpageOverflowMask) != 0 ? this.allocateRun((int)normCapacity) : this.allocateSubpage((int)normCapacity);
        if (handle < 0L) {
            return false;
        }
        ByteBuffer nioBuffer = this.cachedNioBuffers != null ? this.cachedNioBuffers.pollLast() : null;
        this.initBuf(buf, (ByteBuffer)nioBuffer, (long)handle, (int)reqCapacity);
        return true;
    }

    private void updateParentsAlloc(int id) {
        while (id > 1) {
            byte val2;
            int parentId = id >>> 1;
            byte val1 = this.value((int)id);
            byte val = val1 < (val2 = this.value((int)(id ^ 1))) ? val1 : val2;
            this.setValue((int)parentId, (byte)val);
            id = parentId;
        }
    }

    private void updateParentsFree(int id) {
        int logChild = this.depth((int)id) + 1;
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = this.value((int)id);
            byte val2 = this.value((int)(id ^ 1));
            if (val1 == --logChild && val2 == logChild) {
                this.setValue((int)parentId, (byte)((byte)(logChild - 1)));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                this.setValue((int)parentId, (byte)val);
            }
            id = parentId;
        }
    }

    private int allocateNode(int d) {
        int id = 1;
        int initial = -(1 << d);
        byte val = this.value((int)id);
        if (val > d) {
            return -1;
        }
        while (val < d || (id & initial) == 0) {
            val = this.value((int)(id <<= 1));
            if (val <= d) continue;
            val = this.value((int)(id ^= 1));
        }
        byte value = this.value((int)id);
        assert (value == d && (id & initial) == 1 << d) : String.format((String)"val = %d, id & initial = %d, d = %d", (Object[])new Object[]{java.lang.Byte.valueOf((byte)value), Integer.valueOf((int)(id & initial)), Integer.valueOf((int)d)});
        this.setValue((int)id, (byte)this.unusable);
        this.updateParentsAlloc((int)id);
        return id;
    }

    private long allocateRun(int normCapacity) {
        int d = this.maxOrder - (PoolChunk.log2((int)normCapacity) - this.pageShifts);
        int id = this.allocateNode((int)d);
        if (id < 0) {
            return (long)id;
        }
        this.freeBytes -= this.runLength((int)id);
        return (long)id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long allocateSubpage(int normCapacity) {
        PoolSubpage<T> head = this.arena.findSubpagePoolHead((int)normCapacity);
        int d = this.maxOrder;
        PoolSubpage<T> poolSubpage = head;
        // MONITORENTER : poolSubpage
        int id = this.allocateNode((int)d);
        if (id < 0) {
            // MONITOREXIT : poolSubpage
            return (long)id;
        }
        PoolSubpage<T>[] subpages = this.subpages;
        int pageSize = this.pageSize;
        this.freeBytes -= pageSize;
        int subpageIdx = this.subpageIdx((int)id);
        PoolSubpage<T> subpage = subpages[subpageIdx];
        if (subpage == null) {
            subpage = new PoolSubpage<T>(head, this, (int)id, (int)this.runOffset((int)id), (int)pageSize, (int)normCapacity);
            subpages[subpageIdx] = subpage;
            return subpage.allocate();
        }
        subpage.init(head, (int)normCapacity);
        // MONITOREXIT : poolSubpage
        return subpage.allocate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void free(long handle, ByteBuffer nioBuffer) {
        int memoryMapIdx = PoolChunk.memoryMapIdx((long)handle);
        int bitmapIdx = PoolChunk.bitmapIdx((long)handle);
        if (bitmapIdx != 0) {
            PoolSubpage<T> head;
            PoolSubpage<T> subpage = this.subpages[this.subpageIdx((int)memoryMapIdx)];
            if (!$assertionsDisabled) {
                if (subpage == null) throw new AssertionError();
                if (!subpage.doNotDestroy) {
                    throw new AssertionError();
                }
            }
            PoolSubpage<T> poolSubpage = head = this.arena.findSubpagePoolHead((int)subpage.elemSize);
            // MONITORENTER : poolSubpage
            if (subpage.free(head, (int)(bitmapIdx & 1073741823))) {
                // MONITOREXIT : poolSubpage
                return;
            }
            // MONITOREXIT : poolSubpage
        }
        this.freeBytes += this.runLength((int)memoryMapIdx);
        this.setValue((int)memoryMapIdx, (byte)this.depth((int)memoryMapIdx));
        this.updateParentsFree((int)memoryMapIdx);
        if (nioBuffer == null) return;
        if (this.cachedNioBuffers == null) return;
        if (this.cachedNioBuffers.size() >= PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) return;
        this.cachedNioBuffers.offer((ByteBuffer)nioBuffer);
    }

    void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
        int memoryMapIdx = PoolChunk.memoryMapIdx((long)handle);
        int bitmapIdx = PoolChunk.bitmapIdx((long)handle);
        if (bitmapIdx != 0) {
            this.initBufWithSubpage(buf, (ByteBuffer)nioBuffer, (long)handle, (int)bitmapIdx, (int)reqCapacity);
            return;
        }
        byte val = this.value((int)memoryMapIdx);
        assert (val == this.unusable) : String.valueOf((int)val);
        buf.init(this, (ByteBuffer)nioBuffer, (long)handle, (int)(this.runOffset((int)memoryMapIdx) + this.offset), (int)reqCapacity, (int)this.runLength((int)memoryMapIdx), (PoolThreadCache)this.arena.parent.threadCache());
    }

    void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
        this.initBufWithSubpage(buf, (ByteBuffer)nioBuffer, (long)handle, (int)PoolChunk.bitmapIdx((long)handle), (int)reqCapacity);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int bitmapIdx, int reqCapacity) {
        assert (bitmapIdx != 0);
        int memoryMapIdx = PoolChunk.memoryMapIdx((long)handle);
        PoolSubpage<T> subpage = this.subpages[this.subpageIdx((int)memoryMapIdx)];
        assert (subpage.doNotDestroy);
        assert (reqCapacity <= subpage.elemSize);
        buf.init(this, (ByteBuffer)nioBuffer, (long)handle, (int)(this.runOffset((int)memoryMapIdx) + (bitmapIdx & 1073741823) * subpage.elemSize + this.offset), (int)reqCapacity, (int)subpage.elemSize, (PoolThreadCache)this.arena.parent.threadCache());
    }

    private byte value(int id) {
        return this.memoryMap[id];
    }

    private void setValue(int id, byte val) {
        this.memoryMap[id] = val;
    }

    private byte depth(int id) {
        return this.depthMap[id];
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros((int)val);
    }

    private int runLength(int id) {
        return 1 << this.log2ChunkSize - this.depth((int)id);
    }

    private int runOffset(int id) {
        int shift = id ^ 1 << this.depth((int)id);
        return shift * this.runLength((int)id);
    }

    private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ this.maxSubpageAllocs;
    }

    private static int memoryMapIdx(long handle) {
        return (int)handle;
    }

    private static int bitmapIdx(long handle) {
        return (int)(handle >>> 32);
    }

    @Override
    public int chunkSize() {
        return this.chunkSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int freeBytes() {
        PoolArena<T> poolArena = this.arena;
        // MONITORENTER : poolArena
        // MONITOREXIT : poolArena
        return this.freeBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        PoolArena<T> poolArena = this.arena;
        // MONITORENTER : poolArena
        int freeBytes = this.freeBytes;
        // MONITOREXIT : poolArena
        return "Chunk(" + Integer.toHexString((int)System.identityHashCode((Object)this)) + ": " + this.usage((int)freeBytes) + "%, " + (this.chunkSize - freeBytes) + '/' + this.chunkSize + ')';
    }

    void destroy() {
        this.arena.destroyChunk(this);
    }
}

