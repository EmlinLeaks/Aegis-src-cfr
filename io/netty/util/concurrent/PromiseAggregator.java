/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Deprecated
public class PromiseAggregator<V, F extends Future<V>>
implements GenericFutureListener<F> {
    private final Promise<?> aggregatePromise;
    private final boolean failPending;
    private Set<Promise<V>> pendingPromises;

    public PromiseAggregator(Promise<Void> aggregatePromise, boolean failPending) {
        if (aggregatePromise == null) {
            throw new NullPointerException((String)"aggregatePromise");
        }
        this.aggregatePromise = aggregatePromise;
        this.failPending = failPending;
    }

    public PromiseAggregator(Promise<Void> aggregatePromise) {
        this(aggregatePromise, (boolean)true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SafeVarargs
    public final PromiseAggregator<V, F> add(Promise<V> ... promises) {
        if (promises == null) {
            throw new NullPointerException((String)"promises");
        }
        if (promises.length == 0) {
            return this;
        }
        PromiseAggregator promiseAggregator = this;
        // MONITORENTER : promiseAggregator
        if (this.pendingPromises == null) {
            int size = promises.length > 1 ? promises.length : 2;
            this.pendingPromises = new LinkedHashSet<Promise<V>>((int)size);
        }
        Promise<V>[] size = promises;
        int n = size.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                // MONITOREXIT : promiseAggregator
                return this;
            }
            Promise<V> p = size[n2];
            if (p != null) {
                this.pendingPromises.add(p);
                p.addListener(this);
            }
            ++n2;
        } while (true);
    }

    @Override
    public synchronized void operationComplete(F future) throws Exception {
        if (this.pendingPromises == null) {
            this.aggregatePromise.setSuccess(null);
            return;
        }
        this.pendingPromises.remove(future);
        if (future.isSuccess()) {
            if (!this.pendingPromises.isEmpty()) return;
            this.aggregatePromise.setSuccess(null);
            return;
        }
        Throwable cause = future.cause();
        this.aggregatePromise.setFailure((Throwable)cause);
        if (!this.failPending) return;
        Iterator<Promise<V>> iterator = this.pendingPromises.iterator();
        while (iterator.hasNext()) {
            Promise<V> pendingFuture = iterator.next();
            pendingFuture.setFailure((Throwable)cause);
        }
    }
}

