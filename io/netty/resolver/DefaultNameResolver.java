/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.InetNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.SocketUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class DefaultNameResolver
extends InetNameResolver {
    public DefaultNameResolver(EventExecutor executor) {
        super((EventExecutor)executor);
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        try {
            promise.setSuccess((InetAddress)SocketUtils.addressByName((String)inetHost));
            return;
        }
        catch (UnknownHostException e) {
            promise.setFailure((Throwable)e);
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        try {
            promise.setSuccess(Arrays.asList(SocketUtils.allAddressesByName((String)inetHost)));
            return;
        }
        catch (UnknownHostException e) {
            promise.setFailure((Throwable)e);
        }
    }
}

