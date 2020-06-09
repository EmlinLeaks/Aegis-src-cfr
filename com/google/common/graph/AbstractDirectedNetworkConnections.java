/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.graph.AbstractDirectedNetworkConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.NetworkConnections;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

abstract class AbstractDirectedNetworkConnections<N, E>
implements NetworkConnections<N, E> {
    protected final Map<E, N> inEdgeMap;
    protected final Map<E, N> outEdgeMap;
    private int selfLoopCount;

    protected AbstractDirectedNetworkConnections(Map<E, N> inEdgeMap, Map<E, N> outEdgeMap, int selfLoopCount) {
        this.inEdgeMap = Preconditions.checkNotNull(inEdgeMap);
        this.outEdgeMap = Preconditions.checkNotNull(outEdgeMap);
        this.selfLoopCount = Graphs.checkNonNegative((int)selfLoopCount);
        Preconditions.checkState((boolean)(selfLoopCount <= inEdgeMap.size() && selfLoopCount <= outEdgeMap.size()));
    }

    @Override
    public Set<N> adjacentNodes() {
        return Sets.union(this.predecessors(), this.successors());
    }

    @Override
    public Set<E> incidentEdges() {
        return new AbstractSet<E>((AbstractDirectedNetworkConnections)this){
            final /* synthetic */ AbstractDirectedNetworkConnections this$0;
            {
                this.this$0 = abstractDirectedNetworkConnections;
            }

            public com.google.common.collect.UnmodifiableIterator<E> iterator() {
                java.lang.Iterable<E> incidentEdges = AbstractDirectedNetworkConnections.access$000((AbstractDirectedNetworkConnections)this.this$0) == 0 ? com.google.common.collect.Iterables.concat(this.this$0.inEdgeMap.keySet(), this.this$0.outEdgeMap.keySet()) : Sets.union(this.this$0.inEdgeMap.keySet(), this.this$0.outEdgeMap.keySet());
                return com.google.common.collect.Iterators.unmodifiableIterator(incidentEdges.iterator());
            }

            public int size() {
                return com.google.common.math.IntMath.saturatedAdd((int)this.this$0.inEdgeMap.size(), (int)(this.this$0.outEdgeMap.size() - AbstractDirectedNetworkConnections.access$000((AbstractDirectedNetworkConnections)this.this$0)));
            }

            public boolean contains(@javax.annotation.Nullable Object obj) {
                if (this.this$0.inEdgeMap.containsKey((Object)obj)) return true;
                if (this.this$0.outEdgeMap.containsKey((Object)obj)) return true;
                return false;
            }
        };
    }

    @Override
    public Set<E> inEdges() {
        return Collections.unmodifiableSet(this.inEdgeMap.keySet());
    }

    @Override
    public Set<E> outEdges() {
        return Collections.unmodifiableSet(this.outEdgeMap.keySet());
    }

    @Override
    public N oppositeNode(Object edge) {
        return (N)Preconditions.checkNotNull(this.outEdgeMap.get((Object)edge));
    }

    @Override
    public N removeInEdge(Object edge, boolean isSelfLoop) {
        if (isSelfLoop) {
            Graphs.checkNonNegative((int)(--this.selfLoopCount));
        }
        N previousNode = this.inEdgeMap.remove((Object)edge);
        return (N)Preconditions.checkNotNull(previousNode);
    }

    @Override
    public N removeOutEdge(Object edge) {
        N previousNode = this.outEdgeMap.remove((Object)edge);
        return (N)Preconditions.checkNotNull(previousNode);
    }

    @Override
    public void addInEdge(E edge, N node, boolean isSelfLoop) {
        N previousNode;
        if (isSelfLoop) {
            Graphs.checkPositive((int)(++this.selfLoopCount));
        }
        Preconditions.checkState((boolean)((previousNode = this.inEdgeMap.put(edge, node)) == null));
    }

    @Override
    public void addOutEdge(E edge, N node) {
        N previousNode = this.outEdgeMap.put(edge, node);
        Preconditions.checkState((boolean)(previousNode == null));
    }

    static /* synthetic */ int access$000(AbstractDirectedNetworkConnections x0) {
        return x0.selfLoopCount;
    }
}

