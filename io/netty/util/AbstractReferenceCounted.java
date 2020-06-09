/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted
implements ReferenceCounted {
    private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCounted.class, (String)"refCnt");
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, (String)"refCnt");
    private static final ReferenceCountUpdater<AbstractReferenceCounted> updater = new ReferenceCountUpdater<AbstractReferenceCounted>(){

        protected AtomicIntegerFieldUpdater<AbstractReferenceCounted> updater() {
            return AbstractReferenceCounted.access$000();
        }

        protected long unsafeOffset() {
            return AbstractReferenceCounted.access$100();
        }
    };
    private volatile int refCnt = updater.initialValue();

    @Override
    public int refCnt() {
        return updater.refCnt((AbstractReferenceCounted)this);
    }

    protected final void setRefCnt(int refCnt) {
        updater.setRefCnt((AbstractReferenceCounted)this, (int)refCnt);
    }

    @Override
    public ReferenceCounted retain() {
        return updater.retain((AbstractReferenceCounted)this);
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return updater.retain((AbstractReferenceCounted)this, (int)increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this.touch(null);
    }

    @Override
    public boolean release() {
        return this.handleRelease((boolean)updater.release((AbstractReferenceCounted)this));
    }

    @Override
    public boolean release(int decrement) {
        return this.handleRelease((boolean)updater.release((AbstractReferenceCounted)this, (int)decrement));
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

