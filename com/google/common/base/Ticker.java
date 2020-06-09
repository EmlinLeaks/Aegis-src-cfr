/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ticker;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
@GwtCompatible
public abstract class Ticker {
    private static final Ticker SYSTEM_TICKER = new Ticker(){

        public long read() {
            return com.google.common.base.Platform.systemNanoTime();
        }
    };

    protected Ticker() {
    }

    @CanIgnoreReturnValue
    public abstract long read();

    public static Ticker systemTicker() {
        return SYSTEM_TICKER;
    }
}

