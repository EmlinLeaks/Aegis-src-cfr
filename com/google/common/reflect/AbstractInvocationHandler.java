/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractInvocationHandler
implements InvocationHandler {
    private static final Object[] NO_ARGS = new Object[0];

    @Override
    public final Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
        if (args == null) {
            args = NO_ARGS;
        }
        if (args.length == 0 && method.getName().equals((Object)"hashCode")) {
            return Integer.valueOf((int)this.hashCode());
        }
        if (args.length == 1 && method.getName().equals((Object)"equals") && method.getParameterTypes()[0] == Object.class) {
            boolean bl;
            Object arg = args[0];
            if (arg == null) {
                return Boolean.valueOf((boolean)false);
            }
            if (proxy == arg) {
                return Boolean.valueOf((boolean)true);
            }
            if (AbstractInvocationHandler.isProxyOfSameInterfaces((Object)arg, proxy.getClass()) && this.equals((Object)Proxy.getInvocationHandler((Object)arg))) {
                bl = true;
                return Boolean.valueOf((boolean)bl);
            }
            bl = false;
            return Boolean.valueOf((boolean)bl);
        }
        if (args.length != 0) return this.handleInvocation((Object)proxy, (Method)method, (Object[])args);
        if (!method.getName().equals((Object)"toString")) return this.handleInvocation((Object)proxy, (Method)method, (Object[])args);
        return this.toString();
    }

    protected abstract Object handleInvocation(Object var1, Method var2, Object[] var3) throws Throwable;

    public boolean equals(Object obj) {
        return super.equals((Object)obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return super.toString();
    }

    private static boolean isProxyOfSameInterfaces(Object arg, Class<?> proxyClass) {
        if (proxyClass.isInstance((Object)arg)) return true;
        if (!Proxy.isProxyClass(arg.getClass())) return false;
        if (!Arrays.equals((Object[])arg.getClass().getInterfaces(), (Object[])proxyClass.getInterfaces())) return false;
        return true;
    }
}

