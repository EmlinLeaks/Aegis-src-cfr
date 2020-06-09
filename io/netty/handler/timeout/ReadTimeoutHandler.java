/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.timeout;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import java.util.concurrent.TimeUnit;

public class ReadTimeoutHandler
extends IdleStateHandler {
    private boolean closed;

    public ReadTimeoutHandler(int timeoutSeconds) {
        this((long)((long)timeoutSeconds), (TimeUnit)TimeUnit.SECONDS);
    }

    public ReadTimeoutHandler(long timeout, TimeUnit unit) {
        super((long)timeout, (long)0L, (long)0L, (TimeUnit)unit);
    }

    @Override
    protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        assert (evt.state() == IdleState.READER_IDLE);
        this.readTimedOut((ChannelHandlerContext)ctx);
    }

    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        if (this.closed) return;
        ctx.fireExceptionCaught((Throwable)ReadTimeoutException.INSTANCE);
        ctx.close();
        this.closed = true;
    }
}

