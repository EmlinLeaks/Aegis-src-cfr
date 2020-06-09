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
import com.google.common.primitives.Ints;
import java.math.RoundingMode;

@GwtCompatible(emulated=true)
public final class IntMath {
    @VisibleForTesting
    static final int MAX_SIGNED_POWER_OF_TWO = 1073741824;
    @VisibleForTesting
    static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
    @VisibleForTesting
    static final byte[] maxLog10ForLeadingZeros = new byte[]{9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0};
    @VisibleForTesting
    static final int[] powersOf10 = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
    @VisibleForTesting
    static final int[] halfPowersOf10 = new int[]{3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};
    @VisibleForTesting
    static final int FLOOR_SQRT_MAX_INT = 46340;
    private static final int[] factorials = new int[]{1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600};
    @VisibleForTesting
    static int[] biggestBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33};

    @Beta
    public static int ceilingPowerOfTwo(int x) {
        MathPreconditions.checkPositive((String)"x", (int)x);
        if (x <= 1073741824) return 1 << -Integer.numberOfLeadingZeros((int)(x - 1));
        throw new ArithmeticException((String)("ceilingPowerOfTwo(" + x + ") not representable as an int"));
    }

    @Beta
    public static int floorPowerOfTwo(int x) {
        MathPreconditions.checkPositive((String)"x", (int)x);
        return Integer.highestOneBit((int)x);
    }

    public static boolean isPowerOfTwo(int x) {
        boolean bl;
        boolean bl2 = x > 0;
        if ((x & x - 1) == 0) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    @VisibleForTesting
    static int lessThanBranchFree(int x, int y) {
        return ~(~(x - y)) >>> 31;
    }

    public static int log2(int x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (int)x);
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)IntMath.isPowerOfTwo((int)x));
            }
            case 2: 
            case 3: {
                return 31 - Integer.numberOfLeadingZeros((int)x);
            }
            case 4: 
            case 5: {
                return 32 - Integer.numberOfLeadingZeros((int)(x - 1));
            }
            case 6: 
            case 7: 
            case 8: {
                int leadingZeros = Integer.numberOfLeadingZeros((int)x);
                int cmp = -1257966797 >>> leadingZeros;
                int logFloor = 31 - leadingZeros;
                return logFloor + IntMath.lessThanBranchFree((int)cmp, (int)x);
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    public static int log10(int x, RoundingMode mode) {
        MathPreconditions.checkPositive((String)"x", (int)x);
        int logFloor = IntMath.log10Floor((int)x);
        int floorPow = powersOf10[logFloor];
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
                return logFloor + IntMath.lessThanBranchFree((int)floorPow, (int)x);
            }
            case 6: 
            case 7: 
            case 8: {
                return logFloor + IntMath.lessThanBranchFree((int)halfPowersOf10[logFloor], (int)x);
            }
        }
        throw new AssertionError();
    }

    private static int log10Floor(int x) {
        byte y = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros((int)x)];
        return y - IntMath.lessThanBranchFree((int)x, (int)powersOf10[y]);
    }

    @GwtIncompatible
    public static int pow(int b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        switch (b) {
            case 0: {
                if (k != 0) return 0;
                return 1;
            }
            case 1: {
                return 1;
            }
            case -1: {
                if ((k & 1) != 0) return -1;
                return 1;
            }
            case 2: {
                if (k >= 32) return 0;
                int n = 1 << k;
                return n;
            }
            case -2: {
                int n;
                if (k >= 32) return 0;
                if ((k & 1) == 0) {
                    n = 1 << k;
                    return n;
                }
                n = -(1 << k);
                return n;
            }
        }
        int accum = 1;
        block11 : do {
            switch (k) {
                default: {
                    accum *= (k & 1) == 0 ? 1 : b;
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
        return b * accum;
    }

    @GwtIncompatible
    public static int sqrt(int x, RoundingMode mode) {
        MathPreconditions.checkNonNegative((String)"x", (int)x);
        int sqrtFloor = IntMath.sqrtFloor((int)x);
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(sqrtFloor * sqrtFloor == x));
            }
            case 2: 
            case 3: {
                return sqrtFloor;
            }
            case 4: 
            case 5: {
                return sqrtFloor + IntMath.lessThanBranchFree((int)(sqrtFloor * sqrtFloor), (int)x);
            }
            case 6: 
            case 7: 
            case 8: {
                int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
                return sqrtFloor + IntMath.lessThanBranchFree((int)halfSquare, (int)x);
            }
        }
        throw new AssertionError();
    }

    private static int sqrtFloor(int x) {
        return (int)Math.sqrt((double)((double)x));
    }

    /*
     * Unable to fully structure code
     */
    public static int divide(int p, int q, RoundingMode mode) {
        Preconditions.checkNotNull(mode);
        if (q == 0) {
            throw new ArithmeticException((String)"/ by zero");
        }
        div = p / q;
        rem = p - q * div;
        if (rem == 0) {
            return div;
        }
        signum = 1 | (p ^ q) >> 31;
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)(rem == 0));
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
                absRem = Math.abs((int)rem);
                cmpRemToHalfDivisor = absRem - (Math.abs((int)q) - absRem);
                if (cmpRemToHalfDivisor == 0) {
                    increment = mode == RoundingMode.HALF_UP || (mode == RoundingMode.HALF_EVEN & (div & 1) != 0) != false;
                    ** break;
                }
                increment = cmpRemToHalfDivisor > 0;
                ** break;
            }
        }
        throw new AssertionError();
lbl34: // 6 sources:
        if (increment) {
            v0 = div + signum;
            return v0;
        }
        v0 = div;
        return v0;
    }

    public static int mod(int x, int m) {
        int n;
        if (m <= 0) {
            throw new ArithmeticException((String)("Modulus " + m + " must be > 0"));
        }
        int result = x % m;
        if (result >= 0) {
            n = result;
            return n;
        }
        n = result + m;
        return n;
    }

    public static int gcd(int a, int b) {
        MathPreconditions.checkNonNegative((String)"a", (int)a);
        MathPreconditions.checkNonNegative((String)"b", (int)b);
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        int aTwos = Integer.numberOfTrailingZeros((int)a);
        a >>= aTwos;
        int bTwos = Integer.numberOfTrailingZeros((int)b);
        b >>= bTwos;
        while (a != b) {
            int delta = a - b;
            int minDeltaOrZero = delta & delta >> 31;
            a = delta - minDeltaOrZero - minDeltaOrZero;
            b += minDeltaOrZero;
            a >>= Integer.numberOfTrailingZeros((int)a);
        }
        return a << Math.min((int)aTwos, (int)bTwos);
    }

    public static int checkedAdd(int a, int b) {
        long result = (long)a + (long)b;
        MathPreconditions.checkNoOverflow((boolean)(result == (long)((int)result)));
        return (int)result;
    }

    public static int checkedSubtract(int a, int b) {
        long result = (long)a - (long)b;
        MathPreconditions.checkNoOverflow((boolean)(result == (long)((int)result)));
        return (int)result;
    }

    public static int checkedMultiply(int a, int b) {
        long result = (long)a * (long)b;
        MathPreconditions.checkNoOverflow((boolean)(result == (long)((int)result)));
        return (int)result;
    }

    public static int checkedPow(int b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        switch (b) {
            case 0: {
                if (k != 0) return 0;
                return 1;
            }
            case 1: {
                return 1;
            }
            case -1: {
                if ((k & 1) != 0) return -1;
                return 1;
            }
            case 2: {
                MathPreconditions.checkNoOverflow((boolean)(k < 31));
                return 1 << k;
            }
            case -2: {
                int n;
                MathPreconditions.checkNoOverflow((boolean)(k < 32));
                if ((k & 1) == 0) {
                    n = 1 << k;
                    return n;
                }
                n = -1 << k;
                return n;
            }
        }
        int accum = 1;
        block11 : do {
            switch (k) {
                default: {
                    if ((k & 1) != 0) {
                        accum = IntMath.checkedMultiply((int)accum, (int)b);
                    }
                    if ((k >>= 1) <= 0) continue block11;
                    MathPreconditions.checkNoOverflow((boolean)(-46340 <= b & b <= 46340));
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
        return IntMath.checkedMultiply((int)accum, (int)b);
    }

    @Beta
    public static int saturatedAdd(int a, int b) {
        return Ints.saturatedCast((long)((long)a + (long)b));
    }

    @Beta
    public static int saturatedSubtract(int a, int b) {
        return Ints.saturatedCast((long)((long)a - (long)b));
    }

    @Beta
    public static int saturatedMultiply(int a, int b) {
        return Ints.saturatedCast((long)((long)a * (long)b));
    }

    @Beta
    public static int saturatedPow(int b, int k) {
        MathPreconditions.checkNonNegative((String)"exponent", (int)k);
        switch (b) {
            case 0: {
                if (k != 0) return 0;
                return 1;
            }
            case 1: {
                return 1;
            }
            case -1: {
                if ((k & 1) != 0) return -1;
                return 1;
            }
            case 2: {
                if (k < 31) return 1 << k;
                return Integer.MAX_VALUE;
            }
            case -2: {
                int n;
                if (k >= 32) {
                    return Integer.MAX_VALUE + (k & 1);
                }
                if ((k & 1) == 0) {
                    n = 1 << k;
                    return n;
                }
                n = -1 << k;
                return n;
            }
        }
        int accum = 1;
        int limit = Integer.MAX_VALUE + (b >>> 31 & (k & 1));
        block11 : do {
            switch (k) {
                default: {
                    if ((k & 1) != 0) {
                        accum = IntMath.saturatedMultiply((int)accum, (int)b);
                    }
                    if ((k >>= 1) <= 0) continue block11;
                    if (-46340 > b | b > 46340) {
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
        return IntMath.saturatedMultiply((int)accum, (int)b);
    }

    public static int factorial(int n) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        if (n >= factorials.length) return Integer.MAX_VALUE;
        int n2 = factorials[n];
        return n2;
    }

    @GwtIncompatible
    public static int binomial(int n, int k) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        MathPreconditions.checkNonNegative((String)"k", (int)k);
        Preconditions.checkArgument((boolean)(k <= n), (String)"k (%s) > n (%s)", (int)k, (int)n);
        if (k > n >> 1) {
            k = n - k;
        }
        if (k >= biggestBinomials.length) return Integer.MAX_VALUE;
        if (n > biggestBinomials[k]) {
            return Integer.MAX_VALUE;
        }
        switch (k) {
            case 0: {
                return 1;
            }
            case 1: {
                return n;
            }
        }
        long result = 1L;
        int i = 0;
        while (i < k) {
            result *= (long)(n - i);
            result /= (long)(i + 1);
            ++i;
        }
        return (int)result;
    }

    public static int mean(int x, int y) {
        return (x & y) + ((x ^ y) >> 1);
    }

    @GwtIncompatible
    @Beta
    public static boolean isPrime(int n) {
        return LongMath.isPrime((long)((long)n));
    }

    private IntMath() {
    }
}

