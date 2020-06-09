/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class SimpleUserEventChannelHandler<I>
extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final boolean autoRelease;

    protected SimpleUserEventChannelHandler() {
        this((boolean)true);
    }

    protected SimpleUserEventChannelHandler(boolean autoRelease) {
        this.matcher = TypeParameterMatcher.find((Object)this, SimpleUserEventChannelHandler.class, (String)"I");
        this.autoRelease = autoRelease;
    }

    protected SimpleUserEventChannelHandler(Class<? extends I> eventType) {
        this(eventType, (boolean)true);
    }

    protected SimpleUserEventChannelHandler(Class<? extends I> eventType, boolean autoRelease) {
        this.matcher = TypeParameterMatcher.get(eventType);
        this.autoRelease = autoRelease;
    }

    protected boolean acceptEvent(Object evt) throws Exception {
        return this.matcher.match((Object)evt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean release = true;
        try {
            if (this.acceptEvent((Object)evt)) {
                Object ievt = evt;
                this.eventReceived((ChannelHandlerContext)ctx, ievt);
                return;
            }
            release = false;
            ctx.fireUserEventTriggered((Object)evt);
            return;
        }
        finally {
            if (this.autoRelease && release) {
                ReferenceCountUtil.release((Object)evt);
            }
        }
    }

    protected abstract void eventReceived(ChannelHandlerContext var1, I var2) throws Exception;
}

