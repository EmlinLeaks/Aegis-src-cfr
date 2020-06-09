/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.TimeUnit;

public final class VoidChannelPromise
extends AbstractFuture<Void>
implements ChannelPromise {
    private final Channel channel;
    private final ChannelFutureListener fireExceptionListener;

    public VoidChannelPromise(Channel channel, boolean fireException) {
        if (channel == null) {
            throw new NullPointerException((String)"channel");
        }
        this.channel = channel;
        if (fireException) {
            this.fireExceptionListener = new ChannelFutureListener((VoidChannelPromise)this){
                final /* synthetic */ VoidChannelPromise this$0;
                {
                    this.this$0 = this$0;
                }

                public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                    Throwable cause = future.cause();
                    if (cause == null) return;
                    VoidChannelPromise.access$000((VoidChannelPromise)this.this$0, (Throwable)cause);
                }
            };
            return;
        }
        this.fireExceptionListener = null;
    }

    @Override
    public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        return this;
    }

    @Override
    public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>> ... listeners) {
        return this;
    }

    @Override
    public VoidChannelPromise await() throws InterruptedException {
        if (!Thread.interrupted()) return this;
        throw new InterruptedException();
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public boolean await(long timeoutMillis) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public VoidChannelPromise awaitUninterruptibly() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        VoidChannelPromise.fail();
        return false;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean setUncancellable() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public VoidChannelPromise sync() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise syncUninterruptibly() {
        VoidChannelPromise.fail();
        return this;
    }

    @Override
    public VoidChannelPromise setFailure(Throwable cause) {
        this.fireException0((Throwable)cause);
        return this;
    }

    @Override
    public VoidChannelPromise setSuccess() {
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        this.fireException0((Throwable)cause);
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean trySuccess() {
        return false;
    }

    private static void fail() {
        throw new IllegalStateException((String)"void future");
    }

    @Override
    public VoidChannelPromise setSuccess(Void result) {
        return this;
    }

    @Override
    public boolean trySuccess(Void result) {
        return false;
    }

    @Override
    public Void getNow() {
        return null;
    }

    @Override
    public ChannelPromise unvoid() {
        DefaultChannelPromise promise = new DefaultChannelPromise((Channel)this.channel);
        if (this.fireExceptionListener == null) return promise;
        promise.addListener((GenericFutureListener<? extends Future<? super Void>>)this.fireExceptionListener);
        return promise;
    }

    @Override
    public boolean isVoid() {
        return true;
    }

    private void fireException0(Throwable cause) {
        if (this.fireExceptionListener == null) return;
        if (!this.channel.isRegistered()) return;
        this.channel.pipeline().fireExceptionCaught((Throwable)cause);
    }

    static /* synthetic */ void access$000(VoidChannelPromise x0, Throwable x1) {
        x0.fireException0((Throwable)x1);
    }
}

