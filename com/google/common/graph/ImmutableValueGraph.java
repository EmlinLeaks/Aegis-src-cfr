/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.DirectedGraphConnections;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.UndirectedGraphConnections;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public final class ImmutableValueGraph<N, V>
extends ImmutableGraph.ValueBackedImpl<N, V>
implements ValueGraph<N, V> {
    private ImmutableValueGraph(ValueGraph<N, V> graph) {
        super(ValueGraphBuilder.from(graph), ImmutableValueGraph.getNodeConnections(graph), (long)((long)graph.edges().size()));
    }

    public static <N, V> ImmutableValueGraph<N, V> copyOf(ValueGraph<N, V> graph) {
        ImmutableValueGraph immutableValueGraph;
        if (graph instanceof ImmutableValueGraph) {
            immutableValueGraph = (ImmutableValueGraph)graph;
            return immutableValueGraph;
        }
        immutableValueGraph = new ImmutableValueGraph<N, V>(graph);
        return immutableValueGraph;
    }

    @Deprecated
    public static <N, V> ImmutableValueGraph<N, V> copyOf(ImmutableValueGraph<N, V> graph) {
        return Preconditions.checkNotNull(graph);
    }

    private static <N, V> ImmutableMap<N, GraphConnections<N, V>> getNodeConnections(ValueGraph<N, V> graph) {
        ImmutableMap.Builder<N, GraphConnections<N, V>> nodeConnections = ImmutableMap.builder();
        Iterator<N> i$ = graph.nodes().iterator();
        while (i$.hasNext()) {
            N node = i$.next();
            nodeConnections.put(node, ImmutableValueGraph.connectionsOf(graph, node));
        }
        return nodeConnections.build();
    }

    private static <N, V> GraphConnections<N, V> connectionsOf(ValueGraph<N, V> graph, N node) {
        GraphConnections<N, V> graphConnections;
        Function<N, V> successorNodeToValueFn = new Function<N, V>(graph, node){
            final /* synthetic */ ValueGraph val$graph;
            final /* synthetic */ Object val$node;
            {
                this.val$graph = valueGraph;
                this.val$node = object;
            }

            public V apply(N successorNode) {
                return (V)this.val$graph.edgeValue((Object)this.val$node, successorNode);
            }
        };
        if (graph.isDirected()) {
            graphConnections = DirectedGraphConnections.ofImmutable(graph.predecessors(node), Maps.asMap(graph.successors(node), successorNodeToValueFn));
            return graphConnections;
        }
        graphConnections = UndirectedGraphConnections.ofImmutable(Maps.asMap(graph.adjacentNodes(node), successorNodeToValueFn));
        return graphConnections;
    }

    @Override
    public V edgeValue(Object nodeU, Object nodeV) {
        return (V)this.backingValueGraph.edgeValue((Object)nodeU, (Object)nodeV);
    }

    @Override
    public V edgeValueOrDefault(Object nodeU, Object nodeV, @Nullable V defaultValue) {
        return (V)this.backingValueGraph.edgeValueOrDefault((Object)nodeU, (Object)nodeV, defaultValue);
    }

    @Override
    public String toString() {
        return this.backingValueGraph.toString();
    }
}

