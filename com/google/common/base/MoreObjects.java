/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible
public final class MoreObjects {
    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        T t;
        if (first != null) {
            t = first;
            return (T)((T)t);
        }
        t = Preconditions.checkNotNull(second);
        return (T)t;
    }

    public static ToStringHelper toStringHelper(Object self) {
        return new ToStringHelper((String)self.getClass().getSimpleName(), null);
    }

    public static ToStringHelper toStringHelper(Class<?> clazz) {
        return new ToStringHelper((String)clazz.getSimpleName(), null);
    }

    public static ToStringHelper toStringHelper(String className) {
        return new ToStringHelper((String)className, null);
    }

    private MoreObjects() {
    }
}

