/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.EndpointPairIterator;
import com.google.common.graph.Graph;
import java.util.Iterator;
import java.util.Set;

abstract class EndpointPairIterator<N>
extends AbstractIterator<EndpointPair<N>> {
    private final Graph<N> graph;
    private final Iterator<N> nodeIterator;
    protected N node = null;
    protected Iterator<N> successorIterator = ImmutableSet.of().iterator();

    static <N> EndpointPairIterator<N> of(Graph<N> graph) {
        EndpointPairIterator endpointPairIterator;
        if (graph.isDirected()) {
            endpointPairIterator = new Directed<N>(graph, null);
            return endpointPairIterator;
        }
        endpointPairIterator = new Undirected<N>(graph, null);
        return endpointPairIterator;
    }

    private EndpointPairIterator(Graph<N> graph) {
        this.graph = graph;
        this.nodeIterator = graph.nodes().iterator();
    }

    protected final boolean advance() {
        Preconditions.checkState((boolean)(!this.successorIterator.hasNext()));
        if (!this.nodeIterator.hasNext()) {
            return false;
        }
        this.node = this.nodeIterator.next();
        this.successorIterator = this.graph.successors(this.node).iterator();
        return true;
    }
}

