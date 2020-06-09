/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.cache.Striped64;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

@GwtIncompatible
abstract class Striped64
extends Number {
    static final ThreadLocal<int[]> threadHashCode = new ThreadLocal<T>();
    static final Random rng = new Random();
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    volatile transient Cell[] cells;
    volatile transient long base;
    volatile transient int busy;
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;

    Striped64() {
    }

    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong((Object)this, (long)baseOffset, (long)cmp, (long)val);
    }

    final boolean casBusy() {
        return UNSAFE.compareAndSwapInt((Object)this, (long)busyOffset, (int)0, (int)1);
    }

    abstract long fn(long var1, long var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void retryUpdate(long x, int[] hc, boolean wasUncontended) {
        int h;
        if (hc == null) {
            hc = new int[1];
            threadHashCode.set((int[])hc);
            int r = rng.nextInt();
            hc[0] = r == 0 ? 1 : r;
            h = hc[0];
        } else {
            h = hc[0];
        }
        boolean collide = false;
        do {
            Cell[] as;
            int n;
            long v;
            if ((as = this.cells) != null && (n = as.length) > 0) {
                Cell a = as[n - 1 & h];
                if (a == null) {
                    if (this.busy == 0) {
                        Cell r = new Cell((long)x);
                        if (this.busy == 0 && this.casBusy()) {
                            boolean created = false;
                            try {
                                int j;
                                int m;
                                Cell[] rs = this.cells;
                                if (rs != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                this.busy = 0;
                            }
                            if (!created) continue;
                            return;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v = a.value;
                    if (a.cas((long)v, (long)this.fn((long)v, (long)x))) {
                        return;
                    }
                    if (n >= NCPU || this.cells != as) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.busy == 0 && this.casBusy()) {
                        try {
                            if (this.cells == as) {
                                Cell[] rs = new Cell[n << 1];
                                for (int i = 0; i < n; ++i) {
                                    rs[i] = as[i];
                                }
                                this.cells = rs;
                            }
                        }
                        finally {
                            this.busy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h ^= h << 13;
                h ^= h >>> 17;
                h ^= h << 5;
                hc[0] = h;
                continue;
            }
            if (this.busy == 0 && this.cells == as && this.casBusy()) {
                boolean init = false;
                try {
                    if (this.cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell((long)x);
                        this.cells = rs;
                        init = true;
                    }
                }
                finally {
                    this.busy = 0;
                }
                if (!init) continue;
                return;
            }
            v = this.base;
            if (this.casBase((long)v, (long)this.fn((long)v, (long)x))) return;
        } while (true);
    }

    final void internalReset(long initialValue) {
        Cell[] as = this.cells;
        this.base = initialValue;
        if (as == null) return;
        int n = as.length;
        int i = 0;
        while (i < n) {
            Cell a = as[i];
            if (a != null) {
                a.value = initialValue;
            }
            ++i;
        }
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException tryReflectionInstead) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                    public Unsafe run() throws Exception {
                        Class<Unsafe> k = Unsafe.class;
                        Field[] arr$ = k.getDeclaredFields();
                        int len$ = arr$.length;
                        int i$ = 0;
                        while (i$ < len$) {
                            Field f = arr$[i$];
                            f.setAccessible((boolean)true);
                            Object x = f.get(null);
                            if (k.isInstance((Object)x)) {
                                return (Unsafe)k.cast((Object)x);
                            }
                            ++i$;
                        }
                        throw new java.lang.NoSuchFieldError((String)"the Unsafe");
                    }
                });
            }
            catch (PrivilegedActionException e) {
                throw new RuntimeException((String)"Could not initialize intrinsics", (Throwable)e.getCause());
            }
        }
    }

    static /* synthetic */ Unsafe access$000() {
        return Striped64.getUnsafe();
    }

    static {
        try {
            UNSAFE = Striped64.getUnsafe();
            Class<Striped64> sk = Striped64.class;
            baseOffset = UNSAFE.objectFieldOffset((Field)sk.getDeclaredField((String)"base"));
            busyOffset = UNSAFE.objectFieldOffset((Field)sk.getDeclaredField((String)"busy"));
            return;
        }
        catch (Exception e) {
            throw new Error((Throwable)e);
        }
    }
}

