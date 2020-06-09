/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.channel.nio.SelectedSelectionKeySetSelector;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEY_SET_OPTIMIZATION;
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier = new IntSupplier((NioEventLoop)this){
        final /* synthetic */ NioEventLoop this$0;
        {
            this.this$0 = this$0;
        }

        public int get() throws Exception {
            return this.this$0.selectNow();
        }
    };
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private volatile long nextWakeupTime = Long.MAX_VALUE;
    private final SelectStrategy selectStrategy;
    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory queueFactory) {
        super((EventLoopGroup)parent, (Executor)executor, (boolean)false, NioEventLoop.newTaskQueue((EventLoopTaskQueueFactory)queueFactory), NioEventLoop.newTaskQueue((EventLoopTaskQueueFactory)queueFactory), (RejectedExecutionHandler)rejectedExecutionHandler);
        if (selectorProvider == null) {
            throw new NullPointerException((String)"selectorProvider");
        }
        if (strategy == null) {
            throw new NullPointerException((String)"selectStrategy");
        }
        this.provider = selectorProvider;
        SelectorTuple selectorTuple = this.openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
        this.selectStrategy = strategy;
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory != null) return queueFactory.newTaskQueue((int)DEFAULT_MAX_PENDING_TASKS);
        return NioEventLoop.newTaskQueue0((int)DEFAULT_MAX_PENDING_TASKS);
    }

    private SelectorTuple openSelector() {
        AbstractSelector unwrappedSelector;
        try {
            unwrappedSelector = this.provider.openSelector();
        }
        catch (IOException e) {
            throw new ChannelException((String)"failed to open a new selector", (Throwable)e);
        }
        if (DISABLE_KEY_SET_OPTIMIZATION) {
            return new SelectorTuple((Selector)unwrappedSelector);
        }
        Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>((NioEventLoop)this){
            final /* synthetic */ NioEventLoop this$0;
            {
                this.this$0 = this$0;
            }

            public Object run() {
                try {
                    return Class.forName((String)"sun.nio.ch.SelectorImpl", (boolean)false, (java.lang.ClassLoader)PlatformDependent.getSystemClassLoader());
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            if (!(maybeSelectorImplClass instanceof Throwable)) return new SelectorTuple((Selector)unwrappedSelector);
            Throwable t = (Throwable)maybeSelectorImplClass;
            logger.trace((String)"failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)t);
            return new SelectorTuple((Selector)unwrappedSelector);
        }
        Class selectorImplClass = (Class)maybeSelectorImplClass;
        SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>((NioEventLoop)this, (Class)selectorImplClass, (Selector)unwrappedSelector, (SelectedSelectionKeySet)selectedKeySet){
            final /* synthetic */ Class val$selectorImplClass;
            final /* synthetic */ Selector val$unwrappedSelector;
            final /* synthetic */ SelectedSelectionKeySet val$selectedKeySet;
            final /* synthetic */ NioEventLoop this$0;
            {
                this.this$0 = this$0;
                this.val$selectorImplClass = class_;
                this.val$unwrappedSelector = selector;
                this.val$selectedKeySet = selectedSelectionKeySet;
            }

            public Object run() {
                try {
                    Throwable cause;
                    java.lang.reflect.Field selectedKeysField = this.val$selectorImplClass.getDeclaredField((String)"selectedKeys");
                    java.lang.reflect.Field publicSelectedKeysField = this.val$selectorImplClass.getDeclaredField((String)"publicSelectedKeys");
                    if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset((java.lang.reflect.Field)selectedKeysField);
                        long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset((java.lang.reflect.Field)publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                            PlatformDependent.putObject((Object)this.val$unwrappedSelector, (long)selectedKeysFieldOffset, (Object)this.val$selectedKeySet);
                            PlatformDependent.putObject((Object)this.val$unwrappedSelector, (long)publicSelectedKeysFieldOffset, (Object)this.val$selectedKeySet);
                            return null;
                        }
                    }
                    if ((cause = io.netty.util.internal.ReflectionUtil.trySetAccessible((java.lang.reflect.AccessibleObject)selectedKeysField, (boolean)true)) != null) {
                        return cause;
                    }
                    cause = io.netty.util.internal.ReflectionUtil.trySetAccessible((java.lang.reflect.AccessibleObject)publicSelectedKeysField, (boolean)true);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set((Object)this.val$unwrappedSelector, (Object)this.val$selectedKeySet);
                    publicSelectedKeysField.set((Object)this.val$unwrappedSelector, (Object)this.val$selectedKeySet);
                    return null;
                }
                catch (java.lang.NoSuchFieldException e) {
                    return e;
                }
                catch (java.lang.IllegalAccessException e) {
                    return e;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            Exception e = (Exception)maybeException;
            logger.trace((String)"failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)e);
            return new SelectorTuple((Selector)unwrappedSelector);
        }
        this.selectedKeys = selectedKeySet;
        logger.trace((String)"instrumented a special java.util.Set into: {}", (Object)unwrappedSelector);
        return new SelectorTuple((Selector)unwrappedSelector, (Selector)new SelectedSelectionKeySetSelector((Selector)unwrappedSelector, (SelectedSelectionKeySet)selectedKeySet));
    }

    public SelectorProvider selectorProvider() {
        return this.provider;
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return NioEventLoop.newTaskQueue0((int)maxPendingTasks);
    }

    private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
        Queue<Runnable> queue;
        if (maxPendingTasks == Integer.MAX_VALUE) {
            queue = PlatformDependent.newMpscQueue();
            return queue;
        }
        queue = PlatformDependent.newMpscQueue((int)maxPendingTasks);
        return queue;
    }

    public void register(SelectableChannel ch, int interestOps, NioTask<?> task) {
        if (ch == null) {
            throw new NullPointerException((String)"ch");
        }
        if (interestOps == 0) {
            throw new IllegalArgumentException((String)"interestOps must be non-zero.");
        }
        if ((interestOps & ~ch.validOps()) != 0) {
            throw new IllegalArgumentException((String)("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')'));
        }
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        if (this.isShutdown()) {
            throw new IllegalStateException((String)"event loop shut down");
        }
        if (this.inEventLoop()) {
            this.register0((SelectableChannel)ch, (int)interestOps, task);
            return;
        }
        try {
            this.submit((Runnable)new Runnable((NioEventLoop)this, (SelectableChannel)ch, (int)interestOps, task){
                final /* synthetic */ SelectableChannel val$ch;
                final /* synthetic */ int val$interestOps;
                final /* synthetic */ NioTask val$task;
                final /* synthetic */ NioEventLoop this$0;
                {
                    this.this$0 = this$0;
                    this.val$ch = selectableChannel;
                    this.val$interestOps = n;
                    this.val$task = nioTask;
                }

                public void run() {
                    NioEventLoop.access$000((NioEventLoop)this.this$0, (SelectableChannel)this.val$ch, (int)this.val$interestOps, (NioTask)this.val$task);
                }
            }).sync();
            return;
        }
        catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private void register0(SelectableChannel ch, int interestOps, NioTask<?> task) {
        try {
            ch.register((Selector)this.unwrappedSelector, (int)interestOps, task);
            return;
        }
        catch (Exception e) {
            throw new EventLoopException((String)"failed to register a channel", (Throwable)e);
        }
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0) throw new IllegalArgumentException((String)("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"));
        if (ioRatio > 100) {
            throw new IllegalArgumentException((String)("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"));
        }
        this.ioRatio = ioRatio;
    }

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute((Runnable)new Runnable((NioEventLoop)this){
                final /* synthetic */ NioEventLoop this$0;
                {
                    this.this$0 = this$0;
                }

                public void run() {
                    NioEventLoop.access$100((NioEventLoop)this.this$0);
                }
            });
            return;
        }
        this.rebuildSelector0();
    }

    @Override
    public int registeredChannels() {
        return this.selector.keys().size() - this.cancelledKeys;
    }

    private void rebuildSelector0() {
        int nChannels;
        block10 : {
            SelectorTuple newSelectorTuple;
            Selector oldSelector = this.selector;
            if (oldSelector == null) {
                return;
            }
            try {
                newSelectorTuple = this.openSelector();
            }
            catch (Exception e) {
                logger.warn((String)"Failed to create a new Selector.", (Throwable)e);
                return;
            }
            nChannels = 0;
            for (SelectionKey key : oldSelector.keys()) {
                Object a = key.attachment();
                try {
                    if (!key.isValid() || key.channel().keyFor((Selector)newSelectorTuple.unwrappedSelector) != null) continue;
                    int interestOps = key.interestOps();
                    key.cancel();
                    SelectionKey newKey = key.channel().register((Selector)newSelectorTuple.unwrappedSelector, (int)interestOps, (Object)a);
                    if (a instanceof AbstractNioChannel) {
                        ((AbstractNioChannel)a).selectionKey = newKey;
                    }
                    ++nChannels;
                }
                catch (Exception e) {
                    logger.warn((String)"Failed to re-register a Channel to the new Selector.", (Throwable)e);
                    if (a instanceof AbstractNioChannel) {
                        AbstractNioChannel ch = (AbstractNioChannel)a;
                        ch.unsafe().close((ChannelPromise)ch.unsafe().voidPromise());
                        continue;
                    }
                    NioTask task = (NioTask)a;
                    NioEventLoop.invokeChannelUnregistered((NioTask<SelectableChannel>)task, (SelectionKey)key, (Throwable)e);
                }
            }
            this.selector = newSelectorTuple.selector;
            this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
            try {
                oldSelector.close();
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block10;
                logger.warn((String)"Failed to close the old Selector.", (Throwable)t);
            }
        }
        if (!logger.isInfoEnabled()) return;
        logger.info((String)("Migrated " + nChannels + " channel(s) to the new Selector."));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void run() {
        do {
            block20 : {
                try {
                    do {
                        try {
                            block18 : do {
                                switch (this.selectStrategy.calculateStrategy((IntSupplier)this.selectNowSupplier, (boolean)this.hasTasks())) {
                                    case -2: {
                                        continue block18;
                                    }
                                    case -3: 
                                    case -1: {
                                        this.select((boolean)this.wakenUp.getAndSet((boolean)false));
                                        if (!this.wakenUp.get()) break block18;
                                        this.selector.wakeup();
                                    }
                                }
                                break;
                            } while (true);
                        }
                        catch (IOException e) {
                            this.rebuildSelector0();
                            NioEventLoop.handleLoopException((Throwable)e);
                            continue;
                        }
                        break;
                    } while (true);
                    this.cancelledKeys = 0;
                    this.needsToSelectAgain = false;
                    int ioRatio = this.ioRatio;
                    if (ioRatio == 100) {
                        try {
                            this.processSelectedKeys();
                            break block20;
                        }
                        finally {
                            this.runAllTasks();
                        }
                    }
                    long ioStartTime = System.nanoTime();
                    try {
                        this.processSelectedKeys();
                    }
                    finally {
                        long ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks((long)(ioTime * (long)(100 - ioRatio) / (long)ioRatio));
                    }
                }
                catch (Throwable t) {
                    NioEventLoop.handleLoopException((Throwable)t);
                }
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                NioEventLoop.handleLoopException((Throwable)t);
                continue;
            }
            break;
        } while (true);
    }

    private static void handleLoopException(Throwable t) {
        logger.warn((String)"Unexpected exception in the selector loop.", (Throwable)t);
        try {
            Thread.sleep((long)1000L);
            return;
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
            return;
        }
        this.processSelectedKeysPlain(this.selector.selectedKeys());
    }

    @Override
    protected void cleanup() {
        try {
            this.selector.close();
            return;
        }
        catch (IOException e) {
            logger.warn((String)"Failed to close a selector.", (Throwable)e);
        }
    }

    void cancel(SelectionKey key) {
        key.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys < 256) return;
        this.cancelledKeys = 0;
        this.needsToSelectAgain = true;
    }

    @Override
    protected Runnable pollTask() {
        Runnable task = super.pollTask();
        if (!this.needsToSelectAgain) return task;
        this.selectAgain();
        return task;
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        do {
            SelectionKey k = i.next();
            Object a = k.attachment();
            i.remove();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey((SelectionKey)k, (AbstractNioChannel)((AbstractNioChannel)a));
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey((SelectionKey)k, (NioTask<SelectableChannel>)task);
            }
            if (!i.hasNext()) {
                return;
            }
            if (!this.needsToSelectAgain) continue;
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) {
                return;
            }
            i = selectedKeys.iterator();
        } while (true);
    }

    private void processSelectedKeysOptimized() {
        int i = 0;
        while (i < this.selectedKeys.size) {
            SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey((SelectionKey)k, (AbstractNioChannel)((AbstractNioChannel)a));
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey((SelectionKey)k, (NioTask<SelectableChannel>)task);
            }
            if (this.needsToSelectAgain) {
                this.selectedKeys.reset((int)(i + 1));
                this.selectAgain();
                i = -1;
            }
            ++i;
        }
    }

    private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
        if (!k.isValid()) {
            NioEventLoop eventLoop;
            try {
                eventLoop = ch.eventLoop();
            }
            catch (Throwable ignored) {
                return;
            }
            if (eventLoop != this) return;
            if (eventLoop == null) {
                return;
            }
            unsafe.close((ChannelPromise)unsafe.voidPromise());
            return;
        }
        try {
            int readyOps = k.readyOps();
            if ((readyOps & 8) != 0) {
                int ops = k.interestOps();
                k.interestOps((int)(ops &= -9));
                unsafe.finishConnect();
            }
            if ((readyOps & 4) != 0) {
                ch.unsafe().forceFlush();
            }
            if ((readyOps & 17) == 0) {
                if (readyOps != 0) return;
            }
            unsafe.read();
            return;
        }
        catch (CancelledKeyException ignored) {
            unsafe.close((ChannelPromise)unsafe.voidPromise());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
        int state = 0;
        try {
            task.channelReady((SelectableChannel)k.channel(), (SelectionKey)k);
            state = 1;
            return;
        }
        catch (Exception e) {
            k.cancel();
            NioEventLoop.invokeChannelUnregistered(task, (SelectionKey)k, (Throwable)e);
            state = 2;
            return;
        }
        finally {
            switch (state) {
                case 0: {
                    k.cancel();
                    NioEventLoop.invokeChannelUnregistered(task, (SelectionKey)k, null);
                    break;
                }
                case 1: {
                    if (k.isValid()) break;
                    NioEventLoop.invokeChannelUnregistered(task, (SelectionKey)k, null);
                }
            }
        }
    }

    private void closeAll() {
        this.selectAgain();
        Set<SelectionKey> keys = this.selector.keys();
        ArrayList<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>((int)keys.size());
        for (SelectionKey k : keys) {
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                channels.add((AbstractNioChannel)a);
                continue;
            }
            k.cancel();
            NioTask task = (NioTask)a;
            NioEventLoop.invokeChannelUnregistered((NioTask<SelectableChannel>)task, (SelectionKey)k, null);
        }
        Iterator<SelectionKey> iterator = channels.iterator();
        while (iterator.hasNext()) {
            AbstractNioChannel ch = (AbstractNioChannel)((Object)iterator.next());
            ch.unsafe().close((ChannelPromise)ch.unsafe().voidPromise());
        }
    }

    private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
        try {
            task.channelUnregistered((SelectableChannel)k.channel(), (Throwable)cause);
            return;
        }
        catch (Exception e) {
            logger.warn((String)"Unexpected exception while running NioTask.channelUnregistered()", (Throwable)e);
        }
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (inEventLoop) return;
        if (!this.wakenUp.compareAndSet((boolean)false, (boolean)true)) return;
        this.selector.wakeup();
    }

    @Override
    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        if (deadlineNanos >= this.nextWakeupTime) return false;
        return true;
    }

    @Override
    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        if (deadlineNanos >= this.nextWakeupTime) return false;
        return true;
    }

    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }

    int selectNow() throws IOException {
        try {
            int n = this.selector.selectNow();
            return n;
        }
        finally {
            if (this.wakenUp.get()) {
                this.selector.wakeup();
            }
        }
    }

    private void select(boolean oldWakenUp) throws IOException {
        Selector selector = this.selector;
        try {
            int selectCnt = 0;
            long currentTimeNanos = System.nanoTime();
            long selectDeadLineNanos = currentTimeNanos + this.delayNanos((long)currentTimeNanos);
            long normalizedDeadlineNanos = selectDeadLineNanos - NioEventLoop.initialNanoTime();
            if (this.nextWakeupTime != normalizedDeadlineNanos) {
                this.nextWakeupTime = normalizedDeadlineNanos;
            }
            do {
                long timeoutMillis;
                if ((timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L) <= 0L) {
                    if (selectCnt != 0) break;
                    selector.selectNow();
                    selectCnt = 1;
                    break;
                }
                if (this.hasTasks() && this.wakenUp.compareAndSet((boolean)false, (boolean)true)) {
                    selector.selectNow();
                    selectCnt = 1;
                    break;
                }
                int selectedKeys = selector.select((long)timeoutMillis);
                ++selectCnt;
                if (selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || this.hasTasks() || this.hasScheduledTasks()) break;
                if (Thread.interrupted()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((String)"Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
                    }
                    selectCnt = 1;
                    break;
                }
                long time = System.nanoTime();
                if (time - TimeUnit.MILLISECONDS.toNanos((long)timeoutMillis) >= currentTimeNanos) {
                    selectCnt = 1;
                } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                    selector = this.selectRebuildSelector((int)selectCnt);
                    selectCnt = 1;
                    break;
                }
                currentTimeNanos = time;
            } while (true);
            if (selectCnt <= 3) return;
            if (!logger.isDebugEnabled()) return;
            logger.debug((String)"Selector.select() returned prematurely {} times in a row for Selector {}.", (Object)Integer.valueOf((int)(selectCnt - 1)), (Object)selector);
            return;
        }
        catch (CancelledKeyException e) {
            if (!logger.isDebugEnabled()) return;
            logger.debug((String)(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?"), (Object)selector, (Object)e);
        }
    }

    private Selector selectRebuildSelector(int selectCnt) throws IOException {
        logger.warn((String)"Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)Integer.valueOf((int)selectCnt), (Object)this.selector);
        this.rebuildSelector();
        Selector selector = this.selector;
        selector.selectNow();
        return selector;
    }

    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
            return;
        }
        catch (Throwable t) {
            logger.warn((String)"Failed to update SelectionKeys.", (Throwable)t);
        }
    }

    static /* synthetic */ void access$000(NioEventLoop x0, SelectableChannel x1, int x2, NioTask x3) {
        x0.register0((SelectableChannel)x1, (int)x2, x3);
    }

    static /* synthetic */ void access$100(NioEventLoop x0) {
        x0.rebuildSelector0();
    }

    static {
        int selectorAutoRebuildThreshold;
        logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
        DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean((String)"io.netty.noKeySetOptimization", (boolean)false);
        String key = "sun.nio.ch.bugLevel";
        String bugLevel = SystemPropertyUtil.get((String)"sun.nio.ch.bugLevel");
        if (bugLevel == null) {
            try {
                AccessController.doPrivileged(new PrivilegedAction<Void>(){

                    public Void run() {
                        System.setProperty((String)"sun.nio.ch.bugLevel", (String)"");
                        return null;
                    }
                });
            }
            catch (SecurityException e) {
                logger.debug((String)"Unable to get/set System Property: sun.nio.ch.bugLevel", (Throwable)e);
            }
        }
        if ((selectorAutoRebuildThreshold = SystemPropertyUtil.getInt((String)"io.netty.selectorAutoRebuildThreshold", (int)512)) < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (!logger.isDebugEnabled()) return;
        logger.debug((String)"-Dio.netty.noKeySetOptimization: {}", (Object)Boolean.valueOf((boolean)DISABLE_KEY_SET_OPTIMIZATION));
        logger.debug((String)"-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)Integer.valueOf((int)SELECTOR_AUTO_REBUILD_THRESHOLD));
    }
}

