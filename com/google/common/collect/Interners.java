/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;

@Beta
@GwtIncompatible
public final class Interners {
    private Interners() {
    }

    public static <E> Interner<E> newStrongInterner() {
        ConcurrentMap<K, V> map = new MapMaker().makeMap();
        return new Interner<E>(map){
            final /* synthetic */ ConcurrentMap val$map;
            {
                this.val$map = concurrentMap;
            }

            public E intern(E sample) {
                E e;
                E canonical = this.val$map.putIfAbsent(Preconditions.checkNotNull(sample), sample);
                if (canonical == null) {
                    e = sample;
                    return (E)((E)e);
                }
                e = canonical;
                return (E)e;
            }
        };
    }

    @GwtIncompatible(value="java.lang.ref.WeakReference")
    public static <E> Interner<E> newWeakInterner() {
        return new WeakInterner<E>(null);
    }

    public static <E> Function<E, E> asFunction(Interner<E> interner) {
        return new InternerFunction<E>(Preconditions.checkNotNull(interner));
    }
}

