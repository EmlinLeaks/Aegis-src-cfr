/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import javax.annotation.Nullable;

@GwtCompatible
@CanIgnoreReturnValue
final class MathPreconditions {
    static int checkPositive(@Nullable String role, int x) {
        if (x > 0) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be > 0"));
    }

    static long checkPositive(@Nullable String role, long x) {
        if (x > 0L) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be > 0"));
    }

    static BigInteger checkPositive(@Nullable String role, BigInteger x) {
        if (x.signum() > 0) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be > 0"));
    }

    static int checkNonNegative(@Nullable String role, int x) {
        if (x >= 0) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be >= 0"));
    }

    static long checkNonNegative(@Nullable String role, long x) {
        if (x >= 0L) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be >= 0"));
    }

    static BigInteger checkNonNegative(@Nullable String role, BigInteger x) {
        if (x.signum() >= 0) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be >= 0"));
    }

    static double checkNonNegative(@Nullable String role, double x) {
        if (x >= 0.0) return x;
        throw new IllegalArgumentException((String)(role + " (" + x + ") must be >= 0"));
    }

    static void checkRoundingUnnecessary(boolean condition) {
        if (condition) return;
        throw new ArithmeticException((String)"mode was UNNECESSARY, but rounding was necessary");
    }

    static void checkInRange(boolean condition) {
        if (condition) return;
        throw new ArithmeticException((String)"not in range");
    }

    static void checkNoOverflow(boolean condition) {
        if (condition) return;
        throw new ArithmeticException((String)"overflow");
    }

    private MathPreconditions() {
    }
}

