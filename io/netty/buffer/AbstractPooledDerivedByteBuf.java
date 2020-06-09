/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCounted;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractPooledDerivedByteBuf
extends AbstractReferenceCountedByteBuf {
    private final Recycler.Handle<AbstractPooledDerivedByteBuf> recyclerHandle;
    private AbstractByteBuf rootParent;
    private ByteBuf parent;

    AbstractPooledDerivedByteBuf(Recycler.Handle<? extends AbstractPooledDerivedByteBuf> recyclerHandle) {
        super((int)0);
        this.recyclerHandle = recyclerHandle;
    }

    final void parent(ByteBuf newParent) {
        assert (newParent instanceof SimpleLeakAwareByteBuf);
        this.parent = newParent;
    }

    @Override
    public final AbstractByteBuf unwrap() {
        return this.rootParent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final <U extends AbstractPooledDerivedByteBuf> U init(AbstractByteBuf unwrapped, ByteBuf wrapped, int readerIndex, int writerIndex, int maxCapacity) {
        wrapped.retain();
        this.parent = wrapped;
        this.rootParent = unwrapped;
        try {
            this.maxCapacity((int)maxCapacity);
            this.setIndex0((int)readerIndex, (int)writerIndex);
            this.resetRefCnt();
            AbstractPooledDerivedByteBuf castThis = this;
            wrapped = null;
            AbstractPooledDerivedByteBuf abstractPooledDerivedByteBuf = castThis;
            return (U)((U)abstractPooledDerivedByteBuf);
        }
        finally {
            if (wrapped != null) {
                this.rootParent = null;
                this.parent = null;
                wrapped.release();
            }
        }
    }

    @Override
    protected final void deallocate() {
        ByteBuf parent = this.parent;
        this.recyclerHandle.recycle((AbstractPooledDerivedByteBuf)this);
        parent.release();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.unwrap().alloc();
    }

    @Deprecated
    @Override
    public final ByteOrder order() {
        return this.unwrap().order();
    }

    @Override
    public boolean isReadOnly() {
        return this.unwrap().isReadOnly();
    }

    @Override
    public final boolean isDirect() {
        return this.unwrap().isDirect();
    }

    @Override
    public boolean hasArray() {
        return this.unwrap().hasArray();
    }

    @Override
    public byte[] array() {
        return this.unwrap().array();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.unwrap().hasMemoryAddress();
    }

    @Override
    public final int nioBufferCount() {
        return this.unwrap().nioBufferCount();
    }

    @Override
    public final ByteBuffer internalNioBuffer(int index, int length) {
        return this.nioBuffer((int)index, (int)length);
    }

    @Override
    public final ByteBuf retainedSlice() {
        int index = this.readerIndex();
        return this.retainedSlice((int)index, (int)(this.writerIndex() - index));
    }

    @Override
    public ByteBuf slice(int index, int length) {
        this.ensureAccessible();
        return new PooledNonRetainedSlicedByteBuf((ReferenceCounted)this, (AbstractByteBuf)this.unwrap(), (int)index, (int)length);
    }

    final ByteBuf duplicate0() {
        this.ensureAccessible();
        return new PooledNonRetainedDuplicateByteBuf((ReferenceCounted)this, (AbstractByteBuf)this.unwrap());
    }
}

