/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf
extends AbstractByteBuf {
    private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, (String)"refCnt");
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, (String)"refCnt");
    private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>(){

        protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater() {
            return AbstractReferenceCountedByteBuf.access$000();
        }

        protected long unsafeOffset() {
            return AbstractReferenceCountedByteBuf.access$100();
        }
    };
    private volatile int refCnt = updater.initialValue();

    protected AbstractReferenceCountedByteBuf(int maxCapacity) {
        super((int)maxCapacity);
    }

    @Override
    boolean isAccessible() {
        return updater.isLiveNonVolatile((AbstractReferenceCountedByteBuf)this);
    }

    @Override
    public int refCnt() {
        return updater.refCnt((AbstractReferenceCountedByteBuf)this);
    }

    protected final void setRefCnt(int refCnt) {
        updater.setRefCnt((AbstractReferenceCountedByteBuf)this, (int)refCnt);
    }

    protected final void resetRefCnt() {
        updater.resetRefCnt((AbstractReferenceCountedByteBuf)this);
    }

    @Override
    public ByteBuf retain() {
        return (ByteBuf)updater.retain((AbstractReferenceCountedByteBuf)this);
    }

    @Override
    public ByteBuf retain(int increment) {
        return (ByteBuf)updater.retain((AbstractReferenceCountedByteBuf)this, (int)increment);
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
        return this.handleRelease((boolean)updater.release((AbstractReferenceCountedByteBuf)this));
    }

    @Override
    public boolean release(int decrement) {
        return this.handleRelease((boolean)updater.release((AbstractReferenceCountedByteBuf)this, (int)decrement));
    }

    private boolean handleRelease(boolean result) {
        if (!result) return result;
        this.deallocate();
        return result;
    }

    protected abstract void deallocate();

    static /* synthetic */ AtomicIntegerFieldUpdater access$000() {
        return AIF_UPDATER;
    }

    static /* synthetic */ long access$100() {
        return REFCNT_FIELD_OFFSET;
    }
}

