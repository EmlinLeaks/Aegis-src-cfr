/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFutureListener
extends GenericFutureListener<ChannelFuture> {
    public static final ChannelFutureListener CLOSE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            future.channel().close();
        }
    };
    public static final ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) return;
            future.channel().close();
        }
    };
    public static final ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener(){

        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) return;
            future.channel().pipeline().fireExceptionCaught((java.lang.Throwable)future.cause());
        }
    };
}

