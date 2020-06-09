/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraph;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Set;

@Beta
public abstract class AbstractGraph<N>
implements Graph<N> {
    protected long edgeCount() {
        long degreeSum = 0L;
        for (N node : this.nodes()) {
            degreeSum += (long)this.degree(node);
        }
        Preconditions.checkState((boolean)((degreeSum & 1L) == 0L));
        return degreeSum >>> 1;
    }

    @Override
    public Set<EndpointPair<N>> edges() {
        return new AbstractSet<EndpointPair<N>>((AbstractGraph)this){
            final /* synthetic */ AbstractGraph this$0;
            {
                this.this$0 = abstractGraph;
            }

            public com.google.common.collect.UnmodifiableIterator<EndpointPair<N>> iterator() {
                return com.google.common.graph.EndpointPairIterator.of(this.this$0);
            }

            public int size() {
                return com.google.common.primitives.Ints.saturatedCast((long)this.this$0.edgeCount());
            }

            public boolean contains(@javax.annotation.Nullable Object obj) {
                if (!(obj instanceof EndpointPair)) {
                    return false;
                }
                EndpointPair endpointPair = (EndpointPair)obj;
                if (this.this$0.isDirected() != endpointPair.isOrdered()) return false;
                if (!this.this$0.nodes().contains(endpointPair.nodeU())) return false;
                if (!this.this$0.successors(endpointPair.nodeU()).contains(endpointPair.nodeV())) return false;
                return true;
            }
        };
    }

    @Override
    public int degree(Object node) {
        if (this.isDirected()) {
            return IntMath.saturatedAdd((int)this.predecessors((Object)node).size(), (int)this.successors((Object)node).size());
        }
        Set<N> neighbors = this.adjacentNodes((Object)node);
        int selfLoopCount = this.allowsSelfLoops() && neighbors.contains((Object)node) ? 1 : 0;
        return IntMath.saturatedAdd((int)neighbors.size(), (int)selfLoopCount);
    }

    @Override
    public int inDegree(Object node) {
        int n;
        if (this.isDirected()) {
            n = this.predecessors((Object)node).size();
            return n;
        }
        n = this.degree((Object)node);
        return n;
    }

    @Override
    public int outDegree(Object node) {
        int n;
        if (this.isDirected()) {
            n = this.successors((Object)node).size();
            return n;
        }
        n = this.degree((Object)node);
        return n;
    }

    public String toString() {
        String propertiesString = String.format((String)"isDirected: %s, allowsSelfLoops: %s", (Object[])new Object[]{Boolean.valueOf((boolean)this.isDirected()), Boolean.valueOf((boolean)this.allowsSelfLoops())});
        return String.format((String)"%s, nodes: %s, edges: %s", (Object[])new Object[]{propertiesString, this.nodes(), this.edges()});
    }
}

