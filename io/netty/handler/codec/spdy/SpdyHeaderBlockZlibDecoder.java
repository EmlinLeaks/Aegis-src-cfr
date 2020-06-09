/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawDecoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyProtocolException;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

final class SpdyHeaderBlockZlibDecoder
extends SpdyHeaderBlockRawDecoder {
    private static final int DEFAULT_BUFFER_CAPACITY = 4096;
    private static final SpdyProtocolException INVALID_HEADER_BLOCK = new SpdyProtocolException((String)"Invalid Header Block");
    private final Inflater decompressor = new Inflater();
    private ByteBuf decompressed;

    SpdyHeaderBlockZlibDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        super((SpdyVersion)spdyVersion, (int)maxHeaderSize);
    }

    @Override
    void decode(ByteBufAllocator alloc, ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        int numBytes;
        int len = this.setInput((ByteBuf)headerBlock);
        while ((numBytes = this.decompress((ByteBufAllocator)alloc, (SpdyHeadersFrame)frame)) > 0) {
        }
        if (this.decompressor.getRemaining() != 0) {
            throw INVALID_HEADER_BLOCK;
        }
        headerBlock.skipBytes((int)len);
    }

    private int setInput(ByteBuf compressed) {
        int len = compressed.readableBytes();
        if (compressed.hasArray()) {
            this.decompressor.setInput((byte[])compressed.array(), (int)(compressed.arrayOffset() + compressed.readerIndex()), (int)len);
            return len;
        }
        byte[] in = new byte[len];
        compressed.getBytes((int)compressed.readerIndex(), (byte[])in);
        this.decompressor.setInput((byte[])in, (int)0, (int)in.length);
        return len;
    }

    private int decompress(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        this.ensureBuffer((ByteBufAllocator)alloc);
        byte[] out = this.decompressed.array();
        int off = this.decompressed.arrayOffset() + this.decompressed.writerIndex();
        try {
            int numBytes = this.decompressor.inflate((byte[])out, (int)off, (int)this.decompressed.writableBytes());
            if (numBytes == 0 && this.decompressor.needsDictionary()) {
                try {
                    this.decompressor.setDictionary((byte[])SpdyCodecUtil.SPDY_DICT);
                }
                catch (IllegalArgumentException ignored) {
                    throw INVALID_HEADER_BLOCK;
                }
                numBytes = this.decompressor.inflate((byte[])out, (int)off, (int)this.decompressed.writableBytes());
            }
            if (frame == null) return numBytes;
            this.decompressed.writerIndex((int)(this.decompressed.writerIndex() + numBytes));
            this.decodeHeaderBlock((ByteBuf)this.decompressed, (SpdyHeadersFrame)frame);
            this.decompressed.discardReadBytes();
            return numBytes;
        }
        catch (DataFormatException e) {
            throw new SpdyProtocolException((String)"Received invalid header block", (Throwable)e);
        }
    }

    private void ensureBuffer(ByteBufAllocator alloc) {
        if (this.decompressed == null) {
            this.decompressed = alloc.heapBuffer((int)4096);
        }
        this.decompressed.ensureWritable((int)1);
    }

    @Override
    void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        super.endHeaderBlock((SpdyHeadersFrame)frame);
        this.releaseBuffer();
    }

    @Override
    public void end() {
        super.end();
        this.releaseBuffer();
        this.decompressor.end();
    }

    private void releaseBuffer() {
        if (this.decompressed == null) return;
        this.decompressed.release();
        this.decompressed = null;
    }
}

