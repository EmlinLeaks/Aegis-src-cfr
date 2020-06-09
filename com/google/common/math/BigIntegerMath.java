/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.BigIntegerMath;
import com.google.common.math.DoubleMath;
import com.google.common.math.DoubleUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.math.MathPreconditions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@GwtCompatible(emulated=true)
public final class BigIntegerMath {
    @VisibleForTesting
    static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
    @VisibleForTesting
    static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger((String)"16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", (int)16);
    private static final double LN_10 = Math.log((double)10.0);
    private static final double LN_2 = Math.log((double)2.0);

    @Beta
    public static BigInteger ceilingPowerOfTwo(BigInteger x) {
        return BigInteger.ZERO.setBit((int)BigIntegerMath.log2((BigInteger)x, (RoundingMode)RoundingMode.CEILING));
    }

    @Beta
    public static BigInteger floorPowerOfTwo(BigInteger x) {
        return BigInteger.ZERO.setBit((int)BigIntegerMath.log2((BigInteger)x, (RoundingMode)RoundingMode.FLOOR));
    }

    public static boolean isPowerOfTwo(BigInteger x) {
        Preconditions.checkNotNull(x);
        if (x.signum() <= 0) return false;
        if (x.getLowestSetBit() != x.bitLength() - 1) return false;
        return true;
    }

    public static int log2(BigInteger x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (BigInteger)Preconditions.checkNotNull(x));
        int logFloor = x.bitLength() - 1;
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)BigIntegerMath.isPowerOfTwo((BigInteger)x));
            }
            case 2: 
            case 3: {
                return logFloor;
            }
            case 4: 
            case 5: {
                int n;
                if (BigIntegerMath.isPowerOfTwo((BigInteger)x)) {
                    n = logFloor;
                    return n;
                }
                n = logFloor + 1;
                return n;
            }
            case 6: 
            case 7: 
            case 8: {
                int n;
                if (logFloor < 256) {
                    BigInteger halfPower = SQRT2_PRECOMPUTED_BITS.shiftRight((int)(256 - logFloor));
                    if (x.compareTo((BigInteger)halfPower) > 0) return logFloor + 1;
                    return logFloor;
                }
                BigInteger x2 = x.pow((int)2);
                int logX2Floor = x2.bitLength() - 1;
                if (logX2Floor < 2 * logFloor + 1) {
                    n = logFloor;
                    return n;
                }
                n = logFloor + 1;
                return n;
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    public static int log10(BigInteger x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (BigInteger)x);
        if (BigIntegerMath.fitsInLong((BigInteger)x)) {
            return LongMath.log10((long)x.longValue(), (RoundingMode)mode);
        }
        int approxLog10 = (int)((double)BigIntegerMath.log2((BigInteger)x, (RoundingMode)RoundingMode.FLOOR) * LN_2 / LN_10);
        BigInteger approxPow = BigInteger.TEN.pow((int)approxLog10);
        int approxCmp = approxPow.compareTo((BigInteger)x);
        if (approxCmp > 0) {
            do {
                --approxLog10;
            } while ((approxCmp = (approxPow = approxPow.divide((BigInteger)BigInteger.TEN)).compareTo((BigInteger)x)) > 0);
        } else {
            BigInteger nextPow = BigInteger.TEN.multiply((BigInteger)approxPow);
            int nextCmp = nextPow.compareTo((BigInteger)x);
            while (nextCmp <= 0) {
                ++approxLog10;
                approxPow = nextPow;
                approxCmp = nextCmp;
                nextPow = BigInteger.TEN.multiply((BigInteger)approxPow);
                nextCmp = nextPow.compareTo((BigInteger)x);
            }
        }
        int floorLog = approxLog10;
        BigInteger floorPow = approxPow;
        int floorCmp = approxCmp;
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(floorCmp == 0));
            }
            case 2: 
            case 3: {
                return floorLog;
            }
            case 4: 
            case 5: {
                int n;
                if (floorPow.equals((Object)x)) {
                    n = floorLog;
                    return n;
                }
                n = floorLog + 1;
                return n;
            }
            case 6: 
            case 7: 
            case 8: {
                int n;
                BigInteger x2 = x.pow((int)2);
                BigInteger halfPowerSquared = floorPow.pow((int)2).multiply((BigInteger)BigInteger.TEN);
                if (x2.compareTo((BigInteger)halfPowerSquared) <= 0) {
                    n = floorLog;
                    return n;
                }
                n = floorLog + 1;
                return n;
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    public static BigInteger sqrt(BigInteger x, RoundingMode mode) {
        MathPreconditions.checkNonNegative((String)"x", (BigInteger)x);
        if (BigIntegerMath.fitsInLong((BigInteger)x)) {
            return BigInteger.valueOf((long)LongMath.sqrt((long)x.longValue(), (RoundingMode)mode));
        }
        BigInteger sqrtFloor = BigIntegerMath.sqrtFloor((BigInteger)x);
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)sqrtFloor.pow((int)2).equals((Object)x));
            }
            case 2: 
            case 3: {
                return sqrtFloor;
            }
            case 4: 
            case 5: {
                BigInteger bigInteger;
                boolean sqrtFloorIsExact;
                int sqrtFloorInt = sqrtFloor.intValue();
                boolean bl = sqrtFloorIsExact = sqrtFloorInt * sqrtFloorInt == x.intValue() && sqrtFloor.pow((int)2).equals((Object)x);
                if (sqrtFloorIsExact) {
                    bigInteger = sqrtFloor;
                    return bigInteger;
                }
                bigInteger = sqrtFloor.add((BigInteger)BigInteger.ONE);
                return bigInteger;
            }
            case 6: 
            case 7: 
            case 8: {
                BigInteger bigInteger;
                BigInteger halfSquare = sqrtFloor.pow((int)2).add((BigInteger)sqrtFloor);
                if (halfSquare.compareTo((BigInteger)x) >= 0) {
                    bigInteger = sqrtFloor;
                    return bigInteger;
                }
                bigInteger = sqrtFloor.add((BigInteger)BigInteger.ONE);
                return bigInteger;
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    private static BigInteger sqrtFloor(BigInteger x) {
        BigInteger sqrt0;
        int log2 = BigIntegerMath.log2((BigInteger)x, (RoundingMode)RoundingMode.FLOOR);
        if (log2 < 1023) {
            sqrt0 = BigIntegerMath.sqrtApproxWithDoubles((BigInteger)x);
        } else {
            int shift = log2 - 52 & -2;
            sqrt0 = BigIntegerMath.sqrtApproxWithDoubles((BigInteger)x.shiftRight((int)shift)).shiftLeft((int)(shift >> 1));
        }
        BigInteger sqrt1 = sqrt0.add((BigInteger)x.divide((BigInteger)sqrt0)).shiftRight((int)1);
        if (sqrt0.equals((Object)sqrt1)) {
            return sqrt0;
        }
        do {
            sqrt0 = sqrt1;
        } while ((sqrt1 = sqrt0.add((BigInteger)x.divide((BigInteger)sqrt0)).shiftRight((int)1)).compareTo((BigInteger)sqrt0) < 0);
        return sqrt0;
    }

    @GwtIncompatible
    private static BigInteger sqrtApproxWithDoubles(BigInteger x) {
        return DoubleMath.roundToBigInteger((double)Math.sqrt((double)DoubleUtils.bigToDouble((BigInteger)x)), (RoundingMode)RoundingMode.HALF_EVEN);
    }

    @GwtIncompatible
    public static BigInteger divide(BigInteger p, BigInteger q, RoundingMode mode) {
        BigDecimal pDec = new BigDecimal((BigInteger)p);
        BigDecimal qDec = new BigDecimal((BigInteger)q);
        return pDec.divide((BigDecimal)qDec, (int)0, (RoundingMode)mode).toBigIntegerExact();
    }

    public static BigInteger factorial(int n) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        if (n < LongMath.factorials.length) {
            return BigInteger.valueOf((long)LongMath.factorials[n]);
        }
        int approxSize = IntMath.divide((int)(n * IntMath.log2((int)n, (RoundingMode)RoundingMode.CEILING)), (int)64, (RoundingMode)RoundingMode.CEILING);
        ArrayList<BigInteger> bignums = new ArrayList<BigInteger>((int)approxSize);
        int startingNumber = LongMath.factorials.length;
        long product = LongMath.factorials[startingNumber - 1];
        int shift = Long.numberOfTrailingZeros((long)product);
        int productBits = LongMath.log2((long)(product >>= shift), (RoundingMode)RoundingMode.FLOOR) + 1;
        int bits = LongMath.log2((long)((long)startingNumber), (RoundingMode)RoundingMode.FLOOR) + 1;
        int nextPowerOfTwo = 1 << bits - 1;
        long num = (long)startingNumber;
        do {
            if (num > (long)n) {
                if (product <= 1L) return BigIntegerMath.listProduct(bignums).shiftLeft((int)shift);
                bignums.add((BigInteger)BigInteger.valueOf((long)product));
                return BigIntegerMath.listProduct(bignums).shiftLeft((int)shift);
            }
            if ((num & (long)nextPowerOfTwo) != 0L) {
                nextPowerOfTwo <<= 1;
                ++bits;
            }
            int tz = Long.numberOfTrailingZeros((long)num);
            long normalizedNum = num >> tz;
            shift += tz;
            int normalizedBits = bits - tz;
            if (normalizedBits + productBits >= 64) {
                bignums.add((BigInteger)BigInteger.valueOf((long)product));
                product = 1L;
                productBits = 0;
            }
            productBits = LongMath.log2((long)(product *= normalizedNum), (RoundingMode)RoundingMode.FLOOR) + 1;
            ++num;
        } while (true);
    }

    static BigInteger listProduct(List<BigInteger> nums) {
        return BigIntegerMath.listProduct(nums, (int)0, (int)nums.size());
    }

    static BigInteger listProduct(List<BigInteger> nums, int start, int end) {
        switch (end - start) {
            case 0: {
                return BigInteger.ONE;
            }
            case 1: {
                return nums.get((int)start);
            }
            case 2: {
                return nums.get((int)start).multiply((BigInteger)nums.get((int)(start + 1)));
            }
            case 3: {
                return nums.get((int)start).multiply((BigInteger)nums.get((int)(start + 1))).multiply((BigInteger)nums.get((int)(start + 2)));
            }
        }
        int m = end + start >>> 1;
        return BigIntegerMath.listProduct(nums, (int)start, (int)m).multiply((BigInteger)BigIntegerMath.listProduct(nums, (int)m, (int)end));
    }

    public static BigInteger binomial(int n, int k) {
        int bits;
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        MathPreconditions.checkNonNegative((String)"k", (int)k);
        Preconditions.checkArgument((boolean)(k <= n), (String)"k (%s) > n (%s)", (int)k, (int)n);
        if (k > n >> 1) {
            k = n - k;
        }
        if (k < LongMath.biggestBinomials.length && n <= LongMath.biggestBinomials[k]) {
            return BigInteger.valueOf((long)LongMath.binomial((int)n, (int)k));
        }
        BigInteger accum = BigInteger.ONE;
        long numeratorAccum = (long)n;
        long denominatorAccum = 1L;
        int numeratorBits = bits = LongMath.log2((long)((long)n), (RoundingMode)RoundingMode.CEILING);
        int i = 1;
        while (i < k) {
            int p = n - i;
            int q = i + 1;
            if (numeratorBits + bits >= 63) {
                accum = accum.multiply((BigInteger)BigInteger.valueOf((long)numeratorAccum)).divide((BigInteger)BigInteger.valueOf((long)denominatorAccum));
                numeratorAccum = (long)p;
                denominatorAccum = (long)q;
                numeratorBits = bits;
            } else {
                numeratorAccum *= (long)p;
                denominatorAccum *= (long)q;
                numeratorBits += bits;
            }
            ++i;
        }
        return accum.multiply((BigInteger)BigInteger.valueOf((long)numeratorAccum)).divide((BigInteger)BigInteger.valueOf((long)denominatorAccum));
    }

    @GwtIncompatible
    static boolean fitsInLong(BigInteger x) {
        if (x.bitLength() > 63) return false;
        return true;
    }

    private BigIntegerMath() {
    }
}

