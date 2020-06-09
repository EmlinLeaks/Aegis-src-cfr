/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

@GwtCompatible(serializable=true)
class EmptyImmutableListMultimap
extends ImmutableListMultimap<Object, Object> {
    static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
    private static final long serialVersionUID = 0L;

    private EmptyImmutableListMultimap() {
        super(ImmutableMap.<K, V>of(), (int)0);
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

