/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import com.google.common.primitives.ParseRequest;
import com.google.common.primitives.UnsignedLongs;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedLongs {
    public static final long MAX_VALUE = -1L;
    private static final long[] maxValueDivs = new long[37];
    private static final int[] maxValueMods = new int[37];
    private static final int[] maxSafeDigits = new int[37];

    private UnsignedLongs() {
    }

    private static long flip(long a) {
        return a ^ Long.MIN_VALUE;
    }

    public static int compare(long a, long b) {
        return Longs.compare((long)UnsignedLongs.flip((long)a), (long)UnsignedLongs.flip((long)b));
    }

    public static long min(long ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        long min = UnsignedLongs.flip((long)array[0]);
        int i = 1;
        while (i < array.length) {
            long next = UnsignedLongs.flip((long)array[i]);
            if (next < min) {
                min = next;
            }
            ++i;
        }
        return UnsignedLongs.flip((long)min);
    }

    public static long max(long ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        long max = UnsignedLongs.flip((long)array[0]);
        int i = 1;
        while (i < array.length) {
            long next = UnsignedLongs.flip((long)array[i]);
            if (next > max) {
                max = next;
            }
            ++i;
        }
        return UnsignedLongs.flip((long)max);
    }

    public static String join(String separator, long ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 5));
        builder.append((String)UnsignedLongs.toString((long)array[0]));
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((String)UnsignedLongs.toString((long)array[i]));
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static long divide(long dividend, long divisor) {
        int n;
        if (divisor < 0L) {
            if (UnsignedLongs.compare((long)dividend, (long)divisor) >= 0) return 1L;
            return 0L;
        }
        if (dividend >= 0L) {
            return dividend / divisor;
        }
        long quotient = (dividend >>> 1) / divisor << 1;
        long rem = dividend - quotient * divisor;
        if (UnsignedLongs.compare((long)rem, (long)divisor) >= 0) {
            n = 1;
            return quotient + (long)n;
        }
        n = 0;
        return quotient + (long)n;
    }

    public static long remainder(long dividend, long divisor) {
        long l;
        if (divisor < 0L) {
            if (UnsignedLongs.compare((long)dividend, (long)divisor) >= 0) return dividend - divisor;
            return dividend;
        }
        if (dividend >= 0L) {
            return dividend % divisor;
        }
        long quotient = (dividend >>> 1) / divisor << 1;
        long rem = dividend - quotient * divisor;
        if (UnsignedLongs.compare((long)rem, (long)divisor) >= 0) {
            l = divisor;
            return rem - l;
        }
        l = 0L;
        return rem - l;
    }

    @CanIgnoreReturnValue
    public static long parseUnsignedLong(String string) {
        return UnsignedLongs.parseUnsignedLong((String)string, (int)10);
    }

    @CanIgnoreReturnValue
    public static long decode(String stringValue) {
        ParseRequest request = ParseRequest.fromString((String)stringValue);
        try {
            return UnsignedLongs.parseUnsignedLong((String)request.rawValue, (int)request.radix);
        }
        catch (NumberFormatException e) {
            NumberFormatException decodeException = new NumberFormatException((String)("Error parsing value: " + stringValue));
            decodeException.initCause((Throwable)e);
            throw decodeException;
        }
    }

    @CanIgnoreReturnValue
    public static long parseUnsignedLong(String string, int radix) {
        Preconditions.checkNotNull(string);
        if (string.length() == 0) {
            throw new NumberFormatException((String)"empty string");
        }
        if (radix < 2) throw new NumberFormatException((String)("illegal radix: " + radix));
        if (radix > 36) {
            throw new NumberFormatException((String)("illegal radix: " + radix));
        }
        int maxSafePos = maxSafeDigits[radix] - 1;
        long value = 0L;
        int pos = 0;
        while (pos < string.length()) {
            int digit = Character.digit((char)string.charAt((int)pos), (int)radix);
            if (digit == -1) {
                throw new NumberFormatException((String)string);
            }
            if (pos > maxSafePos && UnsignedLongs.overflowInParse((long)value, (int)digit, (int)radix)) {
                throw new NumberFormatException((String)("Too large for unsigned long: " + string));
            }
            value = value * (long)radix + (long)digit;
            ++pos;
        }
        return value;
    }

    private static boolean overflowInParse(long current, int digit, int radix) {
        if (current < 0L) return true;
        if (current < maxValueDivs[radix]) {
            return false;
        }
        if (current > maxValueDivs[radix]) {
            return true;
        }
        if (digit <= maxValueMods[radix]) return false;
        return true;
    }

    public static String toString(long x) {
        return UnsignedLongs.toString((long)x, (int)10);
    }

    public static String toString(long x, int radix) {
        Preconditions.checkArgument((boolean)(radix >= 2 && radix <= 36), (String)"radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", (int)radix);
        if (x == 0L) {
            return "0";
        }
        if (x > 0L) {
            return Long.toString((long)x, (int)radix);
        }
        char[] buf = new char[64];
        int i = buf.length;
        if ((radix & radix - 1) == 0) {
            int shift = Integer.numberOfTrailingZeros((int)radix);
            int mask = radix - 1;
            do {
                buf[--i] = Character.forDigit((int)((int)x & mask), (int)radix);
            } while ((x >>>= shift) != 0L);
            return new String((char[])buf, (int)i, (int)(buf.length - i));
        }
        long quotient = (radix & 1) == 0 ? (x >>> 1) / (long)(radix >>> 1) : UnsignedLongs.divide((long)x, (long)((long)radix));
        long rem = x - quotient * (long)radix;
        buf[--i] = Character.forDigit((int)((int)rem), (int)radix);
        x = quotient;
        while (x > 0L) {
            buf[--i] = Character.forDigit((int)((int)(x % (long)radix)), (int)radix);
            x /= (long)radix;
        }
        return new String((char[])buf, (int)i, (int)(buf.length - i));
    }

    static {
        BigInteger overflow = new BigInteger((String)"10000000000000000", (int)16);
        int i = 2;
        while (i <= 36) {
            UnsignedLongs.maxValueDivs[i] = UnsignedLongs.divide((long)-1L, (long)((long)i));
            UnsignedLongs.maxValueMods[i] = (int)UnsignedLongs.remainder((long)-1L, (long)((long)i));
            UnsignedLongs.maxSafeDigits[i] = overflow.toString((int)i).length() - 1;
            ++i;
        }
    }
}

