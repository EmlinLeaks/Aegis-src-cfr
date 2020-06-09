/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

abstract class WebSocketProtocolHandler
extends MessageToMessageDecoder<WebSocketFrame> {
    private final boolean dropPongFrames;

    WebSocketProtocolHandler() {
        this((boolean)true);
    }

    WebSocketProtocolHandler(boolean dropPongFrames) {
        this.dropPongFrames = dropPongFrames;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.channel().writeAndFlush((Object)new PongWebSocketFrame((ByteBuf)frame.content()));
            WebSocketProtocolHandler.readIfNeeded((ChannelHandlerContext)ctx);
            return;
        }
        if (frame instanceof PongWebSocketFrame && this.dropPongFrames) {
            WebSocketProtocolHandler.readIfNeeded((ChannelHandlerContext)ctx);
            return;
        }
        out.add((Object)frame.retain());
    }

    private static void readIfNeeded(ChannelHandlerContext ctx) {
        if (ctx.channel().config().isAutoRead()) return;
        ctx.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught((Throwable)cause);
        ctx.close();
    }
}

