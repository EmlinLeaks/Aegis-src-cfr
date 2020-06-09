/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.Constant;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConstantPool<T extends Constant<T>> {
    private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
    private final AtomicInteger nextId = new AtomicInteger((int)1);

    public T valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        if (firstNameComponent == null) {
            throw new NullPointerException((String)"firstNameComponent");
        }
        if (secondNameComponent != null) return (T)this.valueOf((String)(firstNameComponent.getName() + '#' + secondNameComponent));
        throw new NullPointerException((String)"secondNameComponent");
    }

    public T valueOf(String name) {
        ConstantPool.checkNotNullAndNotEmpty((String)name);
        return (T)this.getOrCreate((String)name);
    }

    private T getOrCreate(String name) {
        Constant constant = (Constant)this.constants.get((Object)name);
        if (constant != null) return (T)constant;
        T tempConstant = this.newConstant((int)this.nextId(), (String)name);
        constant = (Constant)this.constants.putIfAbsent((String)name, tempConstant);
        if (constant != null) return (T)constant;
        return (T)tempConstant;
    }

    public boolean exists(String name) {
        ConstantPool.checkNotNullAndNotEmpty((String)name);
        return this.constants.containsKey((Object)name);
    }

    public T newInstance(String name) {
        ConstantPool.checkNotNullAndNotEmpty((String)name);
        return (T)this.createOrThrow((String)name);
    }

    private T createOrThrow(String name) {
        T tempConstant;
        Constant constant = (Constant)this.constants.get((Object)name);
        if (constant != null || (constant = (Constant)this.constants.putIfAbsent((String)name, tempConstant = this.newConstant((int)this.nextId(), (String)name))) != null) throw new IllegalArgumentException((String)String.format((String)"'%s' is already in use", (Object[])new Object[]{name}));
        return (T)tempConstant;
    }

    private static String checkNotNullAndNotEmpty(String name) {
        ObjectUtil.checkNotNull(name, (String)"name");
        if (!name.isEmpty()) return name;
        throw new IllegalArgumentException((String)"empty name");
    }

    protected abstract T newConstant(int var1, String var2);

    @Deprecated
    public final int nextId() {
        return this.nextId.getAndIncrement();
    }
}

