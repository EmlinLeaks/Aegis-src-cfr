/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupException;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroupFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class DefaultChannelGroupFuture
extends DefaultPromise<Void>
implements ChannelGroupFuture {
    private final ChannelGroup group;
    private final Map<Channel, ChannelFuture> futures;
    private int successCount;
    private int failureCount;
    private final ChannelFutureListener childListener = new ChannelFutureListener((DefaultChannelGroupFuture)this){
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ DefaultChannelGroupFuture this$0;
        {
            this.this$0 = this$0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void operationComplete(ChannelFuture future) throws java.lang.Exception {
            boolean callSetDone;
            boolean success = future.isSuccess();
            DefaultChannelGroupFuture defaultChannelGroupFuture = this.this$0;
            // MONITORENTER : defaultChannelGroupFuture
            if (success) {
                DefaultChannelGroupFuture.access$008((DefaultChannelGroupFuture)this.this$0);
            } else {
                DefaultChannelGroupFuture.access$108((DefaultChannelGroupFuture)this.this$0);
            }
            boolean bl = callSetDone = DefaultChannelGroupFuture.access$000((DefaultChannelGroupFuture)this.this$0) + DefaultChannelGroupFuture.access$100((DefaultChannelGroupFuture)this.this$0) == DefaultChannelGroupFuture.access$200((DefaultChannelGroupFuture)this.this$0).size();
            if (!$assertionsDisabled && DefaultChannelGroupFuture.access$000((DefaultChannelGroupFuture)this.this$0) + DefaultChannelGroupFuture.access$100((DefaultChannelGroupFuture)this.this$0) > DefaultChannelGroupFuture.access$200((DefaultChannelGroupFuture)this.this$0).size()) {
                throw new java.lang.AssertionError();
            }
            // MONITOREXIT : defaultChannelGroupFuture
            if (!callSetDone) return;
            if (DefaultChannelGroupFuture.access$100((DefaultChannelGroupFuture)this.this$0) <= 0) {
                DefaultChannelGroupFuture.access$400((DefaultChannelGroupFuture)this.this$0);
                return;
            }
            java.util.ArrayList<java.util.Map$Entry<Channel, Throwable>> failed = new java.util.ArrayList<java.util.Map$Entry<Channel, Throwable>>((int)DefaultChannelGroupFuture.access$100((DefaultChannelGroupFuture)this.this$0));
            Iterator<V> iterator = DefaultChannelGroupFuture.access$200((DefaultChannelGroupFuture)this.this$0).values().iterator();
            do {
                if (!iterator.hasNext()) {
                    DefaultChannelGroupFuture.access$300((DefaultChannelGroupFuture)this.this$0, (ChannelGroupException)new ChannelGroupException(failed));
                    return;
                }
                ChannelFuture f = (ChannelFuture)iterator.next();
                if (f.isSuccess()) continue;
                failed.add(new io.netty.channel.group.DefaultChannelGroupFuture$DefaultEntry<Channel, Throwable>(f.channel(), f.cause()));
            } while (true);
        }

        static {
            $assertionsDisabled = !DefaultChannelGroupFuture.class.desiredAssertionStatus();
        }
    };

    DefaultChannelGroupFuture(ChannelGroup group, Collection<ChannelFuture> futures, EventExecutor executor) {
        super((EventExecutor)executor);
        if (group == null) {
            throw new NullPointerException((String)"group");
        }
        if (futures == null) {
            throw new NullPointerException((String)"futures");
        }
        this.group = group;
        LinkedHashMap<Channel, ChannelFuture> futureMap = new LinkedHashMap<Channel, ChannelFuture>();
        for (ChannelFuture f : futures) {
            futureMap.put(f.channel(), f);
        }
        this.futures = Collections.unmodifiableMap(futureMap);
        Iterator<ChannelFuture> iterator = this.futures.values().iterator();
        do {
            ChannelFuture f;
            if (!iterator.hasNext()) {
                if (!this.futures.isEmpty()) return;
                this.setSuccess0();
                return;
            }
            f = iterator.next();
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)this.childListener);
        } while (true);
    }

    DefaultChannelGroupFuture(ChannelGroup group, Map<Channel, ChannelFuture> futures, EventExecutor executor) {
        super((EventExecutor)executor);
        this.group = group;
        this.futures = Collections.unmodifiableMap(futures);
        Iterator<ChannelFuture> iterator = this.futures.values().iterator();
        do {
            if (!iterator.hasNext()) {
                if (!this.futures.isEmpty()) return;
                this.setSuccess0();
                return;
            }
            ChannelFuture f = iterator.next();
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)this.childListener);
        } while (true);
    }

    @Override
    public ChannelGroup group() {
        return this.group;
    }

    @Override
    public ChannelFuture find(Channel channel) {
        return this.futures.get((Object)channel);
    }

    @Override
    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }

    @Override
    public synchronized boolean isPartialSuccess() {
        if (this.successCount == 0) return false;
        if (this.successCount == this.futures.size()) return false;
        return true;
    }

    @Override
    public synchronized boolean isPartialFailure() {
        if (this.failureCount == 0) return false;
        if (this.failureCount == this.futures.size()) return false;
        return true;
    }

    @Override
    public DefaultChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        super.addListeners((GenericFutureListener[])listeners);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        super.removeListeners((GenericFutureListener[])listeners);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelGroupException cause() {
        return (ChannelGroupException)super.cause();
    }

    private void setSuccess0() {
        super.setSuccess(null);
    }

    private void setFailure0(ChannelGroupException cause) {
        super.setFailure((Throwable)cause);
    }

    public DefaultChannelGroupFuture setSuccess(Void result) {
        throw new IllegalStateException();
    }

    @Override
    public boolean trySuccess(Void result) {
        throw new IllegalStateException();
    }

    public DefaultChannelGroupFuture setFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    protected void checkDeadLock() {
        EventExecutor e = this.executor();
        if (e == null) return;
        if (e == ImmediateEventExecutor.INSTANCE) return;
        if (!e.inEventLoop()) return;
        throw new BlockingOperationException();
    }

    static /* synthetic */ int access$008(DefaultChannelGroupFuture x0) {
        return x0.successCount++;
    }

    static /* synthetic */ int access$108(DefaultChannelGroupFuture x0) {
        return x0.failureCount++;
    }

    static /* synthetic */ int access$000(DefaultChannelGroupFuture x0) {
        return x0.successCount;
    }

    static /* synthetic */ int access$100(DefaultChannelGroupFuture x0) {
        return x0.failureCount;
    }

    static /* synthetic */ Map access$200(DefaultChannelGroupFuture x0) {
        return x0.futures;
    }

    static /* synthetic */ void access$300(DefaultChannelGroupFuture x0, ChannelGroupException x1) {
        x0.setFailure0((ChannelGroupException)x1);
    }

    static /* synthetic */ void access$400(DefaultChannelGroupFuture x0) {
        x0.setSuccess0();
    }
}

