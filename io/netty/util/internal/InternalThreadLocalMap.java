/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.util.internal.UnpaddedInternalThreadLocalMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class InternalThreadLocalMap
extends UnpaddedInternalThreadLocalMap {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(InternalThreadLocalMap.class);
    private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
    private static final int STRING_BUILDER_INITIAL_SIZE;
    private static final int STRING_BUILDER_MAX_SIZE;
    public static final Object UNSET;
    private BitSet cleanerFlags;
    public long rp1;
    public long rp2;
    public long rp3;
    public long rp4;
    public long rp5;
    public long rp6;
    public long rp7;
    public long rp8;
    public long rp9;

    public static InternalThreadLocalMap getIfSet() {
        Thread thread = Thread.currentThread();
        if (!(thread instanceof FastThreadLocalThread)) return (InternalThreadLocalMap)slowThreadLocalMap.get();
        return ((FastThreadLocalThread)thread).threadLocalMap();
    }

    public static InternalThreadLocalMap get() {
        Thread thread = Thread.currentThread();
        if (!(thread instanceof FastThreadLocalThread)) return InternalThreadLocalMap.slowGet();
        return InternalThreadLocalMap.fastGet((FastThreadLocalThread)((FastThreadLocalThread)thread));
    }

    private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
        InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
        if (threadLocalMap != null) return threadLocalMap;
        threadLocalMap = new InternalThreadLocalMap();
        thread.setThreadLocalMap((InternalThreadLocalMap)threadLocalMap);
        return threadLocalMap;
    }

    private static InternalThreadLocalMap slowGet() {
        ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
        InternalThreadLocalMap ret = slowThreadLocalMap.get();
        if (ret != null) return ret;
        ret = new InternalThreadLocalMap();
        slowThreadLocalMap.set((InternalThreadLocalMap)ret);
        return ret;
    }

    public static void remove() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            ((FastThreadLocalThread)thread).setThreadLocalMap(null);
            return;
        }
        slowThreadLocalMap.remove();
    }

    public static void destroy() {
        slowThreadLocalMap.remove();
    }

    public static int nextVariableIndex() {
        int index = nextIndex.getAndIncrement();
        if (index >= 0) return index;
        nextIndex.decrementAndGet();
        throw new IllegalStateException((String)"too many thread-local indexed variables");
    }

    public static int lastVariableIndex() {
        return nextIndex.get() - 1;
    }

    private InternalThreadLocalMap() {
        super((Object[])InternalThreadLocalMap.newIndexedVariableTable());
    }

    private static Object[] newIndexedVariableTable() {
        Object[] array = new Object[32];
        Arrays.fill((Object[])array, (Object)UNSET);
        return array;
    }

    public int size() {
        int count = 0;
        if (this.futureListenerStackDepth != 0) {
            ++count;
        }
        if (this.localChannelReaderStackDepth != 0) {
            ++count;
        }
        if (this.handlerSharableCache != null) {
            ++count;
        }
        if (this.counterHashCode != null) {
            ++count;
        }
        if (this.random != null) {
            ++count;
        }
        if (this.typeParameterMatcherGetCache != null) {
            ++count;
        }
        if (this.typeParameterMatcherFindCache != null) {
            ++count;
        }
        if (this.stringBuilder != null) {
            ++count;
        }
        if (this.charsetEncoderCache != null) {
            ++count;
        }
        if (this.charsetDecoderCache != null) {
            ++count;
        }
        if (this.arrayList != null) {
            ++count;
        }
        Object[] arrobject = this.indexedVariables;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object o = arrobject[n2];
            if (o != UNSET) {
                ++count;
            }
            ++n2;
        }
        return count - 1;
    }

    public StringBuilder stringBuilder() {
        StringBuilder sb = this.stringBuilder;
        if (sb == null) {
            this.stringBuilder = new StringBuilder((int)STRING_BUILDER_INITIAL_SIZE);
            return this.stringBuilder;
        }
        if (sb.capacity() > STRING_BUILDER_MAX_SIZE) {
            sb.setLength((int)STRING_BUILDER_INITIAL_SIZE);
            sb.trimToSize();
        }
        sb.setLength((int)0);
        return sb;
    }

    public Map<Charset, CharsetEncoder> charsetEncoderCache() {
        IdentityHashMap<K, V> cache = this.charsetEncoderCache;
        if (cache != null) return cache;
        this.charsetEncoderCache = cache = new IdentityHashMap<K, V>();
        return cache;
    }

    public Map<Charset, CharsetDecoder> charsetDecoderCache() {
        IdentityHashMap<K, V> cache = this.charsetDecoderCache;
        if (cache != null) return cache;
        this.charsetDecoderCache = cache = new IdentityHashMap<K, V>();
        return cache;
    }

    public <E> ArrayList<E> arrayList() {
        return this.arrayList((int)8);
    }

    public <E> ArrayList<E> arrayList(int minCapacity) {
        ArrayList list = this.arrayList;
        if (list == null) {
            this.arrayList = new ArrayList<E>((int)minCapacity);
            return this.arrayList;
        }
        list.clear();
        list.ensureCapacity((int)minCapacity);
        return list;
    }

    public int futureListenerStackDepth() {
        return this.futureListenerStackDepth;
    }

    public void setFutureListenerStackDepth(int futureListenerStackDepth) {
        this.futureListenerStackDepth = futureListenerStackDepth;
    }

    public ThreadLocalRandom random() {
        ThreadLocalRandom r = this.random;
        if (r != null) return r;
        this.random = r = new ThreadLocalRandom();
        return r;
    }

    public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
        IdentityHashMap<K, V> cache = this.typeParameterMatcherGetCache;
        if (cache != null) return cache;
        this.typeParameterMatcherGetCache = cache = new IdentityHashMap<K, V>();
        return cache;
    }

    public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
        IdentityHashMap<K, V> cache = this.typeParameterMatcherFindCache;
        if (cache != null) return cache;
        this.typeParameterMatcherFindCache = cache = new IdentityHashMap<K, V>();
        return cache;
    }

    @Deprecated
    public IntegerHolder counterHashCode() {
        return this.counterHashCode;
    }

    @Deprecated
    public void setCounterHashCode(IntegerHolder counterHashCode) {
        this.counterHashCode = counterHashCode;
    }

    public Map<Class<?>, Boolean> handlerSharableCache() {
        WeakHashMap<K, V> cache = this.handlerSharableCache;
        if (cache != null) return cache;
        this.handlerSharableCache = cache = new WeakHashMap<K, V>((int)4);
        return cache;
    }

    public int localChannelReaderStackDepth() {
        return this.localChannelReaderStackDepth;
    }

    public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
        this.localChannelReaderStackDepth = localChannelReaderStackDepth;
    }

    public Object indexedVariable(int index) {
        Object object;
        Object[] lookup = this.indexedVariables;
        if (index < lookup.length) {
            object = lookup[index];
            return object;
        }
        object = UNSET;
        return object;
    }

    public boolean setIndexedVariable(int index, Object value) {
        Object[] lookup = this.indexedVariables;
        if (index >= lookup.length) {
            this.expandIndexedVariableTableAndSet((int)index, (Object)value);
            return true;
        }
        Object oldValue = lookup[index];
        lookup[index] = value;
        if (oldValue != UNSET) return false;
        return true;
    }

    private void expandIndexedVariableTableAndSet(int index, Object value) {
        Object[] oldArray = this.indexedVariables;
        int oldCapacity = oldArray.length;
        int newCapacity = index;
        newCapacity |= newCapacity >>> 1;
        newCapacity |= newCapacity >>> 2;
        newCapacity |= newCapacity >>> 4;
        newCapacity |= newCapacity >>> 8;
        newCapacity |= newCapacity >>> 16;
        Object[] newArray = Arrays.copyOf(oldArray, (int)(++newCapacity));
        Arrays.fill((Object[])newArray, (int)oldCapacity, (int)newArray.length, (Object)UNSET);
        newArray[index] = value;
        this.indexedVariables = newArray;
    }

    public Object removeIndexedVariable(int index) {
        Object[] lookup = this.indexedVariables;
        if (index >= lookup.length) return UNSET;
        Object v = lookup[index];
        lookup[index] = UNSET;
        return v;
    }

    public boolean isIndexedVariableSet(int index) {
        Object[] lookup = this.indexedVariables;
        if (index >= lookup.length) return false;
        if (lookup[index] == UNSET) return false;
        return true;
    }

    public boolean isCleanerFlagSet(int index) {
        if (this.cleanerFlags == null) return false;
        if (!this.cleanerFlags.get((int)index)) return false;
        return true;
    }

    public void setCleanerFlag(int index) {
        if (this.cleanerFlags == null) {
            this.cleanerFlags = new BitSet();
        }
        this.cleanerFlags.set((int)index);
    }

    static {
        UNSET = new Object();
        STRING_BUILDER_INITIAL_SIZE = SystemPropertyUtil.getInt((String)"io.netty.threadLocalMap.stringBuilder.initialSize", (int)1024);
        logger.debug((String)"-Dio.netty.threadLocalMap.stringBuilder.initialSize: {}", (Object)Integer.valueOf((int)STRING_BUILDER_INITIAL_SIZE));
        STRING_BUILDER_MAX_SIZE = SystemPropertyUtil.getInt((String)"io.netty.threadLocalMap.stringBuilder.maxSize", (int)4096);
        logger.debug((String)"-Dio.netty.threadLocalMap.stringBuilder.maxSize: {}", (Object)Integer.valueOf((int)STRING_BUILDER_MAX_SIZE));
    }
}

