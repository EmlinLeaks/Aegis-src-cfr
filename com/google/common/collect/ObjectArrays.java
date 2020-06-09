/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Platform;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class ObjectArrays {
    static final Object[] EMPTY_ARRAY = new Object[0];

    private ObjectArrays() {
    }

    @GwtIncompatible
    public static <T> T[] newArray(Class<T> type, int length) {
        return (Object[])Array.newInstance(type, (int)length);
    }

    public static <T> T[] newArray(T[] reference, int length) {
        return Platform.newArray(reference, (int)length);
    }

    @GwtIncompatible
    public static <T> T[] concat(T[] first, T[] second, Class<T> type) {
        T[] result = ObjectArrays.newArray(type, (int)(first.length + second.length));
        System.arraycopy(first, (int)0, result, (int)0, (int)first.length);
        System.arraycopy(second, (int)0, result, (int)first.length, (int)second.length);
        return result;
    }

    public static <T> T[] concat(@Nullable T element, T[] array) {
        T[] result = ObjectArrays.newArray(array, (int)(array.length + 1));
        result[0] = element;
        System.arraycopy(array, (int)0, result, (int)1, (int)array.length);
        return result;
    }

    public static <T> T[] concat(T[] array, @Nullable T element) {
        T[] result = ObjectArrays.arraysCopyOf(array, (int)(array.length + 1));
        result[array.length] = element;
        return result;
    }

    static <T> T[] arraysCopyOf(T[] original, int newLength) {
        T[] copy = ObjectArrays.newArray(original, (int)newLength);
        System.arraycopy(original, (int)0, copy, (int)0, (int)Math.min((int)original.length, (int)newLength));
        return copy;
    }

    static <T> T[] toArrayImpl(Collection<?> c, T[] array) {
        int size = c.size();
        if (array.length < size) {
            array = ObjectArrays.newArray(array, (int)size);
        }
        ObjectArrays.fillArray(c, (Object[])array);
        if (array.length <= size) return array;
        array[size] = null;
        return array;
    }

    static <T> T[] toArrayImpl(Object[] src, int offset, int len, T[] dst) {
        Preconditions.checkPositionIndexes((int)offset, (int)(offset + len), (int)src.length);
        if (dst.length < len) {
            dst = ObjectArrays.newArray(dst, (int)len);
        } else if (dst.length > len) {
            dst[len] = null;
        }
        System.arraycopy((Object)src, (int)offset, dst, (int)0, (int)len);
        return dst;
    }

    static Object[] toArrayImpl(Collection<?> c) {
        return ObjectArrays.fillArray(c, (Object[])new Object[c.size()]);
    }

    static Object[] copyAsObjectArray(Object[] elements, int offset, int length) {
        Preconditions.checkPositionIndexes((int)offset, (int)(offset + length), (int)elements.length);
        if (length == 0) {
            return EMPTY_ARRAY;
        }
        Object[] result = new Object[length];
        System.arraycopy((Object)elements, (int)offset, (Object)result, (int)0, (int)length);
        return result;
    }

    @CanIgnoreReturnValue
    private static Object[] fillArray(Iterable<?> elements, Object[] array) {
        int i = 0;
        Iterator<?> i$ = elements.iterator();
        while (i$.hasNext()) {
            ? element = i$.next();
            array[i++] = element;
        }
        return array;
    }

    static void swap(Object[] array, int i, int j) {
        Object temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(Object ... array) {
        return ObjectArrays.checkElementsNotNull((Object[])array, (int)array.length);
    }

    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(Object[] array, int length) {
        int i = 0;
        while (i < length) {
            ObjectArrays.checkElementNotNull((Object)array[i], (int)i);
            ++i;
        }
        return array;
    }

    @CanIgnoreReturnValue
    static Object checkElementNotNull(Object element, int index) {
        if (element != null) return element;
        throw new NullPointerException((String)("at index " + index));
    }
}

