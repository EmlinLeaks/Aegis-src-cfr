/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.representer.Represent;

public abstract class BaseRepresenter {
    protected final Map<Class<?>, Represent> representers = new HashMap<Class<?>, Represent>();
    protected Represent nullRepresenter;
    protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap<Class<?>, Represent>();
    protected DumperOptions.ScalarStyle defaultScalarStyle = null;
    protected DumperOptions.FlowStyle defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
    protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>((BaseRepresenter)this){
        private static final long serialVersionUID = -5576159264232131854L;
        final /* synthetic */ BaseRepresenter this$0;
        {
            this.this$0 = this$0;
        }

        public Node put(Object key, Node value) {
            return (Node)super.put(key, new org.yaml.snakeyaml.nodes.AnchorNode((Node)value));
        }
    };
    protected Object objectToRepresent;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils = false;

    public Node represent(Object data) {
        Node node = this.representData((Object)data);
        this.representedObjects.clear();
        this.objectToRepresent = null;
        return node;
    }

    protected final Node representData(Object data) {
        Object representer2;
        this.objectToRepresent = data;
        if (this.representedObjects.containsKey((Object)this.objectToRepresent)) {
            return this.representedObjects.get((Object)this.objectToRepresent);
        }
        if (data == null) {
            return this.nullRepresenter.representData(null);
        }
        Class<?> clazz = data.getClass();
        if (this.representers.containsKey(clazz)) {
            Represent representer2 = this.representers.get(clazz);
            return representer2.representData((Object)data);
        }
        for (Class<?> repr : this.multiRepresenters.keySet()) {
            if (repr == null || !repr.isInstance((Object)data)) continue;
            Represent representer3 = this.multiRepresenters.get(repr);
            return representer3.representData((Object)data);
        }
        if (this.multiRepresenters.containsKey(null)) {
            representer2 = this.multiRepresenters.get(null);
            return representer2.representData((Object)data);
        }
        representer2 = this.representers.get(null);
        return representer2.representData((Object)data);
    }

    protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
        if (style != null) return new ScalarNode((Tag)tag, (String)value, null, null, (DumperOptions.ScalarStyle)style);
        style = this.defaultScalarStyle;
        return new ScalarNode((Tag)tag, (String)value, null, null, (DumperOptions.ScalarStyle)style);
    }

    protected Node representScalar(Tag tag, String value) {
        return this.representScalar((Tag)tag, (String)value, null);
    }

    protected Node representSequence(Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle) {
        int size = 10;
        if (sequence instanceof List) {
            size = ((List)sequence).size();
        }
        ArrayList<Node> value = new ArrayList<Node>((int)size);
        SequenceNode node = new SequenceNode((Tag)tag, value, (DumperOptions.FlowStyle)flowStyle);
        this.representedObjects.put((Object)this.objectToRepresent, (Node)node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
        for (? item : sequence) {
            Node nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || !((ScalarNode)nodeItem).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
            value.add((Node)nodeItem);
        }
        if (flowStyle != DumperOptions.FlowStyle.AUTO) return node;
        if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            node.setFlowStyle((DumperOptions.FlowStyle)this.defaultFlowStyle);
            return node;
        }
        node.setFlowStyle((DumperOptions.FlowStyle)bestStyle);
        return node;
    }

    protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
        ArrayList<NodeTuple> value = new ArrayList<NodeTuple>((int)mapping.size());
        MappingNode node = new MappingNode((Tag)tag, value, (DumperOptions.FlowStyle)flowStyle);
        this.representedObjects.put((Object)this.objectToRepresent, (Node)node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
        for (Map.Entry<?, ?> entry : mapping.entrySet()) {
            Node nodeKey = this.representData(entry.getKey());
            Node nodeValue = this.representData(entry.getValue());
            if (!(nodeKey instanceof ScalarNode) || !((ScalarNode)nodeKey).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
            if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
            value.add((NodeTuple)new NodeTuple((Node)nodeKey, (Node)nodeValue));
        }
        if (flowStyle != DumperOptions.FlowStyle.AUTO) return node;
        if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            node.setFlowStyle((DumperOptions.FlowStyle)this.defaultFlowStyle);
            return node;
        }
        node.setFlowStyle((DumperOptions.FlowStyle)bestStyle);
        return node;
    }

    public void setDefaultScalarStyle(DumperOptions.ScalarStyle defaultStyle) {
        this.defaultScalarStyle = defaultStyle;
    }

    public DumperOptions.ScalarStyle getDefaultScalarStyle() {
        if (this.defaultScalarStyle != null) return this.defaultScalarStyle;
        return DumperOptions.ScalarStyle.PLAIN;
    }

    public void setDefaultFlowStyle(DumperOptions.FlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public DumperOptions.FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
        this.explicitPropertyUtils = true;
    }

    public final PropertyUtils getPropertyUtils() {
        if (this.propertyUtils != null) return this.propertyUtils;
        this.propertyUtils = new PropertyUtils();
        return this.propertyUtils;
    }

    public final boolean isExplicitPropertyUtils() {
        return this.explicitPropertyUtils;
    }
}

