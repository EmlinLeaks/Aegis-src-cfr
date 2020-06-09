/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.hash.LittleEndianByteArray;
import java.nio.ByteOrder;

final class LittleEndianByteArray {
    private static final LittleEndianBytes byteArray;

    static long load64(byte[] input, int offset) {
        if ($assertionsDisabled) return byteArray.getLongLittleEndian((byte[])input, (int)offset);
        if (input.length >= offset + 8) return byteArray.getLongLittleEndian((byte[])input, (int)offset);
        throw new AssertionError();
    }

    static long load64Safely(byte[] input, int offset, int length) {
        long result = 0L;
        int limit = Math.min((int)length, (int)8);
        int i = 0;
        while (i < limit) {
            result |= ((long)input[offset + i] & 255L) << i * 8;
            ++i;
        }
        return result;
    }

    static void store64(byte[] sink, int offset, long value) {
        if (!$assertionsDisabled) {
            if (offset < 0) throw new AssertionError();
            if (offset + 8 > sink.length) {
                throw new AssertionError();
            }
        }
        byteArray.putLongLittleEndian((byte[])sink, (int)offset, (long)value);
    }

    static int load32(byte[] source, int offset) {
        return source[offset] & 255 | (source[offset + 1] & 255) << 8 | (source[offset + 2] & 255) << 16 | (source[offset + 3] & 255) << 24;
    }

    static boolean usingUnsafe() {
        return byteArray instanceof UnsafeByteArray;
    }

    private LittleEndianByteArray() {
    }

    static {
        Enum theGetter = JavaLittleEndianBytes.INSTANCE;
        try {
            String arch = System.getProperty((String)"os.arch");
            if ("amd64".equals((Object)arch) || "aarch64".equals((Object)arch)) {
                theGetter = ByteOrder.nativeOrder().equals((Object)ByteOrder.LITTLE_ENDIAN) ? UnsafeByteArray.UNSAFE_LITTLE_ENDIAN : UnsafeByteArray.UNSAFE_BIG_ENDIAN;
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
        byteArray = theGetter;
    }
}

