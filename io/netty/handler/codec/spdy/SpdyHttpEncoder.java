/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.spdy.DefaultSpdyDataFrame;
import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynReplyFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpdyHttpEncoder
extends MessageToMessageEncoder<HttpObject> {
    private int currentStreamId;
    private final boolean validateHeaders;
    private final boolean headersToLowerCase;

    public SpdyHttpEncoder(SpdyVersion version) {
        this((SpdyVersion)version, (boolean)true, (boolean)true);
    }

    public SpdyHttpEncoder(SpdyVersion version, boolean headersToLowerCase, boolean validateHeaders) {
        if (version == null) {
            throw new NullPointerException((String)"version");
        }
        this.headersToLowerCase = headersToLowerCase;
        this.validateHeaders = validateHeaders;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        boolean valid;
        block3 : {
            block6 : {
                Iterator<Map.Entry<CharSequence, CharSequence>> itr;
                DefaultSpdyHeadersFrame spdyHeadersFrame;
                DefaultSpdyDataFrame spdyDataFrame;
                block7 : {
                    block4 : {
                        HttpHeaders trailers;
                        block5 : {
                            valid = false;
                            boolean last = false;
                            if (msg instanceof HttpRequest) {
                                HttpRequest httpRequest = (HttpRequest)msg;
                                SpdySynStreamFrame spdySynStreamFrame = this.createSynStreamFrame((HttpRequest)httpRequest);
                                out.add((Object)spdySynStreamFrame);
                                last = spdySynStreamFrame.isLast() || spdySynStreamFrame.isUnidirectional();
                                valid = true;
                            }
                            if (msg instanceof HttpResponse) {
                                HttpResponse httpResponse = (HttpResponse)msg;
                                SpdyHeadersFrame spdyHeadersFrame2 = this.createHeadersFrame((HttpResponse)httpResponse);
                                out.add((Object)spdyHeadersFrame2);
                                last = spdyHeadersFrame2.isLast();
                                valid = true;
                            }
                            if (!(msg instanceof HttpContent) || last) break block3;
                            HttpContent chunk = (HttpContent)msg;
                            chunk.content().retain();
                            spdyDataFrame = new DefaultSpdyDataFrame((int)this.currentStreamId, (ByteBuf)chunk.content());
                            if (!(chunk instanceof LastHttpContent)) break block4;
                            LastHttpContent trailer = (LastHttpContent)chunk;
                            trailers = trailer.trailingHeaders();
                            if (!trailers.isEmpty()) break block5;
                            spdyDataFrame.setLast((boolean)true);
                            out.add((Object)spdyDataFrame);
                            break block6;
                        }
                        spdyHeadersFrame = new DefaultSpdyHeadersFrame((int)this.currentStreamId, (boolean)this.validateHeaders);
                        spdyHeadersFrame.setLast((boolean)true);
                        itr = trailers.iteratorCharSequence();
                        break block7;
                    }
                    out.add((Object)spdyDataFrame);
                    break block6;
                }
                while (itr.hasNext()) {
                    Map.Entry<CharSequence, CharSequence> entry = itr.next();
                    CharSequence headerName = this.headersToLowerCase ? AsciiString.of((CharSequence)entry.getKey()).toLowerCase() : entry.getKey();
                    spdyHeadersFrame.headers().add(headerName, entry.getValue());
                }
                out.add((Object)spdyDataFrame);
                out.add((Object)spdyHeadersFrame);
            }
            valid = true;
        }
        if (valid) return;
        throw new UnsupportedMessageTypeException((Object)msg, new Class[0]);
    }

    private SpdySynStreamFrame createSynStreamFrame(HttpRequest httpRequest) throws Exception {
        HttpHeaders httpHeaders = httpRequest.headers();
        int streamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID).intValue();
        int associatedToStreamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, (int)0);
        byte priority = (byte)httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.PRIORITY, (int)0);
        String scheme = httpHeaders.get((CharSequence)SpdyHttpHeaders.Names.SCHEME);
        httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
        httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID);
        httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.PRIORITY);
        httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.SCHEME);
        httpHeaders.remove((CharSequence)HttpHeaderNames.CONNECTION);
        httpHeaders.remove((String)"Keep-Alive");
        httpHeaders.remove((String)"Proxy-Connection");
        httpHeaders.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
        DefaultSpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame((int)streamId, (int)associatedToStreamId, (byte)priority, (boolean)this.validateHeaders);
        SpdyHeaders frameHeaders = spdySynStreamFrame.headers();
        frameHeaders.set(SpdyHeaders.HttpNames.METHOD, httpRequest.method().name());
        frameHeaders.set(SpdyHeaders.HttpNames.PATH, httpRequest.uri());
        frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpRequest.protocolVersion().text());
        String host = httpHeaders.get((CharSequence)HttpHeaderNames.HOST);
        httpHeaders.remove((CharSequence)HttpHeaderNames.HOST);
        frameHeaders.set(SpdyHeaders.HttpNames.HOST, host);
        if (scheme == null) {
            scheme = "https";
        }
        frameHeaders.set(SpdyHeaders.HttpNames.SCHEME, scheme);
        Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
        while (itr.hasNext()) {
            Map.Entry<CharSequence, CharSequence> entry = itr.next();
            CharSequence headerName = this.headersToLowerCase ? AsciiString.of((CharSequence)entry.getKey()).toLowerCase() : entry.getKey();
            frameHeaders.add(headerName, entry.getValue());
        }
        this.currentStreamId = spdySynStreamFrame.streamId();
        if (associatedToStreamId == 0) {
            spdySynStreamFrame.setLast((boolean)SpdyHttpEncoder.isLast((HttpMessage)httpRequest));
            return spdySynStreamFrame;
        }
        spdySynStreamFrame.setUnidirectional((boolean)true);
        return spdySynStreamFrame;
    }

    private SpdyHeadersFrame createHeadersFrame(HttpResponse httpResponse) throws Exception {
        HttpHeaders httpHeaders = httpResponse.headers();
        int streamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID).intValue();
        httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
        httpHeaders.remove((CharSequence)HttpHeaderNames.CONNECTION);
        httpHeaders.remove((String)"Keep-Alive");
        httpHeaders.remove((String)"Proxy-Connection");
        httpHeaders.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
        DefaultSpdyHeadersFrame spdyHeadersFrame = SpdyCodecUtil.isServerId((int)streamId) ? new DefaultSpdyHeadersFrame((int)streamId, (boolean)this.validateHeaders) : new DefaultSpdySynReplyFrame((int)streamId, (boolean)this.validateHeaders);
        SpdyHeaders frameHeaders = spdyHeadersFrame.headers();
        frameHeaders.set(SpdyHeaders.HttpNames.STATUS, httpResponse.status().codeAsText());
        frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpResponse.protocolVersion().text());
        Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
        do {
            if (!itr.hasNext()) {
                this.currentStreamId = streamId;
                spdyHeadersFrame.setLast((boolean)SpdyHttpEncoder.isLast((HttpMessage)httpResponse));
                return spdyHeadersFrame;
            }
            Map.Entry<CharSequence, CharSequence> entry = itr.next();
            CharSequence headerName = this.headersToLowerCase ? AsciiString.of((CharSequence)entry.getKey()).toLowerCase() : entry.getKey();
            spdyHeadersFrame.headers().add(headerName, entry.getValue());
        } while (true);
    }

    private static boolean isLast(HttpMessage httpMessage) {
        if (!(httpMessage instanceof FullHttpMessage)) return false;
        FullHttpMessage fullMessage = (FullHttpMessage)httpMessage;
        if (!fullMessage.trailingHeaders().isEmpty()) return false;
        if (fullMessage.content().isReadable()) return false;
        return true;
    }
}

