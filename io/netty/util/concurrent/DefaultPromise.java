/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultFutureListeners;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultPromise<V>
extends AbstractFuture<V>
implements Promise<V> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
    private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance((String)(DefaultPromise.class.getName() + ".rejectedExecution"));
    private static final int MAX_LISTENER_STACK_DEPTH = Math.min((int)8, (int)SystemPropertyUtil.getInt((String)"io.netty.defaultPromise.maxListenerStackDepth", (int)8));
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, (String)"result");
    private static final Object SUCCESS = new Object();
    private static final Object UNCANCELLABLE = new Object();
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder((Throwable)ThrowableUtil.unknownStackTrace(new CancellationException(), DefaultPromise.class, (String)"cancel(...)"));
    private static final StackTraceElement[] CANCELLATION_STACK = DefaultPromise.CANCELLATION_CAUSE_HOLDER.cause.getStackTrace();
    private volatile Object result;
    private final EventExecutor executor;
    private Object listeners;
    private short waiters;
    private boolean notifyingListeners;

    public DefaultPromise(EventExecutor executor) {
        this.executor = ObjectUtil.checkNotNull(executor, (String)"executor");
    }

    protected DefaultPromise() {
        this.executor = null;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        if (!this.setSuccess0(result)) throw new IllegalStateException((String)("complete already: " + this));
        return this;
    }

    @Override
    public boolean trySuccess(V result) {
        return this.setSuccess0(result);
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (!this.setFailure0((Throwable)cause)) throw new IllegalStateException((String)("complete already: " + this), (Throwable)cause);
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return this.setFailure0((Throwable)cause);
    }

    @Override
    public boolean setUncancellable() {
        if (RESULT_UPDATER.compareAndSet((DefaultPromise)this, null, (Object)UNCANCELLABLE)) {
            return true;
        }
        Object result = this.result;
        if (!DefaultPromise.isDone0((Object)result)) return true;
        if (!DefaultPromise.isCancelled0((Object)result)) return true;
        return false;
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        if (result == null) return false;
        if (result == UNCANCELLABLE) return false;
        if (result instanceof CauseHolder) return false;
        return true;
    }

    @Override
    public boolean isCancellable() {
        if (this.result != null) return false;
        return true;
    }

    @Override
    public Throwable cause() {
        return this.cause0((Object)this.result);
    }

    private Throwable cause0(Object result) {
        if (!(result instanceof CauseHolder)) {
            return null;
        }
        if (result != CANCELLATION_CAUSE_HOLDER) return ((CauseHolder)result).cause;
        LeanCancellationException ce = new LeanCancellationException(null);
        if (RESULT_UPDATER.compareAndSet((DefaultPromise)this, (Object)CANCELLATION_CAUSE_HOLDER, (Object)new CauseHolder((Throwable)ce))) {
            return ce;
        }
        result = this.result;
        return ((CauseHolder)result).cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        ObjectUtil.checkNotNull(listener, (String)"listener");
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        this.addListener0(listener);
        // MONITOREXIT : defaultPromise
        if (!this.isDone()) return this;
        this.notifyListeners();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        ObjectUtil.checkNotNull(listeners, (String)"listeners");
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
            if (listener == null) break;
            this.addListener0(listener);
        }
        // MONITOREXIT : defaultPromise
        if (!this.isDone()) return this;
        this.notifyListeners();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        ObjectUtil.checkNotNull(listener, (String)"listener");
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        this.removeListener0(listener);
        // MONITOREXIT : defaultPromise
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        ObjectUtil.checkNotNull(listeners, (String)"listeners");
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        GenericFutureListener<? extends Future<? super V>>[] arrgenericFutureListener = listeners;
        int n = arrgenericFutureListener.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                // MONITOREXIT : defaultPromise
                return this;
            }
            GenericFutureListener<Future<V>> listener = arrgenericFutureListener[n2];
            if (listener == null) {
                return this;
            }
            this.removeListener0(listener);
            ++n2;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    public Promise<V> await() throws InterruptedException {
        if (this.isDone()) {
            return this;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException((String)this.toString());
        }
        this.checkDeadLock();
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        do {
            if (this.isDone()) {
                // MONITOREXIT : defaultPromise
                return this;
            }
            this.incWaiters();
            try {
                this.wait();
                continue;
            }
            finally {
                this.decWaiters();
                continue;
            }
            break;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> awaitUninterruptibly() {
        if (this.isDone()) {
            return this;
        }
        this.checkDeadLock();
        boolean interrupted = false;
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        while (!this.isDone()) {
            this.incWaiters();
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
            finally {
                this.decWaiters();
            }
        }
        // MONITOREXIT : defaultPromise
        if (!interrupted) return this;
        Thread.currentThread().interrupt();
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.await0((long)unit.toNanos((long)timeout), (boolean)true);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.await0((long)TimeUnit.MILLISECONDS.toNanos((long)timeoutMillis), (boolean)true);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return this.await0((long)unit.toNanos((long)timeout), (boolean)false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return this.await0((long)TimeUnit.MILLISECONDS.toNanos((long)timeoutMillis), (boolean)false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public V getNow() {
        Object result = this.result;
        if (result instanceof CauseHolder) return (V)null;
        if (result == SUCCESS) return (V)null;
        if (result != UNCANCELLABLE) return (V)result;
        return (V)null;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        Object result = this.result;
        if (!DefaultPromise.isDone0((Object)result)) {
            this.await();
            result = this.result;
        }
        if (result == SUCCESS) return (V)null;
        if (result == UNCANCELLABLE) {
            return (V)null;
        }
        Throwable cause = this.cause0((Object)result);
        if (cause == null) {
            return (V)result;
        }
        if (!(cause instanceof CancellationException)) throw new ExecutionException((Throwable)cause);
        throw (CancellationException)cause;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Object result = this.result;
        if (!DefaultPromise.isDone0((Object)result)) {
            if (!this.await((long)timeout, (TimeUnit)unit)) {
                throw new TimeoutException();
            }
            result = this.result;
        }
        if (result == SUCCESS) return (V)null;
        if (result == UNCANCELLABLE) {
            return (V)null;
        }
        Throwable cause = this.cause0((Object)result);
        if (cause == null) {
            return (V)result;
        }
        if (!(cause instanceof CancellationException)) throw new ExecutionException((Throwable)cause);
        throw (CancellationException)cause;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!RESULT_UPDATER.compareAndSet((DefaultPromise)this, null, (Object)CANCELLATION_CAUSE_HOLDER)) return false;
        if (!this.checkNotifyWaiters()) return true;
        this.notifyListeners();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return DefaultPromise.isCancelled0((Object)this.result);
    }

    @Override
    public boolean isDone() {
        return DefaultPromise.isDone0((Object)this.result);
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Promise<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    public String toString() {
        return this.toStringBuilder().toString();
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder buf = new StringBuilder((int)64).append((String)StringUtil.simpleClassName((Object)this)).append((char)'@').append((String)Integer.toHexString((int)this.hashCode()));
        Object result = this.result;
        if (result == SUCCESS) {
            buf.append((String)"(success)");
            return buf;
        }
        if (result == UNCANCELLABLE) {
            buf.append((String)"(uncancellable)");
            return buf;
        }
        if (result instanceof CauseHolder) {
            buf.append((String)"(failure: ").append((Object)((CauseHolder)result).cause).append((char)')');
            return buf;
        }
        if (result != null) {
            buf.append((String)"(success: ").append((Object)result).append((char)')');
            return buf;
        }
        buf.append((String)"(incomplete)");
        return buf;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    protected void checkDeadLock() {
        EventExecutor e = this.executor();
        if (e == null) return;
        if (!e.inEventLoop()) return;
        throw new BlockingOperationException((String)this.toString());
    }

    protected static void notifyListener(EventExecutor eventExecutor, Future<?> future, GenericFutureListener<?> listener) {
        ObjectUtil.checkNotNull(eventExecutor, (String)"eventExecutor");
        ObjectUtil.checkNotNull(future, (String)"future");
        ObjectUtil.checkNotNull(listener, (String)"listener");
        DefaultPromise.notifyListenerWithStackOverFlowProtection((EventExecutor)eventExecutor, future, listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyListeners() {
        int stackDepth;
        InternalThreadLocalMap threadLocals;
        EventExecutor executor = this.executor();
        if (executor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < MAX_LISTENER_STACK_DEPTH) {
            threadLocals.setFutureListenerStackDepth((int)(stackDepth + 1));
            try {
                this.notifyListenersNow();
                return;
            }
            finally {
                threadLocals.setFutureListenerStackDepth((int)stackDepth);
            }
        }
        DefaultPromise.safeExecute((EventExecutor)executor, (Runnable)new Runnable((DefaultPromise)this){
            final /* synthetic */ DefaultPromise this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                DefaultPromise.access$200((DefaultPromise)this.this$0);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void notifyListenerWithStackOverFlowProtection(EventExecutor executor, Future<?> future, GenericFutureListener<?> listener) {
        int stackDepth;
        InternalThreadLocalMap threadLocals;
        if (executor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < MAX_LISTENER_STACK_DEPTH) {
            threadLocals.setFutureListenerStackDepth((int)(stackDepth + 1));
            try {
                DefaultPromise.notifyListener0(future, listener);
                return;
            }
            finally {
                threadLocals.setFutureListenerStackDepth((int)stackDepth);
            }
        }
        DefaultPromise.safeExecute((EventExecutor)executor, (Runnable)new Runnable(future, listener){
            final /* synthetic */ Future val$future;
            final /* synthetic */ GenericFutureListener val$listener;
            {
                this.val$future = future;
                this.val$listener = genericFutureListener;
            }

            public void run() {
                DefaultPromise.access$300((Future)this.val$future, (GenericFutureListener)this.val$listener);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyListenersNow() {
        DefaultPromise defaultPromise = this;
        // MONITORENTER : defaultPromise
        if (this.notifyingListeners || this.listeners == null) {
            // MONITOREXIT : defaultPromise
            return;
        }
        this.notifyingListeners = true;
        Object listeners = this.listeners;
        this.listeners = null;
        // MONITOREXIT : defaultPromise
        do {
            if (listeners instanceof DefaultFutureListeners) {
                this.notifyListeners0((DefaultFutureListeners)((DefaultFutureListeners)listeners));
            } else {
                DefaultPromise.notifyListener0((Future)this, (GenericFutureListener)((GenericFutureListener)listeners));
            }
            defaultPromise = this;
            // MONITORENTER : defaultPromise
            if (this.listeners == null) {
                this.notifyingListeners = false;
                // MONITOREXIT : defaultPromise
                return;
            }
            listeners = this.listeners;
            this.listeners = null;
            // MONITOREXIT : defaultPromise
        } while (true);
    }

    private void notifyListeners0(DefaultFutureListeners listeners) {
        GenericFutureListener<? extends Future<?>>[] a = listeners.listeners();
        int size = listeners.size();
        int i = 0;
        while (i < size) {
            DefaultPromise.notifyListener0((Future)this, a[i]);
            ++i;
        }
    }

    private static void notifyListener0(Future future, GenericFutureListener l) {
        try {
            l.operationComplete(future);
            return;
        }
        catch (Throwable t) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)("An exception was thrown by " + l.getClass().getName() + ".operationComplete()"), (Throwable)t);
        }
    }

    private void addListener0(GenericFutureListener<? extends Future<? super V>> listener) {
        if (this.listeners == null) {
            this.listeners = listener;
            return;
        }
        if (this.listeners instanceof DefaultFutureListeners) {
            ((DefaultFutureListeners)this.listeners).add(listener);
            return;
        }
        this.listeners = new DefaultFutureListeners((GenericFutureListener)this.listeners, listener);
    }

    private void removeListener0(GenericFutureListener<? extends Future<? super V>> listener) {
        if (this.listeners instanceof DefaultFutureListeners) {
            ((DefaultFutureListeners)this.listeners).remove(listener);
            return;
        }
        if (this.listeners != listener) return;
        this.listeners = null;
    }

    private boolean setSuccess0(V result) {
        Object object;
        if (result == null) {
            object = SUCCESS;
            return this.setValue0((Object)object);
        }
        object = result;
        return this.setValue0((Object)object);
    }

    private boolean setFailure0(Throwable cause) {
        return this.setValue0((Object)new CauseHolder((Throwable)ObjectUtil.checkNotNull(cause, (String)"cause")));
    }

    private boolean setValue0(Object objResult) {
        if (!RESULT_UPDATER.compareAndSet((DefaultPromise)this, null, (Object)objResult)) {
            if (!RESULT_UPDATER.compareAndSet((DefaultPromise)this, (Object)UNCANCELLABLE, (Object)objResult)) return false;
        }
        if (!this.checkNotifyWaiters()) return true;
        this.notifyListeners();
        return true;
    }

    private synchronized boolean checkNotifyWaiters() {
        if (this.waiters > 0) {
            this.notifyAll();
        }
        if (this.listeners == null) return false;
        return true;
    }

    private void incWaiters() {
        if (this.waiters == 32767) {
            throw new IllegalStateException((String)("too many waiters: " + this));
        }
        this.waiters = (short)(this.waiters + 1);
    }

    private void decWaiters() {
        this.waiters = (short)(this.waiters - 1);
    }

    private void rethrowIfFailed() {
        Throwable cause = this.cause();
        if (cause == null) {
            return;
        }
        PlatformDependent.throwException((Throwable)cause);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (this.isDone()) {
            return true;
        }
        if (timeoutNanos <= 0L) {
            return this.isDone();
        }
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException((String)this.toString());
        }
        this.checkDeadLock();
        long startTime = System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            do {
                DefaultPromise defaultPromise = this;
                // MONITORENTER : defaultPromise
                if (this.isDone()) {
                    boolean bl = true;
                    // MONITOREXIT : defaultPromise
                    return bl;
                }
                this.incWaiters();
                try {
                    this.wait((long)(waitTime / 1000000L), (int)((int)(waitTime % 1000000L)));
                }
                catch (InterruptedException e) {
                    if (interruptable) {
                        throw e;
                    }
                    interrupted = true;
                }
                finally {
                    this.decWaiters();
                }
                // MONITOREXIT : defaultPromise
                if (!this.isDone()) continue;
                boolean bl = true;
                return bl;
            } while ((waitTime = timeoutNanos - (System.nanoTime() - startTime)) > 0L);
            boolean bl = this.isDone();
            return bl;
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    void notifyProgressiveListeners(long progress, long total) {
        Object listeners = this.progressiveListeners();
        if (listeners == null) {
            return;
        }
        ProgressiveFuture self = (ProgressiveFuture)((Object)this);
        EventExecutor executor = this.executor();
        if (executor.inEventLoop()) {
            if (listeners instanceof GenericProgressiveFutureListener[]) {
                DefaultPromise.notifyProgressiveListeners0(self, (GenericProgressiveFutureListener[])listeners, (long)progress, (long)total);
                return;
            }
            DefaultPromise.notifyProgressiveListener0((ProgressiveFuture)self, (GenericProgressiveFutureListener)((GenericProgressiveFutureListener)listeners), (long)progress, (long)total);
            return;
        }
        if (listeners instanceof GenericProgressiveFutureListener[]) {
            GenericProgressiveFutureListener[] array = (GenericProgressiveFutureListener[])listeners;
            DefaultPromise.safeExecute((EventExecutor)executor, (Runnable)new Runnable((DefaultPromise)this, (ProgressiveFuture)self, (GenericProgressiveFutureListener[])array, (long)progress, (long)total){
                final /* synthetic */ ProgressiveFuture val$self;
                final /* synthetic */ GenericProgressiveFutureListener[] val$array;
                final /* synthetic */ long val$progress;
                final /* synthetic */ long val$total;
                final /* synthetic */ DefaultPromise this$0;
                {
                    this.this$0 = this$0;
                    this.val$self = progressiveFuture;
                    this.val$array = arrgenericProgressiveFutureListener;
                    this.val$progress = l;
                    this.val$total = l2;
                }

                public void run() {
                    DefaultPromise.access$400((ProgressiveFuture)this.val$self, (GenericProgressiveFutureListener[])this.val$array, (long)this.val$progress, (long)this.val$total);
                }
            });
            return;
        }
        GenericProgressiveFutureListener l = (GenericProgressiveFutureListener)listeners;
        DefaultPromise.safeExecute((EventExecutor)executor, (Runnable)new Runnable((DefaultPromise)this, (ProgressiveFuture)self, (GenericProgressiveFutureListener)l, (long)progress, (long)total){
            final /* synthetic */ ProgressiveFuture val$self;
            final /* synthetic */ GenericProgressiveFutureListener val$l;
            final /* synthetic */ long val$progress;
            final /* synthetic */ long val$total;
            final /* synthetic */ DefaultPromise this$0;
            {
                this.this$0 = this$0;
                this.val$self = progressiveFuture;
                this.val$l = genericProgressiveFutureListener;
                this.val$progress = l;
                this.val$total = l2;
            }

            public void run() {
                DefaultPromise.access$500((ProgressiveFuture)this.val$self, (GenericProgressiveFutureListener)this.val$l, (long)this.val$progress, (long)this.val$total);
            }
        });
    }

    private synchronized Object progressiveListeners() {
        Object listeners = this.listeners;
        if (listeners == null) {
            return null;
        }
        if (!(listeners instanceof DefaultFutureListeners)) {
            if (!(listeners instanceof GenericProgressiveFutureListener)) return null;
            return listeners;
        }
        DefaultFutureListeners dfl = (DefaultFutureListeners)listeners;
        int progressiveSize = dfl.progressiveSize();
        switch (progressiveSize) {
            case 0: {
                return null;
            }
            case 1: {
                GenericFutureListener<? extends Future<?>>[] arrgenericFutureListener = dfl.listeners();
                int n = arrgenericFutureListener.length;
                int n2 = 0;
                while (n2 < n) {
                    GenericFutureListener<Future<?>> l = arrgenericFutureListener[n2];
                    if (l instanceof GenericProgressiveFutureListener) {
                        return l;
                    }
                    ++n2;
                }
                return null;
            }
        }
        GenericFutureListener<? extends Future<?>>[] array = dfl.listeners();
        GenericProgressiveFutureListener[] copy = new GenericProgressiveFutureListener[progressiveSize];
        int i = 0;
        int j = 0;
        while (j < progressiveSize) {
            GenericFutureListener<Future<?>> l = array[i];
            if (l instanceof GenericProgressiveFutureListener) {
                copy[j++] = (GenericProgressiveFutureListener)l;
            }
            ++i;
        }
        return copy;
    }

    private static void notifyProgressiveListeners0(ProgressiveFuture<?> future, GenericProgressiveFutureListener<?>[] listeners, long progress, long total) {
        GenericProgressiveFutureListener<?>[] arrgenericProgressiveFutureListener = listeners;
        int n = arrgenericProgressiveFutureListener.length;
        int n2 = 0;
        while (n2 < n) {
            GenericProgressiveFutureListener<?> l = arrgenericProgressiveFutureListener[n2];
            if (l == null) {
                return;
            }
            DefaultPromise.notifyProgressiveListener0(future, l, (long)progress, (long)total);
            ++n2;
        }
    }

    private static void notifyProgressiveListener0(ProgressiveFuture future, GenericProgressiveFutureListener l, long progress, long total) {
        try {
            l.operationProgressed(future, (long)progress, (long)total);
            return;
        }
        catch (Throwable t) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)("An exception was thrown by " + l.getClass().getName() + ".operationProgressed()"), (Throwable)t);
        }
    }

    private static boolean isCancelled0(Object result) {
        if (!(result instanceof CauseHolder)) return false;
        if (!(((CauseHolder)result).cause instanceof CancellationException)) return false;
        return true;
    }

    private static boolean isDone0(Object result) {
        if (result == null) return false;
        if (result == UNCANCELLABLE) return false;
        return true;
    }

    private static void safeExecute(EventExecutor executor, Runnable task) {
        try {
            executor.execute((Runnable)task);
            return;
        }
        catch (Throwable t) {
            rejectedExecutionLogger.error((String)"Failed to submit a listener notification task. Event loop shut down?", (Throwable)t);
        }
    }

    static /* synthetic */ StackTraceElement[] access$000() {
        return CANCELLATION_STACK;
    }

    static /* synthetic */ void access$200(DefaultPromise x0) {
        x0.notifyListenersNow();
    }

    static /* synthetic */ void access$300(Future x0, GenericFutureListener x1) {
        DefaultPromise.notifyListener0((Future)x0, (GenericFutureListener)x1);
    }

    static /* synthetic */ void access$400(ProgressiveFuture x0, GenericProgressiveFutureListener[] x1, long x2, long x3) {
        DefaultPromise.notifyProgressiveListeners0(x0, x1, (long)x2, (long)x3);
    }

    static /* synthetic */ void access$500(ProgressiveFuture x0, GenericProgressiveFutureListener x1, long x2, long x3) {
        DefaultPromise.notifyProgressiveListener0((ProgressiveFuture)x0, (GenericProgressiveFutureListener)x1, (long)x2, (long)x3);
    }
}

