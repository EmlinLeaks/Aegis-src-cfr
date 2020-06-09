/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecoder;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder
extends MessageToMessageCodec<HttpRequest, HttpObject> {
    private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
    private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
    private static final int CONTINUE_CODE = HttpResponseStatus.CONTINUE.code();
    private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque<CharSequence>();
    private EmbeddedChannel encoder;
    private State state = State.AWAIT_HEADERS;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (msg instanceof HttpContent) return true;
        if (msg instanceof HttpResponse) return true;
        return false;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
        acceptEncodingHeaders = msg.headers().getAll((CharSequence)HttpHeaderNames.ACCEPT_ENCODING);
        switch (acceptEncodingHeaders.size()) {
            case 0: {
                acceptEncoding = HttpContentDecoder.IDENTITY;
                ** break;
            }
            case 1: {
                acceptEncoding = (CharSequence)acceptEncodingHeaders.get((int)0);
                ** break;
            }
        }
        acceptEncoding = StringUtil.join((CharSequence)",", acceptEncodingHeaders);
lbl10: // 3 sources:
        method = msg.method();
        if (HttpMethod.HEAD.equals((Object)method)) {
            acceptEncoding = HttpContentEncoder.ZERO_LENGTH_HEAD;
        } else if (HttpMethod.CONNECT.equals((Object)method)) {
            acceptEncoding = HttpContentEncoder.ZERO_LENGTH_CONNECT;
        }
        this.acceptEncodingQueue.add((CharSequence)acceptEncoding);
        out.add((Object)ReferenceCountUtil.retain(msg));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        boolean isFull = msg instanceof HttpResponse && msg instanceof LastHttpContent;
        switch (1.$SwitchMap$io$netty$handler$codec$http$HttpContentEncoder$State[this.state.ordinal()]) {
            case 1: {
                CharSequence acceptEncoding;
                HttpContentEncoder.ensureHeaders((HttpObject)msg);
                assert (this.encoder == null);
                HttpResponse res = (HttpResponse)msg;
                int code = res.status().code();
                if (code == CONTINUE_CODE) {
                    acceptEncoding = null;
                } else {
                    acceptEncoding = this.acceptEncodingQueue.poll();
                    if (acceptEncoding == null) {
                        throw new IllegalStateException((String)"cannot send more responses than requests");
                    }
                }
                if (HttpContentEncoder.isPassthru((HttpVersion)res.protocolVersion(), (int)code, (CharSequence)acceptEncoding)) {
                    if (isFull) {
                        out.add((Object)ReferenceCountUtil.retain(res));
                        return;
                    }
                    out.add((Object)res);
                    this.state = State.PASS_THROUGH;
                    return;
                }
                if (isFull && !((ByteBufHolder)((Object)res)).content().isReadable()) {
                    out.add((Object)ReferenceCountUtil.retain(res));
                    return;
                }
                Result result = this.beginEncode((HttpResponse)res, (String)acceptEncoding.toString());
                if (result == null) {
                    if (isFull) {
                        out.add((Object)ReferenceCountUtil.retain(res));
                        return;
                    }
                    out.add((Object)res);
                    this.state = State.PASS_THROUGH;
                    return;
                }
                this.encoder = result.contentEncoder();
                res.headers().set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)result.targetContentEncoding());
                if (isFull) {
                    DefaultHttpResponse newRes = new DefaultHttpResponse((HttpVersion)res.protocolVersion(), (HttpResponseStatus)res.status());
                    newRes.headers().set((HttpHeaders)res.headers());
                    out.add((Object)newRes);
                    HttpContentEncoder.ensureContent((HttpObject)res);
                    this.encodeFullResponse((HttpResponse)newRes, (HttpContent)((HttpContent)((Object)res)), out);
                    return;
                }
                res.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
                res.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
                out.add((Object)res);
                this.state = State.AWAIT_CONTENT;
                if (!(msg instanceof HttpContent)) {
                    return;
                }
            }
            case 2: {
                HttpContentEncoder.ensureContent((HttpObject)msg);
                if (!this.encodeContent((HttpContent)((HttpContent)msg), out)) return;
                this.state = State.AWAIT_HEADERS;
                return;
            }
            case 3: {
                HttpContentEncoder.ensureContent((HttpObject)msg);
                out.add((Object)ReferenceCountUtil.retain(msg));
                if (!(msg instanceof LastHttpContent)) return;
                this.state = State.AWAIT_HEADERS;
            }
        }
    }

    private void encodeFullResponse(HttpResponse newRes, HttpContent content, List<Object> out) {
        int existingMessages = out.size();
        this.encodeContent((HttpContent)content, out);
        if (!HttpUtil.isContentLengthSet((HttpMessage)newRes)) {
            newRes.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            return;
        }
        int messageSize = 0;
        int i = existingMessages;
        do {
            if (i >= out.size()) {
                HttpUtil.setContentLength((HttpMessage)newRes, (long)((long)messageSize));
                return;
            }
            Object item = out.get((int)i);
            if (item instanceof HttpContent) {
                messageSize += ((HttpContent)item).content().readableBytes();
            }
            ++i;
        } while (true);
    }

    private static boolean isPassthru(HttpVersion version, int code, CharSequence httpMethod) {
        if (code < 200) return true;
        if (code == 204) return true;
        if (code == 304) return true;
        if (httpMethod == ZERO_LENGTH_HEAD) return true;
        if (httpMethod == ZERO_LENGTH_CONNECT) {
            if (code == 200) return true;
        }
        if (version == HttpVersion.HTTP_1_0) return true;
        return false;
    }

    private static void ensureHeaders(HttpObject msg) {
        if (msg instanceof HttpResponse) return;
        throw new IllegalStateException((String)("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')'));
    }

    private static void ensureContent(HttpObject msg) {
        if (msg instanceof HttpContent) return;
        throw new IllegalStateException((String)("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')'));
    }

    private boolean encodeContent(HttpContent c, List<Object> out) {
        ByteBuf content = c.content();
        this.encode((ByteBuf)content, out);
        if (!(c instanceof LastHttpContent)) return false;
        this.finishEncode(out);
        LastHttpContent last = (LastHttpContent)c;
        HttpHeaders headers = last.trailingHeaders();
        if (headers.isEmpty()) {
            out.add((Object)LastHttpContent.EMPTY_LAST_CONTENT);
            return true;
        }
        out.add((Object)new ComposedLastHttpContent((HttpHeaders)headers, (DecoderResult)DecoderResult.SUCCESS));
        return true;
    }

    protected abstract Result beginEncode(HttpResponse var1, String var2) throws Exception;

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

    private void cleanup() {
        if (this.encoder == null) return;
        this.encoder.finishAndReleaseAll();
        this.encoder = null;
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

    private void encode(ByteBuf in, List<Object> out) {
        this.encoder.writeOutbound((Object[])new Object[]{in.retain()});
        this.fetchEncoderOutput(out);
    }

    private void finishEncode(List<Object> out) {
        if (this.encoder.finish()) {
            this.fetchEncoderOutput(out);
        }
        this.encoder = null;
    }

    private void fetchEncoderOutput(List<Object> out) {
        ByteBuf buf;
        while ((buf = (ByteBuf)this.encoder.readOutbound()) != null) {
            if (!buf.isReadable()) {
                buf.release();
                continue;
            }
            out.add((Object)new DefaultHttpContent((ByteBuf)buf));
        }
        return;
    }
}

