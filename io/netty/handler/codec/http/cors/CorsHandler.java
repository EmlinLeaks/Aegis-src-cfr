/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CorsHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
    private static final String ANY_ORIGIN = "*";
    private static final String NULL_ORIGIN = "null";
    private CorsConfig config;
    private HttpRequest request;
    private final List<CorsConfig> configList;
    private boolean isShortCircuit;

    public CorsHandler(CorsConfig config) {
        this(Collections.singletonList(ObjectUtil.checkNotNull(config, (String)"config")), (boolean)config.isShortCircuit());
    }

    public CorsHandler(List<CorsConfig> configList, boolean isShortCircuit) {
        ObjectUtil.checkNonEmpty(configList, (String)"configList");
        this.configList = configList;
        this.isShortCircuit = isShortCircuit;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest)msg;
            String origin = this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
            this.config = this.getForOrigin((String)origin);
            if (CorsHandler.isPreflightRequest((HttpRequest)this.request)) {
                this.handlePreflight((ChannelHandlerContext)ctx, (HttpRequest)this.request);
                return;
            }
            if (this.isShortCircuit && origin != null && this.config == null) {
                CorsHandler.forbidden((ChannelHandlerContext)ctx, (HttpRequest)this.request);
                return;
            }
        }
        ctx.fireChannelRead((Object)msg);
    }

    private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse((HttpVersion)request.protocolVersion(), (HttpResponseStatus)HttpResponseStatus.OK, (boolean)true, (boolean)true);
        if (this.setOrigin((HttpResponse)response)) {
            this.setAllowMethods((HttpResponse)response);
            this.setAllowHeaders((HttpResponse)response);
            this.setAllowCredentials((HttpResponse)response);
            this.setMaxAge((HttpResponse)response);
            this.setPreflightHeaders((HttpResponse)response);
        }
        if (!response.headers().contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH)) {
            response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        }
        ReferenceCountUtil.release((Object)request);
        CorsHandler.respond((ChannelHandlerContext)ctx, (HttpRequest)request, (HttpResponse)response);
    }

    private void setPreflightHeaders(HttpResponse response) {
        response.headers().add((HttpHeaders)this.config.preflightResponseHeaders());
    }

    private CorsConfig getForOrigin(String requestOrigin) {
        CorsConfig corsConfig;
        Iterator<CorsConfig> iterator = this.configList.iterator();
        do {
            if (!iterator.hasNext()) return null;
            corsConfig = iterator.next();
            if (corsConfig.isAnyOriginSupported()) {
                return corsConfig;
            }
            if (corsConfig.origins().contains((Object)requestOrigin)) {
                return corsConfig;
            }
            if (corsConfig.isNullOriginAllowed()) return corsConfig;
        } while (!NULL_ORIGIN.equals((Object)requestOrigin));
        return corsConfig;
    }

    private boolean setOrigin(HttpResponse response) {
        String origin = this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
        if (origin == null) return false;
        if (this.config == null) return false;
        if (NULL_ORIGIN.equals((Object)origin) && this.config.isNullOriginAllowed()) {
            CorsHandler.setNullOrigin((HttpResponse)response);
            return true;
        }
        if (this.config.isAnyOriginSupported()) {
            if (this.config.isCredentialsAllowed()) {
                this.echoRequestOrigin((HttpResponse)response);
                CorsHandler.setVaryHeader((HttpResponse)response);
                return true;
            }
            CorsHandler.setAnyOrigin((HttpResponse)response);
            return true;
        }
        if (this.config.origins().contains((Object)origin)) {
            CorsHandler.setOrigin((HttpResponse)response, (String)origin);
            CorsHandler.setVaryHeader((HttpResponse)response);
            return true;
        }
        logger.debug((String)"Request origin [{}]] was not among the configured origins [{}]", (Object)origin, this.config.origins());
        return false;
    }

    private void echoRequestOrigin(HttpResponse response) {
        CorsHandler.setOrigin((HttpResponse)response, (String)this.request.headers().get((CharSequence)HttpHeaderNames.ORIGIN));
    }

    private static void setVaryHeader(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.VARY, (Object)HttpHeaderNames.ORIGIN);
    }

    private static void setAnyOrigin(HttpResponse response) {
        CorsHandler.setOrigin((HttpResponse)response, (String)ANY_ORIGIN);
    }

    private static void setNullOrigin(HttpResponse response) {
        CorsHandler.setOrigin((HttpResponse)response, (String)NULL_ORIGIN);
    }

    private static void setOrigin(HttpResponse response, String origin) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, (Object)origin);
    }

    private void setAllowCredentials(HttpResponse response) {
        if (!this.config.isCredentialsAllowed()) return;
        if (response.headers().get((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals((Object)ANY_ORIGIN)) return;
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)"true");
    }

    private static boolean isPreflightRequest(HttpRequest request) {
        HttpHeaders headers = request.headers();
        if (!HttpMethod.OPTIONS.equals((Object)request.method())) return false;
        if (!headers.contains((CharSequence)HttpHeaderNames.ORIGIN)) return false;
        if (!headers.contains((CharSequence)HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD)) return false;
        return true;
    }

    private void setExposeHeaders(HttpResponse response) {
        if (this.config.exposedHeaders().isEmpty()) return;
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, this.config.exposedHeaders());
    }

    private void setAllowMethods(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, this.config.allowedRequestMethods());
    }

    private void setAllowHeaders(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, this.config.allowedRequestHeaders());
    }

    private void setMaxAge(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, (Object)Long.valueOf((long)this.config.maxAge()));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HttpResponse response;
        if (this.config != null && this.config.isCorsSupportEnabled() && msg instanceof HttpResponse && this.setOrigin((HttpResponse)(response = (HttpResponse)msg))) {
            this.setAllowCredentials((HttpResponse)response);
            this.setExposeHeaders((HttpResponse)response);
        }
        ctx.write((Object)msg, (ChannelPromise)promise);
    }

    private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse((HttpVersion)request.protocolVersion(), (HttpResponseStatus)HttpResponseStatus.FORBIDDEN, (ByteBuf)ctx.alloc().buffer((int)0));
        response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        ReferenceCountUtil.release((Object)request);
        CorsHandler.respond((ChannelHandlerContext)ctx, (HttpRequest)request, (HttpResponse)response);
    }

    private static void respond(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive((HttpMessage)request);
        HttpUtil.setKeepAlive((HttpMessage)response, (boolean)keepAlive);
        ChannelFuture future = ctx.writeAndFlush((Object)response);
        if (keepAlive) return;
        future.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
    }
}

