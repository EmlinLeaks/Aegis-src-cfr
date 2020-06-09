/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class Constructor
extends SafeConstructor {
    public Constructor() {
        this(Object.class);
    }

    public Constructor(Class<? extends Object> theRoot) {
        this((TypeDescription)new TypeDescription(Constructor.checkRoot(theRoot)));
    }

    private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
        if (theRoot != null) return theRoot;
        throw new NullPointerException((String)"Root class must be provided.");
    }

    public Constructor(TypeDescription theRoot) {
        this((TypeDescription)theRoot, null);
    }

    public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs) {
        if (theRoot == null) {
            throw new NullPointerException((String)"Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstructYamlObject((Constructor)this));
        if (!Object.class.equals(theRoot.getType())) {
            this.rootTag = new Tag(theRoot.getType());
        }
        this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar((Constructor)this));
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping((Constructor)this));
        this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence((Constructor)this));
        this.addTypeDescription((TypeDescription)theRoot);
        if (moreTDs == null) return;
        Iterator<TypeDescription> iterator = moreTDs.iterator();
        while (iterator.hasNext()) {
            TypeDescription td = iterator.next();
            this.addTypeDescription((TypeDescription)td);
        }
    }

    public Constructor(String theRoot) throws ClassNotFoundException {
        this(Class.forName((String)Constructor.check((String)theRoot)));
    }

    private static final String check(String s) {
        if (s == null) {
            throw new NullPointerException((String)"Root type must be provided.");
        }
        if (s.trim().length() != 0) return s;
        throw new YAMLException((String)"Root type must be provided.");
    }

    protected Class<?> getClassForNode(Node node) {
        Class<?> cl;
        Class classForTag = (Class)this.typeTags.get((Object)node.getTag());
        if (classForTag != null) return classForTag;
        String name = node.getTag().getClassName();
        try {
            cl = this.getClassForName((String)name);
        }
        catch (ClassNotFoundException e) {
            throw new YAMLException((String)("Class not found: " + name));
        }
        this.typeTags.put(node.getTag(), cl);
        return cl;
    }

    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName((String)name, (boolean)true, (ClassLoader)Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e) {
            return Class.forName((String)name);
        }
    }
}

