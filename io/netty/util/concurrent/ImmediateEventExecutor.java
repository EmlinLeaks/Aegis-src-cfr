/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor
extends AbstractEventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ImmediateEventExecutor.class);
    public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
    private static final FastThreadLocal<Queue<Runnable>> DELAYED_RUNNABLES = new FastThreadLocal<Queue<Runnable>>(){

        protected Queue<Runnable> initialValue() throws java.lang.Exception {
            return new java.util.ArrayDeque<Runnable>();
        }
    };
    private static final FastThreadLocal<Boolean> RUNNING = new FastThreadLocal<Boolean>(){

        protected Boolean initialValue() throws java.lang.Exception {
            return Boolean.valueOf((boolean)false);
        }
    };
    private final Future<?> terminationFuture = new FailedFuture<?>((EventExecutor)GlobalEventExecutor.INSTANCE, (Throwable)new UnsupportedOperationException());

    private ImmediateEventExecutor() {
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException((String)"command");
        }
        if (RUNNING.get().booleanValue()) {
            DELAYED_RUNNABLES.get().add((Runnable)command);
            return;
        }
        RUNNING.set((Boolean)Boolean.valueOf((boolean)true));
        try {
            command.run();
            return;
        }
        catch (Throwable cause) {
            logger.info((String)"Throwable caught while executing Runnable {}", (Object)command, (Object)cause);
            return;
        }
        finally {
            Queue<Runnable> delayedRunnables = DELAYED_RUNNABLES.get();
            do {
                Runnable runnable;
                if ((runnable = delayedRunnables.poll()) == null) {
                    RUNNING.set((Boolean)Boolean.valueOf((boolean)false));
                }
                try {
                    runnable.run();
                }
                catch (Throwable cause) {
                    logger.info((String)"Throwable caught while executing Runnable {}", (Object)runnable, (Object)cause);
                    continue;
                }
                break;
            } while (true);
        }
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new ImmediatePromise<V>((EventExecutor)this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new ImmediateProgressivePromise<V>((EventExecutor)this);
    }
}

