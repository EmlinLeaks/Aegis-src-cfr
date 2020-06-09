/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.DirectedGraphConnections;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ForwardingGraph;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.GraphConstants;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.UndirectedGraphConnections;
import java.util.Iterator;
import java.util.Set;

@Beta
public abstract class ImmutableGraph<N>
extends ForwardingGraph<N> {
    ImmutableGraph() {
    }

    public static <N> ImmutableGraph<N> copyOf(Graph<N> graph) {
        ImmutableGraph immutableGraph;
        if (graph instanceof ImmutableGraph) {
            immutableGraph = (ImmutableGraph)graph;
            return immutableGraph;
        }
        immutableGraph = new ValueBackedImpl<N, GraphConstants.Presence>(GraphBuilder.from(graph), ImmutableGraph.getNodeConnections(graph), (long)((long)graph.edges().size()));
        return immutableGraph;
    }

    @Deprecated
    public static <N> ImmutableGraph<N> copyOf(ImmutableGraph<N> graph) {
        return Preconditions.checkNotNull(graph);
    }

    private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(Graph<N> graph) {
        ImmutableMap.Builder<N, GraphConnections<N, GraphConstants.Presence>> nodeConnections = ImmutableMap.builder();
        Iterator<N> i$ = graph.nodes().iterator();
        while (i$.hasNext()) {
            N node = i$.next();
            nodeConnections.put(node, ImmutableGraph.connectionsOf(graph, node));
        }
        return nodeConnections.build();
    }

    private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(Graph<N> graph, N node) {
        GraphConnections<Object, GraphConstants.Presence> graphConnections;
        Function<Object, GraphConstants.Presence> edgeValueFn = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
        if (graph.isDirected()) {
            graphConnections = DirectedGraphConnections.ofImmutable(graph.predecessors(node), Maps.asMap(graph.successors(node), edgeValueFn));
            return graphConnections;
        }
        graphConnections = UndirectedGraphConnections.ofImmutable(Maps.asMap(graph.adjacentNodes(node), edgeValueFn));
        return graphConnections;
    }
}

