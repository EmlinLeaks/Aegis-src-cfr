/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContentDecoder;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;

public class HttpContentDecompressor
extends HttpContentDecoder {
    private final boolean strict;

    public HttpContentDecompressor() {
        this((boolean)false);
    }

    public HttpContentDecompressor(boolean strict) {
        this.strict = strict;
    }

    @Override
    protected EmbeddedChannel newContentDecoder(String contentEncoding) throws Exception {
        if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase((CharSequence)contentEncoding) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            return new EmbeddedChannel((ChannelId)this.ctx.channel().id(), (boolean)this.ctx.channel().metadata().hasDisconnect(), (ChannelConfig)this.ctx.channel().config(), (ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)ZlibWrapper.GZIP)});
        }
        if (!HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase((CharSequence)contentEncoding)) {
            if (!HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase((CharSequence)contentEncoding)) return null;
        }
        ZlibWrapper wrapper = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
        return new EmbeddedChannel((ChannelId)this.ctx.channel().id(), (boolean)this.ctx.channel().metadata().hasDisconnect(), (ChannelConfig)this.ctx.channel().config(), (ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)wrapper)});
    }
}

