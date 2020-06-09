/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import java.util.ArrayDeque;
import java.util.Queue;

public final class HttpServerCodec
extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder>
implements HttpServerUpgradeHandler.SourceCodec {
    private final Queue<HttpMethod> queue = new ArrayDeque<HttpMethod>();

    public HttpServerCodec() {
        this((int)4096, (int)8192, (int)8192);
    }

    public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        this.init(new HttpServerRequestDecoder((HttpServerCodec)this, (int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize), new HttpServerResponseEncoder((HttpServerCodec)this, null));
    }

    public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        this.init(new HttpServerRequestDecoder((HttpServerCodec)this, (int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)validateHeaders), new HttpServerResponseEncoder((HttpServerCodec)this, null));
    }

    public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
        this.init(new HttpServerRequestDecoder((HttpServerCodec)this, (int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)validateHeaders, (int)initialBufferSize), new HttpServerResponseEncoder((HttpServerCodec)this, null));
    }

    @Override
    public void upgradeFrom(ChannelHandlerContext ctx) {
        ctx.pipeline().remove((ChannelHandler)this);
    }

    static /* synthetic */ Queue access$100(HttpServerCodec x0) {
        return x0.queue;
    }
}

