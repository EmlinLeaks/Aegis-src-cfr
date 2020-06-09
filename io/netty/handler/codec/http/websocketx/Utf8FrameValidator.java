/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8Validator;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Utf8FrameValidator
extends ChannelInboundHandlerAdapter {
    private int fragmentedFramesCount;
    private Utf8Validator utf8Validator;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame)msg;
            if (((WebSocketFrame)msg).isFinalFragment()) {
                if (!(frame instanceof PingWebSocketFrame)) {
                    this.fragmentedFramesCount = 0;
                    if (frame instanceof TextWebSocketFrame || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                        this.checkUTF8String((ByteBuf)frame.content());
                        this.utf8Validator.finish();
                    }
                }
            } else {
                if (this.fragmentedFramesCount == 0) {
                    if (frame instanceof TextWebSocketFrame) {
                        this.checkUTF8String((ByteBuf)frame.content());
                    }
                } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                    this.checkUTF8String((ByteBuf)frame.content());
                }
                ++this.fragmentedFramesCount;
            }
        }
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    private void checkUTF8String(ByteBuf buffer) {
        if (this.utf8Validator == null) {
            this.utf8Validator = new Utf8Validator();
        }
        this.utf8Validator.check((ByteBuf)buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CorruptedFrameException && ctx.channel().isOpen()) {
            ctx.writeAndFlush((Object)Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
        super.exceptionCaught((ChannelHandlerContext)ctx, (Throwable)cause);
    }
}

