/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.Bzip2BitWriter;
import io.netty.handler.codec.compression.Bzip2BlockCompressor;
import io.netty.handler.codec.compression.Bzip2DivSufSort;
import io.netty.handler.codec.compression.Bzip2HuffmanStageEncoder;
import io.netty.handler.codec.compression.Bzip2MTFAndRLE2StageEncoder;
import io.netty.handler.codec.compression.Crc32;
import io.netty.util.ByteProcessor;

final class Bzip2BlockCompressor {
    private final ByteProcessor writeProcessor = new ByteProcessor((Bzip2BlockCompressor)this){
        final /* synthetic */ Bzip2BlockCompressor this$0;
        {
            this.this$0 = this$0;
        }

        public boolean process(byte value) throws java.lang.Exception {
            return this.this$0.write((int)value);
        }
    };
    private final Bzip2BitWriter writer;
    private final Crc32 crc = new Crc32();
    private final byte[] block;
    private int blockLength;
    private final int blockLengthLimit;
    private final boolean[] blockValuesPresent = new boolean[256];
    private final int[] bwtBlock;
    private int rleCurrentValue = -1;
    private int rleLength;

    Bzip2BlockCompressor(Bzip2BitWriter writer, int blockSize) {
        this.writer = writer;
        this.block = new byte[blockSize + 1];
        this.bwtBlock = new int[blockSize + 1];
        this.blockLengthLimit = blockSize - 6;
    }

    private void writeSymbolMap(ByteBuf out) {
        int j;
        int k;
        Bzip2BitWriter writer = this.writer;
        boolean[] blockValuesPresent = this.blockValuesPresent;
        boolean[] condensedInUse = new boolean[16];
        for (int i = 0; i < condensedInUse.length; ++i) {
            k = i << 4;
            for (j = 0; j < 16; ++j, ++k) {
                if (!blockValuesPresent[k]) continue;
                condensedInUse[i] = true;
            }
        }
        for (boolean isCondensedInUse : condensedInUse) {
            writer.writeBoolean((ByteBuf)out, (boolean)isCondensedInUse);
        }
        int i = 0;
        while (i < condensedInUse.length) {
            if (condensedInUse[i]) {
                k = i << 4;
                for (j = 0; j < 16; ++j, ++k) {
                    writer.writeBoolean((ByteBuf)out, (boolean)blockValuesPresent[k]);
                }
            }
            ++i;
        }
    }

    private void writeRun(int value, int runLength) {
        int blockLength = this.blockLength;
        byte[] block = this.block;
        this.blockValuesPresent[value] = true;
        this.crc.updateCRC((int)value, (int)runLength);
        byte byteValue = (byte)value;
        switch (runLength) {
            case 1: {
                block[blockLength] = byteValue;
                this.blockLength = blockLength + 1;
                return;
            }
            case 2: {
                block[blockLength] = byteValue;
                block[blockLength + 1] = byteValue;
                this.blockLength = blockLength + 2;
                return;
            }
            case 3: {
                block[blockLength] = byteValue;
                block[blockLength + 1] = byteValue;
                block[blockLength + 2] = byteValue;
                this.blockLength = blockLength + 3;
                return;
            }
        }
        this.blockValuesPresent[runLength -= 4] = true;
        block[blockLength] = byteValue;
        block[blockLength + 1] = byteValue;
        block[blockLength + 2] = byteValue;
        block[blockLength + 3] = byteValue;
        block[blockLength + 4] = (byte)runLength;
        this.blockLength = blockLength + 5;
    }

    boolean write(int value) {
        if (this.blockLength > this.blockLengthLimit) {
            return false;
        }
        int rleCurrentValue = this.rleCurrentValue;
        int rleLength = this.rleLength;
        if (rleLength == 0) {
            this.rleCurrentValue = value;
            this.rleLength = 1;
            return true;
        }
        if (rleCurrentValue != value) {
            this.writeRun((int)(rleCurrentValue & 255), (int)rleLength);
            this.rleCurrentValue = value;
            this.rleLength = 1;
            return true;
        }
        if (rleLength == 254) {
            this.writeRun((int)(rleCurrentValue & 255), (int)255);
            this.rleLength = 0;
            return true;
        }
        this.rleLength = rleLength + 1;
        return true;
    }

    int write(ByteBuf buffer, int offset, int length) {
        int n;
        int index = buffer.forEachByte((int)offset, (int)length, (ByteProcessor)this.writeProcessor);
        if (index == -1) {
            n = length;
            return n;
        }
        n = index - offset;
        return n;
    }

    void close(ByteBuf out) {
        if (this.rleLength > 0) {
            this.writeRun((int)(this.rleCurrentValue & 255), (int)this.rleLength);
        }
        this.block[this.blockLength] = this.block[0];
        Bzip2DivSufSort divSufSort = new Bzip2DivSufSort((byte[])this.block, (int[])this.bwtBlock, (int)this.blockLength);
        int bwtStartPointer = divSufSort.bwt();
        Bzip2BitWriter writer = this.writer;
        writer.writeBits((ByteBuf)out, (int)24, (long)3227993L);
        writer.writeBits((ByteBuf)out, (int)24, (long)2511705L);
        writer.writeInt((ByteBuf)out, (int)this.crc.getCRC());
        writer.writeBoolean((ByteBuf)out, (boolean)false);
        writer.writeBits((ByteBuf)out, (int)24, (long)((long)bwtStartPointer));
        this.writeSymbolMap((ByteBuf)out);
        Bzip2MTFAndRLE2StageEncoder mtfEncoder = new Bzip2MTFAndRLE2StageEncoder((int[])this.bwtBlock, (int)this.blockLength, (boolean[])this.blockValuesPresent);
        mtfEncoder.encode();
        Bzip2HuffmanStageEncoder huffmanEncoder = new Bzip2HuffmanStageEncoder((Bzip2BitWriter)writer, (char[])mtfEncoder.mtfBlock(), (int)mtfEncoder.mtfLength(), (int)mtfEncoder.mtfAlphabetSize(), (int[])mtfEncoder.mtfSymbolFrequencies());
        huffmanEncoder.encode((ByteBuf)out);
    }

    int availableSize() {
        if (this.blockLength != 0) return this.blockLengthLimit - this.blockLength + 1;
        return this.blockLengthLimit + 2;
    }

    boolean isFull() {
        if (this.blockLength <= this.blockLengthLimit) return false;
        return true;
    }

    boolean isEmpty() {
        if (this.blockLength != 0) return false;
        if (this.rleLength != 0) return false;
        return true;
    }

    int crc() {
        return this.crc.getCRC();
    }
}

