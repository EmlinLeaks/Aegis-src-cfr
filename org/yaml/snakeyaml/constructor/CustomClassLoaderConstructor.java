/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.constructor.Constructor;

public class CustomClassLoaderConstructor
extends Constructor {
    private ClassLoader loader = CustomClassLoaderConstructor.class.getClassLoader();

    public CustomClassLoaderConstructor(ClassLoader cLoader) {
        this(Object.class, (ClassLoader)cLoader);
    }

    public CustomClassLoaderConstructor(Class<? extends Object> theRoot, ClassLoader theLoader) {
        super(theRoot);
        if (theLoader == null) {
            throw new NullPointerException((String)"Loader must be provided.");
        }
        this.loader = theLoader;
    }

    @Override
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName((String)name, (boolean)true, (ClassLoader)this.loader);
    }
}

