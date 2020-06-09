/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.nodes;

import java.util.Iterator;
import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

public class MappingNode
extends CollectionNode<NodeTuple> {
    private List<NodeTuple> value;
    private boolean merged = false;

    public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super((Tag)tag, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)flowStyle);
        if (value == null) {
            throw new NullPointerException((String)"value in a Node is required.");
        }
        this.value = value;
        this.resolved = resolved;
    }

    public MappingNode(Tag tag, List<NodeTuple> value, DumperOptions.FlowStyle flowStyle) {
        this((Tag)tag, (boolean)true, value, null, null, (DumperOptions.FlowStyle)flowStyle);
    }

    @Deprecated
    public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, Mark startMark, Mark endMark, Boolean flowStyle) {
        this((Tag)tag, (boolean)resolved, value, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.fromBoolean((Boolean)flowStyle));
    }

    @Deprecated
    public MappingNode(Tag tag, List<NodeTuple> value, Boolean flowStyle) {
        this((Tag)tag, value, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.fromBoolean((Boolean)flowStyle));
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.mapping;
    }

    @Override
    public List<NodeTuple> getValue() {
        return this.value;
    }

    public void setValue(List<NodeTuple> mergedValue) {
        this.value = mergedValue;
    }

    public void setOnlyKeyType(Class<? extends Object> keyType) {
        Iterator<NodeTuple> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            NodeTuple nodes = iterator.next();
            nodes.getKeyNode().setType(keyType);
        }
    }

    public void setTypes(Class<? extends Object> keyType, Class<? extends Object> valueType) {
        Iterator<NodeTuple> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            NodeTuple nodes = iterator.next();
            nodes.getValueNode().setType(valueType);
            nodes.getKeyNode().setType(keyType);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator<NodeTuple> iterator = this.getValue().iterator();
        do {
            if (!iterator.hasNext()) {
                String values = buf.toString();
                return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", values=" + values + ")>";
            }
            NodeTuple node = iterator.next();
            buf.append((String)"{ key=");
            buf.append((Object)node.getKeyNode());
            buf.append((String)"; value=");
            if (node.getValueNode() instanceof CollectionNode) {
                buf.append((int)System.identityHashCode((Object)node.getValueNode()));
            } else {
                buf.append((String)node.toString());
            }
            buf.append((String)" }");
        } while (true);
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public boolean isMerged() {
        return this.merged;
    }
}

