/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import javax.net.ssl.SSLEngine;

public class OptionalSslHandler
extends ByteToMessageDecoder {
    private final SslContext sslContext;

    public OptionalSslHandler(SslContext sslContext) {
        this.sslContext = ObjectUtil.checkNotNull(sslContext, (String)"sslContext");
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {
            return;
        }
        if (SslHandler.isEncrypted((ByteBuf)in)) {
            this.handleSsl((ChannelHandlerContext)context);
            return;
        }
        this.handleNonSsl((ChannelHandlerContext)context);
    }

    private void handleSsl(ChannelHandlerContext context) {
        SslHandler sslHandler = null;
        try {
            sslHandler = this.newSslHandler((ChannelHandlerContext)context, (SslContext)this.sslContext);
            context.pipeline().replace((ChannelHandler)this, (String)this.newSslHandlerName(), (ChannelHandler)sslHandler);
            sslHandler = null;
            return;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease((Object)sslHandler.engine());
            }
        }
    }

    private void handleNonSsl(ChannelHandlerContext context) {
        ChannelHandler handler = this.newNonSslHandler((ChannelHandlerContext)context);
        if (handler != null) {
            context.pipeline().replace((ChannelHandler)this, (String)this.newNonSslHandlerName(), (ChannelHandler)handler);
            return;
        }
        context.pipeline().remove((ChannelHandler)this);
    }

    protected String newSslHandlerName() {
        return null;
    }

    protected SslHandler newSslHandler(ChannelHandlerContext context, SslContext sslContext) {
        return sslContext.newHandler((ByteBufAllocator)context.alloc());
    }

    protected String newNonSslHandlerName() {
        return null;
    }

    protected ChannelHandler newNonSslHandler(ChannelHandlerContext context) {
        return null;
    }
}

