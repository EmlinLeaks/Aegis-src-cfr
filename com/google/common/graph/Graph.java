/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public interface Graph<N> {
    public Set<N> nodes();

    public Set<EndpointPair<N>> edges();

    public boolean isDirected();

    public boolean allowsSelfLoops();

    public ElementOrder<N> nodeOrder();

    public Set<N> adjacentNodes(Object var1);

    public Set<N> predecessors(Object var1);

    public Set<N> successors(Object var1);

    public int degree(Object var1);

    public int inDegree(Object var1);

    public int outDegree(Object var1);

    public boolean equals(@Nullable Object var1);

    public int hashCode();
}

