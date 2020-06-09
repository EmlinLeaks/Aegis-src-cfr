/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AdvancedLeakAwareByteBuf;
import io.netty.buffer.AdvancedLeakAwareCompositeByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.buffer.SimpleLeakAwareCompositeByteBuf;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

public abstract class AbstractByteBufAllocator
implements ByteBufAllocator {
    static final int DEFAULT_INITIAL_CAPACITY = 256;
    static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;
    static final int DEFAULT_MAX_COMPONENTS = 16;
    static final int CALCULATE_THRESHOLD = 4194304;
    private final boolean directByDefault;
    private final ByteBuf emptyBuf;

    protected static ByteBuf toLeakAwareBuffer(ByteBuf buf) {
        switch (1.$SwitchMap$io$netty$util$ResourceLeakDetector$Level[ResourceLeakDetector.getLevel().ordinal()]) {
            case 1: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track((ByteBuf)buf);
                if (leak == null) return buf;
                return new SimpleLeakAwareByteBuf((ByteBuf)buf, leak);
            }
            case 2: 
            case 3: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track((ByteBuf)buf);
                if (leak == null) return buf;
                buf = new AdvancedLeakAwareByteBuf((ByteBuf)buf, leak);
                break;
            }
        }
        return buf;
    }

    protected static CompositeByteBuf toLeakAwareBuffer(CompositeByteBuf buf) {
        switch (1.$SwitchMap$io$netty$util$ResourceLeakDetector$Level[ResourceLeakDetector.getLevel().ordinal()]) {
            case 1: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track((ByteBuf)buf);
                if (leak == null) return buf;
                return new SimpleLeakAwareCompositeByteBuf((CompositeByteBuf)buf, leak);
            }
            case 2: 
            case 3: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track((ByteBuf)buf);
                if (leak == null) return buf;
                buf = new AdvancedLeakAwareCompositeByteBuf((CompositeByteBuf)buf, leak);
                break;
            }
        }
        return buf;
    }

    protected AbstractByteBufAllocator() {
        this((boolean)false);
    }

    protected AbstractByteBufAllocator(boolean preferDirect) {
        this.directByDefault = preferDirect && PlatformDependent.hasUnsafe();
        this.emptyBuf = new EmptyByteBuf((ByteBufAllocator)this);
    }

    @Override
    public ByteBuf buffer() {
        if (!this.directByDefault) return this.heapBuffer();
        return this.directBuffer();
    }

    @Override
    public ByteBuf buffer(int initialCapacity) {
        if (!this.directByDefault) return this.heapBuffer((int)initialCapacity);
        return this.directBuffer((int)initialCapacity);
    }

    @Override
    public ByteBuf buffer(int initialCapacity, int maxCapacity) {
        if (!this.directByDefault) return this.heapBuffer((int)initialCapacity, (int)maxCapacity);
        return this.directBuffer((int)initialCapacity, (int)maxCapacity);
    }

    @Override
    public ByteBuf ioBuffer() {
        if (PlatformDependent.hasUnsafe()) return this.directBuffer((int)256);
        if (!this.isDirectBufferPooled()) return this.heapBuffer((int)256);
        return this.directBuffer((int)256);
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity) {
        if (PlatformDependent.hasUnsafe()) return this.directBuffer((int)initialCapacity);
        if (!this.isDirectBufferPooled()) return this.heapBuffer((int)initialCapacity);
        return this.directBuffer((int)initialCapacity);
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
        if (PlatformDependent.hasUnsafe()) return this.directBuffer((int)initialCapacity, (int)maxCapacity);
        if (!this.isDirectBufferPooled()) return this.heapBuffer((int)initialCapacity, (int)maxCapacity);
        return this.directBuffer((int)initialCapacity, (int)maxCapacity);
    }

    @Override
    public ByteBuf heapBuffer() {
        return this.heapBuffer((int)256, (int)Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        return this.heapBuffer((int)initialCapacity, (int)Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return this.emptyBuf;
        }
        AbstractByteBufAllocator.validate((int)initialCapacity, (int)maxCapacity);
        return this.newHeapBuffer((int)initialCapacity, (int)maxCapacity);
    }

    @Override
    public ByteBuf directBuffer() {
        return this.directBuffer((int)256, (int)Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity) {
        return this.directBuffer((int)initialCapacity, (int)Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return this.emptyBuf;
        }
        AbstractByteBufAllocator.validate((int)initialCapacity, (int)maxCapacity);
        return this.newDirectBuffer((int)initialCapacity, (int)maxCapacity);
    }

    @Override
    public CompositeByteBuf compositeBuffer() {
        if (!this.directByDefault) return this.compositeHeapBuffer();
        return this.compositeDirectBuffer();
    }

    @Override
    public CompositeByteBuf compositeBuffer(int maxNumComponents) {
        if (!this.directByDefault) return this.compositeHeapBuffer((int)maxNumComponents);
        return this.compositeDirectBuffer((int)maxNumComponents);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        return this.compositeHeapBuffer((int)16);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        return AbstractByteBufAllocator.toLeakAwareBuffer((CompositeByteBuf)new CompositeByteBuf((ByteBufAllocator)this, (boolean)false, (int)maxNumComponents));
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer() {
        return this.compositeDirectBuffer((int)16);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
        return AbstractByteBufAllocator.toLeakAwareBuffer((CompositeByteBuf)new CompositeByteBuf((ByteBufAllocator)this, (boolean)true, (int)maxNumComponents));
    }

    private static void validate(int initialCapacity, int maxCapacity) {
        ObjectUtil.checkPositiveOrZero((int)initialCapacity, (String)"initialCapacity");
        if (initialCapacity <= maxCapacity) return;
        throw new IllegalArgumentException((String)String.format((String)"initialCapacity: %d (expected: not greater than maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)initialCapacity), Integer.valueOf((int)maxCapacity)}));
    }

    protected abstract ByteBuf newHeapBuffer(int var1, int var2);

    protected abstract ByteBuf newDirectBuffer(int var1, int var2);

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + "(directByDefault: " + this.directByDefault + ')';
    }

    @Override
    public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
        ObjectUtil.checkPositiveOrZero((int)minNewCapacity, (String)"minNewCapacity");
        if (minNewCapacity > maxCapacity) {
            throw new IllegalArgumentException((String)String.format((String)"minNewCapacity: %d (expected: not greater than maxCapacity(%d)", (Object[])new Object[]{Integer.valueOf((int)minNewCapacity), Integer.valueOf((int)maxCapacity)}));
        }
        int threshold = 4194304;
        if (minNewCapacity == 4194304) {
            return 4194304;
        }
        if (minNewCapacity > 4194304) {
            int newCapacity = minNewCapacity / 4194304 * 4194304;
            if (newCapacity > maxCapacity - 4194304) {
                return maxCapacity;
            }
            newCapacity += 4194304;
            return newCapacity;
        }
        int newCapacity = 64;
        while (newCapacity < minNewCapacity) {
            newCapacity <<= 1;
        }
        return Math.min((int)newCapacity, (int)maxCapacity);
    }

    static {
        ResourceLeakDetector.addExclusions(AbstractByteBufAllocator.class, (String[])new String[]{"toLeakAwareBuffer"});
    }
}

