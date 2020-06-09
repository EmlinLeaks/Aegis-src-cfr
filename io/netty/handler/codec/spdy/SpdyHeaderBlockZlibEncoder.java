/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder
extends SpdyHeaderBlockRawEncoder {
    private final Deflater compressor;
    private boolean finished;

    SpdyHeaderBlockZlibEncoder(SpdyVersion spdyVersion, int compressionLevel) {
        super((SpdyVersion)spdyVersion);
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        this.compressor = new Deflater((int)compressionLevel);
        this.compressor.setDictionary((byte[])SpdyCodecUtil.SPDY_DICT);
    }

    private int setInput(ByteBuf decompressed) {
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            this.compressor.setInput((byte[])decompressed.array(), (int)(decompressed.arrayOffset() + decompressed.readerIndex()), (int)len);
            return len;
        }
        byte[] in = new byte[len];
        decompressed.getBytes((int)decompressed.readerIndex(), (byte[])in);
        this.compressor.setInput((byte[])in, (int)0, (int)in.length);
        return len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ByteBuf encode(ByteBufAllocator alloc, int len) {
        ByteBuf compressed = alloc.heapBuffer((int)len);
        boolean release = true;
        try {
            while (this.compressInto((ByteBuf)compressed)) {
                compressed.ensureWritable((int)(compressed.capacity() << 1));
            }
            release = false;
            ByteBuf byteBuf = compressed;
            return byteBuf;
        }
        finally {
            if (release) {
                compressed.release();
            }
        }
    }

    @SuppressJava6Requirement(reason="Guarded by java version check")
    private boolean compressInto(ByteBuf compressed) {
        byte[] out = compressed.array();
        int off = compressed.arrayOffset() + compressed.writerIndex();
        int toWrite = compressed.writableBytes();
        int numBytes = PlatformDependent.javaVersion() >= 7 ? this.compressor.deflate((byte[])out, (int)off, (int)toWrite, (int)2) : this.compressor.deflate((byte[])out, (int)off, (int)toWrite);
        compressed.writerIndex((int)(compressed.writerIndex() + numBytes));
        if (numBytes != toWrite) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        if (frame == null) {
            throw new IllegalArgumentException((String)"frame");
        }
        if (this.finished) {
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBuf decompressed = super.encode((ByteBufAllocator)alloc, (SpdyHeadersFrame)frame);
        try {
            if (!decompressed.isReadable()) {
                ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
                return byteBuf;
            }
            int len = this.setInput((ByteBuf)decompressed);
            ByteBuf byteBuf = this.encode((ByteBufAllocator)alloc, (int)len);
            return byteBuf;
        }
        finally {
            decompressed.release();
        }
    }

    @Override
    public void end() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        this.compressor.end();
        super.end();
    }
}

