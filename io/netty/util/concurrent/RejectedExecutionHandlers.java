/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public final class RejectedExecutionHandlers {
    private static final RejectedExecutionHandler REJECT = new RejectedExecutionHandler(){

        public void rejected(java.lang.Runnable task, io.netty.util.concurrent.SingleThreadEventExecutor executor) {
            throw new java.util.concurrent.RejectedExecutionException();
        }
    };

    private RejectedExecutionHandlers() {
    }

    public static RejectedExecutionHandler reject() {
        return REJECT;
    }

    public static RejectedExecutionHandler backoff(int retries, long backoffAmount, TimeUnit unit) {
        ObjectUtil.checkPositive((int)retries, (String)"retries");
        long backOffNanos = unit.toNanos((long)backoffAmount);
        return new RejectedExecutionHandler((int)retries, (long)backOffNanos){
            final /* synthetic */ int val$retries;
            final /* synthetic */ long val$backOffNanos;
            {
                this.val$retries = n;
                this.val$backOffNanos = l;
            }

            public void rejected(java.lang.Runnable task, io.netty.util.concurrent.SingleThreadEventExecutor executor) {
                if (executor.inEventLoop()) throw new java.util.concurrent.RejectedExecutionException();
                int i = 0;
                while (i < this.val$retries) {
                    executor.wakeup((boolean)false);
                    java.util.concurrent.locks.LockSupport.parkNanos((long)this.val$backOffNanos);
                    if (executor.offerTask((java.lang.Runnable)task)) {
                        return;
                    }
                    ++i;
                }
                throw new java.util.concurrent.RejectedExecutionException();
            }
        };
    }
}

