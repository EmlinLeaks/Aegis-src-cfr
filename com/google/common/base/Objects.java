/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ExtraObjectsMethodsForWeb;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.Arrays;
import javax.annotation.Nullable;

@GwtCompatible
public final class Objects
extends ExtraObjectsMethodsForWeb {
    private Objects() {
    }

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        if (a == b) return true;
        if (a == null) return false;
        if (!a.equals((Object)b)) return false;
        return true;
    }

    public static int hashCode(@Nullable Object ... objects) {
        return Arrays.hashCode((Object[])objects);
    }

    @Deprecated
    public static ToStringHelper toStringHelper(Object self) {
        return new ToStringHelper((String)self.getClass().getSimpleName(), null);
    }

    @Deprecated
    public static ToStringHelper toStringHelper(Class<?> clazz) {
        return new ToStringHelper((String)clazz.getSimpleName(), null);
    }

    @Deprecated
    public static ToStringHelper toStringHelper(String className) {
        return new ToStringHelper((String)className, null);
    }

    @Deprecated
    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return (T)MoreObjects.firstNonNull(first, second);
    }
}

