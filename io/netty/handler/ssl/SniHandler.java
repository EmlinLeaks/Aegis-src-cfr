/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.AbstractSniHandler;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AsyncMapping;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.SSLEngine;

public class SniHandler
extends AbstractSniHandler<SslContext> {
    private static final Selection EMPTY_SELECTION = new Selection(null, null);
    protected final AsyncMapping<String, SslContext> mapping;
    private volatile Selection selection = EMPTY_SELECTION;

    public SniHandler(Mapping<? super String, ? extends SslContext> mapping) {
        this((AsyncMapping<? super String, ? extends SslContext>)new AsyncMappingAdapter(mapping, null));
    }

    public SniHandler(DomainNameMapping<? extends SslContext> mapping) {
        this(mapping);
    }

    public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping) {
        this.mapping = ObjectUtil.checkNotNull(mapping, (String)"mapping");
    }

    public String hostname() {
        return this.selection.hostname;
    }

    public SslContext sslContext() {
        return this.selection.context;
    }

    @Override
    protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname) throws Exception {
        return this.mapping.map((String)hostname, ctx.executor().newPromise());
    }

    @Override
    protected final void onLookupComplete(ChannelHandlerContext ctx, String hostname, Future<SslContext> future) throws Exception {
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (!(cause instanceof Error)) throw new DecoderException((String)("failed to get the SslContext for " + hostname), (Throwable)cause);
            throw (Error)cause;
        }
        SslContext sslContext = future.getNow();
        this.selection = new Selection((SslContext)sslContext, (String)hostname);
        try {
            this.replaceHandler((ChannelHandlerContext)ctx, (String)hostname, (SslContext)sslContext);
            return;
        }
        catch (Throwable cause) {
            this.selection = EMPTY_SELECTION;
            PlatformDependent.throwException((Throwable)cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext) throws Exception {
        SslHandler sslHandler = null;
        try {
            sslHandler = this.newSslHandler((SslContext)sslContext, (ByteBufAllocator)ctx.alloc());
            ctx.pipeline().replace((ChannelHandler)this, (String)SslHandler.class.getName(), (ChannelHandler)sslHandler);
            sslHandler = null;
            return;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease((Object)sslHandler.engine());
            }
        }
    }

    protected SslHandler newSslHandler(SslContext context, ByteBufAllocator allocator) {
        return context.newHandler((ByteBufAllocator)allocator);
    }
}

