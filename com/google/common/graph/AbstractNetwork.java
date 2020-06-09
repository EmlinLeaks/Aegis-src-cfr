/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.AbstractGraph;
import com.google.common.graph.AbstractNetwork;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Network;
import com.google.common.math.IntMath;
import java.util.Map;
import java.util.Set;

@Beta
public abstract class AbstractNetwork<N, E>
implements Network<N, E> {
    @Override
    public Graph<N> asGraph() {
        return new AbstractGraph<N>((AbstractNetwork)this){
            final /* synthetic */ AbstractNetwork this$0;
            {
                this.this$0 = abstractNetwork;
            }

            public Set<N> nodes() {
                return this.this$0.nodes();
            }

            public Set<EndpointPair<N>> edges() {
                if (!this.this$0.allowsParallelEdges()) return new java.util.AbstractSet<EndpointPair<N>>(this){
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                    }

                    public java.util.Iterator<EndpointPair<N>> iterator() {
                        return com.google.common.collect.Iterators.transform(this.this$1.this$0.edges().iterator(), new Function<E, EndpointPair<N>>(this){
                            final /* synthetic */ com.google.common.graph.AbstractNetwork$1$1 this$2;
                            {
                                this.this$2 = var1_1;
                            }

                            public EndpointPair<N> apply(E edge) {
                                return this.this$2.this$1.this$0.incidentNodes(edge);
                            }
                        });
                    }

                    public int size() {
                        return this.this$1.this$0.edges().size();
                    }

                    public boolean contains(@javax.annotation.Nullable Object obj) {
                        if (!(obj instanceof EndpointPair)) {
                            return false;
                        }
                        EndpointPair endpointPair = (EndpointPair)obj;
                        if (this.this$1.isDirected() != endpointPair.isOrdered()) return false;
                        if (!this.this$1.nodes().contains(endpointPair.nodeU())) return false;
                        if (!this.this$1.successors(endpointPair.nodeU()).contains(endpointPair.nodeV())) return false;
                        return true;
                    }
                };
                return super.edges();
            }

            public com.google.common.graph.ElementOrder<N> nodeOrder() {
                return this.this$0.nodeOrder();
            }

            public boolean isDirected() {
                return this.this$0.isDirected();
            }

            public boolean allowsSelfLoops() {
                return this.this$0.allowsSelfLoops();
            }

            public Set<N> adjacentNodes(Object node) {
                return this.this$0.adjacentNodes((Object)node);
            }

            public Set<N> predecessors(Object node) {
                return this.this$0.predecessors((Object)node);
            }

            public Set<N> successors(Object node) {
                return this.this$0.successors((Object)node);
            }
        };
    }

    @Override
    public int degree(Object node) {
        if (!this.isDirected()) return IntMath.saturatedAdd((int)this.incidentEdges((Object)node).size(), (int)this.edgesConnecting((Object)node, (Object)node).size());
        return IntMath.saturatedAdd((int)this.inEdges((Object)node).size(), (int)this.outEdges((Object)node).size());
    }

    @Override
    public int inDegree(Object node) {
        int n;
        if (this.isDirected()) {
            n = this.inEdges((Object)node).size();
            return n;
        }
        n = this.degree((Object)node);
        return n;
    }

    @Override
    public int outDegree(Object node) {
        int n;
        if (this.isDirected()) {
            n = this.outEdges((Object)node).size();
            return n;
        }
        n = this.degree((Object)node);
        return n;
    }

    @Override
    public Set<E> adjacentEdges(Object edge) {
        EndpointPair<N> endpointPair = this.incidentNodes((Object)edge);
        Sets.SetView<E> endpointPairIncidentEdges = Sets.union(this.incidentEdges(endpointPair.nodeU()), this.incidentEdges(endpointPair.nodeV()));
        return Sets.difference(endpointPairIncidentEdges, ImmutableSet.of(edge));
    }

    public String toString() {
        String propertiesString = String.format((String)"isDirected: %s, allowsParallelEdges: %s, allowsSelfLoops: %s", (Object[])new Object[]{Boolean.valueOf((boolean)this.isDirected()), Boolean.valueOf((boolean)this.allowsParallelEdges()), Boolean.valueOf((boolean)this.allowsSelfLoops())});
        return String.format((String)"%s, nodes: %s, edges: %s", (Object[])new Object[]{propertiesString, this.nodes(), this.edgeIncidentNodesMap()});
    }

    private Map<E, EndpointPair<N>> edgeIncidentNodesMap() {
        Function<E, EndpointPair<N>> edgeToIncidentNodesFn = new Function<E, EndpointPair<N>>((AbstractNetwork)this){
            final /* synthetic */ AbstractNetwork this$0;
            {
                this.this$0 = abstractNetwork;
            }

            public EndpointPair<N> apply(E edge) {
                return this.this$0.incidentNodes(edge);
            }
        };
        return Maps.asMap(this.edges(), edgeToIncidentNodesFn);
    }
}

