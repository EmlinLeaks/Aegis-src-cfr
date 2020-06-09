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
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Throwables {
    @GwtIncompatible
    private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
    @GwtIncompatible
    @VisibleForTesting
    static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
    @Nullable
    @GwtIncompatible
    private static final Object jla = Throwables.getJLA();
    @Nullable
    @GwtIncompatible
    private static final Method getStackTraceElementMethod = jla == null ? null : Throwables.getGetMethod();
    @Nullable
    @GwtIncompatible
    private static final Method getStackTraceDepthMethod = jla == null ? null : Throwables.getSizeMethod();

    private Throwables() {
    }

    @GwtIncompatible
    public static <X extends Throwable> void throwIfInstanceOf(Throwable throwable, Class<X> declaredType) throws Throwable {
        Preconditions.checkNotNull(throwable);
        if (!declaredType.isInstance((Object)throwable)) return;
        throw (Throwable)declaredType.cast((Object)throwable);
    }

    @Deprecated
    @GwtIncompatible
    public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable, Class<X> declaredType) throws Throwable {
        if (throwable == null) return;
        Throwables.throwIfInstanceOf((Throwable)throwable, declaredType);
    }

    public static void throwIfUnchecked(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (!(throwable instanceof Error)) return;
        throw (Error)throwable;
    }

    @Deprecated
    @GwtIncompatible
    public static void propagateIfPossible(@Nullable Throwable throwable) {
        if (throwable == null) return;
        Throwables.throwIfUnchecked((Throwable)throwable);
    }

    @GwtIncompatible
    public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X> declaredType) throws Throwable {
        Throwables.propagateIfInstanceOf((Throwable)throwable, declaredType);
        Throwables.propagateIfPossible((Throwable)throwable);
    }

    @GwtIncompatible
    public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2) throws Throwable {
        Preconditions.checkNotNull(declaredType2);
        Throwables.propagateIfInstanceOf((Throwable)throwable, declaredType1);
        Throwables.propagateIfPossible((Throwable)throwable, declaredType2);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static RuntimeException propagate(Throwable throwable) {
        Throwables.throwIfUnchecked((Throwable)throwable);
        throw new RuntimeException((Throwable)throwable);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
        }
        return throwable;
    }

    @Beta
    public static List<Throwable> getCausalChain(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        ArrayList<Throwable> causes = new ArrayList<Throwable>((int)4);
        while (throwable != null) {
            causes.add(throwable);
            throwable = throwable.getCause();
        }
        return Collections.unmodifiableList(causes);
    }

    @GwtIncompatible
    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace((PrintWriter)new PrintWriter((Writer)stringWriter));
        return stringWriter.toString();
    }

    @Beta
    @GwtIncompatible
    public static List<StackTraceElement> lazyStackTrace(Throwable throwable) {
        List<StackTraceElement> list;
        if (Throwables.lazyStackTraceIsLazy()) {
            list = Throwables.jlaStackTrace((Throwable)throwable);
            return list;
        }
        list = Collections.unmodifiableList(Arrays.asList(throwable.getStackTrace()));
        return list;
    }

    @Beta
    @GwtIncompatible
    public static boolean lazyStackTraceIsLazy() {
        boolean bl;
        boolean bl2 = getStackTraceElementMethod != null;
        if (getStackTraceDepthMethod != null) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    @GwtIncompatible
    private static List<StackTraceElement> jlaStackTrace(Throwable t) {
        Preconditions.checkNotNull(t);
        return new AbstractList<StackTraceElement>((Throwable)t){
            final /* synthetic */ Throwable val$t;
            {
                this.val$t = throwable;
            }

            public StackTraceElement get(int n) {
                return (StackTraceElement)Throwables.access$200((Method)Throwables.access$000(), (Object)Throwables.access$100(), (Object[])new Object[]{this.val$t, Integer.valueOf((int)n)});
            }

            public int size() {
                return ((Integer)Throwables.access$200((Method)Throwables.access$300(), (Object)Throwables.access$100(), (Object[])new Object[]{this.val$t})).intValue();
            }
        };
    }

    @GwtIncompatible
    private static Object invokeAccessibleNonThrowingMethod(Method method, Object receiver, Object ... params) {
        try {
            return method.invoke((Object)receiver, (Object[])params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException((Throwable)e);
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }

    @Nullable
    @GwtIncompatible
    private static Object getJLA() {
        try {
            Class<?> sharedSecrets = Class.forName((String)SHARED_SECRETS_CLASSNAME, (boolean)false, null);
            Method langAccess = sharedSecrets.getMethod((String)"getJavaLangAccess", new Class[0]);
            return langAccess.invoke(null, (Object[])new Object[0]);
        }
        catch (ThreadDeath death) {
            throw death;
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    @GwtIncompatible
    private static Method getGetMethod() {
        return Throwables.getJlaMethod((String)"getStackTraceElement", Throwable.class, Integer.TYPE);
    }

    @Nullable
    @GwtIncompatible
    private static Method getSizeMethod() {
        return Throwables.getJlaMethod((String)"getStackTraceDepth", Throwable.class);
    }

    @Nullable
    @GwtIncompatible
    private static Method getJlaMethod(String name, Class<?> ... parameterTypes) throws ThreadDeath {
        try {
            return Class.forName((String)JAVA_LANG_ACCESS_CLASSNAME, (boolean)false, null).getMethod((String)name, parameterTypes);
        }
        catch (ThreadDeath death) {
            throw death;
        }
        catch (Throwable t) {
            return null;
        }
    }

    static /* synthetic */ Method access$000() {
        return getStackTraceElementMethod;
    }

    static /* synthetic */ Object access$100() {
        return jla;
    }

    static /* synthetic */ Object access$200(Method x0, Object x1, Object[] x2) {
        return Throwables.invokeAccessibleNonThrowingMethod((Method)x0, (Object)x1, (Object[])x2);
    }

    static /* synthetic */ Method access$300() {
        return getStackTraceDepthMethod;
    }
}

