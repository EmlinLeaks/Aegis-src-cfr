/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.AbstractAddressResolver;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class InetSocketAddressResolver
extends AbstractAddressResolver<InetSocketAddress> {
    final NameResolver<InetAddress> nameResolver;

    public InetSocketAddressResolver(EventExecutor executor, NameResolver<InetAddress> nameResolver) {
        super((EventExecutor)executor, InetSocketAddress.class);
        this.nameResolver = nameResolver;
    }

    @Override
    protected boolean doIsResolved(InetSocketAddress address) {
        if (address.isUnresolved()) return false;
        return true;
    }

    @Override
    protected void doResolve(InetSocketAddress unresolvedAddress, Promise<InetSocketAddress> promise) throws Exception {
        this.nameResolver.resolve((String)unresolvedAddress.getHostName()).addListener((GenericFutureListener<Future<InetAddress>>)new FutureListener<InetAddress>((InetSocketAddressResolver)this, promise, (InetSocketAddress)unresolvedAddress){
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ InetSocketAddress val$unresolvedAddress;
            final /* synthetic */ InetSocketAddressResolver this$0;
            {
                this.this$0 = this$0;
                this.val$promise = promise;
                this.val$unresolvedAddress = inetSocketAddress;
            }

            public void operationComplete(Future<InetAddress> future) throws Exception {
                if (future.isSuccess()) {
                    this.val$promise.setSuccess(new InetSocketAddress((InetAddress)future.getNow(), (int)this.val$unresolvedAddress.getPort()));
                    return;
                }
                this.val$promise.setFailure((java.lang.Throwable)future.cause());
            }
        });
    }

    @Override
    protected void doResolveAll(InetSocketAddress unresolvedAddress, Promise<List<InetSocketAddress>> promise) throws Exception {
        this.nameResolver.resolveAll((String)unresolvedAddress.getHostName()).addListener((GenericFutureListener<Future<List<InetAddress>>>)new FutureListener<List<InetAddress>>((InetSocketAddressResolver)this, (InetSocketAddress)unresolvedAddress, promise){
            final /* synthetic */ InetSocketAddress val$unresolvedAddress;
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ InetSocketAddressResolver this$0;
            {
                this.this$0 = this$0;
                this.val$unresolvedAddress = inetSocketAddress;
                this.val$promise = promise;
            }

            public void operationComplete(Future<List<InetAddress>> future) throws Exception {
                if (!future.isSuccess()) {
                    this.val$promise.setFailure((java.lang.Throwable)future.cause());
                    return;
                }
                List<InetAddress> inetAddresses = future.getNow();
                java.util.ArrayList<InetSocketAddress> socketAddresses = new java.util.ArrayList<InetSocketAddress>((int)inetAddresses.size());
                java.util.Iterator<InetAddress> iterator = inetAddresses.iterator();
                do {
                    if (!iterator.hasNext()) {
                        this.val$promise.setSuccess(socketAddresses);
                        return;
                    }
                    InetAddress inetAddress = iterator.next();
                    socketAddresses.add(new InetSocketAddress((InetAddress)inetAddress, (int)this.val$unresolvedAddress.getPort()));
                } while (true);
            }
        });
    }

    @Override
    public void close() {
        this.nameResolver.close();
    }
}

