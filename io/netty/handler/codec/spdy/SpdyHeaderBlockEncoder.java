/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdyHeaderBlockJZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeaderBlockZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.internal.PlatformDependent;

abstract class SpdyHeaderBlockEncoder {
    SpdyHeaderBlockEncoder() {
    }

    static SpdyHeaderBlockEncoder newInstance(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() < 7) return new SpdyHeaderBlockJZlibEncoder((SpdyVersion)version, (int)compressionLevel, (int)windowBits, (int)memLevel);
        return new SpdyHeaderBlockZlibEncoder((SpdyVersion)version, (int)compressionLevel);
    }

    abstract ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception;

    abstract void end();
}

