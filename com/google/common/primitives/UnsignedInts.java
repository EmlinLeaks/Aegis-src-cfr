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
import com.google.common.primitives.Ints;
import com.google.common.primitives.ParseRequest;
import com.google.common.primitives.UnsignedInts;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedInts {
    static final long INT_MASK = 0xFFFFFFFFL;

    private UnsignedInts() {
    }

    static int flip(int value) {
        return value ^ Integer.MIN_VALUE;
    }

    public static int compare(int a, int b) {
        return Ints.compare((int)UnsignedInts.flip((int)a), (int)UnsignedInts.flip((int)b));
    }

    public static long toLong(int value) {
        return (long)value & 0xFFFFFFFFL;
    }

    public static int min(int ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int min = UnsignedInts.flip((int)array[0]);
        int i = 1;
        while (i < array.length) {
            int next = UnsignedInts.flip((int)array[i]);
            if (next < min) {
                min = next;
            }
            ++i;
        }
        return UnsignedInts.flip((int)min);
    }

    public static int max(int ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int max = UnsignedInts.flip((int)array[0]);
        int i = 1;
        while (i < array.length) {
            int next = UnsignedInts.flip((int)array[i]);
            if (next > max) {
                max = next;
            }
            ++i;
        }
        return UnsignedInts.flip((int)max);
    }

    public static String join(String separator, int ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 5));
        builder.append((String)UnsignedInts.toString((int)array[0]));
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((String)UnsignedInts.toString((int)array[i]));
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static int divide(int dividend, int divisor) {
        return (int)(UnsignedInts.toLong((int)dividend) / UnsignedInts.toLong((int)divisor));
    }

    public static int remainder(int dividend, int divisor) {
        return (int)(UnsignedInts.toLong((int)dividend) % UnsignedInts.toLong((int)divisor));
    }

    @CanIgnoreReturnValue
    public static int decode(String stringValue) {
        ParseRequest request = ParseRequest.fromString((String)stringValue);
        try {
            return UnsignedInts.parseUnsignedInt((String)request.rawValue, (int)request.radix);
        }
        catch (NumberFormatException e) {
            NumberFormatException decodeException = new NumberFormatException((String)("Error parsing value: " + stringValue));
            decodeException.initCause((Throwable)e);
            throw decodeException;
        }
    }

    @CanIgnoreReturnValue
    public static int parseUnsignedInt(String s) {
        return UnsignedInts.parseUnsignedInt((String)s, (int)10);
    }

    @CanIgnoreReturnValue
    public static int parseUnsignedInt(String string, int radix) {
        Preconditions.checkNotNull(string);
        long result = Long.parseLong((String)string, (int)radix);
        if ((result & 0xFFFFFFFFL) == result) return (int)result;
        throw new NumberFormatException((String)("Input " + string + " in base " + radix + " is not in the range of an unsigned integer"));
    }

    public static String toString(int x) {
        return UnsignedInts.toString((int)x, (int)10);
    }

    public static String toString(int x, int radix) {
        long asLong = (long)x & 0xFFFFFFFFL;
        return Long.toString((long)asLong, (int)radix);
    }
}

