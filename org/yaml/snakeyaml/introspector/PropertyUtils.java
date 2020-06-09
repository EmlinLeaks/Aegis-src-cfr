/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.introspector;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.FieldProperty;
import org.yaml.snakeyaml.introspector.MethodProperty;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.util.PlatformFeatureDetector;

public class PropertyUtils {
    private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<Class<?>, Map<String, Property>>();
    private final Map<Class<?>, Set<Property>> readableProperties = new HashMap<Class<?>, Set<Property>>();
    private BeanAccess beanAccess = BeanAccess.DEFAULT;
    private boolean allowReadOnlyProperties = false;
    private boolean skipMissingProperties = false;
    private PlatformFeatureDetector platformFeatureDetector;
    private static final String TRANSIENT = "transient";

    public PropertyUtils() {
        this((PlatformFeatureDetector)new PlatformFeatureDetector());
    }

    PropertyUtils(PlatformFeatureDetector platformFeatureDetector) {
        this.platformFeatureDetector = platformFeatureDetector;
        if (!platformFeatureDetector.isRunningOnAndroid()) return;
        this.beanAccess = BeanAccess.FIELD;
    }

    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (this.propertiesCache.containsKey(type)) {
            return this.propertiesCache.get(type);
        }
        LinkedHashMap<String, Property> properties = new LinkedHashMap<String, Property>();
        boolean inaccessableFieldsExist = false;
        switch (1.$SwitchMap$org$yaml$snakeyaml$introspector$BeanAccess[bAccess.ordinal()]) {
            case 1: {
                for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isStatic((int)modifiers) || Modifier.isTransient((int)modifiers) || properties.containsKey((Object)field.getName())) continue;
                        properties.put((String)field.getName(), (Property)new FieldProperty((Field)field));
                    }
                }
                break;
            }
            default: {
                Object c;
                try {
                    c = Introspector.getBeanInfo(type).getPropertyDescriptors();
                    int n = ((PropertyDescriptor[])c).length;
                    for (int i = 0; i < n; ++i) {
                        PropertyDescriptor property = c[i];
                        Method readMethod = property.getReadMethod();
                        if (readMethod != null && readMethod.getName().equals((Object)"getClass") || this.isTransient((FeatureDescriptor)property)) continue;
                        properties.put((String)property.getName(), (Property)new MethodProperty((PropertyDescriptor)property));
                    }
                }
                catch (IntrospectionException e) {
                    throw new YAMLException((Throwable)e);
                }
                for (c = type; c != null; c = c.getSuperclass()) {
                    for (Field field : ((Class)c).getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isStatic((int)modifiers) || Modifier.isTransient((int)modifiers)) continue;
                        if (Modifier.isPublic((int)modifiers)) {
                            properties.put((String)field.getName(), (Property)new FieldProperty((Field)field));
                            continue;
                        }
                        inaccessableFieldsExist = true;
                    }
                }
            }
        }
        if (properties.isEmpty() && inaccessableFieldsExist) {
            throw new YAMLException((String)("No JavaBean properties found in " + type.getName()));
        }
        this.propertiesCache.put(type, properties);
        return properties;
    }

    private boolean isTransient(FeatureDescriptor fd) {
        return Boolean.TRUE.equals((Object)fd.getValue((String)TRANSIENT));
    }

    public Set<Property> getProperties(Class<? extends Object> type) {
        return this.getProperties(type, (BeanAccess)this.beanAccess);
    }

    public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) {
        if (this.readableProperties.containsKey(type)) {
            return this.readableProperties.get(type);
        }
        Set<Property> properties = this.createPropertySet(type, (BeanAccess)bAccess);
        this.readableProperties.put(type, properties);
        return properties;
    }

    protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
        TreeSet<Property> properties = new TreeSet<Property>();
        Collection<Property> props = this.getPropertiesMap(type, (BeanAccess)bAccess).values();
        Iterator<Property> iterator = props.iterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            if (!property.isReadable() || !this.allowReadOnlyProperties && !property.isWritable()) continue;
            properties.add((Property)property);
        }
        return properties;
    }

    public Property getProperty(Class<? extends Object> type, String name) {
        return this.getProperty(type, (String)name, (BeanAccess)this.beanAccess);
    }

    public Property getProperty(Class<? extends Object> type, String name, BeanAccess bAccess) {
        Map<String, Property> properties = this.getPropertiesMap(type, (BeanAccess)bAccess);
        Property property = properties.get((Object)name);
        if (property == null && this.skipMissingProperties) {
            property = new MissingProperty((String)name);
        }
        if (property != null) return property;
        throw new YAMLException((String)("Unable to find property '" + name + "' on class: " + type.getName()));
    }

    public void setBeanAccess(BeanAccess beanAccess) {
        if (this.platformFeatureDetector.isRunningOnAndroid() && beanAccess != BeanAccess.FIELD) {
            throw new IllegalArgumentException((String)"JVM is Android - only BeanAccess.FIELD is available");
        }
        if (this.beanAccess == beanAccess) return;
        this.beanAccess = beanAccess;
        this.propertiesCache.clear();
        this.readableProperties.clear();
    }

    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        if (this.allowReadOnlyProperties == allowReadOnlyProperties) return;
        this.allowReadOnlyProperties = allowReadOnlyProperties;
        this.readableProperties.clear();
    }

    public boolean isAllowReadOnlyProperties() {
        return this.allowReadOnlyProperties;
    }

    public void setSkipMissingProperties(boolean skipMissingProperties) {
        if (this.skipMissingProperties == skipMissingProperties) return;
        this.skipMissingProperties = skipMissingProperties;
        this.readableProperties.clear();
    }

    public boolean isSkipMissingProperties() {
        return this.skipMissingProperties;
    }
}

