/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.NettyRuntime;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler<T> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
    private static final Handle NOOP_HANDLE = new Handle(){

        public void recycle(Object object) {
        }
    };
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger((int)Integer.MIN_VALUE);
    private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
    private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
    private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
    private static final int INITIAL_CAPACITY;
    private static final int MAX_SHARED_CAPACITY_FACTOR;
    private static final int MAX_DELAYED_QUEUES_PER_THREAD;
    private static final int LINK_CAPACITY;
    private static final int RATIO;
    private final int maxCapacityPerThread;
    private final int maxSharedCapacityFactor;
    private final int ratioMask;
    private final int maxDelayedQueuesPerThread;
    private final FastThreadLocal<Stack<T>> threadLocal = new FastThreadLocal<Stack<T>>((Recycler)this){
        final /* synthetic */ Recycler this$0;
        {
            this.this$0 = this$0;
        }

        protected Stack<T> initialValue() {
            return new Stack<T>(this.this$0, (java.lang.Thread)java.lang.Thread.currentThread(), (int)Recycler.access$000((Recycler)this.this$0), (int)Recycler.access$100((Recycler)this.this$0), (int)Recycler.access$200((Recycler)this.this$0), (int)Recycler.access$300((Recycler)this.this$0));
        }

        protected void onRemoval(Stack<T> value) {
            if (value.threadRef.get() != java.lang.Thread.currentThread()) return;
            if (!Recycler.access$400().isSet()) return;
            ((Map)Recycler.access$400().get()).remove(value);
        }
    };
    private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED;

    protected Recycler() {
        this((int)DEFAULT_MAX_CAPACITY_PER_THREAD);
    }

    protected Recycler(int maxCapacityPerThread) {
        this((int)maxCapacityPerThread, (int)MAX_SHARED_CAPACITY_FACTOR);
    }

    protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor) {
        this((int)maxCapacityPerThread, (int)maxSharedCapacityFactor, (int)RATIO, (int)MAX_DELAYED_QUEUES_PER_THREAD);
    }

    protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread) {
        this.ratioMask = MathUtil.safeFindNextPositivePowerOfTwo((int)ratio) - 1;
        if (maxCapacityPerThread <= 0) {
            this.maxCapacityPerThread = 0;
            this.maxSharedCapacityFactor = 1;
            this.maxDelayedQueuesPerThread = 0;
            return;
        }
        this.maxCapacityPerThread = maxCapacityPerThread;
        this.maxSharedCapacityFactor = Math.max((int)1, (int)maxSharedCapacityFactor);
        this.maxDelayedQueuesPerThread = Math.max((int)0, (int)maxDelayedQueuesPerThread);
    }

    public final T get() {
        if (this.maxCapacityPerThread == 0) {
            return (T)this.newObject(NOOP_HANDLE);
        }
        Stack<T> stack = this.threadLocal.get();
        DefaultHandle<T> handle = stack.pop();
        if (handle != null) return (T)handle.value;
        handle = stack.newHandle();
        handle.value = this.newObject(handle);
        return (T)handle.value;
    }

    @Deprecated
    public final boolean recycle(T o, Handle<T> handle) {
        if (handle == NOOP_HANDLE) {
            return false;
        }
        DefaultHandle h = (DefaultHandle)handle;
        if (DefaultHandle.access$600((DefaultHandle)h).parent != this) {
            return false;
        }
        h.recycle(o);
        return true;
    }

    final int threadLocalCapacity() {
        return this.threadLocal.get().elements.length;
    }

    final int threadLocalSize() {
        return this.threadLocal.get().size;
    }

    protected abstract T newObject(Handle<T> var1);

    static /* synthetic */ int access$000(Recycler x0) {
        return x0.maxCapacityPerThread;
    }

    static /* synthetic */ int access$100(Recycler x0) {
        return x0.maxSharedCapacityFactor;
    }

    static /* synthetic */ int access$200(Recycler x0) {
        return x0.ratioMask;
    }

    static /* synthetic */ int access$300(Recycler x0) {
        return x0.maxDelayedQueuesPerThread;
    }

    static /* synthetic */ FastThreadLocal access$400() {
        return DELAYED_RECYCLED;
    }

    static /* synthetic */ int access$900() {
        return LINK_CAPACITY;
    }

    static /* synthetic */ AtomicInteger access$1000() {
        return ID_GENERATOR;
    }

    static /* synthetic */ int access$1500() {
        return INITIAL_CAPACITY;
    }

    static /* synthetic */ int access$1900() {
        return OWN_THREAD_ID;
    }

    static {
        int maxCapacityPerThread = SystemPropertyUtil.getInt((String)"io.netty.recycler.maxCapacityPerThread", (int)SystemPropertyUtil.getInt((String)"io.netty.recycler.maxCapacity", (int)4096));
        if (maxCapacityPerThread < 0) {
            maxCapacityPerThread = 4096;
        }
        DEFAULT_MAX_CAPACITY_PER_THREAD = maxCapacityPerThread;
        MAX_SHARED_CAPACITY_FACTOR = Math.max((int)2, (int)SystemPropertyUtil.getInt((String)"io.netty.recycler.maxSharedCapacityFactor", (int)2));
        MAX_DELAYED_QUEUES_PER_THREAD = Math.max((int)0, (int)SystemPropertyUtil.getInt((String)"io.netty.recycler.maxDelayedQueuesPerThread", (int)(NettyRuntime.availableProcessors() * 2)));
        LINK_CAPACITY = MathUtil.safeFindNextPositivePowerOfTwo((int)Math.max((int)SystemPropertyUtil.getInt((String)"io.netty.recycler.linkCapacity", (int)16), (int)16));
        RATIO = MathUtil.safeFindNextPositivePowerOfTwo((int)SystemPropertyUtil.getInt((String)"io.netty.recycler.ratio", (int)8));
        if (logger.isDebugEnabled()) {
            if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
                logger.debug((String)"-Dio.netty.recycler.maxCapacityPerThread: disabled");
                logger.debug((String)"-Dio.netty.recycler.maxSharedCapacityFactor: disabled");
                logger.debug((String)"-Dio.netty.recycler.linkCapacity: disabled");
                logger.debug((String)"-Dio.netty.recycler.ratio: disabled");
            } else {
                logger.debug((String)"-Dio.netty.recycler.maxCapacityPerThread: {}", (Object)Integer.valueOf((int)DEFAULT_MAX_CAPACITY_PER_THREAD));
                logger.debug((String)"-Dio.netty.recycler.maxSharedCapacityFactor: {}", (Object)Integer.valueOf((int)MAX_SHARED_CAPACITY_FACTOR));
                logger.debug((String)"-Dio.netty.recycler.linkCapacity: {}", (Object)Integer.valueOf((int)LINK_CAPACITY));
                logger.debug((String)"-Dio.netty.recycler.ratio: {}", (Object)Integer.valueOf((int)RATIO));
            }
        }
        INITIAL_CAPACITY = Math.min((int)DEFAULT_MAX_CAPACITY_PER_THREAD, (int)256);
        DELAYED_RECYCLED = new FastThreadLocal<Map<Stack<?>, WeakOrderQueue>>(){

            protected Map<Stack<?>, WeakOrderQueue> initialValue() {
                return new java.util.WeakHashMap<Stack<?>, WeakOrderQueue>();
            }
        };
    }
}

