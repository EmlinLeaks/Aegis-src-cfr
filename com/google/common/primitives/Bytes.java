/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@GwtCompatible
public final class Bytes {
    private Bytes() {
    }

    public static int hashCode(byte value) {
        return value;
    }

    public static boolean contains(byte[] array, byte target) {
        byte[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            byte value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(byte[] array, byte target) {
        return Bytes.indexOf((byte[])array, (byte)target, (int)0, (int)array.length);
    }

    private static int indexOf(byte[] array, byte target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(byte[] array, byte[] target) {
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

    public static int lastIndexOf(byte[] array, byte target) {
        return Bytes.lastIndexOf((byte[])array, (byte)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(byte[] array, byte target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static byte[] concat(byte[] ... arrays) {
        byte[] array;
        int length = 0;
        byte[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        byte[] result = new byte[length];
        int pos = 0;
        byte[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            byte[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    public static byte[] ensureCapacity(byte[] array, int minLength, int padding) {
        byte[] arrby;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrby = Arrays.copyOf((byte[])array, (int)(minLength + padding));
            return arrby;
        }
        arrby = array;
        return arrby;
    }

    public static byte[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof ByteArrayAsList) {
            return ((ByteArrayAsList)collection).toByteArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        byte[] array = new byte[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).byteValue();
            ++i;
        }
        return array;
    }

    public static List<Byte> asList(byte ... backingArray) {
        if (backingArray.length != 0) return new ByteArrayAsList((byte[])backingArray);
        return Collections.emptyList();
    }

    static /* synthetic */ int access$000(byte[] x0, byte x1, int x2, int x3) {
        return Bytes.indexOf((byte[])x0, (byte)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(byte[] x0, byte x1, int x2, int x3) {
        return Bytes.lastIndexOf((byte[])x0, (byte)x1, (int)x2, (int)x3);
    }
}

