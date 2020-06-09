/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

class Bzip2BitReader {
    private static final int MAX_COUNT_OF_READABLE_BYTES = 268435455;
    private ByteBuf in;
    private long bitBuffer;
    private int bitCount;

    Bzip2BitReader() {
    }

    void setByteBuf(ByteBuf in) {
        this.in = in;
    }

    /*
     * Unable to fully structure code
     */
    int readBits(int count) {
        if (count < 0) throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-32 )"));
        if (count > 32) {
            throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-32 )"));
        }
        bitCount = this.bitCount;
        bitBuffer = this.bitBuffer;
        if (bitCount < count) {
            switch (this.in.readableBytes()) {
                case 1: {
                    readData = (long)this.in.readUnsignedByte();
                    offset = 8;
                    ** break;
                }
                case 2: {
                    readData = (long)this.in.readUnsignedShort();
                    offset = 16;
                    ** break;
                }
                case 3: {
                    readData = (long)this.in.readUnsignedMedium();
                    offset = 24;
                    ** break;
                }
            }
            readData = this.in.readUnsignedInt();
            offset = 32;
lbl22: // 4 sources:
            bitBuffer = bitBuffer << offset | readData;
            bitCount += offset;
            this.bitBuffer = bitBuffer;
        }
        this.bitCount = bitCount -= count;
        if (count != 32) {
            v0 = (long)((1 << count) - 1);
            return (int)(bitBuffer >>> bitCount & v0);
        }
        v0 = 0xFFFFFFFFL;
        return (int)(bitBuffer >>> bitCount & v0);
    }

    boolean readBoolean() {
        if (this.readBits((int)1) == 0) return false;
        return true;
    }

    int readInt() {
        return this.readBits((int)32);
    }

    void refill() {
        short readData = this.in.readUnsignedByte();
        this.bitBuffer = this.bitBuffer << 8 | (long)readData;
        this.bitCount += 8;
    }

    boolean isReadable() {
        if (this.bitCount > 0) return true;
        if (this.in.isReadable()) return true;
        return false;
    }

    boolean hasReadableBits(int count) {
        if (count < 0) {
            throw new IllegalArgumentException((String)("count: " + count + " (expected value greater than 0)"));
        }
        if (this.bitCount >= count) return true;
        if ((this.in.readableBytes() << 3 & Integer.MAX_VALUE) >= count - this.bitCount) return true;
        return false;
    }

    boolean hasReadableBytes(int count) {
        if (count < 0) throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-" + 268435455 + ')'));
        if (count <= 268435455) return this.hasReadableBits((int)(count << 3));
        throw new IllegalArgumentException((String)("count: " + count + " (expected: 0-" + 268435455 + ')'));
    }
}

