/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.DecompressionException;

final class FastLz {
    private static final int MAX_DISTANCE = 8191;
    private static final int MAX_FARDISTANCE = 73725;
    private static final int HASH_LOG = 13;
    private static final int HASH_SIZE = 8192;
    private static final int HASH_MASK = 8191;
    private static final int MAX_COPY = 32;
    private static final int MAX_LEN = 264;
    private static final int MIN_RECOMENDED_LENGTH_FOR_LEVEL_2 = 65536;
    static final int MAGIC_NUMBER = 4607066;
    static final byte BLOCK_TYPE_NON_COMPRESSED = 0;
    static final byte BLOCK_TYPE_COMPRESSED = 1;
    static final byte BLOCK_WITHOUT_CHECKSUM = 0;
    static final byte BLOCK_WITH_CHECKSUM = 16;
    static final int OPTIONS_OFFSET = 3;
    static final int CHECKSUM_OFFSET = 4;
    static final int MAX_CHUNK_LENGTH = 65535;
    static final int MIN_LENGTH_TO_COMPRESSION = 32;
    static final int LEVEL_AUTO = 0;
    static final int LEVEL_1 = 1;
    static final int LEVEL_2 = 2;

    static int calculateOutputBufferLength(int inputLength) {
        int outputLength = (int)((double)inputLength * 1.06);
        return Math.max((int)outputLength, (int)66);
    }

    static int compress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int proposedLevel) {
        int hslot;
        int level = proposedLevel == 0 ? (inLength < 65536 ? 1 : 2) : proposedLevel;
        int ip = 0;
        int ipBound = ip + inLength - 2;
        int ipLimit = ip + inLength - 12;
        int op = 0;
        int[] htab = new int[8192];
        if (inLength < 4) {
            if (inLength == 0) return 0;
            output[outOffset + op++] = (byte)(inLength - 1);
            while (ip <= ++ipBound) {
                output[outOffset + op++] = input[inOffset + ip++];
            }
            return inLength + 1;
        }
        for (hslot = 0; hslot < 8192; ++hslot) {
            htab[hslot] = ip;
        }
        int copy = 2;
        output[outOffset + op++] = 31;
        output[outOffset + op++] = input[inOffset + ip++];
        output[outOffset + op++] = input[inOffset + ip++];
        do {
            long distance;
            int hval;
            int len;
            block37 : {
                int ref;
                byte x;
                block36 : {
                    block31 : {
                        block35 : {
                            block32 : {
                                int anchor;
                                block34 : {
                                    block33 : {
                                        if (ip >= ipLimit) break block31;
                                        ref = 0;
                                        distance = 0L;
                                        len = 3;
                                        anchor = ip;
                                        boolean matchLabel = false;
                                        if (level == 2 && input[inOffset + ip] == input[inOffset + ip - 1] && FastLz.readU16((byte[])input, (int)(inOffset + ip - 1)) == FastLz.readU16((byte[])input, (int)(inOffset + ip + 1))) {
                                            distance = 1L;
                                            ip += 3;
                                            ref = anchor + 2;
                                            matchLabel = true;
                                        }
                                        if (matchLabel) break block32;
                                        hslot = hval = FastLz.hashFunction((byte[])input, (int)(inOffset + ip));
                                        ref = htab[hval];
                                        distance = (long)(anchor - ref);
                                        htab[hslot] = anchor;
                                        if (distance == 0L || (level != 1 ? distance >= 73725L : distance >= 8191L)) break block33;
                                        if (input[inOffset + ref++] == input[inOffset + ip++] && input[inOffset + ref++] == input[inOffset + ip++] && input[inOffset + ref++] == input[inOffset + ip++]) break block34;
                                    }
                                    output[outOffset + op++] = input[inOffset + anchor++];
                                    ip = anchor;
                                    if (++copy != 32) continue;
                                    copy = 0;
                                    output[outOffset + op++] = 31;
                                    continue;
                                }
                                if (level == 2 && distance >= 8191L) {
                                    if (input[inOffset + ip++] != input[inOffset + ref++] || input[inOffset + ip++] != input[inOffset + ref++]) {
                                        output[outOffset + op++] = input[inOffset + anchor++];
                                        ip = anchor;
                                        if (++copy != 32) continue;
                                        copy = 0;
                                        output[outOffset + op++] = 31;
                                        continue;
                                    }
                                    len += 2;
                                }
                            }
                            if (--distance != 0L) break block35;
                            x = input[inOffset + ip - 1];
                            break block36;
                        }
                        if (input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++] || input[inOffset + ref++] != input[inOffset + ip++]) break block37;
                        while (ip < ipBound && input[inOffset + ref++] == input[inOffset + ip++]) {
                        }
                        break block37;
                    }
                    while (ip <= ++ipBound) {
                        output[outOffset + op++] = input[inOffset + ip++];
                        if (++copy != 32) continue;
                        copy = 0;
                        output[outOffset + op++] = 31;
                    }
                    if (copy != 0) {
                        output[outOffset + op - copy - 1] = (byte)(copy - 1);
                    } else {
                        --op;
                    }
                    if (level != 2) return op;
                    byte[] arrby = output;
                    int n = outOffset;
                    arrby[n] = (byte)(arrby[n] | 32);
                    return op;
                }
                for (ip = anchor + len; ip < ipBound && input[inOffset + ref++] == x; ++ip) {
                }
            }
            if (copy != 0) {
                output[outOffset + op - copy - 1] = (byte)(copy - 1);
            } else {
                --op;
            }
            copy = 0;
            if (level == 2) {
                if (distance < 8191L) {
                    if (len < 7) {
                        output[outOffset + op++] = (byte)((int)((long)(len << 5) + (distance >>> 8)));
                        output[outOffset + op++] = (byte)((int)(distance & 255L));
                    } else {
                        output[outOffset + op++] = (byte)((int)(224L + (distance >>> 8)));
                        len -= 7;
                        while (len >= 255) {
                            output[outOffset + op++] = -1;
                            len -= 255;
                        }
                        output[outOffset + op++] = (byte)len;
                        output[outOffset + op++] = (byte)((int)(distance & 255L));
                    }
                } else if (len < 7) {
                    output[outOffset + op++] = (byte)((len << 5) + 31);
                    output[outOffset + op++] = -1;
                    output[outOffset + op++] = (byte)((int)((distance -= 8191L) >>> 8));
                    output[outOffset + op++] = (byte)((int)(distance & 255L));
                } else {
                    distance -= 8191L;
                    output[outOffset + op++] = -1;
                    len -= 7;
                    while (len >= 255) {
                        output[outOffset + op++] = -1;
                        len -= 255;
                    }
                    output[outOffset + op++] = (byte)len;
                    output[outOffset + op++] = -1;
                    output[outOffset + op++] = (byte)((int)(distance >>> 8));
                    output[outOffset + op++] = (byte)((int)(distance & 255L));
                }
            } else {
                if (len > 262) {
                    for (len = (ip -= 3) - anchor; len > 262; len -= 262) {
                        output[outOffset + op++] = (byte)((int)(224L + (distance >>> 8)));
                        output[outOffset + op++] = -3;
                        output[outOffset + op++] = (byte)((int)(distance & 255L));
                    }
                }
                if (len < 7) {
                    output[outOffset + op++] = (byte)((int)((long)(len << 5) + (distance >>> 8)));
                    output[outOffset + op++] = (byte)((int)(distance & 255L));
                } else {
                    output[outOffset + op++] = (byte)((int)(224L + (distance >>> 8)));
                    output[outOffset + op++] = (byte)(len - 7);
                    output[outOffset + op++] = (byte)((int)(distance & 255L));
                }
            }
            hval = FastLz.hashFunction((byte[])input, (int)(inOffset + ip));
            htab[hval] = ip++;
            hval = FastLz.hashFunction((byte[])input, (int)(inOffset + ip));
            htab[hval] = ip++;
            output[outOffset + op++] = 31;
        } while (true);
    }

    static int decompress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int outLength) {
        int level = (input[inOffset] >> 5) + 1;
        if (level != 1 && level != 2) {
            throw new DecompressionException((String)String.format((String)"invalid level: %d (expected: %d or %d)", (Object[])new Object[]{Integer.valueOf((int)level), Integer.valueOf((int)1), Integer.valueOf((int)2)}));
        }
        int ip = 0;
        int op = 0;
        long ctrl = (long)(input[inOffset + ip++] & 31);
        boolean loop = true;
        do {
            int ref = op;
            long len = ctrl >> 5;
            long ofs = (ctrl & 31L) << 8;
            if (ctrl >= 32L) {
                int code;
                ref = (int)((long)ref - ofs);
                if (--len == 6L) {
                    if (level == 1) {
                        len += (long)(input[inOffset + ip++] & 255);
                    } else {
                        do {
                            code = input[inOffset + ip++] & 255;
                            len += (long)code;
                        } while (code == 255);
                    }
                }
                if (level == 1) {
                    ref -= input[inOffset + ip++] & 255;
                } else {
                    code = input[inOffset + ip++] & 255;
                    ref -= code;
                    if (code == 255 && ofs == 7936L) {
                        ofs = (long)((input[inOffset + ip++] & 255) << 8);
                        ref = (int)((long)op - (ofs += (long)(input[inOffset + ip++] & 255)) - 8191L);
                    }
                }
                if ((long)op + len + 3L > (long)outLength) {
                    return 0;
                }
                if (ref - 1 < 0) {
                    return 0;
                }
                if (ip < inLength) {
                    ctrl = (long)(input[inOffset + ip++] & 255);
                } else {
                    loop = false;
                }
                if (ref == op) {
                    byte b = output[outOffset + ref - 1];
                    output[outOffset + op++] = b;
                    output[outOffset + op++] = b;
                    output[outOffset + op++] = b;
                    while (len != 0L) {
                        output[outOffset + op++] = b;
                        --len;
                    }
                } else {
                    int n = op++;
                    int n2 = --ref;
                    output[outOffset + n] = output[outOffset + n2];
                    int n3 = op++;
                    int n4 = ++ref;
                    output[outOffset + n3] = output[outOffset + n4];
                    int n5 = op++;
                    int n6 = ++ref;
                    ++ref;
                    output[outOffset + n5] = output[outOffset + n6];
                    while (len != 0L) {
                        output[outOffset + op++] = output[outOffset + ref++];
                        --len;
                    }
                }
            } else {
                if ((long)op + ++ctrl > (long)outLength) {
                    return 0;
                }
                if ((long)ip + ctrl > (long)inLength) {
                    return 0;
                }
                output[outOffset + op++] = input[inOffset + ip++];
                --ctrl;
                while (ctrl != 0L) {
                    output[outOffset + op++] = input[inOffset + ip++];
                    --ctrl;
                }
                boolean bl = loop = ip < inLength;
                if (!loop) continue;
                ctrl = (long)(input[inOffset + ip++] & 255);
            }
        } while (loop);
        return op;
    }

    private static int hashFunction(byte[] p, int offset) {
        int v = FastLz.readU16((byte[])p, (int)offset);
        v ^= FastLz.readU16((byte[])p, (int)(offset + 1)) ^ v >> 3;
        return v &= 8191;
    }

    private static int readU16(byte[] data, int offset) {
        if (offset + 1 < data.length) return (data[offset + 1] & 255) << 8 | data[offset] & 255;
        return data[offset] & 255;
    }

    private FastLz() {
    }
}

