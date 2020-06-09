/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;

class ClassLoaderClassResolver
implements ClassResolver {
    private final ClassLoader classLoader;

    ClassLoaderClassResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> resolve(String className) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass((String)className);
        }
        catch (ClassNotFoundException ignored) {
            return Class.forName((String)className, (boolean)false, (ClassLoader)this.classLoader);
        }
    }
}

