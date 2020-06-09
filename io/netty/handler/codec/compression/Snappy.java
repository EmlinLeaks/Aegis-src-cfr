/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.Crc32c;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;

public final class Snappy {
    private static final int MAX_HT_SIZE = 16384;
    private static final int MIN_COMPRESSIBLE_BYTES = 15;
    private static final int PREAMBLE_NOT_FULL = -1;
    private static final int NOT_ENOUGH_INPUT = -1;
    private static final int LITERAL = 0;
    private static final int COPY_1_BYTE_OFFSET = 1;
    private static final int COPY_2_BYTE_OFFSET = 2;
    private static final int COPY_4_BYTE_OFFSET = 3;
    private State state = State.READY;
    private byte tag;
    private int written;

    public void reset() {
        this.state = State.READY;
        this.tag = 0;
        this.written = 0;
    }

    public void encode(ByteBuf in, ByteBuf out, int length) {
        int b;
        int inIndex;
        int i = 0;
        while (((b = length >>> i * 7) & -128) != 0) {
            out.writeByte((int)(b & 127 | 128));
            ++i;
        }
        out.writeByte((int)b);
        int baseIndex = inIndex = in.readerIndex();
        short[] table = Snappy.getHashTable((int)length);
        int shift = Integer.numberOfLeadingZeros((int)table.length) + 1;
        int nextEmit = inIndex;
        if (length - inIndex >= 15) {
            int nextHash = Snappy.hash((ByteBuf)in, (int)(++inIndex), (int)shift);
            block1 : do {
                int candidate;
                int insertTail;
                int skip = 32;
                int nextIndex = inIndex;
                do {
                    int bytesBetweenHashLookups;
                    inIndex = nextIndex;
                    int hash = nextHash;
                    if ((nextIndex = inIndex + (bytesBetweenHashLookups = skip++ >> 5)) > length - 4) break block1;
                    nextHash = Snappy.hash((ByteBuf)in, (int)nextIndex, (int)shift);
                    candidate = baseIndex + table[hash];
                    table[hash] = (short)(inIndex - baseIndex);
                } while (in.getInt((int)inIndex) != in.getInt((int)candidate));
                Snappy.encodeLiteral((ByteBuf)in, (ByteBuf)out, (int)(inIndex - nextEmit));
                do {
                    int base = inIndex;
                    int matched = 4 + Snappy.findMatchingLength((ByteBuf)in, (int)(candidate + 4), (int)(inIndex + 4), (int)length);
                    int offset = base - candidate;
                    Snappy.encodeCopy((ByteBuf)out, (int)offset, (int)matched);
                    in.readerIndex((int)(in.readerIndex() + matched));
                    insertTail = (inIndex += matched) - 1;
                    nextEmit = inIndex;
                    if (inIndex >= length - 4) break block1;
                    int prevHash = Snappy.hash((ByteBuf)in, (int)insertTail, (int)shift);
                    table[prevHash] = (short)(inIndex - baseIndex - 1);
                    int currentHash = Snappy.hash((ByteBuf)in, (int)(insertTail + 1), (int)shift);
                    candidate = baseIndex + table[currentHash];
                    table[currentHash] = (short)(inIndex - baseIndex);
                } while (in.getInt((int)(insertTail + 1)) == in.getInt((int)candidate));
                nextHash = Snappy.hash((ByteBuf)in, (int)(insertTail + 2), (int)shift);
                ++inIndex;
            } while (true);
        }
        if (nextEmit >= length) return;
        Snappy.encodeLiteral((ByteBuf)in, (ByteBuf)out, (int)(length - nextEmit));
    }

    private static int hash(ByteBuf in, int index, int shift) {
        return in.getInt((int)index) * 506832829 >>> shift;
    }

    private static short[] getHashTable(int inputSize) {
        int htSize = 256;
        while (htSize < 16384) {
            if (htSize >= inputSize) return new short[htSize];
            htSize <<= 1;
        }
        return new short[htSize];
    }

    private static int findMatchingLength(ByteBuf in, int minIndex, int inIndex, int maxIndex) {
        int matched = 0;
        while (inIndex <= maxIndex - 4 && in.getInt((int)inIndex) == in.getInt((int)(minIndex + matched))) {
            inIndex += 4;
            matched += 4;
        }
        while (inIndex < maxIndex) {
            if (in.getByte((int)(minIndex + matched)) != in.getByte((int)inIndex)) return matched;
            ++inIndex;
            ++matched;
        }
        return matched;
    }

    private static int bitsToEncode(int value) {
        int highestOneBit = Integer.highestOneBit((int)value);
        int bitLength = 0;
        while ((highestOneBit >>= 1) != 0) {
            ++bitLength;
        }
        return bitLength;
    }

    static void encodeLiteral(ByteBuf in, ByteBuf out, int length) {
        if (length < 61) {
            out.writeByte((int)(length - 1 << 2));
        } else {
            int bitLength = Snappy.bitsToEncode((int)(length - 1));
            int bytesToEncode = 1 + bitLength / 8;
            out.writeByte((int)(59 + bytesToEncode << 2));
            for (int i = 0; i < bytesToEncode; ++i) {
                out.writeByte((int)(length - 1 >> i * 8 & 255));
            }
        }
        out.writeBytes((ByteBuf)in, (int)length);
    }

    private static void encodeCopyWithOffset(ByteBuf out, int offset, int length) {
        if (length < 12 && offset < 2048) {
            out.writeByte((int)(1 | length - 4 << 2 | offset >> 8 << 5));
            out.writeByte((int)(offset & 255));
            return;
        }
        out.writeByte((int)(2 | length - 1 << 2));
        out.writeByte((int)(offset & 255));
        out.writeByte((int)(offset >> 8 & 255));
    }

    private static void encodeCopy(ByteBuf out, int offset, int length) {
        while (length >= 68) {
            Snappy.encodeCopyWithOffset((ByteBuf)out, (int)offset, (int)64);
            length -= 64;
        }
        if (length > 64) {
            Snappy.encodeCopyWithOffset((ByteBuf)out, (int)offset, (int)60);
            length -= 60;
        }
        Snappy.encodeCopyWithOffset((ByteBuf)out, (int)offset, (int)length);
    }

    public void decode(ByteBuf in, ByteBuf out) {
        while (in.isReadable()) {
            block0 : switch (1.$SwitchMap$io$netty$handler$codec$compression$Snappy$State[this.state.ordinal()]) {
                case 1: {
                    this.state = State.READING_PREAMBLE;
                }
                case 2: {
                    int uncompressedLength = Snappy.readPreamble((ByteBuf)in);
                    if (uncompressedLength == -1) {
                        return;
                    }
                    if (uncompressedLength == 0) {
                        this.state = State.READY;
                        return;
                    }
                    out.ensureWritable((int)uncompressedLength);
                    this.state = State.READING_TAG;
                }
                case 3: {
                    if (!in.isReadable()) {
                        return;
                    }
                    this.tag = in.readByte();
                    switch (this.tag & 3) {
                        case 0: {
                            this.state = State.READING_LITERAL;
                            break block0;
                        }
                        case 1: 
                        case 2: 
                        case 3: {
                            this.state = State.READING_COPY;
                        }
                    }
                    break;
                }
                case 4: {
                    int literalWritten = Snappy.decodeLiteral((byte)this.tag, (ByteBuf)in, (ByteBuf)out);
                    if (literalWritten == -1) return;
                    this.state = State.READING_TAG;
                    this.written += literalWritten;
                    break;
                }
                case 5: {
                    switch (this.tag & 3) {
                        case 1: {
                            int decodeWritten = Snappy.decodeCopyWith1ByteOffset((byte)this.tag, (ByteBuf)in, (ByteBuf)out, (int)this.written);
                            if (decodeWritten == -1) return;
                            this.state = State.READING_TAG;
                            this.written += decodeWritten;
                            break block0;
                        }
                        case 2: {
                            int decodeWritten = Snappy.decodeCopyWith2ByteOffset((byte)this.tag, (ByteBuf)in, (ByteBuf)out, (int)this.written);
                            if (decodeWritten == -1) return;
                            this.state = State.READING_TAG;
                            this.written += decodeWritten;
                            break block0;
                        }
                        case 3: {
                            int decodeWritten = Snappy.decodeCopyWith4ByteOffset((byte)this.tag, (ByteBuf)in, (ByteBuf)out, (int)this.written);
                            if (decodeWritten == -1) return;
                            this.state = State.READING_TAG;
                            this.written += decodeWritten;
                            break block0;
                        }
                    }
                }
            }
        }
    }

    private static int readPreamble(ByteBuf in) {
        int length = 0;
        int byteIndex = 0;
        do {
            if (!in.isReadable()) return 0;
            short current = in.readUnsignedByte();
            length |= (current & 127) << byteIndex++ * 7;
            if ((current & 128) != 0) continue;
            return length;
        } while (byteIndex < 4);
        throw new DecompressionException((String)"Preamble is greater than 4 bytes");
    }

    /*
     * Unable to fully structure code
     */
    static int decodeLiteral(byte tag, ByteBuf in, ByteBuf out) {
        in.markReaderIndex();
        switch (tag >> 2 & 63) {
            case 60: {
                if (!in.isReadable()) {
                    return -1;
                }
                length = in.readUnsignedByte();
                ** break;
            }
            case 61: {
                if (in.readableBytes() < 2) {
                    return -1;
                }
                length = in.readUnsignedShortLE();
                ** break;
            }
            case 62: {
                if (in.readableBytes() < 3) {
                    return -1;
                }
                length = in.readUnsignedMediumLE();
                ** break;
            }
            case 63: {
                if (in.readableBytes() < 4) {
                    return -1;
                }
                length = in.readIntLE();
                ** break;
            }
        }
        length = tag >> 2 & 63;
lbl25: // 5 sources:
        if (in.readableBytes() < ++length) {
            in.resetReaderIndex();
            return -1;
        }
        out.writeBytes((ByteBuf)in, (int)length);
        return length;
    }

    private static int decodeCopyWith1ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (!in.isReadable()) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 4 + ((tag & 28) >> 2);
        int offset = (tag & 224) << 8 >> 5 | in.readUnsignedByte();
        Snappy.validateOffset((int)offset, (int)writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)offset);
            }
            if (length % offset != 0) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)(length % offset));
            }
        } else {
            out.readerIndex((int)(initialIndex - offset));
            out.readBytes((ByteBuf)out, (int)length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static int decodeCopyWith2ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (in.readableBytes() < 2) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 1 + (tag >> 2 & 63);
        int offset = in.readUnsignedShortLE();
        Snappy.validateOffset((int)offset, (int)writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)offset);
            }
            if (length % offset != 0) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)(length % offset));
            }
        } else {
            out.readerIndex((int)(initialIndex - offset));
            out.readBytes((ByteBuf)out, (int)length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static int decodeCopyWith4ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
        if (in.readableBytes() < 4) {
            return -1;
        }
        int initialIndex = out.writerIndex();
        int length = 1 + (tag >> 2 & 63);
        int offset = in.readIntLE();
        Snappy.validateOffset((int)offset, (int)writtenSoFar);
        out.markReaderIndex();
        if (offset < length) {
            for (int copies = length / offset; copies > 0; --copies) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)offset);
            }
            if (length % offset != 0) {
                out.readerIndex((int)(initialIndex - offset));
                out.readBytes((ByteBuf)out, (int)(length % offset));
            }
        } else {
            out.readerIndex((int)(initialIndex - offset));
            out.readBytes((ByteBuf)out, (int)length);
        }
        out.resetReaderIndex();
        return length;
    }

    private static void validateOffset(int offset, int chunkSizeSoFar) {
        if (offset == 0) {
            throw new DecompressionException((String)"Offset is less than minimum permissible value");
        }
        if (offset < 0) {
            throw new DecompressionException((String)"Offset is greater than maximum value supported by this implementation");
        }
        if (offset <= chunkSizeSoFar) return;
        throw new DecompressionException((String)"Offset exceeds size of chunk");
    }

    static int calculateChecksum(ByteBuf data) {
        return Snappy.calculateChecksum((ByteBuf)data, (int)data.readerIndex(), (int)data.readableBytes());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int calculateChecksum(ByteBuf data, int offset, int length) {
        Crc32c crc32 = new Crc32c();
        try {
            crc32.update((ByteBuf)data, (int)offset, (int)length);
            int n = Snappy.maskChecksum((int)((int)crc32.getValue()));
            return n;
        }
        finally {
            crc32.reset();
        }
    }

    static void validateChecksum(int expectedChecksum, ByteBuf data) {
        Snappy.validateChecksum((int)expectedChecksum, (ByteBuf)data, (int)data.readerIndex(), (int)data.readableBytes());
    }

    static void validateChecksum(int expectedChecksum, ByteBuf data, int offset, int length) {
        int actualChecksum = Snappy.calculateChecksum((ByteBuf)data, (int)offset, (int)length);
        if (actualChecksum == expectedChecksum) return;
        throw new DecompressionException((String)("mismatching checksum: " + Integer.toHexString((int)actualChecksum) + " (expected: " + Integer.toHexString((int)expectedChecksum) + ')'));
    }

    static int maskChecksum(int checksum) {
        return (checksum >> 15 | checksum << 17) + -1568478504;
    }
}

