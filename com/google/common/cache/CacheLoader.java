/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import java.util.concurrent.Executor;

@GwtCompatible(emulated=true)
public abstract class CacheLoader<K, V> {
    protected CacheLoader() {
    }

    public abstract V load(K var1) throws Exception;

    @GwtIncompatible
    public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(oldValue);
        return Futures.immediateFuture(this.load(key));
    }

    public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
        throw new UnsupportedLoadingOperationException();
    }

    public static <K, V> CacheLoader<K, V> from(Function<K, V> function) {
        return new FunctionToCacheLoader<K, V>(function);
    }

    public static <V> CacheLoader<Object, V> from(Supplier<V> supplier) {
        return new SupplierToCacheLoader<V>(supplier);
    }

    @GwtIncompatible
    public static <K, V> CacheLoader<K, V> asyncReloading(CacheLoader<K, V> loader, Executor executor) {
        Preconditions.checkNotNull(loader);
        Preconditions.checkNotNull(executor);
        return new CacheLoader<K, V>(loader, (Executor)executor){
            final /* synthetic */ CacheLoader val$loader;
            final /* synthetic */ Executor val$executor;
            {
                this.val$loader = cacheLoader;
                this.val$executor = executor;
            }

            public V load(K key) throws Exception {
                return (V)this.val$loader.load(key);
            }

            public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
                com.google.common.util.concurrent.ListenableFutureTask<V> task = com.google.common.util.concurrent.ListenableFutureTask.create(new java.util.concurrent.Callable<V>(this, key, oldValue){
                    final /* synthetic */ Object val$key;
                    final /* synthetic */ Object val$oldValue;
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$key = object;
                        this.val$oldValue = object2;
                    }

                    public V call() throws Exception {
                        return (V)this.this$0.val$loader.reload(this.val$key, this.val$oldValue).get();
                    }
                });
                this.val$executor.execute(task);
                return task;
            }

            public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
                return this.val$loader.loadAll(keys);
            }
        };
    }
}

