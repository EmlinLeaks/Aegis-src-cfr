/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Deprecated
public final class ConcurrentSet<E>
extends AbstractSet<E>
implements Serializable {
    private static final long serialVersionUID = -6761513279741915432L;
    private final ConcurrentMap<E, Boolean> map = PlatformDependent.newConcurrentHashMap();

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey((Object)o);
    }

    @Override
    public boolean add(E o) {
        if (this.map.putIfAbsent(o, (Boolean)Boolean.TRUE) != null) return false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (this.map.remove((Object)o) == null) return false;
        return true;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}

