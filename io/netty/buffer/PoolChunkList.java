/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class PoolChunkList<T>
implements PoolChunkListMetric {
    private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.emptyList().iterator();
    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;
    private PoolChunk<T> head;
    private PoolChunkList<T> prevList;

    PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
        assert (minUsage <= maxUsage);
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        this.maxCapacity = PoolChunkList.calculateMaxCapacity((int)minUsage, (int)chunkSize);
    }

    private static int calculateMaxCapacity(int minUsage, int chunkSize) {
        if ((minUsage = PoolChunkList.minUsage0((int)minUsage)) != 100) return (int)((long)chunkSize * (100L - (long)minUsage) / 100L);
        return 0;
    }

    void prevList(PoolChunkList<T> prevList) {
        assert (this.prevList == null);
        this.prevList = prevList;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        if (normCapacity > this.maxCapacity) {
            return false;
        }
        PoolChunk<T> cur = this.head;
        while (cur != null) {
            if (cur.allocate(buf, (int)reqCapacity, (int)normCapacity)) {
                if (cur.usage() < this.maxUsage) return true;
                this.remove(cur);
                this.nextList.add(cur);
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    boolean free(PoolChunk<T> chunk, long handle, ByteBuffer nioBuffer) {
        chunk.free((long)handle, (ByteBuffer)nioBuffer);
        if (chunk.usage() >= this.minUsage) return true;
        this.remove(chunk);
        return this.move0(chunk);
    }

    private boolean move(PoolChunk<T> chunk) {
        assert (chunk.usage() < this.maxUsage);
        if (chunk.usage() < this.minUsage) {
            return this.move0(chunk);
        }
        this.add0(chunk);
        return true;
    }

    private boolean move0(PoolChunk<T> chunk) {
        if (this.prevList != null) return PoolChunkList.super.move(chunk);
        if ($assertionsDisabled) return false;
        if (chunk.usage() == 0) return false;
        throw new AssertionError();
    }

    void add(PoolChunk<T> chunk) {
        if (chunk.usage() >= this.maxUsage) {
            this.nextList.add(chunk);
            return;
        }
        this.add0(chunk);
    }

    void add0(PoolChunk<T> chunk) {
        chunk.parent = this;
        if (this.head == null) {
            this.head = chunk;
            chunk.prev = null;
            chunk.next = null;
            return;
        }
        chunk.prev = null;
        chunk.next = this.head;
        this.head.prev = chunk;
        this.head = chunk;
    }

    private void remove(PoolChunk<T> cur) {
        PoolChunk<T> next;
        if (cur == this.head) {
            this.head = cur.next;
            if (this.head == null) return;
            this.head.prev = null;
            return;
        }
        cur.prev.next = next = cur.next;
        if (next == null) return;
        next.prev = cur.prev;
    }

    @Override
    public int minUsage() {
        return PoolChunkList.minUsage0((int)this.minUsage);
    }

    @Override
    public int maxUsage() {
        return Math.min((int)this.maxUsage, (int)100);
    }

    private static int minUsage0(int value) {
        return Math.max((int)1, (int)value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<PoolChunkMetric> iterator() {
        PoolArena<T> poolArena = this.arena;
        // MONITORENTER : poolArena
        if (this.head == null) {
            // MONITOREXIT : poolArena
            return EMPTY_METRICS;
        }
        ArrayList<PoolChunk<T>> metrics = new ArrayList<PoolChunk<T>>();
        PoolChunk<T> cur = this.head;
        do {
            metrics.add(cur);
        } while ((cur = cur.next) != null);
        // MONITOREXIT : poolArena
        return metrics.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        PoolArena<T> poolArena = this.arena;
        // MONITORENTER : poolArena
        if (this.head == null) {
            // MONITOREXIT : poolArena
            return "none";
        }
        PoolChunk<T> cur = this.head;
        do {
            buf.append(cur);
            cur = cur.next;
            if (cur == null) {
                return buf.toString();
            }
            buf.append((String)StringUtil.NEWLINE);
        } while (true);
    }

    void destroy(PoolArena<T> arena) {
        PoolChunk<T> chunk = this.head;
        do {
            if (chunk == null) {
                this.head = null;
                return;
            }
            arena.destroyChunk(chunk);
            chunk = chunk.next;
        } while (true);
    }
}

