/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import java.util.concurrent.Executor;

@GwtIncompatible
public final class RemovalListeners {
    private RemovalListeners() {
    }

    public static <K, V> RemovalListener<K, V> asynchronous(RemovalListener<K, V> listener, Executor executor) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkNotNull(executor);
        return new RemovalListener<K, V>((Executor)executor, listener){
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ RemovalListener val$listener;
            {
                this.val$executor = executor;
                this.val$listener = removalListener;
            }

            public void onRemoval(com.google.common.cache.RemovalNotification<K, V> notification) {
                this.val$executor.execute((java.lang.Runnable)new java.lang.Runnable(this, notification){
                    final /* synthetic */ com.google.common.cache.RemovalNotification val$notification;
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$notification = removalNotification;
                    }

                    public void run() {
                        this.this$0.val$listener.onRemoval(this.val$notification);
                    }
                });
            }
        };
    }
}

