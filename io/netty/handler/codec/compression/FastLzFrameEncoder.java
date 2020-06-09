/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.FastLz;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    private final int level;
    private final Checksum checksum;

    public FastLzFrameEncoder() {
        this((int)0, null);
    }

    public FastLzFrameEncoder(int level) {
        this((int)level, null);
    }

    public FastLzFrameEncoder(boolean validateChecksums) {
        this((int)0, (Checksum)(validateChecksums ? new Adler32() : null));
    }

    public FastLzFrameEncoder(int level, Checksum checksum) {
        super((boolean)false);
        if (level != 0 && level != 1 && level != 2) {
            throw new IllegalArgumentException((String)String.format((String)"level: %d (expected: %d or %d or %d)", (Object[])new Object[]{Integer.valueOf((int)level), Integer.valueOf((int)0), Integer.valueOf((int)1), Integer.valueOf((int)2)}));
        }
        this.level = level;
        this.checksum = checksum;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        Checksum checksum = this.checksum;
        while (in.isReadable()) {
            int chunkLength;
            int blockType;
            int idx = in.readerIndex();
            int length = Math.min((int)in.readableBytes(), (int)65535);
            int outputIdx = out.writerIndex();
            out.setMedium((int)outputIdx, (int)4607066);
            int outputOffset = outputIdx + 4 + (checksum != null ? 4 : 0);
            if (length < 32) {
                blockType = 0;
                out.ensureWritable((int)(outputOffset + 2 + length));
                byte[] output = out.array();
                int outputPtr = out.arrayOffset() + outputOffset + 2;
                if (checksum != null) {
                    int inputPtr;
                    byte[] input;
                    if (in.hasArray()) {
                        input = in.array();
                        inputPtr = in.arrayOffset() + idx;
                    } else {
                        input = new byte[length];
                        in.getBytes((int)idx, (byte[])input);
                        inputPtr = 0;
                    }
                    checksum.reset();
                    checksum.update((byte[])input, (int)inputPtr, (int)length);
                    out.setInt((int)(outputIdx + 4), (int)((int)checksum.getValue()));
                    System.arraycopy((Object)input, (int)inputPtr, (Object)output, (int)outputPtr, (int)length);
                } else {
                    in.getBytes((int)idx, (byte[])output, (int)outputPtr, (int)length);
                }
                chunkLength = length;
            } else {
                byte[] input;
                int inputPtr;
                if (in.hasArray()) {
                    input = in.array();
                    inputPtr = in.arrayOffset() + idx;
                } else {
                    input = new byte[length];
                    in.getBytes((int)idx, (byte[])input);
                    inputPtr = 0;
                }
                if (checksum != null) {
                    checksum.reset();
                    checksum.update((byte[])input, (int)inputPtr, (int)length);
                    out.setInt((int)(outputIdx + 4), (int)((int)checksum.getValue()));
                }
                int maxOutputLength = FastLz.calculateOutputBufferLength((int)length);
                out.ensureWritable((int)(outputOffset + 4 + maxOutputLength));
                byte[] output = out.array();
                int outputPtr = out.arrayOffset() + outputOffset + 4;
                int compressedLength = FastLz.compress((byte[])input, (int)inputPtr, (int)length, (byte[])output, (int)outputPtr, (int)this.level);
                if (compressedLength < length) {
                    blockType = 1;
                    chunkLength = compressedLength;
                    out.setShort((int)outputOffset, (int)chunkLength);
                    outputOffset += 2;
                } else {
                    blockType = 0;
                    System.arraycopy((Object)input, (int)inputPtr, (Object)output, (int)(outputPtr - 2), (int)length);
                    chunkLength = length;
                }
            }
            out.setShort((int)outputOffset, (int)length);
            out.setByte((int)(outputIdx + 3), (int)(blockType | (checksum != null ? 16 : 0)));
            out.writerIndex((int)(outputOffset + 2 + chunkLength));
            in.skipBytes((int)length);
        }
        return;
    }
}

