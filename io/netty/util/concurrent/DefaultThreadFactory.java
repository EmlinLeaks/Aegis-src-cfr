/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocalRunnable;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.StringUtil;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory
implements ThreadFactory {
    private static final AtomicInteger poolId = new AtomicInteger();
    private final AtomicInteger nextId = new AtomicInteger();
    private final String prefix;
    private final boolean daemon;
    private final int priority;
    protected final ThreadGroup threadGroup;

    public DefaultThreadFactory(Class<?> poolType) {
        this(poolType, (boolean)false, (int)5);
    }

    public DefaultThreadFactory(String poolName) {
        this((String)poolName, (boolean)false, (int)5);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon) {
        this(poolType, (boolean)daemon, (int)5);
    }

    public DefaultThreadFactory(String poolName, boolean daemon) {
        this((String)poolName, (boolean)daemon, (int)5);
    }

    public DefaultThreadFactory(Class<?> poolType, int priority) {
        this(poolType, (boolean)false, (int)priority);
    }

    public DefaultThreadFactory(String poolName, int priority) {
        this((String)poolName, (boolean)false, (int)priority);
    }

    public DefaultThreadFactory(Class<?> poolType, boolean daemon, int priority) {
        this((String)DefaultThreadFactory.toPoolName(poolType), (boolean)daemon, (int)priority);
    }

    public static String toPoolName(Class<?> poolType) {
        if (poolType == null) {
            throw new NullPointerException((String)"poolType");
        }
        String poolName = StringUtil.simpleClassName(poolType);
        switch (poolName.length()) {
            case 0: {
                return "unknown";
            }
            case 1: {
                return poolName.toLowerCase((Locale)Locale.US);
            }
        }
        if (!Character.isUpperCase((char)poolName.charAt((int)0))) return poolName;
        if (!Character.isLowerCase((char)poolName.charAt((int)1))) return poolName;
        return Character.toLowerCase((char)poolName.charAt((int)0)) + poolName.substring((int)1);
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {
        if (poolName == null) {
            throw new NullPointerException((String)"poolName");
        }
        if (priority < 1) throw new IllegalArgumentException((String)("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)"));
        if (priority > 10) {
            throw new IllegalArgumentException((String)("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)"));
        }
        this.prefix = poolName + '-' + poolId.incrementAndGet() + '-';
        this.daemon = daemon;
        this.priority = priority;
        this.threadGroup = threadGroup;
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
        this((String)poolName, (boolean)daemon, (int)priority, (ThreadGroup)(System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup()));
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = this.newThread((Runnable)FastThreadLocalRunnable.wrap((Runnable)r), (String)(this.prefix + this.nextId.incrementAndGet()));
        try {
            if (t.isDaemon() != this.daemon) {
                t.setDaemon((boolean)this.daemon);
            }
            if (t.getPriority() == this.priority) return t;
            t.setPriority((int)this.priority);
            return t;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return t;
    }

    protected Thread newThread(Runnable r, String name) {
        return new FastThreadLocalThread((ThreadGroup)this.threadGroup, (Runnable)r, (String)name);
    }
}

