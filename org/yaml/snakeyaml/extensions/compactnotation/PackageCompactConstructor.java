/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.extensions.compactnotation.CompactConstructor;

public class PackageCompactConstructor
extends CompactConstructor {
    private String packageName;

    public PackageCompactConstructor(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf((int)46) >= 0) return super.getClassForName((String)name);
        try {
            return Class.forName((String)(this.packageName + "." + name));
        }
        catch (ClassNotFoundException clazz) {
            // empty catch block
        }
        return super.getClassForName((String)name);
    }
}

