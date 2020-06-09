/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.ConfigurableValueGraph;
import com.google.common.graph.DirectedGraphConnections;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.UndirectedGraphConnections;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

final class ConfigurableMutableValueGraph<N, V>
extends ConfigurableValueGraph<N, V>
implements MutableValueGraph<N, V> {
    ConfigurableMutableValueGraph(AbstractGraphBuilder<? super N> builder) {
        super(builder);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addNode(N node) {
        Preconditions.checkNotNull(node, (Object)"node");
        if (this.containsNode(node)) {
            return false;
        }
        this.addNodeInternal(node);
        return true;
    }

    @CanIgnoreReturnValue
    private GraphConnections<N, V> addNodeInternal(N node) {
        GraphConnections<N, V> connections = this.newConnections();
        Preconditions.checkState((boolean)(this.nodeConnections.put(node, connections) == null));
        return connections;
    }

    @CanIgnoreReturnValue
    @Override
    public V putEdgeValue(N nodeU, N nodeV, V value) {
        GraphConnections<N, V> connectionsU;
        Preconditions.checkNotNull(nodeU, (Object)"nodeU");
        Preconditions.checkNotNull(nodeV, (Object)"nodeV");
        Preconditions.checkNotNull(value, (Object)"value");
        if (!this.allowsSelfLoops()) {
            Preconditions.checkArgument((boolean)(!nodeU.equals(nodeV)), (String)"Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", nodeU);
        }
        if ((connectionsU = (GraphConnections<N, V>)this.nodeConnections.get(nodeU)) == null) {
            connectionsU = this.addNodeInternal(nodeU);
        }
        V previousValue = connectionsU.addSuccessor(nodeV, value);
        GraphConnections<N, V> connectionsV = (GraphConnections<N, V>)this.nodeConnections.get(nodeV);
        if (connectionsV == null) {
            connectionsV = this.addNodeInternal(nodeV);
        }
        connectionsV.addPredecessor(nodeU, value);
        if (previousValue != null) return (V)previousValue;
        Graphs.checkPositive((long)(++this.edgeCount));
        return (V)previousValue;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeNode(Object node) {
        Preconditions.checkNotNull(node, (Object)"node");
        GraphConnections connections = (GraphConnections)this.nodeConnections.get((Object)node);
        if (connections == null) {
            return false;
        }
        if (this.allowsSelfLoops() && connections.removeSuccessor((Object)node) != null) {
            connections.removePredecessor((Object)node);
            --this.edgeCount;
        }
        for (N successor : connections.successors()) {
            ((GraphConnections)this.nodeConnections.getWithoutCaching(successor)).removePredecessor((Object)node);
            --this.edgeCount;
        }
        if (this.isDirected()) {
            for (N predecessor : connections.predecessors()) {
                Preconditions.checkState((boolean)(((GraphConnections)this.nodeConnections.getWithoutCaching(predecessor)).removeSuccessor((Object)node) != null));
                --this.edgeCount;
            }
        }
        this.nodeConnections.remove((Object)node);
        Graphs.checkNonNegative((long)this.edgeCount);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public V removeEdge(Object nodeU, Object nodeV) {
        Preconditions.checkNotNull(nodeU, (Object)"nodeU");
        Preconditions.checkNotNull(nodeV, (Object)"nodeV");
        GraphConnections connectionsU = (GraphConnections)this.nodeConnections.get((Object)nodeU);
        GraphConnections connectionsV = (GraphConnections)this.nodeConnections.get((Object)nodeV);
        if (connectionsU == null) return (V)null;
        if (connectionsV == null) {
            return (V)null;
        }
        V previousValue = connectionsU.removeSuccessor((Object)nodeV);
        if (previousValue == null) return (V)previousValue;
        connectionsV.removePredecessor((Object)nodeU);
        Graphs.checkNonNegative((long)(--this.edgeCount));
        return (V)previousValue;
    }

    private GraphConnections<N, V> newConnections() {
        GraphConnections<N, V> graphConnections;
        if (this.isDirected()) {
            graphConnections = DirectedGraphConnections.of();
            return graphConnections;
        }
        graphConnections = UndirectedGraphConnections.of();
        return graphConnections;
    }
}

