/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Chars;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@GwtCompatible(emulated=true)
public final class Chars {
    public static final int BYTES = 2;

    private Chars() {
    }

    public static int hashCode(char value) {
        return value;
    }

    public static char checkedCast(long value) {
        char result = (char)((int)value);
        if ((long)result == value) return result;
        throw new IllegalArgumentException((String)("Out of range: " + value));
    }

    public static char saturatedCast(long value) {
        if (value > 65535L) {
            return '\uffff';
        }
        if (value >= 0L) return (char)((int)value);
        return '\u0000';
    }

    public static int compare(char a, char b) {
        return a - b;
    }

    public static boolean contains(char[] array, char target) {
        char[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            char value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(char[] array, char target) {
        return Chars.indexOf((char[])array, (char)target, (int)0, (int)array.length);
    }

    private static int indexOf(char[] array, char target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(char[] array, char[] target) {
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

    public static int lastIndexOf(char[] array, char target) {
        return Chars.lastIndexOf((char[])array, (char)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(char[] array, char target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static char min(char ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        char min = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] < min) {
                min = array[i];
            }
            ++i;
        }
        return min;
    }

    public static char max(char ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        char max = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    public static char[] concat(char[] ... arrays) {
        char[] array;
        int length = 0;
        char[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        char[] result = new char[length];
        int pos = 0;
        char[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            char[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    @GwtIncompatible
    public static byte[] toByteArray(char value) {
        return new byte[]{(byte)(value >> 8), (byte)value};
    }

    @GwtIncompatible
    public static char fromByteArray(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length >= 2), (String)"array too small: %s < %s", (int)bytes.length, (int)2);
        return Chars.fromBytes((byte)bytes[0], (byte)bytes[1]);
    }

    @GwtIncompatible
    public static char fromBytes(byte b1, byte b2) {
        return (char)(b1 << 8 | b2 & 255);
    }

    public static char[] ensureCapacity(char[] array, int minLength, int padding) {
        char[] arrc;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrc = Arrays.copyOf((char[])array, (int)(minLength + padding));
            return arrc;
        }
        arrc = array;
        return arrc;
    }

    public static String join(String separator, char ... array) {
        Preconditions.checkNotNull(separator);
        int len = array.length;
        if (len == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(len + separator.length() * (len - 1)));
        builder.append((char)array[0]);
        int i = 1;
        while (i < len) {
            builder.append((String)separator).append((char)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<char[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static char[] toArray(Collection<Character> collection) {
        if (collection instanceof CharArrayAsList) {
            return ((CharArrayAsList)collection).toCharArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        char[] array = new char[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Character)Preconditions.checkNotNull(boxedArray[i])).charValue();
            ++i;
        }
        return array;
    }

    public static List<Character> asList(char ... backingArray) {
        if (backingArray.length != 0) return new CharArrayAsList((char[])backingArray);
        return Collections.emptyList();
    }

    static /* synthetic */ int access$000(char[] x0, char x1, int x2, int x3) {
        return Chars.indexOf((char[])x0, (char)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(char[] x0, char x1, int x2, int x3) {
        return Chars.lastIndexOf((char[])x0, (char)x1, (int)x2, (int)x3);
    }
}

