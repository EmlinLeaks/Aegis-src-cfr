/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpdyHttpDecoder
extends MessageToMessageDecoder<SpdyFrame> {
    private final boolean validateHeaders;
    private final int spdyVersion;
    private final int maxContentLength;
    private final Map<Integer, FullHttpMessage> messageMap;

    public SpdyHttpDecoder(SpdyVersion version, int maxContentLength) {
        this((SpdyVersion)version, (int)maxContentLength, new HashMap<Integer, FullHttpMessage>(), (boolean)true);
    }

    public SpdyHttpDecoder(SpdyVersion version, int maxContentLength, boolean validateHeaders) {
        this((SpdyVersion)version, (int)maxContentLength, new HashMap<Integer, FullHttpMessage>(), (boolean)validateHeaders);
    }

    protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap) {
        this((SpdyVersion)version, (int)maxContentLength, messageMap, (boolean)true);
    }

    protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap, boolean validateHeaders) {
        if (version == null) {
            throw new NullPointerException((String)"version");
        }
        ObjectUtil.checkPositive((int)maxContentLength, (String)"maxContentLength");
        this.spdyVersion = version.getVersion();
        this.maxContentLength = maxContentLength;
        this.messageMap = messageMap;
        this.validateHeaders = validateHeaders;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Iterator<Map.Entry<Integer, FullHttpMessage>> iterator = this.messageMap.entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                this.messageMap.clear();
                super.channelInactive((ChannelHandlerContext)ctx);
                return;
            }
            Map.Entry<Integer, FullHttpMessage> entry = iterator.next();
            ReferenceCountUtil.safeRelease((Object)entry.getValue());
        } while (true);
    }

    protected FullHttpMessage putMessage(int streamId, FullHttpMessage message) {
        return this.messageMap.put((Integer)Integer.valueOf((int)streamId), (FullHttpMessage)message);
    }

    protected FullHttpMessage getMessage(int streamId) {
        return this.messageMap.get((Object)Integer.valueOf((int)streamId));
    }

    protected FullHttpMessage removeMessage(int streamId) {
        return this.messageMap.remove((Object)Integer.valueOf((int)streamId));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, SpdyFrame msg, List<Object> out) throws Exception {
        if (msg instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            int streamId = spdySynStreamFrame.streamId();
            if (SpdyCodecUtil.isServerId((int)streamId)) {
                int associatedToStreamId = spdySynStreamFrame.associatedStreamId();
                if (associatedToStreamId == 0) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INVALID_STREAM);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                    return;
                }
                if (spdySynStreamFrame.isLast()) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                    return;
                }
                if (spdySynStreamFrame.isTruncated()) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INTERNAL_ERROR);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                    return;
                }
                try {
                    FullHttpRequest httpRequestWithEntity = SpdyHttpDecoder.createHttpRequest((SpdyHeadersFrame)spdySynStreamFrame, (ByteBufAllocator)ctx.alloc());
                    httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, (int)streamId);
                    httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, (int)associatedToStreamId);
                    httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.PRIORITY, (int)spdySynStreamFrame.priority());
                    out.add((Object)httpRequestWithEntity);
                    return;
                }
                catch (Throwable ignored) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                    return;
                }
            }
            if (spdySynStreamFrame.isTruncated()) {
                DefaultSpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame((int)streamId);
                spdySynReplyFrame.setLast((boolean)true);
                SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
                frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, (int)HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
                frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, (Object)HttpVersion.HTTP_1_0);
                ctx.writeAndFlush((Object)spdySynReplyFrame);
                return;
            }
            try {
                FullHttpRequest httpRequestWithEntity = SpdyHttpDecoder.createHttpRequest((SpdyHeadersFrame)spdySynStreamFrame, (ByteBufAllocator)ctx.alloc());
                httpRequestWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, (int)streamId);
                if (spdySynStreamFrame.isLast()) {
                    out.add((Object)httpRequestWithEntity);
                    return;
                }
                this.putMessage((int)streamId, (FullHttpMessage)httpRequestWithEntity);
                return;
            }
            catch (Throwable t) {
                DefaultSpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame((int)streamId);
                spdySynReplyFrame.setLast((boolean)true);
                SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
                frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, (int)HttpResponseStatus.BAD_REQUEST.code());
                frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, (Object)HttpVersion.HTTP_1_0);
                ctx.writeAndFlush((Object)spdySynReplyFrame);
                return;
            }
        }
        if (msg instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            int streamId = spdySynReplyFrame.streamId();
            if (spdySynReplyFrame.isTruncated()) {
                DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INTERNAL_ERROR);
                ctx.writeAndFlush((Object)spdyRstStreamFrame);
                return;
            }
            try {
                FullHttpResponse httpResponseWithEntity = SpdyHttpDecoder.createHttpResponse((SpdyHeadersFrame)spdySynReplyFrame, (ByteBufAllocator)ctx.alloc(), (boolean)this.validateHeaders);
                httpResponseWithEntity.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, (int)streamId);
                if (spdySynReplyFrame.isLast()) {
                    HttpUtil.setContentLength((HttpMessage)httpResponseWithEntity, (long)0L);
                    out.add((Object)httpResponseWithEntity);
                    return;
                }
                this.putMessage((int)streamId, (FullHttpMessage)httpResponseWithEntity);
                return;
            }
            catch (Throwable t) {
                DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                ctx.writeAndFlush((Object)spdyRstStreamFrame);
                return;
            }
        }
        if (msg instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
            int streamId = spdyHeadersFrame.streamId();
            FullHttpMessage fullHttpMessage = this.getMessage((int)streamId);
            if (fullHttpMessage == null) {
                if (!SpdyCodecUtil.isServerId((int)streamId)) return;
                if (spdyHeadersFrame.isTruncated()) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.INTERNAL_ERROR);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                    return;
                }
                try {
                    fullHttpMessage = SpdyHttpDecoder.createHttpResponse((SpdyHeadersFrame)spdyHeadersFrame, (ByteBufAllocator)ctx.alloc(), (boolean)this.validateHeaders);
                    fullHttpMessage.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, (int)streamId);
                    if (spdyHeadersFrame.isLast()) {
                        HttpUtil.setContentLength((HttpMessage)fullHttpMessage, (long)0L);
                        out.add((Object)fullHttpMessage);
                        return;
                    }
                    this.putMessage((int)streamId, (FullHttpMessage)fullHttpMessage);
                    return;
                }
                catch (Throwable t) {
                    DefaultSpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame((int)streamId, (SpdyStreamStatus)SpdyStreamStatus.PROTOCOL_ERROR);
                    ctx.writeAndFlush((Object)spdyRstStreamFrame);
                }
                return;
            }
            if (!spdyHeadersFrame.isTruncated()) {
                for (Map.Entry<K, V> e : spdyHeadersFrame.headers()) {
                    fullHttpMessage.headers().add((CharSequence)((CharSequence)e.getKey()), e.getValue());
                }
            }
            if (!spdyHeadersFrame.isLast()) return;
            HttpUtil.setContentLength((HttpMessage)fullHttpMessage, (long)((long)fullHttpMessage.content().readableBytes()));
            this.removeMessage((int)streamId);
            out.add((Object)fullHttpMessage);
            return;
        }
        if (!(msg instanceof SpdyDataFrame)) {
            if (!(msg instanceof SpdyRstStreamFrame)) return;
            SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            int streamId = spdyRstStreamFrame.streamId();
            this.removeMessage((int)streamId);
            return;
        }
        SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
        int streamId = spdyDataFrame.streamId();
        FullHttpMessage fullHttpMessage = this.getMessage((int)streamId);
        if (fullHttpMessage == null) {
            return;
        }
        ByteBuf content = fullHttpMessage.content();
        if (content.readableBytes() > this.maxContentLength - spdyDataFrame.content().readableBytes()) {
            this.removeMessage((int)streamId);
            throw new TooLongFrameException((String)("HTTP content length exceeded " + this.maxContentLength + " bytes."));
        }
        ByteBuf spdyDataFrameData = spdyDataFrame.content();
        int spdyDataFrameDataLen = spdyDataFrameData.readableBytes();
        content.writeBytes((ByteBuf)spdyDataFrameData, (int)spdyDataFrameData.readerIndex(), (int)spdyDataFrameDataLen);
        if (!spdyDataFrame.isLast()) return;
        HttpUtil.setContentLength((HttpMessage)fullHttpMessage, (long)((long)content.readableBytes()));
        this.removeMessage((int)streamId);
        out.add((Object)fullHttpMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FullHttpRequest createHttpRequest(SpdyHeadersFrame requestFrame, ByteBufAllocator alloc) throws Exception {
        SpdyHeaders headers = requestFrame.headers();
        HttpMethod method = HttpMethod.valueOf((String)headers.getAsString((CharSequence)SpdyHeaders.HttpNames.METHOD));
        String url = headers.getAsString((CharSequence)SpdyHeaders.HttpNames.PATH);
        HttpVersion httpVersion = HttpVersion.valueOf((String)headers.getAsString((CharSequence)SpdyHeaders.HttpNames.VERSION));
        headers.remove(SpdyHeaders.HttpNames.METHOD);
        headers.remove(SpdyHeaders.HttpNames.PATH);
        headers.remove(SpdyHeaders.HttpNames.VERSION);
        boolean release = true;
        ByteBuf buffer = alloc.buffer();
        try {
            DefaultFullHttpRequest req = new DefaultFullHttpRequest((HttpVersion)httpVersion, (HttpMethod)method, (String)url, (ByteBuf)buffer);
            headers.remove(SpdyHeaders.HttpNames.SCHEME);
            CharSequence host = (CharSequence)headers.get(SpdyHeaders.HttpNames.HOST);
            headers.remove(SpdyHeaders.HttpNames.HOST);
            req.headers().set((CharSequence)HttpHeaderNames.HOST, (Object)host);
            for (Map.Entry<K, V> e : requestFrame.headers()) {
                req.headers().add((CharSequence)((CharSequence)e.getKey()), e.getValue());
            }
            HttpUtil.setKeepAlive((HttpMessage)req, (boolean)true);
            req.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
            release = false;
            DefaultFullHttpRequest defaultFullHttpRequest = req;
            return defaultFullHttpRequest;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FullHttpResponse createHttpResponse(SpdyHeadersFrame responseFrame, ByteBufAllocator alloc, boolean validateHeaders) throws Exception {
        SpdyHeaders headers = responseFrame.headers();
        HttpResponseStatus status = HttpResponseStatus.parseLine((CharSequence)((CharSequence)headers.get(SpdyHeaders.HttpNames.STATUS)));
        HttpVersion version = HttpVersion.valueOf((String)headers.getAsString((CharSequence)SpdyHeaders.HttpNames.VERSION));
        headers.remove(SpdyHeaders.HttpNames.STATUS);
        headers.remove(SpdyHeaders.HttpNames.VERSION);
        boolean release = true;
        ByteBuf buffer = alloc.buffer();
        try {
            DefaultFullHttpResponse res = new DefaultFullHttpResponse((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)buffer, (boolean)validateHeaders);
            for (Map.Entry<K, V> e : responseFrame.headers()) {
                res.headers().add((CharSequence)((CharSequence)e.getKey()), e.getValue());
            }
            HttpUtil.setKeepAlive((HttpMessage)res, (boolean)true);
            res.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
            res.headers().remove((CharSequence)HttpHeaderNames.TRAILER);
            release = false;
            DefaultFullHttpResponse defaultFullHttpResponse = res;
            return defaultFullHttpResponse;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }
}

