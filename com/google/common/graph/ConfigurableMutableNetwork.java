/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.graph.ConfigurableNetwork;
import com.google.common.graph.DirectedMultiNetworkConnections;
import com.google.common.graph.DirectedNetworkConnections;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.NetworkConnections;
import com.google.common.graph.UndirectedMultiNetworkConnections;
import com.google.common.graph.UndirectedNetworkConnections;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.Set;

final class ConfigurableMutableNetwork<N, E>
extends ConfigurableNetwork<N, E>
implements MutableNetwork<N, E> {
    ConfigurableMutableNetwork(NetworkBuilder<? super N, ? super E> builder) {
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
    private NetworkConnections<N, E> addNodeInternal(N node) {
        NetworkConnections<N, E> connections = this.newConnections();
        Preconditions.checkState((boolean)(this.nodeConnections.put(node, connections) == null));
        return connections;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addEdge(N nodeU, N nodeV, E edge) {
        Preconditions.checkNotNull(nodeU, (Object)"nodeU");
        Preconditions.checkNotNull(nodeV, (Object)"nodeV");
        Preconditions.checkNotNull(edge, (Object)"edge");
        if (this.containsEdge(edge)) {
            EndpointPair<N> existingIncidentNodes = this.incidentNodes(edge);
            EndpointPair<N> newIncidentNodes = EndpointPair.of(this, nodeU, nodeV);
            Preconditions.checkArgument((boolean)existingIncidentNodes.equals(newIncidentNodes), (String)"Edge %s already exists between the following nodes: %s, so it cannot be reused to connect the following nodes: %s.", edge, existingIncidentNodes, newIncidentNodes);
            return false;
        }
        NetworkConnections<N, E> connectionsU = (NetworkConnections<N, E>)this.nodeConnections.get(nodeU);
        if (!this.allowsParallelEdges()) {
            Preconditions.checkArgument((boolean)(connectionsU == null || !connectionsU.successors().contains(nodeV)), (String)"Nodes %s and %s are already connected by a different edge. To construct a graph that allows parallel edges, call allowsParallelEdges(true) on the Builder.", nodeU, nodeV);
        }
        boolean isSelfLoop = nodeU.equals(nodeV);
        if (!this.allowsSelfLoops()) {
            Preconditions.checkArgument((boolean)(!isSelfLoop), (String)"Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", nodeU);
        }
        if (connectionsU == null) {
            connectionsU = this.addNodeInternal(nodeU);
        }
        connectionsU.addOutEdge(edge, nodeV);
        NetworkConnections<N, E> connectionsV = (NetworkConnections<N, E>)this.nodeConnections.get(nodeV);
        if (connectionsV == null) {
            connectionsV = this.addNodeInternal(nodeV);
        }
        connectionsV.addInEdge(edge, nodeU, (boolean)isSelfLoop);
        this.edgeToReferenceNode.put(edge, nodeU);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeNode(Object node) {
        Preconditions.checkNotNull(node, (Object)"node");
        NetworkConnections connections = (NetworkConnections)this.nodeConnections.get((Object)node);
        if (connections == null) {
            return false;
        }
        Iterator i$ = ImmutableList.copyOf(connections.incidentEdges()).iterator();
        do {
            if (!i$.hasNext()) {
                this.nodeConnections.remove((Object)node);
                return true;
            }
            E edge = i$.next();
            this.removeEdge(edge);
        } while (true);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeEdge(Object edge) {
        Preconditions.checkNotNull(edge, (Object)"edge");
        V nodeU = this.edgeToReferenceNode.get((Object)edge);
        if (nodeU == null) {
            return false;
        }
        NetworkConnections connectionsU = (NetworkConnections)this.nodeConnections.get(nodeU);
        N nodeV = connectionsU.oppositeNode((Object)edge);
        NetworkConnections connectionsV = (NetworkConnections)this.nodeConnections.get(nodeV);
        connectionsU.removeOutEdge((Object)edge);
        connectionsV.removeInEdge((Object)edge, (boolean)(this.allowsSelfLoops() && nodeU.equals(nodeV)));
        this.edgeToReferenceNode.remove((Object)edge);
        return true;
    }

    private NetworkConnections<N, E> newConnections() {
        NetworkConnections<N, E> networkConnections;
        if (this.isDirected()) {
            if (this.allowsParallelEdges()) {
                networkConnections = DirectedMultiNetworkConnections.of();
                return networkConnections;
            }
            networkConnections = DirectedNetworkConnections.of();
            return networkConnections;
        }
        if (this.allowsParallelEdges()) {
            networkConnections = UndirectedMultiNetworkConnections.of();
            return networkConnections;
        }
        networkConnections = UndirectedNetworkConnections.of();
        return networkConnections;
    }
}

