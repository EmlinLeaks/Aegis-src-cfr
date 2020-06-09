/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nullable
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@GwtCompatible
public final class Longs {
    public static final int BYTES = 8;
    public static final long MAX_POWER_OF_TWO = 0x4000000000000000L;
    private static final byte[] asciiDigits = Longs.createAsciiDigits();

    private Longs() {
    }

    public static int hashCode(long value) {
        return (int)(value ^ value >>> 32);
    }

    public static int compare(long a, long b) {
        if (a < b) {
            return -1;
        }
        if (a <= b) return 0;
        return 1;
    }

    public static boolean contains(long[] array, long target) {
        long[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            long value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(long[] array, long target) {
        return Longs.indexOf((long[])array, (long)target, (int)0, (int)array.length);
    }

    private static int indexOf(long[] array, long target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(long[] array, long[] target) {
        Preconditions.checkNotNull(array, (Object)"array");
        Preconditions.checkNotNull(target, (Object)"target");
        if (target.length == 0) {
            return 0;
        }
        int i = 0;
        block0 : while (i < array.length - target.length + 1) {
            int j = 0;
            while (j < target.length) {
                if (array[i + j] != target[j]) {
                    ++i;
                    continue block0;
                }
                ++j;
            }
            return i;
            break;
        }
        return -1;
    }

    public static int lastIndexOf(long[] array, long target) {
        return Longs.lastIndexOf((long[])array, (long)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(long[] array, long target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static long min(long ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        long min = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] < min) {
                min = array[i];
            }
            ++i;
        }
        return min;
    }

    public static long max(long ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        long max = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    public static long[] concat(long[] ... arrays) {
        long[] array;
        int length = 0;
        long[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        long[] result = new long[length];
        int pos = 0;
        long[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            long[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    public static byte[] toByteArray(long value) {
        byte[] result = new byte[8];
        int i = 7;
        while (i >= 0) {
            result[i] = (byte)((int)(value & 255L));
            value >>= 8;
            --i;
        }
        return result;
    }

    public static long fromByteArray(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length >= 8), (String)"array too small: %s < %s", (int)bytes.length, (int)8);
        return Longs.fromBytes((byte)bytes[0], (byte)bytes[1], (byte)bytes[2], (byte)bytes[3], (byte)bytes[4], (byte)bytes[5], (byte)bytes[6], (byte)bytes[7]);
    }

    public static long fromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return ((long)b1 & 255L) << 56 | ((long)b2 & 255L) << 48 | ((long)b3 & 255L) << 40 | ((long)b4 & 255L) << 32 | ((long)b5 & 255L) << 24 | ((long)b6 & 255L) << 16 | ((long)b7 & 255L) << 8 | (long)b8 & 255L;
    }

    private static byte[] createAsciiDigits() {
        int i;
        byte[] result = new byte[128];
        Arrays.fill((byte[])result, (byte)-1);
        for (i = 0; i <= 9; ++i) {
            result[48 + i] = (byte)i;
        }
        i = 0;
        while (i <= 26) {
            result[65 + i] = (byte)(10 + i);
            result[97 + i] = (byte)(10 + i);
            ++i;
        }
        return result;
    }

    private static int digit(char c) {
        if (c >= '?') return -1;
        int n = asciiDigits[c];
        return n;
    }

    @Nullable
    @CheckForNull
    @Beta
    public static Long tryParse(String string) {
        return Longs.tryParse((String)string, (int)10);
    }

    @Nullable
    @CheckForNull
    @Beta
    public static Long tryParse(String string, int radix) {
        int index;
        int digit;
        if (Preconditions.checkNotNull(string).isEmpty()) {
            return null;
        }
        if (radix < 2) throw new IllegalArgumentException((String)("radix must be between MIN_RADIX and MAX_RADIX but was " + radix));
        if (radix > 36) {
            throw new IllegalArgumentException((String)("radix must be between MIN_RADIX and MAX_RADIX but was " + radix));
        }
        boolean negative = string.charAt((int)0) == '-';
        int n = index = negative ? 1 : 0;
        if (index == string.length()) {
            return null;
        }
        if ((digit = Longs.digit((char)string.charAt((int)index++))) < 0) return null;
        if (digit >= radix) {
            return null;
        }
        long accum = (long)(-digit);
        long cap = Long.MIN_VALUE / (long)radix;
        while (index < string.length()) {
            if ((digit = Longs.digit((char)string.charAt((int)index++))) < 0) return null;
            if (digit >= radix) return null;
            if (accum < cap) {
                return null;
            }
            if ((accum *= (long)radix) < Long.MIN_VALUE + (long)digit) {
                return null;
            }
            accum -= (long)digit;
        }
        if (negative) {
            return Long.valueOf((long)accum);
        }
        if (accum != Long.MIN_VALUE) return Long.valueOf((long)(-accum));
        return null;
    }

    @Beta
    public static Converter<String, Long> stringConverter() {
        return LongConverter.INSTANCE;
    }

    public static long[] ensureCapacity(long[] array, int minLength, int padding) {
        long[] arrl;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrl = Arrays.copyOf((long[])array, (int)(minLength + padding));
            return arrl;
        }
        arrl = array;
        return arrl;
    }

    public static String join(String separator, long ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 10));
        builder.append((long)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((long)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static long[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof LongArrayAsList) {
            return ((LongArrayAsList)collection).toLongArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        long[] array = new long[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).longValue();
            ++i;
        }
        return array;
    }

    public static List<Long> asList(long ... backingArray) {
        if (backingArray.length != 0) return new LongArrayAsList((long[])backingArray);
        return Collections.emptyList();
    }

    static /* synthetic */ int access$000(long[] x0, long x1, int x2, int x3) {
        return Longs.indexOf((long[])x0, (long)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(long[] x0, long x1, int x2, int x3) {
        return Longs.lastIndexOf((long[])x0, (long)x1, (int)x2, (int)x3);
    }
}

