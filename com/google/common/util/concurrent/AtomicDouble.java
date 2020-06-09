/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

@GwtIncompatible
public class AtomicDouble
extends Number
implements Serializable {
    private static final long serialVersionUID = 0L;
    private volatile transient long value;
    private static final AtomicLongFieldUpdater<AtomicDouble> updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, (String)"value");

    public AtomicDouble(double initialValue) {
        this.value = Double.doubleToRawLongBits((double)initialValue);
    }

    public AtomicDouble() {
    }

    public final double get() {
        return Double.longBitsToDouble((long)this.value);
    }

    public final void set(double newValue) {
        long next;
        this.value = next = Double.doubleToRawLongBits((double)newValue);
    }

    public final void lazySet(double newValue) {
        this.set((double)newValue);
    }

    public final double getAndSet(double newValue) {
        long next = Double.doubleToRawLongBits((double)newValue);
        return Double.longBitsToDouble((long)updater.getAndSet((AtomicDouble)this, (long)next));
    }

    public final boolean compareAndSet(double expect, double update) {
        return updater.compareAndSet((AtomicDouble)this, (long)Double.doubleToRawLongBits((double)expect), (long)Double.doubleToRawLongBits((double)update));
    }

    public final boolean weakCompareAndSet(double expect, double update) {
        return updater.weakCompareAndSet((AtomicDouble)this, (long)Double.doubleToRawLongBits((double)expect), (long)Double.doubleToRawLongBits((double)update));
    }

    @CanIgnoreReturnValue
    public final double getAndAdd(double delta) {
        double nextVal;
        long next;
        double currentVal;
        long current;
        while (!updater.compareAndSet((AtomicDouble)this, (long)(current = this.value), (long)(next = Double.doubleToRawLongBits((double)(nextVal = (currentVal = Double.longBitsToDouble((long)current)) + delta))))) {
        }
        return currentVal;
    }

    @CanIgnoreReturnValue
    public final double addAndGet(double delta) {
        double nextVal;
        long next;
        double currentVal;
        long current;
        while (!updater.compareAndSet((AtomicDouble)this, (long)(current = this.value), (long)(next = Double.doubleToRawLongBits((double)(nextVal = (currentVal = Double.longBitsToDouble((long)current)) + delta))))) {
        }
        return nextVal;
    }

    public String toString() {
        return Double.toString((double)this.get());
    }

    @Override
    public int intValue() {
        return (int)this.get();
    }

    @Override
    public long longValue() {
        return (long)this.get();
    }

    @Override
    public float floatValue() {
        return (float)this.get();
    }

    @Override
    public double doubleValue() {
        return this.get();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeDouble((double)this.get());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.set((double)s.readDouble());
    }
}

