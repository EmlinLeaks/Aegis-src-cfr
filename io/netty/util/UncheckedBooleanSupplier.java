/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.BooleanSupplier;
import io.netty.util.UncheckedBooleanSupplier;

public interface UncheckedBooleanSupplier
extends BooleanSupplier {
    public static final UncheckedBooleanSupplier FALSE_SUPPLIER = new UncheckedBooleanSupplier(){

        public boolean get() {
            return false;
        }
    };
    public static final UncheckedBooleanSupplier TRUE_SUPPLIER = new UncheckedBooleanSupplier(){

        public boolean get() {
            return true;
        }
    };

    @Override
    public boolean get();
}

