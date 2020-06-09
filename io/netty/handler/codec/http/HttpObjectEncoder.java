/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeadersEncoder;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HttpObjectEncoder<H extends HttpMessage>
extends MessageToMessageEncoder<Object> {
    static final int CRLF_SHORT = 3338;
    private static final int ZERO_CRLF_MEDIUM = 3149066;
    private static final byte[] ZERO_CRLF_CRLF = new byte[]{48, 13, 10, 13, 10};
    private static final ByteBuf CRLF_BUF = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)2).writeByte((int)13).writeByte((int)10));
    private static final ByteBuf ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)ZERO_CRLF_CRLF.length).writeBytes((byte[])ZERO_CRLF_CRLF));
    private static final float HEADERS_WEIGHT_NEW = 0.2f;
    private static final float HEADERS_WEIGHT_HISTORICAL = 0.8f;
    private static final float TRAILERS_WEIGHT_NEW = 0.2f;
    private static final float TRAILERS_WEIGHT_HISTORICAL = 0.8f;
    private static final int ST_INIT = 0;
    private static final int ST_CONTENT_NON_CHUNK = 1;
    private static final int ST_CONTENT_CHUNK = 2;
    private static final int ST_CONTENT_ALWAYS_EMPTY = 3;
    private int state = 0;
    private float headersEncodedSizeAccumulator = 256.0f;
    private float trailersEncodedSizeAccumulator = 256.0f;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf potentialEmptyBuf;
        ByteBuf buf = null;
        if (msg instanceof HttpMessage) {
            if (this.state != 0) {
                throw new IllegalStateException((String)("unexpected message type: " + StringUtil.simpleClassName((Object)msg) + ", state: " + this.state));
            }
            HttpMessage m = (HttpMessage)msg;
            buf = ctx.alloc().buffer((int)((int)this.headersEncodedSizeAccumulator));
            this.encodeInitialLine((ByteBuf)buf, m);
            this.state = this.isContentAlwaysEmpty(m) ? 3 : (HttpUtil.isTransferEncodingChunked((HttpMessage)m) ? 2 : 1);
            this.sanitizeHeadersBeforeEncode(m, (boolean)(this.state == 3));
            this.encodeHeaders((HttpHeaders)m.headers(), (ByteBuf)buf);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
            this.headersEncodedSizeAccumulator = 0.2f * (float)HttpObjectEncoder.padSizeForAccumulation((int)buf.readableBytes()) + 0.8f * this.headersEncodedSizeAccumulator;
        }
        if (msg instanceof ByteBuf && !(potentialEmptyBuf = (ByteBuf)msg).isReadable()) {
            out.add((Object)potentialEmptyBuf.retain());
            return;
        }
        if (!(msg instanceof HttpContent || msg instanceof ByteBuf || msg instanceof FileRegion)) {
            if (buf == null) return;
            out.add((Object)buf);
            return;
        }
        switch (this.state) {
            case 0: {
                throw new IllegalStateException((String)("unexpected message type: " + StringUtil.simpleClassName((Object)msg)));
            }
            case 1: {
                long contentLength = HttpObjectEncoder.contentLength((Object)msg);
                if (contentLength > 0L) {
                    if (buf != null && (long)buf.writableBytes() >= contentLength && msg instanceof HttpContent) {
                        buf.writeBytes((ByteBuf)((HttpContent)msg).content());
                        out.add((Object)buf);
                    } else {
                        if (buf != null) {
                            out.add((Object)buf);
                        }
                        out.add((Object)HttpObjectEncoder.encodeAndRetain((Object)msg));
                    }
                    if (!(msg instanceof LastHttpContent)) break;
                    this.state = 0;
                    break;
                }
            }
            case 3: {
                if (buf != null) {
                    out.add((Object)buf);
                    break;
                }
                out.add((Object)Unpooled.EMPTY_BUFFER);
                break;
            }
            case 2: {
                if (buf != null) {
                    out.add((Object)buf);
                }
                this.encodeChunkedContent((ChannelHandlerContext)ctx, (Object)msg, (long)HttpObjectEncoder.contentLength((Object)msg), out);
                break;
            }
            default: {
                throw new Error();
            }
        }
        if (!(msg instanceof LastHttpContent)) return;
        this.state = 0;
    }

    protected void encodeHeaders(HttpHeaders headers, ByteBuf buf) {
        Iterator<Map.Entry<CharSequence, CharSequence>> iter = headers.iteratorCharSequence();
        while (iter.hasNext()) {
            Map.Entry<CharSequence, CharSequence> header = iter.next();
            HttpHeadersEncoder.encoderHeader((CharSequence)header.getKey(), (CharSequence)header.getValue(), (ByteBuf)buf);
        }
    }

    private void encodeChunkedContent(ChannelHandlerContext ctx, Object msg, long contentLength, List<Object> out) {
        ByteBuf buf;
        if (contentLength > 0L) {
            String lengthHex = Long.toHexString((long)contentLength);
            buf = ctx.alloc().buffer((int)(lengthHex.length() + 2));
            buf.writeCharSequence((CharSequence)lengthHex, (Charset)CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
            out.add((Object)buf);
            out.add((Object)HttpObjectEncoder.encodeAndRetain((Object)msg));
            out.add((Object)CRLF_BUF.duplicate());
        }
        if (!(msg instanceof LastHttpContent)) {
            if (contentLength != 0L) return;
            out.add((Object)HttpObjectEncoder.encodeAndRetain((Object)msg));
            return;
        }
        HttpHeaders headers = ((LastHttpContent)msg).trailingHeaders();
        if (headers.isEmpty()) {
            out.add((Object)ZERO_CRLF_CRLF_BUF.duplicate());
            return;
        }
        buf = ctx.alloc().buffer((int)((int)this.trailersEncodedSizeAccumulator));
        ByteBufUtil.writeMediumBE((ByteBuf)buf, (int)3149066);
        this.encodeHeaders((HttpHeaders)headers, (ByteBuf)buf);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
        this.trailersEncodedSizeAccumulator = 0.2f * (float)HttpObjectEncoder.padSizeForAccumulation((int)buf.readableBytes()) + 0.8f * this.trailersEncodedSizeAccumulator;
        out.add((Object)buf);
    }

    protected void sanitizeHeadersBeforeEncode(H msg, boolean isAlwaysEmpty) {
    }

    protected boolean isContentAlwaysEmpty(H msg) {
        return false;
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (msg instanceof HttpObject) return true;
        if (msg instanceof ByteBuf) return true;
        if (msg instanceof FileRegion) return true;
        return false;
    }

    private static Object encodeAndRetain(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).retain();
        }
        if (msg instanceof HttpContent) {
            return ((HttpContent)msg).content().retain();
        }
        if (!(msg instanceof FileRegion)) throw new IllegalStateException((String)("unexpected message type: " + StringUtil.simpleClassName((Object)msg)));
        return ((FileRegion)msg).retain();
    }

    private static long contentLength(Object msg) {
        if (msg instanceof HttpContent) {
            return (long)((HttpContent)msg).content().readableBytes();
        }
        if (msg instanceof ByteBuf) {
            return (long)((ByteBuf)msg).readableBytes();
        }
        if (!(msg instanceof FileRegion)) throw new IllegalStateException((String)("unexpected message type: " + StringUtil.simpleClassName((Object)msg)));
        return ((FileRegion)msg).count();
    }

    private static int padSizeForAccumulation(int readableBytes) {
        return (readableBytes << 2) / 3;
    }

    @Deprecated
    protected static void encodeAscii(String s, ByteBuf buf) {
        buf.writeCharSequence((CharSequence)s, (Charset)CharsetUtil.US_ASCII);
    }

    protected abstract void encodeInitialLine(ByteBuf var1, H var2) throws Exception;
}

