/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.spdy.SpdyHttpDecoder;
import io.netty.handler.codec.spdy.SpdyHttpEncoder;
import io.netty.handler.codec.spdy.SpdyVersion;

public final class SpdyHttpCodec
extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder> {
    public SpdyHttpCodec(SpdyVersion version, int maxContentLength) {
        super(new SpdyHttpDecoder((SpdyVersion)version, (int)maxContentLength), new SpdyHttpEncoder((SpdyVersion)version));
    }

    public SpdyHttpCodec(SpdyVersion version, int maxContentLength, boolean validateHttpHeaders) {
        super(new SpdyHttpDecoder((SpdyVersion)version, (int)maxContentLength, (boolean)validateHttpHeaders), new SpdyHttpEncoder((SpdyVersion)version));
    }
}

