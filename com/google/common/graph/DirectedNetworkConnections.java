/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.graph.AbstractDirectedNetworkConnections;
import com.google.common.graph.EdgesConnecting;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class DirectedNetworkConnections<N, E>
extends AbstractDirectedNetworkConnections<N, E> {
    protected DirectedNetworkConnections(Map<E, N> inEdgeMap, Map<E, N> outEdgeMap, int selfLoopCount) {
        super(inEdgeMap, outEdgeMap, (int)selfLoopCount);
    }

    static <N, E> DirectedNetworkConnections<N, E> of() {
        return new DirectedNetworkConnections<V, K>(HashBiMap.create((int)2), HashBiMap.create((int)2), (int)0);
    }

    static <N, E> DirectedNetworkConnections<N, E> ofImmutable(Map<E, N> inEdges, Map<E, N> outEdges, int selfLoopCount) {
        return new DirectedNetworkConnections<N, E>(ImmutableBiMap.copyOf(inEdges), ImmutableBiMap.copyOf(outEdges), (int)selfLoopCount);
    }

    @Override
    public Set<N> predecessors() {
        return Collections.unmodifiableSet(((BiMap)this.inEdgeMap).values());
    }

    @Override
    public Set<N> successors() {
        return Collections.unmodifiableSet(((BiMap)this.outEdgeMap).values());
    }

    @Override
    public Set<E> edgesConnecting(Object node) {
        return new EdgesConnecting<K>(((BiMap)this.outEdgeMap).inverse(), (Object)node);
    }
}

