/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.FastLz;
import io.netty.handler.codec.compression.FastLzFrameDecoder;
import io.netty.util.internal.EmptyArrays;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameDecoder
extends ByteToMessageDecoder {
    private State currentState = State.INIT_BLOCK;
    private final Checksum checksum;
    private int chunkLength;
    private int originalLength;
    private boolean isCompressed;
    private boolean hasChecksum;
    private int currentChecksum;

    public FastLzFrameDecoder() {
        this((boolean)false);
    }

    public FastLzFrameDecoder(boolean validateChecksums) {
        this((Checksum)(validateChecksums ? new Adler32() : null));
    }

    public FastLzFrameDecoder(Checksum checksum) {
        this.checksum = checksum;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            switch (1.$SwitchMap$io$netty$handler$codec$compression$FastLzFrameDecoder$State[this.currentState.ordinal()]) {
                case 1: {
                    if (in.readableBytes() < 4) {
                        return;
                    }
                    int magic = in.readUnsignedMedium();
                    if (magic != 4607066) {
                        throw new DecompressionException((String)"unexpected block identifier");
                    }
                    byte options = in.readByte();
                    this.isCompressed = (options & 1) == 1;
                    this.hasChecksum = (options & 16) == 16;
                    this.currentState = State.INIT_BLOCK_PARAMS;
                }
                case 2: {
                    if (in.readableBytes() < 2 + (this.isCompressed ? 2 : 0) + (this.hasChecksum ? 4 : 0)) {
                        return;
                    }
                    this.currentChecksum = this.hasChecksum ? in.readInt() : 0;
                    this.chunkLength = in.readUnsignedShort();
                    this.originalLength = this.isCompressed ? in.readUnsignedShort() : this.chunkLength;
                    this.currentState = State.DECOMPRESS_DATA;
                }
                case 3: {
                    byte[] output;
                    int outputPtr;
                    ByteBuf uncompressed;
                    int chunkLength = this.chunkLength;
                    if (in.readableBytes() < chunkLength) {
                        return;
                    }
                    int idx = in.readerIndex();
                    int originalLength = this.originalLength;
                    if (originalLength != 0) {
                        uncompressed = ctx.alloc().heapBuffer((int)originalLength, (int)originalLength);
                        output = uncompressed.array();
                        outputPtr = uncompressed.arrayOffset() + uncompressed.writerIndex();
                    } else {
                        uncompressed = null;
                        output = EmptyArrays.EMPTY_BYTES;
                        outputPtr = 0;
                    }
                    boolean success = false;
                    try {
                        if (this.isCompressed) {
                            int inputPtr;
                            byte[] input;
                            if (in.hasArray()) {
                                input = in.array();
                                inputPtr = in.arrayOffset() + idx;
                            } else {
                                input = new byte[chunkLength];
                                in.getBytes((int)idx, (byte[])input);
                                inputPtr = 0;
                            }
                            int decompressedBytes = FastLz.decompress((byte[])input, (int)inputPtr, (int)chunkLength, (byte[])output, (int)outputPtr, (int)originalLength);
                            if (originalLength != decompressedBytes) {
                                throw new DecompressionException((String)String.format((String)"stream corrupted: originalLength(%d) and actual length(%d) mismatch", (Object[])new Object[]{Integer.valueOf((int)originalLength), Integer.valueOf((int)decompressedBytes)}));
                            }
                        } else {
                            in.getBytes((int)idx, (byte[])output, (int)outputPtr, (int)chunkLength);
                        }
                        Checksum checksum = this.checksum;
                        if (this.hasChecksum && checksum != null) {
                            checksum.reset();
                            checksum.update((byte[])output, (int)outputPtr, (int)originalLength);
                            int checksumResult = (int)checksum.getValue();
                            if (checksumResult != this.currentChecksum) {
                                throw new DecompressionException((String)String.format((String)"stream corrupted: mismatching checksum: %d (expected: %d)", (Object[])new Object[]{Integer.valueOf((int)checksumResult), Integer.valueOf((int)this.currentChecksum)}));
                            }
                        }
                        if (uncompressed != null) {
                            uncompressed.writerIndex((int)(uncompressed.writerIndex() + originalLength));
                            out.add((Object)uncompressed);
                        }
                        in.skipBytes((int)chunkLength);
                        this.currentState = State.INIT_BLOCK;
                        success = true;
                        return;
                    }
                    finally {
                        if (!success && uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                }
                case 4: {
                    in.skipBytes((int)in.readableBytes());
                    return;
                }
            }
            throw new IllegalStateException();
        }
        catch (Exception e) {
            this.currentState = State.CORRUPTED;
            throw e;
        }
    }
}

