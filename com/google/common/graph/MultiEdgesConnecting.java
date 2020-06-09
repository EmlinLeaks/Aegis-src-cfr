/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.MultiEdgesConnecting;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

abstract class MultiEdgesConnecting<E>
extends AbstractSet<E> {
    private final Map<E, ?> outEdgeToNode;
    private final Object targetNode;

    MultiEdgesConnecting(Map<E, ?> outEdgeToNode, Object targetNode) {
        this.outEdgeToNode = Preconditions.checkNotNull(outEdgeToNode);
        this.targetNode = Preconditions.checkNotNull(targetNode);
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        Iterator<Map.Entry<E, ?>> entries = this.outEdgeToNode.entrySet().iterator();
        return new AbstractIterator<E>((MultiEdgesConnecting)this, entries){
            final /* synthetic */ Iterator val$entries;
            final /* synthetic */ MultiEdgesConnecting this$0;
            {
                this.this$0 = multiEdgesConnecting;
                this.val$entries = iterator;
            }

            protected E computeNext() {
                Map.Entry entry;
                do {
                    if (!this.val$entries.hasNext()) return (E)this.endOfData();
                    entry = (Map.Entry)this.val$entries.next();
                } while (!MultiEdgesConnecting.access$000((MultiEdgesConnecting)this.this$0).equals(entry.getValue()));
                return (E)entry.getKey();
            }
        };
    }

    @Override
    public boolean contains(@Nullable Object edge) {
        return this.targetNode.equals(this.outEdgeToNode.get((Object)edge));
    }

    static /* synthetic */ Object access$000(MultiEdgesConnecting x0) {
        return x0.targetNode;
    }
}

