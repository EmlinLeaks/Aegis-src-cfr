/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import java.util.Comparator;

@GwtCompatible(serializable=true)
class EmptyImmutableSetMultimap
extends ImmutableSetMultimap<Object, Object> {
    static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
    private static final long serialVersionUID = 0L;

    private EmptyImmutableSetMultimap() {
        super(ImmutableMap.<K, V>of(), (int)0, null);
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

