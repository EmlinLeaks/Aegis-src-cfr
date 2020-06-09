/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseConstructor {
    protected final Map<NodeId, Construct> yamlClassConstructors = new EnumMap<NodeId, Construct>(NodeId.class);
    protected final Map<Tag, Construct> yamlConstructors = new HashMap<Tag, Construct>();
    protected final Map<String, Construct> yamlMultiConstructors = new HashMap<String, Construct>();
    protected Composer composer;
    final Map<Node, Object> constructedObjects = new HashMap<Node, Object>();
    private final Set<Node> recursiveObjects = new HashSet<Node>();
    private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill = new ArrayList<E>();
    private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill = new ArrayList<E>();
    protected Tag rootTag = null;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils = false;
    private boolean allowDuplicateKeys = true;
    private boolean wrappedToRootException = false;
    protected final Map<Class<? extends Object>, TypeDescription> typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
    protected final Map<Tag, Class<? extends Object>> typeTags = new HashMap<Tag, Class<? extends Object>>();

    public BaseConstructor() {
        this.typeDefinitions.put(SortedMap.class, (TypeDescription)new TypeDescription(SortedMap.class, (Tag)Tag.OMAP, TreeMap.class));
        this.typeDefinitions.put(SortedSet.class, (TypeDescription)new TypeDescription(SortedSet.class, (Tag)Tag.SET, TreeSet.class));
    }

    public void setComposer(Composer composer) {
        this.composer = composer;
    }

    public boolean checkData() {
        return this.composer.checkNode();
    }

    public Object getData() {
        this.composer.checkNode();
        Node node = this.composer.getNode();
        if (this.rootTag == null) return this.constructDocument((Node)node);
        node.setTag((Tag)this.rootTag);
        return this.constructDocument((Node)node);
    }

    public Object getSingleData(Class<?> type) {
        Node node = this.composer.getSingleNode();
        if (node != null && !Tag.NULL.equals((Object)node.getTag())) {
            if (Object.class != type) {
                node.setTag((Tag)new Tag(type));
                return this.constructDocument((Node)node);
            }
            if (this.rootTag == null) return this.constructDocument((Node)node);
            node.setTag((Tag)this.rootTag);
            return this.constructDocument((Node)node);
        }
        Construct construct = this.yamlConstructors.get((Object)Tag.NULL);
        return construct.construct((Node)node);
    }

    protected final Object constructDocument(Node node) {
        try {
            Object data = this.constructObject((Node)node);
            this.fillRecursive();
            this.constructedObjects.clear();
            this.recursiveObjects.clear();
            return data;
        }
        catch (RuntimeException e) {
            if (!this.wrappedToRootException) throw e;
            if (e instanceof YAMLException) throw e;
            throw new YAMLException((Throwable)e);
        }
    }

    private void fillRecursive() {
        if (!this.maps2fill.isEmpty()) {
            for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : this.maps2fill) {
                RecursiveTuple<Object, Object> key_value = entry._2();
                entry._1().put((Object)key_value._1(), (Object)key_value._2());
            }
            this.maps2fill.clear();
        }
        if (this.sets2fill.isEmpty()) return;
        Iterator<RecursiveTuple<Object, Object>> iterator = this.sets2fill.iterator();
        do {
            if (!iterator.hasNext()) {
                this.sets2fill.clear();
                return;
            }
            RecursiveTuple<Object, Object> value = iterator.next();
            ((Set)value._1()).add(value._2());
        } while (true);
    }

    protected Object constructObject(Node node) {
        if (!this.constructedObjects.containsKey((Object)node)) return this.constructObjectNoCheck((Node)node);
        return this.constructedObjects.get((Object)node);
    }

    protected Object constructObjectNoCheck(Node node) {
        if (this.recursiveObjects.contains((Object)node)) {
            throw new ConstructorException(null, null, (String)"found unconstructable recursive node", (Mark)node.getStartMark());
        }
        this.recursiveObjects.add((Node)node);
        Construct constructor = this.getConstructor((Node)node);
        Object data = this.constructedObjects.containsKey((Object)node) ? this.constructedObjects.get((Object)node) : constructor.construct((Node)node);
        this.finalizeConstruction((Node)node, (Object)data);
        this.constructedObjects.put((Node)node, (Object)data);
        this.recursiveObjects.remove((Object)node);
        if (!node.isTwoStepsConstruction()) return data;
        constructor.construct2ndStep((Node)node, (Object)data);
        return data;
    }

    protected Construct getConstructor(Node node) {
        String prefix;
        if (node.useClassConstructor()) {
            return this.yamlClassConstructors.get((Object)((Object)node.getNodeId()));
        }
        Construct constructor = this.yamlConstructors.get((Object)node.getTag());
        if (constructor != null) return constructor;
        Iterator<String> iterator = this.yamlMultiConstructors.keySet().iterator();
        do {
            if (!iterator.hasNext()) return this.yamlConstructors.get(null);
            prefix = iterator.next();
        } while (!node.getTag().startsWith((String)prefix));
        return this.yamlMultiConstructors.get((Object)prefix);
    }

    protected String constructScalar(ScalarNode node) {
        return node.getValue();
    }

    protected List<Object> createDefaultList(int initSize) {
        return new ArrayList<Object>((int)initSize);
    }

    protected Set<Object> createDefaultSet(int initSize) {
        return new LinkedHashSet<Object>((int)initSize);
    }

    protected Map<Object, Object> createDefaultMap(int initSize) {
        return new LinkedHashMap<Object, Object>((int)initSize);
    }

    protected Object createArray(Class<?> type, int size) {
        return Array.newInstance(type.getComponentType(), (int)size);
    }

    protected Object finalizeConstruction(Node node, Object data) {
        Class<? extends Object> type = node.getType();
        if (!this.typeDefinitions.containsKey(type)) return data;
        return this.typeDefinitions.get(type).finalizeConstruction((Object)data);
    }

    protected Object newInstance(Node node) {
        try {
            return this.newInstance(Object.class, (Node)node);
        }
        catch (InstantiationException e) {
            throw new YAMLException((Throwable)e);
        }
    }

    protected final Object newInstance(Class<?> ancestor, Node node) throws InstantiationException {
        return this.newInstance(ancestor, (Node)node, (boolean)true);
    }

    protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) throws InstantiationException {
        TypeDescription td;
        Object instance;
        Class<? extends Object> type = node.getType();
        if (this.typeDefinitions.containsKey(type) && (instance = (td = this.typeDefinitions.get(type)).newInstance((Node)node)) != null) {
            return instance;
        }
        if (!tryDefault) throw new InstantiationException();
        if (!ancestor.isAssignableFrom(type)) throw new InstantiationException();
        if (Modifier.isAbstract((int)type.getModifiers())) throw new InstantiationException();
        try {
            Constructor<? extends Object> c = type.getDeclaredConstructor(new Class[0]);
            c.setAccessible((boolean)true);
            return c.newInstance((Object[])new Object[0]);
        }
        catch (NoSuchMethodException e) {
            throw new InstantiationException((String)("NoSuchMethodException:" + e.getLocalizedMessage()));
        }
        catch (Exception e) {
            throw new YAMLException((Throwable)e);
        }
    }

    protected Set<Object> newSet(CollectionNode<?> node) {
        try {
            return (Set)this.newInstance(Set.class, node);
        }
        catch (InstantiationException e) {
            return this.createDefaultSet((int)node.getValue().size());
        }
    }

    protected List<Object> newList(SequenceNode node) {
        try {
            return (List)this.newInstance(List.class, (Node)node);
        }
        catch (InstantiationException e) {
            return this.createDefaultList((int)node.getValue().size());
        }
    }

    protected Map<Object, Object> newMap(MappingNode node) {
        try {
            return (Map)this.newInstance(Map.class, (Node)node);
        }
        catch (InstantiationException e) {
            return this.createDefaultMap((int)node.getValue().size());
        }
    }

    protected List<? extends Object> constructSequence(SequenceNode node) {
        List<Object> result = this.newList((SequenceNode)node);
        this.constructSequenceStep2((SequenceNode)node, result);
        return result;
    }

    protected Set<? extends Object> constructSet(SequenceNode node) {
        Set<Object> result = this.newSet(node);
        this.constructSequenceStep2((SequenceNode)node, result);
        return result;
    }

    protected Object constructArray(SequenceNode node) {
        return this.constructArrayStep2((SequenceNode)node, (Object)this.createArray(node.getType(), (int)node.getValue().size()));
    }

    protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
        Iterator<Node> iterator = node.getValue().iterator();
        while (iterator.hasNext()) {
            Node child = iterator.next();
            collection.add((Object)this.constructObject((Node)child));
        }
    }

    protected Object constructArrayStep2(SequenceNode node, Object array) {
        Class<?> componentType = node.getType().getComponentType();
        int index = 0;
        Iterator<Node> iterator = node.getValue().iterator();
        while (iterator.hasNext()) {
            Node child = iterator.next();
            if (child.getType() == Object.class) {
                child.setType(componentType);
            }
            Object value = this.constructObject((Node)child);
            if (componentType.isPrimitive()) {
                if (value == null) {
                    throw new NullPointerException((String)("Unable to construct element value for " + child));
                }
                if (Byte.TYPE.equals(componentType)) {
                    Array.setByte((Object)array, (int)index, (byte)((Number)value).byteValue());
                } else if (Short.TYPE.equals(componentType)) {
                    Array.setShort((Object)array, (int)index, (short)((Number)value).shortValue());
                } else if (Integer.TYPE.equals(componentType)) {
                    Array.setInt((Object)array, (int)index, (int)((Number)value).intValue());
                } else if (Long.TYPE.equals(componentType)) {
                    Array.setLong((Object)array, (int)index, (long)((Number)value).longValue());
                } else if (Float.TYPE.equals(componentType)) {
                    Array.setFloat((Object)array, (int)index, (float)((Number)value).floatValue());
                } else if (Double.TYPE.equals(componentType)) {
                    Array.setDouble((Object)array, (int)index, (double)((Number)value).doubleValue());
                } else if (Character.TYPE.equals(componentType)) {
                    Array.setChar((Object)array, (int)index, (char)((Character)value).charValue());
                } else {
                    if (!Boolean.TYPE.equals(componentType)) throw new YAMLException((String)"unexpected primitive type");
                    Array.setBoolean((Object)array, (int)index, (boolean)((Boolean)value).booleanValue());
                }
            } else {
                Array.set((Object)array, (int)index, (Object)value);
            }
            ++index;
        }
        return array;
    }

    protected Set<Object> constructSet(MappingNode node) {
        Set<Object> set = this.newSet(node);
        this.constructSet2ndStep((MappingNode)node, set);
        return set;
    }

    protected Map<Object, Object> constructMapping(MappingNode node) {
        Map<Object, Object> mapping = this.newMap((MappingNode)node);
        this.constructMapping2ndStep((MappingNode)node, mapping);
        return mapping;
    }

    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        List<NodeTuple> nodeValue = node.getValue();
        Iterator<NodeTuple> iterator = nodeValue.iterator();
        while (iterator.hasNext()) {
            NodeTuple tuple = iterator.next();
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();
            Object key = this.constructObject((Node)keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException((String)"while constructing a mapping", (Mark)node.getStartMark(), (String)("found unacceptable key " + key), (Mark)tuple.getKeyNode().getStartMark(), (Throwable)e);
                }
            }
            Object value = this.constructObject((Node)valueNode);
            if (keyNode.isTwoStepsConstruction()) {
                this.postponeMapFilling(mapping, (Object)key, (Object)value);
                continue;
            }
            mapping.put((Object)key, (Object)value);
        }
    }

    protected void postponeMapFilling(Map<Object, Object> mapping, Object key, Object value) {
        this.maps2fill.add((int)0, new RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>(mapping, new RecursiveTuple<Object, Object>(key, value)));
    }

    protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
        List<NodeTuple> nodeValue = node.getValue();
        Iterator<NodeTuple> iterator = nodeValue.iterator();
        while (iterator.hasNext()) {
            NodeTuple tuple = iterator.next();
            Node keyNode = tuple.getKeyNode();
            Object key = this.constructObject((Node)keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException((String)"while constructing a Set", (Mark)node.getStartMark(), (String)("found unacceptable key " + key), (Mark)tuple.getKeyNode().getStartMark(), (Throwable)e);
                }
            }
            if (keyNode.isTwoStepsConstruction()) {
                this.postponeSetFilling(set, (Object)key);
                continue;
            }
            set.add((Object)key);
        }
    }

    protected void postponeSetFilling(Set<Object> set, Object key) {
        this.sets2fill.add((int)0, new RecursiveTuple<Set<Object>, Object>(set, key));
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
        this.explicitPropertyUtils = true;
        Collection<TypeDescription> tds = this.typeDefinitions.values();
        Iterator<TypeDescription> iterator = tds.iterator();
        while (iterator.hasNext()) {
            TypeDescription typeDescription = iterator.next();
            typeDescription.setPropertyUtils((PropertyUtils)propertyUtils);
        }
    }

    public final PropertyUtils getPropertyUtils() {
        if (this.propertyUtils != null) return this.propertyUtils;
        this.propertyUtils = new PropertyUtils();
        return this.propertyUtils;
    }

    public TypeDescription addTypeDescription(TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException((String)"TypeDescription is required.");
        }
        Tag tag = definition.getTag();
        this.typeTags.put((Tag)tag, definition.getType());
        definition.setPropertyUtils((PropertyUtils)this.getPropertyUtils());
        return this.typeDefinitions.put(definition.getType(), (TypeDescription)definition);
    }

    public final boolean isExplicitPropertyUtils() {
        return this.explicitPropertyUtils;
    }

    public boolean isAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }

    public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
    }

    public boolean isWrappedToRootException() {
        return this.wrappedToRootException;
    }

    public void setWrappedToRootException(boolean wrappedToRootException) {
        this.wrappedToRootException = wrappedToRootException;
    }
}

