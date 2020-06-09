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
import com.google.common.graph.AbstractDirectedNetworkConnections;
import com.google.common.graph.DirectedMultiNetworkConnections;
import com.google.common.graph.MultiEdgesConnecting;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class DirectedMultiNetworkConnections<N, E>
extends AbstractDirectedNetworkConnections<N, E> {
    @LazyInit
    private transient Reference<Multiset<N>> predecessorsReference;
    @LazyInit
    private transient Reference<Multiset<N>> successorsReference;

    private DirectedMultiNetworkConnections(Map<E, N> inEdges, Map<E, N> outEdges, int selfLoopCount) {
        super(inEdges, outEdges, (int)selfLoopCount);
    }

    static <N, E> DirectedMultiNetworkConnections<N, E> of() {
        return new DirectedMultiNetworkConnections<V, K>(new HashMap<K, V>((int)2, (float)1.0f), new HashMap<K, V>((int)2, (float)1.0f), (int)0);
    }

    static <N, E> DirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> inEdges, Map<E, N> outEdges, int selfLoopCount) {
        return new DirectedMultiNetworkConnections<N, E>(ImmutableMap.copyOf(inEdges), ImmutableMap.copyOf(outEdges), (int)selfLoopCount);
    }

    @Override
    public Set<N> predecessors() {
        return Collections.unmodifiableSet(this.predecessorsMultiset().elementSet());
    }

    private Multiset<N> predecessorsMultiset() {
        Multiset<N> predecessors = DirectedMultiNetworkConnections.getReference(this.predecessorsReference);
        if (predecessors != null) return predecessors;
        predecessors = HashMultiset.create(this.inEdgeMap.values());
        this.predecessorsReference = new SoftReference<Multiset<N>>(predecessors);
        return predecessors;
    }

    @Override
    public Set<N> successors() {
        return Collections.unmodifiableSet(this.successorsMultiset().elementSet());
    }

    private Multiset<N> successorsMultiset() {
        Multiset<N> successors = DirectedMultiNetworkConnections.getReference(this.successorsReference);
        if (successors != null) return successors;
        successors = HashMultiset.create(this.outEdgeMap.values());
        this.successorsReference = new SoftReference<Multiset<N>>(successors);
        return successors;
    }

    @Override
    public Set<E> edgesConnecting(Object node) {
        return new MultiEdgesConnecting<E>((DirectedMultiNetworkConnections)this, (Map)this.outEdgeMap, (Object)node, (Object)node){
            final /* synthetic */ Object val$node;
            final /* synthetic */ DirectedMultiNetworkConnections this$0;
            {
                this.this$0 = directedMultiNetworkConnections;
                this.val$node = object;
                super(x0, (Object)x1);
            }

            public int size() {
                return DirectedMultiNetworkConnections.access$000((DirectedMultiNetworkConnections)this.this$0).count((Object)this.val$node);
            }
        };
    }

    @Override
    public N removeInEdge(Object edge, boolean isSelfLoop) {
        N node = super.removeInEdge((Object)edge, (boolean)isSelfLoop);
        Multiset<N> predecessors = DirectedMultiNetworkConnections.getReference(this.predecessorsReference);
        if (predecessors == null) return (N)node;
        Preconditions.checkState((boolean)predecessors.remove(node));
        return (N)node;
    }

    @Override
    public N removeOutEdge(Object edge) {
        N node = super.removeOutEdge((Object)edge);
        Multiset<N> successors = DirectedMultiNetworkConnections.getReference(this.successorsReference);
        if (successors == null) return (N)node;
        Preconditions.checkState((boolean)successors.remove(node));
        return (N)node;
    }

    @Override
    public void addInEdge(E edge, N node, boolean isSelfLoop) {
        super.addInEdge(edge, node, (boolean)isSelfLoop);
        Multiset<N> predecessors = DirectedMultiNetworkConnections.getReference(this.predecessorsReference);
        if (predecessors == null) return;
        Preconditions.checkState((boolean)predecessors.add(node));
    }

    @Override
    public void addOutEdge(E edge, N node) {
        super.addOutEdge(edge, node);
        Multiset<N> successors = DirectedMultiNetworkConnections.getReference(this.successorsReference);
        if (successors == null) return;
        Preconditions.checkState((boolean)successors.add(node));
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

    static /* synthetic */ Multiset access$000(DirectedMultiNetworkConnections x0) {
        return x0.successorsMultiset();
    }
}

