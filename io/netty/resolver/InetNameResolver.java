/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.util.concurrent.EventExecutor;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class InetNameResolver
extends SimpleNameResolver<InetAddress> {
    private volatile AddressResolver<InetSocketAddress> addressResolver;

    protected InetNameResolver(EventExecutor executor) {
        super((EventExecutor)executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AddressResolver<InetSocketAddress> asAddressResolver() {
        InetSocketAddressResolver result = this.addressResolver;
        if (result != null) return result;
        InetNameResolver inetNameResolver = this;
        // MONITORENTER : inetNameResolver
        result = this.addressResolver;
        if (result == null) {
            this.addressResolver = result = new InetSocketAddressResolver((EventExecutor)this.executor(), (NameResolver<InetAddress>)this);
        }
        // MONITOREXIT : inetNameResolver
        return result;
    }
}

