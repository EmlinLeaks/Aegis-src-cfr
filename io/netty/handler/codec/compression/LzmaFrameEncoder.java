/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lzma.sdk.ICodeProgress
 *  lzma.sdk.lzma.Encoder
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import lzma.sdk.ICodeProgress;
import lzma.sdk.lzma.Encoder;

public class LzmaFrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(LzmaFrameEncoder.class);
    private static final int MEDIUM_DICTIONARY_SIZE = 65536;
    private static final int MIN_FAST_BYTES = 5;
    private static final int MEDIUM_FAST_BYTES = 32;
    private static final int MAX_FAST_BYTES = 273;
    private static final int DEFAULT_MATCH_FINDER = 1;
    private static final int DEFAULT_LC = 3;
    private static final int DEFAULT_LP = 0;
    private static final int DEFAULT_PB = 2;
    private final Encoder encoder;
    private final byte properties;
    private final int littleEndianDictionarySize;
    private static boolean warningLogged;

    public LzmaFrameEncoder() {
        this((int)65536);
    }

    public LzmaFrameEncoder(int lc, int lp, int pb) {
        this((int)lc, (int)lp, (int)pb, (int)65536);
    }

    public LzmaFrameEncoder(int dictionarySize) {
        this((int)3, (int)0, (int)2, (int)dictionarySize);
    }

    public LzmaFrameEncoder(int lc, int lp, int pb, int dictionarySize) {
        this((int)lc, (int)lp, (int)pb, (int)dictionarySize, (boolean)false, (int)32);
    }

    public LzmaFrameEncoder(int lc, int lp, int pb, int dictionarySize, boolean endMarkerMode, int numFastBytes) {
        if (lc < 0) throw new IllegalArgumentException((String)("lc: " + lc + " (expected: 0-8)"));
        if (lc > 8) {
            throw new IllegalArgumentException((String)("lc: " + lc + " (expected: 0-8)"));
        }
        if (lp < 0) throw new IllegalArgumentException((String)("lp: " + lp + " (expected: 0-4)"));
        if (lp > 4) {
            throw new IllegalArgumentException((String)("lp: " + lp + " (expected: 0-4)"));
        }
        if (pb < 0) throw new IllegalArgumentException((String)("pb: " + pb + " (expected: 0-4)"));
        if (pb > 4) {
            throw new IllegalArgumentException((String)("pb: " + pb + " (expected: 0-4)"));
        }
        if (lc + lp > 4 && !warningLogged) {
            logger.warn((String)"The latest versions of LZMA libraries (for example, XZ Utils) has an additional requirement: lc + lp <= 4. Data which don't follow this requirement cannot be decompressed with this libraries.");
            warningLogged = true;
        }
        if (dictionarySize < 0) {
            throw new IllegalArgumentException((String)("dictionarySize: " + dictionarySize + " (expected: 0+)"));
        }
        if (numFastBytes < 5 || numFastBytes > 273) throw new IllegalArgumentException((String)String.format((String)"numFastBytes: %d (expected: %d-%d)", (Object[])new Object[]{Integer.valueOf((int)numFastBytes), Integer.valueOf((int)5), Integer.valueOf((int)273)}));
        this.encoder = new Encoder();
        this.encoder.setDictionarySize((int)dictionarySize);
        this.encoder.setEndMarkerMode((boolean)endMarkerMode);
        this.encoder.setMatchFinder((int)1);
        this.encoder.setNumFastBytes((int)numFastBytes);
        this.encoder.setLcLpPb((int)lc, (int)lp, (int)pb);
        this.properties = (byte)((pb * 5 + lp) * 9 + lc);
        this.littleEndianDictionarySize = Integer.reverseBytes((int)dictionarySize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int length = in.readableBytes();
        ByteBufInputStream bbIn = null;
        OutputStream bbOut = null;
        try {
            bbIn = new ByteBufInputStream((ByteBuf)in);
            bbOut = new ByteBufOutputStream((ByteBuf)out);
            ((ByteBufOutputStream)bbOut).writeByte((int)this.properties);
            ((ByteBufOutputStream)bbOut).writeInt((int)this.littleEndianDictionarySize);
            ((ByteBufOutputStream)bbOut).writeLong((long)Long.reverseBytes((long)((long)length)));
            this.encoder.code((InputStream)bbIn, (OutputStream)bbOut, (long)-1L, (long)-1L, null);
            return;
        }
        finally {
            if (bbIn != null) {
                ((InputStream)bbIn).close();
            }
            if (bbOut != null) {
                bbOut.close();
            }
        }
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf in, boolean preferDirect) throws Exception {
        int length = in.readableBytes();
        int maxOutputLength = LzmaFrameEncoder.maxOutputBufferLength((int)length);
        return ctx.alloc().ioBuffer((int)maxOutputLength);
    }

    private static int maxOutputBufferLength(int inputLength) {
        double factor;
        if (inputLength < 200) {
            factor = 1.5;
            return 13 + (int)((double)inputLength * factor);
        }
        if (inputLength < 500) {
            factor = 1.2;
            return 13 + (int)((double)inputLength * factor);
        }
        if (inputLength < 1000) {
            factor = 1.1;
            return 13 + (int)((double)inputLength * factor);
        }
        if (inputLength < 10000) {
            factor = 1.05;
            return 13 + (int)((double)inputLength * factor);
        }
        factor = 1.02;
        return 13 + (int)((double)inputLength * factor);
    }
}

