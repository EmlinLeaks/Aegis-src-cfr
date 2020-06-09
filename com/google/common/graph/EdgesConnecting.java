/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

final class EdgesConnecting<E>
extends AbstractSet<E> {
    private final Map<?, E> nodeToOutEdge;
    private final Object targetNode;

    EdgesConnecting(Map<?, E> nodeToEdgeMap, Object targetNode) {
        this.nodeToOutEdge = Preconditions.checkNotNull(nodeToEdgeMap);
        this.targetNode = Preconditions.checkNotNull(targetNode);
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        UnmodifiableIterator<E> unmodifiableIterator;
        E connectingEdge = this.getConnectingEdge();
        if (connectingEdge == null) {
            unmodifiableIterator = ImmutableSet.of().iterator();
            return unmodifiableIterator;
        }
        unmodifiableIterator = Iterators.singletonIterator(connectingEdge);
        return unmodifiableIterator;
    }

    @Override
    public int size() {
        if (this.getConnectingEdge() != null) return 1;
        return 0;
    }

    @Override
    public boolean contains(@Nullable Object edge) {
        E connectingEdge = this.getConnectingEdge();
        if (connectingEdge == null) return false;
        if (!connectingEdge.equals((Object)edge)) return false;
        return true;
    }

    @Nullable
    private E getConnectingEdge() {
        return (E)this.nodeToOutEdge.get((Object)this.targetNode);
    }
}

