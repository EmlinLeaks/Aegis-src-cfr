/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

class EpollEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    private final FileDescriptor epollFd;
    private final FileDescriptor eventFd;
    private final FileDescriptor timerFd;
    private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap<AbstractEpollChannel>((int)4096);
    private final BitSet pendingFlagChannels = new BitSet();
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private IovArray iovArray;
    private NativeDatagramPacketArray datagramPacketArray;
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier = new IntSupplier((EpollEventLoop)this){
        final /* synthetic */ EpollEventLoop this$0;
        {
            this.this$0 = this$0;
        }

        public int get() throws Exception {
            return EpollEventLoop.access$000((EpollEventLoop)this.this$0);
        }
    };
    private final AtomicLong nextWakeupNanos = new AtomicLong((long)-1L);
    private boolean pendingWakeup;
    private volatile int ioRatio = 50;
    private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory queueFactory) {
        super((EventLoopGroup)parent, (Executor)executor, (boolean)false, EpollEventLoop.newTaskQueue((EventLoopTaskQueueFactory)queueFactory), EpollEventLoop.newTaskQueue((EventLoopTaskQueueFactory)queueFactory), (RejectedExecutionHandler)rejectedExecutionHandler);
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, (String)"strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray((int)4096);
        } else {
            this.allowGrowing = false;
            this.events = new EpollEventArray((int)maxEvents);
        }
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                Native.epollCtlAdd((int)epollFd.intValue(), (int)eventFd.intValue(), (int)(Native.EPOLLIN | Native.EPOLLET));
            }
            catch (IOException e) {
                throw new IllegalStateException((String)"Unable to add eventFd filedescriptor to epoll", (Throwable)e);
            }
            this.timerFd = timerFd = Native.newTimerFd();
            try {
                Native.epollCtlAdd((int)epollFd.intValue(), (int)timerFd.intValue(), (int)(Native.EPOLLIN | Native.EPOLLET));
            }
            catch (IOException e) {
                throw new IllegalStateException((String)"Unable to add timerFd filedescriptor to epoll", (Throwable)e);
            }
            success = true;
            return;
        }
        finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception e) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception e) {}
                }
                if (timerFd != null) {
                    try {
                        timerFd.close();
                    }
                    catch (Exception e) {}
                }
            }
        }
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory != null) return queueFactory.newTaskQueue((int)DEFAULT_MAX_PENDING_TASKS);
        return EpollEventLoop.newTaskQueue0((int)DEFAULT_MAX_PENDING_TASKS);
    }

    IovArray cleanIovArray() {
        if (this.iovArray == null) {
            this.iovArray = new IovArray();
            return this.iovArray;
        }
        this.iovArray.clear();
        return this.iovArray;
    }

    NativeDatagramPacketArray cleanDatagramPacketArray() {
        if (this.datagramPacketArray == null) {
            this.datagramPacketArray = new NativeDatagramPacketArray();
            return this.datagramPacketArray;
        }
        this.datagramPacketArray.clear();
        return this.datagramPacketArray;
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (inEventLoop) return;
        if (this.nextWakeupNanos.getAndSet((long)-1L) == -1L) return;
        Native.eventFdWrite((int)this.eventFd.intValue(), (long)1L);
    }

    @Override
    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        if (deadlineNanos >= this.nextWakeupNanos.get()) return false;
        return true;
    }

    @Override
    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        if (deadlineNanos >= this.nextWakeupNanos.get()) return false;
        return true;
    }

    void add(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        int fd = ch.socket.intValue();
        Native.epollCtlAdd((int)this.epollFd.intValue(), (int)fd, (int)ch.flags);
        ch.activeFlags = ch.flags;
        AbstractEpollChannel old = this.channels.put((int)fd, (AbstractEpollChannel)ch);
        if ($assertionsDisabled) return;
        if (old == null) return;
        if (!old.isOpen()) return;
        throw new AssertionError();
    }

    void modify(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        Native.epollCtlMod((int)this.epollFd.intValue(), (int)ch.socket.intValue(), (int)ch.flags);
        ch.activeFlags = ch.flags;
    }

    void updatePendingFlagsSet(AbstractEpollChannel ch) {
        this.pendingFlagChannels.set((int)ch.socket.intValue(), (boolean)(ch.flags != ch.activeFlags));
    }

    private void processPendingChannelFlags() {
        if (this.pendingFlagChannels.isEmpty()) return;
        int fd = 0;
        while ((fd = this.pendingFlagChannels.nextSetBit((int)fd)) >= 0) {
            AbstractEpollChannel ch = this.channels.get((int)fd);
            if (ch != null) {
                try {
                    ch.modifyEvents();
                }
                catch (IOException e) {
                    ch.pipeline().fireExceptionCaught((Throwable)e);
                    ch.close();
                }
            }
            this.pendingFlagChannels.clear((int)fd);
        }
    }

    void remove(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        int fd = ch.socket.intValue();
        AbstractEpollChannel old = this.channels.remove((int)fd);
        if (old != null && old != ch) {
            this.channels.put((int)fd, (AbstractEpollChannel)old);
            if ($assertionsDisabled) return;
            if (!ch.isOpen()) return;
            throw new AssertionError();
        }
        ch.activeFlags = 0;
        this.pendingFlagChannels.clear((int)fd);
        if (!ch.isOpen()) return;
        Native.epollCtlDel((int)this.epollFd.intValue(), (int)fd);
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return EpollEventLoop.newTaskQueue0((int)maxPendingTasks);
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

    @Override
    public int registeredChannels() {
        return this.channels.size();
    }

    private int epollWait(long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return Native.epollWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events, (FileDescriptor)this.timerFd, (int)Integer.MAX_VALUE, (int)0);
        }
        long totalDelay = EpollEventLoop.deadlineToDelayNanos((long)deadlineNanos);
        int delaySeconds = (int)Math.min((long)(totalDelay / 1000000000L), (long)Integer.MAX_VALUE);
        int delayNanos = (int)Math.min((long)(totalDelay - (long)delaySeconds * 1000000000L), (long)999999999L);
        return Native.epollWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events, (FileDescriptor)this.timerFd, (int)delaySeconds, (int)delayNanos);
    }

    private int epollWaitNoTimerChange() throws IOException {
        return Native.epollWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events, (boolean)false);
    }

    private int epollWaitNow() throws IOException {
        return Native.epollWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events, (boolean)true);
    }

    private int epollBusyWait() throws IOException {
        return Native.epollBusyWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events);
    }

    private int epollWaitTimeboxed() throws IOException {
        return Native.epollWait((FileDescriptor)this.epollFd, (EpollEventArray)this.events, (int)1000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    protected void run() {
        prevDeadlineNanos = Long.MAX_VALUE;
        do {
            try {
                block18 : do {
                    this.processPendingChannelFlags();
                    strategy = this.selectStrategy.calculateStrategy((IntSupplier)this.selectNowSupplier, (boolean)this.hasTasks());
                    switch (strategy) {
                        case -2: {
                            continue block18;
                        }
                        case -3: {
                            strategy = this.epollBusyWait();
                            break block18;
                        }
                        case -1: {
                            if (this.pendingWakeup) {
                                strategy = this.epollWaitTimeboxed();
                                if (strategy != 0) break block18;
                                EpollEventLoop.logger.warn((String)"Missed eventfd write (not seen after > 1 second)");
                                this.pendingWakeup = false;
                                if (this.hasTasks()) break block18;
                            }
                            if ((curDeadlineNanos = this.nextScheduledTaskDeadlineNanos()) == -1L) {
                                curDeadlineNanos = Long.MAX_VALUE;
                            }
                            this.nextWakeupNanos.set((long)curDeadlineNanos);
                            try {
                                if (!this.hasTasks()) {
                                    if (curDeadlineNanos == prevDeadlineNanos) {
                                        strategy = this.epollWaitNoTimerChange();
                                    } else {
                                        prevDeadlineNanos = curDeadlineNanos;
                                        strategy = this.epollWait((long)curDeadlineNanos);
                                    }
                                }
                                if (this.nextWakeupNanos.get() != -1L && this.nextWakeupNanos.getAndSet((long)-1L) != -1L) break block18;
                                this.pendingWakeup = true;
                                break block18;
                            }
                            catch (Throwable var6_10) {
                                if (this.nextWakeupNanos.get() != -1L) {
                                    if (this.nextWakeupNanos.getAndSet((long)-1L) != -1L) throw var6_10;
                                }
                                this.pendingWakeup = true;
                                throw var6_10;
                            }
                        }
                    }
                    break;
                } while (true);
                ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    try {
                        if (strategy <= 0 || !this.processReady((EpollEventArray)this.events, (int)strategy)) ** GOTO lbl58
                        prevDeadlineNanos = Long.MAX_VALUE;
                    }
                    finally {
                        this.runAllTasks();
                    }
                } else {
                    ioStartTime = System.nanoTime();
                    try {
                        if (strategy > 0 && this.processReady((EpollEventArray)this.events, (int)strategy)) {
                            prevDeadlineNanos = Long.MAX_VALUE;
                        }
                    }
                    finally {
                        ioTime = System.nanoTime() - ioStartTime;
                        this.runAllTasks((long)(ioTime * (long)(100 - ioRatio) / (long)ioRatio));
                    }
                }
                if (this.allowGrowing && strategy == this.events.length()) {
                    this.events.increase();
                }
            }
            catch (Throwable t) {
                this.handleLoopException((Throwable)t);
            }
            try {
                if (!this.isShuttingDown()) continue;
                this.closeAll();
                if (!this.confirmShutdown()) continue;
                return;
            }
            catch (Throwable t) {
                this.handleLoopException((Throwable)t);
                continue;
            }
            break;
        } while (true);
    }

    void handleLoopException(Throwable t) {
        logger.warn((String)"Unexpected exception in the selector loop.", (Throwable)t);
        try {
            Thread.sleep((long)1000L);
            return;
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void closeAll() {
        AbstractEpollChannel[] localChannels;
        AbstractEpollChannel[] arrabstractEpollChannel = localChannels = this.channels.values().toArray(new AbstractEpollChannel[0]);
        int n = arrabstractEpollChannel.length;
        int n2 = 0;
        while (n2 < n) {
            AbstractEpollChannel ch = arrabstractEpollChannel[n2];
            ch.unsafe().close((ChannelPromise)ch.unsafe().voidPromise());
            ++n2;
        }
    }

    private boolean processReady(EpollEventArray events, int ready) {
        boolean timerFired = false;
        int i = 0;
        while (i < ready) {
            int fd = events.fd((int)i);
            if (fd == this.eventFd.intValue()) {
                this.pendingWakeup = false;
            } else if (fd == this.timerFd.intValue()) {
                timerFired = true;
            } else {
                long ev = (long)events.events((int)i);
                AbstractEpollChannel ch = this.channels.get((int)fd);
                if (ch != null) {
                    AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
                    if ((ev & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                        unsafe.epollOutReady();
                    }
                    if ((ev & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                        unsafe.epollInReady();
                    }
                    if ((ev & (long)Native.EPOLLRDHUP) != 0L) {
                        unsafe.epollRdHupReady();
                    }
                } else {
                    try {
                        Native.epollCtlDel((int)this.epollFd.intValue(), (int)fd);
                    }
                    catch (IOException unsafe) {
                        // empty catch block
                    }
                }
            }
            ++i;
        }
        return timerFired;
    }

    @Override
    protected void cleanup() {
        try {
            block11 : while (this.pendingWakeup) {
                try {
                    int count = this.epollWaitTimeboxed();
                    if (count == 0) break;
                    for (int i = 0; i < count; ++i) {
                        if (this.events.fd((int)i) != this.eventFd.intValue()) continue;
                        this.pendingWakeup = false;
                        continue block11;
                    }
                }
                catch (IOException count) {
                }
            }
            try {
                this.eventFd.close();
            }
            catch (IOException e) {
                logger.warn((String)"Failed to close the event fd.", (Throwable)e);
            }
            try {
                this.timerFd.close();
            }
            catch (IOException e) {
                logger.warn((String)"Failed to close the timer fd.", (Throwable)e);
            }
            try {
                this.epollFd.close();
                return;
            }
            catch (IOException e) {
                logger.warn((String)"Failed to close the epoll fd.", (Throwable)e);
                return;
            }
        }
        finally {
            if (this.iovArray != null) {
                this.iovArray.release();
                this.iovArray = null;
            }
            if (this.datagramPacketArray != null) {
                this.datagramPacketArray.release();
                this.datagramPacketArray = null;
            }
            this.events.free();
        }
    }

    static /* synthetic */ int access$000(EpollEventLoop x0) throws IOException {
        return x0.epollWaitNow();
    }

    static {
        Epoll.ensureAvailability();
    }
}

