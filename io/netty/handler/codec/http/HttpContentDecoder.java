/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder
extends MessageToMessageDecoder<HttpObject> {
    static final String IDENTITY = HttpHeaderValues.IDENTITY.toString();
    protected ChannelHandlerContext ctx;
    private EmbeddedChannel decoder;
    private boolean continueResponse;
    private boolean needRead = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        try {
            if (msg instanceof HttpResponse && ((HttpResponse)msg).status().code() == 100) {
                if (!(msg instanceof LastHttpContent)) {
                    this.continueResponse = true;
                }
                out.add((Object)ReferenceCountUtil.retain(msg));
                return;
            }
            if (this.continueResponse) {
                if (msg instanceof LastHttpContent) {
                    this.continueResponse = false;
                }
                out.add((Object)ReferenceCountUtil.retain(msg));
                return;
            }
            if (msg instanceof HttpMessage) {
                String targetContentEncoding;
                this.cleanup();
                HttpMessage message = (HttpMessage)msg;
                HttpHeaders headers = message.headers();
                String contentEncoding = headers.get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
                contentEncoding = contentEncoding != null ? contentEncoding.trim() : IDENTITY;
                this.decoder = this.newContentDecoder((String)contentEncoding);
                if (this.decoder == null) {
                    if (message instanceof HttpContent) {
                        ((HttpContent)((Object)message)).retain();
                    }
                    out.add((Object)message);
                    return;
                }
                if (headers.contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH)) {
                    headers.remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
                    headers.set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
                }
                if (HttpHeaderValues.IDENTITY.contentEquals((CharSequence)(targetContentEncoding = this.getTargetContentEncoding((String)contentEncoding)))) {
                    headers.remove((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
                } else {
                    headers.set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)targetContentEncoding);
                }
                if (message instanceof HttpContent) {
                    DefaultHttpMessage copy;
                    if (message instanceof HttpRequest) {
                        HttpRequest r = (HttpRequest)message;
                        copy = new DefaultHttpRequest((HttpVersion)r.protocolVersion(), (HttpMethod)r.method(), (String)r.uri());
                    } else {
                        if (!(message instanceof HttpResponse)) throw new CodecException((String)("Object of class " + message.getClass().getName() + " is not an HttpRequest or HttpResponse"));
                        HttpResponse r = (HttpResponse)message;
                        copy = new DefaultHttpResponse((HttpVersion)r.protocolVersion(), (HttpResponseStatus)r.status());
                    }
                    copy.headers().set((HttpHeaders)message.headers());
                    copy.setDecoderResult((DecoderResult)message.decoderResult());
                    out.add((Object)copy);
                } else {
                    out.add((Object)message);
                }
            }
            if (!(msg instanceof HttpContent)) return;
            HttpContent c = (HttpContent)msg;
            if (this.decoder == null) {
                out.add((Object)c.retain());
                return;
            }
            this.decodeContent((HttpContent)c, out);
            return;
        }
        finally {
            this.needRead = out.isEmpty();
        }
    }

    private void decodeContent(HttpContent c, List<Object> out) {
        ByteBuf content = c.content();
        this.decode((ByteBuf)content, out);
        if (!(c instanceof LastHttpContent)) return;
        this.finishDecode(out);
        LastHttpContent last = (LastHttpContent)c;
        HttpHeaders headers = last.trailingHeaders();
        if (headers.isEmpty()) {
            out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
            return;
        }
        out.add((Object)new ComposedLastHttpContent((HttpHeaders)headers, (DecoderResult)DecoderResult.SUCCESS));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        boolean needRead = this.needRead;
        this.needRead = true;
        try {
            ctx.fireChannelReadComplete();
            return;
        }
        finally {
            if (needRead && !ctx.channel().config().isAutoRead()) {
                ctx.read();
            }
        }
    }

    protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

    protected String getTargetContentEncoding(String contentEncoding) throws Exception {
        return IDENTITY;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely((ChannelHandlerContext)ctx);
        super.handlerRemoved((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely((ChannelHandlerContext)ctx);
        super.channelInactive((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded((ChannelHandlerContext)ctx);
    }

    private void cleanup() {
        if (this.decoder == null) return;
        this.decoder.finishAndReleaseAll();
        this.decoder = null;
    }

    private void cleanupSafely(ChannelHandlerContext ctx) {
        try {
            this.cleanup();
            return;
        }
        catch (Throwable cause) {
            ctx.fireExceptionCaught((Throwable)cause);
        }
    }

    private void decode(ByteBuf in, List<Object> out) {
        this.decoder.writeInbound((Object[])new Object[]{in.retain()});
        this.fetchDecoderOutput(out);
    }

    private void finishDecode(List<Object> out) {
        if (this.decoder.finish()) {
            this.fetchDecoderOutput(out);
        }
        this.decoder = null;
    }

    private void fetchDecoderOutput(List<Object> out) {
        ByteBuf buf;
        while ((buf = (ByteBuf)this.decoder.readInbound()) != null) {
            if (!buf.isReadable()) {
                buf.release();
                continue;
            }
            out.add((Object)new DefaultHttpContent((ByteBuf)buf));
        }
        return;
    }
}

