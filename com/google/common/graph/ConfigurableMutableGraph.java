/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.ConfigurableMutableValueGraph;
import com.google.common.graph.ForwardingGraph;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphConstants;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;

final class ConfigurableMutableGraph<N>
extends ForwardingGraph<N>
implements MutableGraph<N> {
    private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;

    ConfigurableMutableGraph(AbstractGraphBuilder<? super N> builder) {
        this.backingValueGraph = new ConfigurableMutableValueGraph<N, GraphConstants.Presence>(builder);
    }

    @Override
    protected Graph<N> delegate() {
        return this.backingValueGraph;
    }

    @Override
    public boolean addNode(N node) {
        return this.backingValueGraph.addNode(node);
    }

    @Override
    public boolean putEdge(N nodeU, N nodeV) {
        if (this.backingValueGraph.putEdgeValue(nodeU, nodeV, (GraphConstants.Presence)GraphConstants.Presence.EDGE_EXISTS) != null) return false;
        return true;
    }

    @Override
    public boolean removeNode(Object node) {
        return this.backingValueGraph.removeNode((Object)node);
    }

    @Override
    public boolean removeEdge(Object nodeU, Object nodeV) {
        if (this.backingValueGraph.removeEdge((Object)nodeU, (Object)nodeV) == null) return false;
        return true;
    }
}

