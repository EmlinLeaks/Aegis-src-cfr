/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractNonStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.LittleEndianByteArray;

final class FarmHashFingerprint64
extends AbstractNonStreamingHashFunction {
    private static final long K0 = -4348849565147123417L;
    private static final long K1 = -5435081209227447693L;
    private static final long K2 = -7286425919675154353L;

    FarmHashFingerprint64() {
    }

    @Override
    public HashCode hashBytes(byte[] input, int off, int len) {
        Preconditions.checkPositionIndexes((int)off, (int)(off + len), (int)input.length);
        return HashCode.fromLong((long)FarmHashFingerprint64.fingerprint((byte[])input, (int)off, (int)len));
    }

    @Override
    public int bits() {
        return 64;
    }

    public String toString() {
        return "Hashing.farmHashFingerprint64()";
    }

    @VisibleForTesting
    static long fingerprint(byte[] bytes, int offset, int length) {
        if (length <= 32) {
            if (length > 16) return FarmHashFingerprint64.hashLength17to32((byte[])bytes, (int)offset, (int)length);
            return FarmHashFingerprint64.hashLength0to16((byte[])bytes, (int)offset, (int)length);
        }
        if (length > 64) return FarmHashFingerprint64.hashLength65Plus((byte[])bytes, (int)offset, (int)length);
        return FarmHashFingerprint64.hashLength33To64((byte[])bytes, (int)offset, (int)length);
    }

    private static long shiftMix(long val) {
        return val ^ val >>> 47;
    }

    private static long hashLength16(long u, long v, long mul) {
        long a = (u ^ v) * mul;
        a ^= a >>> 47;
        long b = (v ^ a) * mul;
        b ^= b >>> 47;
        return b *= mul;
    }

    private static void weakHashLength32WithSeeds(byte[] bytes, int offset, long seedA, long seedB, long[] output) {
        long part1 = LittleEndianByteArray.load64((byte[])bytes, (int)offset);
        long part2 = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 8));
        long part3 = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 16));
        long part4 = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 24));
        seedB = Long.rotateRight((long)(seedB + (seedA += part1) + part4), (int)21);
        long c = seedA;
        seedA += part2;
        output[0] = seedA + part4;
        output[1] = (seedB += Long.rotateRight((long)(seedA += part3), (int)44)) + c;
    }

    private static long hashLength0to16(byte[] bytes, int offset, int length) {
        if (length >= 8) {
            long mul = -7286425919675154353L + (long)(length * 2);
            long a = LittleEndianByteArray.load64((byte[])bytes, (int)offset) + -7286425919675154353L;
            long b = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 8));
            long c = Long.rotateRight((long)b, (int)37) * mul + a;
            long d = (Long.rotateRight((long)a, (int)25) + b) * mul;
            return FarmHashFingerprint64.hashLength16((long)c, (long)d, (long)mul);
        }
        if (length >= 4) {
            long mul = -7286425919675154353L + (long)(length * 2);
            long a = (long)LittleEndianByteArray.load32((byte[])bytes, (int)offset) & 0xFFFFFFFFL;
            return FarmHashFingerprint64.hashLength16((long)((long)length + (a << 3)), (long)((long)LittleEndianByteArray.load32((byte[])bytes, (int)(offset + length - 4)) & 0xFFFFFFFFL), (long)mul);
        }
        if (length <= 0) return -7286425919675154353L;
        byte a = bytes[offset];
        byte b = bytes[offset + (length >> 1)];
        byte c = bytes[offset + (length - 1)];
        int y = (a & 255) + ((b & 255) << 8);
        int z = length + ((c & 255) << 2);
        return FarmHashFingerprint64.shiftMix((long)((long)y * -7286425919675154353L ^ (long)z * -4348849565147123417L)) * -7286425919675154353L;
    }

    private static long hashLength17to32(byte[] bytes, int offset, int length) {
        long mul = -7286425919675154353L + (long)(length * 2);
        long a = LittleEndianByteArray.load64((byte[])bytes, (int)offset) * -5435081209227447693L;
        long b = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 8));
        long c = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 8)) * mul;
        long d = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 16)) * -7286425919675154353L;
        return FarmHashFingerprint64.hashLength16((long)(Long.rotateRight((long)(a + b), (int)43) + Long.rotateRight((long)c, (int)30) + d), (long)(a + Long.rotateRight((long)(b + -7286425919675154353L), (int)18) + c), (long)mul);
    }

    private static long hashLength33To64(byte[] bytes, int offset, int length) {
        long mul = -7286425919675154353L + (long)(length * 2);
        long a = LittleEndianByteArray.load64((byte[])bytes, (int)offset) * -7286425919675154353L;
        long b = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 8));
        long c = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 8)) * mul;
        long d = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 16)) * -7286425919675154353L;
        long y = Long.rotateRight((long)(a + b), (int)43) + Long.rotateRight((long)c, (int)30) + d;
        long z = FarmHashFingerprint64.hashLength16((long)y, (long)(a + Long.rotateRight((long)(b + -7286425919675154353L), (int)18) + c), (long)mul);
        long e = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 16)) * mul;
        long f = LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 24));
        long g = (y + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 32))) * mul;
        long h = (z + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + length - 24))) * mul;
        return FarmHashFingerprint64.hashLength16((long)(Long.rotateRight((long)(e + f), (int)43) + Long.rotateRight((long)g, (int)30) + h), (long)(e + Long.rotateRight((long)(f + a), (int)18) + g), (long)mul);
    }

    private static long hashLength65Plus(byte[] bytes, int offset, int length) {
        int seed = 81;
        long x = 81L;
        long y = 2480279821605975764L;
        long z = FarmHashFingerprint64.shiftMix((long)(y * -7286425919675154353L + 113L)) * -7286425919675154353L;
        long[] v = new long[2];
        long[] w = new long[2];
        x = x * -7286425919675154353L + LittleEndianByteArray.load64((byte[])bytes, (int)offset);
        int end = offset + (length - 1) / 64 * 64;
        int last64offset = end + (length - 1 & 63) - 63;
        do {
            x = Long.rotateRight((long)(x + y + v[0] + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 8))), (int)37) * -5435081209227447693L;
            y = Long.rotateRight((long)(y + v[1] + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 48))), (int)42) * -5435081209227447693L;
            z = Long.rotateRight((long)(z + w[0]), (int)33) * -5435081209227447693L;
            FarmHashFingerprint64.weakHashLength32WithSeeds((byte[])bytes, (int)offset, (long)(v[1] * -5435081209227447693L), (long)((x ^= w[1]) + w[0]), (long[])v);
            FarmHashFingerprint64.weakHashLength32WithSeeds((byte[])bytes, (int)(offset + 32), (long)(z + w[1]), (long)((y += v[0] + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 40))) + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 16))), (long[])w);
            long tmp = x;
            x = z;
            z = tmp;
        } while ((offset += 64) != end);
        long mul = -5435081209227447693L + ((z & 255L) << 1);
        offset = last64offset;
        long[] arrl = w;
        arrl[0] = arrl[0] + (long)(length - 1 & 63);
        long[] arrl2 = v;
        arrl2[0] = arrl2[0] + w[0];
        long[] arrl3 = w;
        arrl3[0] = arrl3[0] + v[0];
        x = Long.rotateRight((long)(x + y + v[0] + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 8))), (int)37) * mul;
        y = Long.rotateRight((long)(y + v[1] + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 48))), (int)42) * mul;
        z = Long.rotateRight((long)(z + w[0]), (int)33) * mul;
        FarmHashFingerprint64.weakHashLength32WithSeeds((byte[])bytes, (int)offset, (long)(v[1] * mul), (long)((x ^= w[1] * 9L) + w[0]), (long[])v);
        FarmHashFingerprint64.weakHashLength32WithSeeds((byte[])bytes, (int)(offset + 32), (long)(z + w[1]), (long)((y += v[0] * 9L + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 40))) + LittleEndianByteArray.load64((byte[])bytes, (int)(offset + 16))), (long[])w);
        return FarmHashFingerprint64.hashLength16((long)(FarmHashFingerprint64.hashLength16((long)v[0], (long)w[0], (long)mul) + FarmHashFingerprint64.shiftMix((long)y) * -4348849565147123417L + x), (long)(FarmHashFingerprint64.hashLength16((long)v[1], (long)w[1], (long)mul) + z), (long)mul);
    }
}

