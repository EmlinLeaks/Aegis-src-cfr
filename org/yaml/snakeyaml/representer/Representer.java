/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.representer.SafeRepresenter;

public class Representer
extends SafeRepresenter {
    protected Map<Class<? extends Object>, TypeDescription> typeDefinitions = Collections.emptyMap();

    public Representer() {
        this.representers.put(null, new RepresentJavaBean((Representer)this));
    }

    public Representer(DumperOptions options) {
        super((DumperOptions)options);
        this.representers.put(null, new RepresentJavaBean((Representer)this));
    }

    public TypeDescription addTypeDescription(TypeDescription td) {
        if (Collections.EMPTY_MAP == this.typeDefinitions) {
            this.typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
        }
        if (td.getTag() != null) {
            this.addClassTag(td.getType(), (Tag)td.getTag());
        }
        td.setPropertyUtils((PropertyUtils)this.getPropertyUtils());
        return this.typeDefinitions.put(td.getType(), (TypeDescription)td);
    }

    @Override
    public void setPropertyUtils(PropertyUtils propertyUtils) {
        super.setPropertyUtils((PropertyUtils)propertyUtils);
        Collection<TypeDescription> tds = this.typeDefinitions.values();
        Iterator<TypeDescription> iterator = tds.iterator();
        while (iterator.hasNext()) {
            TypeDescription typeDescription = iterator.next();
            typeDescription.setPropertyUtils((PropertyUtils)propertyUtils);
        }
    }

    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        ArrayList<NodeTuple> value = new ArrayList<NodeTuple>((int)properties.size());
        Tag customTag = (Tag)this.classTags.get(javaBean.getClass());
        Tag tag = customTag != null ? customTag : new Tag(javaBean.getClass());
        MappingNode node = new MappingNode((Tag)tag, value, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.AUTO);
        this.representedObjects.put(javaBean, node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
        Iterator<Property> iterator = properties.iterator();
        while (iterator.hasNext()) {
            Node nodeValue;
            Object memberValue;
            Property property;
            Tag customPropertyTag = (memberValue = (property = iterator.next()).get((Object)javaBean)) == null ? null : (Tag)this.classTags.get(memberValue.getClass());
            NodeTuple tuple = this.representJavaBeanProperty((Object)javaBean, (Property)property, (Object)memberValue, (Tag)customPropertyTag);
            if (tuple == null) continue;
            if (!((ScalarNode)tuple.getKeyNode()).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
            if (!((nodeValue = tuple.getValueNode()) instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
            value.add((NodeTuple)tuple);
        }
        if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            node.setFlowStyle((DumperOptions.FlowStyle)this.defaultFlowStyle);
            return node;
        }
        node.setFlowStyle((DumperOptions.FlowStyle)bestStyle);
        return node;
    }

    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        ScalarNode nodeKey = (ScalarNode)this.representData((Object)property.getName());
        boolean hasAlias = this.representedObjects.containsKey((Object)propertyValue);
        Node nodeValue = this.representData((Object)propertyValue);
        if (propertyValue == null) return new NodeTuple((Node)nodeKey, (Node)nodeValue);
        if (hasAlias) return new NodeTuple((Node)nodeKey, (Node)nodeValue);
        NodeId nodeId = nodeValue.getNodeId();
        if (customTag != null) return new NodeTuple((Node)nodeKey, (Node)nodeValue);
        if (nodeId == NodeId.scalar) {
            if (property.getType() == Enum.class) return new NodeTuple((Node)nodeKey, (Node)nodeValue);
            if (!(propertyValue instanceof Enum)) return new NodeTuple((Node)nodeKey, (Node)nodeValue);
            nodeValue.setTag((Tag)Tag.STR);
            return new NodeTuple((Node)nodeKey, (Node)nodeValue);
        }
        if (nodeId == NodeId.mapping && property.getType() == propertyValue.getClass() && !(propertyValue instanceof Map) && !nodeValue.getTag().equals((Object)Tag.SET)) {
            nodeValue.setTag((Tag)Tag.MAP);
        }
        this.checkGlobalTag((Property)property, (Node)nodeValue, (Object)propertyValue);
        return new NodeTuple((Node)nodeKey, (Node)nodeValue);
    }

    protected void checkGlobalTag(Property property, Node node, Object object) {
        if (object.getClass().isArray() && object.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Class<?>[] arguments = property.getActualTypeArguments();
        if (arguments == null) return;
        if (node.getNodeId() == NodeId.sequence) {
            Class<?> t = arguments[0];
            SequenceNode snode = (SequenceNode)node;
            Iterable<Object> memberList = Collections.EMPTY_LIST;
            if (object.getClass().isArray()) {
                memberList = Arrays.asList((Object[])object);
            } else if (object instanceof Iterable) {
                memberList = (Iterable)object;
            }
            Iterator<T> iter = memberList.iterator();
            if (!iter.hasNext()) return;
            Iterator<Node> iterator = snode.getValue().iterator();
            while (iterator.hasNext()) {
                Node childNode = iterator.next();
                T member = iter.next();
                if (member == null || !t.equals(member.getClass()) || childNode.getNodeId() != NodeId.mapping) continue;
                childNode.setTag((Tag)Tag.MAP);
            }
            return;
        }
        if (object instanceof Set) {
            Class<?> t = arguments[0];
            MappingNode mnode = (MappingNode)node;
            Iterator<NodeTuple> iter = mnode.getValue().iterator();
            Set set = (Set)object;
            Iterator<E> iterator = set.iterator();
            while (iterator.hasNext()) {
                E member = iterator.next();
                NodeTuple tuple = iter.next();
                Node keyNode = tuple.getKeyNode();
                if (!t.equals(member.getClass()) || keyNode.getNodeId() != NodeId.mapping) continue;
                keyNode.setTag((Tag)Tag.MAP);
            }
            return;
        }
        if (!(object instanceof Map)) return;
        Class<?> keyType = arguments[0];
        Class<?> valueType = arguments[1];
        MappingNode mnode = (MappingNode)node;
        Iterator<NodeTuple> set = mnode.getValue().iterator();
        while (set.hasNext()) {
            NodeTuple tuple = set.next();
            this.resetTag(keyType, (Node)tuple.getKeyNode());
            this.resetTag(valueType, (Node)tuple.getValueNode());
        }
    }

    private void resetTag(Class<? extends Object> type, Node node) {
        Tag tag = node.getTag();
        if (!tag.matches(type)) return;
        if (Enum.class.isAssignableFrom(type)) {
            node.setTag((Tag)Tag.STR);
            return;
        }
        node.setTag((Tag)Tag.MAP);
    }

    protected Set<Property> getProperties(Class<? extends Object> type) {
        if (!this.typeDefinitions.containsKey(type)) return this.getPropertyUtils().getProperties(type);
        return this.typeDefinitions.get(type).getProperties();
    }
}

