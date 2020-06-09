/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.channel.oio.AbstractOioChannel;
import java.net.SocketAddress;

@Deprecated
public abstract class AbstractOioChannel
extends AbstractChannel {
    protected static final int SO_TIMEOUT = 1000;
    boolean readPending;
    private final Runnable readTask = new Runnable((AbstractOioChannel)this){
        final /* synthetic */ AbstractOioChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.doRead();
        }
    };
    private final Runnable clearReadPendingRunnable = new Runnable((AbstractOioChannel)this){
        final /* synthetic */ AbstractOioChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.readPending = false;
        }
    };

    protected AbstractOioChannel(Channel parent) {
        super((Channel)parent);
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new DefaultOioUnsafe((AbstractOioChannel)this, null);
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof ThreadPerChannelEventLoop;
    }

    protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

    @Override
    protected void doBeginRead() throws Exception {
        if (this.readPending) {
            return;
        }
        this.readPending = true;
        this.eventLoop().execute((Runnable)this.readTask);
    }

    protected abstract void doRead();

    @Deprecated
    protected boolean isReadPending() {
        return this.readPending;
    }

    @Deprecated
    protected void setReadPending(boolean readPending) {
        if (!this.isRegistered()) {
            this.readPending = readPending;
            return;
        }
        EventLoop eventLoop = this.eventLoop();
        if (eventLoop.inEventLoop()) {
            this.readPending = readPending;
            return;
        }
        eventLoop.execute((Runnable)new Runnable((AbstractOioChannel)this, (boolean)readPending){
            final /* synthetic */ boolean val$readPending;
            final /* synthetic */ AbstractOioChannel this$0;
            {
                this.this$0 = this$0;
                this.val$readPending = bl;
            }

            public void run() {
                this.this$0.readPending = this.val$readPending;
            }
        });
    }

    protected final void clearReadPending() {
        if (!this.isRegistered()) {
            this.readPending = false;
            return;
        }
        EventLoop eventLoop = this.eventLoop();
        if (eventLoop.inEventLoop()) {
            this.readPending = false;
            return;
        }
        eventLoop.execute((Runnable)this.clearReadPendingRunnable);
    }
}

