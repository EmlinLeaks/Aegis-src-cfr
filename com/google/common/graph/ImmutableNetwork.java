/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.graph.AbstractDirectedNetworkConnections;
import com.google.common.graph.AbstractUndirectedNetworkConnections;
import com.google.common.graph.ConfigurableNetwork;
import com.google.common.graph.DirectedMultiNetworkConnections;
import com.google.common.graph.DirectedNetworkConnections;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.NetworkConnections;
import com.google.common.graph.UndirectedMultiNetworkConnections;
import com.google.common.graph.UndirectedNetworkConnections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Beta
public final class ImmutableNetwork<N, E>
extends ConfigurableNetwork<N, E> {
    private ImmutableNetwork(Network<N, E> network) {
        super(NetworkBuilder.from(network), ImmutableNetwork.getNodeConnections(network), ImmutableNetwork.getEdgeToReferenceNode(network));
    }

    public static <N, E> ImmutableNetwork<N, E> copyOf(Network<N, E> network) {
        ImmutableNetwork immutableNetwork;
        if (network instanceof ImmutableNetwork) {
            immutableNetwork = (ImmutableNetwork)network;
            return immutableNetwork;
        }
        immutableNetwork = new ImmutableNetwork<N, E>(network);
        return immutableNetwork;
    }

    @Deprecated
    public static <N, E> ImmutableNetwork<N, E> copyOf(ImmutableNetwork<N, E> network) {
        return Preconditions.checkNotNull(network);
    }

    @Override
    public ImmutableGraph<N> asGraph() {
        Graph<N> asGraph = super.asGraph();
        return new ImmutableGraph<N>((ImmutableNetwork)this, asGraph){
            final /* synthetic */ Graph val$asGraph;
            final /* synthetic */ ImmutableNetwork this$0;
            {
                this.this$0 = immutableNetwork;
                this.val$asGraph = graph;
            }

            protected Graph<N> delegate() {
                return this.val$asGraph;
            }
        };
    }

    private static <N, E> Map<N, NetworkConnections<N, E>> getNodeConnections(Network<N, E> network) {
        ImmutableMap.Builder<N, NetworkConnections<N, E>> nodeConnections = ImmutableMap.builder();
        Iterator<N> i$ = network.nodes().iterator();
        while (i$.hasNext()) {
            N node = i$.next();
            nodeConnections.put(node, ImmutableNetwork.connectionsOf(network, node));
        }
        return nodeConnections.build();
    }

    private static <N, E> Map<E, N> getEdgeToReferenceNode(Network<N, E> network) {
        ImmutableMap.Builder<E, N> edgeToReferenceNode = ImmutableMap.builder();
        Iterator<E> i$ = network.edges().iterator();
        while (i$.hasNext()) {
            E edge = i$.next();
            edgeToReferenceNode.put(edge, network.incidentNodes(edge).nodeU());
        }
        return edgeToReferenceNode.build();
    }

    private static <N, E> NetworkConnections<N, E> connectionsOf(Network<N, E> network, N node) {
        AbstractUndirectedNetworkConnections abstractUndirectedNetworkConnections;
        if (network.isDirected()) {
            AbstractDirectedNetworkConnections abstractDirectedNetworkConnections;
            Map<E, N> inEdgeMap = Maps.asMap(network.inEdges(node), ImmutableNetwork.sourceNodeFn(network));
            Map<E, N> outEdgeMap = Maps.asMap(network.outEdges(node), ImmutableNetwork.targetNodeFn(network));
            int selfLoopCount = network.edgesConnecting(node, node).size();
            if (network.allowsParallelEdges()) {
                abstractDirectedNetworkConnections = DirectedMultiNetworkConnections.ofImmutable(inEdgeMap, outEdgeMap, (int)selfLoopCount);
                return abstractDirectedNetworkConnections;
            }
            abstractDirectedNetworkConnections = DirectedNetworkConnections.ofImmutable(inEdgeMap, outEdgeMap, (int)selfLoopCount);
            return abstractDirectedNetworkConnections;
        }
        Map<E, N> incidentEdgeMap = Maps.asMap(network.incidentEdges(node), ImmutableNetwork.adjacentNodeFn(network, node));
        if (network.allowsParallelEdges()) {
            abstractUndirectedNetworkConnections = UndirectedMultiNetworkConnections.ofImmutable(incidentEdgeMap);
            return abstractUndirectedNetworkConnections;
        }
        abstractUndirectedNetworkConnections = UndirectedNetworkConnections.ofImmutable(incidentEdgeMap);
        return abstractUndirectedNetworkConnections;
    }

    private static <N, E> Function<E, N> sourceNodeFn(Network<N, E> network) {
        return new Function<E, N>(network){
            final /* synthetic */ Network val$network;
            {
                this.val$network = network;
            }

            public N apply(E edge) {
                return (N)this.val$network.incidentNodes(edge).source();
            }
        };
    }

    private static <N, E> Function<E, N> targetNodeFn(Network<N, E> network) {
        return new Function<E, N>(network){
            final /* synthetic */ Network val$network;
            {
                this.val$network = network;
            }

            public N apply(E edge) {
                return (N)this.val$network.incidentNodes(edge).target();
            }
        };
    }

    private static <N, E> Function<E, N> adjacentNodeFn(Network<N, E> network, N node) {
        return new Function<E, N>(network, node){
            final /* synthetic */ Network val$network;
            final /* synthetic */ Object val$node;
            {
                this.val$network = network;
                this.val$node = object;
            }

            public N apply(E edge) {
                return (N)this.val$network.incidentNodes(edge).adjacentNode((Object)this.val$node);
            }
        };
    }
}

