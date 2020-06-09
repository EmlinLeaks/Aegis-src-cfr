/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.AbstractNioMessageChannel;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.List;

public abstract class AbstractNioMessageChannel
extends AbstractNioChannel {
    boolean inputShutdown;

    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super((Channel)parent, (SelectableChannel)ch, (int)readInterestOp);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe((AbstractNioMessageChannel)this, null);
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (this.inputShutdown) {
            return;
        }
        super.doBeginRead();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        SelectionKey key = this.selectionKey();
        int interestOps = key.interestOps();
        do {
            Object msg;
            if ((msg = in.current()) == null) {
                if ((interestOps & 4) == 0) return;
                key.interestOps((int)(interestOps & -5));
                return;
            }
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                    if (!this.doWriteMessage((Object)msg, (ChannelOutboundBuffer)in)) continue;
                    done = true;
                    break;
                }
                if (!done) {
                    if ((interestOps & 4) != 0) return;
                    key.interestOps((int)(interestOps | 4));
                    return;
                }
                in.remove();
                continue;
            }
            catch (Exception e) {
                if (!this.continueOnWriteError()) throw e;
                in.remove((Throwable)e);
                continue;
            }
            break;
        } while (true);
    }

    protected boolean continueOnWriteError() {
        return false;
    }

    protected boolean closeOnReadError(Throwable cause) {
        if (!this.isActive()) {
            return true;
        }
        if (cause instanceof PortUnreachableException) {
            return false;
        }
        if (!(cause instanceof IOException)) return true;
        if (this instanceof ServerChannel) return false;
        return true;
    }

    protected abstract int doReadMessages(List<Object> var1) throws Exception;

    protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;
}

