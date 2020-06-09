/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import java.util.List;

public class SnappyFrameDecoder
extends ByteToMessageDecoder {
    private static final int SNAPPY_IDENTIFIER_LEN = 6;
    private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
    private final Snappy snappy = new Snappy();
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;

    public SnappyFrameDecoder() {
        this((boolean)false);
    }

    public SnappyFrameDecoder(boolean validateChecksums) {
        this.validateChecksums = validateChecksums;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.corrupted) {
            in.skipBytes((int)in.readableBytes());
            return;
        }
        try {
            idx = in.readerIndex();
            inSize = in.readableBytes();
            if (inSize < 4) {
                return;
            }
            chunkTypeVal = in.getUnsignedByte((int)idx);
            chunkType = SnappyFrameDecoder.mapChunkType((byte)((byte)chunkTypeVal));
            chunkLength = in.getUnsignedMediumLE((int)(idx + 1));
            switch (1.$SwitchMap$io$netty$handler$codec$compression$SnappyFrameDecoder$ChunkType[chunkType.ordinal()]) {
                case 1: {
                    if (chunkLength != 6) {
                        throw new DecompressionException((String)("Unexpected length of stream identifier: " + chunkLength));
                    }
                    if (inSize < 10) {
                        return;
                    }
                    in.skipBytes((int)4);
                    offset = in.readerIndex();
                    in.skipBytes((int)6);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset++), (byte)115);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset++), (byte)78);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset++), (byte)97);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset++), (byte)80);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset++), (byte)112);
                    SnappyFrameDecoder.checkByte((byte)in.getByte((int)offset), (byte)89);
                    this.started = true;
                    return;
                }
                case 2: {
                    if (!this.started) {
                        throw new DecompressionException((String)"Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes((int)(4 + chunkLength));
                    return;
                }
                case 3: {
                    throw new DecompressionException((String)("Found reserved unskippable chunk type: 0x" + Integer.toHexString((int)chunkTypeVal)));
                }
                case 4: {
                    if (!this.started) {
                        throw new DecompressionException((String)"Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 65540) {
                        throw new DecompressionException((String)"Received UNCOMPRESSED_DATA larger than 65540 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes((int)4);
                    if (this.validateChecksums) {
                        checksum = in.readIntLE();
                        Snappy.validateChecksum((int)checksum, (ByteBuf)in, (int)in.readerIndex(), (int)(chunkLength - 4));
                    } else {
                        in.skipBytes((int)4);
                    }
                    out.add((Object)in.readRetainedSlice((int)(chunkLength - 4)));
                    return;
                }
                case 5: {
                    if (!this.started) {
                        throw new DecompressionException((String)"Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes((int)4);
                    checksum = in.readIntLE();
                    uncompressed = ctx.alloc().buffer();
                    try {
                        if (!this.validateChecksums) ** GOTO lbl83
                        oldWriterIndex = in.writerIndex();
                        try {
                            in.writerIndex((int)(in.readerIndex() + chunkLength - 4));
                            this.snappy.decode((ByteBuf)in, (ByteBuf)uncompressed);
                        }
                        finally {
                            in.writerIndex((int)oldWriterIndex);
                        }
                        Snappy.validateChecksum((int)checksum, (ByteBuf)uncompressed, (int)0, (int)uncompressed.writerIndex());
                        ** GOTO lbl84
lbl83: // 1 sources:
                        this.snappy.decode((ByteBuf)in.readSlice((int)(chunkLength - 4)), (ByteBuf)uncompressed);
lbl84: // 2 sources:
                        out.add((Object)uncompressed);
                        uncompressed = null;
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                    this.snappy.reset();
                }
            }
            return;
        }
        catch (Exception e) {
            this.corrupted = true;
            throw e;
        }
    }

    private static void checkByte(byte actual, byte expect) {
        if (actual == expect) return;
        throw new DecompressionException((String)"Unexpected stream identifier contents. Mismatched snappy protocol version?");
    }

    private static ChunkType mapChunkType(byte type) {
        if (type == 0) {
            return ChunkType.COMPRESSED_DATA;
        }
        if (type == 1) {
            return ChunkType.UNCOMPRESSED_DATA;
        }
        if (type == -1) {
            return ChunkType.STREAM_IDENTIFIER;
        }
        if ((type & 128) != 128) return ChunkType.RESERVED_UNSKIPPABLE;
        return ChunkType.RESERVED_SKIPPABLE;
    }
}

