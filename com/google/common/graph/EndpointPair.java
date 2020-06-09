/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Network;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
public abstract class EndpointPair<N>
implements Iterable<N> {
    private final N nodeU;
    private final N nodeV;

    private EndpointPair(N nodeU, N nodeV) {
        this.nodeU = Preconditions.checkNotNull(nodeU);
        this.nodeV = Preconditions.checkNotNull(nodeV);
    }

    public static <N> EndpointPair<N> ordered(N source, N target) {
        return new Ordered<N>(source, target, null);
    }

    public static <N> EndpointPair<N> unordered(N nodeU, N nodeV) {
        return new Unordered<N>(nodeV, nodeU, null);
    }

    static <N> EndpointPair<N> of(Graph<?> graph, N nodeU, N nodeV) {
        EndpointPair<N> endpointPair;
        if (graph.isDirected()) {
            endpointPair = EndpointPair.ordered(nodeU, nodeV);
            return endpointPair;
        }
        endpointPair = EndpointPair.unordered(nodeU, nodeV);
        return endpointPair;
    }

    static <N> EndpointPair<N> of(Network<?, ?> network, N nodeU, N nodeV) {
        EndpointPair<N> endpointPair;
        if (network.isDirected()) {
            endpointPair = EndpointPair.ordered(nodeU, nodeV);
            return endpointPair;
        }
        endpointPair = EndpointPair.unordered(nodeU, nodeV);
        return endpointPair;
    }

    public abstract N source();

    public abstract N target();

    public final N nodeU() {
        return (N)this.nodeU;
    }

    public final N nodeV() {
        return (N)this.nodeV;
    }

    public final N adjacentNode(Object node) {
        if (node.equals(this.nodeU)) {
            return (N)this.nodeV;
        }
        if (!node.equals(this.nodeV)) throw new IllegalArgumentException((String)String.format((String)"EndpointPair %s does not contain node %s", (Object[])new Object[]{this, node}));
        return (N)this.nodeU;
    }

    public abstract boolean isOrdered();

    @Override
    public final UnmodifiableIterator<N> iterator() {
        return Iterators.forArray(this.nodeU, this.nodeV);
    }

    public abstract boolean equals(@Nullable Object var1);

    public abstract int hashCode();
}

