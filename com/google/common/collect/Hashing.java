/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
final class Hashing {
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static final int MAX_TABLE_SIZE = 1073741824;

    private Hashing() {
    }

    static int smear(int hashCode) {
        return 461845907 * Integer.rotateLeft((int)(hashCode * -862048943), (int)15);
    }

    static int smearedHash(@Nullable Object o) {
        int n;
        if (o == null) {
            n = 0;
            return Hashing.smear((int)n);
        }
        n = o.hashCode();
        return Hashing.smear((int)n);
    }

    static int closedTableSize(int expectedEntries, double loadFactor) {
        int tableSize;
        if ((expectedEntries = Math.max((int)expectedEntries, (int)2)) <= (int)(loadFactor * (double)(tableSize = Integer.highestOneBit((int)expectedEntries)))) return tableSize;
        if ((tableSize <<= 1) <= 0) return 1073741824;
        int n = tableSize;
        return n;
    }

    static boolean needsResizing(int size, int tableSize, double loadFactor) {
        if (!((double)size > loadFactor * (double)tableSize)) return false;
        if (tableSize >= 1073741824) return false;
        return true;
    }
}

