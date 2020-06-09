/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.AbstractValueGraph;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MapRetrievalCache;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ConfigurableValueGraph<N, V>
extends AbstractValueGraph<N, V> {
    private final boolean isDirected;
    private final boolean allowsSelfLoops;
    private final ElementOrder<N> nodeOrder;
    protected final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
    protected long edgeCount;

    ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder) {
        this(builder, builder.nodeOrder.createMap((int)builder.expectedNodeCount.or((Integer)Integer.valueOf((int)10)).intValue()), (long)0L);
    }

    ConfigurableValueGraph(AbstractGraphBuilder<? super N> builder, Map<N, GraphConnections<N, V>> nodeConnections, long edgeCount) {
        this.isDirected = builder.directed;
        this.allowsSelfLoops = builder.allowsSelfLoops;
        this.nodeOrder = builder.nodeOrder.cast();
        this.nodeConnections = nodeConnections instanceof TreeMap ? new MapRetrievalCache<N, GraphConnections<N, V>>(nodeConnections) : new MapIteratorCache<N, GraphConnections<N, V>>(nodeConnections);
        this.edgeCount = Graphs.checkNonNegative((long)edgeCount);
    }

    @Override
    public Set<N> nodes() {
        return this.nodeConnections.unmodifiableKeySet();
    }

    @Override
    public boolean isDirected() {
        return this.isDirected;
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
    public Set<N> adjacentNodes(Object node) {
        return this.checkedConnections((Object)node).adjacentNodes();
    }

    @Override
    public Set<N> predecessors(Object node) {
        return this.checkedConnections((Object)node).predecessors();
    }

    @Override
    public Set<N> successors(Object node) {
        return this.checkedConnections((Object)node).successors();
    }

    @Override
    public V edgeValueOrDefault(Object nodeU, Object nodeV, @Nullable V defaultValue) {
        GraphConnections<N, V> connectionsU = this.nodeConnections.get((Object)nodeU);
        if (connectionsU == null) {
            return (V)defaultValue;
        }
        V value = connectionsU.value((Object)nodeV);
        if (value != null) return (V)value;
        return (V)defaultValue;
    }

    @Override
    protected long edgeCount() {
        return this.edgeCount;
    }

    protected final GraphConnections<N, V> checkedConnections(Object node) {
        GraphConnections<N, V> connections = this.nodeConnections.get((Object)node);
        if (connections != null) return connections;
        Preconditions.checkNotNull(node);
        throw new IllegalArgumentException((String)String.format((String)"Node %s is not an element of this graph.", (Object[])new Object[]{node}));
    }

    protected final boolean containsNode(@Nullable Object node) {
        return this.nodeConnections.containsKey((Object)node);
    }
}

