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
import java.util.concurrent.atomic.AtomicLongArray;

@GwtIncompatible
public class AtomicDoubleArray
implements Serializable {
    private static final long serialVersionUID = 0L;
    private transient AtomicLongArray longs;

    public AtomicDoubleArray(int length) {
        this.longs = new AtomicLongArray((int)length);
    }

    public AtomicDoubleArray(double[] array) {
        int len = array.length;
        long[] longArray = new long[len];
        int i = 0;
        do {
            if (i >= len) {
                this.longs = new AtomicLongArray((long[])longArray);
                return;
            }
            longArray[i] = Double.doubleToRawLongBits((double)array[i]);
            ++i;
        } while (true);
    }

    public final int length() {
        return this.longs.length();
    }

    public final double get(int i) {
        return Double.longBitsToDouble((long)this.longs.get((int)i));
    }

    public final void set(int i, double newValue) {
        long next = Double.doubleToRawLongBits((double)newValue);
        this.longs.set((int)i, (long)next);
    }

    public final void lazySet(int i, double newValue) {
        this.set((int)i, (double)newValue);
    }

    public final double getAndSet(int i, double newValue) {
        long next = Double.doubleToRawLongBits((double)newValue);
        return Double.longBitsToDouble((long)this.longs.getAndSet((int)i, (long)next));
    }

    public final boolean compareAndSet(int i, double expect, double update) {
        return this.longs.compareAndSet((int)i, (long)Double.doubleToRawLongBits((double)expect), (long)Double.doubleToRawLongBits((double)update));
    }

    public final boolean weakCompareAndSet(int i, double expect, double update) {
        return this.longs.weakCompareAndSet((int)i, (long)Double.doubleToRawLongBits((double)expect), (long)Double.doubleToRawLongBits((double)update));
    }

    @CanIgnoreReturnValue
    public final double getAndAdd(int i, double delta) {
        long current;
        long next;
        double currentVal;
        double nextVal;
        while (!this.longs.compareAndSet((int)i, (long)(current = this.longs.get((int)i)), (long)(next = Double.doubleToRawLongBits((double)(nextVal = (currentVal = Double.longBitsToDouble((long)current)) + delta))))) {
        }
        return currentVal;
    }

    @CanIgnoreReturnValue
    public double addAndGet(int i, double delta) {
        long current;
        long next;
        double currentVal;
        double nextVal;
        while (!this.longs.compareAndSet((int)i, (long)(current = this.longs.get((int)i)), (long)(next = Double.doubleToRawLongBits((double)(nextVal = (currentVal = Double.longBitsToDouble((long)current)) + delta))))) {
        }
        return nextVal;
    }

    public String toString() {
        int iMax = this.length() - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder((int)(19 * (iMax + 1)));
        b.append((char)'[');
        int i = 0;
        do {
            b.append((double)Double.longBitsToDouble((long)this.longs.get((int)i)));
            if (i == iMax) {
                return b.append((char)']').toString();
            }
            b.append((char)',').append((char)' ');
            ++i;
        } while (true);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int length = this.length();
        s.writeInt((int)length);
        int i = 0;
        while (i < length) {
            s.writeDouble((double)this.get((int)i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int length = s.readInt();
        this.longs = new AtomicLongArray((int)length);
        int i = 0;
        while (i < length) {
            this.set((int)i, (double)s.readDouble());
            ++i;
        }
    }
}

