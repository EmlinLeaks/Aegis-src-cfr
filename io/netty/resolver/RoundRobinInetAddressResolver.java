/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.InetNameResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.RoundRobinInetAddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.List;

public class RoundRobinInetAddressResolver
extends InetNameResolver {
    private final NameResolver<InetAddress> nameResolver;

    public RoundRobinInetAddressResolver(EventExecutor executor, NameResolver<InetAddress> nameResolver) {
        super((EventExecutor)executor);
        this.nameResolver = nameResolver;
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        this.nameResolver.resolveAll((String)inetHost).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>((RoundRobinInetAddressResolver)this, promise, (String)inetHost){
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ String val$inetHost;
            final /* synthetic */ RoundRobinInetAddressResolver this$0;
            {
                this.this$0 = this$0;
                this.val$promise = promise;
                this.val$inetHost = string;
            }

            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (!future.isSuccess()) {
                    this.val$promise.setFailure((java.lang.Throwable)future.cause());
                    return;
                }
                List<InetAddress> inetAddresses = future.getNow();
                int numAddresses = inetAddresses.size();
                if (numAddresses > 0) {
                    this.val$promise.setSuccess(inetAddresses.get((int)RoundRobinInetAddressResolver.access$000((int)numAddresses)));
                    return;
                }
                this.val$promise.setFailure((java.lang.Throwable)new java.net.UnknownHostException((String)this.val$inetHost));
            }
        });
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        this.nameResolver.resolveAll((String)inetHost).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>((RoundRobinInetAddressResolver)this, promise){
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ RoundRobinInetAddressResolver this$0;
            {
                this.this$0 = this$0;
                this.val$promise = promise;
            }

            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (!future.isSuccess()) {
                    this.val$promise.setFailure((java.lang.Throwable)future.cause());
                    return;
                }
                List<InetAddress> inetAddresses = future.getNow();
                if (!inetAddresses.isEmpty()) {
                    java.util.ArrayList<InetAddress> result = new java.util.ArrayList<InetAddress>(inetAddresses);
                    java.util.Collections.rotate(result, (int)RoundRobinInetAddressResolver.access$000((int)inetAddresses.size()));
                    this.val$promise.setSuccess(result);
                    return;
                }
                this.val$promise.setSuccess(inetAddresses);
            }
        });
    }

    private static int randomIndex(int numAddresses) {
        if (numAddresses == 1) {
            return 0;
        }
        int n = PlatformDependent.threadLocalRandom().nextInt((int)numAddresses);
        return n;
    }

    @Override
    public void close() {
        this.nameResolver.close();
    }

    static /* synthetic */ int access$000(int x0) {
        return RoundRobinInetAddressResolver.randomIndex((int)x0);
    }
}

