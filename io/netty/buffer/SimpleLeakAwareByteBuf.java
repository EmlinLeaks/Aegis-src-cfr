/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareByteBuf
extends WrappedByteBuf {
    private final ByteBuf trackedByteBuf;
    final ResourceLeakTracker<ByteBuf> leak;

    SimpleLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
        super((ByteBuf)wrapped);
        this.trackedByteBuf = ObjectUtil.checkNotNull(trackedByteBuf, (String)"trackedByteBuf");
        this.leak = ObjectUtil.checkNotNull(leak, (String)"leak");
    }

    SimpleLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
        this((ByteBuf)wrapped, (ByteBuf)wrapped, leak);
    }

    @Override
    public ByteBuf slice() {
        return this.newSharedLeakAwareByteBuf((ByteBuf)super.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.unwrappedDerived((ByteBuf)super.retainedSlice());
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.unwrappedDerived((ByteBuf)super.retainedSlice((int)index, (int)length));
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.unwrappedDerived((ByteBuf)super.retainedDuplicate());
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.unwrappedDerived((ByteBuf)super.readRetainedSlice((int)length));
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.newSharedLeakAwareByteBuf((ByteBuf)super.slice((int)index, (int)length));
    }

    @Override
    public ByteBuf duplicate() {
        return this.newSharedLeakAwareByteBuf((ByteBuf)super.duplicate());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.newSharedLeakAwareByteBuf((ByteBuf)super.readSlice((int)length));
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.newSharedLeakAwareByteBuf((ByteBuf)super.asReadOnly());
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        if (!super.release()) return false;
        this.closeLeak();
        return true;
    }

    @Override
    public boolean release(int decrement) {
        if (!super.release((int)decrement)) return false;
        this.closeLeak();
        return true;
    }

    private void closeLeak() {
        boolean closed = this.leak.close((ByteBuf)this.trackedByteBuf);
        if ($assertionsDisabled) return;
        if (closed) return;
        throw new AssertionError();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (this.order() != endianness) return this.newSharedLeakAwareByteBuf((ByteBuf)super.order((ByteOrder)endianness));
        return this;
    }

    private ByteBuf unwrappedDerived(ByteBuf derived) {
        ByteBuf unwrappedDerived = SimpleLeakAwareByteBuf.unwrapSwapped((ByteBuf)derived);
        if (!(unwrappedDerived instanceof AbstractPooledDerivedByteBuf)) return this.newSharedLeakAwareByteBuf((ByteBuf)derived);
        ((AbstractPooledDerivedByteBuf)unwrappedDerived).parent((ByteBuf)this);
        ResourceLeakTracker<ByteBuf> newLeak = AbstractByteBuf.leakDetector.track((ByteBuf)derived);
        if (newLeak != null) return this.newLeakAwareByteBuf((ByteBuf)derived, newLeak);
        return derived;
    }

    private static ByteBuf unwrapSwapped(ByteBuf buf) {
        if (!(buf instanceof SwappedByteBuf)) return buf;
        while ((buf = buf.unwrap()) instanceof SwappedByteBuf) {
        }
        return buf;
    }

    private SimpleLeakAwareByteBuf newSharedLeakAwareByteBuf(ByteBuf wrapped) {
        return this.newLeakAwareByteBuf((ByteBuf)wrapped, (ByteBuf)this.trackedByteBuf, this.leak);
    }

    private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leakTracker) {
        return this.newLeakAwareByteBuf((ByteBuf)wrapped, (ByteBuf)wrapped, leakTracker);
    }

    protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
        return new SimpleLeakAwareByteBuf((ByteBuf)buf, (ByteBuf)trackedByteBuf, leakTracker);
    }
}

