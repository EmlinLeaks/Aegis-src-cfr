/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ValueGraph;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@Beta
public interface MutableValueGraph<N, V>
extends ValueGraph<N, V> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CanIgnoreReturnValue
    public V putEdgeValue(N var1, N var2, V var3);

    @CanIgnoreReturnValue
    public boolean removeNode(Object var1);

    @CanIgnoreReturnValue
    public V removeEdge(Object var1, Object var2);
}

