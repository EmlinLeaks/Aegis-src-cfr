/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerMask;
import io.netty.util.internal.InternalThreadLocalMap;
import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class ChannelHandlerAdapter
implements ChannelHandler {
    boolean added;

    protected void ensureNotSharable() {
        if (!this.isSharable()) return;
        throw new IllegalStateException((String)("ChannelHandler " + this.getClass().getName() + " is not allowed to be shared"));
    }

    public boolean isSharable() {
        Class<?> clazz = this.getClass();
        Map<Class<?>, Boolean> cache = InternalThreadLocalMap.get().handlerSharableCache();
        Boolean sharable = cache.get(clazz);
        if (sharable != null) return sharable.booleanValue();
        sharable = Boolean.valueOf((boolean)clazz.isAnnotationPresent(ChannelHandler.Sharable.class));
        cache.put(clazz, (Boolean)sharable);
        return sharable.booleanValue();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }

    @ChannelHandlerMask.Skip
    @Deprecated
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught((Throwable)cause);
    }
}

