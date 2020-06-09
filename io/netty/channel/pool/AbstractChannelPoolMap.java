/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.pool;

import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool>
implements ChannelPoolMap<K, P>,
Iterable<Map.Entry<K, P>>,
Closeable {
    private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();

    @Override
    public final P get(K key) {
        ChannelPool pool = (ChannelPool)this.map.get(ObjectUtil.checkNotNull(key, (String)"key"));
        if (pool != null) return (P)pool;
        pool = this.newPool(key);
        ChannelPool old = this.map.putIfAbsent(key, pool);
        if (old == null) return (P)pool;
        pool.close();
        pool = old;
        return (P)pool;
    }

    public final boolean remove(K key) {
        ChannelPool pool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, (String)"key"));
        if (pool == null) return false;
        pool.close();
        return true;
    }

    @Override
    public final Iterator<Map.Entry<K, P>> iterator() {
        return new ReadOnlyIterator<Map.Entry<K, P>>(this.map.entrySet().iterator());
    }

    public final int size() {
        return this.map.size();
    }

    public final boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public final boolean contains(K key) {
        return this.map.containsKey(ObjectUtil.checkNotNull(key, (String)"key"));
    }

    protected abstract P newPool(K var1);

    @Override
    public final void close() {
        Iterator<K> iterator = this.map.keySet().iterator();
        while (iterator.hasNext()) {
            K key = iterator.next();
            this.remove(key);
        }
    }
}

