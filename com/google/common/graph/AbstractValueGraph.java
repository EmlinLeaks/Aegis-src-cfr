/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.graph.AbstractGraph;
import com.google.common.graph.AbstractValueGraph;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ValueGraph;
import java.util.Map;
import java.util.Set;

@Beta
public abstract class AbstractValueGraph<N, V>
extends AbstractGraph<N>
implements ValueGraph<N, V> {
    @Override
    public V edgeValue(Object nodeU, Object nodeV) {
        V value = this.edgeValueOrDefault((Object)nodeU, (Object)nodeV, null);
        if (value != null) return (V)value;
        Preconditions.checkArgument((boolean)this.nodes().contains((Object)nodeU), (String)"Node %s is not an element of this graph.", (Object)nodeU);
        Preconditions.checkArgument((boolean)this.nodes().contains((Object)nodeV), (String)"Node %s is not an element of this graph.", (Object)nodeV);
        throw new IllegalArgumentException((String)String.format((String)"Edge connecting %s to %s is not present in this graph.", (Object[])new Object[]{nodeU, nodeV}));
    }

    @Override
    public String toString() {
        String propertiesString = String.format((String)"isDirected: %s, allowsSelfLoops: %s", (Object[])new Object[]{Boolean.valueOf((boolean)this.isDirected()), Boolean.valueOf((boolean)this.allowsSelfLoops())});
        return String.format((String)"%s, nodes: %s, edges: %s", (Object[])new Object[]{propertiesString, this.nodes(), this.edgeValueMap()});
    }

    private Map<EndpointPair<N>, V> edgeValueMap() {
        Function<EndpointPair<N>, V> edgeToValueFn = new Function<EndpointPair<N>, V>((AbstractValueGraph)this){
            final /* synthetic */ AbstractValueGraph this$0;
            {
                this.this$0 = abstractValueGraph;
            }

            public V apply(EndpointPair<N> edge) {
                return (V)this.this$0.edgeValue(edge.nodeU(), edge.nodeV());
            }
        };
        return Maps.asMap(this.edges(), edgeToValueFn);
    }
}

