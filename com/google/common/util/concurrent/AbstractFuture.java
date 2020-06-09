/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.AbstractFuture$SafeAtomicHelper
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public abstract class AbstractFuture<V>
implements ListenableFuture<V> {
    private static final boolean GENERATE_CANCELLATION_CAUSES;
    private static final Logger log;
    private static final long SPIN_THRESHOLD_NANOS = 1000L;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Object NULL;
    private volatile Object value;
    private volatile Listener listeners;
    private volatile Waiter waiters;

    /*
     * Unable to fully structure code
     */
    private void removeWaiter(Waiter node) {
        node.thread = null;
        block0 : do {
            pred = null;
            curr = this.waiters;
            if (curr == Waiter.TOMBSTONE) {
                return;
            }
            while (curr != null) {
                succ = curr.next;
                if (curr.thread != null) {
                    pred = curr;
                } else if (pred != null) {
                    pred.next = succ;
                    if (pred.thread == null) {
                        continue block0;
                    }
                } else {
                    if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, (Waiter)curr, (Waiter)succ)) ** break;
                    continue block0;
                }
                curr = succ;
            }
            return;
            break;
        } while (true);
    }

    protected AbstractFuture() {
    }

    @CanIgnoreReturnValue
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        long endNanos;
        long remainingNanos = unit.toNanos((long)timeout);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object localValue = this.value;
        if (localValue != null & !(localValue instanceof SetFuture)) {
            return (V)this.getDoneValue((Object)localValue);
        }
        long l = endNanos = remainingNanos > 0L ? System.nanoTime() + remainingNanos : 0L;
        if (remainingNanos >= 1000L) {
            Waiter node;
            block9 : {
                Waiter oldHead = this.waiters;
                if (oldHead == Waiter.TOMBSTONE) return (V)this.getDoneValue((Object)this.value);
                node = new Waiter();
                do {
                    node.setNext((Waiter)oldHead);
                    if (ATOMIC_HELPER.casWaiters(this, (Waiter)oldHead, (Waiter)node)) break block9;
                } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
                return (V)this.getDoneValue((Object)this.value);
            }
            do {
                LockSupport.parkNanos((Object)this, (long)remainingNanos);
                if (Thread.interrupted()) {
                    this.removeWaiter((Waiter)node);
                    throw new InterruptedException();
                }
                localValue = this.value;
                if (!(localValue != null & !(localValue instanceof SetFuture))) continue;
                return (V)this.getDoneValue((Object)localValue);
            } while ((remainingNanos = endNanos - System.nanoTime()) >= 1000L);
            this.removeWaiter((Waiter)node);
        }
        while (remainingNanos > 0L) {
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return (V)this.getDoneValue((Object)localValue);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            remainingNanos = endNanos - System.nanoTime();
        }
        throw new TimeoutException();
    }

    @CanIgnoreReturnValue
    @Override
    public V get() throws InterruptedException, ExecutionException {
        Waiter node;
        Object localValue;
        block4 : {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return (V)this.getDoneValue((Object)localValue);
            }
            Waiter oldHead = this.waiters;
            if (oldHead == Waiter.TOMBSTONE) return (V)this.getDoneValue((Object)this.value);
            node = new Waiter();
            do {
                node.setNext((Waiter)oldHead);
                if (ATOMIC_HELPER.casWaiters(this, (Waiter)oldHead, (Waiter)node)) break block4;
            } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
            return (V)this.getDoneValue((Object)this.value);
        }
        do {
            LockSupport.park((Object)this);
            if (!Thread.interrupted()) continue;
            this.removeWaiter((Waiter)node);
            throw new InterruptedException();
        } while (!((localValue = this.value) != null & !(localValue instanceof SetFuture)));
        return (V)this.getDoneValue((Object)localValue);
    }

    private V getDoneValue(Object obj) throws ExecutionException {
        if (obj instanceof Cancellation) {
            throw AbstractFuture.cancellationExceptionWithCause((String)"Task was cancelled.", (Throwable)((Cancellation)obj).cause);
        }
        if (obj instanceof Failure) {
            throw new ExecutionException((Throwable)((Failure)obj).exception);
        }
        if (obj == NULL) {
            return (V)null;
        }
        Object asV = obj;
        return (V)asV;
    }

    @Override
    public boolean isDone() {
        boolean bl;
        Object localValue = this.value;
        boolean bl2 = localValue != null;
        if (!(localValue instanceof SetFuture)) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    @Override
    public boolean isCancelled() {
        Object localValue = this.value;
        return localValue instanceof Cancellation;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object localValue = this.value;
        boolean rValue = false;
        if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
        CancellationException cause = GENERATE_CANCELLATION_CAUSES ? new CancellationException((String)"Future.cancel() was called.") : null;
        Cancellation valueToSet = new Cancellation((boolean)mayInterruptIfRunning, (Throwable)cause);
        AbstractFuture abstractFuture = this;
        do {
            if (ATOMIC_HELPER.casValue(abstractFuture, (Object)localValue, (Object)valueToSet)) {
                rValue = true;
                if (mayInterruptIfRunning) {
                    abstractFuture.interruptTask();
                }
                AbstractFuture.complete(abstractFuture);
                if (!(localValue instanceof SetFuture)) return rValue;
                ListenableFuture<V> futureToPropagateTo = ((SetFuture)localValue).future;
                if (!(futureToPropagateTo instanceof TrustedFuture)) {
                    futureToPropagateTo.cancel((boolean)mayInterruptIfRunning);
                    return rValue;
                }
                AbstractFuture trusted = (AbstractFuture)futureToPropagateTo;
                localValue = trusted.value;
                if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
                abstractFuture = trusted;
                continue;
            }
            localValue = abstractFuture.value;
            if (!(localValue instanceof SetFuture)) return rValue;
        } while (true);
    }

    protected void interruptTask() {
    }

    protected final boolean wasInterrupted() {
        Object localValue = this.value;
        if (!(localValue instanceof Cancellation)) return false;
        if (!((Cancellation)localValue).wasInterrupted) return false;
        return true;
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
        Preconditions.checkNotNull(listener, (Object)"Runnable was null.");
        Preconditions.checkNotNull(executor, (Object)"Executor was null.");
        Listener oldHead = this.listeners;
        if (oldHead != Listener.TOMBSTONE) {
            Listener newNode = new Listener((Runnable)listener, (Executor)executor);
            do {
                newNode.next = oldHead;
                if (!ATOMIC_HELPER.casListeners(this, (Listener)oldHead, (Listener)newNode)) continue;
                return;
            } while ((oldHead = this.listeners) != Listener.TOMBSTONE);
        }
        AbstractFuture.executeListener((Runnable)listener, (Executor)executor);
    }

    @CanIgnoreReturnValue
    protected boolean set(@Nullable V value) {
        Object valueToSet = value == null ? NULL : value;
        if (!ATOMIC_HELPER.casValue(this, null, (Object)valueToSet)) return false;
        AbstractFuture.complete(this);
        return true;
    }

    @CanIgnoreReturnValue
    protected boolean setException(Throwable throwable) {
        Failure valueToSet = new Failure((Throwable)Preconditions.checkNotNull(throwable));
        if (!ATOMIC_HELPER.casValue(this, null, (Object)valueToSet)) return false;
        AbstractFuture.complete(this);
        return true;
    }

    @Beta
    @CanIgnoreReturnValue
    protected boolean setFuture(ListenableFuture<? extends V> future) {
        Preconditions.checkNotNull(future);
        Object localValue = this.value;
        if (localValue == null) {
            if (future.isDone()) {
                Object value = AbstractFuture.getFutureValue(future);
                if (!ATOMIC_HELPER.casValue(this, null, (Object)value)) return false;
                AbstractFuture.complete(this);
                return true;
            }
            SetFuture<? extends V> valueToSet = new SetFuture<V>(this, future);
            if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
                try {
                    future.addListener(valueToSet, (Executor)MoreExecutors.directExecutor());
                    return true;
                }
                catch (Throwable t) {
                    Failure failure;
                    try {
                        failure = new Failure((Throwable)t);
                    }
                    catch (Throwable oomMostLikely) {
                        failure = Failure.FALLBACK_INSTANCE;
                    }
                    boolean unused = ATOMIC_HELPER.casValue(this, valueToSet, (Object)failure);
                }
                return true;
            }
            localValue = this.value;
        }
        if (!(localValue instanceof Cancellation)) return false;
        future.cancel((boolean)((Cancellation)localValue).wasInterrupted);
        return false;
    }

    private static Object getFutureValue(ListenableFuture<?> future) {
        if (future instanceof TrustedFuture) {
            return ((AbstractFuture)future).value;
        }
        try {
            ? v = Futures.getDone(future);
            return v == null ? NULL : v;
        }
        catch (ExecutionException exception) {
            return new Failure((Throwable)exception.getCause());
        }
        catch (CancellationException cancellation) {
            return new Cancellation((boolean)false, (Throwable)cancellation);
        }
        catch (Throwable t) {
            return new Failure((Throwable)t);
        }
    }

    private static void complete(AbstractFuture<?> future) {
        Listener next = null;
        block0 : do {
            AbstractFuture.super.releaseWaiters();
            future.afterDone();
            next = AbstractFuture.super.clearListeners(next);
            future = null;
            while (next != null) {
                Listener curr = next;
                next = next.next;
                Runnable task = curr.task;
                if (task instanceof SetFuture) {
                    Object valueToSet;
                    SetFuture setFuture = (SetFuture)task;
                    future = setFuture.owner;
                    if (future.value != setFuture || !ATOMIC_HELPER.casValue(future, (Object)setFuture, (Object)(valueToSet = AbstractFuture.getFutureValue(setFuture.future)))) continue;
                    continue block0;
                }
                AbstractFuture.executeListener((Runnable)task, (Executor)curr.executor);
            }
            return;
            break;
        } while (true);
    }

    @Beta
    protected void afterDone() {
    }

    final Throwable trustedGetException() {
        return ((Failure)this.value).exception;
    }

    final void maybePropagateCancellation(@Nullable Future<?> related) {
        if (!(related != null & this.isCancelled())) return;
        related.cancel((boolean)this.wasInterrupted());
    }

    private void releaseWaiters() {
        Waiter head;
        while (!ATOMIC_HELPER.casWaiters(this, (Waiter)(head = this.waiters), (Waiter)Waiter.TOMBSTONE)) {
        }
        Waiter currentWaiter = head;
        while (currentWaiter != null) {
            currentWaiter.unpark();
            currentWaiter = currentWaiter.next;
        }
    }

    private Listener clearListeners(Listener onto) {
        Listener head;
        while (!ATOMIC_HELPER.casListeners(this, (Listener)(head = this.listeners), (Listener)Listener.TOMBSTONE)) {
        }
        Listener reversedList = onto;
        while (head != null) {
            Listener tmp = head;
            head = head.next;
            tmp.next = reversedList;
            reversedList = tmp;
        }
        return reversedList;
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute((Runnable)runnable);
            return;
        }
        catch (RuntimeException e) {
            log.log((Level)Level.SEVERE, (String)("RuntimeException while executing runnable " + runnable + " with executor " + executor), (Throwable)e);
        }
    }

    private static CancellationException cancellationExceptionWithCause(@Nullable String message, @Nullable Throwable cause) {
        CancellationException exception = new CancellationException((String)message);
        exception.initCause((Throwable)cause);
        return exception;
    }

    static /* synthetic */ AtomicHelper access$200() {
        return ATOMIC_HELPER;
    }

    static /* synthetic */ Object access$300(AbstractFuture x0) {
        return x0.value;
    }

    static /* synthetic */ Object access$400(ListenableFuture x0) {
        return AbstractFuture.getFutureValue(x0);
    }

    static /* synthetic */ void access$500(AbstractFuture x0) {
        AbstractFuture.complete(x0);
    }

    static /* synthetic */ Waiter access$700(AbstractFuture x0) {
        return x0.waiters;
    }

    static /* synthetic */ Waiter access$702(AbstractFuture x0, Waiter x1) {
        x0.waiters = x1;
        return x0.waiters;
    }

    static /* synthetic */ Listener access$800(AbstractFuture x0) {
        return x0.listeners;
    }

    static /* synthetic */ Listener access$802(AbstractFuture x0, Listener x1) {
        x0.listeners = x1;
        return x0.listeners;
    }

    static /* synthetic */ Object access$302(AbstractFuture x0, Object x1) {
        x0.value = x1;
        return x0.value;
    }

    static {
        AtomicHelper helper;
        GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean((String)System.getProperty((String)"guava.concurrent.generate_cancellation_cause", (String)"false"));
        log = Logger.getLogger((String)AbstractFuture.class.getName());
        try {
            helper = new UnsafeAtomicHelper(null);
        }
        catch (Throwable unsafeFailure) {
            try {
                helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, (String)"thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, (String)"next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, (String)"waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, (String)"listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, (String)"value"));
            }
            catch (Throwable atomicReferenceFieldUpdaterFailure) {
                log.log((Level)Level.SEVERE, (String)"UnsafeAtomicHelper is broken!", (Throwable)unsafeFailure);
                log.log((Level)Level.SEVERE, (String)"SafeAtomicHelper is broken!", (Throwable)atomicReferenceFieldUpdaterFailure);
                helper = new SynchronizedHelper(null);
            }
        }
        ATOMIC_HELPER = helper;
        Class<LockSupport> ensureLoaded = LockSupport.class;
        NULL = new Object();
    }
}

