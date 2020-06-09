/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.internal.UnsafeAllocator;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {
    public abstract <T> T newInstance(Class<T> var1) throws Exception;

    public static UnsafeAllocator create() {
        try {
            Class<?> unsafeClass = Class.forName((String)"sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField((String)"theUnsafe");
            f.setAccessible((boolean)true);
            Object unsafe = f.get(null);
            Method allocateInstance = unsafeClass.getMethod((String)"allocateInstance", Class.class);
            return new UnsafeAllocator((Method)allocateInstance, (Object)unsafe){
                final /* synthetic */ Method val$allocateInstance;
                final /* synthetic */ Object val$unsafe;
                {
                    this.val$allocateInstance = method;
                    this.val$unsafe = object;
                }

                public <T> T newInstance(Class<T> c) throws Exception {
                    UnsafeAllocator.access$000(c);
                    return (T)this.val$allocateInstance.invoke((Object)this.val$unsafe, (Object[])new Object[]{c});
                }
            };
        }
        catch (Exception unsafeClass) {
            try {
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod((String)"getConstructorId", Class.class);
                getConstructorId.setAccessible((boolean)true);
                int constructorId = ((Integer)getConstructorId.invoke(null, (Object[])new Object[]{Object.class})).intValue();
                Method newInstance = ObjectStreamClass.class.getDeclaredMethod((String)"newInstance", Class.class, Integer.TYPE);
                newInstance.setAccessible((boolean)true);
                return new UnsafeAllocator((Method)newInstance, (int)constructorId){
                    final /* synthetic */ Method val$newInstance;
                    final /* synthetic */ int val$constructorId;
                    {
                        this.val$newInstance = method;
                        this.val$constructorId = n;
                    }

                    public <T> T newInstance(Class<T> c) throws Exception {
                        UnsafeAllocator.access$000(c);
                        return (T)this.val$newInstance.invoke(null, (Object[])new Object[]{c, Integer.valueOf((int)this.val$constructorId)});
                    }
                };
            }
            catch (Exception getConstructorId) {
                try {
                    Method newInstance = ObjectInputStream.class.getDeclaredMethod((String)"newInstance", Class.class, Class.class);
                    newInstance.setAccessible((boolean)true);
                    return new UnsafeAllocator((Method)newInstance){
                        final /* synthetic */ Method val$newInstance;
                        {
                            this.val$newInstance = method;
                        }

                        public <T> T newInstance(Class<T> c) throws Exception {
                            UnsafeAllocator.access$000(c);
                            return (T)this.val$newInstance.invoke(null, (Object[])new Object[]{c, Object.class});
                        }
                    };
                }
                catch (Exception newInstance) {
                    return new UnsafeAllocator(){

                        public <T> T newInstance(Class<T> c) {
                            throw new UnsupportedOperationException((String)("Cannot allocate " + c));
                        }
                    };
                }
            }
        }
    }

    private static void assertInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface((int)modifiers)) {
            throw new UnsupportedOperationException((String)("Interface can't be instantiated! Interface name: " + c.getName()));
        }
        if (!Modifier.isAbstract((int)modifiers)) return;
        throw new UnsupportedOperationException((String)("Abstract class can't be instantiated! Class name: " + c.getName()));
    }

    static /* synthetic */ void access$000(Class x0) {
        UnsafeAllocator.assertInstantiable(x0);
    }
}

