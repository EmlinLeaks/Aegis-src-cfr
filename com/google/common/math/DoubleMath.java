/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.DoubleMath;
import com.google.common.math.DoubleUtils;
import com.google.common.math.LongMath;
import com.google.common.math.MathPreconditions;
import com.google.common.primitives.Booleans;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;

@GwtCompatible(emulated=true)
public final class DoubleMath {
    private static final double MIN_INT_AS_DOUBLE = -2.147483648E9;
    private static final double MAX_INT_AS_DOUBLE = 2.147483647E9;
    private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E18;
    private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18;
    private static final double LN_2 = Math.log((double)2.0);
    @VisibleForTesting
    static final int MAX_FACTORIAL = 170;
    @VisibleForTesting
    static final double[] everySixteenthFactorial = new double[]{1.0, 2.0922789888E13, 2.631308369336935E35, 1.2413915592536073E61, 1.2688693218588417E89, 7.156945704626381E118, 9.916779348709496E149, 1.974506857221074E182, 3.856204823625804E215, 5.5502938327393044E249, 4.7147236359920616E284};

    @GwtIncompatible
    static double roundIntermediate(double x, RoundingMode mode) {
        if (!DoubleUtils.isFinite((double)x)) {
            throw new ArithmeticException((String)"input is infinite or NaN");
        }
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)DoubleMath.isMathematicalInteger((double)x));
                return x;
            }
            case 2: {
                if (x >= 0.0) return x;
                if (!DoubleMath.isMathematicalInteger((double)x)) return (double)((long)x - 1L);
                return x;
            }
            case 3: {
                if (x <= 0.0) return x;
                if (!DoubleMath.isMathematicalInteger((double)x)) return (double)((long)x + 1L);
                return x;
            }
            case 4: {
                return x;
            }
            case 5: {
                int n;
                if (DoubleMath.isMathematicalInteger((double)x)) {
                    return x;
                }
                if (x > 0.0) {
                    n = 1;
                    return (double)((long)x + (long)n);
                }
                n = -1;
                return (double)((long)x + (long)n);
            }
            case 6: {
                return Math.rint((double)x);
            }
            case 7: {
                double z = Math.rint((double)x);
                if (Math.abs((double)(x - z)) != 0.5) return z;
                return x + Math.copySign((double)0.5, (double)x);
            }
            case 8: {
                double z = Math.rint((double)x);
                if (Math.abs((double)(x - z)) != 0.5) return z;
                return x;
            }
        }
        throw new AssertionError();
    }

    @GwtIncompatible
    public static int roundToInt(double x, RoundingMode mode) {
        double z = DoubleMath.roundIntermediate((double)x, (RoundingMode)mode);
        MathPreconditions.checkInRange((boolean)(z > -2.147483649E9 & z < 2.147483648E9));
        return (int)z;
    }

    @GwtIncompatible
    public static long roundToLong(double x, RoundingMode mode) {
        double z = DoubleMath.roundIntermediate((double)x, (RoundingMode)mode);
        MathPreconditions.checkInRange((boolean)(-9.223372036854776E18 - z < 1.0 & z < 9.223372036854776E18));
        return (long)z;
    }

    @GwtIncompatible
    public static BigInteger roundToBigInteger(double x, RoundingMode mode) {
        BigInteger bigInteger;
        if (-9.223372036854776E18 - (x = DoubleMath.roundIntermediate((double)x, (RoundingMode)mode)) < 1.0 & x < 9.223372036854776E18) {
            return BigInteger.valueOf((long)((long)x));
        }
        int exponent = Math.getExponent((double)x);
        long significand = DoubleUtils.getSignificand((double)x);
        BigInteger result = BigInteger.valueOf((long)significand).shiftLeft((int)(exponent - 52));
        if (x < 0.0) {
            bigInteger = result.negate();
            return bigInteger;
        }
        bigInteger = result;
        return bigInteger;
    }

    @GwtIncompatible
    public static boolean isPowerOfTwo(double x) {
        if (!(x > 0.0)) return false;
        if (!DoubleUtils.isFinite((double)x)) return false;
        if (!LongMath.isPowerOfTwo((long)DoubleUtils.getSignificand((double)x))) return false;
        return true;
    }

    public static double log2(double x) {
        return Math.log((double)x) / LN_2;
    }

    /*
     * Unable to fully structure code
     */
    @GwtIncompatible
    public static int log2(double x, RoundingMode mode) {
        Preconditions.checkArgument((boolean)(x > 0.0 && DoubleUtils.isFinite((double)x) != false), (Object)"x must be positive and finite");
        exponent = Math.getExponent((double)x);
        if (!DoubleUtils.isNormal((double)x)) {
            return DoubleMath.log2((double)(x * 4.503599627370496E15), (RoundingMode)mode) - 52;
        }
        switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
            case 1: {
                MathPreconditions.checkRoundingUnnecessary((boolean)DoubleMath.isPowerOfTwo((double)x));
            }
            case 2: {
                increment = false;
                ** break;
            }
            case 3: {
                increment = DoubleMath.isPowerOfTwo((double)x) == false;
                ** break;
            }
            case 4: {
                increment = exponent < 0 & DoubleMath.isPowerOfTwo((double)x) == false;
                ** break;
            }
            case 5: {
                increment = exponent >= 0 & DoubleMath.isPowerOfTwo((double)x) == false;
                ** break;
            }
            case 6: 
            case 7: 
            case 8: {
                xScaled = DoubleUtils.scaleNormalize((double)x);
                increment = xScaled * xScaled > 2.0;
                ** break;
            }
        }
        throw new AssertionError();
lbl25: // 5 sources:
        if (increment) {
            v0 = exponent + 1;
            return v0;
        }
        v0 = exponent;
        return v0;
    }

    @GwtIncompatible
    public static boolean isMathematicalInteger(double x) {
        if (!DoubleUtils.isFinite((double)x)) return false;
        if (x == 0.0) return true;
        if (52 - Long.numberOfTrailingZeros((long)DoubleUtils.getSignificand((double)x)) > Math.getExponent((double)x)) return false;
        return true;
    }

    public static double factorial(int n) {
        MathPreconditions.checkNonNegative((String)"n", (int)n);
        if (n > 170) {
            return Double.POSITIVE_INFINITY;
        }
        double accum = 1.0;
        int i = 1 + (n & -16);
        while (i <= n) {
            accum *= (double)i;
            ++i;
        }
        return accum * everySixteenthFactorial[n >> 4];
    }

    public static boolean fuzzyEquals(double a, double b, double tolerance) {
        MathPreconditions.checkNonNegative((String)"tolerance", (double)tolerance);
        if (Math.copySign((double)(a - b), (double)1.0) <= tolerance) return true;
        if (a == b) return true;
        if (!Double.isNaN((double)a)) return false;
        if (!Double.isNaN((double)b)) return false;
        return true;
    }

    public static int fuzzyCompare(double a, double b, double tolerance) {
        if (DoubleMath.fuzzyEquals((double)a, (double)b, (double)tolerance)) {
            return 0;
        }
        if (a < b) {
            return -1;
        }
        if (!(a > b)) return Booleans.compare((boolean)Double.isNaN((double)a), (boolean)Double.isNaN((double)b));
        return 1;
    }

    @Deprecated
    @GwtIncompatible
    public static double mean(double ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0), (Object)"Cannot take mean of 0 values");
        long count = 1L;
        double mean = DoubleMath.checkFinite((double)values[0]);
        int index = 1;
        while (index < values.length) {
            DoubleMath.checkFinite((double)values[index]);
            mean += (values[index] - mean) / (double)(++count);
            ++index;
        }
        return mean;
    }

    @Deprecated
    public static double mean(int ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0), (Object)"Cannot take mean of 0 values");
        long sum = 0L;
        int index = 0;
        while (index < values.length) {
            sum += (long)values[index];
            ++index;
        }
        return (double)sum / (double)values.length;
    }

    @Deprecated
    public static double mean(long ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0), (Object)"Cannot take mean of 0 values");
        long count = 1L;
        double mean = (double)values[0];
        int index = 1;
        while (index < values.length) {
            mean += ((double)values[index] - mean) / (double)(++count);
            ++index;
        }
        return mean;
    }

    @Deprecated
    @GwtIncompatible
    public static double mean(Iterable<? extends Number> values) {
        return DoubleMath.mean(values.iterator());
    }

    @Deprecated
    @GwtIncompatible
    public static double mean(Iterator<? extends Number> values) {
        Preconditions.checkArgument((boolean)values.hasNext(), (Object)"Cannot take mean of 0 values");
        long count = 1L;
        double mean = DoubleMath.checkFinite((double)values.next().doubleValue());
        while (values.hasNext()) {
            double value = DoubleMath.checkFinite((double)values.next().doubleValue());
            mean += (value - mean) / (double)(++count);
        }
        return mean;
    }

    @GwtIncompatible
    @CanIgnoreReturnValue
    private static double checkFinite(double argument) {
        Preconditions.checkArgument((boolean)DoubleUtils.isFinite((double)argument));
        return argument;
    }

    private DoubleMath() {
    }
}

