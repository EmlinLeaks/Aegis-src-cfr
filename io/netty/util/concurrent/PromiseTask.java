/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseTask;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V>
extends DefaultPromise<V>
implements RunnableFuture<V> {
    private static final Runnable COMPLETED = new SentinelRunnable((String)"COMPLETED");
    private static final Runnable CANCELLED = new SentinelRunnable((String)"CANCELLED");
    private static final Runnable FAILED = new SentinelRunnable((String)"FAILED");
    private Object task;

    PromiseTask(EventExecutor executor, Runnable runnable, V result) {
        super((EventExecutor)executor);
        this.task = result == null ? runnable : new RunnableAdapter<V>((Runnable)runnable, result);
    }

    PromiseTask(EventExecutor executor, Runnable runnable) {
        super((EventExecutor)executor);
        this.task = runnable;
    }

    PromiseTask(EventExecutor executor, Callable<V> callable) {
        super((EventExecutor)executor);
        this.task = callable;
    }

    public final int hashCode() {
        return System.identityHashCode((Object)this);
    }

    public final boolean equals(Object obj) {
        if (this != obj) return false;
        return true;
    }

    final V runTask() throws Exception {
        Object task = this.task;
        if (task instanceof Callable) {
            return (V)((Callable)task).call();
        }
        ((Runnable)task).run();
        return (V)null;
    }

    @Override
    public void run() {
        try {
            if (!this.setUncancellableInternal()) return;
            V result = this.runTask();
            this.setSuccessInternal(result);
            return;
        }
        catch (Throwable e) {
            this.setFailureInternal((Throwable)e);
        }
    }

    private boolean clearTaskAfterCompletion(boolean done, Runnable result) {
        if (!done) return done;
        this.task = result;
        return done;
    }

    @Override
    public final Promise<V> setFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setFailureInternal(Throwable cause) {
        super.setFailure((Throwable)cause);
        this.clearTaskAfterCompletion((boolean)true, (Runnable)FAILED);
        return this;
    }

    @Override
    public final boolean tryFailure(Throwable cause) {
        return false;
    }

    protected final boolean tryFailureInternal(Throwable cause) {
        return this.clearTaskAfterCompletion((boolean)super.tryFailure((Throwable)cause), (Runnable)FAILED);
    }

    @Override
    public final Promise<V> setSuccess(V result) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setSuccessInternal(V result) {
        super.setSuccess(result);
        this.clearTaskAfterCompletion((boolean)true, (Runnable)COMPLETED);
        return this;
    }

    @Override
    public final boolean trySuccess(V result) {
        return false;
    }

    protected final boolean trySuccessInternal(V result) {
        return this.clearTaskAfterCompletion((boolean)super.trySuccess(result), (Runnable)COMPLETED);
    }

    @Override
    public final boolean setUncancellable() {
        throw new IllegalStateException();
    }

    protected final boolean setUncancellableInternal() {
        return super.setUncancellable();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.clearTaskAfterCompletion((boolean)super.cancel((boolean)mayInterruptIfRunning), (Runnable)CANCELLED);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.setCharAt((int)(buf.length() - 1), (char)',');
        return buf.append((String)" task: ").append((Object)this.task).append((char)')');
    }
}

