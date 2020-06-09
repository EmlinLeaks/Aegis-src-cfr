/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.jpountz.lz4.LZ4Exception
 *  net.jpountz.lz4.LZ4Factory
 *  net.jpountz.lz4.LZ4FastDecompressor
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.CompressionUtil;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Lz4FrameDecoder;
import io.netty.handler.codec.compression.Lz4XXHash32;
import io.netty.util.ReferenceCounted;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class Lz4FrameDecoder
extends ByteToMessageDecoder {
    private State currentState = State.INIT_BLOCK;
    private LZ4FastDecompressor decompressor;
    private ByteBufChecksum checksum;
    private int blockType;
    private int compressedLength;
    private int decompressedLength;
    private int currentChecksum;

    public Lz4FrameDecoder() {
        this((boolean)false);
    }

    public Lz4FrameDecoder(boolean validateChecksums) {
        this((LZ4Factory)LZ4Factory.fastestInstance(), (boolean)validateChecksums);
    }

    public Lz4FrameDecoder(LZ4Factory factory, boolean validateChecksums) {
        this((LZ4Factory)factory, (Checksum)(validateChecksums ? new Lz4XXHash32((int)-1756908916) : null));
    }

    public Lz4FrameDecoder(LZ4Factory factory, Checksum checksum) {
        if (factory == null) {
            throw new NullPointerException((String)"factory");
        }
        this.decompressor = factory.fastDecompressor();
        this.checksum = checksum == null ? null : ByteBufChecksum.wrapChecksum((Checksum)checksum);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            switch (1.$SwitchMap$io$netty$handler$codec$compression$Lz4FrameDecoder$State[this.currentState.ordinal()]) {
                case 1: {
                    if (in.readableBytes() < 21) {
                        return;
                    }
                    magic = in.readLong();
                    if (magic != 5501767354678207339L) {
                        throw new DecompressionException((String)"unexpected block identifier");
                    }
                    token = in.readByte();
                    compressionLevel = (token & 15) + 10;
                    blockType = token & 240;
                    compressedLength = Integer.reverseBytes((int)in.readInt());
                    if (compressedLength < 0 || compressedLength > 33554432) {
                        throw new DecompressionException((String)String.format((String)"invalid compressedLength: %d (expected: 0-%d)", (Object[])new Object[]{Integer.valueOf((int)compressedLength), Integer.valueOf((int)33554432)}));
                    }
                    decompressedLength = Integer.reverseBytes((int)in.readInt());
                    maxDecompressedLength = 1 << compressionLevel;
                    if (decompressedLength < 0 || decompressedLength > maxDecompressedLength) {
                        throw new DecompressionException((String)String.format((String)"invalid decompressedLength: %d (expected: 0-%d)", (Object[])new Object[]{Integer.valueOf((int)decompressedLength), Integer.valueOf((int)maxDecompressedLength)}));
                    }
                    if (decompressedLength == 0 && compressedLength != 0 || decompressedLength != 0 && compressedLength == 0 || blockType == 16 && decompressedLength != compressedLength) {
                        throw new DecompressionException((String)String.format((String)"stream corrupted: compressedLength(%d) and decompressedLength(%d) mismatch", (Object[])new Object[]{Integer.valueOf((int)compressedLength), Integer.valueOf((int)decompressedLength)}));
                    }
                    currentChecksum = Integer.reverseBytes((int)in.readInt());
                    if (decompressedLength == 0 && compressedLength == 0) {
                        if (currentChecksum != 0) {
                            throw new DecompressionException((String)"stream corrupted: checksum error");
                        }
                        this.currentState = State.FINISHED;
                        this.decompressor = null;
                        this.checksum = null;
                        return;
                    }
                    this.blockType = blockType;
                    this.compressedLength = compressedLength;
                    this.decompressedLength = decompressedLength;
                    this.currentChecksum = currentChecksum;
                    this.currentState = State.DECOMPRESS_DATA;
                }
                case 2: {
                    blockType = this.blockType;
                    compressedLength = this.compressedLength;
                    decompressedLength = this.decompressedLength;
                    currentChecksum = this.currentChecksum;
                    if (in.readableBytes() < compressedLength) {
                        return;
                    }
                    checksum = this.checksum;
                    uncompressed = null;
                    try {
                        switch (blockType) {
                            case 16: {
                                uncompressed = in.retainedSlice((int)in.readerIndex(), (int)decompressedLength);
                                ** break;
                            }
                            case 32: {
                                uncompressed = ctx.alloc().buffer((int)decompressedLength, (int)decompressedLength);
                                this.decompressor.decompress((ByteBuffer)CompressionUtil.safeNioBuffer((ByteBuf)in), (ByteBuffer)uncompressed.internalNioBuffer((int)uncompressed.writerIndex(), (int)decompressedLength));
                                uncompressed.writerIndex((int)(uncompressed.writerIndex() + decompressedLength));
                                ** break;
                            }
                        }
                        throw new DecompressionException((String)String.format((String)"unexpected blockType: %d (expected: %d or %d)", (Object[])new Object[]{Integer.valueOf((int)blockType), Integer.valueOf((int)16), Integer.valueOf((int)32)}));
lbl55: // 2 sources:
                        in.skipBytes((int)compressedLength);
                        if (checksum != null) {
                            CompressionUtil.checkChecksum((ByteBufChecksum)checksum, (ByteBuf)uncompressed, (int)currentChecksum);
                        }
                        out.add((Object)uncompressed);
                        uncompressed = null;
                        this.currentState = State.INIT_BLOCK;
                        return;
                    }
                    catch (LZ4Exception e) {
                        throw new DecompressionException((Throwable)e);
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                }
                case 3: 
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

    public boolean isClosed() {
        if (this.currentState != State.FINISHED) return false;
        return true;
    }
}

