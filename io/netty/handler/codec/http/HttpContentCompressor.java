/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AsciiString;

public class HttpContentCompressor
extends HttpContentEncoder {
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    private final int contentSizeThreshold;
    private ChannelHandlerContext ctx;

    public HttpContentCompressor() {
        this((int)6);
    }

    public HttpContentCompressor(int compressionLevel) {
        this((int)compressionLevel, (int)15, (int)8, (int)0);
    }

    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
        this((int)compressionLevel, (int)windowBits, (int)memLevel, (int)0);
    }

    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel, int contentSizeThreshold) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        if (windowBits < 9) throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        if (windowBits > 15) {
            throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        }
        if (memLevel < 1) throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        if (memLevel > 9) {
            throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        }
        if (contentSizeThreshold < 0) {
            throw new IllegalArgumentException((String)("contentSizeThreshold: " + contentSizeThreshold + " (expected: non negative number)"));
        }
        this.compressionLevel = compressionLevel;
        this.windowBits = windowBits;
        this.memLevel = memLevel;
        this.contentSizeThreshold = contentSizeThreshold;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected HttpContentEncoder.Result beginEncode(HttpResponse headers, String acceptEncoding) throws Exception {
        if (this.contentSizeThreshold > 0 && headers instanceof HttpContent && ((HttpContent)((Object)headers)).content().readableBytes() < this.contentSizeThreshold) {
            return null;
        }
        String contentEncoding = headers.headers().get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
        if (contentEncoding != null) {
            return null;
        }
        ZlibWrapper wrapper = this.determineWrapper((String)acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        switch (1.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[wrapper.ordinal()]) {
            case 1: {
                String targetContentEncoding = "gzip";
                return new HttpContentEncoder.Result((String)targetContentEncoding, (EmbeddedChannel)new EmbeddedChannel((ChannelId)this.ctx.channel().id(), (boolean)this.ctx.channel().metadata().hasDisconnect(), (ChannelConfig)this.ctx.channel().config(), (ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder((ZlibWrapper)wrapper, (int)this.compressionLevel, (int)this.windowBits, (int)this.memLevel)}));
            }
            case 2: {
                String targetContentEncoding = "deflate";
                return new HttpContentEncoder.Result((String)targetContentEncoding, (EmbeddedChannel)new EmbeddedChannel((ChannelId)this.ctx.channel().id(), (boolean)this.ctx.channel().metadata().hasDisconnect(), (ChannelConfig)this.ctx.channel().config(), (ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder((ZlibWrapper)wrapper, (int)this.compressionLevel, (int)this.windowBits, (int)this.memLevel)}));
            }
        }
        throw new Error();
    }

    protected ZlibWrapper determineWrapper(String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : acceptEncoding.split((String)",")) {
            float q = 1.0f;
            int equalsPos = encoding.indexOf((int)61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat((String)encoding.substring((int)(equalsPos + 1)));
                }
                catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains((CharSequence)"*")) {
                starQ = q;
                continue;
            }
            if (encoding.contains((CharSequence)"gzip") && q > gzipQ) {
                gzipQ = q;
                continue;
            }
            if (!encoding.contains((CharSequence)"deflate") || !(q > deflateQ)) continue;
            deflateQ = q;
        }
        if (gzipQ > 0.0f || deflateQ > 0.0f) {
            if (!(gzipQ >= deflateQ)) return ZlibWrapper.ZLIB;
            return ZlibWrapper.GZIP;
        }
        if (!(starQ > 0.0f)) return null;
        if (gzipQ == -1.0f) {
            return ZlibWrapper.GZIP;
        }
        if (deflateQ != -1.0f) return null;
        return ZlibWrapper.ZLIB;
    }
}

