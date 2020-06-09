/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PromiseNotifier<V, F extends Future<V>>
implements GenericFutureListener<F> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PromiseNotifier.class);
    private final Promise<? super V>[] promises;
    private final boolean logNotifyFailure;

    @SafeVarargs
    public PromiseNotifier(Promise<? super V> ... promises) {
        this((boolean)true, promises);
    }

    @SafeVarargs
    public PromiseNotifier(boolean logNotifyFailure, Promise<? super V> ... promises) {
        ObjectUtil.checkNotNull(promises, (String)"promises");
        Promise<? super V>[] arrpromise = promises;
        int n = arrpromise.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.promises = (Promise[])promises.clone();
                this.logNotifyFailure = logNotifyFailure;
                return;
            }
            Promise<? super V> promise = arrpromise[n2];
            if (promise == null) {
                throw new IllegalArgumentException((String)"promises contains null Promise");
            }
            ++n2;
        } while (true);
    }

    @Override
    public void operationComplete(F future) throws Exception {
        InternalLogger internalLogger;
        InternalLogger internalLogger2 = internalLogger = this.logNotifyFailure ? logger : null;
        if (future.isSuccess()) {
            V result = future.get();
            Promise<? super V>[] arrpromise = this.promises;
            int n = arrpromise.length;
            int n2 = 0;
            while (n2 < n) {
                Promise<? super V> p = arrpromise[n2];
                PromiseNotificationUtil.trySuccess(p, result, (InternalLogger)internalLogger);
                ++n2;
            }
            return;
        }
        if (future.isCancelled()) {
            Promise<? super V>[] result = this.promises;
            int n = result.length;
            int n3 = 0;
            while (n3 < n) {
                Promise<? super V> p = result[n3];
                PromiseNotificationUtil.tryCancel(p, (InternalLogger)internalLogger);
                ++n3;
            }
            return;
        }
        Throwable cause = future.cause();
        Promise<? super V>[] arrpromise = this.promises;
        int n = arrpromise.length;
        int p = 0;
        while (p < n) {
            Promise<? super V> p2 = arrpromise[p];
            PromiseNotificationUtil.tryFailure(p2, (Throwable)cause, (InternalLogger)internalLogger);
            ++p;
        }
    }
}

