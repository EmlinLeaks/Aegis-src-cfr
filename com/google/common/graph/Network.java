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
import com.google.common.graph.Graph;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public interface Network<N, E> {
    public Set<N> nodes();

    public Set<E> edges();

    public Graph<N> asGraph();

    public boolean isDirected();

    public boolean allowsParallelEdges();

    public boolean allowsSelfLoops();

    public ElementOrder<N> nodeOrder();

    public ElementOrder<E> edgeOrder();

    public Set<N> adjacentNodes(Object var1);

    public Set<N> predecessors(Object var1);

    public Set<N> successors(Object var1);

    public Set<E> incidentEdges(Object var1);

    public Set<E> inEdges(Object var1);

    public Set<E> outEdges(Object var1);

    public int degree(Object var1);

    public int inDegree(Object var1);

    public int outDegree(Object var1);

    public EndpointPair<N> incidentNodes(Object var1);

    public Set<E> adjacentEdges(Object var1);

    public Set<E> edgesConnecting(Object var1, Object var2);

    public boolean equals(@Nullable Object var1);

    public int hashCode();
}

