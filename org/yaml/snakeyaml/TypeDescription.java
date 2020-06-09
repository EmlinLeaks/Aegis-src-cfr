/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public class TypeDescription {
    private static final Logger log = Logger.getLogger((String)TypeDescription.class.getPackage().getName());
    private final Class<? extends Object> type;
    private Class<?> impl;
    private Tag tag;
    private transient Set<Property> dumpProperties;
    private transient PropertyUtils propertyUtils;
    private transient boolean delegatesChecked;
    private Map<String, PropertySubstitute> properties = Collections.emptyMap();
    protected Set<String> excludes = Collections.emptySet();
    protected String[] includes = null;
    protected BeanAccess beanAccess;

    public TypeDescription(Class<? extends Object> clazz, Tag tag) {
        this(clazz, (Tag)tag, null);
    }

    public TypeDescription(Class<? extends Object> clazz, Tag tag, Class<?> impl) {
        this.type = clazz;
        this.tag = tag;
        this.impl = impl;
        this.beanAccess = null;
    }

    public TypeDescription(Class<? extends Object> clazz, String tag) {
        this(clazz, (Tag)new Tag((String)tag), null);
    }

    public TypeDescription(Class<? extends Object> clazz) {
        this(clazz, (Tag)((Tag)null), null);
    }

    public TypeDescription(Class<? extends Object> clazz, Class<?> impl) {
        this(clazz, null, impl);
    }

    public Tag getTag() {
        return this.tag;
    }

    @Deprecated
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Deprecated
    public void setTag(String tag) {
        this.setTag((Tag)new Tag((String)tag));
    }

    public Class<? extends Object> getType() {
        return this.type;
    }

    @Deprecated
    public void putListPropertyType(String property, Class<? extends Object> type) {
        this.addPropertyParameters((String)property, type);
    }

    @Deprecated
    public Class<? extends Object> getListPropertyType(String property) {
        if (!this.properties.containsKey((Object)property)) return null;
        Class<?>[] typeArguments = this.properties.get((Object)property).getActualTypeArguments();
        if (typeArguments == null) return null;
        if (typeArguments.length <= 0) return null;
        return typeArguments[0];
    }

    @Deprecated
    public void putMapPropertyType(String property, Class<? extends Object> key, Class<? extends Object> value) {
        this.addPropertyParameters((String)property, key, value);
    }

    @Deprecated
    public Class<? extends Object> getMapKeyType(String property) {
        if (!this.properties.containsKey((Object)property)) return null;
        Class<?>[] typeArguments = this.properties.get((Object)property).getActualTypeArguments();
        if (typeArguments == null) return null;
        if (typeArguments.length <= 0) return null;
        return typeArguments[0];
    }

    @Deprecated
    public Class<? extends Object> getMapValueType(String property) {
        if (!this.properties.containsKey((Object)property)) return null;
        Class<?>[] typeArguments = this.properties.get((Object)property).getActualTypeArguments();
        if (typeArguments == null) return null;
        if (typeArguments.length <= 1) return null;
        return typeArguments[1];
    }

    public void addPropertyParameters(String pName, Class<?> ... classes) {
        if (!this.properties.containsKey((Object)pName)) {
            this.substituteProperty((String)pName, null, null, null, classes);
            return;
        }
        PropertySubstitute pr = this.properties.get((Object)pName);
        pr.setActualTypeArguments(classes);
    }

    public String toString() {
        return "TypeDescription for " + this.getType() + " (tag='" + this.getTag() + "')";
    }

    private void checkDelegates() {
        Collection<PropertySubstitute> values = this.properties.values();
        Iterator<PropertySubstitute> iterator = values.iterator();
        do {
            if (!iterator.hasNext()) {
                this.delegatesChecked = true;
                return;
            }
            PropertySubstitute p = iterator.next();
            try {
                p.setDelegate((Property)this.discoverProperty((String)p.getName()));
            }
            catch (YAMLException yAMLException) {
            }
        } while (true);
    }

    private Property discoverProperty(String name) {
        if (this.propertyUtils == null) return null;
        if (this.beanAccess != null) return this.propertyUtils.getProperty(this.type, (String)name, (BeanAccess)this.beanAccess);
        return this.propertyUtils.getProperty(this.type, (String)name);
    }

    public Property getProperty(String name) {
        Property property;
        if (!this.delegatesChecked) {
            this.checkDelegates();
        }
        if (this.properties.containsKey((Object)name)) {
            property = (Property)this.properties.get((Object)name);
            return property;
        }
        property = this.discoverProperty((String)name);
        return property;
    }

    public void substituteProperty(String pName, Class<?> pType, String getter, String setter, Class<?> ... argParams) {
        this.substituteProperty((PropertySubstitute)new PropertySubstitute((String)pName, pType, (String)getter, (String)setter, argParams));
    }

    public void substituteProperty(PropertySubstitute substitute) {
        if (Collections.EMPTY_MAP == this.properties) {
            this.properties = new LinkedHashMap<String, PropertySubstitute>();
        }
        substitute.setTargetType(this.type);
        this.properties.put((String)substitute.getName(), (PropertySubstitute)substitute);
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
    }

    public void setIncludes(String ... propNames) {
        this.includes = propNames != null && propNames.length > 0 ? propNames : null;
    }

    public void setExcludes(String ... propNames) {
        if (propNames != null && propNames.length > 0) {
            this.excludes = new HashSet<String>();
            String[] arrstring = propNames;
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String name = arrstring[n2];
                this.excludes.add((String)name);
                ++n2;
            }
            return;
        }
        this.excludes = Collections.emptySet();
    }

    public Set<Property> getProperties() {
        Set<Property> readableProps;
        if (this.dumpProperties != null) {
            return this.dumpProperties;
        }
        if (this.propertyUtils == null) return null;
        if (this.includes != null) {
            this.dumpProperties = new LinkedHashSet<Property>();
            String[] arrstring = this.includes;
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String propertyName = arrstring[n2];
                if (!this.excludes.contains((Object)propertyName)) {
                    this.dumpProperties.add((Property)this.getProperty((String)propertyName));
                }
                ++n2;
            }
            return this.dumpProperties;
        }
        Set<Property> set = readableProps = this.beanAccess == null ? this.propertyUtils.getProperties(this.type) : this.propertyUtils.getProperties(this.type, (BeanAccess)this.beanAccess);
        if (this.properties.isEmpty()) {
            if (this.excludes.isEmpty()) {
                this.dumpProperties = readableProps;
                return this.dumpProperties;
            }
            this.dumpProperties = new LinkedHashSet<Property>();
            Iterator<Property> iterator = readableProps.iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();
                if (this.excludes.contains((Object)property.getName())) continue;
                this.dumpProperties.add((Property)property);
            }
            return this.dumpProperties;
        }
        if (!this.delegatesChecked) {
            this.checkDelegates();
        }
        this.dumpProperties = new LinkedHashSet<Property>();
        for (Property property : this.properties.values()) {
            if (this.excludes.contains((Object)property.getName()) || !property.isReadable()) continue;
            this.dumpProperties.add((Property)property);
        }
        Iterator<Property> iterator = readableProps.iterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            if (this.excludes.contains((Object)property.getName())) continue;
            this.dumpProperties.add((Property)property);
        }
        return this.dumpProperties;
    }

    public boolean setupPropertyType(String key, Node valueNode) {
        return false;
    }

    public boolean setProperty(Object targetBean, String propertyName, Object value) throws Exception {
        return false;
    }

    public Object newInstance(Node node) {
        if (this.impl == null) return null;
        try {
            Constructor<?> c = this.impl.getDeclaredConstructor(new Class[0]);
            c.setAccessible((boolean)true);
            return c.newInstance((Object[])new Object[0]);
        }
        catch (Exception e) {
            log.fine((String)e.getLocalizedMessage());
            this.impl = null;
        }
        return null;
    }

    public Object newInstance(String propertyName, Node node) {
        return null;
    }

    public Object finalizeConstruction(Object obj) {
        return obj;
    }
}

