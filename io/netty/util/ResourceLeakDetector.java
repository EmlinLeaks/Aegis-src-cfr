/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.ResourceLeak;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceLeakDetector<T> {
    private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
    private static final String PROP_LEVEL = "io.netty.leakDetection.level";
    private static final Level DEFAULT_LEVEL;
    private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
    private static final int DEFAULT_TARGET_RECORDS = 4;
    private static final String PROP_SAMPLING_INTERVAL = "io.netty.leakDetection.samplingInterval";
    private static final int DEFAULT_SAMPLING_INTERVAL = 128;
    private static final int TARGET_RECORDS;
    static final int SAMPLING_INTERVAL;
    private static Level level;
    private static final InternalLogger logger;
    private final Set<DefaultResourceLeak<?>> allLeaks = Collections.newSetFromMap(new ConcurrentHashMap<K, V>());
    private final ReferenceQueue<Object> refQueue = new ReferenceQueue<T>();
    private final ConcurrentMap<String, Boolean> reportedLeaks = PlatformDependent.newConcurrentHashMap();
    private final String resourceType;
    private final int samplingInterval;
    private static final AtomicReference<String[]> excludedMethods;

    @Deprecated
    public static void setEnabled(boolean enabled) {
        ResourceLeakDetector.setLevel((Level)(enabled ? Level.SIMPLE : Level.DISABLED));
    }

    public static boolean isEnabled() {
        if (ResourceLeakDetector.getLevel().ordinal() <= Level.DISABLED.ordinal()) return false;
        return true;
    }

    public static void setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException((String)"level");
        }
        ResourceLeakDetector.level = level;
    }

    public static Level getLevel() {
        return level;
    }

    @Deprecated
    public ResourceLeakDetector(Class<?> resourceType) {
        this((String)StringUtil.simpleClassName(resourceType));
    }

    @Deprecated
    public ResourceLeakDetector(String resourceType) {
        this((String)resourceType, (int)128, (long)Long.MAX_VALUE);
    }

    @Deprecated
    public ResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
        this(resourceType, (int)samplingInterval);
    }

    public ResourceLeakDetector(Class<?> resourceType, int samplingInterval) {
        this((String)StringUtil.simpleClassName(resourceType), (int)samplingInterval, (long)Long.MAX_VALUE);
    }

    @Deprecated
    public ResourceLeakDetector(String resourceType, int samplingInterval, long maxActive) {
        if (resourceType == null) {
            throw new NullPointerException((String)"resourceType");
        }
        this.resourceType = resourceType;
        this.samplingInterval = samplingInterval;
    }

    @Deprecated
    public final ResourceLeak open(T obj) {
        return this.track0(obj);
    }

    public final ResourceLeakTracker<T> track(T obj) {
        return this.track0(obj);
    }

    private DefaultResourceLeak track0(T obj) {
        Level level = ResourceLeakDetector.level;
        if (level == Level.DISABLED) {
            return null;
        }
        if (level.ordinal() < Level.PARANOID.ordinal()) {
            if (PlatformDependent.threadLocalRandom().nextInt((int)this.samplingInterval) != 0) return null;
            this.reportLeak();
            return new DefaultResourceLeak<T>(obj, this.refQueue, this.allLeaks);
        }
        this.reportLeak();
        return new DefaultResourceLeak<T>(obj, this.refQueue, this.allLeaks);
    }

    private void clearRefQueue() {
        DefaultResourceLeak ref;
        while ((ref = (DefaultResourceLeak)this.refQueue.poll()) != null) {
            ref.dispose();
        }
        return;
    }

    private void reportLeak() {
        if (!logger.isErrorEnabled()) {
            this.clearRefQueue();
            return;
        }
        DefaultResourceLeak ref;
        while ((ref = (DefaultResourceLeak)this.refQueue.poll()) != null) {
            String records;
            if (!ref.dispose() || this.reportedLeaks.putIfAbsent((String)(records = ref.toString()), (Boolean)Boolean.TRUE) != null) continue;
            if (records.isEmpty()) {
                this.reportUntracedLeak((String)this.resourceType);
                continue;
            }
            this.reportTracedLeak((String)this.resourceType, (String)records);
        }
        return;
    }

    protected void reportTracedLeak(String resourceType, String records) {
        logger.error((String)"LEAK: {}.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.{}", (Object)resourceType, (Object)records);
    }

    protected void reportUntracedLeak(String resourceType) {
        logger.error((String)"LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See https://netty.io/wiki/reference-counted-objects.html for more information.", (Object[])new Object[]{resourceType, PROP_LEVEL, Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName((Object)this)});
    }

    @Deprecated
    protected void reportInstancesLeak(String resourceType) {
    }

    public static void addExclusions(Class clz, String ... methodNames) {
        String[] newMethods;
        String[] oldMethods;
        Method method;
        HashSet<String> nameSet = new HashSet<String>(Arrays.asList(methodNames));
        Method[] arrmethod = clz.getDeclaredMethods();
        int n = arrmethod.length;
        for (int i = 0; !(i >= n || nameSet.remove((Object)(method = arrmethod[i]).getName()) && nameSet.isEmpty()); ++i) {
        }
        if (!nameSet.isEmpty()) {
            throw new IllegalArgumentException((String)("Can't find '" + nameSet + "' in " + clz.getName()));
        }
        do {
            oldMethods = excludedMethods.get();
            newMethods = Arrays.copyOf(oldMethods, (int)(oldMethods.length + 2 * methodNames.length));
            for (int i = 0; i < methodNames.length; ++i) {
                newMethods[oldMethods.length + i * 2] = clz.getName();
                newMethods[oldMethods.length + i * 2 + 1] = methodNames[i];
            }
        } while (!excludedMethods.compareAndSet((String[])oldMethods, (String[])newMethods));
    }

    static /* synthetic */ Level access$000() {
        return DEFAULT_LEVEL;
    }

    static /* synthetic */ int access$200() {
        return TARGET_RECORDS;
    }

    static /* synthetic */ AtomicReference access$500() {
        return excludedMethods;
    }

    static {
        boolean disabled;
        DEFAULT_LEVEL = Level.SIMPLE;
        logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
        if (SystemPropertyUtil.get((String)"io.netty.noResourceLeakDetection") != null) {
            disabled = SystemPropertyUtil.getBoolean((String)"io.netty.noResourceLeakDetection", (boolean)false);
            logger.debug((String)"-Dio.netty.noResourceLeakDetection: {}", (Object)Boolean.valueOf((boolean)disabled));
            logger.warn((String)"-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", (Object)PROP_LEVEL, (Object)DEFAULT_LEVEL.name().toLowerCase());
        } else {
            disabled = false;
        }
        Level defaultLevel = disabled ? Level.DISABLED : DEFAULT_LEVEL;
        String levelStr = SystemPropertyUtil.get((String)PROP_LEVEL_OLD, (String)defaultLevel.name());
        levelStr = SystemPropertyUtil.get((String)PROP_LEVEL, (String)levelStr);
        Level level = Level.parseLevel((String)levelStr);
        TARGET_RECORDS = SystemPropertyUtil.getInt((String)PROP_TARGET_RECORDS, (int)4);
        SAMPLING_INTERVAL = SystemPropertyUtil.getInt((String)PROP_SAMPLING_INTERVAL, (int)128);
        ResourceLeakDetector.level = level;
        if (logger.isDebugEnabled()) {
            logger.debug((String)"-D{}: {}", (Object)PROP_LEVEL, (Object)level.name().toLowerCase());
            logger.debug((String)"-D{}: {}", (Object)PROP_TARGET_RECORDS, (Object)Integer.valueOf((int)TARGET_RECORDS));
        }
        excludedMethods = new AtomicReference<String[]>(EmptyArrays.EMPTY_STRINGS);
    }
}

