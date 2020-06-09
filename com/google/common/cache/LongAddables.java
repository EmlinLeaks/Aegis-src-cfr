/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import com.google.common.cache.LongAddable;
import com.google.common.cache.LongAddables;
import com.google.common.cache.LongAdder;

@GwtCompatible(emulated=true)
final class LongAddables {
    private static final Supplier<LongAddable> SUPPLIER;

    LongAddables() {
    }

    public static LongAddable create() {
        return SUPPLIER.get();
    }

    static {
        Supplier<LongAddable> supplier;
        try {
            new LongAdder();
            supplier = new Supplier<LongAddable>(){

                public LongAddable get() {
                    return new LongAdder();
                }
            };
        }
        catch (Throwable t) {
            supplier = new Supplier<LongAddable>(){

                public LongAddable get() {
                    return new com.google.common.cache.LongAddables$PureJavaLongAddable(null);
                }
            };
        }
        SUPPLIER = supplier;
    }
}

