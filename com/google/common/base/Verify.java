/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.VerifyException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class Verify {
    public static void verify(boolean expression) {
        if (expression) return;
        throw new VerifyException();
    }

    public static void verify(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        if (expression) return;
        throw new VerifyException((String)Preconditions.format((String)errorMessageTemplate, (Object[])errorMessageArgs));
    }

    @CanIgnoreReturnValue
    public static <T> T verifyNotNull(@Nullable T reference) {
        return (T)Verify.verifyNotNull(reference, (String)"expected a non-null reference", (Object[])new Object[0]);
    }

    @CanIgnoreReturnValue
    public static <T> T verifyNotNull(@Nullable T reference, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        Verify.verify((boolean)(reference != null), (String)errorMessageTemplate, (Object[])errorMessageArgs);
        return (T)reference;
    }

    private Verify() {
    }
}

