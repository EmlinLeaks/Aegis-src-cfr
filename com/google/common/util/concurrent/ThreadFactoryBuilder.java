/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckReturnValue;

@CanIgnoreReturnValue
@GwtIncompatible
public final class ThreadFactoryBuilder {
    private String nameFormat = null;
    private Boolean daemon = null;
    private Integer priority = null;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
    private ThreadFactory backingThreadFactory = null;

    public ThreadFactoryBuilder setNameFormat(String nameFormat) {
        String unused = ThreadFactoryBuilder.format((String)nameFormat, (Object[])new Object[]{Integer.valueOf((int)0)});
        this.nameFormat = nameFormat;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = Boolean.valueOf((boolean)daemon);
        return this;
    }

    public ThreadFactoryBuilder setPriority(int priority) {
        Preconditions.checkArgument((boolean)(priority >= 1), (String)"Thread priority (%s) must be >= %s", (int)priority, (int)1);
        Preconditions.checkArgument((boolean)(priority <= 10), (String)"Thread priority (%s) must be <= %s", (int)priority, (int)10);
        this.priority = Integer.valueOf((int)priority);
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = Preconditions.checkNotNull(uncaughtExceptionHandler);
        return this;
    }

    public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = Preconditions.checkNotNull(backingThreadFactory);
        return this;
    }

    @CheckReturnValue
    public ThreadFactory build() {
        return ThreadFactoryBuilder.build((ThreadFactoryBuilder)this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        String nameFormat = builder.nameFormat;
        Boolean daemon = builder.daemon;
        Integer priority = builder.priority;
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
        ThreadFactory backingThreadFactory = builder.backingThreadFactory != null ? builder.backingThreadFactory : Executors.defaultThreadFactory();
        AtomicLong count = nameFormat != null ? new AtomicLong((long)0L) : null;
        return new ThreadFactory((ThreadFactory)backingThreadFactory, (String)nameFormat, (AtomicLong)count, (Boolean)daemon, (Integer)priority, (Thread.UncaughtExceptionHandler)uncaughtExceptionHandler){
            final /* synthetic */ ThreadFactory val$backingThreadFactory;
            final /* synthetic */ String val$nameFormat;
            final /* synthetic */ AtomicLong val$count;
            final /* synthetic */ Boolean val$daemon;
            final /* synthetic */ Integer val$priority;
            final /* synthetic */ Thread.UncaughtExceptionHandler val$uncaughtExceptionHandler;
            {
                this.val$backingThreadFactory = threadFactory;
                this.val$nameFormat = string;
                this.val$count = atomicLong;
                this.val$daemon = bl;
                this.val$priority = n;
                this.val$uncaughtExceptionHandler = uncaughtExceptionHandler;
            }

            public Thread newThread(java.lang.Runnable runnable) {
                Thread thread = this.val$backingThreadFactory.newThread((java.lang.Runnable)runnable);
                if (this.val$nameFormat != null) {
                    thread.setName((String)ThreadFactoryBuilder.access$000((String)this.val$nameFormat, (Object[])new Object[]{java.lang.Long.valueOf((long)this.val$count.getAndIncrement())}));
                }
                if (this.val$daemon != null) {
                    thread.setDaemon((boolean)this.val$daemon.booleanValue());
                }
                if (this.val$priority != null) {
                    thread.setPriority((int)this.val$priority.intValue());
                }
                if (this.val$uncaughtExceptionHandler == null) return thread;
                thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)this.val$uncaughtExceptionHandler);
                return thread;
            }
        };
    }

    private static String format(String format, Object ... args) {
        return String.format((Locale)Locale.ROOT, (String)format, (Object[])args);
    }

    static /* synthetic */ String access$000(String x0, Object[] x1) {
        return ThreadFactoryBuilder.format((String)x0, (Object[])x1);
    }
}

