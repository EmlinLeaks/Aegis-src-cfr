/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public final class SimpleTimeLimiter
implements TimeLimiter {
    private final ExecutorService executor;

    public SimpleTimeLimiter(ExecutorService executor) {
        this.executor = Preconditions.checkNotNull(executor);
    }

    public SimpleTimeLimiter() {
        this((ExecutorService)Executors.newCachedThreadPool());
    }

    @Override
    public <T> T newProxy(T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit) {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(interfaceType);
        Preconditions.checkNotNull(timeoutUnit);
        Preconditions.checkArgument((boolean)(timeoutDuration > 0L), (String)"bad timeout: %s", (long)timeoutDuration);
        Preconditions.checkArgument((boolean)interfaceType.isInterface(), (Object)"interfaceType must be an interface type");
        Set<Method> interruptibleMethods = SimpleTimeLimiter.findInterruptibleMethods(interfaceType);
        InvocationHandler handler = new InvocationHandler((SimpleTimeLimiter)this, target, (long)timeoutDuration, (TimeUnit)timeoutUnit, interruptibleMethods){
            final /* synthetic */ Object val$target;
            final /* synthetic */ long val$timeoutDuration;
            final /* synthetic */ TimeUnit val$timeoutUnit;
            final /* synthetic */ Set val$interruptibleMethods;
            final /* synthetic */ SimpleTimeLimiter this$0;
            {
                this.this$0 = simpleTimeLimiter;
                this.val$target = object;
                this.val$timeoutDuration = l;
                this.val$timeoutUnit = timeUnit;
                this.val$interruptibleMethods = set;
            }

            public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
                Callable<Object> callable = new Callable<Object>(this, (Method)method, (Object[])args){
                    final /* synthetic */ Method val$method;
                    final /* synthetic */ Object[] val$args;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$method = method;
                        this.val$args = arrobject;
                    }

                    public Object call() throws Exception {
                        try {
                            return this.val$method.invoke((Object)this.this$1.val$target, (Object[])this.val$args);
                        }
                        catch (java.lang.reflect.InvocationTargetException e) {
                            throw SimpleTimeLimiter.access$000((Exception)e, (boolean)false);
                        }
                    }
                };
                return this.this$0.callWithTimeout(callable, (long)this.val$timeoutDuration, (TimeUnit)this.val$timeoutUnit, (boolean)this.val$interruptibleMethods.contains((Object)method));
            }
        };
        return (T)SimpleTimeLimiter.newProxy(interfaceType, (InvocationHandler)handler);
    }

    @CanIgnoreReturnValue
    @Override
    public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible) throws Exception {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(timeoutUnit);
        Preconditions.checkArgument((boolean)(timeoutDuration > 0L), (String)"timeout must be positive: %s", (long)timeoutDuration);
        Future<T> future = this.executor.submit(callable);
        try {
            if (!amInterruptible) return (T)Uninterruptibles.getUninterruptibly(future, (long)timeoutDuration, (TimeUnit)timeoutUnit);
            try {
                return (T)future.get((long)timeoutDuration, (TimeUnit)timeoutUnit);
            }
            catch (InterruptedException e) {
                future.cancel((boolean)true);
                throw e;
            }
        }
        catch (ExecutionException e) {
            throw SimpleTimeLimiter.throwCause((Exception)e, (boolean)true);
        }
        catch (TimeoutException e) {
            future.cancel((boolean)true);
            throw new UncheckedTimeoutException((Throwable)e);
        }
    }

    private static Exception throwCause(Exception e, boolean combineStackTraces) throws Exception {
        Throwable cause = e.getCause();
        if (cause == null) {
            throw e;
        }
        if (combineStackTraces) {
            StackTraceElement[] combined = ObjectArrays.concat(cause.getStackTrace(), e.getStackTrace(), StackTraceElement.class);
            cause.setStackTrace((StackTraceElement[])combined);
        }
        if (cause instanceof Exception) {
            throw (Exception)cause;
        }
        if (!(cause instanceof Error)) throw e;
        throw (Error)cause;
    }

    private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
        HashSet<Method> set = Sets.newHashSet();
        Method[] arr$ = interfaceType.getMethods();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Method m = arr$[i$];
            if (SimpleTimeLimiter.declaresInterruptedEx((Method)m)) {
                set.add((Method)m);
            }
            ++i$;
        }
        return set;
    }

    private static boolean declaresInterruptedEx(Method method) {
        Class<?>[] arr$ = method.getExceptionTypes();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Class<?> exType = arr$[i$];
            if (exType == InterruptedException.class) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Object object = Proxy.newProxyInstance((ClassLoader)interfaceType.getClassLoader(), new Class[]{interfaceType}, (InvocationHandler)handler);
        return (T)interfaceType.cast((Object)object);
    }

    static /* synthetic */ Exception access$000(Exception x0, boolean x1) throws Exception {
        return SimpleTimeLimiter.throwCause((Exception)x0, (boolean)x1);
    }
}

