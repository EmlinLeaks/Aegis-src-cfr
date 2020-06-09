/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@GwtCompatible
public final class Booleans {
    private Booleans() {
    }

    public static int hashCode(boolean value) {
        if (!value) return 1237;
        return 1231;
    }

    public static int compare(boolean a, boolean b) {
        if (a == b) {
            return 0;
        }
        if (!a) return -1;
        return 1;
    }

    public static boolean contains(boolean[] array, boolean target) {
        boolean[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            boolean value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(boolean[] array, boolean target) {
        return Booleans.indexOf((boolean[])array, (boolean)target, (int)0, (int)array.length);
    }

    private static int indexOf(boolean[] array, boolean target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(boolean[] array, boolean[] target) {
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

    public static int lastIndexOf(boolean[] array, boolean target) {
        return Booleans.lastIndexOf((boolean[])array, (boolean)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(boolean[] array, boolean target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static boolean[] concat(boolean[] ... arrays) {
        boolean[] array;
        int length = 0;
        boolean[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        boolean[] result = new boolean[length];
        int pos = 0;
        boolean[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            boolean[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    public static boolean[] ensureCapacity(boolean[] array, int minLength, int padding) {
        boolean[] arrbl;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrbl = Arrays.copyOf((boolean[])array, (int)(minLength + padding));
            return arrbl;
        }
        arrbl = array;
        return arrbl;
    }

    public static String join(String separator, boolean ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 7));
        builder.append((boolean)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((boolean)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<boolean[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static boolean[] toArray(Collection<Boolean> collection) {
        if (collection instanceof BooleanArrayAsList) {
            return ((BooleanArrayAsList)collection).toBooleanArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        boolean[] array = new boolean[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Boolean)Preconditions.checkNotNull(boxedArray[i])).booleanValue();
            ++i;
        }
        return array;
    }

    public static List<Boolean> asList(boolean ... backingArray) {
        if (backingArray.length != 0) return new BooleanArrayAsList((boolean[])backingArray);
        return Collections.emptyList();
    }

    @Beta
    public static int countTrue(boolean ... values) {
        int count = 0;
        boolean[] arr$ = values;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            boolean value = arr$[i$];
            if (value) {
                ++count;
            }
            ++i$;
        }
        return count;
    }

    static /* synthetic */ int access$000(boolean[] x0, boolean x1, int x2, int x3) {
        return Booleans.indexOf((boolean[])x0, (boolean)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(boolean[] x0, boolean x1, int x2, int x3) {
        return Booleans.lastIndexOf((boolean[])x0, (boolean)x1, (int)x2, (int)x3);
    }
}

