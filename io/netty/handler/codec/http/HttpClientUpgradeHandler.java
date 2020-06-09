/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class HttpClientUpgradeHandler
extends HttpObjectAggregator
implements ChannelOutboundHandler {
    private final SourceCodec sourceCodec;
    private final UpgradeCodec upgradeCodec;
    private boolean upgradeRequested;

    public HttpClientUpgradeHandler(SourceCodec sourceCodec, UpgradeCodec upgradeCodec, int maxContentLength) {
        super((int)maxContentLength);
        if (sourceCodec == null) {
            throw new NullPointerException((String)"sourceCodec");
        }
        if (upgradeCodec == null) {
            throw new NullPointerException((String)"upgradeCodec");
        }
        this.sourceCodec = sourceCodec;
        this.upgradeCodec = upgradeCodec;
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close((ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister((ChannelPromise)promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            ctx.write((Object)msg, (ChannelPromise)promise);
            return;
        }
        if (this.upgradeRequested) {
            promise.setFailure((Throwable)new IllegalStateException((String)"Attempting to write HTTP request with upgrade in progress"));
            return;
        }
        this.upgradeRequested = true;
        this.setUpgradeRequestHeaders((ChannelHandlerContext)ctx, (HttpRequest)((HttpRequest)msg));
        ctx.write((Object)msg, (ChannelPromise)promise);
        ctx.fireUserEventTriggered((Object)((Object)UpgradeEvent.UPGRADE_ISSUED));
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        FullHttpResponse response = null;
        try {
            HttpResponse rep;
            if (!this.upgradeRequested) {
                throw new IllegalStateException((String)"Read HTTP response without requesting protocol switch");
            }
            if (msg instanceof HttpResponse && !HttpResponseStatus.SWITCHING_PROTOCOLS.equals((Object)(rep = (HttpResponse)msg).status())) {
                ctx.fireUserEventTriggered((Object)((Object)UpgradeEvent.UPGRADE_REJECTED));
                HttpClientUpgradeHandler.removeThisHandler((ChannelHandlerContext)ctx);
                ctx.fireChannelRead((Object)msg);
                return;
            }
            if (msg instanceof FullHttpResponse) {
                response = (FullHttpResponse)msg;
                response.retain();
                out.add((Object)response);
            } else {
                super.decode((ChannelHandlerContext)ctx, msg, out);
                if (out.isEmpty()) {
                    return;
                }
                assert (out.size() == 1);
                response = (FullHttpResponse)out.get((int)0);
            }
            String upgradeHeader = response.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
            if (upgradeHeader != null && !AsciiString.contentEqualsIgnoreCase((CharSequence)this.upgradeCodec.protocol(), (CharSequence)upgradeHeader)) {
                throw new IllegalStateException((String)("Switching Protocols response with unexpected UPGRADE protocol: " + upgradeHeader));
            }
            this.sourceCodec.prepareUpgradeFrom((ChannelHandlerContext)ctx);
            this.upgradeCodec.upgradeTo((ChannelHandlerContext)ctx, (FullHttpResponse)response);
            ctx.fireUserEventTriggered((Object)((Object)UpgradeEvent.UPGRADE_SUCCESSFUL));
            this.sourceCodec.upgradeFrom((ChannelHandlerContext)ctx);
            response.release();
            out.clear();
            HttpClientUpgradeHandler.removeThisHandler((ChannelHandlerContext)ctx);
            return;
        }
        catch (Throwable t) {
            ReferenceCountUtil.release(response);
            ctx.fireExceptionCaught((Throwable)t);
            HttpClientUpgradeHandler.removeThisHandler((ChannelHandlerContext)ctx);
        }
    }

    private static void removeThisHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().remove((String)ctx.name());
    }

    private void setUpgradeRequestHeaders(ChannelHandlerContext ctx, HttpRequest request) {
        request.headers().set((CharSequence)HttpHeaderNames.UPGRADE, (Object)this.upgradeCodec.protocol());
        LinkedHashSet<CharSequence> connectionParts = new LinkedHashSet<CharSequence>((int)2);
        connectionParts.addAll(this.upgradeCodec.setUpgradeHeaders((ChannelHandlerContext)ctx, (HttpRequest)request));
        StringBuilder builder = new StringBuilder();
        Iterator<E> iterator = connectionParts.iterator();
        do {
            if (!iterator.hasNext()) {
                builder.append((CharSequence)HttpHeaderValues.UPGRADE);
                request.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)builder.toString());
                return;
            }
            CharSequence part = (CharSequence)iterator.next();
            builder.append((CharSequence)part);
            builder.append((char)',');
        } while (true);
    }
}

