/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

final class Bzip2BitWriter {
    private long bitBuffer;
    private int bitCount;

    Bzip2BitWriter() {
    }

    void writeBits(ByteBuf out, int count, long value) {
        if (count < 0) throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-32)"));
        if (count > 32) {
            throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-32)"));
        }
        int bitCount = this.bitCount;
        long bitBuffer = this.bitBuffer | value << 64 - count >>> bitCount;
        if ((bitCount += count) >= 32) {
            out.writeInt((int)((int)(bitBuffer >>> 32)));
            bitBuffer <<= 32;
            bitCount -= 32;
        }
        this.bitBuffer = bitBuffer;
        this.bitCount = bitCount;
    }

    void writeBoolean(ByteBuf out, boolean value) {
        int bitCount = this.bitCount + 1;
        long bitBuffer = this.bitBuffer | (value ? 1L << 64 - bitCount : 0L);
        if (bitCount == 32) {
            out.writeInt((int)((int)(bitBuffer >>> 32)));
            bitBuffer = 0L;
            bitCount = 0;
        }
        this.bitBuffer = bitBuffer;
        this.bitCount = bitCount;
    }

    void writeUnary(ByteBuf out, int value) {
        if (value < 0) {
            throw new IllegalArgumentException((String)("value: " + value + " (expected 0 or more)"));
        }
        do {
            if (value-- <= 0) {
                this.writeBoolean((ByteBuf)out, (boolean)false);
                return;
            }
            this.writeBoolean((ByteBuf)out, (boolean)true);
        } while (true);
    }

    void writeInt(ByteBuf out, int value) {
        this.writeBits((ByteBuf)out, (int)32, (long)((long)value));
    }

    void flush(ByteBuf out) {
        int bitCount = this.bitCount;
        if (bitCount <= 0) return;
        long bitBuffer = this.bitBuffer;
        int shiftToRight = 64 - bitCount;
        if (bitCount <= 8) {
            out.writeByte((int)((int)(bitBuffer >>> shiftToRight << 8 - bitCount)));
            return;
        }
        if (bitCount <= 16) {
            out.writeShort((int)((int)(bitBuffer >>> shiftToRight << 16 - bitCount)));
            return;
        }
        if (bitCount <= 24) {
            out.writeMedium((int)((int)(bitBuffer >>> shiftToRight << 24 - bitCount)));
            return;
        }
        out.writeInt((int)((int)(bitBuffer >>> shiftToRight << 32 - bitCount)));
    }
}

