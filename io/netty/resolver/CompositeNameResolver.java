/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.CompositeNameResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.List;

public final class CompositeNameResolver<T>
extends SimpleNameResolver<T> {
    private final NameResolver<T>[] resolvers;

    public CompositeNameResolver(EventExecutor executor, NameResolver<T> ... resolvers) {
        super((EventExecutor)executor);
        ObjectUtil.checkNotNull(resolvers, (String)"resolvers");
        for (int i = 0; i < resolvers.length; ++i) {
            if (resolvers[i] != null) continue;
            throw new NullPointerException((String)("resolvers[" + i + ']'));
        }
        if (resolvers.length < 2) {
            throw new IllegalArgumentException((String)("resolvers: " + Arrays.asList(resolvers) + " (expected: at least 2 resolvers)"));
        }
        this.resolvers = (NameResolver[])resolvers.clone();
    }

    @Override
    protected void doResolve(String inetHost, Promise<T> promise) throws Exception {
        this.doResolveRec((String)inetHost, promise, (int)0, null);
    }

    private void doResolveRec(String inetHost, Promise<T> promise, int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure((Throwable)lastFailure);
            return;
        }
        NameResolver<T> resolver = this.resolvers[resolverIndex];
        resolver.resolve((String)inetHost).addListener(new FutureListener<T>((CompositeNameResolver)this, promise, (String)inetHost, (int)resolverIndex){
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ String val$inetHost;
            final /* synthetic */ int val$resolverIndex;
            final /* synthetic */ CompositeNameResolver this$0;
            {
                this.this$0 = this$0;
                this.val$promise = promise;
                this.val$inetHost = string;
                this.val$resolverIndex = n;
            }

            public void operationComplete(Future<T> future) throws Exception {
                if (future.isSuccess()) {
                    this.val$promise.setSuccess(future.getNow());
                    return;
                }
                CompositeNameResolver.access$000((CompositeNameResolver)this.this$0, (String)this.val$inetHost, (Promise)this.val$promise, (int)(this.val$resolverIndex + 1), (Throwable)future.cause());
            }
        });
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<T>> promise) throws Exception {
        this.doResolveAllRec((String)inetHost, promise, (int)0, null);
    }

    private void doResolveAllRec(String inetHost, Promise<List<T>> promise, int resolverIndex, Throwable lastFailure) throws Exception {
        if (resolverIndex >= this.resolvers.length) {
            promise.setFailure((Throwable)lastFailure);
            return;
        }
        NameResolver<T> resolver = this.resolvers[resolverIndex];
        resolver.resolveAll((String)inetHost).addListener(new FutureListener<List<T>>((CompositeNameResolver)this, promise, (String)inetHost, (int)resolverIndex){
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ String val$inetHost;
            final /* synthetic */ int val$resolverIndex;
            final /* synthetic */ CompositeNameResolver this$0;
            {
                this.this$0 = this$0;
                this.val$promise = promise;
                this.val$inetHost = string;
                this.val$resolverIndex = n;
            }

            public void operationComplete(Future<List<T>> future) throws Exception {
                if (future.isSuccess()) {
                    this.val$promise.setSuccess(future.getNow());
                    return;
                }
                CompositeNameResolver.access$100((CompositeNameResolver)this.this$0, (String)this.val$inetHost, (Promise)this.val$promise, (int)(this.val$resolverIndex + 1), (Throwable)future.cause());
            }
        });
    }

    static /* synthetic */ void access$000(CompositeNameResolver x0, String x1, Promise x2, int x3, Throwable x4) throws Exception {
        x0.doResolveRec((String)x1, x2, (int)x3, (Throwable)x4);
    }

    static /* synthetic */ void access$100(CompositeNameResolver x0, String x1, Promise x2, int x3, Throwable x4) throws Exception {
        x0.doResolveAllRec((String)x1, x2, (int)x3, (Throwable)x4);
    }
}

