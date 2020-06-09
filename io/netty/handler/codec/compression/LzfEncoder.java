/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.ning.compress.BufferRecycler
 *  com.ning.compress.lzf.ChunkEncoder
 *  com.ning.compress.lzf.LZFEncoder
 *  com.ning.compress.lzf.util.ChunkEncoderFactory
 */
package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_BLOCK_TO_COMPRESS = 16;
    private final ChunkEncoder encoder;
    private final BufferRecycler recycler;

    public LzfEncoder() {
        this((boolean)false, (int)65535);
    }

    public LzfEncoder(boolean safeInstance) {
        this((boolean)safeInstance, (int)65535);
    }

    public LzfEncoder(int totalLength) {
        this((boolean)false, (int)totalLength);
    }

    public LzfEncoder(boolean safeInstance, int totalLength) {
        super((boolean)false);
        if (totalLength < 16) throw new IllegalArgumentException((String)("totalLength: " + totalLength + " (expected: " + 16 + '-' + 65535 + ')'));
        if (totalLength > 65535) {
            throw new IllegalArgumentException((String)("totalLength: " + totalLength + " (expected: " + 16 + '-' + 65535 + ')'));
        }
        this.encoder = safeInstance ? ChunkEncoderFactory.safeNonAllocatingInstance((int)totalLength) : ChunkEncoderFactory.optimalNonAllocatingInstance((int)totalLength);
        this.recycler = BufferRecycler.instance();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int inputPtr;
        byte[] input;
        int length = in.readableBytes();
        int idx = in.readerIndex();
        if (in.hasArray()) {
            input = in.array();
            inputPtr = in.arrayOffset() + idx;
        } else {
            input = this.recycler.allocInputBuffer((int)length);
            in.getBytes((int)idx, (byte[])input, (int)0, (int)length);
            inputPtr = 0;
        }
        int maxOutputLength = LZFEncoder.estimateMaxWorkspaceSize((int)length);
        out.ensureWritable((int)maxOutputLength);
        byte[] output = out.array();
        int outputPtr = out.arrayOffset() + out.writerIndex();
        int outputLength = LZFEncoder.appendEncoded((ChunkEncoder)this.encoder, (byte[])input, (int)inputPtr, (int)length, (byte[])output, (int)outputPtr) - outputPtr;
        out.writerIndex((int)(out.writerIndex() + outputLength));
        in.skipBytes((int)length);
        if (in.hasArray()) return;
        this.recycler.releaseInputBuffer((byte[])input);
    }
}

