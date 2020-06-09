/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelHandler {
    public void handlerAdded(ChannelHandlerContext var1) throws Exception;

    public void handlerRemoved(ChannelHandlerContext var1) throws Exception;

    @Deprecated
    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception;
}

