/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.buffer.WrappedCompositeByteBuf;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareCompositeByteBuf
extends WrappedCompositeByteBuf {
    final ResourceLeakTracker<ByteBuf> leak;

    SimpleLeakAwareCompositeByteBuf(CompositeByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
        super((CompositeByteBuf)wrapped);
        this.leak = ObjectUtil.checkNotNull(leak, (String)"leak");
    }

    @Override
    public boolean release() {
        ByteBuf unwrapped = this.unwrap();
        if (!super.release()) return false;
        this.closeLeak((ByteBuf)unwrapped);
        return true;
    }

    @Override
    public boolean release(int decrement) {
        ByteBuf unwrapped = this.unwrap();
        if (!super.release((int)decrement)) return false;
        this.closeLeak((ByteBuf)unwrapped);
        return true;
    }

    private void closeLeak(ByteBuf trackedByteBuf) {
        boolean closed = this.leak.close((ByteBuf)trackedByteBuf);
        if ($assertionsDisabled) return;
        if (closed) return;
        throw new AssertionError();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (this.order() != endianness) return this.newLeakAwareByteBuf((ByteBuf)super.order((ByteOrder)endianness));
        return this;
    }

    @Override
    public ByteBuf slice() {
        return this.newLeakAwareByteBuf((ByteBuf)super.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.newLeakAwareByteBuf((ByteBuf)super.retainedSlice());
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.newLeakAwareByteBuf((ByteBuf)super.slice((int)index, (int)length));
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.newLeakAwareByteBuf((ByteBuf)super.retainedSlice((int)index, (int)length));
    }

    @Override
    public ByteBuf duplicate() {
        return this.newLeakAwareByteBuf((ByteBuf)super.duplicate());
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.newLeakAwareByteBuf((ByteBuf)super.retainedDuplicate());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.newLeakAwareByteBuf((ByteBuf)super.readSlice((int)length));
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.newLeakAwareByteBuf((ByteBuf)super.readRetainedSlice((int)length));
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.newLeakAwareByteBuf((ByteBuf)super.asReadOnly());
    }

    private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped) {
        return this.newLeakAwareByteBuf((ByteBuf)wrapped, (ByteBuf)this.unwrap(), this.leak);
    }

    protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
        return new SimpleLeakAwareByteBuf((ByteBuf)wrapped, (ByteBuf)trackedByteBuf, leakTracker);
    }
}

