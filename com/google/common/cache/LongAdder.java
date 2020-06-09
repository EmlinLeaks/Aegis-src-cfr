/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.cache.LongAddable;
import com.google.common.cache.Striped64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@GwtCompatible(emulated=true)
final class LongAdder
extends Striped64
implements Serializable,
LongAddable {
    private static final long serialVersionUID = 7249069246863182397L;

    @Override
    final long fn(long v, long x) {
        return v + x;
    }

    @Override
    public void add(long x) {
        int n;
        Striped64.Cell a;
        Striped64.Cell[] as = this.cells;
        if (as == null) {
            long b = this.base;
            if (this.casBase((long)b, (long)(b + x))) return;
        }
        boolean uncontended = true;
        int[] hc = (int[])threadHashCode.get();
        if (hc != null && as != null && (n = as.length) >= 1 && (a = as[n - 1 & hc[0]]) != null) {
            long v = a.value;
            uncontended = a.cas((long)v, (long)(v + x));
            if (uncontended) return;
        }
        this.retryUpdate((long)x, (int[])hc, (boolean)uncontended);
    }

    @Override
    public void increment() {
        this.add((long)1L);
    }

    public void decrement() {
        this.add((long)-1L);
    }

    @Override
    public long sum() {
        long sum = this.base;
        Striped64.Cell[] as = this.cells;
        if (as == null) return sum;
        int n = as.length;
        int i = 0;
        while (i < n) {
            Striped64.Cell a = as[i];
            if (a != null) {
                sum += a.value;
            }
            ++i;
        }
        return sum;
    }

    public void reset() {
        this.internalReset((long)0L);
    }

    public long sumThenReset() {
        long sum = this.base;
        Striped64.Cell[] as = this.cells;
        this.base = 0L;
        if (as == null) return sum;
        int n = as.length;
        int i = 0;
        while (i < n) {
            Striped64.Cell a = as[i];
            if (a != null) {
                sum += a.value;
                a.value = 0L;
            }
            ++i;
        }
        return sum;
    }

    public String toString() {
        return Long.toString((long)this.sum());
    }

    @Override
    public long longValue() {
        return this.sum();
    }

    @Override
    public int intValue() {
        return (int)this.sum();
    }

    @Override
    public float floatValue() {
        return (float)this.sum();
    }

    @Override
    public double doubleValue() {
        return (double)this.sum();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeLong((long)this.sum());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.busy = 0;
        this.cells = null;
        this.base = s.readLong();
    }
}

