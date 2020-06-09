/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.ObjectUtil;

public final class PromiseCombiner {
    private int expectedCount;
    private int doneCount;
    private Promise<Void> aggregatePromise;
    private Throwable cause;
    private final GenericFutureListener<Future<?>> listener = new GenericFutureListener<Future<?>>((PromiseCombiner)this){
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ PromiseCombiner this$0;
        {
            this.this$0 = this$0;
        }

        public void operationComplete(Future<?> future) {
            if (PromiseCombiner.access$000((PromiseCombiner)this.this$0).inEventLoop()) {
                this.operationComplete0(future);
                return;
            }
            PromiseCombiner.access$000((PromiseCombiner)this.this$0).execute((java.lang.Runnable)new java.lang.Runnable(this, future){
                final /* synthetic */ Future val$future;
                final /* synthetic */ 1 this$1;
                {
                    this.this$1 = this$1;
                    this.val$future = future;
                }

                public void run() {
                    1.access$100(this.this$1, (Future)this.val$future);
                }
            });
        }

        private void operationComplete0(Future<?> future) {
            if (!$assertionsDisabled && !PromiseCombiner.access$000((PromiseCombiner)this.this$0).inEventLoop()) {
                throw new java.lang.AssertionError();
            }
            PromiseCombiner.access$204((PromiseCombiner)this.this$0);
            if (!future.isSuccess() && PromiseCombiner.access$300((PromiseCombiner)this.this$0) == null) {
                PromiseCombiner.access$302((PromiseCombiner)this.this$0, (Throwable)future.cause());
            }
            if (PromiseCombiner.access$200((PromiseCombiner)this.this$0) != PromiseCombiner.access$400((PromiseCombiner)this.this$0)) return;
            if (PromiseCombiner.access$500((PromiseCombiner)this.this$0) == null) return;
            PromiseCombiner.access$600((PromiseCombiner)this.this$0);
        }

        static /* synthetic */ void access$100(1 x0, Future x1) {
            x0.operationComplete0(x1);
        }

        static {
            $assertionsDisabled = !PromiseCombiner.class.desiredAssertionStatus();
        }
    };
    private final EventExecutor executor;

    @Deprecated
    public PromiseCombiner() {
        this((EventExecutor)ImmediateEventExecutor.INSTANCE);
    }

    public PromiseCombiner(EventExecutor executor) {
        this.executor = ObjectUtil.checkNotNull(executor, (String)"executor");
    }

    @Deprecated
    public void add(Promise promise) {
        this.add((Future)promise);
    }

    public void add(Future future) {
        this.checkAddAllowed();
        this.checkInEventLoop();
        ++this.expectedCount;
        future.addListener(this.listener);
    }

    @Deprecated
    public void addAll(Promise ... promises) {
        this.addAll((Future[])((Future[])promises));
    }

    public void addAll(Future ... futures) {
        Future[] arrfuture = futures;
        int n = arrfuture.length;
        int n2 = 0;
        while (n2 < n) {
            Future future = arrfuture[n2];
            this.add((Future)future);
            ++n2;
        }
    }

    public void finish(Promise<Void> aggregatePromise) {
        ObjectUtil.checkNotNull(aggregatePromise, (String)"aggregatePromise");
        this.checkInEventLoop();
        if (this.aggregatePromise != null) {
            throw new IllegalStateException((String)"Already finished");
        }
        this.aggregatePromise = aggregatePromise;
        if (this.doneCount != this.expectedCount) return;
        this.tryPromise();
    }

    private void checkInEventLoop() {
        if (this.executor.inEventLoop()) return;
        throw new IllegalStateException((String)"Must be called from EventExecutor thread");
    }

    private boolean tryPromise() {
        boolean bl;
        if (this.cause == null) {
            bl = this.aggregatePromise.trySuccess(null);
            return bl;
        }
        bl = this.aggregatePromise.tryFailure((Throwable)this.cause);
        return bl;
    }

    private void checkAddAllowed() {
        if (this.aggregatePromise == null) return;
        throw new IllegalStateException((String)"Adding promises is not allowed after finished adding");
    }

    static /* synthetic */ EventExecutor access$000(PromiseCombiner x0) {
        return x0.executor;
    }

    static /* synthetic */ int access$204(PromiseCombiner x0) {
        return ++x0.doneCount;
    }

    static /* synthetic */ Throwable access$300(PromiseCombiner x0) {
        return x0.cause;
    }

    static /* synthetic */ Throwable access$302(PromiseCombiner x0, Throwable x1) {
        x0.cause = x1;
        return x0.cause;
    }

    static /* synthetic */ int access$200(PromiseCombiner x0) {
        return x0.doneCount;
    }

    static /* synthetic */ int access$400(PromiseCombiner x0) {
        return x0.expectedCount;
    }

    static /* synthetic */ Promise access$500(PromiseCombiner x0) {
        return x0.aggregatePromise;
    }

    static /* synthetic */ boolean access$600(PromiseCombiner x0) {
        return x0.tryPromise();
    }
}

