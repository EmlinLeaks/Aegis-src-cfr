/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public final class Graphs {
    private Graphs() {
    }

    public static boolean hasCycle(Graph<?> graph) {
        ? node;
        int numEdges = graph.edges().size();
        if (numEdges == 0) {
            return false;
        }
        if (!graph.isDirected() && numEdges >= graph.nodes().size()) {
            return true;
        }
        HashMap<Object, NodeVisitState> visitedNodes = Maps.newHashMapWithExpectedSize((int)graph.nodes().size());
        Iterator<?> i$ = graph.nodes().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!Graphs.subgraphHasCycle(graph, visitedNodes, node = i$.next(), null));
        return true;
    }

    public static boolean hasCycle(Network<?, ?> network) {
        if (network.isDirected()) return Graphs.hasCycle(network.asGraph());
        if (!network.allowsParallelEdges()) return Graphs.hasCycle(network.asGraph());
        if (network.edges().size() <= network.asGraph().edges().size()) return Graphs.hasCycle(network.asGraph());
        return true;
    }

    private static boolean subgraphHasCycle(Graph<?> graph, Map<Object, NodeVisitState> visitedNodes, Object node, @Nullable Object previousNode) {
        ? nextNode;
        NodeVisitState state = visitedNodes.get((Object)node);
        if (state == NodeVisitState.COMPLETE) {
            return false;
        }
        if (state == NodeVisitState.PENDING) {
            return true;
        }
        visitedNodes.put((Object)node, (NodeVisitState)NodeVisitState.PENDING);
        Iterator<?> i$ = graph.successors((Object)node).iterator();
        do {
            if (i$.hasNext()) continue;
            visitedNodes.put((Object)node, (NodeVisitState)NodeVisitState.COMPLETE);
            return false;
        } while (!Graphs.canTraverseWithoutReusingEdge(graph, nextNode = i$.next(), (Object)previousNode) || !Graphs.subgraphHasCycle(graph, visitedNodes, nextNode, (Object)node));
        return true;
    }

    private static boolean canTraverseWithoutReusingEdge(Graph<?> graph, Object nextNode, @Nullable Object previousNode) {
        if (graph.isDirected()) return true;
        if (Objects.equal((Object)previousNode, (Object)nextNode)) return false;
        return true;
    }

    /*
     * Exception decompiling
     */
    public static <N> Graph<N> transitiveClosure(Graph<N> graph) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[DOLOOP]], but top level block is 4[UNCONDITIONALDOLOOP]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    public static <N> Set<N> reachableNodes(Graph<N> graph, Object node) {
        Preconditions.checkArgument((boolean)graph.nodes().contains((Object)node), (String)"Node %s is not an element of this graph.", (Object)node);
        LinkedHashSet<Object> visitedNodes = new LinkedHashSet<Object>();
        ArrayDeque<Object> queuedNodes = new ArrayDeque<Object>();
        visitedNodes.add(node);
        queuedNodes.add(node);
        block0 : while (!queuedNodes.isEmpty()) {
            E currentNode = queuedNodes.remove();
            Iterator<N> i$ = graph.successors(currentNode).iterator();
            do {
                if (!i$.hasNext()) continue block0;
                N successor = i$.next();
                if (!visitedNodes.add(successor)) continue;
                queuedNodes.add(successor);
            } while (true);
            break;
        }
        return Collections.unmodifiableSet(visitedNodes);
    }

    public static boolean equivalent(@Nullable Graph<?> graphA, @Nullable Graph<?> graphB) {
        if (graphA == graphB) {
            return true;
        }
        if (graphA == null) return false;
        if (graphB == null) {
            return false;
        }
        if (graphA.isDirected() != graphB.isDirected()) return false;
        if (!graphA.nodes().equals(graphB.nodes())) return false;
        if (!graphA.edges().equals(graphB.edges())) return false;
        return true;
    }

    public static boolean equivalent(@Nullable ValueGraph<?, ?> graphA, @Nullable ValueGraph<?, ?> graphB) {
        EndpointPair<N> edge;
        if (graphA == graphB) {
            return true;
        }
        if (graphA == null) return false;
        if (graphB == null) {
            return false;
        }
        if (graphA.isDirected() != graphB.isDirected()) return false;
        if (!graphA.nodes().equals(graphB.nodes())) return false;
        if (!graphA.edges().equals(graphB.edges())) {
            return false;
        }
        Iterator<EndpointPair<N>> i$ = graphA.edges().iterator();
        do {
            if (!i$.hasNext()) return true;
        } while (graphA.edgeValue((edge = i$.next()).nodeU(), edge.nodeV()).equals(graphB.edgeValue(edge.nodeU(), edge.nodeV())));
        return false;
    }

    public static boolean equivalent(@Nullable Network<?, ?> networkA, @Nullable Network<?, ?> networkB) {
        ? edge;
        if (networkA == networkB) {
            return true;
        }
        if (networkA == null) return false;
        if (networkB == null) {
            return false;
        }
        if (networkA.isDirected() != networkB.isDirected()) return false;
        if (!networkA.nodes().equals(networkB.nodes())) return false;
        if (!networkA.edges().equals(networkB.edges())) {
            return false;
        }
        Iterator<?> i$ = networkA.edges().iterator();
        do {
            if (!i$.hasNext()) return true;
        } while (networkA.incidentNodes(edge = i$.next()).equals(networkB.incidentNodes(edge)));
        return false;
    }

    public static <N> Graph<N> transpose(Graph<N> graph) {
        if (!graph.isDirected()) {
            return graph;
        }
        if (!(graph instanceof TransposedGraph)) return new TransposedGraph<N>(graph);
        return ((TransposedGraph)((TransposedGraph)graph)).graph;
    }

    public static <N, V> ValueGraph<N, V> transpose(ValueGraph<N, V> graph) {
        if (!graph.isDirected()) {
            return graph;
        }
        if (!(graph instanceof TransposedValueGraph)) return new TransposedValueGraph<N, V>(graph);
        return ((TransposedValueGraph)((TransposedValueGraph)graph)).graph;
    }

    public static <N, E> Network<N, E> transpose(Network<N, E> network) {
        if (!network.isDirected()) {
            return network;
        }
        if (!(network instanceof TransposedNetwork)) return new TransposedNetwork<N, E>(network);
        return ((TransposedNetwork)((TransposedNetwork)network)).network;
    }

    public static <N> MutableGraph<N> inducedSubgraph(Graph<N> graph, Iterable<? extends N> nodes) {
        MutableGraph<N1> subgraph = GraphBuilder.from(graph).build();
        for (Object node : nodes) {
            subgraph.addNode(node);
        }
        Iterator<Object> i$ = subgraph.nodes().iterator();
        block1 : while (i$.hasNext()) {
            Object node;
            node = i$.next();
            Iterator<N> i$2 = graph.successors(node).iterator();
            do {
                if (!i$2.hasNext()) continue block1;
                N successorNode = i$2.next();
                if (!subgraph.nodes().contains(successorNode)) continue;
                subgraph.putEdge(node, successorNode);
            } while (true);
            break;
        }
        return subgraph;
    }

    public static <N, V> MutableValueGraph<N, V> inducedSubgraph(ValueGraph<N, V> graph, Iterable<? extends N> nodes) {
        MutableValueGraph<N1, V1> subgraph = ValueGraphBuilder.from(graph).build();
        for (Object node : nodes) {
            subgraph.addNode(node);
        }
        Iterator<Object> i$ = subgraph.nodes().iterator();
        block1 : while (i$.hasNext()) {
            Object node;
            node = i$.next();
            Iterator<N> i$2 = graph.successors(node).iterator();
            do {
                if (!i$2.hasNext()) continue block1;
                N successorNode = i$2.next();
                if (!subgraph.nodes().contains(successorNode)) continue;
                subgraph.putEdgeValue(node, successorNode, graph.edgeValue(node, successorNode));
            } while (true);
            break;
        }
        return subgraph;
    }

    public static <N, E> MutableNetwork<N, E> inducedSubgraph(Network<N, E> network, Iterable<? extends N> nodes) {
        MutableNetwork<N1, E1> subgraph = NetworkBuilder.from(network).build();
        for (Object node : nodes) {
            subgraph.addNode(node);
        }
        Iterator<Object> i$ = subgraph.nodes().iterator();
        block1 : while (i$.hasNext()) {
            Object node;
            node = i$.next();
            Iterator<E> i$2 = network.outEdges(node).iterator();
            do {
                if (!i$2.hasNext()) continue block1;
                E edge = i$2.next();
                N successorNode = network.incidentNodes(edge).adjacentNode(node);
                if (!subgraph.nodes().contains(successorNode)) continue;
                subgraph.addEdge(node, successorNode, edge);
            } while (true);
            break;
        }
        return subgraph;
    }

    public static <N> MutableGraph<N> copyOf(Graph<N> graph) {
        MutableGraph<N1> copy = GraphBuilder.from(graph).expectedNodeCount((int)graph.nodes().size()).build();
        for (N node : graph.nodes()) {
            copy.addNode(node);
        }
        Iterator<Object> i$ = graph.edges().iterator();
        while (i$.hasNext()) {
            EndpointPair edge = (EndpointPair)i$.next();
            copy.putEdge(edge.nodeU(), edge.nodeV());
        }
        return copy;
    }

    public static <N, V> MutableValueGraph<N, V> copyOf(ValueGraph<N, V> graph) {
        MutableValueGraph<N1, V1> copy = ValueGraphBuilder.from(graph).expectedNodeCount((int)graph.nodes().size()).build();
        for (N node : graph.nodes()) {
            copy.addNode(node);
        }
        Iterator<Object> i$ = graph.edges().iterator();
        while (i$.hasNext()) {
            EndpointPair edge = (EndpointPair)i$.next();
            copy.putEdgeValue(edge.nodeU(), edge.nodeV(), graph.edgeValue(edge.nodeU(), edge.nodeV()));
        }
        return copy;
    }

    public static <N, E> MutableNetwork<N, E> copyOf(Network<N, E> network) {
        MutableNetwork<N1, E1> copy = NetworkBuilder.from(network).expectedNodeCount((int)network.nodes().size()).expectedEdgeCount((int)network.edges().size()).build();
        for (N node : network.nodes()) {
            copy.addNode(node);
        }
        Iterator<Object> i$ = network.edges().iterator();
        while (i$.hasNext()) {
            Object edge = i$.next();
            EndpointPair<N> endpointPair = network.incidentNodes((Object)edge);
            copy.addEdge(endpointPair.nodeU(), endpointPair.nodeV(), edge);
        }
        return copy;
    }

    @CanIgnoreReturnValue
    static int checkNonNegative(int value) {
        Preconditions.checkArgument((boolean)(value >= 0), (String)"Not true that %s is non-negative.", (int)value);
        return value;
    }

    @CanIgnoreReturnValue
    static int checkPositive(int value) {
        Preconditions.checkArgument((boolean)(value > 0), (String)"Not true that %s is positive.", (int)value);
        return value;
    }

    @CanIgnoreReturnValue
    static long checkNonNegative(long value) {
        Preconditions.checkArgument((boolean)(value >= 0L), (String)"Not true that %s is non-negative.", (long)value);
        return value;
    }

    @CanIgnoreReturnValue
    static long checkPositive(long value) {
        Preconditions.checkArgument((boolean)(value > 0L), (String)"Not true that %s is positive.", (long)value);
        return value;
    }
}

