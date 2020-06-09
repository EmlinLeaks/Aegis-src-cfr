/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.CombinedIterator;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.group.DefaultChannelGroupFuture;
import io.netty.channel.group.VoidChannelGroupFuture;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup
extends AbstractSet<Channel>
implements ChannelGroup {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<ChannelId, Channel> serverChannels = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<ChannelId, Channel> nonServerChannels = PlatformDependent.newConcurrentHashMap();
    private final ChannelFutureListener remover = new ChannelFutureListener((DefaultChannelGroup)this){
        final /* synthetic */ DefaultChannelGroup this$0;
        {
            this.this$0 = this$0;
        }

        public void operationComplete(ChannelFuture future) throws java.lang.Exception {
            this.this$0.remove((Object)future.channel());
        }
    };
    private final VoidChannelGroupFuture voidFuture = new VoidChannelGroupFuture((ChannelGroup)this);
    private final boolean stayClosed;
    private volatile boolean closed;

    public DefaultChannelGroup(EventExecutor executor) {
        this((EventExecutor)executor, (boolean)false);
    }

    public DefaultChannelGroup(String name, EventExecutor executor) {
        this((String)name, (EventExecutor)executor, (boolean)false);
    }

    public DefaultChannelGroup(EventExecutor executor, boolean stayClosed) {
        this((String)("group-0x" + Integer.toHexString((int)nextId.incrementAndGet())), (EventExecutor)executor, (boolean)stayClosed);
    }

    public DefaultChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        this.name = name;
        this.executor = executor;
        this.stayClosed = stayClosed;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Channel find(ChannelId id) {
        Channel c = (Channel)this.nonServerChannels.get((Object)id);
        if (c == null) return (Channel)this.serverChannels.get((Object)id);
        return c;
    }

    @Override
    public boolean isEmpty() {
        if (!this.nonServerChannels.isEmpty()) return false;
        if (!this.serverChannels.isEmpty()) return false;
        return true;
    }

    @Override
    public int size() {
        return this.nonServerChannels.size() + this.serverChannels.size();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof ServerChannel) {
            return this.serverChannels.containsValue((Object)o);
        }
        if (!(o instanceof Channel)) return false;
        return this.nonServerChannels.containsValue((Object)o);
    }

    @Override
    public boolean add(Channel channel) {
        boolean added;
        ConcurrentMap<ChannelId, Channel> map = channel instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
        boolean bl = added = map.putIfAbsent((ChannelId)channel.id(), (Channel)channel) == null;
        if (added) {
            channel.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)this.remover);
        }
        if (!this.stayClosed) return added;
        if (!this.closed) return added;
        channel.close();
        return added;
    }

    @Override
    public boolean remove(Object o) {
        Channel c = null;
        if (o instanceof ChannelId) {
            c = (Channel)this.nonServerChannels.remove((Object)o);
            if (c == null) {
                c = (Channel)this.serverChannels.remove((Object)o);
            }
        } else if (o instanceof Channel) {
            c = (Channel)o;
            c = c instanceof ServerChannel ? (Channel)this.serverChannels.remove((Object)c.id()) : (Channel)this.nonServerChannels.remove((Object)c.id());
        }
        if (c == null) {
            return false;
        }
        c.closeFuture().removeListener((GenericFutureListener<? extends Future<? super Void>>)this.remover);
        return true;
    }

    @Override
    public void clear() {
        this.nonServerChannels.clear();
        this.serverChannels.clear();
    }

    @Override
    public Iterator<Channel> iterator() {
        return new CombinedIterator<Channel>(this.serverChannels.values().iterator(), this.nonServerChannels.values().iterator());
    }

    @Override
    public Object[] toArray() {
        ArrayList<V> channels = new ArrayList<V>((int)this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ArrayList<V> channels = new ArrayList<V>((int)this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray(a);
    }

    @Override
    public ChannelGroupFuture close() {
        return this.close((ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect() {
        return this.disconnect((ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture deregister() {
        return this.deregister((ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture write(Object message) {
        return this.write((Object)message, (ChannelMatcher)ChannelMatchers.all());
    }

    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).retainedDuplicate();
        }
        if (!(message instanceof ByteBufHolder)) return ReferenceCountUtil.retain(message);
        return ((ByteBufHolder)message).retainedDuplicate();
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
        return this.write((Object)message, (ChannelMatcher)matcher, (boolean)false);
    }

    @Override
    public ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise) {
        ChannelGroupFuture future;
        if (message == null) {
            throw new NullPointerException((String)"message");
        }
        if (matcher == null) {
            throw new NullPointerException((String)"matcher");
        }
        if (voidPromise) {
            for (Channel c : this.nonServerChannels.values()) {
                if (!matcher.matches((Channel)c)) continue;
                c.write((Object)DefaultChannelGroup.safeDuplicate((Object)message), (ChannelPromise)c.voidPromise());
            }
            future = this.voidFuture;
        } else {
            LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
            for (Channel c : this.nonServerChannels.values()) {
                if (!matcher.matches((Channel)c)) continue;
                futures.put((Channel)c, (ChannelFuture)c.write((Object)DefaultChannelGroup.safeDuplicate((Object)message)));
            }
            future = new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
        }
        ReferenceCountUtil.release((Object)message);
        return future;
    }

    @Override
    public ChannelGroup flush() {
        return this.flush((ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message) {
        return this.writeAndFlush((Object)message);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message) {
        return this.writeAndFlush((Object)message, (ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException((String)"matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
        for (Channel c : this.serverChannels.values()) {
            if (!matcher.matches((Channel)c)) continue;
            futures.put(c, c.disconnect());
        }
        Iterator<V> iterator = this.nonServerChannels.values().iterator();
        while (iterator.hasNext()) {
            Channel c;
            c = (Channel)iterator.next();
            if (!matcher.matches((Channel)c)) continue;
            futures.put((Channel)c, (ChannelFuture)c.disconnect());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException((String)"matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
        if (this.stayClosed) {
            this.closed = true;
        }
        for (Channel c : this.serverChannels.values()) {
            if (!matcher.matches((Channel)c)) continue;
            futures.put(c, c.close());
        }
        Iterator<V> iterator = this.nonServerChannels.values().iterator();
        while (iterator.hasNext()) {
            Channel c;
            c = (Channel)iterator.next();
            if (!matcher.matches((Channel)c)) continue;
            futures.put((Channel)c, (ChannelFuture)c.close());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
    }

    @Override
    public ChannelGroupFuture deregister(ChannelMatcher matcher) {
        if (matcher == null) {
            throw new NullPointerException((String)"matcher");
        }
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
        for (Channel c : this.serverChannels.values()) {
            if (!matcher.matches((Channel)c)) continue;
            futures.put(c, c.deregister());
        }
        Iterator<V> iterator = this.nonServerChannels.values().iterator();
        while (iterator.hasNext()) {
            Channel c;
            c = (Channel)iterator.next();
            if (!matcher.matches((Channel)c)) continue;
            futures.put((Channel)c, (ChannelFuture)c.deregister());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
    }

    @Override
    public ChannelGroup flush(ChannelMatcher matcher) {
        Iterator<V> iterator = this.nonServerChannels.values().iterator();
        while (iterator.hasNext()) {
            Channel c = (Channel)iterator.next();
            if (!matcher.matches((Channel)c)) continue;
            c.flush();
        }
        return this;
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
        return this.writeAndFlush((Object)message, (ChannelMatcher)matcher);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
        return this.writeAndFlush((Object)message, (ChannelMatcher)matcher, (boolean)false);
    }

    @Override
    public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
        ChannelGroupFuture future;
        if (message == null) {
            throw new NullPointerException((String)"message");
        }
        if (voidPromise) {
            for (Channel c : this.nonServerChannels.values()) {
                if (!matcher.matches((Channel)c)) continue;
                c.writeAndFlush((Object)DefaultChannelGroup.safeDuplicate((Object)message), (ChannelPromise)c.voidPromise());
            }
            future = this.voidFuture;
        } else {
            LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
            for (Channel c : this.nonServerChannels.values()) {
                if (!matcher.matches((Channel)c)) continue;
                futures.put((Channel)c, (ChannelFuture)c.writeAndFlush((Object)DefaultChannelGroup.safeDuplicate((Object)message)));
            }
            future = new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
        }
        ReferenceCountUtil.release((Object)message);
        return future;
    }

    @Override
    public ChannelGroupFuture newCloseFuture() {
        return this.newCloseFuture((ChannelMatcher)ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture newCloseFuture(ChannelMatcher matcher) {
        LinkedHashMap<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>((int)this.size());
        for (Channel c : this.serverChannels.values()) {
            if (!matcher.matches((Channel)c)) continue;
            futures.put(c, c.closeFuture());
        }
        Iterator<V> iterator = this.nonServerChannels.values().iterator();
        while (iterator.hasNext()) {
            Channel c;
            c = (Channel)iterator.next();
            if (!matcher.matches((Channel)c)) continue;
            futures.put((Channel)c, (ChannelFuture)c.closeFuture());
        }
        return new DefaultChannelGroupFuture((ChannelGroup)this, futures, (EventExecutor)this.executor);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode((Object)this);
    }

    @Override
    public boolean equals(Object o) {
        if (this != o) return false;
        return true;
    }

    @Override
    public int compareTo(ChannelGroup o) {
        int v = this.name().compareTo((String)o.name());
        if (v == 0) return System.identityHashCode((Object)this) - System.identityHashCode((Object)o);
        return v;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName((Object)this) + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}

