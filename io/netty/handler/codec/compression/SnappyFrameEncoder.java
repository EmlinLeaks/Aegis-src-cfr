/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.Snappy;

public class SnappyFrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_COMPRESSIBLE_LENGTH = 18;
    private static final byte[] STREAM_START = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final Snappy snappy = new Snappy();
    private boolean started;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int dataLength;
        if (!in.isReadable()) {
            return;
        }
        if (!this.started) {
            this.started = true;
            out.writeBytes((byte[])STREAM_START);
        }
        if ((dataLength = in.readableBytes()) <= 18) {
            SnappyFrameEncoder.writeUnencodedChunk((ByteBuf)in, (ByteBuf)out, (int)dataLength);
            return;
        }
        do {
            ByteBuf slice;
            int lengthIdx = out.writerIndex() + 1;
            if (dataLength < 18) {
                slice = in.readSlice((int)dataLength);
                SnappyFrameEncoder.writeUnencodedChunk((ByteBuf)slice, (ByteBuf)out, (int)dataLength);
                return;
            }
            out.writeInt((int)0);
            if (dataLength <= 32767) {
                slice = in.readSlice((int)dataLength);
                SnappyFrameEncoder.calculateAndWriteChecksum((ByteBuf)slice, (ByteBuf)out);
                this.snappy.encode((ByteBuf)slice, (ByteBuf)out, (int)dataLength);
                SnappyFrameEncoder.setChunkLength((ByteBuf)out, (int)lengthIdx);
                return;
            }
            slice = in.readSlice((int)32767);
            SnappyFrameEncoder.calculateAndWriteChecksum((ByteBuf)slice, (ByteBuf)out);
            this.snappy.encode((ByteBuf)slice, (ByteBuf)out, (int)32767);
            SnappyFrameEncoder.setChunkLength((ByteBuf)out, (int)lengthIdx);
            dataLength -= 32767;
        } while (true);
    }

    private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
        out.writeByte((int)1);
        SnappyFrameEncoder.writeChunkLength((ByteBuf)out, (int)(dataLength + 4));
        SnappyFrameEncoder.calculateAndWriteChecksum((ByteBuf)in, (ByteBuf)out);
        out.writeBytes((ByteBuf)in, (int)dataLength);
    }

    private static void setChunkLength(ByteBuf out, int lengthIdx) {
        int chunkLength = out.writerIndex() - lengthIdx - 3;
        if (chunkLength >>> 24 != 0) {
            throw new CompressionException((String)("compressed data too large: " + chunkLength));
        }
        out.setMediumLE((int)lengthIdx, (int)chunkLength);
    }

    private static void writeChunkLength(ByteBuf out, int chunkLength) {
        out.writeMediumLE((int)chunkLength);
    }

    private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
        out.writeIntLE((int)Snappy.calculateChecksum((ByteBuf)slice));
    }
}

