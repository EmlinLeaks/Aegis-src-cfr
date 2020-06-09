/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import javax.annotation.concurrent.ThreadSafe;

@Beta
@CanIgnoreReturnValue
@ThreadSafe
@GwtIncompatible
public class CycleDetectingLockFactory {
    private static final ConcurrentMap<Class<? extends Enum>, Map<? extends Enum, LockGraphNode>> lockGraphNodesPerType = new MapMaker().weakKeys().makeMap();
    private static final Logger logger = Logger.getLogger((String)CycleDetectingLockFactory.class.getName());
    final Policy policy;
    private static final ThreadLocal<ArrayList<LockGraphNode>> acquiredLocks = new ThreadLocal<ArrayList<LockGraphNode>>(){

        protected ArrayList<LockGraphNode> initialValue() {
            return Lists.newArrayListWithCapacity((int)3);
        }
    };

    public static CycleDetectingLockFactory newInstance(Policy policy) {
        return new CycleDetectingLockFactory((Policy)policy);
    }

    public ReentrantLock newReentrantLock(String lockName) {
        return this.newReentrantLock((String)lockName, (boolean)false);
    }

    public ReentrantLock newReentrantLock(String lockName, boolean fair) {
        ReentrantLock reentrantLock;
        if (this.policy == Policies.DISABLED) {
            reentrantLock = new ReentrantLock((boolean)fair);
            return reentrantLock;
        }
        reentrantLock = new CycleDetectingReentrantLock((CycleDetectingLockFactory)this, (LockGraphNode)new LockGraphNode((String)lockName), (boolean)fair, null);
        return reentrantLock;
    }

    public ReentrantReadWriteLock newReentrantReadWriteLock(String lockName) {
        return this.newReentrantReadWriteLock((String)lockName, (boolean)false);
    }

    public ReentrantReadWriteLock newReentrantReadWriteLock(String lockName, boolean fair) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        if (this.policy == Policies.DISABLED) {
            reentrantReadWriteLock = new ReentrantReadWriteLock((boolean)fair);
            return reentrantReadWriteLock;
        }
        reentrantReadWriteLock = new CycleDetectingReentrantReadWriteLock((CycleDetectingLockFactory)this, (LockGraphNode)new LockGraphNode((String)lockName), (boolean)fair, null);
        return reentrantReadWriteLock;
    }

    public static <E extends Enum<E>> WithExplicitOrdering<E> newInstanceWithExplicitOrdering(Class<E> enumClass, Policy policy) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull(policy);
        Map<? extends Enum, LockGraphNode> lockGraphNodes = CycleDetectingLockFactory.getOrCreateNodes(enumClass);
        return new WithExplicitOrdering<Enum>((Policy)policy, lockGraphNodes);
    }

    private static Map<? extends Enum, LockGraphNode> getOrCreateNodes(Class<? extends Enum> clazz) {
        Map<? extends Enum, LockGraphNode> existing = (Map<? extends Enum, LockGraphNode>)lockGraphNodesPerType.get(clazz);
        if (existing != null) {
            return existing;
        }
        Map<? extends Enum, LockGraphNode> created = CycleDetectingLockFactory.createNodes(clazz);
        existing = lockGraphNodesPerType.putIfAbsent(clazz, created);
        return MoreObjects.firstNonNull(existing, created);
    }

    @VisibleForTesting
    static <E extends Enum<E>> Map<E, LockGraphNode> createNodes(Class<E> clazz) {
        int i;
        EnumMap<E, LockGraphNode> map = Maps.newEnumMap(clazz);
        Enum[] keys = (Enum[])clazz.getEnumConstants();
        int numKeys = keys.length;
        ArrayList<LockGraphNode> nodes = Lists.newArrayListWithCapacity((int)numKeys);
        for (Enum key : keys) {
            LockGraphNode node = new LockGraphNode((String)CycleDetectingLockFactory.getLockName(key));
            nodes.add(node);
            map.put(key, node);
        }
        for (i = 1; i < numKeys; ++i) {
            ((LockGraphNode)nodes.get((int)i)).checkAcquiredLocks((Policy)Policies.THROW, nodes.subList((int)0, (int)i));
        }
        i = 0;
        while (i < numKeys - 1) {
            ((LockGraphNode)nodes.get((int)i)).checkAcquiredLocks((Policy)Policies.DISABLED, nodes.subList((int)(i + 1), (int)numKeys));
            ++i;
        }
        return Collections.unmodifiableMap(map);
    }

    private static String getLockName(Enum<?> rank) {
        return rank.getDeclaringClass().getSimpleName() + "." + rank.name();
    }

    private CycleDetectingLockFactory(Policy policy) {
        this.policy = Preconditions.checkNotNull(policy);
    }

    private void aboutToAcquire(CycleDetectingLock lock) {
        if (lock.isAcquiredByCurrentThread()) return;
        ArrayList<LockGraphNode> acquiredLockList = acquiredLocks.get();
        LockGraphNode node = lock.getLockGraphNode();
        node.checkAcquiredLocks((Policy)this.policy, acquiredLockList);
        acquiredLockList.add((LockGraphNode)node);
    }

    private static void lockStateChanged(CycleDetectingLock lock) {
        if (lock.isAcquiredByCurrentThread()) return;
        ArrayList<LockGraphNode> acquiredLockList = acquiredLocks.get();
        LockGraphNode node = lock.getLockGraphNode();
        int i = acquiredLockList.size() - 1;
        while (i >= 0) {
            if (acquiredLockList.get((int)i) == node) {
                acquiredLockList.remove((int)i);
                return;
            }
            --i;
        }
    }

    static /* synthetic */ Logger access$100() {
        return logger;
    }

    static /* synthetic */ void access$600(CycleDetectingLockFactory x0, CycleDetectingLock x1) {
        x0.aboutToAcquire((CycleDetectingLock)x1);
    }

    static /* synthetic */ void access$700(CycleDetectingLock x0) {
        CycleDetectingLockFactory.lockStateChanged((CycleDetectingLock)x0);
    }
}

