/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AddressResolverGroup<T extends SocketAddress>
implements Closeable {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
    private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<EventExecutor, AddressResolver<T>>();

    protected AddressResolverGroup() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AddressResolver<T> getResolver(EventExecutor executor) {
        if (executor == null) {
            throw new NullPointerException((String)"executor");
        }
        if (executor.isShuttingDown()) {
            throw new IllegalStateException((String)"executor not accepting a task");
        }
        Map<EventExecutor, AddressResolver<T>> map = this.resolvers;
        // MONITORENTER : map
        AddressResolver<T> r = this.resolvers.get((Object)executor);
        if (r == null) {
            AddressResolver<T> newResolver;
            try {
                newResolver = this.newResolver((EventExecutor)executor);
            }
            catch (Exception e) {
                throw new IllegalStateException((String)"failed to create a new resolver", (Throwable)e);
            }
            this.resolvers.put((EventExecutor)executor, newResolver);
            executor.terminationFuture().addListener(new FutureListener<Object>((AddressResolverGroup)this, (EventExecutor)executor, newResolver){
                final /* synthetic */ EventExecutor val$executor;
                final /* synthetic */ AddressResolver val$newResolver;
                final /* synthetic */ AddressResolverGroup this$0;
                {
                    this.this$0 = this$0;
                    this.val$executor = eventExecutor;
                    this.val$newResolver = addressResolver;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void operationComplete(Future<Object> future) throws Exception {
                    Map map = AddressResolverGroup.access$000((AddressResolverGroup)this.this$0);
                    // MONITORENTER : map
                    AddressResolverGroup.access$000((AddressResolverGroup)this.this$0).remove((Object)this.val$executor);
                    // MONITOREXIT : map
                    this.val$newResolver.close();
                }
            });
            r = newResolver;
        }
        // MONITOREXIT : map
        return r;
    }

    protected abstract AddressResolver<T> newResolver(EventExecutor var1) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        AddressResolver[] arraddressResolver = this.resolvers;
        // MONITORENTER : arraddressResolver
        AddressResolver[] rArray = this.resolvers.values().toArray(new AddressResolver[0]);
        this.resolvers.clear();
        // MONITOREXIT : arraddressResolver
        arraddressResolver = rArray;
        int n = arraddressResolver.length;
        int n2 = 0;
        while (n2 < n) {
            AddressResolver r = arraddressResolver[n2];
            try {
                r.close();
            }
            catch (Throwable t) {
                logger.warn((String)"Failed to close a resolver:", (Throwable)t);
            }
            ++n2;
        }
    }

    static /* synthetic */ Map access$000(AddressResolverGroup x0) {
        return x0.resolvers;
    }
}

