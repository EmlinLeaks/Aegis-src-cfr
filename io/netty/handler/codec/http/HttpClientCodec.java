/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public final class HttpClientCodec
extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder>
implements HttpClientUpgradeHandler.SourceCodec {
    private final Queue<HttpMethod> queue = new ArrayDeque<HttpMethod>();
    private final boolean parseHttpAfterConnectRequest;
    private boolean done;
    private final AtomicLong requestResponseCounter = new AtomicLong();
    private final boolean failOnMissingResponse;

    public HttpClientCodec() {
        this((int)4096, (int)8192, (int)8192, (boolean)false);
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)false);
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)failOnMissingResponse, (boolean)true);
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)failOnMissingResponse, (boolean)validateHeaders, (boolean)false);
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, boolean parseHttpAfterConnectRequest) {
        this.init(new Decoder((HttpClientCodec)this, (int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)validateHeaders), new Encoder((HttpClientCodec)this, null));
        this.failOnMissingResponse = failOnMissingResponse;
        this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize) {
        this((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)failOnMissingResponse, (boolean)validateHeaders, (int)initialBufferSize, (boolean)false);
    }

    public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize, boolean parseHttpAfterConnectRequest) {
        this.init(new Decoder((HttpClientCodec)this, (int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)validateHeaders, (int)initialBufferSize), new Encoder((HttpClientCodec)this, null));
        this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
        this.failOnMissingResponse = failOnMissingResponse;
    }

    @Override
    public void prepareUpgradeFrom(ChannelHandlerContext ctx) {
        ((Encoder)this.outboundHandler()).upgraded = true;
    }

    @Override
    public void upgradeFrom(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.remove((ChannelHandler)this);
    }

    public void setSingleDecode(boolean singleDecode) {
        ((HttpResponseDecoder)this.inboundHandler()).setSingleDecode((boolean)singleDecode);
    }

    public boolean isSingleDecode() {
        return ((HttpResponseDecoder)this.inboundHandler()).isSingleDecode();
    }

    static /* synthetic */ Queue access$100(HttpClientCodec x0) {
        return x0.queue;
    }

    static /* synthetic */ boolean access$200(HttpClientCodec x0) {
        return x0.failOnMissingResponse;
    }

    static /* synthetic */ boolean access$300(HttpClientCodec x0) {
        return x0.done;
    }

    static /* synthetic */ AtomicLong access$400(HttpClientCodec x0) {
        return x0.requestResponseCounter;
    }

    static /* synthetic */ boolean access$500(HttpClientCodec x0) {
        return x0.parseHttpAfterConnectRequest;
    }

    static /* synthetic */ boolean access$302(HttpClientCodec x0, boolean x1) {
        x0.done = x1;
        return x0.done;
    }
}

