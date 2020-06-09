/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.AbstractNetwork;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MapRetrievalCache;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.NetworkConnections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ConfigurableNetwork<N, E>
extends AbstractNetwork<N, E> {
    private final boolean isDirected;
    private final boolean allowsParallelEdges;
    private final boolean allowsSelfLoops;
    private final ElementOrder<N> nodeOrder;
    private final ElementOrder<E> edgeOrder;
    protected final MapIteratorCache<N, NetworkConnections<N, E>> nodeConnections;
    protected final MapIteratorCache<E, N> edgeToReferenceNode;

    ConfigurableNetwork(NetworkBuilder<? super N, ? super E> builder) {
        this(builder, builder.nodeOrder.createMap((int)builder.expectedNodeCount.or(Integer.valueOf((int)10)).intValue()), builder.edgeOrder.createMap((int)builder.expectedEdgeCount.or((Integer)Integer.valueOf((int)20)).intValue()));
    }

    ConfigurableNetwork(NetworkBuilder<? super N, ? super E> builder, Map<N, NetworkConnections<N, E>> nodeConnections, Map<E, N> edgeToReferenceNode) {
        this.isDirected = builder.directed;
        this.allowsParallelEdges = builder.allowsParallelEdges;
        this.allowsSelfLoops = builder.allowsSelfLoops;
        this.nodeOrder = builder.nodeOrder.cast();
        this.edgeOrder = builder.edgeOrder.cast();
        this.nodeConnections = nodeConnections instanceof TreeMap ? new MapRetrievalCache<N, NetworkConnections<N, E>>(nodeConnections) : new MapIteratorCache<N, NetworkConnections<N, E>>(nodeConnections);
        this.edgeToReferenceNode = new MapIteratorCache<E, N>(edgeToReferenceNode);
    }

    @Override
    public Set<N> nodes() {
        return this.nodeConnections.unmodifiableKeySet();
    }

    @Override
    public Set<E> edges() {
        return this.edgeToReferenceNode.unmodifiableKeySet();
    }

    @Override
    public boolean isDirected() {
        return this.isDirected;
    }

    @Override
    public boolean allowsParallelEdges() {
        return this.allowsParallelEdges;
    }

    @Override
    public boolean allowsSelfLoops() {
        return this.allowsSelfLoops;
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return this.nodeOrder;
    }

    @Override
    public ElementOrder<E> edgeOrder() {
        return this.edgeOrder;
    }

    @Override
    public Set<E> incidentEdges(Object node) {
        return this.checkedConnections((Object)node).incidentEdges();
    }

    @Override
    public EndpointPair<N> incidentNodes(Object edge) {
        N nodeU = this.checkedReferenceNode((Object)edge);
        N nodeV = this.nodeConnections.get(nodeU).oppositeNode((Object)edge);
        return EndpointPair.of(this, nodeU, nodeV);
    }

    @Override
    public Set<N> adjacentNodes(Object node) {
        return this.checkedConnections((Object)node).adjacentNodes();
    }

    @Override
    public Set<E> edgesConnecting(Object nodeU, Object nodeV) {
        NetworkConnections<N, E> connectionsU = this.checkedConnections((Object)nodeU);
        if (!this.allowsSelfLoops && nodeU == nodeV) {
            return ImmutableSet.of();
        }
        Preconditions.checkArgument((boolean)this.containsNode((Object)nodeV), (String)"Node %s is not an element of this graph.", (Object)nodeV);
        return connectionsU.edgesConnecting((Object)nodeV);
    }

    @Override
    public Set<E> inEdges(Object node) {
        return this.checkedConnections((Object)node).inEdges();
    }

    @Override
    public Set<E> outEdges(Object node) {
        return this.checkedConnections((Object)node).outEdges();
    }

    @Override
    public Set<N> predecessors(Object node) {
        return this.checkedConnections((Object)node).predecessors();
    }

    @Override
    public Set<N> successors(Object node) {
        return this.checkedConnections((Object)node).successors();
    }

    protected final NetworkConnections<N, E> checkedConnections(Object node) {
        NetworkConnections<N, E> connections = this.nodeConnections.get((Object)node);
        if (connections != null) return connections;
        Preconditions.checkNotNull(node);
        throw new IllegalArgumentException((String)String.format((String)"Node %s is not an element of this graph.", (Object[])new Object[]{node}));
    }

    protected final N checkedReferenceNode(Object edge) {
        N referenceNode = this.edgeToReferenceNode.get((Object)edge);
        if (referenceNode != null) return (N)referenceNode;
        Preconditions.checkNotNull(edge);
        throw new IllegalArgumentException((String)String.format((String)"Edge %s is not an element of this graph.", (Object[])new Object[]{edge}));
    }

    protected final boolean containsNode(@Nullable Object node) {
        return this.nodeConnections.containsKey((Object)node);
    }

    protected final boolean containsEdge(@Nullable Object edge) {
        return this.edgeToReferenceNode.containsKey((Object)edge);
    }
}

