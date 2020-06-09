/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;

public final class UnpooledByteBufAllocator
extends AbstractByteBufAllocator
implements ByteBufAllocatorMetricProvider {
    private final UnpooledByteBufAllocatorMetric metric = new UnpooledByteBufAllocatorMetric(null);
    private final boolean disableLeakDetector;
    private final boolean noCleaner;
    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator((boolean)PlatformDependent.directBufferPreferred());

    public UnpooledByteBufAllocator(boolean preferDirect) {
        this((boolean)preferDirect, (boolean)false);
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector) {
        this((boolean)preferDirect, (boolean)disableLeakDetector, (boolean)PlatformDependent.useDirectBufferNoCleaner());
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector, boolean tryNoCleaner) {
        super((boolean)preferDirect);
        this.disableLeakDetector = disableLeakDetector;
        this.noCleaner = tryNoCleaner && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor();
    }

    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        UnpooledHeapByteBuf unpooledHeapByteBuf;
        if (PlatformDependent.hasUnsafe()) {
            unpooledHeapByteBuf = new InstrumentedUnpooledUnsafeHeapByteBuf((UnpooledByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity);
            return unpooledHeapByteBuf;
        }
        unpooledHeapByteBuf = new InstrumentedUnpooledHeapByteBuf((UnpooledByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity);
        return unpooledHeapByteBuf;
    }

    @Override
    protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
        ByteBuf byteBuf;
        UnpooledDirectByteBuf buf = PlatformDependent.hasUnsafe() ? (this.noCleaner ? new InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf((UnpooledByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity) : new InstrumentedUnpooledUnsafeDirectByteBuf((UnpooledByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity)) : new InstrumentedUnpooledDirectByteBuf((UnpooledByteBufAllocator)this, (int)initialCapacity, (int)maxCapacity);
        if (this.disableLeakDetector) {
            byteBuf = buf;
            return byteBuf;
        }
        byteBuf = UnpooledByteBufAllocator.toLeakAwareBuffer((ByteBuf)buf);
        return byteBuf;
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        CompositeByteBuf compositeByteBuf;
        CompositeByteBuf buf = new CompositeByteBuf((ByteBufAllocator)this, (boolean)false, (int)maxNumComponents);
        if (this.disableLeakDetector) {
            compositeByteBuf = buf;
            return compositeByteBuf;
        }
        compositeByteBuf = UnpooledByteBufAllocator.toLeakAwareBuffer((CompositeByteBuf)buf);
        return compositeByteBuf;
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
        CompositeByteBuf compositeByteBuf;
        CompositeByteBuf buf = new CompositeByteBuf((ByteBufAllocator)this, (boolean)true, (int)maxNumComponents);
        if (this.disableLeakDetector) {
            compositeByteBuf = buf;
            return compositeByteBuf;
        }
        compositeByteBuf = UnpooledByteBufAllocator.toLeakAwareBuffer((CompositeByteBuf)buf);
        return compositeByteBuf;
    }

    @Override
    public boolean isDirectBufferPooled() {
        return false;
    }

    @Override
    public ByteBufAllocatorMetric metric() {
        return this.metric;
    }

    void incrementDirect(int amount) {
        this.metric.directCounter.add((long)((long)amount));
    }

    void decrementDirect(int amount) {
        this.metric.directCounter.add((long)((long)(-amount)));
    }

    void incrementHeap(int amount) {
        this.metric.heapCounter.add((long)((long)amount));
    }

    void decrementHeap(int amount) {
        this.metric.heapCounter.add((long)((long)(-amount)));
    }
}

