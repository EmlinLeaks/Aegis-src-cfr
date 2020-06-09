/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.math.BigInteger;

@GwtIncompatible
final class DoubleUtils {
    static final long SIGNIFICAND_MASK = 0xFFFFFFFFFFFFFL;
    static final long EXPONENT_MASK = 9218868437227405312L;
    static final long SIGN_MASK = Long.MIN_VALUE;
    static final int SIGNIFICAND_BITS = 52;
    static final int EXPONENT_BIAS = 1023;
    static final long IMPLICIT_BIT = 0x10000000000000L;
    private static final long ONE_BITS = Double.doubleToRawLongBits((double)1.0);

    private DoubleUtils() {
    }

    static double nextDown(double d) {
        return -Math.nextUp((double)(-d));
    }

    static long getSignificand(double d) {
        long l;
        Preconditions.checkArgument((boolean)DoubleUtils.isFinite((double)d), (Object)"not a normal value");
        int exponent = Math.getExponent((double)d);
        long bits = Double.doubleToRawLongBits((double)d);
        bits &= 0xFFFFFFFFFFFFFL;
        if (exponent == -1023) {
            l = bits << 1;
            return l;
        }
        l = bits | 0x10000000000000L;
        return l;
    }

    static boolean isFinite(double d) {
        if (Math.getExponent((double)d) > 1023) return false;
        return true;
    }

    static boolean isNormal(double d) {
        if (Math.getExponent((double)d) < -1022) return false;
        return true;
    }

    static double scaleNormalize(double x) {
        long significand = Double.doubleToRawLongBits((double)x) & 0xFFFFFFFFFFFFFL;
        return Double.longBitsToDouble((long)(significand | ONE_BITS));
    }

    static double bigToDouble(BigInteger x) {
        BigInteger absX = x.abs();
        int exponent = absX.bitLength() - 1;
        if (exponent < 63) {
            return (double)x.longValue();
        }
        if (exponent > 1023) {
            return (double)x.signum() * Double.POSITIVE_INFINITY;
        }
        int shift = exponent - 52 - 1;
        long twiceSignifFloor = absX.shiftRight((int)shift).longValue();
        long signifFloor = twiceSignifFloor >> 1;
        boolean increment = (twiceSignifFloor & 1L) != 0L && (((signifFloor &= 0xFFFFFFFFFFFFFL) & 1L) != 0L || absX.getLowestSetBit() < shift);
        long signifRounded = increment ? signifFloor + 1L : signifFloor;
        long bits = (long)(exponent + 1023) << 52;
        bits += signifRounded;
        return Double.longBitsToDouble((long)(bits |= (long)x.signum() & Long.MIN_VALUE));
    }

    static double ensureNonNegative(double value) {
        Preconditions.checkArgument((boolean)(!Double.isNaN((double)value)));
        if (!(value > 0.0)) return 0.0;
        return value;
    }
}

