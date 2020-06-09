/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.Bzip2BitReader;
import io.netty.handler.codec.compression.Bzip2BlockDecompressor;
import io.netty.handler.codec.compression.Bzip2Decoder;
import io.netty.handler.codec.compression.Bzip2HuffmanStageDecoder;
import io.netty.handler.codec.compression.Bzip2MoveToFrontTable;
import io.netty.handler.codec.compression.DecompressionException;
import java.util.List;

public class Bzip2Decoder
extends ByteToMessageDecoder {
    private State currentState = State.INIT;
    private final Bzip2BitReader reader = new Bzip2BitReader();
    private Bzip2BlockDecompressor blockDecompressor;
    private Bzip2HuffmanStageDecoder huffmanStageDecoder;
    private int blockSize;
    private int blockCRC;
    private int streamCRC;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        reader = this.reader;
        reader.setByteBuf((ByteBuf)in);
        block15 : do {
            switch (1.$SwitchMap$io$netty$handler$codec$compression$Bzip2Decoder$State[this.currentState.ordinal()]) {
                case 1: {
                    if (in.readableBytes() < 4) {
                        return;
                    }
                    magicNumber = in.readUnsignedMedium();
                    if (magicNumber != 4348520) {
                        throw new DecompressionException((String)"Unexpected stream identifier contents. Mismatched bzip2 protocol version?");
                    }
                    blockSize = in.readByte() - 48;
                    if (blockSize < 1) throw new DecompressionException((String)"block size is invalid");
                    if (blockSize > 9) {
                        throw new DecompressionException((String)"block size is invalid");
                    }
                    this.blockSize = blockSize * 100000;
                    this.streamCRC = 0;
                    this.currentState = State.INIT_BLOCK;
                }
                case 2: {
                    if (!reader.hasReadableBytes((int)10)) {
                        return;
                    }
                    magic1 = reader.readBits((int)24);
                    magic2 = reader.readBits((int)24);
                    if (magic1 == 1536581 && magic2 == 3690640) {
                        storedCombinedCRC = reader.readInt();
                        if (storedCombinedCRC != this.streamCRC) {
                            throw new DecompressionException((String)"stream CRC error");
                        }
                        this.currentState = State.EOF;
                        continue block15;
                    }
                    if (magic1 != 3227993) throw new DecompressionException((String)"bad block header");
                    if (magic2 != 2511705) {
                        throw new DecompressionException((String)"bad block header");
                    }
                    this.blockCRC = reader.readInt();
                    this.currentState = State.INIT_BLOCK_PARAMS;
                }
                case 3: {
                    if (!reader.hasReadableBits((int)25)) {
                        return;
                    }
                    blockRandomised = reader.readBoolean();
                    bwtStartPointer = reader.readBits((int)24);
                    this.blockDecompressor = new Bzip2BlockDecompressor((int)this.blockSize, (int)this.blockCRC, (boolean)blockRandomised, (int)bwtStartPointer, (Bzip2BitReader)reader);
                    this.currentState = State.RECEIVE_HUFFMAN_USED_MAP;
                }
                case 4: {
                    if (!reader.hasReadableBits((int)16)) {
                        return;
                    }
                    this.blockDecompressor.huffmanInUse16 = reader.readBits((int)16);
                    this.currentState = State.RECEIVE_HUFFMAN_USED_BITMAPS;
                }
                case 5: {
                    blockDecompressor = this.blockDecompressor;
                    inUse16 = blockDecompressor.huffmanInUse16;
                    bitNumber = Integer.bitCount((int)inUse16);
                    huffmanSymbolMap = blockDecompressor.huffmanSymbolMap;
                    if (!reader.hasReadableBits((int)(bitNumber * 16 + 3))) {
                        return;
                    }
                    huffmanSymbolCount = 0;
                    if (bitNumber > 0) {
                        for (i = 0; i < 16; ++i) {
                            if ((inUse16 & 32768 >>> i) == 0) continue;
                            k = i << 4;
                            for (j = 0; j < 16; ++j, ++k) {
                                if (!reader.readBoolean()) continue;
                                huffmanSymbolMap[huffmanSymbolCount++] = (byte)k;
                            }
                        }
                    }
                    blockDecompressor.huffmanEndOfBlockSymbol = huffmanSymbolCount + 1;
                    totalTables = reader.readBits((int)3);
                    if (totalTables < 2) throw new DecompressionException((String)"incorrect huffman groups number");
                    if (totalTables > 6) {
                        throw new DecompressionException((String)"incorrect huffman groups number");
                    }
                    alphaSize = huffmanSymbolCount + 2;
                    if (alphaSize > 258) {
                        throw new DecompressionException((String)"incorrect alphabet size");
                    }
                    this.huffmanStageDecoder = new Bzip2HuffmanStageDecoder((Bzip2BitReader)reader, (int)totalTables, (int)alphaSize);
                    this.currentState = State.RECEIVE_SELECTORS_NUMBER;
                }
                case 6: {
                    if (!reader.hasReadableBits((int)15)) {
                        return;
                    }
                    totalSelectors = reader.readBits((int)15);
                    if (totalSelectors < 1) throw new DecompressionException((String)"incorrect selectors number");
                    if (totalSelectors > 18002) {
                        throw new DecompressionException((String)"incorrect selectors number");
                    }
                    this.huffmanStageDecoder.selectors = new byte[totalSelectors];
                    this.currentState = State.RECEIVE_SELECTORS;
                }
                case 7: {
                    huffmanStageDecoder = this.huffmanStageDecoder;
                    selectors = huffmanStageDecoder.selectors;
                    totalSelectors = selectors.length;
                    tableMtf = huffmanStageDecoder.tableMTF;
                    for (currSelector = huffmanStageDecoder.currentSelector; currSelector < totalSelectors; ++currSelector) {
                        if (!reader.hasReadableBits((int)6)) {
                            huffmanStageDecoder.currentSelector = currSelector;
                            return;
                        }
                        index = 0;
                        while (reader.readBoolean()) {
                            ++index;
                        }
                        selectors[currSelector] = tableMtf.indexToFront((int)index);
                    }
                    this.currentState = State.RECEIVE_HUFFMAN_LENGTH;
                }
                case 8: {
                    huffmanStageDecoder = this.huffmanStageDecoder;
                    totalTables = huffmanStageDecoder.totalTables;
                    codeLength = huffmanStageDecoder.tableCodeLengths;
                    alphaSize = huffmanStageDecoder.alphabetSize;
                    currLength = huffmanStageDecoder.currentLength;
                    currAlpha = 0;
                    modifyLength = huffmanStageDecoder.modifyLength;
                    saveStateAndReturn = false;
                    block20 : for (currGroup = huffmanStageDecoder.currentGroup; currGroup < totalTables; ++currGroup) {
                        if (reader.hasReadableBits((int)5)) ** GOTO lbl113
                        saveStateAndReturn = true;
                        ** GOTO lbl142
lbl113: // 1 sources:
                        if (currLength < 0) {
                            currLength = reader.readBits((int)5);
                        }
                        currAlpha = huffmanStageDecoder.currentAlpha;
lbl116: // 2 sources:
                        do {
                            if (currAlpha >= alphaSize) ** GOTO lbl121
                            if (reader.isReadable()) ** GOTO lbl129
                            saveStateAndReturn = true;
                            ** GOTO lbl142
lbl121: // 1 sources:
                            currLength = -1;
                            huffmanStageDecoder.currentAlpha = 0;
                            currAlpha = 0;
                            modifyLength = false;
                            continue block20;
                            break;
                        } while (true);
                    }
                    ** GOTO lbl142
                }
                default: {
                    throw new IllegalStateException();
                }
lbl129: // 2 sources:
                while (modifyLength || reader.readBoolean()) {
                    if (!reader.isReadable()) {
                        modifyLength = true;
                        saveStateAndReturn = true;
                    } else {
                        currLength += reader.readBoolean() != false ? -1 : 1;
                        modifyLength = false;
                        if (reader.isReadable()) continue;
                        saveStateAndReturn = true;
                    }
                    ** GOTO lbl142
                }
                codeLength[currGroup][currAlpha] = (byte)currLength;
                ++currAlpha;
                ** continue;
lbl142: // 5 sources:
                if (saveStateAndReturn) {
                    huffmanStageDecoder.currentGroup = currGroup;
                    huffmanStageDecoder.currentLength = currLength;
                    huffmanStageDecoder.currentAlpha = currAlpha;
                    huffmanStageDecoder.modifyLength = modifyLength;
                    return;
                }
                huffmanStageDecoder.createHuffmanDecodingTables();
                this.currentState = State.DECODE_HUFFMAN_DATA;
                case 9: {
                    blockDecompressor = this.blockDecompressor;
                    oldReaderIndex = in.readerIndex();
                    decoded = blockDecompressor.decodeHuffmanData((Bzip2HuffmanStageDecoder)this.huffmanStageDecoder);
                    if (!decoded) {
                        return;
                    }
                    if (in.readerIndex() == oldReaderIndex && in.isReadable()) {
                        reader.refill();
                    }
                    blockLength = blockDecompressor.blockLength();
                    uncompressed = ctx.alloc().buffer((int)blockLength);
                    success = false;
                    try {
                        while ((uncByte = blockDecompressor.read()) >= 0) {
                            uncompressed.writeByte((int)uncByte);
                        }
                        currentBlockCRC = blockDecompressor.checkCRC();
                        this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ currentBlockCRC;
                        out.add((Object)uncompressed);
                        success = true;
                    }
                    finally {
                        if (!success) {
                            uncompressed.release();
                        }
                    }
                    this.currentState = State.INIT_BLOCK;
                    continue block15;
                }
                case 10: 
            }
            break;
        } while (true);
        in.skipBytes((int)in.readableBytes());
    }

    public boolean isClosed() {
        if (this.currentState != State.EOF) return false;
        return true;
    }
}

