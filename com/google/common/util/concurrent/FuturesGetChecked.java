/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.FuturesGetChecked;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;

@GwtIncompatible
final class FuturesGetChecked {
    private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function<Constructor<?>, Boolean>(){

        public Boolean apply(Constructor<?> input) {
            return Boolean.valueOf((boolean)Arrays.asList(input.getParameterTypes()).contains(String.class));
        }
    }).reverse();

    @CanIgnoreReturnValue
    static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws Exception {
        return (V)FuturesGetChecked.getChecked((GetCheckedTypeValidator)FuturesGetChecked.bestGetCheckedTypeValidator(), future, exceptionClass);
    }

    @CanIgnoreReturnValue
    @VisibleForTesting
    static <V, X extends Exception> V getChecked(GetCheckedTypeValidator validator, Future<V> future, Class<X> exceptionClass) throws Exception {
        validator.validateClass(exceptionClass);
        try {
            return (V)future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw FuturesGetChecked.newWithCause(exceptionClass, (Throwable)e);
        }
        catch (ExecutionException e) {
            FuturesGetChecked.wrapAndThrowExceptionOrError((Throwable)e.getCause(), exceptionClass);
            throw new AssertionError();
        }
    }

    @CanIgnoreReturnValue
    static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws Exception {
        FuturesGetChecked.bestGetCheckedTypeValidator().validateClass(exceptionClass);
        try {
            return (V)future.get((long)timeout, (TimeUnit)unit);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw FuturesGetChecked.newWithCause(exceptionClass, (Throwable)e);
        }
        catch (TimeoutException e) {
            throw FuturesGetChecked.newWithCause(exceptionClass, (Throwable)e);
        }
        catch (ExecutionException e) {
            FuturesGetChecked.wrapAndThrowExceptionOrError((Throwable)e.getCause(), exceptionClass);
            throw new AssertionError();
        }
    }

    private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
        return GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
    }

    @VisibleForTesting
    static GetCheckedTypeValidator weakSetValidator() {
        return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
    }

    @VisibleForTesting
    static GetCheckedTypeValidator classValueValidator() {
        return GetCheckedTypeValidatorHolder.ClassValueValidator.INSTANCE;
    }

    private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable cause, Class<X> exceptionClass) throws Exception {
        if (cause instanceof Error) {
            throw new ExecutionError((Error)((Error)cause));
        }
        if (!(cause instanceof RuntimeException)) throw FuturesGetChecked.newWithCause(exceptionClass, (Throwable)cause);
        throw new UncheckedExecutionException((Throwable)cause);
    }

    private static boolean hasConstructorUsableByGetChecked(Class<? extends Exception> exceptionClass) {
        try {
            Exception unused = FuturesGetChecked.newWithCause(exceptionClass, (Throwable)new Exception());
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause) {
        Exception instance;
        Constructor<X> constructor;
        List<Constructor<X>> constructors = Arrays.asList(exceptionClass.getConstructors());
        Iterator<Constructor<X>> i$ = FuturesGetChecked.preferringStrings(constructors).iterator();
        do {
            if (!i$.hasNext()) throw new IllegalArgumentException((String)("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception"), (Throwable)cause);
        } while ((instance = (Exception)FuturesGetChecked.newFromConstructor(constructor = i$.next(), (Throwable)cause)) == null);
        if (instance.getCause() != null) return (X)instance;
        instance.initCause((Throwable)cause);
        return (X)instance;
    }

    private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> constructors) {
        return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
    }

    @Nullable
    private static <X> X newFromConstructor(Constructor<X> constructor, Throwable cause) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            Class<?> paramType = paramTypes[i];
            if (paramType.equals(String.class)) {
                params[i] = cause.toString();
                continue;
            }
            if (!paramType.equals(Throwable.class)) return (X)null;
            params[i] = cause;
        }
        try {
            return (X)constructor.newInstance((Object[])params);
        }
        catch (IllegalArgumentException e) {
            return (X)null;
        }
        catch (InstantiationException e) {
            return (X)null;
        }
        catch (IllegalAccessException e) {
            return (X)null;
        }
        catch (InvocationTargetException e) {
            return (X)null;
        }
    }

    @VisibleForTesting
    static boolean isCheckedException(Class<? extends Exception> type) {
        if (RuntimeException.class.isAssignableFrom(type)) return false;
        return true;
    }

    @VisibleForTesting
    static void checkExceptionClassValidity(Class<? extends Exception> exceptionClass) {
        Preconditions.checkArgument((boolean)FuturesGetChecked.isCheckedException(exceptionClass), (String)"Futures.getChecked exception type (%s) must not be a RuntimeException", exceptionClass);
        Preconditions.checkArgument((boolean)FuturesGetChecked.hasConstructorUsableByGetChecked(exceptionClass), (String)"Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable", exceptionClass);
    }

    private FuturesGetChecked() {
    }
}

