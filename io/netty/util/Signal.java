/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.Constant;
import io.netty.util.ConstantPool;
import io.netty.util.Signal;

public final class Signal
extends Error
implements Constant<Signal> {
    private static final long serialVersionUID = -221145131122459977L;
    private static final ConstantPool<Signal> pool = new ConstantPool<Signal>(){

        protected Signal newConstant(int id, String name) {
            return new Signal((int)id, (String)name);
        }
    };
    private final SignalConstant constant;

    public static Signal valueOf(String name) {
        return pool.valueOf((String)name);
    }

    public static Signal valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return pool.valueOf(firstNameComponent, (String)secondNameComponent);
    }

    private Signal(int id, String name) {
        this.constant = new SignalConstant((int)id, (String)name);
    }

    public void expect(Signal signal) {
        if (this == signal) return;
        throw new IllegalStateException((String)("unexpected signal: " + signal));
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public int id() {
        return this.constant.id();
    }

    @Override
    public String name() {
        return this.constant.name();
    }

    public boolean equals(Object obj) {
        if (this != obj) return false;
        return true;
    }

    public int hashCode() {
        return System.identityHashCode((Object)this);
    }

    @Override
    public int compareTo(Signal other) {
        if (this != other) return this.constant.compareTo(other.constant);
        return 0;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

