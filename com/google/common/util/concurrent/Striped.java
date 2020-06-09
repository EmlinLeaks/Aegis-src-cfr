/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.math.IntMath;
import com.google.common.util.concurrent.Striped;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Beta
@GwtIncompatible
public abstract class Striped<L> {
    private static final int LARGE_LAZY_CUTOFF = 1024;
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>(){

        public ReadWriteLock get() {
            return new java.util.concurrent.locks.ReentrantReadWriteLock();
        }
    };
    private static final int ALL_SET = -1;

    private Striped() {
    }

    public abstract L get(Object var1);

    public abstract L getAt(int var1);

    abstract int indexFor(Object var1);

    public abstract int size();

    public Iterable<L> bulkGet(Iterable<?> keys) {
        Object[] array = Iterables.toArray(keys, Object.class);
        if (array.length == 0) {
            return ImmutableList.of();
        }
        int[] stripes = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            stripes[i] = this.indexFor((Object)array[i]);
        }
        Arrays.sort((int[])stripes);
        int previousStripe = stripes[0];
        array[0] = this.getAt((int)previousStripe);
        int i = 1;
        do {
            if (i >= array.length) {
                List<Object> asList = Arrays.asList(array);
                return Collections.unmodifiableList(asList);
            }
            int currentStripe = stripes[i];
            if (currentStripe == previousStripe) {
                array[i] = array[i - 1];
            } else {
                array[i] = this.getAt((int)currentStripe);
                previousStripe = currentStripe;
            }
            ++i;
        } while (true);
    }

    public static Striped<Lock> lock(int stripes) {
        return new CompactStriped<Lock>((int)stripes, (Supplier)new Supplier<Lock>(){

            public Lock get() {
                return new com.google.common.util.concurrent.Striped$PaddedLock();
            }
        }, null);
    }

    public static Striped<Lock> lazyWeakLock(int stripes) {
        return Striped.lazy((int)stripes, new Supplier<Lock>(){

            public Lock get() {
                return new java.util.concurrent.locks.ReentrantLock((boolean)false);
            }
        });
    }

    private static <L> Striped<L> lazy(int stripes, Supplier<L> supplier) {
        PowerOfTwoStriped powerOfTwoStriped;
        if (stripes < 1024) {
            powerOfTwoStriped = new SmallLazyStriped<L>((int)stripes, supplier);
            return powerOfTwoStriped;
        }
        powerOfTwoStriped = new LargeLazyStriped<L>((int)stripes, supplier);
        return powerOfTwoStriped;
    }

    public static Striped<Semaphore> semaphore(int stripes, int permits) {
        return new CompactStriped<Semaphore>((int)stripes, (Supplier)new Supplier<Semaphore>((int)permits){
            final /* synthetic */ int val$permits;
            {
                this.val$permits = n;
            }

            public Semaphore get() {
                return new com.google.common.util.concurrent.Striped$PaddedSemaphore((int)this.val$permits);
            }
        }, null);
    }

    public static Striped<Semaphore> lazyWeakSemaphore(int stripes, int permits) {
        return Striped.lazy((int)stripes, new Supplier<Semaphore>((int)permits){
            final /* synthetic */ int val$permits;
            {
                this.val$permits = n;
            }

            public Semaphore get() {
                return new Semaphore((int)this.val$permits, (boolean)false);
            }
        });
    }

    public static Striped<ReadWriteLock> readWriteLock(int stripes) {
        return new CompactStriped<ReadWriteLock>((int)stripes, READ_WRITE_LOCK_SUPPLIER, null);
    }

    public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int stripes) {
        return Striped.lazy((int)stripes, READ_WRITE_LOCK_SUPPLIER);
    }

    private static int ceilToPowerOfTwo(int x) {
        return 1 << IntMath.log2((int)x, (RoundingMode)RoundingMode.CEILING);
    }

    private static int smear(int hashCode) {
        hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
        return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
    }

    static /* synthetic */ int access$200(int x0) {
        return Striped.ceilToPowerOfTwo((int)x0);
    }

    static /* synthetic */ int access$300(int x0) {
        return Striped.smear((int)x0);
    }
}

