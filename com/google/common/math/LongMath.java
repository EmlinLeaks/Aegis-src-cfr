/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.math.MathPreconditions;
import java.math.RoundingMode;

@GwtCompatible(emulated=true)
public final class LongMath {
    @VisibleForTesting
    static final long MAX_SIGNED_POWER_OF_TWO = 0x4000000000000000L;
    @VisibleForTesting
    static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = new byte[]{19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};
    @GwtIncompatible
    @VisibleForTesting
    static final long[] powersOf10 = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};
    @GwtIncompatible
    @VisibleForTesting
    static final long[] halfPowersOf10 = new long[]{3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L};
    @VisibleForTesting
    static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
    static final long[] factorials = new long[]{1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};
    static final int[] biggestBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66};
    @VisibleForTesting
    static final int[] biggestSimpleBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61};
    private static final int SIEVE_30 = -545925251;
    private static final long[][] millerRabinBaseSets = new long[][]{{291830L, 126401071349994536L}, {885594168L, 725270293939359937L, 3569819667048198375L}, {273919523040L, 15L, 7363882082L, 992620450144556L}, {47636622961200L, 2L, 2570940L, 211991001L, 3749873356L}, {7999252175582850L, 2L, 4130806001517L, 149795463772692060L, 186635894390467037L, 3967304179347715805L}, {585226005592931976L, 2L, 123635709730000L, 9233062284813009L, 43835965440333360L, 761179012939631437L, 1263739024124850375L}, {Long.MAX_VALUE, 2L, 325L, 9375L, 28178L, 450775L, 9780504L, 1795265022L}};

    @Beta
    public static long ceilingPowerOfTwo(long x) {
        MathPreconditions.checkPositive((String)"x", (long)x);
        if (x <= 0x4000000000000000L) return 1L << -Long.numberOfLeadingZeros((long)(x - 1L));
        throw new ArithmeticException((String)("ceilingPowerOfTwo(" + x + ") is not representable as a long"));
    }

    @Beta
    public static long floorPowerOfTwo(long x) {
        MathPreconditions.checkPositive((String)"x", (long)x);
        return 1L << 63 - Long.numberOfLeadingZeros((long)x);
    }

    public static boolean isPowerOfTwo(long x) {
        boolean bl;
        boolean bl2 = x > 0L;
        if ((x & x - 1L) == 0L) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    @VisibleForTesting
    static int lessThanBranchFree(long x, long y) {
        return (int)((x - y ^ -1L ^ -1L) >>> 63);
    }

    public static int log2(long x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (long)x);
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)LongMath.isPowerOfTwo((long)x));
            }
            case 2: 
            case 3: {
                return 63 - Long.numberOfLeadingZeros((long)x);
            }
            case 4: 
            case 5: {
                return 64 - Long.numberOfLeadingZeros((long)(x - 1L));
            }
            case 6: 
            case 7: 
            case 8: {
                int leadingZeros = Long.numberOfLeadingZeros((long)x);
                long cmp = -5402926248376769404L >>> leadingZeros;
                int logFloor = 63 - leadingZeros;
                return logFloor + LongMath.lessThanBranchFree((long)cmp, (long)x);
            }
        }
        throw new AssertionError((Object)"impossible");
    }

    @GwtIncompatible
    public static int log10(long x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (long)x);
        int logFloor = LongMath.log10Floor((long)x);
        long floorPow = powersOf10[logFloor];
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(x == floorPow));
            }
            case 2: 
            case 3: {
                return logFloor;
            }
            case 4: 
            case 5: {
                return logFloor + LongMath.lessThanBranchFree((long)floorPow, (long)x);
            }
            case 6: 
            case 7: 
            case 8: {
                return logFloor + LongMath.lessThanBranchFree((long)halfPowersOf10[logFloor], (long)x);
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    static int log10Floor(long x) {
        byte y = maxLog10ForLeadingZeros[Long.numberOfLeadingZeros((long)x)];
        return y - LongMath.lessThanBranchFree((long)x, (long)powersOf10[y]);
    }

    @GwtIncompatible
    public static long pow(long b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        if (-2L <= b && b <= 2L) {
            switch ((int)b) {
                case 0: {
                    if (k != 0) return 0L;
                    return 1L;
                }
                case 1: {
                    return 1L;
                }
                case -1: {
                    if ((k & 1) != 0) return -1L;
                    return 1L;
                }
                case 2: {
                    if (k >= 64) return 0L;
                    long l = 1L << k;
                    return l;
                }
                case -2: {
                    long l;
                    if (k >= 64) return 0L;
                    if ((k & 1) == 0) {
                        l = 1L << k;
                        return l;
                    }
                    l = -(1L << k);
                    return l;
                }
            }
            throw new AssertionError();
        }
        long accum = 1L;
        block11 : do {
            switch (k) {
                default: {
                    accum *= (k & 1) == 0 ? 1L : b;
                    b *= b;
                    k >>= 1;
                    continue block11;
                }
                case 0: {
                    return accum;
                }
                case 1: 
            }
            break;
        } while (true);
        return accum * b;
    }

    @GwtIncompatible
    public static long sqrt(long x, RoundingMode mode) {
        MathPreconditions.checkNonNegative((String)"x", (long)x);
        if (LongMath.fitsInInt((long)x)) {
            return (long)IntMath.sqrt((int)((int)x), (RoundingMode)mode);
        }
        long guess = (long)Math.sqrt((double)((double)x));
        long guessSquared = guess * guess;
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(guessSquared == x));
                return guess;
            }
            case 2: 
            case 3: {
                if (x >= guessSquared) return guess;
                return guess - 1L;
            }
            case 4: 
            case 5: {
                if (x <= guessSquared) return guess;
                return guess + 1L;
            }
            case 6: 
            case 7: 
            case 8: {
                long sqrtFloor = guess - (long)(x < guessSquared ? 1 : 0);
                long halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
                return sqrtFloor + (long)LongMath.lessThanBranchFree((long)halfSquare, (long)x);
            }
        }
        throw new AssertionError();
    }

    /*
     * Unable to fully structure code
     */
    @GwtIncompatible
    public static long divide(long p, long q, RoundingMode mode) {
        Preconditions.checkNotNull(mode);
        div = p / q;
        rem = p - q * div;
        if (rem == 0L) {
            return div;
        }
        signum = 1 | (int)((p ^ q) >> 63);
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(rem == 0L));
            }
            case 2: {
                increment = false;
                ** break;
            }
            case 4: {
                increment = true;
                ** break;
            }
            case 5: {
                increment = signum > 0;
                ** break;
            }
            case 3: {
                increment = signum < 0;
                ** break;
            }
            case 6: 
            case 7: 
            case 8: {
                absRem = Math.abs((long)rem);
                cmpRemToHalfDivisor = absRem - (Math.abs((long)q) - absRem);
                if (cmpRemToHalfDivisor == 0L) {
                    increment = mode == RoundingMode.HALF_UP | mode == RoundingMode.HALF_EVEN & (div & 1L) != 0L;
                    ** break;
                }
                increment = cmpRemToHalfDivisor > 0L;
                ** break;
            }
        }
        throw new AssertionError();
lbl32: // 6 sources:
        if (increment) {
            v0 = div + (long)signum;
            return v0;
        }
        v0 = div;
        return v0;
    }

    @GwtIncompatible
    public static int mod(long x, int m) {
        return (int)LongMath.mod((long)x, (long)((long)m));
    }

    @GwtIncompatible
    public static long mod(long x, long m) {
        long l;
        if (m <= 0L) {
            throw new ArithmeticException((String)"Modulus must be positive");
        }
        long result = x % m;
        if (result >= 0L) {
            l = result;
            return l;
        }
        l = result + m;
        return l;
    }

    public static long gcd(long a, long b) {
        MathPreconditions.checkNonNegative((String)"a", (long)a);
        MathPreconditions.checkNonNegative((String)"b", (long)b);
        if (a == 0L) {
            return b;
        }
        if (b == 0L) {
            return a;
        }
        int aTwos = Long.numberOfTrailingZeros((long)a);
        a >>= aTwos;
        int bTwos = Long.numberOfTrailingZeros((long)b);
        b >>= bTwos;
        while (a != b) {
            long delta = a - b;
            long minDeltaOrZero = delta & delta >> 63;
            a = delta - minDeltaOrZero - minDeltaOrZero;
            b += minDeltaOrZero;
            a >>= Long.numberOfTrailingZeros((long)a);
        }
        return a << Math.min((int)aTwos, (int)bTwos);
    }

    @GwtIncompatible
    public static long checkedAdd(long a, long b) {
        long result = a + b;
        MathPreconditions.checkNoOverflow((boolean)((a ^ b) < 0L | (a ^ result) >= 0L));
        return result;
    }

    @GwtIncompatible
    public static long checkedSubtract(long a, long b) {
        long result = a - b;
        MathPreconditions.checkNoOverflow((boolean)((a ^ b) >= 0L | (a ^ result) >= 0L));
        return result;
    }

    @GwtIncompatible
    public static long checkedMultiply(long a, long b) {
        int leadingZeros = Long.numberOfLeadingZeros((long)a) + Long.numberOfLeadingZeros((long)(a ^ -1L)) + Long.numberOfLeadingZeros((long)b) + Long.numberOfLeadingZeros((long)(b ^ -1L));
        if (leadingZeros > 65) {
            return a * b;
        }
        MathPreconditions.checkNoOverflow((boolean)(leadingZeros >= 64));
        MathPreconditions.checkNoOverflow((boolean)(a >= 0L | b != Long.MIN_VALUE));
        long result = a * b;
        MathPreconditions.checkNoOverflow((boolean)(a == 0L || result / a == b));
        return result;
    }

    @GwtIncompatible
    public static long checkedPow(long b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        if (b >= -2L & b <= 2L) {
            switch ((int)b) {
                case 0: {
                    if (k != 0) return 0L;
                    return 1L;
                }
                case 1: {
                    return 1L;
                }
                case -1: {
                    if ((k & 1) != 0) return -1L;
                    return 1L;
                }
                case 2: {
                    MathPreconditions.checkNoOverflow((boolean)(k < 63));
                    return 1L << k;
                }
                case -2: {
                    long l;
                    MathPreconditions.checkNoOverflow((boolean)(k < 64));
                    if ((k & 1) == 0) {
                        l = 1L << k;
                        return l;
                    }
                    l = -1L << k;
                    return l;
                }
            }
            throw new AssertionError();
        }
        long accum = 1L;
        block11 : do {
            switch (k) {
                default: {
                    if ((k & 1) != 0) {
                        accum = LongMath.checkedMultiply((long)accum, (long)b);
                    }
                    if ((k >>= 1) <= 0) continue block11;
                    MathPreconditions.checkNoOverflow((boolean)(-3037000499L <= b && b <= 3037000499L));
                    b *= b;
                    continue block11;
                }
                case 0: {
                    return accum;
                }
                case 1: 
            }
            break;
        } while (true);
        return LongMath.checkedMultiply((long)accum, (long)b);
    }

    @Beta
    public static long saturatedAdd(long a, long b) {
        long naiveSum = a + b;
        if (!((a ^ b) < 0L | (a ^ naiveSum) >= 0L)) return Long.MAX_VALUE + (naiveSum >>> 63 ^ 1L);
        return naiveSum;
    }

    @Beta
    public static long saturatedSubtract(long a, long b) {
        long naiveDifference = a - b;
        if (!((a ^ b) >= 0L | (a ^ naiveDifference) >= 0L)) return Long.MAX_VALUE + (naiveDifference >>> 63 ^ 1L);
        return naiveDifference;
    }

    @Beta
    public static long saturatedMultiply(long a, long b) {
        int leadingZeros = Long.numberOfLeadingZeros((long)a) + Long.numberOfLeadingZeros((long)(a ^ -1L)) + Long.numberOfLeadingZeros((long)b) + Long.numberOfLeadingZeros((long)(b ^ -1L));
        if (leadingZeros > 65) {
            return a * b;
        }
        long limit = Long.MAX_VALUE + ((a ^ b) >>> 63);
        if (leadingZeros < 64 | a < 0L & b == Long.MIN_VALUE) {
            return limit;
        }
        long result = a * b;
        if (a == 0L) return result;
        if (result / a != b) return limit;
        return result;
    }

    @Beta
    public static long saturatedPow(long b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        if (b >= -2L & b <= 2L) {
            switch ((int)b) {
                case 0: {
                    if (k != 0) return 0L;
                    return 1L;
                }
                case 1: {
                    return 1L;
                }
                case -1: {
                    if ((k & 1) != 0) return -1L;
                    return 1L;
                }
                case 2: {
                    if (k < 63) return 1L << k;
                    return Long.MAX_VALUE;
                }
                case -2: {
                    long l;
                    if (k >= 64) {
                        return Long.MAX_VALUE + (long)(k & 1);
                    }
                    if ((k & 1) == 0) {
                        l = 1L << k;
                        return l;
                    }
                    l = -1L << k;
                    return l;
                }
            }
            throw new AssertionError();
        }
        long accum = 1L;
        long limit = Long.MAX_VALUE + (b >>> 63 & (long)(k & 1));
        block11 : do {
            switch (k) {
                default: {
                    if ((k & 1) != 0) {
                        accum = LongMath.saturatedMultiply((long)accum, (long)b);
                    }
                    if ((k >>= 1) <= 0) continue block11;
                    if (-3037000499L > b | b > 3037000499L) {
                        return limit;
                    }
                    b *= b;
                    continue block11;
                }
                case 0: {
                    return accum;
                }
                case 1: 
            }
            break;
        } while (true);
        return LongMath.saturatedMultiply((long)accum, (long)b);
    }

    @GwtIncompatible
    public static long factorial(int n) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        if (n >= factorials.length) return Long.MAX_VALUE;
        long l = factorials[n];
        return l;
    }

    public static long binomial(int n, int k) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        MathPreconditions.checkNonNegative((String)"k", (int)k);
        Preconditions.checkArgument((boolean)(k <= n), (String)"k (%s) > n (%s)", (int)k, (int)n);
        if (k > n >> 1) {
            k = n - k;
        }
        switch (k) {
            case 0: {
                return 1L;
            }
            case 1: {
                return (long)n;
            }
        }
        if (n < factorials.length) {
            return factorials[n] / (factorials[k] * factorials[n - k]);
        }
        if (k >= biggestBinomials.length) return Long.MAX_VALUE;
        if (n > biggestBinomials[k]) {
            return Long.MAX_VALUE;
        }
        if (k < biggestSimpleBinomials.length && n <= biggestSimpleBinomials[k]) {
            long result = (long)n--;
            int i = 2;
            while (i <= k) {
                result *= (long)n;
                result /= (long)i;
                --n;
                ++i;
            }
            return result;
        }
        int nBits = LongMath.log2((long)((long)n), (RoundingMode)RoundingMode.CEILING);
        long result = 1L;
        long numerator = (long)n--;
        long denominator = 1L;
        int numeratorBits = nBits;
        int i = 2;
        while (i <= k) {
            if (numeratorBits + nBits < 63) {
                numerator *= (long)n;
                denominator *= (long)i;
                numeratorBits += nBits;
            } else {
                result = LongMath.multiplyFraction((long)result, (long)numerator, (long)denominator);
                numerator = (long)n;
                denominator = (long)i;
                numeratorBits = nBits;
            }
            ++i;
            --n;
        }
        return LongMath.multiplyFraction((long)result, (long)numerator, (long)denominator);
    }

    static long multiplyFraction(long x, long numerator, long denominator) {
        if (x == 1L) {
            return numerator / denominator;
        }
        long commonDivisor = LongMath.gcd((long)x, (long)denominator);
        return (x /= commonDivisor) * (numerator / (denominator /= commonDivisor));
    }

    static boolean fitsInInt(long x) {
        if ((long)((int)x) != x) return false;
        return true;
    }

    public static long mean(long x, long y) {
        return (x & y) + ((x ^ y) >> 1);
    }

    @GwtIncompatible
    @Beta
    public static boolean isPrime(long n) {
        long[] baseSet;
        block9 : {
            if (n < 2L) {
                MathPreconditions.checkNonNegative((String)"n", (long)n);
                return false;
            }
            if (n == 2L) return true;
            if (n == 3L) return true;
            if (n == 5L) return true;
            if (n == 7L) return true;
            if (n == 11L) return true;
            if (n == 13L) {
                return true;
            }
            if ((-545925251 & 1 << (int)(n % 30L)) != 0) {
                return false;
            }
            if (n % 7L == 0L) return false;
            if (n % 11L == 0L) return false;
            if (n % 13L == 0L) {
                return false;
            }
            if (n < 289L) {
                return true;
            }
            long[][] arr$ = millerRabinBaseSets;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                baseSet = arr$[i$];
                if (n > baseSet[0]) {
                    ++i$;
                    continue;
                }
                break block9;
            }
            throw new AssertionError();
        }
        int i = 1;
        while (i < baseSet.length) {
            if (!MillerRabinTester.test((long)baseSet[i], (long)n)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private LongMath() {
    }
}

