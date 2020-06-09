/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@GwtCompatible
final class CollectPreconditions {
    CollectPreconditions() {
    }

    static void checkEntryNotNull(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException((String)("null key in entry: null=" + value));
        }
        if (value != null) return;
        throw new NullPointerException((String)("null value in entry: " + key + "=null"));
    }

    @CanIgnoreReturnValue
    static int checkNonnegative(int value, String name) {
        if (value >= 0) return value;
        throw new IllegalArgumentException((String)(name + " cannot be negative but was: " + value));
    }

    static void checkPositive(int value, String name) {
        if (value > 0) return;
        throw new IllegalArgumentException((String)(name + " must be positive but was: " + value));
    }

    static void checkRemove(boolean canRemove) {
        Preconditions.checkState((boolean)canRemove, (Object)"no calls to next() since the last call to remove()");
    }
}

