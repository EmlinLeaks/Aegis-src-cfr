/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpServerUpgradeHandler
extends HttpObjectAggregator {
    private final SourceCodec sourceCodec;
    private final UpgradeCodecFactory upgradeCodecFactory;
    private boolean handlingUpgrade;

    public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory) {
        this((SourceCodec)sourceCodec, (UpgradeCodecFactory)upgradeCodecFactory, (int)0);
    }

    public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory, int maxContentLength) {
        super((int)maxContentLength);
        this.sourceCodec = ObjectUtil.checkNotNull(sourceCodec, (String)"sourceCodec");
        this.upgradeCodecFactory = ObjectUtil.checkNotNull(upgradeCodecFactory, (String)"upgradeCodecFactory");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        FullHttpRequest fullRequest;
        this.handlingUpgrade |= HttpServerUpgradeHandler.isUpgradeRequest((HttpObject)msg);
        if (!this.handlingUpgrade) {
            ReferenceCountUtil.retain(msg);
            out.add((Object)msg);
            return;
        }
        if (msg instanceof FullHttpRequest) {
            fullRequest = (FullHttpRequest)msg;
            ReferenceCountUtil.retain(msg);
            out.add((Object)msg);
        } else {
            super.decode((ChannelHandlerContext)ctx, msg, out);
            if (out.isEmpty()) {
                return;
            }
            assert (out.size() == 1);
            this.handlingUpgrade = false;
            fullRequest = (FullHttpRequest)out.get((int)0);
        }
        if (!this.upgrade((ChannelHandlerContext)ctx, (FullHttpRequest)fullRequest)) return;
        out.clear();
    }

    private static boolean isUpgradeRequest(HttpObject msg) {
        if (!(msg instanceof HttpRequest)) return false;
        if (((HttpRequest)msg).headers().get((CharSequence)HttpHeaderNames.UPGRADE) == null) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean upgrade(ChannelHandlerContext ctx, FullHttpRequest request) {
        List<CharSequence> requestedProtocols = HttpServerUpgradeHandler.splitHeader((CharSequence)request.headers().get((CharSequence)HttpHeaderNames.UPGRADE));
        int numRequestedProtocols = requestedProtocols.size();
        UpgradeCodec upgradeCodec = null;
        CharSequence upgradeProtocol = null;
        for (int i = 0; i < numRequestedProtocols; ++i) {
            CharSequence p = requestedProtocols.get((int)i);
            UpgradeCodec c = this.upgradeCodecFactory.newUpgradeCodec((CharSequence)p);
            if (c == null) continue;
            upgradeProtocol = p;
            upgradeCodec = c;
            break;
        }
        if (upgradeCodec == null) {
            return false;
        }
        List<String> connectionHeaderValues = request.headers().getAll((CharSequence)HttpHeaderNames.CONNECTION);
        if (connectionHeaderValues == null) {
            return false;
        }
        StringBuilder concatenatedConnectionValue = new StringBuilder((int)(connectionHeaderValues.size() * 10));
        for (CharSequence connectionHeaderValue : connectionHeaderValues) {
            concatenatedConnectionValue.append((CharSequence)connectionHeaderValue).append((char)',');
        }
        concatenatedConnectionValue.setLength((int)(concatenatedConnectionValue.length() - 1));
        Collection<CharSequence> requiredHeaders = upgradeCodec.requiredUpgradeHeaders();
        List<CharSequence> values = HttpServerUpgradeHandler.splitHeader((CharSequence)concatenatedConnectionValue);
        if (!AsciiString.containsContentEqualsIgnoreCase(values, (CharSequence)HttpHeaderNames.UPGRADE)) return false;
        if (!AsciiString.containsAllContentEqualsIgnoreCase(values, requiredHeaders)) {
            return false;
        }
        for (CharSequence requiredHeader : requiredHeaders) {
            if (request.headers().contains((CharSequence)requiredHeader)) continue;
            return false;
        }
        FullHttpResponse upgradeResponse = HttpServerUpgradeHandler.createUpgradeResponse((CharSequence)upgradeProtocol);
        if (!upgradeCodec.prepareUpgradeResponse((ChannelHandlerContext)ctx, (FullHttpRequest)request, (HttpHeaders)upgradeResponse.headers())) {
            return false;
        }
        UpgradeEvent event = new UpgradeEvent((CharSequence)upgradeProtocol, (FullHttpRequest)request);
        try {
            ChannelFuture writeComplete = ctx.writeAndFlush((Object)upgradeResponse);
            this.sourceCodec.upgradeFrom((ChannelHandlerContext)ctx);
            upgradeCodec.upgradeTo((ChannelHandlerContext)ctx, (FullHttpRequest)request);
            ctx.pipeline().remove((ChannelHandler)this);
            ctx.fireUserEventTriggered((Object)event.retain());
            writeComplete.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
            return true;
        }
        finally {
            event.release();
        }
    }

    private static FullHttpResponse createUpgradeResponse(CharSequence upgradeProtocol) {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.SWITCHING_PROTOCOLS, (ByteBuf)Unpooled.EMPTY_BUFFER, (boolean)false);
        res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
        res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)upgradeProtocol);
        return res;
    }

    private static List<CharSequence> splitHeader(CharSequence header) {
        StringBuilder builder = new StringBuilder((int)header.length());
        ArrayList<CharSequence> protocols = new ArrayList<CharSequence>((int)4);
        int i = 0;
        do {
            if (i >= header.length()) {
                if (builder.length() <= 0) return protocols;
                protocols.add((CharSequence)builder.toString());
                return protocols;
            }
            char c = header.charAt((int)i);
            if (!Character.isWhitespace((char)c)) {
                if (c == ',') {
                    protocols.add((CharSequence)builder.toString());
                    builder.setLength((int)0);
                } else {
                    builder.append((char)c);
                }
            }
            ++i;
        } while (true);
    }
}

