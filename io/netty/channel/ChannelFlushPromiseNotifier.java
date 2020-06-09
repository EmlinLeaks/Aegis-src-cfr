/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelFlushPromiseNotifier;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public final class ChannelFlushPromiseNotifier {
    private long writeCounter;
    private final Queue<FlushCheckpoint> flushCheckpoints = new ArrayDeque<FlushCheckpoint>();
    private final boolean tryNotify;

    public ChannelFlushPromiseNotifier(boolean tryNotify) {
        this.tryNotify = tryNotify;
    }

    public ChannelFlushPromiseNotifier() {
        this((boolean)false);
    }

    @Deprecated
    public ChannelFlushPromiseNotifier add(ChannelPromise promise, int pendingDataSize) {
        return this.add((ChannelPromise)promise, (long)((long)pendingDataSize));
    }

    public ChannelFlushPromiseNotifier add(ChannelPromise promise, long pendingDataSize) {
        if (promise == null) {
            throw new NullPointerException((String)"promise");
        }
        ObjectUtil.checkPositiveOrZero((long)pendingDataSize, (String)"pendingDataSize");
        long checkpoint = this.writeCounter + pendingDataSize;
        if (promise instanceof FlushCheckpoint) {
            FlushCheckpoint cp = (FlushCheckpoint)((Object)promise);
            cp.flushCheckpoint((long)checkpoint);
            this.flushCheckpoints.add((FlushCheckpoint)cp);
            return this;
        }
        this.flushCheckpoints.add((FlushCheckpoint)new DefaultFlushCheckpoint((long)checkpoint, (ChannelPromise)promise));
        return this;
    }

    public ChannelFlushPromiseNotifier increaseWriteCounter(long delta) {
        ObjectUtil.checkPositiveOrZero((long)delta, (String)"delta");
        this.writeCounter += delta;
        return this;
    }

    public long writeCounter() {
        return this.writeCounter;
    }

    public ChannelFlushPromiseNotifier notifyPromises() {
        this.notifyPromises0(null);
        return this;
    }

    @Deprecated
    public ChannelFlushPromiseNotifier notifyFlushFutures() {
        return this.notifyPromises();
    }

    public ChannelFlushPromiseNotifier notifyPromises(Throwable cause) {
        this.notifyPromises();
        FlushCheckpoint cp;
        while ((cp = this.flushCheckpoints.poll()) != null) {
            if (this.tryNotify) {
                cp.promise().tryFailure((Throwable)cause);
                continue;
            }
            cp.promise().setFailure((Throwable)cause);
        }
        return this;
    }

    @Deprecated
    public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause) {
        return this.notifyPromises((Throwable)cause);
    }

    public ChannelFlushPromiseNotifier notifyPromises(Throwable cause1, Throwable cause2) {
        this.notifyPromises0((Throwable)cause1);
        FlushCheckpoint cp;
        while ((cp = this.flushCheckpoints.poll()) != null) {
            if (this.tryNotify) {
                cp.promise().tryFailure((Throwable)cause2);
                continue;
            }
            cp.promise().setFailure((Throwable)cause2);
        }
        return this;
    }

    @Deprecated
    public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause1, Throwable cause2) {
        return this.notifyPromises((Throwable)cause1, (Throwable)cause2);
    }

    private void notifyPromises0(Throwable cause) {
        if (this.flushCheckpoints.isEmpty()) {
            this.writeCounter = 0L;
            return;
        }
        long writeCounter = this.writeCounter;
        do {
            FlushCheckpoint cp;
            if ((cp = this.flushCheckpoints.peek()) == null) {
                this.writeCounter = 0L;
                break;
            }
            if (cp.flushCheckpoint() > writeCounter) {
                if (writeCounter <= 0L || this.flushCheckpoints.size() != 1) break;
                this.writeCounter = 0L;
                cp.flushCheckpoint((long)(cp.flushCheckpoint() - writeCounter));
                break;
            }
            this.flushCheckpoints.remove();
            ChannelPromise promise = cp.promise();
            if (cause == null) {
                if (this.tryNotify) {
                    promise.trySuccess();
                    continue;
                }
                promise.setSuccess();
                continue;
            }
            if (this.tryNotify) {
                promise.tryFailure((Throwable)cause);
                continue;
            }
            promise.setFailure((Throwable)cause);
        } while (true);
        long newWriteCounter = this.writeCounter;
        if (newWriteCounter < 0x8000000000L) return;
        this.writeCounter = 0L;
        Iterator<E> iterator = this.flushCheckpoints.iterator();
        while (iterator.hasNext()) {
            FlushCheckpoint cp = (FlushCheckpoint)iterator.next();
            cp.flushCheckpoint((long)(cp.flushCheckpoint() - newWriteCounter));
        }
    }
}

