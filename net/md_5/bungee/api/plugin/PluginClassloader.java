/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginClassloader
extends URLClassLoader {
    private static final Set<PluginClassloader> allLoaders = new CopyOnWriteArraySet<PluginClassloader>();

    public PluginClassloader(URL[] urls) {
        super((URL[])urls);
        allLoaders.add((PluginClassloader)this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return this.loadClass0((String)name, (boolean)resolve, (boolean)true);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass((String)name, (boolean)resolve);
        }
        catch (ClassNotFoundException classNotFoundException) {
            if (!checkOther) throw new ClassNotFoundException((String)name);
            Iterator<PluginClassloader> iterator = allLoaders.iterator();
            while (iterator.hasNext()) {
                PluginClassloader loader = iterator.next();
                if (loader == this) continue;
                try {
                    return loader.loadClass0((String)name, (boolean)resolve, (boolean)false);
                }
                catch (ClassNotFoundException classNotFoundException2) {
                }
            }
            throw new ClassNotFoundException((String)name);
        }
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}

