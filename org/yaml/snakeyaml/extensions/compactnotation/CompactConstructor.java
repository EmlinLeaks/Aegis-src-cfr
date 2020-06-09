/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.extensions.compactnotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.extensions.compactnotation.CompactConstructor;
import org.yaml.snakeyaml.extensions.compactnotation.CompactData;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class CompactConstructor
extends Constructor {
    private static final Pattern GUESS_COMPACT = Pattern.compile((String)"\\p{Alpha}.*\\s*\\((?:,?\\s*(?:(?:\\w*)|(?:\\p{Alpha}\\w*\\s*=.+))\\s*)+\\)");
    private static final Pattern FIRST_PATTERN = Pattern.compile((String)"(\\p{Alpha}.*)(\\s*)\\((.*?)\\)");
    private static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile((String)"\\s*(\\p{Alpha}\\w*)\\s*=(.+)");
    private Construct compactConstruct;

    protected Object constructCompactFormat(ScalarNode node, CompactData data) {
        try {
            Object obj = this.createInstance((ScalarNode)node, (CompactData)data);
            HashMap<String, Object> properties = new HashMap<String, Object>(data.getProperties());
            this.setProperties((Object)obj, properties);
            return obj;
        }
        catch (Exception e) {
            throw new YAMLException((Throwable)e);
        }
    }

    protected Object createInstance(ScalarNode node, CompactData data) throws Exception {
        Class<?> clazz = this.getClassForName((String)data.getPrefix());
        Class[] args = new Class[data.getArguments().size()];
        int i = 0;
        do {
            if (i >= args.length) {
                java.lang.reflect.Constructor<?> c = clazz.getDeclaredConstructor(args);
                c.setAccessible((boolean)true);
                return c.newInstance((Object[])data.getArguments().toArray());
            }
            args[i] = String.class;
            ++i;
        } while (true);
    }

    protected void setProperties(Object bean, Map<String, Object> data) throws Exception {
        if (data == null) {
            throw new NullPointerException((String)"Data for Compact Object Notation cannot be null.");
        }
        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Property property = this.getPropertyUtils().getProperty(bean.getClass(), (String)key);
            try {
                property.set((Object)bean, (Object)entry.getValue());
            }
            catch (IllegalArgumentException e) {
                throw new YAMLException((String)("Cannot set property='" + key + "' with value='" + data.get((Object)key) + "' (" + data.get((Object)key).getClass() + ") in " + bean));
            }
        }
    }

    public CompactData getCompactData(String scalar) {
        if (!scalar.endsWith((String)")")) {
            return null;
        }
        if (scalar.indexOf((int)40) < 0) {
            return null;
        }
        Matcher m = FIRST_PATTERN.matcher((CharSequence)scalar);
        if (!m.matches()) return null;
        String tag = m.group((int)1).trim();
        String content = m.group((int)3);
        CompactData data = new CompactData((String)tag);
        if (content.length() == 0) {
            return data;
        }
        String[] names = content.split((String)"\\s*,\\s*");
        int i = 0;
        while (i < names.length) {
            String section = names[i];
            if (section.indexOf((int)61) < 0) {
                data.getArguments().add((String)section);
            } else {
                Matcher sm = PROPERTY_NAME_PATTERN.matcher((CharSequence)section);
                if (!sm.matches()) return null;
                String name = sm.group((int)1);
                String value = sm.group((int)2).trim();
                data.getProperties().put((String)name, (String)value);
            }
            ++i;
        }
        return data;
    }

    private Construct getCompactConstruct() {
        if (this.compactConstruct != null) return this.compactConstruct;
        this.compactConstruct = this.createCompactConstruct();
        return this.compactConstruct;
    }

    protected Construct createCompactConstruct() {
        return new ConstructCompactObject((CompactConstructor)this);
    }

    @Override
    protected Construct getConstructor(Node node) {
        if (node instanceof MappingNode) {
            MappingNode mnode = (MappingNode)node;
            List<NodeTuple> list = mnode.getValue();
            if (list.size() != 1) return super.getConstructor((Node)node);
            NodeTuple tuple = list.get((int)0);
            Node key = tuple.getKeyNode();
            if (!(key instanceof ScalarNode)) return super.getConstructor((Node)node);
            ScalarNode scalar = (ScalarNode)key;
            if (!GUESS_COMPACT.matcher((CharSequence)scalar.getValue()).matches()) return super.getConstructor((Node)node);
            return this.getCompactConstruct();
        }
        if (!(node instanceof ScalarNode)) return super.getConstructor((Node)node);
        ScalarNode scalar = (ScalarNode)node;
        if (!GUESS_COMPACT.matcher((CharSequence)scalar.getValue()).matches()) return super.getConstructor((Node)node);
        return this.getCompactConstruct();
    }

    protected void applySequence(Object bean, List<?> value) {
        try {
            Property property = this.getPropertyUtils().getProperty(bean.getClass(), (String)this.getSequencePropertyName(bean.getClass()));
            property.set((Object)bean, value);
            return;
        }
        catch (Exception e) {
            throw new YAMLException((Throwable)e);
        }
    }

    protected String getSequencePropertyName(Class<?> bean) {
        Set<Property> properties = this.getPropertyUtils().getProperties(bean);
        Iterator<Property> iterator = properties.iterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            if (List.class.isAssignableFrom(property.getType())) continue;
            iterator.remove();
        }
        if (properties.size() == 0) {
            throw new YAMLException((String)("No list property found in " + bean));
        }
        if (properties.size() <= 1) return properties.iterator().next().getName();
        throw new YAMLException((String)("Many list properties found in " + bean + "; Please override getSequencePropertyName() to specify which property to use."));
    }

    static /* synthetic */ List access$000(CompactConstructor x0, SequenceNode x1) {
        return x0.constructSequence((SequenceNode)x1);
    }

    static /* synthetic */ String access$100(CompactConstructor x0, ScalarNode x1) {
        return x0.constructScalar((ScalarNode)x1);
    }
}

