/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class ReferenceCountUpdater<T extends ReferenceCounted> {
    protected ReferenceCountUpdater() {
    }

    public static long getUnsafeOffset(Class<? extends ReferenceCounted> clz, String fieldName) {
        try {
            if (!PlatformDependent.hasUnsafe()) return -1L;
            return PlatformDependent.objectFieldOffset((Field)clz.getDeclaredField((String)fieldName));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return -1L;
    }

    protected abstract AtomicIntegerFieldUpdater<T> updater();

    protected abstract long unsafeOffset();

    public final int initialValue() {
        return 2;
    }

    private static int realRefCnt(int rawCnt) {
        if (rawCnt != 2 && rawCnt != 4 && (rawCnt & 1) != 0) {
            return 0;
        }
        int n = rawCnt >>> 1;
        return n;
    }

    private static int toLiveRealRefCnt(int rawCnt, int decrement) {
        if (rawCnt == 2) return rawCnt >>> 1;
        if (rawCnt == 4) return rawCnt >>> 1;
        if ((rawCnt & 1) != 0) throw new IllegalReferenceCountException((int)0, (int)(-decrement));
        return rawCnt >>> 1;
    }

    private int nonVolatileRawCnt(T instance) {
        int n;
        long offset = this.unsafeOffset();
        if (offset != -1L) {
            n = PlatformDependent.getInt(instance, (long)offset);
            return n;
        }
        n = this.updater().get(instance);
        return n;
    }

    public final int refCnt(T instance) {
        return ReferenceCountUpdater.realRefCnt((int)this.updater().get(instance));
    }

    public final boolean isLiveNonVolatile(T instance) {
        long offset = this.unsafeOffset();
        int rawCnt = offset != -1L ? PlatformDependent.getInt(instance, (long)offset) : this.updater().get(instance);
        if (rawCnt == 2) return true;
        if (rawCnt == 4) return true;
        if (rawCnt == 6) return true;
        if (rawCnt == 8) return true;
        if ((rawCnt & 1) == 0) return true;
        return false;
    }

    public final void setRefCnt(T instance, int refCnt) {
        this.updater().set(instance, (int)(refCnt > 0 ? refCnt << 1 : 1));
    }

    public final void resetRefCnt(T instance) {
        this.updater().set(instance, (int)this.initialValue());
    }

    public final T retain(T instance) {
        return (T)this.retain0(instance, (int)1, (int)2);
    }

    public final T retain(T instance, int increment) {
        int rawIncrement = ObjectUtil.checkPositive((int)increment, (String)"increment") << 1;
        return (T)this.retain0(instance, (int)increment, (int)rawIncrement);
    }

    private T retain0(T instance, int increment, int rawIncrement) {
        int oldRef = this.updater().getAndAdd(instance, (int)rawIncrement);
        if (oldRef != 2 && oldRef != 4 && (oldRef & 1) != 0) {
            throw new IllegalReferenceCountException((int)0, (int)increment);
        }
        if (oldRef > 0 || oldRef + rawIncrement < 0) {
            if (oldRef < 0) return (T)instance;
            if (oldRef + rawIncrement >= oldRef) return (T)instance;
        }
        this.updater().getAndAdd(instance, (int)(-rawIncrement));
        throw new IllegalReferenceCountException((int)ReferenceCountUpdater.realRefCnt((int)oldRef), (int)increment);
    }

    public final boolean release(T instance) {
        int rawCnt = this.nonVolatileRawCnt(instance);
        if (rawCnt != 2) {
            boolean bl = this.nonFinalRelease0(instance, (int)1, (int)rawCnt, (int)ReferenceCountUpdater.toLiveRealRefCnt((int)rawCnt, (int)1));
            return bl;
        }
        if (this.tryFinalRelease0(instance, (int)2)) return true;
        if (this.retryRelease0(instance, (int)1)) return true;
        return false;
    }

    public final boolean release(T instance, int decrement) {
        int rawCnt = this.nonVolatileRawCnt(instance);
        int realCnt = ReferenceCountUpdater.toLiveRealRefCnt((int)rawCnt, (int)ObjectUtil.checkPositive((int)decrement, (String)"decrement"));
        if (decrement != realCnt) {
            boolean bl = this.nonFinalRelease0(instance, (int)decrement, (int)rawCnt, (int)realCnt);
            return bl;
        }
        if (this.tryFinalRelease0(instance, (int)rawCnt)) return true;
        if (this.retryRelease0(instance, (int)decrement)) return true;
        return false;
    }

    private boolean tryFinalRelease0(T instance, int expectRawCnt) {
        return this.updater().compareAndSet(instance, (int)expectRawCnt, (int)1);
    }

    private boolean nonFinalRelease0(T instance, int decrement, int rawCnt, int realCnt) {
        if (decrement >= realCnt) return this.retryRelease0(instance, (int)decrement);
        if (!this.updater().compareAndSet(instance, (int)rawCnt, (int)(rawCnt - (decrement << 1)))) return this.retryRelease0(instance, (int)decrement);
        return false;
    }

    private boolean retryRelease0(T instance, int decrement) {
        do {
            int rawCnt;
            int realCnt;
            if (decrement == (realCnt = ReferenceCountUpdater.toLiveRealRefCnt((int)(rawCnt = this.updater().get(instance)), (int)decrement))) {
                if (this.tryFinalRelease0(instance, (int)rawCnt)) {
                    return true;
                }
            } else {
                if (decrement >= realCnt) throw new IllegalReferenceCountException((int)realCnt, (int)(-decrement));
                if (this.updater().compareAndSet(instance, (int)rawCnt, (int)(rawCnt - (decrement << 1)))) {
                    return false;
                }
            }
            Thread.yield();
        } while (true);
    }
}

