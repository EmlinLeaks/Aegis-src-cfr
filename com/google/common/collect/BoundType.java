/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;

@GwtCompatible
public enum BoundType {
    OPEN{

        BoundType flip() {
            return CLOSED;
        }
    }
    ,
    CLOSED{

        BoundType flip() {
            return OPEN;
        }
    };
    

    static BoundType forBoolean(boolean inclusive) {
        BoundType boundType;
        if (inclusive) {
            boundType = CLOSED;
            return boundType;
        }
        boundType = OPEN;
        return boundType;
    }

    abstract BoundType flip();
}

