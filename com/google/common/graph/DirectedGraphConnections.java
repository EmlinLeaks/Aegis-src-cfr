/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.graph.DirectedGraphConnections;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class DirectedGraphConnections<N, V>
implements GraphConnections<N, V> {
    private static final Object PRED = new Object();
    private final Map<N, Object> adjacentNodeValues;
    private int predecessorCount;
    private int successorCount;

    private DirectedGraphConnections(Map<N, Object> adjacentNodeValues, int predecessorCount, int successorCount) {
        this.adjacentNodeValues = Preconditions.checkNotNull(adjacentNodeValues);
        this.predecessorCount = Graphs.checkNonNegative((int)predecessorCount);
        this.successorCount = Graphs.checkNonNegative((int)successorCount);
        Preconditions.checkState((boolean)(predecessorCount <= adjacentNodeValues.size() && successorCount <= adjacentNodeValues.size()));
    }

    static <N, V> DirectedGraphConnections<N, V> of() {
        int initialCapacity = 4;
        return new DirectedGraphConnections<K, V>(new HashMap<K, V>((int)initialCapacity, (float)1.0f), (int)0, (int)0);
    }

    static <N, V> DirectedGraphConnections<N, V> ofImmutable(Set<N> predecessors, Map<N, V> successorValues) {
        HashMap<N, Object> adjacentNodeValues = new HashMap<N, Object>();
        adjacentNodeValues.putAll(successorValues);
        Iterator<N> i$ = predecessors.iterator();
        while (i$.hasNext()) {
            N predecessor = i$.next();
            Object value = adjacentNodeValues.put(predecessor, PRED);
            if (value == null) continue;
            adjacentNodeValues.put(predecessor, new PredAndSucc((Object)value));
        }
        return new DirectedGraphConnections<K, V>(ImmutableMap.copyOf(adjacentNodeValues), (int)predecessors.size(), (int)successorValues.size());
    }

    @Override
    public Set<N> adjacentNodes() {
        return Collections.unmodifiableSet(this.adjacentNodeValues.keySet());
    }

    @Override
    public Set<N> predecessors() {
        return new AbstractSet<N>((DirectedGraphConnections)this){
            final /* synthetic */ DirectedGraphConnections this$0;
            {
                this.this$0 = directedGraphConnections;
            }

            public com.google.common.collect.UnmodifiableIterator<N> iterator() {
                Iterator<java.util.Map$Entry<K, V>> entries = DirectedGraphConnections.access$000((DirectedGraphConnections)this.this$0).entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<N>(this, entries){
                    final /* synthetic */ Iterator val$entries;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$entries = iterator;
                    }

                    protected N computeNext() {
                        java.util.Map$Entry entry;
                        do {
                            if (!this.val$entries.hasNext()) return (N)this.endOfData();
                        } while (!DirectedGraphConnections.access$100((entry = (java.util.Map$Entry)this.val$entries.next()).getValue()));
                        return (N)entry.getKey();
                    }
                };
            }

            public int size() {
                return DirectedGraphConnections.access$200((DirectedGraphConnections)this.this$0);
            }

            public boolean contains(@Nullable Object obj) {
                return DirectedGraphConnections.access$100(DirectedGraphConnections.access$000((DirectedGraphConnections)this.this$0).get((Object)obj));
            }
        };
    }

    @Override
    public Set<N> successors() {
        return new AbstractSet<N>((DirectedGraphConnections)this){
            final /* synthetic */ DirectedGraphConnections this$0;
            {
                this.this$0 = directedGraphConnections;
            }

            public com.google.common.collect.UnmodifiableIterator<N> iterator() {
                Iterator<java.util.Map$Entry<K, V>> entries = DirectedGraphConnections.access$000((DirectedGraphConnections)this.this$0).entrySet().iterator();
                return new com.google.common.collect.AbstractIterator<N>(this, entries){
                    final /* synthetic */ Iterator val$entries;
                    final /* synthetic */ 2 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$entries = iterator;
                    }

                    protected N computeNext() {
                        java.util.Map$Entry entry;
                        do {
                            if (!this.val$entries.hasNext()) return (N)this.endOfData();
                        } while (!DirectedGraphConnections.access$300((entry = (java.util.Map$Entry)this.val$entries.next()).getValue()));
                        return (N)entry.getKey();
                    }
                };
            }

            public int size() {
                return DirectedGraphConnections.access$400((DirectedGraphConnections)this.this$0);
            }

            public boolean contains(@Nullable Object obj) {
                return DirectedGraphConnections.access$300(DirectedGraphConnections.access$000((DirectedGraphConnections)this.this$0).get((Object)obj));
            }
        };
    }

    @Override
    public V value(Object node) {
        Object value = this.adjacentNodeValues.get((Object)node);
        if (value == PRED) {
            return (V)null;
        }
        if (!(value instanceof PredAndSucc)) return (V)value;
        return (V)((PredAndSucc)((PredAndSucc)value)).successorValue;
    }

    @Override
    public void removePredecessor(Object node) {
        Object previousValue = this.adjacentNodeValues.get((Object)node);
        if (previousValue == PRED) {
            this.adjacentNodeValues.remove((Object)node);
            Graphs.checkNonNegative((int)(--this.predecessorCount));
            return;
        }
        if (!(previousValue instanceof PredAndSucc)) return;
        this.adjacentNodeValues.put(node, (Object)((PredAndSucc)((PredAndSucc)previousValue)).successorValue);
        Graphs.checkNonNegative((int)(--this.predecessorCount));
    }

    @Override
    public V removeSuccessor(Object node) {
        Object previousValue = this.adjacentNodeValues.get((Object)node);
        if (previousValue == null) return (V)null;
        if (previousValue == PRED) {
            return (V)null;
        }
        if (previousValue instanceof PredAndSucc) {
            this.adjacentNodeValues.put(node, (Object)PRED);
            Graphs.checkNonNegative((int)(--this.successorCount));
            return (V)((PredAndSucc)((PredAndSucc)previousValue)).successorValue;
        }
        this.adjacentNodeValues.remove((Object)node);
        Graphs.checkNonNegative((int)(--this.successorCount));
        return (V)previousValue;
    }

    @Override
    public void addPredecessor(N node, V unused) {
        Object previousValue = this.adjacentNodeValues.put(node, (Object)PRED);
        if (previousValue == null) {
            Graphs.checkPositive((int)(++this.predecessorCount));
            return;
        }
        if (previousValue instanceof PredAndSucc) {
            this.adjacentNodeValues.put(node, (Object)previousValue);
            return;
        }
        if (previousValue == PRED) return;
        this.adjacentNodeValues.put(node, (Object)new PredAndSucc((Object)previousValue));
        Graphs.checkPositive((int)(++this.predecessorCount));
    }

    @Override
    public V addSuccessor(N node, V value) {
        Object previousValue = this.adjacentNodeValues.put(node, value);
        if (previousValue == null) {
            Graphs.checkPositive((int)(++this.successorCount));
            return (V)null;
        }
        if (previousValue instanceof PredAndSucc) {
            this.adjacentNodeValues.put(node, (Object)new PredAndSucc(value));
            return (V)((PredAndSucc)((PredAndSucc)previousValue)).successorValue;
        }
        if (previousValue != PRED) return (V)previousValue;
        this.adjacentNodeValues.put(node, (Object)new PredAndSucc(value));
        Graphs.checkPositive((int)(++this.successorCount));
        return (V)null;
    }

    private static boolean isPredecessor(@Nullable Object value) {
        if (value == PRED) return true;
        if (value instanceof PredAndSucc) return true;
        return false;
    }

    private static boolean isSuccessor(@Nullable Object value) {
        if (value == PRED) return false;
        if (value == null) return false;
        return true;
    }

    static /* synthetic */ Map access$000(DirectedGraphConnections x0) {
        return x0.adjacentNodeValues;
    }

    static /* synthetic */ boolean access$100(Object x0) {
        return DirectedGraphConnections.isPredecessor((Object)x0);
    }

    static /* synthetic */ int access$200(DirectedGraphConnections x0) {
        return x0.predecessorCount;
    }

    static /* synthetic */ boolean access$300(Object x0) {
        return DirectedGraphConnections.isSuccessor((Object)x0);
    }

    static /* synthetic */ int access$400(DirectedGraphConnections x0) {
        return x0.successorCount;
    }
}

