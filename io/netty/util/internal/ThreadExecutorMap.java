/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThreadExecutorMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadExecutorMap {
    private static final FastThreadLocal<EventExecutor> mappings = new FastThreadLocal<V>();

    private ThreadExecutorMap() {
    }

    public static EventExecutor currentExecutor() {
        return mappings.get();
    }

    private static void setCurrentEventExecutor(EventExecutor executor) {
        mappings.set((EventExecutor)executor);
    }

    public static Executor apply(Executor executor, EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(executor, (String)"executor");
        ObjectUtil.checkNotNull(eventExecutor, (String)"eventExecutor");
        return new Executor((Executor)executor, (EventExecutor)eventExecutor){
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ EventExecutor val$eventExecutor;
            {
                this.val$executor = executor;
                this.val$eventExecutor = eventExecutor;
            }

            public void execute(Runnable command) {
                this.val$executor.execute((Runnable)ThreadExecutorMap.apply((Runnable)command, (EventExecutor)this.val$eventExecutor));
            }
        };
    }

    public static Runnable apply(Runnable command, EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(command, (String)"command");
        ObjectUtil.checkNotNull(eventExecutor, (String)"eventExecutor");
        return new Runnable((EventExecutor)eventExecutor, (Runnable)command){
            final /* synthetic */ EventExecutor val$eventExecutor;
            final /* synthetic */ Runnable val$command;
            {
                this.val$eventExecutor = eventExecutor;
                this.val$command = runnable;
            }

            public void run() {
                ThreadExecutorMap.access$000((EventExecutor)this.val$eventExecutor);
                try {
                    this.val$command.run();
                    return;
                }
                finally {
                    ThreadExecutorMap.access$000(null);
                }
            }
        };
    }

    public static ThreadFactory apply(ThreadFactory threadFactory, EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(threadFactory, (String)"command");
        ObjectUtil.checkNotNull(eventExecutor, (String)"eventExecutor");
        return new ThreadFactory((ThreadFactory)threadFactory, (EventExecutor)eventExecutor){
            final /* synthetic */ ThreadFactory val$threadFactory;
            final /* synthetic */ EventExecutor val$eventExecutor;
            {
                this.val$threadFactory = threadFactory;
                this.val$eventExecutor = eventExecutor;
            }

            public java.lang.Thread newThread(Runnable r) {
                return this.val$threadFactory.newThread((Runnable)ThreadExecutorMap.apply((Runnable)r, (EventExecutor)this.val$eventExecutor));
            }
        };
    }

    static /* synthetic */ void access$000(EventExecutor x0) {
        ThreadExecutorMap.setCurrentEventExecutor((EventExecutor)x0);
    }
}

