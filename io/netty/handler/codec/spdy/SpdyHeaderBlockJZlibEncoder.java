/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Deflater
 *  com.jcraft.jzlib.JZlib
 *  com.jcraft.jzlib.JZlib$WrapperType
 */
package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.ReferenceCounted;

class SpdyHeaderBlockJZlibEncoder
extends SpdyHeaderBlockRawEncoder {
    private final Deflater z = new Deflater();
    private boolean finished;

    SpdyHeaderBlockJZlibEncoder(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
        super((SpdyVersion)version);
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
        int resultCode = this.z.deflateInit((int)compressionLevel, (int)windowBits, (int)memLevel, (JZlib.WrapperType)JZlib.W_ZLIB);
        if (resultCode != 0) {
            throw new CompressionException((String)("failed to initialize an SPDY header block deflater: " + resultCode));
        }
        resultCode = this.z.deflateSetDictionary((byte[])SpdyCodecUtil.SPDY_DICT, (int)SpdyCodecUtil.SPDY_DICT.length);
        if (resultCode == 0) return;
        throw new CompressionException((String)("failed to set the SPDY dictionary: " + resultCode));
    }

    private void setInput(ByteBuf decompressed) {
        byte[] in;
        int offset;
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            in = decompressed.array();
            offset = decompressed.arrayOffset() + decompressed.readerIndex();
        } else {
            in = new byte[len];
            decompressed.getBytes((int)decompressed.readerIndex(), (byte[])in);
            offset = 0;
        }
        this.z.next_in = in;
        this.z.next_in_index = offset;
        this.z.avail_in = len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ByteBuf encode(ByteBufAllocator alloc) {
        boolean release = true;
        ReferenceCounted out = null;
        try {
            int resultCode;
            int oldNextInIndex = this.z.next_in_index;
            int oldNextOutIndex = this.z.next_out_index;
            int maxOutputLength = (int)Math.ceil((double)((double)this.z.next_in.length * 1.001)) + 12;
            out = alloc.heapBuffer((int)maxOutputLength);
            this.z.next_out = ((ByteBuf)out).array();
            this.z.next_out_index = ((ByteBuf)out).arrayOffset() + ((ByteBuf)out).writerIndex();
            this.z.avail_out = maxOutputLength;
            try {
                resultCode = this.z.deflate((int)2);
            }
            finally {
                ((ByteBuf)out).skipBytes((int)(this.z.next_in_index - oldNextInIndex));
            }
            if (resultCode != 0) {
                throw new CompressionException((String)("compression failure: " + resultCode));
            }
            int outputLength = this.z.next_out_index - oldNextOutIndex;
            if (outputLength > 0) {
                ((ByteBuf)out).writerIndex((int)(((ByteBuf)out).writerIndex() + outputLength));
            }
            release = false;
            ReferenceCounted referenceCounted = out;
            return referenceCounted;
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
            if (release && out != null) {
                out.release();
            }
        }
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
            this.setInput((ByteBuf)decompressed);
            ByteBuf byteBuf = this.encode((ByteBufAllocator)alloc);
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
        this.z.deflateEnd();
        this.z.next_in = null;
        this.z.next_out = null;
    }
}

