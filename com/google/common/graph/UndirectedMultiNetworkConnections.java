/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.common.graph.AbstractUndirectedNetworkConnections;
import com.google.common.graph.MultiEdgesConnecting;
import com.google.common.graph.UndirectedMultiNetworkConnections;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class UndirectedMultiNetworkConnections<N, E>
extends AbstractUndirectedNetworkConnections<N, E> {
    @LazyInit
    private transient Reference<Multiset<N>> adjacentNodesReference;

    private UndirectedMultiNetworkConnections(Map<E, N> incidentEdges) {
        super(incidentEdges);
    }

    static <N, E> UndirectedMultiNetworkConnections<N, E> of() {
        return new UndirectedMultiNetworkConnections<V, K>(new HashMap<K, V>((int)2, (float)1.0f));
    }

    static <N, E> UndirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> incidentEdges) {
        return new UndirectedMultiNetworkConnections<N, E>(ImmutableMap.copyOf(incidentEdges));
    }

    @Override
    public Set<N> adjacentNodes() {
        return Collections.unmodifiableSet(this.adjacentNodesMultiset().elementSet());
    }

    private Multiset<N> adjacentNodesMultiset() {
        Multiset<N> adjacentNodes = UndirectedMultiNetworkConnections.getReference(this.adjacentNodesReference);
        if (adjacentNodes != null) return adjacentNodes;
        adjacentNodes = HashMultiset.create(this.incidentEdgeMap.values());
        this.adjacentNodesReference = new SoftReference<Multiset<N>>(adjacentNodes);
        return adjacentNodes;
    }

    @Override
    public Set<E> edgesConnecting(Object node) {
        return new MultiEdgesConnecting<E>((UndirectedMultiNetworkConnections)this, (Map)this.incidentEdgeMap, (Object)node, (Object)node){
            final /* synthetic */ Object val$node;
            final /* synthetic */ UndirectedMultiNetworkConnections this$0;
            {
                this.this$0 = undirectedMultiNetworkConnections;
                this.val$node = object;
                super(x0, (Object)x1);
            }

            public int size() {
                return UndirectedMultiNetworkConnections.access$000((UndirectedMultiNetworkConnections)this.this$0).count((Object)this.val$node);
            }
        };
    }

    @Override
    public N removeInEdge(Object edge, boolean isSelfLoop) {
        if (isSelfLoop) return (N)null;
        return (N)this.removeOutEdge((Object)edge);
    }

    @Override
    public N removeOutEdge(Object edge) {
        N node = super.removeOutEdge((Object)edge);
        Multiset<N> adjacentNodes = UndirectedMultiNetworkConnections.getReference(this.adjacentNodesReference);
        if (adjacentNodes == null) return (N)node;
        Preconditions.checkState((boolean)adjacentNodes.remove(node));
        return (N)node;
    }

    @Override
    public void addInEdge(E edge, N node, boolean isSelfLoop) {
        if (isSelfLoop) return;
        this.addOutEdge(edge, node);
    }

    @Override
    public void addOutEdge(E edge, N node) {
        super.addOutEdge(edge, node);
        Multiset<N> adjacentNodes = UndirectedMultiNetworkConnections.getReference(this.adjacentNodesReference);
        if (adjacentNodes == null) return;
        Preconditions.checkState((boolean)adjacentNodes.add(node));
    }

    @Nullable
    private static <T> T getReference(@Nullable Reference<T> reference) {
        T t;
        if (reference == null) {
            t = null;
            return (T)((T)t);
        }
        t = (T)reference.get();
        return (T)t;
    }

    static /* synthetic */ Multiset access$000(UndirectedMultiNetworkConnections x0) {
        return x0.adjacentNodesMultiset();
    }
}

